/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.profiles.permissions;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.shiro.subject.PrincipalCollection;

import com.elasticpath.rest.authorization.parameter.PermissionParameterStrategy;
import com.elasticpath.rest.definition.profiles.ProfileIdentifier;
import com.elasticpath.rest.id.transform.IdentifierTransformerProvider;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.identity.UserPrincipal;

/**
 * Strategy to look up permission for Profile resource.
 */
@Singleton
@Named
public final class ProfileIdParameterStrategy implements PermissionParameterStrategy {

	@Inject
	private IdentifierTransformerProvider provider;

	@Override
	public String getParameterValue(final PrincipalCollection principals) {
		final UserPrincipal userPrincipal = principals.oneByType(UserPrincipal.class);

		return provider.forUriPart(ProfileIdentifier.PROFILE_ID)
				.identifierToUri(StringIdentifier.of(userPrincipal.getValue()));
	}
}
