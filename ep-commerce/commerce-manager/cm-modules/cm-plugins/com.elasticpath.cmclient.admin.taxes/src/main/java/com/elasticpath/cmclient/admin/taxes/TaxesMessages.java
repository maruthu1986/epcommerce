/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.admin.taxes;

import com.elasticpath.cmclient.core.nls.LocalizedMessagePostProcessor;

/**
 * Messages class for the Taxes plug-in.
 */
@SuppressWarnings ({ "PMD.TooManyFields", "PMD.VariableNamingConventions" })
public final class TaxesMessages {
	private static final String BUNDLE_NAME = "com.elasticpath.cmclient.admin.taxes.TaxesPluginResources"; //$NON-NLS-1$

	private TaxesMessages() {
	}

	// ----------------------------------------------------
	// Default package
	// ----------------------------------------------------

	/** Global messages. */
	public String TaxesAdminSection_TaxJurisdictionAdmin;

	public String TaxesAdminSection_TaxCodes;

	public String TaxesAdminSection_ManageTaxValues;

	public String CreateTaxCode;

	public String DeleteTaxCode;

	public String DeleteTaxCodeTitle;

	public String DeleteTaxCodeText;

	public String EditTaxCode;

	public String TaxCodeNoLongerExists;

	public String TaxCode;

	public String TaxCategory;

	public String TaxDisplayName;

	public String TaxAddressField;

	public String TaxJurisdictionCountry;

	public String TaxJurisdictionCalculationMethod;

	public String TaxJurisdictionConfigureTaxes;

	public String TaxJurisdictionConfigureTaxes_TaxName;

	public String TaxJurisdictionConfigureTaxes_AddressField;

	public String CalculationMethod_Exclusive;

	public String CalculationMethod_Inclusive;

	/** Dialog messages. */
	public String CreateTaxJurisdictionDialogTitle;

	public String EditTaxJurisdictionDialogTitle;

	public String ConfirmDeleteTaxJurisdictionMsgBoxTitle;

	public String ConfirmDeleteTaxJurisdictionMsgBoxText;

	public String ConfirmDeleteTaxCategoryMsgBoxTitle;

	public String ConfirmDeleteTaxCategoryMsgBoxText;

	public String ConfirmDeleteTaxValueMsgBoxTitle;

	public String ConfirmDeleteTaxValueMsgBoxText;

	public String TaxJurisdictionDialogInitialMessage;

	public String TaxCodeExists;

	public String CreateTaxCategory;

	public String EditTaxCategory;

	public String DeleteTaxCategory;

	public String TaxCategoryModifiedTitle;

	public String TaxCategoryModifiedText;

	public String AddTax;

	public String EditTax;

	public String RemoveTax;

	/** Tax Value Operations. */
	public String TaxValueAddDialogTitle;

	public String TaxValueEditDialogTitle;
	
	public String TaxValueDialogInstructions;

	public String EmptyMessage;

	public String ManageTaxValuesTitleDialog;

	public String ManageTaxValuesLabel;

	public String ManageTaxValuesTaxJurisdictionLabel;

	public String ManageTaxValuesMessageDialog;

	public String ManageTaxValuesFilterGroup;

	public String ManageTaxValuesFilterButton;

	public String ManageTaxValuesTaxLabel;

	public String TaxValuesLabel;

	public String TaxValueLabel;

	public String TaxValueAddLabel;

	public String TaxValueEditLabel;

	public String TaxValueRemoveLabel;

	/** List views messages. */
	public String TaxJurisdictionCountryColumnLabel;

	public String TaxJurisdictionCalcMethodColumnLabel;

	/** Action messages. */
	public String CreateTaxJurisdiction;

	public String EditTaxJurisdiction;

	public String DeleteTaxJurisdiction;

	/** Errors. */
	public String NoLongerExistTaxJurisdictionMsgBoxTitle;

	public String NoLongerExistTaxJurisdictionMsgBoxText;

	public String NoLongerExistTaxCategoryMsgBoxTitle;

	public String NoLongerExistTaxCategoryMsgBoxText;

	public String AlreadyExistTaxCategoryMsgBoxTitle;

	public String AlreadyExistTaxCategoryMsgBoxText;

	public String AlreadyExistTaxRegionMsgBoxTitle;

	public String AlreadyExistTaxRegionMsgBoxText;

	public String TaxCodeInUseTitle;

	public String TaxCodeInUseMessage;
	
	public String TaxJurisdictionInUseTitle;

	public String TaxJurisdictionInUseMessage;

	/**
	 * Gets the NLS localize message class.
	 * @return the localized message class.
	 */
	public static TaxesMessages get() {
		return LocalizedMessagePostProcessor.getUTF8Encoded(BUNDLE_NAME, TaxesMessages.class);
	}

}
