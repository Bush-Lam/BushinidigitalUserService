package dev.lam.user;

import java.util.Set;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import feign.RequestLine;

@FeignClient(name="userservice")
public interface UserAccountServiceProxy {
	
	@RequestMapping(method = RequestMethod.GET, value = "/users", produces = "application/json")
	   public Set<UserAccount> findAll();
	   	   
	@RequestMapping(method = RequestMethod.GET, value = "/users/{id}", produces = "application/json")
	   public UserAccount findOne();
	
	@RequestMapping(method = RequestMethod.POST, value = "/userlogin", produces = "application/json")
	   public UserAccount userLogin();
	
	@RequestMapping(method = RequestMethod.POST, value = "/assignuserrole/{id}", produces = "application/json")
	   public UserAccount assignUserRole();
	
	
}
