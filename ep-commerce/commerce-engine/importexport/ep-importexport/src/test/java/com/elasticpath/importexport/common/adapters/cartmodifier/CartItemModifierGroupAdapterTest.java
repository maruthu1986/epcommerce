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
import com.elasticpath.domain.cartmodifier.CartItemModifierField;
import com.elasticpath.domain.cartmodifier.CartItemModifierGroup;
import com.elasticpath.domain.cartmodifier.CartItemModifierGroupLdf;
import com.elasticpath.domain.cartmodifier.impl.CartItemModifierFieldImpl;
import com.elasticpath.domain.cartmodifier.impl.CartItemModifierGroupImpl;
import com.elasticpath.domain.cartmodifier.impl.CartItemModifierGroupLdfImpl;
import com.elasticpath.importexport.common.dto.catalogs.CartItemModifierFieldDTO;
import com.elasticpath.importexport.common.dto.catalogs.CartItemModifierGroupDTO;
import com.elasticpath.service.cartitemmodifier.CartItemModifierService;

/**
 * Tests CartItemModifierGroupAdapter.
 */
@RunWith(MockitoJUnitRunner.class)
public class CartItemModifierGroupAdapterTest {

	private static final String CODE1 = "CODE1";
	private static final String DISPLAY_NAME_1 = "DISPLAY_NAME_1";
	private static final String CART_ITEM_MODIFIER_CODE = "CART_ITEM_MODIFIER_CODE";

	@InjectMocks
	private final CartItemModifierGroupAdapter adapter = new CartItemModifierGroupAdapter();

	@Mock
	private BeanFactory beanFactory;
	@Mock
	private CartItemModifierService cartItemModifierService;

	@Before
	public void setUp() {
		given(beanFactory.getBean(ContextIdNames.CART_ITEM_MODIFIER_GROUP_LDF))
				.willAnswer((Answer<CartItemModifierGroupLdfImpl>) invocationOnMock -> new CartItemModifierGroupLdfImpl());
		given(cartItemModifierService.findCartItemModifierFieldByCode(CART_ITEM_MODIFIER_CODE))
				.willAnswer((Answer<CartItemModifierField>) invocationOnMock -> new CartItemModifierFieldImpl());
	}

	@Test
	public void testPopulateDomainSimple() {
		CartItemModifierGroup cartItemModifierGroup = new CartItemModifierGroupImpl();

		CartItemModifierGroupDTO dto = createDTO();
		dto.setCode(CODE1);

		adapter.populateDomain(dto, cartItemModifierGroup);

		assertEquals(cartItemModifierGroup.getCode(), dto.getCode());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPopulateDomainWithCartItemModifier() {
		CartItemModifierGroup cartItemModifierGroup = new CartItemModifierGroupImpl();

		CartItemModifierGroupDTO dto = createDTO();
		dto.setCode(CODE1);
		dto.setCartItemModifierFields(new ArrayList<>());
		CartItemModifierFieldDTO cartItemModifierFieldDTO = new CartItemModifierFieldDTO();
		cartItemModifierFieldDTO.setCode(CART_ITEM_MODIFIER_CODE);
		dto.getCartItemModifierFields().add(cartItemModifierFieldDTO);

		adapter.populateDomain(dto, cartItemModifierGroup);
	}

	@Test
	public void testPopulateDomainWithLdf() {
		CartItemModifierGroup cartItemModifierGroup = new CartItemModifierGroupImpl();

		CartItemModifierGroupDTO dto = createDTO();
		dto.setCode(CODE1);
		dto.setValues(new ArrayList<>());
		DisplayValue dv1 = new DisplayValue();
		dv1.setValue(DISPLAY_NAME_1);
		dv1.setLanguage(Locale.CANADA.toString());
		dto.getValues().add(dv1);

		adapter.populateDomain(dto, cartItemModifierGroup);

		assertEquals(1, cartItemModifierGroup.getCartItemModifierGroupLdf().size());
		CartItemModifierGroupLdf ldf1 = cartItemModifierGroup.getCartItemModifierGroupLdf().iterator().next();

		assertEquals(cartItemModifierGroup.getCode(), dto.getCode());
		assertEquals(ldf1.getDisplayName(), dv1.getValue());
		assertEquals(ldf1.getLocale(), dv1.getLanguage());
	}

	@Test
	public void testPopulateDTOSimple() {
		CartItemModifierGroup cartItemModifierGroup = createCartItemModifierGroup();

		CartItemModifierGroupDTO dto = createDTO();

		adapter.populateDTO(cartItemModifierGroup, dto);

		assertEquals(cartItemModifierGroup.getCode(), dto.getCode());
	}

	@Test
	public void testPopulateDTOWithLdf() {
		CartItemModifierGroup cartItemModifierGroup = createCartItemModifierGroup();

		CartItemModifierGroupLdf ldf1 = new CartItemModifierGroupLdfImpl();
		ldf1.setDisplayName(DISPLAY_NAME_1);
		ldf1.setLocale(Locale.CANADA.toString());
		cartItemModifierGroup.addCartItemModifierGroupLdf(ldf1);

		CartItemModifierGroupDTO dto = createDTO();

		adapter.populateDTO(cartItemModifierGroup, dto);

		assertEquals(1, dto.getValues().size());
		DisplayValue displayValue = dto.getValues().get(0);

		assertEquals(ldf1.getDisplayName(), displayValue.getValue());
		assertEquals(ldf1.getLocale(), displayValue.getLanguage());
	}

	private CartItemModifierGroup createCartItemModifierGroup() {
		CartItemModifierGroupImpl cartItemModifierGroup = new CartItemModifierGroupImpl();
		cartItemModifierGroup.setCode(CODE1);
		return cartItemModifierGroup;
	}

	private CartItemModifierGroupDTO createDTO() {
		CartItemModifierGroupDTO dto = new CartItemModifierGroupDTO();
		dto.setCartItemModifierFields(new ArrayList<>());
		return dto;
	}

}
