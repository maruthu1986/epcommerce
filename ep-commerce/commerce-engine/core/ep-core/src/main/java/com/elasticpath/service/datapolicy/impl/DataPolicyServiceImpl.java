/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.datapolicy.impl;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.exception.DuplicateKeyException;
import com.elasticpath.domain.datapolicy.DataPolicy;
import com.elasticpath.domain.datapolicy.DataPolicyState;
import com.elasticpath.service.datapolicy.DataPolicyService;
import com.elasticpath.service.impl.AbstractEpPersistenceServiceImpl;
import com.elasticpath.settings.SettingsReader;
import com.elasticpath.settings.domain.SettingValue;

/**
 * Perform CRUD operations with DataPolicy entity.
 */
public class DataPolicyServiceImpl extends AbstractEpPersistenceServiceImpl implements DataPolicyService {

	private SettingsReader settingsReader;

	@Override
	public Boolean areEnabledByStore(final String store) {
		return areDataPoliciesEnabledForTheStore(store);
	}

	@Override
	public DataPolicy save(final DataPolicy dataPolicy) throws DuplicateKeyException {
		getPersistenceEngine().save(dataPolicy);
		return dataPolicy;
	}

	@Override
	public DataPolicy update(final DataPolicy dataPolicy) throws DuplicateKeyException {
		return getPersistenceEngine().update(dataPolicy);
	}

	@Override
	public void remove(final DataPolicy dataPolicy) throws EpServiceException {
		getPersistenceEngine().delete(dataPolicy);
	}

	@Override
	public List<DataPolicy> list() throws EpServiceException {
		return getPersistenceEngine().retrieveByNamedQuery("DATAPOLICY_SELECT_ALL");
	}

	@Override
	public List<DataPolicy> findByStates(final DataPolicyState state1, final DataPolicyState state2) {
		return getPersistenceEngine().retrieveByNamedQuery("DATAPOLICY_FIND_BY_STATE", state1, state2);
	}

	@Override
	public DataPolicy load(final long dataPolicyUid) throws EpServiceException {
		DataPolicy dataPolicy;
		if (dataPolicyUid <= 0) {
			dataPolicy = getBean(ContextIdNames.DATA_POLICY);
		} else {
			dataPolicy = getPersistentBeanFinder().load(ContextIdNames.DATA_POLICY, dataPolicyUid);
		}
		return dataPolicy;
	}

	@Override
	public DataPolicy get(final long dataPolicyUid) throws EpServiceException {
		DataPolicy dataPolicy;
		if (dataPolicyUid <= 0) {
			dataPolicy = getBean(ContextIdNames.DATA_POLICY);
		} else {
			dataPolicy = getPersistentBeanFinder().get(ContextIdNames.DATA_POLICY, dataPolicyUid);
		}
		return dataPolicy;
	}

	@Override
	public DataPolicy findByGuid(final String guid) throws EpServiceException {
		final List<DataPolicy> dataPolicies = getPersistenceEngine().retrieveByNamedQuery("DATAPOLICY_FIND_BY_GUID", guid);
		if (dataPolicies.isEmpty()) {
			return null;
		}
		if (dataPolicies.size() > 1) {
			throw new EpServiceException("Inconsistent data -- duplicate guid:" + guid);
		}
		return dataPolicies.get(0);
	}

	@Override
	public DataPolicy findActiveByGuid(final String guid) throws EpServiceException {
		final List<DataPolicy> dataPolicies = getPersistenceEngine().retrieveByNamedQuery("DATAPOLICY_FIND_ACTIVE_BY_GUID",
				guid, DataPolicyState.ACTIVE);
		if (dataPolicies.isEmpty()) {
			return null;
		}
		if (dataPolicies.size() > 1) {
			throw new EpServiceException("Inconsistent data -- duplicate guid:" + guid);
		}
		return dataPolicies.get(0);
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
	public List<DataPolicy> findByGuids(final List<String> guids) throws EpServiceException {
		final List<DataPolicy> dataPolicies = getPersistenceEngine().retrieveByNamedQueryWithList("DATAPOLICY_FIND_BY_GUIDS", "list", guids);
		if (dataPolicies.isEmpty()) {
			return null;
		}
		return dataPolicies;
	}

	@Override
	public List<DataPolicy> findActiveDataPoliciesForSegmentsAndStore(final List<String> segmentCodes, final String store)
			throws EpServiceException {
		if (areDataPoliciesEnabledForTheStore(store)) {
			List<DataPolicy> activePolicies = getPersistenceEngine().retrieveByNamedQuery("DATAPOLICY_FIND_ACTIVE", DataPolicyState.ACTIVE);
			return activePolicies.stream()
					.filter(dataPolicy -> CollectionUtils.containsAny(dataPolicy.getSegments().stream()
							.map(String::toLowerCase)
							.collect(Collectors.toSet()), streamStringCollectionToLowerCase(segmentCodes)))
					.collect(Collectors.toList());
		}
		return Collections.emptyList();
	}

	@Override
	public boolean areDataPoliciesEnabledForTheStore(final String store) {
		SettingValue settingValue = settingsReader.getSettingValue(COMMERCE_STORE_ENABLE_DATA_POLICIES, store);

		return settingValue != null && settingValue.getBooleanValue();
	}

	public void setSettingsReader(final SettingsReader settingsReader) {
		this.settingsReader = settingsReader;
	}

	private List<String> streamStringCollectionToLowerCase(final List<String> strings) {
		strings.replaceAll(string -> string.toLowerCase(Locale.getDefault()));
		return strings;
	}
}
