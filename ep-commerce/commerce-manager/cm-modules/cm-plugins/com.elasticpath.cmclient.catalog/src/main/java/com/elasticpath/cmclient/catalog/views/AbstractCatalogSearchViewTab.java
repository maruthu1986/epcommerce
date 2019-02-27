/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.catalog.views;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.conversion.Converter;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Control;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.search.SafeSearchCodes;
import com.elasticpath.cmclient.core.search.impl.SafeSearchCodesImpl;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.service.catalog.BrandService;
import com.elasticpath.service.catalog.CatalogService;

/**
 * 
 * The abstract class for product and sku search view tab.
 *
 */
public abstract class AbstractCatalogSearchViewTab {

	private static final int COMBO_MINIMUM_WIDTH = 30;

	private DataBindingContext dataBindingContext;
	
	private List<Catalog> catalogList;

	private List<Brand> brandList;
	
	private final Set<Locale> catalogLocales = new HashSet<>();
	
	/**
	 * delegating method for binding.
	 * 
	 * @param control The SWT control to be bound
	 * @param target The domain model object that the control is to be bound to
	 * @param fieldName The field of the domain model object that the control is to be bound to
	 * @param validator (Optional, can be null) The validator (Atomic or Compound) that checks the user input
	 * @param converter (Optional, can be null) The converter that will convert text input to the data type required by the domain model object
	 */
	protected void bind(final Control control, final Object target, final String fieldName, final IValidator validator, final Converter converter) {
		this.getBindingProvider().bind(this.getDataBindingContext(), control, target, fieldName, validator, converter, false);
	}
	
	/**
	 * delegating method for binding.
	 * 
	 * @param control The SWT control to be bound
	 * @param validator (Optional, can be null) The validator (Atomic or Compound) that checks the user input
	 * @param converter (Optional, can be null) The converter that will convert text input to the data type required by the domain model object
	 * @param customUpdateStrategy (Optional, can be null) pass in a custom update strategy if required.
	 */
	protected void bind(final Control control, final IValidator validator, final Converter converter,
			final ObservableUpdateValueStrategy customUpdateStrategy) {
		this.getBindingProvider().bind(this.getDataBindingContext(), control, validator, converter, customUpdateStrategy, false);
	}
	
	/**
	 * Get binding provider.
	 * 
	 * @return the instance of binding provider
	 */
	protected EpControlBindingProvider getBindingProvider() {
		return EpControlBindingProvider.getInstance();
	}
	
	/**
	 * get data binding context.
	 * 
	 * @return the instance of data binding context
	 */
	protected DataBindingContext getDataBindingContext() {
		if (dataBindingContext == null) {
			dataBindingContext = new DataBindingContext();
		}
		return this.dataBindingContext;
	}
	
	/**
	 * populate the brandCombo.
	 * 
	 * @param brandCombo the brandCombo to be populated
	 */
	protected void populateBrandCombo(final CCombo brandCombo) {
		brandCombo.removeAll();
		// populate brand combo box
		List<Brand> brandList = getAuthorizedBrandList(true);
		brandCombo.add(CatalogMessages.get().SearchView_Filter_Brand_All, getAllFilterIndex());
		brandCombo.select(getAllFilterIndex());
		for (final Brand brand : brandList) {
			String brandDisplayName = brand.getDisplayName(CorePlugin.getDefault().getDefaultLocale(), true);
			//If the brand display name cannot be found for the default locale, we should at least set some value
			if (brandDisplayName == null) {
				getLog().warn("DisplayName cannot be found for brand with code "  //$NON-NLS-1$ 
						+ brand.getCode() 
						+ " in the system's locale. Displaying BrandCode instead."); //$NON-NLS-1$
				brandDisplayName = "?" + brand.getCode() + "?"; //$NON-NLS-1$ //$NON-NLS-2$
			}
			brandCombo.add(brandDisplayName);
		}
	}
	
	/**
	 * populated the catalogCombo.
	 * 
	 * @param catalogCombo the catalogCombo to be populated
	 */
	protected void populateCatalogCombo(final CCombo catalogCombo) {
		// populate catalog combo box
		catalogCombo.removeAll();
		List<Catalog> catalogList = getInitializeAuthorizedCatalogsList(true);
		catalogCombo.add(CatalogMessages.get().SearchView_Filter_Catalog_All, getAllFilterIndex());
		for (final Catalog catalog : catalogList) {
			catalogCombo.add(catalog.getName());
			catalogCombo.setData(catalog.getName(), catalog);
		}
		catalogCombo.select(getAllFilterIndex());
	}
	
	/**
	 * bind catalogCombo.
	 * 
	 * @param catalogCombo the catalogCombo to be bound
	 */
	protected void bindCatalogCombo(final CCombo catalogCombo) {
		// bind catalog
		final ObservableUpdateValueStrategy catalogUpdateStrategy = new ObservableUpdateValueStrategy() {
			// -- Create a custom update strategy to update the model based on the selected catalog
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				SafeSearchCodes catalogCodes = new SafeSearchCodesImpl();
				Set<Locale> searchableLocale;
				if (catalogCombo.getSelectionIndex() == 0) { // ALL
					catalogCodes.extractAndAdd(getInitializeAuthorizedCatalogsList(), "code"); //$NON-NLS-1$
					searchableLocale = getCatalogSearchableLocales(getInitializeAuthorizedCatalogsList());
				} else {
					final Catalog catalog = (Catalog) catalogCombo.getData(catalogCombo.getText()); 
					//ProductSearchView.this.catalogList.get(ProductSearchView.this.catalogCombo.getSelectionIndex() - 1);
					catalogCodes.extractAndAdd(catalog, "code"); //$NON-NLS-1$
					searchableLocale = getCatalogSearchableLocales(Collections.singletonList(catalog));
				}
				bindCatalogCodesToModel(catalogCodes);
				bindCatalogSearchableLocalesToModel(searchableLocale);
				return Status.OK_STATUS;
			}
		};
		this.bind(catalogCombo, null, null, catalogUpdateStrategy);
	}

	/**
	 * bind brandCombo.
	 * 
	 * @param brandCombo the brandCombo to be bound
	 */
	protected void bindBrandCombo(final CCombo brandCombo) {
		// bind brand
		final ObservableUpdateValueStrategy brandUpdateStrategy = new ObservableUpdateValueStrategy() {
			// -- Create a custom update strategy to update the model based on the selected brand
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				if (brandCombo.getSelectionIndex() == 0) { // ALL
					bindBrandCodeToModel(null);
				} else {
					Brand selectedBrand = getBrandList().get(brandCombo.getSelectionIndex() - 1);
					bindBrandCodeToModel(selectedBrand.getCode());
				}
				return Status.OK_STATUS;
			}
		};
		this.bind(brandCombo, null, null, brandUpdateStrategy);
	}

	/**
	 * get catalog codes for search from the catalog list.
	 * 
	 * @param catalogList the catalog list
	 * @return the set of catalog code
	 */
	protected Set<String> getCatalogCodesForSearch(final List<Catalog> catalogList) {
		//initial catalog codes for search criteria
		//This is duplicated code with the one in doSet
		//The "catalogCombo.select()" does not trigger the doSet method on that combo
		SafeSearchCodes catalogCodes = new SafeSearchCodesImpl();
		catalogCodes.extractAndAdd(catalogList, "code");   //$NON-NLS-1$
		return catalogCodes.asSet();
	}
	
	/**
	 * get catalog searchable locales.
	 *
	 * @param catalogs the catalog list
	 * @return the set of searchable locale
	 */
	protected Set<Locale> getCatalogSearchableLocales(final List<Catalog> catalogs) {
		catalogLocales.clear();
		for (Catalog cat : catalogs) {
			for (Locale locale : cat.getSupportedLocales()) {
				catalogLocales.add(locale);
			}
		}
		return catalogLocales;
	}
	
	/**
	 * get brand list without reloading.
	 * 
	 * @return the brand list
	 */
	protected List<Brand> getBrandList() {
		return getAuthorizedBrandList(false);
	}

	/**
	 * get the brand list for the catalog user has access to?
	 * need catalog list in order to get to brands, no?  so get the catalog list - then iterate through that list to build the brand list
	 * 
	 * @param forceReload if it is true, fetch from db
	 * @return the list of brand
	 */
	protected List<Brand> getAuthorizedBrandList(final boolean forceReload) {
		if (forceReload || this.brandList == null) {
			final BrandService brandService = ServiceLocator.getService(ContextIdNames.BRAND_SERVICE);
		
			this.catalogList = getInitializeAuthorizedCatalogsList(); //get the list of authorized Catalogs
			this.brandList = brandService.findAllBrandsFromCatalogList(catalogList);
		}
		return brandList;
	}
	

	/**
	 * get initializing authorized catalog list.
	 * 
	 * @return the list of authorized catalogs
	 */
	protected List<Catalog> getInitializeAuthorizedCatalogsList() {
		return getInitializeAuthorizedCatalogsList(false);
	}
	
	/**
	 * get initializing authorized catalog list.
	 * 
	 * @param forceReload if it is true, fetch from db
	 * @return the list of authorized catalogs
	 */
	protected List<Catalog> getInitializeAuthorizedCatalogsList(final boolean forceReload) {
		if (forceReload || catalogList == null) {
			final CatalogService catalogService = ServiceLocator.getService(ContextIdNames.CATALOG_SERVICE);
			this.catalogList = catalogService.findAllCatalogs(); 
			AuthorizationService.getInstance().filterAuthorizedCatalogs(catalogList);
		}
		return catalogList;
	}

	/**
	 * This method packs the ccombo on its minimum width.
	 *
	 * @param combo the ccombo
	 * */
	protected void configureStandardComboWidth(final CCombo combo) {
		((GridData) combo.getLayoutData()).widthHint = COMBO_MINIMUM_WIDTH;
		combo.layout();
	}

	/**
	 * Get the index of "ALL" in drop down list.
	 * 
	 * @return the index
	 */
	protected int getAllFilterIndex() {
		return 0;
	}
	
	/**
	 * Get the log. 
	 * 
	 * @return the log
	 */
	protected abstract Logger getLog();
	
	/**
	 * bind the brand code to model.
	 * 
	 * @param brandCode the brand code
	 */
	protected abstract void bindBrandCodeToModel(final String brandCode);
	
	/**
	 * bind the searchable locale to the model.
	 * 
	 * @param searchableLocale the searchable locale
	 */
	protected abstract void bindCatalogSearchableLocalesToModel(final Set<Locale> searchableLocale);
	
	/**
	 * bind the catalog codes to the model. 
	 * 
	 * @param catalogCodes the catalog codes
	 */
	protected abstract void bindCatalogCodesToModel(final SafeSearchCodes catalogCodes);
	
	/**
	 * Called when search button is pressed.
	 */
	protected abstract void search();

	/**
	 * Called when the form should be cleared. Clear button is pressed.
	 */
	protected abstract void clear();
}
