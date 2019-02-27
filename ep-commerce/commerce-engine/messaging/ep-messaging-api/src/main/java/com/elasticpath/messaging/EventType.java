/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.messaging;

import java.io.Serializable;

/**
 * Represents an application event.
 */
public interface EventType extends Serializable {

	/**
	 * Gets the name of any EventType implementation as a String.
	 * 
	 * @return the name
	 */
	String getName();

}
