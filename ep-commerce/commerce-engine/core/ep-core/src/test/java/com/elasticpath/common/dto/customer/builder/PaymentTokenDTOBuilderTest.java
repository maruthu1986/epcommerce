/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.common.dto.customer.builder;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.elasticpath.common.dto.customer.PaymentTokenDto;

/**
 * Tests {@link PaymentTokenDTOBuilder}.
 */
public class PaymentTokenDTOBuilderTest {

	private static final String TEST_DISPLAY_VALUE = "testDisplayValue";
	private static final String TEST_VALUE = "testValue";

	/**
	 * Test to ensure build produces correct payment token DTO.
	 */
	@Test
	public void ensureBuildProducesCorrectPaymentTokenDTO() {
		PaymentTokenDto expectedPaymentTokenDTO = new PaymentTokenDto();
		expectedPaymentTokenDTO.setPaymentTokenDisplayValue(TEST_DISPLAY_VALUE);
		expectedPaymentTokenDTO.setPaymentTokenValue(TEST_VALUE);

		PaymentTokenDto paymentTokenDto = new PaymentTokenDTOBuilder()
				.withPaymentTokenDisplayValue(TEST_DISPLAY_VALUE)
				.withPaymentTokenValue(TEST_VALUE)
				.build();

		assertEquals("THe payment token dto produced from the builder should be the same as expected", expectedPaymentTokenDTO, paymentTokenDto);
	}
}
