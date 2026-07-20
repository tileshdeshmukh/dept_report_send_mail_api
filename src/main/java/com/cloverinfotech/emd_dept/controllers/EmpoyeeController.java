package com.cloverinfotech.emd_dept.controllers;

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

    @Autowired
    private DepartmentService departmentService;
    
    @GetMapping("/")
    public String check() {
    	return "Hi! Welcome to my application... ";
    }

    @PostMapping("/addDeptEmployee")
    public Department createDepartment(@RequestBody Department department) {
        return departmentService.saveDepartmentWithEmployees(department);
    }
    
    @GetMapping("/sendReport")
    public String sendReport(@RequestParam String email) {
        try {
            departmentService.generateAndEmailReport(email);
            return "Report sent successfully to " + email;
        } catch (Exception e) {
            return "Failed to send report: " + e.getMessage();
        }
    }
}
