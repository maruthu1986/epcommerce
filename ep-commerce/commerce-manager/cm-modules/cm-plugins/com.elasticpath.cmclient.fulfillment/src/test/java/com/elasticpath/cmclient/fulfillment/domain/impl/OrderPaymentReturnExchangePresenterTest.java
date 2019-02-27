/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
/**
 *
 */
package com.elasticpath.cmclient.fulfillment.domain.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.eclipse.rap.rwt.testfixture.TestContext;

import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.cmclient.fulfillment.domain.impl.OrderPaymentPresenterFactory.OrderPaymentReturnExchangePresenter;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderStatus;
import com.elasticpath.money.Money;
import com.elasticpath.money.MoneyFormatter;
import com.elasticpath.money.StandardMoneyFormatter;
import com.elasticpath.plugin.payment.PaymentType;

/**
 *	Test class for OrderPaymentReturnExchangePresenter.
 */
public class OrderPaymentReturnExchangePresenterTest {

	private static final BigDecimal THREE = new BigDecimal("3.0");

	@Rule
	public final MockitoRule rule = MockitoJUnit.rule();

	@Rule
	public TestContext context = new TestContext();

	@Mock
	private OrderPayment mockOrderPayment;

	/**
	 * Setup method.
	 * @throws Exception on error.
	 */
	@Before
	public void setUp() throws Exception {
		when(mockOrderPayment.getPaymentMethod()).thenReturn(PaymentType.RETURN_AND_EXCHANGE);
	}

	/**
	 * Test that if the OrderPayment's OrderStatus is AWAITING_EXCHANGE then the
	 * payment string will be FulfillmentMessages.get().Exchange_Pending_Payment_Details.
	 */
	@Test
	public void testGetDisplayPaymentDetailsAwaitingExchange() {
		OrderPaymentReturnExchangePresenter presenter =
			createOrderPaymentPresenterFactory().new OrderPaymentReturnExchangePresenter(mockOrderPayment) {
			@Override
			OrderStatus getOrderStatus() {
				return OrderStatus.AWAITING_EXCHANGE;
			}
		};
		assertEquals(FulfillmentMessages.get().Exchange_Pending_Payment_Details, presenter.getDisplayPaymentDetails());
	}

	/**
	 * Test that if the OrderPayment's OrderStatus is COMPLETED
	 * (not AWAITING_EXCHANGE or CANCELLED) then the payment string will be
	 * FulfillmentMessages.get().Exchange_Completed_Payment_Details.
	 */
	@Test
	public void testGetDisplayPaymentDetailsCompletedOrder() {
		OrderPaymentReturnExchangePresenter presenter =
			createOrderPaymentPresenterFactory().new OrderPaymentReturnExchangePresenter(mockOrderPayment) {
			@Override
			OrderStatus getOrderStatus() {
				return OrderStatus.COMPLETED;
			}
		};
		assertEquals(FulfillmentMessages.get().Exchange_Completed_Payment_Details, presenter.getDisplayPaymentDetails());
	}

	/**
	 * Test that if the OrderPayment's OrderStatus is IN_PROGRESS
	 * (not AWAITING_EXCHANGE or CANCELLED) then the payment string will be
	 * FulfillmentMessages.get().Exchange_Completed_Payment_Details.
	 */
	@Test
	public void testGetDisplayPaymentDetailsInProgressOrder() {
		OrderPaymentReturnExchangePresenter presenter =
			createOrderPaymentPresenterFactory().new OrderPaymentReturnExchangePresenter(mockOrderPayment) {
			@Override
			OrderStatus getOrderStatus() {
				return OrderStatus.IN_PROGRESS;
			}
		};
		assertEquals(FulfillmentMessages.get().Exchange_Completed_Payment_Details, presenter.getDisplayPaymentDetails());
	}

	/**
	 * Test that if the OrderPayment's OrderStatus is ON_HOLD
	 * (not AWAITING_EXCHANGE or CANCELLED) then the payment string will be
	 * FulfillmentMessages.get().Exchange_Completed_Payment_Details.
	 */
	@Test
	public void testGetDisplayPaymentDetailsOnHoldOrder() {
		OrderPaymentReturnExchangePresenter presenter =
			createOrderPaymentPresenterFactory().new OrderPaymentReturnExchangePresenter(mockOrderPayment) {
			@Override
			OrderStatus getOrderStatus() {
				return OrderStatus.ONHOLD;
			}
		};
		assertEquals(FulfillmentMessages.get().Exchange_Completed_Payment_Details, presenter.getDisplayPaymentDetails());
	}

	/**
	 * Test that if the OrderPayment's OrderStatus is PARTIALLY_SHIPPED
	 * (not AWAITING_EXCHANGE or CANCELLED) then the payment string will be
	 * FulfillmentMessages.get().Exchange_Completed_Payment_Details.
	 */
	@Test
	public void testGetDisplayPaymentDetailsPartiallyShippedOrder() {
		OrderPaymentReturnExchangePresenter presenter =
			createOrderPaymentPresenterFactory().new OrderPaymentReturnExchangePresenter(mockOrderPayment) {
			@Override
			OrderStatus getOrderStatus() {
				return OrderStatus.PARTIALLY_SHIPPED;
			}
		};
		assertEquals(FulfillmentMessages.get().Exchange_Completed_Payment_Details, presenter.getDisplayPaymentDetails());
	}

	/**
	 * Test that if the OrderPayment's OrderStatus is CANCELLED then the
	 * payment string will be null.
	 */
	@Test
	public void testGetDisplayPaymentDetails() {
		OrderPaymentReturnExchangePresenter presenter =
			createOrderPaymentPresenterFactory().new OrderPaymentReturnExchangePresenter(mockOrderPayment) {
			@Override
			OrderStatus getOrderStatus() {
				return OrderStatus.CANCELLED;
			}
		};
		assertNull(presenter.getDisplayPaymentDetails());
	}

	/**
	 * Test that if the OrderPayment's OrderStatus is AWAITING_EXCHANGE then
	 * the transactionTypeString is OrderPayment.AUTHORIZATION_TRANSACTION.
	 */
	@Test
	public void testGetDisplayTransactionTypeAwaitingTransaction() {
		OrderPaymentReturnExchangePresenter presenter =
			createOrderPaymentPresenterFactory().new OrderPaymentReturnExchangePresenter(mockOrderPayment) {
			@Override
			OrderStatus getOrderStatus() {
				return OrderStatus.AWAITING_EXCHANGE;
			}
		};
		assertEquals(OrderPayment.AUTHORIZATION_TRANSACTION, presenter.getDisplayTransactionType());
	}

	/**
	 * Test that if the OrderPayment's OrderStatus is NOT AWAITING_EXCHANGE
	 * and is NOT CANCELLED (i.e. is anything else), the transactionTypeString is OrderPayment.CAPTURE_TRANSACTION.
	 */
	@Test
	public void testGetDisplayTransactionTypeCapture() {
		OrderPaymentReturnExchangePresenter presenter =
			createOrderPaymentPresenterFactory().new OrderPaymentReturnExchangePresenter(mockOrderPayment) {
			@Override
			OrderStatus getOrderStatus() {
				return OrderStatus.IN_PROGRESS;
			}
		};
		assertEquals(OrderPayment.CAPTURE_TRANSACTION, presenter.getDisplayTransactionType());
	}

	/**
	 * Test that if the OrderPayment's OrderStatus is CANCELLED
	 * the transactionTypeString is OrderPayment.REVERSE_AUTHORIZATION.
	 */
	@Test
	public void testGetDisplayTransactionType() {
		OrderPaymentReturnExchangePresenter presenter =
			createOrderPaymentPresenterFactory().new OrderPaymentReturnExchangePresenter(mockOrderPayment) {
			@Override
			OrderStatus getOrderStatus() {
				return OrderStatus.CANCELLED;
			}
		};
		assertEquals(OrderPayment.REVERSE_AUTHORIZATION, presenter.getDisplayTransactionType());
	}

	/**
	 * Test that getDisplayPaymentAmount() will return the order's
	 * DueToRMAMoney string with currency symbol.
	 */
	@Test
	public void testGetDisplayAmount() {
		final String moneyString = "$3.00"; //$NON-NLS-1$
		final Order mockOrder = mock(Order.class);
		when(mockOrder.getDueToRMAMoney()).thenReturn(Money.valueOf(THREE, Currency.getInstance(Locale.CANADA)));
		when(mockOrder.getLocale()).thenReturn(Locale.CANADA);

		OrderPaymentReturnExchangePresenter presenter =
			createOrderPaymentPresenterFactory().new OrderPaymentReturnExchangePresenter(mockOrderPayment) {
			@Override
			protected Order getOrder() {
				return mockOrder;
			}
		};

		assertEquals(moneyString, presenter.getDisplayPaymentAmount());
	}

	/**
	 * Test that getDisplayPaymentAmount() will return the OrderPayment's money amount
	 * if the OrderPayment's Order is not loaded.
	 */
	@Test
	public void testGetDisplayAmountNullOrder() {
		final String moneyString = "$3.00"; //$NON-NLS-1$

		when(mockOrderPayment.getAmountMoney()).thenReturn(Money.valueOf(THREE, Currency.getInstance(Locale.CANADA)));

		OrderPaymentReturnExchangePresenter presenter =
			createOrderPaymentPresenterFactory().new OrderPaymentReturnExchangePresenter(mockOrderPayment) {
			@Override
			protected Order getOrder() {
				return null;
			}
		};
		assertEquals(moneyString, presenter.getDisplayPaymentAmount());
	}

	/**
	 * Test that getDisplayPaymentAmount() will return the OrderPayment's money amount
	 * if the OrderPayment's Order's DueToRMAMoney value is null.
	 */
	@Test
	public void testGetDisplayAmountNullDueToRMAMoney() {
		final String moneyString = "$3.00"; //$NON-NLS-1$
		final Order mockOrder = mock(Order.class);

		when(mockOrder.getDueToRMAMoney()).thenReturn(null);
		when(mockOrder.getLocale()).thenReturn(Locale.CANADA);
		when(mockOrderPayment.getAmountMoney()).thenReturn(Money.valueOf(THREE, Currency.getInstance(Locale.CANADA)));

		OrderPaymentReturnExchangePresenter presenter =
			createOrderPaymentPresenterFactory().new OrderPaymentReturnExchangePresenter(mockOrderPayment) {
			@Override
			protected Order getOrder() {
				return mockOrder;
			}
		};
		assertEquals(moneyString, presenter.getDisplayPaymentAmount());
	}

	private OrderPaymentPresenterFactory createOrderPaymentPresenterFactory() {
		return new OrderPaymentPresenterFactory() {
			@Override
			protected MoneyFormatter getMoneyFormatter() {
				return new StandardMoneyFormatter();
			}
		};
	}
}
