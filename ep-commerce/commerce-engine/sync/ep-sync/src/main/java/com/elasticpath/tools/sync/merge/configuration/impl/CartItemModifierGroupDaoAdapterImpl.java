/*
 * Copyright (c) Elastic Path Software Inc., 2016
 */

package com.elasticpath.tools.sync.merge.configuration.impl;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.cartmodifier.CartItemModifierGroup;
import com.elasticpath.service.cartitemmodifier.CartItemModifierService;
import com.elasticpath.tools.sync.exception.SyncToolRuntimeException;
import com.elasticpath.tools.sync.target.impl.AbstractDaoAdapter;

/**
 * Dao Adapter for {@link CartItemModifierGroup}.
 */
public class CartItemModifierGroupDaoAdapterImpl extends AbstractDaoAdapter<CartItemModifierGroup> {

	private CartItemModifierService cartItemModifierService;
	private BeanFactory beanFactory;

	@Override
	public CartItemModifierGroup update(final CartItemModifierGroup mergedPersistence) throws SyncToolRuntimeException {
		return cartItemModifierService.saveOrUpdate(mergedPersistence);
	}

	@Override
	public void add(final CartItemModifierGroup newPersistence) throws SyncToolRuntimeException {
		cartItemModifierService.saveOrUpdate(newPersistence);
	}

	@Override
	public boolean remove(final String guid) throws SyncToolRuntimeException {
		CartItemModifierGroup cartItemModifierGroup = get(guid);
		if (cartItemModifierGroup == null) {
			return false;
		}
		cartItemModifierService.remove(cartItemModifierGroup);
		return true;
	}

	@Override
	public CartItemModifierGroup get(final String guid) {
		return cartItemModifierService.findCartItemModifierGroupByCode(guid);
	}

	@Override
	public CartItemModifierGroup createBean(final CartItemModifierGroup bean) {
		return beanFactory.getBean(ContextIdNames.CART_ITEM_MODIFIER_GROUP);
	}

	public void setCartItemModifierService(final CartItemModifierService cartItemModifierService) {
		this.cartItemModifierService = cartItemModifierService;
	}

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

}
