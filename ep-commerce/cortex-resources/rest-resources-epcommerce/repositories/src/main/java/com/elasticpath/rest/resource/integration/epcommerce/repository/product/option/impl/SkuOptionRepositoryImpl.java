/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.product.option.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.Ensure;
import com.elasticpath.rest.chain.ExecutionResultChain;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.product.option.SkuOptionRepository;
import com.elasticpath.service.catalog.SkuOptionService;

/**
 * Implements the {@link SkuOptionRepository}.
 */
@Singleton
@Named("skuOptionRepository")
public class SkuOptionRepositoryImpl implements SkuOptionRepository {

	private static final String CANNOT_FIND_OPTION_VALUE_MESSAGE = "Cannot find option value.";

	private final SkuOptionService skuOptionService;

	/**
	 * Default constructor.
	 *
	 * @param skuOptionService the sku option service
	 */
	@Inject
	public SkuOptionRepositoryImpl(
			@Named("skuOptionService")
			final SkuOptionService skuOptionService) {

		this.skuOptionService = skuOptionService;
	}

	@Override
	public ExecutionResult<SkuOptionValue> findSkuOptionValueByKey(final String skuOptionNameKey, final String skuOptionValueKey) {
		return new ExecutionResultChain() {
			public ExecutionResult<?> build() {
				// The SKU Option name key is not needed for sku option value.
				// However, we may need this when core changes lookup logic.

				SkuOptionValue skuOptionValue = Assign.ifNotNull(skuOptionService.findOptionValueByKey(skuOptionValueKey),
						OnFailure.returnNotFound(CANNOT_FIND_OPTION_VALUE_MESSAGE));
				Ensure.isTrue(skuOptionValue.getSkuOption().getOptionKey().equals(skuOptionNameKey),
						OnFailure.returnNotFound(CANNOT_FIND_OPTION_VALUE_MESSAGE));

				return ExecutionResultFactory.createReadOK(skuOptionValue);
			}
		}.execute();
	}
}
