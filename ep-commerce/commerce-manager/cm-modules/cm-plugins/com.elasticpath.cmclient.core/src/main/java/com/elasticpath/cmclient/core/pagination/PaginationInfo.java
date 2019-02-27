/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.pagination;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.elasticpath.cmclient.core.CmSingletonUtil;
import com.elasticpath.cmclient.core.IPagination;
import com.elasticpath.cmclient.core.util.CookieUtil;

/**
 * A bean holding pagination info per user session.
 */
public final class PaginationInfo implements IPagination {

	private static final List<Integer> VALID_PAGINATIONS = Collections.unmodifiableList(Arrays.asList(10, 20, 50, 100));
	private static final int DEFAULT_PAGING = 10;
	private static final String CM_PAGINATION = "CM_PAGINATION";

	/**
	 * Returns a list of valid pagination settings.
	 * The returned list is unmodifiable.
	 *
	 * @return a list of valid pagination settings
	 */
	public static List<Integer> getValidPaginations() {
		return VALID_PAGINATIONS;
	}

	/**
	 * Get an instance of PaginationInfo.
	 *
	 * @return an instance
	 */
	public static PaginationInfo getInstance() {
		return CmSingletonUtil.getSessionInstance(PaginationInfo.class);
	}

	/**
	 * Sets the pagination settings for the application.
	 *
	 * @param pagination the pagination settings for the application
	 */
	public void setPaginationNumber(final int pagination) {
		if (isValidPagination(pagination)) {
			CookieUtil.setCookie(CM_PAGINATION, String.valueOf(pagination));
		}
	}

	@Override
	public int getPagination() {
		String paginationValue = CookieUtil.getCookieValue(CM_PAGINATION);
		if (paginationValue == null) {
			setPaginationNumber(DEFAULT_PAGING);
			return DEFAULT_PAGING;
		}

		return Integer.parseInt(paginationValue);
	}

	private boolean isValidPagination(final int pagination) {
		// make sure that people can't shoot themselves by setting it too high
		// (or too low)
		return VALID_PAGINATIONS.contains(pagination);
	}
}
