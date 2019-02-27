/**
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shoppingcart.validation.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.EmailValidator;

import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.base.common.dto.StructuredErrorMessageType;
import com.elasticpath.base.common.dto.StructuredErrorResolution;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.service.shoppingcart.validation.ShoppingCartValidationContext;
import com.elasticpath.service.shoppingcart.validation.ShoppingCartValidator;

/**
 * Determines if a valid email address has been provided.
 */
public class EmailAddressShoppingCartValidatorImpl implements ShoppingCartValidator {

	/**
	 * Message id for this validation.
	 */
	public static final String MESSAGE_ID = "need.email";

	private Set<String> reservedEmails;

	/**
	 * Validates customer email address.
	 *
	 * @param context context for the validation.
	 * @return a collection of Structured Error Messages containing validation errors, or an
	 * empty collection if the validation is successful.
	 */
	@Override
	public Collection<StructuredErrorMessage> validate(final ShoppingCartValidationContext context) {
		ShoppingCart shoppingCart = context.getShoppingCart();

		Shopper shopper = shoppingCart.getShopper();

		Customer customer = shopper.getCustomer();

		String email = customer.getEmail();

		if (isValidEmail(email)) {
			return Collections.emptyList();
		}

		StructuredErrorMessage errorMessage = new StructuredErrorMessage(StructuredErrorMessageType.NEEDINFO, MESSAGE_ID,
				"Customer email address must be specified.", Collections.emptyMap(),
				new StructuredErrorResolution(Customer.class, customer.getGuid()));
		return Collections.singletonList(errorMessage);
	}

	/**
	 * Determine if the email address is valid.
	 *
	 * @param emailAddress the email address
	 * @return true if the email is valid
	 */
	protected boolean isValidEmail(final String emailAddress) {
		return StringUtils.isNotBlank(emailAddress) && EmailValidator.getInstance().isValid(emailAddress)
				&& !reservedEmails.contains(emailAddress);
	}

	protected Set<String> getReservedEmails() {
		return reservedEmails;
	}

	public void setReservedEmails(final Set<String> reservedEmails) {
		this.reservedEmails = reservedEmails;
	}
}
