/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.service.catalogview;

import com.elasticpath.domain.catalogview.sitemap.SitemapRequest;
import com.elasticpath.domain.catalogview.sitemap.SitemapResult;
import com.elasticpath.domain.customer.CustomerSession;

/**
 * Provide sitemap service.
 */
public interface SitemapService {

	/**
	 * Retrieves sitemap listing based on the given sitemap request and returns the sitemap result.
	 *
	 * @param sitemapRequest the sitemap request
	 * @param customerSession the customer session
	 * @param pageNumber the current page number of the result   @return a <code>SitemapResult</code> instance
	 * @return the sitemap result
	 */
	SitemapResult sitemap(SitemapRequest sitemapRequest, CustomerSession customerSession, int pageNumber);

}
