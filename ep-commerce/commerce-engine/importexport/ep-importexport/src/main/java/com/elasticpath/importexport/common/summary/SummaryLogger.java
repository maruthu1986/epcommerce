/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.importexport.common.summary;

import com.elasticpath.importexport.common.util.Message;

/**
 * Represents interface for handling error messages and comments.
 */
public interface SummaryLogger extends Summary {

	/**
	 * Adds new failure.

	 * @param failure string which describes a failure
	 */
	void addFailure(Message failure);

	/**
	 * Adds new warning.

	 * @param warning string which describes a warning
	 */
	void addWarning(Message warning);


	/**
 	 * Adds new comment.
 	 *
	 * @param comment string which describes a comment
	 */
	void addComment(Message comment);

}
