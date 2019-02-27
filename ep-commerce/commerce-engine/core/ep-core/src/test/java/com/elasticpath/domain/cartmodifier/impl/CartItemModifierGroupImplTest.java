/*
 * Copyright (c) Elastic Path Software Inc., 2016
 */

package com.elasticpath.domain.cartmodifier.impl;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Locale;

import org.junit.Test;

import com.elasticpath.domain.cartmodifier.CartItemModifierField;
import com.elasticpath.domain.cartmodifier.CartItemModifierGroup;
import com.elasticpath.domain.cartmodifier.CartItemModifierGroupLdf;
import com.elasticpath.domain.cartmodifier.CartItemModifierType;

/**
 * * Unit tests for {@link CartItemModifierGroupImpl}.
 */
public class CartItemModifierGroupImplTest {

	private static final String ILLEGAL_ARGUMENT_EXCEPTION_EXPECTED = "Should have thrown an IllegalArgumentException";
	private static final String GUID1 = "guid1";

	@Test(expected = UnsupportedOperationException.class)
	public void testUnmodifiableCartItemModifierGroupLdf() {
		CartItemModifierGroup cartItemModifierGroup = new CartItemModifierGroupImpl();
		cartItemModifierGroup.getCartItemModifierGroupLdf().add(new CartItemModifierGroupLdfImpl());
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testUnmodifiableCartItemModifierField() {
		CartItemModifierGroup cartItemModifierGroup = new CartItemModifierGroupImpl();
		cartItemModifierGroup.getCartItemModifierFields().add(new CartItemModifierFieldImpl());
	}

	@Test
	public void testAddCartItemModifierFieldSuccess() {
		CartItemModifierGroup cartItemModifierGroup = new CartItemModifierGroupImpl();
		CartItemModifierFieldImpl cartItemModifierField = new CartItemModifierFieldImpl();
		cartItemModifierField.setFieldType(CartItemModifierType.BOOLEAN);
		cartItemModifierField.setOrdering(0);
		cartItemModifierField.setGuid(GUID1);
		cartItemModifierGroup.addCartItemModifierField(cartItemModifierField);
	}

	@Test
	public void testAddCartItemModifierFieldEmptyCode() {
		try {
			CartItemModifierGroup cartItemModifierGroup = new CartItemModifierGroupImpl();
			cartItemModifierGroup.addCartItemModifierField(new CartItemModifierFieldImpl());
		} catch (IllegalArgumentException ex) {
			assertTrue("Shall validate empty CartItemModifierField.code", ex.getMessage().contains("empty code"));
			return;
		}

		fail(ILLEGAL_ARGUMENT_EXCEPTION_EXPECTED);
	}

	@Test
	public void testAddCartItemModifierFieldDuplicatedOrdering() {
		CartItemModifierField cartItemModifierField1 = new CartItemModifierFieldImpl();
		cartItemModifierField1.setCode("code1");
		cartItemModifierField1.setOrdering(0);
		cartItemModifierField1.setFieldType(CartItemModifierType.BOOLEAN);
		cartItemModifierField1.setGuid(GUID1);

		CartItemModifierField cartItemModifierField2 = new CartItemModifierFieldImpl();
		cartItemModifierField2.setCode("code2");
		cartItemModifierField2.setOrdering(0);
		cartItemModifierField2.setFieldType(CartItemModifierType.BOOLEAN);
		cartItemModifierField2.setGuid("guid2");

		CartItemModifierGroup cartItemModifierGroup = new CartItemModifierGroupImpl();
		cartItemModifierGroup.addCartItemModifierField(cartItemModifierField1);

		try {
			cartItemModifierGroup.addCartItemModifierField(cartItemModifierField2);
		} catch (IllegalArgumentException ex) {
			assertTrue("Shall validate duplicated CartItemModifierField.ordering", ex.getMessage().contains("same ordering"));
			return;
		}

		fail(ILLEGAL_ARGUMENT_EXCEPTION_EXPECTED);
	}

	@Test
	public void testAddCartItemModifierGroupLdfEmptyDisplayName() {
		CartItemModifierGroupLdf cartItemModifierGroupLdf = new CartItemModifierGroupLdfImpl();
		cartItemModifierGroupLdf.setLocale(Locale.US.toString());

		CartItemModifierGroup cartItemModifierGroup = new CartItemModifierGroupImpl();

		try {
			cartItemModifierGroup.addCartItemModifierGroupLdf(cartItemModifierGroupLdf);
		} catch (IllegalArgumentException ex) {
			assertTrue("Shall validate empty CartItemModifierGroupLdf.displayName", ex.getMessage().contains("empty display name"));
			return;
		}

		fail(ILLEGAL_ARGUMENT_EXCEPTION_EXPECTED);
	}

	@Test
	public void testAddCartItemModifierGroupLdfEmptyLocale() {
		CartItemModifierGroupLdf cartItemModifierGroupLdf = new CartItemModifierGroupLdfImpl();
		cartItemModifierGroupLdf.setDisplayName("displayName");

		CartItemModifierGroup cartItemModifierGroup = new CartItemModifierGroupImpl();

		try {
			cartItemModifierGroup.addCartItemModifierGroupLdf(cartItemModifierGroupLdf);
		} catch (IllegalArgumentException ex) {
			assertTrue("Shall validate empty CartItemModifierGroupLdf.locale", ex.getMessage().contains("empty locale"));
			return;
		}

		fail(ILLEGAL_ARGUMENT_EXCEPTION_EXPECTED);
	}

	@Test
	public void testAddCartItemModifierGroupLdfSameLocal() {
		CartItemModifierGroupLdf cartItemModifierGroupLdf1 = new CartItemModifierGroupLdfImpl();
		cartItemModifierGroupLdf1.setDisplayName("displayName1");
		cartItemModifierGroupLdf1.setLocale(Locale.US.toString());

		CartItemModifierGroupLdf cartItemModifierGroupLdf2 = new CartItemModifierGroupLdfImpl();
		cartItemModifierGroupLdf2.setDisplayName("displayName2");
		cartItemModifierGroupLdf2.setLocale(Locale.US.toString());

		CartItemModifierGroup cartItemModifierGroup = new CartItemModifierGroupImpl();
		cartItemModifierGroup.addCartItemModifierGroupLdf(cartItemModifierGroupLdf1);

		try {
			cartItemModifierGroup.addCartItemModifierGroupLdf(cartItemModifierGroupLdf2);
		} catch (IllegalArgumentException ex) {
			assertTrue("Shall validate duplicated CartItemModifierGroupLdf.locale", ex.getMessage().contains("same locale"));
			return;
		}

		fail(ILLEGAL_ARGUMENT_EXCEPTION_EXPECTED);
	}

}
