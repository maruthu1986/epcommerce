/**
 * Copyright (c) Elastic Path Software Inc., 2011
 */
package com.elasticpath.service.pricing.datasource;

/**
 * A factory for creating {@link BaseAmountDataSource}s.
 */
public interface BaseAmountDataSourceFactory {
	
	/**
	 * Creates a {@link BaseAmountDataSource} using the backing data source passed. Implementations might decide to return 
	 * different instances upon subsequent calls. The may also return the same result as they did for the first call.   
	 *
	 * @param backingDataSource the data source to be used by the resulting data source. In case the resulting data source does not know how to
	 * retrieve the base amounts, it can use this backing data source.   
	 * @return a {@link BaseAmountDataSource}
	 */
	BaseAmountDataSource createDataSource(BaseAmountDataSource backingDataSource);
}
