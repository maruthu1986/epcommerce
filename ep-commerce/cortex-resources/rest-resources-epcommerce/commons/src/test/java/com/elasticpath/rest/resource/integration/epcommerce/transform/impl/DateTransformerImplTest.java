/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.transform.impl;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.Locale;

import org.junit.Test;

import com.elasticpath.rest.definition.base.DateEntity;
import com.elasticpath.rest.resource.integration.epcommerce.transform.DateTransformer;
import com.elasticpath.rest.util.date.DateUtil;

/**
 * Test class for {@link DateTransformerImpl}.
 */
public class DateTransformerImplTest {

	private final DateTransformer dateTransformer = new DateTransformerImpl();

	/**
	 * Test transform to domain.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void testTransformToDomain() {
		dateTransformer.transformToDomain(null);
	}

	/**
	 * Test transform to entity.
	 */
	@Test
	public void testTransformToEntity() {
		Date date = new Date();

		DateEntity dateEntity = dateTransformer.transformToEntity(date, Locale.ENGLISH);

		assertEquals("Date value does not match expected value.", date.getTime(), dateEntity.getValue().longValue());

		String formatDateTime = DateUtil.formatDateTime(date, Locale.ENGLISH);
		assertEquals("Display value does not match expected formated date.", formatDateTime, dateEntity.getDisplayValue());
	}

}
