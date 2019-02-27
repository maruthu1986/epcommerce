/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core;

import com.elasticpath.cmclient.core.nls.LocalizedMessagePostProcessor;

import java.lang.reflect.Field;

/**
 * Messages class for the core plugin.
 */
@SuppressWarnings({ "PMD.TooManyFields", "PMD.ExcessivePublicCount", "PMD.VariableNamingConventions", "PMD.SuspiciousConstantFieldName" })
public final class CoreMessages {
	private static final String BUNDLE_NAME = "com.elasticpath.cmclient.core.CorePluginResources"; //$NON-NLS-1$

	public static final String YES_NO_FOR_BOOLEAN_MSG_PREFIX = "YesNoForBoolean_"; //$NON-NLS-1$

	public static final String EMPTY_STRING = ""; //$NON-NLS-1$

	public static final String SPACE = " "; //$NON-NLS-1$

	private CoreMessages() {
//		LocalizedMessagePostProcessor.getInstance().process(CoreMessages.class, BUNDLE_NAME);
	}
	
	// ----------------------------------------------------
	// Global
	// ----------------------------------------------------
	public String NoSearchResultsError;
	public String Button_Add;
	public String Button_Edit;
	public String Button_Remove;
	public String RemoveDialog_Title;
	public String RemoveDialog_Message;
	public String SampleDate;
	public String SampleDateTime;
	public String SystemErrorOccured;
	public String SystemErrorTitle;
	public String ConcurringEditingError;
	public String ConcurringEditingErrorMessage;
	
	// ----------------------------------------------------
	// Searching
	// ----------------------------------------------------
	public String SearchProgress_JobTitle;
	public String SearchProgress_StatusBarMessage_StartSearch;
	public String SearchProgress_StatusBarMessage_ConvertToObjects;
	public String SearchProgress_Error;

	// ----------------------------------------------------
	// Default package
	// ----------------------------------------------------


	public String ApplicationActionBarAdvisor_FileMenu_Logout;
	public String  ApplicationWorkbenchAdvisor_Error_Title;
	public String  ApplicationWorkbenchAdvisor_Error_Msg;
	public String ApplicationExit_Warning_Msg;
	

	// ----------------------------------------------------
	// Actions package
	// ----------------------------------------------------
	public String HelpAboutActionTitle;

	public String HelpAboutActionTooltip;

	public String HelpAboutAction_UserManualNotFoundTitle;

	public String HelpAboutAction_UserManualNotFoundMessage;

	public String HelpAboutAction_ProgramNotFoundTitle;

	public String HelpAboutAction_ProgramNotFoundMessage;

	// ----------------------------------------------------
	// Editors package
	// ----------------------------------------------------
	public String AbstractCmClientFormEditor_ErrorTitle_save;

	public String AbstractCmClientFormEditor_Error_save;

	public String AbstractCmClientFormEditor_OK_save;

	public String AbstractCmClientFormEditor_OkTitle_save;

	public String AbstractCmClientFormEditor_Error_Save_Description;
	
	public String AbstractCmClientFormEditor_Error_Widget_Disposed;

	public String LanguagePulldownLabelText;



	// ----------------------------------------------------
	// UI package
	// ----------------------------------------------------
	public String EpLoginDialog_Title;

	public String EpLoginDialog_LoginUserId;

	public String EpLoginDialog_Password;

	public String EpLoginDialog_LoginButton;

	public String EpLoginDialog_Cancel;

	public String EpLoginDialog_ErrorTitle_ServerCommunication;

	public String EpLoginDialog_Error_AuthenticationFailed;
	
	public String EpLoginDialog_Error_AuthorizationFailed;

	public String EpLoginDialog_ErrorTitle_AuthenticationFailed;
	
	public String EpLoginDialog_ErrorTitle_AuthorizationFailed;
	
	public String EpLoginDialog_LockedTitle_AccountLocked;
	
	public String EpLoginDialog_LockedMessage_AccountLocked;
	
	public String EpLoginChangePasswordDialog_TemporaryPasswordTitle;
	
	public String EpLoginChangePasswordDialog_ExpiredPasswordTitle;
	
	public String EpLoginChangePasswordDialog_TemporaryPasswordMessage;
	
	public String EpLoginChangePasswordDialog_ExpiredPasswordMessage;
	
	public String EpLoginChangePasswordDialog_TemporaryPassword;
	
	public String EpLoginChangePasswordDialog_CurrentPassword;
	
	public String EpLoginChangePasswordDialog_NewPassword;
	
	public String EpLoginChangePasswordDialog_ConfirmNewPassword;
	
	public String EpLoginChangePasswordDialog_PasswordRestrictions;
	
	public String EpLoginChangePasswordDialog_TemporaryPasswordIncorrect;
	
	public String EpLoginChangePasswordDialog_CurrentPasswordIncorrect;
	
	public String AbstractEpLayoutComposite_DateSelectTooltip;

	public String AbstractEpDialog_ButtonSave;

	public String AbstractEpDialog_ButtonCancel;

	public String AbstractEpDialog_ButtonOK;
	
	public String CreditCardControl_StartDateNotLessExpiryDate;
	
	public String CreditCardControl_StartDateGreaterCurrentDate;
	
	public String CreditCardControl_CurrentDateGreaterExpiryDate;
	
	public String Dialog_NoneCatalogAssignedToCurrentUser;
	
	public String Given_Object_Not_Exist;
	
	// ----------------------------------------------------
	// Validation package
	// ----------------------------------------------------
	public String EpValidatorFactory_MaxCharLength;

	public String EpValidatorFactory_PasswordMinCharLength_8;

	public String EpValidatorFactory_LeadTrailSpace;

	public String EpValidatorFactory_NoSpace;

	public String EpValidatorFactory_ValueRequired;
	
	public String EpValidatorFactory_ComboValueRequired;

	public String EpValidatorFactory_PhoneValid;

	public String EpValidatorFactory_FaxValid;

	public String EpValidatorFactory_CreditCardValid;

	public String EpValidatorFactory_Integer;
	
	public String EpValidatorFactory_PositiveInt;

	public String EpValidatorFactory_NegativeInt;

	public String EpValidatorFactory_NonPositiveInt;

	public String EpValidatorFactory_NonNegativeInt;

	public String EpValidatorFactory_Long;
	
	public String EpValidatorFactory_BigDecimal;
	
	public String EpValidatorFactory_NonNegativeNonZeroBigDecimal;
	
	public String EpValidatorFactory_NonNegativeBigDecimal;

	public String EpValidatorFactory_NonPositiveBigDecimal;

	public String EpValidatorFactory_Email;
	
	public String EpValidatorFactory_Boolean;
	
	public String EpValidatorFactory_Url;
	
	public String EpValidatorFactory_DateTime;

	public String EpValidatorFactory_Date;
	
	public String EpValidatorFactory_Percent;
	
	public String EpValidatorFactory_Month;

	public String EpValidatorFactory_Year;
	
	public String EpValidatorFactory_FolderName;
	
	public String EpValidatorFactory_ProductName;
	
	public String EpValidatorFactory_AttributeKey;
	
	public String EpValidatorFactory_AttributeName;
	
	public String EpValidatorFactory_DisableDateBeforeStartDate;
	public String EpValidatorFactory_ToDateBeforeFromDate;
	
	public String EpValidatorFactory_NoSpecialCharacters;
	
	public String EpValidatorFactory_ReleaseDateBeforeToday;
	
	public String EpValidatorFactory_LetterAndDigitRequired;
	
	public String EpValidatorFactory_ALPHANUMERIC_REQUIRED;
	
	public String ValidationError_UrlIncorrect;
	
	public String ValidationError_HttpOrHttpsIsRequired;

	public String EpValidatorFactory_EndDateBeforeStartDate;

	public String EpValidatorFactory_EndDateInThePast;

	public String EpValidatorFactory_NoCComboSTypeelecction;

	public String EpValidatorFactory_CurrencyCode;

	// ----------------------------------------------------
	// Password validation
	// ----------------------------------------------------
	public String PasswordValidationError_MaximumPasswordAge;

	public String PasswordValidationError_MaximumRetryAttempts;

	public String PasswordValidationError_MinimumNoRepeatPassword;

	public String PasswordValidationError_ContainsAlphaNumeric;

	public String PasswordValidationError_MinimumLength;

	// ----------------------------------------------------
	// Navigation
	// ----------------------------------------------------
	public String navigation_FirstPage;

	public String navigation_PreviousPage;

	public String navigation_NextPage;

	public String navigation_LastPage;

	public String navigation_Results_of;
	
	public String navigation_Page;
	
	public String navigation_Of_Total_Page;

	public String navigation_Search_Results;
	
	// ----------------------------------------------------
	// Button Text
	// ----------------------------------------------------

	public String button_Add;

	public String button_Remove;

	public String button_AddAll;

	public String button_RemoveAll;

	public String button_MoveUp;

	public String button_MoveDown;

	public String button_Browse;

	public String button_Open;

	// ----------------------------------------------------
	// Yes/No boolean values
	// ----------------------------------------------------

	public String YesNoForBoolean_true;

	public String YesNoForBoolean_false;

	// ----------------------------------------------------
	// boolean values for the Dynamic Content
	// ----------------------------------------------------
	public String Boolean_true;

	public String Boolean_false;

	// ----------------------------------------------------
	// Attribute types
	// ----------------------------------------------------

	public String AttributeType_ShortText;

	public String AttributeType_LongText;

	public String AttributeType_Integer;

	public String AttributeType_Decimal;

	public String AttributeType_Boolean;

	public String AttributeType_Image;

	public String AttributeType_File;

	public String AttributeType_Date;

	public String AttributeType_DateTime;
	
	public String CalculationParameter_FixedPrice;

	public String CalculationParameter_FixedBased;

	public String CalculationParameter_PercentageOfTotalOrder;

	public String CalculationParameter_CostPerUnitWeigh;
	
	public String CostPerUnitWeightMethod_method_text;
	
	public String FixedBaseAndCostPerUnitWeightMethod_method_text;
	
	public String FixedBaseAndOrderTotalPercentageMethod_method_text;
	
	public String FixedPriceMethod_method_text;
	
	public String OrderTotalPercentageMethod_method_text;
	
	public String TaxCategoryType_Country;
	
	public String TaxCategoryType_Subcountry;
	
	public String TaxCategoryType_City;
	
	public String TaxCategoryType_Zip;

	public String ViewImportJobsAction;

	// ----------------------------------------------------
	// Order Return Status
	// ----------------------------------------------------

	public String OrderReturnStatus_AwaitingStockReturn;

	public String OrderReturnStatus_AwaitingCompletion;

	public String OrderReturnStatus_Cancelled;

	public String OrderReturnStatus_Completed;

	// ----------------------------------------------------
    // Order Return Type
	// ----------------------------------------------------

	public String OrderReturnType_Return;

	public String OrderReturnType_Exchange;

	
	// ----------------------------------------------------
	// Dialog
	// ----------------------------------------------------
	public String NotAvailable;
	
	public String SkuFinderDialog_NoResultsFound;
	
	public String SkuFinderDialog_Search;
	
	public String SkuFinderDialog_ProductName;
	
	public String SkuFinderDialog_ProductCode;
	
	public String SkuFinderDialog_SkuResults;
	
	public String SkuFinderDialog_ProductSkuCode;
	
	public String SkuFinderDialog_Brand;
	
	public String SkuFinderDialog_SKU_CONFIGURATION;
	
	public String SkuFinderDialog_Price;
	
	public String SkuFinderDialog_Filters;
	
	public String SkuFinderDialog_Clear;
	
	public String SearchView_Filter_Brand_All;
	
	public String SkuFinderDialog_ErrorMsg_HasMultiSku;
	
	public String SkuFinderDialog_ErrorMsg_SelectSku;
	
	public String SkuFinderDialog_WindowTitle;
	
	public String SkuFinderDialog_Title;
	
	public String SkuFinderDialog_SelectASku;
	
	public String ProductFinderDialog_MultiSku;
	
	public String ProductFinderDialog_SingleSku;
	
	public String ProductFinderDialog_NoResultsFound;
	
	public String ProductFinderDialog_Search;
	
	public String ProductFinderDialog_ProductName;
	
	public String ProductFinderDialog_ProductSKU;
	
	public String ProductFinderDialog_Filters;
	
	public String ProductFinderDialog_Brand;
	
	public String ProductFinderDialog_Clear;
	
	public String ProductFinderDialog_ProductResults;
	
	public String ProductFinderDialog_ProductCode;
	
	public String ProductFinderDialog_Price;
	
	public String ProductFinderDialog_FindAProduct;
	
	public String ProductFinderDialog_ByCategory_FindAProduct;
	
	public String ProductFinderDialog_Title;
	
	public String ProductFinderDialog_ByCategory_Title;
	
	public String ProductFinderDialog_WindowTitle;
	
	public String ProductFinderDialog_ByCategory_WindowTitle;
	
	public String ProductFinderDialog_ErrorMsg_SelectPro;
	
	public String ProductFinderDialog_Catalog;
	
	public String ProductFinderDialog_PriceListName;
	
	public String ProductFinderDialog_CurrencyCode;
	
	public String ProductFinderDialog_Quantity;
	
	public String ProductFinderDialog_ListPrice;
	
	public String ProductFinderDialog_SalePrice;
	
	public String CategoryFinderDialog_NoResultsFound;
	
	public String CategoryFinderDialog_Results;
	
	public String CategoryFinderDialog_CategoryCode;
	
	public String CategoryFinderDialog_CategoryName;
	
	public String CategoryFinderDialog_Catalog;
	
	public String CategoryFinderDialog_ParentCategory;
	
	public String CategoryFinderDialog_Search;
	
	public String CategoryFinderDialog_FindACategory;
	
	public String CategoryFinderDialog_Title;
	
	public String CategoryFinderDialog_WindowTitle;
	
	public String CategoryFinderDialog_ErrorMsg_SelectCategory;
	
	public String CatalogPulldownContribution_Catalog;
	
	public String ChangePasswordDialog_Title_Self;
	public String ChangePasswordDialog_Title_AnotherUser;
	public String ChangePasswordDialog_WindowTitle_Self;
	public String ChangePasswordDialog_WindowTitle_AnotherUser;
	public String ChangePasswordDialog_Description_RequireOldPassword;
	public String ChangePasswordDialog_Description_NoOldPassword;
	public String ChangePasswordDialog_OldPassword;
	public String ChangePasswordDialog_NewPassword;
	public String ChangePasswordDialog_ConfirmPassword;
	public String ChangePasswordDialog_Error_OldPasswordIncorrect;
	public String ChangePasswordDialog_Error_NewPasswordsNoMatch;
	public String ChangePasswordDialog_Error_MinimumLength;
	public String ChangePasswordDialog_Error_NoOldPassword;
	public String ChangePasswordDialog_Error_NoSpaces;
	public String ChangePasswordDialog_Error_EmailError_DialogTitle;
	public String ChangePasswordDialog_Error_EmailError_DialogDescription;
	public String ChangePasswordDialog_Confirmation_DialogTitle;
	public String ChangePasswordDialog_Confirmation_DialogDescription;
	public String ChangePasswordDialog_DuplicatedPassword;
	public String ChangePasswordDialog_DuplicatedPassword_Log;
	
	public String UnlockDialog_WindowTitle;
	public String UnlockDialog_Title;
	public String UnlockDialog_UserId;
	public String UnlockDialog_Password;
	public String UnlockDialog_OkButton;
	public String UnlockDialog_CancelButton;
	public String UnlockDialog_Error_WrongPassword;
	
	public String ChangePaginationDialog_Title;
	public String ChangePaginationDialog_WindowTitle;
	public String ChangePaginationDialog_Description;
	public String ChangePaginationDialog_ResultsPerPage;

	public String ChangeTimezoneDialog_Title;
	public String ChangeTimezoneDialog_WindowTitle;
	public String ChangeTimezoneDialog_CurrentTime;
	public String ChangeTimezoneDialog_Description;
	public String ChangeTimezoneDialog_Browser;
	public String ChangeTimezoneDialog_Custom;


	public String RefreshAction_Name;
	public String RefreshAction_Tooltip;
	
	// ----------------------------------------------------
	// Value Dialogs
	// ----------------------------------------------------
	
	public String BooleanDialog_EditWindowTitle;
	public String BooleanDialog_EditTitle;
	public String BooleanDialog_AddWindowTitle;
	public String BooleanDialog_AddTitle;
	
	public String DateTimeDialog_EditWindowTitle;
	public String DateTimeDialog_EditTitle;
	public String DateTimeDialog_AddWindowTitle;
	public String DateTimeDialog_AddTitle;
	
	public String DecimalDialog_EditWindowTitle;
	public String DecimalDialog_EditTitle;
	public String DecimalDialog_AddWindowTitle;
	public String DecimalDialog_AddTitle;
	
	public String IntegerDialog_EditWindowTitle;
	public String IntegerDialog_EditTitle;
	public String IntegerDialog_AddWindowTitle;
	public String IntegerDialog_AddTitle;
	
	public String LongTextDialog_EditWindowTitle;
	public String LongTextDialog_EditTitle;
	public String LongTextDialog_AddWindowTitle;
	public String LongTextDialog_AddTitle;
	
	public String ShortTextDialog_EditWindowTitle;
	public String ShortTextDialog_EditTitle;
	public String ShortTextDialog_AddWindowTitle;
	public String ShortTextDialog_AddTitle;
	
	public String ShortTextMultiValueDialog_EditWindowTitle;
	public String ShortTextMultiValueDialog_EditTitle;
	public String ShortTextMultiValueDialog_AddWindowTitle;
	public String ShortTextMultiValueDialog_AddTitle;
	public String ShortTextMultiValueDialog_AddButton;
	public String ShortTextMultiValueDialog_EditButton;
	public String ShortTextMultiValueDialog_RemoveButton;
	public String ShortTextMultiValueDialog_Value;
	public String ShortTextMultiValueDialog_RemoveConfirmTitle;
	public String ShortTextMultiValueDialog_RemoveConfirmMsg;
	
	public String UrlDialog_EditWindowTitle;
	public String UrlDialog_EditTitle;
	public String UrlDialog_AddWindowTitle;
	public String UrlDialog_AddTitle;
	
	// ----------------------------------------------------
	// Login Id Trim
	// ----------------------------------------------------
	public String LoginIdTrim_Text;
	
	// ----------------------------------------------------
	// Handlers Package
	// ----------------------------------------------------
	public String UpdateHandler_UpdateSiteGroup;
	public String UpdateHandler_UpdateJobName;
	public String UpdateHandler_UpdateError_Title;
	public String UpdateHandler_UpdateError_Desc;
	public String UpdateHandler_NoUpdates;
	public String UpdateHandler_UpdatesAvailable;
	public String UpdateHandler_Updating;
	public String UpdateHandler_UpdatesInstalled_DialogTitle;
	public String UpdateHandler_UpdatesInstalled_DialogMessage;

	// ----------------------------------------------------
	// Store Editor Model
	// ----------------------------------------------------
	public String StoreCodeExists;
	public String StoreUrlExists;
	public String NotAllRequiredFieldsProvided;
	public String PaymentMethodRequired;
	public String SelectedGatewayPaymentTypeNotInApplicableTypes;

	public String ValidationError_WarningMessage;
	public String ValidationError_NoCatalogIsSelected;
	public String ValidationError_MissingDefaultLocale;
	public String ValidationError_MissingDefaultCurrency;
	public String ValidationError_MissingStoreUrl;
	public String ValidationError_NoWarehouseIsSelected;
	public String ValidationError_MissingContentEncoding;
	public String ValidationError_MissingEmailSenderAddress;
	public String ValidationError_MissingEmailSenderName;
	public String ValidationError_MissingAdminEmail;
	public String ValidationError_NoThemeIsSelected;
	public String ValidationError_Duplicate;

	public String StoreState_Open;
	public String StoreState_Restricted;
	public String StoreState_UnderConstruction;
	
	// ---------------------------------------------------
	// Sorting Composite Control
	// ---------------------------------------------------
	public String SortingCompositeControl_SortingGroup;
	public String SortingCompositeControl_Label_SortByColumn;
	public String SortingCompositeControl_Label_SortOrder;
	public String SortingCompositeControl_Sort_Order_Ascending;
	public String SortingCompositeControl_Sort_Order_Descending; 

	// ---------------------------------------------------
	// Settings
	// ---------------------------------------------------
	public String Store_Marketing_Name;
	public String EditSetting;
	public String Store_Marketing_Type;
	public String Store_Marketing_DefaultValue;
	public String Store_Marketing_AssignedValue;
	public String SettingValidationMessage;
	public String Store_Marketing_EditValue;
	public String Store_Marketing_ClearValue;
	public String Description;
	public String NotDefinedValue;

	// VALUE TYPE
	public String ValueType_StringLong;
	public String ValueType_StringShort;
	public String ValueType_StringShortMultiValue;
	public String ValueType_Boolean;
	public String ValueType_Decimal;
	public String ValueType_Integer;
	public String ValueType_Image;
	public String ValueType_File;
	public String ValueType_Date;
	public String ValueType_DateTime;
	public String ValueType_Url;
	public String ValueType_Product;
	public String ValueType_Category;
	public String ValueType_HTML;


	// Timezones
	public String UTCp12;
	public String UTCp11;
	public String UTCp10;
	public String UTCp9;
	public String UTCp8;
	public String UTCp7;
	public String UTCp6;
	public String UTCp5;
	public String UTCp4;
	public String UTCp3;
	public String UTCp2;
	public String UTCp1;
	public String UTC;
	public String UTCm1;
	public String UTCm2;
	public String UTCm3;
	public String UTCm4;
	public String UTCm5;
	public String UTCm6;
	public String UTCm7;
	public String UTCm8;
	public String UTCm9;
	public String UTCm10;
	public String UTCm11;
	public String UTCm12;
	public String IST;
	public String ACT;



	/**
	 * Return a message String given the message key.
	 *
	 * @param messageKey the message key (field) that holds the message String
	 * @return the message String
	 */
	public String getMessage(final String messageKey) {
		try {
			final Field field = CoreMessages.class.getField(messageKey);
			return (String) field.get(this);
		} catch (final Exception e) {
			throw new MessageException(e);
		}
	}

	/**
	 * Gets the NLS localize message class.
	 * @return the localized message class.
	 */
	public static CoreMessages get() {
		return LocalizedMessagePostProcessor.getUTF8Encoded(BUNDLE_NAME, CoreMessages.class);
	}
}
