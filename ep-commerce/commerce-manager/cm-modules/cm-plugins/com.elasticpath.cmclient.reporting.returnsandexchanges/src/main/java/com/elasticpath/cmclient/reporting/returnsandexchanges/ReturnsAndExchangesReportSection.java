/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.reporting.returnsandexchanges;

import java.util.Arrays;
import java.util.Collection;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
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
import com.elasticpath.cmclient.core.util.DateTimeUtilFactory;
import com.elasticpath.cmclient.core.validation.CompoundValidator;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.reporting.AbstractReportSection;
import com.elasticpath.cmclient.reporting.common.SavedReportParameters;
import com.elasticpath.cmclient.reporting.returnsandexchanges.parameters.ReturnsAndExchangesParameters;
import com.elasticpath.cmclient.reporting.util.ReportAuthorizationUtility;
import com.elasticpath.domain.order.OrderReturnStatus;
import com.elasticpath.domain.order.OrderReturnType;
import com.elasticpath.domain.store.Store;

/**
 * Represents the UI for the Returns and Exchanges parameter section.
 */
@SuppressWarnings({"PMD.GodClass"})
public class ReturnsAndExchangesReportSection extends AbstractReportSection {

	private static final String ORDER_STATUS_TABLE = "order_status_table"; //$NON-NLS-1$

	private CCombo storeCombo;
	private CCombo currencyCombo;
	private CCombo returnTypesCombo;

	private IEpDateTimePicker fromDatePicker;
	private IEpDateTimePicker toDatePicker;

	private final ReturnsAndExchangesParameters controlParameters = ReturnsAndExchangesParameters.getInstance();

	private IEpLayoutComposite parentEpComposite;
	private IEpLayoutComposite errorEpComposite;

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
	@Override
	public void createControl(final FormToolkit toolkit, final Composite parent, final IWorkbenchPartSite site) {
		EpState state = EpState.EDITABLE;
		if (!isAuthorized()) {
			state = EpState.DISABLED;
		}

		parentEpComposite = CompositeFactory.createGridLayoutComposite(parent, 1, false);
		parentEpComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		final IEpLayoutData data = parentEpComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false);

		parentEpComposite.addLabelBoldRequired(ReturnsAndExchangesReportMessages.get().store, state, null);
		storeCombo = parentEpComposite.addComboBox(state, data);

		parentEpComposite.addLabelBoldRequired(ReturnsAndExchangesReportMessages.get().currency, state, null);
		currencyCombo = parentEpComposite.addComboBox(state, data);

		parentEpComposite.addLabelBoldRequired(ReturnsAndExchangesReportMessages.get().fromDate, state, null);
		fromDatePicker = parentEpComposite.addDateTimeComponent(IEpDateTimePicker.STYLE_DATE_AND_TIME, state, data);

		parentEpComposite.addLabelBoldRequired(ReturnsAndExchangesReportMessages.get().toDate, state, data);
		toDatePicker = parentEpComposite.addDateTimeComponent(IEpDateTimePicker.STYLE_DATE_AND_TIME, state, data);

		parentEpComposite.addLabelBold(ReturnsAndExchangesReportMessages.get().rmaType, null);
		returnTypesCombo = parentEpComposite.addComboBox(state, data);

		errorEpComposite = parentEpComposite.addTableWrapLayoutComposite(2, false,
				parentEpComposite.createLayoutData(IEpLayoutData.CENTER, IEpLayoutData.FILL, true, false));
		errorEpComposite.getSwtComposite().setVisible(false);
		errorEpComposite.addImage(ReturnsAndExchangesReportImageRegistry.getImage(ReturnsAndExchangesReportImageRegistry.IMAGE_ERROR), null);

		// adding a wrapping label
		final Label errorMessageLabel = errorEpComposite.getFormToolkit().createLabel(errorEpComposite.getSwtComposite(),
				ReturnsAndExchangesReportMessages.get().checkBoxNoneSelectedError, SWT.WRAP);
		errorMessageLabel.setForeground(CmClientResources.getColor(CmClientResources.COLOR_RED));

		final IEpLayoutComposite orderStatusGroup = parentEpComposite.addGroup(ReturnsAndExchangesReportMessages.get().statusGroupHeader, 1,
				false, null);
		final IEpLayoutData groupData = orderStatusGroup.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false);

		checkboxTableViewer = orderStatusGroup.addCheckboxTableViewer(state, groupData, false, ORDER_STATUS_TABLE);
		checkboxTableViewer.getTable().setHeaderVisible(false);
		checkboxTableViewer.getTable().setLinesVisible(false);
		checkboxTableViewer.getTable().setBackgroundMode(SWT.INHERIT_FORCE);
		checkboxTableViewer.setContentProvider(new ArrayContentProvider());
		checkboxTableViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(final Object element) {
				OrderReturnStatus orderReturnStatus = (OrderReturnStatus) element;
				return ReturnsAndExchangesReportMessages.get().getLocalizedName(orderReturnStatus);
			}
		});

		checkboxTableViewer.setInput(OrderReturnStatus.values());
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
	@Override
	protected void bindControlsInternal(final EpControlBindingProvider bindingProvider, final DataBindingContext context) {
		final boolean hideDecorationOnFirstValidation = true;
		bindStore(bindingProvider, context, hideDecorationOnFirstValidation);
		bindCurrency(bindingProvider, context, hideDecorationOnFirstValidation);
		bindDates(context);
		bindReturnType(bindingProvider, context, hideDecorationOnFirstValidation);
		bindOrderStatus();
		
		// store control listener
		addSelectionListeners();
	}

	private void bindOrderStatus() {
		checkboxTableViewer.addCheckStateListener(new ICheckStateListener() {
			@Override
			public void checkStateChanged(final CheckStateChangedEvent event) {
				updateButtonsStatus();
			}
		});
	}

	private void bindReturnType(final EpControlBindingProvider bindingProvider,
			final DataBindingContext context,
			final boolean hideDecorationOnFirstValidation) {
		final ObservableUpdateValueStrategy rmaTypeUpdateStrategy = new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				OrderReturnType orderReturnType = (OrderReturnType) returnTypesCombo.getData(returnTypesCombo.getText());
				controlParameters.setRmaType(orderReturnType);
				return Status.OK_STATUS;
			}
		};
		bindingProvider.bind(context, returnTypesCombo, null, null, rmaTypeUpdateStrategy, hideDecorationOnFirstValidation);
	}

	private void bindCurrency(final EpControlBindingProvider bindingProvider, final DataBindingContext context,
			final boolean hideDecorationOnFirstValidation) {
		final ObservableUpdateValueStrategy currencyUpdateStrategy = new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				Currency currency = (Currency) currencyCombo.getData(currencyCombo.getText());
				controlParameters.setCurrency(currency);
				return Status.OK_STATUS;
	      	}
	    };
	    bindingProvider.bind(context, currencyCombo, null, null, currencyUpdateStrategy, hideDecorationOnFirstValidation);
	}

	private void bindStore(final EpControlBindingProvider bindingProvider, final DataBindingContext context,
			final boolean hideDecorationOnFirstValidation) {
		final ObservableUpdateValueStrategy storeUpdateStrategy = new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {

				int newSelectedStoreIndex = ((Integer) newValue).intValue();

				if (selectedStoreIndex == newSelectedStoreIndex) {
					return Status.OK_STATUS;
				}

				selectedStoreIndex = newSelectedStoreIndex;
				if (selectedStoreIndex > 0) {
					final Store store = availableStores.get(selectedStoreIndex - 1);
					controlParameters.setStore(store.getCode());
				} else {
					controlParameters.setStore(null);
				}

				return Status.OK_STATUS;
			}
		};
		bindingProvider.bind(context, storeCombo, null, null, storeUpdateStrategy, hideDecorationOnFirstValidation);
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

		fromDatePicker.bind(context, EpValidatorFactory.DATE_TIME_REQUIRED, controlParameters, "startDate"); //$NON-NLS-1$

		IValidator toValidator = new CompoundValidator(new IValidator[] { EpValidatorFactory.DATE_TIME_REQUIRED,
				EpValidatorFactory.createToDateValidator(fromDatePicker, toDatePicker) });
		toDatePicker.bind(context, toValidator, controlParameters, "endDate"); //$NON-NLS-1$
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
	}

	private void addStoreCurrenciesToCombo(final int selectedStoreIndex) {
		currencyCombo.removeAll();
		if (selectedStoreIndex > 0) {
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
	}

	/**
	 * Populates the controls.
	 */
	protected void populateControls() {
		//store
		availableStores = reportUtility.getAuthorizedStores();
		storeCombo.setItems(getStoreNames());
		storeCombo.select(0);
		storeCombo.setText(ReturnsAndExchangesReportMessages.get().selectStore);

		// endDate
		toDatePicker.setDate(new Date());

		// rmaType
		this.returnTypesCombo.setData(ReturnsAndExchangesReportMessages.get().returnsAndExchanges, null);
		this.returnTypesCombo.add(ReturnsAndExchangesReportMessages.get().returnsAndExchanges);
		this.returnTypesCombo.setData(ReturnsAndExchangesReportMessages.get().returnsOnly, OrderReturnType.RETURN);
		this.returnTypesCombo.add(ReturnsAndExchangesReportMessages.get().returnsOnly);
		this.returnTypesCombo.setData(ReturnsAndExchangesReportMessages.get().exchangesOnly, OrderReturnType.EXCHANGE);
		this.returnTypesCombo.add(ReturnsAndExchangesReportMessages.get().exchangesOnly);
		returnTypesCombo.select(0);

		// orderReturnStatuses
		checkboxTableViewer.setChecked(OrderReturnStatus.AWAITING_COMPLETION, true);
		checkboxTableViewer.setChecked(OrderReturnStatus.AWAITING_STOCK_RETURN, true);

		restoreSavedParameters();
	}

	private void restoreSavedParameters() {
		final SavedReportParameters savedParameters = SavedReportParameters.getInstance();
		selectedStoreIndex = savedParameters.restoreStoreSelection(storeCombo, availableStores, controlParameters);
		addStoreCurrenciesToCombo(selectedStoreIndex);
		savedParameters.restoreCurrencySelection(currencyCombo, controlParameters);
		savedParameters.restoreStartDate(fromDatePicker);
		savedParameters.restoreEndDate(toDatePicker);
		updateButtonsStatus();
	}

	private String[] getStoreNames() {
		if (CollectionUtils.isNotEmpty(availableStores)) {
			final String[] names = new String[availableStores.size() + 1];
			names[0] = ReturnsAndExchangesReportMessages.get().selectStore;
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
	@Override
	public boolean isAuthorized() {
		return AuthorizationService.getInstance().isAuthorizedWithPermission(
				ReturnsAndExchangesReportPermissions.REPORTING_RETURNS_AND_EXCHANGES_MANAGE);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void updateParameters() {
		controlParameters.setTitle(generateOrderReturnStatuses());
		controlParameters.setCheckedOrderStatuses((List) Arrays.asList(checkboxTableViewer.getCheckedElements()));
	}

	/**
	 * Gets the report's parameters and stores them in a map.
	 * 
	 * @return map that stores parameter keys and values
	 */
	@Override
	public Map<String, Object> getParameters() {
		updateParameters();

		Map<String, Object> paramsMap = new HashMap<>();

		paramsMap.put("store", controlParameters.getStore());
		paramsMap.put("currency", controlParameters.getCurrency().getCurrencyCode());
		paramsMap.put("startDate", DateTimeUtilFactory.getDateUtil().formatAsDateTime(controlParameters.getStartDate()));
		paramsMap.put("endDate", DateTimeUtilFactory.getDateUtil().formatAsDateTime(controlParameters.getEndDate()));
		if (controlParameters.getRmaType() == null) {
			paramsMap.put("rmaType", null);
		} else {
			paramsMap.put("rmaType", controlParameters.getRmaType().name());
		}
		paramsMap.put("checkedStatuses", controlParameters.getCheckedOrderReturnStatuses());
		paramsMap.put("internationalizedStatuses", controlParameters.getTitle());

		return paramsMap;
	}

	/*
	 * This method should generate order return statuses internationalized header.
	 */
	private String generateOrderReturnStatuses() {
		if (!isInputValid()) {
			return ""; //$NON-NLS-1$
		}
		StringBuilder internationalizedStatuses = new StringBuilder();
		Object[] statuses = checkboxTableViewer.getCheckedElements();
		for (Object object : statuses) {
			OrderReturnStatus status = (OrderReturnStatus) object;
			internationalizedStatuses.append(ReturnsAndExchangesReportMessages.get().getLocalizedName(status));
			internationalizedStatuses.append(", "); //$NON-NLS-1$
		}
		internationalizedStatuses.deleteCharAt(internationalizedStatuses.length() - 2);
		return internationalizedStatuses.toString();
	}

	/**
	 * Gets the title of the report.
	 * 
	 * @return String the title of the report
	 */
	@Override
	public String getReportTitle() {
		return ReturnsAndExchangesReportMessages.get().reportTitle;
	}

	/**
	 * Returns true if there is at least one check box checked, false if none checked.
	 * 
	 * @return true if there is at least one check box checked, false if none checked
	 */
	@Override
	public boolean isInputValid() {
		boolean storeSelected = storeCombo.getSelectionIndex() > 0;
		return storeSelected && isAnyStatusChecked() && super.isInputValid();
	}

	private boolean isAnyStatusChecked() {
		if (checkboxTableViewer != null) {
			return checkboxTableViewer.getCheckedElements().length != 0;
		}
		return false;
	}

	/**
	 * Refreshes the composite to show error message if none of the check box is checked.
	 */
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
}
