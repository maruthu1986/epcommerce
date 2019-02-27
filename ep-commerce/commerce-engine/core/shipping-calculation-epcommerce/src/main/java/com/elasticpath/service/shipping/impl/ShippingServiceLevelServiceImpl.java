/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */

package com.elasticpath.service.shipping.impl;

import static com.elasticpath.commons.constants.EpShippingContextIdNames.SHIPPING_SERVICE_LEVEL;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;
import org.apache.commons.lang3.StringUtils;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.domain.misc.Geography;
import com.elasticpath.domain.rules.RuleParameter;
import com.elasticpath.domain.shipping.Region;
import com.elasticpath.domain.shipping.ShippingCostCalculationMethod;
import com.elasticpath.domain.shipping.ShippingRegion;
import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.impl.AbstractEpPersistenceServiceImpl;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.rules.RuleParameterService;
import com.elasticpath.service.search.query.ShippingServiceLevelSearchCriteria;
import com.elasticpath.service.search.query.SortOrder;
import com.elasticpath.service.search.query.StandardSortBy;
import com.elasticpath.service.shipping.ShippingServiceLevelService;
import com.elasticpath.shipping.connectivity.dto.ShippingAddress;

/**
 * Provide shipping service level-related business service.
 */
@SuppressWarnings("PMD.GodClass")
public class ShippingServiceLevelServiceImpl extends AbstractEpPersistenceServiceImpl implements ShippingServiceLevelService {

	private final List<ShippingCostCalculationMethod> allShippingCostCalculationMethods = new ArrayList<>();

	private RuleParameterService ruleParameterService;

	private TimeService timeService;

	private Geography geography;

	/**
	 * Adds the given shippingServiceLevel.
	 *
	 * @param shippingServiceLevel the shippingServiceLevel to add
	 * @return the persisted instance of shippingServiceLevel
	 * @throws EpServiceException - in case of any errors
	 */
	@Override
	public ShippingServiceLevel add(final ShippingServiceLevel shippingServiceLevel) throws EpServiceException {
		sanityCheck();
		getPersistenceEngine().save(shippingServiceLevel);
		return shippingServiceLevel;
	}

	/**
	 * Updates the given shippingServiceLevel.
	 *
	 * @param shippingServiceLevel the shippingServiceLevel to update
	 * @return ShippingServiceLevel the updated ShippingServiceLevel
	 * @throws EpServiceException - in case of any errors
	 */
	@Override
	public ShippingServiceLevel update(final ShippingServiceLevel shippingServiceLevel) throws EpServiceException {
		sanityCheck();
		shippingServiceLevel.setLastModifiedDate(timeService.getCurrentTime());
		return getPersistenceEngine().merge(shippingServiceLevel);
	}

	/**
	 * Delete the shippingServiceLevel.
	 *
	 * @param shippingServiceLevel the shippingServiceLevel to remove
	 * @throws EpServiceException - in case of any errors
	 */
	@Override
	public void remove(final ShippingServiceLevel shippingServiceLevel) throws EpServiceException {
		sanityCheck();
		getPersistenceEngine().delete(shippingServiceLevel);
	}

	/**
	 * List all shippingServiceLevels stored in the database for the specified store with storeCode.
	 *
	 * @param storeCode the store code
	 * @return a list of shippingServiceLevels
	 * @throws EpServiceException - in case of any errors
	 */
	@Override
	public List<ShippingServiceLevel> findByStore(final String storeCode) throws EpServiceException {
		sanityCheck();

		return getPersistenceEngine().retrieveByNamedQuery("SHIPPINGSERVICELEVEL_SELECT_BY_STORE", storeCode);
	}

	/**
	 * List all shippingServiceLevels stored in the database for the specified store with storeCode.
	 *
	 * @param storeCode the store code
	 * @param active should only active service levels be retrieved or not
	 * @return a list of shippingServiceLevels
	 * @throws EpServiceException - in case of any errors
	 */
	@Override
	public List<ShippingServiceLevel> findByStoreAndState(final String storeCode, final boolean active) throws EpServiceException {
		sanityCheck();

		return getPersistenceEngine().retrieveByNamedQuery("SHIPPINGSERVICELEVEL_SELECT_BY_STORE_AND_STATE", storeCode, active);
	}

	@Override
	public List<Long> findUidsByStoreAndState(final String storeCode, final boolean active) throws EpServiceException {
		return getPersistenceEngine().retrieveByNamedQuery("SHIPPINGSERVICELEVEL_UIDS_SELECT_BY_STORE_AND_STATE", storeCode, active);
	}

	/**
	 * Load the shippingServiceLevel with the given UID. Throw an unrecoverable exception if there is no matching database row.
	 *
	 * @param shippingServiceLevelUid the shippingServiceLevel UID
	 * @return the shippingServiceLevel if UID exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	@Override
	public ShippingServiceLevel load(final long shippingServiceLevelUid) throws EpServiceException {
		sanityCheck();
		ShippingServiceLevel shippingServiceLevel = null;
		if (shippingServiceLevelUid <= 0) {
			shippingServiceLevel = getBean(SHIPPING_SERVICE_LEVEL);
		} else {
			shippingServiceLevel = getPersistentBeanFinder().load(SHIPPING_SERVICE_LEVEL, shippingServiceLevelUid);
		}
		return shippingServiceLevel;
	}

	/**
	 * Get the shippingServiceLevel with the given UID. Return null if no matching record exists.
	 *
	 * @param shippingServiceLevelUid the shippingServiceLevel UID
	 * @return the shippingServiceLevel if UID exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	@Override
	public ShippingServiceLevel get(final long shippingServiceLevelUid) throws EpServiceException {
		sanityCheck();
		ShippingServiceLevel shippingServiceLevel = null;
		if (shippingServiceLevelUid <= 0) {
			shippingServiceLevel = getBean(SHIPPING_SERVICE_LEVEL);
		} else {
			shippingServiceLevel = getPersistentBeanFinder().get(SHIPPING_SERVICE_LEVEL, shippingServiceLevelUid);
		}
		return shippingServiceLevel;
	}

	@Override
	public List<ShippingServiceLevel> findByUids(final Collection<Long> shippingServiceLevelsUids) {
		sanityCheck();

		if (shippingServiceLevelsUids == null || shippingServiceLevelsUids.isEmpty()) {
			return Collections.emptyList();
		}

		return getPersistenceEngine().retrieveByNamedQueryWithList("SHIPPINGSERVICELEVELS_BY_UIDS", "list", shippingServiceLevelsUids);
	}

	@Override
	public List<ShippingServiceLevel> findAll() {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("SHIPPINGSERVICELEVELS_ALL");
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

	/**
	 * Get all the available shipping cost calculation methods configured in the system.
	 *
	 * @return the list available shipping cost calculation methods.
	 */
	@Override
	public List<ShippingCostCalculationMethod> getAllShippingCostCalculationMethods() {
		return this.allShippingCostCalculationMethods;
	}

	/**
	 * Set all the available shipping cost calculation methods configured in the system.
	 *
	 * @param allShippingCostCalculationMethodsName - names of the available shipping cost calculation methods.
	 */
	@Override
	public void setAllShippingCostCalculationMethods(final List<String> allShippingCostCalculationMethodsName) {
		for (final String name : allShippingCostCalculationMethodsName) {
			final ShippingCostCalculationMethod method = getBean(name);
			this.allShippingCostCalculationMethods.add(method);
		}
	}

	/**
	 * Get the list of uids of <code>ShippingRegion</code> used by existing <code>ShippingServiceLevel</code>s.
	 *
	 * @return the list of uids of <code>ShippingRegion</code>s in use.
	 */
	@Override
	public List<Long> getShippingRegionInUseUidList() {
		return getPersistenceEngine().retrieveByNamedQuery("SHIPPINGREGION_UID_IN_USE");
	}

	/**
	 * Get a Sorted Map of CountryName -> CountryCode, where the countries in the map have Shipping service, and the names are appropriate for the
	 * given locale.
	 *
	 * @param locale the locale with which to fetch country names
	 * @param store the store to get shipping service levels for
	 * @return sorted Map of country names to country codes.
	 */
	@Override
	public SortedMap<String, String> getSortedCountriesWithShippingAllowed(final Locale locale, final Store store) {
		List<ShippingRegion> availableShippingRegions = new ArrayList<>();
		List<ShippingServiceLevel> shippingServiceLevels = this.findByStoreAndState(store.getCode(), true);
		for (ShippingServiceLevel shippingServiceLevel : shippingServiceLevels) {
			availableShippingRegions.add(shippingServiceLevel.getShippingRegion());
		}

		Set<String> countryCodes = getCountryCodesForShippingRegions(availableShippingRegions);
		SortedMap<String, String> sortedMap = new TreeMap<>();
		for (String code : countryCodes) {
			sortedMap.put(geography.getCountryDisplayName(code, locale), code);
		}
		return Collections.unmodifiableSortedMap(sortedMap);
	}

	private Set<String> getCountryCodesForShippingRegions(final List<ShippingRegion> shippingRegions) {
		Set<String> countryCodes = new HashSet<>();
		for (ShippingRegion region : shippingRegions) { // for each shipping region
			Set<String> regionStringCodes = region.getRegionMap().keySet(); // get the country codes for the ShippingRegion
			// for each country code in the given shippingRegion
			// Add the code to our set;
			countryCodes.addAll(regionStringCodes);
		}
		return countryCodes;
	}

	/**
	 * Gets a Map of CountryCode -> (Map of SubCountryName to SubCountryCode, Sorted by Name) where the countryCode has a ShippingServiceLevel
	 * configured at the country level OR the subCountry level.
	 *
	 * @param locale the locale into which the subCountry codes must be translated
	 * @return Map of CountryCode -> Sorted Map(SubCountryName -> SubCountryCode)
	 */
	@Override
	public Map<String, Map<String, String>> getCountrySubCountryMapWithShippingService(final Locale locale) {
		List<ShippingRegion> shippingRegionsInUse = getPersistenceEngine().retrieveByNamedQuery("SHIPPINGREGION_IN_USE");
		SetMultimap<String, String> countryCodeToSubcountryCodeMultimap = LinkedHashMultimap.create();
		for (ShippingRegion shippingRegion : shippingRegionsInUse) {

			for (Map.Entry<String, Region> regionEntry : shippingRegion.getRegionMap().entrySet()) {
				final Region region = regionEntry.getValue();
				String countryCode = regionEntry.getKey();
				final List<String> subcountryCodes = region.getSubCountryCodeList();
				countryCodeToSubcountryCodeMultimap.putAll(countryCode, subcountryCodes);
			}
		}
		Map<String, Map<String, String>> codesMap = translateSubCountryCodesToLocalizedSortedMap(countryCodeToSubcountryCodeMultimap, locale);
		return Collections.unmodifiableMap(codesMap);
	}

	/**
	 * Given a map of countryCodes -> List(subCountryCodes), translate the Lists of subCountryCodes into Sorted Maps of subCountry Names specific to
	 * the given locale.
	 *
	 * @param countryCodeMap the map of country codes to subCountryCode Lists
	 * @param locale the locale into which the names should be translated
	 */
	private Map<String, Map<String, String>> translateSubCountryCodesToLocalizedSortedMap(final Multimap<String, String> countryCodeMap,
			final Locale locale) {
		Map<String, Map<String, String>> codesMap = new TreeMap<>();
		for (String code : countryCodeMap.keySet()) {
			codesMap.put(code, getSortedLocalizedMapOfSubCountries(code, countryCodeMap.get(code), locale));
		}
		return codesMap;
	}

	private Map<String, String> getSortedLocalizedMapOfSubCountries(final String countryCode, final Collection<String> subCountryCodes,
			final Locale locale) {
		SortedMap<String, String> sortedMap = new TreeMap<>();
		for (String subCountryCode : subCountryCodes) {
			sortedMap.put(geography.getSubCountryDisplayName(countryCode, subCountryCode, locale), subCountryCode);
		}
		return sortedMap;
	}

	/**
	 * Retrieve the list of valid <code>ShippingServiceLevel</code> based on the region info inside the given <code>Address</code>.
	 *
	 * @param storeCode the store that the shippingServiceLevels are valid in
	 * @param address -- the address to be used to retrieve shippingServiceLevel info.
	 * @return he list of valid <code>ShippingServiceLevel</code> for the given orderAddress.
	 */
	@Override
	public List<ShippingServiceLevel> retrieveShippingServiceLevel(final String storeCode, final ShippingAddress address) {
		if (address == null) {
			return Collections.emptyList();
		}

		List<ShippingServiceLevel> validShippingServiceList = new ArrayList<>();
		List<ShippingServiceLevel> allShippingServiceList = findByStoreAndState(storeCode, true);

		for (ShippingServiceLevel shippingServiceLevel : allShippingServiceList) {
			if (shippingServiceLevel.isApplicable(storeCode, address)) {
				validShippingServiceList.add(shippingServiceLevel);
			}
		}
		return validShippingServiceList;
	}

	/**
	 * Check if shipping service level is in use.
	 *
	 * @param code the shipping service level code
	 * @return true if shipping service level is in use, false otherwise
	 */
	@Override
	public boolean isShippingServiceLevelInUse(final String code) {
		sanityCheck();
		if (getPersistenceEngine().retrieveByNamedQuery("SHIPPINGSERVICELEVEL_CODE_IN_USE").contains(code)) {
			return true;
		}

		List<String> codes = getPersistenceEngine().retrieveByNamedQuery("SHIPPINGSERVICELEVEL_CODE_EXIST", code);
		if (codes.isEmpty()) {
			return false;
		}
		return ruleParameterService.findUniqueParametersWithKey(RuleParameter.SHIPPING_OPTION_CODE_KEY).contains(codes.get(0));
	}

	/**
	 * Set the <code>RuleParameterService</code>.
	 *
	 * @param ruleParameterService the <code>RuleParameterService</code> instance.
	 */
	public void setRuleParameterService(final RuleParameterService ruleParameterService) {
		this.ruleParameterService = ruleParameterService;
	}

	/**
	 * Generates JPA query and fills-in parameters list for searchCriteria.
	 *
	 * @param criteria search criteria.
	 * @param params query parameters (in/out).
	 * @param query initial query.
	 * @return JPA query.
	 */
	private String buildQueryAndParamsForCriteria(final ShippingServiceLevelSearchCriteria criteria, final List<Object> params, final String query) {
		StringBuilder queryBuilder = new StringBuilder(query);
		boolean condition = false;
		int paramNum = 0;

		// check shippingRegionUid filtering parameter
		if (StringUtils.isNotBlank(criteria.getRegionExact())) {
			condition = addRelationClause(queryBuilder, false);
			queryBuilder.append(" ssl.shippingRegion.name = ?").append(++paramNum);
			params.add(criteria.getRegionExact());
		}

		if (criteria.getActiveFlag() != null) {
			condition = addRelationClause(queryBuilder, condition);
			queryBuilder.append(" ssl.enabled = ?").append(++paramNum);
			params.add(criteria.getActiveFlag());
		}

		// check storeUid filtering parameter
		if (StringUtils.isNotBlank(criteria.getStoreExact())) {
			addRelationClause(queryBuilder, condition);
			queryBuilder.append(" ssl.store.name = ?").append(++paramNum);
			params.add(criteria.getStoreExact());
		}
		return queryBuilder.toString();
	}

	private boolean addRelationClause(final StringBuilder queryBuilder, final boolean condition) {
		boolean result = condition;
		if (condition) {
			queryBuilder.append(" AND");
		} else {
			result = true;
			queryBuilder.append(" WHERE");
		}
		return result;
	}


	@Override
	public List<ShippingServiceLevel> findByCriteria(final ShippingServiceLevelSearchCriteria searchCriteria, final int start, final int maxResults) {
		sanityCheck();
		List<Object> params = new ArrayList<>();
		String query = buildQueryAndParamsForCriteria(searchCriteria, params, "SELECT ssl FROM ShippingServiceLevelImpl ssl");
		List<ShippingServiceLevel> levels = getPersistenceEngine().retrieve(query, params.toArray());
		Comparator<ShippingServiceLevel> comparator = null;
		if (StandardSortBy.STORE_NAME.equals(searchCriteria.getSortingType())) {
			comparator = Comparator.comparing(level -> level.getStore().getName());
		} else if (StandardSortBy.REGION.equals(searchCriteria.getSortingType())) {
			comparator = Comparator.comparing(level -> level.getShippingRegion().getName());
		} else if (StandardSortBy.CARRIER.equals(searchCriteria.getSortingType())) {
			comparator = Comparator.comparing(ShippingServiceLevel::getCarrier);
		} else if (StandardSortBy.SERVICE_LEVEL_CODE.equals(searchCriteria.getSortingType())) {
			comparator = Comparator.comparing(ShippingServiceLevel::getCode);
		} else if (StandardSortBy.SERVICE_LEVEL_NAME.equals(searchCriteria.getSortingType())) {
			comparator = Comparator.comparing(level -> level.getDisplayName(level.getStore().getCatalog().getDefaultLocale(), true));
		} else if (StandardSortBy.ACTIVE.equals(searchCriteria.getSortingType())) {
			comparator = Comparator.comparing(ShippingServiceLevel::isEnabled);
		}
		if (comparator != null) {
			levels.sort(SortOrder.DESCENDING.equals(searchCriteria.getSortingOrder()) ? comparator.reversed() : comparator);
		}
		int end = start + maxResults;
		return levels.subList(start, end > levels.size() ? levels.size() : end);
	}

	@Override
	public Long findCountByCriteria(final ShippingServiceLevelSearchCriteria searchCriteria) {
		sanityCheck();
		List<Object> params = new ArrayList<>();
		String query = buildQueryAndParamsForCriteria(searchCriteria, params, "SELECT count(ssl) FROM ShippingServiceLevelImpl ssl");
		return (Long) getPersistenceEngine().retrieve(query, params.toArray()).get(0);
	}

	/**
	 * Find the shipping service level with the given code.
	 *
	 * @param code the shipping service level code.
	 * @return the shipping service level that matches the given code, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	@Override
	public ShippingServiceLevel findByCode(final String code) throws EpServiceException {
		sanityCheck();
		if (code == null) {
			throw new EpServiceException("Cannot retrieve null code.");
		}

		final List<ShippingServiceLevel> results = getPersistenceEngine().retrieveByNamedQuery("SHIPPINGSERVICELEVEL_FIND_BY_CODE", code);
		ShippingServiceLevel shippingServiceLevel = null;
		if (results.size() == 1) {
			shippingServiceLevel = results.get(0);
		} else if (results.size() > 1) {
			throw new EpServiceException("Inconsistent data -- duplicate Shipping Service Level code exist -- " + code);
		}
		return shippingServiceLevel;
	}

	@Override
	public ShippingServiceLevel findByGuid(final String guid) throws EpServiceException {
		sanityCheck();
		if (guid == null) {
			throw new EpServiceException("Cannot retrieve null guid.");
		}

		ShippingServiceLevel shippingServiceLevel = null;

		final List<ShippingServiceLevel> results = getPersistenceEngine().retrieveByNamedQuery("SHIPPINGSERVICELEVEL_FIND_BY_GUID", guid);

		if (!results.isEmpty()) {
			shippingServiceLevel = results.get(0);
		}

		return shippingServiceLevel;
	}

	/**
	 * Checks the given code exists or not.
	 *
	 * @param code the code of shipping service level
	 * @return true if the given code exists
	 * @throws EpServiceException - in case of any errors
	 */
	@Override
	public boolean codeExists(final String code) throws EpServiceException {
		sanityCheck();
		final List<String> codes = getPersistenceEngine().retrieveByNamedQuery("SHIPPINGSERVICELEVEL_CODE_EXIST", code);
		boolean codeExists = false;
		if (!codes.isEmpty()) {
			codeExists = true;
		}
		return codeExists;
	}

	@Override
	public List<Long> findUidsByDeletedDate(final Date date) {
		return Collections.emptyList();
	}

	/**
	 * @param timeService service supplying information about time on the server
	 */
	public void setTimeService(final TimeService timeService) {
		this.timeService = timeService;
	}

	@Override
	public List<Long> findUidsByModifiedDate(final Date date) {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("SHIPPINGSERVICELEVEL_UIDS_SELECT_BY_MODIFIED_DATE", date);
	}

	@Override
	public List<Long> findAllUids() {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("SHIPPINGSERVICELEVEL_UIDS_ALL");
	}

	/**
	 * @param geography The Geography to set.
	 */
	public void setGeography(final Geography geography) {
		this.geography = geography;
	}

	/**
	 * @return The Geography that was set.
	 */
	protected Geography getGeography() {
		return geography;
	}
}
