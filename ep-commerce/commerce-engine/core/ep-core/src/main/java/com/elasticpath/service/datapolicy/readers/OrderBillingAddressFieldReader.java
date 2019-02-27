/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */

package com.elasticpath.service.datapolicy.readers;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.elasticpath.commons.util.Pair;
import com.elasticpath.service.datapolicy.DataPointLocationEnum;

/**
 * A data point value reader for reading any order billing address field specified by
 * {@link DataPoint}'s dataKey property.
 */
public class OrderBillingAddressFieldReader extends AbstractDataPointValueReader {

	private static final Pair<String, String> ENTITY_CLASS_ALIAS_PAIR = Pair.of("OrderImpl", "o");

	@Override
	public String getSupportedLocation() {
		return DataPointLocationEnum.ORDER_BILLING_ADDRESS.getName();
	}

	@Override
	public String getReadQuery(final Collection<String> dataPointKeys) {

		String query =  "SELECT address.uidPk, address.creationDate, address.lastModifiedDate,"
			.concat(getCsvDbFieldsByDataPointKeys("address.", dataPointKeys))
			.concat(" FROM #TABLE#")
			.concat(" INNER JOIN #alias#.billingAddress address")
			.concat(" WHERE #alias#.customer.guid = ?1");

		return finalizedJPQLQuerys(query);
	}

	@Override
	public Map<String, String> getSupportedFields() {
		return DataPointLocationEnum.ORDER_BILLING_ADDRESS.getSupportedFields();
	}

	@Override
	public List<Pair<String, String>> getEntityClassAndAliasPairs() {
		return Arrays.asList(ENTITY_CLASS_ALIAS_PAIR);
	}
}