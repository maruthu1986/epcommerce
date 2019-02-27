/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.cmclient.admin.datapolicies.editors.pages;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.forms.IManagedForm;

import com.elasticpath.cmclient.admin.datapolicies.AdminDataPoliciesMessages;
import com.elasticpath.cmclient.admin.datapolicies.AdminDataPoliciesPlugin;
import com.elasticpath.cmclient.admin.datapolicies.editors.sections.DataPolicySegmentsSection;
import com.elasticpath.cmclient.core.editors.AbstractCmClientEditorPage;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;

/**
 * A page used within the Data Policy editor.
 */
public class DataPolicySegmentsPage extends AbstractCmClientEditorPage {

	/**
	 * Constructor.
	 *
	 * @param editor the form editor
	 */
	public DataPolicySegmentsPage(final AbstractCmClientFormEditor editor) {
		super(editor, "DataPolicySegmentsPage", AdminDataPoliciesMessages.get().DataPolicyEditor_SegmentsPage_Title); //$NON-NLS-1$
	}

	@Override
	protected void addEditorSections(final AbstractCmClientFormEditor editor, final IManagedForm managedForm) {
		managedForm.addPart(new DataPolicySegmentsSection(this, editor));
		addExtensionEditorSections(editor, managedForm, AdminDataPoliciesPlugin.PLUGIN_ID, this.getClass().getSimpleName());
	}

	@Override
	protected int getFormColumnsCount() {
		return 1;
	}

	@Override
	protected String getFormTitle() {
		return AdminDataPoliciesMessages.get().DataPolicyEditor_SegmentsPage_Title;
	}

	@Override
	protected void addToolbarActions(final IToolBarManager toolBarManager) {
		// add nothing
	}
}
