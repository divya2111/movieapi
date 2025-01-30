package com.Movieapi.Controller;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Movieapi.Dto.MailBody;
import com.Movieapi.Service.EmailService;
import com.Movieapi.auth.Model.ForgotPassword;
import com.Movieapi.auth.Model.User;
import com.Movieapi.auth.Repo.ForgotPasswordRepo;
import com.Movieapi.auth.Repo.UserRepo;
import com.Movieapi.auth.Utils.ChangePassword;

@RestController
@RequestMapping("/forgotPassword")
public class ForgotPasswordController {
	
	private final UserRepo userRepo;
	private final EmailService emailService;
	private final ForgotPasswordRepo forgotPasswordRepo;
	private final PasswordEncoder passwordEncoder;
	
	public ForgotPasswordController(UserRepo userRepo, EmailService emailService, ForgotPasswordRepo forgotPasswordRepo,
			PasswordEncoder passwordEncoder) {
		super();
		this.userRepo = userRepo;
		this.emailService = emailService;
		this.forgotPasswordRepo = forgotPasswordRepo;
		this.passwordEncoder = passwordEncoder;
	}
	
	 @PostMapping("/verifyMail/{email}")
	    public ResponseEntity<String> verifyEmail(@PathVariable("email") String email) {
	        User user = userRepo.findByEmail(email)
	                .orElseThrow(() -> new UsernameNotFoundException("Please provide an valid email!" + email));

	        int otp = otpGenerator();
	        MailBody mailBody = MailBody.builder()
	                .to(email)
	                .text("This is the OTP for your Forgot Password request : " + otp)
	                .subject("OTP for Forgot Password request")
	                .build();

	        ForgotPassword fp = ForgotPassword.builder()
	                .otp(otp)
	                .expirationTime(new Date(System.currentTimeMillis() + 20 * 100000))
	                .user(user)
	                .build();

	        emailService.sendSimpleMessage(mailBody);
	        forgotPasswordRepo.save(fp);

	        return ResponseEntity.ok("Email sent for verification!");
	    }
	 
	 @PostMapping("/verifyOtp/{otp}/{email}")
	    public ResponseEntity<String> verifyOtp(@PathVariable("otp") Integer otp, @PathVariable("email") String email) {
	        User user = userRepo.findByEmail(email)
	                .orElseThrow(() -> new UsernameNotFoundException("Please provide an valid email!"));

	        ForgotPassword fp = forgotPasswordRepo.findByOtpAndUser(otp, user)
	                .orElseThrow(() -> new RuntimeException("Invalid OTP for email: " + email));

	        if (fp.getExpirationTime().before(Date.from(Instant.now()))) {
	            forgotPasswordRepo.deleteById(fp.getFpid());
	            return new ResponseEntity<>("OTP has expired!", HttpStatus.EXPECTATION_FAILED);
	        }

	        return ResponseEntity.ok("OTP verified!");
	    }
	 @PostMapping("/changePassword/{email}")
	    public ResponseEntity<String> changePasswordHandler(@RequestBody ChangePassword changePassword,
	                                                        @PathVariable("email") String email) {
	        if (!Objects.equals(changePassword.password(), changePassword.repeatPassword())) {
	            return new ResponseEntity<>("Please enter the password again!", HttpStatus.EXPECTATION_FAILED);
	        }

	        String encodedPassword = passwordEncoder.encode(changePassword.password());
	        userRepo.updatePassword(email, encodedPassword);

	        return ResponseEntity.ok("Password has been changed!");
	    }

	 private Integer otpGenerator() {
	        Random random = new Random();
	        return  1000000 + random.nextInt(999999);
	    }

}
