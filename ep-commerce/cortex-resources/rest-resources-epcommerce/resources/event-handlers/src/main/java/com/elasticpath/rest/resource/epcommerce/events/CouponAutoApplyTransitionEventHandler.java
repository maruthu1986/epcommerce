/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.epcommerce.events;

import io.reactivex.Completable;
import io.reactivex.Single;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.store.Store;
import com.elasticpath.rest.relos.rs.events.RoleTransitionEvent;
import com.elasticpath.rest.relos.rs.events.ScopedEventEntityHandler;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.ShoppingCartRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.store.StoreRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.ReactiveAdapter;

/**
 * Automatically applies coupons to new customer.
 */
@Component(property = {
		Constants.SERVICE_RANKING + ":Integer=50",
		"eventType=" + RoleTransitionEvent.EVENT_TYPE })
public class CouponAutoApplyTransitionEventHandler implements ScopedEventEntityHandler<RoleTransitionEvent> {

	private static final Logger LOG = LoggerFactory.getLogger(CouponAutoApplyTransitionEventHandler.class);

	@Reference
	private StoreRepository storeRepository;
	@Reference
	private CartOrderRepository cartOrderRepository;
	@Reference
	private ShoppingCartRepository shoppingCartRepository;
	@Reference
	private ReactiveAdapter reactiveAdapter;


	@Override
	public void handleEvent(final String scope, final RoleTransitionEvent event) {
		String customerGuid = event.getNewUserGuid();
		filterAndAutoApplyCoupons(scope, customerGuid)
				.subscribe(
						() -> {
							//do nothing
						},
						throwable -> LOG.error("Error auto apply coupons to cart order: {}", throwable.getMessage())
				);
	}


	private Completable filterAndAutoApplyCoupons(final String storeCode, final String customerGuid) {
		/**
		 * Thread Local still doesn't have any resource operations present at this point in time.
		 * Hence it is not possible to get current Session for the user.
		 * Do not use {@link ShoppingCartRepository#getDefaultShoppingCart()} because of this reason.
		 * You must pass customer guid explicitly but not reach Resource Operation Context for it.
		 */
		return shoppingCartRepository.getShoppingCartForCustomer(customerGuid)
				.flatMapCompletable(shoppingCart -> getCartOrder(shoppingCart)
						.flatMapCompletable(cartOrder -> {
							String customerEmailAddress = shoppingCart.getShopper().getCustomer().getEmail();
							return storeRepository.findStoreAsSingle(storeCode)
									.flatMapCompletable(store -> filterAndAutoApplyCoupons(cartOrder, customerEmailAddress, store)
											.flatMap(isCartOrderUpdated -> saveCartOrder(cartOrder, isCartOrderUpdated))
											.toCompletable());
						}));
	}

	private Single<CartOrder> getCartOrder(final ShoppingCart shoppingCart) {
		return cartOrderRepository.findByCartGuidSingle(shoppingCart.getGuid());
	}

	private Single<Boolean> filterAndAutoApplyCoupons(final CartOrder cartOrder, final String customerEmailAddress, final Store store) {
		return reactiveAdapter.fromRepositoryAsSingle(() -> cartOrderRepository.filterAndAutoApplyCoupons(cartOrder, store, customerEmailAddress));
	}

	private Single<CartOrder> saveCartOrder(final CartOrder cartOrder, final Boolean isCartOrderUpdated) {
		return isCartOrderUpdated ? cartOrderRepository.saveCartOrderAsSingle(cartOrder) : Single.never();
	}

}
