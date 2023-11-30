package e63c.Lai.GA.controller;

import java.util.List;

import javax.validation.Valid;

import e63c.Lai.GA.repository.CategoryRepository;
import e63c.Lai.GA.model.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
public class CategoryController {

	@Autowired
	private CategoryRepository categoryRepository;
	
	@GetMapping("/categories")
	public String viewCategory(Model model) {
		List<Category> categories = categoryRepository.findAll();
		model.addAttribute("listCategory", categories);
		return "view_categories";
	}
	
	@GetMapping("/categories/add")
	public String addCategory(Model model) {
		model.addAttribute("category", new Category());
		return "add_category";
	}
	
	@PostMapping("/categories/save")
	public String saveCategory(@Valid Category category, BindingResult bindingResult, RedirectAttributes redirectAttribute) {
		if (bindingResult.hasErrors()) {
			return "add_category";
		}
		categoryRepository.save(category);
		redirectAttribute.addFlashAttribute("success","Category added!");
		return "redirect:/categories";
	}
	
	@GetMapping("/categories/edit/{id}")
	public String editCategory(@PathVariable("id") Integer id, Model model) {
		Category category = categoryRepository.getById(id);
		/*  getById ==> Select * from category where id = id   */
		model.addAttribute("category", category);
		return "edit_category";
	}
	
	@PostMapping("/categories/edit/{id}")
	public String saveUpdatedCaetgory(@PathVariable("id") Integer id, Category category, RedirectAttributes redirectAttribute) {
		categoryRepository.save(category);
		redirectAttribute.addFlashAttribute("success","Category edited!");
		return "redirect:/categories";
	}
	
	@GetMapping("/categories/delete/{id}")
	public String editCategory(@PathVariable("id") Integer id, RedirectAttributes redirectAttribute) {
		categoryRepository.deleteById(id);
		redirectAttribute.addFlashAttribute("success","Category deleted!");
		return "redirect:/categories";
	}
}
