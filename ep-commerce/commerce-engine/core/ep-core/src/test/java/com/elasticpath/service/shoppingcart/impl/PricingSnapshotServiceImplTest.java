/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.service.shoppingcart.impl;

import static java.util.Collections.singletonList;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.jmock.Expectations;
import org.jmock.api.Invocation;
import org.jmock.auto.Mock;
import org.jmock.lib.action.CustomAction;

import com.google.common.collect.Iterables;

import org.apache.log4j.Logger;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.impl.OrderSkuImpl;
import com.elasticpath.domain.shoppingcart.DiscountRecord;
import com.elasticpath.domain.shoppingcart.PromotionRecordContainer;
import com.elasticpath.domain.shoppingcart.ShippingPricingSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.ShoppingItemPricingSnapshot;
import com.elasticpath.domain.shoppingcart.impl.CatalogItemDiscountRecordImpl;
import com.elasticpath.domain.shoppingcart.impl.ShoppingCartImpl;
import com.elasticpath.money.Money;
import com.elasticpath.plugin.tax.domain.TaxAddress;
import com.elasticpath.plugin.tax.domain.TaxOperationContext;
import com.elasticpath.sellingchannel.director.CartDirector;
import com.elasticpath.service.rules.EpRuleEngine;
import com.elasticpath.service.shipping.transformers.PricedShippableItemContainerTransformer;
import com.elasticpath.service.tax.TaxCalculationResult;
import com.elasticpath.service.tax.TaxCalculationService;
import com.elasticpath.shipping.connectivity.dto.PricedShippableItemContainer;
import com.elasticpath.shipping.connectivity.dto.ShippingCalculationResult;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;
import com.elasticpath.shipping.connectivity.service.ShippingCalculationService;
import com.elasticpath.test.jmock.AbstractCatalogDataTestCase;

@SuppressWarnings({"PMD.ExcessiveImports", "PMD.TestClassWithoutTestCases", "unchecked"})
public class PricingSnapshotServiceImplTest extends AbstractCatalogDataTestCase {

	@Mock
	private EpRuleEngine ruleEngine;

	@Mock
	private CartDirector cartDirector;

	@Mock
	private TaxCalculationService taxCalculationService;

	private PricingSnapshotServiceImpl pricingSnapshotService;

	@Mock
	private ShippingCalculationService shippingCalculationService;

	@Mock
	private ShippingCalculationResult shippingCalculationResult;

	@Mock
	private PricedShippableItemContainer<?> pricedShippableItemContainer;

	@Mock
	private PricedShippableItemContainerTransformer pricedShippableItemContainerTransformer;

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		pricingSnapshotService = new PricingSnapshotServiceImpl();
		pricingSnapshotService.setBeanFactory(getBeanFactory());
		pricingSnapshotService.setCartDirector(cartDirector);
		pricingSnapshotService.setProductSkuLookup(getProductSkuLookup());
		pricingSnapshotService.setRuleEngine(ruleEngine);
		pricingSnapshotService.setShippingCalculationService(shippingCalculationService);
		pricingSnapshotService.setPricedShippableItemContainerTransformer(pricedShippableItemContainerTransformer);

	}

	@SuppressWarnings("unchecked")
	@Test
	public void verifyCataloguePromosAreAddedToTheDiscountRecordContainer() throws Exception {
		final ShoppingCartImpl shoppingCart = getShoppingCart();
		final BigDecimal discountAmount = new BigDecimal("1.00");
		final long ruleId = 123L;
		final long actionId = 456L;

		final DiscountRecord discountRecord = new CatalogItemDiscountRecordImpl(ruleId, actionId, discountAmount);

		final TaxCalculationResult taxCalculationResult = context.mock(TaxCalculationResult.class);
		context.checking(new Expectations() {
			{
				allowing(taxCalculationService).calculateTaxes(
						with(any(String.class)),
						with(any(TaxAddress.class)),
						with(any(TaxAddress.class)),
						with(any(Money.class)),
						with(any(Map.class)),
						with(any(Money.class)),
						with(any(TaxOperationContext.class)));
				will(returnValue(taxCalculationResult));
				allowing(taxCalculationService).calculateTaxesAndAddToResult(
						with(any(TaxCalculationResult.class)),
						with(any(String.class)),
						with(any(TaxAddress.class)),
						with(any(TaxAddress.class)),
						with(any(Money.class)),
						with(any(Map.class)),
						with(any(Money.class)),
						with(any(TaxOperationContext.class)));
				will(returnValue(taxCalculationResult));

				allowing(taxCalculationResult).applyTaxes(with(any(Collection.class)));

				allowing(cartDirector).refresh(shoppingCart);
				will(new CustomAction("Adding catalogue promotions to cart items") {
					@Override
					public Object invoke(final Invocation invocation) throws Throwable {
						final ShoppingItem shoppingItem = Iterables.getFirst(shoppingCart.getAllShoppingItems(), null);

						if (shoppingItem != null) {
							final ShoppingItemPricingSnapshot shoppingItemPricingSnapshot =
									shoppingCart.getShoppingItemPricingSnapshot(shoppingItem);
							final Price price = shoppingItemPricingSnapshot.getPrice();
							price.addDiscountRecord(discountRecord);
						}

						return null;
					}
				});

				allowing(ruleEngine).fireOrderPromotionRules(shoppingCart, shoppingCart.getCustomerSession());
				allowing(ruleEngine).fireOrderPromotionSubtotalRules(shoppingCart, shoppingCart.getCustomerSession());

				allowing(pricedShippableItemContainerTransformer).apply(with(any(ShoppingCart.class)), with(any(ShoppingCartPricingSnapshot.class)));
				will(returnValue(pricedShippableItemContainer));
				allowing(shippingCalculationService).getPricedShippingOptions(pricedShippableItemContainer);
				will(returnValue(shippingCalculationResult));
			}
		});

		mockShippingOptionServiceGetPricedShippingOptions(Collections.emptyList());

		final ShoppingCartPricingSnapshot pricingSnapshot = pricingSnapshotService.getPricingSnapshotForCart(shoppingCart);

		final PromotionRecordContainer promotionRecordContainer = pricingSnapshot.getPromotionRecordContainer();

		assertThat(promotionRecordContainer.getAllDiscountRecords(), contains(discountRecord));
	}

	@Test
	public void verifySnapshotForOrderSkuIsSameOrderSku() throws Exception {
		final OrderSku orderSku = new OrderSkuImpl();

		final ShoppingItemPricingSnapshot pricingSnapshot = pricingSnapshotService.getPricingSnapshotForOrderSku(orderSku);

		assertSame(orderSku, pricingSnapshot);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void verifyPrePromotionShippingCostsCalculatedAndSet() throws Exception {
		final ShoppingCartImpl shoppingCart = getShoppingCart();

		final TaxCalculationResult taxCalculationResult = context.mock(TaxCalculationResult.class);
		context.checking(new Expectations() {
			{
				allowing(taxCalculationService).calculateTaxes(
						with(any(String.class)),
						with(any(TaxAddress.class)),
						with(any(TaxAddress.class)),
						with(any(Money.class)),
						with(any(Map.class)),
						with(any(Money.class)),
						with(any(TaxOperationContext.class)));
				will(returnValue(taxCalculationResult));
				allowing(taxCalculationService).calculateTaxesAndAddToResult(
						with(any(TaxCalculationResult.class)),
						with(any(String.class)),
						with(any(TaxAddress.class)),
						with(any(TaxAddress.class)),
						with(any(Money.class)),
						with(any(Map.class)),
						with(any(Money.class)),
						with(any(TaxOperationContext.class)));
				will(returnValue(taxCalculationResult));

				allowing(taxCalculationResult).applyTaxes(with(any(Collection.class)));

				allowing(cartDirector).refresh(shoppingCart);
				allowing(ruleEngine).fireOrderPromotionRules(shoppingCart, shoppingCart.getCustomerSession());
				allowing(ruleEngine).fireOrderPromotionSubtotalRules(shoppingCart, shoppingCart.getCustomerSession());

				allowing(pricedShippableItemContainerTransformer).apply(with(any(ShoppingCart.class)), with(any(ShoppingCartPricingSnapshot.class)));
				will(returnValue(pricedShippableItemContainer));
				allowing(shippingCalculationService).getPricedShippingOptions(pricedShippableItemContainer);
				will(returnValue(shippingCalculationResult));

			}
		});

		final String newShippingOptionCode = "SHIP0001";
		final Money shippingAmount = Money.valueOf(BigDecimal.TEN, CURRENCY);
		final ShippingOption newShippingOption = mockAvailableShippingOption(newShippingOptionCode, shippingAmount);

		final ShoppingCartPricingSnapshot pricingSnapshot = pricingSnapshotService.getPricingSnapshotForCart(shoppingCart);

		final ShippingPricingSnapshot shippingPricingSnapshot = pricingSnapshot.getShippingPricingSnapshot(newShippingOption);
		assertEquals(shippingAmount, shippingPricingSnapshot.getShippingListPrice());
		assertEquals(shippingAmount, shippingPricingSnapshot.getShippingPromotedPrice());
	}

	@Test
	public void verifyCachedShippingPricesClearedWhenShippingAddressCleared() {
		// Given we have a shopping cart with a shipping address and shipping options set up
		final ShoppingCart shoppingCart = getShoppingCart();

		context.checking(new Expectations() {
			{
				allowing(cartDirector).refresh(shoppingCart);
				allowing(ruleEngine).fireOrderPromotionRules(shoppingCart, shoppingCart.getCustomerSession());
				allowing(ruleEngine).fireOrderPromotionSubtotalRules(shoppingCart, shoppingCart.getCustomerSession());
				allowing(pricedShippableItemContainerTransformer).apply(with(any(ShoppingCart.class)), with(any(ShoppingCartPricingSnapshot.class)));
				will(returnValue(pricedShippableItemContainer));
				allowing(shippingCalculationService).getPricedShippingOptions(pricedShippableItemContainer);
				will(returnValue(shippingCalculationResult));
			}
		});

		final String shippingOptionCode = "SHIP0001";
		final Money shippingAmount = Money.valueOf(BigDecimal.TEN, CURRENCY);
		final ShippingOption shippingOption = mockAvailableShippingOption(shippingOptionCode, shippingAmount);

		// And the pricing snapshot returns a shipping pricing snapshot associated with it
		ShoppingCartPricingSnapshot pricingSnapshot = pricingSnapshotService.getPricingSnapshotForCart(shoppingCart);
		ShippingPricingSnapshot shippingPricingSnapshot = pricingSnapshot.getShippingPricingSnapshot(shippingOption);
		assertEquals("The shipping price should be set initially", shippingAmount, shippingPricingSnapshot.getShippingListPrice());

		// When we clear the shipping address
		shoppingCart.setShippingAddress(null);

		// Then when we get the shipping cost it should be zero
		pricingSnapshot = pricingSnapshotService.getPricingSnapshotForCart(shoppingCart);

		assertEquals("The shipping cost should now be zero as no shipping address has been set",
				0, pricingSnapshot.getShippingCost().getAmount().compareTo(BigDecimal.ZERO));

		// And when we get the shipping pricing snapshot directly, it should no longer be available
		boolean pricingSnapshotSucceeded = true;
		try {
			pricingSnapshot.getShippingPricingSnapshot(shippingOption);
		} catch (final EpServiceException e) {
			// Expecting the exception here, but not in the getShippingPricingSnapshot() call earlier so we manually check this invocation
			pricingSnapshotSucceeded = false;
		}

		if (pricingSnapshotSucceeded) {
			fail("The shipping pricing snapshot should no longer be available as the shipping address is null");
		}
	}

	@Test
	public void verifyErrorCauseSuppressedWhenStrategyIndicatesTo() {
		// Given we have a shopping cart with a shipping address and shipping options set up
		final ShoppingCart shoppingCart = getShoppingCart();

		context.checking(new Expectations() {
			{
				allowing(cartDirector).refresh(shoppingCart);
				allowing(ruleEngine).fireOrderPromotionRules(shoppingCart, shoppingCart.getCustomerSession());
				allowing(ruleEngine).fireOrderPromotionSubtotalRules(shoppingCart, shoppingCart.getCustomerSession());
				allowing(pricedShippableItemContainerTransformer).apply(with(any(ShoppingCart.class)), with(any(ShoppingCartPricingSnapshot.class)));
				will(returnValue(pricedShippableItemContainer));
				allowing(shippingCalculationService).getPricedShippingOptions(pricedShippableItemContainer);
				will(returnValue(shippingCalculationResult));
			}
		});

		mockShippingOptionServiceGetPricedShippingOptions(new Throwable());

		// And we do not have a selected shipping option (so we log an error rather than throw an exception)
		shoppingCart.clearSelectedShippingOption();

		// And the exception to be thrown is marked as loggable by the predicate
		pricingSnapshotService.setShippingOptionResultExceptionLogPredicate(throwable -> true);

		context.checking(new Expectations() {
			{
				oneOf(shippingCalculationResult).logError(with(any(Logger.class)), with(any(String.class)), with(true));
			}
		});

		// When we call the ShoppingCartPricingSnapshot service it logs a message without the cause (and so satisfies the expectation above)
		pricingSnapshotService.getPricingSnapshotForCart(shoppingCart);
	}

	@Test
	public void verifyErrorCauseThrownWhenStrategyIndicatesTo() {
		// Given we have a shopping cart with a shipping address and shipping options set up
		final ShoppingCart shoppingCart = getShoppingCart();

		context.checking(new Expectations() {
			{
				allowing(cartDirector).refresh(shoppingCart);
				allowing(ruleEngine).fireOrderPromotionRules(shoppingCart, shoppingCart.getCustomerSession());
				allowing(ruleEngine).fireOrderPromotionSubtotalRules(shoppingCart, shoppingCart.getCustomerSession());
				allowing(pricedShippableItemContainerTransformer).apply(with(any(ShoppingCart.class)), with(any(ShoppingCartPricingSnapshot.class)));
				will(returnValue(pricedShippableItemContainer));
				allowing(shippingCalculationService).getPricedShippingOptions(pricedShippableItemContainer);
				will(returnValue(shippingCalculationResult));
			}
		});

		mockShippingOptionServiceGetPricedShippingOptions(new Throwable());

		// And we do not have a selected shipping option (so we log an error rather than throw an exception)
		shoppingCart.clearSelectedShippingOption();

		// And the exception to be thrown is marked as not loggable by the predicate
		pricingSnapshotService.setShippingOptionResultExceptionLogPredicate(throwable -> false);

		context.checking(new Expectations() {
			{
				oneOf(shippingCalculationResult).logError(with(any(Logger.class)), with(any(String.class)), with(false));
			}
		});

		// When we call the ShoppingCartPricingSnapshot service it logs a message with the cause (and so satisfies the expectation above)
		pricingSnapshotService.getPricingSnapshotForCart(shoppingCart);
	}

	private ShippingOption mockAvailableShippingOption(final String shippingOptionCode, final Money shippingOptionCost) {
		final ShippingOption result = mockShippingOption(shippingOptionCode, shippingOptionCost);

		mockShippingOptionServiceGetPricedShippingOptions(singletonList(result));

		return result;
	}

	private ShippingOption mockShippingOption(final String shippingOptionCode, final Money shippingOptionCost) {
		final ShippingOption newShippingOption = context.mock(ShippingOption.class);

		context.checking(new Expectations() {
			{
				allowing(newShippingOption).getCode();
				will(returnValue(shippingOptionCode));

				allowing(newShippingOption).getShippingCost();
				will(returnValue(Optional.of(shippingOptionCost)));
			}
		});

		return newShippingOption;
	}

	@SuppressWarnings("unchecked")
	private void mockShippingOptionServiceGetPricedShippingOptions(final List<ShippingOption> shippingOptionList) {
		context.checking(new Expectations() {
			{
				allowing(shippingCalculationResult).isSuccessful();
				will(returnValue(true));

				allowing(shippingCalculationResult).getAvailableShippingOptions();
				will(returnValue(shippingOptionList));

				allowing(shippingCalculationService).getPricedShippingOptions(pricedShippableItemContainer);
				will(returnValue(shippingCalculationResult));
			}
		});
	}

	@SuppressWarnings("unchecked")
	private void mockShippingOptionServiceGetPricedShippingOptions(final Throwable cause) {
		final ShippingCalculationResult.ErrorInformation errorInformation = context.mock(ShippingCalculationResult.ErrorInformation.class);

		context.checking(new Expectations() {
			{
				allowing(shippingCalculationResult).isSuccessful();
				will(returnValue(false));

				allowing(shippingCalculationResult).getErrorInformation();
				will(returnValue(Optional.of(errorInformation)));

				allowing(errorInformation).getCause();
				will(returnValue(Optional.of(cause)));

				allowing(shippingCalculationService).getPricedShippingOptions(pricedShippableItemContainer);
				will(returnValue(shippingCalculationResult));
			}
		});
	}

	@Override
	protected Product newProductImpl() {
		return new ProductImpl() {
			private static final long serialVersionUID = 5000000001L;

			@Override
			public String getDisplayName(final Locale locale) {
				return "Test Display Name";
			}
		};
	}

}
