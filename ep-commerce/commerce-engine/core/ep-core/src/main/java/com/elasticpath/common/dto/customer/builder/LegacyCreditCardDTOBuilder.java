/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.common.dto.customer.builder;

import com.elasticpath.common.dto.customer.LegacyCreditCardDTO;

/**
 * {@link LegacyCreditCardDTO} builder.
 */
public class LegacyCreditCardDTOBuilder extends AbstractCreditCardDTOBuilder<LegacyCreditCardDTO> {
	private boolean defaultCard;

	@Override
	public LegacyCreditCardDTO create() {
		return new LegacyCreditCardDTO();
	}

	/**
	 * Sets the default card.
	 *
	 * @param defaultCard the default card value
	 * @return this {@link LegacyCreditCardDTOBuilder}
	 */
	public LegacyCreditCardDTOBuilder withDefaultCard(final boolean defaultCard) {
		this.defaultCard = defaultCard;
		return this;
	}

	@Override
	public LegacyCreditCardDTO build() {
		LegacyCreditCardDTO dto = super.build();
		dto.setDefaultCard(defaultCard);
		return dto;
	}
}
