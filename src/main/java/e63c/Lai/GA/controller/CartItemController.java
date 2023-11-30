package e63c.Lai.GA.controller;

import java.security.Principal;
import java.util.List;

import e63c.Lai.GA.*;
import e63c.Lai.GA.model.Accessory;
import e63c.Lai.GA.model.CartItem;
import e63c.Lai.GA.model.Member;
import e63c.Lai.GA.model.OrderItem;
import e63c.Lai.GA.repository.AccessoryRepository;
import e63c.Lai.GA.repository.CartItemRepository;
import e63c.Lai.GA.repository.MemberRepository;
import e63c.Lai.GA.repository.OrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CartItemController {

	@Autowired
	private CartItemRepository cartItemRepo;
	
	@Autowired
	private OrderItemRepository orderRepo;

	@Autowired
	private AccessoryRepository accessoryRepo;

	@Autowired
	private MemberRepository memberRepo;
	
	@Autowired
	private JavaMailSender javaMailSender;

	@GetMapping("/cart")
	public String showCart(Model model, Principal principal) {
		MemberDetails loggedInMember = (MemberDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		int loggedInMemberId = loggedInMember.getMember().getId();
		
		List<CartItem> cartItemList = cartItemRepo.findByMemberId(loggedInMemberId);
		
		model.addAttribute("cartItemList" ,cartItemList);
		
		double cartTotal = 0.0;
		for (int i=0; i<cartItemList.size(); i++) {
			
			CartItem currentCartItem = cartItemList.get(i);
			int itemQuantityInCart = currentCartItem.getQuantity();
			model.addAttribute("memberId", loggedInMemberId);
			
				Accessory item = currentCartItem.getAccessory();
				double itemPrice = item.getPrice();
				
				currentCartItem.setSubTotal((double)Math.round(itemPrice * itemQuantityInCart * 100)/100);
				cartTotal += itemPrice * itemQuantityInCart;
		}
		cartTotal = ((double)Math.round(cartTotal * 100)/100);
		
		model.addAttribute("cartTotal", cartTotal);
		
		return "cart";
	}

	@PostMapping("/cart/add/{accessoryId}")
	public String addToCart(@PathVariable("accessoryId") int accessoryId, @RequestParam("quantity") int quantity,
			Principal principal, RedirectAttributes redirectAttribute) {

		MemberDetails loggedInMember = (MemberDetails) SecurityContextHolder.getContextHolderStrategy().getContext().getAuthentication().getPrincipal();
		int loggedInMemberId = loggedInMember.getMember().getId();

		CartItem cartItem = cartItemRepo.findByMemberIdAndAccessoryId(loggedInMemberId, accessoryId);

		if (cartItem != null) {
			int currentQuantity = cartItem.getQuantity();
			cartItem.setQuantity(quantity + currentQuantity);
			cartItemRepo.save(cartItem);
		} else {

			Accessory item = accessoryRepo.getById(accessoryId);
			Member member = memberRepo.getById(loggedInMemberId);

			CartItem newCartItem = new CartItem();

			newCartItem.setAccessory(item);
			newCartItem.setMember(member);
			newCartItem.setQuantity(quantity);

			cartItemRepo.save(newCartItem);
		}
		redirectAttribute.addFlashAttribute("success","Accessory added!");
		return "redirect:/";
	}

	@PostMapping("/cart/update/{id}")
	public String updateCart(@PathVariable("id") int cartItemId, @RequestParam("quantity") int quantity, RedirectAttributes redirectAttribute) {

		CartItem cartItem = cartItemRepo.getById(cartItemId);

		if (quantity <= 0) {
			cartItemRepo.deleteById(cartItemId);
			return "redirect:/cart";
		      
		} else {
			cartItem.setQuantity(quantity);
			
			cartItemRepo.save(cartItem);
			redirectAttribute.addFlashAttribute("success","Quantity changed!");
			return "redirect:/cart";			
		}
	}

	@GetMapping("/cart/remove/{id}")
	public String removeFromCart(@PathVariable("id") int cartItemId, RedirectAttributes redirectAttribute) {
		cartItemRepo.deleteById(cartItemId);
		redirectAttribute.addFlashAttribute("success","Accessory deleted!");
		return "redirect:/cart";
	}
	
	@PostMapping("/cart/process_order")
	public String processOrder(Model model, @RequestParam("cartTotal") double cartTotal,
		@RequestParam("memberId") int memberId, @RequestParam("orderId") String orderId,
		@RequestParam("transactionId") String transactionId) {
		List<CartItem> cartItemList = cartItemRepo.findByMemberId(memberId);
		Member member = memberRepo.getById(memberId);
		String details = "";
		for (int i = 0; i < cartItemList.size(); i++) {
			CartItem currentCartItem = cartItemList.get(i);
			Accessory itemToUpdate = currentCartItem.getAccessory();
			int quantityOfItemPurchased = currentCartItem.getQuantity();
			int itemToUpdateId = itemToUpdate.getId();
			currentCartItem.setSubTotal(quantityOfItemPurchased * currentCartItem.getAccessory().getPrice());
			
			System.out.println("Accessory: " + itemToUpdate.getDescription());
			Accessory inventoryItem = accessoryRepo.getById(itemToUpdateId);
			int currentInventoryQuantity = inventoryItem.getQuantity();
			System.out.println("Current inventory quantity: " + inventoryItem.getQuantity());
			inventoryItem.setQuantity(currentInventoryQuantity - quantityOfItemPurchased);
			inventoryItem.setSold(inventoryItem.getSold() + quantityOfItemPurchased);
			accessoryRepo.save(inventoryItem);
			OrderItem orderItem = new OrderItem();
			orderItem.setOrderId(orderId);
			orderItem.setTransactionId(transactionId);
			orderItem.setAccessory(itemToUpdate);
			orderItem.setMember(member);
			orderItem.setQuantity(quantityOfItemPurchased);
			orderRepo.save(orderItem);
			cartItemRepo.deleteById(currentCartItem.getId());
			details += String.format("  %-75s %-20s $%-20s \n", currentCartItem.getAccessory().getName(), currentCartItem.getQuantity(), currentCartItem.getSubTotal());
			
		}
		cartTotal = ((double)Math.round(cartTotal * 100)/100);
		model.addAttribute("cartTotal", cartTotal);
		model.addAttribute("cartItemList", cartItemList);
		model.addAttribute("member", member);
		model.addAttribute("orderId", orderId);
		model.addAttribute("transactionId", transactionId);
		String subject = "Order for radiant accessories is confirmed!";
		String body = String.format("\nINVOICE SUMMARY \n\nThank you for shopping with us at RADIANCE. \n"
				+ "____________________________________________________________________________________\n Customer's Name: %-35s %40s \n"
				+ " Customer's Username: %-33s %-40s \n %101s \n %100s \n____________________________________________________________________________________"
				+ "\n  %-75s %-20s $%-20s  \n"
				+ "____________________________________________________________________________________\n", member.getName(), "Transaction ID: " + transactionId, 
				member.getUsername(), "Order ID: " + orderId, "Payment Term: Paypal", "Total Amount: $" + cartTotal, "Product", "Quantity", "Amount");
		
		String to = member.getEmail();
		sendEmail(to, subject, body+details);

		return "success";
	}
	public void sendEmail(String to, String subject, String body) {
	   SimpleMailMessage msg = new SimpleMailMessage();
	   msg.setTo(to);
	   msg.setSubject(subject);
	   msg.setText(body);
	   System.out.println("Sending");
	   javaMailSender.send(msg);
	   System.out.println("Sent");
	}

	@GetMapping("/purchase_history")
	public String viewHistory(Model model) {
		MemberDetails loggedInMember = (MemberDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		int loggedInMemberId = loggedInMember.getMember().getId();
		List<OrderItem> itemPurchase = orderRepo.findByMemberId(loggedInMemberId);
		model.addAttribute("itemPurchased", itemPurchase);
		
		for (int i=0; i<itemPurchase.size(); i++) {
			
			OrderItem currentItem = itemPurchase.get(i);
			int itemQuantityInCart = currentItem.getQuantity();
			Accessory item = currentItem.getAccessory();
			double itemPrice = item.getPrice();
			currentItem.setSubTotal((double)Math.round(itemPrice * itemQuantityInCart * 100)/100);
		}
		return "purchase_history";
	}
	
	@GetMapping("/member/{id}/purchase_history")
	public String viewUserHistory(@PathVariable("id") Integer id, Model model) {
		Member member = memberRepo.getById(id);
		List<OrderItem> itemPurchase = orderRepo.findByMemberId(id);
		model.addAttribute("itemPurchased", itemPurchase);
		
		for (int i=0; i<itemPurchase.size(); i++) {
			
			OrderItem currentItem = itemPurchase.get(i);
			int itemQuantityInCart = currentItem.getQuantity();
			Accessory item = currentItem.getAccessory();
			double itemPrice = item.getPrice();
			currentItem.setSubTotal((double)Math.round(itemPrice * itemQuantityInCart * 100)/100);
		}
		model.addAttribute("member", member);
		
		return "user_purchase_history";
	}

}
