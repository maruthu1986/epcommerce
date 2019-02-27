/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.profiles.relationships;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import com.google.common.collect.Iterators;
import io.reactivex.Observable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.rest.definition.profiles.DefaultProfileIdentifier;
import com.elasticpath.rest.id.type.StringIdentifier;

@RunWith(MockitoJUnitRunner.class)
public class DefaultProfileFromRootRelationshipImplTest {

	private static final String SCOPE_1  = "scope1";
	private static final String SCOPE_2  = "scope2";

	@Mock
	private Iterable<String> scopes;

	@InjectMocks
	private DefaultProfileFromRootRelationshipImpl fixture;

	@Test
	public void shouldReturnLinkToProfile() {
		when(scopes.iterator()).thenReturn(Iterators.forArray(SCOPE_1, SCOPE_2));

		final Observable<DefaultProfileIdentifier> result =  fixture.onLinkTo();
		final DefaultProfileIdentifier actualIdentifier = result.blockingFirst();
		final DefaultProfileIdentifier expectedIdentifier = DefaultProfileIdentifier.builder()
																	.withScope(StringIdentifier.of(SCOPE_1))
																	.build();

		assertEquals(expectedIdentifier, actualIdentifier);
	}
}