/*
 * Copyright (c) Elastic Path Software Inc., 2016
 */

package com.elasticpath.importexport.common.adapters.cartmodifier;

import static groovy.util.GroovyTestCase.assertEquals;
import static org.mockito.BDDMockito.given;

import java.util.ArrayList;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.elasticpath.common.dto.DisplayValue;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.cartmodifier.CartItemModifierFieldOption;
import com.elasticpath.domain.cartmodifier.CartItemModifierFieldOptionLdf;
import com.elasticpath.domain.cartmodifier.impl.CartItemModifierFieldOptionImpl;
import com.elasticpath.domain.cartmodifier.impl.CartItemModifierFieldOptionLdfImpl;
import com.elasticpath.importexport.common.dto.catalogs.CartItemModifierFieldOptionDTO;

/**
 * Tests CartItemModifierFieldOptionAdapter.
 */
@RunWith(MockitoJUnitRunner.class)
public class CartItemModifierFieldOptionAdapterTest {

	private static final String OPTION_VALUE = "OPTION_VALUE1";
	private static final int ORDERING = 10;
	private static final String DISPLAY_NAME_1 = "DISPLAY_NAME_1";

	@InjectMocks
	private final CartItemModifierFieldOptionAdapter adapter = new CartItemModifierFieldOptionAdapter();

	@Mock
	private BeanFactory beanFactory;

	@Before
	public void setUp() {
		given(beanFactory.getBean(ContextIdNames.CART_ITEM_MODIFIER_OPTION_LDF))
				.willAnswer((Answer<CartItemModifierFieldOptionLdfImpl>) invocationOnMock -> new CartItemModifierFieldOptionLdfImpl());
	}

	@Test
	public void testPopulateDomainSimple() {
		CartItemModifierFieldOption cartItemModifierFieldOption = new CartItemModifierFieldOptionImpl();

		CartItemModifierFieldOptionDTO dto = createDTO();
		dto.setOrdering(ORDERING);
		dto.setValue(OPTION_VALUE);

		adapter.populateDomain(dto, cartItemModifierFieldOption);

		assertEquals(dto.getValue(), cartItemModifierFieldOption.getValue());
		assertEquals(dto.getOrdering(), cartItemModifierFieldOption.getOrdering());
	}

	@Test
	public void testPopulateDomainWithLdf() {
		CartItemModifierFieldOption cartItemModifierFieldOption = new CartItemModifierFieldOptionImpl();

		CartItemModifierFieldOptionDTO dto = createDTO();
		dto.setOrdering(ORDERING);
		dto.setValue(OPTION_VALUE);
		dto.setValues(new ArrayList<>());
		DisplayValue dv1 = new DisplayValue();
		dv1.setValue(DISPLAY_NAME_1);
		dv1.setLanguage(Locale.CANADA.toString());
		dto.getValues().add(dv1);

		adapter.populateDomain(dto, cartItemModifierFieldOption);

		assertEquals(1, cartItemModifierFieldOption.getCartItemModifierFieldOptionsLdf().size());
		CartItemModifierFieldOptionLdf ldf1 = cartItemModifierFieldOption.getCartItemModifierFieldOptionsLdf().iterator().next();

		assertEquals(dv1.getValue(), ldf1.getDisplayName());
		assertEquals(dv1.getLanguage(), ldf1.getLocale());
	}

	@Test
	public void testPopulateDTOSimple() {
		CartItemModifierFieldOption cartItemModifierFieldOption = createCartItemModifierFieldOption();

		CartItemModifierFieldOptionDTO dto = createDTO();

		adapter.populateDTO(cartItemModifierFieldOption, dto);

		assertEquals(cartItemModifierFieldOption.getValue(), dto.getValue());
		assertEquals(cartItemModifierFieldOption.getOrdering(), dto.getOrdering());
	}

	@Test
	public void testPopulateDTOWithLdf() {
		CartItemModifierFieldOption cartItemModifierFieldOption = createCartItemModifierFieldOption();
		CartItemModifierFieldOptionLdf ldf1 = new CartItemModifierFieldOptionLdfImpl();
		ldf1.setDisplayName(DISPLAY_NAME_1);
		ldf1.setLocale(Locale.CANADA.toString());
		cartItemModifierFieldOption.addCartItemModifierFieldOptionLdf(ldf1);

		CartItemModifierFieldOptionDTO dto = createDTO();

		adapter.populateDTO(cartItemModifierFieldOption, dto);

		assertEquals(1, dto.getValues().size());
		DisplayValue dv1 = dto.getValues().get(0);

		assertEquals(ldf1.getDisplayName(), dv1.getValue());
		assertEquals(ldf1.getLocale(), dv1.getLanguage());
	}

	private CartItemModifierFieldOption createCartItemModifierFieldOption() {
		CartItemModifierFieldOption cartItemModifierFieldOption = new CartItemModifierFieldOptionImpl();

		cartItemModifierFieldOption.setValue(OPTION_VALUE);
		cartItemModifierFieldOption.setOrdering(ORDERING);
		return cartItemModifierFieldOption;
	}

	private CartItemModifierFieldOptionDTO createDTO() {
		CartItemModifierFieldOptionDTO dto = new CartItemModifierFieldOptionDTO();
		dto.setValues(new ArrayList<>());
		return dto;
	}

}
