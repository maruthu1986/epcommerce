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
 * Product SKUs detail page.
 */
public class ProductMultiSkuPage extends AbstractPolicyAwareEditorPage {

	/**
	 * The event ID fired when a SKU gets changed (added or removed).
	 */
	public static final int SKU_CHANGED_EVENT_ID = 8023;

	/**
	 * Constructor.
	 * 
	 * @param editor the parent editor
	 */
	public ProductMultiSkuPage(final ProductEditor editor) {
		super(editor, "productMultiSkuPage", CatalogMessages.get().ProductSingleSkuPage_Title); //$NON-NLS-1$
	}

	@Override
	protected void addEditorSections(final AbstractCmClientFormEditor editor, final IManagedForm managedForm) {
		PolicyActionContainer container = addPolicyActionContainer("section"); //$NON-NLS-1$
		addPart(container, managedForm, new ProductEditorMultiSkuSection(this, (ProductEditor) editor));
		addPart(container, managedForm, new ProductEditorMultiSkuInfoSection(this, (ProductEditor) editor));
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
		return CatalogMessages.get().ProductMultiSkuPage_Title;
	}
	
	/**
	 *
	 */
	public void fireSkuChangedEvent() {
		firePropertyChange(SKU_CHANGED_EVENT_ID);
	}
}