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
import com.elasticpath.domain.skuconfiguration.SkuOption;

/**
 * Implementation of the <code>CatalogEditor</code> SKU options page providing details about SKU
 * options available within the catalog.
 */
public class CatalogSkuOptionsPage extends AbstractCmClientEditorPage implements Observer {
	
	private CatalogSkuOptionsSection catalogSkuOptionsSection;
	
	private List<SkuOption> skuOptionList;

	private boolean reloadModel;
	
	/**
	 * This page ID. 
	 */
	public static final String PART_ID = "catalogSkuOptions"; //$NON-NLS-1$
	
	/**
	 * Default constructor.
	 * 
	 * @param editor the editor this <code>FormPage</code> is apart of
	 */
	public CatalogSkuOptionsPage(final AbstractCmClientFormEditor editor) {
		super(editor, PART_ID, CatalogMessages.get().CatalogSkuOptionsPage_Title, true);
	}

	@Override
	protected void addEditorSections(final AbstractCmClientFormEditor editor, final IManagedForm managedForm) {
		if (isNotSaved() && !reloadModel) {
			catalogSkuOptionsSection = new CatalogSkuOptionsSection(this, editor, skuOptionList);
		} else {
			catalogSkuOptionsSection = new CatalogSkuOptionsSection(this, editor);
			reloadModel = false;
		}
		managedForm.addPart(catalogSkuOptionsSection);
		getCustomPageData().put("skuOptionList", skuOptionList);
		addExtensionEditorSections(editor, managedForm, CatalogPlugin.PLUGIN_ID, getClass().getSimpleName());
		if (isNotSaved()) {
			catalogSkuOptionsSection.markDirty();
		}
	}

	@Override
	protected int getFormColumnsCount() {
		return 1;
	}

	@Override
	protected String getFormTitle() {
		return CatalogMessages.get().CatalogSkuOptionsPage_Form_Title;
	}

	@Override
	protected void addToolbarActions(final IToolBarManager toolBarManager) {
		// Empty for now
	}

	@Override
	public void update(final Observable observable, final Object skuOptionList) {
		this.skuOptionList = (List<SkuOption>) skuOptionList;
	}
	
	private boolean isNotSaved() {
		TableItems<SkuOption> skuOptionTableItems = ((CatalogEditor) getEditor()).getModel().getSkuOptionTableItems();
		return skuOptionTableItems != null
				&& !skuOptionTableItems.isAllEmpty();
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	public Object getAdapter(final Class clazz) {
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
