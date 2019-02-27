/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.cucumber.steps;


import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import cucumber.api.DataTable;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.domain.builder.datapolicy.DataPolicyBuilder;
import com.elasticpath.domain.datapolicy.DataPoint;
import com.elasticpath.domain.datapolicy.DataPolicy;
import com.elasticpath.domain.datapolicy.DataPolicyState;
import com.elasticpath.domain.datapolicy.RetentionType;
import com.elasticpath.domain.datapolicy.impl.DataPointImpl;
import com.elasticpath.service.datapolicy.DataPointService;
import com.elasticpath.service.datapolicy.DataPolicyService;

/**
 * Data policy steps.
 */
public class DataPolicySteps {

	/** Number of data point values. */
	public static final int NUMBER_OF_DATA_POINT_VALUES = 6;

	/** Guid index. */
	public static final int GUID_INDEX = 0;

	/** Location index. */
	public static final int NAME_INDEX = 1;

	/** Location index. */
	public static final int LOCATION_INDEX = 2;

	/** Key index. */
	public static final int KEY_INDEX = 3;

	/** Description index. */
	public static final int DESCRIPTION_INDEX = 4;

	/** removeable index. */
	public static final int REMOVEABLE_INDEX = 5;

	@Autowired
	private DataPolicyService dataPolicyService;

	@Autowired
	private DataPolicyBuilder dataPolicyBuilder;

	@Autowired
	private DataPointService dataPointService;

	/**
	 * Setup the tests with data polices.
	 *
	 * @param dataTable data policy info.
	 */
	@Given("^the existing data policies of$")
	public void setUpDataPolicies(final DataTable dataTable) {
		saveDataPoliciesFromDataTable(dataTable.asMaps(String.class, String.class));
	}

	private void saveDataPoliciesFromDataTable(final List<Map<String, String>> dataPolicesMap) {
		final List<DataPolicy> dataPolicies = new ArrayList<>();
		for (Map<String, String> properties : dataPolicesMap) {
			final DataPolicy dataPolicy = dataPolicyBuilder.newInstance()
					.withGuid(properties.get("guid"))
					.withPolicyName(properties.get("dataPolicyName"))
					.withDescription(properties.get("description"))
					.withRefernceKey(properties.get("referenceKey"))
					.withRetentionPeriodInDays(Integer.valueOf(properties.get("retentionPeriod")))
					.withDataPolicyState(DataPolicyState.valueOf(properties.get("policyState")))
					.withStartDate(new Date())
					.withEndDate(new Date())
					.withRetentionType(RetentionType.valueOf(properties.get("retentionType")))
					.withSegments(new HashSet<>(Arrays.asList(properties.get("segments").split(","))))
					.build();

			List<String> dataPointStringsList = Arrays.asList(properties.get("dataPoints").split(","));

			for (String dataPointStrings : dataPointStringsList) {
				String[] dataPointArray = dataPointStrings.trim().split(";");
				if (dataPointArray.length == NUMBER_OF_DATA_POINT_VALUES) {
					DataPoint dataPoint = dataPointService.findByGuid(dataPointArray[GUID_INDEX]);
					if (dataPoint == null) {
						dataPoint = new DataPointImpl();
						dataPoint.setGuid(dataPointArray[GUID_INDEX]);
						dataPoint.setName(dataPointArray[NAME_INDEX]);
						dataPoint.setDescriptionKey(dataPointArray[DESCRIPTION_INDEX]);
						dataPoint.setDataKey(dataPointArray[KEY_INDEX]);
						dataPoint.setDataLocation(dataPointArray[LOCATION_INDEX]);
						dataPoint.setRemovable(Boolean.parseBoolean(dataPointArray[REMOVEABLE_INDEX]));
					}
					dataPolicy.getDataPoints().add(dataPoint);
				}
			}

			dataPolicies.add(dataPolicy);
			dataPolicyService.update(dataPolicy);
		}
	}

	/**
	 * Ensure data policy has enabled value.
	 *
	 * @param guid    the data policy guid
	 * @param policyStateString the policy state string
	 */
	@Then("the data policy \\[([A-Z0-9_]+)\\] should have a policy state of \\[(DRAFT|ACTIVE|DISABLED)\\]$")
	public void ensureDataPolicyHasEnabledValue(final String guid, final String policyStateString) {
		final DataPolicy dataPolicy = dataPolicyService.findByGuid(guid);
		final DataPolicyState shouldBeState = DataPolicyState.valueOf(policyStateString);

		assertThat(dataPolicy)
				.as(String.format("Data policy [%s] not found in imported data policy records", guid))
				.isNotNull();

		assertThat(shouldBeState)
				.as(String.format("Data policy [%s] not found in imported data policy records", guid))
				.isEqualTo(dataPolicy.getState());
	}

	/**
	 * Ensure data policy has roles.
	 *
	 * @param guid  the data policy guid
	 * @param dataPointString the data points string
	 */
	@Then("the data policy \\[([A-Z0-9_]+)\\] should have data points? \\[([A-Z0-9_,]+)\\]$")
	public void ensureDataPolicyHasDataPoints(final String guid, final String dataPointString) {
		validateDataPolicyWithDataPoints(guid, Arrays.asList(dataPointString.split(",")));
	}

	/**
	 * Ensure data policy has no data points.
	 *
	 * @param guid the data policy guid
	 */
	@Then("the data policy \\[([A-Z0-9_]+)\\] should have no data points")
	public void ensureDataPolicyHasDataPoints(final String guid) {
		validateDataPolicyWithDataPoints(guid, Collections.<String>emptyList());
	}

	private void validateDataPolicyWithDataPoints(final String guid, final List<String> expectedDataPoints) {
		final DataPolicy dataPolicy = dataPolicyService.findByGuid(guid);

		assertThat(dataPolicy)
				.as(String.format("Data policy [%s] not found in imported data policy records", guid))
				.isNotNull();

		final List<String> dataPoints = dataPolicy.getDataPoints()
				.stream()
				.map(DataPoint::getGuid)
				.collect(Collectors.toList());

		assertThat(expectedDataPoints)
				.as(String.format("Data policy [%s] has incorrect data points", guid))
				.isEqualTo(dataPoints);
	}

}
