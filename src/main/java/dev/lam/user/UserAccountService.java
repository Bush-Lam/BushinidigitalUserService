package dev.lam.user;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
public class UserAccountService {
	
	private UserAccountRepo uarepo;
	
	UserAccountService(UserAccountRepo uarepo){
		this.uarepo = uarepo;
	}
	
	public Set<UserAccount> getUsers(){
		return this.uarepo.findAll().stream().collect(Collectors.toSet());
	}
	
	public Optional<UserAccount> getUserById(int id) {
		return this.uarepo.findById(id);
	}
	
	public Optional<UserAccount> getUserByEmail(String email){
		return Optional.of(this.uarepo.findByEmail(email));
	}
	
	public UserAccount saveUser(UserAccount useracc) {
		return this.uarepo.save(useracc);
	}
		
	public Boolean deleteUser(UserAccount useracc) {
		try {
			this.uarepo.delete(useracc);
			return true;
		} catch(Exception e) {
			return false;	
		}
	}
	
	public UserAccount updateUser(UserAccount useracc) {
		return this.uarepo.save(useracc);
	}
}
