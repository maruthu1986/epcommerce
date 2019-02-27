/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.importexport.common.summary;

/**
 * SummaryLayout Interface.
 */
public interface SummaryLayout {

	/**
	 * Makes formatted output of summary.
	 *
	 * @param summary the summary for formatted output
	 * @return formatted output
	 */
	String format(Summary summary);
}
