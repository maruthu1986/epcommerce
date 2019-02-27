/**
 * Copyright (c) Elastic Path Software Inc., 2010
 */
package com.elasticpath.cmclient.core.ui.dialog;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Shell;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.search.SafeSearchCodes;
import com.elasticpath.cmclient.core.search.impl.SafeSearchCodesImpl;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.service.catalog.CatalogService;
import com.elasticpath.service.search.CatalogAwareSearchCriteria;

/**
 * An abstract class for EP Finder dialog pages that include a catalog picker. 
 */
public abstract class AbstractCatalogObjectFinderDialog extends AbstractEpFinderDialog {


	/**
	 * @param parentShell the parent shell of this dialog
	 */
	public AbstractCatalogObjectFinderDialog(final Shell parentShell) {
		super(parentShell);
	}

	/**
	 * Set catalog search criteria based on the selection of the catalog combo.
	 */
	protected void setCatalogSearchCriteria() {
		SafeSearchCodes catalogCodes = new SafeSearchCodesImpl();
		Set<Locale> searchableLocale;
		if (getCatalogCombo().getSelectionIndex() == 0) {
			catalogCodes.extractAndAdd(getCatalogs(), "code");  //$NON-NLS-1$
			searchableLocale = getCatalogSearchableLocales(getInitializeAuthorizedCatalogsList());
		} else {
			Catalog selectedCatalog = (Catalog) getCatalogCombo().getData(getCatalogCombo().getText());
			if (selectedCatalog == null) {
				return;
			}
			catalogCodes.extractAndAdd(selectedCatalog, "code");  //$NON-NLS-1$
			searchableLocale = getCatalogSearchableLocales(Collections.singletonList(selectedCatalog));
		}
		if (getModel() instanceof CatalogAwareSearchCriteria) {
			((CatalogAwareSearchCriteria) getModel()).setCatalogCodes(catalogCodes.asSet());
		}
		bindCatalogSearchableLocalesToModel(searchableLocale);
	}
	
	/**
	 * Override this method to bind the searchable locale to the model.
	 * 
	 * @param searchableLocales the searchable locale
	 */
	protected abstract void bindCatalogSearchableLocalesToModel(final Set<Locale> searchableLocales);
	
	/**
	 * @param catalogs the catalog list
	 * @return the set of searchable locale
	 */
	protected Set<Locale> getCatalogSearchableLocales(final List<Catalog> catalogs) {
		final Set<Locale> catalogLocales = new HashSet<Locale>();
		for (Catalog cat : catalogs) {
			catalogLocales.addAll(cat.getSupportedLocales());
		}
		return catalogLocales;
	}
	
	/**
	 * 
	 * @return the list of authorized catalogs
	 */
	protected List<Catalog> getInitializeAuthorizedCatalogsList() {
		final CatalogService catalogService = ServiceLocator.getService(ContextIdNames.CATALOG_SERVICE);
		List<Catalog> catalogList = catalogService.findAllCatalogs(); 
		AuthorizationService.getInstance().filterAuthorizedCatalogs(catalogList);
		
		return catalogList;
	}


	/**
	 * Populate the catalog Combo.
	 */
	protected void populateCatalogCombo() {
		if (getCatalog() == null) {
			// Allow user to choose catalog from list of all catalogs
			getCatalogCombo().add(CoreMessages.get().SearchView_Filter_Brand_All);
			for (final Catalog catalog : getCatalogs()) {
				getCatalogCombo().add(catalog.getName());
				getCatalogCombo().setData(catalog.getName(), catalog);
			}
			getCatalogCombo().setText(CoreMessages.get().SearchView_Filter_Brand_All);
			getCatalogCombo().setEnabled(true);
		} else {
			// Catalog has been specified; only add that catalog and do not allow user to choose catalog
			getCatalogCombo().add(getCatalog().getName());
			getCatalogCombo().setData(getCatalog().getName(), getCatalog());
			getCatalogCombo().select(0);
			getCatalogCombo().setEnabled(false);
		}
	}

	/**
	 * Gets catalog combo.
	 * 
	 * @return the catalog combo
	 */
	protected abstract CCombo getCatalogCombo();

	/**
	 * Gets the collection of catalogs.
	 *  
	 * @return the collection of catalogs 
	 */
	protected abstract Collection<Catalog> getCatalogs();

	/**
	 * Get the specific catalog for the dialog.
	 * 
	 * @return the catalog
	 */
	protected abstract Catalog getCatalog();

}
