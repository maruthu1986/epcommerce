/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.profiles.uri.impl;

import com.elasticpath.rest.schema.uri.ProfilesUriBuilder;
import com.elasticpath.rest.uri.URIUtil;


/**
 * Builds the URI pointing to the specific profile.
 * TODO delete once there are no more dependencies on ProfilesUriBuilderFactory class
 */
public final class ProfilesUriBuilderImpl implements ProfilesUriBuilder {

	private static final String RESOURCE_SERVER_NAME = "profiles";

	private String scope;
	private String profileId;


	@Override
	public ProfilesUriBuilderImpl setScope(final String scope) {
		this.scope = scope;
		return this;
	}

	@Override
	public ProfilesUriBuilderImpl setProfileId(final String profileId) {
		this.profileId = profileId;
		return this;
	}

	@Override
	public String build() {
		assert scope != null : "scope required.";
		assert profileId != null : "profileId required.";
		return URIUtil.format(RESOURCE_SERVER_NAME, scope, profileId);
	}
}
