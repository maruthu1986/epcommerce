/*
 * Copyright (c) Elastic Path Software Inc., 2016
 */

package com.elasticpath.importexport.common.adapters.cartmodifier;

import static groovy.util.GroovyTestCase.assertEquals;
import static org.junit.Assert.assertNull;
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
import com.elasticpath.domain.cartmodifier.CartItemModifierFieldLdf;
import com.elasticpath.domain.cartmodifier.CartItemModifierType;
import com.elasticpath.domain.cartmodifier.impl.CartItemModifierFieldImpl;
import com.elasticpath.domain.cartmodifier.impl.CartItemModifierFieldLdfImpl;
import com.elasticpath.importexport.common.dto.catalogs.CartItemModifierFieldDTO;

/**
 * Tests CartItemModifierFieldAdapter.
 */
@RunWith(MockitoJUnitRunner.class)
public class CartItemModifierFieldAdapterTest {

	private static final String CODE1 = "CODE1";
	private static final String DISPLAY_NAME_1 = "DISPLAY_NAME_1";
	private static final int ORDERING = 10;
	private static final int MAX_SIZE = 5;

	@Mock
	private BeanFactory beanFactory;

	@InjectMocks
	private final CartItemModifierFieldAdapter adapter = new CartItemModifierFieldAdapter();

	@Before
	public void setUp() {
		given(beanFactory.getBean(ContextIdNames.CART_ITEM_MODIFIER_FIELD_LDF))
				.willAnswer((Answer<CartItemModifierFieldLdfImpl>) invocationOnMock -> new CartItemModifierFieldLdfImpl());
	}

	@Test
	public void testPopulateDomainSimple() {
		CartItemModifierField cartItemModifierField = new CartItemModifierFieldImpl();

		CartItemModifierFieldDTO dto = createDTO();
		dto.setCode(CODE1);
		dto.setOrdering(ORDERING);
		dto.setType(CartItemModifierType.SHORT_TEXT.getCamelName());
		dto.setRequired(true);
		dto.setMaxSize(MAX_SIZE);

		adapter.populateDomain(dto, cartItemModifierField);

		assertEquals(dto.getCode(), cartItemModifierField.getCode());
		assertEquals(dto.getOrdering(), cartItemModifierField.getOrdering());
		assertEquals(dto.getType(), cartItemModifierField.getFieldType().getCamelName());
		assertEquals(dto.isRequired(), cartItemModifierField.isRequired());
		assertEquals(dto.getMaxSize(), cartItemModifierField.getMaxSize());
	}

	@Test
	public void testPopulateDomainWithNullMaxSize() {
		CartItemModifierField cartItemModifierField = new CartItemModifierFieldImpl();

		CartItemModifierFieldDTO dto = createDTO();
		dto.setCode(CODE1);
		dto.setOrdering(ORDERING);
		dto.setType(CartItemModifierType.BOOLEAN.getCamelName());
		dto.setRequired(true);
		dto.setMaxSize(null);

		adapter.populateDomain(dto, cartItemModifierField);

		assertEquals(dto.getCode(), cartItemModifierField.getCode());
		assertEquals(dto.getOrdering(), cartItemModifierField.getOrdering());
		assertEquals(dto.getType(), cartItemModifierField.getFieldType().getCamelName());
		assertEquals(dto.isRequired(), cartItemModifierField.isRequired());
		assertNull(cartItemModifierField.getMaxSize());
	}

	@Test
	public void testPopulateDomainWithZeroMaxSize() {
		CartItemModifierField cartItemModifierField = new CartItemModifierFieldImpl();

		CartItemModifierFieldDTO dto = createDTO();
		dto.setCode(CODE1);
		dto.setOrdering(ORDERING);
		dto.setType(CartItemModifierType.BOOLEAN.getCamelName());
		dto.setRequired(true);
		dto.setMaxSize(0);

		adapter.populateDomain(dto, cartItemModifierField);

		assertEquals(dto.getCode(), cartItemModifierField.getCode());
		assertEquals(dto.getOrdering(), cartItemModifierField.getOrdering());
		assertEquals(dto.getType(), cartItemModifierField.getFieldType().getCamelName());
		assertEquals(dto.isRequired(), cartItemModifierField.isRequired());
		assertNull(cartItemModifierField.getMaxSize());
	}


	@Test
	public void testPopulateDomainWithLdf() {
		CartItemModifierField cartItemModifierField = new CartItemModifierFieldImpl();

		CartItemModifierFieldDTO dto = createDTO();
		dto.setCode(CODE1);
		dto.setOrdering(ORDERING);
		dto.setType(CartItemModifierType.SHORT_TEXT.getCamelName());
		dto.setRequired(true);
		dto.setMaxSize(MAX_SIZE);
		dto.setValues(new ArrayList<>());
		DisplayValue dv1 = new DisplayValue();
		dv1.setLanguage(Locale.CANADA.toString());
		dv1.setValue(DISPLAY_NAME_1);
		dto.getValues().add(dv1);

		adapter.populateDomain(dto, cartItemModifierField);

		assertEquals(1, cartItemModifierField.getCartItemModifierFieldsLdf().size());
		CartItemModifierFieldLdf ldf1 = cartItemModifierField.getCartItemModifierFieldsLdf().iterator().next();

		assertEquals(dv1.getValue(), ldf1.getDisplayName());
		assertEquals(dv1.getLanguage(), ldf1.getLocale());
	}

	@Test
	public void testPopulateDTOSimple() {
		CartItemModifierField cartItemModifierField = createCartItemModifierField();

		CartItemModifierFieldDTO dto = createDTO();

		adapter.populateDTO(cartItemModifierField, dto);

		assertEquals(cartItemModifierField.getCode(), dto.getCode());
		assertEquals(cartItemModifierField.getOrdering(), dto.getOrdering());
		assertEquals(cartItemModifierField.getFieldType().getCamelName(), dto.getType());
		assertEquals(cartItemModifierField.isRequired(), dto.isRequired());
		assertEquals(cartItemModifierField.getMaxSize(), dto.getMaxSize());
	}

	@Test
	public void testPopulateDTOWithNullMaxSize() {
		CartItemModifierField cartItemModifierField = createCartItemModifierField();
		cartItemModifierField.setMaxSize(null);

		CartItemModifierFieldDTO dto = createDTO();

		adapter.populateDTO(cartItemModifierField, dto);

		assertEquals(cartItemModifierField.getCode(), dto.getCode());
		assertEquals(cartItemModifierField.getOrdering(), dto.getOrdering());
		assertEquals(cartItemModifierField.getFieldType().getCamelName(), dto.getType());
		assertEquals(cartItemModifierField.isRequired(), dto.isRequired());
		assertNull(dto.getMaxSize());
	}

	@Test
	public void testPopulateDTOWithLdf() {
		CartItemModifierField cartItemModifierField = createCartItemModifierField();
		CartItemModifierFieldLdf ldf1 = new CartItemModifierFieldLdfImpl();
		ldf1.setLocale(Locale.CANADA.toString());
		ldf1.setDisplayName(DISPLAY_NAME_1);
		cartItemModifierField.addCartItemModifierFieldLdf(ldf1);

		CartItemModifierFieldDTO dto = createDTO();

		adapter.populateDTO(cartItemModifierField, dto);

		assertEquals(1, dto.getValues().size());
		DisplayValue dv1 = dto.getValues().get(0);

		assertEquals(ldf1.getDisplayName(), dv1.getValue());
		assertEquals(ldf1.getLocale(), dv1.getLanguage());
	}

	private CartItemModifierField createCartItemModifierField() {
		CartItemModifierField cartItemModifierGroup = new CartItemModifierFieldImpl();
		cartItemModifierGroup.setCode(CODE1);
		cartItemModifierGroup.setOrdering(ORDERING);
		cartItemModifierGroup.setFieldType(CartItemModifierType.BOOLEAN);
		cartItemModifierGroup.setRequired(true);
		cartItemModifierGroup.setMaxSize(MAX_SIZE);

		return cartItemModifierGroup;
	}

	private CartItemModifierFieldDTO createDTO() {
		CartItemModifierFieldDTO dto = new CartItemModifierFieldDTO();
		dto.setCartItemModifierFieldOptions(new ArrayList<>());
		return dto;
	}

}
