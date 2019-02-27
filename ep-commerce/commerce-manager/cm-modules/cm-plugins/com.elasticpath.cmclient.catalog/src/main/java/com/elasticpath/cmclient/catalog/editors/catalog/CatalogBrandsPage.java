/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.catalog.editors.catalog;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import com.elasticpath.cmclient.catalog.CatalogPlugin;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.forms.IManagedForm;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.core.editors.AbstractCmClientEditorPage;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.editors.TableItems;
import com.elasticpath.cmclient.core.editors.TableReloadMarker;
import com.elasticpath.domain.catalog.Brand;

/**
 * Implementation of the <code>CatalogEditor</code> brands page providing available brands
 * within the catalog.
 */
public class CatalogBrandsPage extends AbstractCmClientEditorPage implements Observer {
	
	private List<Brand> catalogBrands;
	
	private CatalogBrandsSection catalogBrandsSection;

	private boolean reloadModel;
	
	/**
	 * Default constructor.
	 * 
	 * @param editor the editor this <code>FormPage</code> is apart of
	 */
	public CatalogBrandsPage(final AbstractCmClientFormEditor editor) {
		super(editor, "catalogBrands", CatalogMessages.get().CatalogBrandsPage_Title, true); //$NON-NLS-1$
	}

	@Override
	protected void addEditorSections(final AbstractCmClientFormEditor editor, final IManagedForm managedForm) {
		if (isNotSaved() && !reloadModel) {
			catalogBrandsSection = new CatalogBrandsSection(this, editor, catalogBrands);
		} else {
			catalogBrandsSection = new CatalogBrandsSection(this, editor);
			reloadModel = false;
		}
		managedForm.addPart(catalogBrandsSection);
		getCustomPageData().put("catalogBrands", this.catalogBrands);
		addExtensionEditorSections(editor, managedForm, CatalogPlugin.PLUGIN_ID, getClass().getSimpleName());
		if (isNotSaved()) {
			catalogBrandsSection.markDirty();
		}
	}

	@Override
	protected int getFormColumnsCount() {
		return 1;
	}

	@Override
	protected String getFormTitle() {
		return CatalogMessages.get().CatalogBrandsPage_Form_Title;
	}

	@Override
	protected void addToolbarActions(final IToolBarManager toolBarManager) {
		// Empty for now
	}

	private boolean isNotSaved() {
		TableItems<Brand> brandTableItems = ((CatalogEditor) getEditor()).getModel().getBrandTableItems();
		return brandTableItems != null
				&& !brandTableItems.isAllEmpty();
	}

	@Override
	public void update(final Observable observable, final Object catalogBrands) {
		this.catalogBrands = (List<Brand>) catalogBrands;
	}
	
	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") final Class clazz) {
		if (clazz == TableReloadMarker.class) {
			return new MyTableMarker();
		} 

		return super.getAdapter(clazz);
	}

	/**
	 * Implementation for this table reload marker.
	 * */
	class MyTableMarker implements TableReloadMarker {

		@Override
		public void markForReload() {
			reloadModel = true;
		}
		
	}	
}
