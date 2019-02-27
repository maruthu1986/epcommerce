/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.changeset;

import java.lang.reflect.Field;

import org.apache.commons.lang.StringUtils;


import com.elasticpath.cmclient.core.nls.LocalizedMessagePostProcessor;
import com.elasticpath.domain.changeset.ChangeSetStateCode;

/**
 * Messages class for the com.elasticpath.cmclient.changeset plugin.
 */
@SuppressWarnings({"PMD.TooManyFields", "PMD.VariableNamingConventions", "PMD.ExcessivePublicCount"})
public final class ChangeSetMessages {
	private static final String BUNDLE_NAME = "com.elasticpath.cmclient.changeset.ChangeSetPluginResources"; //$NON-NLS-1$

	public static final String EMPTY_STRING = ""; //$NON-NLS-1$

	public static final String SPACE = " "; //$NON-NLS-1$

	public static final String COMMA = ","; //$NON-NLS-1$

	private ChangeSetMessages() {
	}

	public String AddToChangeSetAction_NA;
	public String AddToChangeSetAction_BaseAmountToolTip;
	public String AddToChangeSetAction_objectName;
	public String AddToChangeSetAction_objectName_default;

	public String ChangeSetSearchTab_SearchMessage;

	// Create Change Set Wizard
	public String CreateChangeSetWizardSummaryPage_Desc;

	public String CreateChangeSetWizardSummaryPage_Title;

	public String CreateChangeSetWizard_Title;

	public String ChangeSetState_ComboLabel;

	public String ChangeSetSearchView_FiltersGroup;

	// Search View
	public String ChangeSetSearchView_ChangeSetTab;
	public String ChangeSetSearchView_SearchTermsGroup;
	public String ChangeSetSearchView_ChangeSet_User;

	public String ChangeSetSearchView_SearchButton;
	public String ChangeSetSearchView_ClearButton;

	public String ChangeSetSearchView_ShowAll;

	public String ChangeSetEditor_ObjectsTable_SelectedColumn;
	public String ChangeSetsView_NameColumn;


	public String ChangeSetsView_DescriptionColumn;
	public String ChangeSetsView_CreatedDateColumn;
	public String ChangeSetsView_CreatorColumn;

	// Change Set Editor
	public String ChangeSetEditor_Summary_Page_Title;

	public String ChangeSetEditor_Summary_Form_Title;

	public String ChangeSetEditor_ChangeSet_Guid;

	public String ChangeSetEditor_ChangeSet_Name;

	public String ChangeSetEditor_ChangeSet_Description;

	public String ChangeSetEditor_ObjectsPageTitle;

	public String ChangeSetEditor_ConflictsPageTitle;

	public String ChangeSetEditor_Objects_MoveObjects;

	public String ChangeSetEditor_Objects_OpenObjectEditor;

	public String ChangeSetEditor_ObjectsTable_IdColumn;

	public String ChangeSetEditor_ObjectsTable_TypeColumn;

	public String ChangeSetEditor_UsersFormTitle;

	public String ChangeSetEditor_UsersPageTitle;

	public String ChangeSetEditor_NewChangeSet;

	public String ChangeSetEditor_AvailableUsers;

	public String ChangeSetEditor_AssignedUsers;

	public String ChangeSetEditor_SelectUsersMessage;

	public String ChangeSetEditor_CreatedBy;

	public String ChangeSetEditor_DateCreated;

	public String ChangeSetEditor_ObjectsTable_Action;


	// Move Objects Dialog
	public String ChangeSetDialog_MoveObjectsToAnotherChangeset_Title;

	public String ChangeSetDialog_PleaseSelectObjectsToMove_Message;

	public String ChangeSetDialog_MoveObjects_WindowTitle;

	public String ChangeSetDialog_ChangeSet_NameColumn;

	public String ChangeSetDialog_ChangeSet_CreatedByColumn;

	public String ChangeSetDialog_ChangeSet_DateCreatedColumn;

	public String ChangeSetDialog_NotEnoughChangeSets;

	// Search Results View
	public String ChangeSetsView_EditChangeSetTooltip;

	public String ChangeSetsView_CreateChangeSetTooltip;

	public String ChangeSetsView_CreateChangeSet;

	public String ChangeSetsView_DeleteChangeSet;

	public String ChangeSetsView_DeleteChangeSetTooltip;

	public String ChangeSetsView_EditChangeSet;

	public String DeleteChangeSetAction_ConfirmTitle;

	public String DeleteChangeSetAction_ConfirmMessage;

	public String DeleteChangeSetAction_WarningText;

	public String DeleteChangeSetAction_WarningTitle;

	public String DeleteChangeSetAction_WarningText1;

	public String DeleteChangeSetAction_WarningText2;

	public String ChangeSetSwitchAction_SaveChangetSetDialogTitle;

	public String ChangeSetSwitchAction_SaveChangetSetDialogText;

	public String ChangeSetsView_ChangeSetOwner;

	public String ChangeSetsView_State;

	public String ChangeSetDialog_NoObjectsAssigned;

	// change set editor support page
	public String NotAvailable;

	public String ChangeSetInfoPage_NoChangeSetMessage;

	public String ChangeSetInfoPage_ChangeSet;

	public String ChangeSetInfoPage_AddedBy;

	public String ChangeSetInfoPage_DateAdded;

	public String ChangeSetInfoPage_FormTitle;

	public String ChangeSetInfoPage_PageTitle;

	public String ChangeSetEditor_ObjectsFormTitle;

	public String ChangeSetEditor_ObjectsTable_NameColumn;

	public String ChangeSetEditor_ObjectsTable_DateAddedColumn;

	public String ChangeSetEditor_ObjectsTable_AddedByColumn;
	
	public String ChangeSetEditor_ConflictsTable_SourceName;

	public String ChangeSetEditor_ConflictsTable_SourceType;

	public String ChangeSetEditor_ConflictsTable_DependencyName;
	
	public String ChangeSetEditor_ConflictsTable_DependencyType;

	public String ChangeSetEditor_ConflictsTable_DependencyChangeSet;

	public String ChangeSetEditor_ChangeSet_State;

	public String ChangeSetsView_ChangeSetOwnerNone;

	public String ChangeSetInfoPage_ChangeSet_State;

	public String FINALIZED; //NOPMD

	public String OPEN; //NOPMD

	public String LOCKED; //NOPMD
	
	public String READY_TO_PUBLISH; //NOPMD 

	public String ALL_STATES; //NOPMD

	public String ComponentHelper_ObjectDeletedMessage;

	public String ComponentHelper_ObjectDeletedTitle;

	public String FinalizeChangeSetAction_ConfirmTitle;
	
	public String FinalizeChangeSetAction_ConfirmMessage;
	
	public String PublishChangeSetAction_ConfirmTitle;
	  
	public String PublishChangeSetAction_ConfirmMessage;

	public String NoMatchingChangeSets;

	public String ObjectLockedInChangeSet;

	public String AddObjectToChangeset;

	public String ObjectLockedInCurrentChangeset;

	/**
	 * Gets the state label.
	 *
	 * @param stateCode the state code
	 * @return value of the state code
	 */
	public String getMessage(final ChangeSetStateCode stateCode) {
		if (stateCode == null) {
			return StringUtils.EMPTY;
		}
		Class<ChangeSetMessages> clazz = ChangeSetMessages.class;
		try {
			Field field = clazz.getField(stateCode.getName());

			String value = (String) field.get(this);
			if (value != null) {
				return value;
			}
		} catch (Exception e) { //NOPMD
			// skip that exception
		}
		return stateCode.getName();
	}

	/**
	 * Gets the NLS localize message class.
	 * @return the localized message class.
	 */
	public static ChangeSetMessages get() {
		return LocalizedMessagePostProcessor.getUTF8Encoded(BUNDLE_NAME, ChangeSetMessages.class);
	}

}
