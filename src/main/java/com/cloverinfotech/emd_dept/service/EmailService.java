package com.cloverinfotech.emd_dept.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


import java.io.ByteArrayOutputStream;

@Service
public class EmailService {
	
	private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;
    
    @Async
    public void sendExcelReport(String toEmail, ByteArrayOutputStream excelStream) throws MessagingException {
    	
    	log.info("Service :{} Reviced email Id : {}", getClass(), toEmail);
    	
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(toEmail);
        helper.setSubject("Department & Employee Report");
        helper.setText("Please find attached the latest department and employee report.");
        
        byte[] excelBytes = excelStream.toByteArray();

        ByteArrayDataSource excelFile = new ByteArrayDataSource(excelBytes,
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        helper.addAttachment("Department_Report.xlsx", excelFile);

        mailSender.send(message);
        
        log.info("Email send successfully on this email Id : {}", toEmail);
    }
}