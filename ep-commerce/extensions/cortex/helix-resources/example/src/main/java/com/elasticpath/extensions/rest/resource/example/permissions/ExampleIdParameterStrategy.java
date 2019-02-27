/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.extensions.rest.resource.example.permissions;

import java.util.Collection;
import java.util.Collections;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.shiro.subject.PrincipalCollection;

import com.elasticpath.rest.authorization.parameter.AbstractCollectionValueStrategy;
import com.elasticpath.rest.definition.example.ExampleIdentifier;
import com.elasticpath.rest.id.transform.IdentifierTransformerProvider;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.identity.util.PrincipalsUtil;

/**
 * Strategy to look up permission for Example resource.
 */
@Singleton
@Named
public final class ExampleIdParameterStrategy extends AbstractCollectionValueStrategy {

	@Inject
	private IdentifierTransformerProvider provider;


	@Override
	protected Collection<String> getParameterValues(final PrincipalCollection principals) {
		String scope = PrincipalsUtil.getScope(principals);
		String userId = PrincipalsUtil.getUserIdentifier(principals);

		String exampleId = scope + userId + "example-id"; //In reality this would be an id fetched from a CE call
		return Collections.singletonList(provider.forUriPart(ExampleIdentifier.EXAMPLE_ID).identifierToUri(StringIdentifier.of(exampleId)));
	}
}
