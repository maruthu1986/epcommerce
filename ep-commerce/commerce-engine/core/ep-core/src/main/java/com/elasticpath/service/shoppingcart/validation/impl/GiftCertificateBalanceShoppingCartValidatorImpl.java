/**
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shoppingcart.validation.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;

import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.base.common.dto.StructuredErrorMessageType;
import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.service.catalog.GiftCertificateService;
import com.elasticpath.service.shoppingcart.PricingSnapshotService;
import com.elasticpath.service.shoppingcart.validation.ShoppingCartValidationContext;
import com.elasticpath.service.shoppingcart.validation.ShoppingCartValidator;

/**
 * Ensure that any gift certificates being used for payment exist and have sufficient balance to complete a purchase.
 */
public class GiftCertificateBalanceShoppingCartValidatorImpl implements ShoppingCartValidator {

	/**
	 * Message id for this validation.
	 */
	public static final String MESSAGE_ID = "cart.gift.certificate.not.found";

	private GiftCertificateService giftCertificateService;

	private PricingSnapshotService pricingSnapshotService;

	/**
	 * Validates the object.
	 *
	 * @param context object to be validated.
	 * @return a collection of Structured Error Messages containing validation errors, or an
	 * empty collection if the validation is successful.
	 */
	@Override
	public Collection<StructuredErrorMessage> validate(final ShoppingCartValidationContext context) {

		ShoppingCart shoppingCart = context.getShoppingCart();
		List<StructuredErrorMessage> errorMessageList = new ArrayList<>();

		final Set<GiftCertificate> appliedGiftCertificates = shoppingCart.getAppliedGiftCertificates();
		if (appliedGiftCertificates != null && !appliedGiftCertificates.isEmpty()) {
			BigDecimal totalAvailableAmount = BigDecimal.ZERO;
			for (GiftCertificate giftCertificate : appliedGiftCertificates) {
				final GiftCertificate freshGc = giftCertificateService.findByGiftCertificateCode(giftCertificate.getGiftCertificateCode());

				if (freshGc == null) {
					addGifCertificateNotFoundError(errorMessageList, giftCertificate);
					continue;
				}

				final BigDecimal balance = giftCertificateService.getBalance(freshGc);
				if (balance.compareTo(BigDecimal.ZERO) > 0) {
					totalAvailableAmount = totalAvailableAmount.add(balance);
				}
			}
			ShoppingCartPricingSnapshot pricingSnapshot = pricingSnapshotService.getPricingSnapshotForCart(shoppingCart);
			final BigDecimal totalRedeems = pricingSnapshot.getGiftCertificateDiscount();
			// Check if the balance is sufficient
			if (totalAvailableAmount.compareTo(totalRedeems) < 0) {
				addInsufficientBalanceError(errorMessageList, totalAvailableAmount, totalRedeems);
			}
		}
		return errorMessageList;
	}

	private void addGifCertificateNotFoundError(final List<StructuredErrorMessage> errorMessageList, final GiftCertificate giftCertificate) {
		addErrorToList(errorMessageList, MESSAGE_ID,
				String.format("Gift certificate '%s' not found.", giftCertificate.getGiftCertificateCode()),
				ImmutableMap.of("gift-certificate-code", giftCertificate.getGiftCertificateCode()));
	}

	private void addInsufficientBalanceError(final List<StructuredErrorMessage> errorMessageList, final BigDecimal totalAvailableAmount,
											 final BigDecimal totalRedeems) {
		addErrorToList(errorMessageList,
				"cart.gift.certificate.insufficient.balance",
				"Gift certificates does not have sufficient balance to process the payment.",
				ImmutableMap.of("gc-payment-required", totalRedeems.toString(), "gc-balance", totalAvailableAmount.toString()));
	}

	private void addErrorToList(final List<StructuredErrorMessage> errorMessageList, final String messageId, final String debugMessage,
								final Map<String, String> data) {
		StructuredErrorMessage errorMessage = new StructuredErrorMessage(StructuredErrorMessageType.ERROR,
				messageId,
				debugMessage,
				data);
		errorMessageList.add(errorMessage);
	}

	protected GiftCertificateService getGiftCertificateService() {
		return giftCertificateService;
	}

	public void setGiftCertificateService(final GiftCertificateService giftCertificateService) {
		this.giftCertificateService = giftCertificateService;
	}

	protected PricingSnapshotService getPricingSnapshotService() {
		return pricingSnapshotService;
	}

	public void setPricingSnapshotService(final PricingSnapshotService pricingSnapshotService) {
		this.pricingSnapshotService = pricingSnapshotService;
	}
}
