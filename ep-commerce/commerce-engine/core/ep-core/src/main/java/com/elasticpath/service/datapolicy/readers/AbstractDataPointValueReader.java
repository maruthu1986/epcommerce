/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */

package com.elasticpath.service.datapolicy.readers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.commons.util.Pair;
import com.elasticpath.domain.datapolicy.DataPoint;
import com.elasticpath.service.datapolicy.DataPointValueReader;
import com.elasticpath.service.datapolicy.DataPointValueService;
import com.elasticpath.service.datapolicy.impl.DataPointValue;

/**
 * An abstract data point value reader for reading any customer field as specified by the policy.
 */
public abstract class AbstractDataPointValueReader implements DataPointValueReader {

	/** Used in JPQL queries. */
	public static final String PARAMETER_LIST_NAME = "dataPointKeys";
	/** Position of the UidPk field within a data row.*/
	public static final int UIDPK_ROW_INDEX = 0;
	/** Position of the created date field within a data row.*/
	public static final int CREATED_DATE_ROW_INDEX = 1;
	/** Position of the last modified date field within a data row.*/
	public static final int LAST_MODIFIED_DATE_ROW_INDEX = 2;
	/** Position of the attribute's name within a data row.*/
	public static final int ATTRIBUTE_NAME_ROW_INDEX = 3;
	/** Position of the attribute's value within a data row.*/
	public static final int ATTRIBUTE_VALUE_ROW_INDEX = 4;

	private DataPointValueService dataPointValueService;

	@Override
	public Collection<DataPointValue> readValues(final String customerGuid, final Collection<DataPoint> dataPoints) {

		List<String> dataPointKeys = dataPoints.stream()
				.map(DataPoint::getDataKey).collect(Collectors.toList());

		String readQuery = getReadQuery(dataPointKeys);

		return convertRawData(
			dataPointValueService.readValuesByQuery(readQuery, PARAMETER_LIST_NAME, dataPointKeys, new Object[]{customerGuid}),
				customerGuid, dataPoints
		);
	}

	/**
	 * Create a dynamic query and pass it to {@link DataPointValueService#readValuesByQuery(String, String, Collection, Object[])}.
	 *
	 * @param dataPointKeys the list of data point keys to create a query for.
	 * @return the query
	 */
	public abstract String getReadQuery(Collection<String> dataPointKeys);

	/**
	 * Flattens the map of selected supported fields into a CSV string in the format '<LABEL>',<PREFIX>.<DB_FIELD>.
	 *
	 * @param fieldPrefix the prefix e.g. <strong>cust.preferredBillingAddressInternal.</strong>
	 * @param dataPointKeys the list of data point keys e.g. FIRST_NAME, LAST_NAME etc
	 * @return the converted string e.g. 'FIRST_NAME',cust.preferredBillingAddressInternal.firstName
	 */
	protected String getCsvDbFieldsByDataPointKeys(final String fieldPrefix, final Collection<String> dataPointKeys) {

		//transform each map entry [label<=>dbField] into e.g. 'FIRST_NAME',cust.preferredBillingAddressInternal.firstName
		Function<Map.Entry<String, String>, String> addLabelAndPrefixToField = entry -> "'" + entry.getKey() + "'," + fieldPrefix + entry.getValue();

		Map<String, String> selectedSupportedFields = dataPointKeysToDbFields(dataPointKeys);

		return selectedSupportedFields.entrySet().stream()
			.map(addLabelAndPrefixToField)
			.collect(Collectors.joining(","));
	}

	//remove all fields from the supported fields map that do not exist in the list of data point keys
	private Map<String, String> dataPointKeysToDbFields(final Collection<String> dataPointKeys) {
		//create a copy of the default supported fields
		Map<String, String> selectedSupportedFields = new HashMap<>();
		selectedSupportedFields.putAll(getSupportedFields());

		selectedSupportedFields.keySet().retainAll(dataPointKeys);

		return selectedSupportedFields;
	}

	/**
	 * Helper method for converting raw data (Object[]) into a list of {@link DataPointValue} instances.
	 *
	 * @param rawData       the raw data.
	 * @param customerGuid  customer guid.
	 * @param dataPoints    the list of data points
	 * @return the list of {@link DataPointValue}
	 */
	protected List<DataPointValue> convertRawData(final Collection<Object[]> rawData,
												  final String customerGuid,
												  final Collection<DataPoint> dataPoints) {

		List<DataPointValue> dataPointValues = new ArrayList<>();

		/*
			Normally, each row should  represent a single data point value, but in the case of "horizontal" structures like addresses and gift
			certificates, a row may span more data point values.

			The code below transforms a single address data row
			{1234, '2018-02-02 10:15:48', '2018-02-04 18:15:48', 'FIRST_NAME', 'John', 'LAST_NAME', 'TheTester'}

			into 2 DataPointValue instances

			DPV-1
				uidpk=1234
				createdDate=2018-02-02 10:15:48
				lastModifiedDate=2018-02-02 10:15:48
				location="CUSTOMER_BILLING_ADDRESS" //if address is a customer's billing address

				//if values are required then

				key="FIRST_NAME"
				value="John"

			DPV-2
				uidpk=1234
				createdDate=2018-02-02 10:15:48
				lastModifiedDate=2018-02-02 10:15:48
				location="CUSTOMER_BILLING_ADDRESS" //if address is a customer's billing address

				//if values are required then

				key="LAST_NAME"
				value="TheTester"
		 */

		if (rawData.isEmpty()) {

			createEmptyDataPointValuesForPoint(dataPoints, dataPointValues);

		} else {

			Set<String> processedDataKeys = new HashSet<>();

			/* This block handles 3 supported data structures:

				A returned address (or similar table) row looks like (multiple attribute name/val pairs)
				row[] = UIDPK, CREATED_DATE, LAST_MODIFIED_DATE, ATTRIBUTE_1_NAME, ATTRIBUTE_1_VAL, ATTRIBUTE_2_NAME, ATTRIBUTE_2_VAL .....

				A returned customer profile value row looks like (only 1 attribute, multiple values)
				row[] = UIDPK, CREATED_DATE, LAST_MODIFIED_DATE, ATTRIBUTE_1_NAME, ATTRIBUTE_1_VAL_1, ATTRIBUTE_1_VAL_2, ATTRIBUTE_1_VAL_3

				A returned cart/order item GC row looks like (only 1 attribute/val pair)
				row[] = UIDPK, CREATED_DATE, LAST_MODIFIED_DATE, ATTRIBUTE_1_NAME, ATTRIBUTE_1_VAL
			 */

			dataPoints.forEach(dataPoint -> {

				for (Object[] row : rawData) {
					createDataPointValuesForMatchingKeys(row, customerGuid, dataPoint, dataPointValues, processedDataKeys);
				}

				createEmptyDataPointValueIfRequired(dataPoint, dataPointValues, processedDataKeys);

			});
		}

		return dataPointValues;
	}

	//if there are more data points than raw data, create an empty data point value so the reports and tables show all data points
	private void createEmptyDataPointValueIfRequired(final DataPoint dataPoint, final List<DataPointValue> dataPointValues,
		final Set<String> processedDataKeys) {

		if (!processedDataKeys.contains(dataPoint.getDataKey())) {
			createEmptyDataPointValuesForPoint(Collections.singletonList(dataPoint), dataPointValues);
		}
	}

	//create data point values for matching data points
	private void createDataPointValuesForMatchingKeys(final Object[] row,
													  final String customerGuid,
													  final DataPoint dataPoint,
		final List<DataPointValue> dataPointValues, final Set<String> processedDataKeys) {

		//iterate through attributes and find the one that matches current data point key
		for (int i = ATTRIBUTE_NAME_ROW_INDEX; i < row.length; i = i + 2) {
			//if attribute name matches data point key, create a DataPointValue instance
			if (dataPoint.getDataKey().equals(String.valueOf(row[i]))) {

				DataPointValue dpv = createDataPointValueFromRawData(dataPoint, customerGuid, row, i);

				dataPointValues.add(dpv);
				processedDataKeys.add(dataPoint.getDataKey());
			}
		}
	}

	//create data point value with default values
	private void createEmptyDataPointValuesForPoint(final Collection<DataPoint> dataPoints, final List<DataPointValue> dataPointValues) {
		dataPoints.forEach(dataPoint -> {
			String dpKey = dataPoint.getDataKey();

			DataPointValue dpv = new DataPointValue();

			dpv.setLocation(getSupportedLocation());
			dpv.setField(getSupportedFields().get(dpKey));
			dpv.setDataPointName(dataPoint.getName());
			dpv.setKey(dpKey);
			dpv.setRemovable(dataPoint.isRemovable());

			dataPointValues.add(dpv);
		});
	}

	//create data point value from raw data
	private DataPointValue createDataPointValueFromRawData(final DataPoint dataPoint,
														   final String customerGuid,
														   final Object[] row,
														   final int attributeValueIndex) {

		String dpKey = dataPoint.getDataKey();

		DataPointValue dpv = new DataPointValue();

		dpv.setUidPk((Long) row[UIDPK_ROW_INDEX]);
		dpv.setCreatedDate((Date) row[CREATED_DATE_ROW_INDEX]);
		dpv.setLastModifiedDate((Date) row[LAST_MODIFIED_DATE_ROW_INDEX]);
		dpv.setLocation(getSupportedLocation());
		dpv.setField(getSupportedFields().get(dpKey));
		dpv.setDataPointName(dataPoint.getName());
		dpv.setKey(dpKey);
		dpv.setRemovable(dataPoint.isRemovable());
		dpv.setCustomerGuid(customerGuid);
		dpv.setPopulated(true);

		setValue(dpv, row, attributeValueIndex);

		return dpv;
	}

	/**
	 * Set found db value to a newly created {@link DataPointValue}.
	 *
	 * @param dpv the {@link DataPointValue}
	 * @param row the data row
	 * @param attributeValueIndex the index to find the attribute value in the given row
	 */
	protected void setValue(final DataPointValue dpv, final Object[] row, final int attributeValueIndex) {
		dpv.setValue(Objects.toString(row[attributeValueIndex + 1], ""));
	}

	/**
	 * The last method to call to finalize a JPQL query.
	 *
	 * @param query the query to be finalized.
	 * @return finalized query.
	 */
	protected String finalizedJPQLQuerys(final String query) {

		return StringUtils.replaceEach(query, getJPQLSearchList(), getJPQLReplacementList());
	}

	/**
	 * The array of strings denoting table and alias markers to search for and replace with
	 * {@link #getJPQLReplacementList()}.
	 *
	 * @return the array of strings to search for.
	 */
	protected String[] getJPQLSearchList() {
		return new String[]{"#TABLE#", "#alias#"};
	}

	/**
	 * The array of strings to be used as a replacement for {@link #getJPQLSearchList()}.
	 * @return the replacement strings.
	 */
	protected String[] getJPQLReplacementList() {
		List<Pair<String, String>> entityClassAndAliasPairs = getEntityClassAndAliasPairs();
		String entityAlias = entityClassAndAliasPairs.get(0).getSecond();

		return new String[]{getCSVFromTables(entityClassAndAliasPairs), entityAlias};
	}

	/**
	 * Every reader must provide at least one {@link Pair} of domain class name and an alias as part of the FROM clause.
	 *
	 * @param entityAliasPairs the pair e.g. OrderImpl and "o"
	 * @return comma separated string of all provided pairs
	 */
	public String getCSVFromTables(final List<Pair<String, String>> entityAliasPairs) {
		return entityAliasPairs.stream()
			.map(pair -> pair.getFirst() + " " + pair.getSecond())
			.collect(Collectors.joining(", "));
	}

	/**
	 * Return a list of {@link Pair}s of domain class name and its alias used in FROM clause.
	 *
	 * @return the list of pairs.
	 */
	public abstract List<Pair<String, String>> getEntityClassAndAliasPairs();

	// mutators

	public DataPointValueService getDataPointValueService() {
		return dataPointValueService;
	}

	public void setDataPointValueService(final DataPointValueService dataPointValueService) {
		this.dataPointValueService = dataPointValueService;
	}
}
