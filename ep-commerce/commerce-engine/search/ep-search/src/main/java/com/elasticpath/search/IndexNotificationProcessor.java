/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.search;

import java.util.List;

import com.elasticpath.domain.search.IndexNotification;
import com.elasticpath.service.search.IndexType;

/**
 * Helper service which handles retrieval and removal of {@link IndexNotification}s.
 */
public interface IndexNotificationProcessor {

	/**
	 * Finds all new notifications to process. Notifications are collapsed so that only the
	 * necessary notifications are returned. Use {@link #getRawNotifications()} to get the list of
	 * pre-processed {@link IndexNotification}s.
	 *
	 * @param indexType the type of notifications to retrieve
	 * @return new notifications to process
	 * @see #getRawNotifications()
	 */
	List<IndexNotification> findAllNewNotifications(IndexType indexType);

	/**
	 * Retrieves the list of {@link IndexNotification}s. Notifications are collapsed so that
	 * only the necessary notifications are returned. This list is a cached version of what is
	 * stored.
	 *
	 * @return the list of {@link IndexNotification}s
	 * @see #findAllNewNotifications(IndexType)
	 */
	List<IndexNotification> getNotifications();

	/**
	 * Retrieves the list of raw {@link IndexNotification}s. These notifications have not been
	 * pre-processed in any way. These notifications are a cached version of what is stored.
	 *
	 * @return the list of raw {@link IndexNotification}s
	 * @see #findAllNewRawNotifications(IndexType)
	 */
	List<IndexNotification> getRawNotifications();

	/**
	 * Retrieves the list of raw {@link IndexNotification}s. These notifications have not been
	 * pre-processed in any way. This list is freshly retrieved.
	 *
	 * @param indexType the type of notifications to retrieve
	 * @return the list of raw {@link IndexNotification}s
	 */
	List<IndexNotification> findAllNewRawNotifications(IndexType indexType);

	/**
	 * Removes all of the stored notifications.
	 */
	void removeStoredNotifications();

}