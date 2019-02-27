/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.changeset.actions;

import com.elasticpath.cmclient.core.actions.AbstractDynamicPullDownAction;
import org.eclipse.ui.IWorkbenchWindow;

import com.elasticpath.cmclient.changeset.ChangeSetPlugin;
import com.elasticpath.domain.changeset.ChangeSet;

/**
 * The change set switch action.
 */
public class ChangeSetSwitchAction extends AbstractDynamicPullDownAction<ChangeSet> {

	private final ChangeSet changeSet;
	private final IWorkbenchWindow workbenchWindow;
	private final ChangeSetActionUtil changeSetActionUtil = new ChangeSetActionUtil();

	/**
	 * The constructor of the new instance.
	 * @param menuObject the menu object
	 * @param workbenchWindow the workbench window
	 * 
	 */
	public ChangeSetSwitchAction(final ChangeSet menuObject, final IWorkbenchWindow workbenchWindow) {
		super(menuObject.getName(), AS_RADIO_BUTTON);
		this.changeSet = menuObject;
		this.workbenchWindow = workbenchWindow;
		setText(changeSet.getName());
		setToolTipText(changeSet.getDescription());
	}


	@Override
	public void run() {
		final ChangeSet lastActiveChangeSet = ChangeSetPlugin.getDefault().getActiveChangeSet();
		
		// reload all the editors part of the previously active change set
		if (!changeSetActionUtil.saveAndReloadEditors(lastActiveChangeSet, workbenchWindow)) {
			return;
		}
		ChangeSetPlugin.getDefault().setActiveChangeSet(this.changeSet);
		
		// apply policies to all editors part of either the previous active change set or the new active one
		changeSetActionUtil.applyStatePolicyToComponents(lastActiveChangeSet, changeSet, this.workbenchWindow.getActivePage());
	}

	@Override
	public ChangeSet getPullDownObject() {
		return changeSet;
	}
}
