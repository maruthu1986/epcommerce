/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.cucumber.customer;

import static org.assertj.core.api.Assertions.assertThat;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.plugin.payment.dto.PaymentMethod;

/**
 * Test customer payment method validator.
 */
class CustomerPaymentMethodsValidator {
	private final Customer customer;
	private PaymentMethod defaultPaymentMethod;
	private PaymentMethod [] paymentMethods;

	/**
	 * Default constructor.
	 *
	 * @param customer the customer run validation on.
	 */
	@SuppressWarnings("checkstyle:redundantmodifier")
	public CustomerPaymentMethodsValidator(final Customer customer) {
		this.customer = customer;
	}

	/**
	 * Set default payment method to validate.
	 *
	 * @param defaultPaymentMethod the default payment method to validate
	 * @return this {@link CustomerPaymentMethodsValidator}
	 */
	public CustomerPaymentMethodsValidator withDefaultPaymentMethod(final PaymentMethod defaultPaymentMethod) {
		this.defaultPaymentMethod = defaultPaymentMethod;
		return this;
	}

	/**
	 * Sets payment methods to validate.
	 *
	 * @param paymentMethods the payment methods to validate
	 * @return this {@link CustomerPaymentMethodsValidator}
	 */
	public CustomerPaymentMethodsValidator withPaymentMethods(final PaymentMethod... paymentMethods) {
		this.paymentMethods = paymentMethods;
		return this;
	}

	/**
	 * Validate Customer's payment methods and default payment method.
	 */
	public void validate() {
		if (paymentMethods != null && paymentMethods.length > 0) {
			assertThat(customer.getPaymentMethods().all())
					.containsExactlyInAnyOrder(paymentMethods);
		} else {
			assertThat(customer.getPaymentMethods().all())
					.as("The persisted customer should have no payment methods")
					.isEmpty();
		}

		assertThat(customer.getPaymentMethods().getDefault())
				.as("The persisted default payment method should be the one specified or the first one in the list of payment methods in the "
				+ "xml representation of the customer")
				.isEqualTo(defaultPaymentMethod);

	}
}
