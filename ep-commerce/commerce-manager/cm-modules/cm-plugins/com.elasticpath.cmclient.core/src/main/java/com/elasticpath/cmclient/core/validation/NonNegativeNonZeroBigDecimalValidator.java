/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.cmclient.core.validation;

import java.math.BigDecimal;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.elasticpath.cmclient.core.CorePlugin;

/** Checks that the input is greater than zero. */
public class NonNegativeNonZeroBigDecimalValidator extends BigDecimalValidator {
    
	private final String validationFailMessage;
	
	/**
	 * Create a NonNegativeNonZeroBigDecimalValidator that validates if greater than zero.
	 * @param validationFailMessage validation failure message
	 */
	public NonNegativeNonZeroBigDecimalValidator(final String validationFailMessage) {
		this.validationFailMessage = validationFailMessage;
	}

	@Override
	protected IStatus postValidate(final BigDecimal bigDecimal) {
		int result = bigDecimal.compareTo(BigDecimal.valueOf(0));
		// result == 1 if bigDecimal is greater than 0
		if (result == 1) {
			return super.postValidate(bigDecimal);
		}
		return new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, IStatus.ERROR, validationFailMessage, null);
	}
}
