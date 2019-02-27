/*
 * Copyright (c) Elastic Path Software Inc., 2016
 */

package com.elasticpath.domain.cartmodifier.impl;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

import java.util.Locale;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.elasticpath.domain.cartmodifier.CartItemModifierFieldOption;
import com.elasticpath.domain.cartmodifier.CartItemModifierFieldOptionLdf;

/**
 * * Unit tests for {@link CartItemModifierFieldOptionImpl}.
 */
public class CartItemModifierFieldOptionImplTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	/** Verify the field options LDF collection is immutable. */
	@Test(expected = UnsupportedOperationException.class)
	public void testUnmodifiableCartItemModifierFieldOptionLdf() {
		CartItemModifierFieldOption cartItemModifierFieldOption = new CartItemModifierFieldOptionImpl();
		cartItemModifierFieldOption.getCartItemModifierFieldOptionsLdf().add(new CartItemModifierFieldOptionLdfImpl());
	}

	/** Verify that adding a field option LDF via the add method is successful. */
	@Test
	public void testAddCartItemModifierGroupLdfSuccess() {
		CartItemModifierFieldOptionLdf cartItemModifierFieldOptionLdf = new CartItemModifierFieldOptionLdfImpl();
		cartItemModifierFieldOptionLdf.setLocale(Locale.US.toString());
		cartItemModifierFieldOptionLdf.setDisplayName("displayName");

		CartItemModifierFieldOption cartItemModifierFieldOption = new CartItemModifierFieldOptionImpl();
		cartItemModifierFieldOption.addCartItemModifierFieldOptionLdf(cartItemModifierFieldOptionLdf);
		assertThat(cartItemModifierFieldOption.getCartItemModifierFieldOptionsLdf(), contains(cartItemModifierFieldOptionLdf));
	}

	/** Verify the thrown exception message when the display name is empty. */
	@Test
	public void testAddCartItemModifierGroupLdfEmptyDisplayName() {
		CartItemModifierFieldOptionLdf cartItemModifierFieldOptionLdf = new CartItemModifierFieldOptionLdfImpl();
		cartItemModifierFieldOptionLdf.setLocale(Locale.US.toString());

		CartItemModifierFieldOption cartItemModifierFieldOption = new CartItemModifierFieldOptionImpl();

		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage(containsString("empty display name"));
		cartItemModifierFieldOption.addCartItemModifierFieldOptionLdf(cartItemModifierFieldOptionLdf);
	}

	/** Verify the thrown exception message when the locale is empty. */
	@Test
	public void testAddCartItemModifierGroupLdfEmptyLocale() {
		CartItemModifierFieldOptionLdf cartItemModifierFieldOptionLdf = new CartItemModifierFieldOptionLdfImpl();
		cartItemModifierFieldOptionLdf.setDisplayName("displayName");

		CartItemModifierFieldOption cartItemModifierFieldOption = new CartItemModifierFieldOptionImpl();

		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage(containsString("empty locale"));
		cartItemModifierFieldOption.addCartItemModifierFieldOptionLdf(cartItemModifierFieldOptionLdf);
	}

	/** Verify the thrown exception message when the locale is duplicated. */
	@Test
	public void testAddCartItemModifierGroupLdfSameLocal() {
		CartItemModifierFieldOptionLdf cartItemModifierFieldOptionLdf1 = new CartItemModifierFieldOptionLdfImpl();
		cartItemModifierFieldOptionLdf1.setDisplayName("displayName1");
		cartItemModifierFieldOptionLdf1.setLocale(Locale.US.toString());

		CartItemModifierFieldOptionLdf cartItemModifierFieldOptionLdf2 = new CartItemModifierFieldOptionLdfImpl();
		cartItemModifierFieldOptionLdf2.setDisplayName("displayName2");
		cartItemModifierFieldOptionLdf2.setLocale(Locale.US.toString());

		CartItemModifierFieldOption cartItemModifierFieldOption = new CartItemModifierFieldOptionImpl();
		cartItemModifierFieldOption.addCartItemModifierFieldOptionLdf(cartItemModifierFieldOptionLdf1);

		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage(containsString("same locale"));
		cartItemModifierFieldOption.addCartItemModifierFieldOptionLdf(cartItemModifierFieldOptionLdf2);
	}

}
