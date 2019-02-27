/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.changeset.actions;

import org.apache.commons.lang.ObjectUtils;
import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;

import com.elasticpath.cmclient.changeset.ChangeSetMessages;
import com.elasticpath.cmclient.changeset.editors.ChangeSetEditor;
import com.elasticpath.cmclient.changeset.event.ChangeSetEventService;
import com.elasticpath.cmclient.changeset.views.ChangeSetsView;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.editors.GuidEditorInput;
import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.cmclient.core.event.ItemChangeEvent.EventType;
import com.elasticpath.cmclient.core.helpers.ChangeSetHelper;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.changeset.ChangeSet;
import com.elasticpath.service.changeset.ChangeSetManagementService;

/**
 * The action responsible for triggering change set deletion.
 */
public class DeleteChangeSetAction extends Action {

	private static final Logger LOG = Logger.getLogger(DeleteChangeSetAction.class);
	
	private final ChangeSetsView changeSetsView;
	private final IWorkbenchPartSite workbenchPartSite;
	private final ChangeSetHelper changeSetHelper = ServiceLocator.getService(ChangeSetHelper.BEAN_ID);
	/**
	 *
	 * @param changeSetsView the change set view
	 * @param workbenchPartSite the workbench part site
	 * @param name the action's name
	 * @param imageDesc the action's image
	 */
	public DeleteChangeSetAction(final ChangeSetsView changeSetsView, 
			final IWorkbenchPartSite workbenchPartSite, final String name, final ImageDescriptor imageDesc) {
		super(name, imageDesc);
		this.changeSetsView = changeSetsView;
		this.workbenchPartSite = workbenchPartSite;
	}

	@Override
	public void run() {
		final ISelection selection = this.changeSetsView.getViewer().getSelection();
		final ChangeSet changeSet = (ChangeSet) ((IStructuredSelection) selection).getFirstElement();
		
		if (!verifyChangeSetState(changeSet)) {
			return;
		}
		
		if (!verifyEditorNotOpen(changeSet)) {
			return;
		}
		
		if (verifyChangeSetIsActive(changeSet)) {
			return;
		}

		boolean result = MessageDialog.openConfirm(null,
				ChangeSetMessages.get().DeleteChangeSetAction_ConfirmTitle,

				NLS.bind(ChangeSetMessages.get().DeleteChangeSetAction_ConfirmMessage,
				changeSet.getName()));
		if (result) {
			ChangeSetManagementService changeSetManagementService = getChangeSetManagementService();
			changeSetManagementService.remove(changeSet.getGuid());
			// fire an event for the others to update
			ChangeSetEventService.getInstance().fireChangeSetModificationEvent(
					new ItemChangeEvent<ChangeSet>(this, changeSet, EventType.REMOVE));
		}
	}

	private ChangeSetManagementService getChangeSetManagementService() {
		return ServiceLocator.getService(ContextIdNames.CHANGESET_MANAGEMENT_SERVICE);
	}

	private boolean verifyChangeSetIsActive(final ChangeSet changeSet) {
		ChangeSet activeChangeSet = changeSetHelper.getActiveChangeSet();
		if (changeSet.equals(activeChangeSet)) {
			MessageDialog.openWarning(null, 
					ChangeSetMessages.get().DeleteChangeSetAction_WarningTitle,
					ChangeSetMessages.get().DeleteChangeSetAction_WarningText2);
			return true;
		}
		return false;
	}

	/**
	 *
	 */
	private boolean verifyChangeSetState(final ChangeSet changeSet) {
		final ChangeSetManagementService changeSetService = getChangeSetManagementService();
		if (!changeSetService.canRemove(changeSet.getGuid())) {
			MessageDialog.openWarning(null, 
					ChangeSetMessages.get().DeleteChangeSetAction_WarningTitle,
					ChangeSetMessages.get().DeleteChangeSetAction_WarningText1);
			return false;
		}
		return true;
	}

	/**
	 *
	 * @param changeSet the change set to verify
	 */
	private boolean verifyEditorNotOpen(final ChangeSet changeSet) {
		IEditorReference[] editorReferences = workbenchPartSite.getPage().getEditorReferences();
		for (IEditorReference editorRef : editorReferences) {
			if (checkEditor(changeSet, editorRef)) {
				return false;
			}
		}
		return true;
	}

	/**
	 *
	 * @param changeSet change set
	 * @param editorRef editor reference
	 */
	private boolean checkEditor(final ChangeSet changeSet, final IEditorReference editorRef) {
		try {
			if (isSameEditor(editorRef, ChangeSetEditor.ID_EDITOR)) {
				GuidEditorInput input = (GuidEditorInput) editorRef.getEditorInput();
				if (ObjectUtils.equals(input.getGuid(), changeSet.getGuid())) {
					showWarningDialog();
					return true;
				}
			}
		} catch (PartInitException exc) {
			LOG.warn(exc.getMessage(), exc);
		}
		return false;
	}

	/**
	 * Checks whether an editor reference holds an editor with the given editor ID.
	 * 
	 * @param editorRef the editor reference
	 * @param editorId the editor ID
	 * @return true if editor reference holds the same editor
	 */
	public static boolean isSameEditor(final IEditorReference editorRef, final String editorId) {
		final String refEditorId = editorRef.getEditor(false).getEditorSite().getId();
		return ObjectUtils.equals(refEditorId, editorId);
	}

	/**
	 *
	 */
	private void showWarningDialog() {
		MessageDialog.openWarning(null, 
				ChangeSetMessages.get().DeleteChangeSetAction_WarningTitle,
				ChangeSetMessages.get().DeleteChangeSetAction_WarningText);
	}

}
