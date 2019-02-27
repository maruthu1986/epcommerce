/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.reporting.registration;

import org.eclipse.osgi.util.NLS;

/**
 * Messages class for the report plugin.
 *
 */
@SuppressWarnings("PMD.VariableNamingConventions")
public final class CustomerRegistrationReportMessages {

	private static final String BUNDLE_NAME = 
		"com.elasticpath.cmclient.reporting.registration.CustomerRegistrationReportPluginResources"; //$NON-NLS-1$
	
	private CustomerRegistrationReportMessages() {
	}

	public static String reportTitle;
	
	public static String report;
	
	// ----------------------------------------------------
	// Registration Report params UI
	// ----------------------------------------------------
	public static String store;
	
	public static String fromdate;
	
	public static String todate;
	
	public static String anonymous_registration;
	
	public static String allStores;
	
	public static String includingAnonymousRegi;
	
	public static String notIncludingAnonymousRegi;
	
	// ----------------------------------------------------
	// Registration Report service
	// ----------------------------------------------------
	public static String registered;
	
	public static String guest;
	
	public static String yes_as_literal;
	
	public static String no_as_literal;
	
	static {
		NLS.initializeMessages(BUNDLE_NAME, CustomerRegistrationReportMessages.class);
	}
	
}
