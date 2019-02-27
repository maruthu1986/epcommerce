/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.admin.stores.editors;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;

import com.elasticpath.cmclient.admin.stores.AdminStoresMessages;
import com.elasticpath.cmclient.admin.stores.util.EpUrlValidator;
import com.elasticpath.cmclient.admin.stores.util.TimeZonesUtil;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.editors.AbstractCmClientEditorPageSectionPart;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.helpers.store.StoreEditorModel;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.EpCountrySelectorControl;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.CompoundValidator;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;

/**
 * UI representation of the Store Summary Overview Section.
 */
public class StoreSummaryOverviewSectionPart extends AbstractCmClientEditorPageSectionPart {

	private static final int STORE_URL_TEXT_LIMIT = 255;

	private static final int STORE_NAME_TEXT_LIMIT = 255;

	private static final int STORE_CODE_TEXT_LIMIT = 64;

	private static final int DESCRIPTION_AREA_HEIGHT = 75;

	private Text storeCodeText;

	private Text storeNameText;

	private Text storeDescriptionText;

	private Text storeUrlText;

	private Text storeStateText;

	private CCombo timeZoneCombo;

	private Button enableDataPoliciesCheckbox;

	private IEpLayoutComposite controlPane;

	/** instance of <code>EpCountrySelectorControl</code> which manages logic of State and Country combo. */
	private final EpCountrySelectorControl stateCountryManager;

	private final Map<String, String> availableTimeZonesMap;
	
	private final boolean authorized;

	/**
	 * Constructor.
	 * 
	 * @param editor the editor containing this Section Constructor to create a new Section in an editor's FormPage.
	 * @param formPage the form page
	 * @param authorized whether the current user is authorized to edit the store
	 */
	public StoreSummaryOverviewSectionPart(final FormPage formPage, final AbstractCmClientFormEditor editor, final boolean authorized) {
		super(formPage, editor, ExpandableComposite.NO_TITLE);
		this.authorized = authorized;
		stateCountryManager = new EpCountrySelectorControl();
		availableTimeZonesMap = TimeZonesUtil.getAvailableTimeZones(CorePlugin.getDefault().getDefaultLocale());
	}

	@Override
	protected void bindControls(final DataBindingContext bindingContext) {
		final boolean hideFirstValidationErrors = true;
		final EpControlBindingProvider binder = EpControlBindingProvider.getInstance();
		binder.bind(bindingContext, storeCodeText, getStoreEditorModel(), "code", //$NON-NLS-1$
				EpValidatorFactory.getRequiredFieldValidatorInstanceWithCustomMessage(AdminStoresMessages.get().StoreCodeRequired), null,
				hideFirstValidationErrors);
		binder.bind(bindingContext, storeNameText, getStoreEditorModel(), "name", //$NON-NLS-1$ 
				EpValidatorFactory.getRequiredFieldValidatorInstanceWithCustomMessage(AdminStoresMessages.get().StoreNameRequired), null,
				hideFirstValidationErrors);
		binder.bind(bindingContext, storeDescriptionText, getStoreEditorModel(), "description", null, null, hideFirstValidationErrors); //$NON-NLS-1$

		CompoundValidator urlValidator = new CompoundValidator(new IValidator[] { new EpUrlValidator() });
		if (!getStoreEditorModel().getStoreState().isIncomplete()) {
			urlValidator.addValidator(EpValidatorFactory.REQUIRED);
		}

		binder.bind(bindingContext, storeUrlText, getStoreEditorModel(), "url", //$NON-NLS-1$ 
				urlValidator, null, hideFirstValidationErrors);

		final ObservableUpdateValueStrategy timezoneUpdateStrategy = new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				getStoreEditorModel().setTimeZone(TimeZone.getTimeZone((String) timeZoneCombo.getData(timeZoneCombo.getText())));
				return Status.OK_STATUS;
			}
		};
		binder.bind(bindingContext, timeZoneCombo, null, null, timezoneUpdateStrategy, hideFirstValidationErrors);

		final ObservableUpdateValueStrategy countryUpdateStrategy = new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				String selectedCountry = stateCountryManager.getCountryComboItem();
				getStoreEditorModel().setCountry(selectedCountry);
				return Status.OK_STATUS;
			}
		};
		binder.bind(bindingContext, stateCountryManager.getCountryCombo(), null, null, countryUpdateStrategy, hideFirstValidationErrors);

		final ObservableUpdateValueStrategy subCountryUpdateStrategy = new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				String selectedState = stateCountryManager.getStateComboItem();
				getStoreEditorModel().setSubCountry(selectedState);
				return Status.OK_STATUS;
			}
		};

		binder.bind(bindingContext, stateCountryManager.getStateCombo(), null, null, subCountryUpdateStrategy, hideFirstValidationErrors);

		binder.bind(bindingContext, enableDataPoliciesCheckbox, getStoreEditorModel(), "storeEnableDataPoliciesSettingEnabled"); //$NON-NLS-1$
	}

	@Override
	protected void createControls(final Composite client, final FormToolkit toolkit) {
		controlPane = CompositeFactory.createTableWrapLayoutComposite(client, 2, false);

		final IEpLayoutData labelData = controlPane.createLayoutData(IEpLayoutData.END, IEpLayoutData.CENTER);
		final IEpLayoutData fieldData = controlPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.BEGINNING, true, false);

		controlPane.addLabelBoldRequired(AdminStoresMessages.get().StoreCode, getEditableStateOfStoreCode(), labelData);
		storeCodeText = controlPane.addTextField(getEditableStateOfStoreCode(), fieldData);

		storeCodeText.setTextLimit(STORE_CODE_TEXT_LIMIT);

		controlPane.addLabelBoldRequired(AdminStoresMessages.get().StoreName, EpState.EDITABLE, labelData);
		storeNameText = controlPane.addTextField(getEditableState(), fieldData);
		storeNameText.setTextLimit(STORE_NAME_TEXT_LIMIT);

		controlPane.addLabelBold(AdminStoresMessages.get().StoreDescription,
				controlPane.createLayoutData(IEpLayoutData.END, IEpLayoutData.BEGINNING));
		storeDescriptionText = controlPane.addTextArea(true, false, getEditableState(), 
				controlPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true));
		((TableWrapData) storeDescriptionText.getLayoutData()).heightHint = DESCRIPTION_AREA_HEIGHT;

		controlPane.addLabelBoldRequired(AdminStoresMessages.get().StoreUrl, getEditableState(), labelData);
		storeUrlText = controlPane.addTextField(getEditableState(), fieldData);
		storeUrlText.setTextLimit(STORE_URL_TEXT_LIMIT);

		controlPane.addLabelBoldRequired(AdminStoresMessages.get().StoreState, EpState.READ_ONLY, labelData);
		storeStateText = controlPane.addTextField(EpState.READ_ONLY, fieldData);

		controlPane.addLabelBoldRequired(AdminStoresMessages.get().StoreTimeZone, EpState.EDITABLE, labelData);
		timeZoneCombo = controlPane.addComboBox(getEditableState(), fieldData);

		controlPane.addLabelBoldRequired(AdminStoresMessages.get().StoreCountry, EpState.EDITABLE, labelData);
		stateCountryManager.setCountryCombo(controlPane.addComboBox(getEditableState(), fieldData));

		controlPane.addLabelBoldRequired(AdminStoresMessages.get().StoreSubCountry, EpState.EDITABLE, labelData);
		stateCountryManager.setStateCombo(controlPane.addComboBox(getEditableState(), fieldData));

		controlPane.addLabelBold(AdminStoresMessages.get().EnableDataPolicies,
				controlPane.createLayoutData(IEpLayoutData.END, IEpLayoutData.BEGINNING));
		enableDataPoliciesCheckbox = controlPane.addCheckBoxButton(AdminStoresMessages.EMPTY_STRING, EpState.EDITABLE, null);

		stateCountryManager.initStateCountryCombo(getEditableState());
	}
	
	/**
	 * If a required label is editable then its text will be preceded by an asterisk.
	 * If a store is in a COMPLETE state then the store code cannot be edited so there's no
	 * point to having an asterisk in the label.
	 * @return {@code EpState.EDITABLE} if the label should indicate that its field is required,
	 * else {@code EpState.READONLY}
	 */
	private EpState getEditableStateOfStoreCode() {
		if (isStoreComplete()) {
			return EpState.READ_ONLY;
		}
		return EpState.EDITABLE;
	}
	
	/**
	 * @return true if the store is complete, false if not.
	 */
	boolean isStoreComplete() {
		return !getStoreEditorModel().getStoreState().isIncomplete();
	}
	
	@Override
	protected void populateControls() {
		for (final Entry<String, String> entry : availableTimeZonesMap.entrySet()) {
			timeZoneCombo.setData(entry.getValue(), entry.getKey());
			timeZoneCombo.add(entry.getValue());
		}

		stateCountryManager.populateStateCountryCombo();
		stateCountryManager.selectCountryCombo(getStoreEditorModel().getCountry());

		if (isEditStore()) {
			storeCodeText.setText(getStoreEditorModel().getCode());
			storeNameText.setText(getStoreEditorModel().getName());
			if (getStoreEditorModel().getDescription() != null) {
				storeDescriptionText.setText(getStoreEditorModel().getDescription());
			}
			if (getStoreEditorModel().getUrl() != null) {
				storeUrlText.setText(getStoreEditorModel().getUrl());
			}
			timeZoneCombo.setText(getTimeZoneId());
			stateCountryManager.selectStateCombo(getStoreEditorModel().getSubCountry());
		} else {
			timeZoneCombo.setText(TimeZonesUtil.getTimeZoneName(0, CorePlugin.getDefault().getDefaultLocale()));
			stateCountryManager.selectCountryCombo(0);
		}

		storeStateText.setText(AdminStoresMessages.get().getMessage(getStoreEditorModel().getStoreState().getNameMessageKey()));

		enableDataPoliciesCheckbox.setSelection(getStoreEditorModel().isStoreEnableDataPoliciesSettingEnabled());

		controlPane.setControlModificationListener(getEditor());
	}

	private String getTimeZoneId() {
		String timeZoneId = availableTimeZonesMap.get(getStoreEditorModel().getTimeZone().getID());
		if (timeZoneId == null) {
			timeZoneId = TimeZonesUtil.getTimeZoneName(0, CorePlugin.getDefault().getDefaultLocale());
		}
		return timeZoneId;
	}

	@Override
	protected String getSectionTitle() {
		return AdminStoresMessages.get().StoreOverview_Title;
	}

	/*
	 * @return true if it is edit dialog
	 */
	private boolean isEditStore() {
		return getStoreEditorModel().isPersistent();
	}

	/*
	 * @return current store
	 */
	private StoreEditorModel getStoreEditorModel() {
		return (StoreEditorModel) getEditor().getModel();
	}
	
	/**
	 * @return true if the current user is authorized to edit the current store, false if not.
	 */
	boolean isCurrentUserAuthorized() {
		return authorized;
	}
	
	/**
	 * @return {@code EpState.EDITABLE} if a control should be editable, else {@code EpState.READ_ONLY}.
	 */
	EpState getEditableState() {
		if (isCurrentUserAuthorized()) {
			return EpState.EDITABLE;
		}
		return EpState.READ_ONLY;
	}
}
