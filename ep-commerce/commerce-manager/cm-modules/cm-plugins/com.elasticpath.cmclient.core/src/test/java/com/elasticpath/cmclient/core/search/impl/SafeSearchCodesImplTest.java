/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.search.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import com.elasticpath.cmclient.core.search.SafeSearchCodes;
import com.elasticpath.domain.catalog.Catalog;

/**
 * Test for SafeSearchCodesImpl.
 */
public class SafeSearchCodesImplTest {
	
	private SafeSearchCodes safeSearchCodes;

	@Rule
	public final MockitoRule rule = MockitoJUnit.rule();
	
	/**
	 * The setup method.
	 */
	@Before
	public void setUp() {
		safeSearchCodes = new SafeSearchCodesImpl();
	}
	
	/**
	 * Test the default Set contains one default dummy code.
	 */
	@Test
	public void testAsSet() {
		assertSame("the default set should have the dummy code", 1, safeSearchCodes.asSet().size());   //$NON-NLS-1$
	}

	/**
	 * Test extract and add one object. 
	 */
	@Test
	public void testExtractAndAddObject() {
		final Catalog catalog = mock(Catalog.class);

		when(catalog.getCode()).thenReturn("testcode");
		
		safeSearchCodes.extractAndAdd(catalog, "code");   //$NON-NLS-1$
		Set<String> set = safeSearchCodes.asSet();
		assertTrue("The set contains the expected code", set.contains("testcode"));    //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals("The return set size is expected", 2, set.size()); //$NON-NLS-1$
		verify(catalog).getCode();
 	}

	/**
	 * Test extract and add a collection.
	 */
	@Test
	public void testExtractAndAddCollectionString() {
		final Catalog catalog1 = mock(Catalog.class);
		final Catalog catalog2 = mock(Catalog.class);
		List<Catalog> source = new ArrayList<Catalog>();
		source.add(catalog1);
		source.add(catalog2);
		when(catalog1.getCode()).thenReturn("catalog1");  //$NON-NLS-1$
		when(catalog2.getCode()).thenReturn("catalog2");  //$NON-NLS-1$

		safeSearchCodes.extractAndAdd(source, "code");   //$NON-NLS-1$
		Set<String> set = safeSearchCodes.asSet();
		assertTrue("The set contains the expected code", set.contains("catalog1"));    //$NON-NLS-1$ //$NON-NLS-2$
		assertTrue("The set contains the expected code", set.contains("catalog2"));    //$NON-NLS-1$ //$NON-NLS-2$
		final int expectedSize = 3;
		assertEquals("The return set size is expected", expectedSize, set.size()); //$NON-NLS-1$	
		verify(catalog1).getCode();
		verify(catalog2).getCode();
	}

}
