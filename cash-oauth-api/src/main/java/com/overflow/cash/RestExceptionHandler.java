package com.overflow.cash;

import java.util.ArrayList;
import java.util.List;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.slerp.core.CoreException;
import org.slerp.core.Domain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

@RestControllerAdvice
public class RestExceptionHandler {
	@Autowired
	MessageSource messageSource;
	static private Logger log = LoggerFactory.getLogger(RestExceptionHandler.class);

	@ExceptionHandler(HttpServerErrorException.class)
	public ResponseEntity<ErrorItem> httpServerExceptionHandler(HttpServerErrorException e) {
		log.error("HttpServerErrorException : {}", e.getMessage());
		ErrorItem item = new ErrorItem();
		String errResult = e.getResponseBodyAsString();
		log.info("Error Response ", errResult);
		if (!errResult.isEmpty()) {
			Domain errorDomain = new Domain(errResult);
			item.setStatus("FAIL");

			try {
				String message = messageSource.getMessage(errorDomain.getString("message"), null,
						LocaleContextHolder.getLocale());
				item.setMessage(message);
			} catch (NoSuchMessageException ex) {
				item.setMessage(e.getMessage());
			}
		}
		return new ResponseEntity<>(item, HttpStatus.OK);
	}

	@ExceptionHandler(ResourceAccessException.class)
	public ResponseEntity<ErrorItem> resourceAccessExceptionHandler(ResourceAccessException e) {
		log.error("ResourceAccessException : {}", e.getMessage());
		ErrorItem item = new ErrorItem();
		String errResult = "server.fault";
		item.setStatus("FAIL");
		try {
			String message = messageSource.getMessage(errResult, null, LocaleContextHolder.getLocale());
			item.setMessage(message);
		} catch (NoSuchMessageException ex) {
			item.setMessage(e.getMessage());
		}
		return new ResponseEntity<>(item, HttpStatus.OK);
	}

	@SuppressWarnings("rawtypes")
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorResponse> constraintViolationHandler(ConstraintViolationException e) {
		log.error("ConstraintViolationException : {}", e.getMessage());
		ErrorResponse errors = new ErrorResponse();
		for (ConstraintViolation violation : e.getConstraintViolations()) {
			ErrorItem error = new ErrorItem();
			error.setStatus("FAIL");
			error.setMessage(violation.getMessage());
			errors.addError(error);
		}
		return new ResponseEntity<>(errors, HttpStatus.OK);
	}

	@ExceptionHandler(CoreException.class)
	public ResponseEntity<ErrorItem> coreHandle(CoreException e) {
		log.error("CoreException : {}", e.getMessage());
		ErrorItem item = new ErrorItem();
		item.setStatus("FAIL");
		try {
			String body = messageSource.getMessage(e.getMessage(), null, LocaleContextHolder.getLocale());
			item.setMessage(body);
		} catch (NoSuchMessageException ex) {
			item.setMessage(e.getMessage());
		}

		return new ResponseEntity<>(item, HttpStatus.OK);
	}
	@ExceptionHandler(OAuth2Exception.class)
	public ResponseEntity<ErrorItem> coreHandle(OAuth2Exception e) {
		e.printStackTrace();
		ErrorItem item = new ErrorItem("FAIL",
				messageSource.getMessage("failed.to.read.json.data", null, LocaleContextHolder.getLocale()));
		return new ResponseEntity<>(item, HttpStatus.OK);
	}
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorItem> coreHandle(HttpMessageNotReadableException e) {
		ErrorItem item = new ErrorItem("FAIL",
				messageSource.getMessage("failed.to.read.json.data", null, LocaleContextHolder.getLocale()));
		return new ResponseEntity<>(item, HttpStatus.OK);
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<ErrorItem> httpMethod(HttpRequestMethodNotSupportedException e) {
		ErrorItem item = new ErrorItem("FAIL", "should.be.use.method." + e.getSupportedMethods()[0]);
		return new ResponseEntity<>(item, HttpStatus.OK);
	}

	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	public ResponseEntity<ErrorItem> unsupportMediaType(HttpMediaTypeNotSupportedException e) {
		ErrorItem item = new ErrorItem("FAIL", "failed.to.handle.media.type");
		return new ResponseEntity<>(item, HttpStatus.OK);
	}

	public static class ErrorItem {
		private String status;
		private String message;

		ErrorItem() {
		}

		 ErrorItem(String status, String message) {
			super();
			this.status = status;
			this.message = message;
		}

		String getMessage() {
			return message;
		}

		void setMessage(String message) {
			this.message = message;
		}

		String getStatus() {
			return status;
		}

		void setStatus(String status) {
			this.status = status;
		}

	}

	@XmlRootElement(name = "errors")
	public static class ErrorResponse {
		private List<ErrorItem> errors = new ArrayList<>();

		@XmlElement(name = "error")
		public List<ErrorItem> getErrors() {
			return errors;
		}

		public void setErrors(List<ErrorItem> errors) {
			this.errors = errors;
		}

		public void addError(ErrorItem error) {
			this.errors.add(error);
		}
	}
}