/*
 * Copyright © 2018 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.service.shoppingcart.actions.impl;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.service.catalog.GiftCertificateService;
import com.elasticpath.service.shoppingcart.actions.CheckoutAction;
import com.elasticpath.service.shoppingcart.actions.CheckoutActionContext;

/**
 * Checkout action for removing empty gift cards.
 */
public class RemoveEmptyGiftCardsCheckoutAction implements CheckoutAction {

	private GiftCertificateService giftCertificateService;

	/**
	 * Action to execute during normal checkout flow.
	 *
	 * @param context object containing data required for execution
	 * @throws EpSystemException exception object which could be thrown by execution
	 */
	@Override
	public void execute(final CheckoutActionContext context) throws EpSystemException {
		ShoppingCart shoppingCart = context.getShoppingCart();

		final Set<GiftCertificate> appliedGiftCertificates = shoppingCart.getAppliedGiftCertificates();

		Set<GiftCertificate> giftCertificatesToRemove = new HashSet<>();

		if (appliedGiftCertificates != null && !appliedGiftCertificates.isEmpty()) {
			for (GiftCertificate giftCertificate : appliedGiftCertificates) {
				final GiftCertificate freshGc = giftCertificateService.findByGiftCertificateCode(giftCertificate.getGiftCertificateCode());

				if (freshGc == null) {
					//ignore non existing certificates.
					continue;
				}
				final BigDecimal balance = giftCertificateService.getBalance(freshGc);
				if (balance.compareTo(BigDecimal.ZERO) <= 0) {
					giftCertificatesToRemove.add(giftCertificate);
				}
			}
			appliedGiftCertificates.removeAll(giftCertificatesToRemove);
		}
	}

	protected GiftCertificateService getGiftCertificateService() {
		return giftCertificateService;
	}

	public void setGiftCertificateService(final GiftCertificateService giftCertificateService) {
		this.giftCertificateService = giftCertificateService;
	}
}
