/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.tools.sync.job;

/**
 * Represents a command to run. Add and Update are logically equal.
 */
public enum Command {
	/** ADD or UPDATE.*/
	UPDATE,
	/** REMOVE.*/
	REMOVE
}
