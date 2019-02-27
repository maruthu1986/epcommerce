/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
/**
 * 
 */
package com.elasticpath.cmclient.reporting.customerpersonaldata;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.reporting.AbstractReportSection;
import com.elasticpath.cmclient.reporting.common.SavedReportParameters;
import com.elasticpath.cmclient.reporting.customerpersonaldata.parameters.CustomerPersonalDataParameters;
import com.elasticpath.cmclient.reporting.util.ReportAuthorizationUtility;
import com.elasticpath.domain.store.Store;

/**
 * A report section.
 *
 */
@SuppressWarnings({"PMD.GodClass"})
public class CustomerPersonalDataReportSection extends AbstractReportSection {

	/** The key for the "storeUidPk" query parameter. */
	public static final String PARAMETER_STORE_UIDPK = "storeUidPk"; //$NON-NLS-1$
	/** The key for the "store" query parameter. */
	public static final String PARAMETER_STORE = "store"; //$NON-NLS-1$
	/** The key for the "userId" query parameter. */
	public static final String PARAMETER_USER_ID = "userId"; //$NON-NLS-1$

	/** The key for the parameters object. */
	public static final String PARAMETER_PARAMETERS = "parameters"; //$NON-NLS-1$

	private CCombo storeCombo;

	private Text userIdText;

	private final CustomerPersonalDataParameters parameters = new CustomerPersonalDataParameters();
	private final ReportAuthorizationUtility reportUtility = new ReportAuthorizationUtility();
	private List<Store> availableStores;

	private int selectedStoreIndex = -1;

	/**
	 * Default constructor.
	 */
	public CustomerPersonalDataReportSection() {
		super();
	}


	@Override
	public void bindControlsInternal(final EpControlBindingProvider bindingProvider,
			final DataBindingContext context) {

		bindStoreCombo(bindingProvider, context, true);
		bindUserId(bindingProvider, context);

		ModifyListener modifyListener = getModifyListener();

		userIdText.addModifyListener(modifyListener);
		storeCombo.addModifyListener(modifyListener);
	}

	@Override
	public void createControl(final FormToolkit toolkit, final Composite parent,
			final IWorkbenchPartSite site) {

		EpState state = EpState.EDITABLE;
		if (!isAuthorized()) {
			state = EpState.DISABLED;
		}

		IEpLayoutComposite parentEpComposite = CompositeFactory.createGridLayoutComposite(parent, 1, false);
		parentEpComposite.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		final IEpLayoutData data = parentEpComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false);

		parentEpComposite.addLabelBoldRequired(CustomerPersonalDataMessages.get().store, state, null);
		storeCombo = parentEpComposite.addComboBox(state, data);

		parentEpComposite.addLabelBoldRequired(CustomerPersonalDataMessages.get().userId,  EpState.EDITABLE, data);
		userIdText = parentEpComposite.addTextField(state, data);

		populateControls();
	}

	@Override
	public Map<String, Object> getParameters() {
		final int paramsLength = 11;

		Map<String, Object> paramsMap = new LinkedHashMap<>(paramsLength);

		paramsMap.put(PARAMETER_STORE_UIDPK, parameters.getStoreUidPk());
		paramsMap.put(PARAMETER_STORE, parameters.getStore());

		paramsMap.put(PARAMETER_USER_ID, parameters.getUserId());

		paramsMap.put(PARAMETER_PARAMETERS, parameters);
		return paramsMap;
	}

	@Override
	public String getReportTitle() {
		return CustomerPersonalDataMessages.get().reportTitle;
	}

	@Override
	public boolean isAuthorized() {
		return AuthorizationService
			.getInstance()
			.isAuthorizedWithPermission(
				CustomerPersonalDataReportPermissions.REPORTING_CUSTOMER_PERSONAL_DATA_MANAGE);
	}

	private void bindUserId(final EpControlBindingProvider bindingProvider, final DataBindingContext context) {


		this.userIdText.addModifyListener(getModifyListener());

		bindingProvider.bind(context, this.userIdText, parameters, PARAMETER_USER_ID, EpValidatorFactory.REQUIRED, null, true); //$NON-NLS-1$
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

				selectedStoreIndex = newSelectedStoreIndex;
				if (selectedStoreIndex > 0) {
					final Store store = availableStores.get(selectedStoreIndex - 1);
					parameters.setStore(store.getCode());
					parameters.setStoreUidPk(store.getUidPk());
				}
				return Status.OK_STATUS;
			}

		};
		bindingProvider.bind(context, storeCombo, EpValidatorFactory.REQUIRED, null, storeComboUpdateStrategy, hideDecorationOnFirstValidation);

	}

	/**
	 * Populates the controls.
	 */
	protected void populateControls() {
		availableStores = reportUtility.getAuthorizedStores();
		storeCombo.setItems(getStoreNames());
		storeCombo.select(0);

		restoreSavedParameters();
	}

	private void restoreSavedParameters() {
		final SavedReportParameters savedParameters = SavedReportParameters.getInstance();
		selectedStoreIndex = savedParameters.restoreStoreSelection(storeCombo, availableStores, parameters);
		if (selectedStoreIndex > 0) {
			final Store store = availableStores.get(selectedStoreIndex - 1);
			parameters.setStoreUidPk(store.getUidPk());
		}
		updateButtonsStatus();
	}

	@Override
	public boolean isInputValid() {
		boolean storeSelected = storeCombo.getSelectionIndex() > 0;
		return storeSelected && StringUtils.isNotBlank(userIdText.getText()) && super.isInputValid();
	}

	private String[] getStoreNames() {
		if (CollectionUtils.isNotEmpty(availableStores)) {
			final String[] names = new String[availableStores.size() + 1];
			names[0] = CustomerPersonalDataMessages.get().selectStore;
			for (int index = 0; index < availableStores.size(); index++) {
				names[index + 1] = availableStores.get(index).getName();
			}
			return names;
		}
		return new String[0];
	}

	/**
	 * Sets available stores list.
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

	private ModifyListener getModifyListener() {
		return (event) -> updateButtonsStatus();
	}
}
