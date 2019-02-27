/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.reporting.giftcertificatedetails;

import com.elasticpath.cmclient.core.nls.LocalizedMessageNLS;
import org.eclipse.osgi.util.NLS;

/**
 * Messages class for the report plugin.
 *
 */
@SuppressWarnings("PMD.VariableNamingConventions")
public final class GiftCertificateDetailsMessages {

	private static final String BUNDLE_NAME = 
		"com.elasticpath.cmclient.reporting.giftcertificatedetails.GiftCertificateDetailsReportPluginResources"; //$NON-NLS-1$
	
	private GiftCertificateDetailsMessages() {
	}

	public static String reportTitle;
	public static String report;
	
	public static String selectStore;
	public static String store;
	public static String currency;
	public static String fromdate;
	public static String todate;

	static{
		load();
	}

	/**
	 * loads localized messages for this plugin.
	 */
	public static void load() {
		LocalizedMessageNLS.getUTF8Encoded(BUNDLE_NAME, GiftCertificateDetailsMessages.class);
	}
	
}
