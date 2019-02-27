/**
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shoppingcart.validation.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import com.google.common.collect.ImmutableMap;

import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.shoppingcart.validation.ProductSkuValidationContext;
import com.elasticpath.service.shoppingcart.validation.ProductSkuValidator;

/**
 * Validator to check that the product sku is within dates.
 */
public class ProductSkuDatesValidatorImpl implements ProductSkuValidator {

	/**
	 * Message id for this items no longer available.
	 */
	private static final String MESSAGE_ID_NO_LONGER_AVAILABLE = "item.no.longer.available";

	/**
	 * Message id for this items not yet available.
	 */
	private static final String MESSAGE_ID_NOT_YET_AVAILABLE = "item.not.yet.available";

	private TimeService timeService;

	private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

	@Override
	public Collection<StructuredErrorMessage> validate(final ProductSkuValidationContext context) {
		Date currentDate = timeService.getCurrentTime();

		Date startDate = context.getProductSku().getEffectiveStartDate();
		if (startDate != null && startDate.after(currentDate)) {
			return Collections.singletonList(new StructuredErrorMessage(MESSAGE_ID_NOT_YET_AVAILABLE,
					String.format("Item '%s' is not yet available for purchase", context.getProductSku().getSkuCode()),
					ImmutableMap.of("item-code", context.getProductSku().getSkuCode(), "available-date", dateFormat.format(startDate))));
		}

		Date endDate = context.getProductSku().getEffectiveEndDate();
		if (endDate != null && endDate.before(currentDate)) {
			return Collections.singletonList(new StructuredErrorMessage(MESSAGE_ID_NO_LONGER_AVAILABLE,
					String.format("Item '%s' is no longer available for purchase", context.getProductSku().getSkuCode()),
					ImmutableMap.of("item-code", context.getProductSku().getSkuCode(), "expiry-date", dateFormat.format(endDate))));
		}

		return Collections.emptyList();

	}

	protected TimeService getTimeService() {
		return timeService;
	}

	public void setTimeService(final TimeService timeService) {
		this.timeService = timeService;
	}
}
