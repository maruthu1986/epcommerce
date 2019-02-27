/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.store.promotions.model;

import java.util.ArrayList;
import java.util.List;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.common.dto.CouponUsageModelDto;
import com.elasticpath.commons.pagination.Page;
import com.elasticpath.commons.pagination.SearchCriterion;
import com.elasticpath.commons.pagination.SearchablePaginatorLocator;
import com.elasticpath.domain.rules.CouponUsage;
import com.elasticpath.service.rules.CouponUsageService;

/**
 * Connects a paginator to the database server.
 */
public class CouponUsageModelDtoDatabasePaginatorLocator implements SearchablePaginatorLocator<CouponUsageModelDto> {
	private CouponUsageService couponUsageService;

	@Override
	public List<CouponUsageModelDto> findItems(
			final Page<CouponUsageModelDto> unpopulatedPage, final String objectId, final List<SearchCriterion> searchCriteria) {
		int startIndex = unpopulatedPage.getPageStartIndex() - 1;

		long configId = Long.parseLong(objectId);
		List<CouponUsageModelDto> couponModelDtoList = new ArrayList<>();
		for (CouponUsage couponUsage : getCouponUsageService()
				.findCouponUsagesForCouponConfigId(
						configId,
						searchCriteria
								.toArray(
										new SearchCriterion[searchCriteria
												.size()]), startIndex,
						unpopulatedPage.getPageSize(),
						unpopulatedPage.getOrderingFields())) {
			CouponUsageModelDto dto = new CouponUsageModelDto(couponUsage.getUidPk(), couponUsage
					.getCoupon().getCouponCode(), couponUsage
					.getCustomerEmailAddress(), couponUsage.isSuspended());
			couponModelDtoList.add(dto);
		}

		return couponModelDtoList;
	}

	@Override
	public long getTotalItems(final List<SearchCriterion> searchCriteria, final String objectId) {
		long couponConfigId = Long.parseLong(objectId);
		return getCouponUsageService().getCountForSearchCriteria(
				couponConfigId,
				searchCriteria.toArray(new SearchCriterion[searchCriteria.size()]));
	}

	/**
	 * 
	 * @return the coupon usage service
	 */
	protected CouponUsageService getCouponUsageService() {
		if (couponUsageService == null) {
			couponUsageService = ServiceLocator.getService("couponUsageService"); //$NON-NLS-1$
		}
		return couponUsageService;
	}
}
