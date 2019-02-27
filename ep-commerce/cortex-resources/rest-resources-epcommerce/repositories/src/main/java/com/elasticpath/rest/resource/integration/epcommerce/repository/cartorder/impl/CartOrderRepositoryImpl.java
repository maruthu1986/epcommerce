/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.google.common.annotations.VisibleForTesting;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingCartTaxSnapshot;
import com.elasticpath.domain.store.Store;
import com.elasticpath.rest.cache.CacheRemove;
import com.elasticpath.rest.cache.CacheResult;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.Ensure;
import com.elasticpath.rest.chain.ExecutionResultChain;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.ShoppingCartRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipmentdetails.ShipmentDetailsConstants;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.ReactiveAdapter;
import com.elasticpath.service.cartorder.CartOrderCouponService;
import com.elasticpath.service.cartorder.CartOrderService;
import com.elasticpath.service.cartorder.CartOrderShippingService;
import com.elasticpath.service.rules.CartOrderCouponAutoApplier;

/**
 * The facade for {@link CartOrder} related operations.
 */
@Singleton
@Named("cartOrderRepository")
@SuppressWarnings("PMD.TooManyMethods")
public class CartOrderRepositoryImpl implements CartOrderRepository {

	private static final Logger LOG = LoggerFactory.getLogger(CartOrderRepositoryImpl.class);

	/**
	 * Error message when cart order not found.
	 */
	public static final String ORDER_WITH_GUID_NOT_FOUND = "No cart order with GUID %s was found in store %s.";
	private static final String ORDER_WITH_CART_GUID_NOT_FOUND = "No cart order with cart GUID %s was found.";

	/**
	 * Error message when cart orders not found for customer.
	 */
	@VisibleForTesting
	public static final String NO_CART_ORDERS_FOR_CUSTOMER = "No cart orders for customer with GUID %s were found in store %s.";

	private final CartOrderService coreCartOrderService;
	private final CartOrderCouponService coreCartOrderCouponService;
	private final ShoppingCartRepository shoppingCartRepository;
	private final CartOrderShippingService coreCartOrderShippingService;
	private final CartOrderCouponAutoApplier cartOrderCouponAutoApplier;
	private final ReactiveAdapter reactiveAdapter;

	/**
	 * Constructor.
	 *
	 * @param coreCartOrderService         the core cart order service
	 * @param shoppingCartRepository       the shopping cart repository
	 * @param coreCartOrderShippingService the core cart order shipping service
	 * @param coreCartOrderCouponService   the core cart order coupon service
	 * @param cartOrderCouponAutoApplier   cart order coupon auto applier
	 * @param reactiveAdapter              reactiveAdapter
	 */
	@Inject
	public CartOrderRepositoryImpl(
			@Named("cartOrderService") final CartOrderService coreCartOrderService,
			@Named("shoppingCartRepository") final ShoppingCartRepository shoppingCartRepository,
			@Named("cartOrderShippingService") final CartOrderShippingService coreCartOrderShippingService,
			@Named("cartOrderCouponService") final CartOrderCouponService coreCartOrderCouponService,
			@Named("cartOrderCouponAutoApplier") final CartOrderCouponAutoApplier cartOrderCouponAutoApplier,
			@Named("reactiveAdapter") final ReactiveAdapter reactiveAdapter) {

		this.coreCartOrderService = coreCartOrderService;
		this.shoppingCartRepository = shoppingCartRepository;
		this.coreCartOrderShippingService = coreCartOrderShippingService;
		this.coreCartOrderCouponService = coreCartOrderCouponService;
		this.cartOrderCouponAutoApplier = cartOrderCouponAutoApplier;
		this.reactiveAdapter = reactiveAdapter;
	}

	@Override
	public ExecutionResult<CartOrder> findByGuid(final String storeCode, final String cartOrderGuid) {
		return new ExecutionResultChain() {
			public ExecutionResult<?> build() {
				CartOrder cartOrder = Assign.ifNotNull(findByStoreCodeAndGuid(storeCode, cartOrderGuid),
						OnFailure.returnNotFound(ORDER_WITH_GUID_NOT_FOUND, cartOrderGuid, storeCode));

				return ExecutionResultFactory.createReadOK(cartOrder);
			}
		}.execute();
	}

	@Override
	public Single<CartOrder> findByGuidAsSingle(final String storeCode, final String cartOrderGuid) {
		return reactiveAdapter.fromServiceAsSingle(() -> findByStoreCodeAndGuid(storeCode, cartOrderGuid),
				String.format(ORDER_WITH_GUID_NOT_FOUND, cartOrderGuid, storeCode));
	}

	@CacheResult
	private CartOrder findByStoreCodeAndGuid(final String storeCode, final String cartOrderGuid) {
		return coreCartOrderService.findByStoreCodeAndGuid(storeCode, cartOrderGuid);
	}

	@Override
	public ExecutionResult<CartOrder> findByCartGuid(final String cartGuid) {
		return new ExecutionResultChain() {
			public ExecutionResult<?> build() {
				CartOrder cartOrder = Assign.ifNotNull(findByShoppingCartGuid(cartGuid),
						OnFailure.returnNotFound(ORDER_WITH_CART_GUID_NOT_FOUND, cartGuid));
				return ExecutionResultFactory.createReadOK(cartOrder);
			}
		}.execute();
	}

	@CacheResult
	private CartOrder findByShoppingCartGuid(final String shoppingCartGuid) {
		return coreCartOrderService.findByShoppingCartGuid(shoppingCartGuid);
	}

	@Override
	@CacheResult
	public Single<CartOrder> findByCartGuidSingle(final String cartGuid) {
		return reactiveAdapter.fromServiceAsSingle(() -> coreCartOrderService.findByShoppingCartGuid(cartGuid),
				String.format(ORDER_WITH_CART_GUID_NOT_FOUND, cartGuid));
	}

	@Override
	@CacheResult(uniqueIdentifier = "findByShipmentDetailsId")
	public Single<CartOrder> findByShipmentDetailsId(final String storeCode, final Map<String, String> shipmentDetailsId) {
		String cartId = shipmentDetailsId.get(ShipmentDetailsConstants.ORDER_ID);
		return reactiveAdapter.fromServiceAsSingle(() -> coreCartOrderService
				.findByStoreCodeAndGuid(storeCode, cartId), String.format(ORDER_WITH_GUID_NOT_FOUND, cartId, storeCode));
	}

	@Override
	public ExecutionResult<Collection<String>> findCartOrderGuidsByCustomer(final String storeCode, final String customerGuid) {
		return new ExecutionResultChain() {
			public ExecutionResult<?> build() {
				Collection<String> cartOrderGuids = Assign.ifNotNull(findCartOrderGuidsByCustomerGuid(storeCode, customerGuid),
						OnFailure.returnNotFound(NO_CART_ORDERS_FOR_CUSTOMER, customerGuid, storeCode));
				return ExecutionResultFactory.createReadOK(cartOrderGuids);
			}
		}.execute();
	}

	@Override
	public Observable<String> findCartOrderGuidsByCustomerAsObservable(final String storeCode, final String customerGuid) {
		return reactiveAdapter.fromService(() -> findCartOrderGuidsByCustomerGuid(storeCode, customerGuid),
				String.format(NO_CART_ORDERS_FOR_CUSTOMER, customerGuid, storeCode))
				.flatMap(Observable::fromIterable);
	}

	@CacheResult
	private List<String> findCartOrderGuidsByCustomerGuid(final String storeCode, final String customerGuid) {
		return coreCartOrderService.findCartOrderGuidsByCustomerGuid(storeCode, customerGuid);
	}

	@Override
	@CacheResult(uniqueIdentifier = "billingAddress")
	public Maybe<Address> getBillingAddress(final CartOrder cartOrder) {
		return reactiveAdapter.fromServiceAsMaybe(() -> coreCartOrderService.getBillingAddress(cartOrder), Maybe.empty());
	}

	@CacheResult
	private Address getShippingAddressForCartOrder(final CartOrder cartOrder) {
		return coreCartOrderService.getShippingAddress(cartOrder);
	}

	@Override
	public Maybe<Address> getShippingAddress(final CartOrder cartOrder) {
		return reactiveAdapter.fromServiceAsMaybe(() -> getShippingAddressForCartOrder(cartOrder), Maybe.empty());
	}

	@Override
	@CacheRemove(typesToInvalidate = {CartOrder.class, Address.class, ShoppingCart.class,
			ShoppingCartPricingSnapshot.class, ShoppingCartTaxSnapshot.class})
	public ExecutionResult<CartOrder> saveCartOrder(final CartOrder cartOrder) {
		//cannot be final
		ExecutionResult<CartOrder> result;
		try {
			result = ExecutionResultFactory.createCreateOKWithData(coreCartOrderService.saveOrUpdate(cartOrder), true);
		} catch (Exception e) {
			result = ExecutionResultFactory.createServerError("Unable to save cart order");
			LOG.error("Unable to save cart order " + cartOrder, e);
		}
		return result;
	}

	@Override
	@CacheRemove(typesToInvalidate = {CartOrder.class, Address.class, ShoppingCart.class,
			ShoppingCartPricingSnapshot.class, ShoppingCartTaxSnapshot.class})
	public Single<CartOrder> saveCartOrderAsSingle(final CartOrder cartOrder) {
		return reactiveAdapter.fromServiceAsSingle(() -> coreCartOrderService.saveOrUpdate(cartOrder), "Unable to save cart order");
	}

	@Override
	public ExecutionResult<CartOrder> getCartOrder(final String storeCode, final String guid, final FindCartOrder findBy) {

		CartOrder cartOrder;
		switch (findBy) {
			case BY_CART_GUID:
				cartOrder = Assign.ifSuccessful(findByCartGuid(guid));
				break;
			case BY_ORDER_GUID:
				cartOrder = Assign.ifSuccessful(findByGuid(storeCode, guid));
				break;
			default:
				return ExecutionResultFactory.createServerError("Invalid FindCartOrder criteria: " + findBy);
		}

		return ExecutionResultFactory.createReadOK(cartOrder);
	}

	@Override
	public ExecutionResult<ShoppingCart> getEnrichedShoppingCart(final String storeCode, final String guid, final FindCartOrder findBy) {
			return getEnrichedShoppingCartSingle(storeCode, guid, findBy)
						.map(this::createExecutionResult)
						.onErrorReturnItem(ExecutionResultFactory.createNotFound())
						.blockingGet();

	}

	private ExecutionResult<ShoppingCart> createExecutionResult(final ShoppingCart enrichedShoppingCartResult) {
		return ExecutionResultFactory.createReadOK(enrichedShoppingCartResult);
	}

	@Override
	@CacheResult(uniqueIdentifier = "enrichedCartByGuid")
	public Single<ShoppingCart> getEnrichedShoppingCartSingle(final String storeCode, final String guid, final FindCartOrder findBy) {
		return reactiveAdapter.fromRepositoryAsSingle(() -> getCartOrder(storeCode, guid, findBy))
				.flatMap(cartOrder -> getEnrichedShoppingCartSingle(storeCode, cartOrder));
	}

	@Override
	@CacheResult(uniqueIdentifier = "enrichedCartForShipments")
	public Single<ShoppingCart> getEnrichedShoppingCartForShipments(final String storeCode, final Map<String, String> shipmentDetailsId) {
		return findByShipmentDetailsId(storeCode, shipmentDetailsId)
				.flatMap(cartOrder -> getEnrichedShoppingCartSingle(storeCode, cartOrder));
	}

	@Override
	@CacheResult(uniqueIdentifier = "enrichedCart")
	public Single<ShoppingCart> getEnrichedShoppingCartSingle(final String storeCode, final CartOrder cartOrder) {
		return shoppingCartRepository.getShoppingCart(cartOrder.getShoppingCartGuid())
				// update the coupon codes on the shopping cart.
				.map(shoppingCart -> coreCartOrderCouponService.populateCouponCodesOnShoppingCart(shoppingCart, cartOrder))
				// Update shipping information on the cart as shipping info is transient.
				.map(enrichedShoppingCart -> coreCartOrderShippingService.populateAddressAndShippingFields(enrichedShoppingCart, cartOrder));
	}

	@Override
	@CacheRemove(typesToInvalidate = {Address.class})
	public ExecutionResult<Boolean> updateShippingAddressOnCartOrder(final String shippingAddressGuid, final String cartOrderGuid,
																	 final String storeCode) {

		return new ExecutionResultChain() {
			public ExecutionResult<?> build() {
				CartOrder cartOrder = Assign.ifSuccessful(findByGuid(storeCode, cartOrderGuid));
				final ShoppingCart shoppingCart = shoppingCartRepository.getShoppingCart(cartOrder.getShoppingCartGuid()).blockingGet();
				boolean updatedAddress = coreCartOrderShippingService.updateCartOrderShippingAddress(shippingAddressGuid, shoppingCart, cartOrder);
				if (updatedAddress) {
					Ensure.successful(saveCartOrder(cartOrder));
				}
				return ExecutionResultFactory.createReadOK(updatedAddress);
			}
		}.execute();
	}

	@Override
	@CacheRemove(typesToInvalidate = {Address.class})
	public Single<Boolean> updateShippingAddressOnCartOrderAsSingle(final String shippingAddressGuid, final String cartOrderGuid,
																	final String storeCode) {
		return findByGuidAsSingle(storeCode, cartOrderGuid)
				.flatMap(cartOrder -> updateCartOrderShippingAddress(shippingAddressGuid, cartOrder));
	}


	private Single<Boolean> updateCartOrderShippingAddress(final String shippingAddressGuid, final CartOrder cartOrder) {
		return  shoppingCartRepository.getShoppingCart(cartOrder.getShoppingCartGuid())
				.map(cart -> coreCartOrderShippingService.updateCartOrderShippingAddress(shippingAddressGuid, cart, cartOrder))
				.flatMap(isAddressUpdated -> saveCartOrderIfAddressUpdated(cartOrder, isAddressUpdated));
	}

	private Single<Boolean> saveCartOrderIfAddressUpdated(final CartOrder cartOrder, final Boolean isAddressUpdated) {
		if (isAddressUpdated) {
			return saveCartOrderAsSingle(cartOrder)
					.map(savedCartOrder -> true);
		}
		return Single.just(false);
	}

	@Override
	@CacheRemove(typesToInvalidate = {CartOrder.class, ShoppingCartPricingSnapshot.class, ShoppingCartTaxSnapshot.class})
	public ExecutionResult<Boolean> filterAndAutoApplyCoupons(final CartOrder cartOrder, final Store store, final String customerEmailAddress) {
		return new ExecutionResultChain() {
			public ExecutionResult<?> build() {
				boolean isCouponsApplied = Assign.ifNotNull(
						cartOrderCouponAutoApplier.filterAndAutoApplyCoupons(cartOrder, store, customerEmailAddress),
						OnFailure.returnServerError("Server error when auto applying coupons to cart order"));
				return ExecutionResultFactory.createReadOK(isCouponsApplied);
			}
		}.execute();
	}

	@Override
	@CacheResult
	public String getShoppingCartGuid(final String storeCode, final String cartOrderGuid) {
		return coreCartOrderService.getShoppingCartGuid(storeCode, cartOrderGuid);
	}
}
