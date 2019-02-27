/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.reporting.giftcertificatedetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Currency;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpDateTimePicker;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpListViewer;
import com.elasticpath.cmclient.core.validation.CompoundValidator;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.reporting.AbstractReportSection;
import com.elasticpath.cmclient.reporting.giftcertificatedetails.parameters.GiftCertificateDetailsParameters;
import com.elasticpath.cmclient.reporting.util.ReportAuthorizationUtility;
import com.elasticpath.domain.store.Store;

/**
 * Represents the UI for the customer registration report parameter section.
 */
public class GiftCertificateDetailsReportSection extends AbstractReportSection {

	private static final int TABLE_HEIGHT = 100;

	private static final int TABLE_WIDTH = 200;
	
	/** The key for the "storeUidPk" query parameter. */
	public static final String PARAMETER_STORE_UIDPK = "storeUidPk"; //$NON-NLS-1$
	/** The key for the "store" query parameter. */
	public static final String PARAMETER_STORE = "store"; //$NON-NLS-1$
	/** The key for the "currencies" query parameter. */
	public static final String PARAMETER_CURRENCIES = "currencies"; //$NON-NLS-1$
	/** The key for the "currenciesNames" query parameter. */
	public static final String PARAMETER_CURRENCIES_NAMES = "currenciesNames"; //$NON-NLS-1$
	/** The key for the "startDate" query parameter. */
	public static final String PARAMETER_START_DATE = "startDate"; //$NON-NLS-1$
	/** The key for the "endDate" query parameter. */
	public static final String PARAMETER_END_DATE = "endDate"; //$NON-NLS-1$
	
	
	
	private Map<String, Object> paramsMap;

	private final GiftCertificateDetailsParameters parameters = new GiftCertificateDetailsParameters();

	private CCombo storeCombo;

	private IEpListViewer currencyList;

	private IEpDateTimePicker fromDatePicker;

	private IEpDateTimePicker toDatePicker;

	private IEpLayoutComposite parentEpComposite;

	private final ReportAuthorizationUtility reportUtility = new ReportAuthorizationUtility();

	private final List<Store> availableStores = reportUtility.getAuthorizedStores();
	
	private Currency[] supportedCurrencies;

	/**
	 * Creates the parameter controls specified by the Report.
	 * 
	 * @param toolkit
	 *            the top level toolkit which contains the Report configuration
	 *            pane
	 * @param parent
	 *            the parent composite which is the container for this specific
	 *            Report Parameters section
	 * @param site
	 *            the Workbench site, so that the composite can get a reference
	 *            to Views that it should open.
	 */
	public void createControl(final FormToolkit toolkit,
			final Composite parent, final IWorkbenchPartSite site) {
		EpState state = EpState.EDITABLE;
		if (!isAuthorized()) {
			state = EpState.DISABLED;
		}

		parentEpComposite = CompositeFactory.createGridLayoutComposite(parent, 1, false);
		final IEpLayoutData data = parentEpComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false);

		parentEpComposite.addLabelBoldRequired(GiftCertificateDetailsMessages.store, EpState.EDITABLE, null);
		storeCombo = parentEpComposite.addComboBox(state, data);

		final GridData tableViewerData = new GridData(GridData.FILL, GridData.FILL, true, true);
		tableViewerData.heightHint = TABLE_HEIGHT;
		tableViewerData.widthHint = TABLE_WIDTH;
		currencyList = parentEpComposite.addListViewer(GiftCertificateDetailsMessages.currency, true, state, data, true);
		currencyList.getSwtTableViewer().getTable().setLayoutData(tableViewerData);
		currencyList.setContentProvider(getCurrencyListContentProvider());
		currencyList.setLabelProvider(getCurrencyListLabelProvider());
		currencyList.getSwtTable().addSelectionListener(getCurrencyListSelectionListener());

		parentEpComposite.addLabelBold(GiftCertificateDetailsMessages.fromdate, null);
		fromDatePicker = parentEpComposite.addDateTimeComponent(IEpDateTimePicker.STYLE_DATE_AND_TIME, state, data);

		parentEpComposite.addLabelBoldRequired(GiftCertificateDetailsMessages.todate, EpState.EDITABLE, data);
		toDatePicker = parentEpComposite.addDateTimeComponent(IEpDateTimePicker.STYLE_DATE_AND_TIME, state, data);
		parameters.setCurrencies(null);
		
		populateControls();
	}

	/**
	 * Populates the controls.
	 */
	protected void populateControls() {		
		storeCombo.setItems(getStoreNames());
		storeCombo.setText(GiftCertificateDetailsMessages.selectStore);
		toDatePicker.setDate(new Date());
		currencyList.setInput(null);
	}
	
	private String[] getStoreNames() {
		if (CollectionUtils.isNotEmpty(availableStores)) {
			final String[] names = new String[availableStores.size()];
			for (int index = 0; index < availableStores.size(); index++) {
				names[index] = availableStores.get(index).getName();
			}
			return names;
		}
		return new String[0];
	}

	@Override
	public void bindControlsInternal(final EpControlBindingProvider bindingProvider,
			final DataBindingContext context) {

		bindStoreCombo(bindingProvider, context, true);
		bindDates(context);
	}

	private void bindStoreCombo(final EpControlBindingProvider bindingProvider,
			final DataBindingContext context,
			final boolean hideDecorationOnFirstValidation) {

		final ObservableUpdateValueStrategy storeComboUpdateStrategy = new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue,
					final Object newValue) {
				final int selectedStore = ((Integer) newValue).intValue();
				if (selectedStore >= 0) {
					final Store store = availableStores.get(selectedStore);
					supportedCurrencies = null;

					setStoreParameters(selectedStore, store);
					populateCurrenciesForSelectedStore(store);
					setCurrencyParams((StructuredSelection) currencyList.getSwtTableViewer().getSelection());
					updateButtonsStatus();
				}

				return Status.OK_STATUS;
			}

			private void setStoreParameters(final int selectedStore,
					final Store store) {
				parameters.setStoreName(storeCombo.getItem(selectedStore));
				parameters.setStoreUidPk(store.getUidPk());
			}

			private void populateCurrenciesForSelectedStore(final Store store) {
				final Collection<Currency> currencies = store.getSupportedCurrencies();
				if (CollectionUtils.isNotEmpty(currencies)) {
					supportedCurrencies = currencies.toArray(new Currency[currencies.size()]);
					sortAscendingCaseInsensitive(supportedCurrencies);
				}				
				currencyList.setInput(supportedCurrencies);
				
			}
			
			private void sortAscendingCaseInsensitive(final Currency[] currencies) {
				Arrays.sort(currencies, new Comparator<Currency>() {
					public int compare(final Currency curr1, final Currency curr2) {
						return curr1.getCurrencyCode().compareToIgnoreCase(curr2.getCurrencyCode());
					}
				});
			}
			
		};
		
		bindingProvider.bind(context, storeCombo, null, null,
				storeComboUpdateStrategy, hideDecorationOnFirstValidation);
	}

	@Override
	public boolean isInputValid() {
		boolean currencySelected = parameters.getCurrencies() != null && parameters.getCurrencies().length != 0;
		return currencySelected && super.isInputValid();
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


		fromDatePicker.bind(context, EpValidatorFactory.DATE_TIME, parameters, "startDate"); //$NON-NLS-1$

		IValidator toValidator = new CompoundValidator(new IValidator[] { EpValidatorFactory.DATE_TIME_REQUIRED,
				EpValidatorFactory.createToDateValidator(fromDatePicker, toDatePicker) });
		toDatePicker.bind(context, toValidator, parameters, "endDate"); //$NON-NLS-1$
	}

	/**
	 * Returns whether the user is authorized to view the Report.
	 * 
	 * @return <code>true</code> if the user authorized to view the Report,
	 *         <code>false</code> otherwise
	 */
	public boolean isAuthorized() {
		return AuthorizationService
				.getInstance()
				.isAuthorizedWithPermission(
						GiftCertificateDetailsReportPermissions.REPORTING_GIFT_CERTIFICATE_DETAILS_MANAGE);
	}

	/**
	 * Gets the report's parameters and stores them in a map.
	 * 
	 * @return map that stores parameter keys and values
	 */
	public Map<String, Object> getParameters() {
		final int paramsLength = 5;
		paramsMap = new LinkedHashMap<String, Object>(paramsLength); 
		paramsMap.put(PARAMETER_END_DATE, parameters.getEndDate());
		paramsMap.put(PARAMETER_START_DATE, parameters.getStartDate());
		paramsMap.put(PARAMETER_STORE_UIDPK, parameters.getStoreUidPk());
		paramsMap.put(PARAMETER_STORE, parameters.getStoreName());
		paramsMap.put(PARAMETER_CURRENCIES, parameters.getCurrencies());
		paramsMap.put(PARAMETER_CURRENCIES_NAMES, commaSeparateString(parameters.getCurrencies()));
		return paramsMap;
	}
	
	private String commaSeparateString(final String[] strings) {
		if (strings != null && strings.length > 0) {
			final StringBuilder builder = new StringBuilder();
			for (int index = 0; index < strings.length; index++) {
				builder.append(strings[index]);
				if (index < strings.length - 1) {
					builder.append(", "); //$NON-NLS-1$
				}
			}
			return builder.toString();
		}
		return null;
	}

	/**
	 * Gets the title of the report.
	 * 
	 * @return String the title of the report
	 */
	public String getReportTitle() {
		return GiftCertificateDetailsMessages.reportTitle;
	}

	private IStructuredContentProvider getCurrencyListContentProvider() {
		return new IStructuredContentProvider() {

			/** {@inheritDoc} */
			public Object[] getElements(final Object inputElement) {
				return (Currency[]) inputElement;
			}

			/** {@inheritDoc} */
			public void dispose() {
				// do nothing
			}

			/** {@inheritDoc} */
			public void inputChanged(final Viewer viewer,
					final Object oldInput, final Object newInput) {
				// do nothing
			}

		};
	}
	
	private ILabelProvider getCurrencyListLabelProvider() {
		return new LabelProvider() {
			/** {@inheritDoc} */
			@Override
			public Image getImage(final Object element) {
				return null;
			}

			/** {@inheritDoc} */
			@Override
			public String getText(final Object element) {
				return ((Currency) element).getCurrencyCode();
			}

			/** {@inheritDoc} */
			@Override
			public boolean isLabelProperty(final Object element, final String property) {
				return false;
			}
		};
	}

	private SelectionListener getCurrencyListSelectionListener() {
		return new SelectionAdapter() {
			/** {@inheritDoc} */
			public void widgetSelected(final SelectionEvent event) {
				setCurrencyParams((StructuredSelection) currencyList.getSwtTableViewer().getSelection());
				updateButtonsStatus();
			}
		};
	}
	
	private void setCurrencyParams(final StructuredSelection selection) {
		Iterator<Currency> iter = selection.iterator();
		String[] currencies = new String[selection.size()];
		int index = 0;
		while (iter.hasNext()) {
			currencies[index] = iter.next().getCurrencyCode();
			index++;
		}
		parameters.setCurrencies(currencies);
	}

	@Override
	public void refreshLayout() {
		// do nothing
	}
}
