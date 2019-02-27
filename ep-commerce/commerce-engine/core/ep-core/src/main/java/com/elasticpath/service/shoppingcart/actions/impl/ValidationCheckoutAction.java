/**
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shoppingcart.actions.impl;

import java.util.Collection;

import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.service.shoppingcart.actions.CheckoutAction;
import com.elasticpath.service.shoppingcart.actions.CheckoutActionContext;
import com.elasticpath.service.shoppingcart.actions.exception.CheckoutValidationException;
import com.elasticpath.service.shoppingcart.validation.PurchaseCartValidationService;
import com.elasticpath.service.shoppingcart.validation.ShoppingCartValidationContext;

/**
 * Checkout action for validating the shopping cart state.
 */
public class ValidationCheckoutAction implements CheckoutAction {

	private PurchaseCartValidationService validationService;

	@Override
	public void execute(final CheckoutActionContext context) throws EpSystemException {
		ShoppingCartValidationContext validationContext = validationService.buildContext(context.getShoppingCart());

		Collection<StructuredErrorMessage> errorMessages = validationService.validate(validationContext);

		if (!errorMessages.isEmpty()) {
			throw new CheckoutValidationException(errorMessages);
		}
	}

	protected PurchaseCartValidationService getValidationService() {
		return validationService;
	}

	public void setValidationService(final PurchaseCartValidationService validationService) {
		this.validationService = validationService;
	}
}
