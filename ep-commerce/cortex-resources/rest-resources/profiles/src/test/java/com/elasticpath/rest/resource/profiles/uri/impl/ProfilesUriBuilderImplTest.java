/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.profiles.uri.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
/**
 * Test class for {@link com.elasticpath.rest.resource.profiles.uri.impl.ProfilesUriBuilderImpl}.
 */
public class ProfilesUriBuilderImplTest {

	@Test
	public void shouldBuildProfilesUriWhenScopeAndProfileIdExists() {
		final ProfilesUriBuilderImpl fixture = new ProfilesUriBuilderImpl();
		fixture.setScope("scope");
		fixture.setProfileId("profileId");

		final String expectedProfileId = "/profiles/scope/profileid";

		assertEquals(expectedProfileId, fixture.build());
	}

	@Test (expected = AssertionError.class)
	public void shouldFailWhenScopeIsMissing() {
		final ProfilesUriBuilderImpl fixture = new ProfilesUriBuilderImpl();
		fixture.setProfileId("profileId");

		fixture.build();
	}

	@Test (expected = AssertionError.class)
	public void shouldFailWhenProfileIdIsMissing() {
		final ProfilesUriBuilderImpl fixture = new ProfilesUriBuilderImpl();
		fixture.setScope("scope");

		fixture.build();
	}
}
