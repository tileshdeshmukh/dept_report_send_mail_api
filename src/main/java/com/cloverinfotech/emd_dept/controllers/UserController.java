package com.cloverinfotech.emd_dept.controllers;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cloverinfotech.emd_dept.apiDto.ApiResponse;
import com.cloverinfotech.emd_dept.modal.User;
import com.cloverinfotech.emd_dept.service.UserService;

@RestController
@RequestMapping("/api/user")
public class UserController {
	
	private static final Logger log = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	private UserService userService;
	
	@PostMapping("/userRegistor")
	public ResponseEntity<ApiResponse<User>> userRegistration(@RequestBody User user){
		
		log.info("Exicute User Controler : {}", getClass().getSimpleName());
		try {
			User registeredUser = userService.userRegistration(user);
			
			if(registeredUser == null || registeredUser.getId() == null)
			{
				log.error("User registration failed: no data returned after save");
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body(ApiResponse.error(500, "User registration failed"));
			}
			
			log.info("User registered successfully: {}", registeredUser.getUsername());
			
			return ResponseEntity.ok(ApiResponse.success("User registered successfully", registeredUser));
		}
		catch (RuntimeException e) {
            log.warn("User registration failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.error(409, e.getMessage()));
        }
	}
	
	@PostMapping("/login")
	public ResponseEntity<ApiResponse<String>> login(@RequestBody Map<String, String> request) {
	    try {
	        String token = userService.login(request.get("username"), request.get("password"));
	        return ResponseEntity.ok(ApiResponse.success("Login successful", token));
	    } catch (RuntimeException e) {
	        return ResponseEntity.status(401).body(ApiResponse.error(401, e.getMessage()));
	    }
	}

	@PostMapping("/logout")
	public ResponseEntity<ApiResponse<String>> logout(@RequestHeader("Authorization") String token) {
	    userService.logout(token);
	    return ResponseEntity.ok(ApiResponse.success("Logged out successfully"));
	}

}
