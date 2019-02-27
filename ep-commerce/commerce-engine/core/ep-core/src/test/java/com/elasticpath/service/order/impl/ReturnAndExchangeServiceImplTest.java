/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */

package com.elasticpath.service.order.impl; // NOPMD All Imports required

import static java.util.Collections.singletonList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Test;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.LocaleDependantFields;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.PriceTier;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.PriceImpl;
import com.elasticpath.domain.catalog.impl.PriceTierImpl;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.catalog.impl.ProductLocaleDependantFieldsImpl;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.catalog.impl.ProductTypeImpl;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.customer.impl.CustomerAddressImpl;
import com.elasticpath.domain.customer.impl.CustomerAuthenticationImpl;
import com.elasticpath.domain.customer.impl.CustomerImpl;
import com.elasticpath.domain.customer.impl.CustomerSessionImpl;
import com.elasticpath.domain.event.EventOriginator;
import com.elasticpath.domain.event.OrderEventHelper;
import com.elasticpath.domain.event.impl.EventOriginatorHelperImpl;
import com.elasticpath.domain.event.impl.EventOriginatorImpl;
import com.elasticpath.domain.event.impl.OrderEventHelperImpl;
import com.elasticpath.domain.misc.impl.LocalizedPropertiesImpl;
import com.elasticpath.domain.misc.impl.RandomGuidImpl;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderAddress;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderPaymentStatus;
import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.domain.order.OrderReturnSkuReason;
import com.elasticpath.domain.order.OrderReturnStatus;
import com.elasticpath.domain.order.OrderReturnType;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.OrderShipmentStatus;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.domain.order.impl.OrderAddressImpl;
import com.elasticpath.domain.order.impl.OrderEventImpl;
import com.elasticpath.domain.order.impl.OrderImpl;
import com.elasticpath.domain.order.impl.OrderPaymentImpl;
import com.elasticpath.domain.order.impl.OrderReturnImpl;
import com.elasticpath.domain.order.impl.OrderReturnSkuImpl;
import com.elasticpath.domain.order.impl.OrderReturnSkuReasonImpl;
import com.elasticpath.domain.order.impl.OrderSkuImpl;
import com.elasticpath.domain.order.impl.PhysicalOrderShipmentImpl;
import com.elasticpath.domain.payment.CreditCardPaymentGateway;
import com.elasticpath.domain.payment.PaymentGateway;
import com.elasticpath.domain.payment.PaymentHandlerFactory;
import com.elasticpath.domain.payment.impl.PaymentHandlerFactoryImpl;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.impl.ShoppingCartImpl;
import com.elasticpath.domain.shoppingcart.impl.ShoppingCartMementoImpl;
import com.elasticpath.domain.shoppingcart.impl.ShoppingItemImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.domain.tax.TaxCategory;
import com.elasticpath.domain.tax.TaxCategoryTypeEnum;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.domain.tax.TaxJurisdiction;
import com.elasticpath.domain.tax.TaxRegion;
import com.elasticpath.domain.tax.TaxValue;
import com.elasticpath.domain.tax.impl.TaxCategoryImpl;
import com.elasticpath.domain.tax.impl.TaxCodeImpl;
import com.elasticpath.domain.tax.impl.TaxJurisdictionImpl;
import com.elasticpath.domain.tax.impl.TaxRegionImpl;
import com.elasticpath.domain.tax.impl.TaxValueImpl;
import com.elasticpath.inventory.impl.InventoryDtoImpl;
import com.elasticpath.money.Money;
import com.elasticpath.money.StandardMoneyFormatter;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.persistence.support.OrderCriterion;
import com.elasticpath.persistence.support.impl.FetchGroupLoadTunerImpl;
import com.elasticpath.persistence.support.impl.OrderCriterionImpl;
import com.elasticpath.plugin.payment.PaymentType;
import com.elasticpath.plugin.tax.domain.TaxAddress;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.service.customer.CustomerSessionService;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.misc.impl.DatabaseServerTimeServiceImpl;
import com.elasticpath.service.misc.impl.OpenJPAFetchPlanHelperImpl;
import com.elasticpath.service.order.IllegalReturnStateException;
import com.elasticpath.service.order.OrderReturnValidator;
import com.elasticpath.service.order.ReturnAndExchangeService;
import com.elasticpath.service.order.ReturnExchangeType;
import com.elasticpath.service.payment.PaymentService;
import com.elasticpath.service.payment.impl.PaymentResultImpl;
import com.elasticpath.service.payment.impl.PaymentServiceImpl;
import com.elasticpath.service.shipping.ShippingOptionResult;
import com.elasticpath.service.shipping.ShippingOptionService;
import com.elasticpath.service.shipping.impl.ShippingOptionResultImpl;
import com.elasticpath.service.shopper.ShopperService;
import com.elasticpath.service.shoppingcart.CheckoutService;
import com.elasticpath.service.shoppingcart.PricingSnapshotService;
import com.elasticpath.service.shoppingcart.ShoppingItemSubtotalCalculator;
import com.elasticpath.service.shoppingcart.TaxSnapshotService;
import com.elasticpath.service.shoppingcart.impl.BundleApportioningCalculatorImpl;
import com.elasticpath.service.shoppingcart.impl.OrderSkuFactoryImpl;
import com.elasticpath.service.store.StoreService;
import com.elasticpath.service.tax.TaxCalculationResult;
import com.elasticpath.service.tax.TaxCodeRetriever;
import com.elasticpath.service.tax.TaxJurisdictionService;
import com.elasticpath.service.tax.adapter.TaxAddressAdapter;
import com.elasticpath.service.tax.impl.DiscountApportioningCalculatorImpl;
import com.elasticpath.service.tax.impl.ReturnTaxOperationServiceImpl;
import com.elasticpath.service.tax.impl.TaxCalculationResultImpl;
import com.elasticpath.service.tax.impl.TaxCalculationServiceImpl;
import com.elasticpath.service.tax.impl.TaxJurisdictionServiceImpl;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;
import com.elasticpath.shipping.connectivity.dto.impl.ShippingOptionImpl;
import com.elasticpath.test.factory.TestCustomerSessionFactory;
import com.elasticpath.test.factory.TestShopperFactory;
import com.elasticpath.test.factory.TestTaxCalculationServiceImpl;
import com.elasticpath.test.jmock.AbstractEPServiceTestCase;

/**
 * Test cases for <code>ReturnAndExchangeServiceImpl</code>.
 */
@SuppressWarnings({ "PMD.ExcessiveClassLength", "PMD.TooManyMethods", "PMD.TooManyFields" })
public class ReturnAndExchangeServiceImplTest extends AbstractEPServiceTestCase {

	private static final String ORDER_NUMBER = "10000";

	private static final BigDecimal ITEM_TAX = new BigDecimal("5.72");

	private static final String CARD_HOLDER_NAME = "John";

	private static final String SALES_TAX_CODE_GOODS = "GOODS";

	private static final Currency CURRENCY = Currency.getInstance("CAD");

	private static final BigDecimal PRODUCT_PRICE = new BigDecimal("44");

	private static final String PST_TAX_CODE = "PST";

	private static final String GST_TAX_CODE = "GST";

	private static final String REGION_CODE_CA = "CA";

	private static final Locale DEFAULT_LOCALE = Locale.CANADA;

	private static final String REGION_CODE_BC = "BC";

	private static final String FAKE_ORDER_NUMBER = ORDER_NUMBER;

	private static final long FAKE_UIDPK = 10000L;

	private Order orderImpl;

	private ReturnAndExchangeServiceImpl exchangeService;

	private OrderServiceImpl orderServiceImpl;

	private CheckoutService mockCheckoutService;

	private CustomerSessionService mockCustomerSessionService;

	private ShopperService mockShopperService;

	private TimeService mockTimeService;

	private ProductSkuLookup mockProductSkuLookup;

	private ShippingOption shippingOption;

	private ProductSku productSku;

	private static final long DAY = 1000 * 60 * 60 * 24;

	private static final String CART_CURRENCY = "CAD";
	private StoreService storeService;

	private OrderReturnValidator mockOrderReturnValidator;

	@Mock
	private PricingSnapshotService pricingSnapshotService;

	@Mock
	private TaxSnapshotService taxSnapshotService;

	@Mock
	private ShoppingItemSubtotalCalculator shoppingItemSubtotalCalculator;

	/**
	 * Prepare for the tests.
	 *
	 * @throws Exception on error
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();

		mockProductSkuLookup = context.mock(ProductSkuLookup.class);
		stubGetBean(ContextIdNames.CUSTOMER_AUTHENTICATION, CustomerAuthenticationImpl.class);
		stubGetBean(ContextIdNames.MONEY_FORMATTER, StandardMoneyFormatter.class);
		stubGetBean(ContextIdNames.PRODUCT_SKU_LOOKUP, mockProductSkuLookup);
		stubGetBean(ContextIdNames.SHOPPING_ITEM_SUBTOTAL_CALCULATOR, shoppingItemSubtotalCalculator);

		/* setup order. */
		setupOrder();
		/* setup order service impl. */
		setupOrderService();
		/* setup checkout service: */
		setupCheckoutService();
		/* set up exchange service */
		setupReturnService();
		/* setup for order auditing */
		setUpForOrderAuditing();

		/* setup return tax operation service. */
		setUpTaxCalculationService();

		stubGetBean(ContextIdNames.ORDER_EVENT_HELPER, new OrderEventHelperImpl() {
			@Override
			public void logOrderExchangeCreated(final Order order) {
				// skip logging of the event
			}
		});

		setUpOrderSkuFactory();
	}

	private void setUpTaxCalculationService() {
		TaxCalculationServiceImpl taxCalculationService = new TestTaxCalculationServiceImpl();
		taxCalculationService.setBeanFactory(getBeanFactory());

		final TaxJurisdictionService taxJurisdictionService = new TaxJurisdictionServiceImpl() {
			@Override
			public TaxJurisdiction retrieveEnabledInStoreTaxJurisdiction(final String storeCode, final TaxAddress address) throws EpServiceException {
				return getTaxJurisdiction();
			}
		};

		taxCalculationService.setTaxJurisdictionService(taxJurisdictionService);
		taxCalculationService.setStoreService(storeService);

		ReturnTaxOperationServiceImpl taxOperationService = new ReturnTaxOperationServiceImpl();
		taxOperationService.setTaxCalculationService(taxCalculationService);
		taxOperationService.setBeanFactory(getBeanFactory());

		stubGetBean(ContextIdNames.RETURN_TAX_OPERATION_SERVICE, taxOperationService);
		stubGetBean(ContextIdNames.TAX_CALCULATION_SERVICE, taxCalculationService);
	}

	private void setupOrder() {
		orderImpl = new TestOrderImpl();
		orderImpl.initialize();
		final Store store = getMockedStore();
		final Set <TaxCode> taxCodes = new HashSet<>();
		taxCodes.add(createTaxCode(TaxCode.TAX_CODE_SHIPPING));
		taxCodes.add(createTaxCode(SALES_TAX_CODE_GOODS));
		store.setTaxCodes(taxCodes);
		orderImpl.setStoreCode(store.getCode());


		orderImpl.setUidPk(FAKE_UIDPK);
		orderImpl.setOrderNumber(FAKE_ORDER_NUMBER);
		orderImpl.setCurrency(CURRENCY);
		orderImpl.setLocale(DEFAULT_LOCALE);
		orderImpl.setCustomer(getCustomer());
		orderImpl.setBillingAddress(mockOrderAddress());
		final OrderShipment shipment = mockOrderShipment();
		orderImpl.addShipment(shipment);
		orderImpl.setOrderPayments(mockOrderPayments(shipment));
		orderImpl.setModifiedBy(new EventOriginatorImpl());
	}

	private void setupCheckoutService() {
		mockCheckoutService = context.mock(CheckoutService.class);
	}

	private void setUpForOrderAuditing() {
		final OrderEventHelperImpl orderEventHelper = new OrderEventHelperImpl();
		orderEventHelper.setTimeService(new DummyTimeService());

		stubGetBean(ContextIdNames.ORDER_EVENT, OrderEventImpl.class);
		stubGetBean(ContextIdNames.ORDER_EVENT_HELPER, orderEventHelper);
		stubGetBean(ContextIdNames.EVENT_ORIGINATOR, EventOriginatorImpl.class);
		stubGetBean(ContextIdNames.EVENT_ORIGINATOR_HELPER, EventOriginatorHelperImpl.class);
	}

	private void setupOrderService() {
		orderServiceImpl = new OrderServiceImpl() {

			@Override
			public Order get(final long orderUid) throws EpServiceException {
				final Order order = new OrderImpl();
				order.setOrderPayments(mockOrderPayments(null));
				return order;
			}

			@Override
			public Order add(final Order order) throws EpServiceException {
				final Order savedOrder = super.add(order);
				savedOrder.setUidPk(1);
				savedOrder.setStoreCode(getMockedStore().getCode());
				return savedOrder;
			}

			@Override
			public Order update(final Order order) throws EpServiceException {
				order.setStoreCode(getMockedStore().getCode());
				return order;
			}

			@Override
			public Order processRefundOrderPayment(final long orderUid, final String shipmentNumber, final OrderPayment refundPayment,
					final BigDecimal refundAmount, final EventOriginator eventOriginator) {
				final Order returnOrder = new OrderImpl();
				refundPayment.setAmount(refundAmount);
				refundPayment.setStatus(OrderPaymentStatus.APPROVED);
				final Set<OrderPayment> paymentList = new HashSet<>();
				paymentList.add(refundPayment);
				returnOrder.setOrderPayments(paymentList);
				return returnOrder;
			}
		};
		orderServiceImpl.setPersistenceEngine(getPersistenceEngine());
		orderServiceImpl.setFetchPlanHelper(getFetchPlanHelper());

		OrderCriterion orderCriterion = new OrderCriterionImpl();
		stubGetBean(ContextIdNames.ORDER_CRITERION, orderCriterion);

		mockTimeService = context.mock(TimeService.class);
		context.checking(new Expectations() {
			{
				allowing(mockTimeService).getCurrentTime();
				will(returnValue(new Date()));
			}
		});
		orderServiceImpl.setTimeService(mockTimeService);

		stubGetBean(ContextIdNames.ORDER_SERVICE, orderServiceImpl);

	}

	@SuppressWarnings("unchecked")
	private void setupReturnService() {
		exchangeService = new ReturnAndExchangeServiceImpl() {
			@Override
			protected ReturnAndExchangeService getReturnAndExchangeService() {
				return exchangeService;
			}

			@Override
			protected OrderEventHelper getOrderEventHelper() {
				final OrderEventHelperImpl orderEventHelper = new OrderEventHelperImpl();
				orderEventHelper.setTimeService(new DummyTimeService());
				return orderEventHelper;
			}
		};
		exchangeService.setPersistenceEngine(getPersistenceEngine());

		mockCustomerSessionService = context.mock(CustomerSessionService.class);
		exchangeService.setCustomerSessionService(mockCustomerSessionService);

		mockShopperService = context.mock(ShopperService.class);
		exchangeService.setShopperService(mockShopperService);

		mockOrderReturnValidator = context.mock(OrderReturnValidator.class);
		exchangeService.setOrderReturnValidator(mockOrderReturnValidator);

		context.checking(new Expectations() {
			{
				allowing(mockTimeService).getCurrentTime();
				will(returnValue(new Date()));
			}
		});
		exchangeService.setTimeService(mockTimeService);
		exchangeService.setProductSkuLookup(mockProductSkuLookup);

		shippingOption = getShippingOption();

		final ShippingOptionService shippingOptionService = context.mock(ShippingOptionService.class);
		context.checking(new Expectations() {
			{
				allowing(shippingOptionService).getShippingOptions(with(any(ShoppingCart.class)));
				will(returnValue(createShippingOptionResult(singletonList(shippingOption))));
			}
		});

		exchangeService.setShippingOptionService(shippingOptionService);

		stubGetBean(ContextIdNames.CUSTOMER_SESSION, new CustomerSessionImpl());

		storeService = context.mock(StoreService.class);
		context.checking(new Expectations() {
			{
				allowing(storeService).findStoreWithCode(getMockedStore().getCode());
				will(returnValue(getMockedStore()));
			}
		});

		final OrderReturnSkuReason orderReturnSkuReason = new OrderReturnSkuReasonImpl();
		orderReturnSkuReason.setPropertiesMap(new HashMap<>());
		stubGetBean(ContextIdNames.ORDER_RETURN_SKU_REASON, orderReturnSkuReason);

		exchangeService.setCheckoutService(mockCheckoutService);
		exchangeService.setOrderService(orderServiceImpl);
		exchangeService.setPaymentService(getPaymentService());
		exchangeService.setStoreService(storeService);
		exchangeService.setPricingSnapshotService(pricingSnapshotService);
		exchangeService.setTaxSnapshotService(taxSnapshotService);

		context.checking(new Expectations() {
			{
				allowing(shoppingItemSubtotalCalculator).calculate(
						with(any(Stream.class)),
						with(any(ShoppingCartPricingSnapshot.class)),
						with(any(Currency.class))
				);
				will(returnValue(Money.valueOf(BigDecimal.ZERO, CURRENCY)));
			}
		});

	}

	private void setUpOrderSkuFactory() {
		final TaxCodeImpl taxCode = new TaxCodeImpl();
		taxCode.setCode(SALES_TAX_CODE_GOODS);

		final TaxCodeRetriever taxCodeRetriever = context.mock(TaxCodeRetriever.class);
		context.checking(new Expectations() {
			{
				allowing(taxCodeRetriever).getEffectiveTaxCode(with(any(ProductSku.class)));
				will(returnValue(taxCode));

				allowing(mockTimeService).getCurrentTime();
				will(returnValue(new Date()));
			}
		});

		OrderSkuFactoryImpl orderSkuFactory = new OrderSkuFactoryImpl() {
			@Override
			protected OrderSku createSimpleOrderSku() {
				return new OrderSkuImpl();
			}
		};
		orderSkuFactory.setTaxCodeRetriever(taxCodeRetriever);
		orderSkuFactory.setProductSkuLookup(mockProductSkuLookup);
		orderSkuFactory.setBundleApportioner(new BundleApportioningCalculatorImpl());
		orderSkuFactory.setDiscountApportioner(new DiscountApportioningCalculatorImpl());
		orderSkuFactory.setTimeService(mockTimeService);
		stubGetBean(ContextIdNames.ORDER_SKU_FACTORY, orderSkuFactory);
	}

	/**
	 * Test method for 'com.elasticpath.service.ReturnAndExchangeServiceImpl.add()'.
	 */
	@Test
	public void testAdd() {
		final OrderReturn orderReturn = new OrderReturnImpl();
		orderReturn.setOrder(new OrderImpl());
		context.checking(new Expectations() {
			{
				oneOf(getMockPersistenceEngine()).save(with(same(orderReturn)));
			}
		});
		exchangeService.add(orderReturn);
	}

	/**
	 * Test method for 'com.elasticpath.service.ReturnAndExchangeServiceImpl.add() with dummy payment'.
	 */
	@Test
	public void testAdd2() {
		stubGetBean(ContextIdNames.ORDER_PAYMENT, OrderPaymentImpl.class);

		final OrderReturn orderReturn = new OrderReturnImpl();
		orderReturn.setOrder(new OrderImpl());
		final OrderPaymentImpl orderPayment = new OrderPaymentImpl();
		orderPayment.setUidPk(1);
		orderReturn.setReturnPayment(orderPayment);
		// expectations
		context.checking(new Expectations() {
			{
				oneOf(getMockPersistenceEngine()).get(with(same(OrderPaymentImpl.class)), with(orderPayment.getUidPk()));
				will(returnValue(orderPayment));

				oneOf(getMockPersistenceEngine()).save(with(same(orderReturn)));
			}
		});

		exchangeService.add(orderReturn);
	}

	/**
	 * Test method for 'com.elasticpath.service.ReturnAndExchangeServiceImpl.findAllUids()'.
	 */
	@Test
	public void testFind() {
		final List<Long> listOfUids = new ArrayList<>();
		listOfUids.add(FAKE_UIDPK);
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).retrieveByNamedQuery(with("ORDER_RETURN_UIDS_LIST"), with(any(Object[].class)));
				will(returnValue(listOfUids));
			}
		});

		assertEquals(listOfUids, exchangeService.findAllUids());
	}

	/**
	 * Test method for 'com.elasticpath.service.ReturnAndExchangeServiceImpl.update()'.
	 */
	@Test
	public void testUpdate() {
		final OrderReturn orderReturn = new OrderReturnImpl();
		orderReturn.setOrder(new OrderImpl());
		context.checking(new Expectations() {
			{
				oneOf(getMockPersistenceEngine()).merge(with(same(orderReturn)));
				will(returnValue(null));
			}
		});
		exchangeService.update(orderReturn);
	}

	/**
	 * Test method for 'com.elasticpath.service.ReturnAndExchangeServiceImpl.list(final long uidPk)'.
	 */
	@Test
	public void testList() {
		final long uid = 1L;
		final OrderReturn orderReturn = new OrderReturnImpl();
		final List<OrderReturn> orderReturns = new ArrayList<>();
		orderReturns.add(orderReturn);
		// expectations
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).retrieveByNamedQuery("ORDER_RETURN_LIST", uid);
				will(returnValue(orderReturns));
			}
		});
		assertSame(orderReturns, exchangeService.list(uid));
	}

	/**
	 * Test method for 'com.elasticpath.service.ReturnAndExchangeServiceImpl.list(final long uidPk, final OrderReturnType returnType)'.
	 */
	@Test
	public void testList2() {
		final long uid = 1L;
		final OrderReturn orderReturn = new OrderReturnImpl();
		final List<OrderReturn> orderReturns = new ArrayList<>();
		orderReturns.add(orderReturn);
		// expectations
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).retrieveByNamedQuery(
						"ORDER_RETURN_LIST_BY_RETURN_TYPE", uid, OrderReturnType.RETURN);
				will(returnValue(orderReturns));
			}
		});
		assertSame(orderReturns, exchangeService.list(uid, OrderReturnType.RETURN));
	}

	/**
	 * Test method for 'com.elasticpath.service.ReturnAndExchangeServiceImpl.list()'.
	 */
	@Test
	public void testList3() {
		final OrderReturn orderReturn = new OrderReturnImpl();
		final List<OrderReturn> orderReturns = new ArrayList<>();
		orderReturns.add(orderReturn);
		// expectations
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).retrieveByNamedQuery(
						with("ORDER_EXCHANGE_AND_RETURN_LIST_BY_ORDER_UID"), with(any(Object[].class)));
				will(returnValue(orderReturns));
			}
		});

		assertSame(orderReturns, exchangeService.list());
	}

	/**
	 * Test method for 'com.elasticpath.service.ReturnAndExchangeServiceImpl.get(uid)'.
	 */
	@Test
	public void testGet() {
		final OrderReturn orderReturn = new OrderReturnImpl();
		stubGetBean(ContextIdNames.ORDER_RETURN, orderReturn);

		final FetchGroupLoadTuner mockFetchGroupLoadTuner = context.mock(FetchGroupLoadTuner.class);
		stubGetBean(ContextIdNames.FETCH_GROUP_LOAD_TUNER, mockFetchGroupLoadTuner);
		assertSame(orderReturn, exchangeService.get(0));
	}

	/**
	 * Test method for 'com.elasticpath.service.ReturnAndExchangeServiceImpl.get(uid)'.
	 */
	@Test
	public void testGet2() {
		stubGetBean(ContextIdNames.FETCH_GROUP_LOAD_TUNER, FetchGroupLoadTunerImpl.class);
		stubGetBean(ContextIdNames.ORDER_RETURN, OrderReturnImpl.class);

		final long uidPk = 1;
		final OrderReturn orderReturn = new OrderReturnImpl();
		orderReturn.setUidPk(uidPk);

		exchangeService.setFetchPlanHelper(new OpenJPAFetchPlanHelperImpl() {
			@Override
			public void clearFetchPlan() {
				// empty
			}

			@Override
			public void configureFetchGroupLoadTuner(final FetchGroupLoadTuner loadTuner) {
				// empty
			}

		});
		context.checking(new Expectations() {
			{
				oneOf(getMockPersistenceEngine()).get(with(same(OrderReturnImpl.class)), with(uidPk));
				will(returnValue(orderReturn));
			}
		});

		assertSame(orderReturn, exchangeService.get(uidPk));
	}

	/**
	 * Test method for 'com.elasticpath.service.ReturnAndExchangeServiceImpl.populateShoppingCart()'.
	 */
	@Test
	public void testPopulateShoppingCartContainsCheckoutDetailsFromOriginalOrder() {
		stubGetBean(ContextIdNames.LOCALIZED_PROPERTIES, LocalizedPropertiesImpl.class);
		stubGetBean(ContextIdNames.ORDER_RETURN_SKU, OrderReturnSkuImpl.class);
		stubGetBean(ContextIdNames.SHOPPING_CART, ShoppingCartImpl.class);
		stubGetBean(ContextIdNames.SHOPPING_CART_MEMENTO, ShoppingCartMementoImpl.class);
		stubGetBean(ContextIdNames.TAX_CALCULATION_RESULT, TaxCalculationResultImpl.class);

		TaxAddressAdapter adapter = new TaxAddressAdapter();
		stubGetBean(ContextIdNames.TAX_ADDRESS_ADAPTER, adapter);

		final TimeService mockTimeService = context.mock(TimeService.class, "orderReturnTimeService");
		stubGetBean(ContextIdNames.TIME_SERVICE, mockTimeService);

		final Shopper shopper = TestShopperFactory.getInstance().createNewShopperWithMemento();
		final CustomerSession customerSession = TestCustomerSessionFactory.getInstance().createNewCustomerSessionWithContext(shopper);
		context.checking(new Expectations() {
			{
				allowing(mockCustomerSessionService).createWithShopper(with(any(Shopper.class)));
				will(returnValue(customerSession));

				allowing(mockCustomerSessionService)
						.initializeCustomerSessionForPricing(with(equal(customerSession)), with(any(String.class)), with(any(Currency.class)));
				will(returnValue(customerSession));

				allowing(mockShopperService).findOrCreateShopper(with(any(Customer.class)), with(any(String.class)));
				will(returnValue(shopper));

				allowing(pricingSnapshotService).getPricingSnapshotForCart(with(any(ShoppingCart.class)));
				will(returnValue(new ShoppingCartImpl()));

				allowing(taxSnapshotService).getTaxSnapshotForCart(with(any(ShoppingCart.class)), with(any(ShoppingCartPricingSnapshot.class)));
				will(returnValue(new ShoppingCartImpl()));

				allowing(mockTimeService).getCurrentTime();
				will(returnValue(new Date()));
			}
		});

		final OrderReturn exchangeOrder = new OrderReturnImpl();

		exchangeOrder.populateOrderReturn(orderImpl, orderImpl.getAllShipments().get(0), OrderReturnType.RETURN);

		final List<ShoppingItem> cartItemList = mockCartItemList();

		final Address shippingAddress = mockShippingAddress();
		final ShoppingCart shoppingCart = exchangeService.populateShoppingCart(exchangeOrder, cartItemList, shippingOption, shippingAddress);
		final Optional<ShippingOption> selectedShippingOption = shoppingCart.getSelectedShippingOption();

		assertEquals("Incorrect shipping address set on the exchange shopping cart", shippingAddress, shoppingCart.getShippingAddress());
		assertTrue("No shipping option set on the exchange shopping cart", selectedShippingOption.isPresent());
		assertEquals("Incorrect shipping option set on the exchange shopping cart", shippingOption, selectedShippingOption.get());
		assertEquals("Incorrect list of cart items set on the exchange shopping cart", cartItemList, shoppingCart.getAllShoppingItems());
	}

	/**
	 * Test that you can't edit an OrderReturn with a Completed status -
	 * an {@link IllegalReturnStateException} should be thrown.
	 */
	@Test
	public void testEditReturnCompletedStateThrowsException() {
		final OrderReturn mockOrderReturn = context.mock(OrderReturn.class);
		context.checking(new Expectations() {
			{
				allowing(mockOrderReturn).getReturnStatus();
				will(returnValue(OrderReturnStatus.COMPLETED));

				allowing(mockOrderReturn).isInTerminalState();
				will(returnValue(true));
			}
		});
		try {
			exchangeService.editReturn(mockOrderReturn);
			fail("Should never rich here.");
		} catch (final IllegalReturnStateException e) { //NOPMD
			//empty
		}
	}

	/**
	 * Test that when an OrderReturn is received, the order event helper logs the receipt and
	 * then the OrderReturn is saved by retrieving the ReturnAndExchange service again from Spring
	 * (for transactions).
	 */
	@Test
	public void testReceiveReturn() {
		final OrderReturn mockOrderReturn = context.mock(OrderReturn.class);
		final OrderEventHelper mockOrderEventHelper = context.mock(OrderEventHelper.class);
		final ReturnAndExchangeService mockReturnAndExchangeService = context.mock(ReturnAndExchangeService.class);
		context.checking(new Expectations() {
			{
				oneOf(mockOrderReturn).getOrder();

				oneOf(mockOrderEventHelper).logOrderReturnReceived(with(any(Order.class)), with(any(OrderReturn.class)));

				oneOf(mockReturnAndExchangeService).update(with(same(mockOrderReturn)));
			}
		});

		final ReturnAndExchangeServiceImpl service = new ReturnAndExchangeServiceImpl() {
			@Override
			protected OrderEventHelper getOrderEventHelper() {
				return mockOrderEventHelper;
			}
			@Override
			protected ReturnAndExchangeService getReturnAndExchangeService() {
				return mockReturnAndExchangeService;
			}

		};
		service.receiveReturn(mockOrderReturn);
	}

	/**
	 * Create the payment service.
	 *
	 * @return the payment service.
	 */
	private PaymentService getPaymentService() {
		final PaymentHandlerFactory paymentHandlerFactory = new PaymentHandlerFactoryImpl();
		stubGetBean(ContextIdNames.PAYMENT_HANDLER_FACTORY, paymentHandlerFactory);
		stubGetBean(ContextIdNames.PAYMENT_RESULT, PaymentResultImpl.class);
		final PaymentServiceImpl paymentService = new PaymentServiceImpl();
		paymentService.setPaymentHandlerFactory(paymentHandlerFactory);

		final CreditCardPaymentGateway creditCardPaymentGateway = context.mock(CreditCardPaymentGateway.class);
		final PaymentGateway exchangePaymentGateway = context.mock(PaymentGateway.class);
		context.checking(new Expectations() {
			{
				allowing(creditCardPaymentGateway).getPaymentGatewayType();
				will(returnValue(PaymentType.CREDITCARD_DIRECT_POST));

				allowing(creditCardPaymentGateway).preAuthorize(with(any(OrderPayment.class)), with(any(Address.class)));
				allowing(creditCardPaymentGateway).reversePreAuthorization(with(any(OrderPayment.class)));
				allowing(creditCardPaymentGateway).capture(with(any(OrderPayment.class)));
				allowing(creditCardPaymentGateway).sale(with(any(OrderPayment.class)), with(any(Address.class)));
				allowing(creditCardPaymentGateway).setValidateCvv2(with(any(boolean.class)));

				allowing(exchangePaymentGateway).getPaymentGatewayType();
				will(returnValue(PaymentType.RETURN_AND_EXCHANGE));

				allowing(exchangePaymentGateway).preAuthorize(with(any(OrderPayment.class)), with(any(Address.class)));
				allowing(exchangePaymentGateway).reversePreAuthorization(with(any(OrderPayment.class)));
				allowing(exchangePaymentGateway).capture(with(any(OrderPayment.class)));
				allowing(exchangePaymentGateway).sale(with(any(OrderPayment.class)), with(any(Address.class)));
			}
		});

		final Set<PaymentGateway> paymentGateways = new HashSet<>();
		paymentGateways.add(creditCardPaymentGateway);
		paymentGateways.add(exchangePaymentGateway);

		getMockedStore().setPaymentGateways(paymentGateways);

		final StoreImpl store = new StoreImpl();
		store.setPaymentGateways(paymentGateways);

		return paymentService;
	}

	private static TaxCode createTaxCode(final String taxCodeName) {
		final TaxCode taxCode = new TaxCodeImpl();
		taxCode.setCode(taxCodeName);
		taxCode.setGuid(System.currentTimeMillis() + taxCodeName);
		return taxCode;
	}

	/**
	 * Test method for 'com.elasticpath.service.ReturnAndExchangeServiceImpl.manualRefundOrderReturn()'.
	 */
	@Test
	public void testRefundOrderReturnException() {
		try {

			final OrderReturn mockOrderReturn = context.mock(OrderReturn.class);

			context.checking(new Expectations() {
				{
					allowing(mockOrderReturn).recalculateOrderReturn();
					allowing(mockOrderReturn).updateOrderReturnableQuantity(with(any(Order.class)), with(mockProductSkuLookup));
					allowing(mockOrderReturn).getOrder();
					will(returnValue(new OrderImpl()));
					allowing(mockOrderReturnValidator).validate(mockOrderReturn, null);
				}
			});

			exchangeService.createShipmentReturn(
					mockOrderReturn,
					ReturnExchangeType.NEW_PAYMENT,
					null,
					null);
			fail("EpServiceException must be thrown, because of unexpected type");
		} catch (final EpServiceException ex) {
			// success
			assertNotNull(ex);
		}
	}

	/**
	 * Test method for 'com.elasticpath.service.ReturnAndExchangeServiceImpl.completeExchangeOrder()'.
	 */
	@Test
	public void testCompleteExchangeOrderWithException() {
		try {
			exchangeService.completeExchange(null, ReturnExchangeType.PHYSICAL_RETURN_REQUIRED, null);
			fail("EpServiceException must be thrown");
		} catch (final EpServiceException ex) {
			// Success
			assertNotNull(ex);
		}
	}

	private Set<OrderSku> mockOrderSkuList() {
		final Set<OrderSku> orderSkus = new HashSet<>();
		// Create mock cart items
		final OrderSku orderSku = new OrderSkuImpl();
		orderSku.setUidPk(Calendar.getInstance().getTimeInMillis());
		final ProductSku productSkuImpl = mockProductSku();
		orderSku.setSkuGuid(productSkuImpl.getGuid());
		orderSku.setPrice(1, null);
		//Tax amount needs to be set, normally set by TaxCalculationService
		orderSku.setTaxAmount(ITEM_TAX);
		orderSkus.add(orderSku);
		return orderSkus;
	}

	private List<ShoppingItem> mockCartItemList() {
		final List<ShoppingItem> orderSkus = new ArrayList<>();
		// Create mock cart items
		final ShoppingItem cartItem = new ShoppingItemImpl();
		cartItem.setUidPk(Calendar.getInstance().getTimeInMillis());
		final ProductSku mockProductSku = mockProductSku();
		cartItem.setSkuGuid(mockProductSku.getGuid());
		cartItem.setGuid(new RandomGuidImpl().toString());
		context.checking(new Expectations() {
			{
				allowing(mockProductSkuLookup).findByUid(mockProductSku.getUidPk());
				will(returnValue(mockProductSku));
			}
		});

		final Price price = new PriceImpl();
		price.setListPrice(Money.valueOf(PRODUCT_PRICE, CURRENCY));

		// any product sku price for the current cart item can be set up here.
		cartItem.setPrice(1, getPrice(CURRENCY, PRODUCT_PRICE, PRODUCT_PRICE));

		orderSkus.add(cartItem);
		return orderSkus;
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

	private Address mockShippingAddress() {
		final Address address = new CustomerAddressImpl();

		address.setFirstName("Joe");
		address.setLastName("Doe");
		address.setCountry(REGION_CODE_CA);
		address.setStreet1("1295 Charleston Road");
		address.setCity("Mountain View");
		address.setSubCountry(REGION_CODE_BC);
		address.setZipOrPostalCode("94043");
		return address;
	}

	private ProductSku mockProductSku() {
		productSku = new ProductSkuImpl();
		productSku.setUidPk(Calendar.getInstance().getTimeInMillis());
		productSku.setGuid("Irrelevant sku code");

		productSku.setWeight(BigDecimal.ONE);

		final Product productImpl = new ProductImpl() {
			private static final long serialVersionUID = 5000000001L;

			@Override
			public String getDisplayName(final Locale locale) {
				return "Test Display Name";
			}
		};
		productImpl.setGuid("Irrelevant product guid");
		productImpl.setProductSkus(new HashMap<>());
		final ProductTypeImpl productTypeImpl = new ProductTypeImpl();
		final TaxCode taxCode = new TaxCodeImpl();
		taxCode.setCode(SALES_TAX_CODE_GOODS);
		productTypeImpl.setTaxCode(taxCode);
		productImpl.setProductType(productTypeImpl);
		productImpl.setTaxCodeOverride(taxCode);
		productSku.setProduct(productImpl);
		productImpl.addOrUpdateSku(productSku);

		context.checking(new Expectations() {
			{
				allowing(mockProductSkuLookup).findByGuid(productSku.getGuid());
				will(returnValue(productSku));
			}
		});

		final LocaleDependantFields ldf = new ProductLocaleDependantFieldsImpl();
		ldf.setDisplayName("TestDisplayName");
		ldf.setDescription("TestDescription");
		ldf.setLocale(DEFAULT_LOCALE);
		productImpl.addOrUpdateLocaleDependantFields(ldf);

		final InventoryDtoImpl inventoryDto = new InventoryDtoImpl();
		inventoryDto.setWarehouseUid(getMockedStore().getWarehouse().getUidPk());
		inventoryDto.setQuantityOnHand(1);
		inventoryDto.setSkuCode(productSku.getSkuCode());

		return productSku;
	}

	private Set<OrderPayment> mockOrderPayments(final OrderShipment shipment) {
		final OrderPayment orderPayment = new OrderPaymentImpl();
		orderPayment.setTransactionType(OrderPayment.CAPTURE_TRANSACTION);
		orderPayment.setDisplayValue(CARD_HOLDER_NAME);
		orderPayment.setPaymentMethod(PaymentType.CREDITCARD_DIRECT_POST);
		orderPayment.setOrderShipment(shipment);
		final Set<OrderPayment> paymentSet = new HashSet<>();
		paymentSet.add(orderPayment);
		return paymentSet;
	}

	private OrderShipment mockOrderShipment() {
		final PhysicalOrderShipment orderShipment = getMockPhysicalOrderShipment();
		orderShipment.setUidPk(BigDecimal.ONE.longValue());

		/*
		* The status set to INVENTORY_ASSIGNED so that tax can be calculated and the transient variables can be
		* initialized in orderShipment.Currently tax is not getting recalculated for PhysicalOrderShipment with
		* status CANCELLED,SHIPPED or RELEASED.
		 */
		orderShipment.setStatus(OrderShipmentStatus.INVENTORY_ASSIGNED);
		orderShipment.setSubtotalDiscount(BigDecimal.ZERO.setScale(2));
		orderShipment.setShippingCost(BigDecimal.ZERO.setScale(2));

		orderShipment.setShipmentAddress(mockOrderAddress());

		final Set<OrderSku> orderSkuSet = mockOrderSkuList();
		for (final OrderSku sku : orderSkuSet) {
			orderShipment.addShipmentOrderSku(sku);
		}

		return orderShipment;
	}

	private PhysicalOrderShipmentImpl getMockPhysicalOrderShipment() {

		final TaxCalculationResult result = new TaxCalculationResultImpl() {

			private static final long serialVersionUID = 3596418723515022523L;

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

		return new PhysicalOrderShipmentImpl() {
			private static final long serialVersionUID = -8478099243134627014L;

			@Override
			public TaxCalculationResult calculateTaxes() {
				return result;
			}
		};

	}

	/**
	 * Creates tax jurisdiction jurisdiction. That jurisdiction is:<br>
	 * Region code: CA<br>
	 * Country category: GST<br>
	 * Tax Region: CA<br>
	 * Tax Values: SHIPPING==6%, GOODS==6%<br>
	 * Subcountry category: PST<br>
	 * Tax Region: BC<br>
	 * Tax Values: SHIPPING==7%, GOODS==7%<br>
	 * Subcountry category: ANOTHER_CATEGORY<br>
	 * Tax Region: VANCOUVER<br>
	 * Tax Values: SHIPPING==7%, GOODS==7%<br>
	 * The last category musn't be taken into account while calculating taxes. This category doesn't match the shipping address.
	 */
	private TaxJurisdiction getTaxJurisdiction() {
		final TaxJurisdiction taxJurisdiction = new TaxJurisdictionImpl();
		taxJurisdiction.setPriceCalculationMethod(TaxJurisdiction.PRICE_CALCULATION_EXCLUSIVE);
		taxJurisdiction.setRegionCode(REGION_CODE_CA);

		// 1) category
		TaxCategory taxCategory = new TaxCategoryImpl();
		taxCategory.setFieldMatchType(TaxCategoryTypeEnum.FIELD_MATCH_COUNTRY);
		taxCategory.setName(GST_TAX_CODE);

		TaxRegion taxRegion = new TaxRegionImpl();

		Map<String, TaxValue> taxValueMap = new HashMap<>();

		TaxValue taxValue = new TaxValueImpl();
		taxValue.setTaxCode(createTaxCode(SALES_TAX_CODE_GOODS));
		taxValue.setTaxValue(new BigDecimal("6"));
		taxValueMap.put(taxValue.getTaxCode().getCode(), taxValue);

		taxValue = new TaxValueImpl();
		taxValue.setTaxCode(createTaxCode(TaxCode.TAX_CODE_SHIPPING));
		taxValue.setTaxValue(new BigDecimal("6"));
		taxValueMap.put(taxValue.getTaxCode().getCode(), taxValue);

		taxRegion.setTaxValuesMap(taxValueMap);
		taxRegion.setRegionName(REGION_CODE_CA);

		taxCategory.addTaxRegion(taxRegion);

		taxJurisdiction.addTaxCategory(taxCategory);

		// 2) category
		taxCategory = new TaxCategoryImpl();
		taxCategory.setFieldMatchType(TaxCategoryTypeEnum.FIELD_MATCH_SUBCOUNTRY);
		taxCategory.setName(PST_TAX_CODE);

		taxValueMap = new HashMap<>();

		taxRegion = new TaxRegionImpl();
		taxValue = new TaxValueImpl();
		taxValue.setTaxCode(createTaxCode(SALES_TAX_CODE_GOODS));
		taxValue.setTaxValue(new BigDecimal("7"));
		taxValueMap.put(taxValue.getTaxCode().getCode(), taxValue);

		taxValue = new TaxValueImpl();
		taxValue.setTaxCode(createTaxCode(TaxCode.TAX_CODE_SHIPPING));
		taxValue.setTaxValue(new BigDecimal("7"));
		taxValueMap.put(taxValue.getTaxCode().getCode(), taxValue);

		taxRegion.setTaxValuesMap(taxValueMap);

		taxRegion.setRegionName(REGION_CODE_BC);

		taxCategory.addTaxRegion(taxRegion);

		taxJurisdiction.addTaxCategory(taxCategory);

		return taxJurisdiction;
	}


	/**
	 * Dummy implementation of the TimeService.
	 */
	public static final class DummyTimeService extends DatabaseServerTimeServiceImpl {
		/**
		 * Gets the current time.
		 * @return current date
		 */
		@Override
		public Date getCurrentTime() {
			return new Date();
		}
	}

	/**
	 * Implementation of Order with auto-recalculation enabled by default for testing purposes.
	 */
	class TestOrderImpl extends OrderImpl {
		private static final long serialVersionUID = -4395039923898026316L;

		/**
		 * Override default constructor to enable auto-recalculation.
		 */
		TestOrderImpl() {
			super();
			enableRecalculation();
		}
	}

	/**
	 * Tests the find last refund payment gets really the last by date successful refund payment.
	 */
	@Test
	public void testFindLastRefundPayment() {

		final ReturnAndExchangeServiceImpl serviceImpl = new ReturnAndExchangeServiceImpl();

		final Set<OrderPayment> orderPayments = new HashSet<>();

		final OrderPayment orderPayment = new OrderPaymentImpl();
		orderPayment.setCreatedDate(new Date(DAY));
		orderPayment.setTransactionType(OrderPayment.CREDIT_TRANSACTION);
		orderPayment.setStatus(OrderPaymentStatus.APPROVED);

		orderPayments.add(orderPayment);

		final OrderPayment orderPayment2 = new OrderPaymentImpl();
		orderPayment2.setCreatedDate(new Date(DAY * 2));
		orderPayment2.setTransactionType(OrderPayment.CREDIT_TRANSACTION);
		orderPayment2.setStatus(OrderPaymentStatus.FAILED);

		orderPayments.add(orderPayment2);

		final OrderPayment orderPayment3 = new OrderPaymentImpl();
		orderPayment3.setCreatedDate(new Date(DAY * 2));
		orderPayment3.setTransactionType(OrderPayment.AUTHORIZATION_TRANSACTION);
		orderPayment3.setStatus(OrderPaymentStatus.APPROVED);

		orderPayments.add(orderPayment3);

		final OrderPayment orderPayment4 = new OrderPaymentImpl();
		orderPayment4.setCreatedDate(new Date(DAY * 2));
		orderPayment4.setTransactionType(OrderPayment.CREDIT_TRANSACTION);
		orderPayment4.setStatus(OrderPaymentStatus.APPROVED);

		orderPayments.add(orderPayment4);


		assertEquals(orderPayment4, serviceImpl.findLastRefundPayment(orderPayments));
	}

	/**
	 * Returns a new <code>Customer</code> instance.
	 *
	 * @return a new <code>Customer</code> instance.
	 */
	protected Customer getCustomer() {
		final Customer customer = new CustomerImpl();
		customer.setGuid(new RandomGuidImpl().toString());
		customer.initialize();
		return customer;
	}

	protected ShippingOption getShippingOption() {
		final ShippingOptionImpl option = new ShippingOptionImpl();
		option.setCode("fedex1");
		option.setCarrierCode("Fed Ex");
		return option;
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

	/**
	 * @return a new <code>Price</code> instance.
	 */
	private Price getPrice() {
		PriceImpl price = new PriceImpl();
		price.addOrUpdatePriceTier(getPriceTier());
		price.setCurrency(Currency.getInstance(CART_CURRENCY));
		price.initialize();
		return price;
	}

	/**
	 * @return a new <code>PriceTier</code> instance.
	 */
	private PriceTier getPriceTier() {
		PriceTier priceTier = new PriceTierImpl();
		priceTier.initialize();
		return priceTier;
	}

	private ShippingOptionResult createShippingOptionResult(final List<ShippingOption> availableShippingOptions) {
		final ShippingOptionResultImpl result = new ShippingOptionResultImpl();
		result.setAvailableShippingOptions(availableShippingOptions);
		return result;
	}
}
