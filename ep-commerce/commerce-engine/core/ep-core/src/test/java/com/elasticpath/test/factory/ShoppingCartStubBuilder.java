/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.test.factory;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.UUID;

import org.jmock.Expectations;
import org.jmock.Mockery;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.catalog.ProductSkuLookup;

/**
 * A builder for ShoppingCarts and its items.
 */
@SuppressWarnings({"PMD.TooManyMethods"})
public final class ShoppingCartStubBuilder {

	private static final Currency CURRENCY_CAD = Currency.getInstance("CAD");
	private static Currency currency;

	/**
	 * Prevent subclassing.
	 */
	private ShoppingCartStubBuilder() {
		// Prevent subclassings, the inner classes should be extended instead
	}
	
	/**
	 * Simple workaround for providing unique mock id's.
	 */
	protected static final class Incrementer {
		private static int value;
		
		private Incrementer() {
			// prevent external Instantiation
		}
		
		/**
		 * @return the next incremental id.
		 */
		public static int nextId() {
			return value++;
		}
	}
	
	/**
	 * @param prefix a prefix to help identify the mock, eg. 'cart-item'.
	 * @return a unique-number prefixed by <code>prefix</code> and a hyphen.
	 */
	static String nextMockId(final String prefix) {
		return prefix + "-" + Incrementer.nextId();
	}
	
	
	/**
	 * @param context the Mockery being used by the test.
	 * @return a new builder.
	 */
	public static ShoppingCartBuilder aCart(final Mockery context) {
		return new ShoppingCartBuilder(context);
	}

	/** 
	 * Factory method for shopping items. 
	 *
	 * @param context the JMock context used to create the subs around.
	 * @param mockProductSkuLookup a mock product sku builder created by the jmock context
	 * @return a ShoppingItemBuilder.
	 */
	public static ShoppingItemBuilder aShoppingItem(final Mockery context, final ProductSkuLookup mockProductSkuLookup) {
		return new ShoppingItemBuilder(context, mockProductSkuLookup);
	}
	
	/**
	 * A builder for stubbed shopping carts to ease test setup.
	 */
	/**
	 * @author ivanjensen
	 *
	 */
	public static class ShoppingCartBuilder {
		
		private final List<ShoppingItem> items = new ArrayList<>();
		private final Mockery context;
		private final ShoppingCart cart;
		private CustomerSession customerSession;
		private ShoppingCartPricingSnapshot pricingSnapshot;

		/**
		 * @param context the Mockery used by the test.
		 */
		public ShoppingCartBuilder(final Mockery context) {
			this.context = context;

			cart = context.mock(ShoppingCart.class, nextMockId("cart"));
			pricingSnapshot = context.mock(ShoppingCartPricingSnapshot.class, nextMockId("cart-pricing-snapshot"));
		}

		/**
		 * @param address the shipping address for the cart.
		 * @return this cart builder.
		 */
		public ShoppingCartBuilder withShippingAddress(final Address address) {
			context.checking(new Expectations() { {
				allowing(cart).getShippingAddress();
				will(returnValue(address));
			} });
			return this;
		}

		/**
		 * @param currency the currency for the cart.
		 * @return this cart builder.
		 */
		public ShoppingCartBuilder withCurrency(final Currency currency) {
			ShoppingCartStubBuilder.currency = currency;
			return this;
		}

		/**
		 * Sets the cart's currency to Canadian Dollars, a convenience version of <code>withCurrency</code>.
		 * @return this cart builder.
		 */
		public ShoppingCartBuilder withCurrencyCAD() {
			withCurrency(CURRENCY_CAD);
			return this;
		}

		/**
		 * Add the result of the item builder.
		 * @param itemBuilder the builder for the shopping item
		 * @return this cart builder
		 */
		public ShoppingCartBuilder with(final ShoppingItemBuilder itemBuilder) {
			items.add(itemBuilder.build());
			return this;
		}
		
		/**
		 * Specify the store for the cart.
		 * @param store the store the cart is for
		 * @return this cart builder
		 */
		public ShoppingCartBuilder forStore(final Store store) {
			context.checking(new Expectations() {
				{
					allowing(cart).getStore();
					will(returnValue(store));
				}
			});
			return this;
		}
		
		/**
		 * Specify the cart subtotal.
		 * @param subTotal the cart's subtotal
		 * @return this cart builder
		 */
		public ShoppingCartBuilder withSubtotal(final BigDecimal subTotal) {
			context.checking(new Expectations() { {
				allowing(pricingSnapshot).getSubtotal();
				will(returnValue(subTotal));
			} });
			return this;
		}

		/**
		 * Specify the cart subtotal discount.
		 * @param subTotalDiscount the discount for the cart subtotal
		 * @return this cart builder
		 */
		public ShoppingCartBuilder withSubtotalDiscount(final BigDecimal subTotalDiscount) {
			context.checking(new Expectations() { {
				allowing(pricingSnapshot).getSubtotalDiscount();
				will(returnValue(subTotalDiscount));
			} });
			return this;
		}

		/**
		 * Specify the pricing snapshot.
		 *
		 * @param pricingSnapshot the pricing snapshot
		 * @return this cart builder
		 */
		public ShoppingCartBuilder withPricingSnapshot(final ShoppingCartPricingSnapshot pricingSnapshot) {
			this.pricingSnapshot = pricingSnapshot;
			return this;
		}

		/**
		 * @param requiresShipping true if the cart requires shipping.
		 * @return this cart builder.
		 */
		public ShoppingCartBuilder withRequiresShipping(final boolean requiresShipping) {
			context.checking(new Expectations() { {
				allowing(cart).requiresShipping();
				will(returnValue(requiresShipping));
			} });
			return this;
		}

		/**
		 * Specify the Customer Session.
		 *
		 * @param customerSession the customer session
		 * @return this cart builder
		 */
		public ShoppingCartBuilder withCustomerSession(final CustomerSession customerSession) {
			this.customerSession = customerSession;
			return this;
		}

		/**
		 * @return the built cart
		 */
		public ShoppingCart build() {

			context.checking(new Expectations() { {
				allowing(cart).getAllShoppingItems();
				will(returnValue(items));

				allowing(cart).getRootShoppingItems();
				will(returnValue(items));

				if (customerSession == null) {
					customerSession = context.mock(CustomerSession.class, "cartStubBuilderCustomerSession_" + UUID.randomUUID());

					if (currency != null) {
						allowing(customerSession).getCurrency();
						will(returnValue(currency));
					}
				}

				allowing(cart).getCustomerSession();
				will(returnValue(customerSession));

			} });

			return cart;
		}

		protected Mockery getContext() {
			return context;
		}

		protected ShoppingCart getCart() {
			return cart;
		}

	}
	
	/**
	 * A builder for stub cart items for use JMock unit tests.
	 */
	/**
	 * @author ivanjensen
	 *
	 */
	public static class ShoppingItemBuilder {

		private final Mockery context;
		private final ProductSkuLookup mockProductSkuLookup;
		private final ShoppingItem item;

		private final ProductSku sku = new ProductSkuImpl();
		
		/**
		 * Create a builder for shopping items.
		 * @param context the mockery to register the item's expectations with.
		 * @param mockProductSkuLookup a mock product sku lookup previously created in the given context
		 */
		public ShoppingItemBuilder(final Mockery context, final ProductSkuLookup mockProductSkuLookup) {
			this.context = context;
			this.mockProductSkuLookup = mockProductSkuLookup;
			this.item = context.mock(ShoppingItem.class, nextMockId("cartItem"));

			sku.initialize();

			context.checking(new Expectations() { {
				allowing(item).getSkuGuid();
				will(returnValue(sku.getGuid()));

				allowing(mockProductSkuLookup).findByGuid(sku.getGuid());
				will(returnValue(sku));
			} });
		}
		
		/**
		 * Call once, once all the other builder methods have been called.
		 * @return the built shopping item.
		 */
		public ShoppingItem build() {
			return item;
		}			

		/**
		 * Makes the item report that it is not shippable.
		 * @return this shopping item builder.
		 */
		public ShoppingItemBuilder thatsNotShippable() {
			getContext().checking(new Expectations() { {
				allowing(item).isShippable(mockProductSkuLookup);
				will(returnValue(false));
			} });
			sku.setShippable(false);
			return this;
		}
		
		/**
		 * Makes the item report that it is shippable.
		 * @return this shopping item builder.
		 */
		public ShoppingItemBuilder thatsShippable() {
			getContext().checking(new Expectations() { {
				allowing(item).isShippable(mockProductSkuLookup);
				will(returnValue(true));
			} });
			sku.setShippable(true);
			return this;
		}
		
		/**
		 * Makes the item report that it has the specified quantity.
		 * @param quantity the quantity to be ordered.
		 * @return this shopping item builder.
		 */
		public ShoppingItemBuilder withQuantity(final int quantity) {
			getContext().checking(new Expectations() { {
				allowing(item).getQuantity(); 
				will(returnValue(quantity));
			} });
			return this;
		}

		/** 
		 * Makes the shopping item's sku the specified code.
		 * @param skuCode the code for the sku.
		 * @return this shopping item builder.
		 */
		public ShoppingItemBuilder withSkuCode(final String skuCode) {
			sku.setSkuCode(skuCode);

			getContext().checking(new Expectations() { {
				allowing(mockProductSkuLookup).findBySkuCode(skuCode);
				will(returnValue(sku));
			}});

			return this;
		}

		/**
		 * Makes the shopping item report that it is for the specified product.
		 * @param product the product.
		 * @return this shopping item builder.
		 */
		public ShoppingItemBuilder withProduct(final Product product) {
			sku.setProduct(product);
			return this;
		}
		
		/**
		 * Makes the shopping item report that it is discountable.
		 * @return this shopping item builder.
		 */
		public ShoppingItemBuilder thatsDiscountable() {
			getContext().checking(new Expectations() { {
				allowing(item).isDiscountable(mockProductSkuLookup);
				will(returnValue(true));
			} });
			return this;
		}


		/**
		 * Makes the shopping item report that it is not discountable.
		 * @return this shopping item builder.
		 */
		public ShoppingItemBuilder thatsNotDiscountable() {
			getContext().checking(new Expectations() { {
				allowing(item).isDiscountable(mockProductSkuLookup);
				will(returnValue(true));
			} });
			return this;
		}

		/**
		 * Makes the shopping item report that it is a bundle constituent.
		 * @return this shopping item builder.
		 */
		public ShoppingItemBuilder thatsABundleConstituent() {
			getContext().checking(new Expectations() { {
				allowing(item).isBundleConstituent();
				will(returnValue(true));
			} });
			return this;
		}

		/**
		 * Makes the shopping item report that it is not a bundle constituent.
		 * @return this shopping item builder.
		 */
		public ShoppingItemBuilder thatsNotABundleConstituent() {
			getContext().checking(new Expectations() { {
				allowing(item).isBundleConstituent();
				will(returnValue(false));
			} });
			return this;
		}

		/**
		 * Makes the shopping item's sku report that it weighs the specified amount.
		 * @param weight the weight of the sku.
		 * @return this shopping item builder.
		 */
		public ShoppingItemBuilder withWeight(final BigDecimal weight) {
			sku.setWeight(weight);
			return this;
		}

		protected Mockery getContext() {
			return context;
		}

		protected ShoppingItem getItem() {
			return item;
		}

	}
}

