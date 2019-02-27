/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
/**
 * 
 */
package com.elasticpath.cmclient.reporting.promotionusage;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
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
import com.elasticpath.cmclient.core.util.DateTimeUtilFactory;
import com.elasticpath.cmclient.core.validation.CompoundValidator;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.reporting.AbstractReportSection;
import com.elasticpath.cmclient.reporting.common.SavedReportParameters;
import com.elasticpath.cmclient.reporting.promotionusage.parameters.PromotionUsageParameters;
import com.elasticpath.cmclient.reporting.util.ReportAuthorizationUtility;
import com.elasticpath.domain.rules.RuleScenarios;
import com.elasticpath.domain.store.Store;

/**
 * A report section.
 *
 */
@SuppressWarnings({"PMD.GodClass"})
public class PromotionUsageReportSection extends AbstractReportSection {

	/** The key for the "storeUidPk" query parameter. */
	public static final String PARAMETER_STORE_UIDPK = "storeUidPk"; //$NON-NLS-1$
	/** The key for the "store" query parameter. */
	public static final String PARAMETER_STORE = "store"; //$NON-NLS-1$
	/** The key for the "startDate" query parameter. */
	public static final String PARAMETER_START_DATE = "startDate"; //$NON-NLS-1$
	/** The key for the "endDate" query parameter. */
	public static final String PARAMETER_END_DATE = "endDate"; //$NON-NLS-1$
	/** The key for the parameters object. */
	public static final String PARAMETER_PARAMETERS = "parameters"; //$NON-NLS-1$

	private CCombo storeCombo;

	private IEpDateTimePicker fromDatePicker;
	private IEpDateTimePicker toDatePicker;

	private final PromotionUsageParameters parameters = new PromotionUsageParameters();
	private final ReportAuthorizationUtility reportUtility = new ReportAuthorizationUtility();
	private List<Store> availableStores;

	private int selectedStoreIndex = -1;

	private Button onlyPromotionsWithCouponCodes;
	
	/**
	 * Default constructor.
	 */
	public PromotionUsageReportSection() {
		super();
	}


	@Override
	public void bindControlsInternal(final EpControlBindingProvider bindingProvider,
			final DataBindingContext context) {
		bindStoreCombo(bindingProvider, context, true);
		bindDates(context);

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
		
		bindingProvider.bind(context, onlyPromotionsWithCouponCodes, parameters, 
				"onlyPromotionsWithCouponCodes", null, null, true); //$NON-NLS-1$
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

		parentEpComposite.addLabelBoldRequired(PromotionUsageMessages.get().store, state, null);
		storeCombo = parentEpComposite.addComboBox(state, data);

		parentEpComposite.addLabelBoldRequired(PromotionUsageMessages.get().fromdate, state, null);
		fromDatePicker = parentEpComposite.addDateTimeComponent(IEpDateTimePicker.STYLE_DATE_AND_TIME, state, data);

		parentEpComposite.addLabelBoldRequired(PromotionUsageMessages.get().todate, EpState.EDITABLE, data);
		toDatePicker = parentEpComposite.addDateTimeComponent(IEpDateTimePicker.STYLE_DATE_AND_TIME, state, data);

		onlyPromotionsWithCouponCodes = parentEpComposite.addCheckBoxButton(PromotionUsageMessages.get().onlyWithCoupons, state, data);

		populateControls();
	}

	@Override
	public Map<String, Object> getParameters() {
		final int paramsLength = 5;
		Map<String, Object> paramsMap = new LinkedHashMap<>(paramsLength);
		paramsMap.put(PARAMETER_END_DATE, DateTimeUtilFactory.getDateUtil().formatAsDateTime(parameters.getEndDate()));
		paramsMap.put(PARAMETER_START_DATE, DateTimeUtilFactory.getDateUtil().formatAsDateTime(parameters.getStartDate()));
		paramsMap.put(PARAMETER_STORE_UIDPK, parameters.getStoreUidPk());
		paramsMap.put(PARAMETER_STORE, parameters.getStore());
		paramsMap.put(PARAMETER_PARAMETERS, parameters);
		return paramsMap;
	}

	@Override
	public String getReportTitle() {
		return PromotionUsageMessages.get().reportTitle;
	}

	@Override
	public boolean isAuthorized() {
		return AuthorizationService
			.getInstance()
			.isAuthorizedWithPermission(
					PromotionUsageReportPermissions.REPORTING_PROMOTION_USAGE_MANAGE);
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

				selectedStoreIndex = newSelectedStoreIndex;
				if (selectedStoreIndex > 0) {
					final Store store = availableStores.get(selectedStoreIndex - 1);
					parameters.setStore(store.getCode());
					parameters.setStoreUidPk(store.getUidPk());
				}
				return Status.OK_STATUS;

			}

		};
		bindingProvider.bind(context, storeCombo, null, null, storeComboUpdateStrategy, hideDecorationOnFirstValidation);

	}
	
	/**
	 * Populates the controls.
	 */
	protected void populateControls() {
		availableStores = reportUtility.getAuthorizedStores();
		storeCombo.setItems(getStoreNames());
		storeCombo.select(0);
		toDatePicker.setDate(new Date());

		parameters.setPromotionType(RuleScenarios.CART_SCENARIO);

		restoreSavedParameters();
	}

	private void restoreSavedParameters() {
		final SavedReportParameters savedParameters = SavedReportParameters.getInstance();
		selectedStoreIndex = savedParameters.restoreStoreSelection(storeCombo, availableStores, parameters);
		if (selectedStoreIndex > 0) {
			final Store store = availableStores.get(selectedStoreIndex - 1);
			parameters.setStoreUidPk(store.getUidPk());
		}
		savedParameters.restoreStartDate(fromDatePicker);
		savedParameters.restoreEndDate(toDatePicker);
		updateButtonsStatus();
	}

	@Override
	public boolean isInputValid() {
		boolean storeSelected = storeCombo.getSelectionIndex() > 0;
		return storeSelected && super.isInputValid();
	}

	private String[] getStoreNames() {
		if (CollectionUtils.isNotEmpty(availableStores)) {
			final String[] names = new String[availableStores.size() + 1];
			names[0] = PromotionUsageMessages.get().selectStore;
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
}
