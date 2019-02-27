/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.core.formatting;

import java.text.SimpleDateFormat;

/**
 * Date formatter for dates stored in metadata as a string.
 * This uses a locale-agnostic format string.
 */
public class MetadataDateFormat extends SimpleDateFormat {
	private static final long serialVersionUID = 1207761583144749989L;

	/**
	 * Constructs a <code>MetadataDateFormat</code> with a locale-agnostic pattern.
	 */
	@SuppressWarnings("PMD.SimpleDateFormatNeedsLocale") // format is locale agnostic
	public MetadataDateFormat() {
		super("yyyyMMddHHmmssSSS"); //$NON-NLS-1$
	}

}
