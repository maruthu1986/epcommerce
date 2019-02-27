/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.editors.product;

import com.elasticpath.cmclient.catalog.CatalogPlugin;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.policy.StatePolicyDelegate;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareEditorPage;

/**
 * Product Attribute page class.
 */
public class ProductAttributePage extends AbstractPolicyAwareEditorPage {

	/**
	 * the Id of this page.
	 */
	public static final String PAGE_ID = "productAttributes"; //$NON-NLS-1$

	/**
	 * Constructor.
	 * 
	 * @param editor the editor object passed in for the page.
	 */
	public ProductAttributePage(final AbstractCmClientFormEditor editor) {
		super(editor, PAGE_ID, CatalogMessages.get().ProductAttributePage_Title, true);
	}

	@Override
	protected void addEditorSections(final AbstractCmClientFormEditor editor, final IManagedForm managedForm) {
		PolicyActionContainer pageContainer = addPolicyActionContainer("page"); //$NON-NLS-1$
		// Create the product attribute section
		IFormPart section = new ProductEditorAttributeEditSection(this, editor);
		pageContainer.addDelegate((StatePolicyDelegate) section);
		addPart(pageContainer, managedForm, section);
		addExtensionEditorSections(editor, managedForm, CatalogPlugin.PLUGIN_ID, getClass().getSimpleName());
	}

	@Override
	protected void addToolbarActions(final IToolBarManager toolBarManager) {
		// not used
	}

	@Override
	protected int getFormColumnsCount() {
		return 1;
	}

	@Override
	protected String getFormTitle() {
		return CatalogMessages.get().ProductAttributePage_Title;
	}

	@Override
	protected Layout getLayout() {
		return new GridLayout(getFormColumnsCount(), false);
	}
	
	
}