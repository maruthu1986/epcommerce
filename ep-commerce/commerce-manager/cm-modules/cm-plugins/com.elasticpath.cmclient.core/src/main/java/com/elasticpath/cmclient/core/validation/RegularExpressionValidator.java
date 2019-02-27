/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.elasticpath.cmclient.core.CorePlugin;

/**
 * A common validator for regular expression string.
 */
public class RegularExpressionValidator implements IValidator {
	private final String errorMsg;
	private final Pattern pattern;
	
	/**
	 * Constructor.
	 * @param regularExpression - regular expression against which value will be validated.
	 * @param errorMsg - error message that will be thrown on error.
	 */
	public RegularExpressionValidator(final String regularExpression, final String errorMsg) {
		this.errorMsg = errorMsg;
		pattern = Pattern.compile(regularExpression);
	}
	
	/**
	 * Validates value against regular expression.
	 * @param value - value to be validated.
	 * @return - validation status. 
	 */
	public IStatus validate(final Object value) {
		final String stringValue = (String) value;
		
		final Matcher matcher = pattern.matcher(stringValue);
		if (!matcher.matches()) {
			return new Status(
					IStatus.ERROR,
					CorePlugin.PLUGIN_ID,
					IStatus.ERROR,
					errorMsg,
					null);
		}
		
		return Status.OK_STATUS;
	}
}
