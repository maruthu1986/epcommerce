/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.cmclient.changeset.actions;

import java.util.Iterator;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionDelegate;

import com.elasticpath.cmclient.changeset.event.ChangeSetEventListener;
import com.elasticpath.cmclient.changeset.event.ChangeSetEventService;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.changeset.ChangeSet;
import com.elasticpath.domain.changeset.ChangeSetStateCode;
import com.elasticpath.service.changeset.ChangeSetLoadTuner;
import com.elasticpath.service.changeset.ChangeSetManagementService;

/**
 * An abstract implementation of an action delegate that is targeted towards
 * action delegates changing the state of a change set from A to B.
 */
public abstract class AbstractChangeStateActionDelegate extends ActionDelegate 
	implements IViewActionDelegate, ISelectionChangedListener, ChangeSetEventListener {
	
	private IViewPart view;
	private IAction action;
	
	private ChangeSetActionUtil changeSetActionUtil;

	/**
	 *
	 * @param view the view part
	 */
	@Override
	public void init(final IViewPart view) {
		this.view = view;
		this.view.getViewSite().getSelectionProvider().addSelectionChangedListener(this);
		ChangeSetEventService.getInstance().registerChangeSetEventListener(this);
		changeSetActionUtil = new ChangeSetActionUtil();
	}

	@Override
	public void init(final IAction action) {
		this.action = action;
		this.action.setEnabled(false);
	}

	@Override
	public void run(final IAction action) {
		final IStructuredSelection selection = (IStructuredSelection) view.getViewSite().getSelectionProvider().getSelection();
		final ChangeSetManagementService changeSetService = ServiceLocator.getService(ContextIdNames.CHANGESET_MANAGEMENT_SERVICE);
		Iterator<ChangeSet> iterator = selection.iterator();
		while (iterator.hasNext()) {
			ChangeSet changeSet = iterator.next();

			if (checkEditors(changeSet)) {
				ChangeSetLoadTuner noMembersLoadTuner = ServiceLocator.getService(ContextIdNames.CHANGESET_LOAD_TUNER);
				noMembersLoadTuner.setLoadingMemberObjects(false);
				noMembersLoadTuner.setLoadingMemberObjectsMetadata(false);
				
				ChangeSet updatedChangeSet = changeSetService.updateState(changeSet.getGuid(), getNewState(), noMembersLoadTuner);
				// fire a change event
				ChangeSetEventService.getInstance().fireChangeSetModificationEvent(new ItemChangeEvent<ChangeSet>(this, updatedChangeSet));
				
				refreshChangeSetInfoPage(changeSet);
			}
		}
	}

	
	/**
	 * If the action need check all dirty editors and prompt user to save them, this method should be override.
	 * @param changeSet the change set.
	 * @return boolean result of checking editors.
	 */
	protected boolean checkEditors(final ChangeSet changeSet) {
		return true;
	}
	
	/**
	 * Refreshes the editor's pages.
	 * 
	 * @param changeSet the change set which state is changed
	 */
	protected void refreshChangeSetInfoPage(final ChangeSet changeSet) {
		IWorkbenchWindow workbenchWindow = view.getSite().getWorkbenchWindow();
		final IWorkbenchPage activePage = workbenchWindow.getActivePage();
		final IEditorReference[] openEditorReferences = activePage.getEditorReferences();

		// check for open editors and only try to refresh the editor which model belongs to the change set
		for (IEditorReference editorReference : openEditorReferences) {
			final IEditorPart editorPart = editorReference.getEditor(false);
			final AbstractCmClientFormEditor cmClientFormEditor = (AbstractCmClientFormEditor) editorPart;
			cmClientFormEditor.refreshEditorPages();
		}
	}

	/**
	 * Gets the desired state that has to be set to a change set.
	 * 
	 * @return the new state of a change set
	 */
	protected abstract ChangeSetStateCode getNewState();
	
	/**
	 *
	 * @param event the event to use
	 */
	@Override
	public void selectionChanged(final SelectionChangedEvent event) {
		updateActionState(event.getSelection());
	}

	/**
	 *
	 * @param selection the selection
	 */
	private void updateActionState(final ISelection selection) {
		IStructuredSelection structuredSelection = (IStructuredSelection) selection;
		Iterator<ChangeSet> iterator = structuredSelection.iterator();
		boolean actionEnabled = false;
		while (iterator.hasNext()) {
			ChangeSet changeSet = iterator.next();
			actionEnabled |= (isStateEnabler(changeSet.getStateCode()) && isAuthorized());
		}
		
		this.action.setEnabled(actionEnabled);
	}

	/**
	 * Should be implemented by clients and should return whether 
	 * this action can be executed by the current user.
	 * 
	 * @return true if authorisation is granted
	 */
	protected abstract boolean isAuthorized();
		
	/**
	 * Checks whether the current user has that permission.
	 *  
	 * @param permissionId the permission ID
	 * @return true if the user is authorised
	 */
	protected boolean isAuthorized(final String permissionId) {
		return AuthorizationService.getInstance().isAuthorizedWithPermission(permissionId);
	}

	/**
	 * Checks whether a state is an enabler for this action.
	 * 
	 * @param stateCode the state code to check
	 * @return true if the action should be enabled
	 */
	protected abstract boolean isStateEnabler(ChangeSetStateCode stateCode);

	/**
	 *
	 * @param event the event
	 */
	@Override
	public void changeSetModified(final ItemChangeEvent<ChangeSet> event) {
		updateActionState(this.view.getViewSite().getSelectionProvider().getSelection());
	}
	
	/**
	 * Get the change set action util.
	 * @return the instance of change set action util.
	 */
	public ChangeSetActionUtil getChangeSetActionUtil() {
		return changeSetActionUtil;
	}

	/**
	 * Set the change set action util.
	 *
	 * @param changeSetActionUtil the instance of change set action util
	 */
	public void setChangeSetActionUtil(final ChangeSetActionUtil changeSetActionUtil) {
		this.changeSetActionUtil = changeSetActionUtil;
	}

	/**
	 * Get the view part. 
	 * @return the view part.
	 */
	public IViewPart getView() {
		return view;
	}

}
