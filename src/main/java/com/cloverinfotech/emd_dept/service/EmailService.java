package com.cloverinfotech.emd_dept.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendExcelReport(String toEmail, ByteArrayOutputStream excelStream) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(toEmail);
        helper.setSubject("Department & Employee Report");
        helper.setText("Please find attached the latest department and employee report.");

        helper.addAttachment("Department_Report.xlsx",
                new jakarta.mail.util.ByteArrayDataSource(
                        excelStream.toByteArray(),
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));

        mailSender.send(message);
    }
}