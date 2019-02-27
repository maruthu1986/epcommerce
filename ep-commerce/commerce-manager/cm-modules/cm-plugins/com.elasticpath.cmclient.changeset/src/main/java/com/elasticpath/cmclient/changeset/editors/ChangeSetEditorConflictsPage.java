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
 * Represents the change set editor objects page.
 */
public class ChangeSetEditorConflictsPage extends AbstractPolicyAwareEditorPage {

	/**
	 * This page ID.
	 */
	private static final String CONFLICTS_PAGE_ID = "ConflictsPage"; //$NON-NLS-1$

	/**
	 * Constructs a new page.
	 * 
	 * @param editor the cm client editor
	 */
	public ChangeSetEditorConflictsPage(final AbstractCmClientFormEditor editor) {
		super(editor, CONFLICTS_PAGE_ID, ChangeSetMessages.get().ChangeSetEditor_ConflictsPageTitle);
	}

	@Override
	protected void addEditorSections(final AbstractCmClientFormEditor editor, final IManagedForm managedForm) {
		ChangeSetEditorConflictsSection objectsSection = new ChangeSetEditorConflictsSection(this, editor);

		PolicyActionContainer container = addPolicyActionContainer("conflictsPage"); //$NON-NLS-1$
		addPart(container, managedForm, objectsSection);
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
		return ChangeSetMessages.get().ChangeSetEditor_ConflictsPageTitle;
	}
}
