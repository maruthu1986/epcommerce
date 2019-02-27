/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.shipping.impl;

import java.util.Locale;
import java.util.Objects;
import javax.inject.Inject;
import javax.inject.Named;

import com.elasticpath.rest.cache.CacheResult;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.ExecutionResultChain;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.identity.util.SubjectUtil;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipping.ShipmentShippingOptionRepository;
import com.elasticpath.service.shipping.ShippingOptionService;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;

/**
 * Default implementation of {@link ShipmentShippingOptionRepository}.
 */
@Named("shipmentShippingOptionRepository")
public class ShipmentShippingOptionRepositoryImpl implements ShipmentShippingOptionRepository {

	private static final String SHIPPING_OPTION_NOT_FOUND = "Shipping option not found.";

	private final ResourceOperationContext resourceOperationContext;
	private final ShippingOptionService shippingOptionService;

	/**
	 * Constructor.
	 *
	 * @param resourceOperationContext a {@link ResourceOperationContext}
	 * @param shippingOptionService a {@link ShippingOptionService}
	 */
	@Inject
	public ShipmentShippingOptionRepositoryImpl(
		@Named("resourceOperationContext")
		final ResourceOperationContext resourceOperationContext,
		@Named("shippingOptionService")
		final ShippingOptionService shippingOptionService) {
		this.resourceOperationContext = resourceOperationContext;
		this.shippingOptionService = shippingOptionService;
	}

	@Override
	@CacheResult
	public ExecutionResult<ShippingOption> findByCode(final String shippingOptionCode) {

		final Locale locale = SubjectUtil.getLocale(resourceOperationContext.getSubject());
		final String scope = SubjectUtil.getScope(resourceOperationContext.getSubject());

		return new ExecutionResultChain() {
			@Override
			protected ExecutionResult<?> build() {
				ShippingOption shippingOption = Assign.ifNotNull(shippingOptionService
								.getAllShippingOptions(scope, locale)
								.getAvailableShippingOptions().stream()
								.filter(shippingOptionParam -> Objects.equals(shippingOptionCode, shippingOptionParam.getCode()))
								.findFirst().orElse(null),
						OnFailure.returnNotFound(SHIPPING_OPTION_NOT_FOUND));
				return ExecutionResultFactory.createReadOK(shippingOption);
			}
		}.execute();
	}

}
