/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */

package com.elasticpath.commons.constants;

/**
 * Ep Shipping Calculation bean id constants.
 */
public final class EpShippingContextIdNames {

	/**
	 * bean id for implementation of com.elasticpath.domain.shipping.ShippingRegion.
	 */
	public static final String SHIPPING_REGION = "shippingRegion";

	/**
	 * bean id for implementation of com.elasticpath.domain.shipping.ShippingServiceLevel.
	 */
	public static final String SHIPPING_SERVICE_LEVEL = "shippingServiceLevel";

	/**
	 * bean id for implementation of com.elasticpath.domain.shipping.ShippingServiceLevelDeleted.
	 */
	public static final String SHIPPING_SERVICE_LEVEL_DELETED = "shippingServiceLevelDeleted";

	/**
	 * bean id for implementation of com.elasticpath.domain.shipping.ShippingCostCalculationMethod.
	 */
	public static final String SHIPPING_COST_CALCULATION_METHOD = "shippingCostCalculationMethod";

	/**
	 * bean id for implementation of com.elasticpath.domain.shipping.ShippingCostCalculationParameter.
	 */
	public static final String SHIPPING_COST_CALCULATION_PARAMETER = "shippingCostCalculationParameter";

	/**
	 * bean id for implementation of com.elasticpath.domain.shipping.impl.FixedPriceMethodImpl.
	 */
	public static final String SHIPPING_FIXED_PRICE_METHOD = "fixedPriceMethod";

	/**
	 * bean id for implementation of com.elasticpath.domain.shipping.impl.OrderTotalPercentageMethodImpl.
	 */
	public static final String SHIPPING_TOTAL_PERCENT_METHOD = "orderTotalPercentageMethod";

	/**
	 * bean id for implementation of com.elasticpath.domain.shipping.impl.FixedBaseAndOrderTotalPercentageMethodImpl.
	 */
	public static final String SHIPPING_FIXED_BASE_TOTAL_PERCENT_METHOD = "fixedBaseAndOrderTotalPercentageMethod";

	/**
	 * bean id for implementation of com.elasticpath.domain.shipping.impl.CostPerUnitWeightMethodImpl.
	 */
	public static final String SHIPPING_COST_PER_UNIT_WEIGHT_METHOD = "costPerUnitWeightMethod";

	/**
	 * bean id for implementation of com.elasticpath.domain.shipping.impl.FixedBaseAndCostPerUnitWeightMethodImpl.
	 */
	public static final String SHIPPING_FIXED_BASE_COST_PER_UNIT_WEIGHT_METHOD = "fixedBaseAndCostPerUnitWeightMethod";

	/**
	 * bean id for implementation of com.elasticpath.service.ShippingREgionService.
	 */
	public static final String SHIPPING_REGION_SERVICE = "shippingRegionService";

	/**
	 * bean id for implementation of com.elasticpath.service.ShippingServiceLevelService.
	 */
	public static final String SHIPPING_SERVICE_LEVEL_SERVICE = "shippingServiceLevelService";

	/**
	 * bean id for implementation of com.elasticpath.service.shipping.ShippingOptionTransformer.
	 */
	public static final String SHIPPING_OPTION_TRANSFORMER = "shippingOptionTransformer";

	/** bean id for {@link com.elasticpath.domain.misc.LocalizedPropertyValue}. */
	public static final String SHIPPING_SERVICE_LEVEL_LOCALIZED_PROPERTY_VALUE = "shippingServiceLevelLocalizedPropertyValue";

	private EpShippingContextIdNames() {
		// to hide the implicit public constructor.
		// do nothing
	}

}
