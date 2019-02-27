/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.datapolicy.job.impl.filter;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.function.Predicate;

import com.elasticpath.domain.datapolicy.DataPolicy;
import com.elasticpath.service.datapolicy.impl.DataPointValue;

/**
 * The Predicate that tests DataPointValues associated with DataPolicy with
 * retention type {@link com.elasticpath.domain.datapolicy.RetentionType#FROM_CREATION_DATE}.
 */
class FromCreationDatePredicate implements Predicate<DataPointValue> {

	private final Instant now;
	private final DataPolicy dataPolicy;

	/**
	 * Main constructor used to create <code>FromCreationDatePredicate</code> instance.
	 *
	 * @param now        Instant object that represents "now". Should not be null.
	 * @param dataPolicy data policy to use for test.
	 */
	FromCreationDatePredicate(final Instant now, final DataPolicy dataPolicy) {
		this.now = Objects.requireNonNull(now);
		this.dataPolicy = Objects.requireNonNull(dataPolicy);
	}

	@Override
	public boolean test(final DataPointValue dataPointValue) {
		Instant start = dataPointValue.getCreatedDate().toInstant();
		long between = Duration.between(start, now).toDays();
		return between >= dataPolicy.getRetentionPeriodInDays();
	}
}
