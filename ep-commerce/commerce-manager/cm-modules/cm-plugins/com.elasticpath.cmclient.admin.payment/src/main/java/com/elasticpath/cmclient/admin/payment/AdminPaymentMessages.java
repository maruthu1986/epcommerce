/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.admin.payment;

import com.elasticpath.cmclient.core.nls.LocalizedMessagePostProcessor;

/**
 * Messages class for the Payment plugin's PaymentGateways section.
 */
@SuppressWarnings ({ "PMD.TooManyFields", "PMD.VariableNamingConventions" })
public final class AdminPaymentMessages {

	private static final String BUNDLE_NAME = "com.elasticpath.cmclient.admin.payment.AdminPaymentPluginResources"; //$NON-NLS-1$

	private AdminPaymentMessages() {
	}

	// ----------------------------------------------------
	// Default package
	// ----------------------------------------------------
	public String PaymentAdminItemCompositeFactory_PaymentAdmin;

	// Global
	public String GatewayName;

	public String GatewayImpl;

	// Payment gateways operations
	public String CreatePaymentGateway;

	public String EditPaymentGateway;

	public String DeletePaymentGateway;

	// Dialogs
	public String CreatePaymentGatewayDialog;

	public String EditPaymentGatewayDialog;

	public String PaymentDialogInitialMessage;

	public String DeletePaymentGatewayTitle;

	public String DeletePaymentGatewayText;

	public String PaymentGatewayNameAlreadyExist;

	public String PaymentGatewayPropertiesModifiedTitle;

	public String PaymentGatewayPropertiesModifiedText;

	public String GatewayNameLabel;

	public String GatewayImplLabel;

	// Payment properties list key
	public String PaymentPropertyListKey;

	// Payment properties list value
	public String PaymentPropertyListValue;

	public String PaymentPropertyInvalid;

	// Payment properties
	public String PaymentProperty;

	// Errors
	public String PaymentGatewayNoLongerExists;
	
	public String PaymentGatewayInUseTitle;
	
	public String PaymentGatewayInUseMessage;

	/**
	 * Gets the NLS localize message class.
	 * @return the localized message class.
	 */
	public static AdminPaymentMessages get() {
		return LocalizedMessagePostProcessor.getUTF8Encoded(BUNDLE_NAME, AdminPaymentMessages.class);
	}

}