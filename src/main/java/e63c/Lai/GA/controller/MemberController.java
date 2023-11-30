package e63c.Lai.GA.controller;

import java.util.List;

import javax.validation.Valid;

import e63c.Lai.GA.repository.CartItemRepository;
import e63c.Lai.GA.MemberDetails;
import e63c.Lai.GA.repository.MemberRepository;
import e63c.Lai.GA.repository.OrderItemRepository;
import e63c.Lai.GA.model.CartItem;
import e63c.Lai.GA.model.Member;
import e63c.Lai.GA.model.OrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MemberController {
	@Autowired
	private MemberRepository memberRepository;
	
	@Autowired
	private CartItemRepository cartRepo;
	
	@Autowired
	private OrderItemRepository orderRepo;

	@GetMapping("/members")
	public String viewMembers(Model model) {

		List<Member> listMember = memberRepository.findAll();
		model.addAttribute("listMember", listMember);
		return "view_member";
	}

	@GetMapping("/members/add")
	public String addCategory(Model model) {
		model.addAttribute("member", new Member());
		return "add_member";
	}

	@PostMapping("/members/save")
	public String saveMember(Member member, RedirectAttributes redirectAttribute) {
		
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String encodedPassword = passwordEncoder.encode(member.getPassword());
		
		member.setPassword(encodedPassword);
		Member existingMember = memberRepository.findByUsername(member.getUsername());
		if (existingMember != null) {
			redirectAttribute.addFlashAttribute("error", "Username already taken. Please enter another");
			return "redirect:/members/save";
		}
		memberRepository.save(member);
		
		redirectAttribute.addFlashAttribute("success","Member registered!");
		
		return "redirect:/members";
	}
	
	@GetMapping("/Sign-Up")
	public String addSignUpMember(Model model) {
		model.addAttribute("member", new Member());
		return "signup";
	}
	
	@PostMapping("/Sign-Up/save")
	public String saveSignupMember(Member member, RedirectAttributes redirectAttribute) {
		
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String encodedPassword = passwordEncoder.encode(member.getPassword());
		member.setPassword(encodedPassword);
		member.setRole("ROLE_USER");
		Member existingMember = memberRepository.findByUsername(member.getUsername());
		if (existingMember != null) {
			redirectAttribute.addFlashAttribute("error", "Username already taken. Please enter another");
			return "redirect:/Sign-Up";
		}
		
		memberRepository.save(member);
		redirectAttribute.addFlashAttribute("success","Sign Up Success! Please Login");
		
		return "redirect:/login";
	}


	@GetMapping("/members/edit/{id}")
	public String editMembers(@PathVariable("id") Integer id, Model model) {

		Member member = memberRepository.getById(id);
		model.addAttribute("member", member);

		return "edit_member";
	}

	@PostMapping("/members/edit/{id}")
	public String saveUpdatedMember(@PathVariable("id") Integer id, Member member, RedirectAttributes redirectAttribute) {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String encodedPassword = passwordEncoder.encode(member.getPassword());
		member.setPassword(encodedPassword);
		
		Member existingMember = memberRepository.findByUsername(member.getUsername());
		if (existingMember != null) {
			redirectAttribute.addFlashAttribute("error", "Username already taken. Please enter a different username");
			return "redirect:/members/save";
		}
		
		memberRepository.save(member);
		return "redirect:/members";
	}

	@GetMapping("/members/delete/{id}")
	public String deleteCategory(@PathVariable("id") Integer id) {
		
		List<CartItem> cartItemList = cartRepo.findByMemberId(id);
		List<OrderItem> orderItemList = orderRepo.findByMemberId(id);
		if (cartItemList != null) {
			for (int i=0; i<cartItemList.size(); i++) {
				CartItem currentCartItem = cartItemList.get(i);
				int itemId = currentCartItem.getId();
				cartRepo.deleteById(itemId);
			}
		}
		if (orderItemList != null) {
			for (int i=0; i<orderItemList.size(); i++) {
				OrderItem currentOrderItem = orderItemList.get(i);
				int orderId = currentOrderItem.getId();
				orderRepo.deleteById(orderId);
			}
		} 
		
		memberRepository.deleteById(id);
		return "redirect:/members";
	}
	@GetMapping("/personal_details")
	public String pd(Model model) {
		MemberDetails loggedInMember = (MemberDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		int loggedInMemberId = loggedInMember.getMember().getId();
	    Member member = memberRepository.getById(loggedInMemberId);
	    model.addAttribute("member", member);
		return "view_pd";
	}
	@GetMapping("/members/editByUser/{id}")
	public String editpd(@PathVariable("id") Integer id, Model model) {
		Member member = memberRepository.getById(id);
		model.addAttribute("member", member);

		return "personal_details";
	}
	
	@PostMapping("/members/editByUser/{id}")
	public String saveUpdatedmember(@PathVariable("id") Integer id, @Valid Member member, BindingResult bindingResult, RedirectAttributes redirectAttribute) {
		if (bindingResult.hasErrors()) {
		  return "personal_details";
		}
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String encodedPassword = passwordEncoder.encode(member.getPassword());
		    
		member.setPassword(encodedPassword);
		memberRepository.save(member);
		return "home";
	}
	

}