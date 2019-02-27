/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.fulfillment;

import org.apache.commons.collections.CollectionUtils;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.common.pricing.service.PriceLookupFacade;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.customer.CustomerSessionService;
import com.elasticpath.service.shopper.ShopperService;
import com.elasticpath.service.shoppingcart.ShoppingCartService;

/**
 * Helper Class for ProductSkus.
 */
public class ProductSkuChecker {

	private final CustomerSession customerSession;
	private final PriceLookupFacade priceLookupFacade;
	private final Store store;

	/**
	 * Constructor.
	 * 
	 * @param order The ProductSkus to be checked must belong to this Order.
	 */
	public ProductSkuChecker(final Order order) {
		store = order.getStore();
		priceLookupFacade = ServiceLocator.getService(ContextIdNames.PRICE_LOOKUP_FACADE);

		final Customer customer = order.getCustomer();

		final ShopperService shopperService = ServiceLocator.getService(ContextIdNames.SHOPPER_SERVICE);
		Shopper shopper = shopperService.findOrCreateShopper(customer, store.getCode());
		
		final CustomerSessionService customerSessionService = ServiceLocator.getService(ContextIdNames.CUSTOMER_SESSION_SERVICE);
		CustomerSession customerSessionWithShopper = customerSessionService.createWithShopper(shopper);
		customerSession = customerSessionService.initializeCustomerSessionForPricing(customerSessionWithShopper, 
				store.getCode(), order.getCurrency());

		final ShoppingCartService shoppingCartService = ServiceLocator.getService(ContextIdNames.SHOPPING_CART_SERVICE);
		final ShoppingCart shoppingCart = shoppingCartService.findOrCreateByShopper(customerSession.getShopper());
		customerSession.getShopper().setCurrentShoppingCart(shoppingCart);
	}

	/**
	 * Return true if the given ProductSku has a recurring pricing scheme.
	 * 
	 * @param sku The ProductSku to test.
	 * @return True if the given ProductSku has a recurring pricing scheme.
	 */
	public boolean isRecurringSku(final ProductSku sku) {
		final Price price = getPriceLookupFacade().getPromotedPriceForSku(sku, getStore(), getCustomerSession().getShopper());
		if (price != null && price.getPricingScheme() != null) {
			return CollectionUtils.isNotEmpty(price.getPricingScheme().getRecurringSchedules());
		}
		return false;
	}

	private CustomerSession getCustomerSession() {
		return customerSession;
	}

	private PriceLookupFacade getPriceLookupFacade() {
		return priceLookupFacade;
	}

	private Store getStore() {
		return store;
	}

}
