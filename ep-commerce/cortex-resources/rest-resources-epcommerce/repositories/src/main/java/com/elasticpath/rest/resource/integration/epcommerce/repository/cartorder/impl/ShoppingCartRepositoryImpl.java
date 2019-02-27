/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.impl;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.google.common.annotations.VisibleForTesting;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.common.dto.sellingchannel.ShoppingItemDtoFactory;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingCartTaxSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.WishList;
import com.elasticpath.rest.cache.CacheRemove;
import com.elasticpath.rest.cache.CacheResult;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartPostProcessor;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.ShoppingCartRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.CustomerSessionRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.sku.ProductSkuRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.ReactiveAdapter;
import com.elasticpath.sellingchannel.director.CartDirectorService;
import com.elasticpath.service.shoppingcart.ShoppingCartService;
import com.elasticpath.service.shoppingcart.impl.AddToWishlistResult;

/**
 * The facade for shopping cart related operations.
 */
@Singleton
@Named("shoppingCartRepository")
public class ShoppingCartRepositoryImpl implements ShoppingCartRepository {

	/**
	 * Error message used when default cart can't be found.
	 */
	@VisibleForTesting
	public static final String DEFAULT_CART_NOT_FOUND = "Default cart cannot be found.";

	/**
	 * Error message used when line item was not found.
	 */
	public static final String LINEITEM_WAS_NOT_FOUND = "No line item was found with GUID = %s.";

	private final ShoppingCartService shoppingCartService;
	private final CartDirectorService cartDirectorService;
	private final CustomerSessionRepository customerSessionRepository;
	private final ShoppingItemDtoFactory shoppingItemDtoFactory;
	private final CartPostProcessor cartPostProcessor;
	private final ReactiveAdapter reactiveAdapter;
	private final ProductSkuRepository productSkuRepository;

	/**
	 * Constructor.
	 *
	 * @param shoppingCartService       the shoppingCartService
	 * @param cartDirectorService       the cart director service
	 * @param customerSessionRepository the customer session repo
	 * @param shoppingItemDtoFactory    the shopping item dto factory
	 * @param cartPostProcessor         the {@link CartPostProcessor}
	 * @param reactiveAdapter           the reactive adapter
	 * @param productSkuRepository      the product sku repository
	 */
	@Inject
	@SuppressWarnings("parameternumber")
	public ShoppingCartRepositoryImpl(
			@Named("shoppingCartService") final ShoppingCartService shoppingCartService,
			@Named("cartDirectorService") final CartDirectorService cartDirectorService,
			@Named("customerSessionRepository") final CustomerSessionRepository customerSessionRepository,
			@Named("shoppingItemDtoFactory") final ShoppingItemDtoFactory shoppingItemDtoFactory,
			@Named("cartPostProcessor") final CartPostProcessor cartPostProcessor,
			@Named("reactiveAdapter") final ReactiveAdapter reactiveAdapter,
			@Named("productSkuRepository") final ProductSkuRepository productSkuRepository) {

		this.shoppingCartService = shoppingCartService;
		this.cartDirectorService = cartDirectorService;
		this.customerSessionRepository = customerSessionRepository;
		this.shoppingItemDtoFactory = shoppingItemDtoFactory;
		this.cartPostProcessor = cartPostProcessor;
		this.reactiveAdapter = reactiveAdapter;
		this.productSkuRepository = productSkuRepository;
	}


	@Override
	public Single<String> getDefaultShoppingCartGuid() {
		return customerSessionRepository.findOrCreateCustomerSessionAsSingle()
				.flatMap(this::getDefaultCartGuid);
	}

	@Override
	public Single<ShoppingCart> getDefaultShoppingCart() {
		return customerSessionRepository.findOrCreateCustomerSessionAsSingle()
				.flatMap(this::getDefaultCart);
	}

	@Override
	public Single<ShoppingCart> getShoppingCartForCustomer(final String customerGuid) {
		return customerSessionRepository.findCustomerSessionByGuidAsSingle(customerGuid)
				.flatMap(this::getDefaultCart);
	}

	@Override
	public Single<ShoppingCart> getShoppingCart(final String cartGuid) {
		return getShoppingCartSingle(cartGuid);
	}

	@CacheResult
	private Single<ShoppingCart> getShoppingCartSingle(final String cartGuid) {
		return customerSessionRepository.findOrCreateCustomerSessionAsSingle()
		.flatMap(customerSession -> getCartByGuid(cartGuid).flatMap(cart -> reactiveAdapter.fromServiceAsSingle(() -> {
			cartPostProcessor.postProcessCart(cart, cart.getShopper(), customerSession);
			return cart;
		})));
	}


	@Override
	@CacheResult(uniqueIdentifier = "verifyShoppingCartExistsForStore")
	public Single<Boolean> verifyShoppingCartExistsForStore(final String cartGuid, final String storeCode) {
		return reactiveAdapter.fromServiceAsSingle(() -> shoppingCartService.shoppingCartExistsForStore(cartGuid, storeCode));
	}

	@CacheResult
	private ShoppingCart getCartForCustomerSession(final CustomerSession customerSession) {
		final ShoppingCart cart = shoppingCartService.findOrCreateByCustomerSession(customerSession);
		final ShoppingCart savedCart = shoppingCartService.saveIfNotPersisted(cart);
		cartPostProcessor.postProcessCart(savedCart, customerSession.getShopper(), customerSession);
		return savedCart;
	}

	private Single<ShoppingCart> getCartByGuid(final String cartGuid) {
		return reactiveAdapter.fromServiceAsSingle(() -> shoppingCartService.findByGuid(cartGuid));
	}

	private Single<ShoppingCart> getDefaultCart(final CustomerSession customerSession) {
		return reactiveAdapter.fromServiceAsSingle(() -> getCartForCustomerSession(customerSession), DEFAULT_CART_NOT_FOUND);
	}

	@CacheResult
	private Single<String> getDefaultCartGuid(final CustomerSession customerSession) {
		return reactiveAdapter.fromServiceAsSingle(()
				-> shoppingCartService.findDefaultShoppingCartGuidByShopper(customerSession.getShopper()));
	}

	@CacheResult(uniqueIdentifier = "getStoreForCartGuid")
	private Single<String>  getStoreForCartGuid(final String cartGuid) {
		return reactiveAdapter.fromServiceAsSingle(()
				-> shoppingCartService.findStoreCodeByCartGuid(cartGuid), "STORE NOT FOUND");
	}


	@Override
	@CacheResult
	public Observable<String> findAllCarts(final String customerGuid, final String storeCode) {
		final String storeCodeUpperCase = storeCode.toUpperCase(Locale.getDefault());
		return Observable.fromIterable(shoppingCartService.findByCustomerAndStore(customerGuid, storeCodeUpperCase));
	}

	@Override
	public Single<String> findStoreForCartGuid(final String cartGuid) {
		return getStoreForCartGuid(cartGuid);
	}



	@Override
	@CacheRemove(typesToInvalidate = {ShoppingCart.class, ShoppingCartPricingSnapshot.class, ShoppingCartTaxSnapshot.class})
	public Single<ShoppingItem> addItemToCart(final ShoppingCart cart, final String skuCode, final int quantity, final Map<String, String> fields) {
		ShoppingItemDto shoppingItemDto = shoppingItemDtoFactory.createDto(skuCode, quantity);
		shoppingItemDto.setItemFields(fields);

		return reactiveAdapter.fromServiceAsSingle(() -> cartDirectorService.addItemToCart(cart, shoppingItemDto));
	}

	@Override
	@CacheRemove(typesToInvalidate = {ShoppingCart.class, WishList.class, ShoppingCartPricingSnapshot.class, ShoppingCartTaxSnapshot.class})
	public Single<ShoppingItem> moveItemToCart(final ShoppingCart cart, final String wishlistLineItemGuid, final String skuCode,
											   final int quantity, final Map<String, String> fields) {
		ShoppingItemDto dto = shoppingItemDtoFactory.createDto(skuCode, quantity);
		dto.setItemFields(fields);
		return reactiveAdapter.fromServiceAsSingle(() -> cartDirectorService.moveItemFromWishListToCart(cart, dto, wishlistLineItemGuid));
	}

	@Override
	public Single<AddToWishlistResult> moveItemToWishlist(final ShoppingCart cart, final String cartLineItemGuid) {
		return reactiveAdapter.fromServiceAsSingle(() -> cartDirectorService.moveItemFromCartToWishList(cart, cartLineItemGuid));
	}

	@Override
	@CacheResult
	public Single<ProductSku> getProductSku(final String cartId, final String lineItemId) {
		return getShoppingCart(cartId)
				.flatMap(cart -> getShoppingItem(lineItemId, cart))
				.flatMap(shoppingItem -> productSkuRepository.getProductSkuWithAttributesByGuidAsSingle(shoppingItem.getSkuGuid()));
	}

	@Override
	@CacheResult
	public Single<ShoppingItem> getShoppingItem(final String lineItemId, final ShoppingCart cart) {
		return reactiveAdapter.fromNullableAsSingle(() -> cart.getCartItemByGuid(lineItemId), String.format(LINEITEM_WAS_NOT_FOUND, lineItemId));
	}

	@Override
	@CacheRemove(typesToInvalidate = {ShoppingCart.class, ShoppingCartPricingSnapshot.class, ShoppingCartTaxSnapshot.class})
	public Completable updateCartItem(final ShoppingCart cart, final ShoppingItem shoppingItem, final String skuCode, final int quantity) {
		Map<String, String> itemFields = Optional.ofNullable(shoppingItem.getFields())
				.orElse(Collections.emptyMap());

		ShoppingItemDto shoppingItemDto = shoppingItemDtoFactory.createDto(skuCode, quantity);
		shoppingItemDto.setItemFields(itemFields);

		return reactiveAdapter.fromServiceAsCompletable(() -> cartDirectorService.updateCartItem(cart, shoppingItem.getUidPk(),
				shoppingItemDto));
	}

	@Override
	@CacheRemove(typesToInvalidate = {ShoppingCart.class, ShoppingCartPricingSnapshot.class, ShoppingCartTaxSnapshot.class})
	public Completable removeItemFromCart(final ShoppingCart cart, final String shoppingItemGuid) {
		//LOG.warn("remove Item from Cart");
		return reactiveAdapter.fromServiceAsCompletable(() -> cartDirectorService.removeItemsFromCart(cart, shoppingItemGuid));
	}

	@Override
	@CacheRemove(typesToInvalidate = {ShoppingCart.class, ShoppingCartPricingSnapshot.class, ShoppingCartTaxSnapshot.class})
	public Completable removeAllItemsFromDefaultCart() {
		return getDefaultShoppingCart()
				.flatMapCompletable(cart -> reactiveAdapter.fromServiceAsCompletable(() -> cartDirectorService.clearItems(cart)));
	}

	@Override
	@CacheRemove(typesToInvalidate = {ShoppingCart.class, ShoppingCartPricingSnapshot.class, ShoppingCartTaxSnapshot.class})
	public Completable removeAllItemsFromCart(final ShoppingCart cart) {
		return reactiveAdapter.fromServiceAsCompletable(() -> cartDirectorService.clearItems(cart));
	}

	@Override
	public void reApplyCatalogPromotions(final ShoppingCart cart) {
		cartDirectorService.reApplyCatalogPromotions(cart);
	}
}
