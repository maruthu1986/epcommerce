/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.cmclient.core.validation;

import java.math.BigDecimal;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.elasticpath.cmclient.core.CorePlugin;

/** Checks that the input is less than or equal to zero. */
public class NonPositiveBigDecimalValidator extends BigDecimalValidator {
    
	private final String validationFailMessage;
	
	/**
	 * Create a NonPositiveBigDecimalValidator that validates if negative.
	 * @param validationFailMessage validation failure message
	 */
	public NonPositiveBigDecimalValidator(final String validationFailMessage) {
		this.validationFailMessage = validationFailMessage;
	}

	@Override
	protected IStatus postValidate(final BigDecimal bigDecimal) {
		int result = bigDecimal.compareTo(BigDecimal.valueOf(0));
		// result == -1 if bigDecimal is less than 0, result == 0 if bigDecimal is 0
		if (result == -1 || result == 0) {
			return super.postValidate(bigDecimal);
		}
		return new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, IStatus.ERROR, validationFailMessage, null);
	}
}
