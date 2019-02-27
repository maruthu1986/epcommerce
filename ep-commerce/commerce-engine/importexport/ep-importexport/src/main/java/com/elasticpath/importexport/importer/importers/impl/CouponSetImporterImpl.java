/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.importexport.importer.importers.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.elasticpath.domain.rules.Coupon;
import com.elasticpath.domain.rules.CouponConfig;
import com.elasticpath.domain.rules.CouponUsage;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.adapters.promotion.coupon.CouponSet;
import com.elasticpath.importexport.common.adapters.promotion.coupon.CouponSetAdapter;
import com.elasticpath.importexport.common.dto.promotion.coupon.CouponSetDTO;
import com.elasticpath.importexport.common.dto.promotion.coupon.CouponUsageDTO;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.importer.configuration.ImporterConfiguration;
import com.elasticpath.importexport.importer.context.ImportContext;
import com.elasticpath.importexport.importer.importers.CollectionsStrategy;
import com.elasticpath.importexport.importer.importers.SavingStrategy;
import com.elasticpath.importexport.importer.types.CollectionStrategyType;
import com.elasticpath.importexport.importer.types.DependentElementType;
import com.elasticpath.service.rules.CouponConfigService;
import com.elasticpath.service.rules.CouponService;
import com.elasticpath.service.rules.CouponUsageService;

/**
 * Coupon set importer for importing coupon set.
 */
public class CouponSetImporterImpl extends AbstractImporterImpl<CouponSet, CouponSetDTO> {

	private CouponConfigService couponConfigService;

	private CouponService couponService;

	private CouponSetAdapter couponSetAdapter;

	private CouponUsageService couponUsageService;

	private SavingManager<CouponSet> couponSetSavingManager;


	/**
	 * @param couponUsageService the couponUsageService to set
	 */
	public void setCouponUsageService(final CouponUsageService couponUsageService) {
		this.couponUsageService = couponUsageService;
	}

	@Override
	protected CouponSet findPersistentObject(final CouponSetDTO dto) {

		CouponConfig couponConfig = couponConfigService.findByRuleCode(dto.getCouponConfigDTO().getRuleCode());
		if (couponConfig == null) {
			return null;
		}

		CouponSet couponSet = new CouponSet();
		couponSet.setCouponConfig(couponConfig);

		for (String couponCode : dto.getCouponCodes()) {
			Coupon coupon = couponService.findByCouponCode(couponCode);
			if (coupon != null) {
				couponSet.getCoupons().add(coupon);
			}
		}

		for (CouponUsageDTO usageDTO : dto.getCouponUsageDTO()) {
			List<CouponUsage> usages = couponUsageService.findByCode(usageDTO.getCouponCode());
			for (CouponUsage usage : usages) {
				couponSet.addUsage(usage.getCoupon().getCouponCode(), usage);
			}

		}

		return couponSet;
	}

	@Override
	protected DomainAdapter<CouponSet, CouponSetDTO> getDomainAdapter() {
		return couponSetAdapter;
	}

	@Override
	protected String getDtoGuid(final CouponSetDTO dto) {
		return null;
	}

	@Override
	public String getImportedObjectName() {
		return CouponSetDTO.ROOT_ELEMENT;
	}

	@Override
	public void initialize(final ImportContext context, final SavingStrategy<CouponSet, CouponSetDTO> savingStrategy) {
		super.initialize(context, savingStrategy);
		getSavingStrategy().setSavingManager(couponSetSavingManager);
	}

	/**
	 * Setter for {@link CouponConfigService}.
	 *
	 * @param couponConfigService {@link CouponConfigService}.
	 */
	public void setCouponConfigService(final CouponConfigService couponConfigService) {
		this.couponConfigService = couponConfigService;
	}

	/**
	 * Setter for {@link CouponService}.
	 *
	 * @param couponService {@link CouponService}.
	 */
	public void setCouponService(final CouponService couponService) {
		this.couponService = couponService;
	}

	/**
	 * Setter for {@link CouponSetAdapter}.
	 *
	 * @param couponSetAdapter {@link CouponSetAdapter}.
	 */
	public void setCouponSetAdapter(final CouponSetAdapter couponSetAdapter) {
		this.couponSetAdapter = couponSetAdapter;
	}

	public void setCouponSetSavingManager(final SavingManager<CouponSet> couponSetSavingManager) {
		this.couponSetSavingManager = couponSetSavingManager;
	}

	@Override
	protected void setImportStatus(final CouponSetDTO object) {
		getStatusHolder().setImportStatus("(for coupon set " + object.getCouponConfigDTO().getCouponConfigCode() + ")");
	}

	@Override
	protected CollectionsStrategy<CouponSet, CouponSetDTO> getCollectionsStrategy() {
		return new CouponSetCollectionsStrategy(getContext().getImportConfiguration().getImporterConfiguration(JobType.COUPONSET));
	}

	@Override
	public Class<? extends CouponSetDTO> getDtoClass() {
		return CouponSetDTO.class;
	}

	/**
	 * Coupon set collection strategy.
	 */
	private class CouponSetCollectionsStrategy implements CollectionsStrategy<CouponSet, CouponSetDTO> {
		private final ImporterConfiguration importerConfiguration;

		CouponSetCollectionsStrategy(final ImporterConfiguration importerConfiguration) {
			this.importerConfiguration = importerConfiguration;
		}

		@Override
		public boolean isForPersistentObjectsOnly() {
			return false;
		}

		@Override
		public void prepareCollections(final CouponSet domainObject, final CouponSetDTO dto) {
			if (importerConfiguration.getCollectionStrategyType(DependentElementType.COUPONSET).equals(CollectionStrategyType.CLEAR_COLLECTION)) {
				CouponConfig couponConfig = domainObject.getCouponConfig();
				if (couponConfig != null) {
					couponService.deleteCouponsByCouponConfigGuid(couponConfig.getGuid());
				}

				domainObject.setCoupons(new ArrayList<>());
				domainObject.setUsagesMap(new HashMap<>());
			}
		}
	}
}
