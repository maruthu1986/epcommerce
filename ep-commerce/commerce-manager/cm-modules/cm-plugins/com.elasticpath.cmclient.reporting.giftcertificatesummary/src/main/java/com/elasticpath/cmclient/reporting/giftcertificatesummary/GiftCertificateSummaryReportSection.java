/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
/**
 * 
 */
package com.elasticpath.cmclient.reporting.giftcertificatesummary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Currency;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.ArrayUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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
import com.elasticpath.cmclient.core.validation.CompoundValidator;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.reporting.AbstractReportSection;
import com.elasticpath.cmclient.reporting.giftcertificatesummary.parameters.GiftCertificateSummaryParameters;
import com.elasticpath.cmclient.reporting.util.ReportAuthorizationUtility;
import com.elasticpath.domain.store.Store;

/**
 * A report section.
 */
@SuppressWarnings({"PMD.GodClass"})
public class GiftCertificateSummaryReportSection extends AbstractReportSection {

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
	/** The key for the parameters object. */
	public static final String PARAMETER_PARAMETERS = "parameters"; //$NON-NLS-1$

	private IEpLayoutComposite parentEpComposite;
	private CCombo storeCombo;
	private CCombo currencyCombo;

	private IEpDateTimePicker fromDatePicker;
	private IEpDateTimePicker toDatePicker;

	private final GiftCertificateSummaryParameters parameters = new GiftCertificateSummaryParameters();
	private List<Currency> supportedCurrencies;
	private final ReportAuthorizationUtility reportUtility = new ReportAuthorizationUtility();
	private List<Store> availableStores;

	private int selectedStoreIndex = -1;
	
	/** {@inheritDoc} */
	@Override
	public void bindControlsInternal(final EpControlBindingProvider bindingProvider,
			final DataBindingContext context) {
		bindStoreCombo(bindingProvider, context, true);
		bindCurrencyCombo(bindingProvider, context, true);
		bindDates(context);
	}

	/** {@inheritDoc} */
	public void createControl(final FormToolkit toolkit, final Composite parent,
			final IWorkbenchPartSite site) {
		EpState state = EpState.EDITABLE;
		if (!isAuthorized()) {
			state = EpState.DISABLED;
		}

		parentEpComposite = CompositeFactory.createGridLayoutComposite(parent, 1, false);
		final IEpLayoutData data = parentEpComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false);

		parentEpComposite.addLabelBold(GiftCertificateSummaryMessages.store, null);
		storeCombo = parentEpComposite.addComboBox(state, data);

		parentEpComposite.addLabelBold(GiftCertificateSummaryMessages.currency, null);
		final GridData tableViewerData = new GridData(GridData.FILL, GridData.FILL, true, true);
		tableViewerData.widthHint = TABLE_WIDTH;
		currencyCombo = parentEpComposite.addComboBox(state, data);
		currencyCombo.setLayoutData(tableViewerData);

		parentEpComposite.addLabelBold(GiftCertificateSummaryMessages.fromdate, null);
		fromDatePicker = parentEpComposite.addDateTimeComponent(IEpDateTimePicker.STYLE_DATE_AND_TIME, state, data);

		parentEpComposite.addLabelBoldRequired(GiftCertificateSummaryMessages.todate, EpState.EDITABLE, data);
		toDatePicker = parentEpComposite.addDateTimeComponent(IEpDateTimePicker.STYLE_DATE_AND_TIME, state, data);

		populateControls();
	}

	/** {@inheritDoc} */
	public Map<String, Object> getParameters() {
		final int paramsLength = 5;
		Map<String, Object> paramsMap = new LinkedHashMap<String, Object>(paramsLength); 
		paramsMap.put(PARAMETER_END_DATE, parameters.getEndDate());
		paramsMap.put(PARAMETER_START_DATE, parameters.getStartDate());
		paramsMap.put(PARAMETER_STORE_UIDPK, parameters.getStoreUidPkList());
		paramsMap.put(PARAMETER_STORE, getCommaSeparatedStoreNames(parameters.getStoreUidPkList()));
		paramsMap.put(PARAMETER_CURRENCIES, parameters.getCurrencies());
		if (parameters.getCurrencies() == null || parameters.getCurrencies().length == 0) {
			paramsMap.put(PARAMETER_CURRENCIES_NAMES, commaSeparateString(getAllStoresSupportedCurrenciesAsStringArray()));
		} else {
			paramsMap.put(PARAMETER_CURRENCIES_NAMES, commaSeparateString(parameters.getCurrencies()));
		}
		paramsMap.put(PARAMETER_PARAMETERS, parameters);
		return paramsMap;
	}

	private String[] getAllStoresSupportedCurrenciesAsStringArray() {
		final String[] allCurrencies = new String[supportedCurrencies.size()];
		for (int index = 0; index < supportedCurrencies.size(); index++) {
			allCurrencies[index] = supportedCurrencies.get(index).getCurrencyCode();
		}
		return allCurrencies;
	}

	/** {@inheritDoc} */
	public String getReportTitle() {
		return GiftCertificateSummaryMessages.reportTitle;
	}

	/** {@inheritDoc} */
	public boolean isAuthorized() {
		return AuthorizationService
			.getInstance()
			.isAuthorizedWithPermission(
					GiftCertificateSummaryReportPermissions.REPORTING_GIFT_CERTIFICATE_SUMMARY_MANAGE);
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

	private void bindStoreCombo(final EpControlBindingProvider bindingProvider,
			final DataBindingContext context,
			final boolean hideDecorationOnFirstValidation) {

		final ObservableUpdateValueStrategy storeComboUpdateStrategy = new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				
				int newSelectedStoreIndex = ((Integer) newValue).intValue();
				if (selectedStoreIndex == newSelectedStoreIndex) {
					return Status.OK_STATUS;
				}
				
				List<Store> selectedStores = new LinkedList<Store>();
				List<Currency> currencies = null;
				selectedStoreIndex = newSelectedStoreIndex;
				if (selectedStoreIndex == 0) {
					selectedStores.addAll(availableStores);
					currencies = getAllStoresSupportedCurrencies();
				} else {
					final Store store = availableStores.get(selectedStoreIndex - 1);
					selectedStores.add(store);
					currencies = new ArrayList<Currency>(store.getSupportedCurrencies());
				}
				supportedCurrencies = currencies;
				String[] supportedCurrencies = convertCurrencies(currencies);				
				currencyCombo.setItems(supportedCurrencies);
				currencyCombo.select(0);
				
				long[] uiIdPkList = new long[selectedStores.size()];
				int index = 0;
				for (Store store : selectedStores) {
					uiIdPkList[index++] = store.getUidPk();
				}
				parameters.setStoreUidPkList(uiIdPkList);
				parameters.setCurrencies(null);

				updateButtonsStatus();
				return Status.OK_STATUS;
			}

		};
		bindingProvider.bind(context, storeCombo, null, null,
				storeComboUpdateStrategy, hideDecorationOnFirstValidation);

	}
	
	private List<Currency> getAllStoresSupportedCurrencies() {
		
		Set<Currency> currencies = new TreeSet<Currency>(new Comparator<Currency>() {
			/**
			 * {@inheritDoc}
			 */
			public int compare(final Currency curr1, final Currency curr2) {
				if (curr1 == null || curr2 == null) {
					return 1;
				}
				return curr1.getCurrencyCode().compareTo(curr2.getCurrencyCode());
			}
			
		});
		for (Store currStore : availableStores) {
			currencies.addAll(currStore.getSupportedCurrencies());
		}
		return new ArrayList<Currency>(currencies);
	}
	

	private void bindCurrencyCombo(final EpControlBindingProvider bindingProvider,
			final DataBindingContext context,
			final boolean hideDecorationOnFirstValidation) {

		final ObservableUpdateValueStrategy storeComboUpdateStrategy = new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				int selectedIndex = currencyCombo.getSelectionIndex();
				if (selectedIndex == 0) {
					parameters.setCurrencies(null);
				} else {
					Currency currency = supportedCurrencies.get(currencyCombo.getSelectionIndex() - 1);
					parameters.setCurrencies(new String[] { currency.getCurrencyCode() });
				}

				return Status.OK_STATUS;
			}
		};
		bindingProvider.bind(context, currencyCombo, null, null,
				storeComboUpdateStrategy, hideDecorationOnFirstValidation);
	}
	
	private String[] convertCurrencies(final List<Currency> currenciesList) {
		String[] result = new String[currenciesList.size() + 1];
		result[0] = GiftCertificateSummaryMessages.selectCurrency;
		sortCurrencies(currenciesList);
		int index = 1;
		for (Currency currency : currenciesList) {
			result[index++] = currency.getCurrencyCode();
		}
		return result;
	}

	private void sortCurrencies(final List<Currency> currenciesList) {
		Collections.sort(currenciesList, new Comparator<Currency>() {
			public int compare(final Currency curr1, final Currency curr2) {
				return curr1.getCurrencyCode().compareToIgnoreCase(curr2.getCurrencyCode());
			}
		});
	}

	/**
	 * Populates the controls.
	 */
	protected void populateControls() {
		availableStores = reportUtility.getAuthorizedStores();
		storeCombo.setItems(getStoreNames());
		storeCombo.add(GiftCertificateSummaryMessages.selectStore, 0);
		storeCombo.select(0);
		toDatePicker.setDate(new Date());
		currencyCombo.setItems(convertCurrencies(getAllStoresSupportedCurrencies()));
		currencyCombo.select(0);
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
	
	/**
	 * Builds comma separated store names string for given store uids.
	 * 
	 * @param uidPks store uids
	 * @return store names comma separated string
	 */
	String getCommaSeparatedStoreNames(final long[] uidPks) {
		if (CollectionUtils.isNotEmpty(availableStores) 
				&& !ArrayUtils.isEmpty(uidPks)) {
			List<Store> storeSearchResult = listStoresWithUidPks(uidPks);
			return buildCommaSeparatedStoreNamesString(storeSearchResult);
		}
		return null;
	}

	private String buildCommaSeparatedStoreNamesString(final List<Store> storeSearchResult) {
		final Iterator<Store> iter = storeSearchResult.iterator();
		final StringBuilder builder = new StringBuilder();
		
		while (iter.hasNext()) {
			Store currStore = iter.next();
			builder.append(currStore.getName());
			if (iter.hasNext()) {
				builder.append(", "); //$NON-NLS-1$
			}
		}
		
		return builder.toString();
	}

	private List<Store> listStoresWithUidPks(final long[] uidPks) {
		final List<Store> storeSearchResult = new ArrayList<Store>();
		for (final long uidPk : uidPks) {
			Store store = (Store) CollectionUtils.find(availableStores, new Predicate() {
				public boolean evaluate(final Object object) {
					Store store = (Store) object;
					return store.getUidPk() == uidPk;
				}
			});
			if (store != null) {
				storeSearchResult.add(store);
			}	
		}
		return storeSearchResult;
	}

	private String commaSeparateString(final String[] strings) {
		Arrays.sort(strings);
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
	 * Sets available stores list for testing purposes only.
	 * 
	 * @param availableStores available stores list
	 */
	void setAvailableStores(final List<Store> availableStores) {
		this.availableStores = availableStores;
	}

	@Override
	public void refreshLayout() {
		// not used
	}
}
