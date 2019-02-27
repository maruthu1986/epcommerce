/*
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.service.cartitemmodifier;

import java.util.List;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.domain.cartmodifier.CartItemModifierField;
import com.elasticpath.domain.cartmodifier.CartItemModifierGroup;
import com.elasticpath.domain.cartmodifier.CartItemModifierGroupLdf;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.service.EpPersistenceService;

/**
 * Manages cart item modifiers.
 */
public interface CartItemModifierService extends EpPersistenceService {

	/**
	 * Updates the given cart item modifier field.
	 *
	 * @param cartItemModifierGroup the cart item modifier group
	 * @return the persisted instance of cart item modifier group
	 * @throws EpServiceException - in case of any errors
	 */
	CartItemModifierGroup saveOrUpdate(
		CartItemModifierGroup cartItemModifierGroup) throws EpServiceException;

	/**
	 * Removes the given cart item modifier group.
	 *
	 * @param cartItemModifierGroup the cart item modifier group
	 * @throws EpServiceException if the cart item modifier group cannot be removed.
	 */
	void remove(CartItemModifierGroup cartItemModifierGroup) throws EpServiceException;

	/**
	 * This method retrieves the cart item modifier group corresponding to the given guid.
	 *
	 * @param guid the guid for the cart item modifier field to retrieve
	 * @return the cart item modifier field or null if no matching cart can be found
	 * @throws EpServiceException - in case of any errors
	 */
	CartItemModifierGroup findCartItemModifierGroupByCode(String guid) throws EpServiceException;

	/**
	 * This method retrieves the cart item modifier group LDF corresponding to the given guid.
	 *
	 * @param guid the guid for the cart item modifier group LDF to retrieve
	 * @return the cart item modifier group LDF or null if no matching cart can be found
	 * @throws EpServiceException - in case of any errors
	 */
	CartItemModifierGroupLdf findCartItemModifierGroupLdfByGuid(String guid) throws EpServiceException;

	/**
	 * This method retrieves the cart item modifier field corresponding to the given guid.
	 *
	 * @param code the guid for the cart item modifier field to retrieve
	 * @return the cart item modifier field or null if no matching cart can be found
	 * @throws EpServiceException - in case of any errors
	 */
	CartItemModifierField findCartItemModifierFieldByCode(String code) throws EpServiceException;

	/**
	 * Updates the given cart item modifier field.
	 *
	 * @param cartItemModifierGroup the cart item modifier group
	 * @return the persisted instance of cart item modifier group
	 * @throws EpServiceException - in case of any errors
	 */
	CartItemModifierGroup update(CartItemModifierGroup cartItemModifierGroup);

	/**
	 * Adds the given cart item modifier field.
	 *
	 * @param cartItemModifierGroup the cart item modifier group
	 * @throws EpServiceException - in case of any errors
	 * @return added cart item modifier group
	 */
	CartItemModifierGroup add(CartItemModifierGroup cartItemModifierGroup);

	/**
	 * Find a list of CartItemModifierGroup by catalog UID.
	 *
	 * @param catalogUid the catalog id
	 * @return a list of CartItemModifierGroup
	 */
	List<CartItemModifierGroup> findCartItemModifierGroupByCatalogUid(long catalogUid);

	/**
	 * Retrieve all the cart item modifier fields corresponding to the given product type.
	 *
	 * @param productType the type of product.
	 * @return the list of cart item modifier.
	 */
	List<CartItemModifierField> findCartItemModifierFieldsByProductType(ProductType productType);

	/**
	 * Checks whether the given UID is in use.
	 *
	 * @param uidToCheck the UID to check that is in use
	 * @return whether the UID is currently in use or not
	 * @throws EpServiceException in case of any errors
	 */
	boolean isInUse(long uidToCheck) throws EpServiceException;
}
