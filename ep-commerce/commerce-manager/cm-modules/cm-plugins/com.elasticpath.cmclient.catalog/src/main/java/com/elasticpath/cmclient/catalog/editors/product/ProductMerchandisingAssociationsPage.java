/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.editors.product;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.ui.forms.IManagedForm;

import com.elasticpath.cmclient.catalog.CatalogImageRegistry;
import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.catalog.CatalogPlugin;
import com.elasticpath.cmclient.catalog.editors.AbstractCatalogTabFolderPage;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTabFolder;
import com.elasticpath.cmclient.core.ui.framework.IEpViewPart;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Product;

/**
 * This page of the product editor displays product merchandising associations. 
 */
public class ProductMerchandisingAssociationsPage extends AbstractCatalogTabFolderPage {

	private IEpTabFolder tabFolder;
	private IEpLayoutComposite mainComposite;
	private final Map<Integer, IEpViewPart> tabViewParts = new HashMap<>();
	
	/**
	 * Constructor.
	 * 
	 * @param editor the form editor
	 * 
	 */
	public ProductMerchandisingAssociationsPage(final AbstractCmClientFormEditor editor) {

		super(editor, "productMerchandisingAssociation", CatalogMessages.get().ProductMerchandisingAssociationPage_Title); //$NON-NLS-1$

	}

	@Override
	protected void addEditorSections(final AbstractCmClientFormEditor editor, final IManagedForm managedForm) {
		mainComposite = CompositeFactory.createGridLayoutComposite(managedForm.getForm().getBody(), 1, false);
		
		final GridData mainTWD = new GridData(GridData.FILL, GridData.FILL, true, true);
		mainComposite.getSwtComposite().setLayoutData(mainTWD);
		
		// ---- DOCaddEditorSections
		
		final IEpLayoutData tableFolderData = mainComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true);

		PolicyActionContainer tabContainer = addPolicyActionContainer("tab"); //$NON-NLS-1$
		
		this.tabFolder = mainComposite.addTabFolder(tableFolderData);
		int index = 0;
		for (Catalog catalog : getSortedCatalogs()) {

			Image image;
			if (catalog.isMaster()) {
				image = CatalogImageRegistry.getImage(CatalogImageRegistry.CATALOG_MASTER);
			} else {
				image = CatalogImageRegistry.getImage(CatalogImageRegistry.CATALOG_VIRTUAL);
			}
			
			final IEpLayoutComposite catalogTabItem = this.tabFolder.addTabItem(catalog.getName(), image, index, 1, false);
			catalogTabItem.getSwtComposite().setLayoutData(tableFolderData.getSwtLayoutData());

			ProductMerchandisingAssociationsViewPart merchAssociationViewPart = 
				new ProductMerchandisingAssociationsViewPart(catalog, (ProductEditor) getEditor());
			
			tabContainer.addDelegate(merchAssociationViewPart); 
			merchAssociationViewPart.createControls(catalogTabItem, tableFolderData);
			tabViewParts.put(index, merchAssociationViewPart);
			
			getEditor().addPropertyListener(merchAssociationViewPart);
			index++;
		}

		addExtensionEditorSections(editor, managedForm, CatalogPlugin.PLUGIN_ID, this.getClass().getSimpleName());

		this.tabFolder.getSwtTabFolder().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				tabViewParts.get(tabFolder.getSelectedTabIndex()).populateControls();
			}
		});
		
		this.tabFolder.setSelection(0);
		// ---- DOCaddEditorSections
		tabViewParts.get(0).populateControls();
		addCompositesToRefresh(mainComposite.getSwtComposite());
	}

	@Override
	protected int getFormColumnsCount() {
		return 1;
	}

	@Override
	protected String getFormTitle() {
		return CatalogMessages.get().ProductMerchandisingAssociationPage_Title;
	}

	@Override
	protected void addToolbarActions(final IToolBarManager toolBarManager) {
		// Empty for now

	}

	@Override
	public Catalog getMasterCatalog() {
		return getModel().getMasterCatalog();
	}

	@Override
	public Product getModel() {
		return ((ProductEditor) this.getEditor()).getModel().getProduct();
	}

	@Override
	protected Layout getLayout() {
		return new GridLayout(getFormColumnsCount(), false);
	}
}
