/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.cucumber.datapolicy;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Named;

import cucumber.api.DataTable;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.elasticpath.cucumber.ScenarioContextValueHolder;
import com.elasticpath.domain.attribute.CustomerProfileValue;
import com.elasticpath.domain.builder.datapolicy.DataPolicyBuilder;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.datapolicy.ConsentAction;
import com.elasticpath.domain.datapolicy.CustomerConsent;
import com.elasticpath.domain.datapolicy.DataPoint;
import com.elasticpath.domain.datapolicy.DataPolicy;
import com.elasticpath.domain.datapolicy.DataPolicyState;
import com.elasticpath.domain.datapolicy.RetentionType;
import com.elasticpath.domain.datapolicy.impl.CustomerConsentImpl;
import com.elasticpath.domain.datapolicy.impl.DataPointImpl;
import com.elasticpath.service.customer.CustomerService;
import com.elasticpath.service.datapolicy.CustomerConsentService;
import com.elasticpath.service.datapolicy.DataPointService;
import com.elasticpath.service.datapolicy.DataPointValueService;
import com.elasticpath.service.datapolicy.DataPolicyService;
import com.elasticpath.service.datapolicy.impl.DataPointValue;
import com.elasticpath.service.datapolicy.job.DataPointValueJob;
import com.elasticpath.test.persister.TestApplicationContext;
import com.elasticpath.test.util.Utils;

/**
 * Data policy test step definitions class.
 */
public class DataPolicyStepDefinitions {

	/**
	 * Number of data point values.
	 */
	public static final int NUMBER_OF_DATA_POINT_VALUES = 6;

	/**
	 * Guid index.
	 */
	public static final int GUID_INDEX = 0;

	/**
	 * Location index.
	 */
	public static final int NAME_INDEX = 1;

	/**
	 * Location index.
	 */
	public static final int LOCATION_INDEX = 2;

	/**
	 * Key index.
	 */
	public static final int KEY_INDEX = 3;

	/**
	 * Description index.
	 */
	public static final int DESCRIPTION_INDEX = 4;

	/**
	 * removeable index.
	 */
	public static final int REMOVEABLE_INDEX = 5;

	@Autowired
	private TestApplicationContext tac;

	@Autowired
	private DataPointValueService dataPointValueService;

	@Autowired
	private DataPointService dataPointService;

	@Autowired
	private DataPolicyService dataPolicyService;

	@Autowired
	private DataPolicyBuilder dataPolicyBuilder;

	@Autowired
	private CustomerConsentService customerConsentService;

	@Autowired
	@Qualifier("expiredDataPointValuesJobProcessor")
	private DataPointValueJob expiredDataPointValuesJobProcessor;

	@Autowired
	@Qualifier("dataPointRevokedConsentsJobProcessor")
	private DataPointValueJob dataPointRevokedConsentsJobProcessor;

	@Inject
	@Named("customerHolder")
	private ScenarioContextValueHolder<Customer> customerHolder;

	@Autowired
	private CustomerService customerService;

	private DataPoint dataPoint;
	private DataPointValue dataPointValue;

	/**
	 * Sets up a default data point.
	 *
	 * @param location the data point location.
	 * @param key      the data point key.
	 */
	@Given("^a data point defined with location (.+?) and key (.+?)$")
	public void createDataPointWithLocationAndKey(final String location, final String key) {
		dataPoint = new DataPointImpl();
		dataPoint.setDataKey(key);
		dataPoint.setDataLocation(location);
	}

	/**
	 * Setup the tests with data polices.
	 *
	 * @param dataTable data policy info.
	 */
	@Given("^the existing data policies of$")
	public void setUpDataPolicies(final DataTable dataTable) {
		saveDataPoliciesFromDataTable(dataTable.asMaps(String.class, String.class));
	}

	/**
	 * Setup the tests with customer consents.
	 *
	 * @param dataTable customer consent info.
	 * @throws ParseException in case of date parsing error.
	 */
	@Given("^the existing customer consents of$")
	public void setUpCustomerConsents(final DataTable dataTable) throws ParseException {
		saveCustomerConsentsFromDataTable(dataTable.asMaps(String.class, String.class));
	}

	/**
	 * Run expired data point values job.
	 */
	@When("^expired data point values job processor runs")
	public void runExpiredDataPointValuesJobProcessor() {
		expiredDataPointValuesJobProcessor.process();
	}

	/**
	 * Run revoked consents job.
	 */
	@When("^revoked consents data point values job processor runs")
	public void runDataPointRevokedConsentsJobProcessor() {
		dataPointRevokedConsentsJobProcessor.process();
	}

	/**
	 * Ensure the data point values processed by job.
	 *
	 * @param customerGuid customer guid.
	 * @param dataPointGuid  data point guid.
	 * @param dataPointValue expected data point value.
	 */
	@Then("^the data point value with customer guid \\[([a-zA-Z0-9_]+)\\] and data point guid \\[([a-zA-Z0-9_]+)\\] should have value \\["
			+ "([a-zA-Z0-9_‐]+)\\]$")
	public void ensureDataPointValue(final String customerGuid, final String dataPointGuid, final String dataPointValue) {

		DataPoint dataPoint = dataPointService.findByGuid(dataPointGuid);

		Map<String, List<DataPoint>> dataPointMap = new HashMap<>();
		dataPointMap.put(customerGuid, Collections.singletonList(dataPoint));

		Collection<DataPointValue> dataPointValues = dataPointValueService.getValues(dataPointMap);

		DataPointValue actualValue = (DataPointValue) dataPointValues.toArray()[0];

		assertThat(actualValue.getValue())
				.as("Expected data point value not match")
				.isEqualTo(resolveValue(dataPointValue));
	}

	/**
	 * Ensure the customer profile data point value processed by job.
	 *
	 * @param customerGuid the customer guid.
	 * @param dataPointKey data point key.
	 */
	@Then("^the customer profile data point value with customer guid \\[([a-zA-Z0-9_]+)\\] and data point key \\[([A-Z_]+)\\] has been deleted$")
	public void ensureCustomerProfileDataPointValue(final String customerGuid, final String dataPointKey) {
		Customer customer = customerService.findByGuid(customerGuid);
		Map<String, CustomerProfileValue> profileValueMap = customer.getProfileValueMap();
		assertThat(profileValueMap.get(dataPointKey))
				.as("Expected customer profile data point value not match")
				.isNull();
	}

	/**
	 * Read data point value.
	 */
	@When("^I request the data point value belonging to customer$")
	public void readDataPointValue() {
		Map<String, Collection<DataPoint>> customerGuidToDataPoints = new HashMap<>(1);
		customerGuidToDataPoints.put(customerHolder.get().getGuid(), Collections.singletonList(dataPoint));

		Collection<DataPointValue> dataPointValues = dataPointValueService.getValues(customerGuidToDataPoints);
		if (!dataPointValues.isEmpty()) {
			dataPointValue = dataPointValues.iterator().next();
		}
	}

	/**
	 * Assert that data point value is same as the expected one.
	 *
	 * @param expectedValue the expected data point value.
	 */
	@Then("^I should see the value (.+?)$")
	public void assertDataPointValueAgainstExpectedValue(final String expectedValue) {
		assertThat(dataPointValue)
				.as("Data point value must not be null")
				.isNotNull();

		assertThat(dataPointValue.getValue())
				.as("Actual and expected values are not the same")
				.isEqualTo(expectedValue);
	}

	/**
	 * Remove data point.
	 */
	@When("^I remove the data point value belonging to customer$")
	public void removeDataPoint() {
		readDataPointValue();

		dataPointValueService.removeValues(Collections.singletonList(dataPointValue));
	}

	/**
	 * Assert that data point value is null after removal.
	 */
	@Then("^there should no longer be a value for that data point for customer$")
	public void assertDataPointValueIsNull() {
		Customer customer = tac.getPersistersFactory().getStoreTestPersister().getCustomerByGuid(customerHolder.get().getGuid());

		assertThat(customer.getFirstName())
				.as("Data point value must be null")
				.isNull();
	}

	private void saveDataPoliciesFromDataTable(final List<Map<String, String>> dataPolicesMap) {
		for (Map<String, String> properties : dataPolicesMap) {
			final DataPolicy dataPolicy = dataPolicyBuilder.newInstance()
					.withGuid(properties.get("guid"))
					.withPolicyName(properties.get("dataPolicyName"))
					.withDescription(properties.get("description"))
					.withRefernceKey(properties.get("referenceKey"))
					.withRetentionPeriodInDays(Integer.valueOf(properties.get("retentionPeriod")))
					.withDataPolicyState(DataPolicyState.valueOf(properties.get("policyState")))
					.withStartDate(new Date())
					.withEndDate(Utils.getDate(properties.get("endDate")))
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
			dataPolicyService.update(dataPolicy);
		}
	}

	private String resolveValue(final String input) {
		return (input == null || input.equals("null")) ? "" : input;
	}

	private void saveCustomerConsentsFromDataTable(final List<Map<String, String>> customerConsentsMap) throws ParseException {
		for (Map<String, String> properties : customerConsentsMap) {
			final CustomerConsent customerConsent = new CustomerConsentImpl();

			customerConsent.setGuid(properties.get("guid"));
			DataPolicy dataPolicy = dataPolicyService.findByGuid(properties.get("dataPolicyGuid"));
			customerConsent.setDataPolicy(dataPolicy);
			customerConsent.setAction(ConsentAction.valueOf(properties.get("action")));
			customerConsent.setConsentDate(Utils.getDate(properties.get("consentDate")));
			customerConsent.setCustomerGuid(properties.get("customerGuid"));

			customerConsentService.save(customerConsent);
		}
	}
}