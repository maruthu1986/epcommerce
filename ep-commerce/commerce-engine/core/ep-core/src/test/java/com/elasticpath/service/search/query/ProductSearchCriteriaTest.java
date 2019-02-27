/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.service.search.query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

/**
 * Test <code>ProductSearchCriteria</code>.
 */
public class ProductSearchCriteriaTest {


	private ProductSearchCriteria productSearchCriteria;

	/**
	 * Prepare for the tests.
	 * 
	 * @throws Exception on error
	 */
	@Before
	public void setUp() throws Exception {
		this.productSearchCriteria = new ProductSearchCriteria();
	}

	/**
	 * Test method for
	 * 'com.elasticpath.service.search.query.ProductSearchCriteria.getBrandCode()'.
	 */
	@Test
	public void testGetBrandCode() {
		assertNull(this.productSearchCriteria.getBrandCode());
	}

	/**
	 * Test method for
	 * 'com.elasticpath.service.search.query.ProductSearchCriteria.setBrandCode(String)'.
	 */
	@Test
	public void testSetBrandCode() {
		final String brandCode = "brandCode";
		this.productSearchCriteria.setBrandCode(brandCode);
		assertEquals(brandCode, this.productSearchCriteria.getBrandCode());

	}

	/**
	 * Test method for
	 * 'com.elasticpath.service.search.query.ProductSearchCriteria.isActiveOnly()'.
	 */
	@Test
	public void testIsActiveOnly() {
		assertFalse(this.productSearchCriteria.isActiveOnly());
	}

	/**
	 * Test method for
	 * 'com.elasticpath.service.search.query.ProductSearchCriteria.setActive(boolean)'.
	 */
	@Test
	public void testSetActiveOnly() {
		this.productSearchCriteria.setActiveOnly(true);
		assertTrue(this.productSearchCriteria.isActiveOnly());
	}

	/**
	 * Test method for
	 * 'com.elasticpath.service.search.query.ProductSearchCriteria.getCategoryUid()'.
	 */
	@Test
	public void testGetCategoryUid() {
		assertNull(this.productSearchCriteria.getAncestorCategoryUids());
	}

	/**
	 * Test method for
	 * 'com.elasticpath.service.search.query.ProductSearchCriteria.setCategoryUid(long)'.
	 */
	@Test
	public void testSetCategoryUid() {
		final long categoryUid = Long.MAX_VALUE;
		Set<Long> categoryUids = new HashSet<>(Arrays.asList(new Long[]{new Long(categoryUid)}));
		this.productSearchCriteria.setAncestorCategoryUids(categoryUids);
		assertEquals(categoryUids, this.productSearchCriteria.getAncestorCategoryUids());
	}

	/**
	 * Test method for
	 * 'com.elasticpath.service.search.query.ProductSearchCriteria.isInActiveOnly()'.
	 */
	@Test
	public void testIsInActiveOnly() {
		assertFalse(this.productSearchCriteria.isInActiveOnly());
	}

	/**
	 * Test method for
	 * 'com.elasticpath.service.search.query.ProductSearchCriteria.setInActive(boolean)'.
	 */
	@Test
	public void testSetInActiveOnly() {
		this.productSearchCriteria.setInActiveOnly(true);
		assertTrue(this.productSearchCriteria.isInActiveOnly());
	}
}
