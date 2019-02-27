/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.cmclient.conditionbuilder.plugin;

import java.lang.reflect.Field;

import com.elasticpath.cmclient.core.MessageException;
import com.elasticpath.cmclient.core.nls.LocalizedMessagePostProcessor;

/**
 * Messages class for the promotions plug-in.
 */
@SuppressWarnings({ "PMD.VariableNamingConventions", "PMD.TooManyFields" })
public final class ConditionBuilderMessages { 

	// Empty private constructor to ensure this class can never be constructed.
	private ConditionBuilderMessages() {
	}

	private static final String BUNDLE_NAME = "com.elasticpath.cmclient.conditionbuilder.plugin.ConditionBuilderResources"; //$NON-NLS-1$

	public String ConditionBuilder_Add_Condition_Tooltip;
	public String ConditionBuilder_Page_Remove_Condition_Tooltip;
	
	public String ConditionBuilder_Add_Rule_label;
	public String ConditionBuilder_Remove_Rule_label;
	public String ConditionBuilder_Remove_Statement_label;
	public String ConditionBuilder_AddConditionButton;
	public String ConditionBuilder_Title;
	
	public String EditConditionalExpressionTagValue;
	
	public String ShopperPage_Tag_FirstItem;
	public String AvailableStores_Label;
	public String SelectedStores_Label;
	public String AssignSpecificStores;
	public String Start_Date_Label;
	public String End_Date_Label;
	public String Validation_EndDateBeforeStartDate;
	public String Validation_EndDateInThePast;
	public String PleaseSelect;
	public String ShowStoresSavedConditions;
	public String Wizard_Store_Page_Button_All;
	// LogicalOperatorType
	public String LogicalOperator_AND;
	public String LogicalOperator_OR;
	
	public String ShowTimeSavedConditions;
	
	public String Wizard_Dates_Range_Page_Button_All;
	
	public String Wizard_Dates_Range_Page_Button_Range;

	public String Wizard_Store_Button_Specific;
	
	public String Wizard_ShopperPage_RadioButtonAll;
	public String Wizard_ShopperPage_RadioButtonConditions;
	public String Wizard_ShopperPage_RadioButtonSavedConditions;

	public String Wizard_TimePage_RadioButtonAll;
	public String Wizard_TimePage_RadioButtonConditions;
	public String Wizard_TimePage_RadioButtonSavedConditions;

	public String ConditionRequired;
	public String ShopperSectionPart_Error_Title;
	public String TimeSectionPart_Error_Title;

	
	/**
	 * Return a message String given the message key.
	 * 
	 * @param messageKey the message key (static field) that holds the message String
	 * @return the message String
	 */
	public String getMessage(final String messageKey) {
		try {
			final Field field = ConditionBuilderMessages.class.getField(messageKey);
			return (String) field.get(this);
		} catch (final Exception e) {
			throw new MessageException(e);
		}
	}

	/**
	 * Gets the NLS localize message class.
	 * @return the localized message class.
	 */
	public static ConditionBuilderMessages get() {
		return LocalizedMessagePostProcessor.getUTF8Encoded(BUNDLE_NAME, ConditionBuilderMessages.class);
	}


}
