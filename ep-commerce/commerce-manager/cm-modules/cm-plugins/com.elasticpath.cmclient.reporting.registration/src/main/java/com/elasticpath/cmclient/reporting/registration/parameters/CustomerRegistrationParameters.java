/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.reporting.registration.parameters;

import java.util.Date;

/**
 * Represents the parameters defined for the customer registration report.
 *
 */
public class CustomerRegistrationParameters {

	private String storeName;
	
	private Date startDate;
	
	private Date endDate;
	
	private boolean anonymousRegistration;

	/**
	 * Gets the store name.
	 * 
	 * @return String the store name
	 */
	public String getStoreName() {
		return storeName;
	}

	/**
	 * Sets the store name.
	 * 
	 * @param storeName the store name
	 */
	public void setStoreName(final String storeName) {
		this.storeName = storeName;
	}

	/**
	 * Gets the starting date.
	 * 
	 * @return Date the starting date
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * Sets the starting date.
	 * 
	 * @param startDate the starting date
	 */
	public void setStartDate(final Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * Gets the end date.
	 * 
	 * @return Date the end date
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * Sets the end date.
	 * 
	 * @param endDate the end date
	 */
	public void setEndDate(final Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * Gets boolean state is registration anonymous.
	 * 
	 * @return true if registration is anonymous, false otherwise
	 */
	public boolean isAnonymousRegistration() {
		return anonymousRegistration;
	}

	/**
	 * Sets the boolean state of anonymous registration.
	 * 
	 * @param anonymousRegistration boolean
	 */
	public void setAnonymousRegistration(final boolean anonymousRegistration) {
		this.anonymousRegistration = anonymousRegistration;
	}	
}
