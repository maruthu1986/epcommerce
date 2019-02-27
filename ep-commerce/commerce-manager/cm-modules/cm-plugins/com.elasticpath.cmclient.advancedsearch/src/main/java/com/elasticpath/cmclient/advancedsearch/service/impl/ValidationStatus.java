/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.advancedsearch.service.impl;

/**
 * This class contains information regarding validation status such as validation state and status message.
 */
public class ValidationStatus {
	
	private final boolean valid;
	
	private final String statusMessage;
	
	/**
	 * Constructs the validation status object with given arguments.
	 * 
	 * @param valid the validation state, true if query valid and false otherwise.
	 * @param statusMessage the status message for validated query
	 */
	public ValidationStatus(final boolean valid, final String statusMessage) {
		this.valid = valid;
		this.statusMessage = statusMessage;
	}

	/**
	 * Gets the validation state.
	 * 
	 * @return true in case valid query and false otherwise.
	 */
	public boolean isValid() {
		return valid;
	}

	/**
	 * Gets the status message for verified query.
	 * 
	 * @return the status message
	 */
	public String getStatusMessage() {
		return statusMessage;
	}
}
