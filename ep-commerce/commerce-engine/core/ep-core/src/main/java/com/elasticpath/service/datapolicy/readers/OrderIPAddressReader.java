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
 * A data point value reader for reading order IP address field.
 */
public class OrderIPAddressReader extends AbstractDataPointValueReader {

	private static final Pair<String, String> ENTITY_CLASS_ALIAS_PAIR = Pair.of("OrderImpl", "o");

	@Override
	public String getSupportedLocation() {
		return DataPointLocationEnum.ORDER_IP_ADDRESS.getName();
	}

	@Override
	public String getReadQuery(final Collection<String> dataPointKeys) {

		String query =  "SELECT #alias#.uidPk, #alias#.createdDate, #alias#.lastModifiedDate,"
			.concat(getCsvDbFieldsByDataPointKeys("#alias#.", dataPointKeys))
			.concat(" FROM #TABLE#")
			.concat(" WHERE #alias#.customer.guid = ?1");

		return finalizedJPQLQuerys(query);
	}

	public Map<String, String> getSupportedFields() {
		return DataPointLocationEnum.ORDER_IP_ADDRESS.getSupportedFields();
	}

	@Override
	public List<Pair<String, String>> getEntityClassAndAliasPairs() {
		return Arrays.asList(ENTITY_CLASS_ALIAS_PAIR);
	}
}
