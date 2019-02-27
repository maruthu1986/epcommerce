/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.availabilities;

import com.elasticpath.rest.definition.availabilities.AvailabilityEntity;
import com.elasticpath.rest.definition.availabilities.AvailabilityForCartLineItemIdentifier;
import com.elasticpath.rest.definition.availabilities.AvailabilityForItemIdentifier;
import com.elasticpath.rest.definition.base.DateEntity;
import com.elasticpath.rest.resource.integration.epcommerce.repository.IdentifierTestFactory;

/**
 * Factory methods for building identifiers.
 */
public final class AvailabilityTestFactory {

	private AvailabilityTestFactory() {
	}

	/**
	 * Creates an availability entity, delegating to create and link a date entity with the given values.
	 *
	 * @param availabilityCriteria criteria for availability (ex: {code Availability.ALWAYS})
	 * @param dateValue            long representation of the date
	 * @param displayValue         way to display the date
	 * @return the corresponding availability entity.
	 */
	public static AvailabilityEntity createAvailabilityEntity(final String availabilityCriteria, final Long dateValue, final String displayValue) {
		return AvailabilityEntity.builder()
				.withReleaseDate(createDateEntity(dateValue, displayValue))
				.withState(availabilityCriteria)
				.build();
	}

	/**
	 * Creates a date entity.
	 *
	 * @param dateValue    long representation of the date
	 * @param displayValue way to display the date
	 * @return the date entity.
	 */
	public static DateEntity createDateEntity(final Long dateValue, final String displayValue) {
		return DateEntity.builder()
				.withValue(dateValue)
				.withDisplayValue(displayValue)
				.build();
	}

	/**
	 * Creates an availability for cart line item identifier, delegating to create and link a line item identifier with the given values.
	 *
	 * @param cartId     the cart id
	 * @param scope      the scope
	 * @param lineItemId the item id
	 * @return the availability for cart line item identifier.
	 */
	public static AvailabilityForCartLineItemIdentifier createAvailabilityForCartLineItemIdentifier(final String cartId, final String scope,
																									final String lineItemId) {
		return AvailabilityForCartLineItemIdentifier.builder()
				.withLineItem(IdentifierTestFactory.buildLineItemIdentifier(scope, cartId, lineItemId))
				.build();
	}

	/**
	 * Creates an availability for item identifier, delegating to create and link an item identifier with the given values.
	 *
	 * @param itemId the item id
	 * @param scope  the scope
	 * @return the availability for item identifier.
	 */
	public static AvailabilityForItemIdentifier createAvailabilityForItemIdentifier(final String itemId, final String scope) {
		return AvailabilityForItemIdentifier.builder()
				.withItem(IdentifierTestFactory.buildItemIdentifier(scope, itemId))
				.build();
	}
}
