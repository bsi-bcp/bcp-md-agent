package com.bsi.md.agent.email;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class AgEmailConfig {

	@Bean
	public JavaMailSenderImpl getMailSender() {
		JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
		return javaMailSender;
	}
	
}