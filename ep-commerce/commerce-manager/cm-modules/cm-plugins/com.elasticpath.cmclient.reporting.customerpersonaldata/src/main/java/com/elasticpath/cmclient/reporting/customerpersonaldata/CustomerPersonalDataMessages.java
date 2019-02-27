/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.cmclient.reporting.customerpersonaldata;

import com.elasticpath.cmclient.core.nls.LocalizedMessagePostProcessor;

/**
 * Messages class for the report plugin.
 *
 */
@SuppressWarnings("PMD.VariableNamingConventions")
public final class CustomerPersonalDataMessages {

	private static final String BUNDLE_NAME = 
		"com.elasticpath.cmclient.reporting.customerpersonaldata.CustomerPersonalDataMessages"; //$NON-NLS-1$
	
	private CustomerPersonalDataMessages() {
	}

	public String reportTitle;
	public String report;

	//Report parameters
	public String selectStore;
	public String store;
	public String fromDate;
	public String toDate;
	public String userId;

	/**
	 * Gets the NLS localize message class.
	 * @return the localized message class.
	 */
	public static CustomerPersonalDataMessages get() {
		return LocalizedMessagePostProcessor.getUTF8Encoded(BUNDLE_NAME, CustomerPersonalDataMessages.class);
	}

}
