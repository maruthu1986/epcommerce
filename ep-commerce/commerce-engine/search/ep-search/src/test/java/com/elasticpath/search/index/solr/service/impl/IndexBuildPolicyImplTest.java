/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.search.index.solr.service.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.search.index.solr.service.IndexBuildPolicyContext;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.search.IndexType;
import com.elasticpath.settings.provider.SettingValueProvider;

/**
 * Test cases for {@link IndexBuildPolicyImpl}.
 */
public class IndexBuildPolicyImplTest {

	private IndexBuildPolicyImpl indexPolicy;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private TimeService timeService;

	private SettingValueProvider<Integer> settingOptimizationIntervalProvider;

	private IndexBuildPolicyContext indexContext;

	/**
	 * Sets up the test case.
	 */
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() {
		indexPolicy = new IndexBuildPolicyImpl();
		
		timeService = context.mock(TimeService.class);
		settingOptimizationIntervalProvider = context.mock(SettingValueProvider.class);
		indexContext = new IndexBuildPolicyContext();
		
		indexContext.setIndexType(IndexType.CATEGORY);
		
		indexPolicy.setTimeService(timeService);
		indexPolicy.setSettingOptimizationIntervalProvider(settingOptimizationIntervalProvider);
	}

	/**
	 * Tests the happy case of isOptimizationRequired() when no check has been performed.
	 * The expectation is that optimization won't be triggered on the first check.
	 */
	@Test
	public void testIsOptimizationRequired() {
		context.checking(new Expectations() { {
			oneOf(settingOptimizationIntervalProvider).get(IndexType.CATEGORY.getIndexName());
			will(returnValue(1));

			exactly(2).of(timeService).getCurrentTime();
			will(returnValue(new Date()));
		} });
		
		assertFalse("Timeout has not elapsed. Should not be eligible for optimization",
				indexPolicy.isOptimizationRequired(indexContext));
	}

	/**
	 * Tests the happy case of isOptimizationRequired() when a check has already 
	 * been performed and another one is done some time after the timeout is expected to have elapsed.
	 */
	@Test
	public void testIsOptimizationRequiredWhenTimeoutHasElapsed() {
		context.checking(new Expectations() { {
			oneOf(settingOptimizationIntervalProvider).get(IndexType.CATEGORY.getIndexName());
			will(returnValue(1));
			
			exactly(2).of(timeService).getCurrentTime();
			will(returnValue(DateUtils.addMinutes(new Date(), 0)));
		} });
		
		
		assertFalse("Timeout has not elapsed. Should not be eligible for optimization",
				indexPolicy.isOptimizationRequired(indexContext));
		
		// the next expectations are set in the future - 2 seconds after the first call
		context.checking(new Expectations() { {
			oneOf(settingOptimizationIntervalProvider).get(IndexType.CATEGORY.getIndexName());
			will(returnValue(1));

			// first invocation is for the check
			oneOf(timeService).getCurrentTime();
			will(returnValue(DateUtils.addMinutes(new Date(), 2)));

			// the second is for setting the last last optimization check
			oneOf(timeService).getCurrentTime();
			will(returnValue(DateUtils.addMinutes(new Date(), 2)));
		} });
		assertTrue("Timeout must have elapsed. Should be eligible for optimization",
				indexPolicy.isOptimizationRequired(indexContext));
	}

}
