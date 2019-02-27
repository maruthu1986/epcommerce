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
 * Represents the change set editor summary page.
 */
public class ChangeSetEditorSummaryPage extends AbstractPolicyAwareEditorPage {

	/**
	 * This page ID.
	 */
	private static final String SUMMARY_PAGE_ID = "SummaryPage"; //$NON-NLS-1$

	/**
	 * Constructs a new page.
	 * 
	 * @param editor the cm client editor
	 */
	public ChangeSetEditorSummaryPage(final AbstractCmClientFormEditor editor) {
		super(editor, SUMMARY_PAGE_ID, ChangeSetMessages.get().ChangeSetEditor_Summary_Page_Title);
	}
	@Override
	protected void addEditorSections(final AbstractCmClientFormEditor editor, final IManagedForm managedForm) {
		PolicyActionContainer container = addPolicyActionContainer("section"); //$NON-NLS-1$
		ChangeSetEditorSummarySection summarySection = new ChangeSetEditorSummarySection(this, editor);

		addPart(container, managedForm, summarySection);
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
		return ChangeSetMessages.get().ChangeSetEditor_Summary_Form_Title;
	}
}
