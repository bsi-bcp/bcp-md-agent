package com.bsi.md.agent.email;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Properties;

@RestController
@RequestMapping("/api/email")
@Api("发送Email接口")
@Slf4j
public class DemoEmailController {

	@Autowired
    private AgEmailService agEmailService;

	@Autowired
	private JavaMailSenderImpl javaMailSenderImpl;
	
	@GetMapping("send")
	public void testEmailConfig(){
	    AgEmailEntity email = new AgEmailEntity();
	    email.setReceiver("java_fish@126.com");
	    email.setContent("welcome Email Sender");
	    email.setSubject("Spring Boot Java EE Developer");
	    try {
			agEmailService.sendEmail(email);
		}catch (Exception e){
	    	log.error(e.getMessage());
		}

		log.info("host:{}",javaMailSenderImpl.getHost());
		javaMailSenderImpl.setHost("smtp.exmail.qq.com");
		javaMailSenderImpl.setUsername("systeminfo@bizsoftinfo.com");
		javaMailSenderImpl.setPassword("Bcp@9527");
		Properties properties = new Properties();
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.timeout", "60000");
		properties.put("bcp.mail.from", "systeminfo@bizsoftinfo.com");
		javaMailSenderImpl.setJavaMailProperties(properties);
	    log.info("successful to send message!");
	}
}