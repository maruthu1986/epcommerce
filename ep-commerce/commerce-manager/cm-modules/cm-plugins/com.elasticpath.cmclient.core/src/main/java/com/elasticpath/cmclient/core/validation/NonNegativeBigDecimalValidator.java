/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.cmclient.core.validation;

import java.math.BigDecimal;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.elasticpath.cmclient.core.CorePlugin;

/** Checks that the input is greater than or equal to zero. */
public class NonNegativeBigDecimalValidator extends BigDecimalValidator {

	private final String validationFailMessage;

	/**
	 * Create a NonNegativeBigDecimalValidator that validates not negative.
	 * @param validationFailMessage validation failure message
	 */
	public NonNegativeBigDecimalValidator(final String validationFailMessage) {
		this.validationFailMessage = validationFailMessage;
	}
	
	/**
	 * Create a NonNegativeBigDecimalValidator that validates not negative.
	 * @param validationFailMessage validation failure message
	 * @param bigDecimalValidationMessage validation failure message
	 */
	public NonNegativeBigDecimalValidator(final String validationFailMessage, final String bigDecimalValidationMessage) {
		super(bigDecimalValidationMessage);
		this.validationFailMessage = validationFailMessage;
	}

	/**
	 * Create a NonNegativeBigDecimalValidator that validates not negative with maximum scale.
	 * @param validationFailMessage validation failure message
	 * @param scale maximum scale
	 */
	public NonNegativeBigDecimalValidator(final String validationFailMessage, final int scale) {
		super(scale);
		this.validationFailMessage = validationFailMessage;
	}
	
	@Override
	protected IStatus postValidate(final BigDecimal bigDecimal) {
		int result = bigDecimal.compareTo(BigDecimal.valueOf(0));
		// result == 0 if bigDecimal is 0, result == 1 if bigDecimal is greater than 0
		if (result == 0 || result == 1) {
			return super.postValidate(bigDecimal);
		}
		return new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, IStatus.ERROR, validationFailMessage, null);
	}
}
