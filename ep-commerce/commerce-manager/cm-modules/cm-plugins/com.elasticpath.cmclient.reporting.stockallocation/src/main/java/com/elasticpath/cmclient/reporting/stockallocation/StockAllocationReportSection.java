/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.reporting.stockallocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPartSite;
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
import com.elasticpath.cmclient.reporting.stockallocation.parameters.StockAllocationParameters;
import com.elasticpath.cmclient.reporting.util.ReportAuthorizationUtility;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.store.StoreService;

/**
 * Represents the UI for the stock allocation report parameter section.
 * 
 */
public class StockAllocationReportSection extends AbstractReportSection {

	private CCombo storeCombo;
	
	private Text skuCodeText;

	private CCombo skuAvailRuleCombo;

	private List<String> storeNames;
	
	private String selectedSkuCode;
	
	private Map<String, Object> paramsMap;
	
	private IEpLayoutComposite parentEpComposite;
	
	private static final String ALL_STORES_STRING = StockAllocationReportMessages.allStores;
	
	private final StockAllocationParameters stockAllocationParameters  = new StockAllocationParameters();
	
	private final ReportAuthorizationUtility reportUtility = new ReportAuthorizationUtility();
	
	/** SkuAvailability parameter key in the map of parameters accessible from this Report Section. */
	public static final String PARAMETER_SKUAVAIL_RULE = "skuAvailRule"; //$NON-NLS-1$
	/** StoreNames parameter key in the map of parameters accessible from this Report Section. */
	public static final String PARAMETER_STORENAMES = "store"; //$NON-NLS-1$
	/** SkuCode parameter key in the map of parameters accessible from this Report Section. */
	public static final String PARAMETER_SKUCODE = "skuCode"; //$NON-NLS-1$
	
	private static final Map<String, Integer> AVAILABILITY_CRITERIA_OPTIONS;
	
	static {
		AVAILABILITY_CRITERIA_OPTIONS = new HashMap<String, Integer>();
		AVAILABILITY_CRITERIA_OPTIONS.put(StockAllocationReportMessages.preOrderOnly, StockAllocationParameters.AVAIL_PRE_ORDER_ONLY);
		AVAILABILITY_CRITERIA_OPTIONS.put(StockAllocationReportMessages.backOrderOnly, StockAllocationParameters.AVAIL_BACK_ORDER_ONLY);
		AVAILABILITY_CRITERIA_OPTIONS.put(StockAllocationReportMessages.backAndPreOrders, StockAllocationParameters.AVAIL_PRE_BACK_ORDER);
	}
	
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
		
		parentEpComposite.addLabelBoldRequired(StockAllocationReportMessages.store, state, dataSpan2Columns); 
		storeCombo = parentEpComposite.addComboBox(state, dataSpan2Columns);

		final IEpLayoutData dataSpan1Column = parentEpComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false, 1, 1);
		final IEpLayoutData dataSpan1Column2 = parentEpComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL, false, false, 1, 1);
		
		parentEpComposite.addLabelBold(StockAllocationReportMessages.skuCode, dataSpan2Columns);
		
		skuCodeText = parentEpComposite.addTextField(state, dataSpan1Column);
			
		final ImageHyperlink skuCodeIcon = parentEpComposite.addHyperLinkImage(
			StockAllocationkReportImageRegistry.getImage(StockAllocationkReportImageRegistry.IMAGE_SKU), state, dataSpan1Column2);
		skuCodeIcon.setToolTipText(StockAllocationReportMessages.skuToolTip);
		skuCodeIcon.addMouseListener(new MouseAdapter() {
			@Override
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

		parentEpComposite.addLabelBold(StockAllocationReportMessages.skuAvailabilityRule, dataSpan2Columns); 
		skuAvailRuleCombo = parentEpComposite.addComboBox(state, dataSpan2Columns);

		populateControls();
	}
	
	
	/**
	 * Binds inputs to controls.
	 * 
	 * @param bindingProvider the binding provider
	 * @param context the data binding context
	 */
	public void bindControlsInternal(final EpControlBindingProvider bindingProvider, final DataBindingContext context) {
		
		final boolean hideDecorationOnFirstValidation = true;
		
		//
		// STORE COMBO BOX - bind the StoreNames
		//
		bindingProvider.bind(context, storeCombo, null, null, new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object value) {
				final int selectionIndex = (Integer) value;
				String selectedString = storeCombo.getItem(selectionIndex);
				List<String> selectedStores = new ArrayList<String>();
				if (ALL_STORES_STRING.equals(selectedString)) {
					selectedStores = reportUtility.getAuthorizedStoreNames();
				} else {
					selectedStores.add(selectedString);
				}
				stockAllocationParameters.setStoreNames(selectedStores);
				return Status.OK_STATUS;
			}
		}, true);
		 
		//
		// SKU CODE TEXT BOX
		//
		bindingProvider.bind(context, skuCodeText, stockAllocationParameters, 
			"skuCode", null, null, hideDecorationOnFirstValidation); //$NON-NLS-1$
		
		//
		// SKU_AVAILABILITY_RULE - bind the code mapped by the chosen name
		//
		bindingProvider.bind(context, skuAvailRuleCombo, null, null, new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object value) {
				final int selectionIndex = (Integer) value;
				String selectedString = skuAvailRuleCombo.getItem(selectionIndex);
				int availabilityCode = AVAILABILITY_CRITERIA_OPTIONS.get(selectedString);
				stockAllocationParameters.setSkuAvailRule(availabilityCode);
				return Status.OK_STATUS;
			}
		}, true);
	}
	
	/**
	 * Populates the controls.
	 */
	protected void populateControls() {
		this.storeCombo.setItems(getAllStoreNames().toArray(new String[storeNames.size()]));
		this.storeCombo.add(StockAllocationReportMessages.allStores, 0); 
		storeCombo.select(0);

		skuAvailRuleCombo.setItems(AVAILABILITY_CRITERIA_OPTIONS.keySet().toArray(new String[0]));
		skuAvailRuleCombo.select(0);
	}

	private List<String> getAllStoreNames() {
		StoreService storeService = LoginManager.getInstance().getBean(ContextIdNames.STORE_SERVICE);
		List<Store> stores = storeService.findAllCompleteStores();
		storeNames = new ArrayList<String>(stores.size());
		for (Store store : stores) {
			if (AuthorizationService.getInstance().isAuthorizedForStore(store)) {
				storeNames.add(store.getName());
			}
		}
		return storeNames;
	}

	/**
	 * Returns whether the user is authorized to view the Report.
	 *
	 * @return <code>true</code> if the user authorized to view the Report, <code>false</code> otherwise
	 */
	public boolean isAuthorized() {
		return AuthorizationService.getInstance().isAuthorizedWithPermission(StockAllocationReportPermissions.REPORTING_STOCK_ALLOCATION_MANAGE);
	}

	/**
	 * Gets the report's parameters and stores them in a map.
	 * 
	 * @return map that stores parameter keys and values
	 */
	public Map<String, Object> getParameters() {
		paramsMap = new LinkedHashMap<String, Object>();
		paramsMap.put(PARAMETER_SKUAVAIL_RULE, stockAllocationParameters.getSkuAvailRule());
		paramsMap.put(PARAMETER_STORENAMES, stockAllocationParameters.getStoreNames());
		paramsMap.put(PARAMETER_SKUCODE, stockAllocationParameters.getSkuCode());
		
		return paramsMap;
	}
	
	/**
	 * Gets the title of the report.
	 * @return String the title of the report
	 */
	public String getReportTitle() {
		return StockAllocationReportMessages.reportTitle;
	}

	@Override
	public void refreshLayout() {
		// not used
	}
}
