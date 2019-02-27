/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */

package com.elasticpath.test.factory;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import com.elasticpath.shipping.connectivity.dto.ShippableItem;
import com.elasticpath.shipping.connectivity.dto.ShippableItemContainer;

/**
 * A impl for ShippableItemContainer and its items.
 */
public final class ShippableItemContainerStubBuilder {

	private static final Currency CURRENCY_CAD = Currency.getInstance("CAD");

	/**
	 * Prevent subclassing.
	 */
	private ShippableItemContainerStubBuilder() {
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
	 * @param prefix a prefix to help identify the mock, eg. 'container-item'.
	 * @return a unique-number prefixed by <code>prefix</code> and a hyphen.
	 */
	static String nextMockId(final String prefix) {
		return prefix + "-" + Incrementer.nextId();
	}

	/**
	 * @return a new impl.
	 */
	public static ShippableItemContainerBuilder aContainer() {
		return new ShippableItemContainerBuilder();
	}

	/**
	 * Factory method for shippable items.
	 *
	 * @return a ShippableItemBuilder.
	 */
	public static ShippableItemBuilder aShippableItem() {
		return new ShippableItemBuilder();
	}

	/**
	 * A impl for stubbed shippable carts to ease test setup.
	 */
	/**
	 * @author ivanjensen
	 */
	public static class ShippableItemContainerBuilder {

		private final List<ShippableItem> items = new ArrayList<>();
		private final ShippableItemContainer<ShippableItem> container;

		/**
		 * Default constructor.
		 */
		@SuppressWarnings("unchecked")
		public ShippableItemContainerBuilder() {

			container = mock(ShippableItemContainer.class, nextMockId("container"));
		}

		/**
		 * @param currency the currency for the container.
		 * @return this container impl.
		 */
		public ShippableItemContainerBuilder withCurrency(final Currency currency) {
			when(container.getCurrency()).thenReturn(currency);
			return this;
		}

		/**
		 * Sets the container's currency to Canadian Dollars, a convenience version of <code>withCurrency</code>.
		 *
		 * @return this container impl.
		 */
		public ShippableItemContainerBuilder withCurrencyCAD() {
			withCurrency(CURRENCY_CAD);
			return this;
		}

		/**
		 * Add the result of the item impl.
		 *
		 * @param itemBuilder the impl for the shippable item
		 * @return this container impl
		 */
		public ShippableItemContainerBuilder with(final ShippableItemBuilder itemBuilder) {
			items.add(itemBuilder.build());
			return this;
		}

		/**
		 * Specify the store code for the shipping item container.
		 *
		 * @param storeCode the store code
		 * @return this shipping item container impl
		 */
		public ShippableItemContainerBuilder forStoreCode(final String storeCode) {
			when(container.getStoreCode()).thenReturn(storeCode);
			return this;
		}

		/**
		 * @return the built container
		 */
		public ShippableItemContainer<ShippableItem> build() {
			doReturn(items).when(container).getShippableItems();
			return container;
		}

		protected ShippableItemContainer<ShippableItem> getContainer() {
			return container;
		}

	}

	/**
	 * A impl for stub container items for use JMock unit tests.
	 */
	/**
	 * @author ivanjensen
	 */
	public static class ShippableItemBuilder {

		private final ShippableItem item;

		/**
		 * Create a impl for shippable items.
		 */
		public ShippableItemBuilder() {
			this.item = mock(ShippableItem.class, nextMockId("cartItem"));

		}

		/**
		 * Call once, once all the other impl methods have been called.
		 *
		 * @return the built shopping item.
		 */
		public ShippableItem build() {
			return item;
		}

		/**
		 * Makes the item report that it has the specified quantity.
		 *
		 * @param quantity the quantity to be ordered.
		 * @return this shippable item impl.
		 */
		public ShippableItemBuilder withQuantity(final int quantity) {
			when(item.getQuantity()).thenReturn(quantity);
			return this;
		}

		/**
		 * Makes the shippable item's sku the specified code.
		 *
		 * @param skuGuid the guid for the sku.
		 * @return this shippable item impl.
		 */
		public ShippableItemBuilder withSkuGuid(final String skuGuid) {
			when(item.getSkuGuid()).thenReturn(skuGuid);
			return this;
		}

		/**
		 * Makes the shippable item's sku report that it weighs the specified amount.
		 *
		 * @param weight the weight of the sku.
		 * @return this shippable item impl.
		 */
		public ShippableItemBuilder withWeight(final BigDecimal weight) {
			when(item.getWeight()).thenReturn(weight);
			return this;
		}

		protected ShippableItem getItem() {
			return item;
		}

	}
}

