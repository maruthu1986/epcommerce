/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.datapolicy.job.impl.filter;

import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

import com.elasticpath.domain.datapolicy.DataPolicy;
import com.elasticpath.domain.datapolicy.RetentionType;
import com.elasticpath.service.datapolicy.impl.DataPointValue;

/**
 * Predicate which chains {@link FromCreationDatePredicate} and {@link FromLastUpdatePredicate}
 * according to passed <code>(dataPoint -> [dataPolicy])</code> values.<br></br>
 * Allows to have as many data policies associated with data point as required.
 */
public class DataPointRetentionPeriodFilter implements Predicate<DataPointValue> {

	private static final Predicate<DataPointValue> ALWAYS_FALSE_PREDICATE = value -> false;
	private final Map<String, Set<DataPolicy>> dataPointMap;
	private final Instant now;

	/**
	 * Main constructor used to create <code>DataPointRetentionPeriodFilter</code>.
	 *
	 * @param now          Date object that represents current time. Should not be null, otherwise runtime exception will be thrown upon construction.
	 * @param dataPointMap <code>(dataPoint -> [dataPolicy])</code> map. If empty, or if nested <code>Set</code>
	 *                     is empty, {@link #ALWAYS_FALSE_PREDICATE} will be used as a predicate.
	 */
	public DataPointRetentionPeriodFilter(final Date now, final Map<String, Set<DataPolicy>> dataPointMap) {
		this.dataPointMap = Objects.requireNonNull(dataPointMap);
		this.now = Objects.requireNonNull(now).toInstant();
	}

	@Override
	public boolean test(final DataPointValue dataPointValue) {
		return dataPointMap.getOrDefault(dataPointValue.getExpandedLocation(), Collections.emptySet())
				.stream()
				.map(this::getPredicate)
				.reduce(Predicate::and)
				.orElse(ALWAYS_FALSE_PREDICATE)
				.test(dataPointValue);
	}

	/**
	 * Factory method to get predicate according to DataPolicy retention type and retention period.
	 * If no corresponding predicate found, {@link #ALWAYS_FALSE_PREDICATE} will be used by default.
	 *
	 * @param dataPolicy DataPolicy object to get predicate for.
	 * @return corresponding <code>Predicate</code> object.
	 */
	private Predicate<DataPointValue> getPredicate(final DataPolicy dataPolicy) {
		String retentionType = dataPolicy.getRetentionType().getName();
		switch (retentionType) {
			case RetentionType.FROM_CREATION_DATE_VALUE:
				return new FromCreationDatePredicate(now, dataPolicy);
			case RetentionType.FROM_LAST_UPDATE_VALUE:
				return new FromLastUpdatePredicate(now, dataPolicy);
			default:
				return ALWAYS_FALSE_PREDICATE;
		}
	}
}
