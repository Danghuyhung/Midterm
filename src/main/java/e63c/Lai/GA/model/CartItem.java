package e63c.Lai.GA.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

@Entity
public class CartItem {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	
	@ManyToOne
	@JoinColumn(name="member_id")
	private Member member;
	
	@ManyToOne
	@JoinColumn(name="accessory_id")
	private Accessory accessory;
	
	private int quantity;
	
	@Transient
	private double subTotal;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	public Accessory getAccessory() {
		return accessory;
	}

	public void setAccessory(Accessory accessory) {
		this.accessory = accessory;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public double getSubTotal() {
		return subTotal;
	}

	public void setSubTotal(double subTotal) {
		this.subTotal = subTotal;
	}
}
