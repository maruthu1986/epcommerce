/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.store.promotions;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.elasticpath.cmclient.core.helpers.extenders.PluginHelper;
import com.elasticpath.cmclient.core.nls.LocalizedMessagePostProcessor;
import com.elasticpath.cmclient.store.StorePlugin;
import com.elasticpath.commons.util.extenum.ExtensibleEnum;
import com.elasticpath.domain.rules.DiscountType;
import com.elasticpath.domain.rules.RuleElementType;
import com.elasticpath.domain.rules.RuleExceptionType;
import com.elasticpath.domain.rules.RuleParameterNumItemsQuantifier;

/**
 * Messages class for the promotions plug-in.
 */
@SuppressWarnings({ "PMD.TooManyFields", "PMD.ExcessivePublicCount", "PMD.VariableNamingConventions" })
public final class PromotionsMessages {

	/**
	 * LOG logger.
	 */
	private static final Logger LOG = Logger.getLogger(PromotionsMessages.class);



	private static final String BUNDLE_NAME = "com.elasticpath.cmclient.store.promotions.PromotionsResources"; //$NON-NLS-1$

	// ----------------------------------------------------
	// Global keys
	// ----------------------------------------------------
	public String Promotion_State_Active;

	public String Promotion_State_Disabled;

	public String Promotion_State_Expired;

	public String Discount_Type_Cart_Item;

	public String Discount_Type_Cart_Subtotal;

	public String Discount_Type_Shipping;

	public String Discount_Type_Catalog;

	public String Discount_Type_Coupon;

	public String Promotion_State;

	public String Promotion_Type;

	public String Promotion_Catalog;

	public String Promotion_Store;

	public String Promotion_CouponCode;

	public String Promotion_PromotionName;

	// ----------------------------------------------------
	// Promotion Editors
	// ----------------------------------------------------

	public String PromotionEditor_Save_StatusBarMsg;

	// Promotion Details Page

	public String PromotionDetailsPage_None;

	public String PromotionDetailsPage_Title;

	public String PromotionDetailsPage_Form_Title;

	public String PromoDetailsOverview_Title;

	public String PromoDetailsCatalogRules_Title;

	public String PromoDetailsOverview_Description;

	public String PromoDetailsOverview_StoreCombo_InitialMessage;

	public String PromoDetailsOverview_CatalogCombo_InitialMessage;

	public String PromoDetailsOverview_Label_PromotionName;

	public String PromoDetailsOverview_Label_Description;

	public String PromoDetailsOverview_Label_CreatedBy;

	public String PromoDetailsOverview_Label_PromotionType;

	public String PromoDetailsOverview_StoreVisible;

	public String PromoDetailsOverview_Label_ActiveFrom;

	public String PromoDetailsOverview_Label_ActiveTo;

	public String PromoDetailsOverview_Error_DuplicateName;

	public String PromoDetailsOverview_Label_DisplayName;

	// Limited Usage Promotion
	public String PromoDetailsOverview_LimitedUsagePromotion;

	public String PromoDetailsOverview_AllowedLimit;

	public String CreatePromotionWizardDetailsPage_LimUsagePromt_Error;

	public String PromoDetailsOverview_AllowedLimitCurentlyUsed;

	// Promotion Rules Page
	public String PromotionRulesPage_Title;

	public String PromotionRulesPage_Form_Title;

	public String PromoRulesDefinition_Description;

	public String PromoRulesDefinition_Label_Any;

	public String PromoRulesDefinition_Label_All;

	public String PromoRulesDefinition_Label_Is;

	public String PromoRulesDefinition_Label_IsNot;

	public String PromoRulesDefinition_Label_AtLeast;

	public String PromoRulesDefinition_Label_Exactly;

	public String PromoRulesDefinition_Tooltip_AddEligibility;

	public String PromoRulesDefinition_Tooltip_RemoveEligibility;

	public String PromoRulesDefinition_Tooltip_AddCondition;

	public String PromoRulesDefinition_Tooltip_RemoveCondition;

	public String PromoRulesDefinition_Tooltip_RemoveAction;

	public String PromoRulesDefinition_Tooltip_AddExclusion;

	public String PromoRulesDefinition_Tooltip_RemoveExclusion;

	public String PromoRulesDefinition_Tooltip_EditValue;

	public String PromoRulesDefinition_Tooltip_RemoveException;

	public String PromoRulesDefinition_Label_EligibilityStart;

	public String PromoRulesDefinition_Label_EligibilityEnd;

	public String PromoRulesDefinition_Label_ConditionStart;

	public String PromoRulesDefinition_Label_ConditionEnd;

	public String PromoRulesDefinition_Label_Action_CartPromos;

	public String PromoRulesDefinition_Label_Action_CatalogPromos;

	public String PromoRulesDefinition_Label_Select;

	public String PromoRulesDefinition_Label_Excluding;

	public String PromoRulesDefintion_Error_Title;

	public String PromoRulesDefintion_Error_Required_Eligibility;

	public String PromoRulesDefintion_Error_Required_MultiUse_Coupon_Code;

	public String PromoRulesDefintion_Error_Required_Action;

	public String PromoRulesDefintion_Error_Required_Condition;

	public String PromoRulesDefintion_Error_Select_Links;

	public String PromoRulesDefinition_Error_Brand;

	// Promotion Rules Page
	public String PromotionCouponsPage_Title;

	public String PromotionCouponsPage_Form_Title;

	public String PromoCoupons_Label_MultiUseCouponCode;

	public String PromoCoupons_label_NoCoupons;

	public String PromoStoreRules_State;

	// PromotionShopperPage
	public String PromotionShopperPage_Title;

	// PromotionTimePage
	public String PromotionTimePage_Title;

	// ----------------------------------------------------
	// Promotion Views
	// ----------------------------------------------------
	public String SearchView_PromotionsTab;

	public String SearchView_FiltersGroup;

	public String SearchView_Filters_AllPromotions;

	public String SearchView_Filters_AllPromotionTypes;

	public String SearchView_Filters_CatalogPromotionType;

	public String SearchView_Filters_ShoppingCartPromotionType;

	public String SearchView_Filters_AllCatalogs;

	public String SearchView_Filters_AllStores;

	public String SearchView_SearchTermsGroup;

	public String SearchView_Error_CodeNotFound;

	public String SearchView_SearchButton;

	public String SearchView_ClearButton;

	public String SearchResultsView_TableColumnTitle_PromotionName;

	public String SearchResultsView_TableColumnTitle_Store;

	public String SearchResultsView_TableColumnTitle_Description;

	public String SearchResultsView_TableColumnTitle_PromotionType;

	public String SearchResultsView_TableColumnTitle_ActiveFrom;

	public String SearchResultsView_TableColumnTitle_ActiveTo;

	public String SearchResultsView_TableColumnTitle_Enabled;
	public String SearchResultsView_TableColumnTitle_Expired;
	public String SearchView_SortingGroup;

	public String SearchView_Sorting_Label_SortByColumn;

	public String SearchView_Sorting_Label_SortOrder;

	// ----------------------------------------------------
	// Promotions Wizards
	// ----------------------------------------------------
	public String CreatePromotionsWizard_Title;

	public String NewCatalogPromotionWizard_Title;

	public String CreatePromotionsWizard_Error_Dialog_Title;

	// Promotion Details Page
	public String CreateCatalogPromotionWizardDetailsPage_Catalog_Title;

	public String CreatePromotionWizardDetailsPage_Title;

	public String CreatePromotionWizardDetailsPage_Description;

	public String CreatePromotionWizardDetailsPage_Name_Uniqueness_Error;

	public String CreatePromotionWizardDetailsPage_Date_Error;

	// Promotion step2 page
	public String CreatePromotionWizard_Page_Step2_Title;
	public String CreatePromotionWizard_Page_Step2_Description;

	// Promotion step3 page
	public String CreatePromotionWizard_Page_Step3_Title;
	public String CreatePromotionWizard_Page_Step3_Description;

	// Promotion Rules Page
	public String CreateCatalogPromotionWizardRulesPage_Title;

	public String CreatePromotionWizardRulesPage_Title;

	public String CreatePromotionWizardRulesPage_Description;

	public String CreatePromotionWizardRulesPage_Validation_Error;

	public String CreatePromotion_GiftCertificateError;

	// Promotion Coupons Page
	public String CreatePromotionWizardCouponsPage_Title;

	public String CreatePromotionWizardCouponsPage_Description;

	public String CreatePromoWizardCouponsPage_CouponCode_Unique_Error;

	public String CouponConfigPageNotActivateByCoupons;

	public String CouponConfigPageActivatedByPublicCoupons;

	public String CouponConfigPageActivatedByPrivateCoupons;

	public String CouponConfigPagePrivateCouponsExpireDays;

	public String CouponConfigPagePrivateCouponsExpireDaysTagged;

	public String CouponConfigPagePrivateCouponsNoExpiry;

	public String CouponConfigPagePrivateCouponsLimited;

	public String CouponConfigPageManageCouponCodes;

	public String CouponConfigPagePrivateCouponsNoLimit;

	public String CouponConfigPagePublicCouponsLimited;

	public String CouponConfigPagePublicCouponsLimitedEachShopper;

	public String CouponConfigPagePublicCouponsNoLimit;

	public String CouponCodesAlreadyExist;

	public String CouponCodeAlreadyExists;

	public String CouponConfigPageNoMaxUseError;

	public String CouponConfigPageNoMaxUseEachShopperError;

	public String CouponConfigPageNoExpireDaysError;

	public String CouponConfigPageUnsavedCouponsTitle;

	public String CouponConfigPageUnsavedCoupons;

	public String CouponConfigPageLimitToSingleOrder;

	public String CreatePromotionWizardRulesPage_Coupon_Validation_Error;

	// ----------------------------------------------------
	// Promotion Rule Elements
	// ----------------------------------------------------
	// Rule Eligibilities
	public String CustomerGroupEligibility;

	public String EveryoneEligibility;

	public String ExistingCustomerEligibility;

	public String FirstTimeBuyerEligibility;

	// Rule Conditions
	public String BrandCondition;

	public String CartContainsItemsOfCategoryCondition;

	public String CartCurrencyCondition;

	public String CartSubtotalCondition;

	public String ProductCategoryCondition;

	public String ProductCondition;

	public String ProductInCartCondition;

	public String SkuInCartCondition;

	public String AnySkuInCartCondition;

	// Rule Actions
	public String CartCategoryAmountDiscountAction;

	public String CartCategoryPercentDiscountAction;

	public String CartNFreeSkusAction;

	public String CartNthProductPercentDiscountAction;

	public String CartProductAmountDiscountAction;

	public String CartProductPercentDiscountAction;

	public String CartSkuAmountDiscountAction;

	public String CartAnySkuAmountDiscountAction;

	public String CartSkuPercentDiscountAction;

	public String CartAnySkuPercentDiscountAction;

	public String CartSubtotalAmountDiscountAction;

	public String CartSubtotalPercentDiscountAction;

	public String CartSubtotalDiscountAction;

	public String CatelogSkuAmountDiscountAction;

	public String CatelogSkuPercentDiscountAction;

	public String ProductAmountDiscountAction;

	public String ProductPercentDiscountAction;

	public String ShippingAmountDiscountAction;

	public String ShippingPercentDiscountAction;

	public String CatalogCurrencyAmountDiscountAction;

	public String CatalogCurrencyPercentDiscountAction;

	public String CouponAssignmentAction;

	// ----------------------------------------------------
	// Promotion Rule Exceptions
	// ----------------------------------------------------
	public String CategoryException;

	public String ProductException;

	public String SkuException;

	// Define the map of enum constants to localized names
	private final Map<ExtensibleEnum, String> localizedPromotionEnums = new HashMap<>();

	// Sorting
	public String SearchView_Sort_Promotion_Name;

	public String SearchView_Sort_Promotion_Enabled;

	public String SearchView_Sort_Promotion_Type;

	public String SearchView_Sort_Promotion_Start_Date;

	public String SearchView_Sort_Promotion_Exp_Date;

	public String PromotionEditor_OnSavePrompt;

	public String CouponEditorDialog_CouponCode;

	public String CouponEditorDialog_Results;

	public String CouponEditorDialog_Code;

	public String CouponEditorDialog_Clear;

	public String CouponEditorDialog_Search;

	public String CouponEditorDialog_Add;

	public String CouponEditorDialog_Edit;

	public String CouponEditorDialog_Delete;

	public String CouponEditorDialog_ImportCodes;

	public String CouponEditorDialog_Title;

	public String CouponEditorDialog_WindowTitle;

	public String CouponEditorDialog_InitialMessage;

	public String CouponEditorDialog_NoResultsFound;

	public String CouponSingleEditorDialog_CouponCode;

	public String CouponSingleEditorDialog_Status;

	public String CouponSingleEditorDialog_Status_In_Use;

	public String CouponSingleEditorDialog_Status_Suspended;

	public String CouponSingleEditorDialog_Title;

	public String CouponSingleEditorDialog_DuplicateCoupon;

	public String CouponSingleEditorDialog_DuplicateCouponWithCode;

	public String CouponSingleEditorDialog_DuplicateCouponAndEmail;

	public String CouponSingleEditorDialog_DuplicateCouponAndNullEmail;

	public String CouponAddPopupDialogDescription;

	public String CouponEditPopupDialogDescription;

	public String Coupon_Delete_Title;

	public String Coupon_Delete_Message;

	public String Coupon_Email;

	public String CouponEditorPage_Form_Title;

	public String CouponEditorPage_Title;

	public String CouponEditorPart_Add;

	public String CouponEditorPart_Edit;

	public String CouponEditorPart_Import;

	public String CouponEditorPart_Table_CouponCode;

	public String CouponEditorPart_Search;

	public String CouponEditorPart_Filter;

	public String CouponEditorPart_Status_All;
	public String CouponEditorPart_Status_In_Use;
	public String CouponEditorPart_Status_Suspended;

	public String CouponEditorPart_Label_CouponCode;
	public String CouponEditorPart_Label_EmailAddress;

	public String CouponEditorPart_Label_Status;

	public String CouponEditorPart_Table_EmailAddress;

	public String CouponEditorPart_Table_Status;

	public String CouponEditorPart_Table_InUse;
	public String CouponEditorPart_Table_Suspended;

	public String CouponEditorPart_Table_EmptyString;
	public String promotionNotAvailable;

	// Empty private constructor to ensure this class can never be constructed.
	private PromotionsMessages() {
	}

	private void instantiateEnums() {
		if (localizedPromotionEnums.isEmpty()) {
			// DiscountTypes
			localizedPromotionEnums.put(DiscountType.CART_ITEM_DISCOUNT, Discount_Type_Cart_Item);
			localizedPromotionEnums.put(DiscountType.CART_SUBTOTAL_DISCOUNT, Discount_Type_Cart_Subtotal);
			localizedPromotionEnums.put(DiscountType.SHIPPING_DISCOUNT, Discount_Type_Shipping);
			localizedPromotionEnums.put(DiscountType.CATALOG_DISCOUNT, Discount_Type_Catalog);
			localizedPromotionEnums.put(DiscountType.COUPON_DISCOUNT, Discount_Type_Coupon);
			// RuleParameter number of item Quantifiers
			localizedPromotionEnums.put(RuleParameterNumItemsQuantifier.AT_LEAST, PromoRulesDefinition_Label_AtLeast);
			localizedPromotionEnums.put(RuleParameterNumItemsQuantifier.EXACTLY, PromoRulesDefinition_Label_Exactly);
			// RuleElementTypes
			localizedPromotionEnums.put(RuleElementType.CUSTOMER_GROUP_ELIGIBILITY, CustomerGroupEligibility);
			localizedPromotionEnums.put(RuleElementType.EVERYONE_ELIGIBILITY, EveryoneEligibility);
			localizedPromotionEnums.put(RuleElementType.EXISTING_CUSTOMER_ELIGIBILITY, ExistingCustomerEligibility);
			localizedPromotionEnums.put(RuleElementType.FIRST_TIME_BUYER_ELIGIBILITY, FirstTimeBuyerEligibility);
			localizedPromotionEnums.put(RuleElementType.BRAND_CONDITION, BrandCondition);
			localizedPromotionEnums.put(RuleElementType.CART_CONTAINS_ITEMS_OF_CATEGORY_CONDITION, CartContainsItemsOfCategoryCondition);
			localizedPromotionEnums.put(RuleElementType.CART_CURRENCY_CONDITION, CartCurrencyCondition);
			localizedPromotionEnums.put(RuleElementType.CART_SUBTOTAL_CONDITION, CartSubtotalCondition);
			localizedPromotionEnums.put(RuleElementType.PRODUCT_CATEGORY_CONDITION, ProductCategoryCondition);
			localizedPromotionEnums.put(RuleElementType.PRODUCT_CONDITION, ProductCondition);
			localizedPromotionEnums.put(RuleElementType.PRODUCT_IN_CART_CONDITION, ProductInCartCondition);
			localizedPromotionEnums.put(RuleElementType.SKU_IN_CART_CONDITION, SkuInCartCondition);
			localizedPromotionEnums.put(RuleElementType.ANY_SKU_IN_CART_CONDITION, AnySkuInCartCondition);
			localizedPromotionEnums.put(RuleElementType.CART_CATEGORY_AMOUNT_DISCOUNT_ACTION, CartCategoryAmountDiscountAction);
			localizedPromotionEnums.put(RuleElementType.CART_CATEGORY_PERCENT_DISCOUNT_ACTION, CartCategoryPercentDiscountAction);
			localizedPromotionEnums.put(RuleElementType.CART_N_FREE_SKUS_ACTION, CartNFreeSkusAction);
			localizedPromotionEnums.put(RuleElementType.CART_NTH_PRODUCT_PERCENT_DISCOUNT_ACTION, CartNthProductPercentDiscountAction);
			localizedPromotionEnums.put(RuleElementType.CART_PRODUCT_AMOUNT_DISCOUNT_ACTION, CartProductAmountDiscountAction);
			localizedPromotionEnums.put(RuleElementType.CART_PRODUCT_PERCENT_DISCOUNT_ACTION, CartProductPercentDiscountAction);
			localizedPromotionEnums.put(RuleElementType.CART_SKU_AMOUNT_DISCOUNT_ACTION, CartSkuAmountDiscountAction);
			localizedPromotionEnums.put(RuleElementType.CART_ANY_SKU_AMOUNT_DISCOUNT_ACTION, CartAnySkuAmountDiscountAction);
			localizedPromotionEnums.put(RuleElementType.CART_SKU_PERCENT_DISCOUNT_ACTION, CartSkuPercentDiscountAction);
			localizedPromotionEnums.put(RuleElementType.CART_ANY_SKU_PERCENT_DISCOUNT_ACTION, CartAnySkuPercentDiscountAction);
			localizedPromotionEnums.put(RuleElementType.CART_SUBTOTAL_AMOUNT_DISCOUNT_ACTION, CartSubtotalAmountDiscountAction);
			localizedPromotionEnums.put(RuleElementType.CART_SUBTOTAL_PERCENT_DISCOUNT_ACTION, CartSubtotalPercentDiscountAction);
			localizedPromotionEnums.put(RuleElementType.CART_SUBTOTAL_DISCOUNT_ACTION, CartSubtotalDiscountAction);
			localizedPromotionEnums.put(RuleElementType.CATALOG_SKU_AMOUNT_DISCOUNT_ACTION, CatelogSkuAmountDiscountAction);
			localizedPromotionEnums.put(RuleElementType.CATALOG_SKU_PERCENT_DISCOUNT_ACTION, CatelogSkuPercentDiscountAction);
			localizedPromotionEnums.put(RuleElementType.PRODUCT_AMOUNT_DISCOUNT_ACTION, ProductAmountDiscountAction);
			localizedPromotionEnums.put(RuleElementType.PRODUCT_PERCENT_DISCOUNT_ACTION, ProductPercentDiscountAction);
			localizedPromotionEnums.put(RuleElementType.SHIPPING_AMOUNT_DISCOUNT_ACTION, ShippingAmountDiscountAction);
			localizedPromotionEnums.put(RuleElementType.SHIPPING_PERCENT_DISCOUNT_ACTION, ShippingPercentDiscountAction);
			localizedPromotionEnums.put(RuleElementType.CATALOG_CURRENCY_AMOUNT_DISCOUNT_ACTION, CatalogCurrencyAmountDiscountAction);
			localizedPromotionEnums.put(RuleElementType.CATALOG_CURRENCY_PERCENT_DISCOUNT_ACTION, CatalogCurrencyPercentDiscountAction);
			localizedPromotionEnums.put(RuleElementType.COUPON_ASSIGNMENT_ACTION, CouponAssignmentAction);

			// RuleExceptionTypes
			localizedPromotionEnums.put(RuleExceptionType.CATEGORY_EXCEPTION, CategoryException);
			localizedPromotionEnums.put(RuleExceptionType.PRODUCT_EXCEPTION, ProductException);
			localizedPromotionEnums.put(RuleExceptionType.SKU_EXCEPTION, SkuException);
		}

	}
	
	/**
	 * Returns the localized name of the given enum constant.
	 *
	 * @param anEnum - The enum constant to get a localized name for
	 * @return The localized name of the given enum constant
	 */
	public String getLocalizedName(final ExtensibleEnum anEnum) {
		try {
			String localisedString = localizedPromotionEnums.get(anEnum);

			if (localisedString == null) {
				localisedString = PluginHelper.getExtendedLocalizedString(StorePlugin.PLUGIN_ID, anEnum);
			}
			return localisedString;
		} catch (Exception e) {
			LOG.error("Error getting extended localized string.", e);
		}

		return null;
	}

	/**
	 * Gets the NLS localize message class.
	 * @return the localized message class.
	 */
	public static PromotionsMessages get() {
		PromotionsMessages promotionsMessages = LocalizedMessagePostProcessor.getUTF8Encoded(BUNDLE_NAME, PromotionsMessages.class);
		promotionsMessages.instantiateEnums();

		return promotionsMessages;
	}

}
