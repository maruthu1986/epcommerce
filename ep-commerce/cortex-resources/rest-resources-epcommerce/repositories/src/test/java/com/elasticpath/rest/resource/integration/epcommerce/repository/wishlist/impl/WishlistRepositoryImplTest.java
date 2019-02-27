/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.wishlist.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;
import io.reactivex.Single;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.WishList;
import com.elasticpath.domain.shoppingcart.impl.ShoppingItemImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.CustomerSessionRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.store.StoreRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.impl.ReactiveAdapterImpl;
import com.elasticpath.service.shoppingcart.WishListService;
import com.elasticpath.service.shoppingcart.impl.AddToWishlistResult;

/**
 * Test for {@link WishlistRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class WishlistRepositoryImplTest {

	private static final String SKU_GUID = "sku-guid";
	private static final String DIFFERENT_SKU_GUID = "different-sku-guid";
	private static final String GOOD_WL_GUID = "good-wl-guid";
	private static final String BAD_WL_GUID = "bad-wl-guid";
	private static final String SKU_CODE = "sku-code";
	private static final String STORE_CODE = "store-code";
	private static final String ITEM_ID = "item-id";

	@Mock
	private WishListService wishListService;

	@Mock
	private StoreRepository storeRepository;

	@Mock
	private Shopper mockShopper;

	@Mock
	private CustomerSessionRepository customerSessionRepository;

	@Mock
	private ItemRepository itemRepository;

	@Mock
	private ResourceOperationContext resourceOperationContext;

	@InjectMocks
	private ReactiveAdapterImpl reactiveAdapterImpl;

	private WishlistRepositoryImpl repository;

	@Before
	public void initialize() {
		repository = new WishlistRepositoryImpl(wishListService, customerSessionRepository, itemRepository, storeRepository,
				resourceOperationContext, reactiveAdapterImpl);
	}

	/**
	 * Test getting a favorites by id.
	 */
	@Test
	public void testGetWishlistSuccess() {
		WishList wishlist = createMockWishlist();
		when(wishListService.findByGuid(GOOD_WL_GUID)).thenReturn(wishlist);

		repository.getWishlist(GOOD_WL_GUID)
				.test()
				.assertNoErrors()
				.assertValueCount(1);
	}

	/**
	 * Test getting a favorites by id where the favorites doesn't exist.
	 */
	@Test
	public void testGetWishlistNotFound() {
		when(wishListService.findByGuid(BAD_WL_GUID)).thenReturn(null);

		repository.getWishlist(BAD_WL_GUID)
				.test()
				.assertNotComplete()
				.assertError(ResourceOperationFailure.class);
	}

	/**
	 * Test getting the favorites ids for a customer.
	 */
	@Test
	public void testGetWishlistIdsForCustomer() {
		WishList wishlist = createMockWishlist();
		CustomerSession customerSession = createMockSession();
		when(customerSessionRepository.findCustomerSessionByGuidAsSingle(any())).thenReturn(Single.just(customerSession));
		when(wishListService.findOrCreateWishListByShopper(mockShopper)).thenReturn(wishlist);
		when(wishListService.save(wishlist)).thenReturn(wishlist);

		repository.getWishlistIds("customer-id", STORE_CODE)
				.test()
				.assertNoErrors()
				.assertValueCount(1)
				.assertValue(GOOD_WL_GUID);
	}

	/**
	 * Test adding an item to a favorites.
	 */
	@Test
	public void testAddItemToWishList() {
		WishList wishlist = createMockWishlist();
		Store store = createMockStore();

		when(wishListService.findByGuid(GOOD_WL_GUID)).thenReturn(wishlist);
		when(storeRepository.findStoreAsSingle(STORE_CODE)).thenReturn(Single.just(store));
		when(wishListService.addProductSku(wishlist, store, SKU_CODE)).thenReturn(new AddToWishlistResult(new ShoppingItemImpl(), true));

		repository.addItemToWishlist(GOOD_WL_GUID, STORE_CODE, SKU_CODE)
				.test()
				.assertNoErrors();
	}

	@Test
	public void testRemoveAllItems() {
		ShoppingItem shoppingItem = createMockShoppingItem();
		WishList wishlist = createMockWishlist(shoppingItem);

		when(wishListService.findByGuid(GOOD_WL_GUID)).thenReturn(wishlist);
		when(wishListService.save(wishlist)).thenReturn(wishlist);

		repository.removeAllItemsFromWishlist(GOOD_WL_GUID)
				.test()
				.assertNoErrors();
	}


	/**
	 * Test to ensure that an empty favorites doesn't contain an item.
	 */
	@Test
	public void testFindWishlistsContainingItemWhenEmpty() {
		WishList wishlist = createMockWishlist();
		CustomerSession customerSession = createMockSession();
		ProductSku productSku = createMockProductSku();

		when(itemRepository.getSkuForItemIdAsSingle(ITEM_ID)).thenReturn(Single.just(productSku));
		when(customerSessionRepository.findOrCreateCustomerSessionAsSingle()).thenReturn(Single.just(customerSession));
		when(wishListService.findOrCreateWishListByShopper(mockShopper)).thenReturn(wishlist);
		when(wishListService.save(wishlist)).thenReturn(wishlist);

		repository.findWishlistsContainingItem(ITEM_ID)
				.test()
				.assertNoErrors()
				.assertValueCount(0);
	}

	/**
	 * Test to ensure that a favorites containing one item doesn't claim to contain a different item.
	 */
	@Test
	public void testFindWishlistsContainingDifferentItem() {
		ShoppingItem differentShoppingItem = mock(ShoppingItem.class);
		when(differentShoppingItem.getSkuGuid()).thenReturn(DIFFERENT_SKU_GUID);

		WishList wishlist = createMockWishlist(differentShoppingItem);
		CustomerSession customerSession = createMockSession();
		ProductSku productSku = createMockProductSku();

		when(itemRepository.getSkuForItemIdAsSingle(ITEM_ID)).thenReturn(Single.just(productSku));
		when(customerSessionRepository.findOrCreateCustomerSessionAsSingle()).thenReturn(Single.just(customerSession));
		when(wishListService.findOrCreateWishListByShopper(mockShopper)).thenReturn(wishlist);
		when(wishListService.save(wishlist)).thenReturn(wishlist);

		repository.findWishlistsContainingItem(ITEM_ID)
				.test()
				.assertNoErrors()
				.assertValueCount(0);
	}


	/**
	 * Test checking to see if a favorites contains an item.
	 */
	@Test
	public void testFindWishlistsContainingItem() {
		ShoppingItem shoppingItem = createMockShoppingItem();
		WishList wishlist = createMockWishlist(shoppingItem);
		CustomerSession customerSession = createMockSession();
		ProductSku productSku = createMockProductSku();

		when(itemRepository.getSkuForItemIdAsSingle(ITEM_ID)).thenReturn(Single.just(productSku));
		when(customerSessionRepository.findOrCreateCustomerSessionAsSingle()).thenReturn(Single.just(customerSession));
		when(wishListService.findOrCreateWishListByShopper(mockShopper)).thenReturn(wishlist);
		when(wishListService.save(wishlist)).thenReturn(wishlist);

		repository.findWishlistsContainingItem(ITEM_ID)
				.test()
				.assertNoErrors()
				.assertValueCount(1);
	}

	/**
	 * Test discoverable product.
	 */
	@Test
	public void testDiscoverableProduct() {
		ProductSku productSku = createMockProductSku(true, false);
		assertThat(WishlistRepositoryImpl.isProductDiscoverable(productSku)).isTrue();
	}

	/**
	 * Test Hidden product is not discoverable.
	 */
	@Test
	public void testHiddenProduct() {
		ProductSku productSku = createMockProductSku(true, true);
		assertThat(WishlistRepositoryImpl.isProductDiscoverable(productSku)).isFalse();
	}

	/**
	 * Test product not in date range is not discoverable.
	 */
	@Test
	public void testProductNotInDateRange() {
		ProductSku productSku = createMockProductSku(false, false);
		assertThat(WishlistRepositoryImpl.isProductDiscoverable(productSku)).isFalse();
	}

	/**
	 * Test finding shopping item in favorites.
	 */
	@Test
	public void findShoppingItemInWishlist() {
		String guid = "guid";
		ShoppingItem item = new ShoppingItemImpl();
		item.setGuid(guid);
		List<ShoppingItem> items = Lists.newArrayList(item);

		WishList wishlist = mock(WishList.class);

		when(wishlist.getAllItems()).thenReturn(items);

		repository.getShoppingItem(wishlist, guid)
				.test()
				.assertNoErrors()
				.assertValue(item);
	}

	/**
	 * Test finding shopping item not in favorites.
	 */
	@Test
	public void shoppingItemNotInWishlist() {
		WishList wishlist = createMockWishlist();

		repository.getShoppingItem(wishlist, "bad")
				.test()
				.assertError(ResourceOperationFailure.class);
	}

	private ProductSku createMockProductSku(final boolean isWithinRange, final boolean isHidden) {
		ProductSku productSku = mock(ProductSku.class, Answers.RETURNS_DEEP_STUBS.get());

		when(productSku.isWithinDateRange(any())).thenReturn(isWithinRange);
		when(productSku.getProduct().isHidden()).thenReturn(isHidden);
		when(productSku.getProduct().isWithinDateRange(any())).thenReturn(isWithinRange);

		return productSku;
	}

	private ProductSku createMockProductSku() {
		ProductSku productSku = mock(ProductSku.class);
		when(productSku.getGuid()).thenReturn(SKU_GUID);
		return productSku;
	}

	/**
	 * Creates a mock wish list with the good guid which, when asked, will return
	 * the given items.
	 *
	 * @param items the items to use in the mock list
	 * @return a lovely mock with the good guid and the given items
	 */
	private WishList createMockWishlist(final ShoppingItem... items) {
		WishList wishlist = mock(WishList.class);
		when(wishlist.getGuid()).thenReturn(GOOD_WL_GUID);
		when(wishlist.getAllItems()).thenReturn(new ArrayList<>(Arrays.asList(items)));
		return wishlist;
	}


	private CustomerSession createMockSession() {
		CustomerSession session = mock(CustomerSession.class);
		when(session.getShopper()).thenReturn(mockShopper);
		return session;
	}


	private Store createMockStore() {
		return mock(Store.class);
	}


	private ShoppingItem createMockShoppingItem() {
		ShoppingItem item = mock(ShoppingItem.class);
		when(item.getSkuGuid()).thenReturn(SKU_GUID);
		return item;
	}

}
