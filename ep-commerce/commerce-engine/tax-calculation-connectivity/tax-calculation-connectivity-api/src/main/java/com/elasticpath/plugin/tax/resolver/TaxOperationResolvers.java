/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.plugin.tax.resolver;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * A map of {@link TaxOperationResolver}s that provide EP platform tax functionality to tax provider plugins.
 *
 */
public class TaxOperationResolvers {
	
	private final Map<Class<? extends TaxOperationResolver>, Object> resolvers =
		new HashMap<>();
	
	/**
	 * Puts an instance of a given type into the container.
     *
	 * @param type the class type
	 * @param resolver the instance of the class type
	 * @param <T> the type of the element
	 */
	public <T extends TaxOperationResolver> void putResolver(final Class<T> type, final T resolver) {
		if (type != null) {
			resolvers.put(type, resolver);
		}
	}

	/**
	 * Gets an instance of a given type from the container.
     *
	 * @param type the class type
	 * @param <T> the type of the element
	 * @return <T> an instance of the given class type
	 */
	public <T extends TaxOperationResolver> T getResolver(final Class<T> type) {
		return type.cast(resolvers.get(type));
	}
}
