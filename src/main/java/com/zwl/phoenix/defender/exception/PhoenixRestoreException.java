package com.zwl.phoenix.defender.exception;

public class PhoenixRestoreException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7905259833368370476L;
	private String code;
	private String message;
	
	public PhoenixRestoreException() {
		super();
	}
	
	public PhoenixRestoreException(String message) {
		super(message);
		this.message=message;
	}
	
	public PhoenixRestoreException(String code, String message) {
		super(message);
		this.message=message;
		this.code=code;
	}
	
	public PhoenixRestoreException(Throwable throwable) {
		super(throwable);
	}

	public PhoenixRestoreException(String message,Throwable throwable) {
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
