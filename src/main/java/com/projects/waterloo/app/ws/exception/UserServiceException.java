package com.projects.waterloo.app.ws.exception;

public class UserServiceException extends RuntimeException{

	private static final long serialVersionUID = 1747979241814921878L;
	
	public UserServiceException(String message) {
		super(message);
	}
}
