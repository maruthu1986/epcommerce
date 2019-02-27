/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.service.customer;

import java.util.Date;
import java.util.List;

/**
 * Methods that clean up {@link CustomerSession)s (stale or otherwise). They normally do not need to create an
 * instance of {@link CustomerSession).
 */
public interface CustomerSessionCleanupService {
	
	/**
	 * Checks to see if a specific persisted customerSessionGuid exists.
	 *
	 * @param customerSessionGuid customerSessionGuid to check.
	 * @return true if found, false otherwise.
	 */
	boolean checkPersistedCustomerSessionGuidExists(String customerSessionGuid);
	


	/**
	 * Delete customer sessions and associated carts for any sessions with empty carts
	 * that were last accessed before the given date.
	 * 
	 * @param guids list of sessions to delete
	 * @return the number of sessions deleted
	 */
	int deleteSessions(List<String> guids);

	/**
	 * Retrieves guids for all anonymous customer sessions that have not been accessed since the provided date.
	 *
	 * @param beforeDate the date that the session must have been accessed since.
	 * @param maxResults the maximum number of results to return
	 * @return the old anonymous session guids
	 */
	List<String> getOldCustomerSessionGuids(Date beforeDate, int maxResults);

	/**
	 * Deletes customer session that have the shopper.
	 * @param shopperUids the list of shopper uidPks 
	 * @return the number of sessions deleted.
	 */
	int deleteByShopperUids(List<Long> shopperUids);
	
	
}