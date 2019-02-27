/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.permission;

import java.util.Collection;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.apache.shiro.subject.PrincipalCollection;

import com.elasticpath.rest.authorization.parameter.AbstractCollectionValueStrategy;
import com.elasticpath.rest.definition.shipmentdetails.ShipmentDetailsIdIdentifierPart;
import com.elasticpath.rest.id.Identifier;
import com.elasticpath.rest.id.transform.IdentifierTransformer;
import com.elasticpath.rest.id.transform.IdentifierTransformerProvider;
import com.elasticpath.rest.identity.util.PrincipalsUtil;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipmentdetails.ShipmentDetailsIdParameterService;

/**
 * Strategy for resolving the shipment details id parameter.
 */
@Singleton
@Named
public class ShipmentDetailsIdParameterStrategy extends AbstractCollectionValueStrategy {

	@Inject
	private Provider<ShipmentDetailsIdParameterService> shipmentDetailsIdParameterService;

	@Inject
	private IdentifierTransformerProvider identifierTransformerProvider;

	@Override
	protected Collection<String> getParameterValues(final PrincipalCollection principalCollection) {
		String scope = PrincipalsUtil.getScope(principalCollection);
		String userId = PrincipalsUtil.getUserIdentifier(principalCollection);
		final IdentifierTransformer<Identifier> identifierTransformer = identifierTransformerProvider
				.forUriPart(ShipmentDetailsIdIdentifierPart.URI_PART_NAME);
		return shipmentDetailsIdParameterService.get().findShipmentDetailsIds(scope, userId)
				.map(identifierTransformer::identifierToUri)
				.toList()
				.blockingGet();
	}
}
