package e63c.Lai.GA.repository;

import java.util.List;

import e63c.Lai.GA.model.Accessory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AccessoryRepository extends JpaRepository<Accessory, Integer>{

	@Query("SELECT a.name, a.sold FROM Accessory a")
    List<Object[]> findAccessorySoldCounts();
}
