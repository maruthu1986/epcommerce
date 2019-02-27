/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.test.integration.shipping;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Locale;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.catalog.AvailabilityCriteria;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.service.shipping.transformers.PricedShippableItemContainerTransformer;
import com.elasticpath.service.shipping.transformers.ShippableItemContainerTransformer;
import com.elasticpath.service.shoppingcart.PricingSnapshotService;
import com.elasticpath.shipping.connectivity.dto.PricedShippableItem;
import com.elasticpath.shipping.connectivity.dto.PricedShippableItemContainer;
import com.elasticpath.shipping.connectivity.dto.ShippableItem;
import com.elasticpath.shipping.connectivity.dto.ShippableItemContainer;
import com.elasticpath.shipping.connectivity.dto.ShippingAddress;
import com.elasticpath.shipping.connectivity.dto.ShippingCalculationResult;
import com.elasticpath.shipping.connectivity.service.ShippingCalculationService;
import com.elasticpath.shipping.connectivity.service.cache.impl.CachingShippingCalculationServiceImpl;
import com.elasticpath.test.integration.DirtiesDatabase;

/**
 * Tests the caching shipping calculation service.
 */
public class CachingShippingCalculationServiceImplTest extends AbstractShippingTestCase {

	private static final String AVAILABLE_SHIPPING_OPTION_COUNT_DOES_NOT_MATCH_EXPECTED = "available shipping option count does not match expected";
	private static final String UNPRICED_INVOCATION_COUNT_DOES_NOT_MATCH_EXPECTED = "unpriced provider invocation count does not match expected";
	private static final String PRICED_INVOCATION_COUNT_DOES_NOT_MATCH_EXPECTED = "priced provider invocation count does not match expected";

	@Resource
	private CachingShippingCalculationServiceImpl cachingShippingCalculationService;

	@Resource(name = "nonCachingShippingCalculationService")
	private ShippingCalculationService nonCachingShippingCalculationService;

	private ShippingCalculationServiceSpy ShippingCalculationServiceSpy;

	@Resource
	private ShippableItemContainerTransformer shippableItemContainerTransformer;

	@Resource
	private PricedShippableItemContainerTransformer pricedShippableItemContainerTransformer;

	@Resource
	private PricingSnapshotService pricingSnapshotService;

	private Product product;

	@Before
	public void setUp() {
		ShippingCalculationServiceSpy = new ShippingCalculationServiceSpy(nonCachingShippingCalculationService);
		cachingShippingCalculationService.setShippingCalculationService(ShippingCalculationServiceSpy);
		product = createTestProduct();
	}

	@DirtiesDatabase
	@Test
	public void testGetUnpricedShippingOptionsFromCache() {
		final ShoppingCart shoppingCart = checkoutTestCartBuilder.build();
		final ShippableItemContainer<ShippableItem> shippableItemContainer = shippableItemContainerTransformer.apply(shoppingCart);

		ShippingCalculationResult unpricedShippingOptions = cachingShippingCalculationService.getUnpricedShippingOptions
				(shippableItemContainer);

		assertThat(unpricedShippingOptions.getAvailableShippingOptions()).as(AVAILABLE_SHIPPING_OPTION_COUNT_DOES_NOT_MATCH_EXPECTED).hasSize(1);
		assertThat(ShippingCalculationServiceSpy.getInvocationCount()).as(UNPRICED_INVOCATION_COUNT_DOES_NOT_MATCH_EXPECTED).isEqualTo(1);

		unpricedShippingOptions = cachingShippingCalculationService.getUnpricedShippingOptions(shippableItemContainer);

		assertThat(unpricedShippingOptions.getAvailableShippingOptions()).as(AVAILABLE_SHIPPING_OPTION_COUNT_DOES_NOT_MATCH_EXPECTED).hasSize(1);
		assertThat(ShippingCalculationServiceSpy.getInvocationCount()).as(UNPRICED_INVOCATION_COUNT_DOES_NOT_MATCH_EXPECTED).isEqualTo(1);
	}

	@DirtiesDatabase
	@Test
	public void testGetUnpricedShippingOptionsFromProvider() {
		final ShoppingCart shoppingCart = checkoutTestCartBuilder.build();
		final ShippableItemContainer<ShippableItem> shippableItemContainer = shippableItemContainerTransformer.apply(shoppingCart);

		ShippingCalculationResult result = cachingShippingCalculationService.getUnpricedShippingOptions(shippableItemContainer);

		assertThat(result.getAvailableShippingOptions()).as(AVAILABLE_SHIPPING_OPTION_COUNT_DOES_NOT_MATCH_EXPECTED).hasSize(1);
		assertThat(ShippingCalculationServiceSpy.getInvocationCount()).as(UNPRICED_INVOCATION_COUNT_DOES_NOT_MATCH_EXPECTED).isEqualTo(1);

		final ShoppingCart shoppingCartWithNewItem = checkoutTestCartBuilder.withProduct(product).build();
		final ShippableItemContainer<ShippableItem> shippableItemContainerWithNewItem =
				shippableItemContainerTransformer.apply(shoppingCartWithNewItem);

		ShippingCalculationResult resultWithNewItem = cachingShippingCalculationService
				.getUnpricedShippingOptions(shippableItemContainerWithNewItem);
		assertThat(resultWithNewItem.getAvailableShippingOptions()).as(AVAILABLE_SHIPPING_OPTION_COUNT_DOES_NOT_MATCH_EXPECTED).hasSize(1);
		assertThat(ShippingCalculationServiceSpy.getInvocationCount()).as(UNPRICED_INVOCATION_COUNT_DOES_NOT_MATCH_EXPECTED).isEqualTo(2);
	}

	@DirtiesDatabase
	@Test
	public void testGetPricedShippingOptionsFromCache() {
		final ShoppingCart shoppingCart = checkoutTestCartBuilder.build();
		final ShoppingCartPricingSnapshot shoppingCartPricingSnapshot = pricingSnapshotService.getPricingSnapshotForCart(shoppingCart);
		final PricedShippableItemContainer<PricedShippableItem> pricedShippableItemContainer =
				pricedShippableItemContainerTransformer.apply(shoppingCart, shoppingCartPricingSnapshot);

		ShippingCalculationResult pricedShippingOptions = cachingShippingCalculationService.getPricedShippingOptions(pricedShippableItemContainer);

		assertThat(pricedShippingOptions.getAvailableShippingOptions()).as(AVAILABLE_SHIPPING_OPTION_COUNT_DOES_NOT_MATCH_EXPECTED).hasSize(1);
		assertThat(ShippingCalculationServiceSpy.getInvocationCount()).as(PRICED_INVOCATION_COUNT_DOES_NOT_MATCH_EXPECTED).isEqualTo(1);

		pricedShippingOptions = cachingShippingCalculationService.getPricedShippingOptions(pricedShippableItemContainer);
		assertThat(pricedShippingOptions.getAvailableShippingOptions()).as(AVAILABLE_SHIPPING_OPTION_COUNT_DOES_NOT_MATCH_EXPECTED).hasSize(1);
		assertThat(ShippingCalculationServiceSpy.getInvocationCount()).as(PRICED_INVOCATION_COUNT_DOES_NOT_MATCH_EXPECTED).isEqualTo(1);
	}

	@DirtiesDatabase
	@Test
	public void testGetPricedShippingOptionsFromProvider() {
		final ShoppingCart shoppingCart = checkoutTestCartBuilder.build();
		final ShoppingCartPricingSnapshot shoppingCartPricingSnapshot = pricingSnapshotService.getPricingSnapshotForCart(shoppingCart);
		final PricedShippableItemContainer<PricedShippableItem> pricedShippableItemContainer =
				pricedShippableItemContainerTransformer.apply(shoppingCart, shoppingCartPricingSnapshot);

		ShippingCalculationResult pricedShippingOptions = cachingShippingCalculationService
				.getPricedShippingOptions(pricedShippableItemContainer);

		assertThat(pricedShippingOptions.getAvailableShippingOptions()).as(AVAILABLE_SHIPPING_OPTION_COUNT_DOES_NOT_MATCH_EXPECTED).hasSize(1);
		assertThat(ShippingCalculationServiceSpy.getInvocationCount()).as(PRICED_INVOCATION_COUNT_DOES_NOT_MATCH_EXPECTED).isEqualTo(1);

		final ShoppingCart shoppingCartWithNewItem = checkoutTestCartBuilder.withProduct(product).build();
		final ShoppingCartPricingSnapshot pricingSnapshotWithNewItem = pricingSnapshotService.getPricingSnapshotForCart(shoppingCartWithNewItem);
		final PricedShippableItemContainer<PricedShippableItem> pricedShippableItemContainerWithNewItem =
				pricedShippableItemContainerTransformer.apply(shoppingCartWithNewItem, pricingSnapshotWithNewItem);

		ShippingCalculationResult pricedShippingOptions2 = cachingShippingCalculationService
				.getPricedShippingOptions(pricedShippableItemContainerWithNewItem);

		assertThat(pricedShippingOptions2.getAvailableShippingOptions()).as(AVAILABLE_SHIPPING_OPTION_COUNT_DOES_NOT_MATCH_EXPECTED).hasSize(1);
		assertThat(ShippingCalculationServiceSpy.getInvocationCount()).as(PRICED_INVOCATION_COUNT_DOES_NOT_MATCH_EXPECTED).isEqualTo(2);

	}

	@DirtiesDatabase
	@Test
	public void testGetAllShippingOptionsFromProvider() {

		ShippingCalculationResult allShippingOptions = cachingShippingCalculationService.getAllShippingOptions(storeCode, locale);
		assertThat(allShippingOptions.getAvailableShippingOptions()).as(AVAILABLE_SHIPPING_OPTION_COUNT_DOES_NOT_MATCH_EXPECTED).hasSize(1);
		assertThat(ShippingCalculationServiceSpy.getInvocationCount()).as(UNPRICED_INVOCATION_COUNT_DOES_NOT_MATCH_EXPECTED).isEqualTo(1);

		ShippingCalculationResult allShippingOptions2 = cachingShippingCalculationService.getAllShippingOptions(storeCode, Locale.UK);

		assertThat(allShippingOptions2.getAvailableShippingOptions()).as(AVAILABLE_SHIPPING_OPTION_COUNT_DOES_NOT_MATCH_EXPECTED).hasSize(1);
		assertThat(ShippingCalculationServiceSpy.getInvocationCount()).as(UNPRICED_INVOCATION_COUNT_DOES_NOT_MATCH_EXPECTED).isEqualTo(2);
	}

	@DirtiesDatabase
	@Test
	public void testGetAllShippingOptionsFromCache() {

		ShippingCalculationResult allShippingOptions = cachingShippingCalculationService.getAllShippingOptions(storeCode, locale);
		assertThat(allShippingOptions.getAvailableShippingOptions()).as(AVAILABLE_SHIPPING_OPTION_COUNT_DOES_NOT_MATCH_EXPECTED).hasSize(1);
		assertThat(ShippingCalculationServiceSpy.getInvocationCount()).as(UNPRICED_INVOCATION_COUNT_DOES_NOT_MATCH_EXPECTED).isEqualTo(1);

		allShippingOptions = cachingShippingCalculationService.getAllShippingOptions(storeCode, locale);

		assertThat(allShippingOptions.getAvailableShippingOptions()).as(AVAILABLE_SHIPPING_OPTION_COUNT_DOES_NOT_MATCH_EXPECTED).hasSize(1);
		assertThat(ShippingCalculationServiceSpy.getInvocationCount()).as(UNPRICED_INVOCATION_COUNT_DOES_NOT_MATCH_EXPECTED).isEqualTo(1);
	}

	@DirtiesDatabase
	@Test
	public void testGetPricedShippingOptionsCachesForUnpriced() {
		final ShoppingCart shoppingCart = checkoutTestCartBuilder.build();
		final ShoppingCartPricingSnapshot shoppingCartPricingSnapshot = pricingSnapshotService.getPricingSnapshotForCart(shoppingCart);
		final ShippableItemContainer<ShippableItem>  unpricedShippableItemContainer = shippableItemContainerTransformer.apply(shoppingCart);
		final PricedShippableItemContainer<PricedShippableItem> pricedShippableItemContainer =
				pricedShippableItemContainerTransformer.apply(shoppingCart, shoppingCartPricingSnapshot);

		ShippingCalculationResult pricedShippingOptions = cachingShippingCalculationService
				.getPricedShippingOptions(pricedShippableItemContainer);

		assertThat(pricedShippingOptions.getAvailableShippingOptions()).as(AVAILABLE_SHIPPING_OPTION_COUNT_DOES_NOT_MATCH_EXPECTED).hasSize(1);
		assertThat(ShippingCalculationServiceSpy.getInvocationCount()).as(PRICED_INVOCATION_COUNT_DOES_NOT_MATCH_EXPECTED).isEqualTo(1);

		ShippingCalculationResult unpricedShippingOptions = cachingShippingCalculationService.getUnpricedShippingOptions
				(unpricedShippableItemContainer);

		assertThat(unpricedShippingOptions.getAvailableShippingOptions()).as(AVAILABLE_SHIPPING_OPTION_COUNT_DOES_NOT_MATCH_EXPECTED).hasSize(1);
		assertThat(ShippingCalculationServiceSpy.getInvocationCount()).as(UNPRICED_INVOCATION_COUNT_DOES_NOT_MATCH_EXPECTED).isEqualTo(1);
	}

	private static class ShippingCalculationServiceSpy implements ShippingCalculationService {

		private final ShippingCalculationService shippingCalculationService;

		private int invocationCount = 0;

		public ShippingCalculationServiceSpy(ShippingCalculationService shippingCalculationService) {
			this.shippingCalculationService = shippingCalculationService;
		}

		@Override
		public ShippingCalculationResult getUnpricedShippingOptions(final ShippableItemContainer<?> shippableItemContainer) {
			invocationCount++;
			return shippingCalculationService.getUnpricedShippingOptions(shippableItemContainer);
		}

		@Override
		public ShippingCalculationResult getPricedShippingOptions(final PricedShippableItemContainer<?> pricedShippableItemContainer) {
			invocationCount++;
			return shippingCalculationService.getPricedShippingOptions(pricedShippableItemContainer);
		}

		@Override
		public ShippingCalculationResult getAllShippingOptions(final String storeCode, final Locale locale) {
			invocationCount++;
			return shippingCalculationService.getAllShippingOptions(storeCode, locale);
		}

		@Override
		public ShippingCalculationResult getUnpricedShippingOptions(final ShippingAddress destAddress, final String storeCode, final Locale locale) {
			invocationCount++;
			return shippingCalculationService.getUnpricedShippingOptions(destAddress, storeCode, locale);
		}

		public int getInvocationCount() {
			return invocationCount;
		}
	}

	private Product createTestProduct() {
		return persisterFactory.getCatalogTestPersister()
				.persistProductWithSku(scenario.getCatalog(),
						scenario.getCategory(),
						scenario.getWarehouse(),
						"Test Product",
						BigDecimal.TEN,
						scenario.getStore().getDefaultCurrency(),
						"Store",
						"Test_" + System.currentTimeMillis() + "_" + Math.random(),
						"Test Product",
						"testproductsku_" + System.currentTimeMillis() + "_" + Math.random(),
						"NONE",
						null,
						true,
						AvailabilityCriteria.ALWAYS_AVAILABLE,
						0,
						false,
						null,
						-1,
						-1);
	}

}
