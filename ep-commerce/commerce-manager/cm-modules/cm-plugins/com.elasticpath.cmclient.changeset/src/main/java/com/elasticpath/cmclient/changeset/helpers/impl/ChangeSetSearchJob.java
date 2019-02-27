/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.changeset.helpers.impl;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Display;

import com.elasticpath.cmclient.changeset.ChangeSetMessages;
import com.elasticpath.cmclient.changeset.ChangeSetPermissions;
import com.elasticpath.cmclient.core.LoginManager;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.helpers.SearchItemsLocator;
import com.elasticpath.cmclient.core.search.impl.AbstractSearchJobImpl;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.changeset.ChangeSet;
import com.elasticpath.service.changeset.ChangeSetLoadTuner;
import com.elasticpath.service.changeset.ChangeSetManagementService;
import com.elasticpath.service.changeset.ChangeSetSearchCriteria;

/**
 * ChangeSetSearchJob provides a way to carry out a change set search using
 * criteria in an asynchronous manner.	 
 */
public class ChangeSetSearchJob extends AbstractSearchJobImpl {

	private static final int COUNT_UNITS_WORK = 1;
	private static final int THREE_UNITS_OF_WORK = 3;
	private final Display display;
	private final String taskName;

	/**
	 * ChangeSetSearchJob constructor.
	 * 
	 * @param locator the locator for items
	 * @param display the display
	 */
	public ChangeSetSearchJob(final SearchItemsLocator<ChangeSet> locator, final Display display) {
		super(locator);
		this.display = display;
		taskName = ChangeSetMessages.get().ChangeSetSearchTab_SearchMessage;
	}
	
	@Override
	protected IStatus run(final IProgressMonitor monitor) {

		monitor.beginTask(
				taskName,
				COUNT_UNITS_WORK + THREE_UNITS_OF_WORK);

		try {
			ChangeSetManagementService changeSetManagementService = ServiceLocator.getService(
				ContextIdNames.CHANGESET_MANAGEMENT_SERVICE);

			ChangeSetLoadTuner noMembersLoadTuner = ServiceLocator.getService(ContextIdNames.CHANGESET_LOAD_TUNER);
			noMembersLoadTuner.setLoadingMemberObjects(false);
			noMembersLoadTuner.setLoadingMemberObjectsMetadata(false);

			final int startIndex = getStartIndexQueue().poll();

			ChangeSetSearchCriteria searchCriteria;
			synchronized (getSearchCriteriaQueue()) {
				searchCriteria = (ChangeSetSearchCriteria) getCriteria();
			}

			display.syncExec(() -> {
				// If user can ONLY work with change sets then filter
				if (AuthorizationService.getInstance().isAuthorizedWithPermission(ChangeSetPermissions.WORK_WITH_CHANGE_SETS_PERMISSION)
					&& !AuthorizationService.getInstance().isAuthorizedWithPermission(ChangeSetPermissions.CHANGE_SET_PERMISSIONS_MANAGE)) {

					String cmUserGuid = LoginManager.getCmUserGuid();
					searchCriteria.setUserGuid(cmUserGuid);
				}
		});

			final long totalItemsCount = changeSetManagementService.getCountByCriteria(searchCriteria);
			monitor.worked(COUNT_UNITS_WORK);

			final List changeSets = changeSetManagementService.findByCriteria(searchCriteria, noMembersLoadTuner, startIndex, getPagination());

			display.asyncExec(() -> getLocator().fireItemsUpdated(changeSets, startIndex, (int) totalItemsCount));

			monitor.worked(THREE_UNITS_OF_WORK);

		} finally {
			monitor.done();
		}

		return Status.OK_STATUS;
	}	

}
