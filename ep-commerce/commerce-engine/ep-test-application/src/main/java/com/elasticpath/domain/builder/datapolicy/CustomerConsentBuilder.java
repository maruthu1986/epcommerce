/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.domain.builder.datapolicy;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.builder.DomainObjectBuilder;
import com.elasticpath.domain.datapolicy.ConsentAction;
import com.elasticpath.domain.datapolicy.CustomerConsent;
import com.elasticpath.domain.datapolicy.DataPolicy;

/**
 * A builder that builds {@link CustomerConsent}s for testing purposes.
 */
public class CustomerConsentBuilder implements DomainObjectBuilder<CustomerConsent> {

	@Autowired
	private BeanFactory beanFactory;

	private Long uidpk = 0L;
	private String guid;
	private DataPolicy dataPolicy;
	private ConsentAction action;
	private Date consentDate;
	private String customerGuid;

	/**
	 * Create a new instance.
	 * @return the builder
	 */
	public CustomerConsentBuilder newInstance() {
		final CustomerConsentBuilder newBuilder = new CustomerConsentBuilder();
		newBuilder.setBeanFactory(beanFactory);

		return newBuilder;
	}

	/**
	 * Sets the uidpk for the Customer Consent.
	 *
	 * @param uidpk the uidpk
	 * @return the builder
	 */
	public CustomerConsentBuilder withUidpk(final Long uidpk) {
		this.uidpk = uidpk;
		return this;
	}

	/**
	 * Sets the guid for the Customer Consent.
	 *
	 * @param guid the guid
	 * @return the builder
	 */
	public CustomerConsentBuilder withGuid(final String guid) {
		this.guid = guid;
		return this;
	}

	/**
	 * Sets the data policy for the Customer Consent.
	 *
	 * @param dataPolicy the data policy
	 * @return the builder
	 */
	public CustomerConsentBuilder withDataPolicy(final DataPolicy dataPolicy) {
		this.dataPolicy = dataPolicy;
		return this;
	}

	/**
	 * Sets the consent action for the Customer Consent.
	 *
	 * @param action the consent action
	 * @return the builder
	 */
	public CustomerConsentBuilder withAction(final ConsentAction action) {
		this.action = action;
		return this;
	}

	/**
	 * Sets the consent date for the Customer Consent.
	 *
	 * @param consentDate the consent date
	 * @return the builder
	 */
	public CustomerConsentBuilder withConsentDate(final Date consentDate) {
		this.consentDate = consentDate;
		return this;
	}

	/**
	 * Sets the customer guid for the Customer Consent.
	 *
	 * @param customerGuid the customer guid
	 * @return the builder
	 */
	public CustomerConsentBuilder withCustomerGuid(final String customerGuid) {
		this.customerGuid = customerGuid;
		return this;
	}

	@Override
	public CustomerConsent build() {
		CustomerConsent customerConsent = beanFactory.getBean(ContextIdNames.CUSTOMER_CONSENT);
		customerConsent.setUidPk(uidpk);
		customerConsent.setGuid(guid);
		customerConsent.setDataPolicy(dataPolicy);
		customerConsent.setAction(action);
		customerConsent.setConsentDate(consentDate);
		customerConsent.setCustomerGuid(customerGuid);
		return customerConsent;
	}

	/**
	 * Sets the bean factory for the Data Policy.
	 *
	 * @param beanFactory the bean factory
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
}
