/*
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.warehouse;

import java.util.HashMap;
import java.util.Map;

import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.nls.LocalizedMessagePostProcessor;
import com.elasticpath.domain.order.OrderReturnStatus;
import com.elasticpath.domain.order.OrderReturnType;
import com.elasticpath.domain.order.OrderShipmentStatus;

/**
 * Messages class for the com.elasticpath.cmclient.warehouse plugin.
 */
@SuppressWarnings({ "PMD.TooManyFields", "PMD.ExcessivePublicCount", "PMD.VariableNamingConventions" })
public final class WarehouseMessages {
	private static final String BUNDLE_NAME = "com.elasticpath.cmclient.warehouse.WarehousePluginResources"; //$NON-NLS-1$

	public static final String EMPTY_STRING = ""; //$NON-NLS-1$

	public static final String SPACE = " "; //$NON-NLS-1$

	public static final String COMMA = ","; //$NON-NLS-1$

	public static final String BAR = "#"; //$NON-NLS-1$

	public String Shipments;

	public String Warehouse_Title;

	public String CustomerDetails_FirstNameLabel;

	public String CustomerDetails_LastNameLabel;

	public String OrderEditor_Save_StatusBarMsg;

	public String OrderEditor_ToolTipText;

	public String OrderReturnPage_Title;

	public String OrderReturnSummaryOverviewSection_Title;

	public String OrderReturnSummaryOverviewSection_Description;

	public String OrderReturnSummaryOverviewSection_RMAType;

	public String OrderReturnSummaryOverviewSection_RMANumber;

	public String OrderReturnSummaryOverviewSection_OrderNumber;

	public String OrderReturnSummaryOverviewSection_RMADate;

	public String OrderReturnSummaryOverviewSection_RMAStatus;

	public String OrderReturnSummaryOverviewSection_ReceivedBy;

	public String OrderReturnSummaryOverviewSection_OpenButton;
	
	public String OrderReturn_Editor_OnSavePrompt;

	public String OrderReturnDetailsSection_Title;

	public String OrderReturnDetailsSection_Description;

	public String OrderReturnDetailsSection_RMATableTooltip;

	public String OrderReturnDetailsSection_SKUCodeColumn;

	public String OrderReturnDetailsSection_ProductNameColumn;

	public String OrderReturnDetailsSection_ExpQtyColumn;

	public String OrderReturnDetailsSection_RecQtyColumn;

	public String OrderReturnDetailsSection_RecStateColumn;

	public String OrderReturnNoteSection_Title;

	public String OrderReturnNoteSection_Description;

	public String OrderReturn_SelectRecState;

	public String OrderReturn_RecQtyError_Title;

	public String OrderReturn_RecQtyError_Msg;

	public String SearchView_ClearButton;

	public String SearchView_OrderNumber;

	public String SearchView_RMATab;

	public String SearchView_InventoryTab;

	public String SearchView_RetrieveSkuGroup;

	public String SearchViewInventoryError_InvalidSkuCode;

	public String SearchViewInventoryError_SkuAlwaysAvailable;

	public String SearchView_SkuCodeLabel;

	public String SearchView_RMA;

	public String SearchView_SearchButton;

	public String InventorySearchView_RetrieveButton;

	public String SearchView_SearchTermsGroup;

	public String OrderSearchResultsView_OrderNumber;

	public String OrderSearchResultsView_RMADate;

	public String OrderSearchResultsView_RMANumber;

	public String OrderSearchResultsView_Status;

	public String OverviewSection_Title;

	public String OverviewSection_PickPackStatus;

	public String OverviewSection_ReleasedForPickPack_Label;

	public String OverviewSection_ShipmentsNumber;

	public String Inventory_EditorTooltip;
	
	public String Inventory_Editor_OnSavePrompt;

	public String Inventory_Warehouse;

	public String Inventory_SummarySectionPart;

	public String Inventory_QuantityOnHand;

	public String Inventory_AvailableQuantity;

	public String Inventory_QuantityAllocated;
	
	public String Inventory_Always_Available;

	public String Inventory_SettingsSectionPart;

	public String Inventory_ReservedQuantity;

	public String Inventory_ReOrderMinimum;

	public String Inventory_ReOrderQuantity;

	public String Inventory_ExpectedReStockDate;

	public String Inventory_AdjustQuantityOnHandSectionPart;

	public String Inventory_Adjustment;

	public String Inventory_Quantity;

	public String Inventory_Reason;

	public String Inventory_Comment;

	public String Inventory_AdjustmentSelectAction;

	public String Inventory_AdjustmentAddStock;

	public String Inventory_AdjustmentRemoveStock;
	
	public String InventoryError_ReservedQuantityGreaterOnHand;

	public String ThereAreNoWarehousesMsgBoxTitle;

	public String ThereAreNoWarehousesMsgBoxText;
	
	public String CompleteShipment_DialogTitle;

	public String CompleteShipment_OkButton;
	
	public String CompleteShipment_ForceCompleteButton;

	public String CompleteShipment_SelectSectionPart;

	public String CompleteShipment_DetailsSectionPart;

	public String CompleteShipment_ValidateButton;

	public String CompleteShipment_ShipmentIDText;

	public String CompleteShipment_CustomerIDText;

	public String CompleteShipment_CustomerNameText;

	public String CompleteShipment_ShippingAddressText;

	public String CompleteShipment_ShippingMethodText;

	public String CompleteShipment_TrackingNumberText;

	public String CompleteShipment_OperationNotification;
	
	public String CompleteShipment_ForceCompletionNotification;

	public String CompleteShipment_InvalidShipmentIDErrorMessage;

	public String CompleteShipment_InvalidShipmentStateErrorMessage;
	
	public String CompleteShipment_ShipmentCompletionFailedDialogTitle;

	public String CompleteShipment_ShipmentCompletionFailedDialogMessage;
	
	public String CompleteShipment_ShipmentForceCompletionFailedDialogMessage; //NOPMD

	public String CompleteShipment_ShipmentForceCompletionOkDialogTitle;
	
	public String CompleteShipment_ShipmentForceCompletionOkDialogMessage;
	
	public String CompleteShipment_Warehouse_No_Permission;
	
	public String CompleteShipment_NoAddressDefined;
	
	public String CompleteShipment_InsufficientInventory;
	
	public String ReceiveInventory_DialogTitle;

	public String ReceiveInventory_SkuCode;

	public String ReceiveInventory_Quantity;

	public String ReceiveInventory_ProductName;

	public String ReceiveInventory_SkuOptions;

	public String ReceiveInventory_SaveFailedDialogTitle;

	public String ReceiveInventory_SaveFailedDialogMessage;

	public String ReceiveInventory_Error_SkuInvalidOrAlwaysAvailable;
	
	public String ReceiveInventory_Error_InvalidQuantity;
	
	public String OrderReturn_ErrDlgCollisionTitle;
	
	public String OrderReturn_ErrDlgCollisionMessage;

	/** Order Shipment Statuses. */
	public String ShipmentStatus_OnHold;

	public String ShipmentStatus_Released;

	public String ShipmentStatus_Completed;

	public String ShipmentStatus_Cancelled;

	public String ShipmentStatus_Awaiting;

	public String ShipmentStatus_Assigned;

	public String SearchView_Error_NoSearchTerms;

	// Define the map of enum constants to localized names
	private final Map<OrderShipmentStatus, String> localizedExtensibleEnums = new HashMap<>();

	/**
	 * Returns string or empty string if the string is null.
	 * 
	 * @param string to return
	 * @return string or empty string if the string is null.
	 */
	public String getString(final String string) {
		if (string == null) {
			return EMPTY_STRING;
		}

		return string;
	}

	/**
	 * Converts OrderReturnType to the localized string.
	 * 
	 * @param orderReturnType the order return type
	 * @return the localized name of the order return type
	 */
	public String getLocalizedOrderReturnType(final OrderReturnType orderReturnType) {
		return CoreMessages.get().getMessage(orderReturnType.getPropertyKey());
	}

	/**
	 * Converts OrderReturnStatus to the localized string.
	 * 
	 * @param orderReturnStatus the order return status
	 * @return the localized name of the order return status
	 */
	public String getLocalizedOrderReturnStatus(final OrderReturnStatus orderReturnStatus) {
		return CoreMessages.get().getMessage(orderReturnStatus.getPropertyKey());
	}


	/**
	 * Returns the localized name of the given enum constant.
	 * 
	 * @param enumValue the enum to be localized
	 * @return the localized string for the enum
	 */
	public String getLocalizedName(final OrderShipmentStatus enumValue) {
		return localizedExtensibleEnums.get(enumValue);
	}



	private WarehouseMessages() {

	}

	private void instantiateEnums() {
		if (localizedExtensibleEnums.isEmpty()) {

			localizedExtensibleEnums.put(OrderShipmentStatus.ONHOLD, ShipmentStatus_OnHold);
			localizedExtensibleEnums.put(OrderShipmentStatus.RELEASED, ShipmentStatus_Released);
			localizedExtensibleEnums.put(OrderShipmentStatus.SHIPPED, ShipmentStatus_Completed);
			localizedExtensibleEnums.put(OrderShipmentStatus.CANCELLED, ShipmentStatus_Cancelled);
			localizedExtensibleEnums.put(OrderShipmentStatus.AWAITING_INVENTORY, ShipmentStatus_Awaiting);
			localizedExtensibleEnums.put(OrderShipmentStatus.INVENTORY_ASSIGNED, ShipmentStatus_Assigned);
		}
	}

	/**
	 * Gets the NLS localize message class.
	 * @return the localized message class.
	 */
	public static WarehouseMessages get() {
		WarehouseMessages warehouseMessages = LocalizedMessagePostProcessor.getUTF8Encoded(BUNDLE_NAME, WarehouseMessages.class);
		warehouseMessages.instantiateEnums();
		return warehouseMessages;
	}

}