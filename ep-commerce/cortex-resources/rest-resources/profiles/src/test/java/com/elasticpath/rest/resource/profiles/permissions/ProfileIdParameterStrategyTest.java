/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.profiles.permissions;

import static org.junit.Assert.assertEquals;

import java.security.Principal;
import java.util.Collection;
import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.rest.definition.profiles.ProfileIdentifier;
import com.elasticpath.rest.id.Identifier;
import com.elasticpath.rest.id.transform.IdentifierTransformer;
import com.elasticpath.rest.id.transform.IdentifierTransformerProvider;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.identity.UserPrincipal;
import com.elasticpath.rest.id.util.Base32Util;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test class for {@link ProfileIdParameterStrategy}.
 */
@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings({"unchecked", "rawtypes"})
public final class ProfileIdParameterStrategyTest {

	private static final String TEST_REALM = "testRealm";
	private static final String DECODED_PROFILE_ID = "7F4E992F-9CFC-E648-BA11-DF1D5B23968F";

	@Mock
	private IdentifierTransformerProvider identifierTransformerProvider;
	@Mock
	private IdentifierTransformer identifierTransformer;

	@InjectMocks
	private ProfileIdParameterStrategy fixture;
	/**
	 * Test get profile id parameter.
	 */
	@Test
	public void testGetProfileIdParameterValue() {
		final Identifier profileIdentifier = StringIdentifier.of(DECODED_PROFILE_ID);

		when(identifierTransformerProvider.forUriPart(ProfileIdentifier.PROFILE_ID)).thenReturn(identifierTransformer);
		when(identifierTransformer.identifierToUri(profileIdentifier)).thenReturn(Base32Util.encode(DECODED_PROFILE_ID));

		final Collection<Principal> principalCollection = Collections.singleton(new UserPrincipal(DECODED_PROFILE_ID));
		final PrincipalCollection principals = new SimplePrincipalCollection(principalCollection, TEST_REALM);

		final String expectedProfileId = Base32Util.encode(DECODED_PROFILE_ID);
		final String actualProfileIdString = fixture.getParameterValue(principals);

		assertEquals(expectedProfileId, actualProfileIdString);

		verify(identifierTransformerProvider).forUriPart(ProfileIdentifier.PROFILE_ID);
		verify(identifierTransformer).identifierToUri(profileIdentifier);

	}
}
