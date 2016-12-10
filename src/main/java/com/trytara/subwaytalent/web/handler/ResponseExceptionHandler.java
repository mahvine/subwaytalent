/**
 * 
 */
package com.trytara.subwaytalent.web.handler;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.trytara.subwaytalent.service.exception.GenericUserException;
import com.trytara.subwaytalent.service.exception.GenericUserException.InvalidSessionToken;

/**
 * @author JRDomingo
 * @since Aug 27, 2015 11:01:13 AM
 * 
 */
@ControllerAdvice
public class ResponseExceptionHandler {
	
	@ExceptionHandler(GenericUserException.class)
	public ResponseEntity<DefaultValidationResponse> handle(Throwable throwable){
		GenericUserException genericUserException = (GenericUserException) throwable;
		DefaultValidationResponse defaultValidationResponse = new DefaultValidationResponse(genericUserException.getMessage());
		if(genericUserException.getCode()!=null){
			defaultValidationResponse.code = genericUserException.getCode();
		}
		HttpStatus status = HttpStatus.BAD_REQUEST;
		
		if(genericUserException instanceof InvalidSessionToken){
			status = HttpStatus.UNAUTHORIZED;
		}
		
		return new ResponseEntity<>(defaultValidationResponse, status);
	}
	

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseBody
	public ResponseEntity<DefaultValidationResponse> handleValidationException(MethodArgumentNotValidException e) {
	    List<ObjectError> errors = e.getBindingResult().getAllErrors();
    	ObjectError objectError = errors.get(0);
    	DefaultValidationResponse defaultValidationResponse = new DefaultValidationResponse(objectError.getDefaultMessage());
	    return new ResponseEntity<>(defaultValidationResponse, HttpStatus.BAD_REQUEST);
	}
	
	@JsonInclude(Include.NON_NULL)
	public static class DefaultValidationResponse{
		public String message;
		public String errorField;
		public String code;
		
		public DefaultValidationResponse(){
			
		}

		public DefaultValidationResponse(String message){
			this.message = message;
		}

		public DefaultValidationResponse(GenericUserException e){
			this.message = e.getMessage();
			this.code = e.getCode();
			
		}

		public DefaultValidationResponse(String message, String field){
			this.message = message;
			this.errorField = field;
		}
	}

}
