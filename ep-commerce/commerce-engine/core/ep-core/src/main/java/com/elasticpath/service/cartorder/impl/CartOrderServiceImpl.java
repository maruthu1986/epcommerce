/*
 * Copyright (c) Elastic Path Software Inc., 2011.
 */
package com.elasticpath.service.cartorder.impl;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.service.cartorder.CartOrderPopulationStrategy;
import com.elasticpath.service.cartorder.CartOrderService;
import com.elasticpath.service.cartorder.CartOrderShippingInformationSanitizer;
import com.elasticpath.service.cartorder.dao.CartOrderDao;
import com.elasticpath.service.customer.dao.CustomerAddressDao;
import com.elasticpath.service.shoppingcart.ShoppingCartService;

/**
 * Implementation of CartOrderService, CartOrder should not be used in versions of EP prior to 6.4.
 */
public class CartOrderServiceImpl implements CartOrderService {
	private CustomerAddressDao addressDao;
	private CartOrderDao cartOrderDao;
	private ShoppingCartService shoppingCartService;
	private CartOrderPopulationStrategy cartOrderPopulationStrategy;
	private CartOrderShippingInformationSanitizer cartOrderShippingInformationSanitizer;
	private PersistenceEngine persistenceEngine;

	protected CartOrderPopulationStrategy getCartOrderPopulationStrategy() {
		return cartOrderPopulationStrategy;
	}

	public void setCartOrderPopulationStrategy(
			final CartOrderPopulationStrategy cartOrderPopulationStrategy) {
		this.cartOrderPopulationStrategy = cartOrderPopulationStrategy;
	}

	@Override
	public CartOrder findByStoreCodeAndGuid(final String storeCode, final String guid) {

		CartOrder cartOrder;

		List<CartOrder> result = getPersistenceEngine().retrieveByNamedQuery("ACTIVE_CART_ORDER_BY_STORECODE_AND_GUID", storeCode, guid);
		if (result.isEmpty()) {
			cartOrder = null;
		} else if (result.size() == 1) {
			cartOrder = result.get(0);
		} else {
			throw new EpServiceException("Inconsistent data -- duplicate GUIDs exist -- " + guid);
		}

		return postProcessing(cartOrder);
	}

	/**
	 * Hook to perform additional processing on a {@link CartOrder} before usage.
	 *
	 * @param cartOrder the cartOrder to be processed
	 * @return the processed CartOrder
	 */
	private CartOrder postProcessing(final CartOrder cartOrder) {
		if (cartOrder == null) {
			return null;
		}

		final boolean cartOrderWasUpdated = getCartOrderShippingInformationSanitizer().sanitize(cartOrder);
		if (cartOrderWasUpdated) {
			return cartOrderDao.saveOrUpdate(cartOrder);
		}

		return cartOrder;
	}

	@Override
	public Address getBillingAddress(final CartOrder cartOrder) {
		return addressDao.findByGuid(cartOrder.getBillingAddressGuid());
	}

	@Override
	public Address getShippingAddress(final CartOrder cartOrder) {
		return addressDao.findByGuid(cartOrder.getShippingAddressGuid());
	}

	@Override
	public CartOrder findByShoppingCartGuid(final String guid) {
		return cartOrderDao.findByShoppingCartGuid(guid);
	}

	/**
	 * @return The ShoppingCartService.
	 */
	protected ShoppingCartService getShoppingCartService() {
		return shoppingCartService;
	}

	@Override
	public void remove(final CartOrder cartOrder) {
		cartOrderDao.remove(cartOrder);
	}

	@Override
	public CartOrder saveOrUpdate(final CartOrder cartOrder) {
		touchShoppingCart(cartOrder);
		return cartOrderDao.saveOrUpdate(cartOrder);
	}

	@Override
	public boolean createOrderIfPossible(final ShoppingCart shoppingCart) {
		final boolean notExists = getCartOrderGuidByShoppingCartGuid(shoppingCart.getGuid()) == null;
		if (notExists) {
			final CartOrder cartOrder = cartOrderPopulationStrategy.createCartOrder(shoppingCart);
			saveOrUpdate(cartOrder);
		}
		return notExists;
	}

	/**
	 * @param cartOrderDao The CartOrderDao to set.
	 */
	public void setCartOrderDao(final CartOrderDao cartOrderDao) {
		this.cartOrderDao = cartOrderDao;
	}

	/**
	 * @param addressDao The CustomerAddressDao to set.
	 */
	public void setCustomerAddressDao(final CustomerAddressDao addressDao) {
		this.addressDao = addressDao;
	}

	/**
	 * @param shoppingCartService The ShoppingCartService to set.
	 */
	public void setShoppingCartService(final ShoppingCartService shoppingCartService) {
		this.shoppingCartService = shoppingCartService;
	}

	@Override
	public void removeIfExistsByShoppingCart(final ShoppingCart shoppingCart) {
		cartOrderDao.removeByShoppingCartGuid(shoppingCart.getGuid());
	}

	/**
	 * When a CartOrder is changed the ShoppingCart should also be updated and therefore both the
	 * CartOrder and ShoppingCart are not considered abandoned after 60 days.
	 * This method exists only because CartOrder and ShoppingCart are not integrated yet.
	 *
	 * @param cartOrder The CartOrder to use to lookup the ShoppingCart.
	 */
	private void touchShoppingCart(final CartOrder cartOrder) {
		if (cartOrder != null && cartOrder.getShoppingCartGuid() != null) {
			getShoppingCartService().touch(cartOrder.getShoppingCartGuid());
		}
	}

	@Override
	public int removeIfExistsByShoppingCartGuids(final List<String> shoppingCartGuids) {
		return cartOrderDao.removeByShoppingCartGuids(shoppingCartGuids);
	}

	@Override
	public List<String> findCartOrderGuidsByCustomerGuid(final String storeCode, final String customerGuid) {
		return cartOrderDao.findCartOrderGuidsByCustomerGuid(storeCode, customerGuid);
	}

	@Override
	public Date getCartOrderLastModifiedDate(final String cartOrderGuid) {
		List<Date> lastModifiedDates = getPersistenceEngine().retrieveByNamedQuery("CART_ORDER_LAST_MODIFIED_DATE", cartOrderGuid);
		if (lastModifiedDates.isEmpty()) {
			return null;
		}
		return lastModifiedDates.get(0);
	}

	@Override
	public String getCartOrderGuidByShoppingCartGuid(final String shoppingCartGuid) {
		List<String> result = getPersistenceEngine().retrieveByNamedQuery("CART_ORDER_GUID_BY_SHOPPING_CART_GUID", shoppingCartGuid);
		if (result.isEmpty()) {
			return null;
		}
		return result.get(0);
	}

	@Override
	public Collection<String> getCartOrderCouponCodesByShoppingCartGuid(final String shoppingCartGuid) {

		return getPersistenceEngine().retrieveByNamedQuery("CART_ORDER_COUPON_CODES_BY_SHOPPING_CART_GUID", shoppingCartGuid);
	}


	@Override
	public String getShoppingCartGuid(final String storeCode, final String cartOrderGuid) {
		final List<String> result =  getPersistenceEngine().retrieveByNamedQuery("CART_ORDER_SHOPPING_CART_GUID_BY_STORE_CODE_AND_CART_ORDER_GUID",
																						storeCode, cartOrderGuid);
		if (result.isEmpty()) {
			return null;
		}

		return result.get(0);
	}

	protected CartOrderShippingInformationSanitizer getCartOrderShippingInformationSanitizer() {
		return cartOrderShippingInformationSanitizer;
	}

	public void setCartOrderShippingInformationSanitizer(final CartOrderShippingInformationSanitizer cartOrderShippingInformationSanitizer) {
		this.cartOrderShippingInformationSanitizer = cartOrderShippingInformationSanitizer;
	}

	protected PersistenceEngine getPersistenceEngine() {
		return persistenceEngine;
	}

	public void setPersistenceEngine(final PersistenceEngine persistenceEngine) {
		this.persistenceEngine = persistenceEngine;
	}

}
