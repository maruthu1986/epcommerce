/*
 * Copyright (c) Elastic Path Software Inc., 2016
 */

package com.elasticpath.service.cartitemmodifier.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.cartmodifier.CartItemModifierField;
import com.elasticpath.domain.cartmodifier.CartItemModifierGroup;
import com.elasticpath.domain.cartmodifier.CartItemModifierGroupLdf;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.service.cartitemmodifier.CartItemModifierService;
import com.elasticpath.service.impl.AbstractEpPersistenceServiceImpl;

/**
 * Manages cart item modifiers.
 */
public class CartItemModifierServiceImpl extends AbstractEpPersistenceServiceImpl implements CartItemModifierService {

	@Override
	public CartItemModifierGroup saveOrUpdate(final CartItemModifierGroup cartItemModifierGroup) throws EpServiceException {
		sanityCheck();
		return getPersistenceEngine().saveOrUpdate(cartItemModifierGroup);
	}

	@Override
	public void remove(final CartItemModifierGroup cartItemModifierGroup) throws EpServiceException {
		sanityCheck();
		getPersistenceEngine().delete(cartItemModifierGroup);
	}

	@Override
	public Object getObject(final long uid) throws EpServiceException {
		sanityCheck();
		return get(uid);
	}

	private CartItemModifierGroup get(final long cartItemModifierFieldUid) throws EpServiceException {
		sanityCheck();
		CartItemModifierGroup cartItemModifierGroup;
		if (cartItemModifierFieldUid <= 0) {
			cartItemModifierGroup = getBean(ContextIdNames.CART_ITEM_MODIFIER_GROUP);
		} else {
			cartItemModifierGroup = getPersistentBeanFinder().get(ContextIdNames.CART_ITEM_MODIFIER_GROUP, cartItemModifierFieldUid);
		}
		return cartItemModifierGroup;
	}

	@Override
	public CartItemModifierGroup findCartItemModifierGroupByCode(final String code) throws EpServiceException {
		sanityCheck();
		List<CartItemModifierGroup> cartItemModifierGroups = getPersistenceEngine().retrieveByNamedQuery("CART_ITEM_MODIFIER_GROUP_BY_CODE", code);

		if (cartItemModifierGroups != null && cartItemModifierGroups.size() > 1) {
			throw new IllegalStateException("Cannot have two CartItemModifierGroup with the same GUID");
		}

		if (cartItemModifierGroups != null && !cartItemModifierGroups.isEmpty()) {
			return cartItemModifierGroups.get(0);
		}

		return null;
	}

	@Override
	public CartItemModifierGroupLdf findCartItemModifierGroupLdfByGuid(final String guid) throws EpServiceException {
		sanityCheck();
		List<CartItemModifierGroupLdf> cartItemModifierGroupLdfs = getPersistenceEngine()
				.retrieveByNamedQuery("CART_ITEM_MODIFIER_GROUP_LDF_BY_GUID", guid);

		if (cartItemModifierGroupLdfs != null && cartItemModifierGroupLdfs.size() > 1) {
			throw new IllegalStateException("Cannot have two CartItemModifierGroupLdf with the same GUID");
		}

		if (cartItemModifierGroupLdfs != null && !cartItemModifierGroupLdfs.isEmpty()) {
			return cartItemModifierGroupLdfs.get(0);
		}

		return null;
	}

	@Override
	public List<CartItemModifierGroup> findCartItemModifierGroupByCatalogUid(final long catalogUid) {
		sanityCheck();
		List<CartItemModifierGroup> cartItemModifierGroups = getPersistenceEngine().retrieveByNamedQuery("CART_ITEM_MODIFIER_GROUP_BY_CATALOG_UID",
				catalogUid);

		if (cartItemModifierGroups == null || cartItemModifierGroups.isEmpty()) {
			return new ArrayList<>();
		}

		return cartItemModifierGroups;
	}

	@Override
	public CartItemModifierField findCartItemModifierFieldByCode(final String code) throws EpServiceException {
		sanityCheck();
		List<CartItemModifierField> cartItemModifierFields = getPersistenceEngine().retrieveByNamedQuery("CART_ITEM_MODIFIER_FIELD_BY_CODE", code);

		if (cartItemModifierFields != null && cartItemModifierFields.size() > 1) {
			throw new IllegalStateException("Cannot have two CartItemModifierField with the same GUID");
		}

		if (cartItemModifierFields != null && !cartItemModifierFields.isEmpty()) {
			return cartItemModifierFields.get(0);
		}

		return null;
	}

	@Override
	public CartItemModifierGroup update(final CartItemModifierGroup cartItemModifierGroup) {
		return getPersistenceEngine().merge(cartItemModifierGroup);
	}

	@Override
	public CartItemModifierGroup add(final CartItemModifierGroup cartItemModifierGroup) {
		return getPersistenceEngine().update(cartItemModifierGroup);
	}

	@Override
	public List<CartItemModifierField> findCartItemModifierFieldsByProductType(final ProductType productType) {

		List<CartItemModifierField> fields = new ArrayList<>();

		Set<CartItemModifierGroup> cartItemModifierGroups = productType.getCartItemModifierGroups();

		for (CartItemModifierGroup cartItemModifierGroup : cartItemModifierGroups) {
			fields.addAll(cartItemModifierGroup.getCartItemModifierFields());
		}
		return fields;
	}

	@Override
	public boolean isInUse(final long uidToCheck) throws EpServiceException {
		sanityCheck();
		return !getPersistenceEngine().retrieveByNamedQuery("CART_ITEM_MODIFIER_GROUP_IN_USE", uidToCheck).isEmpty();
	}
}
