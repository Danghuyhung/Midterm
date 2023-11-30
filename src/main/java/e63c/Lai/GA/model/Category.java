package e63c.Lai.GA.model;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;



@Entity
public class Category {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	
	@NotNull
	@NotEmpty(message="Category name cannot be empty!")
	@Size(min=3, max=10, message="Category length must be between 3 to 10 characters!")
	@Column(unique = true)
	private String name;
	
	@OneToMany(mappedBy="category")
	private Set<Accessory> accessory;
	
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setId(int id) {
		this.id = id;
	}

}
