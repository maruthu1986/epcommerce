/**
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.lookups.impl;


import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.elasticpath.rest.resource.dispatch.operator.annotation.Form;
import com.elasticpath.rest.resource.lookups.Items;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Tests Uris constructed by {@link ItemLookupUriBuilderImpl}.
 */
public class ItemLookupUriBuilderImplTest {

	private static final String RESOURCE_SERVER = "lookups";
	private static final String SCOPE = "SCOPE";
	private static final String RFO_URI_PART = "/blah/blah";

	@Test
	public void testBuilduriWhenEverythingSet() {
		String uri = new ItemLookupUriBuilderImpl(RESOURCE_SERVER)
				.setScope(SCOPE)
				.setFormPart()
				.build();

		assertThat(uri)
				.as("Incorrect URI generated")
				.isEqualTo(URIUtil.format(RESOURCE_SERVER, SCOPE, Items.PATH_PART, Form.PATH_PART));
	}

	@Test(expected = AssertionError.class)
	public void testBuilduriWhenScopeMissing() {
		new ItemLookupUriBuilderImpl(RESOURCE_SERVER)
				.setFormPart()
				.build();
	}

	@Test
	public void testBuildItemSearchUriWhenEverythingSet() {
		String uri = new ItemLookupUriBuilderImpl(RESOURCE_SERVER)
				.setScope(SCOPE)
				.setItemsPart()
				.build();

		assertThat(uri)
				.as("Incorrect URI generated")
				.isEqualTo(URIUtil.format(RESOURCE_SERVER, SCOPE, Items.PATH_PART));
	}

	@Test(expected = AssertionError.class)
	public void testBuildItemSearchUriWhenScopeMissing() {
		new ItemLookupUriBuilderImpl(RESOURCE_SERVER)
				.setItemsPart()
				.build();
	}

	@Test
	public void testBuildItemRfoUriWhenEverythingSet() {
		String uri = new ItemLookupUriBuilderImpl(RESOURCE_SERVER)
				.setSourceUri(RFO_URI_PART)
				.build();

		assertThat(uri)
				.as("Incorrect URI generated")
				.isEqualTo(URIUtil.format(RESOURCE_SERVER, RFO_URI_PART));
	}
}
