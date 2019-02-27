/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.editors.product;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.action.IToolBarManager;
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
import com.elasticpath.cmclient.policy.StatePolicyDelegate;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.core.dto.catalog.ProductModel;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Product;

/**
 * Product Attribute page class.
 */
public class ProductEditorCategoryAssignmentPage extends AbstractCatalogTabFolderPage {

	private IEpTabFolder tabFolder;
	private final DataBindingContext dataBindingContext = new DataBindingContext();
	private IEpLayoutComposite mainComposite;
	
	/**
	 * Constructor.
	 * 
	 * @param editor the editor object passed in for the page.
	 */
	public ProductEditorCategoryAssignmentPage(final AbstractCmClientFormEditor editor) {
		super(editor, "CategoryAssignment", CatalogMessages.get().ProductCategoryAssignmentPage_Title); //$NON-NLS-1$
	}

	@Override
	protected void addEditorSections(final AbstractCmClientFormEditor editor, final IManagedForm managedForm) {
		mainComposite = CompositeFactory.createGridLayoutComposite(managedForm.getForm().getBody(), 1, false);
		
		final GridData mainTWD = new GridData(GridData.FILL, GridData.FILL, true, true);
		
		mainComposite.getSwtComposite().setLayoutData(mainTWD);
		
		final IEpLayoutData tableFolderData = mainComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true);
		
		PolicyActionContainer tabContainer = addPolicyActionContainer("tab"); //$NON-NLS-1$

		this.tabFolder = mainComposite.addTabFolder(tableFolderData);
		int index = 0;

		// ---- DOCaddEditorSections
		for (Catalog catalog : getSortedCatalogs()) {

			Image image;
			if (catalog.isMaster()) {
				image = CatalogImageRegistry.getImage(CatalogImageRegistry.CATALOG_MASTER);
			} else {
				image = CatalogImageRegistry.getImage(CatalogImageRegistry.CATALOG_VIRTUAL);
			}
			
			final IEpLayoutComposite catalogTabItem = this.tabFolder.addTabItem(catalog.getName(), image, index, 1, false);
			catalogTabItem.getSwtComposite().setLayoutData(tableFolderData.getSwtLayoutData());
			
			IEpViewPart categoryAssignmentViewPart = 
				new ProductEditorCategoryAssignmentViewPart(catalog, (AbstractCmClientFormEditor) this.getEditor());
			
			tabContainer.addDelegate((StatePolicyDelegate) categoryAssignmentViewPart);
			
			categoryAssignmentViewPart.createControls(catalogTabItem, tableFolderData);
			categoryAssignmentViewPart.bindControls(dataBindingContext);
			categoryAssignmentViewPart.populateControls();
			index++;
		}
		this.tabFolder.setSelection(0);
		addCompositesToRefresh(mainComposite.getSwtComposite());
		addExtensionEditorSections(editor, managedForm, CatalogPlugin.PLUGIN_ID, this.getClass().getSimpleName());
	}
	// ---- DOCaddEditorSections

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
		return CatalogMessages.get().ProductCategoryAssignmentPage_Title;
	}

	@Override
	public Catalog getMasterCatalog() {
		return getModel().getMasterCatalog();
	}

	@Override
	public Product getModel() {
		ProductModel model = ((ProductEditor) this.getEditor()).getModel();
		return model.getProduct();
	}
	
	@Override
	protected Layout getLayout() {
		return new GridLayout(getFormColumnsCount(), false);
	}

}
