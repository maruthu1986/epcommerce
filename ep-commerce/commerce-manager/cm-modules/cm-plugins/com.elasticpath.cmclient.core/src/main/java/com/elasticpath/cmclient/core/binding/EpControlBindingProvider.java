/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.binding;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateListStrategy;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.conversion.Converter;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Spinner;

import com.elasticpath.cmclient.core.CmSingletonUtil;
import com.elasticpath.cmclient.core.CmClientResources;
import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.binding.EpBindingConfiguration.ValidationErrorLocation;

/**
 * Provides binding, validation, and conversion for SWT controls. Binding: Connects SWT controls to domain object model properties so that the domain
 * objects are updated when the value of the control changes. Validation: Performs field validation, e.g. must be less than 50 characters.
 * Conversion: Peforms data type conversion, e.g. from a string number in a textbox to an integer property of a domain model object. To use this
 * class, call <code>bind</code> and pass in the required parameters to specify how to bind, validate, and convert. This class also provides
 * control decorations that display validation errors. TODO: Make sure all objects are properly disposed
 */
public final class EpControlBindingProvider {

	/**
	 * Private constructor to enforce singleton pattern.
	 */
	private EpControlBindingProvider() {

	}

	/**
	 * Returns the session instance of <code>EpControlBindingProvider</code>.
	 * 
	 * @return the instance
	 */
	public static EpControlBindingProvider getInstance() {

		return  CmSingletonUtil.getSessionInstance(EpControlBindingProvider.class);
	}

	/**
	 * <p>
	 * Binds an SWT control to a domain object property.
	 * </p>
	 * <p>
	 * Convenience method - This method is typically used when no validation or conversion is required.
	 * </p>
	 * 
	 * @param bindingContext Pass this in so that all related controls have the same context
	 * @param control The SWT control to be bound
	 * @param target The domain model object that the control is to be bound to
	 * @param fieldName The field of the domain model object that the control is to be bound to
	 * @return the EpValueBinding
	 */
	public EpValueBinding bind(final DataBindingContext bindingContext, final Control control, final Object target, final String fieldName) {
		EpBindingConfiguration bindingConfig = new EpBindingConfiguration(bindingContext, control, target, fieldName);
		bindingConfig.configureUiToModelBinding(false);
		return this.internalBind(bindingConfig);
	}

	/**
	 * <p>
	 * Binds an SWT control to a domain object property.
	 * </p>
	 * 
	 * @param bindingConfig the configuration from which to create the binding
	 * @return the EpValueBinding
	 */
	public EpValueBinding bind(final EpBindingConfiguration bindingConfig) {
		return this.internalBind(bindingConfig);
	}

	/**
	 * Binds an SWT control to a domain object property. This method is typically used when the control's value can be directly mapped to a model
	 * bean's property.
	 * 
	 * @param bindingContext Pass this in so that all related controls have the same context
	 * @param control The SWT control to be bound
	 * @param target The domain model object that the control is to be bound to
	 * @param fieldName The field of the domain model object that the control is to be bound to
	 * @param validator (Optional, can be null) The validator (Atomic or Compound) that checks the user input
	 * @param converter (Optional, can be null) The converter that will convert text input to the data type required by the domain model object
	 * @param hideDecorationOnFirstValidation Hide the control's field decoration on the first validation pass
	 * @return the EpValueBinding
	 */
	public EpValueBinding bind(final DataBindingContext bindingContext, final Control control, final Object target, final String fieldName,
			final IValidator validator, final Converter converter, final boolean hideDecorationOnFirstValidation) {
		EpBindingConfiguration bindingConfig = new EpBindingConfiguration(bindingContext, control, target, fieldName);
		bindingConfig.configureUiToModelBinding(converter, validator, hideDecorationOnFirstValidation);
		return this.internalBind(bindingConfig);
	}

	/**
	 * Binds an SWT control to a domain object property. This method is typically used when the control's value cannot be directly mapped to a model
	 * bean's property, and a customUpdateStrategy is provided instead.
	 * 
	 * @param bindingContext Pass this in so that all related controls have the same context
	 * @param control The SWT control to be bound
	 * @param validator (Optional, can be null) The validator (Atomic or Compound) that checks the user input
	 * @param converter (Optional, can be null) The converter that will convert text input to the data type required by the domain model object
	 * @param customUpdateStrategy optional, pass in a custom update strategy if required.
	 * @param hideDecorationOnFirstValidation Hide the control's field decoration on the first validation pass
	 * @return returns the EpValueBinding
	 */
	public EpValueBinding bind(final DataBindingContext bindingContext, final Control control, final IValidator validator,
			final IConverter converter, final ObservableUpdateValueStrategy customUpdateStrategy, final boolean hideDecorationOnFirstValidation) {

		EpBindingConfiguration bindingConfig = new EpBindingConfiguration(bindingContext, control);
		bindingConfig.configureUiToModelBinding(converter, validator, customUpdateStrategy, hideDecorationOnFirstValidation);

		return this.internalBind(bindingConfig);
	}

	/**
	 * Binds an SWT control's list to a domain object's List. The only supported widgets are Combo, CCombo, and List.
	 * 
	 * @param dataBindingContext Pass this in so that all related controls have the same context
	 * @param control The SWT control to be bound
	 * @param target The domain model object that the control is to be bound to
	 * @param fieldName The field of the domain model object that the control is to be bound to
	 * @param converter (Optional, can be null) The converter that will convert text input to the data type required by the domain model object
	 * @param customUpdateStrategy optional, pass in a custom update strategy if required.
	 */
	public void bindList(final DataBindingContext dataBindingContext, final Control control, final Object target, final String fieldName,
			final IConverter converter, final UpdateListStrategy customUpdateStrategy) {

		final IObservableList targetObservableList = SWTObservables.observeItems(control);
		final IObservableList modelObservableList = this.getObservableList(target, fieldName);

		UpdateListStrategy updateStrategy;
		if (customUpdateStrategy == null) {
			updateStrategy = prepareUpdateListStrategy(converter);
		} else {
			updateStrategy = customUpdateStrategy;
		}

		dataBindingContext.bindList(targetObservableList, modelObservableList,
		// UI to Model binding strategy
				updateStrategy,
				// Model to UI binding strategy
				new UpdateListStrategy(UpdateListStrategy.POLICY_NEVER));
	}

	/**
	 * Binds an SWT control to a domain object property.
	 * 
	 * @param bindingConfig the configuration from which to create the binding
	 * @return the EpValueBinding
	 */
	private EpValueBinding internalBind(final EpBindingConfiguration bindingConfig) {
		final Control control = bindingConfig.getControl();
		ControlDecoration validationErrorDecoration = null;

		final UpdateValueStrategy updateStrategy;
		if (bindingConfig.isUiToModelBindingConfigured()) {
			// Add the validation error decoration
			validationErrorDecoration = addControlDecoration(control, bindingConfig.getErrorLocation());

			// Prepare the update strategy
			updateStrategy = prepareUpdateValueStrategy(bindingConfig.getValidator(), bindingConfig.getUiToModelConverter(), bindingConfig
					.getCustomUpdateStrategy(), validationErrorDecoration, bindingConfig.isHideDecorationOnFirstValidation());
		} else {
			updateStrategy = new UpdateValueStrategy(false, bindingConfig.getUpdatePolicy().getPolicyNumber());
		}

		ISWTObservableValue targetObservable = null;
		if (control instanceof Combo || control instanceof CCombo) {
			targetObservable = SWTObservables.observeSingleSelectionIndex(control);
		} else if (control instanceof Button) {
			targetObservable = SWTObservables.observeSelection(control);
			// Work-around for bug in ButtonObservableValue:
			targetObservable.setValue(Boolean.valueOf(((Button) control).getSelection()));
		} else if (control instanceof Scale) {
			targetObservable = SWTObservables.observeSelection(control);
			targetObservable.setValue(((Scale) control).getSelection());			
		} else if (control instanceof Spinner) {
			targetObservable = SWTObservables.observeSelection(control);
			targetObservable.setValue(((Spinner) control).getSelection());
		} else if (control instanceof List) {
			throw new IllegalArgumentException("Lists must be bound with the bindList method."); //$NON-NLS-1$
		} else {
			targetObservable = SWTObservables.observeText(control, SWT.Modify);
		}

		UpdateValueStrategy reverseUpdateStrategy = new UpdateValueStrategy(bindingConfig.getUpdatePolicy().getPolicyNumber());
		if (bindingConfig.isModelToUiBindingConfigured()) {
			reverseUpdateStrategy.setConverter(bindingConfig.getModelToUiConverter());
		}

		// Bind
		final Binding binding = bindingConfig.getBindingContext().bindValue(targetObservable, bindingConfig.getObservableValue(),
		// UI to model binding
				updateStrategy,
				// model to UI binding
				reverseUpdateStrategy);
		EpValueBinding binder = new EpValueBinding(binding, validationErrorDecoration);
		
		EPControlUnbinder.getInstance().registerForUnbind(bindingConfig.getBindingContext(), binder);
		return binder;
	}

	/**
	 * Removes EpValueBindings from the databinding context, and remove the decoration.
	 * 
	 * @param dbc the databinding context
	 * @param epBinding the EpValueBinding
	 */
	public static void removeEpValueBinding(final DataBindingContext dbc, final EpValueBinding epBinding) {
		dbc.removeBinding(epBinding.getBinding());
		epBinding.getDecoration().getControl().setBackground(CmClientResources.getBackgroundColor());
		epBinding.getDecoration().hide();
		epBinding.getDecoration().dispose();
	}

	/**
	 * Prepares the update strategy by added validation, conversion, and connecting an update handler to it as a listener.
	 * 
	 * @param validator the validator to use, or null if no validation is required.
	 * @param converter the converter to user, or null if no conversion is required.
	 * @param customUpdateStrategy the update strategy to use, or null if no customized strategy is required.
	 * @param validationErrorDecoration the control decoration to be managed by the handler associated with this update strategy
	 * @return an <code>ObservableUpdateValueStrategy</code>
	 */
	private ObservableUpdateValueStrategy prepareUpdateValueStrategy(final IValidator validator, final IConverter converter,
			final ObservableUpdateValueStrategy customUpdateStrategy, final ControlDecoration validationErrorDecoration,
			final boolean hideDecorationOnFirstValidation) {
		ObservableUpdateValueStrategy updateStrategy = customUpdateStrategy;
		if (updateStrategy == null) {
			updateStrategy = new ObservableUpdateValueStrategy();
		}
		updateStrategy.addListener(new ValueUpdateHandler(validationErrorDecoration, hideDecorationOnFirstValidation));
		if (validator != null) {
			updateStrategy.setAfterGetValidator(validator);
		}
		if (converter != null) {
			updateStrategy.setConverter(converter);
		}
		return updateStrategy;
	}

	private UpdateListStrategy prepareUpdateListStrategy(final IConverter converter) {
		final UpdateListStrategy updateStrategy = new UpdateListStrategy();
		if (converter != null) {
			updateStrategy.setConverter(converter);
		}
		return updateStrategy;
	}

	private IObservableList getObservableList(final Object target, final String fieldName) {
		return BeansObservables.observeList(Realm.getDefault(), target, fieldName);
	}

	/**
	 * Add the decoration(s) to a control using the default location.
	 * 
	 * @param control the control that the decorations are to be added to
	 * @return the control decoration
	 */
	public ControlDecoration addControlDecoration(final Control control) {
		return addControlDecoration(control, ValidationErrorLocation.RIGHT);
	}

	/**
	 * Add the decoration(s) to a control.
	 * 
	 * @param control the control that the decorations are to be added to
	 * @param errorLocation the location of the validation error
	 * @return the control decoration
	 */
	public ControlDecoration addControlDecoration(final Control control, final ValidationErrorLocation errorLocation) {
		final ControlDecoration validationErrorDecoration = new ControlDecoration(control, errorLocation.getPosition());
		validationErrorDecoration.setImage(CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_ERROR_SMALL));
		return validationErrorDecoration;
	}
}
