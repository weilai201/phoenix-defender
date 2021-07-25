package com.zwl.phoenix.defender.exception;

public class PhoenixClientException extends RuntimeException{
	
	private String code;
	private String message;
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -5855339546278250704L;
	
	public PhoenixClientException() {
		super();
	}
	
	public PhoenixClientException(String message) {
		super(message);
		this.message=message;
	}
	
	public PhoenixClientException(String code, String message) {
		super(message);
		this.message=message;
		this.code=code;
	}
	
	public PhoenixClientException(Throwable throwable) {
		super(throwable);
	}

	public PhoenixClientException(String message,Throwable throwable) {
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
