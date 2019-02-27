/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shipping.transformers.impl;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Currency;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.ShoppingItemPricingSnapshot;
import com.elasticpath.domain.store.Store;
import com.elasticpath.money.Money;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.service.shipping.transformers.visitors.PricedShippableItemPopulatorVisitor;
import com.elasticpath.service.shipping.transformers.visitors.ShippableItemContainerPopulatorVisitor;
import com.elasticpath.service.shipping.transformers.visitors.ShippableItemPopulatorVisitor;
import com.elasticpath.service.shipping.transformers.visitors.impl.PricedShippableItemContainerItemsPopulatorVisitorImpl;
import com.elasticpath.service.shipping.transformers.visitors.impl.PricedShippableItemPopulatorVisitorImpl;
import com.elasticpath.service.shipping.transformers.visitors.impl.ShippableItemContainerMetadataPopulatorVisitorImpl;
import com.elasticpath.service.shipping.transformers.visitors.impl.ShippableItemContainerPopulatorVisitorImpl;
import com.elasticpath.service.shipping.transformers.visitors.impl.ShippableItemPopulatorVisitorImpl;
import com.elasticpath.service.shoppingcart.ShippableItemPredicate;
import com.elasticpath.service.tax.impl.DiscountApportioningCalculatorImpl;
import com.elasticpath.shipping.connectivity.dto.PricedShippableItem;
import com.elasticpath.shipping.connectivity.dto.PricedShippableItemContainer;
import com.elasticpath.shipping.connectivity.dto.ShippableItem;
import com.elasticpath.shipping.connectivity.dto.builder.PricedShippableItemContainerBuilder;
import com.elasticpath.shipping.connectivity.dto.builder.impl.PricedShippableItemBuilderImpl;
import com.elasticpath.shipping.connectivity.dto.builder.impl.PricedShippableItemContainerBuilderImpl;
import com.elasticpath.shipping.connectivity.dto.builder.impl.ShippingAddressBuilderImpl;
import com.elasticpath.shipping.connectivity.dto.builder.populators.BaseShippableItemContainerBuilderPopulator;
import com.elasticpath.shipping.connectivity.dto.builder.populators.PricedShippableItemContainerBuilderPopulator;

/**
 * A hybrid test in between a unit test and an integration test which uses real collaborators but mocked inputs to test that
 * the generated {@link com.elasticpath.shipping.connectivity.dto.PricedShippableItemContainer} returned by
 * {@link PricedShippableItemContainerTransformerImpl} has correct pricing when taking into account a subtotal discount that has been applied
 * to the {@link com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot}.
 * <p>
 * This is to ensure that any shipping calculation service calculates the available priced shipping options using the effective price the customer
 * pays for the shopping items, which is after apportioning any subtotal discount as this doesn't actually get reflected in a shopping item's price
 * until order submission.
 * <p>
 * Should be replaced by a proper integration test but was created initially for speed to provide as a template for a fully-fledged integration test.
 */
@RunWith(MockitoJUnitRunner.class)
public class PricedShippableItemContainerTransformerImplApportioningTest {
	private static final Currency CURRENCY = Currency.getInstance("CAD");
	private static final String STORE_CODE = "DummyStoreCode";

	private static final int TWO = 2;
	private static final int THREE = 3;
	private static final int FIVE = 5;

	private static final String SKU_A_GUID = "SKU_A_GUID";
	private static final String SKU_B_GUID = "SKU_B_GUID";
	private static final String SKU_C_GUID = "SKU_C_GUID";

	private static final Money TWO_DOLLARS = Money.valueOf(new BigDecimal("2"), CURRENCY);
	private static final Money FIVE_DOLLARS = Money.valueOf(new BigDecimal("5"), CURRENCY);
	private static final Money FIVE_DOLLARS_TWENTY = Money.valueOf(new BigDecimal("5.20"), CURRENCY);
	private static final Money SIX_DOLLARS = Money.valueOf(new BigDecimal("6"), CURRENCY);
	private static final Money TEN_DOLLARS = Money.valueOf(BigDecimal.TEN, CURRENCY);
	private static final Money THIRTEEN_DOLLARS = Money.valueOf(new BigDecimal("13"), CURRENCY);
	private static final Money TWENTY_SIX_DOLLARS = Money.valueOf(new BigDecimal("26"), CURRENCY);
	private static final Money THIRTY_TWO_DOLLARS = Money.valueOf(new BigDecimal("32"), CURRENCY);
	private static final Money SIXTY_DOLLARS = Money.valueOf(new BigDecimal("60"), CURRENCY);

	@Mock
	private ProductSkuLookup productSkuLookup;

	@Mock
	private ShoppingCart shoppingCart;

	@Mock
	private Shopper shopper;

	@Mock
	private Store store;

	@Mock
	private ShoppingCartPricingSnapshot shoppingCartPricingSnapshot;

	private Collection<ShoppingItem> shoppingItems;

	private PricedShippableItemContainerTransformerImpl objectUnderTest;

	@Before
	public void setUp() {
		objectUnderTest = new PricedShippableItemContainerTransformerImpl();
		objectUnderTest.setShippableItemPredicate(new ShippableItemPredicate<>(productSkuLookup));
		objectUnderTest.setShippableItemsTransformer(createPricedShippableItemsTransformer());
		objectUnderTest.setBaseTransformer(createBaseTransformer());

		when(shoppingCart.getStore()).thenReturn(store);
		when(shoppingCart.getShopper()).thenReturn(shopper);
		when(shopper.getCurrency()).thenReturn(CURRENCY);
		when(store.getCode()).thenReturn(STORE_CODE);

		shoppingItems = new ArrayList<>();
		when(shoppingCart.getApportionedLeafItems()).thenReturn(shoppingItems);
	}

	/**
	 * Verifies that a cart subtotal discount is correctly apportioned and discounted in the pricing information set in PricedShippableItem
	 * as that is used by Shipping Calculation integrations to determine availability and costs of shipping options and it should have the
	 * effective price that the customer pays for each item. See the Javadoc for this class for more information.
	 * <p>
	 * Verifies the following scenario:
	 * <p>
	 * Given I have the following items in my cart
	 * * SKU A; Unit Price $5; Qty: 3; Shippable: Yes
	 * * SKU B; Unit Price $10; Qty: 2; Shippable: No
	 * * SKU C; Unit Price $13; Qty: 5; Shippable: Yes
	 * <p>
	 * And I have a Cart Subtotal discount of $60
	 * When I send a request to the shipping service
	 * Then it should contain:
	 * * SKU A: Unit Price: $2.00
	 * * SKU C: Unit Price $5.20
	 * With a Shippable Order total: $32
	 */
	@Test
	public void verifyPricedShippableItemsHaveCorrectPricingWithSubtotalDiscountsApportioned() {
		mockShoppingItem(SKU_A_GUID, FIVE_DOLLARS, THREE, true);
		mockShoppingItem(SKU_B_GUID, TEN_DOLLARS, TWO, false);
		mockShoppingItem(SKU_C_GUID, THIRTEEN_DOLLARS, FIVE, true);

		when(shoppingCartPricingSnapshot.getSubtotalDiscountMoney()).thenReturn(SIXTY_DOLLARS);

		final PricedShippableItemContainer<PricedShippableItem> pricedShippableItemContainer
				= objectUnderTest.apply(shoppingCart, shoppingCartPricingSnapshot);

		assertThat(pricedShippableItemContainer).isNotNull();

		final Collection<PricedShippableItem> shippableItems = pricedShippableItemContainer.getShippableItems();
		assertThat(shippableItems).isNotNull();
		assertThat(shippableItems).hasSize(2);

		for (PricedShippableItem shippableItem : shippableItems) {
			switch (shippableItem.getSkuGuid()) {
				case SKU_A_GUID:
					validateShippableItem(shippableItem, TWO_DOLLARS, SIX_DOLLARS);
					break;
				case SKU_C_GUID:
					validateShippableItem(shippableItem, FIVE_DOLLARS_TWENTY, TWENTY_SIX_DOLLARS);
					break;
				default:
					fail(format("Unexpected PricedShippableItem found with SKU guid '%s': %s", shippableItem.getSkuGuid(), shippableItem));
					break; // For PMD, bless.
			}
		}

		final Money orderTotal = shippableItems.stream()
				.map(PricedShippableItem::getTotalPrice)
				.reduce(Money.zero(CURRENCY), Money::add);

		assertThat(orderTotal).isEqualTo(THIRTY_TWO_DOLLARS);
	}

	private void validateShippableItem(final PricedShippableItem shippableItem, final Money expectedUnitPrice, final Money expectedTotalPrice) {
		assertThat(shippableItem.getUnitPrice()).isEqualTo(expectedUnitPrice);
		assertThat(shippableItem.getTotalPrice()).isEqualTo(expectedTotalPrice);
	}

	// Don't look below this line! Nasty mocking and instantiations occur!
	// As mentioned in Javadoc, this class just serves as a quick validation of the apportioning and serves as a template
	// for creating a real integration test.

	private void mockShoppingItem(final String skuGuid, final Money unitPrice, final int quantity, final boolean isShippable) {
		final ShoppingItem shoppingItem = mock(ShoppingItem.class);
		when(shoppingItem.getGuid()).thenReturn("shoppingItemGuidFor_" + skuGuid);
		when(shoppingItem.getSkuGuid()).thenReturn(skuGuid);
		when(shoppingItem.getQuantity()).thenReturn(quantity);
		when(shoppingItem.hasPrice()).thenReturn(true);
		when(shoppingItem.isDiscountable(productSkuLookup)).thenReturn(true);
		when(shoppingItem.isBundle(productSkuLookup)).thenReturn(false);
		when(shoppingItem.isShippable(productSkuLookup)).thenReturn(isShippable);

		final ShoppingItemPricingSnapshot itemPricingSnapshot = mock(ShoppingItemPricingSnapshot.class, RETURNS_DEEP_STUBS);
		when(shoppingCartPricingSnapshot.getShoppingItemPricingSnapshot(shoppingItem)).thenReturn(itemPricingSnapshot);

		when(itemPricingSnapshot.getPriceCalc().forUnitPrice().withCartDiscounts().getMoney()).thenReturn(unitPrice);

		final Money totalPrice = unitPrice.multiply(quantity);
		when(itemPricingSnapshot.getPriceCalc().withCartDiscounts().getMoney()).thenReturn(totalPrice);
		when(itemPricingSnapshot.getPriceCalc().withCartDiscounts().getAmount()).thenReturn(totalPrice.getAmount());

		final ProductSku productSku = mock(ProductSku.class);
		when(productSkuLookup.findByGuid(skuGuid)).thenReturn(productSku);
		when(productSku.getSkuCode()).thenReturn(skuGuid);

		shoppingItems.add(shoppingItem);
	}

	private BaseTransformer createBaseTransformer() {
		final BaseTransformer baseTransformer = new BaseTransformer();

		baseTransformer.setSupplier(PricedShippableItemContainerBuilderImpl::new);
		baseTransformer.setBaseVisitors(createBaseVisitors());
		baseTransformer.setItemSpecificVisitors(createItemSpecificVisitors());

		return baseTransformer;
	}

	private PricedShippableItemsTransformerImpl createPricedShippableItemsTransformer() {
		final PricedShippableItemsTransformerImpl pricedShippableItemsTransformer = new PricedShippableItemsTransformerImpl();

		final DiscountApportioningCalculatorImpl discountApportioningCalculator = new DiscountApportioningCalculatorImpl();
		discountApportioningCalculator.setProductSkuLookup(productSkuLookup);

		pricedShippableItemsTransformer.setDiscountApportioningCalculator(discountApportioningCalculator);
		pricedShippableItemsTransformer.setPricedShippableItemTransformer(createPricedShippableItemTransformer());

		return pricedShippableItemsTransformer;
	}

	private PricedShippableItemTransformerImpl createPricedShippableItemTransformer() {
		final PricedShippableItemTransformerImpl pricedShippableItemTransformer = new PricedShippableItemTransformerImpl();

		pricedShippableItemTransformer.setSupplier(PricedShippableItemBuilderImpl::new);
		pricedShippableItemTransformer.setUnpricedVisitors(createUnpricedVisitors());
		pricedShippableItemTransformer.setPricedVisitors(createPricedVisitors());

		return pricedShippableItemTransformer;
	}

	private List<ShippableItemContainerPopulatorVisitor<ShippableItem, BaseShippableItemContainerBuilderPopulator>> createBaseVisitors() {
		final ShippableItemContainerPopulatorVisitorImpl mainVisitor = new ShippableItemContainerPopulatorVisitorImpl();

		final ShippingAddressTransformerImpl shippingAddressTransformer = new ShippingAddressTransformerImpl();
		shippingAddressTransformer.setShippingAddressBuilderSupplier(ShippingAddressBuilderImpl::new);

		mainVisitor.setShippingAddressTransformer(shippingAddressTransformer);

		final ShippableItemContainerMetadataPopulatorVisitorImpl metadataVisitor = new ShippableItemContainerMetadataPopulatorVisitorImpl();

		return asList(mainVisitor, metadataVisitor);
	}

	private List<ShippableItemContainerPopulatorVisitor<PricedShippableItem, PricedShippableItemContainerBuilderPopulator<PricedShippableItem>>>
	createItemSpecificVisitors() {
		final PricedShippableItemContainerItemsPopulatorVisitorImpl mainVisitor = new PricedShippableItemContainerItemsPopulatorVisitorImpl();

		return singletonList(mainVisitor);
	}

	private List<ShippableItemPopulatorVisitor> createUnpricedVisitors() {
		final ShippableItemPopulatorVisitorImpl mainVisitor = new ShippableItemPopulatorVisitorImpl();
		mainVisitor.setProductSkuLookup(productSkuLookup);

		return singletonList(mainVisitor);
	}

	private List<PricedShippableItemPopulatorVisitor> createPricedVisitors() {
		final PricedShippableItemPopulatorVisitorImpl mainVisitor = new PricedShippableItemPopulatorVisitorImpl();

		return singletonList(mainVisitor);
	}

	/**
	 * Simple sub-class of {@link BaseShippableItemContainerTransformerImpl} to harden the generic types to make it easier to work with
	 * programmatically above and avoid the need to repeat the long generics declaration above.
	 */
	private static class BaseTransformer
			extends BaseShippableItemContainerTransformerImpl<PricedShippableItemContainer<PricedShippableItem>,
			PricedShippableItem,
			PricedShippableItemContainerBuilderPopulator<PricedShippableItem>,
			PricedShippableItemContainerBuilder> {
	}
}
