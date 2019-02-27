package com.elasticpath.rest.resource.items.impl;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.rest.schema.uri.ItemsUriBuilder;
import com.elasticpath.rest.schema.uri.ItemsUriBuilderFactory;

public class ItemsUriBuilderFactoryImplTest {

	private static final String RESOURCE_NAME = "defaultResource";

	private ItemsUriBuilderFactory itemsUriBuilderFactory;

	@Before
	public void setup() {
		itemsUriBuilderFactory = new ItemsUriBuilderFactoryImpl(RESOURCE_NAME);
	}

	@Test
	public void verifyGetMethodReturnsNewUriBuilder() {
		ItemsUriBuilder builder1 = itemsUriBuilderFactory.get();
		ItemsUriBuilder builder2 = itemsUriBuilderFactory.get();

		assertThat(builder1)
				.isNotSameAs(builder2);
	}

}