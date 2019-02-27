/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.ql.parser;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper class shielding characters with escape symbols.
 */
public final class ShieldUtility {

	private static Map<Character, String> escapeMap = new HashMap<>();

	private ShieldUtility() {
	}

	static {
		escapeMap.put('\\', "\\\\");
		escapeMap.put('+', "\\+");
		escapeMap.put('-', "\\-");
		escapeMap.put('&', "\\&");
		escapeMap.put('|', "\\|");
		escapeMap.put('!', "\\!");
		escapeMap.put('(', "\\(");
		escapeMap.put(')', "\\)");
		escapeMap.put('{', "\\{");
		escapeMap.put('}', "\\}");
		escapeMap.put('[', "\\[");
		escapeMap.put(']', "\\]");
		escapeMap.put('^', "\\^");
		escapeMap.put('\"', "\\&quot;");
		escapeMap.put('~', "\\~");
		escapeMap.put('*', "\\*");
		escapeMap.put('?', "\\?");
		escapeMap.put(':', "\\:");
		escapeMap.put(' ', "\\ ");
	}

	/**
	 * Shields special characters with back slash symbol.
	 *
	 * @param inputString string to shield
	 * @return shielded string
	 */
	public static String shieldString(final String inputString) {
		final StringBuilder buffer = new StringBuilder();
		String inUse;
		for (char letter : inputString.toCharArray()) {
			inUse = escapeMap.get(letter);
			if (inUse == null) {
				buffer.append(letter);
			} else {
				buffer.append(inUse);
			}
		}
		return buffer.toString();
	}
}
