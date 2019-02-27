/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.datapolicy.job.impl.filter;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.function.Predicate;

import com.elasticpath.domain.datapolicy.DataPolicy;
import com.elasticpath.domain.datapolicy.DataPolicyState;
import com.elasticpath.service.datapolicy.impl.DataPointValue;

/**
 * The Predicate that tests DataPointValues associated with DataPolicy with
 * retention type {@link com.elasticpath.domain.datapolicy.RetentionType#FROM_LAST_UPDATE}.
 */
class FromLastUpdatePredicate implements Predicate<DataPointValue> {

	private final Instant now;
	private final DataPolicy dataPolicy;

	/**
	 * Main constructor used to create <code>FromLastUpdatePredicate</code> instance.
	 *
	 * @param now        Instant object that represents "now". Should not be null.
	 * @param dataPolicy data policy to use for test.
	 */
	FromLastUpdatePredicate(final Instant now, final DataPolicy dataPolicy) {
		this.now = Objects.requireNonNull(now);
		this.dataPolicy = Objects.requireNonNull(dataPolicy);
	}

	@Override
	public boolean test(final DataPointValue dataPointValue) {
		Instant start = resolveDataPointRetentionStartDate(dataPointValue);
		long between = Duration.between(start, now).toDays();
		return between >= dataPolicy.getRetentionPeriodInDays();
	}

	/**
	 * Resolves data point retention start date.
	 * If data policy is in {@link com.elasticpath.domain.datapolicy.DataPolicyState#DISABLED} state,
	 * takes the earliest from last modified date of data point value and data policy end date to prevent
	 * associated data been stored forever.
	 * Otherwise, takes last modified date from data point value.
	 *
	 * @param dataPointValue data point value to resolve start date for.
	 * @return Instant object that represents start date.
	 */
	private Instant resolveDataPointRetentionStartDate(final DataPointValue dataPointValue) {
		final Instant start;
		if (DataPolicyState.DISABLED.equals(dataPolicy.getState())) {
			Date dataPolicyEndDate = dataPolicy.getEndDate();
			Date dataPointValueLastModifiedDate = dataPointValue.getLastModifiedDate();
			if (dataPolicyEndDate.before(dataPointValueLastModifiedDate)) {
				start = dataPolicyEndDate.toInstant();
			} else {
				start = dataPointValueLastModifiedDate.toInstant();
			}
		} else {
			start = dataPointValue.getLastModifiedDate().toInstant();
		}
		return start;
	}
}
