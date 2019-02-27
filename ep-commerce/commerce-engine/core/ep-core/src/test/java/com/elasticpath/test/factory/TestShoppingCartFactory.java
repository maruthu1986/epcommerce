/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.test.factory;

import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCartMemento;
import com.elasticpath.domain.shoppingcart.impl.ShoppingCartImpl;
import com.elasticpath.domain.shoppingcart.impl.ShoppingCartMementoImpl;
import com.elasticpath.domain.store.Store;

/**
 * A factory for producing properly constructed {@link com.elasticpath.domain.shoppingcart.ShoppingCart ShoppingCart}s for use in tests.
 * <p/>
 * The intent is to keep all the {@link com.elasticpath.domain.shoppingcart.ShoppingCart ShoppingCart} creation code in one place so that changes
 * in its instantiation can be fixed all at once.
 */
public final class TestShoppingCartFactory {

	private static final TestShoppingCartFactory INSTANCE = new TestShoppingCartFactory();
	
	/**
	 * Default private constructor.
	 */
	private TestShoppingCartFactory() {
		
	}
	
	/**
	 * Gets an instance of the factory for use.
	 *
	 * @return {@link TestShoppingCartFactory}.
	 */
	public static TestShoppingCartFactory getInstance() {
		return INSTANCE;
	}	

	/**
	 * Creates a new {@link com.elasticpath.domain.shoppingcart.ShoppingCart ShoppingCart} with a {@link ShoppingCartMemento}.
	 *
	 * @param shopper the {@link Shopper} this cart belongs to.
	 * @param store the {@link Store} this cart belongs to.
	 * @return a new {@link com.elasticpath.domain.shoppingcart.ShoppingCart ShoppingCart}.
	 */
	public ShoppingCartImpl createNewCartWithMemento(final Shopper shopper, final Store store) {
		final ShoppingCartMemento shoppingCartMemento = new ShoppingCartMementoImpl();
		shoppingCartMemento.setGuid(TestGuidUtility.getGuid());
		
		final ShoppingCartImpl shoppingCart = new ShoppingCartImpl();
		shoppingCart.setShoppingCartMemento(shoppingCartMemento);
		shoppingCart.setShopper(shopper);
		shoppingCart.setStore(store);
		
		shopper.setCurrentShoppingCart(shoppingCart);

		return shoppingCart;
	}

}
 