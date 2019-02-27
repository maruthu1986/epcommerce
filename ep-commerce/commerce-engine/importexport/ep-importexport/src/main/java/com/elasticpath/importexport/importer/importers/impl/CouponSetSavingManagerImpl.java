/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.importexport.importer.importers.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.elasticpath.domain.rules.Coupon;
import com.elasticpath.domain.rules.CouponConfig;
import com.elasticpath.domain.rules.CouponUsage;
import com.elasticpath.importexport.common.adapters.promotion.coupon.CouponSet;
import com.elasticpath.service.rules.CouponConfigService;
import com.elasticpath.service.rules.CouponService;
import com.elasticpath.service.rules.CouponUsageService;

/**
 * Coupon set saving manager.
 */
class CouponSetSavingManagerImpl implements SavingManager<CouponSet> {

	private CouponConfigService couponConfigService;
	private CouponService couponService;
	private CouponUsageService couponUsageService;

	@Override
	public void save(final CouponSet persistable) {
		update(persistable);
	}

	@Override
	public CouponSet update(final CouponSet oldCouponSet) {
		CouponSet updatedCouponSet = new CouponSet();

		// coupon config
		CouponConfig updatedConfig = saveOrUpdateCouponConfig(oldCouponSet.getCouponConfig());
		updatedCouponSet.setCouponConfig(updatedConfig);

		// coupon
		List<Coupon> updatedCoupons = new ArrayList<>();
		for (Coupon oldCoupon : oldCouponSet.getCoupons()) {
			oldCoupon.setCouponConfig(updatedConfig);
			Coupon updatedCoupon = saveOrUpdateCoupon(oldCoupon);
			updatedCoupons.add(updatedCoupon);

		}
		updatedCouponSet.setCoupons(updatedCoupons);

		// coupon usage
		for (Map.Entry<String, Collection<CouponUsage>> entry : oldCouponSet.getUsagesMap().entrySet()) {
			String couponCode = entry.getKey();
			Collection<CouponUsage> addedUsages = saveOrUpdateUsages(couponCode, entry.getValue());
			for (CouponUsage addedUsage : addedUsages) {
				updatedCouponSet.addUsage(couponCode, addedUsage);
			}
		}

		return updatedCouponSet;
	}

	private CouponConfig saveOrUpdateCouponConfig(final CouponConfig config) {
		CouponConfig couponConfig = couponConfigService.findByRuleCode(config.getRuleCode());
		if (couponConfig == null) {
			couponConfig = couponConfigService.add(config);
		} else {
			couponConfig.setDurationDays(config.getDurationDays());
			couponConfig.setGuid(config.getGuid());
			couponConfig.setLimitedDuration(config.isLimitedDuration());
			couponConfig.setUsageType(config.getUsageType());
			couponConfig.setRuleCode(config.getRuleCode());
			couponConfig.setUsageLimit(config.getUsageLimit());

			couponConfig = couponConfigService.update(couponConfig);
		}

		return couponConfig;
	}

	private Coupon saveOrUpdateCoupon(final Coupon coupon) {
		Coupon newCoupon = couponService.findByCouponCode(coupon.getCouponCode());
		if (newCoupon == null) {
			newCoupon = couponService.add(coupon);
		} else {
			newCoupon.setCouponCode(coupon.getCouponCode());
			newCoupon.setCouponConfig(coupon.getCouponConfig());

			newCoupon = couponService.update(newCoupon);
		}

		return newCoupon;
	}

	// always adding coupon usages since import/export doesn't support update of coupon usage.
	private Collection<CouponUsage> saveOrUpdateUsages(final String couponCode, final Collection<CouponUsage> usages) {
		List<CouponUsage> updatedUsages = new ArrayList<>();
		for (CouponUsage usage : usages) {
			Coupon coupon = couponService.findByCouponCode(couponCode);
			usage.setCoupon(coupon);
			CouponUsage updatedUsage = saveOrUpdateUsage(usage);
			updatedUsages.add(updatedUsage);
		}

		return updatedUsages;
	}

	private CouponUsage saveOrUpdateUsage(final CouponUsage usage) {
		CouponUsage couponUsage = couponUsageService.findByCouponCodeAndEmail(usage.getCoupon().getCouponCode(), usage.getCustomerEmailAddress());
		if (couponUsage == null) {
			couponUsage = couponUsageService.add(usage);
		} else {
			couponUsage.setActiveInCart(usage.isActiveInCart());
			couponUsage.setCoupon(usage.getCoupon());
			couponUsage.setCustomerEmailAddress(usage.getCustomerEmailAddress());
			couponUsage.setUseCount(usage.getUseCount());

			couponUsage = couponUsageService.update(couponUsage);
		}

		return couponUsage;
	}

	protected CouponConfigService getCouponConfigService() {
		return couponConfigService;
	}

	public void setCouponConfigService(final CouponConfigService couponConfigService) {
		this.couponConfigService = couponConfigService;
	}

	protected CouponService getCouponService() {
		return couponService;
	}

	public void setCouponService(final CouponService couponService) {
		this.couponService = couponService;
	}

	protected CouponUsageService getCouponUsageService() {
		return couponUsageService;
	}

	public void setCouponUsageService(final CouponUsageService couponUsageService) {
		this.couponUsageService = couponUsageService;
	}
}
