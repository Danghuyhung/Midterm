package e63c.Lai.GA.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
public class Accessory {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	
	@NotNull
	@NotEmpty(message="Name name cannot be empty!")
	@Size(min=5, max=50, message="Name length must be between 5 to 50 characters!")
	@Column(unique = true)
	private String name;
	
	@NotNull
	@NotEmpty(message="Description cannot be empty!")
	@Size(min=5, max=200, message="Description length must be between 5 to 200 characters!")	
	private String description;
	
	@Min(value=0, message="Item price must be greater than 0")
	private double price;
	
	@Min(value=0, message="Quantity price must be greater than 0")
	private int quantity;
	
	@NotNull
	@NotEmpty(message="Name name cannot be empty!")
	@Size(min=5, max=100, message="Name length must be between 5 to 100 characters!")
	private String material;
	
	@NotNull(message="Image name cannot be null")
	private String imgName;
	
	@NotNull(message="Category cannot be null")
	@ManyToOne
	@JoinColumn(name="category_id", nullable=false)
	private Category category;
	
	private int sold;
	
	public Category getCategory() {
		return category;
	}
	public void setCategory(Category category) {
		this.category = category;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public String getMaterial() {
		return material;
	}
	public void setMaterial(String material) {
		this.material = material;
	}
	public String getImgName() {
		return imgName;
	}
	public void setImgName(String imgName) {
		this.imgName = imgName;
	}
	public int getSold() {
		return sold;
	}
	public void setSold(int sold) {
		this.sold = sold;
	}
}
