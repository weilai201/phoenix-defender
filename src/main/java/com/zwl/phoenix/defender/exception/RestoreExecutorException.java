package com.zwl.phoenix.defender.exception;

public class RestoreExecutorException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7905259833368370476L;
	private String code;
	private String message;
	
	public RestoreExecutorException() {
		super();
	}
	
	public RestoreExecutorException(String message) {
		super(message);
		this.message=message;
	}
	
	public RestoreExecutorException(String code, String message) {
		super(message);
		this.message=message;
		this.code=code;
	}
	
	public RestoreExecutorException(Throwable throwable) {
		super(throwable);
	}

	public RestoreExecutorException(String message,Throwable throwable) {
		super(message,throwable);
		this.message=message;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	
}
