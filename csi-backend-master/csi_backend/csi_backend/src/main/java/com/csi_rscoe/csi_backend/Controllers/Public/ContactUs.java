package com.csi_rscoe.csi_backend.Controllers.Public;


import com.csi_rscoe.csi_backend.DTOs.ContactMsg;
import com.csi_rscoe.csi_backend.Services.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@RequestMapping("/api/public")
@CrossOrigin(origins = {"http://localhost:5173", "https://csirscoe.netlify.app", "https://csi-rscoe-nexus-main.netlify.app", "https://csi-rscoe.vercel.app"})
public class ContactUs {

    private static final Logger log = LoggerFactory.getLogger(ContactUs.class);

    @Autowired
    private EmailService emailService;

    @PostMapping("/contactus")
    public ResponseEntity<String> contactUs(@RequestBody ContactMsg contactMsg){
        try {
            emailService.sendEmailWithReplyTo("bhavsarmayur664@gmail.com",
                    "New Contact Us Message from "+contactMsg.getName()+"  email:"+contactMsg.getEmail(),
                    contactMsg.getMsg(),
                    contactMsg.getEmail());
            emailService.sendEmailWithReplyTo("vaibhavvyavahare20@gmail.com",
                    "New Contact Us Message from "+contactMsg.getName()+"  email:"+contactMsg.getEmail(),
                    contactMsg.getMsg(),
                    contactMsg.getEmail());
            emailService.sendEmailWithReplyTo("kshitijthorat15@gmail.com",
                    "New Contact Us Message from "+contactMsg.getName()+"  email:"+contactMsg.getEmail(),
                    contactMsg.getMsg(),
                    contactMsg.getEmail());
            emailService.sendEmailWithReplyTo("csirscoestudentchapter@gmail.com",
                    "New Contact Us Message from "+contactMsg.getName()+"  email:"+contactMsg.getEmail(),
                    contactMsg.getMsg(),
                    contactMsg.getEmail());
            return ResponseEntity.ok("Message sent successfully");
        } catch (Exception ex) {
            log.error("ContactUs email send failed for name={} email={} error={}", contactMsg.getName(), contactMsg.getEmail(), ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.ACCEPTED)
                    .body("Message received. Email delivery failed; we will review manually.");
        }
    }

}
