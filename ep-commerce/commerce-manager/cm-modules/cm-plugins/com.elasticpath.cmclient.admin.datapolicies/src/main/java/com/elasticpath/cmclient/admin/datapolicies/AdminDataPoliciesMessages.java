/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.cmclient.admin.datapolicies;

import com.elasticpath.cmclient.core.nls.LocalizedMessagePostProcessor;

/**
 * Messages class for data policies.
 */
@SuppressWarnings({"PMD.TooManyFields", "PMD.VariableNamingConventions", "PMD.LongVariable"})
public final class AdminDataPoliciesMessages {

	private static final String BUNDLE_NAME = "com.elasticpath.cmclient.admin.datapolicies.AdminDataPoliciesPluginResources"; //$NON-NLS-1$
	public String DataPoliciesAdminSection_DataPolicies;
	public String CreateDataPolicy;
	public String EditDataPolicy;
	public String ViewDataPolicy;
	public String DisableDataPolicy;
	public String DataPolicyEditor_DisableDialogTitle;
	public String DataPolicyEditor_DisableConfirmation;
	public String DataPolicyEditor_Tooltip;
	public String DataPolicyEditor_NewSegmentName;
	public String DataPolicyEditor_SummaryPage_Title;
	public String DataPolicyEditor_SummaryPage_Details;
	public String DataPolicyEditor_SummaryPage_RetentionType_FromCreationDate;
	public String DataPolicyEditor_SummaryPage_RetentionType_FromLastModifiedDate;
	public String DataPolicyEditor_SummaryPage_State_Draft;
	public String DataPolicyEditor_SummaryPage_State_Active;
	public String DataPolicyEditor_SummaryPage_State_Disabled;
	public String DataPolicyEditor_SummaryPage_OnSavePrompt;
	public String DataPolicyEditor_SummaryPage_NameField;
	public String DataPolicyEditor_SummaryPage_ReferenceKeyField;
	public String DataPolicyEditor_SummaryPage_RetentionType;
	public String DataPolicyEditor_SummaryPage_RetentionPeriod;
	public String DataPolicyEditor_SummaryPage_DataPolicyState;
	public String DataPolicyEditor_SummaryPage_StartDate;
	public String DataPolicyEditor_SummaryPage_EndDate;
	public String DataPolicyEditor_SummaryPage_Description;
	public String DataPolicyEditor_SummaryPage_Activities;
	public String DataPolicyEditor_DataPoints_Title;
	public String DataPolicyEditor_DataPoints_Section;
	public String DataPolicyEditor_DataPoints_CreateSection;
	public String DataPolicyEditor_DataPoints_Available_Title;
	public String DataPolicyEditor_DataPoints_Assigned_Title;
	public String DataPolicyEditor_DataPoints_StartDateBeforeEndDate;
	public String DataPolicyEditor_SegmentsPage_Title;
	public String DataPolicyEditor_SegmentsPage_SegmentsSection;
	public String DataPolicyEditor_SegmentsPage_TableValueColumn;
	public String DataPolicyEditor_SegmentsPage_AddSegmentButton;
	public String DataPolicyEditor_SegmentsPage_RemoveSegmentButton;
	public String DataPolicyEditor_SegmentsPage_Dialog_Title;
	public String DataPolicyEditor_SegmentsPage_Dialog_SegmentFieldText;
	public String DataPolicyListView_Column_DataPolicyName;
	public String DataPolicyListView_Column_DataPolicyState;
	public String DataPolicyListView_Column_DataPolicyStartDate;
	public String DataPolicyListView_Column_DataPolicyEndDate;
	public String DataPolicyEditor_SegmentsPage_SegmentsRequiredTitle;
	public String DataPolicyEditor_SegmentsPage_SegmentsRequiredDetailedMessage;
	public String DataPolicyEditor_DataPoints_KeyRequiredTitle;
	public String DataPolicyEditor_DataPoints_KeyRequiredMessage;
	public String DataPolicyEditor_DataPoints_Button_CreateDataPoint;
	public String DataPolicyEditor_DataPoints_Button_CreateDataPoint_Tooltip;
	public String DataPolicyEditor_DataPoints_Dialog_Label_Name;
	public String DataPolicyEditor_DataPoints_Dialog_Label_DataLocation;
	public String DataPolicyEditor_DataPoints_Dialog_Label_DataKey;
	public String DataPolicyEditor_DataPoints_Dialog_Label_DescriptionKey;
	public String DataPolicyEditor_DataPoints_Dialog_Label_Removable;
	public String DataPolicyEditor_DataPoints_Dialog_CreateTitle;
	public String DataPolicyEditor_DataPoints_Dialog_ViewTitle;
	public String DataPolicyEditor_DataPoints_DuplicateNameTitle;
	public String DataPolicyEditor_DataPoints_DuplicateNameMessage;
	public String DataPolicyEditor_DataPoints_KeyIsNotValidTitle;
	public String DataPolicyEditor_DataPoints_KeyIsNotValidMessage;
	public String DataPolicyEditor_DataPoints_KeyAlreadyInUseTitle;
	public String DataPolicyEditor_DataPoints_KeyAlreadyInUseMessage;

	private AdminDataPoliciesMessages() {
	}

	/**
	 * Gets the NLS localize message class.
	 *
	 * @return the localized message class.
	 */
	public static AdminDataPoliciesMessages get() {
		return LocalizedMessagePostProcessor.getUTF8Encoded(BUNDLE_NAME, AdminDataPoliciesMessages.class);
	}

}
