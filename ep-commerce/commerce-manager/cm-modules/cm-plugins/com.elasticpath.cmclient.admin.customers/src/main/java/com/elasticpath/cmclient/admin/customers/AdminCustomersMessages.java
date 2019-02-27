/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.admin.customers;

import com.elasticpath.cmclient.core.nls.LocalizedMessagePostProcessor;

/**
 * Messages class for the customers plug-in.
 */
@SuppressWarnings({"PMD.VariableNamingConventions", "PMD.TooManyFields"})
public final class AdminCustomersMessages {
	private static final String BUNDLE_NAME = "com.elasticpath.cmclient.admin.customers.AdminCustomersPluginResources"; //$NON-NLS-1$

	private AdminCustomersMessages() {
	}

	// ----------------------------------------------------
	// Default package
	// ----------------------------------------------------
	public String CustomerAdminSection_ProfileAttributes;
	
	public String CustomerAdminSection_CustomerSegments;

	// Global
	public String AttributeKey;

	public String AttributeName;

	public String AttributeType;

	public String SystemAttribute;

	public String Required;

	public String CreateAttribute;

	public String EditAttribute;

	public String DeleteAttribute;
	
	public String CustomerSegmentName;
	
	public String CustomerSegmentDescription;
	
	public String CustomerSegmentEnabledFlag;
	
	public String EditCustomerSegment;
	
	public String CreateCustomerSegment;
	
	public String DeleteCustomerSegment;

	// Dialogs
	public String CreateProfileAttributeTitle;

	public String CreateProfileAttributeText;

	public String EditProfileAttributeTitle;

	public String EditProfileAttributeText;

	public String DeleteProfileAttributeTitle;

	public String DeleteProfileAttributeText;
	
	public String DeleteCustomerSegmentTitle;
	
	public String DeleteCustomerSegmentText;
	
	public String CustomerSegmentEditor_SaveTaskName;

	public String CustomerSegmentEditor_OnSavePrompt;
	
	public String CustomerSegmentEditor_ToolTip;
	
	public String CustomerSegmentEditor_NewSegmentName;
	
	// Errors
	public String ProfileAttributeNoLongerExists;

	public String ProfileAttributeKeyExists;

	public String ProfileAttributeNameExists;
	
	public String ProfileAttributeInUseTitle;
	
	public String ProfileAttributeInUseMessage;
	
	public String CustomerSegmentNoLongerExists;

	public String CustomerSegmentExists;

	public String CustomerSegmentExistsWithName;
	
	public String CustomerSegmentInUseTitle;
	
	public String CustomerSegmentInUseText;	
	
	public String CustomerSegmentEditor_SummaryPage;
	
	public String CustomerSegmentEditor_SummaryPage_Details;

	/**
	 * Gets the NLS localize message class.
	 * @return the localized message class.
	 */
	public static AdminCustomersMessages get() {
		return LocalizedMessagePostProcessor.getUTF8Encoded(BUNDLE_NAME, AdminCustomersMessages.class);
	}


}