/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */

package com.elasticpath.service.datapolicy.readers;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.ObjectUtils;

import com.elasticpath.commons.util.Pair;
import com.elasticpath.service.datapolicy.DataPointLocationEnum;
import com.elasticpath.service.datapolicy.impl.DataPointValue;

/**
 * A data point value reader for any customer profile value.
 */
public class CustomerProfileValueReader extends AbstractDataPointValueReader {

	private static final Pair<String, String> ENTITY_CLASS_ALIAS_PAIR = Pair.of("CustomerImpl", "cust");
	private static final String NAMED_VALIDATION_QUERY = "VERIFY_EXISTS_CUSTOMER_PROFILE_ATTRIBUTE";

	@Override
	public String getSupportedLocation() {
		return DataPointLocationEnum.CUSTOMER_PROFILE.getName();
	}

	@Override
	public String getReadQuery(final Collection<String> dataPointKeys) {
		String query = "SELECT cpv.uidPk,cpv.creationDate,cpv.lastModifiedDate,"
			.concat(" cpv.localizedAttributeKey,cpv.shortTextValue,cpv.integerValue,cpv.longTextValue,")
			.concat(" cpv.decimalValue,cpv.booleanValue,cpv.dateValue")
			.concat(" FROM #TABLE#")
			.concat(" INNER JOIN #alias#.profileValueMap cpv")
			.concat(" WHERE #alias#.guid = ?1")
			.concat(" AND cpv.localizedAttributeKey IN (:#paramList#)");

		return finalizedJPQLQuerys(query);
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
	public boolean validateKey(final String dataKey) {
		return getDataPointValueService().validateKey(dataKey, getNamedValidationQuery());
	}

	@Override
	public Map<String, String> getSupportedFields() {
		return DataPointLocationEnum.CUSTOMER_PROFILE.getSupportedFields();
	}

	@Override
	public void setValue(final DataPointValue dpv, final Object[] row, final int attributeValueIndex) {
		dpv.setValue(getAttributeValue(row));
	}

	private String getAttributeValue(final Object[] row) {
		Object[] attributeValues = Arrays.copyOfRange(row, ATTRIBUTE_VALUE_ROW_INDEX, row.length);

		//each profile attribute can have 6 possible types, but only 1 non-null value
		Object firstNonNull = ObjectUtils.firstNonNull(attributeValues);
		return Objects.toString(firstNonNull, "");
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
	public List<Pair<String, String>> getEntityClassAndAliasPairs() {
		return Arrays.asList(ENTITY_CLASS_ALIAS_PAIR);
	}
}
