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

import com.elasticpath.domain.catalog.Category;
import com.elasticpath.search.index.pipeline.IndexingStage;
import com.elasticpath.search.index.pipeline.stats.impl.PipelinePerformanceImpl;
import com.elasticpath.service.catalog.CategoryLookup;

/**
 * Tests {@link BatchCategoryLoader}.
 */
public class BatchCategoryLoaderTest {
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	
	private final CategoryLookup categoryLookup = context.mock(CategoryLookup.class);
	@SuppressWarnings("unchecked")
	private final IndexingStage<Category, ?> nextStage = context.mock(IndexingStage.class);
	private BatchCategoryLoader batchCategoryLoader;
	
	/**
	 * Initialize the services on the {@link BatchCategoryLoader}.
	 */
	@Before
	public void setUp() {
		batchCategoryLoader = new BatchCategoryLoader();
		batchCategoryLoader.setCategoryLookup(categoryLookup);
		batchCategoryLoader.setPipelinePerformance(new PipelinePerformanceImpl());
	}
	
	/**
	 * Test that loading a set of categories works.
	 */
	@Test
	public void testLoadingValidList() {
		
		final List<Category> categories = new ArrayList<>();
		final Category firstCategory = context.mock(Category.class, "first");
		final Category secondCategory = context.mock(Category.class, "second");
		categories.add(firstCategory);
		categories.add(secondCategory);
		
		final Set<Long> uidsToLoad = createUidsToLoad();

		context.checking(new Expectations() { {
			allowing(categoryLookup).findByUids(with(uidsToLoad));
			will(returnValue(categories));

			oneOf(nextStage).send(with(firstCategory));
			oneOf(nextStage).send(with(secondCategory));
		} });

		batchCategoryLoader.setBatch(uidsToLoad);
		batchCategoryLoader.setNextStage(nextStage);
		batchCategoryLoader.run();
	}

	/**
	 * Test sending an empty set of {@link Category} uids to load returns an empty list.
	 */
	@Test
	public void testLoadingNoCategories() {
		batchCategoryLoader.setBatch(new HashSet<>());
		batchCategoryLoader.setNextStage(nextStage);
		batchCategoryLoader.run();
	}
	
	/**
	 * Test that trying to load a set of {@link Category}s without setting the next stage fails.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testLoadingInvalidNextStage() {
		batchCategoryLoader.setBatch(createUidsToLoad());
		batchCategoryLoader.run();
	}
	
	private Set<Long> createUidsToLoad() {
		final Set<Long> uidsToLoad = new HashSet<>();
		uidsToLoad.add(Long.valueOf(1));
		return uidsToLoad;
	}
	
}
