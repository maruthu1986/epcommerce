/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.editors;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.action.IToolBarManager;

import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareEditorPage;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Product;


/**
 * This page of the product editor displays product details. Pages are responsible for creating the sections that belong in those pages and laying
 * them out.
 */
public abstract class AbstractCatalogTabFolderPage extends AbstractPolicyAwareEditorPage {


	/**
	 * Constructor.
	 * 
	 * @param partId the unique part id
	 * @param editor the form editor
	 * @param title the page title
	 * 
	 */
	public AbstractCatalogTabFolderPage(final AbstractCmClientFormEditor editor,  final String partId, final String title) {
		
		super(editor, partId, title, false);
		
	}
	
	
	
	/**
	 * Gets sorted catalogs.
	 *
	 * @return catalog list
	 */
	protected List<Catalog> getSortedCatalogs() {

		List<Catalog> sortedCatalogs = new ArrayList<>(getCatalogs());

		java.util.Collections.sort(sortedCatalogs, (catalog1, catalog2) -> {

			if (catalog1.isMaster() ^ catalog2.isMaster()) {
				if (catalog1.isMaster()) {
					return -1;
				}
				return 1;
			}

			return catalog1.getCode().compareTo(catalog2.getCode());
		});
		return sortedCatalogs;
		
	}
	
	/**
	 * Get the master catalog that the model belongs to.
	 * @return the master catalog
	 */
	public abstract Catalog getMasterCatalog();

	/**
	 * Get the catalog set this model belongs to.
	 * @return a set of catalogs
	 */
	public Set<Catalog> getCatalogs() {
		Set<Catalog> catalogs = ((Product) this.getModel()).getCatalogs();
		AuthorizationService.getInstance().filterAuthorizedCatalogs(catalogs);
		return catalogs;
	}
	
	/**
	 * Get the model, could be either <code>Product</code> or <code>ProductSku</code>.
	 * @return the model
	 */
	public abstract Object getModel();
	

	@Override
	protected int getFormColumnsCount() {
		return 1;
	}

	@Override
	protected void addToolbarActions(final IToolBarManager toolBarManager) {
		//Empty
		
	}
	
	/**
	 * Convenient method to fire a property change.
	 * 
	 * @param propertyId the id of the property that changed
	 */
	public void firePropertyChangeEvent(final int propertyId) {
		firePropertyChange(propertyId);
	}

	
}
