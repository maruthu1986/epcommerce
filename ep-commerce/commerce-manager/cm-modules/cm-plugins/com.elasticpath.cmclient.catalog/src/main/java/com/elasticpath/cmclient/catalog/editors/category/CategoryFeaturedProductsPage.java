/**
 * Copyright (c) Elastic Path Software Inc., 2007
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
 * Category Featured Products page.
 */
public class CategoryFeaturedProductsPage extends AbstractPolicyAwareEditorPage {

	/**
	 * Constructor.
	 * 
	 * @param editor the parent editor
	 */
	public CategoryFeaturedProductsPage(final CategoryEditor editor) {
		super(editor, "categoryFeaturedProductsPage", CatalogMessages.get().CategoryFeaturedProductsPage_Title, true); //$NON-NLS-1$
	}

	@Override
	protected void addEditorSections(final AbstractCmClientFormEditor editor, final IManagedForm managedForm) {
		PolicyActionContainer categoryFeaturedProductsContainer = addPolicyActionContainer("categoryFeaturedProductsContainer"); //$NON-NLS-1$
		addPart(categoryFeaturedProductsContainer, managedForm, new CategoryEditorFeaturedProductsSection(this, editor));
		addExtensionEditorSections(editor, managedForm, CatalogPlugin.PLUGIN_ID, getClass().getSimpleName());
	}

	@Override
	protected void addToolbarActions(final IToolBarManager toolBarManager) {
		// TODO Auto-generated method stub
	}

	@Override
	protected int getFormColumnsCount() {
		return 1;
	}

	@Override
	protected String getFormTitle() {
		return CatalogMessages.get().CategoryFeaturedProductsForm_Title;
	}
}