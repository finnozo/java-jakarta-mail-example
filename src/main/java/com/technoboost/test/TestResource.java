package com.technoboost.test;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Properties;

@RestController
@RequestMapping("/api/v1/test")
public class TestResource {

    public static final String MAIL_SMTP_USERNAME = "AK**************";
    public static final String MAIL_SMTP_PASSWORD = "BHE******************************Cj";
    public static final Logger logger = LogManager.getLogger(TestResource.class);
    private static Properties props = null;
    private static Session session = null;

    public static Session getSession() {
        if (session == null) {
            session = Session.getInstance(getProps(), new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(MAIL_SMTP_USERNAME, MAIL_SMTP_PASSWORD);
                }
            });
        }
        return session;
    }

    public static void sendMail(String emailSubjectLine, String emailBodyContent,
                                String emailFrom, String emailTo) {
        logger.debug("Sending Email to " + emailTo);
        logger.debug("Email Body " + emailBodyContent);
        try {
            Message message = new MimeMessage(getSession());
            message.setFrom(new InternetAddress(emailFrom));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailTo));
            message.setRecipients(Message.RecipientType.BCC, InternetAddress.parse("puneet@truevisual.io"));
            message.setSubject(emailSubjectLine);
            message.setContent(emailBodyContent, "text/html");
            Transport.send(message);
            logger.info("Email Sent Successfully");
        } catch (MessagingException e) {
            logger.info("Email Sending Failure: {}" , e.getMessage());
            throw new IllegalStateException(e.getMessage());
        }
    }

    public static Properties getProps() {
        if (props == null) {
            props = System.getProperties();
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.port", 587);
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.host", "email-smtp.ap-south-1.amazonaws.com");
        }
        return props;
    }

    @GetMapping("/mail")
    public String testMail() {
        sendMail("Hello Subject",
                "Dear Sir/Ma'am<br>My is <b>Sushil Dangi</b>",
                "sushil@truevisual.io",
                "sushil@truevisual.io");
        return "Mail Send successfully";
    }
}
