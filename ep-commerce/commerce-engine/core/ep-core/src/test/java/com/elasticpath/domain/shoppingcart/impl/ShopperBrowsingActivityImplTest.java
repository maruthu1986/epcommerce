/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.domain.shoppingcart.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.util.Collections;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalogview.CatalogViewResultHistory;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Test class for {@link com.elasticpath.domain.shoppingcart.impl.ShopperBrowsingActivityImpl}.
 */
public class ShopperBrowsingActivityImplTest {

	private ShopperBrowsingActivityImpl shopperBrowsingActivity;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	@Mock
	private BeanFactory beanFactory;

	private BeanFactoryExpectationsFactory beanFactoryExpectationsFactory;

	@Before
	public void setUp() {
		beanFactoryExpectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);

		shopperBrowsingActivity = new ShopperBrowsingActivityImpl();
		shopperBrowsingActivity.setBeanFactory(beanFactory);

		final CatalogViewResultHistory catalogViewResultHistory = context.mock(CatalogViewResultHistory.class);

		beanFactoryExpectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.CATALOG_VIEW_RESULT_HISTORY, catalogViewResultHistory);

		context.checking(new Expectations() {
			{
				allowing(catalogViewResultHistory).getResultList();
				will(returnValue(Collections.emptyList()));
			}
		});
	}

	/**
	 * Test getSearchResultHistory() method.
	 */
	@Test
	public void testGetSearchResultHistory() {
		CatalogViewResultHistory catalogViewResultHistory = shopperBrowsingActivity.getSearchResultHistory();
		assertNotNull(catalogViewResultHistory);

		// Should get the same search results next timet
		assertSame(catalogViewResultHistory, shopperBrowsingActivity.getSearchResultHistory());
		assertSame(catalogViewResultHistory, shopperBrowsingActivity.getCatalogViewResultHistory());
	}

	/**
	 * Test getBrowsingResultHistory() method.
	 */
	@Test
	public void testGetBrowsingResultHistory() {
		CatalogViewResultHistory catalogViewResultHistory = shopperBrowsingActivity.getBrowsingResultHistory();
		assertNotNull(catalogViewResultHistory);
		assertEquals(0, shopperBrowsingActivity.getSearchResultHistory().getResultList().size());

		// Switch to search result history will clear the browsing history.
		CatalogViewResultHistory searchResultHistory = shopperBrowsingActivity.getSearchResultHistory();
		assertSame(searchResultHistory, shopperBrowsingActivity.getCatalogViewResultHistory());
	}

}