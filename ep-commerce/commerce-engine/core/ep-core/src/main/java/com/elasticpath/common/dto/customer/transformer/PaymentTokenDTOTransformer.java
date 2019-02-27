/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.common.dto.customer.transformer;

import com.elasticpath.common.dto.customer.PaymentTokenDto;
import com.elasticpath.common.dto.customer.builder.PaymentTokenDTOBuilder;
import com.elasticpath.domain.customer.PaymentToken;
import com.elasticpath.domain.customer.impl.PaymentTokenImpl;

/**
 *  Transforms {@link PaymentTokenDto}s to {@link PaymentToken}s and vice versa.
 */
public class PaymentTokenDTOTransformer {
	private PaymentTokenDTOBuilder paymentTokenDTOBuilder;

	/**
	 * Transforms {@link PaymentTokenDto} to {@link PaymentToken}.
	 *
	 * @param dto the {@link PaymentTokenDto} to transform
	 * @return the transformed {@link PaymentTokenDto}
	 */
	public PaymentToken transformToDomain(final PaymentTokenDto dto) {
		return new PaymentTokenImpl.TokenBuilder()
				.withValue(dto.getPaymentTokenValue())
				.withDisplayValue(dto.getPaymentTokenDisplayValue())
				.build();
	}

	/**
	 * Transforms {@link PaymentToken} to {@link PaymentTokenDto}.
	 *
	 * @param domainEntity the {@link PaymentToken} to transform
	 * @return the transformed {@link PaymentToken}
	 */
	public PaymentTokenDto transformToDto(final PaymentToken domainEntity) {
		return getPaymentTokenDTOBuilder()
				.withPaymentTokenDisplayValue(domainEntity.getDisplayValue())
				.withPaymentTokenValue(domainEntity.getValue())
				.build();
	}

	public void setPaymentTokenDTOBuilder(final PaymentTokenDTOBuilder paymentTokenDTOBuilder) {
		this.paymentTokenDTOBuilder = paymentTokenDTOBuilder;
	}

	public PaymentTokenDTOBuilder getPaymentTokenDTOBuilder() {
		return this.paymentTokenDTOBuilder;
	}
}
