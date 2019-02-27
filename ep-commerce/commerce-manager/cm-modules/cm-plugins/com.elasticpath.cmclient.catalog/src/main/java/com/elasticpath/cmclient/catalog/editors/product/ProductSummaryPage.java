/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.editors.product;

import com.elasticpath.cmclient.catalog.CatalogPlugin;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.forms.IManagedForm;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareEditorPage;

/**
 * This page of the product editor displays product details. Pages are responsible for creating the sections that belong in those pages and laying
 * them out.
 */
public class ProductSummaryPage extends AbstractPolicyAwareEditorPage {

	/**
	 * Constructor.
	 * 
	 * @param editor the form editor
	 * 
	 */
	public ProductSummaryPage(final AbstractCmClientFormEditor editor) {

		super(editor, "productSummary", CatalogMessages.get().ProductSummaryPage_Title, true); //$NON-NLS-1$
	}

	@Override
	protected void addEditorSections(final AbstractCmClientFormEditor editor, final IManagedForm managedForm) {
		// Create the sections
		PolicyActionContainer container = addPolicyActionContainer("section"); //$NON-NLS-1$
		addPart(container, managedForm, new ProductEditorOverviewSection(this, editor));
		addPart(container, managedForm, new ProductEditorStoreRuleSection(this, editor));
		addExtensionEditorSections(editor, managedForm, CatalogPlugin.PLUGIN_ID, getClass().getSimpleName());
	}

	@Override
	protected int getFormColumnsCount() {
		return 1;
	}

	@Override
	protected String getFormTitle() {
		return CatalogMessages.get().ProductSummaryPage_Title;
	}

	@Override
	protected void addToolbarActions(final IToolBarManager toolBarManager) {
		// Empty for now

	}
}
