/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
/**
* Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.pricelistmanager; //NOPMD

import java.lang.reflect.Field;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.cmclient.core.MessageException;
import com.elasticpath.cmclient.core.nls.LocalizedMessagePostProcessor;

/**
 * Messages class for the com.elasticpath.cmclient.pricelistmanager plugin.
 */
@SuppressWarnings({ "PMD.TooManyFields", "PMD.UseSingleton", "PMD.VariableNamingConventions" })
public final class PriceListManagerMessages {
	private static final String BUNDLE_NAME = "com.elasticpath.cmclient.pricelistmanager.PriceListManagerPluginResources"; //$NON-NLS-1$

	public String PriceListEditorTooltip;

	public String PriceList_New;
	
	public String PriceListCsvExport_ActionTooltip;
	
	public String PriceListCsvExport_Action;
	
	public String PriceListEditorError_Title;

	public String Price_List_name_unique;
	
	public String BaseAmountDialogError_Title;

	public String BaseAmountExists;
	
	public String AddEditPriceTierDialog_Duplicate_PriceTier;

	public String PriceListDescriptorSummaryPage_Form_Title;

	public String PriceListDescriptor;

	public String PriceListDescriptorEditorPage_Title;
	
	public String BaseAmountDialog_Title;
	
	public String BaseAmount_Edit;
	
	public String BaseAmount_Add;
	
	public String BaseAmount_Delete;
	
	public String BaseAmount_OpenItemButton;

	public String BaseAmount_ChangeSet;
	
	public String BaseAmount_ObjectType;
	
	public String BaseAmount_ObjectCode;
	
	public String BaseAmount_ProductName;
		
	public String BaseAmount_ProductCode;
	
	public String BaseAmount_SkuCode;
	
	public String BaseAmount_SkuConfiguration;
	
	public String BaseAmountEditorPage_Title;
	
	public String PriceListSearchTab_SearchButton;
	
	public String PriceListSearchResults_TableColumnTitle_Name;
	
	public String PriceListSearchResults_TableColumnTitle_CatalogName;
	
	public String PriceListSearchResults_TableColumnTitle_CurrencyCode;
	
	public String PriceListSearchResults_TableColumnTitle_Description;
	
	public String OpenPriceListEditorActionToolTip;
	
	public String PriceListSummaryPage_UserDefinedSection;

	public String BaseAmount_UnsavedChanges;

	public String BaseAmount_Quantity;
	
	public String BaseAmount_ListPrice;
	
	public String BaseAmount_SalePrice;
	
	public String BaseAmount_PaymentSchedule;
	
	public String BaseAmount_PaymentSchedule_Null;
	
	public String BaseAmount_Delete_Title;
	
	public String BaseAmount_Delete_Message;
	
	public String BaseAmount_Title;
	
	public String PriceListSearchTab_Name;
	
	public String PriceListAssignmentSearchTab_Name;
	
	public String PriceListAssignmentSearchTab_SearchButton;
	
	public String PriceListAssignmentSearchTab_ClearButton;
	
	public String PriceListAssignmentSearchTab_PriceListName;
	
	public String PriceListAssignmentSearchTab_Catalog;
	
	public String PriceListAssignmentSearchTab_Title;
	
	public String PriceListAssignmentSearchTab_AllCatalogs;
	
	public String PriceListAssignmentSearchResult_Name;
	
	public String PriceListAssignmentSearchResult_Description;
	
	public String PriceListAssignmentSearchResult_CatalogName;
	
	public String PriceListAssignmentSearchResult_Priority;
	
	public String PriceListAssignmentSearchResult_PriceListName;
	
	public String PriceListAssignmentSearchResult_StartDate;
	
	public String PriceListAssignmentSearchResult_EndDate;
	
	public String PriceListAssignmentSearchResult_CreateActionTooltip;
	
	public String PriceListAssignmentSearchResult_EditActionTooltip;
	
	public String PriceListAssignmentSearchResult_DeleteActionTooltip;
	
	public String PriceListAssignmentSearchResult_CreateAction;
	
	public String PriceListAssignmentSearchResult_EditAction;
	
	public String PriceListAssignmentSearchResult_DeleteAction;
	
	public String ConfirmDeletePriceListAssignmentMsgBoxTitle;
	
	public String ConfirmDeletePriceListAssignmentMsgBoxText;
	
	public String ConfirmDeletePriceListAssignmentMsgBoxTextShort;
	
	public String PriceListSearchResult_CreateActionTooltip;
	
	public String PriceListSearchResult_EditActionTooltip;
	
	public String PriceListSearchResult_DeleteActionTooltip;

	public String PriceListSearchResult_CreateAction;
	
	public String PriceListSearchResult_EditAction;
	
	public String PriceListSearchResult_DeleteAction;
	
	public String DenyDeletePriceListMsgBoxText;
	
	public String DenyDeletePriceListOfferMsgBoxText;
	
	public String DenyDeletePriceListMsgBoxTitle;
	
	public String DeletePriceList_CanNotRemove;
	
	public String DeletePriceList_CloseEditor;
	
	public String DeletePriceList_BaseAmounts;

	public String ConfirmDeletePriceListMsgBoxText;
	
	public String ConfirmDeletePriceListMsgBoxTitle;

	public String PriceListEditor_DuplicateBaseAmount;

	public String PLA_Wizard_Title_Edit;

	public String PLA_Wizard_Title;

	public String PLA_Wizard_Name_Page_Title;

	public String PLA_Wizard_Stores_Page_Title;

	public String PLA_Wizard_Time_Range_Page_Title;

	public String PLA_Wizard_Shopper_Page_Title;

	public String PLA_Wizard_Name_Page_Title_Edit;

	public String PLA_Wizard_Stores_Page_Title_Edit;

	public String PLA_Wizard_Time_Range_Page_Title_Edit;

	public String PLA_Wizard_Shopper_Page_Title_Edit;

	public String PLA_Wizard_Shopper_Page_Description;

	public String PLA_Wizard_Time_Range_Page_Description;

	public String PLA_Wizard_Stores_Page_Description;
	
	public String TotalLengthOfConditionsReached;

	public String Name_Priorty_Page_Description;

	public String Name_Label;

	public String Description_Label;

	public String Priority_Label;

	public String Priority_Lowest_Label;

	public String Priority_Highest_Label;

	public String NameExists;

	public String PriceListDescriptor_Column_Name;

	public String PriceListDescriptor_Column_Currency;

	public String PriceListDescriptor_Column_Description;

	public String PLA_Wizard_PriceListSelection_Page_Title;

	public String PLA_Wizard_PriceListSelection_Page_Title_Edit;

	public String PLA_Wizard_PriceListSelection_Page_Description;

	public String Catalog_Column_Name;

	public String PLA_Wizard_CatalogSelection_Page_Title;

	public String PLA_Wizard_CatalogSelection_Page_Title_Edit;

	public String PLA_Wizard_CatalogSelection_Page_Description;

	public String PLA_Wizard_Currencies_Label;
	
	public String All_Currencies;

	public String validator_baseAmount_invalidQuantity;
	public String validator_baseAmount_invalidListPrice;
	public String validator_baseAmount_listPriceRequired;
	public String validator_baseAmount_listPriceNonNegative;
	public String validator_baseAmount_listPriceBigDecimal;
	public String validator_baseAmount_invalidSalePrice;
	public String validator_baseAmount_salePriceRequired;
	public String validator_baseAmount_salePriceNonNegative;
	public String validator_baseAmount_salePriceBigDecimal;
	public String validator_baseAmount_salePriceIsMoreThenListPrice;
	//------------------
	public String CSV_Exporting_Prices;
	public String CSV_Retrieving_Data;
	public String CSV_Converting_Data;
	public String CSV_Exporting_File;
	public String CSV_Opening_File;
	public String CSV_Export_Cancelled;
	public String CSV_Export_Failed;

	//----------- base amount filter and search
	public String PriceListBaseAmountFilter_PriceFrom;
	public String PriceListBaseAmountFilter_PriceTo;
	public String PriceListBaseAmountFilter_Quantity;
	public String PriceListBaseAmountFilter_Search;
	public String PriceListBaseAmountSearch_Search;	
	public String PriceListBaseAmountFilter_Clear;
	public String PriceListBaseAmountFilter_AllTypes;
	public String PriceListBaseAmountFilter_Product;
	public String PriceListBaseAmountFilter_Sku;



	/**
	 * Returns string or empty string if the string is null.
	 * 
	 * @param string to return
	 * @return string or empty string if the string is null.
	 */
	public String getString(final String string) {
		if (string == null) {
			return StringUtils.EMPTY;
		}

		return string;
	}
	

	
	/**
	 * Return a message String given the message key.
	 * 
	 * @param messageKey the message key (static field) that holds the message String
	 * @return the message String
	 */
	public String getMessage(final String messageKey) {
		try {
			final Field field = PriceListManagerMessages.class.getField(messageKey);
			return (String) field.get(this);
		} catch (final Exception e) {
			throw new MessageException(e);
		}
	}

	/**
	 * Gets the NLS localize message class.
	 * @return the localized message class.
	 */
	public static PriceListManagerMessages get() {
		return LocalizedMessagePostProcessor.getUTF8Encoded(BUNDLE_NAME, PriceListManagerMessages.class);
	}


	/**
	 * Empty constructor.
	 */
	private PriceListManagerMessages() {
	}


}