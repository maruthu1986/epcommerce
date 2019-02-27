/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.store.promotions.editors;

/**
 * Common logic for rule presentation.
 */
public final class RulePresentationHelper {
	
	private RulePresentationHelper() {
		// Private ctor as everything is static.
	}
	
	/**
	 * Formats the given <code>String</code> by omitting strings inside rule parameters. E.g. "Discount of [discountPercent]%" is formatted to
	 * "Discount of []%"
	 * 
	 * @param string the string to format
	 * @return the formatted string
	 */
	public static String toMenuDisplayString(final String string) {
		final String regularExpression = "\\[\\S*\\]"; //$NON-NLS-1$
		final String brackets = "[] "; //$NON-NLS-1$
		String regExpReplaced = string.replaceAll(regularExpression, brackets);
		
		int newlineIndex = regExpReplaced.indexOf('\n');
		if (newlineIndex == -1) {
			return regExpReplaced;
		}

		return regExpReplaced.substring(0, newlineIndex);
	}
}
