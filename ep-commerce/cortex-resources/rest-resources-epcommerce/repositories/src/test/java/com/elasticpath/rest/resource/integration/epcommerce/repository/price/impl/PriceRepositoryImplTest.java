/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.price.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import io.reactivex.Single;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.common.dto.sellingchannel.ShoppingItemDtoFactory;
import com.elasticpath.common.pricing.service.PriceLookupFacade;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.DiscountRecord;
import com.elasticpath.domain.store.Store;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.CustomerSessionRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.sku.ProductSkuRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.store.StoreRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.impl.ReactiveAdapterImpl;

@RunWith(MockitoJUnitRunner.class)
public class PriceRepositoryImplTest {

	private static final String STORE_CODE = "test store code";
	private static final String SKU_CODE = "sku code";

	@Mock
	private Price price;
	@Mock
	private StoreRepository mockStoreRepository;
	@Mock
	private PriceLookupFacade mockPriceLookupFacade;
	@Mock
	private ShoppingItemDtoFactory mockShoppingItemDtoFactory;
	@Mock
	private ShoppingItemDto mockShoppingItemDto;
	@Mock
	private CustomerSessionRepository mockCustomerSessionRepository;
	@Mock
	private CustomerSession mockCustomerSession;
	@Mock
	private Shopper mockShopper;
	@Mock
	private Store mockStore;
	@Mock
	private ProductSkuRepository mockProductSkuRepository;

	private Product product;
	private ProductSkuImpl sku;

	@InjectMocks
	private ReactiveAdapterImpl reactiveAdapterImpl;

	private PriceRepositoryImpl priceRepository;

	@Before
	public void setUp() {
		priceRepository = new PriceRepositoryImpl(mockShoppingItemDtoFactory, mockStoreRepository, mockCustomerSessionRepository,
				mockPriceLookupFacade, mockProductSkuRepository, reactiveAdapterImpl);
	}

	@Test
	public void ensurePriceExistsReturnsTrue() {
		mockPriceResult(price);

		priceRepository.priceExists(STORE_CODE, SKU_CODE)
				.test()
				.assertNoErrors()
				.assertValue(Boolean.TRUE);
	}

	@Test
	public void ensurePriceExistsReturnsTrueWhenCached() {
		mockPriceResult(price);

		priceRepository.priceExists(STORE_CODE, SKU_CODE)
				.test()
				.assertNoErrors()
				.assertValue(Boolean.TRUE);
	}

	@Test
	public void ensurePriceDoesNotExistReturnsFalse() {
		mockPriceResult(null);

		priceRepository.priceExists(STORE_CODE, SKU_CODE)
				.test()
				.assertNoErrors()
				.assertValue(Boolean.FALSE);
	}

	@Test
	public void ensureGetPriceReturnsExpectedPrice() {
		mockPriceResult(price);

		priceRepository.getPrice(STORE_CODE, SKU_CODE)
				.test()
				.assertNoErrors()
				.assertValue(price);
	}

	@Test
	public void ensureGetPriceReturnsNotFoundForNoPrice() {
		mockPriceResult(null);

		priceRepository.getPrice(STORE_CODE, SKU_CODE)
				.test()
				.assertError(throwable -> checkErrorMessageAndStatus((ResourceOperationFailure) throwable,
						String.format(PriceRepositoryImpl.SKU_PRICE_NOT_FOUND, SKU_CODE)));
	}

	private boolean checkErrorMessageAndStatus(final ResourceOperationFailure failure, final String errorMessage) {
		boolean messageCheck = failure.getLocalizedMessage().equals(errorMessage);
		boolean statusCheck = failure.getResourceStatus().equals(ResourceStatus.NOT_FOUND);
		return messageCheck && statusCheck;
	}

	private void mockPriceResult(final Price price) {

		setupMockStore();
		setupMockShoppingItemDto(SKU_CODE, 1);
		mockProduct(true);
		mockItemRepositoryToReturnSku();
		mockPriceLookupFacadeToReturnPriceForSku(price);
	}

	@Test
	public void ensureGetLowestItemPriceReturnsExpectedPrice() {
		mockLowestPriceResult(price, true);

		priceRepository.getLowestPrice(SKU_CODE)
				.test()
				.assertNoErrors()
				.assertValue(price);
	}

	@Test
	public void ensureGetLowestItemPriceReturnsExpectedPriceWhenCached() {
		mockLowestPriceResult(price, true);

		priceRepository.getLowestPrice(SKU_CODE)
				.test()
				.assertNoErrors()
				.assertValue(price);
	}

	@Test
	public void ensureGetLowestPriceForProductWithNoMultipleSkusReturnsError() {
		mockLowestPriceResult(price, false);

		priceRepository.getLowestPrice(SKU_CODE)
				.test()
				.assertError(ResourceOperationFailure.notFound());
	}

	@Test
	public void ensureGetLowestPriceReturnsNotFoundForNoLowestPrice() {
		mockLowestPriceResult(null, true);

		priceRepository.getLowestPrice(SKU_CODE)
				.test()
				.assertError(throwable -> checkErrorMessageAndStatus((ResourceOperationFailure) throwable,
						String.format(PriceRepositoryImpl.PRODUCT_PRICE_NOT_FOUND, product.getGuid())));
	}

	@Test
	public void ensureGetLowestItemPriceRulesReturnsExpectedRules() {
		mockLowestPriceResult(price, true);
		final long appliedRuleId = 1L;

		final DiscountRecord discountRecord = mock(DiscountRecord.class);
		when(discountRecord.getRuleId()).thenReturn(appliedRuleId);
		when(price.getDiscountRecords()).thenReturn(Collections.singleton(discountRecord));

		Set<Long> appliedRules = new HashSet<>();
		appliedRules.add(appliedRuleId);
		priceRepository.getLowestPriceRules(STORE_CODE, SKU_CODE)
				.test()
				.assertNoErrors()
				.assertValue(appliedRules);
	}

	@Test
	public void ensureGetLowestItemPriceRulesReturnsExpectedRulesWhenCached() {
		mockLowestPriceResult(price, true);
		final long appliedRuleId = 1L;

		final DiscountRecord discountRecord = mock(DiscountRecord.class);
		when(discountRecord.getRuleId()).thenReturn(appliedRuleId);
		when(price.getDiscountRecords()).thenReturn(Collections.singleton(discountRecord));

		Set<Long> appliedRules = new HashSet<>();
		appliedRules.add(appliedRuleId);
		priceRepository.getLowestPriceRules(STORE_CODE, SKU_CODE)
				.test()
				.assertNoErrors()
				.assertValue(appliedRules);
	}

	private void mockLowestPriceResult(final Price price, final boolean hasMultipleSkus) {

		setupMockStore();
		mockProduct(hasMultipleSkus);
		mockItemRepositoryToReturnSku();
		mockPriceLookupFacadeToReturnPromotedPriceForSku(price);

	}

	private void setupMockStore() {
		when(mockCustomerSessionRepository.findOrCreateCustomerSessionAsSingle()).thenReturn(Single.just(mockCustomerSession));
		when(mockCustomerSession.getShopper()).thenReturn(mockShopper);
		when(mockShopper.getStoreCode()).thenReturn(STORE_CODE);
		when(mockStoreRepository.findStoreAsSingle(STORE_CODE)).thenReturn(Single.just(mockStore));
	}

	private void setupMockShoppingItemDto(final String skuCode, final int quantity) {
		when(mockShoppingItemDtoFactory.createDto(skuCode, quantity)).thenReturn(mockShoppingItemDto);
	}

	private void mockProduct(final boolean hasMultipleSkus) {
		product = mock(Product.class);
		when(product.getGuid()).thenReturn("product guid");
		when(product.hasMultipleSkus()).thenReturn(hasMultipleSkus);

		sku = new ProductSkuImpl();
		sku.setSkuCode(SKU_CODE);
		sku.setProduct(product);
	}

	private void mockItemRepositoryToReturnSku() {

		when(mockProductSkuRepository.getProductSkuWithAttributesByCodeAsSingle(SKU_CODE)).thenReturn(Single.just(sku));
	}

	private void mockPriceLookupFacadeToReturnPriceForSku(final Price price) {
		when(mockPriceLookupFacade.getShoppingItemDtoPrice(mockShoppingItemDto, mockStore, mockShopper))
				.thenReturn(price);
	}

	private void mockPriceLookupFacadeToReturnPromotedPriceForSku(final Price price) {
		when(mockPriceLookupFacade.getPromotedPriceForProduct(product, mockStore, mockShopper)).thenReturn(price);
	}

}
