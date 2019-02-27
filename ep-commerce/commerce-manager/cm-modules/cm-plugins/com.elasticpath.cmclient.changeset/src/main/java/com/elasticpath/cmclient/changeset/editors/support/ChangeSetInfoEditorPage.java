/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.changeset.editors.support;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.forms.IManagedForm;

import com.elasticpath.cmclient.changeset.ChangeSetMessages;
import com.elasticpath.cmclient.changeset.ChangeSetPlugin;
import com.elasticpath.cmclient.core.editors.AbstractCmClientEditorPage;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;

/**
 * A page representing the change set info to which 
 * the editor's model object belongs.
 */
public class ChangeSetInfoEditorPage extends AbstractCmClientEditorPage {

	/**
	 * This page ID.
	 */
	public static final String PART_ID = "ChangeSetEditorPage"; //$NON-NLS-1$

	/**
	 * Constructor.
	 * @param editor the editor
	 */
	public ChangeSetInfoEditorPage(final AbstractCmClientFormEditor editor) {
		super(editor, PART_ID, ChangeSetMessages.get().ChangeSetInfoPage_PageTitle);
	}

	@Override
	protected void addEditorSections(final AbstractCmClientFormEditor editor, final IManagedForm managedForm) {
		managedForm.addPart(new ChangeSetInfoEditorPageSummarySection(this, editor));
		addExtensionEditorSections(editor, managedForm, ChangeSetPlugin.PLUGIN_ID, this.getClass().getSimpleName());
	}

	@Override
	protected void addToolbarActions(final IToolBarManager toolBarManager) {
		// no actions to add
	}

	@Override
	protected int getFormColumnsCount() {
		return 1;
	}

	@Override
	protected String getFormTitle() {
		return ChangeSetMessages.get().ChangeSetInfoPage_FormTitle;
	}

}
