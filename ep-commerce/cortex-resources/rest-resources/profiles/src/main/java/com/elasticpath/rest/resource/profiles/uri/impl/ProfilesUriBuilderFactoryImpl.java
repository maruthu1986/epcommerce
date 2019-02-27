/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.profiles.uri.impl;

import javax.inject.Provider;

import org.osgi.service.component.annotations.Component;

import com.elasticpath.rest.schema.uri.ProfilesUriBuilder;
import com.elasticpath.rest.schema.uri.ProfilesUriBuilderFactory;

/**
 * Factory for {@link ProfilesUriBuilder}.
 *
 * TODO delete once there are no more dependencies on this class
 */
@Component(service = ProfilesUriBuilderFactory.class)
public class ProfilesUriBuilderFactoryImpl implements Provider<ProfilesUriBuilder>, ProfilesUriBuilderFactory {

	@Override
	public ProfilesUriBuilder get() {
		return new ProfilesUriBuilderImpl();
	}
}
