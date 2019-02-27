/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.reporting.lowstock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;

import com.elasticpath.cmclient.core.LoginManager;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.dialog.SkuFinderDialog;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.reporting.AbstractReportSection;
import com.elasticpath.cmclient.reporting.lowstock.parameters.InventoryLowStockParameters;
import com.elasticpath.cmclient.reporting.views.ReportingNavigationView;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.service.catalog.BrandService;
import com.elasticpath.service.catalog.CatalogService;
import com.elasticpath.service.store.WarehouseService;

/**
 * Represents the UI for the customer registration report parameter section.
 */
public class InventoryLowStockReportSection extends AbstractReportSection {
	private static final Logger LOG = Logger.getLogger(InventoryLowStockReportSection.class);

	private CCombo warehouseCombo;
	
	private Text skuCodeText;

	private CCombo brandCombo;

	private CCombo localeCombo;

	private List<String> warehouseNames;
	
	private List<Long> warehouseUids;
	
	private List<String> brandNames;
	
	private  Map<String, Brand> brandByCode;
	
	private Map<String, String> brandCodeMap;
	
	private String selectedSkuCode;
	
	private Map<String, Object> paramsMap;
	
	private IEpLayoutComposite parentEpComposite;
	
	private final InventoryLowStockParameters inventoryLowStockParameters = new InventoryLowStockParameters();
	
	/**
	 * Creates the parameter controls specified by the Report.
	 * 
	 * @param toolkit the top level toolkit which contains the Report configuration pane
	 * @param parent the parent composite which is the container for this specific Report Parameters section
	 * @param site the Workbench site, so that the composite can get a reference to Views that it should open.
	 */
	public void createControl(final FormToolkit toolkit, final Composite parent,
			final IWorkbenchPartSite site) {
		EpState state = EpState.EDITABLE;
		if (!isAuthorized()) {
			state = EpState.DISABLED;
		}
		
		parentEpComposite = CompositeFactory.createGridLayoutComposite(parent, 2, false);

		final IEpLayoutData dataSpan2Columns = parentEpComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false, 2, 1);
		
		parentEpComposite.addLabelBoldRequired(InventoryLowStockReportMessages.warehouse, state, dataSpan2Columns); 
		warehouseCombo = parentEpComposite.addComboBox(state, dataSpan2Columns);

		final IEpLayoutData dataSpan1Column = parentEpComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false, 1, 1);
		final IEpLayoutData dataSpan1Column2 = parentEpComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL, false, false, 1, 1);
		
		parentEpComposite.addLabelBold(InventoryLowStockReportMessages.skuCode, dataSpan2Columns);
		
		skuCodeText = parentEpComposite.addTextField(state, dataSpan1Column);
				
		final ImageHyperlink skuCodeIcon = parentEpComposite.addHyperLinkImage(
			InventoryLowStockReportImageRegistry.getImage(InventoryLowStockReportImageRegistry.IMAGE_SKU), state, dataSpan1Column2);
		skuCodeIcon.setToolTipText(InventoryLowStockReportMessages.skuToolTip);
		skuCodeIcon.addMouseListener(new MouseAdapter() {
			public void mouseDown(final MouseEvent event) {
				SkuFinderDialog skufinderDialog = new SkuFinderDialog(site.getShell(), null, false);
				int result = skufinderDialog.open();
				if (result == Window.OK) {
					Object skuObject = skufinderDialog.getSelectedObject();
					String skuCode = ""; //$NON-NLS-1$
					if (skuObject instanceof ProductSku) {
						skuCode = ((ProductSku) skuObject).getSkuCode();
					} else if (skuObject instanceof Product) {
						skuCode = ((Product) skuObject).getDefaultSku().getSkuCode();
					}
					
					selectedSkuCode = String.valueOf(skuCode);
					skuCodeText.setText(selectedSkuCode);
				}
				
			}
		});	

		parentEpComposite.addLabelBold(InventoryLowStockReportMessages.locale, dataSpan2Columns); 
		localeCombo = parentEpComposite.addComboBox(state, dataSpan2Columns);
		
		parentEpComposite.addLabelBold(InventoryLowStockReportMessages.brand, dataSpan2Columns); 
		brandCombo = parentEpComposite.addComboBox(state, dataSpan2Columns);
		
		addSelectionListeners();
		populateControls();
		
	}
	
	
	@Override
	public void bindControlsInternal(final EpControlBindingProvider bindingProvider, final DataBindingContext context) {
		
		final boolean hideDecorationOnFirstValidation = true;
		
		final ObservableUpdateValueStrategy warehouseUpdateStrategy = new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				inventoryLowStockParameters.setWarehouse(String.valueOf(newValue));
				determineButtonStatus();
				return Status.OK_STATUS;
			}
		};
		
		bindingProvider.bind(context, warehouseCombo, null, null,   
				warehouseUpdateStrategy, hideDecorationOnFirstValidation);  //EpValidatorFactory.REQUIRED_COMBO_FIRST_ELEMENT_NOT_VALID 
		 
		bindingProvider.bind(context, skuCodeText, inventoryLowStockParameters, 
			"skuCode", null, null, hideDecorationOnFirstValidation); //$NON-NLS-1$
		 
		bindingProvider.bind(context, brandCombo, inventoryLowStockParameters,
			"brand", null, null, hideDecorationOnFirstValidation); //$NON-NLS-1$
		
		final ObservableUpdateValueStrategy storeUpdateStrategy = new ObservableUpdateValueStrategy() {
				@Override
				protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
					Locale locale = (Locale) localeCombo.getData(localeCombo.getText());
					inventoryLowStockParameters.setLocale(locale);
					return Status.OK_STATUS;
				}
			};
		bindingProvider.bind(context, localeCombo, null, null, storeUpdateStrategy,
					hideDecorationOnFirstValidation); 
		
	}
	
	private void determineButtonStatus() {
		ReportingNavigationView navView = null;
		IViewReference[] viewRef = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getViewReferences();
		for (int i = 0; i < viewRef.length; i++) {
			if (viewRef[i].getId().equals(ReportingNavigationView.VIEW_ID)) {
				navView = ((ReportingNavigationView) viewRef[i].getView(false));

			}
		}
		if (warehouseCombo.getSelectionIndex() == 0) {
			navView.disableButtons();
			parentEpComposite.getSwtComposite().getParent().layout();
		} else {
			navView.enableButtons();
			parentEpComposite.getSwtComposite().getParent().layout();
		}
	}
	

	/**
	 * Populates the controls.
	 */
	protected void populateControls() {
		this.warehouseCombo.setItems(getAllWarehouseNamesAndUids().toArray(new String[warehouseNames.size()]));
		this.warehouseCombo.add(InventoryLowStockReportMessages.warehouseComboFirstItem, 0); 
		warehouseCombo.select(0);

		// load all locals 
		final List<Locale> localeCollection = new ArrayList<Locale>(loadAllLocalesForMasterCatalogsInSystem());

		Collections.sort(localeCollection, new Comparator<Locale>() {
			@Override
			public int compare(final Locale locale1, final Locale locale2) {
				return locale1.getDisplayName().compareToIgnoreCase(locale2.getDisplayName());
			}
		});
		
		for (Locale locale : localeCollection) {
			localeCombo.setData(locale.getDisplayName(), locale);
			localeCombo.add(locale.getDisplayName());
		}
		localeCombo.select(0);
		
		loadAllBrands();
		populateBrandCombo((Locale) localeCombo.getData(localeCombo.getText()));
		

	}

	private Collection<Locale>  loadAllLocalesForMasterCatalogsInSystem() {
		final CatalogService catalogService = (CatalogService) LoginManager.getInstance().getBean(ContextIdNames.CATALOG_SERVICE);
		
		return catalogService.findAllCatalogLocales();
	}
	
	private void populateBrandCombo(final Locale locale) {
		brandNames = new ArrayList<String>(brandByCode.size());
		brandCodeMap = new HashMap<String, String>();
		
		for (Brand brand : brandByCode.values()) {
			String brandName = brand.getDisplayName(locale, false);
			if (brandName == null) {
				LOG.error("Localized BrandName for brand with code = " + brand.getCode() + " cannot be found.");  //$NON-NLS-1$//$NON-NLS-2$
				brandName = brand.getCode();
			}
			brandNames.add(brandName);
			brandCodeMap.put(brandName, brand.getCode());
		}
		if (CollectionUtils.isEmpty(brandNames)) {
			LOG.warn("No brands could be found to populate the brand combo box."); //$NON-NLS-1$
			return;
		}
		brandCombo.setItems(brandNames.toArray(new String[brandNames.size()]));
		brandCombo.add(InventoryLowStockReportMessages.allBrands, 0);
		brandCombo.select(0);
	}
	
	private void loadAllBrands() {
		final BrandService brandService = (BrandService) LoginManager.getInstance().getBean(ContextIdNames.BRAND_SERVICE);
		final List<Brand> brands = brandService.list();
		brandByCode = new HashMap<String, Brand>();
		for (Brand brand : brands) {
			brandByCode.put(brand.getCode(), brand);
		}
	}
	
	private List<String> getAllWarehouseNamesAndUids() {
		WarehouseService warehouseService = (WarehouseService) LoginManager.getInstance().getBean(ContextIdNames.WAREHOUSE_SERVICE);
		List<Warehouse> warehouses = warehouseService.findAllWarehouses();
		warehouseNames = new ArrayList<String>(warehouses.size());
		warehouseUids = new ArrayList<Long>(warehouses.size());
		for (Warehouse warehouse : warehouses) {
			if (AuthorizationService.getInstance().isAuthorizedForWarehouse(warehouse)) {
				warehouseNames.add(warehouse.getName());
				warehouseUids.add(warehouse.getUidPk());
			}
		}
		return warehouseNames;
	}

	/**
	 * Returns whether the user is authorized to view the Report.
	 *
	 * @return <code>true</code> if the user authorized to view the Report, <code>false</code> otherwise
	 */
	public boolean isAuthorized() {
		return AuthorizationService.getInstance().isAuthorizedWithPermission(InventoryLowStockReportPermissions.REPORTING_LOW_STOCK_MANAGE);
	}

	/**
	 * Gets the report's parameters and stores them in a map.
	 * 
	 * @return map that stores parameter keys and values
	 */
	public Map<String, Object> getParameters() {
		
		paramsMap = new HashMap<String, Object>();
		int index = Integer.valueOf(inventoryLowStockParameters.getWarehouse());
		if (index > 0) {
			paramsMap.put("warehouseuid", warehouseUids.get(index - 1)); //$NON-NLS-1$
			paramsMap.put("warehouse", warehouseNames.get(index - 1)); //$NON-NLS-1$
		} 
		
		paramsMap.put("skuCode", inventoryLowStockParameters.getSkuCode()); //$NON-NLS-1$
		
		index = Integer.valueOf(inventoryLowStockParameters.getBrand());
		if (index > 0) {
			paramsMap.put("brand", brandCodeMap.get(brandNames.get(index - 1))); //$NON-NLS-1$
		} else {
			paramsMap.put("brand", InventoryLowStockReportMessages.allBrands); //$NON-NLS-1$
		}
		
		paramsMap.put("locale", inventoryLowStockParameters.getLocale()); //$NON-NLS-1$
		
		
	
		return paramsMap;
	}


	private void addSelectionListeners() {
		localeCombo.addSelectionListener(new SelectionAdapter() {
			/*
			 * Refreshes All currencies when sore is selected.
			 */
			@Override
			public void widgetSelected(final SelectionEvent event) {
				brandCombo.removeAll();
				Locale locale = (Locale) localeCombo.getData(localeCombo.getText());
				if (locale != null) {
					populateBrandCombo(locale);
				}
			}
		});
	}
	/**
	 * Gets the title of the report.
	 * @return String the title of the report
	 */
	public String getReportTitle() {
		return InventoryLowStockReportMessages.reportTitle;
	}


	@Override
	public void refreshLayout() {
		// not used
	}
}
