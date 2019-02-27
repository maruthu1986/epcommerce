/*
 * Copyright (c) Elastic Path Software Inc., 2016
 */

package com.elasticpath.test.integration.cartitemmodifier;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.domain.cartmodifier.CartItemModifierField;
import com.elasticpath.domain.cartmodifier.CartItemModifierFieldLdf;
import com.elasticpath.domain.cartmodifier.CartItemModifierFieldOption;
import com.elasticpath.domain.cartmodifier.CartItemModifierFieldOptionLdf;
import com.elasticpath.domain.cartmodifier.CartItemModifierGroup;
import com.elasticpath.domain.cartmodifier.CartItemModifierGroupLdf;
import com.elasticpath.domain.cartmodifier.CartItemModifierType;
import com.elasticpath.domain.cartmodifier.impl.CartItemModifierFieldImpl;
import com.elasticpath.domain.cartmodifier.impl.CartItemModifierFieldLdfImpl;
import com.elasticpath.domain.cartmodifier.impl.CartItemModifierFieldOptionImpl;
import com.elasticpath.domain.cartmodifier.impl.CartItemModifierFieldOptionLdfImpl;
import com.elasticpath.domain.cartmodifier.impl.CartItemModifierGroupImpl;
import com.elasticpath.domain.cartmodifier.impl.CartItemModifierGroupLdfImpl;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.catalog.impl.ProductTypeImpl;
import com.elasticpath.domain.factory.TestGuidUtility;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.service.cartitemmodifier.CartItemModifierService;
import com.elasticpath.service.catalog.ProductTypeService;
import com.elasticpath.test.db.DbTestCase;
import com.elasticpath.test.util.Utils;

/**
 * Test that the cart item modifier is persisted correctly.
 */
public abstract class AbstractCartItemModifierTest extends DbTestCase {

	private static final int ZERO = 0;
	private static final int FIRST = 1;
	private static final int SECOND = 2;
	private static final int THIRD = 3;
	private static final int FORTH = 4;
	private static final int FIFTH = 5;

	@Autowired
	private CartItemModifierService cartItemModifierService;

	@Autowired
	private ProductTypeService productTypeService;

	protected ProductType persistSimpleProductTypeWithCartItemModifierGroup() {
		Catalog catalog = createPersistedCatalog();
		TaxCode taxCode = createPersistedTaxCode("taxCode");
		final ProductType productType = createProductType(catalog, taxCode);

		// Create CartItemModifierGroup
		CartItemModifierGroup cartItemModifierGroup = new CartItemModifierGroupImpl();
		cartItemModifierGroup.setCode(TestGuidUtility.getGuid());
		cartItemModifierGroup.setCatalog(catalog);

		//Add CartItemModifierGroupLdf to CartItemModifierGroup
		CartItemModifierGroupLdf cartItemModifierGroupLdf = createCartItemModifierGroupLdf(Locale.US, "CartItemModifierGroupLdf1-ENGLISH");
		cartItemModifierGroup.addCartItemModifierGroupLdf(cartItemModifierGroupLdf);
		cartItemModifierGroupLdf = createCartItemModifierGroupLdf(Locale.FRENCH, "CartItemModifierGroupLdf1-French");
		cartItemModifierGroup.addCartItemModifierGroupLdf(cartItemModifierGroupLdf);
		cartItemModifierGroupLdf = createCartItemModifierGroupLdf(Locale.GERMAN, "CartItemModifierGroupLdf2-GERMAN");
		cartItemModifierGroup.addCartItemModifierGroupLdf(cartItemModifierGroupLdf);

		//Add CartItemModifierField to CartItemModifierGroup
		CartItemModifierField cartItemModifierField = createCartItemModifierField("code1", CartItemModifierType.SHORT_TEXT, false, 0);
		cartItemModifierGroup.addCartItemModifierField(cartItemModifierField);
		cartItemModifierField = createCartItemModifierField("code2", CartItemModifierType.INTEGER, false, FIRST);
		cartItemModifierGroup.addCartItemModifierField(cartItemModifierField);
		cartItemModifierField = createCartItemModifierField("code3", CartItemModifierType.BOOLEAN, false, SECOND);
		cartItemModifierGroup.addCartItemModifierField(cartItemModifierField);

		CartItemModifierField cartItemModifierFieldWithOption;
		cartItemModifierFieldWithOption = createCartItemModifierFieldWithOption("code4",
				CartItemModifierType.SHORT_TEXT,
				false, THIRD,
				"optionValue1");
		cartItemModifierGroup.addCartItemModifierField(cartItemModifierFieldWithOption);
		cartItemModifierFieldWithOption = createCartItemModifierFieldWithOption("code5", CartItemModifierType.INTEGER, false, FORTH, "optionValue2");
		cartItemModifierGroup.addCartItemModifierField(cartItemModifierFieldWithOption);
		cartItemModifierFieldWithOption = createCartItemModifierFieldWithOption("code6", CartItemModifierType.BOOLEAN, false, FIFTH, "optionValue3");
		cartItemModifierGroup.addCartItemModifierField(cartItemModifierFieldWithOption);

		// Persist CartItemModifierGroup and dependent objects
		cartItemModifierGroup = cartItemModifierService.add(cartItemModifierGroup);

		//Add CartItemModifierGroup to ExtProductType
		productType.getCartItemModifierGroups().add(cartItemModifierGroup);

		// Persist ExtProductType
		productTypeService.add(productType);

		return productType;
	}

	protected CartItemModifierField createCartItemModifierFieldWithOption(final String code, final CartItemModifierType attributeType,
			final boolean required,
			final int ordering, final String optionValue) {
		CartItemModifierField cartItemModifierField = createCartItemModifierField(code, attributeType, required, ordering);

		CartItemModifierFieldOption cartItemModifierFieldOption = createCartItemModifierFieldOption(optionValue + "1", ZERO);
		cartItemModifierField.addCartItemModifierFieldOption(cartItemModifierFieldOption);
		cartItemModifierFieldOption = createCartItemModifierFieldOption(optionValue + "2", FIRST);
		cartItemModifierField.addCartItemModifierFieldOption(cartItemModifierFieldOption);
		cartItemModifierFieldOption = createCartItemModifierFieldOption(optionValue + "3", SECOND);
		cartItemModifierField.addCartItemModifierFieldOption(cartItemModifierFieldOption);

		return cartItemModifierField;
	}

	protected CartItemModifierFieldOption createCartItemModifierFieldOption(final String value, final int ordering) {
		CartItemModifierFieldOption cartItemModifierFieldOption = new CartItemModifierFieldOptionImpl();
		cartItemModifierFieldOption.setOrdering(ordering);
		cartItemModifierFieldOption.setValue(value);

		CartItemModifierFieldOptionLdf cartItemModifierFieldOptionLdf = createCartItemModifierFieldOptionLdf(Locale.US, "displayName-ENGLISH");
		cartItemModifierFieldOption.addCartItemModifierFieldOptionLdf(cartItemModifierFieldOptionLdf);
		cartItemModifierFieldOptionLdf = createCartItemModifierFieldOptionLdf(Locale.FRENCH, "displayName-FRENCH");
		cartItemModifierFieldOption.addCartItemModifierFieldOptionLdf(cartItemModifierFieldOptionLdf);

		return cartItemModifierFieldOption;
	}

	protected CartItemModifierFieldOptionLdf createCartItemModifierFieldOptionLdf(final Locale locale, final String displayName) {
		CartItemModifierFieldOptionLdf cartItemModifierFieldOptionLdf = new CartItemModifierFieldOptionLdfImpl();
		cartItemModifierFieldOptionLdf.setDisplayName(displayName);
		cartItemModifierFieldOptionLdf.setLocale(locale.toString());

		return cartItemModifierFieldOptionLdf;
	}

	protected CartItemModifierField createCartItemModifierField(final String code, final CartItemModifierType attributeType, final boolean required,
			final int ordering) {
		CartItemModifierField cartItemModifierField = new CartItemModifierFieldImpl();

		cartItemModifierField.setCode(code);
		cartItemModifierField.setFieldType(attributeType);
		cartItemModifierField.setOrdering(ordering);
		cartItemModifierField.setRequired(required);

		CartItemModifierFieldLdf cartItemModifierFieldLdf;
		cartItemModifierFieldLdf = createCartItemModifierFieldLdf(Locale.US, "CartItemModifierFieldLdf1-ENGLISH");
		cartItemModifierField.addCartItemModifierFieldLdf(cartItemModifierFieldLdf);
		cartItemModifierFieldLdf = createCartItemModifierFieldLdf(Locale.FRENCH, "CartItemModifierFieldLdf1-FRENCH");
		cartItemModifierField.addCartItemModifierFieldLdf(cartItemModifierFieldLdf);
		cartItemModifierFieldLdf = createCartItemModifierFieldLdf(Locale.GERMAN, "CartItemModifierFieldLdf21-GERMAN");
		cartItemModifierField.addCartItemModifierFieldLdf(cartItemModifierFieldLdf);

		return cartItemModifierField;
	}

	protected CartItemModifierFieldLdf createCartItemModifierFieldLdf(final Locale locale, final String displayName) {
		CartItemModifierFieldLdf cartItemModifierFieldLdf = new CartItemModifierFieldLdfImpl();
		cartItemModifierFieldLdf.setLocale(locale.toString());
		cartItemModifierFieldLdf.setDisplayName(displayName);

		return cartItemModifierFieldLdf;
	}

	protected CartItemModifierGroupLdf createCartItemModifierGroupLdf(final Locale locale, final String displayName) {
		CartItemModifierGroupLdf cartItemModifierGroupLdf = new CartItemModifierGroupLdfImpl();
		cartItemModifierGroupLdf.setDisplayName(displayName);
		cartItemModifierGroupLdf.setLocale(locale.toString());
		return cartItemModifierGroupLdf;
	}

	protected ProductType createProductType(final Catalog catalog, final TaxCode taxCode) {
		final ProductType productType = new ProductTypeImpl();
		productType.setName(Utils.uniqueCode("productName"));
		productType.initialize();
		productType.setCatalog(catalog);
		productType.setTaxCode(taxCode);

		return productType;
	}

}
