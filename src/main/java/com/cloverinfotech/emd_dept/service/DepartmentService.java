package com.cloverinfotech.emd_dept.service;

import com.cloverinfotech.emd_dept.modal.Department;
import com.cloverinfotech.emd_dept.repository.DepartmentRepository;
import jakarta.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class DepartmentService {
	
	private static final Logger log = LoggerFactory.getLogger(DepartmentService.class);

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private ExcelService excelService;

    @Autowired
    private EmailService emailService;

    public Department saveDepartmentWithEmployees(Department department) {
        department.getEmployees().forEach(emp -> emp.setDepartment(department));
        
        Department deptData = departmentRepository.save(department);

        if (deptData != null) {
            log.info("Data stored in database successfully: {}", deptData);
        } else {
            log.warn("Failed to store department data: {}", department);
        }

        return deptData;
        
    }

    public List<Department> getAllDepartments() {
    	log.info("Fetch all data from Department ");
        return departmentRepository.findAll();
    }
    
    public void generateAndEmailReport(String toEmail) throws IOException, MessagingException {
    	log.info("Reviced email Id : {}", toEmail);
    	
//      List<Department> departments = departmentRepository.findAll(); 
//    	this function tacking time to execute (N+1) issue (Hibernate call sub internal queries) 
       
    	List<Department> departments = departmentRepository.findAllWithEmployees();
    	
    	// Validation Check input data is not null/empty before processing  	
    	if (departments == null || departments.isEmpty()) {
            log.warn("No department data available to generate Excel report");
            throw new IllegalArgumentException("Cannot generate Excel report: department list is empty");
            
        }else {
        	
        	ByteArrayOutputStream excel = excelService.generateDepartmentExcel(departments);
        	
            // Validation Confirm the output stream actually has bytes written
        	if(excel.size() == 0) {
        		 log.error("Excel generation failed: output stream is empty");
                 throw new IOException("Generated Excel file is empty");
        	
        	}else {
        		emailService.sendExcelReport(toEmail, excel);
        	}
        	
        }
    	
        
    }
}