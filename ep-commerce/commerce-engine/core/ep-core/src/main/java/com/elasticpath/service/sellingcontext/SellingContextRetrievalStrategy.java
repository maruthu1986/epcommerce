/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.service.sellingcontext;

import com.elasticpath.domain.sellingcontext.SellingContext;

/**
 * The interface for selling context retrieval strategy.
 */
public interface SellingContextRetrievalStrategy {

	/**
	 * Get the selling context by the guid.
	 *
	 * @param guid the selling context guid
	 * @return the selling context
	 */
	SellingContext getByGuid(String guid);

}
