/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.cmclient.core.controlcontribution;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.action.ControlContribution;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.TableWrapData;

import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;

/**
 * This is <code>ControlContribution</code> to display currency drop down list in the editor page. 
 *
 */
public class CatalogPulldownContribution extends ControlContribution {
	
	private CCombo catalogSelectorCombo;
	
	private List<Catalog> catalogList;
	
	private final List<CatalogSelectionListener> catalogSelectionListeners = new LinkedList<CatalogSelectionListener>();

	private final Object model;

	
	
		
	/**
	 * Constructor.
	 * 
	 * @param contributionId the id for this control contribution.
	 * @param model the model
	 * 
	 */
	public CatalogPulldownContribution(final String contributionId, final Object model) {
		super(contributionId);
		this.model = model;
	
	}

	@Override
	protected Control createControl(final Composite parent) {

		this.catalogList = new ArrayList<Catalog>();
		
		final IEpLayoutComposite epComposite = CompositeFactory.createTableWrapLayoutComposite(parent, 2, false);

		epComposite.setLayoutData(new TableWrapData(TableWrapData.CENTER, TableWrapData.FILL, 1, 2));
				
		epComposite.addLabelBold(CoreMessages.get().CatalogPulldownContribution_Catalog, null);
		catalogSelectorCombo = epComposite.addComboBox(EpState.EDITABLE, null);
	
		populateCatalog();
		
		catalogSelectorCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent event) {				
				for (CatalogSelectionListener catalogSelectionListener : CatalogPulldownContribution.this.catalogSelectionListeners) {
					catalogSelectionListener.catalogSelected(getSelectedCatalog());
				}
			}
		});
					
		return epComposite.getSwtComposite();
	}
	
	private void populateCatalog() {
		
		if (model instanceof Product) {
			populateCatalog((Product) model);
		} else if (model instanceof ProductSku) {
			populateCatalog((ProductSku) model);
		} 
		
	}
	
	private void populateCatalog(final Product product) {
				
		for (Category category : product.getCategories()) {
			Catalog catalog = category.getCatalog();
			if (!catalogList.contains(catalog)) {
				catalogList.add(catalog);	
				catalogSelectorCombo.add(catalog.getName());
				if (catalog.isMaster()) {
					catalogSelectorCombo.select(catalogList.size() - 1);
				}
			}
		}
	}
	
	private void populateCatalog(final ProductSku productSku) {
		this.populateCatalog(productSku.getProduct());		
	}
	
	
	/**
	 * Get the catalog combo.
	 * @return the catalog <code>CCombo</code> object
	 */
	public CCombo getCatalogSelectorCombo() {
		return catalogSelectorCombo;
	}

	/**
	 * Get the catalog list.
	 * @return the catalog list
	 */
	public List<Catalog> getCatalogList() {
		return catalogList;
	}
	
	/**
	 * Add catalog selected listener.
	 * @param catalogSelectedListener the listener
	 */
	public void addCatalogSelectedListeners(final CatalogSelectionListener catalogSelectedListener) {
		this.catalogSelectionListeners.add(catalogSelectedListener);
	}
	
	
	/**
	 * Remove catalog selected listener.
	 * @param catalogSelectedListener the listener
	 */
	public void removeCatalogSelectedListeners(final CatalogSelectionListener catalogSelectedListener) {
		this.catalogSelectionListeners.remove(catalogSelectedListener);
	}
	
	/**
	 * Get the selected catalog.
	 * @return the catalog
	 */
	public Catalog getSelectedCatalog() {
		
		return getCatalogList().get(getCatalogSelectorCombo().getSelectionIndex());
	}
	
	
	
	
}
