package com.cloverinfotech.emd_dept.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.cloverinfotech.emd_dept.apiDto.ApiResponse;
import com.cloverinfotech.emd_dept.modal.Department;
import com.cloverinfotech.emd_dept.service.DepartmentService;
import com.cloverinfotech.emd_dept.validation.EmailValidator;
import com.cloverinfotech.emd_dept.validation.ValidationResult;


@RestController
@RequestMapping("/api/departments")
public class EmpoyeeController {
	
	private static final Logger log = LoggerFactory.getLogger(EmpoyeeController.class);
	
//	======================================== Dependency injection 

    @Autowired
    private DepartmentService departmentService;
    
    @Autowired
    private EmailValidator emailValidator;
    
//    ========================================== HTTP Request handler like (GET, POST, PUT, DELETE, UPDATE....) 
    
    @GetMapping("/")
    public ResponseEntity<ApiResponse<String>> check() {
    	
    	log.info("Executing controller: {}", getClass().getSimpleName());
    	return ResponseEntity.ok(ApiResponse.success("Hi! Welcome to my application..."));
    }

    @PostMapping("/addDeptEmployee")
    public ResponseEntity<ApiResponse<Department>> createDepartment(@RequestBody Department department) {
    	
    	log.info("Received request with data : {}", department);  	
        
        try {
            Department saved = departmentService.saveDepartmentWithEmployees(department);
            return ResponseEntity.ok(ApiResponse.success("Department created successfully", saved));
            
        } catch (Exception e) {
            log.error("Failed to create department: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.error(500, "Failed to create department: " + e.getMessage()));
        }
    }
    
    @GetMapping("/sendReport")
    public ResponseEntity<ApiResponse<String>> sendReport(@RequestParam String email) {
    	
    	log.info("Received request, send report to: {}", email); 	
    	
    	ValidationResult result = emailValidator.isValidGmailDomain(email);
    	
    	 try 
    	 { 
    		if (!result.isValid()) {
		        log.warn("Email validation failed: {}", result.getMessage());
		        return  ResponseEntity.badRequest().body(ApiResponse.error(400,"Invalid email: "+ result.getMessage()));
		    }
    		else {
    			
    			log.info("Email validation Pass: {}", result.getMessage());
    			
    			departmentService.generateAndEmailReport(email);
    			return  ResponseEntity.ok().body(ApiResponse.success("Report sent successfully to: "+ email));

    		}
        } catch (Exception e) {
        
        	log.error("Failed to send report to {}: {}", email, e.getMessage(), e);
           
            return  ResponseEntity.badRequest().body(ApiResponse.error(400,"Failed to send report: "+ result.getMessage()));
        }  

	}
}
