/**
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shoppingcart.validation.impl;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.base.common.dto.StructuredErrorMessageType;
import com.elasticpath.base.common.dto.StructuredErrorResolution;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.service.shipping.ShippingOptionResult;
import com.elasticpath.service.shipping.ShippingOptionService;
import com.elasticpath.service.shoppingcart.validation.ShoppingCartValidationContext;
import com.elasticpath.service.shoppingcart.validation.ShoppingCartValidator;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;

/**
 * Determines if a valid shipping option has been specified (if cart contains physical SKUs).
 */
public class ShippingOptionShoppingCartValidatorImpl implements ShoppingCartValidator {

	private static final String NEED_SHIPPING_OPTION_MESSAGE_ID = "need.shipping.option";

	private static final String INVALID_SHIPPING_OPTION_MESSAGE_ID = "invalid.shipping.option";

	private static final String SHIPPING_OPTIONS_UNAVAILABLE_MESSAGE_ID = "shipping.options.unavailable";

	/**
	 * Message ids for this validation.
	 */
	public static final Set<String> MESSAGE_IDS = ImmutableSet
			.of(NEED_SHIPPING_OPTION_MESSAGE_ID, INVALID_SHIPPING_OPTION_MESSAGE_ID, SHIPPING_OPTIONS_UNAVAILABLE_MESSAGE_ID);

	private ShippingOptionService shippingOptionService;

	@Override
	public Collection<StructuredErrorMessage> validate(final ShoppingCartValidationContext context) {
		final ShoppingCart shoppingCart = context.getShoppingCart();

		if (shoppingCart.requiresShipping()) {
			if (!shoppingCart.getSelectedShippingOption().isPresent()) {
				StructuredErrorMessage errorMessage = new StructuredErrorMessage(StructuredErrorMessageType.NEEDINFO, NEED_SHIPPING_OPTION_MESSAGE_ID,
						"Shipping option must be specified.", Collections.emptyMap(),
						new StructuredErrorResolution(ShoppingCart.class, shoppingCart.getGuid()));
				return Collections.singletonList(errorMessage);
			}
			final ShippingOptionResult shippingOptionResult = getShippingOptionService().getShippingOptions(shoppingCart);

			if (!shippingOptionResult.isSuccessful()) {
				StructuredErrorMessage errorMessage = new StructuredErrorMessage(StructuredErrorMessageType.ERROR,
						SHIPPING_OPTIONS_UNAVAILABLE_MESSAGE_ID,
						"There was a problem retrieving shipping options from the shipping service: " + shippingOptionResult
								.getErrorDescription(false),
						Collections.emptyMap(), new StructuredErrorResolution(ShoppingCart.class, shoppingCart.getGuid()));
				return Collections.singletonList(errorMessage);
			}
			final ShippingOption shippingOptionSelected = shoppingCart.getSelectedShippingOption().orElse(null);
			checkNotNull(shippingOptionSelected, "shoppingCart shipping option should have been verified and should exist.");

			if (!isValidShippingOption(shippingOptionResult.getAvailableShippingOptions(), shippingOptionSelected)) {
				StructuredErrorMessage errorMessage = new StructuredErrorMessage(StructuredErrorMessageType.ERROR,
						INVALID_SHIPPING_OPTION_MESSAGE_ID, "Selected shipping option is not valid.",
						ImmutableMap.of(
								"shipping-option", shippingOptionSelected.getCode()));
				return Collections.singletonList(errorMessage);
			}
		}
		return Collections.emptyList();
	}

	/**
	 * Returns whether a shipping option contained in the list of shipping options matches the given non-null one.
	 * It only searches by both shipping option code and carrier code fields for a match.
	 *
	 * @param validShippingOptions the list of shipping options to search through.
	 * @param shippingOptionSelected the shipping option to find a match for, must not be {@code null}.
	 * @return {@code true} if a shipping option matches by both shipping option code and carrier code; {@code false} otherwise.
	 */
	protected boolean isValidShippingOption(final List<ShippingOption> validShippingOptions, final ShippingOption shippingOptionSelected) {
		return isNotEmpty(validShippingOptions)
				&& validShippingOptions.stream().anyMatch(
				shippingOption -> shippingOption.getCode().equals(shippingOptionSelected.getCode())
						&& shippingOption.getCarrierCode().equals(shippingOptionSelected.getCarrierCode()));
	}

	protected ShippingOptionService getShippingOptionService() {
		return this.shippingOptionService;
	}

	public void setShippingOptionService(final ShippingOptionService shippingOptionService) {
		this.shippingOptionService = shippingOptionService;
	}

}
