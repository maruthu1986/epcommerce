/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemdefinitions.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.elasticpath.rest.resource.dispatch.operator.annotation.Options;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Test class for {@link ItemDefinitionsOptionUriBuilderImpl}.
 */
public final class ItemDefinitionsOptionUriBuilderImplTest {

	private static final String OPTION_ID = "option_id";
	private static final String ITEM_ID = "item_id";
	private static final String SCOPE = "scope";
	private static final String ITEMDEFINITIONS = "itemdefinitions";

	/**
	 * Test uri builder.
	 */
	@Test
	public void testUriBuilder() {
		ItemDefinitionsOptionUriBuilderImpl builder =
				new ItemDefinitionsOptionUriBuilderImpl(ITEMDEFINITIONS);

		String uri = builder.setScope(SCOPE)
				.setItemId(ITEM_ID)
				.setOptionId(OPTION_ID)
				.build();

		String expectedUri = URIUtil.format(ITEMDEFINITIONS, SCOPE, ITEM_ID, Options.URI_PART, OPTION_ID);
		assertEquals(expectedUri, uri);
	}
}
