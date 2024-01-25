package com.boot.order.service;

import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import com.boot.order.model.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.velocity.VelocityEngineUtils;



@Slf4j
@Service
public class EmailService {

	private static final String ORDER_EMAIL_TEMPLATE = "/templates/order-email-template.vm";
	private static final String ADDRESS_TEMPLATE = "%s<br>%s<br>Strada %s, %s<br>%s, %s, %s";

	@Autowired
	JavaMailSender emailSender;

	@Autowired
	VelocityEngine velocityEngine;

	@Value("${from.email}")
	private String fromEmail;

	@Value("${owner.email}")
	private String ownerEmail;

	public void sendOrderEmail(Order order) throws MessagingException {

		Email email = new Email();


		Map<String, Object> model = new HashMap<>();
		model.put("uuid", order.getUuid());
		model.put("products", order.getEntries());
		model.put("total", order.getTotal());
		model.put("address", getAddressString(order));
		email.setModel(model);

		MimeMessage mimeMessage = emailSender.createMimeMessage();

		MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

		mimeMessageHelper.setSubject("FractalWoodStories confirmare comanda");
		mimeMessageHelper.setFrom(fromEmail);
		mimeMessageHelper.setBcc(ownerEmail);
		mimeMessageHelper.setTo(order.getShippingAddress().getEmail());
		email.setEmailContent(getContentFromTemplate(email.getModel(), ORDER_EMAIL_TEMPLATE));
		mimeMessageHelper.setText(email.getEmailContent(), true);

		emailSender.send(mimeMessageHelper.getMimeMessage());
	}

	public String getContentFromTemplate(Map<String, Object> model, String templatePath) {
		StringBuffer content = new StringBuffer();
		try {
			content.append(VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, templatePath, model));
		} catch (Exception e) {
			log.info("Exception by getting content from the email template", e);
		}
		return content.toString();
	}

	private String getAddressString(Order order) {
		OrderAddress address = order.getShippingAddress();
		return String.format(ADDRESS_TEMPLATE, address.getRecipient(),
				address.getPhoneNumber(), address.getStreet(), address.getPostalCode(),
				address.getCity(), address.getCounty(), address.getCountry());
	}
}
