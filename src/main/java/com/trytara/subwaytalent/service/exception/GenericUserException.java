/**
 * 
 */
package com.trytara.subwaytalent.service.exception;

/**
 * 
 * @author JRDomingo
 * Dec 10, 2016
 */
@SuppressWarnings("serial")
public class GenericUserException extends RuntimeException{

	private String code;
	
	/**
	 * 
	 */
	public GenericUserException(String message, String code) {
		super(message);
		this.setCode(code);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public GenericUserException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public GenericUserException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public GenericUserException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public GenericUserException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	public static class InvalidResetKey extends GenericUserException{
		public InvalidResetKey(){
			super("Invalid reset key", "1001");
		}
	}
	

	public static class InvalidPassword extends GenericUserException{
		public InvalidPassword(){
			super("Invalid credentials. Please check your email and password.", "1002");
		}
	}

	public static class InvalidSessionToken extends GenericUserException{
		public InvalidSessionToken(){
			super("Invalid session token", "1009");
		}
	}
	

	public static class AccountNotVerified extends GenericUserException{
		public AccountNotVerified(){
			super("Account not yet verified", "1010");
		}
	}

	public static class UnregisteredEmail extends GenericUserException{
		public UnregisteredEmail(){
			super("Email address not recognized", "1011");
		}
	}
	

	public static class EmailAlreadyRegistered extends GenericUserException{
		public EmailAlreadyRegistered(){
			super("Email address already registered", "1012");
		}
	}
	
}
