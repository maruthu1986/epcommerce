/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.transform.impl;

import java.util.Date;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;

import com.elasticpath.rest.definition.base.DateEntity;
import com.elasticpath.rest.resource.integration.epcommerce.transform.DateTransformer;
import com.elasticpath.rest.resource.transform.AbstractDomainTransformer;
import com.elasticpath.rest.util.date.DateUtil;

/**
 * Transforms {@link Date} to {@link DateEntity} and vice versa.
 */
@Component(property = AbstractDomainTransformer.DS_SERVICE_RANKING)
public class DateTransformerImpl extends AbstractDomainTransformer<Date, DateEntity> implements DateTransformer {

	@Override
	public Date transformToDomain(final DateEntity dateEntity, final Locale locale) {
		throw new UnsupportedOperationException("This operation is not implemented.");
	}

	@Override
	public DateEntity transformToEntity(final Date date, final Locale locale) {
		return DateEntity.builder()
				.withValue(date.getTime())
				.withDisplayValue(DateUtil.formatDateTime(date, locale))
				.build();
	}
}