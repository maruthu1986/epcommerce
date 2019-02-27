/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.store.promotions;

import java.util.Collection;

import com.elasticpath.common.dto.CouponModelDto;
import com.elasticpath.common.dto.CouponUsageModelDto;
import com.elasticpath.common.dto.DatabaseMemoryMergeSearchablePaginatorLocator;

/**
 * Model for CouponEditorPage.
 */
public class CouponPageModel {
	private DatabaseMemoryMergeSearchablePaginatorLocator<CouponModelDto> couponDatabaseLocator;
	private DatabaseMemoryMergeSearchablePaginatorLocator<CouponUsageModelDto> couponUsageDatabaseLocator;
	
	/**
	 * 
	 * @param paginatorLocator The merge model for coupons.
	 */
	public void setCouponMemoryPaginatorLocator(final 
			DatabaseMemoryMergeSearchablePaginatorLocator<CouponModelDto> paginatorLocator) {
		this.couponDatabaseLocator = paginatorLocator;		
	}

	/**
	 * 
	 * @param paginatorLocator The merge model for coupon usages.
	 */
	public void setCouponUsageMemoryPaginatorLocator(final 
			DatabaseMemoryMergeSearchablePaginatorLocator<CouponUsageModelDto> paginatorLocator) {
		this.couponUsageDatabaseLocator = paginatorLocator;
	}

	/**
	 * 
	 * @return The coupon usages to be added.
	 */
	public Collection<CouponUsageModelDto> getAddedCouponUsageItems() {
		return couponUsageDatabaseLocator.getAddedItems();
	}

	/**
	 * 
	 * @return The coupon usages to be updated. 
	 */
	public Collection<CouponUsageModelDto> getUpdatedCouponUsageItems() {
		return couponUsageDatabaseLocator.getUpdatedItems();
	}

	/**
	 * 
	 * @return The coupons to be added.
	 */
	public Collection<CouponModelDto> getAddedCouponItems() {
		return couponDatabaseLocator.getAddedItems();
	}

	/**
	 * 
	 * @return The coupons to be updated.
	 */
	public Collection<CouponModelDto> getUpdatedCouponItems() {
		return couponDatabaseLocator.getUpdatedItems();
	}

	/**
	 * 
	 * @param dto The dto to add.
	 */
	public void add(final CouponUsageModelDto dto) {
		couponUsageDatabaseLocator.add(dto);
	}

	/**
	 * 
	 * @param dto The dto to add.
	 */
	public void add(final CouponModelDto dto) {
		couponDatabaseLocator.add(dto);
	}

	/**
	 * 
	 * @param dto The dto to update.
	 */
	public void update(final CouponUsageModelDto dto) {
		couponUsageDatabaseLocator.update(dto);
	}

	/**
	 * 
	 * @param dto The dto to update.
	 */
	public void update(final CouponModelDto dto) {
		couponDatabaseLocator.update(dto);
	}

}
