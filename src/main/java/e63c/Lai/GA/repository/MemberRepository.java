package e63c.Lai.GA.repository;

import e63c.Lai.GA.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Integer> {
	
	public Member findByUsername(String username);
	
}