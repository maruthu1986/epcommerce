/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.tools.sync.merge.impl;

import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.tools.sync.exception.SyncToolRuntimeException;

/**
 * Factory class that creates merge exceptions.
 */
public final class MergeExceptionFactory {

	private MergeExceptionFactory() {
		//  Singleton
	}

	/**
	 * Creates a runtime exception that should be thrown when an Entity association cannot be found in the target system.
	 *
	 * @param sourceClass the entity class
	 * @param guid the entity guid
	 *
	 * @return the exception
	 */
	public static RuntimeException createEntityNotFoundException(final Class<? extends Persistable> sourceClass, final String guid) {
		throw new SyncToolRuntimeException("Cannot retrieve entity at target environment corresponding to source environment: "
				+ sourceClass + " with code: " + guid);
	}
}
