package com.springboot.starter.email.services;

import com.springboot.starter.email.interfaces.EmailService;
import com.springboot.starter.email.model.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Description;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.stream.Collectors;

@Description(value = "Service layer that implements method for sending e-mails.")
@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);
    @Value("${email.username:#{null}}")
    private String from;
    @Autowired
    private JavaMailSender emailSender;

    @Override
    public Boolean sendSimpleMessage(Email email)throws MessagingException
    {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper mailMessage = new MimeMessageHelper(message, true);
        mailMessage.setTo(email.getRecipients());

        return getaBoolean(email, message);
    }

    private Boolean getaBoolean(Email email, MimeMessage mailMessage)throws MessagingException {
        mailMessage.setSubject(email.getSubject());
        mailMessage.setText(email.getBody());
        mailMessage.setFrom(from);
        boolean isSent = false;
        try
        {

            emailSender.send(mailMessage);
            isSent = true;
        }
        catch (Exception e) {
            LOGGER.error("Sending e-mail error: {}", e.getMessage());
        }
        return isSent;
    }

    @Override
    public Boolean sendMessageWithCC(Email email)
    {
//        SimpleMailMessage mailMessage = new SimpleMailMessage();
//        mailMessage.setTo(String.join(",", email.getRecipients()));
//        mailMessage.setCc(String.join(",", email.getCcList()));
//        return getaBoolean(email, mailMessage);
        return true;
    }

    @Override
    public Boolean sendMessageWithAttachment(Email email) throws IOException, MessagingException
    {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);

        //messageHelper.setTo(String.join(",", email.getRecipients()));
        messageHelper.setSubject(email.getSubject());
        messageHelper.setText(email.getBody());

        Resource resource = new ClassPathResource(email.getAttachmentPath());
        messageHelper.addAttachment("attachment", resource.getFile());

        boolean isSent = false;
        try
        {
            emailSender.send(message);
            isSent = true;
        }
        catch (Exception e) {
            LOGGER.error("Sending e-mail with attachment error: {}", e.getMessage());
        }
        return isSent;
    }
}
