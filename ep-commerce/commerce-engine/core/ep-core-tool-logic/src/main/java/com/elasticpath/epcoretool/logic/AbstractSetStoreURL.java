/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.epcoretool.logic;

import org.apache.commons.validator.UrlValidator;

import com.elasticpath.domain.store.Store;
import com.elasticpath.service.store.StoreService;

/**
 * Class to update the Store URL.
 */
public abstract class AbstractSetStoreURL extends AbstractEpCore {
	//Validator with default url schemes http, https, ftp
	private final UrlValidator urlValidator = new UrlValidator();

	/**
	 * Instantiates a new abstract ep core.
	 *
	 * @param jdbcUrl                   the jdbc url
	 * @param jdbcUsername              the jdbc username
	 * @param jdbcPassword              the jdbc password
	 * @param jdbcDriverClass           the jdbc driver class
	 * @param jdbcConnectionPoolMinIdle the jdbc connection pool min idle
	 * @param jdbcConnectionPoolMaxIdle the jdbc connection pool max idle
	 */
	public AbstractSetStoreURL(final String jdbcUrl, final String jdbcUsername, final String jdbcPassword, final String jdbcDriverClass,
			final Integer jdbcConnectionPoolMinIdle, final Integer jdbcConnectionPoolMaxIdle) {
		super(jdbcUrl, jdbcUsername, jdbcPassword, jdbcDriverClass, jdbcConnectionPoolMinIdle, jdbcConnectionPoolMaxIdle);
	}

	/**
	 * Apply new store code url.
	 *
	 * @param storeCode the store code
	 * @param storeURL the new store url
	 */
	public void execute(final String storeCode, final String storeURL) {
		StoreService storeService = epCore().getStoreService();
		Store store = storeService.findStoreWithCode(storeCode);

		if (store == null) {
			throw new IllegalArgumentException("Unable to find store " + storeCode);
		} else if (!urlValidator.isValid(storeURL)) {
			throw new IllegalArgumentException("Not a valid url " + storeURL);
		}

		store.setUrl(storeURL);
		storeService.saveOrUpdate(store);
	}
}
