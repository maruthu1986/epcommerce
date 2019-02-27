/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.admin.warehouses;

import com.elasticpath.cmclient.core.nls.LocalizedMessagePostProcessor;

/**
 * Utility class for message storing.
 */
@SuppressWarnings({ "PMD.TooManyFields", "PMD.VariableNamingConventions" })
public final class AdminWarehousesMessages {

	/** Property file binding. */
	private static final String BUNDLE_NAME = "com.elasticpath.cmclient.admin.warehouses.AdminWarehousesPluginResources"; //$NON-NLS-1$

	/**
	 * Empty constructor.
	 */
	private AdminWarehousesMessages() {
	}

	/** Default package. */
	public String UserAdminSection_UserAdmin;

	/** Global. */
	public String Warehouse;

	public String WarehouseName;

	public String PickDelay;

	public String Street;

	public String City;

	public String Country;

	public String AddressLine1;

	public String AddressLine2;

	public String StateProvinceRegion;

	public String ZipPostalCode;

	public String WarehouseCode;

	/** Delete dialog. */
	public String ConfirmDeleteWarehouseTitle;

	public String ConfirmDeleteWarehouseText;

	public String DialogInitialMessage;

	/** Warehouse operations/actions. */
	public String CreateWarehouse;

	public String EditWarehouse;

	public String DeleteWarehouse;

	public String UploadInventory;

	/** Upload inventory dialog. */
	public String UploadInventoryDialog_ImportFile;

	public String UploadInventoryDialog_CsvImportDialogTitle;

	public String UploadInventoryDialog_VerifyButtonText;

	public String button_Browse;

	public String UploadInventoryDialog_sku_code;

	public String UploadInventoryDialog_product_name;

	public String UploadInventoryDialog_sku_options;

	public String UploadInventoryDialog_upload_quantity;

	/** Errors. */
	public String WarehouseNoLongerExists;

	public String WarehouseInUseTitle;

	public String WarehouseInUseMessage;

	public String WarehouseCodeExists;
	
	public String WarehouseNoPermission;

	/**
	 * Gets the NLS localize message class.
	 * @return the localized message class.
	 */
	public static AdminWarehousesMessages get() {
		return LocalizedMessagePostProcessor.getUTF8Encoded(BUNDLE_NAME, AdminWarehousesMessages.class);
	}

}

