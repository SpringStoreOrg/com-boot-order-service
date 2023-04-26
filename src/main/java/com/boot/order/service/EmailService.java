package com.boot.order.service;

import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import com.boot.order.dto.UserDTO;
import com.boot.order.model.Email;
import com.boot.order.model.Order;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.velocity.VelocityEngineUtils;



@Slf4j
@Service
public class EmailService {

	private static final String ORDER_EMAIL_TEMPLATE = "/templates/order-email-template.vm";

	@Autowired
	JavaMailSender emailSender;

	@Autowired
	VelocityEngine velocityEngine;

	public void sendOrderEmail(Order order) throws MessagingException {

		Email email = new Email();


		Map<String, Object> model = new HashMap<>();
		model.put("uuid", order.getUuid());
		model.put("products", order.getEntries());
		model.put("total", order.getTotal());
		model.put("adress", order.getAddressLine1());
		email.setModel(model);

		MimeMessage mimeMessage = emailSender.createMimeMessage();

		MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

		mimeMessageHelper.setSubject("SpringStore confirmation Email");
		mimeMessageHelper.setFrom("noreply@springwebstore.com");
		mimeMessageHelper.setTo(order.getEmail());
		email.setEmailContent(getContentFromTemplate(email.getModel(), ORDER_EMAIL_TEMPLATE));
		mimeMessageHelper.setText(email.getEmailContent(), true);

		emailSender.send(mimeMessageHelper.getMimeMessage());
	}

	public String getContentFromTemplate(Map<String, Object> model, String templatePath) {
		StringBuffer content = new StringBuffer();
		try {
			content.append(VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, templatePath, model));
		} catch (Exception e) {
			log.info("Exception by getting content from the email template: {}", e.getStackTrace());
		}
		return content.toString();
	}

}
