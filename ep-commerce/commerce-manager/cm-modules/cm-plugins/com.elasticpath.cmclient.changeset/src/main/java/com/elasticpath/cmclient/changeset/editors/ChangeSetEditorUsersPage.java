/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.changeset.editors;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.forms.IManagedForm;

import com.elasticpath.cmclient.changeset.ChangeSetMessages;
import com.elasticpath.cmclient.changeset.ChangeSetPlugin;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareEditorPage;

/**
 * Represents the change set editor user list page.
 */
public class ChangeSetEditorUsersPage extends AbstractPolicyAwareEditorPage {

	/**
	 * This page ID.
	 */
	private static final String USERS_PAGE_ID = "UsersPage"; //$NON-NLS-1$

	/**
	 * Constructs a new page.
	 * 
	 * @param editor the cm client editor
	 */
	public ChangeSetEditorUsersPage(final AbstractCmClientFormEditor editor) {
		super(editor, USERS_PAGE_ID, ChangeSetMessages.get().ChangeSetEditor_UsersFormTitle);
	}

	@Override
	protected void addEditorSections(final AbstractCmClientFormEditor editor, final IManagedForm managedForm) {
		ChangeSetEditorUsersSection usersSection = new ChangeSetEditorUsersSection(this, editor);

		PolicyActionContainer container = addPolicyActionContainer("usersPage"); //$NON-NLS-1$
		
		addPart(container, managedForm, usersSection);
		addExtensionEditorSections(editor, managedForm, ChangeSetPlugin.PLUGIN_ID, this.getClass().getSimpleName());
	}

	@Override
	protected void addToolbarActions(final IToolBarManager toolBarManager) {
		// no toolbar actions to add
	}

	@Override
	protected int getFormColumnsCount() {
		return 1;
	}

	@Override
	protected String getFormTitle() {
		return ChangeSetMessages.get().ChangeSetEditor_UsersPageTitle;
	}
}