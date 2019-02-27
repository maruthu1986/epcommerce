/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.validation;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.CorePlugin;

/**
 * Required validator class.
 */
public class RequiredValidator implements IValidator {

	private static final Logger LOG = Logger.getLogger(RequiredValidator.class);
	
	private final int comboBoxFirstValidIndex;

	private String comboValueRequired = CoreMessages.get().EpValidatorFactory_ComboValueRequired;
	private String valueRequired = CoreMessages.get().EpValidatorFactory_ValueRequired;
	
	/**
	 * Constructor that provides default validation error messages.
	 * @param comboBoxFirstValidIndex combo box index to begin validation from
	 */
	public RequiredValidator(final int comboBoxFirstValidIndex) {
		super();
		this.comboBoxFirstValidIndex = comboBoxFirstValidIndex;
	}

	/**
	 * Constructor that provides custom validation error messages.
	 * @param comboBoxFirstValidIndex combo box index to begin validation from 
	 * @param comboValueRequiredMessage error message for combo box validation, or default if empty
	 * @param valueRequiredMessage error message for field validation, or default if empty
	 */
	public RequiredValidator(final int comboBoxFirstValidIndex, final String comboValueRequiredMessage, final String valueRequiredMessage) {
		super();
		this.comboBoxFirstValidIndex = comboBoxFirstValidIndex;
		if (StringUtils.isNotEmpty(comboValueRequiredMessage)) {
			this.comboValueRequired = comboValueRequiredMessage;
		}
		if (StringUtils.isNotEmpty(valueRequiredMessage)) {
			this.valueRequired = valueRequiredMessage;
		}
	}
	
	@Override
	public IStatus validate(final Object value) {
		if (!(value instanceof String || value instanceof Integer)) { //support for combo boxes
			LOG.error("REQUIRED validator: " + value + " is not a String or Integer (for combo boxes).");  //$NON-NLS-1$//$NON-NLS-2$
			throw new IllegalArgumentException("This validation type supports String or Integer"); //$NON-NLS-1$
		}
		boolean valid = false;
		boolean isCombo = false;
		if (value instanceof String) {
			final String stringValue = (String) value;
			valid = stringValue.trim().length() > 0;
		} else { // value is Integer
			valid = ((Integer) value) >= comboBoxFirstValidIndex;
			isCombo = true;
		}
		if (!valid && isCombo) {
			return new Status(
					IStatus.ERROR,
					CorePlugin.PLUGIN_ID,
					IStatus.ERROR,
					comboValueRequired,
					null);
		} else if (!valid) {
			return new Status(
					IStatus.ERROR,
					CorePlugin.PLUGIN_ID,
					IStatus.ERROR,
					valueRequired,
					null);
		}
		return Status.OK_STATUS;
	}		
}