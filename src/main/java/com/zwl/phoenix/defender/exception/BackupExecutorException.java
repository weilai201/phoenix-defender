package com.zwl.phoenix.defender.exception;

public class BackupExecutorException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8154883814162664777L;
	private String code;
	private String message;
	
	/**
	 * 
	 */
	
	public BackupExecutorException() {
		super();
	}
	
	public BackupExecutorException(String message) {
		super(message);
		this.message=message;
	}
	
	public BackupExecutorException(String code, String message) {
		super(message);
		this.message=message;
		this.code=code;
	}
	
	public BackupExecutorException(Throwable throwable) {
		super(throwable);
	}

	public BackupExecutorException(String message,Throwable throwable) {
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
