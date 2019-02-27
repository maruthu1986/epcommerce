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
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.changeset.ChangeSet;
import com.elasticpath.domain.changeset.ChangeSetStateCode;
import com.elasticpath.settings.SettingsReader;
import com.elasticpath.settings.domain.SettingValue;

/**
 * An action delegate to publish a change set.
 */
public class PublishChangeSetActionDelegate extends AbstractChangeStateActionDelegate {

	private static final String SETTINGS_PUBLISH_CHANGESETS_ENABLED = "COMMERCE/SYSTEM/CHANGESETS/enablePublishWorkflow"; //$NON-NLS-1$
	private SettingsReader settingsReader;


	@Override
	protected boolean isStateEnabler(final ChangeSetStateCode stateCode) {
		SettingValue settingValue = getSettingReader().getSettingValue(SETTINGS_PUBLISH_CHANGESETS_ENABLED);
		Boolean publishChangeSetsEnabled = false;
		if (settingValue != null) {
			publishChangeSetsEnabled = settingValue.getBooleanValue();
		}
		return stateCode == ChangeSetStateCode.LOCKED && publishChangeSetsEnabled;
	}

	@Override
	protected ChangeSetStateCode getNewState() {
		return ChangeSetStateCode.READY_TO_PUBLISH;
	}

	@Override
	protected boolean isAuthorized() {
		return (isAuthorized(ChangeSetPermissions.CHANGE_SET_PERMISSIONS_MANAGE)
				|| isAuthorized(ChangeSetPermissions.WORK_WITH_CHANGE_SETS_PERMISSION))
				&& isAuthorized(ChangeSetPermissions.PUBLISH_CHANGE_SETS_PERMISSION);
	}

	@Override
	public void run(final IAction action) {
		final ISelection selection = ((ChangeSetsView) getView()).getViewer().getSelection();
		final ChangeSet changeSet = (ChangeSet) ((IStructuredSelection) selection).getFirstElement();

		boolean result = MessageDialog.openConfirm(null, ChangeSetMessages.get().PublishChangeSetAction_ConfirmTitle,

				NLS.bind(ChangeSetMessages.get().PublishChangeSetAction_ConfirmMessage,
				changeSet.getName()));
		if (result) {
			super.run(action);
		}
	}

	private SettingsReader getSettingReader() {
		if (settingsReader == null) {
			settingsReader = ServiceLocator.getService(ContextIdNames.CACHED_SETTINGS_READER);
		}
		return settingsReader;
	}
}
