package com.projects.waterloo.app.ws.ui.model.response;

public enum ErrorMessages {
	
	MISSING_REQUIRED_FIELD("Missing Required Field, Please check documentation"),
	RECORD_ALREADY_EXISTS("Record Already Exists"),
	INTERNAL_SERVER_ERROR("Internal Server Error"),
	NO_RECORD_FOUND("No Record Found"),
	AUTHENTICATION_FAILED("Authentication Failed"),
	COULD_NOT_UPDATE_RECORD("Could not update Record"),
	COULD_NOT_DELETE_RECORD("Could not Delete Record"),
	EMAIL_ADDRESS_NOT_VERIFIED("Email Address could not be verified");
	
	private String errorMessage;

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	ErrorMessages(String errorMessage) {
		this.errorMessage = errorMessage;
	} 

}
