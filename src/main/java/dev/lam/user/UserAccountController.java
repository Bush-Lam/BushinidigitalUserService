package dev.lam.user;

import java.io.IOException;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.AuthenticationController;
import com.auth0.IdentityVerificationException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;

import dev.lam.config.AuthConfig;
import dev.lam.user.ApiService;

@RestController
@CrossOrigin()
public class UserAccountController {

	private UserAccountService uas;
	private AuthConfig config;
    private ApiService apiService;
    private AuthController authcontroller;
    
	UserAccountController(UserAccountService uas, AuthConfig config, ApiService apiservice, AuthController authcontroller){
		this.uas = uas;
		this.config = config;
		this.apiService = apiservice;
		this.authcontroller = authcontroller;
	}
	
	//admin 
	
    @GetMapping(value="/users")
    @ResponseBody
    public ResponseEntity<String> users(HttpServletRequest request, HttpServletResponse response) throws IOException, IdentityVerificationException {
        ResponseEntity<String> result = apiService.getCall(config.getUsersUrl(), request);
        return result;
    }

    @GetMapping(value = "/userbyemail")
    @ResponseBody
    public ResponseEntity<String> userByEmail(HttpServletRequest httprequest, HttpServletResponse response, @RequestParam String email) {
        ResponseEntity<String> result = apiService.getCall(config.getUsersByEmailUrl()+email, httprequest);
        return result;
    }
    
    @PostMapping(value = "/createuser")
    public ResponseEntity<String> adminCreateUser(@RequestBody UserAccount useracc, HttpServletRequest httprequest, HttpServletResponse response) {
    	try {
    	    this.uas.saveUser(useracc);
    		
            JSONObject request = new JSONObject();
            request.put("email", useracc.getEmail());
            request.put("given_name", useracc.getFirst_name());
            request.put("family_name", useracc.getLast_name());
            request.put("connection", "Username-Password-Authentication");
            request.put("password", useracc.getPassword());
            ResponseEntity<String> result = apiService.postCall(config.getUsersUrl(), httprequest, request.toString());
            return result;	
    	} catch (Exception e) {
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    	}
    }
    
    @DeleteMapping(value = "/deleteuser/{id}")
    public ResponseEntity<String> adminDeleteUser(@RequestBody UserAccount useracc, @PathVariable String id, HttpServletRequest request, HttpServletResponse response){
    	try {
    		if (this.uas.deleteUser(useracc) == false)
    			throw new Exception();
    			
            ResponseEntity<String> result = apiService.getDeleteCall(config.getUsersUrl() + id, request);
            return result;
    	} catch (Exception e) {
    		e.printStackTrace();
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    	}
    }
    
    @PostMapping(value = "/assignuserrole/{id}")
    public ResponseEntity<String> assignUserRole(HttpServletRequest httprequest, HttpServletResponse response, @PathVariable String id){
    	JSONObject requestjson = new JSONObject();
    	requestjson.put("roles", "user");
    	
        ResponseEntity<String> result = apiService.postCall(config.getUsersUrl() + id + "/roles", httprequest, requestjson.toString());
        return result;
    }
    
    //users 
    
    @PostMapping(value = "/signup")
    public ResponseEntity<String> createUser(@RequestBody UserAccount useracc, HttpServletResponse response) {
        JSONObject request = new JSONObject();
        request.put("client_id", config.getClientId());
        request.put("email", useracc.getEmail());
        request.put("password", useracc.getPassword());
        request.put("connection", "Username-Password-Authentication");
        
        try {
        	//implement - first see if there is a connnection for both before making these transactions
            this.uas.saveUser(useracc);
            ResponseEntity<String> result = apiService.postCallWithoutToken(config.getSignupUrl(), request.toString());
        	return result;
        } catch (Exception e) {
        	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }       
    }
    
    @PostMapping(value = "/userlogin")
    public ResponseEntity<String> login(@RequestParam String email, @RequestParam String password, HttpServletResponse response){
    	try {
        	JSONObject requestjson = new JSONObject();        	
        	UserAccount user = this.uas.getUserByEmail(email).get();
        	
        	if (user.getRoles().equals("user")) {
            	requestjson.put("grant_type", "password");
            	requestjson.put("username", email);
            	requestjson.put("password", password);
            	requestjson.put("audience", config.getAudience());
            	requestjson.put("client_id", config.getClientId());
            	requestjson.put("client_secret", config.getClientSecret());

        	} else if (user.getRoles().equals("admin")) {
            	requestjson.put("grant_type", "client_credentials");
            	requestjson.put("username", email);
            	requestjson.put("password", password);
            	requestjson.put("audience", config.getAudience());
            	requestjson.put("client_id", config.getManagementApiClientId());
            	requestjson.put("client_secret", config.getManagementApiClientSecret());
        	} else if (user.getRoles().equals("moderator")) {
        		// need to implement still
        	}
        	       	
        	ResponseEntity<String> result = apiService.postCallWithoutToken(config.getLoginUrl(), requestjson.toString());
        	
            return result;	
    	} catch (Exception e) {
    		e.printStackTrace();
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    	}
    }
    
    @GetMapping("users/{email}")
    public ResponseEntity<UserAccount> getUserByEmail(@PathVariable String email, HttpServletRequest request){
    	try {
    		this.authcontroller.validate(request).orElseThrow(() -> new Exception());

    		return ResponseEntity.ok(this.uas.getUserByEmail(email).get());
    	} catch(Exception e) {
    		e.printStackTrace();
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    	}
    }
    
}
