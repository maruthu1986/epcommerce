/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.cmclient.changeset.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osgi.util.NLS;

import com.elasticpath.cmclient.changeset.ChangeSetMessages;
import com.elasticpath.cmclient.changeset.ChangeSetPermissions;
import com.elasticpath.cmclient.changeset.views.ChangeSetsView;
import com.elasticpath.domain.changeset.ChangeSet;
import com.elasticpath.domain.changeset.ChangeSetStateCode;

/**
 * An action delegate to finalise a change set.
 */
public class FinalizeChangeSetActionDelegate extends AbstractChangeStateActionDelegate {

	@Override
	protected boolean isStateEnabler(final ChangeSetStateCode stateCode) {
		return stateCode == ChangeSetStateCode.LOCKED || stateCode == ChangeSetStateCode.READY_TO_PUBLISH;
	}

	@Override
	protected ChangeSetStateCode getNewState() {
		return ChangeSetStateCode.FINALIZED;
	}

	@Override
	protected boolean isAuthorized() {
		return isAuthorized(ChangeSetPermissions.CHANGE_SET_PERMISSIONS_MANAGE)
			|| isAuthorized(ChangeSetPermissions.WORK_WITH_CHANGE_SETS_PERMISSION);
	}

	@Override
	public void run(final IAction action) {
		final ISelection selection = ((ChangeSetsView) getView()).getViewer().getSelection();
		final ChangeSet changeSet = (ChangeSet) ((IStructuredSelection) selection).getFirstElement();

		boolean result = MessageDialog.openConfirm(null,
				ChangeSetMessages.get().FinalizeChangeSetAction_ConfirmTitle,

				NLS.bind(ChangeSetMessages.get().FinalizeChangeSetAction_ConfirmMessage,
				changeSet.getName()));
		if (result) {
			super.run(action);
		}		
	}
}
