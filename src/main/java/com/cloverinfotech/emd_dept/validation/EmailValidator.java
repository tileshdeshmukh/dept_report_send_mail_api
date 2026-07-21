package com.cloverinfotech.emd_dept.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.cloverinfotech.emd_dept.controllers.EmpoyeeController;

@Component
public class EmailValidator {

	private static final Logger log = LoggerFactory.getLogger(EmpoyeeController.class);
	
	public ValidationResult isValidGmailDomain(String email) {
	   	 
	   	if (email == null) 
	   	{   		 
	   	    log.warn("Invalid email: {}", email);
	   	    return new ValidationResult(false, "email id not found");	
		}
	    if (!email.contains("@")) {
	    	log.warn("Email is missing '@' symbol: {}", email);
	        return new ValidationResult(false, "Email is missing '@' symbol");
	    }

	    String[] parts = email.split("@");

	    if (parts.length != 2) {
	    	log.warn("Invalid email format: multiple or misplaced '@' symbols : {}", email);
	        return new ValidationResult(false, "Invalid email format: multiple or misplaced '@' symbols");
	    }

	    String domain = parts[1];

	    if (!domain.equalsIgnoreCase("gmail.com")) {
	    	log.warn("Only gmail.com domain is allowed, found: {}", email);
	        return new ValidationResult(false, "Only gmail.com domain is allowed, found: " + domain);
	    }

	    return new ValidationResult(true, "Valid Gmail address");
	}
	

}
