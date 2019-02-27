/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */

package com.elasticpath.cmclient.reporting.ordersummary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Currency;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.elasticpath.cmclient.core.util.DateTimeUtilFactory;
import com.elasticpath.cmclient.reporting.common.SavedReportParameters;
import com.elasticpath.cmclient.reporting.util.ReportAuthorizationUtility;
import org.apache.commons.collections.CollectionUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.elasticpath.cmclient.core.CmClientResources;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpDateTimePicker;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.CompoundValidator;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.reporting.AbstractReportSection;
import com.elasticpath.cmclient.reporting.ordersummary.parameters.OrderSummaryParameters;
import com.elasticpath.domain.order.OrderStatus;
import com.elasticpath.domain.store.Store;

/**
 * Represents the UI for the Order Summary report parameter section.
 */
public class OrderSummaryReportSection extends AbstractReportSection {
	
	private static final String ORDER_STATUS_TABLE = "order_status_table"; //$NON-NLS-1$

	private IEpDateTimePicker fromDatePicker;
	private IEpDateTimePicker toDatePicker;

	private CCombo currencyCombo;
	private CCombo storeCombo;
	private Button isShowExchangeOnly;
	
	private final OrderSummaryParameters orderSummaryParameters = OrderSummaryParameters.getInstance();
	
	private IEpLayoutComposite errorEpComposite;
	private IEpLayoutComposite parentEpComposite;
	private CheckboxTableViewer checkboxTableViewer;

	private final ReportAuthorizationUtility reportUtility = new ReportAuthorizationUtility();
	private List<Store> availableStores;

	private int selectedStoreIndex = -1;

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
		
		parentEpComposite = CompositeFactory.createGridLayoutComposite(parent, 1, false);
		parentEpComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		final IEpLayoutData data = parentEpComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false);

		parentEpComposite.addLabelBoldRequired(OrderSummaryReportMessages.get().store, state, null);
		storeCombo = parentEpComposite.addComboBox(state, data);

		parentEpComposite.addLabelBoldRequired(OrderSummaryReportMessages.get().currency, state, null);
		currencyCombo = parentEpComposite.addComboBox(state, data);

		parentEpComposite.addLabelBoldRequired(OrderSummaryReportMessages.get().fromDate, state, null);
		fromDatePicker = parentEpComposite.addDateTimeComponent(IEpDateTimePicker.STYLE_DATE_AND_TIME, state, data);

		parentEpComposite.addLabelBoldRequired(OrderSummaryReportMessages.get().toDate, state, data);
		toDatePicker = parentEpComposite.addDateTimeComponent(IEpDateTimePicker.STYLE_DATE_AND_TIME, state, data);

		isShowExchangeOnly = parentEpComposite.addCheckBoxButton(OrderSummaryReportMessages.get().exchangeOnly, state, data);

		errorEpComposite = parentEpComposite.addTableWrapLayoutComposite(2, false, parentEpComposite.createLayoutData(IEpLayoutData.CENTER,
				IEpLayoutData.FILL, true, false));
		errorEpComposite.getSwtComposite().setVisible(false);
		
		errorEpComposite.addImage(OrderSummaryReportImageRegistry.getImage(OrderSummaryReportImageRegistry.IMAGE_ERROR), null);

		// adding a wrapping label
		final Label errorMessageLabel = errorEpComposite.getFormToolkit().createLabel(errorEpComposite.getSwtComposite(),
				OrderSummaryReportMessages.get().checkBoxNoneSelectedError, SWT.WRAP);
		errorMessageLabel.setForeground(CmClientResources.getColor(CmClientResources.COLOR_RED));

		final IEpLayoutComposite orderStatusGroup = parentEpComposite.addGroup(OrderSummaryReportMessages.get().orderStatusGroupHeader, 1, false,
				null);

		final IEpLayoutData groupData = orderStatusGroup.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false);

		checkboxTableViewer = orderStatusGroup.addCheckboxTableViewer(state, groupData, false, ORDER_STATUS_TABLE);
		checkboxTableViewer.getTable().setHeaderVisible(false);
		checkboxTableViewer.getTable().setLinesVisible(false);
		checkboxTableViewer.getTable().setBackgroundMode(SWT.INHERIT_FORCE);
		checkboxTableViewer.setContentProvider(new ArrayContentProvider());
		checkboxTableViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(final Object element) {
				OrderStatus orderStatus = (OrderStatus) element;
				return OrderSummaryReportMessages.get().getLocalizedName(orderStatus);
			}
		});
		Collection<OrderStatus> orderStatuses = new ArrayList<>();
		orderStatuses.addAll(OrderStatus.values());
		orderStatuses.remove(OrderStatus.FAILED);
		checkboxTableViewer.setInput(orderStatuses);
		checkboxTableViewer.setSorter(new ViewerSorter() {
		});

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

		bindStores(bindingProvider, context, hideDecorationOnFirstValidation);

		bindDates(context);

		// currency binding
		final ObservableUpdateValueStrategy currencyUpdateStrategy = new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				Currency currency = (Currency) currencyCombo.getData(currencyCombo.getText());
				orderSummaryParameters.setCurrency(currency);
				return Status.OK_STATUS;
			}
		};
		bindingProvider.bind(context, currencyCombo, null, null, currencyUpdateStrategy, hideDecorationOnFirstValidation);

		// others
		bindingProvider.bind(context, isShowExchangeOnly, orderSummaryParameters,
					"showExchangeOnly", null, null, hideDecorationOnFirstValidation); //$NON-NLS-1$

		addSelectionListeners();
	}

	private void bindStores(final EpControlBindingProvider bindingProvider,
								final DataBindingContext context,
								final boolean hideDecorationOnFirstValidation) {

		final ObservableUpdateValueStrategy storeComboUpdateStrategy = new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {

				int newSelectedStoreIndex = ((Integer) newValue).intValue();

				if (selectedStoreIndex == newSelectedStoreIndex) {
					return Status.OK_STATUS;
				}

				selectedStoreIndex = newSelectedStoreIndex;
				if (selectedStoreIndex > 0) {
					final Store store = availableStores.get(selectedStoreIndex - 1);
					orderSummaryParameters.setStore(store.getCode());
				}
				return Status.OK_STATUS;

			}

		};
		bindingProvider.bind(context, storeCombo, null, null, storeComboUpdateStrategy, hideDecorationOnFirstValidation);

	}

	private void bindDates(final DataBindingContext context) {
		// from-to date interbinding for from before to date validation
		final ModifyListener updateModels = new ModifyListener() {
			@Override
			public void modifyText(final ModifyEvent event) {
				context.updateModels(); // re-validate bound events
				updateButtonsStatus();
			}
		};
		fromDatePicker.getSwtText().addModifyListener(updateModels);
		toDatePicker.getSwtText().addModifyListener(updateModels);


		fromDatePicker.bind(context, EpValidatorFactory.DATE_TIME_REQUIRED, orderSummaryParameters, "startDate"); //$NON-NLS-1$

		IValidator toValidator = new CompoundValidator(new IValidator[] { EpValidatorFactory.DATE_TIME_REQUIRED,
				EpValidatorFactory.createToDateValidator(fromDatePicker, toDatePicker) });
		toDatePicker.bind(context, toValidator, orderSummaryParameters, "endDate"); //$NON-NLS-1$
	}
	
	/**
	 * Populates the controls.
	 */
	protected void populateControls() {
		availableStores = reportUtility.getAuthorizedStores();
		storeCombo.setItems(getStoreNames());
		storeCombo.select(0);
		storeCombo.setText(OrderSummaryReportMessages.get().allStores);

		toDatePicker.setDate(new Date());
		
		checkboxTableViewer.setChecked(OrderStatus.AWAITING_EXCHANGE, true);
		checkboxTableViewer.setChecked(OrderStatus.CANCELLED, false);
		checkboxTableViewer.setChecked(OrderStatus.COMPLETED, true);
		checkboxTableViewer.setChecked(OrderStatus.CREATED, true);
		checkboxTableViewer.setChecked(OrderStatus.IN_PROGRESS, true);
		checkboxTableViewer.setChecked(OrderStatus.ONHOLD, true);
		checkboxTableViewer.setChecked(OrderStatus.PARTIALLY_SHIPPED, true);

		restoreSavedParameters();
	}

	private void restoreSavedParameters() {
		final SavedReportParameters savedParameters = SavedReportParameters.getInstance();
		selectedStoreIndex = savedParameters.restoreStoreSelection(storeCombo, availableStores, orderSummaryParameters);
		addStoreCurrenciesToCombo(selectedStoreIndex);
		savedParameters.restoreCurrencySelection(currencyCombo, orderSummaryParameters);
		savedParameters.restoreStartDate(fromDatePicker);
		savedParameters.restoreEndDate(toDatePicker);
		updateButtonsStatus();
	}

	private String[] getStoreNames() {
		if (CollectionUtils.isNotEmpty(availableStores)) {
			final String[] names = new String[availableStores.size() + 1];
			names[0] = OrderSummaryReportMessages.get().allStores;
			for (int index = 0; index < availableStores.size(); index++) {
				names[index + 1] = availableStores.get(index).getName();
			}
			return names;
		}
		return new String[0];
	}

	/**
	 * Returns whether the user is authorized to view the Report.
	 *
	 * @return <code>true</code> if the user authorized to view the Report, <code>false</code> otherwise
	 */
	public boolean isAuthorized() {
		return AuthorizationService.getInstance().isAuthorizedWithPermission(OrderSummaryReportPermissions.REPORTING_ORDER_SUMMARY_MANAGE);
	}
	
	private void addSelectionListeners() {
		storeCombo.addSelectionListener(new SelectionAdapter() {
			/*
			 * Refreshes All currencies when store is selected.
			 */
			@Override
			public void widgetSelected(final SelectionEvent event) {
				addStoreCurrenciesToCombo(selectedStoreIndex);
				updateButtonsStatus();
			}
		});

		checkboxTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(final SelectionChangedEvent event) {
				updateButtonsStatus();
			}
		});
	}

	private void addStoreCurrenciesToCombo(final int selectedStoreIndex) {
		currencyCombo.removeAll();
		if (selectedStoreIndex == 0) {
			return;
		}
		final Store store = availableStores.get(selectedStoreIndex - 1);
		if (store != null) {
			Collection<Currency> supportedCurrencies = store.getSupportedCurrencies();
			Currency defaultCurrency = store.getDefaultCurrency();

			for (Currency currency : supportedCurrencies) {
				currencyCombo.setData(currency.getCurrencyCode(), currency);
				currencyCombo.add(currency.getCurrencyCode());
				if (currency.equals(defaultCurrency)) {
					currencyCombo.select(currencyCombo.indexOf(currency.getCurrencyCode()));
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void updateParameters() {
		orderSummaryParameters.setCheckedOrderStatuses((List) Arrays.asList(checkboxTableViewer.getCheckedElements()));
		orderSummaryParameters.setTitle(generateOrderStatuses());
	}
	
	/**
	 * Gets the report's parameters and stores them in a map.
	 * 
	 * @return map that stores parameter keys and values
	 */
	public Map<String, Object> getParameters() {
		updateParameters();

		Map<String, Object> paramsMap = new LinkedHashMap<>();
		
		paramsMap.put("store", orderSummaryParameters.getStore()); //$NON-NLS-1$
		paramsMap.put("isShowExchangeOnly", orderSummaryParameters.getExchangeString()); //$NON-NLS-1$
		paramsMap.put("startDate", DateTimeUtilFactory.getDateUtil().formatAsDateTime(orderSummaryParameters.getStartDate())); //$NON-NLS-1$
		paramsMap.put("endDate", DateTimeUtilFactory.getDateUtil().formatAsDateTime(orderSummaryParameters.getEndDate())); //$NON-NLS-1$
		paramsMap.put("currency", orderSummaryParameters.getCurrency().getCurrencyCode()); //$NON-NLS-1$
		paramsMap.put("checkedStatuses", orderSummaryParameters.getCheckedOrderStatuses()); //$NON-NLS-1$
		paramsMap.put("internationalizedStatuses", orderSummaryParameters.getTitle()); //$NON-NLS-1$

		return paramsMap;
	}

	/**
	 * Gets the title of the report.
	 * @return String the title of the report
	 */
	public String getReportTitle() {
		return OrderSummaryReportMessages.get().reportTitle;
	}
	
	/**
	 * Returns true if 1) there is at least one check box checked; 2) a store is selected; 3) other inputs are valid.
	 * False if none checked.
	 * 
	 * @return true if the parameter selections are valid, false if none checked
	 */
	@Override
	public boolean isInputValid() {
		boolean storeSelected = storeCombo.getSelectionIndex() > 0;
		return storeSelected && isAnyStatusChecked() && super.isInputValid();
	}

	private boolean isAnyStatusChecked() {
		return checkboxTableViewer.getCheckedElements().length != 0;
	}

	@Override
	public void refreshLayout() {
		if (isAnyStatusChecked()) {
			errorEpComposite.getSwtComposite().setVisible(false);
			parentEpComposite.getSwtComposite().getParent().layout();
		} else {
			errorEpComposite.getSwtComposite().setVisible(true);
			parentEpComposite.getSwtComposite().getParent().layout();
		}
	}

	/*
	 * This method should generate order return statuses internationalized header.
	 */
	private String generateOrderStatuses() {

		if (!isInputValid()) {
			return ""; //$NON-NLS-1$
		}
		StringBuilder internationalizedStatuses = new StringBuilder();
		Object[] statuses =  checkboxTableViewer.getCheckedElements();
		for (Object object : statuses) {
			OrderStatus status = (OrderStatus) object;
			internationalizedStatuses.append(OrderSummaryReportMessages.get().getLocalizedName(status));
			internationalizedStatuses.append(", "); //$NON-NLS-1$
		}
		internationalizedStatuses.deleteCharAt(internationalizedStatuses.length() - 2);
		return internationalizedStatuses.toString();
	}

}
