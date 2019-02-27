/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.common.dto.customer.transformer;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.common.dto.customer.PaymentTokenDto;
import com.elasticpath.common.dto.customer.builder.PaymentTokenDTOBuilder;
import com.elasticpath.domain.customer.PaymentToken;
import com.elasticpath.domain.customer.impl.PaymentTokenImpl;

/**
 * Test {@link PaymentTokenDTOTransformer}.
 */
public class PaymentTokenDTOTransformerTest {


	private static final String TEST_DISPLAY_VALUE = "testDisplayValue";
	private static final String TEST_VALUE = "testValue";
	private PaymentTokenDTOTransformer paymentTokenDTOTransformer;
	private PaymentTokenDto paymentTokenDto;
	private PaymentToken paymentToken;

	/**
	 * Set up common test elements.
	 */
	@Before
	public void setUpCommonTestElements() {
		paymentTokenDto = new PaymentTokenDTOBuilder().withPaymentTokenDisplayValue(TEST_DISPLAY_VALUE)
				.withPaymentTokenValue(TEST_VALUE)
				.build();

		paymentToken = new PaymentTokenImpl.TokenBuilder()
				.withDisplayValue(TEST_DISPLAY_VALUE)
				.withValue(TEST_VALUE)
				.build();

		paymentTokenDTOTransformer = new PaymentTokenDTOTransformer();
		paymentTokenDTOTransformer.setPaymentTokenDTOBuilder(new PaymentTokenDTOBuilder());
	}

	/**
	 * Test to ensure transform to domain works correctly.
	 */
	@Test
	public void ensureTransformToDomainCorrectlyTransformsPaymentTokenDto() {
		assertEquals("The payment token resulting from the transformation should be the same as expected",
				paymentToken, paymentTokenDTOTransformer.transformToDomain(paymentTokenDto));
	}

	/**
	 * Test to ensure transform to dto works correctly.
	 */
	@Test
	public void ensureTransformToDTOCorrectlyTransformsPaymentToken() {
		assertEquals("The payment token dto resulting from the transformation should be the same as expected",
				paymentTokenDto, paymentTokenDTOTransformer.transformToDto(paymentToken));
	}
}
