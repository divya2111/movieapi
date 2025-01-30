package com.Movieapi.Service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.Movieapi.Dto.MailBody;

@Service
public class EmailService {
	
	private final JavaMailSender javaMailSender;

	public EmailService(JavaMailSender javaMailSender) {
		super();
		this.javaMailSender = javaMailSender;
	}
	
	public void sendSimpleMessage(MailBody mailbody) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(mailbody.to());
		message.setFrom("divyaparuchuri2111@gamil.com");
		message.setSubject(mailbody.subject());
		message.setText(mailbody.text());
		
		javaMailSender.send(message);
		
	}
	

}
