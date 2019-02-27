/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.cmclient.changeset.wizards;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.cmclient.changeset.ChangeSetImageRegistry;
import com.elasticpath.cmclient.changeset.ChangeSetMessages;
import com.elasticpath.cmclient.changeset.event.ChangeSetEventService;
import com.elasticpath.cmclient.core.LoginManager;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.cmclient.core.event.ItemChangeEvent.EventType;
import com.elasticpath.cmclient.core.wizard.AbstractEpWizard;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.changeset.ChangeSet;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.service.changeset.ChangeSetManagementService;

/**
 * A wizard for creating a new change set.
 */
public class CreateChangeSetWizard extends AbstractEpWizard<ChangeSet> {

	private ChangeSet changeSet;

	/**
	 * Constructor.
	 */
	public CreateChangeSetWizard() {
		super(ChangeSetMessages.get().CreateChangeSetWizard_Title,
				StringUtils.EMPTY, 
				ChangeSetImageRegistry.getImage(ChangeSetImageRegistry.CHANGESET));
	}
	
	@Override
	public void addPages() {
		addPage(new CreateChangeSetWizardSummaryPage("SummaryPage")); //$NON-NLS-1$
	}


	@Override
	public boolean performFinish() {
		addChangeSet(getModel());
		return true;
	}

	/**
	 *
	 */
	private void addChangeSet(final ChangeSet changeSet) {
		ChangeSetManagementService changeSetManagementService = ServiceLocator.getService(ContextIdNames.CHANGESET_MANAGEMENT_SERVICE);
		// use the current cm user as the creator of the change set
		CmUser createdByCmUser = LoginManager.getCmUser();
		changeSet.setCreatedByUserGuid(createdByCmUser.getGuid());
		
		// create a new change set
		ChangeSet updatedChangeSet = changeSetManagementService.add(changeSet);

		// fire an event for the others to update
		ChangeSetEventService.getInstance().fireChangeSetModificationEvent(
				new ItemChangeEvent<ChangeSet>(this, updatedChangeSet, EventType.ADD));
	}

	@Override
	protected ChangeSet getModel() {
		if (changeSet == null) {
			changeSet = ServiceLocator.getService(ContextIdNames.CHANGE_SET);
		}
		return changeSet;
	}

}
