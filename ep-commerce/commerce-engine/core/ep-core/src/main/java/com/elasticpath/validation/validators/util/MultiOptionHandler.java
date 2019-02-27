/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.validation.validators.util;

import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Util class for getting an array of invalid options for
 * {@link com.elasticpath.domain.cartmodifier.CartItemModifierType#PICK_MULTI_OPTION} type.
 */
public final class MultiOptionHandler {

	private MultiOptionHandler() {
		//nothing
	}

	/**
	 * Get invalid options.
	 *
	 * @param optionsToValidate CSV options to validate.
	 * @param validOptions      an array of valid options.
	 * @return an array of invalid options or empty array.
	 */
	public static String[] getInvalidOptions(final String optionsToValidate, final String[] validOptions) {
		if (isOptionsEmptyBlankOrNull(optionsToValidate, validOptions)) {
			return null;
		}

		final String[] trimmedSelectedOptions = Arrays
				.stream(optionsToValidate.split(","))
				.map(String::trim)
				.toArray(String[]::new);

		return ArrayUtils.removeElements(trimmedSelectedOptions, validOptions);
	}

	/**
	 * Determine if options to validate are blank or null or if valid options are empty or null.
	 *
	 * @param optionsToValidate CSV options to validate
	 * @param validOptions      an array of valid options
	 * @return true if options are blank, empty or null, false otherwise
	 */
	public static boolean isOptionsEmptyBlankOrNull(final String optionsToValidate, final String[] validOptions) {
		return StringUtils.isBlank(optionsToValidate) || ArrayUtils.isEmpty(validOptions);
	}
}
