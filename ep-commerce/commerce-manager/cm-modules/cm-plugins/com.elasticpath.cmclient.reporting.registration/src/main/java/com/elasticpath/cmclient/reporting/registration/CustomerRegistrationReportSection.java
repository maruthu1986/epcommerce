/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.reporting.registration;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Font;
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
import com.elasticpath.cmclient.core.helpers.EPWidgetIdUtil;
import com.elasticpath.cmclient.core.helpers.EPTestUtilFactory;
import com.elasticpath.cmclient.core.validation.CompoundValidator;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.reporting.AbstractReportSection;
import com.elasticpath.cmclient.reporting.registration.parameters.CustomerRegistrationParameters;
import com.elasticpath.cmclient.reporting.util.ReportAuthorizationUtility;

/**
 * Represents the UI for the customer registration report parameter section.
 */
public class CustomerRegistrationReportSection extends AbstractReportSection {

	private CCombo storeCombo;

	private IEpDateTimePicker fromDatePicker;

	private IEpDateTimePicker toDatePicker;

	private Button isAnonymousRegiOnly;

	private Map<String, Object> paramsMap;

	private final CustomerRegistrationParameters customerRegistrationParameters = new CustomerRegistrationParameters();

	private final ReportAuthorizationUtility reportUtility = new ReportAuthorizationUtility();

	/** Localized string indicating all stores. */
	public static final String ALL_STORES_STRING = CustomerRegistrationReportMessages.allStores;
	/** The key for the "store" query parameter. */
	public static final String PARAMETER_STORE = "store"; //$NON-NLS-1$
	/** The key for the "startDate" query parameter. */
	public static final String PARAMETER_START_DATE = "startDate"; //$NON-NLS-1$
	/** The key for the "endDate" query parameter. */
	public static final String PARAMETER_END_DATE = "endDate"; //$NON-NLS-1$
	/** The key for the "anonymous registration" query parameter. */
	public static final String PARAMETER_ANONYMOUS_REGISTRATION = "anonymousregistration"; //$NON-NLS-1$

	private IEpLayoutComposite parentEpComposite;

	/**
	 * Creates the parameter controls specified by the Report.
	 * 
	 * @param toolkit the top level toolkit which contains the Report configuration pane
	 * @param parent the parent composite which is the container for this specific Report Parameters section
	 * @param site the Workbench site, so that the composite can get a reference to Views that it should open.
	 */
	public void createControl(final FormToolkit toolkit, final Composite parent, final IWorkbenchPartSite site) {
		EpState state = EpState.EDITABLE;
		if (!isAuthorized()) {
			state = EpState.DISABLED;
		}

		parentEpComposite = CompositeFactory.createGridLayoutComposite(parent, 1, false);

		final IEpLayoutData data = parentEpComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false);

		parentEpComposite.addLabelBold(CustomerRegistrationReportMessages.store, null);
		storeCombo = parentEpComposite.addComboBox(state, data);

		parentEpComposite.addLabelBold(CustomerRegistrationReportMessages.fromdate, null);
		fromDatePicker = parentEpComposite.addDateTimeComponent(IEpDateTimePicker.STYLE_DATE_AND_TIME, state, data);

		parentEpComposite.addLabelBoldRequired(CustomerRegistrationReportMessages.todate, EpState.EDITABLE, data);
		toDatePicker = parentEpComposite.addDateTimeComponent(IEpDateTimePicker.STYLE_DATE_AND_TIME, state, data);

		isAnonymousRegiOnly = parentEpComposite.addCheckBoxButton(CustomerRegistrationReportMessages.anonymous_registration, state, data);
		EPCustomThemeUtil.setCustomStyle(isAnonymousRegiOnly, EpWidgetIdUtil.CustomStyle.BOLD, false);

		populateControls();
	}

	@Override
	public void bindControlsInternal(final EpControlBindingProvider bindingProvider, final DataBindingContext context) {
		final boolean hideDecorationOnFirstValidation = true;

		//
		// STORE COMBO BOX - bind the StoreName
		//
		final ObservableUpdateValueStrategy storeComboUpdateStrategy = new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				int newIndex = ((Integer) newValue).intValue();
				customerRegistrationParameters.setStoreName(storeCombo.getItem(newIndex));
				return Status.OK_STATUS;
			}
		};
		bindingProvider.bind(context, storeCombo, null, null, storeComboUpdateStrategy, hideDecorationOnFirstValidation);

		bindDates(context);

		//
		// ANONYMOUS REGISTRATIONS CHECKBOX - bind the boolean
		//
		bindingProvider.bind(context, isAnonymousRegiOnly, customerRegistrationParameters,
				"anonymousRegistration", null, null, hideDecorationOnFirstValidation); //$NON-NLS-1$
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

		fromDatePicker.bind(context, EpValidatorFactory.DATE_TIME, customerRegistrationParameters, "startDate"); //$NON-NLS-1$

		IValidator toValidator = new CompoundValidator(new IValidator[] { EpValidatorFactory.DATE_TIME_REQUIRED,
				EpValidatorFactory.createToDateValidator(fromDatePicker, toDatePicker) });
		toDatePicker.bind(context, toValidator, customerRegistrationParameters, "endDate"); //$NON-NLS-1$
	}

	/**
	 * Populates the controls.
	 */
	protected void populateControls() {
		storeCombo.setItems(reportUtility.getAuthorizedStoreNames().toArray(new String[0]));
		storeCombo.add(ALL_STORES_STRING, 0);
		storeCombo.select(0);
		toDatePicker.setDate(new Date());
	}

	/**
	 * Returns whether the user is authorized to view the Report.
	 * 
	 * @return <code>true</code> if the user authorized to view the Report, <code>false</code> otherwise
	 */
	public boolean isAuthorized() {
		return AuthorizationService.getInstance().isAuthorizedWithPermission(
				CustomerRegistrationReportPermissions.REPORTING_CUSTOMER_REGISTRATION_MANAGE);
	}

	/**
	 * Gets the report's parameters and stores them in a map.
	 * 
	 * Customer Registration Queries have four parameters:
	 * <ol>
	 * <li>Start Date ({@link java.util.Date} object) - may be null</li>
	 * <li>End Date ({@link java.util.Date} object) - will not be null</li>
	 * <li>Store Name {@link List} of store names</li>
	 * <li>Anonymous Registration ({@link Boolean} signifying whether anonymous customer registrations should be
	 * included in the query results</li>
	 * </ol>
	 * 
	 * @return map that stores parameter keys and values
	 */
	public Map<String, Object> getParameters() {
		paramsMap = new LinkedHashMap<String, Object>();

		paramsMap.put(PARAMETER_END_DATE, customerRegistrationParameters.getEndDate());

		paramsMap.put(PARAMETER_START_DATE, customerRegistrationParameters.getStartDate());
		
		List<String> storeNames = new ArrayList<String>();
		String storeNameParameter = customerRegistrationParameters.getStoreName();
		if (ALL_STORES_STRING.equals(storeNameParameter)) {
			storeNames.addAll(reportUtility.getAuthorizedStoreNames());
		} else {
			storeNames.add(storeNameParameter);
		}
		paramsMap.put(PARAMETER_STORE, storeNames);
		
		paramsMap.put(PARAMETER_ANONYMOUS_REGISTRATION, customerRegistrationParameters.isAnonymousRegistration());

		return paramsMap;
	}

	/**
	 * Gets the title of the report.
	 * 
	 * @return String the title of the report
	 */
	public String getReportTitle() {
		return CustomerRegistrationReportMessages.reportTitle;
	}

	@Override
	public void refreshLayout() {
		// do nothing
	}
}
