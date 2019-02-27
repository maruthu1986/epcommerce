/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.fulfillment;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.nls.LocalizedMessagePostProcessor;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.util.extenum.ExtensibleEnum;
import com.elasticpath.domain.order.OrderPaymentStatus;
import com.elasticpath.domain.order.OrderShipmentStatus;
import com.elasticpath.domain.order.OrderStatus;
import com.elasticpath.money.Money;
import com.elasticpath.money.MoneyFormatter;
import com.elasticpath.plugin.payment.PaymentType;
import com.elasticpath.plugin.payment.exceptions.InsufficientFundException;
import com.elasticpath.plugin.payment.exceptions.PaymentProcessingException;

/**
 * Messages class for the Fulfillment plugin.
 */
@SuppressWarnings({ "PMD.TooManyFields", "PMD.ExcessiveClassLength", "PMD.ExcessivePublicCount", "PMD.VariableNamingConventions",
	"PMD.LongVariable" })
public final class FulfillmentMessages {
	private static final String BUNDLE_NAME = "com.elasticpath.cmclient.fulfillment.FulfillmentPluginResources"; //$NON-NLS-1$

	public static final String EMPTY_STRING = ""; //$NON-NLS-1$

	public static final String SPACE = " "; //$NON-NLS-1$

	public String Select;

	public String CustomerDetails_Tooltip;

	public String CustomerDetails_SaveTaskName;

	public String CustomerDetails_ChangePassword;

	public String CustomerDetailsPage_ResetPassDialogTitle;

	public String CustomerDetailsPage_ResetPassDialogQuestion;

	public String CustomerDetailsPage_ResetPassInfoTitle;

	public String CustomerDetailsPage_ResetPassInfoMessage;

	public String CustomerDetailsPage_CreatePassInfoTitle;

	public String CustomerDetailsPage_CreatePassInfoMessage;

	public String CustomerDetailsEditor_OnSavePrompt;

	public String ProfileRegistrationSection_No;

	public String ProfileRegistrationSection_Yes;

	public String ProfileRegistrationSection_Male;

	public String ProfileRegistrationSection_Female;

	public String ProfileRegistrationSection_Gender_Not_Available;

	public String ProfileRegistrationSection_TypeRegistered;

	public String ProfileRegistrationSection_TypeGuest;

	public String ProfileAttributesSection_Title;

	// ----------------------------------------------------
	// Global keys
	// ----------------------------------------------------

	public String CustomerDetailsStatus_Active;

	public String CustomerDetailsStatus_Disabled;

	public String CustomerDetailsStatus_Default;

	public String CustomerDetails_FirstNameLabel;

	public String CustomerDetails_LastNameLabel;

	public String AddressDialog_Organization;

	public String CustomerDetails_CustomerIdLabel;

	public String CustomerDetails_UserIdLabel;

	public String CustomerDetails_Anonymous;

	public String Customer;

	public String OrderStatus_Canceled;

	public String OrderStatus_Completed;

	public String OrderStatus_Created;

	public String OrderStatus_OnHold;

	public String OrderStatus_PartialShip;

	public String OrderStatus_AwaitingExchange;

	public String OrderStatus_InProgress;

	public String OrderStatus_Failed;

	public String ShipmentStatus_OnHold;

	public String ShipmentStatus_Released;

	public String ShipmentStatus_Shipped;

	public String ShipmentStatus_Cancelled;

	public String ShipmentStatus_Awaiting;

	public String ShipmentStatus_Assigned;

	public String ShipmentStatus_FailedOrder;

	public String PaymentStatus_Approved;

	public String PaymentStatus_Pending;

	public String PaymentStatus_Failed;

	public String PaymentType_CreditCard;

	public String PaymentType_Token;
	
	public String PaymentType_PayPalExpress;

	public String PaymentType_GiftCertificate;

	public String PaymentType_Exchange;

	public String PaymentType_HostedPage;

	// ----------------------------------------------------
	// Customer Editors package
	// ----------------------------------------------------

	public String AddressDefaultSection_Title;

	public String AddressDefaultSection_Description;

	public String AddressDefaultSection_DefaultBillingAddress;

	public String AddressDefaultSection_DefaultShippingAddress;

	public String AddressMainSection_Title;

	public String AddressMainSection_Description;

	public String AddressMainSection_Name;

	public String AddressMainSection_Address;

	public String AddressMainSection_PhoneNum;

	public String AddressMainSection_AddAddressButton;

	public String AddressMainSection_EditAddressButton;

	public String AddressMainSection_RemoveAddressButton;

	public String AddressMainSection_ViewAddressButton;

	public String AddressPage_Title;

	public String AddressPage_Form_Title;

	public String OrdersPage_Title;

	public String OrdersPage_Form_Title;

	public String OrderSection_Title;

	public String OrderSection_Store;

	public String OrderSection_ID;

	public String OrderSection_Date;

	public String OrderSection_Status;

	public String OrderSection_Total;

	public String OrderSection_ViewOrderButton;

	public String OrderSection_RefreshOrdersButton;

	public String OrderCreate_Title;

	public String OrderCreate_StoreCombo;

	public String OrderCreate_CreateOrderButton;

	public String OrderCreate_Description;

	public String OrderCreate_DialogTitle;

	public String OrderCreate_DialogDescription;

	public String OrderCreate_DialogStoreLabel;

	public String OrderCreate_DialogComboSelectAStore;

	public String ProfileBasicSection_Title;

	public String ProfileBasicSection_UserId;

	public String ProfileBasicSection_Status;

	public String ProfileBasicSection_Email;

	public String ProfileBasicSection_PhoneNum;

	public String ProfileBasicSection_FaxNum;

	public String ProfileBasicSection_Company;

	public String ProfilePage_Title;

	public String ProfilePage_Form_Title;

	public String ProfileRegistrationSection_Title;

	public String ProfileRegistrationSection_RegDate;

	public String ProfileRegistrationSection_StoreReg;

	public String ProfileRegistrationSection_UserType;

	public String ProfileRegistrationSection_LocalePref;

	public String ProfileRegistrationSection_CurrencyPref;

	public String ProfileRegistrationSection_BirthDate;

	public String ProfileRegistrationSection_Gender;

	public String ProfileRegistrationSection_RecHttpMail;

	public String ProfileRegistrationSection_RecNewsletter;

	public String ProfileRegistrationSection_BusinessNumber;

	public String ProfileRegistrationSection_TaxExemptionId;

	public String AddressDialog_FirstName;

	public String AddressDialog_LastName;

	public String AddressDialog_AddressLine1;

	public String AddressDialog_AddressLine2;

	public String AddressDialog_City;

	public String AddressDialog_State;

	public String AddressDialog_Zip;

	public String AddressDialog_Country;

	public String AddressDialog_Phone;

	public String AddressDialog_Fax;

	public String AddressDialog_Commercial;

	public String AddressDialog_RemoveTitle;

	public String AddressDialog_RemoveMessage;

	public String AddressDialog_EditAddressTitle;

	public String AddressDialog_AddAddressTitle;

	public String AddressDialog_ViewAddressTitle;

	// ----------------------------------------------------
	// Order Editors package
	// ----------------------------------------------------

	public String OrderSummaryPage_Title;

	public String OrderSummaryPage_Form_Title;

	public String OrderSummaryPage_EditorDirtyCancelOrder_Title;

	public String OrderSummaryPage_EditorDirtyCancelOrder_Message;

	public String OrderSummaryPage_EditorDirtyPlaceOnHoldOrder_Title;

	public String OrderSummaryPage_EditorDirtyPlaceOnHoldOrder_Message;

	public String OredrSummaryPage_EditorDirtyRemoveHoldOrder_Title;

	public String OrderSummaryPage_EditorDirtyRemoveHoldOrder_Message;

	public String OrderSummaryOverviewSection_Description;

	public String OrderSummaryOverviewSection_Title;

	public String OrderSummaryOverviewSection_OrderUid;

	public String OrderSummaryOverviewSection_StoreName;

	public String OrderSummaryOverviewSection_CreatedDate;

	public String OrderSummaryOverviewSection_Currency;

	public String OrderSummaryOverviewSection_OrderTotal;

	public String OrderSummaryOverviewSection_BalanceDue;

	public String OrderSummaryOverviewSection_CreatedBy;

	public String OrderSummaryOverviewSection_OrderStatus;

	public String OrderSummaryOverviewSection_ExtOrderSystem;

	public String OrderSummaryOverviewSection_ExtOrder;

	public String OrderSummaryOverviewSection_RMA;

	public String OrderSummaryOverviewSection_DueToRMA;

	public String OrderSummaryOverviewSection_HoldOrder;

	public String OrderSummaryOverviewSection_ReleaseOrder;

	public String OrderSummaryOverviewSection_CancelOrder;

	public String OrderSummaryOverviewSection_DialogHoldMessage;

	public String OrderSummaryOverviewSection_DialogHoldTitle;

	public String OrderSummaryOverviewSection_DialogReleaseMessage;

	public String OrderSummaryOverviewSection_DialogReleaseTitle;

	public String OrderSummaryOverviewSection_DialogCancelMessage;

	public String OrderSummaryOverviewSection_DialogCancelTitle;

	public String OrderSummaryCustomerInformationSection_Description;

	public String OrderSummaryCustomerInformationSection_Title;

	public String OrderSummaryCustomerInformationSection_CustomerName;

	public String OrderSummaryCustomerInformationSection_EmailAddress;

	public String OrderSummaryCustomerInformationSection_PhoneNumber;

	public String OrderSummaryCustomerInformationSection_Email;

	public String OrderSummaryCustomerInformationSection_EditCustomerBtn;

	public String OrderSummaryBillingAddressSection_Description;

	public String OrderSummaryBillingAddressSection_Title;

	public String OrderSummaryBillingAddressSection_Name;

	public String OrderSummaryBillingAddressSection_Address;

	public String OrderSummaryBillingAddressSection_PhoneNumber;

	public String OrderSummaryBillingAddressSection_EditBillingAddressBtn;

	public String OrderSummaryPage_BillingAddress_PayPal;

	public String OrderDetailsErrorResendingGiftCert_Title;

	public String OrderDetailsErrorResendingGiftCert_Message;

	public String OrderDetailsErrorAddingItem_Title;

	public String OrderDetailsErrorAddingBundle_Message;

	public String OrderDetailsErrorAddingRecurring_Message;

	public String OrderDetailsErrorAddingItem_Title_NoPrice;

	public String OrderDetailsErrorAddingItem_Message_NoPrice;

	public String OrderDetailsCustomerIsDisabled_Title;

	public String OrderDetailsCustomerIsDisabled_Message;

	public String OrderDetailsErrorAddingItemNotEnabled_Message;

	public String OrderEditor_Save_StatusBarMsg;

	public String OrderEditor_OnSavePrompt;

	public String OrderEditor_ToolTipText;

	public String OrderEditor_CreateRefund_ActionTitle;

	public String OrderEditor_CreateRefund_EditorDirtyTitle;

	public String OrderEditor_CreateRefund_EditorDirtyMessage;

	public String OrderEditor_EditOrderErrorTitle;

	public String OrderEditor_EditOrderIsLockedMessage;

	public String OrderEditor_EditOrderWasModifiedMessage;

	public String OrderEditor_EditOrderWasUnlockedMessage;

	public String OrderEditor_SaveOrderErrorTitle;

	public String OrderEditor_SaveOrderIsLockedMessage;

	public String OrderEditor_SaveOrderWasModifiedMessage;

	public String OrderEditor_SaveOrderWasUnlockedMessage;

	public String OrderEditor_UnlockOrderErrorTytle;

	public String OrderEditor_UnlockOrderFailedMessage;

	public String OrderDetailPage_Title;

	public String OrderDetailPage_Form_Title;

	public String Shipment_For_Subscriptions;

	public String ShipmentSection_Title;

	public String ShipmentSection_To;

	public String ShipmentSection_Description;

	public String ShipmentSection_BundleName;

	public String ShipmentSection_InventoryStatus;

	public String ShipmentSection_SkuCode;

	public String ShipmentSection_ProductName;

	public String ShipmentSection_SkuOption;

	public String ShipmentSection_ListPrice;

	public String ShipmentSection_PaymentSchedule;

	public String ShipmentSection_SalePrice;

	public String ShipmentSection_Discount;

	public String ShipmentSection_Quantity;

	public String ShipmentSection_TotalPrice;

	public String ShipmentSection_InventoryAllocated;

	public String ShipmentSection_WaitingForAllocation;

	public String ShipmentSection_AddItemButton;

	public String ShipmentSection_ItemDetailButton;

	public String ShipmentSection_MoveItemButton;

	public String ShipmentSection_RemoveItemButton;

	public String ShipmentSection_OpenProductButton;

	public String ShipmentSection_EditItemAttributesButton;

	public String ShipmentSection_EditAddressButton;

	public String ShipmentSection_ReleaseShipmentButton;

	public String ShipmentSection_CancelShipmentButton;

	public String ShipmentSection_CancelShipmentTitle;

	public String ShipmentSection_ShippingAddress;

	public String ShipmentSection_ShippingMethod;

	public String ShipmentSection_ShippingItemTotal;

	public String ShipmentSection_ShippingItemTax;

	public String ShipmentSection_ShippingCost;

	public String ShipmentSection_ShipmentDiscount;

	public String ShipmentSection_TotalBeforeTax;

	public String ShipmentSection_ShipmentTaxes;

	public String ShipmentSection_ShipmentTotal;

	public String ShipmentSection_ShipmentStatus;

	public String ShipmentSection_RemoveItemConfirm;

	public String ShipmentSection_ConfirmRemoveItem;

	public String ShipmentSection_EditorDirtyReleaseShipmentTitle;

	public String ShipmentSection_EditorDirtyReleaseShipmentMessage1;

	public String ShipmentSection_EditorDirtyReleaseShipmentMessage2;

	public String ShipmentSection_EditorDirtyCancelShipmentTitle;

	public String ShipmentSection_EditorDirtyCancelShipmentMessage;

	public String ShipmentSection_ReleaseShipmentConfirm;

	public String ShipmentSection_ReleaseShipmentConfirmMessage;

	public String ShipmentSection_CancelShipmentMessage;

	public String ShipmentSection_CancelShipmentTo;

	public String ShipmentSection_CancelShipmentShipment;

	public String ShipmentSection_ConfirmRemoveShipment;

	public String ShipmentSection_ShippingMethodComboBoxSelectAMethod;

	public String ShipmentSection_InsufficientInventoryTitle;

	public String ShipmentSection_InsufficientInventoryNotInStock;

	public String ShipmentSection_InsufficientInventoryOnly;

	public String ShipmentSection_InsufficientInventoryItemsInStock;

	public String ShipmentSection_InSufficientSkuQuantityWarningHeader;

	public String ShipmentSection_DuplicateProductSkuTitle;

	public String ShipmentSection_DuplicateProductSkuWarningHeader;

	public String ShipmentSection_DuplicateProductSkuWarningMessage;

	public String ShipmenSection_DigitalGood;

	public String ShipmenSection_GiftCertificateResend;

	public String ShipmentSection_MinimumOrderQuantityOutOfStock;

	public String ElectronicShipmentSection_Title;

	public String ElectronicShipmentSection_Description;

	public String RecurringItemsSection_Title;

	public String PromotionSection_Title;

	public String PromotionSection_Description;

	public String PromotionSection_PromotionName;

	public String PromotionSection_PromotionDisplayName;

	public String PromotionSection_PromotionDescription;

	public String PromotionSection_PromotionType;

	public String PromotionSection_PromotionCouponCode;

	public String PromotionSection_PromotionCouponUsage;

	public String OrderDataSection_Title;

	public String OrderNotePage_Title;

	public String OrderNotePage_Form_Title;

	public String OrderNoteFilter_Label_Note_Type;

	public String OrderNote_Label_Originate;

	public String OrderNoteNotes_Description;

	public String OrderNoteNotes_Button;

	public String OrderNotePage_DialogTitle;

	public String OrderNoteNotes_DialogTitleOpen;

	public String OrderNoteNotes_DialogTitleViewAll;

	public String OrderNoteNotes_DialogLabelCreatedBy;

	public String OrderNoteNotes_DialogLabelCreatedOn;

	public String OrderNoteNotes_DialogLabelNote;

	public String OrderNoteNotes_ViewAllButton;

	public String OrderNoteIcon_User;

	public String OrderNoteIcon_System;

	public String MoveItem_DialogTitle;

	public String MoveItem_MoveToExistingShipment;

	public String MoveItem_QuantityToMove;

	public String MoveItem_CreateNewShipment;

	public String MoveItem_Address;

	public String MoveItem_ShippingMethod;

	public String MoveItem_SelectAddress;

	public String MoveItem_SelectShipment;

	public String MoveItem_SelectShippingMethod;

	public String ResendGiftCertDialog_Send;

	public String ResendGiftCertDialog_InitialMessage;

	public String ResendGiftCertDialog_Title;

	public String ResendGiftCertDialog_WindowTitle;

	public String ResendGiftCertDialog_Recipient;
	// ----------------------------------------------------
	// Order payments
	// ----------------------------------------------------
	public String OrderPaymentsPage_Title;

	public String OrderPaymentsPage_Form_Title;

	public String PaymentSummarySection_Title;

	public String PaymentSummarySection_Description;

	public String PaymentHistorySection_Title;

	public String PaymentHistorySection_Description;

	public String PaymentHistorySection_NotApplicable;

	public String OrderPaymentSummaySection_Ordered;

	public String OrderPaymentSummaySection_Paid;

	public String OrderPaymentSummaySection_Due;

	// -- payment history section
	public String OrderPaymentHistorySection_TableTitle_DateTime;

	public String OrderPaymentHistorySection_TableTitle_ShipmentId;

	public String OrderPaymentHistorySection_TableTitle_PaymentType;

	public String OrderPaymentHistorySection_TableTitle_TransactionType;

	public String OrderPaymentHistorySection_TableTitle_PaymentDetails;

	public String OrderPaymentHistorySection_TableTitle_Amount;

	public String OrderPaymentHistorySection_TableTitle_Status;

	public String OrderPaymentHistorySection_TableTitle_TransactionID;

	public String OrderPaymentHistorySection_ElectronicShipmentId;

	public String OrderPaymentHistorySection_PaymentTokenDetailsPlaceholder;
	// ----------------------------------------------------
	// Order returns and exchanges
	// ----------------------------------------------------
	public String OrderReturnsPage_Title;

	public String OrderReturnReturnSection_Title;

	public String OrderReturnExchangeSection_Title;

	public String OrderReturnSection_ReturnEditBtn;

	public String OrderReturnSection_ReturnCancelBtn;

	public String OrderReturnSection_ReturnCompleteBtn;

	public String OrderReturnSection_ExchangeCancelBtn;

	public String OrderReturnSection_ExchangeCompleteBtn;

	public String OrderReturnSection_ExchangeOpenOrderBtn;

	public String OrderReturnSection_ResendRMABtn;

	public String OrderReturnSection_DateTimeInitiated;

	public String OrderReturnSection_CreatedBy;

	public String OrderReturnSection_ReceivedBy;

	public String OrderReturnSection_Status;

	public String OrderReturnSection_ExchangeOrderNumber;

	public String OrderReturnSection_Total;

	public String OrderReturnSection_Refunded;

	public String OrderReturnSection_BalanceOwed;

	public String OrderReturnSection_Notes;

	public String OrderReturnSection_TableTitle_SKUCode;

	public String OrderReturnSection_TableTitle_ProductName;

	public String OrderReturnSection_TableTitle_Qty;

	public String OrderReturnSection_TableTitle_UnitPrice;

	public String OrderReturnSection_TableTitle_Reason;

	public String OrderReturnSection_TableTitle_ReceivedQty;

	public String OrderReturnSection_TableTitle_ReceivedState;

	public String OrderReturnSection_CancelReturnTitle;

	public String OrderReturnSection_CancelReturnMessage;

	public String OrderReturnSection_CancelExchangeTitle;

	public String OrderReturnSection_CancelExchangeMessage;

	public String OrderReturnSection_EditorDirtyCancelReturnTitle;

	public String OrderReturnSection_EditorDirtyCancelReturnMessage;

	public String OrderReturnSection_EditorDirtyCompleteReturnTitle;

	public String OrderReturnSection_EditorDirtyCompleteReturnMessage;

	public String OrderReturnSection_EditorDirtyEditReturnTitle;

	public String OrderReturnSection_EditorDirtyEditReturnMessage;

	public String OrderReturn_ErrDlgCollisionTitle;

	public String OrderReturn_ErrDlgCollisionMessage;

	public String RAESection_SubSectionStatus;

	public String RAESection_CreateReturnButton;

	public String RAESection_CreateExchangeButton;

	public String RAESection_EditorDirtyCreateReturnTitle;

	public String RAESection_EditorDirtyCreateReturnMessage;

    public String ReturnSection_SubSectionStatus;

	// ----------------------------------------------------
	// Views package
	// ----------------------------------------------------

	public String SearchView_CustomersTab;

	public String SearchView_OrdersTab;

	public String SearchView_SearchButton;

	public String SearchView_ClearButton;

	public String SearchView_EmailUserId;

	public String SearchView_PostalCode;

	public String SearchView_PhoneNumber;

	public String SearchView_SearchTermsGroup;

	public String SearchView_AdvancedGroup;

	public String SearchView_Error_NoSearchTerms;

	public String SearchView_FiltersGroup;

	public String SearchView_Filter_Stores;

	public String SearchView_AllStore;

	public String CustomerSearchTab_SearchTermsGroup;

	public String ShipmentSection_SubSectionItem;

	public String ShipmentSection_SubSectionInfo;

	public String ShipmentSection_SubSectionSummary;

	public String SearchView_Status_Any;

	public String ShipmentSection_TrackingNumber;

	public String ShipmentSection_ShipmentDate;

	// ----------------------------------------------------
	// Order search view tab
	// ----------------------------------------------------

	public String SearchView_Filter_OrderStatus;

	public String SearchView_Filter_ShipmentStatus;

	public String SearchView_OrderNumber;

	public String SearchView_ContainsSku;

	public String SearchView_ContainsSku_Tooltip;

	public String SearchView_RMA;

	public String SearchView_FromDate;

	public String SearchView_ToDate;

	public String Validation_ToDateBeforeFromDate;

	public String Validation_FromDateAfterToDate;
	// ----------------------------------------------------
	// Customer Views package
	// ----------------------------------------------------
	public String CustomerSearchResultsView_CustomerId;

	public String CustomerSearchResultsView_LastName;

	public String CustomerSearchResultsView_FirstName;

	public String CustomerSearchResultsView_EmailUserId;

	public String CustomerSearchResultsView_DefaultBillingAddress;

	public String CustomerSearchResultsView_TelephoneNum;

	public String CustomerSearchResultsView_UserId;

	public String CustomerSearchResultsView_StoreRegistered;

	// ----------------------------------------------------
	// Order search results Views
	// ----------------------------------------------------

	public String OrderSearchResultsView_OrderNumber;

	public String OrderSearchResultsView_Store;

	public String OrderSearchResultsView_CustomerName;

	public String OrderSearchResultsView_Date;

	public String OrderSearchResultsView_Total;

	public String OrderSearchResultsView_Status;

	// ----------------------------------------------------
	// Return Wizard
	// ----------------------------------------------------

	public String ReturnWizard_Create_Title;

	public String ReturnWizard_Edit_Title;

	public String ReturnWizard_Complete_Title;

	public String ReturnWizard_Step_Info;

	public String ReturnWizard_SubjectPage_Message;

	public String ReturnWizard_MethodPage_Message;

	public String ReturnWizard_Return_Created_Label;

	public String ReturnWizard_Return_Completed_Label;

	public String ReturnWizard_ItemSubTotal_Label;

	public String ReturnWizard_ItemTaxes_Label;

	public String ReturnWizard_LessRestockingFee_Label;

	public String ReturnWizard_TotalReturnAmount_Label;

	public String ReturnWizard_TotalRefundAmount_Label;

	public String ReturnWizard_ItemShippingCost_Label;

	public String ReturnWizard_ShippingCostDiscount_Label;

	public String ReturnWizard_ItemTotalBeforeTax_Label;

	public String ReturnWizard_ItemTotal_Label;

	public String ReturnWizard_ItemShippingTax_Label;

	public String ReturnWizard_PhysicalReturnRequired_Label;

	public String ReturnWizard_ItemsToBeReturned_Section;

	public String ReturnWizard_Notes_Section;

	public String ReturnWizard_Summary_Section;

	public String ReturnWizard_SKUCode_Column;

	public String ReturnWizard_ProductName_Column;

	public String ReturnWizard_SKUOptions_Column;

	public String ReturnWizard_ReturnableQty_Column;

	public String ReturnWizard_InvoicePrice_Column;

	public String ReturnWizard_ReturnQty_Column;

	public String ReturnWizard_Reason_Column;

	public String ReturnWizard_ReturnQtyError_Msg;

	public String ReturnWizard_LessRestcokingFeeError_Msg;

	public String ReturnWizard_ShippingCostError_Msg;

	public String ReturnWizard_ShipmentTotal_Msg;

	public String ReturnWizard_ProceedError_Title;

	public String ReturnWizard_ProceedError_Msg;

	// ----------------------------------------------------
	// Exchange Wizard
	// ----------------------------------------------------

	public String ExchangeWizard_Create_Title;

	public String ExchangeWizard_Complete_Title;

	public String ExchangeWizard_ExchangeItemsExchangePage_Message;

	public String ExchangeWizard_OrderItemsExchangePage_Message;

	public String ExchangeWizard_PaymentPage_Message;

	public String ExchangeWizard_SummaryPage_Message;

	public String ExchangeWizard_ItemsToBeExchanged_Section;

	public String ExchangeWizard_ItemsToBeOrdered_Section;

	public String ExchangeWizard_ShippingInformation_Section;

	public String ExchangeWizard_ExchangeOrderSummary_Section;

	public String ExchangeWizard_ExchangeSummary_Section;

	public String ExchangeWizard_RefundOptions_Section;

	public String ExchangeWizard_AdditionalAuthorizationOptions_Section;

	public String ExchangeWizard_Confirmation_Section;

	public String ExchangeWizard_SKUCode_Column;

	public String ExchangeWizard_ProductName_Column;

	public String ExchangeWizard_SKUOptions_Column;

	public String ExchangeWizard_OrderQty_Column;

	public String ExchangeWizard_UnitPrice_Column;

	public String ExchangeWizard_SelectAddress_Combo;

	public String ExchangeWizard_SelectShippingMethod_Combo;

	public String ExchangeWizard_AddItem_Button;

	public String ExchangeWizard_RemoveItem_Button;

	public String ExchangeWizard_OriginalPaymentSource;

	public String ExchangeWizard_ShippingAddress_Label;

	public String ExchangeWizard_ShippingMethod_Label;

	public String ExchangeWizard_ShippingCost_Label;

	public String ExchangeWizard_ItemSubTotal_Label;

	public String ExchangeWizard_ItemTaxes_Label;

	public String ExchangeWizard_ShippingTaxes_Label;

	public String ExchangeWizard_ShipmentDiscount_Label;

	public String ExchangeWizard_TotalBeforeTax_Label;

	public String ExchangeWizard_OrderTotal_Label;

	public String ExchangeWizard_TotalPriceOfItemsToBeExchanged_Label;

	public String ExchangeWizard_TotalPriceOfItemsToBeOrdered_Label;

	public String ExchangeWizard_RefundAmount_Label;

	public String ExchangeWizard_AdditionalAuthorizationAmmount_Label;

	public String ExchangeWizard_PhysicalReturnRequiredBeforeRefund_Label;

	public String ExchangeWizard_NewExchangeCreated_Label;

	public String ExchangeWizard_Exchange_Completed_Label;

	public String ExchangeWizard_NewOrderCreated_Label;

	public String ExchangeWizard_RemoveLineItemConfirmTitle;

	public String ExchangeWizard_RemoveLineItemConfirmText;

	public String ExchangeWizard_ItemsUnavailableTitle;

	public String ExchangeWizard_ItemsUnavailableText;

	public String ExchangeWizard_NoSkusToExchnage_Message;

	public String ExchangeWizard_MethodShouldBeSelected_Message;

	public String ExchangeWizard_AddressShouldBeSelected_Message;

	public String ExchangeWizard_CardError_Title;

	public String ExchangeWizard_InsufficientInventory_Title;

	public String ExchangeWizard_InsufficientInventory_Message;

	public String ExchangeWizard_TooBigDiscount_Message;

	public String Exchange_Pending_Payment_Details;

	public String Exchange_Completed_Payment_Details;

	public String Exchange_AddingItem_Message_NoPrice;

	public String Exchange_AddingItem_NoRecurring_Title;

	public String Exchange_AddingItem_NoRecurring_Message;

	// ----------------------------------------------------
	// Refund Wizard

	// ----------------------------------------------------

	public String RefundWizard_Title;

	public String RefundWizard_Page_Title;

	public String RefundWizard_CardInfoPage_Message;

	public String RefundWizard_OriginalPaymentSource;

	public String RefundWizard_RefundAmount;

	public String RefundWizard_RefundNote;

	public String RefundWizard_Note;

	public String RefundWizard_Note_Text;

	public String RefundWizard_CardDescription;

	public String RefundWizard_PaymentProceedError_Title;

	public String RefundWizard_PaymentProceedError_Message;

	public String RefundWizard_IncorrectRefundAmount_Title;

	public String RefundWizard_IncorrectRefundAmount_Message;

	public String RefundWizard_Confirmation_Section;

	// ----------------------------------------------------
	// Capture Wizard
	// ----------------------------------------------------

	public String CaptureWizard_Title;

	public String CaptureWizard_Page_Title;

	public String CaptureWizard_CardInfoPage_Message;

	public String CaptureWizard_Note_Text;

	public String CaptureWizard_Cancel_Title;

	public String CaptureWizard_Cancel_Message;

	public String ReAuthWizard_AuthAmount;

	public String ReAuthWizard_ShipmentNumber;

	public String ReAuthWizard_PaymentSource;

	public String ReAuthWizard_SelectPaymentSource;

	public String ReAuthWizard_Shipment_ColumnTitle;

	public String ReAuthWizard_Amount_ColumnTitle;

	public String ReAuthWizard_PaymentSource_ColumnTitle;

	public String ReAuthWizard_TransactionID_ColumnTitle;

	public String ReAuthWizard_Error_ColumnTitle;

	public String ReAuthWizard_AdviceOnError_Note;

	public String ReAuthWizard_GatewayError_Title;

	public String ReAuthWizard_GatewayError_Text;

	public String ReAuthWizard_Failed_SectionTitle;

	public String ReAuthWizard_Failed_Message;

	public String ReAuthWizard_Successful_SectionTitle;

	public String ReAuthWizard_Successful_Message;

	// ----------------------------------------------------
	// PaymentSummary control
	// ----------------------------------------------------

	public String Additional_Payment_Begin;

	public String Refund_Payment_Begin;

	public String Manual_Refund_Payment_Begin;

	public String Payment_End;

	public String Manual_Payment_End;

	public String Payment_Confirmation_Number;

	// ----------------------------------------------------
	// Refund Options Composite
	// ----------------------------------------------------
	public String RefundOptionsComposite_ReturnToOriginal_RadioButton;

	public String RefundOptionsComposite_ManualRefund_RadioButton;

	public String RefundOptionsComposite_Caution_Label;

	public String RefundOptionsComposite_CautionHeader_Label;

	public String RefundOptionsSection_Title;

	// ----------------------------------------------------
	// Resend RMA Email Wizard
	// ----------------------------------------------------

	public String ResendRMAEmailDialog_Send;

	public String ResendRMAEmailDialog_InitialMessage;

	public String ResendRMAEmailDialog_Title;

	public String ResendRMAEmailDialog_WindowTitle;

	public String ResendRMAEmailDialog_Recipient;

	public String ResendRMAEmailSuccess;

	public String ResendRMAEmailFailure;

	// ----------------------------------------------------
	// System
	// ----------------------------------------------------

	public String System;

	// ----------------------------------------------------
	// Order event filters
	// ----------------------------------------------------

	public String Event_Type_All;

	public String Event_Type_System;

	public String Event_Type_CSR;

	public String Event_Originator_All;

	public String Event_Filters;

	public String ErrorReleasingShipment_Title;

	public String ErrorReleasingShipment_Message;

	// ----------------------------------------------------
	// Order actions
	// ----------------------------------------------------

	public String OrderActionAddNode;

	public String OrderActionUnlockOrder;

	public String OrderActionViewAllNotes;

	public String OrderActionResendConfirmationEmail;

	// ----------------------------------------------------
	// Credit card error messages
	// ----------------------------------------------------

	public String InsufficientFundError;

	public String PaymentProcessingCommonError;

	// ----------------------------------------------------
	// Resend order confirmation email messages
	// ----------------------------------------------------

	public String resendConfirmationEmailSuccess;

	public String resendConfirmationEmailFailure;

	// ----------------------------------------------------
	// Price editing support
	// ----------------------------------------------------

	public String priceListName;

	public String listPrice;

	public String salePrice;

	public String quantity;

	// ----------------------------------------------------
	// Edit items details.
	// ----------------------------------------------------

	public String EditItemDetails_WindowTitle;

	public String EditItemDetails_OrderSkuTitle;

	public String EditItemDetails_PropertyValue;

	public String EditItemDetails_PropertyKey;

	public String CustomerSegmentsPage_Title;
	
	public String CustomerSegmentsPage_FormTitle;
	
	public String CustomerSegmentsPage_GroupName;
	
	public String CustomerSegmentsPage_Add;
	
	public String CustomerSegmentsPage_Remove;
	
	public String CustomerSegmentsPageDialog_AddTitle;
	
	public String CustomerSegmentsPageDialog_AddWindowTitle;
	
	public String CustomerSegmentsPageDialog_RemoveMessage;
	
	public String CustomerSegmentsPageDialog_RemoveConfirm;

	public String ShipmentSection_SubSectionStatus;

	// ----------------------------------------------------
	// Data Policy.
	// ----------------------------------------------------

	public String Delete_Label;

	public String DeletePolicyData_Confirm;

	public String DeleteCustomerData_Question;

	public String IncludeDataPointsWithGrantedConsent_Label;

	public String DeletePolicyData_Title;

	public String NoRemovableDataPointValues_Message;

	public String Error_Title;

	public String DataPointValueRemovalError_Message;

	public String DataPointValueSuccessfulDeletion_Message;

	public String CustomerDataPolicies_Title;

	public String ShowDisabledPolicies_Label;

	public String DataPolicyName_Label;

	public String DataPolicyState_Label;

	public String DataPolicyConsentGiven_Label;

	public String DataPolicyConsentUpdated_Label;

	public String ViewDataPoints_Label;

	public String Yes_Text;

	public String No_Text;

	public String DataPointName_Label;

	public String DataPointRemovable_Label;

	public String DataPointValue_Label;

	public String DataPointValueCreated_Label;

	public String DataPointValueLastUpdated_Label;

	// Define the map of enum constants to localized names
	private final Map<Enum<?>, String> localizedEnums = new HashMap<>();

	// Define the map of enum constants to localized names
	private final Map<ExtensibleEnum, String> localizedExtensibleEnums = new HashMap<>();


	/**
	 * Returns the localized name of the given enum constant.
	 *
	 * @param enumValue the enum to be localized
	 * @return the localized string for the enum
	 */
	public String getLocalizedName(final Enum<?> enumValue) {
		return localizedEnums.get(enumValue);
	}

	/**
	 * Returns the localized name of the given ExtensibleEnum constant.
	 *
	 * @param enumValue the enum to be localized
	 * @return the localized string for the enum
	 */
	public String getLocalizedName(final ExtensibleEnum enumValue) {
		return localizedExtensibleEnums.get(enumValue);
	}

	/**
	 * Build string for amount and currency displaying.
	 *
	 * @param currency amount currency.
	 * @param amount amount.
	 * @param locale locale
	 * @return string with amount and currency.
	 * @deprecated
	 * @see #formatMoneyAsString(com.elasticpath.money.Money, java.util.Locale)
	 */
	@Deprecated
	public String getAmountString(final Currency currency, final BigDecimal amount, final Locale locale) {
		final Money money = Money.valueOf(amount, currency);
		return formatMoneyAsString(money, locale);
	}

	/**
	 * Formats a money instance as a String suitable for display in the specified locale.
	 *
	 * @param money money
	 * @param locale locale
	 * @return String representation of specified money
	 */
	public String formatMoneyAsString(final Money money, final Locale locale) {
		final MoneyFormatter formatter = ServiceLocator.getService(ContextIdNames.MONEY_FORMATTER);

		return formatter.formatCurrency(money, locale);
	}

	/**
	 * Return localized displayable message for the specified credit card exception.
	 *
	 * @param error credit card exception.
	 * @return localized displayable message for the specified credit card exception.
	 */
	public String getCreditCardErrorMessage(final PaymentProcessingException error) {
		if (error instanceof InsufficientFundException) {
			return InsufficientFundError;
		}
			return PaymentProcessingCommonError;
	}

	private FulfillmentMessages() {
	}

	private void instantiateEnums() {
		//do we need to synchronize this?
		if (localizedEnums.isEmpty()) {
			localizedEnums.put(OrderPaymentStatus.APPROVED, PaymentStatus_Approved);
			localizedEnums.put(OrderPaymentStatus.FAILED, PaymentStatus_Failed);
			localizedEnums.put(OrderPaymentStatus.PENDING, PaymentStatus_Pending);
		}
		if (localizedExtensibleEnums.isEmpty()) {
			localizedExtensibleEnums.put(OrderStatus.CANCELLED, OrderStatus_Canceled);
			localizedExtensibleEnums.put(OrderStatus.CREATED, OrderStatus_Created);
			localizedExtensibleEnums.put(OrderStatus.COMPLETED, OrderStatus_Completed);
			localizedExtensibleEnums.put(OrderStatus.ONHOLD, OrderStatus_OnHold);
			localizedExtensibleEnums.put(OrderStatus.IN_PROGRESS, OrderStatus_InProgress);
			localizedExtensibleEnums.put(OrderStatus.AWAITING_EXCHANGE, OrderStatus_AwaitingExchange);
			localizedExtensibleEnums.put(OrderStatus.PARTIALLY_SHIPPED, OrderStatus_PartialShip);
			localizedExtensibleEnums.put(OrderStatus.FAILED, OrderStatus_Failed);
			localizedExtensibleEnums.put(OrderShipmentStatus.ONHOLD, ShipmentStatus_OnHold);
			localizedExtensibleEnums.put(OrderShipmentStatus.RELEASED, ShipmentStatus_Released);
			localizedExtensibleEnums.put(OrderShipmentStatus.SHIPPED, ShipmentStatus_Shipped);
			localizedExtensibleEnums.put(OrderShipmentStatus.CANCELLED, ShipmentStatus_Cancelled);
			localizedExtensibleEnums.put(OrderShipmentStatus.AWAITING_INVENTORY, ShipmentStatus_Awaiting);
			localizedExtensibleEnums.put(OrderShipmentStatus.INVENTORY_ASSIGNED, ShipmentStatus_Assigned);
			localizedExtensibleEnums.put(OrderShipmentStatus.FAILED_ORDER, ShipmentStatus_FailedOrder);
			localizedExtensibleEnums.put(PaymentType.CREDITCARD_DIRECT_POST, PaymentType_CreditCard);
			localizedExtensibleEnums.put(PaymentType.PAYPAL_EXPRESS, PaymentType_PayPalExpress);
			localizedExtensibleEnums.put(PaymentType.GIFT_CERTIFICATE, PaymentType_GiftCertificate);
			localizedExtensibleEnums.put(PaymentType.RETURN_AND_EXCHANGE, PaymentType_Exchange);
			localizedExtensibleEnums.put(PaymentType.PAYMENT_TOKEN, PaymentType_Token);
			localizedExtensibleEnums.put(PaymentType.HOSTED_PAGE, PaymentType_HostedPage);
		}

	}

	/**
	 * Gets the NLS localize message class.
	 * @return the localized message class.
	 */
	public static FulfillmentMessages get() {
		FulfillmentMessages fulfillmentMessages = LocalizedMessagePostProcessor.getUTF8Encoded(BUNDLE_NAME, FulfillmentMessages.class);
		fulfillmentMessages.instantiateEnums();
		return fulfillmentMessages;
	}
}
