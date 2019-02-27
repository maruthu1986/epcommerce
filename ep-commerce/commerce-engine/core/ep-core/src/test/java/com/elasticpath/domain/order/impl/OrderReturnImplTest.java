/*
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.domain.order.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static com.elasticpath.service.shoppingcart.impl.OrderSkuAsShoppingItemPricingSnapshotAction.returnTheSameOrderSkuAsPricingSnapshot;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Test;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.PriceTier;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.PriceImpl;
import com.elasticpath.domain.catalog.impl.PriceTierImpl;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.cmuser.impl.CmUserImpl;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.impl.CustomerAuthenticationImpl;
import com.elasticpath.domain.customer.impl.CustomerImpl;
import com.elasticpath.domain.misc.impl.RandomGuidImpl;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderAddress;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.domain.order.OrderReturnSku;
import com.elasticpath.domain.order.OrderReturnSkuReason;
import com.elasticpath.domain.order.OrderReturnStatus;
import com.elasticpath.domain.order.OrderReturnType;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.OrderShipmentStatus;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.OrderTaxValue;
import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartTaxSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.ShoppingItemPricingSnapshot;
import com.elasticpath.domain.shoppingcart.impl.ShoppingCartImpl;
import com.elasticpath.domain.tax.TaxCategory;
import com.elasticpath.money.Money;
import com.elasticpath.money.StandardMoneyFormatter;
import com.elasticpath.plugin.tax.domain.TaxAddress;
import com.elasticpath.plugin.tax.domain.TaxOperationContext;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.order.impl.OrderServiceImpl;
import com.elasticpath.service.order.impl.ReturnAndExchangeServiceImpl;
import com.elasticpath.service.shoppingcart.OrderSkuFactory;
import com.elasticpath.service.shoppingcart.PricingSnapshotService;
import com.elasticpath.service.shoppingcart.TaxSnapshotService;
import com.elasticpath.service.shoppingcart.impl.OrderSkuFactoryImpl;
import com.elasticpath.service.store.StoreService;
import com.elasticpath.service.store.WarehouseService;
import com.elasticpath.service.tax.TaxCalculationResult;
import com.elasticpath.service.tax.TaxCalculationService;
import com.elasticpath.service.tax.adapter.TaxAddressAdapter;
import com.elasticpath.service.tax.impl.ReturnTaxOperationServiceImpl;
import com.elasticpath.service.tax.impl.TaxCalculationResultImpl;
import com.elasticpath.service.tax.impl.TaxCalculationServiceImpl;
import com.elasticpath.test.jmock.AbstractEPServiceTestCase;

/**
 * Test cases for <code>OrderReturnImpl</code>.
 */
@SuppressWarnings({ "PMD.TooManyMethods", "PMD.ExcessiveImports", "PMD.ExcessiveClassLength", "PMD.GodClass" })
public class OrderReturnImplTest extends AbstractEPServiceTestCase {

	private static final Currency CURRENCY = Currency.getInstance("CAD");

	private OrderReturnImpl orderReturn;

	private Order orderImpl;

	private static final BigDecimal ITEM_TAX = new BigDecimal("5.72");

	private static final BigDecimal PRODUCT_PRICE = new BigDecimal("44");

	private static final String REGION_CODE_CA = "CA";

	private static final String REGION_CODE_BC = "BC";

	private static final String TEST_ORDER_NUMBER = "10000";

	private static final long TEST_UIDPK = 10000L;

	private static final String TAX_CODE = "GOODS";

	private static final BigDecimal SHIPPING_COST = new BigDecimal("8");

	private StoreService storeService;

	@Mock private ProductSkuLookup productSkuLookup;
	@Mock private PricingSnapshotService pricingSnapshotService;

	/**
	 * Prepare for the tests.
	 *
	 * @throws Exception on error
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		stubGetBean(ContextIdNames.CUSTOMER_AUTHENTICATION, CustomerAuthenticationImpl.class);
		stubGetBean(ContextIdNames.MONEY_FORMATTER, StandardMoneyFormatter.class);
		stubGetBean(ContextIdNames.PRODUCT_SKU_LOOKUP, productSkuLookup);
		stubGetBean(ContextIdNames.PRICE, PriceImpl.class);

		storeService = context.mock(StoreService.class);
		stubGetBean(ContextIdNames.STORE_SERVICE, storeService);

		context.checking(new Expectations() {
			{
				allowing(storeService).findStoreWithCode(with(any(String.class)));
				will(returnValue(getMockedStore()));
			}
		});

		orderReturn = new OrderReturnImpl();
		orderReturn.initialize();
		orderReturn.setOrderReturnAddress(mockOrderAddress());

		/* setup order. */
		setupOrder();

		orderReturn.setOrder(orderImpl);

		final OrderReturnSkuReason orderReturnSkuReason = new OrderReturnSkuReasonImpl();
		orderReturnSkuReason.setPropertiesMap(new HashMap<>());
		stubGetBean(ContextIdNames.ORDER_RETURN_SKU_REASON, orderReturnSkuReason);

		final OrderServiceImpl orderService = new OrderServiceImpl();
		orderService.setPersistenceEngine(getPersistenceEngine());
		stubGetBean(ContextIdNames.ORDER_SERVICE, orderService);

		ReturnTaxOperationServiceImpl returnTaxOperationService = new ReturnTaxOperationServiceImpl();
		returnTaxOperationService.setProductSkuLookup(productSkuLookup);
		returnTaxOperationService.setPricingSnapshotService(pricingSnapshotService);

		TaxAddressAdapter adapter = new TaxAddressAdapter();
		WarehouseService mockWarehouseService = mock(WarehouseService.class);
		when(mockWarehouseService.findAllWarehouses()).thenReturn(new ArrayList<>());
		returnTaxOperationService.setAddressAdapter(adapter);

		TaxCalculationService taxCalculationService = new TaxCalculationServiceImpl() {
			private final BigDecimal taxValue = new BigDecimal("0.10");

			private final Currency defaultCurrency = Currency.getInstance(Locale.US);

			private static final int CALCULATION_FINAL_SCALE = 2;

			@Override
			public TaxCalculationResult calculateTaxes(final String storeCode, final TaxAddress destinationAddress, final TaxAddress originAddress,
					final Money defaultShippingCost, final Map<? extends ShoppingItem, ShoppingItemPricingSnapshot> shoppingItemPricingSnapshotMap,
					final Money preTaxDiscount, final TaxOperationContext taxOperationContext) {
				final TaxCalculationResult taxResult = new TaxCalculationResultImpl();
				taxResult.setDefaultCurrency(Currency.getInstance(Locale.US));

				for (final Map.Entry<? extends ShoppingItem, ShoppingItemPricingSnapshot> entry : shoppingItemPricingSnapshotMap.entrySet()) {
					final ShoppingItem shoppingItem = entry.getKey();
					final ShoppingItemPricingSnapshot pricingSnapshot = entry.getValue();
					final Money curTax = Money.valueOf(
							pricingSnapshot.getTotal().getAmount().multiply(taxValue).setScale(CALCULATION_FINAL_SCALE,
									BigDecimal.ROUND_HALF_UP), defaultCurrency);
					taxResult.addItemTax(shoppingItem.getGuid(), curTax);
				}

				Money shippingCost = defaultShippingCost;
				if (shippingCost.getAmount() == null) {
					shippingCost = Money.valueOf(BigDecimal.ZERO.setScale(CALCULATION_FINAL_SCALE), defaultCurrency);
				}
				final Money shippingTax = Money.valueOf(
						shippingCost.getAmount().multiply(taxValue).setScale(CALCULATION_FINAL_SCALE, BigDecimal.ROUND_HALF_UP),
						defaultCurrency);
				taxResult.addShippingTax(shippingTax);
				taxResult.setBeforeTaxShippingCost(shippingCost);

				return taxResult;
			}
		};
		returnTaxOperationService.setBeanFactory(getBeanFactory());
		returnTaxOperationService.setTaxCalculationService(taxCalculationService);
		stubGetBean(ContextIdNames.RETURN_TAX_OPERATION_SERVICE, returnTaxOperationService);

		context.checking(new Expectations() {
			{
				allowing(pricingSnapshotService).getPricingSnapshotForOrderSku(with(any(OrderSku.class)));
				will(returnTheSameOrderSkuAsPricingSnapshot());
			}
		});
		stubGetBean(ContextIdNames.PRICING_SNAPSHOT_SERVICE, pricingSnapshotService);
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.OrderReturnImpl.getCreatedDate()'.
	 */
	@Test
	public void testGetSetCreatedDate() {
		final Date date = new Date();
		orderReturn.setCreatedDate(date);
		assertEquals(date, orderReturn.getCreatedDate());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.OrderReturnImpl.getLastModifiedDate()'.
	 */
	@Test
	public void testGetSetLastModifiedDate() {
		final Date date = new Date();
		orderReturn.setLastModifiedDate(date);
		assertEquals(date, orderReturn.getLastModifiedDate());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.OrderReturnImpl.getRmaCode()'.
	 */
	@Test
	public void testGetSetRmaCode() {
		final String rmaCode = "RMA_CODE";
		orderReturn.setRmaCode(rmaCode);
		assertEquals(rmaCode, orderReturn.getRmaCode());
	}

	/**
	 * /** Test method for 'com.elasticpath.domain.impl.OrderReturnImpl.getOrderReturnSkus()'.
	 */
	@Test
	public void testGetSetOrderReturnSkus() {
		Set<OrderReturnSku> returnSkus = new HashSet<>();
		orderReturn.setOrderReturnSkus(returnSkus);
		assertEquals(returnSkus, orderReturn.getOrderReturnSkus());
	}

	/**
	 * /** Test method for 'com.elasticpath.domain.impl.OrderReturnImpl.addOrderReturnSku()'.
	 */
	@Test
	public void testAddOrderReturnSku() {
		Set<OrderReturnSku> returnSkus = new HashSet<>();
		orderReturn.setOrderReturnSkus(returnSkus);
		assertSame(returnSkus, orderReturn.getOrderReturnSkus());

		OrderReturnSku returnSku = new OrderReturnSkuImpl();
		orderReturn.addOrderReturnSku(returnSku);
		assertEquals(1, orderReturn.getOrderReturnSkus().size());
		assertEquals(returnSku, orderReturn.getOrderReturnSkus().iterator().next());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.OrderReturnImpl.getReturnComment()'.
	 */
	@Test
	public void testGetSetReturnComment() {
		final String comment = "comment";
		orderReturn.setReturnComment(comment);
		assertEquals(comment, orderReturn.getReturnComment());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.OrderReturnImpl.getReturnStatus()'.
	 */
	@Test
	public void testGetSetReturnStatus() {
		orderReturn.setReturnStatus(OrderReturnStatus.AWAITING_COMPLETION);
		assertEquals(OrderReturnStatus.AWAITING_COMPLETION, orderReturn.getReturnStatus());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.OrderReturnImpl.getReturnType()'.
	 */
	@Test
	public void testGetSetReturnType() {
		orderReturn.setReturnType(OrderReturnType.EXCHANGE);
		assertEquals(OrderReturnType.EXCHANGE, orderReturn.getReturnType());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.OrderReturnImpl.getPhysicalReturn()'.
	 */
	@Test
	public void testGetSetPhysicalReturn() {
		orderReturn.setPhysicalReturn(true);
		assertTrue(orderReturn.getPhysicalReturn());
		orderReturn.setPhysicalReturn(false);
		assertFalse(orderReturn.getPhysicalReturn());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.OrderReturnImpl.getCreatedByCmUser()'.
	 */
	@Test
	public void testGetSetCreatedByCmUser() {
		CmUser testUser = new CmUserImpl();
		orderReturn.setCreatedByCmUser(testUser);
		assertEquals(testUser, orderReturn.getCreatedByCmUser());

	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.OrderReturnImpl.getReceivedByCmUser()'.
	 */
	@Test
	public void testGetSetReceivedByCmUser() {
		CmUser testUser = new CmUserImpl();
		orderReturn.setReceivedByCmUser(testUser);
		assertEquals(testUser, orderReturn.getReceivedByCmUser());

	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.OrderReturnImpl.getExchangeOrder()'.
	 */
	@Test
	public void testGetSetExchangeOrder() {
		final Order exchangeOrder = new OrderImpl();
		orderReturn.setExchangeOrder(exchangeOrder);
		assertEquals(exchangeOrder, orderReturn.getExchangeOrder());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.OrderReturnImpl.getReturnPayment()'.
	 */
	@Test
	public void testGetSetReturnPayment() {
		OrderPayment orderPayment = new OrderPaymentImpl();
		orderReturn.setReturnPayment(orderPayment);
		assertEquals(orderPayment, orderReturn.getReturnPayment());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.OrderReturnImpl.getCurrency()'.
	 */
	@Test
	public void testGetSetCurrency() {
		Currency currency = Currency.getInstance("USD");
		Order order = new OrderImpl();
		order.setCurrency(currency);
		orderReturn.setOrder(order);
		assertEquals(currency, orderReturn.getCurrency());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.OrderReturnImpl.getReturnTaxes()'.
	 */
	@Test
	public void testGetSetReturnTaxes() {
		assertNotNull(orderReturn.getReturnTaxes());

		Set<OrderTaxValue> returnTaxes = new HashSet<>();
		orderReturn.setReturnTaxes(returnTaxes);
		assertEquals(returnTaxes, orderReturn.getReturnTaxes());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.OrderReturnImpl.getBeforeTaxReturnTotalMoney()'.
	 */

	@Test

	public void testGetSetBeforeTaxReturnTotalMoney() {
		BigDecimal beforeTaxReturnTotal = BigDecimal.valueOf(2).setScale(2);
		orderReturn.setBeforeTaxReturnTotal(beforeTaxReturnTotal);
		assertEquals(beforeTaxReturnTotal, orderReturn.getBeforeTaxReturnTotalMoney().getAmount());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.OrderReturnImpl.getReturnTotalMoney()'.
	 */
	@Test
	public void testGetSetReturnTotalMoney() {
		BigDecimal returnTotal = BigDecimal.TEN.setScale(2);
		orderReturn.setReturnTotal(returnTotal);
		assertEquals(returnTotal, orderReturn.getReturnTotalMoney().getAmount());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.OrderReturnImpl.getTaxTotalMoney()'.
	 */
	@Test
	public void testGetSetTaxTotalMoney() {
		BigDecimal returnTaxTotal = BigDecimal.TEN.setScale(2);
		orderReturn.setTaxTotal(returnTaxTotal);
		assertEquals(returnTaxTotal, orderReturn.getTaxTotalMoney().getAmount());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.OrderReturnImpl.getLessRestockAmountMoney()'.
	 */
	@Test
	public void testGetLessRestockAmountMoney() {
		BigDecimal lessRestockAmount = BigDecimal.ONE.setScale(2);
		orderReturn.setLessRestockAmount(lessRestockAmount);
		assertEquals(lessRestockAmount, orderReturn.getLessRestockAmountMoney().getAmount());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.OrderReturnImpl.getShippingTaxMoney()'.
	 */
	@Test
	public void testGetShippingTaxMoney() {
		BigDecimal shippingTax = BigDecimal.ONE.setScale(2);
		orderReturn.setShippingTax(shippingTax);
		assertEquals(shippingTax, orderReturn.getShippingTaxMoney().getAmount());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.OrderReturnImpl.getRefundedTotalMoney()'.
	 */
	@Test
	public void testGetSetRefundedTotalMoney() {
		orderReturn.setReturnPayment(null);
		assertEquals(BigDecimal.ZERO.setScale(2), orderReturn.getRefundedTotalMoney().getAmount());

		OrderPaymentImpl orderPayment = new OrderPaymentImpl();
		orderPayment.setTransactionType(OrderPayment.CREDIT_TRANSACTION);
		orderPayment.setAmount(BigDecimal.ONE);
		orderReturn.setReturnPayment(orderPayment);
		assertEquals(BigDecimal.ONE.setScale(2), orderReturn.getRefundedTotalMoney().getAmount());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.OrderReturnImpl.getRefundTotalMoney()'.
	 */
	@Test
	public void testGetSetRefundTotalMoney() {

		final int expectedCartTotal = -8; // 2 - 10
		ShoppingCart shoppingCart = new ShoppingCartImpl();
		ShoppingCartTaxSnapshot pricingSnapshotForCart = new ShoppingCartImpl() {
			private static final long serialVersionUID = 2285944468697006262L;

			@Override
			public BigDecimal getTotal() { // return 10
				return BigDecimal.TEN.setScale(2);
			}
		};

		final int orderCartTotal = -9; // 2 - 11
		Order order = new OrderImpl() {
			private static final long serialVersionUID = -1299867884559350200L;

			@Override
			public BigDecimal getTotal() { // return 11
				return BigDecimal.TEN.add(BigDecimal.ONE);
			}
		};
		orderReturn.setReturnTotal(BigDecimal.valueOf(2));
		orderReturn.setReturnType(OrderReturnType.RETURN);
		assertEquals(2, orderReturn.getRefundTotalMoney().getAmount().intValue());

		orderReturn.setReturnType(OrderReturnType.EXCHANGE);
		orderReturn.setExchangeOrder(null);
		orderReturn.setExchangeShoppingCart(null, null);
		assertEquals(2, orderReturn.getRefundTotalMoney().getAmount().intValue());

		orderReturn.setExchangeShoppingCart(shoppingCart, pricingSnapshotForCart);
		assertEquals(expectedCartTotal, orderReturn.getRefundTotalMoney().getAmount().intValue());

		orderReturn.setExchangeOrder(order);
		assertEquals(orderCartTotal, orderReturn.getRefundTotalMoney().getAmount().intValue());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.OrderReturnImpl.getExchangeShoppingCart()'.
	 */
	@Test
	public void testGetSetExchangeShoppingCart() {
		ShoppingCart exchangeShoppingCart = new ShoppingCartImpl();
		ShoppingCartTaxSnapshot pricingSnapshotForCart = new ShoppingCartImpl();
		orderReturn.setExchangeShoppingCart(exchangeShoppingCart, pricingSnapshotForCart);
		assertEquals(exchangeShoppingCart, orderReturn.getExchangeShoppingCart());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.OrderReturnImpl.getSubtotalMoney()'.
	 */
	@Test
	public void testGetSetSubtotalMoney() {
		final BigDecimal subtotal = BigDecimal.ONE.setScale(2);
		orderReturn.setSubtotal(subtotal);
		assertEquals(subtotal, orderReturn.getSubtotalMoney().getAmount());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.OrderReturnImpl.getOrder()'.
	 */
	@Test
	public void testGetSetOrder() {
		orderReturn = new OrderReturnImpl();

		assertNull(orderReturn.getOrder());

		Order order = new OrderImpl();
		orderReturn.setOrder(order);
		assertEquals(order, orderReturn.getOrder());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.OrderReturnImpl.populateOrderReturn()'.
	 */
	@Test
	public void testPopulateOrderReturn() {
		mockTimeService();
		stubGetBean(ContextIdNames.ORDER_RETURN_SKU, OrderReturnSkuImpl.class);

		OrderReturn orderReturn = new OrderReturnImpl();
		orderReturn.populateOrderReturn(orderImpl, orderImpl.getAllShipments().get(0), OrderReturnType.RETURN);

		assertEquals(2, orderReturn.getOrderReturnSkus().size());
		assertEquals(OrderReturnType.RETURN, orderReturn.getReturnType());
		assertEquals(orderImpl, orderReturn.getOrder());
		assertNotNull(orderReturn.getCreatedDate());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.OrderReturnImpl.getOwedToCustomerMoney()'.
	 */
	@Test
	public void testGetOwedToCustomerMoney() {
		final BigDecimal returnTotal = BigDecimal.valueOf(10, 2);
		final BigDecimal orderPaymentAmount = BigDecimal.ONE;
		final BigDecimal totalAmount = returnTotal.subtract(orderPaymentAmount);

		OrderPaymentImpl orderPayment = new OrderPaymentImpl();
		orderPayment.setTransactionType(OrderPayment.CREDIT_TRANSACTION);
		orderPayment.setAmount(orderPaymentAmount);
		orderReturn.setReturnPayment(orderPayment);
		orderReturn.setReturnTotal(returnTotal);

		assertEquals(totalAmount, orderReturn.getOwedToCustomerMoney().getAmount());
	}

	/**
	 * Test update order return status to make sure it updates status after received quantity changes.
	 */
	@Test
	public void testUpdateOrderReturnStatus() {
		OrderReturnSku returnSku = new OrderReturnSkuImpl();
		returnSku.initialize();
		returnSku.setQuantity(2);
		returnSku.setReceivedQuantity(0);
		orderReturn.addOrderReturnSku(returnSku);
		orderReturn.updateOrderReturnStatus();
		assertEquals("Expected awaiting stock", OrderReturnStatus.AWAITING_STOCK_RETURN, orderReturn.getReturnStatus());
		returnSku.setReceivedQuantity(2);
		orderReturn.updateOrderReturnStatus();
		assertEquals("Expected awaiting completion", OrderReturnStatus.AWAITING_COMPLETION, orderReturn.getReturnStatus());
	}

	/**
	 * Test update order return status to make sure it doesn't update status when the order is cancelled or completed.
	 */
	@Test
	public void testUpdateOrderReturnStatusInTerminalState() {

		orderReturn.setReturnStatus(OrderReturnStatus.COMPLETED);
		OrderReturnSku returnSku = new OrderReturnSkuImpl();
		returnSku.initialize();
		returnSku.setQuantity(2);
		returnSku.setReceivedQuantity(0);
		orderReturn.addOrderReturnSku(returnSku);
		orderReturn.updateOrderReturnStatus();
		assertEquals("Expected completed", OrderReturnStatus.COMPLETED, orderReturn.getReturnStatus());

		orderReturn.setReturnStatus(OrderReturnStatus.CANCELLED);
		orderReturn.updateOrderReturnStatus();
		assertEquals("Expected cancelled", OrderReturnStatus.CANCELLED, orderReturn.getReturnStatus());
	}

	/**
	 * Test whether isFullyReceived method works correctly.
	 */
	@Test
	public void testIsFullyReceived() {
		OrderReturnSku returnSku = new OrderReturnSkuImpl();
		returnSku.initialize();
		returnSku.setQuantity(2);
		returnSku.setReceivedQuantity(0);
		orderReturn.addOrderReturnSku(returnSku);
		assertFalse("Expected not fully received.", orderReturn.isFullyReceived());
		returnSku.setReceivedQuantity(2);
		assertTrue("Expected fully received.", orderReturn.isFullyReceived());
		OrderReturnSku secondReturnSku = new OrderReturnSkuImpl();
		secondReturnSku.initialize();
		secondReturnSku.setQuantity(1);
		secondReturnSku.setReceivedQuantity(0);
		orderReturn.addOrderReturnSku(secondReturnSku);
		assertFalse("Expected not fully received.", orderReturn.isFullyReceived());
		secondReturnSku.setReceivedQuantity(1);
		assertTrue("Expected fully received.", orderReturn.isFullyReceived());
	}

	/**
	 * Test whether isPartiallyReceived method works correctly.
	 */
	@Test
	public void testIsPartiallyReceived() {
		OrderReturnSku returnSku = new OrderReturnSkuImpl();
		returnSku.initialize();
		returnSku.setQuantity(2);
		returnSku.setReceivedQuantity(0);
		orderReturn.addOrderReturnSku(returnSku);
		OrderReturnSku secondReturnSku = new OrderReturnSkuImpl();
		secondReturnSku.initialize();
		secondReturnSku.setQuantity(1);
		secondReturnSku.setReceivedQuantity(0);
		orderReturn.addOrderReturnSku(secondReturnSku);
		assertFalse("Expected not partially received", orderReturn.isPartiallyReceived());
		returnSku.setReceivedQuantity(1);
		assertTrue("Expected partially received", orderReturn.isPartiallyReceived());
		returnSku.setReceivedQuantity(2);
		assertTrue("Expected partially received", orderReturn.isPartiallyReceived());
		secondReturnSku.setReceivedQuantity(1);
		assertFalse("Expected not partially received", orderReturn.isPartiallyReceived());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.OrderReturnImpl.updateOrderReturnableQuantity()'.
	 */
	@Test
	public void testUpdateOrderReturnableQuantity() {
		mockTimeService();
		stubGetBean(ContextIdNames.ORDER_RETURN_SKU, OrderReturnSkuImpl.class);

		OrderReturn orderReturn = new OrderReturnImpl();

		long uidPk = 1L;

		final List<OrderReturn> returnsList = new ArrayList<>();
		returnsList.add(orderReturn);

		orderImpl.addReturn(orderReturn);
		orderImpl.setUidPk(uidPk);

		orderReturn.populateOrderReturn(orderImpl, orderImpl.getAllShipments().get(0), OrderReturnType.RETURN);

		for (OrderReturnSku orderReturnSku : orderReturn.getOrderReturnSkus()) {
			orderReturnSku.setQuantity(0);
		}

		ReturnAndExchangeServiceImpl returnAndExchangeService = new ReturnAndExchangeServiceImpl() {
			@Override
			public List<OrderReturn> list(final long uidPk) throws EpServiceException {
				return returnsList;
			}
		};

		stubGetBean(ContextIdNames.ORDER_RETURN_SERVICE, returnAndExchangeService);
		orderReturn.updateOrderReturnableQuantity(orderImpl, productSkuLookup);
		for (OrderSku orderSku : orderImpl.getOrderSkus()) {
			assertEquals(1, orderSku.getQuantity());
			assertEquals(1, orderSku.getReturnableQuantity());
		}
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.OrderReturnImpl.normalizeOrderReturn()'.
	 */
	@Test
	public void testNormalizeOrderReturn() {
		mockTimeService();
		stubGetBean(ContextIdNames.ORDER_RETURN_SKU, OrderReturnSkuImpl.class);

		OrderReturn orderReturn = new OrderReturnImpl();

		orderReturn.populateOrderReturn(orderImpl, orderImpl.getAllShipments().get(0), OrderReturnType.RETURN);

		assertEquals(2, orderReturn.getOrderReturnSkus().size());

		for (OrderReturnSku orderReturnSku : orderReturn.getOrderReturnSkus()) {
			orderReturnSku.setQuantity(1); // return all sku, 1 item each
		}

		orderReturn.getOrderReturnSkus().iterator().next().setQuantity(0); // don't return 1st sku

		orderReturn.normalizeOrderReturn();

		assertEquals(1, orderReturn.getOrderReturnSkus().size());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.OrderReturnImpl.recalculateOrderReturn()'.
	 * Without shipment discount (promotion) or restock amount
	 */
	@Test
	public void testRecalculateOrderReturn() {
		prepareTestRecalculateOrderReturn();
		orderReturn.setShippingCost(SHIPPING_COST);

		orderReturn.recalculateOrderReturn();

		assertEquals("order return subtotal equals product price * quantity.",
				new BigDecimal("88.00"), orderReturn.getSubtotal());  // 44*2
		assertEquals("order before tax return should include product and shipment cost",
				new BigDecimal("96.00"), orderReturn.getBeforeTaxReturnTotal());
		// 44 * 2 + 5.72 + 8 + 0.8 = 102.52
		assertEquals("order return total is product cost, shipment cost, taxes if there are neither shipment discount nor restock amount",
				new BigDecimal("102.52"), orderReturn.getReturnTotal());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.OrderReturnImpl.recalculateOrderReturn()'.
	 * With valid shipment discount (promotion) and restock amount
	 */
	@Test
	public void testRecalculateOrderReturnWithValidDiscountAndRestockAmount() {
		prepareTestRecalculateOrderReturn();
		orderReturn.setShipmentDiscount(new BigDecimal("20.0"));
		orderReturn.setLessRestockAmount(new BigDecimal("10.0"));
		orderReturn.setShippingCost(SHIPPING_COST);

		orderReturn.recalculateOrderReturn();

		// 44 * 2 + 5.72 + 8 + 0.8 - 20 - 10 = 72.52
		assertEquals("order return total is product cost, shipment cost, taxes, subtract discount and restock amount",
				new BigDecimal("72.52"), orderReturn.getReturnTotal());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.OrderReturnImpl.recalculateOrderReturn()'.
	 * With invalid shipment discount (promotion) but valid restock amount
	 */
	@Test
	public void testRecalculateOrderReturnWithInvalidShippingDiscount() {
		prepareTestRecalculateOrderReturn();
		orderReturn.setShipmentDiscount(new BigDecimal("120.0"));
		orderReturn.setLessRestockAmount(new BigDecimal("10.0"));
		orderReturn.setShippingCost(SHIPPING_COST);

		orderReturn.recalculateOrderReturn();

		// 44 * 2 + 5.72 + 8 + 0.8 - 10 = 92.52
		assertEquals("order return total calculation should neglect invalid shipping discount",
				new BigDecimal("92.52"), orderReturn.getReturnTotal());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.OrderReturnImpl.recalculateOrderReturn()'.
	 * With valid shipment discount (promotion) but invalid restock amount
	 */
	@Test
	public void testRecalculateOrderReturnWithInvalidRestockAmount() {
		prepareTestRecalculateOrderReturn();
		orderReturn.setShipmentDiscount(new BigDecimal("20.0"));
		orderReturn.setLessRestockAmount(new BigDecimal("100.0"));
		orderReturn.setShippingCost(SHIPPING_COST);

		orderReturn.recalculateOrderReturn();

		// 44 * 2 + 5.72 + 8 + 0.8 - 20 = 82.52
		assertEquals("order return total calculation should neglect invalid restock amount",
				new BigDecimal("82.52"), orderReturn.getReturnTotal());
	}

	@SuppressWarnings("unchecked")
	private void prepareTestRecalculateOrderReturn() {
		final OrderSkuFactory orderSkuFactory = new OrderSkuFactoryImpl() {
			@Override
			protected String getTaxCode(final ProductSku productSku) {
				return TAX_CODE;
			}

			@Override
			protected OrderSku createSimpleOrderSku() {
				return new OrderSkuImpl();
			}
		};
		stubGetBean(ContextIdNames.ORDER_SKU_FACTORY, orderSkuFactory);

		final TaxCalculationService mockTaxCalculationService = context.mock(TaxCalculationService.class);
		stubGetBean(ContextIdNames.TAX_CALCULATION_SERVICE, mockTaxCalculationService);

		final TaxSnapshotService mockTaxSnapshotService = context.mock(TaxSnapshotService.class);
		stubGetBean(ContextIdNames.TAX_SNAPSHOT_SERVICE, mockTaxSnapshotService);

		final Set<OrderSku> orderSkus = mockOrderSkuList();
		final OrderSku orderSku = orderSkus.iterator().next();

		final TaxCalculationResult taxResult = mockTaxCalculationResult();
		context.checking(new Expectations() {
			{
				allowing(mockTaxCalculationService).calculateTaxes(
						with(any(String.class)),
						with(any(TaxAddress.class)),
						with(any(TaxAddress.class)),
						with(any(Money.class)),
						with(any(Map.class)),
						with(any(Money.class)),
						with(any(TaxOperationContext.class)));
				will(returnValue(taxResult));

				allowing(pricingSnapshotService).getPricingSnapshotForOrderSku(orderSku);
				will(returnValue(orderSku));

				allowing(mockTaxSnapshotService).getTaxSnapshotForOrderSku(orderSku, (ShoppingItemPricingSnapshot) orderSku);
				will(returnValue(orderSku));
			}
		});

		stubGetBean(ContextIdNames.ORDER_TAX_VALUE, new OrderTaxValueImpl());

		OrderReturnSku returnSku = new OrderReturnSkuImpl();
		returnSku.initialize();
		returnSku.setQuantity(2);
		returnSku.setReceivedQuantity(0);

		orderSku.setShipment(getMockPhysicalOrderShipment());
		returnSku.setOrderSku(orderSku);
		orderSku.setPrice(2, getPrice(CURRENCY, PRODUCT_PRICE, PRODUCT_PRICE));
		orderReturn.addOrderReturnSku(returnSku);
		orderReturn.setOrder(orderImpl);
	}

	private TaxCalculationResult mockTaxCalculationResult() {
		final TaxCategory taxCategory = context.mock(TaxCategory.class);
		context.checking(new Expectations() {
			{
				allowing(taxCategory).getDisplayName(with(any(Locale.class)));
				will(returnValue("PST"));

				allowing(taxCategory).getName();
				will(returnValue("PST"));
			}
		});

		final TaxCalculationResult mockTaxResult = context.mock(TaxCalculationResult.class);
		final Money money = Money.valueOf("0.8", Currency.getInstance(Locale.US));
		context.checking(new Expectations() {
			{
				allowing(mockTaxResult).getShippingTax();
				will(returnValue(money));

				allowing(mockTaxResult).getTaxValue(with(any(TaxCategory.class)));
				will(returnValue(money));
			}
		});
		final Collection<TaxCategory> taxCategories = new ArrayList<>();
		taxCategories.add(taxCategory);
		context.checking(new Expectations() {
			{
				allowing(mockTaxResult).getTaxCategoriesIterator();
				will(returnValue(taxCategories.iterator()));
			}
		});

		return mockTaxResult;
	}

	/**
	 *
	 */
	private void setupOrder() {
		orderImpl = new TestOrderImpl();
		orderImpl.initialize();
		orderImpl.setCurrency(CURRENCY);
		orderImpl.setLocale(Locale.CANADA);
		orderImpl.setCustomer(getCustomer());
		orderImpl.setBillingAddress(mockOrderAddress());
		orderImpl.setOrderPayments(mockOrderPayments());
		orderImpl.setUidPk(TEST_UIDPK);
		orderImpl.setOrderNumber(TEST_ORDER_NUMBER);
		OrderShipment orderShipment = mockOrderShipment();
		orderImpl.addShipment(orderShipment);
		orderImpl.setStoreCode(getMockedStore().getCode());
		orderShipment.setStatus(OrderShipmentStatus.SHIPPED);
	}

	private Set<OrderPayment> mockOrderPayments() {
		final OrderPayment orderPayment = new OrderPaymentImpl();
		orderPayment.setTransactionType(OrderPayment.CAPTURE_TRANSACTION);
		final Set<OrderPayment> paymentSet = new HashSet<>();
		paymentSet.add(orderPayment);
		return paymentSet;
	}

	private OrderShipment mockOrderShipment() {
		final PhysicalOrderShipment orderShipment = getMockPhysicalOrderShipment();
		orderShipment.setStatus(OrderShipmentStatus.INVENTORY_ASSIGNED);
		orderShipment.setSubtotalDiscount(BigDecimal.ZERO.setScale(2));
		orderShipment.setShippingCost(BigDecimal.ZERO.setScale(2));

		orderShipment.setShipmentAddress(mockOrderAddress());
		Set<OrderSku> orderSkuSet = mockOrderSkuList();
		for (OrderSku sku : orderSkuSet) {
			orderShipment.addShipmentOrderSku(sku);
		}

		return orderShipment;
	}

	private PhysicalOrderShipmentImpl getMockPhysicalOrderShipment() {

		final TaxCalculationResult result = new TaxCalculationResultImpl() {
			private static final long serialVersionUID = -6367307173387929904L;

			@Override
			public Money getBeforeTaxShippingCost() {
				return Money.valueOf(BigDecimal.ONE, Currency.getInstance(Locale.US));
			}

			@Override
			public Money getBeforeTaxSubTotal() {
				return Money.valueOf(BigDecimal.TEN, Currency.getInstance(Locale.US));
			}

			@Override
			public void applyTaxes(final Collection<? extends ShoppingItem> shoppingItems) { //NOPMD
			}
		};
		result.setDefaultCurrency(Currency.getInstance(Locale.US));

		PhysicalOrderShipmentImpl physicalOrderShipmentImpl = new PhysicalOrderShipmentImpl() {
			private static final long serialVersionUID = -8878050727570071273L;

			@Override
			public TaxCalculationResult calculateTaxes() {
				return result;
			}
		};

		return physicalOrderShipmentImpl;

	}

	private OrderAddress mockOrderAddress() {
		final OrderAddress address = new OrderAddressImpl();

		address.setFirstName("Joe");
		address.setLastName("Doe");
		address.setCountry(REGION_CODE_CA);
		address.setStreet1("1295 Charleston Road");
		address.setCity("Mountain View");
		address.setSubCountry(REGION_CODE_BC);
		address.setZipOrPostalCode("94043");
		return address;
	}

	private Set<OrderSku> mockOrderSkuList() {
		final Set<OrderSku> orderSkus = new HashSet<>();
		// Create mock cart items
		OrderSku orderSku = new OrderSkuImpl();
		orderSku.setUidPk(Calendar.getInstance().getTimeInMillis());
		ProductSku productSkuImpl = mockProductSku();

		orderSku.setSkuGuid(productSkuImpl.getGuid());
		//Tax amount needs to be set, normally set by TaxCalculationService
		orderSku.setTaxAmount(ITEM_TAX);
		BigDecimal unitPrice = getPrice(CURRENCY, PRODUCT_PRICE, PRODUCT_PRICE).getLowestPrice().getAmount();

		// needed for tests that call populateOrderReturn
		PriceImpl price = new PriceImpl();
		price.setCurrency(Currency.getInstance("CAD"));
		orderSku.setPrice(1, price);

		orderSku.setUnitPrice(unitPrice);
		orderSku.setGuid("OSKUGUID1");
		orderSkus.add(orderSku);
		OrderSku orderSku2 = new OrderSkuImpl();
		orderSku2.setUidPk(Calendar.getInstance().getTimeInMillis());
		productSkuImpl = mockProductSku();
		//Tax amount needs to be set, normally set by TaxCalculationService
		orderSku2.setTaxAmount(ITEM_TAX);

		orderSku2.setGuid("OSKUGUID2");
		orderSku2.setSkuGuid(productSkuImpl.getGuid());
		orderSku2.setPrice(1, price);

		orderSku2.setUnitPrice(unitPrice);
		orderSkus.add(orderSku2);

		return orderSkus;
	}

	private ProductSku mockProductSku() {
		final ProductSku productSku = new ProductSkuImpl();
		productSku.initialize();
		productSku.setUidPk(Calendar.getInstance().getTimeInMillis());
		productSku.setWeight(BigDecimal.ONE);

		final Product productImpl = new ProductImpl();
		productImpl.setGuid("irrelevant product guid" + new RandomGuidImpl());
		productImpl.setProductSkus(new HashMap<>());
		productSku.setProduct(productImpl);
		productImpl.addOrUpdateSku(productSku);

		context.checking(new Expectations() {
			{
				allowing(productSkuLookup).findByGuid(productSku.getGuid());
				will(returnValue(productSku));
			}
		});
		return productSku;
	}

	/**
	 * Implementation of Order with auto-recalculation enabled by default for testing purposes.
	 */
	class TestOrderImpl extends OrderImpl {
		private static final long serialVersionUID = 2197342999715348692L;

		/**
		 * Override default constructor to enable auto-recalculation.
		 */
		TestOrderImpl() {
			super();
			enableRecalculation();
		}
	}

	/**
	 * @return a new <code>Customer</code> instance.
	 */
	protected Customer getCustomer() {
		final Customer customer = new CustomerImpl();
		customer.setGuid(new RandomGuidImpl().toString());
		customer.initialize();
		//		customer.setStore(getMockedStore());
		return customer;
	}

	/**
	 * Returns a new <code>Price</code> instance.
	 *
	 * @return a new <code>Price</code> instance.
	 */
	private Price getPrice() {
		PriceImpl price = new PriceImpl();
		price.addOrUpdatePriceTier(getPriceTier());
		price.setCurrency(Currency.getInstance(Locale.CANADA));
		price.initialize();
		return price;
	}

	/**
	 * Returns a new <code>PriceTier</code> instance.
	 *
	 * @return a new <code>PriceTier</code> instance.
	 */
	private PriceTier getPriceTier() {
		PriceTier priceTier = new PriceTierImpl();
		priceTier.initialize();
		return priceTier;
	}


	/**
	 * Returns a new <code>Price</code> instance with the currency, listPrice, and salePrice
	 * set.
	 *
	 * @param currency Currency
	 * @param listPrice the listPrice for this <code>Price</code>
	 * @param salePrice the salePrice for this <code>Price</code>
	 * @return a new <code>Price</code> instance.
	 */

	protected Price getPrice(final Currency currency, final BigDecimal listPrice, final BigDecimal salePrice) {
		Price price = getPrice();
		price.setCurrency(currency);

		Money listPriceMoney = Money.valueOf(listPrice, currency);
		price.setListPrice(listPriceMoney);

		Money salePriceMoney = Money.valueOf(salePrice, currency);
		price.setSalePrice(salePriceMoney);

		return price;
	}

	private void mockTimeService() {
		final TimeService timeService = context.mock(TimeService.class);
		stubGetBean(ContextIdNames.TIME_SERVICE, timeService);

		context.checking(new Expectations() {
			{
				allowing(timeService).getCurrentTime();
				will(returnValue(new Date()));
			}
		});
	}
}
