/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.common.dto.customer.builder;

import com.elasticpath.common.dto.customer.CreditCardDTO;

/**
 * {@link CreditCardDTO} builder.
 */
public class CreditCardDTOBuilder extends AbstractCreditCardDTOBuilder<CreditCardDTO> {

	@Override
	public CreditCardDTO create() {
		return new CreditCardDTO();
	}
}
