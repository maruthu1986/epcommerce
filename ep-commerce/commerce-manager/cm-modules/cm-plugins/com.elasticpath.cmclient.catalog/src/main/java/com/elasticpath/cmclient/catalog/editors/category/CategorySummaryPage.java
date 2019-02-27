/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.catalog.editors.category;

import com.elasticpath.cmclient.catalog.CatalogPlugin;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.forms.IManagedForm;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareEditorPage;

/**
 * Implementation of the category editor details page providing high level details about a
 * category.
 */
public class CategorySummaryPage extends AbstractPolicyAwareEditorPage {
	/**
	 * Default constructor.
	 * 
	 * @param editor the editor this <code>FormPage</code> is apart of
	 */
	public CategorySummaryPage(final AbstractCmClientFormEditor editor) {
		super(editor, "categorySummary", CatalogMessages.get().CategorySummaryPage_Title, true); //$NON-NLS-1$
	}

	@Override
	protected void addEditorSections(final AbstractCmClientFormEditor editor, final IManagedForm managedForm) {
		PolicyActionContainer categorySummaryContainer = addPolicyActionContainer("categorySummaryContainer"); //$NON-NLS-1$
		
		addPart(categorySummaryContainer, managedForm, new CategoryEditorSummarySection(this, editor));
		addExtensionEditorSections(editor, managedForm, CatalogPlugin.PLUGIN_ID, getClass().getSimpleName());
	}

	@Override
	protected int getFormColumnsCount() {
		return 2;
	}

	@Override
	protected String getFormTitle() {
		return CatalogMessages.get().CategorySummaryPage_Form_Title;
	}

	@Override
	protected void addToolbarActions(final IToolBarManager toolBarManager) {
		// Empty for now
	}
}
