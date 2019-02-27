/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.helpers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elasticpath.cmclient.core.LoginManager;
import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.cmclient.core.formatting.MetadataDateFormat;
import com.elasticpath.cmclient.core.registry.ObjectRegistry;
import com.elasticpath.cmclient.core.service.ChangeSetEventService;
import com.elasticpath.common.dto.ChangeSetObjects;
import com.elasticpath.common.dto.Dto;
import com.elasticpath.domain.changeset.ChangeSet;
import com.elasticpath.domain.changeset.ChangeSetObjectStatus;
import com.elasticpath.service.changeset.ChangeSetMemberAction;
import com.elasticpath.service.changeset.ChangeSetService;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.settings.SettingsReader;
import com.elasticpath.settings.domain.SettingValue;

/**
 * Class to assist in Change Set related functionality.
 */
public class ChangeSetHelper {	

	/**
	 * Object Registry key for the active Change Set.
	 */
	public static final String OBJECT_REG_ACTIVE_CHANGE_SET = "activeChangeSet"; //$NON-NLS-1$
	/** Spring bean id. */
	public static final String BEAN_ID = "cmChangeSetHelper";

	private static final String SETTING_CHANGESET_ENABLED = "COMMERCE/SYSTEM/CHANGESETS/enable"; //$NON-NLS-1$
	
	private ChangeSetService changeSetService;
	private TimeService timeService;
	private SettingsReader settingsReader;

	/**
	 * Checks if change sets are enabled.
	 *
	 * @return True, if change sets are enabled.
	 */
	public boolean isChangeSetsEnabled() {
		SettingValue settingValue = settingsReader.getSettingValue(SETTING_CHANGESET_ENABLED);

		return settingValue != null && settingValue.getBooleanValue();
	}

	/**
	 * Add an object to the currently selected Change Set.
	 *
	 * @param object The object to add to the active Change Set.
	 * @param action the action performed on the object to warrant addition to a change set, edit or delete, for example
	 */
	public void addObjectToChangeSet(final Object object, final ChangeSetMemberAction action) {
		if (!isChangeSetsEnabled()) {
			return;
		}
		
		ChangeSet activeChangeSet = getActiveChangeSet();
		if (activeChangeSet == null) {
			return;
		}
		
		final ChangeSetObjectStatus objectStatus = changeSetService.getStatus(object);
		
		// if the object already exists within the change set, likely from an initial edit action
		// remove the entry and recreate it with the new ChangeSetMemberAction and meta-data
		if (objectStatus.isMember(activeChangeSet.getGuid())) {
			changeSetService.removeObjectFromChangeSet(activeChangeSet.getGuid(), object);
		}
		
		changeSetService.addObjectToChangeSet(activeChangeSet.getGuid(), object, buildChangeSetMetadataMap(action));
		
		fireChangeSetEvent(object, action);
	}

	private void fireChangeSetEvent(final Object object, final ChangeSetMemberAction action) {
		ItemChangeEvent.EventType event;
		if (ChangeSetMemberAction.ADD.equals(action)) {
			event = ItemChangeEvent.EventType.ADD;
		} else if (ChangeSetMemberAction.EDIT.equals(action)) {
			event = ItemChangeEvent.EventType.CHANGE;
		} else {
			event = ItemChangeEvent.EventType.REMOVE;
		}

		ItemChangeEvent<Object> itemChangeEvent = new ItemChangeEvent<Object>(this, object, event);
		ChangeSetEventService.getInstance().notifyChangeSetEvent(itemChangeEvent);
	}

	/**
	 * Add objects from dtoChangeSet to the current change set.
	 *
	 * @param dtoChangeSet the dto change set
	 */
	public void addObjectsToChangeSet(final ChangeSetObjects< ? extends Dto > dtoChangeSet) {
		addObjectsToChangeSet(dtoChangeSet.getRemovalList(), ChangeSetMemberAction.DELETE);
		addObjectsToChangeSet(dtoChangeSet.getAdditionList(), ChangeSetMemberAction.ADD);
		addObjectsToChangeSet(dtoChangeSet.getUpdateList(), ChangeSetMemberAction.EDIT);
	}

	private void addObjectsToChangeSet(final List< ? extends Dto> list, final ChangeSetMemberAction action) {
		for (Dto object : list) {
			addObjectToChangeSet(object, action);
		}
	}


	/**
	 * Remove an object from the currently selected Change Set.
	 *
	 * @param object The object to remove from the active Change Set.
	 */
	public void removeObjectFromChangeSet(final Object object) {
		if (!isChangeSetsEnabled()) {
			return;
		}
		
		ChangeSet activeChangeSet = getActiveChangeSet();
		if (activeChangeSet == null) {
			return;
		}
		
		changeSetService.removeObjectFromChangeSet(activeChangeSet.getGuid(), object);
	}
		
	/**
	 * Check if change set is enabled and one change set is selected.
	 * 
	 * @return true if change set is not enabled
	 *         true if change set is enabled and one change set is selected
	 *         otherwise it will return false
	 */
	public boolean isEnabledByCheckingChangeSet() {
		return !isChangeSetsEnabled() || isActiveChangeSet();
	}

	/**
	 * Builds the meta-data map for the Rule object being added to the active Change Set.
	 * 
	 * @param action the action performed on the object to warrant addition to a change set, edit or delete, for example 
	 * @return a map of key, value pairs
	 */
	protected Map<String, String> buildChangeSetMetadataMap(final ChangeSetMemberAction action) {
		Map<String, String> metadata = new HashMap<String, String>();
		metadata.put("addedByUserGuid", getCmUserGuid()); //$NON-NLS-1$
		metadata.put("dateAdded", getCurrentDateString()); //$NON-NLS-1$
		metadata.put("action", action.getName()); //$NON-NLS-1$
		
		return metadata;
	}
	
	/**
	 * Get the GUID of the current CM User.
	 * 
	 * @return the GUID of the current CM User
	 */
	protected String getCmUserGuid() {
		return LoginManager.getCmUserGuid();
	}

	/**
	 * Get the string representing the current date. Use a locale-agnostic format
	 * 
	 * @return the current date as a string
	 */
	protected String getCurrentDateString() {
		return String.valueOf(new MetadataDateFormat().format(timeService.getCurrentTime().getTime()));
	}

	/**
	 * Gets the currently selected Change Set.
	 * 
	 * @return The active Change Set selected.
	 */
	public ChangeSet getActiveChangeSet() {
		return (ChangeSet) ObjectRegistry.getInstance().getObject(OBJECT_REG_ACTIVE_CHANGE_SET);
	}
	
	/**
	 * Retrieves the current status of an object.
	 * 
	 * @param object the domain object to determine the change set status for
	 * @return the object status
	 */
	public ChangeSetObjectStatus getChangeSetObjectStatus(final Object object) {
		return changeSetService.getStatus(object);
	}
	
	/**
	 * Retrieves map of locked objects with change sets they are assigned to.  
	 *
	 * @param objects - array of objects which statuses will be retrieved.
	 * @return map of objects and change set guids.
	 */
	public Map<Object, String> getObjectsLocked(final Object[] objects) {
		return changeSetService.getObjectsLocked(objects);
	}
	/**
	 * Checks if any Change Set is selected.
	 * 
	 * @return True, if any Change Set is selected.
	 */
	public boolean isActiveChangeSet() {
		return getActiveChangeSet() != null;
	}	
	
	/**
	 * Checks if the supplied object is a member of the change set identified by the supplied guid.
	 * 
	 * @param object object to check membership for
	 * @param changeSetGuid the guid of the change set to check
	 * @return true if the object is in the change set, false otherwise
	 */
	public boolean isMember(final Object object, final String changeSetGuid) {
		return changeSetService.getStatus(object).isMember(changeSetGuid);
	}
	
	/**
	 * Checks that there is an active change set and that the supplied object is a member of the
	 * active ChangeSet.
	 * @param object to check for membership
	 * @return true if their is an active ChangeSet and the supplied object is a member of that change set 
	 */
	public boolean isMemberOfActiveChangeset(final Object object) {
		return isActiveChangeSet() && isMember(object, getActiveChangeSet().getGuid());
	}
	
	/**
	 * Checks that change sets are enabled and there is an active change set and that the supplied object
	 *  is a member of the active ChangeSet.
	 * @param object to check for membership
	 * @return true if their is an active ChangeSet and the supplied object is a member of that change set 
	 */
	public boolean isDisabledOrMemberOfActiveChangeset(final Object object) {
		return !isChangeSetsEnabled() || isMemberOfActiveChangeset(object);
	}

	/**
	 * Resolves the GUID of the given object.
	 * 
	 * @param object the object
	 * @return the object GUID or null in case it could not be resolved
	 */
	public String resolveObjectGuid(final Object object) {
		return changeSetService.resolveObjectGuid(object);
	}

	/**
	 * Get the changeset for the given object.
	 *
	 * @param object the change set object
	 * @return the changeset object or null
	 */
	public ChangeSet getChangeSet(final Object object) {
		return changeSetService.findChangeSet(object);
	}

	public void setChangeSetService(final ChangeSetService changeSetService) {
		this.changeSetService = changeSetService;
	}

	public void setTimeService(final TimeService timeService) {
		this.timeService = timeService;
	}

	public void setSettingsReader(final SettingsReader settingsReader) {
		this.settingsReader = settingsReader;
	}
}
