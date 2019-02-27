/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.validation;

import java.math.BigDecimal;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.CorePlugin;

/** Checks that the input is a big decimal. */
public class BigDecimalValidator implements IValidator {

	private static final Logger LOG = Logger.getLogger(BigDecimalValidator.class);

	private static final int BIG_DECIMAL_MAX_LENGTH = 20; // 19 characters + '.'

	private static final int BIG_DECIMAL_MAX_LENGTH_NO_DOT = 17;

	private static final String NUMBER_VALIDATION_ERROR_STRING = "Number to validate must be a String or Integer"; //$NON-NLS-1$

	private final String message;

	private final int scale;

	/**
	 * Create a validator with the default message.
	 */
	public BigDecimalValidator() {
		this(CoreMessages.get().EpValidatorFactory_BigDecimal, 2);
	}

	/**
	 * Create a validator with a custom message.
	 * @param message validation failure message when price is not a BigDecimal
	 */
	public BigDecimalValidator(final String message) {
		this(message, 2);
	}

	/**
	 * Create a validator with a custom message.
	 * @param message validation failure message when price is not a BigDecimal
	 * @param scale max amount for scale
	 */
	public BigDecimalValidator(final String message, final int scale) {
		this.message = message;
		this.scale = scale;
	}

	/**
	 * Create a validator with a custom scale.
	 * @param scale max amount for scale
	 */
	public BigDecimalValidator(final int scale) {
		this(CoreMessages.get().EpValidatorFactory_BigDecimal, scale);
	}

	@Override
	public IStatus validate(final Object value) {

		String stringValue = ""; //$NON-NLS-1$
		if (value instanceof String) {
			stringValue = (String) value;
		} else {
			LOG.error("Big decimal validator: " + value + " is not a supported object type.");  //$NON-NLS-1$//$NON-NLS-2$
			throw new IllegalArgumentException(NUMBER_VALIDATION_ERROR_STRING);
		}
		if (stringValue.length() == 0) {
			return Status.OK_STATUS; // value is not required and therefore status is ok for empty strings
		}

		BigDecimal bigDecimal = null;
		try {
			// maximum allowed characters in format: 12345678901234567.99
			if (stringValue.indexOf('.') != -1 && stringValue.length() > BIG_DECIMAL_MAX_LENGTH) {
				throw new NumberFormatException();
			} else // maximum allowed characters in format: 12345678901234567
				if (stringValue.indexOf('.') == -1 && stringValue.length() > BIG_DECIMAL_MAX_LENGTH_NO_DOT) {
					throw new NumberFormatException();
				}

			bigDecimal = new BigDecimal(stringValue).setScale(scale);
			bigDecimal.toString();
		} catch (final RuntimeException nfe) {
			return new Status(
					IStatus.ERROR,
					CorePlugin.PLUGIN_ID,
					IStatus.ERROR,
					message,
					null);

		}

		return postValidate(bigDecimal);
	}

	/**
	 * Intended for overwriting in ancestors.
	 * @param bigDecimal object for validation
	 * @return validation status
	 */
	protected IStatus postValidate(final BigDecimal bigDecimal) {
		return Status.OK_STATUS;
	}
}