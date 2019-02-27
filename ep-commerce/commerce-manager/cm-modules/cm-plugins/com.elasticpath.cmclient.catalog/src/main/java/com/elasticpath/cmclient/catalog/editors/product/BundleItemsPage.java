/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.catalog.editors.product;

import com.elasticpath.cmclient.catalog.CatalogPlugin;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.ui.forms.IManagedForm;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareEditorPage;

/**
 * ProductEditor Constituent Page.
 */
public class BundleItemsPage extends AbstractPolicyAwareEditorPage {

	private static final int HORIZONTAL_SPACING = 15;

	/**
	 * Constructs this page.
	 * 
	 * @param editor the editor
	 */
	public BundleItemsPage(final AbstractCmClientFormEditor editor) {
		super(editor, "productConstituents", CatalogMessages.get().ProductConstituentsPage_Title, true); //$NON-NLS-1$
	}

	@Override
	protected void addEditorSections(final AbstractCmClientFormEditor editor, final IManagedForm managedForm) {
		PolicyActionContainer container = addPolicyActionContainer("section"); //$NON-NLS-1$
		
		BundleItemsSection constituentsSection = new BundleItemsSection(this, (ProductEditor) editor);
		addPart(container, managedForm, constituentsSection);
		addExtensionEditorSections(editor, managedForm, CatalogPlugin.PLUGIN_ID, getClass().getSimpleName());
	}

	@Override
	protected void addToolbarActions(final IToolBarManager toolBarManager) {
		// No tool bar required.
	}

	@Override
	protected int getFormColumnsCount() {
		return 1;
	}

	@Override
	protected String getFormTitle() {
		return CatalogMessages.get().ProductConstituentsPage_Title;
	}
	
	@Override
	protected Layout getLayout() {
		GridLayout layout = new GridLayout(getFormColumnsCount(), true);
		layout.marginLeft = LEFT_MARGIN;
		layout.marginRight = RIGHT_MARGIN;
		layout.horizontalSpacing = HORIZONTAL_SPACING;
		return layout;
	}

}
