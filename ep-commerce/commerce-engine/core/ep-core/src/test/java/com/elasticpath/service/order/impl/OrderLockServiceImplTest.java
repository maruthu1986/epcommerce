/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.service.order.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.jmock.Expectations;
import org.junit.Test;
import org.springframework.dao.DataAccessException;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.cmuser.impl.CmUserImpl;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderLock;
import com.elasticpath.domain.order.impl.OrderImpl;
import com.elasticpath.domain.order.impl.OrderLockImpl;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.order.InvalidUnlockerException;
import com.elasticpath.service.order.OrderLockService;
import com.elasticpath.service.order.OrderService;
import com.elasticpath.test.jmock.AbstractEPServiceTestCase;

/**
 * Test cases for <code>OrderLockServiceImpl</code>.
 */
public class OrderLockServiceImplTest extends AbstractEPServiceTestCase {

	private static final String ORDER_LOCK_BY_ORDER_UID = "ORDER_LOCK_BY_ORDER_UID";

	private OrderLockServiceImpl orderLockService;

	private OrderService mockOrderService;

	private TimeService mockTimeService;

	/**
	 * Prepare for the tests.
	 *
	 * @throws Exception on error
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();

		mockOrderService = context.mock(OrderService.class);
		mockTimeService = context.mock(TimeService.class);

		orderLockService = new OrderLockServiceImpl();
		orderLockService.setOrderService(mockOrderService);
		orderLockService.setPersistenceEngine(getMockPersistenceEngine());
		orderLockService.setTimeService(mockTimeService);

		stubGetBean(ContextIdNames.ORDER_SERVICE, mockOrderService);
		stubGetBean(ContextIdNames.ORDER_LOCK_SERVICE, orderLockService);
	}

	/**
	 * Test method for {@link OrderLockServiceImpl#obtainOrderLock(Order, CmUser, Date)}.
	 * Obtaining order lock should failed because of existing orderLock in database.
	 */
	@Test
	public void testObtainOrderLockFailsWhenExistingLockInDatabase() {
		final OrderLock orderLock = new OrderLockImpl();
		stubGetBean(ContextIdNames.ORDER_LOCK, orderLock);

		final long uid = 1234L;
		final Order order = new OrderImpl();
		order.setUidPk(uid);
		order.setLastModifiedDate(new Date(0));

		// expectations
		context.checking(new Expectations() {
			{
				allowing(mockOrderService).get(uid);
				will(returnValue(order));

				allowing(getMockPersistenceEngine()).save(orderLock);
				will(throwException(new DataAccessException("Unique constraint violation") {
					private static final long serialVersionUID = 7160397053659676303L;
				}));

				allowing(mockTimeService).getCurrentTime();
				will(returnValue(new Date()));
			}
		});
		assertNull(orderLockService.obtainOrderLock(order, new CmUserImpl(), new Date()));
	}

	/**
	 * Test method for {@link OrderLockServiceImpl#obtainOrderLock(Order, CmUser, Date)}.
	 * Obtaining of order lock should failed because order was modified after is was opened in Order Editor.
	 */
	@Test
	public void testObtainOrderLockFailsWhenOrderIsModifiedAfterOpening() {
		final Order order = new OrderImpl();
		final long uid = 1234L;
		final long after = 20L;
		final long before = 10L;
		final Date orderLastModifiedDate = new Date(after);
		final Date openEditorDate = new Date(before);
		order.setLastModifiedDate(orderLastModifiedDate);
		order.setUidPk(uid);

		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).retrieveByNamedQuery(ORDER_LOCK_BY_ORDER_UID, order.getUidPk());
				will(returnValue(Collections.singletonList(null)));

				allowing(mockOrderService).get(uid);
				will(returnValue(order));
			}
		});
		// expectations
		assertNull(orderLockService.obtainOrderLock(order, new CmUserImpl(), openEditorDate));
	}

	/**
	 * Test method for {@link OrderLockServiceImpl#obtainOrderLock(Order, CmUser, Date)}.
	 */
	@Test
	public void testObtainOrderLock() {
		OrderLock orderLock = new OrderLockImpl();
		final Order order = new OrderImpl();
		final long uid = 1234L;
		final long before = 10L;
		final long after = 20L;
		final Date orderLastModifiedDate = new Date(before);
		final Date openEditorDate = new Date(after);
		order.setLastModifiedDate(orderLastModifiedDate);
		order.setUidPk(uid);
		final CmUser user = new CmUserImpl();

		context.checking(new Expectations() {
			{
				allowing(mockOrderService).get(uid);
				will(returnValue(order));
			}
		});

		stubGetBean(ContextIdNames.ORDER_LOCK, orderLock);

		final Date lockCreatedDate = new Date();
		context.checking(new Expectations() {
			{
				allowing(mockTimeService).getCurrentTime();
				will(returnValue(lockCreatedDate));
			}
		});

		context.checking(new Expectations() {
			{
				oneOf(getMockPersistenceEngine()).save(with(any(Persistable.class)));
			}
		});

		orderLock = orderLockService.obtainOrderLock(order, user, openEditorDate);

		assertEquals(user, orderLock.getCmUser());
		assertEquals(order, orderLock.getOrder());
		assertEquals(lockCreatedDate.getTime(), orderLock.getCreatedDate());
	}

	/**
	 * Test method for
	 * {@link com.elasticpath.service.order.impl.OrderLockServiceImpl#validateLock(
	 * com.elasticpath.domain.order.OrderLock, java.util.Date)}.
	 * Order is locked.
	 */
	@Test
	public void testValidateLock1() {
		final OrderLock orderLock = null;
		// expectations
		assertEquals(OrderLockService.ORDER_IS_LOCKED,
				orderLockService.validateLock(orderLock, new Date()));
	}

	/**
	 * Test method for
	 * {@link com.elasticpath.service.order.impl.OrderLockServiceImpl#validateLock(
	 * com.elasticpath.domain.order.OrderLock, java.util.Date)}.
	 * Order was unlocked.
	 */
	@Test
	public void testValidateLock2() {
		final OrderLock orderLock = new OrderLockImpl();
		final Order order = new OrderImpl();
		final long uid = 1234L;
		order.setUidPk(uid);
		orderLock.setOrder(order);
		context.checking(new Expectations() {
			{
				allowing(mockOrderService).get(uid);
				will(returnValue(order));
			}
		});
		final List<OrderLock> results = new ArrayList<>();
		// emulate that order was unlocked.
		results.add(null);
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).retrieveByNamedQuery(ORDER_LOCK_BY_ORDER_UID, order.getUidPk());
				will(returnValue(results));
			}
		});
		// expectations
		assertEquals(OrderLockService.ORDER_WAS_UNLOCKED,
				orderLockService.validateLock(orderLock, new Date()));
	}

	/**
	 * Test method for
	 * {@link com.elasticpath.service.order.impl.OrderLockServiceImpl#validateLock(
	 * com.elasticpath.domain.order.OrderLock, java.util.Date)}.
	 * Lock is alien.
	 */
	@Test
	public void testValidateLock3() {
		final OrderLock orderLock = new OrderLockImpl();
		final Order order = new OrderImpl();
		final long uid = 1234L;
		order.setUidPk(uid);
		orderLock.setOrder(order);
		context.checking(new Expectations() {
			{
				allowing(mockOrderService).get(uid);
				will(returnValue(order));
			}
		});
		final List<OrderLock> results = new ArrayList<>();
		// emulate that order lock exists in database but dates of creation are different
		OrderLock freshOrderLock = new OrderLockImpl();
		final long before = 10L;
		final long after = 20L;
		freshOrderLock.setCreatedDate(before);
		orderLock.setCreatedDate(after);
		results.add(freshOrderLock);
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).retrieveByNamedQuery(ORDER_LOCK_BY_ORDER_UID, order.getUidPk());
				will(returnValue(results));
			}
		});
		// expectations
		assertEquals(OrderLockService.LOCK_IS_ALIEN,
				orderLockService.validateLock(orderLock, new Date()));
	}

	/**
	 * Test method for
	 * {@link com.elasticpath.service.order.impl.OrderLockServiceImpl#validateLock(
	 * com.elasticpath.domain.order.OrderLock, java.util.Date)}.
	 * Order was modified before it was opened in editor.
	 */
	@Test
	public void testValidateLock4() {
		final OrderLock orderLock = new OrderLockImpl();
		final Order order = new OrderImpl();
		final long uid = 1234L;
		final long before = 10L;
		final long after = 20L;
		final Date openEditorDate = new Date(before);
		final Date orderLastModifiedDate = new Date(after);
		order.setLastModifiedDate(orderLastModifiedDate);
		order.setUidPk(uid);
		orderLock.setOrder(order);
		context.checking(new Expectations() {
			{
				allowing(mockOrderService).get(uid);
				will(returnValue(order));
			}
		});
		final List<OrderLock> results = new ArrayList<>();
		// emulate situation when order was modified
		OrderLock freshOrderLock = new OrderLockImpl();
		final long sameDate = 10L;
		freshOrderLock.setCreatedDate(sameDate);
		orderLock.setCreatedDate(sameDate);
		results.add(freshOrderLock);
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).retrieveByNamedQuery(ORDER_LOCK_BY_ORDER_UID, order.getUidPk());
				will(returnValue(results));
			}
		});
		// expectations
		assertEquals(OrderLockService.ORDER_WAS_MODIFIED,
				orderLockService.validateLock(orderLock, openEditorDate));
	}

	/**
	 * Test method for
	 * {@link com.elasticpath.service.order.impl.OrderLockServiceImpl#validateLock(
	 * com.elasticpath.domain.order.OrderLock, java.util.Date)}.
	 * Successful validation.
	 */
	@Test
	public void testValidateLock5() {
		final OrderLock orderLock = new OrderLockImpl();
		final Order order = new OrderImpl();
		final long uid = 1234L;
		final long before = 10L;
		final long after = 20L;
		final Date openEditorDate = new Date(after);
		final Date orderLastModifiedDate = new Date(before);
		order.setLastModifiedDate(orderLastModifiedDate);
		order.setUidPk(uid);
		orderLock.setOrder(order);
		context.checking(new Expectations() {
			{
				allowing(mockOrderService).get(uid);
				will(returnValue(order));
			}
		});
		final List<OrderLock> results = new ArrayList<>();
		// emulate successful validation
		OrderLock freshOrderLock = new OrderLockImpl();
		final long sameDate = 10L;
		freshOrderLock.setCreatedDate(sameDate);
		orderLock.setCreatedDate(sameDate);
		results.add(freshOrderLock);
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).retrieveByNamedQuery(ORDER_LOCK_BY_ORDER_UID, order.getUidPk());
				will(returnValue(results));
			}
		});
		// expectations
		assertEquals(OrderLockService.VALIDATED_SUCCESSFULLY,
				orderLockService.validateLock(orderLock, openEditorDate));
	}

	/**
	 * Test method for {@link com.elasticpath.service.order.impl.OrderLockServiceImpl#getOrderLock(com.elasticpath.domain.order.Order)}.
	 */
	@Test
	public void testGetOrderLock() {
		final OrderLock orderLock = new OrderLockImpl();
		final Order order = new OrderImpl();
		final long uid = 1234L;
		order.setUidPk(uid);
		orderLock.setOrder(order);
		final List<OrderLock> results = new ArrayList<>();
		results.add(orderLock);
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).initialize(with(any(Object.class)));

				allowing(getMockPersistenceEngine()).retrieveByNamedQuery(ORDER_LOCK_BY_ORDER_UID, order.getUidPk());
				will(returnValue(results));
			}
		});
		assertEquals(orderLock, orderLockService.getOrderLock(order));
	}

	/**
	 * Test method for {@link com.elasticpath.service.order.impl.OrderLockServiceImpl#get(long)}.
	 */
	@Test
	public void testGet() {
		stubGetBean(ContextIdNames.ORDER_LOCK, OrderLockImpl.class);

		final OrderLock orderLock = new OrderLockImpl();
		final long orderLockUid = 1234L;
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).get(OrderLockImpl.class, orderLockUid);
				will(returnValue(orderLock));
			}
		});
		assertNotNull(orderLockService.get(orderLockUid));
	}

	/**
	 * Test method for {@link com.elasticpath.service.order.impl.OrderLockServiceImpl#releaseOrderLock
	 * (com.elasticpath.domain.order.OrderLock, com.elasticpath.domain.cmuser.CmUser)}.
	 */
	@Test
	public void testReleaseOrderLock1() {
		final OrderLock orderLock = new OrderLockImpl();
		final CmUser lokerUser = new CmUserImpl();
		orderLock.setCmUser(lokerUser);
		context.checking(new Expectations() {
			{
				oneOf(getMockPersistenceEngine()).delete(orderLock);
			}
		});
		orderLockService.releaseOrderLock(orderLock, lokerUser);
	}

	/**
	 * Test method for {@link com.elasticpath.service.order.impl.OrderLockServiceImpl#releaseOrderLock
	 * (com.elasticpath.domain.order.OrderLock, com.elasticpath.domain.cmuser.CmUser)}.
	 */
	@Test
	public void testReleaseOrderLock2() {
		final OrderLock orderLock = new OrderLockImpl();
		final CmUser lokerUser = new CmUserImpl();
		final long firstUid = 1234L;
		final long secondUid = 4321L;
		lokerUser.setUidPk(firstUid);
		orderLock.setCmUser(lokerUser);
		final Order order = new OrderImpl();
		final long uid = 11L;
		order.setUidPk(uid);
		orderLock.setOrder(order);
		final CmUser unlokerUser = new CmUserImpl();
		unlokerUser.setUidPk(secondUid);

		try {
			orderLockService.releaseOrderLock(orderLock, unlokerUser);
		} catch (InvalidUnlockerException exception) {
			assertNotNull(exception);
		}
	}

	/**
	 * Test method for {@link com.elasticpath.service.order.impl.OrderLockServiceImpl#forceReleaseOrderLock(com.elasticpath.domain.order.OrderLock)}.
	 */
	@Test
	public void testForceReleaseOrderLock() {
		final OrderLock orderLock = new OrderLockImpl();
		context.checking(new Expectations() {
			{
				oneOf(getMockPersistenceEngine()).delete(orderLock);
			}
		});
		orderLockService.forceReleaseOrderLock(orderLock);
	}

	/**
	 * Test method for {@link com.elasticpath.service.order.impl.OrderLockServiceImpl#add(com.elasticpath.domain.order.OrderLock)}.
	 */
	@Test
	public void testAdd() {
		final OrderLock orderLock = new OrderLockImpl();
		context.checking(new Expectations() {
			{
				oneOf(getMockPersistenceEngine()).save(orderLock);
			}
		});
		orderLockService.add(orderLock);
	}

	/**
	 * Test method for {@link com.elasticpath.service.order.impl.OrderLockServiceImpl#remove(com.elasticpath.domain.order.OrderLock)}.
	 */
	@Test
	public void testRemove() {
		final OrderLock orderLock = new OrderLockImpl();
		context.checking(new Expectations() {
			{
				oneOf(getMockPersistenceEngine()).delete(orderLock);
			}
		});
		orderLockService.remove(orderLock);
	}

}
