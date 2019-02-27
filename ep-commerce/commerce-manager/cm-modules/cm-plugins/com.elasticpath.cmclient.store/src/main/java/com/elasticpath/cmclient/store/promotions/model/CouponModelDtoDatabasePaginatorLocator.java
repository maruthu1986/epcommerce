/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.store.promotions.model;

import java.util.ArrayList;
import java.util.List;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.common.dto.CouponModelDto;
import com.elasticpath.commons.pagination.Page;
import com.elasticpath.commons.pagination.SearchCriterion;
import com.elasticpath.commons.pagination.SearchablePaginatorLocator;
import com.elasticpath.domain.rules.Coupon;
import com.elasticpath.service.rules.CouponService;

/**
 * Connects a paginator to the database server.
 */
public class CouponModelDtoDatabasePaginatorLocator implements SearchablePaginatorLocator<CouponModelDto> {
	private CouponService couponService;

	@Override
	public List<CouponModelDto> findItems(
			final Page<CouponModelDto> unpopulatedPage, final String objectId, final List<SearchCriterion> searchCriteria) {
		int startIndex = unpopulatedPage.getPageStartIndex() - 1;

		long configUidPk = Long.parseLong(objectId);
		List<CouponModelDto> couponModelDtoList = new ArrayList<>();
		for (Coupon coupon : getCouponService().findCouponsForCouponConfigId(
				configUidPk,
				searchCriteria.toArray(
						new SearchCriterion[searchCriteria.size()]),
				startIndex, unpopulatedPage.getPageSize(),
				unpopulatedPage.getOrderingFields())) {
			CouponModelDto dto = new CouponModelDto(coupon.getUidPk(), coupon.getCouponCode(), coupon.isSuspended());
			couponModelDtoList.add(dto);
		}

		return couponModelDtoList;
	}

	@Override
	public long getTotalItems(final List<SearchCriterion> searchCriteria, final String objectId) {
		long couponConfigId = Long.parseLong(objectId);
		return getCouponService().getCountForSearchCriteria(
				couponConfigId, searchCriteria.toArray(new SearchCriterion[searchCriteria.size()]));
	}

	/**
	 * @return the coupon service
	 */
	protected CouponService getCouponService() {
		if (couponService == null) {
			couponService = ServiceLocator.getService("couponService"); //$NON-NLS-1$
		}
		return couponService;
	}
}
