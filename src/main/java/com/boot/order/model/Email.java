package com.boot.order.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@Accessors(chain = true)
public class Email implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private String emailFrom;

	private String emailTo;

	private String emailCc;

	private String emailBcc;

	private String emailSubject;

	private String emailContent;

	private String contentType;

	private List<Object> attachments;

	private Map<String, Object> model;

	public Email() {
		contentType = "text/plain";
	}
}
