package e63c.Lai.GA.repository;

import java.util.List;

import e63c.Lai.GA.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;


public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
	public List<OrderItem> findByMemberId(int id);
}
