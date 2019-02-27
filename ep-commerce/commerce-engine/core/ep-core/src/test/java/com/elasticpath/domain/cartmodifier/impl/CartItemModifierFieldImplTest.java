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

import com.elasticpath.domain.cartmodifier.CartItemModifierField;
import com.elasticpath.domain.cartmodifier.CartItemModifierFieldLdf;
import com.elasticpath.domain.cartmodifier.CartItemModifierFieldOption;

/**
 * Unit tests for {@link CartItemModifierFieldImpl}.
 */
public class CartItemModifierFieldImplTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private static final String GUID1 = "guid1";

	/** Verify that the field options collection is immutable. */
	@Test(expected = UnsupportedOperationException.class)
	public void testUnmodifiableCartItemModifierFieldOption() {
		CartItemModifierField cartItemModifierField = new CartItemModifierFieldImpl();
		cartItemModifierField.getCartItemModifierFieldOptions().add(new CartItemModifierFieldOptionImpl());
	}

	/** Verify that the field LDF collection is immutable. */
	@Test(expected = UnsupportedOperationException.class)
	public void testUnmodifiableCartItemModifierFieldLdf() {
		CartItemModifierField cartItemModifierField = new CartItemModifierFieldImpl();
		cartItemModifierField.getCartItemModifierFieldsLdf().add(new CartItemModifierFieldLdfImpl());
	}

	/** Test adding a field LDF via the add method is successful. */
	@Test
	public void testAddCartItemModifierFieldLdfSuccess() {
		CartItemModifierFieldLdf cartItemModifierFieldLdf = new CartItemModifierFieldLdfImpl();
		cartItemModifierFieldLdf.setLocale(Locale.US.toString());
		cartItemModifierFieldLdf.setDisplayName(GUID1);

		CartItemModifierField cartItemModifierField = new CartItemModifierFieldImpl();
		cartItemModifierField.addCartItemModifierFieldLdf(cartItemModifierFieldLdf);
		assertThat(cartItemModifierField.getCartItemModifierFieldsLdf(), contains(cartItemModifierFieldLdf));
	}

	/** Verify the thrown exception message when display name is empty. */
	@Test
	public void testAddCartItemModifierFieldLdfEmptyDisplayName() {
		CartItemModifierFieldLdf cartItemModifierFieldLdf = new CartItemModifierFieldLdfImpl();
		cartItemModifierFieldLdf.setLocale(Locale.US.toString());

		CartItemModifierField cartItemModifierField = new CartItemModifierFieldImpl();

		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage(containsString("empty display name"));
		cartItemModifierField.addCartItemModifierFieldLdf(cartItemModifierFieldLdf);
	}

	/** Verify the thrown exception message when locale is empty. */
	@Test
	public void testAddCartItemModifierFieldLdfEmptyLocale() {
		CartItemModifierFieldLdf cartItemModifierGroupLdf = new CartItemModifierFieldLdfImpl();
		cartItemModifierGroupLdf.setDisplayName("displayName");

		CartItemModifierField cartItemModifierField = new CartItemModifierFieldImpl();

		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage(containsString("empty locale"));
		cartItemModifierField.addCartItemModifierFieldLdf(cartItemModifierGroupLdf);
	}

	/** Verify the thrown exception message when there's a duplicate locale. */
	@Test
	public void testAddCartItemModifierFieldLdfSameLocal() {
		CartItemModifierFieldLdf cartItemModifierGroupLdf1 = new CartItemModifierFieldLdfImpl();
		cartItemModifierGroupLdf1.setDisplayName("displayName1");
		cartItemModifierGroupLdf1.setLocale(Locale.US.toString());

		CartItemModifierFieldLdf cartItemModifierGroupLdf2 = new CartItemModifierFieldLdfImpl();
		cartItemModifierGroupLdf2.setDisplayName("displayName2");
		cartItemModifierGroupLdf2.setLocale(Locale.US.toString());

		CartItemModifierField cartItemModifierField = new CartItemModifierFieldImpl();
		cartItemModifierField.addCartItemModifierFieldLdf(cartItemModifierGroupLdf1);

		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage(containsString("same locale"));
		cartItemModifierField.addCartItemModifierFieldLdf(cartItemModifierGroupLdf2);
	}

	/** Verify that adding a field option via the add method is successful. */
	@Test
	public void testAddCartItemModifierFieldOptionSuccess() {
		CartItemModifierFieldOption cartItemModifierFieldOption = new CartItemModifierFieldOptionImpl();
		cartItemModifierFieldOption.setOrdering(0);
		cartItemModifierFieldOption.setValue("value");

		CartItemModifierField cartItemModifierField = new CartItemModifierFieldImpl();
		cartItemModifierField.addCartItemModifierFieldOption(cartItemModifierFieldOption);
		assertThat(cartItemModifierField.getCartItemModifierFieldOptions(), contains(cartItemModifierFieldOption));
	}

	/** Verify the thrown exception message when a field option value is empty. */
	@Test
	public void testAddCartItemModifierFieldOptionEmptyValue() {
		CartItemModifierFieldOption cartItemModifierFieldOption = new CartItemModifierFieldOptionImpl();
		cartItemModifierFieldOption.setOrdering(0);

		CartItemModifierField cartItemModifierField = new CartItemModifierFieldImpl();

		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage(containsString("empty cartItemModifierFieldOption.value"));
		cartItemModifierField.addCartItemModifierFieldOption(cartItemModifierFieldOption);
	}

	/** Verify the thrown exception message when the field option ordering is duplicated. */
	@Test
	public void testAddCartItemModifierFieldOptionDuplicateOrdering() {
		CartItemModifierFieldOption cartItemModifierFieldOption1 = new CartItemModifierFieldOptionImpl();
		cartItemModifierFieldOption1.setValue("value1");
		cartItemModifierFieldOption1.setOrdering(0);

		CartItemModifierFieldOption cartItemModifierFieldOption2 = new CartItemModifierFieldOptionImpl();
		cartItemModifierFieldOption2.setValue("value2");
		cartItemModifierFieldOption2.setOrdering(0);

		CartItemModifierField cartItemModifierField = new CartItemModifierFieldImpl();
		cartItemModifierField.addCartItemModifierFieldOption(cartItemModifierFieldOption1);

		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage(containsString("same ordering"));
		cartItemModifierField.addCartItemModifierFieldOption(cartItemModifierFieldOption2);
	}

}
