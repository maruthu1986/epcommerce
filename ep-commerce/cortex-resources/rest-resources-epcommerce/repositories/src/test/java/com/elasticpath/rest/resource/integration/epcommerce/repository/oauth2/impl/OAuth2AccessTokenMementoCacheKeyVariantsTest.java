/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.oauth2.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.hamcrest.Matchers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.auth.OAuth2AccessTokenMemento;
import com.elasticpath.rest.util.collection.CollectionUtil;

@RunWith(MockitoJUnitRunner.class)
public class OAuth2AccessTokenMementoCacheKeyVariantsTest {

	OAuth2AccessTokenMementoCacheKeyVariants classUnderTest = new OAuth2AccessTokenMementoCacheKeyVariants();

	@Mock
	OAuth2AccessTokenMemento memento;


	@Test
	public void testGet() throws Exception {
		String tokenId = "tokenId";
		when(memento.getTokenId()).thenReturn(tokenId);
		Collection<Object[]> actual = classUnderTest.get(memento);

		assertThat(actual, Matchers.hasSize(1));
		Object[] data = CollectionUtil.first(actual);
		assertThat(data, Matchers.arrayWithSize(1));
		assertEquals(tokenId, data[0]);
	}

	@Test
	public void testGetType() throws Exception {
		Class<OAuth2AccessTokenMemento> actual = classUnderTest.getType();

		assertEquals(OAuth2AccessTokenMemento.class, actual);
	}
}