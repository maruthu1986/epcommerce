/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog; // NOPMD

import java.lang.reflect.Field;

import com.elasticpath.cmclient.core.MessageException;
import com.elasticpath.cmclient.core.nls.LocalizedMessagePostProcessor;
import com.elasticpath.domain.catalog.ProductBundle;

/**
 * Messages class for the catalog plugin.
 */
@SuppressWarnings({ "PMD.TooManyFields", "PMD.ExcessiveClassLength", "PMD.ExcessivePublicCount",
	"PMD.VariableNamingConventions", "PMD.LongVariable" })
public final class CatalogMessages {
	private static final String BUNDLE_NAME = "com.elasticpath.cmclient.catalog.CatalogPluginResources"; //$NON-NLS-1$

	public static final String EMPTY_STRING = ""; //$NON-NLS-1$

	public static final String NEWLINE = "\n"; //$NON-NLS-1$

	public String NotAvailable;

	private CatalogMessages() {
	}

	// ----------------------------------------------------
	// Global Keys
	// ----------------------------------------------------
	public String Product_NotAvailable;

	public String Item_EnableDate;

	public String Item_DisableDate;

	public String Item_StoreVisible;

	public String Item_NotSoldSeparately;

	public String Item_EnableDateTime;

	public String Item_DisableDateTime;

	// ----------------------------------------------------
	// Catalog Editors package
	// ----------------------------------------------------
	public String CatalogAttributesPage_Title;

	public String CatalogAttributesPage_Form_Title;

	public String CatalogAttributesSection_ButtonText;
	public String CatalogAttributesSection_TableAttributeKey;
	public String CatalogAttributesSection_TableAttributeName;
	public String CatalogAttributesSection_TableAttributeType;
	public String CatalogAttributesSection_TableAttributeUsage;
	public String CatalogAttributesSection_TableAttributeRequired;
	public String CatalogAttributesSection_TableAttributeGlobal;
	public String CatalogAttributesSection_RemoveDialog_title;
	public String CatalogAttributesSection_RemoveDialog_description;
	public String CatalogAttributesSection_ErrorDialog_InUse_title;
	public String CatalogAttributesSection_ErrorDialog_InUse_desc;
	public String CatalogCartItemModifierGroupsSection_ErrorDialog_InUse_desc;

	public String CatalogCartItemModifierGroupsPage_Title;

	public String CatalogCartItemModifierGroupsPage_Form_Title;

	public String CatalogCartItemModifierGroupsPage_TableCodeColumn;
	public String CatalogCartItemModifierGroupsPage_TableDisplayNameColumn;
	public String CatalogCartItemModifierGroupsSection_ButtonText;
	public String CatalogCartItemModifierGroupsSection_RemoveDialog_title;
	public String CatalogCartItemModifierGroupsSectionAddEditDialog_InitMsg_AddNewModifierField;
	public String CatalogCartItemModifierGroupsSectionAddEditDialog_InitMsg_EditAnModifierField;
	public String CatalogCartItemModifierGroupsSectionAddEditDialog_Iitle_AddModifierField;
	public String CatalogCartItemModifierGroupsSectionAddEditDialog_Iitle_EditModifierField;
	public String CatalogCartItemModifierGroupsSectionAddEditDialog_WinIitle_AddModifierField;
	public String CatalogCartItemModifierGroupsSectionAddEditDialog_WinIitle_EditModifierField;
	public String GroupAddEditDialog_AddGroup;
	public String GroupAddEditDialog_EditGroup;
	public String GroupAddEditDialog_GroupCode;
	public String GroupAddEditDialog_GroupName;
	public String GroupAddEditDialog_Add_InitMsg;
	public String GroupAddEditDialog_Edit_InitMsg;
	public String CatalogGroupsSection_RemoveDialog_description;
	public String GroupAddEditDialog_TableCodeColumn;
	public String GroupAddEditDialog_TableDisplayNameColumn;
	public String GroupAddEditDialog_TableDisplayTypeColumn;
	public String GroupAddEditDialog_TableDisplayRequiredColumn;
	public String GroupAddEditDialog_TableAddButton;
	public String GroupAddEditDialog_TableEditButton;
	public String GroupAddEditDialog_TableRemoveButton;
	public String GroupAddEditDialog_ErrorDialog_AddInUse_desc;
	public String GroupAddEditDialog_ErrorDialog_AddInUse_Langdesc;
	public String GroupAddEditDialog_ErrorDialog_noCode_desc;
	public String GroupAddEditDialog_ErrorDialog_noName_desc;
	public String AddEditCartItemModifierFieldDialog_Code;
	public String AddEditCartItemModifierFieldDialog_DisplayName;
	public String AddEditCartItemModifierFieldDialog_FieldType;
	public String AddEditCartItemModifierFieldDialog_MaxSize;
	public String AddEditCartItemModifierFieldDialog_required;
	public String AddEditCartItemModifierFieldDialog_Add;
	public String AddEditCartItemModifierFieldDialog_ErrorDialog_AddInUse_desc;
	public String AddEditCartItemModifierFieldDialog_ErrorDialog_AddInUse_Langdesc;
	public String AddEditCartItemModifierFieldDialog_ErrorDialog_noMaxSize_desc;
	public String AddEditCartItemModifierFieldDialog_ErrorDialog_maxSizeTooLarge;
	public String AddEditCartItemModifierFieldDialog_ErrorDialog_noMulti_desc;
	public String AddEditCartItemModifierFieldDialog_ErrorDialog_noCode_desc;
	public String AddEditCartItemModifierFieldDialog_ErrorDialog_noName_desc;
	public String AddEditCartItemModifierFieldDialog_TableCodeColumn;
	public String AddEditCartItemModifierFieldDialog_TableDisplayNameColumn;
	public String AddEditCartItemModifierFieldDialog_TableAddButton;
	public String AddEditCartItemModifierFieldDialog_TableRemoveButton;
	public String AddEditCartItemModifierFieldDialog_TableEditButton;
	public String AddEditCartItemModifierFieldOptionDialog_Value;
	public String AddEditCartItemModifierFieldOptionDialog_DisplayName;
	public String AddEditCartItemModifierFieldOptionDialog_Add;
	public String AddEditCartItemModifierFieldOptionDialog_ErrorDialog_AddInUse_desc;
	public String AddEditCartItemModifierFieldOptionDialog_ErrorDialog_AddInUse_Langdesc;
	public String AddEditCartItemModifierFieldOptionDialog_ErrorDialog_noValue_desc;
	public String AddEditCartItemModifierFieldOptionDialog_ErrorDialog_noName_desc;
	public String AddEditCartItemModifierFieldOptionDialog_RemoveDialog_title;
	public String AddEditCartItemModifierFieldOptionDialog_InitMsg_AddNewModifierField;
	public String AddEditCartItemModifierFieldOptionDialog_InitMsg_EditAnModifierField;
	public String AddEditCartItemModifierFieldOptionDialog_Iitle_AddModifierField;
	public String AddEditCartItemModifierFieldOptionDialog_Iitle_EditModifierField;
	public String AddEditCartItemModifierFieldOptionDialog_WinIitle_AddModifierField;
	public String AddEditCartItemModifierFieldOptionDialog_WinIitle_EditModifierField;

	public String CartItemModifierFieldTypeName_SHORT_TEXT;

	public String CartItemModifierFieldTypeName_DECIMAL;

	public String CartItemModifierFieldTypeName_BOOLEAN;

	public String CartItemModifierFieldTypeName_DATE_TIME;

	public String CartItemModifierFieldTypeName_PICK_SINGLE_OPTION;

	public String CartItemModifierFieldTypeName_PICK_MULTI_OPTION;

	public String CartItemModifierFieldTypeName_INTEGER;

	public String CartItemModifierFieldTypeName_DATE;

	public String CartItemModifierFieldTypeName_EMAIL;

	public String CatalogBrandsPage_Title;

	public String CatalogBrandsPage_Form_Title;

	public String CatalogBrandsSection_ButtonText;
	public String CatalogBrandsSection_TableBrandCodeColumn;
	public String CatalogBrandsSection_TableBrandNameColumn;
	public String CatalogBrandsSection_RemoveDialog_title;
	public String CatalogBrandsSection_RemoveDialog_description;
	public String CatalogBrandsSection_ErrorDialog_InUse_title;
	public String CatalogBrandsSection_ErrorDialog_InUse_desc;
	public String CatalogBrandsSection_ErrorDialog_AddInUse_title;
	public String CatalogBrandsSection_ErrorDialog_AddInUse_desc;

	public String CatalogCategoryTypesPage_Title;

	public String CatalogCategoryTypesPage_Form_Title;

	public String CatalogCategoryTypes_ButtonText;

	public String CatalogCategoryTypesSection_TableNameColumn;

	public String CatalogCategoryTypesSection_RemoveDialog_title;
	public String CatalogCategoryTypesSection_RemoveDialog_description;
	public String CatalogCategoryTypesSection_ErrorDialog_InUse_title;
	public String CatalogCategoryTypesSection_ErrorDialog_InUse_desc;
	public String CatalogCategoryTypesSection_ErrorDialog_ListingTitle;

	public String CatalogSummaryPage_Title;
	public String CatalogSummaryPage_Form_Title;
	public String CatalogSummaryPage_LocaleOrCurrencyWarningTitle;
	public String CatalogSummaryPage_LocaleOrCurrencyWarningMsg;
	public String CatalogSummaryPage_WarningDescription;

	public String CatalogDetailsSection_LanguageSelection_Available;
	public String CatalogDetailsSection_LanguageSelection_Selected;
	public String CatalogDetailsSection_CurrencySelection_Available;
	public String CatalogDetailsSection_CurrencySelection_Selected;
	public String CatalogDetailsSection_DefaultLanguage;
	public String CatalogDetailsSection_DefaultCurrency;

	public String CatalogEditor_Save_StatusBarMsg;
	public String CatalogEditor_Tooltip;
	public String CatalogEditor_CanNotSaveCatalog;
	public String CatalogEditor_OnSavePrompt;

	public String CatalogProductTypesPage_Title;

	public String CatalogProductTypesPage_Form_Title;

	public String CatalogProductTypesSection_ButtonText;
	public String CatalogProductTypesSection_TableNameColumn;
	public String CatalogProductTypesSection_TableMultipleSkusColumn;
	public String CatalogProductTypesSection_RemoveDialog_title;
	public String CatalogProductTypesSection_RemoveDialog_description;
	public String CatalogProductTypesSection_ErrorDialog_InUse_title;
	public String CatalogProductTypesSection_ErrorDialog_InUse_desc;
	public String CatalogProductTypesSection_ErrorDialog_ListingTitle;

	public String CatalogSkuOptionsPage_Title;

	public String CatalogSkuOptionsPage_Form_Title;

	public String CatalogSkuOptionsSection_TableValueColumn;

	public String CatalogSkuOptionsSection_TableDisplayNameColumn;

	public String CatalogSkuOptionsSection_AddSkuOptionButton;

	public String CatalogSkuOptionsSection_AddSkuOptionValueButton;

	public String CatalogSkuOptionsSection_EditSelectionButton;

	public String CatalogSkuOptionsSection_RemoveSelectionButton;

	public String CatalogSkuOptionsSection_RemoveSelectionDialogTitle;

	public String CatalogSkuOptionsSection_RemoveSelectionDialogMessage;

	public String CatalogSkuOptionsSection_MoveValueUpButton;

	public String CatalogSkuOptionsSection_MoveValueDownButton;
	public String CatalogSkuOptionsSection_ErrorDialog_InUse_title;
	public String CatalogSkuOptionsSection_ErrorDialog_InUse_desc;

	public String CatalogSkuOptionsSection_ErrorDialog_ShipableOrDigital;

	public String CatalogSummarySection_CatalogCode;
	public String CatalogSummarySection_CatalogName;
	public String CatalogSummarySection_AvailableLanguages;
	public String CatalogSummarySection_SelectedLanguages;
	public String CatalogSummarySection_DefaultLanguage;

	public String CatalogSynonymGroupsPage_Title;
	public String CatalogSynonymGroupsPage_Form_Title;

	public String CatalogSynonymGroupsSection_ButtonText;
	public String CatalogSynonymGroupsSection_TableConceptTerm;
	public String CatalogSynonymGroupsSection_TableSynonyms;
	public String CatalogSynonymGroupsSection_RemoveDialog_title;
	public String CatalogSynonymGroupsSection_RemoveDialog_description;

	// ----------------------------------------------------
	// Category Editors package
	// ----------------------------------------------------
	public String CategorySummaryPage_Title;

	public String CategorySummaryPage_Form_Title;

	public String CategoryEditor_Save_StatusBarMsg;

	public String CategoryEditor_OnSavePrompt;

	public String CategoryEditor_Tooltip;

	public String CategoryEditorOverviewSection_CategoryCode;

	public String CategoryEditorOverviewSection_CategoryName;

	public String CategoryEditorOverviewSection_CategoryType;

	public String CategoryEditorOverviewSection_ParentCategory;

	public String CategoryEditorOverviewSection_Duplicate_Code;

	public String CategoryAttributePage_Title;

	// ----------------------------------------------------
	// Product Editors package
	// ----------------------------------------------------
	public String ProductAttributePage_Title;

	public String ProductAttributePage_Form_Title;

	public String ProductSummaryPage_Title;

	public String ProductSummaryPage_Form_Title;

	public String ProductEditor_Save_StatusBarMsg;

	public String ProductEditor_OnSavePrompt;

	public String ProductEditor_Save_Error_SkuExists;

	public String ProductEditor_RequireProductShipType;

	public String ProductEditor_SingleSku_Tooltip;

	public String ProductEditor_MultiSku_Tooltip;

	public String ProductEditorAttributeSection_Title;

	public String ProductEditorSummaySection_Title;

	public String ProductEditorSummaySection_ProductCode;

	public String ProductEditorSummaySection_ProductName;

	public String ProductEditorSummaySection_ProductType;

	public String ProductEditorSummaySection_TaxCode;

	public String ProductEditorSummaySection_Brand;

	public String ProductEditorSummaySection_Duplicate_Code;

	public String ProductSaveBundleCyclicDependencyErrorTitle;

	public String ProductSaveBundleCyclicDependencyErrorMsg1;

	public String ProductSaveBundleCyclicDependencyErrorMsg2;

	public String ProductSaveInvalidSelectionRuleErrorMsg;

	public String ProductSaveInvalidSelectionRuleTitle;

	public String ProductSaveRecurringChargeOnAssignedBundleErrorTitle;

	public String ProductSaveRecurringChargeOnAssignedBundleErrorMsg;

	public String ProductSaveRecurringChargeOnAssignedBundleErrorMsgWithParam;

	// Store Rule section
	public String ProductEditorStoreRuleSection_Title;

	public String ProductEditorStoreRuleSection_AllStoresMessage;

	public String ProductEditorStoreRuleSection_StoreVisible;

	public String ProductEditorStoreRuleSection_AvailFromDate;

	public String ProductEditorStoreRuleSection_AvailToDate;

	public String ProductEditorStoreRuleSection_MinOrderQty;

	public String ProductEditorStoreRuleSection_MinQtyLimit;

	public String ProductEditorStoreRuleSection_ExpReleaseDate;

	public String ProductEditorStoreRuleSection_AvailabilityRule;

	public String ProductEditorStoreRuleSection_AlwaysAvailable;

	public String ProductEditorStoreRuleSection_AvailableForBackOrder;

	public String ProductEditorStoreRuleSection_AvailableForPreOrder;

	public String ProductEditorStoreRuleSection_AvailableWhenInStock;

	public String ProductEditorStoreRuleSection_BundleRules;

	public String ProductEditorStoreRuleSection_EffectiveRules;


	// Product price page
	public String ProductPricePage_Title;

	public String ProductPricePage_Form_Title;

	public String ProductPricePage_ProductName;

	public String ProductPricePage_PriceListCaption;

	public String ProductEditorPriceSection_Title;

	public String ProductEditorPriceSection_TableColumnTitle_Quantity;

	public String ProductEditorPriceSection_TableColumnTitle_RegPrice;

	public String ProductEditorPriceSection_TableColumnTitle_SalePrice;

	public String ProductEditorSection_AddPriceTierButton;

	public String ProductEditorSection_EditPriceTierButton;

	public String UsedWithTheFollowingCatalogs;

	public String ProductEditorSection_RemovePriceTierButton;

	public String ProductEditorSection_RemovePriceTierConfirm;

	public String ProductEditorSection_RemovePriceTierWarning;

	public String ProductEditorSection_RemovePriceTierConfirmMsg;

	public String ProductEditorSection_RemoveFirstPriceTierMsg;

	public String ProductEditorSection_OverrideConfirm;

	public String ProductEditorSection_OverrideConfirmMsg;

	public String ProductEditorSection_UndoOverrideConfirm;

	public String ProductEditorSection_UndoOverrideConfirmMsg;

	public String ProductEditorSection_OverrideButton;

	public String ProductEditorSection_UndoOverrideButton;

	// Product Editor - SKU Pages
	public String ProductEditorSkuSection_Description;

	public String ProductSingleSkuPage_Title;

	public String ProductMultiSkuPage_Title;

	// Product Editor - SKU Page - Overview Section
	public String ProductEditorSingleSkuOverview_Title;

	public String ProductEditorSingleSkuOverview_DigitalAsset;

	public String ProductEditorSingleSkuOverview_Shippable;

	public String ProductEditorSingleSkuOverview_ShippableType;

	public String ProductEditorSingleSkuOverview_DigitalAssetDownloadable;

	public String ProductEditorSingleSkuOverview_DisableDateTime;

	public String ProductEditorSingleSkuOverview_EnableDateTime;

	public String ProductEditorSingleSkuOverview_SkuCode;

	public String ProductEditorSingleSkuOverview_SkuConfiguration;

	public String ProductEditorSingleSkuOverview_ParentProduct;

	public String ProductEditorSingleSkuOverview_TaxCode;

	public String ProductEditorSingleSkuOverview_TaxCodeOption_NotApplicable;

	// Product Editor - SKU Page - Digital Asset Section
	public String ProductEditorSingleSkuDigAsset_Title;

	public String ProductEditorSingleSkuDigitalAsset_DownloadExpiry;

	public String ProductEditorSingleSkuDigitalAsset_DownloadLimit;

	public String ProductEditorSingleSkuDigitalAsset_DownloadLimitExpl;

	public String ProductEditorSingleSkuDigitalAsset_DownloadExpiryExpl;

	public String ProductEditorSingleSkuDigitalAsset_File;

	// Product Editor - SKU Page - Shipping Section
	public String ProductEditorSingleSkuShipping_Title;

	public String ProductEditorSingleSkuInventory_Title;

	public String ProductEditorSingleSkuShipping_AllocatedQuantity;

	public String ProductEditorSingleSkuShipping_AvailableQuantity;

	public String ProductEditorSingleSkuShipping_cm;

	public String ProductEditorSingleSkuShipping_ExpectedRestockDate;

	public String ProductEditorSingleSkuShipping_InventoryDetails;

	public String ProductEditorSingleSkuShipping_Warehouse;

	public String ProductEditorSingleSkuShipping_kg;

	public String ProductEditorSingleSkuShipping_OnHandQuantity;

	public String ProductEditorSingleSkuShipping_ReorderMinimum;

	public String ProductEditorSingleSkuShipping_ReorderQuantity;

	public String ProductEditorSingleSkuShipping_ReservedQuantity;

	public String ProductEditorSingleSkuShipping_ShippingHeight;

	public String ProductEditorSingleSkuShipping_ShippingLength;

	public String ProductEditorSingleSkuShipping_ShippingWeight;

	public String ProductEditorSingleSkuShipping_ShippingWidth;

	public String ProductEditorSingleSkuShipping_TrackInventory;

	// Product Editor - Multi SKU Page
	public String ProductEditorMultiSkuSection_Description;

	public String ProductEditorMultiSkuSection_Title;

	public String ProductEditorMultiSkuSection_AddButton;

	public String ProductEditorMultiSkuSection_EditButton;

	public String ProductEditorMultiSkuSection_RemoveButton;

	public String ProductEditorMultiSkuSection_SkuCode;

	public String ProductEditorMultiSkuSection_SkuConfiguration;

	public String ProductEditorMultiSkuSection_SkuDigAsset;

	public String ProductEditorMultiSkuSection_SkuShippable;

	public String ProductEditorMultiSkuSection_SkuDisableDate;

	public String ProductEditorMultiSkuSection_SkuEnableDate;

	public String ProductEditorMultiSkuSection_Info;

	public String ProductEditorMultiSkuSection_Question;

	public String ProductEditorMultiSkuSection_RemoveConfirmation;

	public String ProductEditorMultiSkuSection_Auto_Change_Association;

	public String ProductEditorMultiSkuSection_CanNotRemove;

	public String ProductEditorMultiSkuSection_CloseEditor;

	public String ProductEditorMultiSkuSection_CanNotRemoveMsg;

	public String ProductEditorMultiSkuSection_Yes;

	public String ProductEditorMultiSkuInfoSection_Change;

	public String ProductEditorMultiSkuInfoSection_Title;

	public String ProductEditorMultiSkuInfoSection_Description;

	// Product SKU Editor
	public String ProductSkuEditor_Save_StatusBarMsg;

	public String ProductSkuEditor_SingleSku_Tooltip;

	public String ProductSkuEditor_MultiSku_Tooltip;

	public String ProductSkuEditor_OnSavePrompt;

	// Product Attributes dialog
	public String EditProductAttributes_DialogTitle;

	// ----------------------------------------------------
	// Views package
	// ----------------------------------------------------

	// Catalog Browse View
	public String CatalogBrowseView_LabelProvider_DefaultText;

	// Catalog actions & tool-tips
	public String CatalogBrowseView_Action_OpenCatalogCategory;

	public String CatalogBrowseView_Action_DeleteCatalogCategory;

	public String CatalogBrowseView_Action_CreateCategory;

	public String CatalogBrowseView_Action_CreateSubCategory;

	public String CatalogBrowseView_Action_CreateProduct;

	public String CatalogBrowseView_Action_CreateProductBundle;

	public String CatalogBrowseView_Action_AddExistingProduct;

	public String CatalogBrowseView_Action_AddLinkedCategory;

	public String CatalogBrowseView_Action_RemoveLinkedCategory;

	public String CatalogBrowseView_Action_CanNotRemove;

	public String CatalogBrowseView_Action_RemoveLinkedCategories;

	public String CatalogBrowseView_Action_Exclude;

	public String CatalogBrowseView_Action_Include;

	public String CatalogBrowseView_Action_DeleteCatalogDialogTitle;

	public String CatalogBrowseView_Action_DeleteCatalogDialogText;

	public String CatalogBrowseView_Action_DeleteCatalog;

	public String CatalogBrowseView_Action_DeleteCatalogErrorTitle;

	public String CatalogBrowseView_Action_DeleteCatalog_CloseEditor;

	public String CatalogBrowseView_Action_DeleteCatalog_InUse;

	public String CatalogBrowseView_Action_DeleteCatalog_InUseByStore;

	public String CatalogBrowseView_Action_DeleteCategoryDialogTitle;

	public String CatalogBrowseView_Action_DeleteCategoryDialogText;

	public String CatalogBrowseView_Action_DeleteCategory;

	public String CatalogBrowseView_Action_DeleteCategory_CloseEditor;

	public String CatalogBrowseView_Action_RemoveLinkedCatDialogTitle;

	public String CatalogBrowseView_Action_RemoveLinkedCatDialogText;

	public String CatalogBrowseView_Action_RemoveLinkedCategory_Close;

	public String CatalogBrowseView_Action_ReorderCategoryUp;

	public String CatalogBrowseView_Action_ReorderCategoryDown;

	public String CatalogBrowseView_Action_Refresh;

	// Product Search View
	public String ProductBundle_Tab_Title;

	public String SearchView_Search_Label_ProductName;

	public String SearchView_Search_Label_ProductCode;

	public String SearchView_Search_Label_SkuCode;

	public String SearchView_SearchButton;

	public String SearchView_ClearButton;

	public String SearchView_FiltersGroup;

	public String SearchView_Filter_Label_Brand;

	public String SearchView_Filter_Label_Catalog;

	public String SearchView_SearchTermsGroup;

	public String SearchView_Error_NoSearchTerms;

	public String SearchView_Filter_Label_ProductActiveOnly;
	public String SearchView_Filter_Label_SKUActiveOnly;

	public String SearchView_Filter_Brand_All;

	public String SearchView_Filter_Catalog_All;

	public String SearchView_Sort_StartDate;
	public String SearchView_Sort_EndDate;
	public String SearchView_Sort_Brand;
	public String SearchView_Sort_DefaultCategory;
	public String SearchView_Sort_ProductName;
	public String SearchView_Sort_ProductCode;
	public String SearchView_Sort_ProductType;

	// Product List View
	public String ProductListView_TableColumnTitle_ProductCode;

	public String ProductListView_TableColumnTitle_ProductName;

	public String ProductListView_TableColumnTitle_ProductType;

	public String ProductListView_TableColumnTitle_Brand;

	public String ProductListView_TableColumnTitle_DefaultCategory;

	public String ProductListView_TableColumnTitle_Price;

	public String ProductListView_TableColumnTitle_Active;

	public String ProductListView_TableColumnTitle_Included;

	public String ProductListView_Active_Yes;

	public String ProductListView_Active_No;

	public String ProductListView_No_Result_Found;

	// Sku Search View
	public String SkuBundle_Tab_Title;

	public String SearchView_SkuOptionFiltersGroup;

	public String SearchView_Add_Another_SkuOptionFilter_Label;

	public String SearchView_Filter_Label_SkuOptions;

	public String SearchView_Filter_Label_SkuOptionValues;

	public String SearchView_Filter_SkuOption_All;

	public String SearchView_Filter_Remove_Sku_Option_Filter_Section;

	// Sku List View
	public String SkuListView_TableColumnTitle_SkuCode;

	public String SkuListView_TableColumnTitle_ProductName;

	public String SkuListView_TableColumnTitle_SkuConfiguration;

	public String SkuListView_TableColumnTitle_Brand;

	public String SkuListView_TableColumnTitle_Active;

	public String SkuListView_Active_Yes;

	public String SkuListView_Active_No;

	public String SkuListView_No_Result_Found;

	// ----------------------------------------------------
	// Temporary text
	// ----------------------------------------------------
	public String DefaultAction1_Title;

	public String DefaultAction1_Tooltip;

	public String DefaultAction1_MsgBox_Text;

	public String DefaultAction2_Title;

	public String DefaultAction2_Tooltip;

	public String DefaultAction2_MsgBox_Text;

	public String Default_MsgBox_Title;

	public String DefaultDblClick_MsgBox_Text;

	// ----------------------------------------------------
	// Product Actions text and tipText
	// ----------------------------------------------------
	public String CreateProductAction;

	public String EditProductAction;

	public String DeleteProductAction;

	public String IncludeProductAction;

	public String IncludeProduct_SaveDirtyEditor;

	public String ExcludeProductAction;

	public String ExcludeProduct_SaveDirtyEditor;
	
	public String DeleteProduct_MsgBox_Title;

	public String DeleteProduct_MsgBox_Content;

	public String DeleteProduct_CanNotRemoveProductInChangeSet;

	public String DeleteProduct_CanNotRemove;

	public String DeleteProduct_CanNotRemoveProductInOrder;

	public String DeleteProduct_CanNotRemoveShippingMsg;

	public String DeleteProduct_CanNotRemoveBundleMsg;

	public String DeleteProduct_CloseEditor;

	public String DeleteProductSku_CanNotRemoveBundleMsg;

	public String DeleteProductSku_CanNotRemove;


	// ----------------------------------------------------
	// Production Attribute section
	// ----------------------------------------------------
	public String ProductEditorAttributeSection_MsgTitle;

	public String ProductEditorAttributeSection_Msg;

	public String ProductEditorAttributeSection_TableColumnTitle_Name;

	public String ProductEditorAttributeSection_TableColumnTitle_Type;

	public String ProductEditorAttributeSection_TableColumnTitle_Value;

	public String ProductEditorAttributeSection_TableColumnTitle_Required;

	public String ProductEditorAttributeSection_TableColumnTitle_MLang;

	public String ProductEditorAttributeSection_MultiValue;

	public String ProductEditorAttributeSection_Yes;

	public String ProductEditorAttributeSection_No;

	public String AttributeBooleanDialog_Value;

	public String AttributeBooleanDialog_SetBooleanValue_Msg;

	public String AttributeBooleanDialog_Title;

	public String AttributeBooleanDialog_WindowTitle;

	public String AttributeDecimalDialog_Value;

	public String AttributeDecimalDialog_SetDecimalValue_Msg;

	public String AttributeDecimalDialog_Title;

	public String AttributeDecimalDialog_WindowTitle;

	public String AttributeIntegerDialog_Value;

	public String AttributeIntegerDialog_SetIntegerValue_Msg;

	public String AttributeIntegerDialog_Title;

	public String AttributeIntegerDialog_WindowTitle;

	public String AttributeLongTextDialog_Value;

	public String AttributeLongTextDialog_SetLongTextValue_Msg;

	public String AttributeLongTextDialog_Title;

	public String AttributeLongTextDialog_WindowTitle;

	public String AttributeDateTimeDialog_Value;

	public String AttributeDateTimeDialog_SetDateTimeValue_Msg;

	public String AttributeDateTimeDialog_Title;

	public String AttributeDateTimeDialog_WindowTitle;

	public String AttributeShortTextDialog_Value;

	public String AttributeShortTextDialog_SetShortTextValue_Msg;

	public String AttributeShortTextDialog_Title;

	public String AttributeShortTextDialog_WindowTitle;

	public String AttributeImageDialog_Value;

	public String AttributeImageDialog_SetImageValue_Msg;

	public String AttributeImageDialog_Title;

	public String AttributeImageDialog_WindowTitle;

	public String AttributeDialog_ButtonSet;

	public String AttributePage_ButtonEdit;

	public String AttributePage_ButtonReset;

	// ----------------------------------------------------
	// Create Catalog Dialog
	// ----------------------------------------------------
	public String AddEditGlobalAttributesDialog_Title;
	public String AddEditGlobalAttributesDialog_WindowTitle;

	public String AddEditSynonymGroupDialog_ConceptTerm;
	public String AddEditSynonymGroupDialog_SynonymsGroupLabel;
	public String AddEditSynonymGroupDialog_Title_Add;
	public String AddEditSynonymGroupDialog_Title_Edit;
	public String AddEditSynonymGroupDialog_WindowTitle_Add;
	public String AddEditSynonymGroupDialog_WindowTitle_Edit;
	public String AddEditSynonymGroupDialog_Error_AtLeastOneSynonym;
	public String AddEditSynonymGroupDialog_Error_LineLength;
	public String AddEditSynonymGroupDialog_Error_SameAsConceptTerm;
	public String AddEditSynonymGroupDialog_Error_ConceptTermExists;

	public String CreateCatalogDialog_Title;

	public String CreateCatalogDialog_WindowTitle;

	public String CreateCatalogDialog_CatalogName_Label;

	public String CreateCatalogDialog_DefaultLanguage_Label;

	public String CreateCatalogDialog_DefaultCurrency_Label;

	public String CreateCatalogDialog_CatalogNameExists_ErrorMessage;

	public String CreateCatalogDialog_EmptyLanguageSelection_ErrorMessage;

	public String CreateCatalogDialog_EmptyDefaultLanguage_ErrorMessage;

	public String CreateCatalogDialog_CatalogCodeExists_ErrorMessage;

	public String CreateCatalogDialog_AvailableCurrency_Label;

	public String CreateCatalogDialog_SelectedCurrency_Label;

	public String CreateCatalogDialog_AvailableLanguage_Label;

	public String CreateCatalogDialog_SelectedLanguage_Label;

	public String CreateCatalogDialog_CannotRemoveLocale_Title;

	public String CreateCatalogDialog_CannotRemoveCurrency_Title;

	public String CreateCatalogDialog_CannotRemoveLocale_Message;

	public String CreateCatalogDialog_CannotRemoveCurrency_Message;

	// ----------------------------------------------------
	// Create Virtual Catalog Dialog
	// ----------------------------------------------------
	public String VirtualCatalogDialog_Create_Title;

	public String VirtualCatalogDialog_Edit_Title;

	public String VirtualCatalogDialog_Open_Title;

	public String VirtualCatalogDialog_Create_WindowTitle;

	public String VirtualCatalogDialog_Edit_WindowTitle;

	public String VirtualCatalogDialog_Open_WindowTitle;

	public String VirtualCatalogDialog_CatalogName_Label;

	public String VirtualCatalogDialog_DefaultLanguage_Label;

	public String VirtualCatalogDialog_DefaultLanguage_ComboEntry;

	public String VirtualCatalogDialog_CatalogNameExists_ErrMsg;

	public String VirtualCatalogDialog_Assigned_Change_Set;

	public String VirtualCatalogDialog_AddedBy;

	public String VirtualCatalogDialog_DateAdded;

	public String VirtualCatalogDialog_ChangeSet;

	// ----------------------------------------------------
	// Create Category Wizard
	// ----------------------------------------------------
	public String CreateCategoryWizard_Title;

	public String CreateCategoryWizard_Error_DuplicateCode;

	public String CreateCategoryWizard_Error_DuplicateName;

	public String CreateCategoryWizard_Error_RequiredAttributes;

	// Page 1
	public String CreateCategoryWizardDetailsPage_Title;

	public String CreateCategoryWizardDetailsPage_Description;

	public String CreateCategoryWizardDetailsPage_Label_CategoryCode;

	public String CreateCategoryWizardDetailsPage_Label_CategoryName;

	public String CreateCategoryWizardDetailsPage_Label_CategoryType;

	public String CreateCategoryWizardDetailsPage_Label_Catalog;

	public String CreateCategoryWizardDetailsPage_Label_ParentCategory;

	public String CreateCategoryWizardDetailsPage_Label_EnableDate;

	public String CreateCategoryWizardDetailsPage_Label_DisableDate;

	public String CreateCategoryWizardDetailsPage_Label_VisibleInStore;

	public String CreateCategoryWizardDetailsPage_NotAvailable;

	public String CreateCategoryWizardDetailsPage_CategoryType_Select;

	public String CreateCategoryWizardDetailsPage_DisplayNameRequired;

	// Page 2
	public String CreateCategoryWizardAttributesPage_Title;

	public String CreateCategoryWizardAttributesPage_Description;



	// ----------------------------------------------------
	// Product Finder Dialog
	// ----------------------------------------------------
	public String ProductFinderDialog_ErrorMsg_SelectPro;

	public String ProductFinderDialog_ProductName;

	public String ProductFinderDialog_ProductSKU;

	public String ProductFinderDialog_CategoryCode;

	public String ProductFinderDialog_ProductCode;

	public String ProductFinderDialog_MultiSku;

	public String ProductFinderDialog_SingleSku;

	public String AddEditPriceTierDialog_Duplicate_PriceTier;

	public String AddEditPriceTierDialog_GreaterSalePriceMsg;

	public String AddEditPriceTierDialog_Quantity;

	public String AddEditPriceTierDialog_ListPrice;

	public String AddEditPriceTierDialog_SalePrice;

	// Product Finder dialog
	public String ProductFinderDialog_Brand;

	public String ProductFinderDialog_Price;

	public String ProductFinderDialog_Search;

	public String ProductFinderDialog_FindAProduct;

	public String ProductFinderDialog_Filters;

	public String ProductFinderDialog_Clear;

	public String ProductFinderDialog_ButtonSave;

	public String ProductFinderDialog_ButtonCancel;

	public String ProductFinderDialog_Results;

	public String ProductFinderDialog_ProductResults;

	public String ProductFinderDialog_Title;

	public String ProductFinderDialog_WindowTitle;

	public String ProductFinderDialog_NoResultsFound;

	// Sku Finder dialog
	public String SkuFinderDialog_ErrorMsg_SelectSku;

	public String SkuFinderDialog_ErrorMsg_HasMultiSku;

	public String SkuFinderDialog_ProductName;

	public String SkuFinderDialog_ProductSkuCode;

	public String SkuFinderDialog_Brand;

	public String SkuFinderDialog_Price;

	public String SkuFinderDialog_Search;

	public String SkuFinderDialog_SelectASku;

	public String SkuFinderDialog_Filters;

	public String SkuFinderDialog_Clear;

	public String SkuFinderDialog_ButtonCancel;

	public String SkuFinderDialog_Results;

	public String SkuFinderDialog_SkuResults;

	public String SkuFinderDialog_Title;

	public String SkuFinderDialog_WindowTitle;

	public String SkuFinderDialog_NoResultsFound;

	// category finder dialog window
	public String CategoryFinderDialog_ParentCategory;

	public String CategoryFinderDialog_Search;

	public String CategoryFinderDialog_FindACategory;

	public String CategoryFinderDialog_Clear;

	public String CategoryFinderDialog_ButtonSave;

	public String CategoryFinderDialog_ButtonCancel;

	public String CategoryFinderDialog_Results;

	public String CategoryFinderDialog_CategoryResults;

	public String CategoryFinderDialog_Title;

	public String CategoryFinderDialog_WindowTitle;

	public String CategoryFinderDialog_ErrorMsg_SelectCategory;

	public String CategoryFinderDialog_CategoryName;

	public String CategoryFinderDialog_categoryCode;

	public String CategoryFinderDialog_NoResultsFound;

	public String AddPriceTierDialog_Title;

	public String EditPriceTierDialog_Title;

	public String AddPriceTierDialog_WindowTitle;

	public String EditPriceTierDialog_WindowTitle;


	// Create/Edit SKU Option Value
	public String ProductSkuCreateDialog_AddValue;

	public String ProductSkuCreateDialog_DisableDate;

	public String ProductSkuCreateDialog_EnableDate;

	public String ProductSkuCreateDialog_InitialMessage;

	public String ProductSkuCreateDialog_Select;

	public String ProductSkuCreateDialog_SkuCode;

	public String ProductSkuCreateDialog_SkuOptions;

	public String ProductSkuCreateDialog_TaxCode;

	public String ProductSkuCreateDialog_TaxCodeOption_NotApplicable; 

	public String ProductSkuCreateDialog_Title;

	public String ProductSkuCreateDialog_WindowTitle;

	public String ProductSkuCreateDialog_Sku_Exist;

	public String ProductSkuCreateDialog_SkuCode_Exist;

	public String ProductSkuCreateDialog_SkuCode_Required;

	public String ProductSkuCreateDialog_ShippableType;

	public String ProductSkuOptionValueDialog_DisplayName;

	public String ProductSkuOptionValueDialog_InitialMessage_Add;

	public String ProductSkuOptionValueDialog_InitialMessage_Edit;

	public String ProductSkuOptionValueDialog_Name;

	public String ProductSkuOptionValueDialog_Title_Add;

	public String ProductSkuOptionValueDialog_Title_Edit;

	public String ProductSkuOptionValueDialog_WindowTitle_Add;

	public String ProductSkuOptionValueDialog_WindowTitle_Edit;

	public String ProductSkuOptionValueDialog_OptionValueKey_Exist;

	public String SelectTemplateDialog_DefaultTypeComboEntry;

	// Product Image Page
	public String ProductImagePage_Title;

	public String ProductImageForm_Title;

	public String ProductImage_File;

	// Attribute Add Dialog
	public String AttributeAddDialog_Btn_Add;
	public String AttributeAddDialog_Btn_Edit;
	public String AttributeAddDialog_AttributeKey;
	public String AttributeAddDialog_AttributeName;
	public String AttributeAddDialog_AttributeUsage;
	public String AttributeAddDialog_AttributeType;
	public String AttributeAddDialog_MultiLanguage;
	public String AttributeAddDialog_RequiredAttribute;
	public String AttributeAddDialog_MultiValuesAllowed;
	public String AttributeAddDialog_InitMsg_AddNewAttribute;
	public String AttributeAddDialog_InitMsg_EditAnAttribute;
	public String AttributeAddDialog_Title_Add;
	public String AttributeAddDialog_Title_Edit;
	public String AttributeAddDialog_WinTitle_Add;
	public String AttributeAddDialog_WinTitle_Edit;
	public String AttributeAddDialog_DefaultUsageComboEntry;
	public String AttributeAddDialog_DefaultTypeComboEntry;
	public String AttributeAddDialog_KeyExists_ErrMsg;
	public String AttributeAddDialog_NameExists_ErrMsg;


	//Product Merchandising Associations Page
	public String ProductMerchandisingAssociationPage_Title;
	public String ProductMerchandisingAssociationSection_Title;
	public String ProductMerchandisingAssociationSection_Cross_Sell;
	public String ProductMerchandisingAssociationSection_Up_Sell;
	public String ProductMerchandisingAssociationSection_Warranty;
	public String ProductMerchandisingAssociationSection_Accessory;
	public String ProductMerchandisingAssociationSection_Replacement;
	public String ProductMerchandisingAssociationSection_Product_Code;
	public String ProductMerchandisingAssociationSection_Proudct_Name;
	public String ProductMerchandisingAssociationSection_Enable_Date;
	public String ProductMerchandisingAssociationSection_Disable_Date;

	public String ProductMerchandisingAssociationSection_Move_Up;
	public String ProductMerchandisingAssociationSection_Move_Down;
	public String ProductMerchandisingAssociationSection_Add;
	public String ProductMerchandisingAssociationSection_Edit;
	public String ProductMerchandisingAssociationSection_Remove;

	public String ProductMerchandisingAssociationDialog_Add_Cross_Sell;
	public String ProductMerchandisingAssociationDialog_Add_Up_Sell;
	public String ProductMerchandisingAssociationDialog_Add_Warranty;
	public String ProductMerchandisingAssociationDialog_Add_Accessory;
	public String ProductMerchandisingAssociationDialog_Add_Replacement;


	public String ProductMerchandisingAssociationDialog_Edit_Cross_Sell;
	public String ProductMerchandisingAssociationDialog_Edit_Up_Sell;
	public String ProductMerchandisingAssociationDialog_Edit_Warranty;
	public String ProductMerchandisingAssociationDialog_Edit_Accessory;
	public String ProductMerchandisingAssociationDialog_Edit_Replacement;

	public String ProductMerchandisingAssociationDialog_Add;
	public String ProductMerchandisingAssociationDialog_Set;
	public String ProductMerchandisingAssociationDialog_Product_Not_Exist;
	public String ProductMerchandisingAssociationDialog_Product_Not_In_Catalog;
	public String ProductMerchandisingAssociationDialog_Self_Assocation;
	public String ProductMerchandisingAssociationDialog_RemoveMsg;
	public String ProductMerchandisingAssociationDialog_RemoveTitle;
	public String ProductMerchandisingAssociationDialog_Enable_Date;
	public String ProductMerchandisingAssociationDialog_Disable_Date;
	public String ProductMerchandisingAssociationDialog_Default_Qty;
	public String ProductMerchandisingAssociationDialog_Product_Code;

	//Category Featured Products page
	public String CategoryFeaturedProductsPage_Title;
	public String CategoryFeaturedProductsForm_Title;
	public String CategoryEditorFeaturedProductsSection_Title;

	public String CategoryFeaturedProductsSection_Move_Up;
	public String CategoryFeaturedProductsSection_Move_Down;
	public String CategoryFeaturedProductsSection_Add;
	public String CategoryFeaturedProductsSection_Remove;

	public String CategoryFeaturedDialog_RemoveTitle;
	public String CategoryFeaturedDialog_RemoveMsg;
	public String CategoryFeaturedDialog_AddWarningTitle;
	public String CategoryFeaturedDialog_AddWarningMsg;

	//Add Sku Wizard
	public String AddSkuWizardPage1_Msg;
	public String AddSkuWizard_Step1;
	public String AddSkuWizard_Title;
	public String AddSkuWizardPage2_Msg;
	public String AddSkuWizard_Step2;
	public String AddSkuWizard_Required_Attribute_Msg;
	public String AddSkuWizard_Validation_Error_Title;

	public String AddSkuWizard_CannotClearAttributeValue;
	public String AddSkuWizard_CannotEditAttributeValue;


	// Category Assignment page.
	public String ProductCategoryAssignmentPage_Title;
	public String CategoryAssignmentPage_VirtualCatalogAssignments;
	public String CategoryAssignmentPage_PrimaryCategory;
	public String CategoryAssignmentPage_Button_Add;
	public String CategoryAssignmentPage_Button_Add_Or_Include;
	public String CategoryAssignmentPage_Button_Remove;
	public String CategoryAssignmentPage_Button_Exclude;
	public String CategoryAssignmentPage_Featured;
	public String CategoryAssignmentPage_CategoryCode;
	public String CategoryAssignmentPage_CategoryName;
	public String CategoryAssignmentPage_CategoryLinkedFlag;
	public String CategoryAssignmentPage_CategoryLinkedFlag_Yes;
	public String CategoryAssignmentPage_CategoryLinkedFlag_No;
	public String CategoryAssignmentPage_VirtualCatalogName;
	public String CategoryAssignmentPage_RemovePrimaryWarningMsg;
	public String CategoryAssignmentPage_RemoveConfirmMsg;
	public String CategoryAssignmentPage_RemoveConfirmTitle;
	public String CategoryAssignmentPage_RemoveVirtualConfirmMsg;
	public String CategoryAssignmentPage_RemovePrimaryWarningTitle;

	// Catalog SKU Option Add/Edit dialog.
	public String SKUOptionAddDialog_Code;
	public String SKUOptionAddDialog_DisplayName;
	public String SKUOptionAddDialog_AddSkuOption;
	public String SKUOptionAddDialog_EditSkuOption;
	public String SKUOptionAddDialog_AddSkuOptionValue;
	public String SKUOptionAddDialog_EditSkuOptionValue;
	public String SKUOptionAddDialog_Image;
	public String SKUOptionAddDialog_DuplicateKeyMsg;

	// Catalog Brand Add/Edit dialog.
	public String BrandAddEditDialog_BrandName;
	public String BrandAddEditDialog_BrandCode;
	public String BrandAddEditDialog_AddBrand;
	public String BrandAddEditDialog_EditBrand;
	public String BrandAddEditDialog_DuplicateKeyMsg;

	// Product Type add/edit wizard.
	public String ProductTypeAddEditWizard_AddWindowTitle;
	public String ProductTypeAddEditWizard_EditWindowTitle;
	public String ProductTypeAddEditWizard_Name;
	public String ProductTypeAddEditWizard_DefaultTaxCode;
	public String ProductTypeAddEditWizard_AvailableAttributes;
	public String ProductTypeAddEditWizard_AssignedAttributes;
	public String ProductTypeAddEditWizard_ProductMultiSku;
	public String ProductTypeAddEditWizard_ProductExcludedFromDiscount;
	public String ProductTypeAddEditWizard_Button_Back;
	public String ProductTypeAddEditWizard_Button_Next;
	public String ProductTypeAddEditWizard_Button_Finish;
	public String ProductTypeAddEditWizard_ProductInitMsg;
	public String ProductTypeAddEditWizard_SkuInitMsg;
	public String ProductTypeAddEditWizard_AvailSku;
	public String ProductTypeAddEditWizard_SelectSku;
	public String ProductTypeAddEditWizard_AvailSkuAtt;
	public String ProductTypeAddEditWizard_SelectSkuAtt;
	public String ProductTypeAddEditWizard_NameExists_ErrMsg;
	public String ProductTypeAddEditWizard_AvailableCartItemModifierGroups;
	public String ProductTypeAddEditWizard_AssignedCartItemModifierGroups;


	// Category Type Add/Edit dialog.
	public String CategoryTypeAddEditDialog_EditWindowTitle;
	public String CategoryTypeAddEditDialog_AddWindowTitle;
	public String CategoryTypeAddEditDialog_Add_InitMsg;
	public String CategoryTypeAddEditDialog_Edit_InitMsg;
	public String CategoryTypeAddEditDialog_Name;
	public String CategoryTypeAddEditDialog_AvailableAttributes;
	public String CategoryTypeAddEditDialog_AssignedAttributes;
	public String CategoryTypeAddEditDialog_Button_Add;
	public String CategoryTypeAddEditDialog_NameExists_ErrMsg;


	//Sku Attribute
	public String SkuAttributePage_Title;

	public String SkuAttributePage_Form_Title;

	//Sku Price
	public String SkuPricePage_Title;

	public String SkuPricePage_Form_Title;

	//Sku Image
	public String SkuImagePage_Title;

	public String SkuImageForm_Title;

	//Browse Job
	public String BrowseProgress_Tip;

	public String BrowseProgress_Error;

	// Create Product Wizard
	public String ProductDetailsPage_ProductCode;
	public String ProductDetailsPage_ProductName;
	public String ProductDetailsPage_BundlePricing;
	public String ProductDetailsPage_BundlePricing_Assigned;
	public String ProductDetailsPage_BundlePricing_Calculated;
	public String ProductDetailsPage_BundlePricing_ToolTip;
	public String ProductDetailsPage_ProductType;
	public String ProductDetailsPage_TaxCode;
	public String ProductDetailsPage_Brand;
	public String ProductDetailsPage_DisplayNameRequired;


	public String ProductCreateWizard_PageTitle;
	public String ProductCreateWizard_ProductDetailsDescription;
	public String ProductCreateWizard_CreatePriceTierDescription;
	public String ProductCreateWizard_AttributeValuesDescription;
	public String ProductCreateWizard_CreateSingleSkuDescription;
	public String ProductCreateWizard_MultiSkuDescription;

	public String ProductBundleCreateWizard_PageTitle;


	// Multi-value short text dialog
	public String ShortTextMultiValueDialog_WinTitle;
	public String ShortTextMultiValueDialog_Title;
	public String ShortTextMultiValueDialog_InitMsg;
	public String ShortTextMultiValueDialog_Value;
	public String ShortTextMultiValueDialog_AddValue;
	public String ShortTextMultiValueDialog_EditValue;
	public String ShortTextMultiValueDialog_RemoveValue;
	public String ShortTextMultiValueDialog_RemoveWarningMsg;
	public String ShortTextMultiValueDialog_RemoveConfirmMsg;
	public String ShortTextMultiValueDialog_RemoveWarningTitle;
	public String ShortTextMultiValueDialog_RemoveConfirmTitle;
	public String ShortTextMultiValueDialog_ErrorDialog_Dupl_Title;
	public String ShortTextMultiValueDialog_ErrorDialog_Dupl_Body;
	public String ShortTextDialog_Add_Title;
	public String ShortTextDialog_ErrorMsg;
	public String ShortTextDialog_RequiredMsg;

	// Create Product wizard
	public String CreateProductWizard_WindowTitle;
	public String CreateBundleWizard_WindowTitle;
	public String CreateProductWizard_ImageFile;
	public String CreateProductWizard_SelectImage;
	public String CreateProductWizard_Overview;
	public String CreateProductWizard_PrimaryCategory;
	public String CreateProductWizard_SelectProductType;
	public String CreateProductWizard_SelectTaxCode;
	public String CreateProductWizard_SelectBrand;
	public String CreateProductWizard_StoreRules;
	public String CreateProductWizard_ShippingDetails;
	public String CreateProductWizard_DigitalAsset;
	public String CreateProductWizard_Error_RequiredAttributes;

	// Attribute dual list un-assign confirmation dialogs

	public String AttributeDualList_UnAssignSelected_ConfirmTitle;

	public String AttributeDualList_UnAssignSelected_ConfirmMsg;

	public String AttributeDualList_UnAssignAll_ConfirmTitle;

	public String AttributeDualList_UnAssignAll_ConfirmMsg;

	public String AddLinkedCategory_ProgressMessage;

	public String ExcludeLinkedCategory_ProgressMessage;

	public String IncludeLinkedCategory_ProgressMessage;

	public String RemoveLinkedCategory_ProgressMessage;

	public String AddLinkedCategory_Error_DuplicateCode_DiagTitle;

	public String AddLinkedCategory_Error_DuplicateCode_DiagDesc;

	// Cart item modifier group dual list

	public String CartItemModifierGroupDualList_UnAssignSelected_ConfirmTitle;

	public String CartItemModifierGroupDualList_UnAssignSelected_ConfirmMsg;

	public String CartItemModifierGroupDualList_UnAssignAll_ConfirmTitle;

	public String CartItemModifierGroupDualList_UnAssignAll_ConfirmMsg;

	// Product Constituents
	public String ProductConstituentsPage_Title;

	public String ProductConstituentsSection_ProductCode;

	public String ProductConstituentsSection_ProductName;

	public String ProductConstituentsSection_ProductType;

	public String ProductConstituentsSection_Qty;

	public String ProductConstituentsSection_SkuCode;

	public String ProductConstituentsSection_SkuConfiguration;

	public String ProductEditorContituentSection_AddButton;

	public String ProductEditorContituentSection_EditButton;

	public String ProductEditorContituentSection_RemoveButton;

	public String ProductEditorContituentSection_MoveUpButton;

	public String ProductEditorContituentSection_MoveDownButton;

	public String ProductBundleAddConstituentsDialog_Title;

	public String ProductBundleEditConstituentsDialog_Title;

	public String CreateProductBundleAction;

	public String ProductBundleInvalidPricingDialogTitle;

	public String ProductBundleInvalidPricingDialogMessage;

	public String ProductBundleInvalidPricingDialogMessageWithParams;

	public String BundleCalculated_PricingType;

	public String BundleAssigned_PricingType;

	public String ProductCreateWizard_CreateBundleConstituentsDescription;

	public String ProductEditorContituentSection_OpenButton;

	public String ProductEditorContituentSection_DialogRemoveMsg;

	public String ProductEditorContituentSection_DialogRemoveTitle;

	public String ProductCreateWizard_CreationType_Product;

	public String ProductCreateWizard_CreationType_Bundle;

	public String ProductSaveDuplicateBaseAmountErrorTitle;

	public String ProductSaveDuplicateBaseAmountErrorMsg;

	public String ProductSavePriceError_Title;

	public String ProductSaveMissingValueForRequiredAttribute;
	public String ProductSaveMissingValueForRequiredAttributeMessage;

	public String RequiredAttributesChangedForProduct;

	public String RequiredAttributesChangedForProductMessage;

	public String Bundle_Selection_Rule_Title;

	public String Bundle_Selection_Rule;
	public String Bundle_Selection_Rule2;

	public String Bundle_Selection_Rule_All;

	public String Bundle_Selection_Rule_X;

	public String Bundle_Selection_Rule_One;

	public String Bundle_Selection_Parameter;

	public String validator_baseAmount_invalidQuantity;
	public String validator_baseAmount_invalidListPrice;
	public String validator_baseAmount_invalidSalePrice;
	public String validator_baseAmount_salePriceIsMoreThenListPrice;

	// Price Adjustment Start
	public String ProductBundlePriceAdjustmentPage_Title;

	public String ProductBundlePriceAdjustmentSelectionWarningDialogTitle;
	public String ProductBundlePriceAdjustmentSelectionWarningMessage;

	public String ProductBundlePriceAdjustmentPriceList;
	public String ProductBundlePriceAdjustmentListPriceBasedOnSelection;
	public String ProductBundlePriceAdjustmentTotalWithAdjustment;
	public String ProductBundlePriceAdjustmentSavings;
	public String ProductBundlePriceAdjustmentFrom;
	public String ProductBundlePriceAdjustment_PriceAdjustmentTooLarge;

	public String ProductBundlePriceAdjustmentColumnHeader_Constituent;
	public String ProductBundlePriceAdjustmentColumnHeader_SkuCode;
	public String ProductBundlePriceAdjustmentColumnHeader_SkuConfiguration;
	public String ProductBundlePriceAdjustmentColumnHeader_Quantity;
	public String ProductBundlePriceAdjustmentColumnHeader_ItemPrices;
	public String ProductBundlePriceAdjustmentColumnHeader_PriceAdjustments;

	// Price Adjustment End

	public String ProductPricePage_PriceListSelectionConfirmation;

	public String CreateProductWizard_PriceListDetails;
	public String CreateProductWizard_PriceListName;
	public String CreateProductWizard_PriceListCurrency;
	public String CreateProductWizard_PriceListPriority;
	public String CreateProductWizard_PriceListTable_Quantity;
	public String CreateProductWizard_PriceListTable_SalePrice;
	public String CreateProductWizard_PriceListTable_ListPrice;
	public String CreateProductWizard_PriceListTable_AddPriceTier;
	public String CreateProductWizard_PriceListTable_EditPriceTier;
	public String CreateProductWizard_PriceListTable_RemovePriceTier;

	public String ProductCreateWizard_PricingInformation;
	public String BundleCreateWizard_PricingInformation;
	public String PriceListShouldBeInTheChangeSet;
	public String NoPermissionToAccessMasterCatalog;

	public String MultipleSku_OpenParentProduct;


	/**
	 * Return a message String given the message key.
	 *
	 * @param messageKey the message key (field) that holds the message String
	 * @return the message String
	 */
	public String getMessage(final String messageKey) {
		try {
			final Field field = CatalogMessages.class.getField(messageKey);
			return (String) field.get(this);
		} catch (final Exception e) {
			throw new MessageException(e);
		}
	}

	/**
	 * Returns the bundle pricing type string for UI purposes.
	 * @param productBunlde the product bundle
	 * @return calculated or assigned string representations for pricing types
	 * */
	public String getBundleTypeString(final ProductBundle productBunlde) {
		if (productBunlde.isCalculated()) {
			return BundleCalculated_PricingType;
		}
		return BundleAssigned_PricingType;
	}
	/**
	 * Gets the NLS localize message class.
	 * @return the localized message class.
	 */
	public static CatalogMessages get() {
		return LocalizedMessagePostProcessor.getUTF8Encoded(BUNDLE_NAME, CatalogMessages.class);
	}

}
