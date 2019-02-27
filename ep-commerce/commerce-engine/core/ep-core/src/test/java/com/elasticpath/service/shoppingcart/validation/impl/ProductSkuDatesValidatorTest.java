/**
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shoppingcart.validation.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

import com.google.common.collect.ImmutableMap;

import org.apache.commons.lang.time.DateUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.shoppingcart.validation.ProductSkuValidationContext;

@RunWith(MockitoJUnitRunner.class)
public class ProductSkuDatesValidatorTest {

	private static final Date NOW = new Date();

	private static final String SKU_CODE = "sku_code";

	private static final int DAYS_DELTA = 5;

	private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

	@InjectMocks
	private ProductSkuDatesValidatorImpl validator;

	@Mock
	private ProductSkuValidationContext context;

	@Mock
	private ProductSku productSku;

	@Mock
	private TimeService timeService;

	@Before
	public void setUp() {
		given(productSku.getSkuCode()).willReturn(SKU_CODE);

		given(context.getProductSku()).willReturn(productSku);

		given(timeService.getCurrentTime()).willReturn(NOW);

	}

	@Test
	public void testProductNotYetAvailable() {
		// Given
		Date startDate = DateUtils.addDays(NOW, DAYS_DELTA);
		given(productSku.getEffectiveStartDate()).willReturn(startDate);

		// When
		Collection<StructuredErrorMessage> messageCollections = validator.validate(context);

		// Then
		StructuredErrorMessage structuredErrorMessage = new StructuredErrorMessage("item.not.yet.available",
				String.format("Item '%s' is not yet available for purchase", SKU_CODE),
				ImmutableMap.of("item-code", SKU_CODE, "available-date", dateFormat.format(startDate)));
		assertThat(messageCollections).containsOnly(structuredErrorMessage);
	}

	@Test
	public void testProductNoLongerAvailable() {
		// Given
		Date startDate = DateUtils.addDays(NOW, -(DAYS_DELTA + DAYS_DELTA));
		Date endDate = DateUtils.addDays(NOW, -DAYS_DELTA);
		given(productSku.getEffectiveStartDate()).willReturn(startDate);
		given(productSku.getEffectiveEndDate()).willReturn(endDate);

		// When
		Collection<StructuredErrorMessage> messageCollections = validator.validate(context);

		// Then
		StructuredErrorMessage structuredErrorMessage = new StructuredErrorMessage("item.no.longer.available",
				String.format("Item '%s' is no longer available for purchase", SKU_CODE),
				ImmutableMap.of("item-code", SKU_CODE, "expiry-date", dateFormat.format(endDate)));
		assertThat(messageCollections).containsOnly(structuredErrorMessage);
	}

	@Test
	public void testProductSkuDatesAretWithinRange() {
		// Given
		Date startDate = DateUtils.addDays(NOW, -DAYS_DELTA);
		Date endDate = DateUtils.addDays(NOW, DAYS_DELTA);
		given(productSku.getEffectiveStartDate()).willReturn(startDate);
		given(productSku.getEffectiveEndDate()).willReturn(endDate);

		// When
		Collection<StructuredErrorMessage> messageCollections = validator.validate(context);

		// Then
		assertThat(messageCollections).isEmpty();
	}

}
