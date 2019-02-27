/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.schema.uri;

/**
 * Builds a URI to a specific profile.
 */
public interface ProfilesUriBuilder extends ScopedUriBuilder<ProfilesUriBuilder> {

	/**
	 * Set the profile ID.
	 *
	 * @param profileId the profile ID
	 * @return this builder
	 */
	ProfilesUriBuilder setProfileId(String profileId);
}
