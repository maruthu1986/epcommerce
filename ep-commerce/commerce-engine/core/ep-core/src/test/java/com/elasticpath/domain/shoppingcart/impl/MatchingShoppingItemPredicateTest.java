/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.domain.shoppingcart.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.junit.Test;

import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.ShoppingItemPredicateUtils;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.service.catalog.impl.ProductSkuLookupImpl;

public final class MatchingShoppingItemPredicateTest {

	private final ProductSkuLookup productSkuLookup = new ProductSkuLookupImpl();

	@Test
	public void predicateShouldMatchWithSameConfigurableItem() {

		ShoppingItem configurableItem1 = mock(ShoppingItem.class);
		ShoppingCart shoppingCart = mock(ShoppingCart.class);

		List<ShoppingItem> cartItems = new ArrayList<>();
		cartItems.add(configurableItem1);

		when(configurableItem1.isGiftCertificate(productSkuLookup)).thenReturn(false);
		when(configurableItem1.isMultiSku(productSkuLookup)).thenReturn(false);
		when(configurableItem1.isConfigurable(productSkuLookup)).thenReturn(true);
		when(configurableItem1.isSameConfigurableItem(productSkuLookup, configurableItem1)).thenReturn(true);
		when(configurableItem1.getSkuGuid()).thenReturn("SKU");
		when(shoppingCart.getAllShoppingItems()).thenReturn(cartItems);

		Predicate matchingShoppingItemPredicate =
				ShoppingItemPredicateUtils.matchingShoppingItemPredicate(configurableItem1, productSkuLookup);
		ShoppingItem matchingRecipient = (ShoppingItem) CollectionUtils.find(cartItems, matchingShoppingItemPredicate);
		assertThat(matchingRecipient).isNotNull();
	}

	@Test
	public void predicateShouldNotMatchWithDifferentNonGiftCertificateItems() {

		ShoppingItem configurableItem1 = mock(ShoppingItem.class);
		ShoppingItem configurableItem2 = mock(ShoppingItem.class);
		ShoppingCart shoppingCart = mock(ShoppingCart.class);

		List<ShoppingItem> cartItems = new ArrayList<>();
		cartItems.add(configurableItem2);

		when(configurableItem1.isGiftCertificate(productSkuLookup)).thenReturn(false);
		when(configurableItem1.isMultiSku(productSkuLookup)).thenReturn(false);
		when(configurableItem1.isConfigurable(productSkuLookup)).thenReturn(true);
		when(configurableItem1.isSameConfigurableItem(productSkuLookup, configurableItem2)).thenReturn(false);
		when(configurableItem1.getSkuGuid()).thenReturn("SKU");
		when(shoppingCart.getAllShoppingItems()).thenReturn(cartItems);

		Predicate matchingShoppingItemPredicate =
				ShoppingItemPredicateUtils.matchingShoppingItemPredicate(configurableItem1, productSkuLookup);
		ShoppingItem matchingRecipient = (ShoppingItem) CollectionUtils.find(cartItems, matchingShoppingItemPredicate);
		assertThat(matchingRecipient).isNull();
	}

	@Test
	public void predicateShouldNotMatchWhenObjectNotAShoppingItem() {

		ShoppingItem configurableItem1 = mock(ShoppingItem.class);
		Object object = mock(Object.class);

		List<Object> cartItems = new ArrayList<>();
		cartItems.add(object);

		Predicate matchingShoppingItemPredicate =
				ShoppingItemPredicateUtils.matchingShoppingItemPredicate(configurableItem1, productSkuLookup);
		ShoppingItem matchingRecipient = (ShoppingItem) CollectionUtils.find(cartItems, matchingShoppingItemPredicate);
		assertThat(matchingRecipient).isNull();
	}

	@Test
	public void predicateShouldNotMatchWhenObjectIsGiftCertificate() {

		ShoppingItem giftCertificateItem = mock(ShoppingItem.class);

		List<ShoppingItem> cartItems = new ArrayList<>();
		cartItems.add(giftCertificateItem);

		when(giftCertificateItem.isGiftCertificate(productSkuLookup)).thenReturn(true);

		Predicate matchingShoppingItemPredicate =
				ShoppingItemPredicateUtils.matchingShoppingItemPredicate(giftCertificateItem, productSkuLookup);
		ShoppingItem matchingRecipient = (ShoppingItem) CollectionUtils.find(cartItems, matchingShoppingItemPredicate);
		assertThat(matchingRecipient).isNull();
	}

}
