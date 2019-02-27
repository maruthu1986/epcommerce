/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.shipping;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.domain.shipping.ShippingCostCalculationMethod;
import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.EpPersistenceService;
import com.elasticpath.service.search.query.ShippingServiceLevelSearchCriteria;
import com.elasticpath.shipping.connectivity.dto.ShippingAddress;

/**
 * Provide ShippingServiceLevel-related business service.
 */
public interface ShippingServiceLevelService extends EpPersistenceService {
	/**
	 * Adds the given shippingServiceLevel.
	 *
	 * @param shippingServiceLevel the shippingServiceLevel to add
	 * @return the persisted instance of shippingServiceLevel
	 * @throws EpServiceException - in case of any errors
	 */
	ShippingServiceLevel add(ShippingServiceLevel shippingServiceLevel) throws EpServiceException;

	/**
	 * Updates the given shippingServiceLevel.
	 *
	 * @param shippingServiceLevel the shippingServiceLevel to update
	 * @return ShippingServiceLevel the updated ShippingServiceLevel
	 * @throws EpServiceException - in case of any errors
	 */
	ShippingServiceLevel update(ShippingServiceLevel shippingServiceLevel) throws EpServiceException;

	/**
	 * Delete the shippingServiceLevel.
	 *
	 * @param shippingServiceLevel the shippingServiceLevel to remove
	 * @throws EpServiceException - in case of any errors
	 */
	void remove(ShippingServiceLevel shippingServiceLevel) throws EpServiceException;

	/**
	 * Lists all shippingServiceLevels stored in the database for the specific store.
	 *
	 * @param storeCode the store code
	 * @return a list of shippingServiceLevels
	 * @throws EpServiceException - in case of any errors
	 */
	List<ShippingServiceLevel> findByStore(String storeCode) throws EpServiceException;

	/**
	 * List all shippingServiceLevels stored in the database for the specified store with storeCode and specific state.
	 *
	 * @param storeCode the store code
	 * @param active should only active service levels be retrieved or not
	 * @return a list of shippingServiceLevels
	 * @throws EpServiceException - in case of any errors
	 */
	List<ShippingServiceLevel> findByStoreAndState(String storeCode, boolean active) throws EpServiceException;

	/**
	 * List the uidpks of all shippingServiceLevels stored in the database for the specified store with storeCode and specific state.
	 *
	 * @param storeCode the store code
	 * @param active should only active service levels be retrieved or not
	 * @return a list of uidpks
	 * @throws EpServiceException - in case of any errors
	 */
	List<Long> findUidsByStoreAndState(String storeCode, boolean active) throws EpServiceException;

	/**
	 * Load the shippingServiceLevel with the given UID. Throw an unrecoverable exception if there is no matching database row.
	 *
	 * @param shippingServiceLevelUid the shippingServiceLevel UID
	 * @return the shippingServiceLevel if UID exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	ShippingServiceLevel load(long shippingServiceLevelUid) throws EpServiceException;

	/**
	 * Get the shippingServiceLevel with the given UID. Return null if no matching record exists.
	 *
	 * @param shippingServiceLevelUid the shippingServiceLevel UID
	 * @return the shippingServiceLevel if UID exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	ShippingServiceLevel get(long shippingServiceLevelUid) throws EpServiceException;

	/**
	 * Get all the available shipping cost calculation methods configured in the system.
	 *
	 * @return the list available shipping cost calculation methods.
	 */
	List<ShippingCostCalculationMethod> getAllShippingCostCalculationMethods();

	/**
	 * Set all the available shipping cost calculation methods configured in the system.
	 *
	 * @param allShippingCostCalculationMethods - the available shipping cost calculation methods.
	 */
	void setAllShippingCostCalculationMethods(List<String> allShippingCostCalculationMethods);

	/**
	 * Get the list of uids of <code>ShippingRegion</code> used by existing <code>ShippingServiceLevel</code>s.
	 *
	 * @return the list of uids of <code>ShippingRegion</code>s in use.
	 */
	List<Long> getShippingRegionInUseUidList();

	/**
	 * Gets a Map of CountryCode -> (Map of SubCountryName to SubCountryCode, Sorted by Name) where the countryCode has a ShippingServiceLevel
	 * configured at the country level OR the subCountry level.
	 *
	 * @param locale the locale into which the subCountry codes must be translated
	 * @return Map of CountryCode -> Sorted Map(SubCountryName -> SubCountryCode)
	 */
	Map<String, Map<String, String>> getCountrySubCountryMapWithShippingService(Locale locale);

	/**
	 * Check if shipping service level is in use.
	 *
	 * @param code the shipping service level code
	 * @return true if shipping service level is in use, false otherwise
	 */
	boolean isShippingServiceLevelInUse(String code);

	/**
	 * Get a Sorted Map of CountryName -> CountryCode, where the countries in the map have Shipping service, and the names are appropriate for the
	 * given locale.
	 *
	 * @param locale the locale with which to fetch country names
	 * @param store the store to get shipping service levels for
	 * @return sorted Map of country names to country codes.
	 */
	SortedMap<String, String> getSortedCountriesWithShippingAllowed(Locale locale, Store store);

	/**
	 * Find {@link ShippingServiceLevel}s by search criteria, start and maxResults.
	 *
	 * @param searchCriteria filtering parameters. Set parameter to null to ignore it.
	 * @param start start.
	 * @param maxResults maxResults.
	 * @return list of Shipping Service Levels filtered according to searchCriteria.
	 */
	List<ShippingServiceLevel> findByCriteria(ShippingServiceLevelSearchCriteria searchCriteria, int start, int maxResults);

	/**
	 * Find count of {@link ShippingServiceLevel}s by search criteria.
	 *
	 * @param searchCriteria filtering parameters. Set parameter to null to ignore it.
	 * @return count of Shipping Service Levels filtered according to searchCriteria.
	 */
	Long findCountByCriteria(ShippingServiceLevelSearchCriteria searchCriteria);
	/**
	 * Find the shipping service level with the given code.
	 *
	 * @param code the shipping service level code.
	 * @return the shipping service level that matches the given code, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	ShippingServiceLevel findByCode(String code) throws EpServiceException;

	/**
	 * Find the shipping service level with the given guid.
	 *
	 * @param guid the shipping service level guid.
	 * @return the shipping service level that matches the given guid, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	ShippingServiceLevel findByGuid(String guid) throws EpServiceException;

	/**
	 * Retrieve the list of valid <code>ShippingServiceLevel</code> based on the region info inside the given <code>Address</code>.
	 *
	 * @param storeCode the store that the shippingServiceLevels are valid in
	 * @param address -- the address to be used to retrieve shippingServiceLevel info.
	 * @return he list of valid <code>ShippingServiceLevel</code> for the given orderAddress.
	 */
	List<ShippingServiceLevel> retrieveShippingServiceLevel(String storeCode, ShippingAddress address);

	/**
	 * Checks the given code exists or not.
	 *
	 * @param code the code of shipping service level
	 * @return true if the given code exists
	 * @throws EpServiceException - in case of any errors
	 */
	boolean codeExists(String code) throws EpServiceException;

	/**
	 * Retrieves list of shipping service level UIDs where the deleted date is later than the specified date.
	 *
	 * @param date date to compare with the deleted date
	 * @return list of shipping service level UIDs whose deleted date is later than the specified date
	 */
	List<Long> findUidsByDeletedDate(Date date);

	/**
	 * Retrieves a list of <code>ShippingServiceLevel</code> UIDs where
	 * the last modified date is later than the specified date.
	 *
	 * @param date date to compare with the last modified date
	 * @return list of <code>Long</code> UIDs whose last modified date is later than the date specified
	 */
	List<Long> findUidsByModifiedDate(Date date);

	/**
	 * @return all <code>ShippingServiceLevel</code> UIDs
	 */
	List<Long> findAllUids();

	/**
	 * Retrieves a list of shipping service levels by the list of UIDs.
	 *
	 * @param shippingServiceLevelsUids UIDs of shipping service levels to find
	 * @return a list of persistent shipping service level UIDs
	 */
	List<ShippingServiceLevel> findByUids(Collection<Long> shippingServiceLevelsUids);

	/**
	 * Retrieves all shipping service levels.
	 * @return a list of shipping service levels.
	 */
	List<ShippingServiceLevel> findAll();
}
