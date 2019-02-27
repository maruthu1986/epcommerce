/**
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shoppingcart.validation.impl;

import java.util.Collection;
import java.util.Collections;

import com.google.common.collect.ImmutableMap;

import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.service.catalog.BundleIdentifier;
import com.elasticpath.service.shoppingcart.validation.ProductSkuValidationContext;
import com.elasticpath.service.shoppingcart.validation.ProductSkuValidator;

/**
 * Validator to check that the price exists.
 */
public class PriceExistsProductSkuValidatorImpl implements ProductSkuValidator {

	/**
	 * Message id for this validation.
	 */
	public static final String MESSAGE_ID = "item.missing.price";

	private BundleIdentifier bundleIdentifier;

	@Override
	public Collection<StructuredErrorMessage> validate(final ProductSkuValidationContext context) {
		if (isPriceRequired(context.getProductSku(), context.getParentProductSku()) && context.getPromotedPrice() == null) {
			return Collections.singletonList(new StructuredErrorMessage(MESSAGE_ID,
					String.format("Item '%s' does not have a price.", context.getProductSku().getSkuCode()),
					ImmutableMap.of("item-code", context.getProductSku().getSkuCode())));
		}
		return Collections.emptyList();
	}

	private boolean isPriceRequired(final ProductSku productSku, final ProductSku parentProductSku) {
		// If sku being validated is a calculated bundle, no price is required.
		// If parent sku is an assigned bundle, and sku being validated is a bundle, no price is required.
		return !(bundleIdentifier.isCalculatedBundle(productSku)
				|| (parentProductSku != null && bundleIdentifier.isBundle(productSku.getProduct())
					&& bundleIdentifier.isAssignedBundle(parentProductSku)));
	}

	protected BundleIdentifier getBundleIdentifier() {
		return bundleIdentifier;
	}

	public void setBundleIdentifier(final BundleIdentifier bundleIdentifier) {
		this.bundleIdentifier = bundleIdentifier;
	}
}