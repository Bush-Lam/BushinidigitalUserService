package dev.lam.user;

import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import com.auth0.AuthenticationController;
import com.auth0.IdentityVerificationException;
import com.auth0.Tokens;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import dev.lam.config.AuthConfig;

@Controller
public class AuthController {

    @Autowired
    private AuthenticationController authenticationController;

    @Autowired
    private AuthConfig config;
    
    @Autowired
    private UserAccountRepo uarepo;
    
    
//    @GetMapping(value = "/login")
//    protected void login(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        String redirectUri = config.getContextPath(request) + "/callback";
//        String authorizeUrl = authenticationController.buildAuthorizeUrl(request, response, redirectUri)
//            .withScope("openid email")
//            .build();
//        response.sendRedirect(authorizeUrl);
//    }
   
    @GetMapping(value="/callback")
    public void callback(HttpServletRequest request, HttpServletResponse response) throws IOException, IdentityVerificationException {
        Tokens tokens = authenticationController.handle(request, response);
        DecodedJWT jwt = JWT.decode(tokens.getIdToken());
        TestingAuthenticationToken authToken2 = new TestingAuthenticationToken(jwt.getSubject(), jwt.getToken());
        authToken2.setAuthenticated(true);
        
        SecurityContextHolder.getContext().setAuthentication(authToken2);
        response.sendRedirect("http://localhost:4200"); 
    }

//    public String getManagementApiToken() {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        JSONObject requestBody = new JSONObject();
//        requestBody.put("client_id", config.getManagementApiClientId());
//        requestBody.put("client_secret", config.getManagementApiClientSecret());
//        requestBody.put("audience", config.getAudience());
//        requestBody.put("grant_type", config.getGrantType()); 
//        
//        HttpEntity<String> request = new HttpEntity<String>(requestBody.toString(), headers);
//
//        RestTemplate restTemplate = new RestTemplate();
//        HashMap<String, String> result = restTemplate.postForObject(AUTH0_TOKEN_URL, request, HashMap.class);
//                
//        return result.get("access_token");
//    }
    
    public Optional<DecodedJWT> validate(HttpServletRequest request) {
    	
		String token = request.getHeader("Authorization");
    	
	    try {
	        Algorithm algorithm = Algorithm.HMAC256(config.getManagementApiClientSecret());
	        JWTVerifier verifier = JWT.require(algorithm)
	            .withIssuer("auth0")
	            .build(); //Reusable verifier instance
	        System.out.println(token);
	        DecodedJWT jwt = verifier.verify(token.substring(7));
	        return Optional.of(jwt);
	    } catch (JWTVerificationException exception){
	    	exception.printStackTrace();
	    	return Optional.empty();
	    }
	    
    }
    
}