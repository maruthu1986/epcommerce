/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.cmclient.admin.datapolicies.editors.pages;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.forms.IManagedForm;

import com.elasticpath.cmclient.admin.datapolicies.AdminDataPoliciesMessages;
import com.elasticpath.cmclient.admin.datapolicies.AdminDataPoliciesPlugin;
import com.elasticpath.cmclient.admin.datapolicies.editors.sections.DataPolicyDataPointSection;
import com.elasticpath.cmclient.core.editors.AbstractCmClientEditorPage;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;

/**
 * Represents the UI of the Data Policy Data point page.
 */
public class DataPolicyDataPointPage extends AbstractCmClientEditorPage {

	/**
	 * Creates DataPolicyDataPointPage Instance.
	 *
	 * @param editor <code>DataPolicyEditor</code>
	 */
	public DataPolicyDataPointPage(final AbstractCmClientFormEditor editor) {
		super(editor, "DataPolicyDataPointPage", AdminDataPoliciesMessages.get().DataPolicyEditor_DataPoints_Title);  //$NON-NLS-1$
	}

	@Override
	protected void addEditorSections(final AbstractCmClientFormEditor editor, final IManagedForm managedForm) {
		managedForm.addPart(new DataPolicyDataPointSection(this, editor));
		addExtensionEditorSections(editor, managedForm, AdminDataPoliciesPlugin.PLUGIN_ID, this.getClass().getSimpleName());
	}

	@Override
	protected String getFormTitle() {
		return AdminDataPoliciesMessages.get().DataPolicyEditor_DataPoints_Title;
	}

	@Override
	protected int getFormColumnsCount() {
		return 1;
	}

	@Override
	protected void addToolbarActions(final IToolBarManager iToolBarManager) {
		//add nothing
	}
}