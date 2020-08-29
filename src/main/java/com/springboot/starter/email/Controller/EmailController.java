package com.springboot.starter.email.Controller;

import com.springboot.starter.email.interfaces.EmailService;
import com.springboot.starter.email.model.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@RestController
@RequestMapping("/email/")
public class EmailController {
    @Value("${files.email-file-path:#{null}}")
    private String path;

    @Autowired
    private EmailService emailService;
    private Email prepareMessage(InternetAddress[] EmailList,Boolean attachmentIncluded)
    {
        Email email = new Email();
        email.setSubject("Simple e-mail test");
        email.setRecipients(EmailList);
        email.setHtml(false);
        email.setBody("This is simple e-mail message.");

        if (attachmentIncluded) {
            email.setAttachmentPath("/attachment/email.jpg");
        }
        return email;
    }
    @RequestMapping(value = "/send/fromfile")
    public String SendFromFile() throws FileNotFoundException, IOException, AddressException, MessagingException {
        File file=new File(path);
        Scanner scanner=new Scanner(file);
        List<String> emailStringList=new ArrayList<>();

        int i=-1;
        while (scanner.hasNextLine()) {
            String data = scanner.nextLine();
            data=data.trim();
            emailStringList.add(data);
            i++;
        }
        InternetAddress[] internetAddress=new InternetAddress[i];
        String maillist=emailStringList.toString().replace('[',' ');
        maillist=maillist.replace(']',' ');
        maillist=maillist.trim();
        internetAddress=InternetAddress.parse(maillist);
        Email email = prepareMessage(internetAddress,false);

        Boolean isSent = emailService.sendSimpleMessage(email);
        scanner.close();
        return "";
    }
    @RequestMapping(value = "/send/fromdb")
    public String getTodayMap() throws Exception {
        return "";
    }
}
