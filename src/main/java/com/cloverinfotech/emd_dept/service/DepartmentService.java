package com.cloverinfotech.emd_dept.service;

import com.cloverinfotech.emd_dept.modal.Department;
import com.cloverinfotech.emd_dept.repository.DepartmentRepository;
import jakarta.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
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
            log.info("Service : {} Data stored in database successfully: {}", DepartmentService.class.getName(), deptData);
        } else {
            log.warn("Service : {} Failed to store department data: {}", DepartmentService.class.getName(), department);
        }

        return deptData;
        
    }

    public List<Department> getAllDepartments() {
    	log.info("Service : {} Fetch all data from Department ", DepartmentService.class.getName());
        return departmentRepository.findAll();
    }
    
    @Async
    public void generateAndEmailReport(String toEmail) throws IOException, MessagingException {
    	log.info("Service :{} Reviced email Id : {}", DepartmentService.class.getName(), toEmail);
    	
//        List<Department> departments = departmentRepository.findAll(); this function tacking time to execute (N+1) issue (Hibernate call sub internal queries) 
       
    	List<Department> departments = departmentRepository.findAllWithEmployees();
    	ByteArrayOutputStream excel = excelService.generateDepartmentExcel(departments);
        emailService.sendExcelReport(toEmail, excel);
    }
}