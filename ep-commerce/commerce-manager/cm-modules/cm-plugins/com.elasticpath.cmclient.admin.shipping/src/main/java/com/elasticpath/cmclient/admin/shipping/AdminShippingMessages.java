/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.admin.shipping;

import com.elasticpath.cmclient.core.nls.LocalizedMessagePostProcessor;

/**
 * Messages class for Shipping plugin.
 */
@SuppressWarnings({ "PMD.VariableNamingConventions", "PMD.TooManyFields" })
public final class AdminShippingMessages {
	private static final String BUNDLE_NAME = "com.elasticpath.cmclient.admin.shipping.AdminShippingPluginResources"; //$NON-NLS-1$

	// Admin items titles
	public String ShippingAdminItemCompositeFactory_RegionsAdmin;

	public String ShippingAdminItemCompositeFactory_ServiceLevelsAdmin;

	// Action titles
	public String CreateShippingRegion;

	public String EditShippingRegion;

	public String DeleteShippingRegion;

	// View columns
	public String ShippingRegionName;

	// ShippingRegionsCreateDialog
	public String RegionNameLabel;

	public String AvailableCountriesLabel;

	public String AddToolTipButton;

	public String RemoveToolTipButton;

	public String AddButton;

	public String RemoveButton;

	public String SaveButton;

	public String SelectedCountriesLabel;

	public String CreateShippingRegionTitle;

	public String EditShippingRegionTitle;

	public String InitialMessage;

	public String NameExistsErrorMessage;

	public String EmptySelectedCountryListBoxErrorMessage;

	// Delete Shipping Region dialog.
	public String DeleteShippingRegionDialogTitle;

	public String DeleteShippingRegionDialogText;

	public String FailedDeleteShippingRegionTitle;

	public String FailedDeleteShippingRegionText;

	public String CantDeleteShippingRegionDialogTitle;

	public String CantDeleteShippingRegionDialogText;


	// Edit Shipping Region action.
	public String ShippingRegionNoLongerExists;

	private AdminShippingMessages() {
	}
	/**
	 * Gets the NLS localize message class.
	 * @return the localized message class.
	 */
	public static AdminShippingMessages get() {
		return LocalizedMessagePostProcessor.getUTF8Encoded(BUNDLE_NAME, AdminShippingMessages.class);
	}


}
