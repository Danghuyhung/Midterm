package e63c.Lai.GA.repository;

import java.util.List;

import e63c.Lai.GA.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
	public List<CartItem> findByMemberId(int id);
	public CartItem findByMemberIdAndAccessoryId(int memberId, int accessoryId);
}
