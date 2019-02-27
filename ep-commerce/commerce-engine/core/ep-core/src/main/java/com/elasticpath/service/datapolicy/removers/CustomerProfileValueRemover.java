/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */

package com.elasticpath.service.datapolicy.removers;

import java.util.Collection;

import com.elasticpath.commons.util.Pair;
import com.elasticpath.service.datapolicy.DataPointLocationEnum;
import com.elasticpath.service.datapolicy.impl.DataPointValue;
import com.elasticpath.service.search.IndexType;

/**
 * The customer profile value remover.
 */
public class CustomerProfileValueRemover extends AbstractDataPointValueRemover {

	@Override
	public String getSupportedLocation() {
		return DataPointLocationEnum.CUSTOMER_PROFILE.getName();
	}

	@Override
	protected Pair<String, String> getJPQLUpdate() {
		return Pair.of("CustomerProfileValueImpl", "profile");
	}

	@Override
	protected String getRemoveQuery(final Collection<DataPointValue> dataPointValues) {
		Pair<String, String> entityAliasPair = getJPQLUpdate();

		String entity = entityAliasPair.getFirst();
		String alias = entityAliasPair.getSecond();

		String deleteQuery = "DELETE FROM " + entity + " " + alias;

		return new StringBuilder(deleteQuery).append(' ')
			.append(getWhereClauseForEntity(alias))
			.toString();
	}

	@Override
	protected IndexType getIndexType() {
		return IndexType.CUSTOMER;
	}
}
