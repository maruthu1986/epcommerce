/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.test.integration;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import com.elasticpath.domain.builder.OrderBuilder;
import com.elasticpath.domain.builder.checkout.CheckoutTestCartBuilder;
import com.elasticpath.domain.builder.customer.CustomerBuilder;
import com.elasticpath.domain.builder.shopper.ShoppingContext;
import com.elasticpath.domain.builder.shopper.ShoppingContextBuilder;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.factory.OrderPaymentFactory;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderLock;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.persister.Persister;
import com.elasticpath.service.cmuser.CmUserService;
import com.elasticpath.service.customer.CustomerService;
import com.elasticpath.service.order.OrderLockService;
import com.elasticpath.test.persister.testscenarios.SimpleStoreScenario;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class OrderLockServiceImplTest extends BasicSpringContextTest {

	private static final Logger LOG = Logger.getLogger(OrderLockServiceImplTest.class);

	@Autowired
	private OrderLockService orderLockService;

	@Autowired
	private OrderPaymentFactory orderPaymentFactory;

	@Autowired
	private CheckoutTestCartBuilder checkoutTestCartBuilder;

	@Autowired
	private OrderBuilder orderBuilder;

	@Autowired
	private CustomerBuilder customerBuilder;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private CmUserService cmUserService;

	@Autowired
	private ShoppingContextBuilder shoppingContextBuilder;

	@Autowired
	private Persister<ShoppingContext> shoppingContextPersister;

	private CmUser cmUser;

	private Collection<OrderLock> orderLocks; // to facilitate clearing in tearDown

	@Before
	public void setUp() {
		orderLocks = new ArrayList<>();

		final SimpleStoreScenario scenario = getTac().useScenario(SimpleStoreScenario.class);

		final Customer testCustomer = customerBuilder
				.withStoreCode(scenario.getStore().getCode())
				.build();
		customerService.add(testCustomer);

		final ShoppingContext shoppingContext = shoppingContextBuilder.withCustomer(testCustomer)
				.build();

		shoppingContextPersister.persist(shoppingContext);
		
		checkoutTestCartBuilder
				.withScenario(scenario)
				.withCustomerSession(shoppingContext.getCustomerSession())
				.withTestDoubleGateway();

		final OrderPayment templateOrderPayment = orderPaymentFactory.createTemplateTokenizedOrderPayment();

		orderBuilder
				.withCheckoutTestCartBuilder(checkoutTestCartBuilder)
				.withTemplateOrderPayment(templateOrderPayment)
				.withShoppingContext(shoppingContext);

		cmUser = cmUserService.findByUserName("admin");
	}

	@After
	public void tearDown() {
		for (final OrderLock orderLock : orderLocks) {
			orderLockService.forceReleaseOrderLock(orderLock);
		}

		orderLocks.clear();
	}

	@Test
	public void testObtainLockConcurrency() throws InterruptedException, BrokenBarrierException {
		final int numberOfTestingThreads = 50;

		final Order order = orderBuilder
				.withNonZeroElectronicShipment()
				.build();

		// The +1 is because the main thread (running the test) is a participant.  The gate will only open when all participants are ready,
		// and we want the main thread to give the go-ahead and for all other threads to start at exactly the same time,
		// or as close to that as possible.
		final CyclicBarrier gate = new CyclicBarrier(numberOfTestingThreads + 1);

		final Collection<OrderLockingThread> threads = new ArrayList<>(numberOfTestingThreads);
		for (int i = 0; i < numberOfTestingThreads; i++) {
			final OrderLockingThread thread = new OrderLockingThread("Thread " + i, order, cmUser, gate);
			threads.add(thread);
			thread.start();
		}

		gate.await();

		for (final OrderLockingThread thread : threads) {
			thread.join();
			assertNull(thread.getName() + " did not complete successfully", thread.getException());
			orderLocks.add(thread.getOrderLock());
		}

		assertEquals(threads.size(), orderLocks.size());
		orderLocks.removeAll(Collections.singleton(null));
		assertEquals("Exactly one lock for the order should be granted", 1, orderLocks.size());
	}

	private class OrderLockingThread extends Thread {
		private final Order order;
		private final CyclicBarrier gate;
		private final CmUser cmUser;
		private OrderLock orderLock;
		private Exception exception;

		public OrderLockingThread(final String threadName, final Order order, final CmUser cmUser, final CyclicBarrier gate) {
			this.cmUser = cmUser;
			this.order = order;
			this.gate = gate;
			setName(threadName);
		}

		@Override
		public void run() {
			LOG.debug(getName() + " - received lock request for order [" + order.getUidPk() + "]");
			try {
				gate.await();
				orderLock = orderLockService.obtainOrderLock(order, cmUser, new Date());
				LOG.debug(getName() + " - received lock [" + (orderLock == null ? null : orderLock.getUidPk()) + "]");
			} catch (Exception e) {
				exception = e;
				LOG.error(getName() + " - encountered unexpected exception while retrieving lock [" + e.getLocalizedMessage() + "]", e);
			}
		}

		public Exception getException() {
			return exception;
		}

		public OrderLock getOrderLock() {
			return orderLock;
		}
	}

}
