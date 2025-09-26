package com.csi_rscoe.csi_backend.Controllers.Public;


import com.csi_rscoe.csi_backend.DTOs.ContactMsg;
import com.csi_rscoe.csi_backend.Services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public")
public class ContactUs {

    @Autowired
   private EmailService emailService;
  @PostMapping("/contactus")
    String contactUs(@RequestBody ContactMsg contactMsg){


        emailService.sendEmail("bhavsarmayur664@gmail.com",
                "New Contact Us Message from "+contactMsg.getName()+"  email:"+contactMsg.getEmail(),
                contactMsg.getMsg());
      emailService.sendEmail("vaibhavvyavahare20@gmail.com",
              "New Contact Us Message from "+contactMsg.getName()+"  email:"+contactMsg.getEmail(),
              contactMsg.getMsg());
      emailService.sendEmail("kshitijthorat15@gmail.com",
              "New Contact Us Message from "+contactMsg.getName()+"  email:"+contactMsg.getEmail(),
              contactMsg.getMsg());
      emailService.sendEmail("csirscoestudentchapter@gmail.com",
              "New Contact Us Message from "+contactMsg.getName()+"  email:"+contactMsg.getEmail(),
              contactMsg.getMsg());



      //kshitijthorat15@gmail.com
// priya.sharma@student.rscoe.edu
      //arjun.patel@student.rscoe.edu
      //shivtejrakhunde@gmail.com
      //bhavsarmayur664@gmail.com
return "Message sent successfully";
    }



}
