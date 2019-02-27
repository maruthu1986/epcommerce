/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.catalog.editors.catalog;

import com.elasticpath.cmclient.catalog.CatalogPlugin;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.ui.forms.IManagedForm;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.core.editors.AbstractCmClientEditorPage;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;

/**
 * Implementation of the <code>CatalogEditor</code> attributes page providing attributes
 * associated with a catalog.
 */
public class CatalogAttributesPage extends AbstractCmClientEditorPage {
	/**
	 * Default constructor.
	 * 
	 * @param editor the editor this <code>FormPage</code> is apart of
	 */
	public CatalogAttributesPage(final AbstractCmClientFormEditor editor) {
		super(editor, "catalogAttributes", CatalogMessages.get().CatalogAttributesPage_Title, false); //$NON-NLS-1$
	}

	@Override
	protected void addEditorSections(final AbstractCmClientFormEditor editor, final IManagedForm managedForm) {
		managedForm.addPart(new CatalogAttributesSection(this, editor));
		addExtensionEditorSections(editor, managedForm, CatalogPlugin.PLUGIN_ID, getClass().getSimpleName());
	}

	@Override
	protected int getFormColumnsCount() {
		return 1;
	}

	@Override
	protected String getFormTitle() {
		return CatalogMessages.get().CatalogAttributesPage_Form_Title;
	}

	@Override
	protected void addToolbarActions(final IToolBarManager toolBarManager) {
		// Empty for now
	}
	
	@Override
	protected Layout getLayout() {
		return new GridLayout(getFormColumnsCount(), false);
	}

}
