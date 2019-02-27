/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.conditionbuilder.wizard.conditioncomposite;

import java.util.Date;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.layout.GridLayout;

import com.elasticpath.cmclient.conditionbuilder.plugin.ConditionBuilderMessages;
import com.elasticpath.cmclient.conditionbuilder.plugin.ConditionBuilderPlugin;
import com.elasticpath.cmclient.conditionbuilder.wizard.model.TimeConditionModelAdapter;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.EpValueBinding;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.ui.framework.IEpDateTimePicker;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.CompoundValidator;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.IPolicyTargetLayoutComposite;

/**
 * Composite with TIME condition controls.
 */
public class TimeConditionComposite {

	private IEpDateTimePicker startDate;

	private IEpDateTimePicker endDate;

	private EpValueBinding startDateBinding;

	private EpValueBinding endDateBinding;

	private IStatus startDateStatus = Status.OK_STATUS;

	private IStatus endDateStatus = Status.OK_STATUS;

	private final DataBindingContext dataBindingContext;

	private final EpControlBindingProvider epControlBindingProvider;

	private final TimeConditionModelAdapter timeConditionModelAdapter;
	
	private static final int TEN = 10;

	/**
	 * Constructor.
	 * 
	 * @param timeConditionModelAdapter instance of TimeConditionModelAdapter
	 * @param numberOfColumnsInTheComposite number of columns in composite
	 * @param composite the Composite
	 * @param container the policy container
	 * @param dataBindingContext the dataBindingContext
	 * @param epControlBindingProvider the epControlBindingProvider
	 * @param isEditorUsage true, if this composite is used in the condition editor
	 */
	public TimeConditionComposite(final TimeConditionModelAdapter timeConditionModelAdapter, 
			final int numberOfColumnsInTheComposite,
			final IPolicyTargetLayoutComposite composite, 
			final PolicyActionContainer container, 
			final DataBindingContext dataBindingContext, 
			final EpControlBindingProvider epControlBindingProvider,
			final boolean isEditorUsage) {
		
		this.timeConditionModelAdapter = timeConditionModelAdapter;
		this.dataBindingContext = dataBindingContext;
		this.epControlBindingProvider = epControlBindingProvider;
		this.timeConditionModelAdapter.setEditorUsage(isEditorUsage);
		createComposite(composite, container, numberOfColumnsInTheComposite);
	}

	private void createComposite(final IPolicyTargetLayoutComposite composite, 
			final PolicyActionContainer container, final int numberOfColumnsInTheComposite) {
		
		IEpLayoutData layoutData = composite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true);
		
		IPolicyTargetLayoutComposite epLayoutComposite = 
			composite.addGridLayoutComposite(numberOfColumnsInTheComposite, true, layoutData, container);
		
		((GridLayout) epLayoutComposite.getSwtComposite().getLayout()).horizontalSpacing = TEN;
		((GridLayout) epLayoutComposite.getSwtComposite().getLayout()).marginWidth = TEN;
		
		// layout
		final IEpLayoutData startLabelData = epLayoutComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.BEGINNING);
		final IEpLayoutData endLabelData = epLayoutComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.BEGINNING);
		final IEpLayoutData fieldData = epLayoutComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.BEGINNING, false, false, 2, 1);
		// start date label
		// just for controls layout
		epLayoutComposite.addEmptyComponent(null, container);

		epLayoutComposite.addLabelBoldRequired(ConditionBuilderMessages.get().Start_Date_Label, startLabelData, container);
		// start date control
		startDate = epLayoutComposite.addDateTimeComponent(IEpDateTimePicker.STYLE_DATE_AND_TIME, fieldData, container);

		// just for controls layout
		epLayoutComposite.addEmptyComponent(null, container);
		// end date label
		epLayoutComposite.addLabelBold(ConditionBuilderMessages.get().End_Date_Label, endLabelData, container);
		// end date control
		endDate = epLayoutComposite.addDateTimeComponent(IEpDateTimePicker.STYLE_DATE_AND_TIME, fieldData, container);
		
		composite.getSwtComposite().layout();
		this.setupDates();
	}

	/**
	 * Binds composite controls.
	 */
	public void bindControls() {
		bindStartDate();
		bindEndDate();
	}

	private void bindEndDate() {
		// make sure always disable date > enable date
		// and end date is in the future
		IValidator endDateValidator = new CompoundValidator(new IValidator[] { EpValidatorFactory.DATE_TIME, new IValidator() {
			public IStatus validate(final Object value) {
				endDateStatus = Status.OK_STATUS;
				Date dateEnd = endDate.getDate(); 
				if (dateEnd != null && null != startDate.getDate() && dateEnd.before(startDate.getDate())) {
					endDateStatus = new Status(IStatus.ERROR, ConditionBuilderPlugin.PLUGIN_ID, IStatus.ERROR,
							ConditionBuilderMessages.get().Validation_EndDateBeforeStartDate, null);
				}
				return endDateStatus;
			}
		} });

		// end date binding
		endDateBinding = epControlBindingProvider.bind(dataBindingContext, endDate.getSwtText(), endDateValidator, null,
				new ObservableUpdateValueStrategy() {
					@Override
					protected IStatus doSet(final IObservableValue observableValue, final Object value) {
						Date end = endDate.getDate();
						timeConditionModelAdapter.setEndDate(end);
						return Status.OK_STATUS;
					}

				}, true);
	}

	private void bindStartDate() {
		// make sure always disable date > enable date
		// and end date is in the future
		IValidator startDateValidator = new CompoundValidator(new IValidator[] { EpValidatorFactory.DATE_TIME_REQUIRED, new IValidator() {
			public IStatus validate(final Object value) {
				startDateStatus = Status.OK_STATUS;
				if (endDate.getDate() != null && null != startDate.getDate() && endDate.getDate().before(startDate.getDate())) {
					startDateStatus = new Status(IStatus.ERROR, ConditionBuilderPlugin.PLUGIN_ID, IStatus.ERROR,
							ConditionBuilderMessages.get().Validation_EndDateBeforeStartDate, null);
				}
				return startDateStatus;
			}
		} });

		// start date binding
		startDateBinding = epControlBindingProvider.bind(dataBindingContext, startDate.getSwtText(), startDateValidator, null,
				new ObservableUpdateValueStrategy() {
					@Override
					protected IStatus doSet(final IObservableValue observableValue, final Object value) {
						Date start = startDate.getDate();
						if (null != start && !start.equals(timeConditionModelAdapter.getStartDate())) {
							timeConditionModelAdapter.setStartDate(start);
						}
						return Status.OK_STATUS;
					}

				}, true);
	}

	private void setupDates() {
		startDate.setDate(timeConditionModelAdapter.getStartDate());
		endDate.setDate(timeConditionModelAdapter.getEndDate());
	}

	/**
	 * Returns start date control binding.
	 * 
	 * @return - start date control binding.
	 */
	public EpValueBinding getStartDateBinding() {
		return startDateBinding;
	}

	/**
	 * Returns end date control binding.
	 * 
	 * @return - end date control binding.
	 */
	public EpValueBinding getEndDateBinding() {
		return endDateBinding;
	}

	/**
	 * Unbind controls.
	 */
	public void unbindControls() {
		EpControlBindingProvider.removeEpValueBinding(dataBindingContext, startDateBinding);
		EpControlBindingProvider.removeEpValueBinding(dataBindingContext, endDateBinding);
	}
}
