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
 * A data point value reader for any order payment gift certificate field.
 *
 */
public class OrderPaymentGiftCertificateFieldReader extends AbstractDataPointValueReader {

	private static final Pair<String, String> ENTITY_CLASS_ALIAS_PAIR = Pair.of("GiftCertificateImpl", "gc");

	@Override
	public String getSupportedLocation() {
		return DataPointLocationEnum.ORDER_PAYMENT_GIFT_CERTIFICATE.getName();
	}

	@Override
	public String getReadQuery(final Collection<String> dataPointKeys) {

		String query =  "SELECT #alias#.uidPk, #alias#.creationDate, #alias#.lastModifiedDate,"
			.concat(getCsvDbFieldsByDataPointKeys("#alias#.", dataPointKeys))
			.concat(" FROM #TABLE#")
			.concat(" WHERE #alias#.purchaser.guid = ?1");

		return finalizedJPQLQuerys(query);
	}

	public Map<String, String> getSupportedFields() {
		return DataPointLocationEnum.ORDER_PAYMENT_GIFT_CERTIFICATE.getSupportedFields();
	}

	@Override
	public List<Pair<String, String>> getEntityClassAndAliasPairs() {
		return Arrays.asList(ENTITY_CLASS_ALIAS_PAIR);
	}
}
