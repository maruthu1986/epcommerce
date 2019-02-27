/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.search.index.solr.builders.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.elasticpath.domain.search.IndexNotification;
import com.elasticpath.search.index.pipeline.IndexingPipeline;
import com.elasticpath.service.catalog.ProductSkuService;
import com.elasticpath.service.search.IndexType;
import com.elasticpath.service.search.solr.SolrIndexConstants;

/**
 * An {@code IndexBuilder} to create an index for SKUs.
 */
public class SkuIndexBuilder extends AbstractIndexBuilder {

	private ProductSkuService skuService;

	private IndexingPipeline<Collection<Long>> skuIndexingPipeline;

	/**
	 * Publishes updates to the Solr server for the specified {@link ProductSku} uids.
	 * 
	 * @param uids the {@link ProductSku} uids to publish.
	 */
	@Override
	public void submit(final Collection<Long> uids) {
		skuIndexingPipeline.start(uids);
	}

	/**
	 * Retrieve added or modified UIDs since last build. This implementation retrieves the UIDs of skus whose products have been directly
	 * added/modified
	 * 
	 * @param lastBuildDate the last build date
	 * @return added or modified UIDs
	 */
	@Override
	public List<Long> findAddedOrModifiedUids(final Date lastBuildDate) {
		final List<Long> modifiedSkuUids = getSkuService().findUidsByProductLastModifiedDate(lastBuildDate);
		return new ArrayList<>(modifiedSkuUids);
	}

	@Override
	public List<Long> findAllUids() {
		return getSkuService().findAllUids();
	}

	/**
	 * @param lastBuildDate the last build date
	 * @return the uids of deleted skus
	 */
	@Override
	public List<Long> findDeletedUids(final Date lastBuildDate) {
		return skuService.findSkuUidsByDeletedDate(lastBuildDate);
	}

	/**
	 * This operation is not supported.
	 * 
	 * @param notification not used.
	 * @return nothing.
	 * @throws UnsupportedOperationException always.
	 */
	@Override
	public Collection<Long> findUidsByNotification(final IndexNotification notification) {
		throw new UnsupportedOperationException("not supported");
	}

	@Override
	public IndexType getIndexType() {
		return IndexType.SKU;
	}

	@Override
	public String getName() {
		return SolrIndexConstants.SKU_SOLR_CORE;
	}

	/**
	 * Set the sku service.
	 * 
	 * @param skuService the sku service
	 */
	public void setSkuService(final ProductSkuService skuService) {
		this.skuService = skuService;
	}

	/**
	 * Get the SKU service.
	 * 
	 * @return the sku service
	 */
	protected ProductSkuService getSkuService() {
		return skuService;
	}

	/**
	 * @param skuIndexingPipeline the skuIndexingPipeline to set
	 */
	public void setSkuIndexingPipeline(final IndexingPipeline<Collection<Long>> skuIndexingPipeline) {
		this.skuIndexingPipeline = skuIndexingPipeline;
	}

	/**
	 * @return the skuIndexingPipeline
	 */
	public IndexingPipeline<Collection<Long>> getSkuIndexingPipeline() {
		return skuIndexingPipeline;
	}

}
