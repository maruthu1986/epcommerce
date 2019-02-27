/*
 * Copyright (c) Elastic Path Software Inc., 2016
 */

package com.elasticpath.tools.sync.merge.configuration.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.cartmodifier.CartItemModifierGroup;
import com.elasticpath.service.cartitemmodifier.CartItemModifierService;

/**
 * Tests for {@link CartItemModifierGroupDaoAdapterImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class CartItemModifierGroupDaoAdapterImplTest {

	public static final String CART_ITEM_MODIFIER_GROUP_GUID = "testCartItemModifierGroupGuid";
	public static final String NON_EXIST_CART_ITEM_MODIFIER_GROUP_GUID = "testNonExistCartItemModifierGroupGuid";

	@Mock
	private CartItemModifierGroup mockCartItemModifierGroup;

	@Mock
	private CartItemModifierService mockCartItemModifierService;

	@Mock
	private BeanFactory mockBeanFactory;

	private CartItemModifierGroupDaoAdapterImpl target;

	@Before
	public void setUp() {

		target = new CartItemModifierGroupDaoAdapterImpl();
		target.setBeanFactory(mockBeanFactory);
		target.setCartItemModifierService(mockCartItemModifierService);

		given(mockCartItemModifierService.findCartItemModifierGroupByCode(CART_ITEM_MODIFIER_GROUP_GUID)).willReturn(mockCartItemModifierGroup);
		given(mockCartItemModifierService.findCartItemModifierGroupByCode(NON_EXIST_CART_ITEM_MODIFIER_GROUP_GUID)).willReturn(null);
		given(mockBeanFactory.getBean(ContextIdNames.CART_ITEM_MODIFIER_GROUP)).willReturn(mockCartItemModifierGroup);

	}

	@Test
	public void testGet() {

		// when
		CartItemModifierGroup resultWithExistingGuid = target.get(CART_ITEM_MODIFIER_GROUP_GUID);
		CartItemModifierGroup resultWithNonExistingGuid = target.get(NON_EXIST_CART_ITEM_MODIFIER_GROUP_GUID);

		// verify
		assertEquals(mockCartItemModifierGroup, resultWithExistingGuid);
		assertNull(resultWithNonExistingGuid);

	}

	@Test
	public void testAdd() {

		// when
		target.add(mockCartItemModifierGroup);

		// verify
		verify(mockCartItemModifierService, times(1)).saveOrUpdate(mockCartItemModifierGroup);

	}

	@Test
	public void testUpdate() {

		// when
		target.update(mockCartItemModifierGroup);

		// verify
		verify(mockCartItemModifierService, times(1)).saveOrUpdate(mockCartItemModifierGroup);

	}

	@Test
	public void testRemove() {

		// when
		boolean resultWithExistingGuid = target.remove(CART_ITEM_MODIFIER_GROUP_GUID);
		boolean resultWithNonExistingGuid = target.remove(NON_EXIST_CART_ITEM_MODIFIER_GROUP_GUID);

		// verify
		assertTrue(resultWithExistingGuid);
		assertFalse(resultWithNonExistingGuid);
		verify(mockCartItemModifierService, times(1)).remove(mockCartItemModifierGroup);
	}

	@Test
	public void testCreateBean() {

		// when
		CartItemModifierGroup result = target.createBean(null);

		// verify
		assertEquals(mockCartItemModifierGroup, result);
	}

}
