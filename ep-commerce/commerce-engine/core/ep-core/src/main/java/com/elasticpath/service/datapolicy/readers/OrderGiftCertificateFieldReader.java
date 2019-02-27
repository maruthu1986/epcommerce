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
 * A data point value reader for any order item gift certificate field.
 *
 */
public class OrderGiftCertificateFieldReader extends AbstractDataPointValueReader {

	private static final String NAMED_VALIDATION_QUERY = "VERIFY_EXISTS_GIFT_CERTIFICATE_ATTRIBUTE";
	private static final Pair<String, String> ENTITY_CLASS_ALIAS_PAIR = Pair.of("OrderImpl", "o");

	@Override
	public String getSupportedLocation() {
		return DataPointLocationEnum.ORDER_GIFT_CERTIFICATE.getName();
	}

	@Override
	public String getReadQuery(final Collection<String> dataPointKeys) {

		String query = "SELECT oItemData.uidPk, oItemData.creationDate, oItemData.lastModifiedDate, oItemData.key, oItemData.value"
			.concat(" FROM #TABLE#")
			.concat(" JOIN #alias#.shipments shipments")
			.concat(" JOIN shipments.shipmentOrderSkusInternal skus")
			.concat(" JOIN skus.itemData oItemData")
			.concat(" WHERE #alias#.customer.guid = ?1")
			.concat(" AND oItemData.key IN (:#paramList#)");

		return finalizedJPQLQuerys(query);
	}

	public Map<String, String> getSupportedFields() {
		return DataPointLocationEnum.ORDER_GIFT_CERTIFICATE.getSupportedFields();
	}

	@Override
	protected String[] getJPQLSearchList() {
		return new String[]{"#TABLE#", "#alias#", "#paramList#"};
	}

	@Override
	protected String[] getJPQLReplacementList() {
		List<Pair<String, String>> entityClassAndAliasPairs = getEntityClassAndAliasPairs();
		String entityAlias = entityClassAndAliasPairs.get(0).getSecond();

		return new String[]{getCSVFromTables(entityClassAndAliasPairs), entityAlias, PARAMETER_LIST_NAME};
	}

	@Override
	public List<Pair<String, String>> getEntityClassAndAliasPairs() {
		return Arrays.asList(ENTITY_CLASS_ALIAS_PAIR);
	}

	/**
	 * Get named validation query.
	 *
	 * @return the named validation query.
	 */
	protected String getNamedValidationQuery() {
		return NAMED_VALIDATION_QUERY;
	}

	@Override
	public boolean validateKey(final String dataKey) {
		return getDataPointValueService().validateKey(dataKey, getNamedValidationQuery());
	}
}
