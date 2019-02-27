/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.datapolicy.impl;

import java.util.List;
import java.util.Set;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.exception.DuplicateKeyException;
import com.elasticpath.domain.datapolicy.ConsentAction;
import com.elasticpath.domain.datapolicy.CustomerConsent;
import com.elasticpath.domain.datapolicy.DataPolicy;
import com.elasticpath.service.datapolicy.CustomerConsentService;
import com.elasticpath.service.impl.AbstractEpPersistenceServiceImpl;

/**
 * Perform CRUD operations with CustomerConsent entity.
 */
public class CustomerConsentServiceImpl extends AbstractEpPersistenceServiceImpl implements CustomerConsentService {

	@Override
	public CustomerConsent save(final CustomerConsent customerConsent) throws DuplicateKeyException {
		getPersistenceEngine().save(customerConsent);
		return customerConsent;
	}

	@Override
	public void updateCustomerGuids(final List<Long> customerConsentUids, final String customerGuid) throws DuplicateKeyException {
		getPersistenceEngine().executeNamedQueryWithList("CUSTOMERCONSENT_UPDATE_CUSTOMER_GUID_BY_UIDS", "list",
				customerConsentUids, customerGuid);
	}

	@Override
	public List<CustomerConsent> list() throws EpServiceException {
		return getPersistenceEngine().retrieveByNamedQuery("CUSTOMERCONSENT_SELECT_ALL");
	}

	@Override
	public CustomerConsent load(final long customerConsentUid) throws EpServiceException {
		CustomerConsent customerConsent;
		if (customerConsentUid <= 0) {
			customerConsent = getBean(ContextIdNames.CUSTOMER_CONSENT);
		} else {
			customerConsent = getPersistentBeanFinder().load(ContextIdNames.CUSTOMER_CONSENT, customerConsentUid);
		}
		return customerConsent;
	}

	@Override
	public CustomerConsent get(final long customerConsentUid) throws EpServiceException {
		CustomerConsent customerConsent;
		if (customerConsentUid <= 0) {
			customerConsent = getBean(ContextIdNames.CUSTOMER_CONSENT);
		} else {
			customerConsent = getPersistentBeanFinder().get(ContextIdNames.CUSTOMER_CONSENT, customerConsentUid);
		}
		return customerConsent;
	}

	@Override
	public CustomerConsent findByGuid(final String guid) throws EpServiceException {
		final List<CustomerConsent> customerConsents =
				getPersistenceEngine().retrieveByNamedQuery("CUSTOMERCONSENT_FIND_BY_GUID", guid);
		if (customerConsents.isEmpty()) {
			return null;
		}
		if (customerConsents.size() > 1) {
			throw new EpServiceException("Inconsistent data -- duplicate guid:" + guid);
		}
		return customerConsents.get(0);
	}

	@Override
	public List<CustomerConsent> findByGuids(final List<String> guids) throws EpServiceException {
		final List<CustomerConsent> customerConsents =
				getPersistenceEngine().retrieveByNamedQueryWithList("CUSTOMERCONSENT_FIND_BY_GUIDS", "list", guids);
		if (customerConsents.isEmpty()) {
			return null;
		}
		return customerConsents;
	}

	@Override
	public List<CustomerConsent> findByCustomerGuid(final String customerGuid) throws EpServiceException {
		final List<CustomerConsent> customerConsents =
				getPersistenceEngine().retrieveByNamedQuery("CUSTOMERCONSENT_FIND_BY_CUSTOMER_GUID", customerGuid);
		if (customerConsents.isEmpty()) {
			return null;
		}
		return customerConsents;
	}

	@Override
	public CustomerConsent findByDataPolicyGuidForCustomerLatest(final String dataPolicyGuid, final String customerGuid) {
		final List<CustomerConsent> customerConsents =
				getPersistenceEngine().retrieveByNamedQuery("CUSTOMER_CONSENT_FIND_BY_CUSTOMER_AND_DATA_POLICY_GUID_LATEST",
						customerGuid, dataPolicyGuid);
		if (customerConsents.isEmpty()) {
			return null;
		}
		return customerConsents.get(0);
	}

	/**
	 * Generic load method for all persistable domain models.
	 *
	 * @param uid the persisted instance uid
	 * @return the persisted instance if exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	@Override
	public Object getObject(final long uid) throws EpServiceException {
		return get(uid);
	}

	@Override
	public List<CustomerConsent> findWithActiveDataPoliciesByCustomerGuid(final String customerGuid, final boolean includeDisabledPolicies)
			throws EpServiceException {
		if (includeDisabledPolicies) {
			return getPersistenceEngine()
					.retrieveByNamedQuery("CUSTOMER_CONSENTS_FIND_LATEST_ACTIVE_OR_DISABLED_BY_CUSTOMER", customerGuid);
		}

		return getPersistenceEngine()
				.retrieveByNamedQuery("CUSTOMER_CONSENTS_FIND_LATEST_ACTIVE_BY_CUSTOMER", customerGuid);
	}

	@Override
	public Boolean customerHasGivenConsentForAtLeastOneDataPolicy(final String customerGuid, final Set<DataPolicy> policies) {

		List<CustomerConsent> acceptedConsents = getPersistenceEngine()
				.retrieveByNamedQuery("CUSTOMER_CONSENTS_FIND_LATEST_BY_CUSTOMER", customerGuid, ConsentAction.GRANTED);
		return acceptedConsents.stream()
				.anyMatch(customerConsent -> policies.contains(customerConsent.getDataPolicy()));
	}
}
