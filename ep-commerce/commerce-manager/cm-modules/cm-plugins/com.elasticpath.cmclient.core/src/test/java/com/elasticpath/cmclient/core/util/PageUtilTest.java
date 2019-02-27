/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * The junit test class of PageUtil.
 */
public class PageUtilTest {
	
	/**
	 * Test the getPage method.
	 */
	@Test
	public void testGetPage() {
		final int numberOfEachPage = 10;
		
		final int itemOne = 1;
		assertEquals("item 1 should in page 1", 1, PageUtil.getPage(itemOne, numberOfEachPage)); //$NON-NLS-1$
		
		final int itemTen = 10;
		assertEquals("item 10 should in page 1", 1, PageUtil.getPage(itemTen, numberOfEachPage)); //$NON-NLS-1$
		
		final int itemEleven = 11;
		assertEquals("item 11 should in page 2", 2, PageUtil.getPage(itemEleven, numberOfEachPage)); //$NON-NLS-1$
		
		final int itemTwenty = 20;
		assertEquals("item 20 should in page 2", 2, PageUtil.getPage(itemTwenty, numberOfEachPage)); //$NON-NLS-1$
		
	}

}
