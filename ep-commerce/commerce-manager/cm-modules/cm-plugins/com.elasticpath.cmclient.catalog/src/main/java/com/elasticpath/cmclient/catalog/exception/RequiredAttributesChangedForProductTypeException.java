/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.exception;

import com.elasticpath.base.exception.EpServiceException;

/**
 * Exception thrown when the required attributes associated to a productType changed.
 */
public class RequiredAttributesChangedForProductTypeException extends EpServiceException {

	/** Serial version id. */
	public static final long serialVersionUID = 5000000001L;
	
	/**
	 * Default constructor.
	 *
	 * @param message - the message
	 */
	public RequiredAttributesChangedForProductTypeException(final String message) {
		super(message);
	}

	
}
