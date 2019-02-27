package com.elasticpath.service.datapolicy.job.impl;

import java.util.Date;
import java.util.UUID;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.impl.CustomerImpl;
import com.elasticpath.domain.datapolicy.ConsentAction;
import com.elasticpath.domain.datapolicy.CustomerConsent;
import com.elasticpath.domain.datapolicy.DataPoint;
import com.elasticpath.domain.datapolicy.DataPolicy;
import com.elasticpath.domain.datapolicy.RetentionType;
import com.elasticpath.domain.datapolicy.impl.CustomerConsentImpl;
import com.elasticpath.domain.datapolicy.impl.DataPointImpl;
import com.elasticpath.domain.datapolicy.impl.DataPolicyImpl;
import com.elasticpath.service.datapolicy.impl.DataPointValue;

/**
 * Utility class for DataPoint/DataPolicy related tests.
 */
public final class DataPointTestUtil {

	/**
	 * Data point key 1 constant.
	 */
	public static final String DATAPOINT_KEY_1 = "DATAPOINT_KEY_1";
	/**
	 * Data point key 2 constant.
	 */
	public static final String DATAPOINT_KEY_2 = "DATAPOINT_KEY_2";
	/**
	 * Data point key 3 constant.
	 */
	public static final String DATAPOINT_KEY_3 = "DATAPOINT_KEY_3";
	private static final String DEFAULT_DPV_VALUE = "DPV_VALUE";
	private static final String DATAPOINT_LOCATION = "DATAPOINT_LOCATION";

	private DataPointTestUtil() {
		//static class
	}

	/**
	 * Build DataPolicy with pre-populated data.
	 *
	 * @param retentionType   retention type.
	 * @param retentionPeriod retention period.
	 * @return new Data Policy.
	 */
	public static DataPolicy buildDataPolicy(final RetentionType retentionType, final Integer retentionPeriod) {
		DataPolicy dataPolicy = new DataPolicyImpl();
		dataPolicy.initialize();
		dataPolicy.setRetentionType(retentionType);
		dataPolicy.setRetentionPeriodInDays(retentionPeriod);
		return dataPolicy;
	}

	/**
	 * Build DataPointValue with pre-populated data.
	 *
	 * @param date         date.
	 * @param customerGuid customer guid.
	 * @param dataPointKey datapoint key.
	 * @return new DataPointValue.
	 */
	public static DataPointValue buildDataPointValue(final Date date, final String customerGuid, final String dataPointKey) {
		final DataPointValue dataPointValue = new DataPointValue();
		dataPointValue.setCreatedDate(date);
		dataPointValue.setLastModifiedDate(date);
		dataPointValue.setValue(DEFAULT_DPV_VALUE);
		dataPointValue.setKey(dataPointKey);
		dataPointValue.setLocation(DATAPOINT_LOCATION);
		dataPointValue.setCustomerGuid(customerGuid);
		dataPointValue.setPopulated(true);
		return dataPointValue;
	}

	/**
	 * Build CustomerConsent with pre-populated data.
	 *
	 * @param action ConsentAction.
	 * @return new CustomerConsent.
	 */
	public static CustomerConsent buildCustomerConsent(final ConsentAction action) {
		final CustomerConsent customerConsent = new CustomerConsentImpl();
		customerConsent.setAction(action);
		return customerConsent;
	}

	/**
	 * Build DataPoint with pre-populated data.
	 *
	 * @param removable    is removable.
	 * @param dataPointKey datapoint key.
	 * @return new DataPoint.
	 */
	public static DataPoint buildDataPoint(final boolean removable, final String dataPointKey) {
		final DataPoint dataPoint = new DataPointImpl();
		dataPoint.setDataLocation(DATAPOINT_LOCATION);
		dataPoint.setDataKey(dataPointKey);
		dataPoint.setRemovable(removable);
		dataPoint.initialize();
		return dataPoint;
	}

	/**
	 * Build Customer with pre-populated data.
	 *
	 * @return new Customer.
	 */
	public static Customer buildCustomer() {
		final Customer customer = new CustomerImpl();
		customer.setGuid(UUID.randomUUID().toString());
		return customer;
	}
}
