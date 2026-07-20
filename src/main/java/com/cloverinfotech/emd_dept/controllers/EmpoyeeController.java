package com.cloverinfotech.emd_dept.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cloverinfotech.emd_dept.modal.Department;
import com.cloverinfotech.emd_dept.service.DepartmentService;


@RestController
@RequestMapping("/api/departments")
public class EmpoyeeController {
	
	private static final Logger log = LoggerFactory.getLogger(EmpoyeeController.class);

    @Autowired
    private DepartmentService departmentService;
    
    @GetMapping("/")
    public String check() {
    	log.info("Executing controller: {}", EmpoyeeController.class.getName());
    	return "Hi! Welcome to my application... ";
    }

    @PostMapping("/addDeptEmployee")
    public Department createDepartment(@RequestBody Department department) {
    	
    	log.info("Received request: {} with data : {}", EmpoyeeController.class.getName(), department);
    	
        return departmentService.saveDepartmentWithEmployees(department);
    }
    
    @GetMapping("/sendReport")
    public String sendReport(@RequestParam String email) {
    	log.info("Received request : {} to send report to: {}", EmpoyeeController.class.getName(), email);
        
    	 String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    	 
    	 if (email == null || !email.matches(emailRegex)) {
    	     log.warn("Invalid email format: {}", email);
    	     return "Invalid email address: " + email;
    	 }
    	 else {
	    	 try {
	            
	        	departmentService.generateAndEmailReport(email);
	            return "Report sent successfully to " + email;
	        
	        } catch (Exception e) {
	        
	        	log.error("Failed to send report to {}: {}", email, e.getMessage(), e);
	            return "Failed to send report: " + e.getMessage();
	        }
    	 }
    }
}
