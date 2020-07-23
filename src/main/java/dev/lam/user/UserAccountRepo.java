package dev.lam.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAccountRepo extends JpaRepository<UserAccount, Integer> {
	public UserAccount findByUsername(String username);
	public UserAccount findByEmail(String email);
}
