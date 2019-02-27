/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.editors.category;

import com.elasticpath.cmclient.catalog.CatalogPlugin;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareEditorPage;

/**
 * Product Attribute page class.
 */
public class CategoryAttributePage extends AbstractPolicyAwareEditorPage {

	/**
	 * This page ID.
	 */
	public static final String PART_ID = "categoryAttributes"; //$NON-NLS-1$

	/**
	 * Constructor.
	 * 
	 * @param editor the editor object passed in for the page.
	 */
	public CategoryAttributePage(final AbstractCmClientFormEditor editor) {
		super(editor, "categoryAttributes", CatalogMessages.get().CategoryAttributePage_Title, true); //$NON-NLS-1$
	}

	@Override
	protected void addEditorSections(final AbstractCmClientFormEditor editor, final IManagedForm managedForm) {
		PolicyActionContainer pageContainer = addPolicyActionContainer("page"); //$NON-NLS-1$
		
		IFormPart section = new CategoryEditorAttributeSection(this, editor);
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
		return CatalogMessages.get().CategoryAttributePage_Title;
	}
	
	@Override
	protected Layout getLayout() {
		return new GridLayout(getFormColumnsCount(), false);
	}
}