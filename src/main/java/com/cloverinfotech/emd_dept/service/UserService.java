package com.cloverinfotech.emd_dept.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.cloverinfotech.emd_dept.modal.LoginToken;
import com.cloverinfotech.emd_dept.modal.User;
import com.cloverinfotech.emd_dept.repository.LoginTokenRepository;
import com.cloverinfotech.emd_dept.repository.UserRepository;
import com.cloverinfotech.emd_dept.security.JwtSecurity;

@Service
public class UserService {
	
	private static final Logger log = LoggerFactory.getLogger(UserService.class);
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private JwtSecurity jwtSecurity; 
	
	@Autowired
	private LoginTokenRepository loginTokenRepository;
	
	final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	
	public User userRegistration(User user) {

	    try {
	        String username = user.getUsername();
	    	User userData = userRepo.findByUserName(username);

	        if (userData != null) {
	        	log.warn("Registration failed: username already exists - {}", username);
	            throw new RuntimeException("Username already exists");
	        }
	          
	        String encodePassword = passwordEncoder.encode(user.getPassword());
	        user.setPassword(encodePassword);
	        
	        User userReg = userRepo.save(user);
	        log.info("User stored successfully: {}", userReg.getUsername());

	        return userReg;

	    } catch (DataAccessException e) {
	        log.error("Database error while saving user", e);
	        throw new RuntimeException("Database operation failed", e);
	    }
	}
	
	public String login(String username, String password) {
		User userData = userRepo.findByUserName(username);
		
		if (userData == null || !passwordEncoder.matches(password, userData.getPassword())) {
			log.warn("UserName Password does not match - {}", username);
	        throw new RuntimeException("Invalid username or password");
	    }
		else {
			 
//			Token creation here
			String token = jwtSecurity.generateToken(username);

			LoginToken loginToken = new LoginToken();
			    
		    loginToken.setToken(token);
		    loginToken.setUsername(username);
		    loginToken.setActive(true);
		    loginTokenRepository.save(loginToken);
		    
		    log.warn("UserName Password match - {}", username);
		    return token;
		}
		
		
	}
	
	public void logout(String token) {
	    LoginToken loginToken = loginTokenRepository.findByToken(token).orElse(null);

	    if (loginToken != null) {
	        loginToken.setActive(false);
//	        loginTokenRepository.save(loginToken);
	        loginTokenRepository.delete(loginToken);
	    }
	}

}
