/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.search.index.loader.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.search.index.pipeline.IndexingStage;
import com.elasticpath.search.index.pipeline.stats.impl.PipelinePerformanceImpl;
import com.elasticpath.service.catalog.ProductSkuLookup;

/**
 * Test {@link BatchProductSkuLoader}.
 */
public class BatchProductSkuLoaderTest {
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	
	private final ProductSkuLookup skuLookup = context.mock(ProductSkuLookup.class);
	
	@SuppressWarnings("unchecked")
	private final IndexingStage<ProductSku, ?> nextStage = context.mock(IndexingStage.class);
	
	private BatchProductSkuLoader batchProductSkuLookup;
	
	/**
	 * Initialize the services on the {@link BatchProductSkuLoader}.
	 */
	@Before
	public void setUpBulkProductFetcher() {
		batchProductSkuLookup = new BatchProductSkuLoader();
		batchProductSkuLookup.setPipelinePerformance(new PipelinePerformanceImpl());
		batchProductSkuLookup.setSkuLookup(skuLookup);
	}

	/**
	 * Test that loading a set of product skus works.
	 */
	@Test
	public void testLoadingValidList() {
		
		final List<ProductSku> productSkus = new ArrayList<>();
		final ProductSku firstProductSku = context.mock(ProductSku.class, "first");
		final ProductSku secondProductSku = context.mock(ProductSku.class, "second");
		productSkus.add(firstProductSku);
		productSkus.add(secondProductSku);

		final Set<Long> uidsToLoad = createUidsToLoad();

		context.checking(new Expectations() { {
			allowing(skuLookup).findByUids(with(uidsToLoad));
			will(returnValue(productSkus));

			oneOf(nextStage).send(with(firstProductSku));
			oneOf(nextStage).send(with(secondProductSku));
		} });

		batchProductSkuLookup.setBatch(uidsToLoad);
		batchProductSkuLookup.setNextStage(nextStage);
		batchProductSkuLookup.run();
	}
	
	/**
	 * Test sending an empty set of product sku uids to load returns an empty list.
	 */
	@Test
	public void testLoadingNoSkus() {
		batchProductSkuLookup.setBatch(new HashSet<>());
		batchProductSkuLookup.setNextStage(nextStage);
		batchProductSkuLookup.run();
	}
	
	
	/**
	 * Test that trying to load a set of product skus without setting the next stage fails.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testLoadingInvalidNextStage() {
		batchProductSkuLookup.setBatch(createUidsToLoad());
		batchProductSkuLookup.run();
	}

	private Set<Long> createUidsToLoad() {
		final Set<Long> uidsToLoad = new HashSet<>();
		uidsToLoad.add(Long.valueOf(1));
		return uidsToLoad;
	}
	
}
