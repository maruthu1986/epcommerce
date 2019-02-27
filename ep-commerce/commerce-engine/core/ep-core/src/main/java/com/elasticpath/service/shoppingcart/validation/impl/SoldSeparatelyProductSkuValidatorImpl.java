/**
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shoppingcart.validation.impl;

import java.util.Collection;
import java.util.Collections;

import com.google.common.collect.ImmutableMap;

import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.service.shoppingcart.validation.ProductSkuValidationContext;
import com.elasticpath.service.shoppingcart.validation.ProductSkuValidator;

/**
 * Validator to check that the product can be sold separately.
 */
public class SoldSeparatelyProductSkuValidatorImpl implements ProductSkuValidator {

	/**
	 * Message id for this validation.
	 */
	public static final String MESSAGE_ID = "item.not.sold.separately";

	@Override
	public Collection<StructuredErrorMessage> validate(final ProductSkuValidationContext context) {

		if (context.getParentProductSku() == null && context.getProductSku().getProduct().isNotSoldSeparately()) {
			return Collections.singletonList(new StructuredErrorMessage(MESSAGE_ID,
					String.format("Item '%s' is not sold separately.", context.getProductSku().getSkuCode()),
					ImmutableMap.of("item-code", context.getProductSku().getSkuCode())));
		}
		return Collections.emptyList();

	}
}
