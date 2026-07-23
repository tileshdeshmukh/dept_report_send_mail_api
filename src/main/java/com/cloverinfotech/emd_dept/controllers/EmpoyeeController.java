package com.cloverinfotech.emd_dept.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.cloverinfotech.emd_dept.apiDto.ApiResponse;
import com.cloverinfotech.emd_dept.modal.Department;
import com.cloverinfotech.emd_dept.repository.DepartmentRepository;
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
    
    @Autowired
    private DepartmentRepository departmentRepository;
    
//    ========================================== HTTP Request handler like (GET, POST, PUT, DELETE, UPDATE....) 
    
    @GetMapping("/")
    public ResponseEntity<ApiResponse<String>> check() {
    	
    	log.info("Executing controller: {}", getClass().getSimpleName());
    	return ResponseEntity.ok(ApiResponse.success("Hi! Welcome to my application..."));
    }
    
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<Department>>> getAllDepartments() {
        List<Department> departments = departmentRepository.findAll();
        return ResponseEntity.ok(ApiResponse.success("Departments fetched successfully", departments));
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
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Department>> getDepartment(@PathVariable Long id) {
        try {
            Department dept = departmentService.getDepartmentById(id);
            return ResponseEntity.ok(ApiResponse.success("Department fetched successfully", dept));
            
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(ApiResponse.error(404, e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Department>> updateDepartment(@PathVariable long id, @RequestBody Department department){
    	 try {
    		 
    	      Department updated = departmentService.updateDepartmentWithEmployees(id, department);
    	      return ResponseEntity.ok(ApiResponse.success("Department updated successfully", updated));
    	      
    	 } catch (RuntimeException e) {
    	      return ResponseEntity.status(404).body(ApiResponse.error(404, e.getMessage()));
    	 }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteDepartment(@PathVariable Long id) {
        try {
            departmentService.deleteDepartment(id);
            return ResponseEntity.ok(ApiResponse.success("Department deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(ApiResponse.error(404, e.getMessage()));
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
