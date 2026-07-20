package com.cloverinfotech.emd_dept.service;

import com.cloverinfotech.emd_dept.modal.Department;
import com.cloverinfotech.emd_dept.repository.DepartmentRepository;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private ExcelService excelService;

    @Autowired
    private EmailService emailService;

    public Department saveDepartmentWithEmployees(Department department) {
        department.getEmployees().forEach(emp -> emp.setDepartment(department));
        return departmentRepository.save(department);
    }

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    public void generateAndEmailReport(String toEmail) throws IOException, MessagingException {
        List<Department> departments = departmentRepository.findAll();
        System.out.println("===========================================");
        System.out.println(departments);
        
        ByteArrayOutputStream excel = excelService.generateDepartmentExcel(departments);
        emailService.sendExcelReport(toEmail, excel);
    }
}