/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.cmclient.admin.datapolicies.editors.sections;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.conversion.Converter;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;

import com.elasticpath.cmclient.admin.datapolicies.AdminDataPoliciesMessages;
import com.elasticpath.cmclient.admin.datapolicies.editors.DataPolicyEditor;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.editors.AbstractCmClientEditorPageSectionPart;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.ControlModificationListener;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpDateTimePicker;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.CompoundValidator;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.domain.datapolicy.DataPolicy;
import com.elasticpath.domain.datapolicy.DataPolicyState;
import com.elasticpath.domain.datapolicy.RetentionType;

/**
 * Customer Data Policy summary section.
 */
public class DataPolicySummarySection extends AbstractCmClientEditorPageSectionPart {

	private static final int DESCRIPTION_AREA_HEIGHT = 75;
	private static final Integer DEFAULT_SELECTION = 0;

	private final ControlModificationListener listener;

	private IEpLayoutComposite mainPane;

	private Text dataPolicyNameField;

	private Text dataPolicyReferenceKeyField;

	private CCombo dataPolicyRetentionType;

	private Text dataPolicyRetentionPeriodInDays;

	private CCombo dataPolicyState;

	private IEpDateTimePicker dataPolicyStartDate;

	private IEpDateTimePicker dataPolicyEndDate;

	private Text dataPolicyDescriptionField;

	private Text activitiesField;

	/**
	 * Constructor.
	 *
	 * @param formPage the form page
	 * @param editor   the editor
	 */
	public DataPolicySummarySection(final FormPage formPage, final AbstractCmClientFormEditor editor) {
		super(formPage, editor, ExpandableComposite.NO_TITLE);
		this.listener = editor;
	}

	@Override
	protected String getSectionTitle() {
		return AdminDataPoliciesMessages.get().DataPolicyEditor_SummaryPage_Details;
	}

	@Override
	protected void createControls(final Composite parent, final FormToolkit toolkit) {
		mainPane = CompositeFactory.createTableWrapLayoutComposite(parent, 2, false);

		final IEpLayoutData labelData = mainPane.createLayoutData(IEpLayoutData.END, IEpLayoutData.CENTER);
		final IEpLayoutData fieldData = mainPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, false, false);

		EpState state = isEditableMode() ? EpState.EDITABLE : EpState.DISABLED;

		mainPane.addLabelBoldRequired(AdminDataPoliciesMessages.get().DataPolicyEditor_SummaryPage_NameField, state, labelData);
		dataPolicyNameField = mainPane.addTextField(state, fieldData);

		mainPane.addLabelBoldRequired(AdminDataPoliciesMessages.get().DataPolicyEditor_SummaryPage_ReferenceKeyField, state, labelData);
		dataPolicyReferenceKeyField = mainPane.addTextField(state, fieldData);

		mainPane.addLabelBoldRequired(AdminDataPoliciesMessages.get().DataPolicyEditor_SummaryPage_RetentionType, state, labelData);
		dataPolicyRetentionType = mainPane.addComboBox(state, fieldData);

		mainPane.addLabelBoldRequired(AdminDataPoliciesMessages.get().DataPolicyEditor_SummaryPage_RetentionPeriod, state, labelData);
		dataPolicyRetentionPeriodInDays = mainPane.addTextField(state, fieldData);

		mainPane.addLabelBoldRequired(AdminDataPoliciesMessages.get().DataPolicyEditor_SummaryPage_DataPolicyState, state, labelData);
		dataPolicyState = mainPane.addComboBox(state, fieldData);

		mainPane.addLabelBoldRequired(AdminDataPoliciesMessages.get().DataPolicyEditor_SummaryPage_StartDate, state, labelData);
		dataPolicyStartDate = mainPane.addDateTimeComponent(IEpDateTimePicker.STYLE_DATE_AND_TIME, state, fieldData);

		boolean endDateEditable = isEndDateEditable();
		EpState endDateState = endDateEditable ? EpState.EDITABLE : EpState.DISABLED;
		Label endDateLabel = mainPane.addLabelBold(AdminDataPoliciesMessages.get().DataPolicyEditor_SummaryPage_EndDate, labelData);
		endDateLabel.setEnabled(endDateEditable);
		dataPolicyEndDate = mainPane.addDateTimeComponent(IEpDateTimePicker.STYLE_DATE_AND_TIME, endDateState, fieldData);

		Label descriptionLabel = mainPane.addLabelBold(AdminDataPoliciesMessages.get().DataPolicyEditor_SummaryPage_Description, labelData);
		descriptionLabel.setEnabled(EpState.EDITABLE == state);
		dataPolicyDescriptionField = mainPane.addTextArea(true, false, state, fieldData);

		((TableWrapData) dataPolicyDescriptionField.getLayoutData()).heightHint = DESCRIPTION_AREA_HEIGHT;

		Label activitiesLabel = mainPane.addLabelBold(AdminDataPoliciesMessages.get().DataPolicyEditor_SummaryPage_Activities, labelData);
		activitiesLabel.setEnabled(EpState.EDITABLE == state);
		activitiesField = mainPane.addTextField(state, fieldData);
	}

	@Override
	protected void populateControls() {
		DataPolicy dataPolicy = (DataPolicy) getModel();
		dataPolicyRetentionType.add(AdminDataPoliciesMessages.get().DataPolicyEditor_SummaryPage_RetentionType_FromCreationDate,
				RetentionType.FROM_CREATION_DATE.getOrdinal());
		dataPolicyRetentionType.add(AdminDataPoliciesMessages.get().DataPolicyEditor_SummaryPage_RetentionType_FromLastModifiedDate,
				RetentionType.FROM_LAST_UPDATE.getOrdinal());

		dataPolicyState.add(AdminDataPoliciesMessages.get().DataPolicyEditor_SummaryPage_State_Draft, DataPolicyState.DRAFT.getOrdinal());
		dataPolicyState.add(AdminDataPoliciesMessages.get().DataPolicyEditor_SummaryPage_State_Active, DataPolicyState.ACTIVE.getOrdinal());
		dataPolicyState.add(AdminDataPoliciesMessages.get().DataPolicyEditor_SummaryPage_State_Disabled, DataPolicyState.DISABLED.getOrdinal());


		if (dataPolicy.isPersisted()) {
			dataPolicyNameField.setText(dataPolicy.getPolicyName());

			dataPolicyReferenceKeyField.setText(dataPolicy.getReferenceKey());

			Integer retentionTypeSelection = Optional.ofNullable(dataPolicy.getRetentionType())
					.map(RetentionType::getOrdinal)
					.orElse(DEFAULT_SELECTION);
			dataPolicyRetentionType.select(retentionTypeSelection);

			dataPolicyRetentionPeriodInDays.setText(dataPolicy.getRetentionPeriodInDays().toString());

			Integer dataPolicyStateSelection = Optional.ofNullable(dataPolicy.getState())
					.map(DataPolicyState::getOrdinal)
					.orElse(DEFAULT_SELECTION);
			dataPolicyState.select(dataPolicyStateSelection);

			dataPolicyStartDate.setDate(dataPolicy.getStartDate());

			Optional.ofNullable(dataPolicy.getEndDate())
					.ifPresent(dataPolicyEndDate::setDate);

			Optional.ofNullable(dataPolicy.getDescription())
					.ifPresent(dataPolicyDescriptionField::setText);

			String activities = dataPolicy.getActivities().stream().collect(Collectors.joining(","));
			activitiesField.setText(activities);
		} else {
			dataPolicyRetentionType.select(DEFAULT_SELECTION);
			dataPolicyState.select(DEFAULT_SELECTION);
		}

		mainPane.setControlModificationListener(listener);
	}

	@Override
	protected void bindControls(final DataBindingContext bindingContext) {
		DataPolicy dataPolicy = (DataPolicy) getModel();

		EpControlBindingProvider binder = EpControlBindingProvider.getInstance();

		IValidator validatorMax255Required = new CompoundValidator(EpValidatorFactory.MAX_LENGTH_255, EpValidatorFactory.REQUIRED);
		binder.bind(bindingContext, dataPolicyNameField, dataPolicy, "policyName",
				validatorMax255Required, null, true);
		binder.bind(bindingContext, dataPolicyReferenceKeyField, dataPolicy, "referenceKey",
				validatorMax255Required, null, true);

		binder.bind(bindingContext, dataPolicyRetentionType, dataPolicy, "retentionType", EpValidatorFactory.REQUIRED,
				new Converter(Integer.class, RetentionType.class) {
					@Override
					public Object convert(final Object input) {
						return RetentionType.valueOf((Integer) input);
					}
				}, true);

		binder.bind(bindingContext, dataPolicyRetentionPeriodInDays, dataPolicy, "retentionPeriodInDays", //$NON-NLS-1$
				EpValidatorFactory.POSITIVE_INTEGER_REQUIRED, null, true);

		binder.bind(bindingContext, dataPolicyState, dataPolicy, "state", EpValidatorFactory.REQUIRED,
				new Converter(Integer.class, DataPolicyState.class) {
					@Override
					public Object convert(final Object input) {
						return DataPolicyState.valueOf((Integer) input);
					}
				}, true);

		// make sure always end date > start date
		IValidator endDateAwareValidator = new CompoundValidator(EpValidatorFactory.DATE_TIME,
				EpValidatorFactory.createDisableDateValidator(dataPolicyStartDate, dataPolicyEndDate,
						AdminDataPoliciesMessages.get().DataPolicyEditor_DataPoints_StartDateBeforeEndDate));

		IValidator requiredStartDate = new CompoundValidator(EpValidatorFactory.DATE_TIME_REQUIRED,
				endDateAwareValidator);

		dataPolicyStartDate.bind(bindingContext, requiredStartDate, dataPolicy, "startDate");
		dataPolicyEndDate.bind(bindingContext, endDateAwareValidator, dataPolicy, "endDate");

		binder.bind(bindingContext, dataPolicyDescriptionField, dataPolicy, "description",
				EpValidatorFactory.MAX_LENGTH_255, null, true);

		binder.bind(bindingContext, activitiesField, dataPolicy, "activities",
				null, new Converter(String.class, Set.class) {
					@Override
					public Object convert(final Object input) {
						List<String> activities = Arrays.asList(StringUtils.split((String) input, ','));
						return activities.stream()
								.map(String::trim)
								.collect(Collectors.toSet());
					}
				}, true);

		bindingContext.updateTargets();
	}

	private boolean isEditableMode() {
		return ((DataPolicyEditor) getEditor()).isEditableMode();
	}

	private boolean isEndDateEditable() {
		return ((DataPolicyEditor) getEditor()).isEndDateEditable();
	}

}
