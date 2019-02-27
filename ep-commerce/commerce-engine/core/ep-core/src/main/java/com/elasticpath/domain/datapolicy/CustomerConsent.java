/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.domain.datapolicy;

import java.util.Date;

import com.elasticpath.persistence.api.Entity;

/**
 * Represents Customer Consent.
 */
public interface CustomerConsent extends Entity {

	/**
	 * Return the data policy.
	 *
	 * @return the data policy.
	 */
	DataPolicy getDataPolicy();

	/**
	 * Set the data policy.
	 *
	 * @param dataPolicy the data policy to set.
	 */
	void setDataPolicy(DataPolicy dataPolicy);

	/**
	 * Get customer consent date.
	 *
	 * @return customer consent date.
	 */
	Date getConsentDate();

	/**
	 * Set customer consent date.
	 *
	 * @param consentDate customer consent date.
	 */
	void setConsentDate(Date consentDate);

	/**
	 * Get action associated with CustomerConsent.
	 *
	 * @return consent action.
	 */
	ConsentAction getAction();

	/**
	 * Set customer consent action.
	 *
	 * @param action consent action.
	 */
	void setAction(ConsentAction action);

	/**
	 * Get customer's guid.
	 *
	 * @return customer's guid.
	 */
	String getCustomerGuid();

	/**
	 * Set customer's guid.
	 *
	 * @param customerGuid customer guid to set.
	 */
	void setCustomerGuid(String customerGuid);
}
