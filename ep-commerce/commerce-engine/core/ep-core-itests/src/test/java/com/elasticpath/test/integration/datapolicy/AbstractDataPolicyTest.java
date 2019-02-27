/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.test.integration.datapolicy;

import java.util.Arrays;
import java.util.Currency;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.elasticpath.common.pricing.service.PriceListHelperService;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.datapolicy.ConsentAction;
import com.elasticpath.domain.datapolicy.CustomerConsent;
import com.elasticpath.domain.datapolicy.DataPoint;
import com.elasticpath.domain.datapolicy.DataPolicy;
import com.elasticpath.domain.datapolicy.DataPolicyState;
import com.elasticpath.domain.datapolicy.RetentionType;
import com.elasticpath.domain.datapolicy.impl.CustomerConsentImpl;
import com.elasticpath.domain.datapolicy.impl.DataPointImpl;
import com.elasticpath.domain.datapolicy.impl.DataPolicyImpl;
import com.elasticpath.domain.factory.TestCustomerSessionFactoryForTestApplication;
import com.elasticpath.domain.factory.TestShopperFactoryForTestApplication;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.service.customer.CustomerService;
import com.elasticpath.service.datapolicy.CustomerConsentService;
import com.elasticpath.service.datapolicy.DataPointService;
import com.elasticpath.service.datapolicy.DataPolicyService;
import com.elasticpath.service.shopper.ShopperService;
import com.elasticpath.test.integration.BasicSpringContextTest;
import com.elasticpath.test.persister.SettingsTestPersister;
import com.elasticpath.test.util.Utils;

public abstract class AbstractDataPolicyTest extends BasicSpringContextTest {

	/** Data point Unique Code 1. */
	protected static final String DATA_POINT_UNIQUE_CODE = "DATA_POINT";
	/** Data Policy Unique Code 1. */
	protected static final String DATA_POLICY_UNIQUE_CODE = "DATA_POLICY";
	/** Data Policy Unique Code 2. */
	protected static final String DATA_POLICY_UNIQUE_CODE2 = "DATA_POLICY2";
	/** Data Policy Unique Code 2. */
	protected static final String DATA_POLICY_UNIQUE_CODE3 = "DATA_POLICY3";
	/** Customer Consent Unique Code 1. */
	protected static final String CUSTOMER_CONSENT_UNIQUE_CODE = "CUSTOMER_CONSENT";
	/** Customer Consent Unique Code 2. */
	protected static final String CUSTOMER_CONSENT_UNIQUE_CODE2 = "CUSTOMER_CONSENT2";
	/** Customer Consent Unique Code 3. */
	protected static final String CUSTOMER_CONSENT_UNIQUE_CODE3 = "CUSTOMER_CONSENT3";

	private static final long DAY_IN_MILLISEC = 1000 * 60 * 60 * 24;
	/** Date object representing yesterday. */
	protected static final Date YESTERDAY_DATE = new Date(System.currentTimeMillis() - DAY_IN_MILLISEC);
	/** Date object representing today. */
	protected static final Date TODAY_DATE = new Date();

	private static final String DESCRIPTION_KEY = "DESCRIPTION_KEY";
	protected static final String DATA_POINT_KEY_1 = "DATA_POINT_KEY_1";
	protected static final String DATA_POINT_KEY_2 = "DATA_POINT_KEY_2";
	protected static final String DATA_POINT_LOCATION = "DATA_POINT_LOCATION";
	private static final String DATA_POLICY_NAME = "DATA_POLICY_NAME";
	private static final String DATA_POLICY_DESCRIPTION = "DATA_POLICY_DESCRIPTION";
	private static final String DATA_POLICY_REFERENCE_KEY = "DATA_POLICY_REFERENCE_KEY";
	protected static final String CA = "CA";
	protected static final String EU = "EU";
	/** Customer Email 1. */
	protected static final String TEST_EMAIL = "test@elasticpath.com";
	/** Customer Email 2. */
	protected static final String TEST_EMAIL2 = "test2@elasticpath.com";
	public static final String DATA_POINT_NAME = "DATA_POINT_NAME";
	public static final String DATA_POINT_NAME_2 = "DATA_POINT_NAME_2";

	@Autowired
	DataPointService dataPointService;

	@Autowired
	DataPolicyService dataPolicyService;

	@Autowired
	CustomerConsentService customerConsentService;

	@Autowired
	@Qualifier("customerService")
	CustomerService customerService;

	@Autowired
	BeanFactory beanFactory;

	@Autowired
	protected ShopperService shopperService;

	DataPoint createAndSaveDataPoint(final String dataPointName) {
		return dataPointService.save(createDataPoint(dataPointName, DATA_POINT_KEY_1, DATA_POINT_LOCATION));
	}

	DataPoint createAndSaveDataPoint(final String dataPointName, final String dataPointKey, final String dataPointLocation) {
		return dataPointService.save(createDataPoint(dataPointName, dataPointKey, dataPointLocation));
	}

	DataPoint createAndSaveDataPoint(final String dataPointName, final String dataPointKey, final String dataPointLocation,
		final boolean removable) {

		DataPoint dataPoint = createDataPoint(dataPointName, dataPointKey, dataPointLocation);
		dataPoint.setRemovable(removable);

		return dataPointService.save(dataPoint);
	}

	DataPoint createDataPoint(final String dataPointName, final String dataPointKey, final String dataPointLocation) {
		final DataPoint dataPoint = new DataPointImpl();
		dataPoint.setGuid(UUID.randomUUID().toString());
		dataPoint.setName(dataPointName);
		dataPoint.setDataKey(dataPointKey);
		dataPoint.setDataLocation(dataPointLocation);
		dataPoint.setDescriptionKey(DESCRIPTION_KEY);
		dataPoint.setRemovable(true);
		return dataPoint;
	}

	protected DataPolicy createAndSaveDataPolicy(final String code) {
		final DataPolicy dataPolicy = new DataPolicyImpl();
		dataPolicy.setGuid(Utils.uniqueCode(code));
		dataPolicy.setPolicyName(DATA_POLICY_NAME);
		dataPolicy.setRetentionPeriodInDays(1);
		dataPolicy.setDescription(DATA_POLICY_DESCRIPTION);
		dataPolicy.setEndDate(new Date());
		dataPolicy.setStartDate(new Date());
		dataPolicy.setState(DataPolicyState.ACTIVE);
		dataPolicy.setRetentionType(RetentionType.FROM_CREATION_DATE);
		dataPolicy.setSegments(getSegments());
		dataPolicy.setReferenceKey(DATA_POLICY_REFERENCE_KEY);
		return dataPolicyService.save(dataPolicy);
	}

	DataPolicy createAndSaveDataPolicyWithStateAndStartDateAndEndDate(final String code, DataPolicyState state, Date startDate, Date endDate) {
		final DataPolicy dataPolicy = new DataPolicyImpl();
		dataPolicy.setGuid(Utils.uniqueCode(code));
		dataPolicy.setPolicyName(DATA_POLICY_NAME);
		dataPolicy.setRetentionPeriodInDays(1);
		dataPolicy.setDescription(DATA_POLICY_DESCRIPTION);
		dataPolicy.setEndDate(endDate);
		dataPolicy.setStartDate(startDate);
		dataPolicy.setState(state);
		dataPolicy.setRetentionType(RetentionType.FROM_CREATION_DATE);
		dataPolicy.setSegments(getSegments());
		dataPolicy.setReferenceKey(DATA_POLICY_REFERENCE_KEY);
		return dataPolicyService.save(dataPolicy);
	}

	DataPolicy createAndSaveDataPolicyWithStateAndSegments(final String code, DataPolicyState state, String... segments) {
		Set<String> policySegments = new HashSet<>(Arrays.asList(segments));
		final DataPolicy dataPolicy = new DataPolicyImpl();
		dataPolicy.setGuid(Utils.uniqueCode(code));
		dataPolicy.setPolicyName(DATA_POLICY_NAME);
		dataPolicy.setRetentionPeriodInDays(1);
		dataPolicy.setDescription(DATA_POLICY_DESCRIPTION);
		dataPolicy.setEndDate(null);
		dataPolicy.setStartDate(new Date());
		dataPolicy.setState(state);
		dataPolicy.setRetentionType(RetentionType.FROM_CREATION_DATE);
		dataPolicy.setSegments(policySegments);
		dataPolicy.setReferenceKey(DATA_POLICY_REFERENCE_KEY);
		return dataPolicyService.save(dataPolicy);
	}

	DataPolicy createAndSaveDataPolicyWithState(final String code, DataPolicyState state) {
		final DataPolicy dataPolicy = new DataPolicyImpl();
		dataPolicy.setGuid(Utils.uniqueCode(code));
		dataPolicy.setPolicyName(DATA_POLICY_NAME);
		dataPolicy.setRetentionPeriodInDays(1);
		dataPolicy.setDescription(DATA_POLICY_DESCRIPTION);
		dataPolicy.setEndDate(new Date());
		dataPolicy.setStartDate(new Date());
		dataPolicy.setState(state);
		dataPolicy.setRetentionType(RetentionType.FROM_CREATION_DATE);
		dataPolicy.setSegments(getSegments());
		dataPolicy.setReferenceKey(DATA_POLICY_REFERENCE_KEY);
		return dataPolicyService.save(dataPolicy);
	}

	CustomerConsent createCustomerConsent(final String customerConsentGuid) {
		final CustomerConsent customerConsent = new CustomerConsentImpl();
		customerConsent.setGuid(customerConsentGuid);
		customerConsent.setDataPolicy(createAndSaveDataPolicy(DATA_POLICY_UNIQUE_CODE));
		customerConsent.setAction(ConsentAction.GRANTED);
		return customerConsent;
	}

	CustomerConsent createCustomerConsent(final String customerConsentGuid, final DataPolicy dataPolicy, final ConsentAction consentAction) {
		final CustomerConsent customerConsent = new CustomerConsentImpl();
		customerConsent.setGuid(Utils.uniqueCode(customerConsentGuid));
		customerConsent.setDataPolicy(dataPolicy);
		customerConsent.setAction(consentAction);
		customerConsent.setConsentDate(new Date());
		return customerConsent;
	}

	protected CustomerConsent createAndSaveCustomerConsent(final String storeCode, final String customerConsentGuid, final String email) {
		return createAndSaveCustomerConsent(storeCode, customerConsentGuid, email, ConsentAction.GRANTED);
	}

	protected CustomerConsent createAndSaveCustomerConsent(final String storeCode,
														   final String customerConsentGuid,
														   final String email,
														   final ConsentAction consentAction) {
		final Customer anonymousCustomer = createPersistedCustomer(storeCode, email, true);
		final CustomerConsent customerConsent = createCustomerConsent(customerConsentGuid);
		customerConsent.setCustomerGuid(anonymousCustomer.getGuid());
		customerConsent.setConsentDate(new Date());
		customerConsent.setAction(consentAction);
		return customerConsentService.save(customerConsent);
	}

	protected CustomerConsent createAndSaveCustomerConsent(final String customerConsentGuid, final Customer customer) {
		final CustomerConsent customerConsent = createCustomerConsent(customerConsentGuid);
		customerConsent.setCustomerGuid(customer.getGuid());
		customerConsent.setConsentDate(new Date());
		return customerConsentService.save(customerConsent);
	}

	protected CustomerConsent createAndSaveCustomerConsent(final String customerConsentGuid,
														   final Customer customer,
														   final DataPolicy dataPolicy,
														   final Date consentDate) {
		final CustomerConsent customerConsent = createCustomerConsent(customerConsentGuid, dataPolicy, ConsentAction.GRANTED);
		customerConsent.setCustomerGuid(customer.getGuid());
		customerConsent.setConsentDate(consentDate);
		return customerConsentService.save(customerConsent);
	}

	protected CustomerConsent createAndSaveCustomerConsent(final String customerConsentGuid,
														   final Customer customer,
														   final DataPolicy dataPolicy,
														   final ConsentAction consentAction,
														   final Date consentDate) {
		final CustomerConsent customerConsent = createCustomerConsent(customerConsentGuid, dataPolicy, consentAction);
		customerConsent.setCustomerGuid(customer.getGuid());
		customerConsent.setConsentDate(consentDate);
		return customerConsentService.save(customerConsent);
	}

	protected Customer createPersistedCustomer(final String storeCode, final String email, final boolean anonymous) {
		final Customer customer = beanFactory.getBean(ContextIdNames.CUSTOMER);
		customer.setAnonymous(anonymous);
		customer.setEmail(email);
		customer.setStoreCode(storeCode);
		return customerService.add(customer);
	}

	private Set<String> getSegments() {
		final Set<String> segments = new HashSet<>();
		segments.add(CA);
		segments.add(EU);
		return segments;
	}

	protected CustomerSession createCustomerSession(final Customer customer, final Catalog catalog) {
		final Shopper shopper = TestShopperFactoryForTestApplication.getInstance().createNewShopperWithMemento();
		shopper.setCustomer(customer);
		shopperService.save(shopper);

		final CustomerSession customerSession = TestCustomerSessionFactoryForTestApplication.getInstance().createNewCustomerSessionWithContext(
				shopper);

		final PriceListHelperService priceListHelperService = getBeanFactory().getBean(ContextIdNames.PRICE_LIST_HELPER_SERVICE);
		final Currency currency = priceListHelperService.getDefaultCurrencyFor(catalog);
		customerSession.setCurrency(currency);
		return customerSession;
	}

	protected void setupEnableDataPoliciesSettingValue(final String storeCode, final Boolean enabled) {
		SettingsTestPersister settingsTestPersister = getTac().getPersistersFactory().getSettingsTestPersister();

		settingsTestPersister.updateSettingValue(DataPolicyService.COMMERCE_STORE_ENABLE_DATA_POLICIES, storeCode, enabled.toString());
	}
}
