package com.cloverinfotech.emd_dept.service;

import com.cloverinfotech.emd_dept.modal.Department;
import com.cloverinfotech.emd_dept.modal.Employee;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ExcelService {

	private static final Logger log = LoggerFactory.getLogger(ExcelService.class);
	
    public ByteArrayOutputStream generateDepartmentExcel(List<Department> departments) throws IOException {
    	
    	log.info("Reviced Department sheet data");
    	
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) 
        {

            Sheet sheet = workbook.createSheet("Departments");

            // Header cell style set
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            
            // Header row column names set 
            Row header = sheet.createRow(0);
            String[] columns = {"Dept ID", "Dept Name", "Emp ID", "Emp Name", "Salary", "Shift"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            // Data rows
            int rowIdx = 1;
            for (Department dept : departments) {
                List<Employee> employees = dept.getEmployees();

                if (employees == null || employees.isEmpty()) {
                    Row row = sheet.createRow(rowIdx++);
                    row.createCell(0).setCellValue(dept.getId());
                    row.createCell(1).setCellValue(dept.getName());
                    // emp columns left blank
                } else {
                    for (Employee emp : employees) {
                        Row row = sheet.createRow(rowIdx++);
                        row.createCell(0).setCellValue(dept.getId());
                        row.createCell(1).setCellValue(dept.getName());
                        row.createCell(2).setCellValue(emp.getId());
                        row.createCell(3).setCellValue(emp.getEmp_name());
                        row.createCell(4).setCellValue(emp.getSalary());
                        row.createCell(5).setCellValue(emp.getShift());
                    }
                }
            }

            // Auto-size columns for readability
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // Validation Confirm at least one data row was actually written (besides header)
            if (sheet.getLastRowNum() < 1) {
                log.warn("Service: {} Excel sheet has no data rows, only header was written", getClass());
                throw new IllegalStateException("Generated Excel report contains no data rows");
            }
            else {
            	
            	workbook.write(out);
            }

            log.info("Service: {} Excel workbook created with {} Size : {} ", getClass(), sheet.getSheetName(), out.size());
            
            return out;
        }
    }
}
