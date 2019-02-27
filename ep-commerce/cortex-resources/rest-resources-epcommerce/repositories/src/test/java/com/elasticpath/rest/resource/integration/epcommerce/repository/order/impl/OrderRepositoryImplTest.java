/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.order.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.impl.ShoppingItemImpl;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.service.order.OrderService;
import com.elasticpath.service.search.query.CustomerSearchCriteria;
import com.elasticpath.service.search.query.OrderSearchCriteria;

/**
 * Tests for {@link OrderRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class OrderRepositoryImplTest {

	private static final String STORE_CODE = "STORE_CODE";
	private static final String ORDER_GUID = "ORDER_GUID";
	private static final String USER_GUID = "USER_GUID";

	@Mock
	private OrderService orderService;
	@Mock
	private BeanFactory coreBeanFactory;
	@InjectMocks
	private OrderRepositoryImpl orderRepository;


	@Test
	public void testFindByGuid() {
		Order order = mock(Order.class);
		when(orderService.findOrderByOrderNumber(ORDER_GUID)).thenReturn(order);

		ExecutionResult<Order> result = orderRepository.findByGuid(STORE_CODE, ORDER_GUID);

		assertTrue("This should be a successful operation.", result.isSuccessful());
		assertEquals("The resulting data should contain the expected order.", order, result.getData());
	}

	@Test
	public void testFindByGuidWithoutFindingAMatch() {
		when(orderService.findOrderByOrderNumber(ORDER_GUID)).thenReturn(null);

		ExecutionResult<Order> result = orderRepository.findByGuid(STORE_CODE, ORDER_GUID);

		assertTrue("This should result in a failed operation.", result.isFailure());
		assertEquals("The result status should be as expected.", ResourceStatus.NOT_FOUND, result.getResourceStatus());
	}

	@Test
	public void testGetOrdersForProfileWhenOneFound() {
		OrderSearchCriteria orderSearchCriteria = new OrderSearchCriteria();
		shouldGetBean(ContextIdNames.CUSTOMER_SEARCH_CRITERIA, new CustomerSearchCriteria());
		shouldGetBean(ContextIdNames.ORDER_SEARCH_CRITERIA, orderSearchCriteria);
		shouldFindOrderNumbersBySearchCriteria(orderSearchCriteria, Collections.singletonList(ORDER_GUID));

		orderRepository.findOrderIdsByCustomerGuid(STORE_CODE, USER_GUID)
				.test()
				.assertValue(ORDER_GUID);
	}

	@Test
	public void testGetOrderForProfileWhenOneFound() {
		OrderSearchCriteria orderSearchCriteria = new OrderSearchCriteria();
		shouldGetBean(ContextIdNames.CUSTOMER_SEARCH_CRITERIA, new CustomerSearchCriteria());
		shouldGetBean(ContextIdNames.ORDER_SEARCH_CRITERIA, orderSearchCriteria);
		shouldFindOrderNumberBySearchCriteria(orderSearchCriteria, Collections.singletonList(ORDER_GUID));

		orderRepository.findByGuidAndCustomerGuid(STORE_CODE, ORDER_GUID, USER_GUID)
				.test()
				.assertValueCount(1)
				.assertValue(order -> order.equals(ORDER_GUID));
	}

	@Test
	public void testGetOrdersForProfileWhenMultipleFound() {
		List<String> orderGuids = Arrays.asList(ORDER_GUID, "guid2");
		OrderSearchCriteria orderSearchCriteria = new OrderSearchCriteria();
		shouldGetBean(ContextIdNames.CUSTOMER_SEARCH_CRITERIA, new CustomerSearchCriteria());
		shouldGetBean(ContextIdNames.ORDER_SEARCH_CRITERIA, orderSearchCriteria);
		shouldFindOrderNumbersBySearchCriteria(orderSearchCriteria, orderGuids);

		orderRepository.findOrderIdsByCustomerGuid(STORE_CODE, USER_GUID)
				.test()
				.assertValueCount(2);
	}

	@Test
	public void testGetOrdersForProfileWhenNoOrdersFound() {
		OrderSearchCriteria orderSearchCriteria = new OrderSearchCriteria();
		shouldGetBean(ContextIdNames.CUSTOMER_SEARCH_CRITERIA, new CustomerSearchCriteria());
		shouldGetBean(ContextIdNames.ORDER_SEARCH_CRITERIA, orderSearchCriteria);
		shouldFindOrderNumbersBySearchCriteria(orderSearchCriteria, Collections.emptyList());

		orderRepository.findOrderIdsByCustomerGuid(STORE_CODE, USER_GUID)
				.isEmpty()
				.test()
				.assertValue(true);
	}

	@Test
	public void testGetOrderForProfileWhenNoOrdersFound() {
		OrderSearchCriteria orderSearchCriteria = new OrderSearchCriteria();
		shouldGetBean(ContextIdNames.CUSTOMER_SEARCH_CRITERIA, new CustomerSearchCriteria());
		shouldGetBean(ContextIdNames.ORDER_SEARCH_CRITERIA, orderSearchCriteria);
		shouldFindOrderNumberBySearchCriteria(orderSearchCriteria, Collections.emptyList());

		orderRepository.findByGuidAndCustomerGuid(STORE_CODE, ORDER_GUID, USER_GUID)
				.isEmpty()
				.test()
				.assertValue(true);
	}

	@Test
	public void testGetLeafShoppingItemWithNoBundledItems() {
		ShoppingItem root1 = new ShoppingItemImpl();
		root1.setGuid("root1");
		ShoppingItem root2 = new ShoppingItemImpl();
		root2.setGuid("root2");
		Order order = mock(Order.class);
		doReturn(ImmutableList.of(root1, root2)).when(order).getRootShoppingItems();
		assertThat(orderRepository.findShoppingItem(order, ImmutableList.of("root2")))
				.isEqualTo(root2);
	}

	@Test
	public void testGetLeafShoppingItemWithBundledItems() {
		ShoppingItem root1 = spy(ShoppingItemImpl.class);
		ShoppingItem firstChildOfRoot1 = new ShoppingItemImpl();
		ShoppingItem secondChildOfRoot1 = new ShoppingItemImpl();

		final String root1Guid = "root1";
		root1.setGuid(root1Guid);
		final String firstChildOfRoot1Guid = "firstChildOfRoot1";
		firstChildOfRoot1.setGuid(firstChildOfRoot1Guid);
		final String secondChildOfRoot1Guid = "secondChildOfRoot1";
		secondChildOfRoot1.setGuid(secondChildOfRoot1Guid);

		ShoppingItem root2 = spy(ShoppingItemImpl.class);
		ShoppingItem firstChildOfRoot2 = new ShoppingItemImpl();

		final String root2Guid = "root2";
		root2.setGuid(root2Guid);
		final String firstChildOfRoot2Guid = "firstChildOfRoot2";
		firstChildOfRoot2.setGuid(firstChildOfRoot2Guid);

		Order order = mock(Order.class);
		doReturn(ImmutableList.of(root1, root2)).when(order).getRootShoppingItems();

		when(root1.getChildren()).thenReturn(ImmutableList.of(firstChildOfRoot1, secondChildOfRoot1));
		when(root2.getChildren()).thenReturn(ImmutableList.of(firstChildOfRoot2));

		assertThat(orderRepository.findShoppingItem(order, ImmutableList.of(root1Guid))).isEqualTo(root1);
		assertThat(orderRepository.findShoppingItem(order, ImmutableList.of(root1Guid, secondChildOfRoot1Guid))).isEqualTo(secondChildOfRoot1);
		assertThat(orderRepository.findShoppingItem(order, ImmutableList.of(root2Guid, secondChildOfRoot1Guid))).isNull();
		assertThat(orderRepository.findShoppingItem(order, ImmutableList.of("invalid guid"))).isNull();
		assertThat(orderRepository.findShoppingItem(order, ImmutableList.of(root2Guid, "invalid guid"))).isNull();
		assertThat(orderRepository.findShoppingItem(order, ImmutableList.of(root2Guid, firstChildOfRoot2Guid, "does not exist"))).isNull();
	}

	private void shouldFindOrderNumbersBySearchCriteria(final OrderSearchCriteria orderSearchCriteria, final List<String> result) {
		when(orderService.findOrderNumbersBySearchCriteria(orderSearchCriteria, 0, Integer.MAX_VALUE)).thenReturn(result);
	}

	private void shouldFindOrderNumberBySearchCriteria(final OrderSearchCriteria orderSearchCriteria, final List<String> result) {
		when(orderService.findOrderNumbersBySearchCriteria(orderSearchCriteria, 0, 1)).thenReturn(result);
	}

	private void shouldGetBean(final String beanName, final Object bean) {
		when(coreBeanFactory.getBean(beanName)).thenReturn(bean);
	}

}
