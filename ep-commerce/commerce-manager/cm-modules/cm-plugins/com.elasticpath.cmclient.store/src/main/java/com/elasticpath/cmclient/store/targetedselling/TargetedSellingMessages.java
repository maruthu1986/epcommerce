/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.cmclient.store.targetedselling;

import java.lang.reflect.Field;

import com.elasticpath.cmclient.core.MessageException;
import com.elasticpath.cmclient.core.nls.LocalizedMessagePostProcessor;

/**
 * Messages class for the promotions plug-in.
 */
@SuppressWarnings({ "PMD.VariableNamingConventions", "PMD.TooManyFields", "PMD.ExcessivePublicCount" })
public final class TargetedSellingMessages {

	// Empty private constructor to ensure this class can never be constructed.
	private TargetedSellingMessages() {
	}

	private static final String BUNDLE_NAME = "com.elasticpath.cmclient.store.targetedselling.TargetedSellingResources"; //$NON-NLS-1$

	public String DynamicContentTabTitle;

	public String DynamicContentDescription;

	public String Assigned;

	public String Search;

	public String FindDynamicContent;

	public String DynamicContentName;

	public String SearchView_SearchButton;

	public String SearchView_ClearButton;

	public String DynamicContent_Status;

	public String DynamicContent_StatusAssigned;

	public String DynamicContent_StatusNotAssigned;

	public String DynamicContent_StatusAll;

	// Saved Condition Editor
	
	public String Summary;
	
	public String ConditionBuilder;
	
	public String Saving_SavedCondition;
	
	public String NewSavedCondition;
	
	public String Name;
	
	public String Description;
	
	public String Dictionary;
	
	public String SavedConditionInUse;
	
	public String SavedConditionInUseByContentDeliverys;
	
	public String ConfirmDeleteSavedConditionMsgBoxTitle;
	
	public String ConfirmDeleteSavedConditionMsgBoxText;
	
	public String CantRemoveStoreTitle;
	
	public String CantRemoveStoreMessage;
	
	
	// Dynamic Content Wizard -------------------------

	public String NewDynamicContentCreateWizard_Title;
	
	public String NewDynamicContentEditWizard_Title;

	public String NewDynamicContentWizard_Description;

	public String NewDynamicContentWizard_Name_Label;

	public String NewDynamicContentWizard_Wrapper_Combo_Label;

	public String NewDynamicContentWizard_TargetID_Label;

	public String NewDynamicContentWizard_Description_Label;

	public String DynamicContentWizardWrapperPage_Title;

	public String DynamicContentWizardParametersPage_Title;
	
	public String DynamicContentWizardWrapperPageEdit_Title;
	
	public String DynamicContentWizardParametersPageEdit_Title;

	public String DynamicContentWizardParametersPage_Description;

	public String DynamicContentNameExists;

	public String DynamicContentWizardParametersPage_ButtonEdit;

	public String DynamicContentWizardParametersPage_ButtonReset;

	// Dynamic Content Toolbar ------------------------

	public String DynamicContentToolbar_CreateAction;

	public String DynamicContentToolbar_DeleteAction;

	public String DynamicContentToolbar_EditAction;

	public String NoLongerExistDynamicContentMsgBoxTitle;

	public String NoLongerExistDynamicContentMsgBoxText;

	public String UsedDynamicContentDialogTitle;

	public String UsedDynamicContentDialogText;

	public String ConfirmDeleteDynamicContentMsgBoxTitle;

	public String ConfirmDeleteDynamicContentMsgBoxText;

	public String NewDynamicContentWizard_LanguageCombo_Label;

	public String ParameterValueEditor_TableColumnTitle_Name;

	public String ParameterValueEditor_TableColumnTitle_Type;

	public String ParameterValueEditor_TableColumnTitle_MLang;

	public String ParameterValueEditor_TableColumnTitle_Value;

	public String YesNoForBoolean_true;

	public String YesNoForBoolean_false;

	public String NewDynamicContentWizard_WrapperCombo_InitialMessage;

	// -------------------------------- Dynamic Content Delivery

	public String DynamicContentDeliveryTabTitle;

	public String FindDynamicContentDelivery;

	public String DynamicContentDeliveryName;

	public String DynamicContentDeliveryDescription;

	public String DynamicContentDelivery_Store;

	public String DynamicContentDelivery_ContentSpace;

	public String DynamicContentDelivery_StartDate;

	public String DynamicContentDelivery_EndDate;

	public String DynamicContent;

	public String DynamicContentAll;

	public String ContentSpace;

	public String NoLongerExistDynamicContentDeliveryMsgBoxTitle;

	public String NoLongerExistDynamicContentDeliveryMsgBoxText;

	public String DynamicContentAttribute_Edit;

	// ---------------------------------- Dynamic Content Delivery Wizard
	
	public String DCDeliveryWizard_EditTitle;
	
	public String DCDeliveryWizard_EditNamePage_Title;
	
	public String DCDeliveryWizard_EditDC_Page_Title;
	
	public String DCDeliveryWizard_EditAT_Page_Title;
	
	public String DCDeliveryWizard_EditStore_Page_Title;
	
	public String DCDeliveryWizard_EditDates_Range_Page_Title;	
	
	public String DCDeliveryWizard_Edit_Shopper_Page_Title;
	
	public String DCDeliveryWizard_Title;

	public String DCDeliveryWizard_NamePage_Title;

	public String DCDeliveryWizard_AT_Page_Title;

	public String DCDeliveryWizard_DC_Page_Title;

	public String DCDeliveryWizard_Stores_Page_Title;

	public String DCDeliveryWizard_Time_Range_Page_Title;
	
	public String DCDeliveryWizard_Shopper_Page_Title;

	public String DCDeliveryWizard_NamePage_Description;

	public String DCDeliveryWizard_AT_Page_Description;

	public String DCDeliveryWizard_DC_Page_Description;

	public String DCDeliveryWizard_Store_Page_Description;

	public String DCDeliveryWizard_Dates_Range_Page_Description;
	
	public String DCDeliveryWizard_Shopper_Page_Description;

	public String DCDeliveryWizard_Name_Label;

	public String DynamicContentDeliveryNameExists;

	public String DCDeliveryWizard_Description_Label;
	
	public String DCDeliveryWizard_Priority_Label;
	
	public String DCDeliveryWizard_Priority_Lowest_Label;
	
	public String DCDeliveryWizard_Priority_Highest_Label;

	public String DCDeliveryWizard_AvailableDynamicContents_Label;

	public String DCDeliveryWizard_SelectedDynamicContent_Label;
	
	public String DCDeliveryWizard_DynamicContent_Columne_Name;
	
	public String DCDeliveryWizard_DynamicContent_Columne_Description;
	
	public String DCDeliveryWizard_DynamicContent_Columne_ContentWrapper;

	public String DCDeliveryWizard_AvailableAT_Label;

	public String DCDeliveryWizard_SelectedAT_Label;

	public String DynamicContentDeliveryDC_Required;

	public String DynamicContentDeliveryAT_Required;

	public String DynamicContentDeliveryToolbar_CreateAction;

	public String DynamicContentDeliveryToolbar_DeleteAction;

	public String DynamicContentDeliveryToolbar_EditAction;

	public String ConfirmDeleteDynamicContentDeliveryMsgBoxTitle;

	public String ConfirmDeleteDynamicContentDeliveryMsgBoxText;

	public String DynamicContentDeliveryBothDateBlankWarnMessage;

	public String DCA_ShopperPage_Tag_Label;
	
	public String DCA_ShopperPage_Value_Label;
	
	public String DCA_ShopperPage_OPERATOR_Label;

	public String DCA_ShopperPage_Add_Condition_Tooltip;
	
	public String DCA_ShopperPage_Remove_Condition_Tooltip;
	
	public String DCA_ShopperPage_Operator_FirstItem;
	
	public String DCDeliveryWizard_TimePage_RadioButtonSavedConditions;
	
	public String operator_equalTo;

	public String operator_notEqualTo;

	public String operator_includes;

	public String operator_notIncludes;

	public String operator_lessThan;

	public String operator_greaterThan;

	public String operator_lessThanOrEqualTo;

	public String operator_greaterThanOrEqualTo;
	
	public String operator_equalsIgnoreCase;
	
	public String operator_includesIgnoreCase;
	
	public String FilterOptionAll;
	
	public String TotalLengthOfConditionsReached;
	
	public String  equalTo;
	
	public String  notEqualTo;

	public String  lessThan;
		
	public String  greaterThan;
	
	public String  includes;
	
	public String  notIncludes;
	
	public String  lessThanOrEqualTo;
	
	public String  greaterThanOrEqualTo;
	
	public String  equalsIgnoreCase;
	
	public String  includesIgnoreCase;	

	public String ErrorMessageUnsupportedType;
	
	public String ErrorMessageCharLength;
	
	public String ErrorMessageValueType;
	
	public String ErrorMessageValueTypeInteger;
	
	public String ErrorMessageValueTypeFloat;
	
	public String ErrorMessageValueTypeBigDecimal;
	
	public String ErrorMessageValueTypeLong;
	
	public String ErrorMessageNotInAllowedValues;

	public String TypeWholeNumber;
	
	public String ConditionalExpressionTabTitle;
	
	public String FindConditionalExpression;

	// Conditional Expression Wizard -----------------------------
	
	public String ConditionalExpressionNameExists;
	
	public String NewConditionalExpressionCreateWizard_Title;
	public String NewConditionalExpressionWizard_Description;
	public String NewConditionalExpressionWizard_Dictionary_Combo_Label;
	
	public String ConditionalExpressionName;
	public String ConditionalExpressionDescription;
	public String ConditionalExpressionType;
	public String ConditionalExpressionTags;
	public String EditConditionalExpressionTagValue;

	public String ConditionalExpressionToolbar_CreateAction;
	public String ConditionalExpressionToolbar_DeleteAction;
	public String ConditionalExpressionToolbar_EditAction;

	public String ConditionalExpressionTag;
	public String ConditionalExpressionDynamicContentDelivery;

	public String ConditionalExpressionAll;
	
	public String ConditionalEditor_OnSavePrompt;
	
	// Conditional Expression Editor warning messages ----------------
	
	public String ConditionalExpressionEditor_InvalidName_Title;
	public String ConditionalExpressionEditor_InvalidName_Body;
	public String ConditionalExpressionEditor_DuplicateName_Title;
	public String ConditionalExpressionEditor_DuplicateName_Body;
	public String ConditionalExpressionEditor_BlankExpression_Title;
	public String ConditionalExpressionEditor_BlankExpression_Body;
	
	public String ConditionalExpressionEditor_WarningDialogTitle;
	public String ConditionalExpressionEditor_WarningDialogMessage;  

	public String ConditionBuilder_Title;
	public String ConditionBuilder_AddConditionButton;
	
	public String ConditionBuilder_Add_Rule_label;
	public String ConditionBuilder_Remove_Rule_label;
	
	public String DeleteSavedConditionMsgBoxTitle;
	public String DeleteSavedCondition_CloseEditor;

	public String AvailableStores_Label;
	public String SelectedStores_Label;
	
	public String LogicalOperator_AND;
	public String LogicalOperator_OR;



	/**
	 * Return a message String given the message key.
	 * 
	 * @param messageKey the message key (static field) that holds the message String
	 * @return the message String
	 */
	public String getMessage(final String messageKey) {
		try {
			final Field field = TargetedSellingMessages.class.getField(messageKey);
			return (String) field.get(this);
		} catch (final Exception e) {
			throw new MessageException(e);
		}
	}

	/**
	 * Gets the NLS localize message class.
	 * @return the localized message class.
	 */
	public static TargetedSellingMessages get() {
		return LocalizedMessagePostProcessor.getUTF8Encoded(BUNDLE_NAME, TargetedSellingMessages.class);
	}

}
