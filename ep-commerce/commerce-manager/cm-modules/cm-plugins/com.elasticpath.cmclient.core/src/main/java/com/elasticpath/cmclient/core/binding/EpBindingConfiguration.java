/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.binding;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.observable.value.AbstractObservableValue;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

/**
 * Configuration object for creating a new data binding.
 */
public class EpBindingConfiguration {

	/**
	 * The update policy used for the binding.
	 */
	public enum UpdatePolicy {
		/**
		 * @see UpdateValueStrategy#POLICY_NEVER
		 */
		NEVER(UpdateValueStrategy.POLICY_NEVER),

		/**
		 * @see UpdateValueStrategy#POLICY_CONVERT
		 */
		CONVERT(UpdateValueStrategy.POLICY_CONVERT),

		/**
		 * @see UpdateValueStrategy#POLICY_ON_REQUEST
		 */
		ON_REQUEST(UpdateValueStrategy.POLICY_ON_REQUEST),

		/**
		 * @see UpdateValueStrategy#POLICY_UPDATE
		 */
		UPDATE(UpdateValueStrategy.POLICY_UPDATE);

		private final int policyNumber;

		/**
		 * @param policyNumber the appropriate policy constant
		 */
		UpdatePolicy(final int policyNumber) {
			this.policyNumber = policyNumber;
		}

		/**
		 * Get the equivalent policy number specified in the eclipse databinding framework.
		 * 
		 * @return the policy number
		 */
		public int getPolicyNumber() {
			return policyNumber;
		}
	};

	/**
	 * The location of the validation error icon.
	 */
	public enum ValidationErrorLocation {
		/**
		 * Locates the validation error icon to the left of the control.
		 */
		LEFT(SWT.LEFT),

		/**
		 * Locates the validation error icon to the right of the control.
		 */
		RIGHT(SWT.RIGHT);

		private final int position;

		/**
		 * @param position the appropriate SWT constant
		 */
		ValidationErrorLocation(final int position) {
			this.position = position;
		}

		/**
		 * Get the equivalent position number specified in SWT.
		 * 
		 * @return the SWT position value
		 */
		public int getPosition() {
			return position;
		}
	};

	private final DataBindingContext bindingContext;

	private final Control control;

	private final Object target;

	private final String fieldName;

	private IValidator validator;

	private UpdatePolicy updatePolicy;

	private boolean hideDecorationOnFirstValidation;

	private ObservableUpdateValueStrategy customUpdateStrategy;

	private IConverter modelToUiConverter;

	private IConverter uiToModelConverter;

	private boolean uiToModelBindingConfigured;

	private boolean modelToUiBindingConfigured;

	private ValidationErrorLocation errorLocation;

	/**
	 * Create a new binding configuration on the specified control.
	 * 
	 * @param bindingContext the data binding context
	 * @param control the control to bind
	 */
	public EpBindingConfiguration(final DataBindingContext bindingContext, final Control control) {
		this(bindingContext, control, null, null);
	}

	/**
	 * Create a new binding configuration on the specified control.
	 * 
	 * @param bindingContext the data binding context
	 * @param control the control to bind
	 * @param target The domain model object that the control is to be bound to
	 * @param fieldName The field of the domain model object that the control is to be bound to
	 */
	public EpBindingConfiguration(final DataBindingContext bindingContext, final Control control, final Object target, final String fieldName) {
		this.bindingContext = bindingContext;
		this.target = target;
		this.fieldName = fieldName;
		this.control = control;
		this.customUpdateStrategy = null;
		this.updatePolicy = UpdatePolicy.NEVER;
	}

	/**
	 * Configure binding from model to ui using the specified converter and update policy.
	 * 
	 * @param converter (Optional, can be null) The converter that will convert text input to the data type required by the domain model object
	 * @param updatePolicy the update policy to use for this binding
	 */
	public void configureModelToUiBinding(final IConverter converter, final UpdatePolicy updatePolicy) {
		this.modelToUiConverter = converter;
		this.updatePolicy = updatePolicy;
		this.modelToUiBindingConfigured = true;
	}

	/**
	 * Configure binding from ui to model using a converter, validator, and a custom update strategy.
	 * 
	 * @param converter (Optional, can be null) The converter that will convert text input to the data type required by the domain model object
	 * @param validator (Optional, can be null) The validator (Atomic or Compound) that checks the user input
	 * @param customUpdateStrategy optional, pass in a custom update strategy if required.
	 * @param hideDecorationOnFirstValidation Hide the control's field decoration on the first validation pass
	 */
	public void configureUiToModelBinding(final IConverter converter, final IValidator validator,
			final ObservableUpdateValueStrategy customUpdateStrategy, final boolean hideDecorationOnFirstValidation) {
		internalConfigureUiToModelBinding(converter, validator, customUpdateStrategy, hideDecorationOnFirstValidation, ValidationErrorLocation.RIGHT);
	}

	/**
	 * Configure binding from ui to model when no conversion or validation is required.
	 * 
	 * @param hideDecorationOnFirstValidation Hide the control's field decoration on the first validation pass
	 */
	public void configureUiToModelBinding(final boolean hideDecorationOnFirstValidation) {
		internalConfigureUiToModelBinding(null, null, null, hideDecorationOnFirstValidation, ValidationErrorLocation.RIGHT);
	}

	/**
	 * Configure binding from ui to model using a converter and validator with the default update strategy.
	 * 
	 * @param converter (Optional, can be null) The converter that will convert text input to the data type required by the domain model object
	 * @param validator (Optional, can be null) The validator (Atomic or Compound) that checks the user input
	 * @param hideDecorationOnFirstValidation Hide the control's field decoration on the first validation pass
	 */
	public void configureUiToModelBinding(final IConverter converter, final IValidator validator, final boolean hideDecorationOnFirstValidation) {
		internalConfigureUiToModelBinding(converter, validator, null, hideDecorationOnFirstValidation, ValidationErrorLocation.RIGHT);
	}

	private void internalConfigureUiToModelBinding(final IConverter converter, final IValidator validator,
			final ObservableUpdateValueStrategy customUpdateStrategy, final boolean hideDecorationOnFirstValidation,
			final ValidationErrorLocation errorLocation) {
		this.uiToModelConverter = converter;
		this.validator = validator;
		this.customUpdateStrategy = customUpdateStrategy;
		this.hideDecorationOnFirstValidation = hideDecorationOnFirstValidation;
		this.uiToModelBindingConfigured = true;
		this.errorLocation = errorLocation;
	}

	/**
	 * @return the bindingContext
	 */
	public DataBindingContext getBindingContext() {
		return bindingContext;
	}

	/**
	 * @return the control
	 */
	public Control getControl() {
		return control;
	}

	/**
	 * @return the target
	 */
	public Object getTarget() {
		return target;
	}

	/**
	 * @return the fieldName
	 */
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * @return the validator
	 */
	public IValidator getValidator() {
		return validator;
	}

	/**
	 * @return the updatePolicy
	 */
	public UpdatePolicy getUpdatePolicy() {
		return updatePolicy;
	}

	/**
	 * @return the hideDecorationOnFirstValidation
	 */
	public boolean isHideDecorationOnFirstValidation() {
		return hideDecorationOnFirstValidation;
	}

	/**
	 * @return the customUpdateStrategy
	 */
	public ObservableUpdateValueStrategy getCustomUpdateStrategy() {
		return customUpdateStrategy;
	}

	/**
	 * @return the modelToUiConverter
	 */
	public IConverter getModelToUiConverter() {
		return modelToUiConverter;
	}

	/**
	 * @return the uiToModelConverter
	 */
	public IConverter getUiToModelConverter() {
		return uiToModelConverter;
	}

	/**
	 * Get an <code>IObservableValue</code> for the given target object and field. If either the target or field name are null, a dummy
	 * observableValue will be created and returned. If a dummy observableValue is passed, then a custom update strategy must be used to update the
	 * observed field.
	 * 
	 * @return an <code>IObservableValue</code>
	 */
	public IObservableValue getObservableValue() {
		IObservableValue observableValue = null;
		if (target == null || fieldName == null) {
			observableValue = getDummyObservableValue();
		} else {
			observableValue = BeansObservables.observeValue(target, fieldName);
		}
		return observableValue;
	}

	/**
	 * Create a dummy observable value instead of the bean observable when the observed value is not a bean property. The value will be set using a
	 * custom update strategy
	 * 
	 * @return the observable value
	 */
	private IObservableValue getDummyObservableValue() {
		return new AbstractObservableValue(SWTObservables.getRealm(Display.getCurrent())) {
			@Override
			protected Object doGetValue() {
				// Do Nothing
				return null;
			}

			public Object getValueType() {
				// Do Nothing
				return null;
			}
		};
	}

	/**
	 * @return the uiToModelBindingConfigured
	 */
	public boolean isUiToModelBindingConfigured() {
		return uiToModelBindingConfigured;
	}

	/**
	 * @return the modelToUiBindingConfigured
	 */
	public boolean isModelToUiBindingConfigured() {
		return modelToUiBindingConfigured;
	}

	/**
	 * @return the location of the validation error icon
	 */
	public ValidationErrorLocation getErrorLocation() {
		return errorLocation;
	}

	/**
	 * @param errorLocation the errorLocation to set
	 */
	public void setErrorLocation(final ValidationErrorLocation errorLocation) {
		this.errorLocation = errorLocation;
	}
}
