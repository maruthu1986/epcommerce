/**
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shoppingcart.validation.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.ImmutableMap;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.common.pricing.service.PriceLookupFacade;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.service.shoppingcart.validation.ShoppingItemDtoValidationContext;
import com.elasticpath.service.shoppingcart.validation.ShoppingItemDtoValidator;

@RunWith(MockitoJUnitRunner.class)
public class AddOrUpdateShoppingItemDtoToCartValidationServiceImplTest {
	private static final String PARENT_SKU_CODE = "PARENT_SKU_CODE";
	private static final String SKU_CODE = "SKU_CODE";
	private static final String SKU_CODE_2 = "SKU_CODE_2";
	private static final String INVALID_SKU_CODE = "INVALID_SKU_CODE";
	private static final String SKU_CODE_KEY = "skuCodeKey";
	private static final String PARENT_SKU_CODE_KEY = "parentSkuCodeKey";

	@InjectMocks
	private AddOrUpdateShoppingItemDtoToCartValidationServiceImpl addOrUpdateShoppingItemDtoToCartValidationService;

	@Mock
	private ProductSkuLookup productSkuLookup;

	@SuppressWarnings("PMD.UnusedPrivateField")
	@Mock
	private PriceLookupFacade priceLookupFacade;

	@Mock
	private BeanFactory beanFactory;

	@Mock
	private ShoppingItem parentShoppingItem;

	private ShoppingItemDto rootShoppingItemDto;

	private ShoppingItemDto childShoppingItemDto;

	@Mock
	private ShoppingCart shoppingCart;

	@Before
	public void setUp() throws Exception {
		addOrUpdateShoppingItemDtoToCartValidationService.setValidators(Collections.singleton(
				(ShoppingItemDtoValidator) context -> Collections.singleton(
						new StructuredErrorMessage("error.id", "", ImmutableMap.of(
								SKU_CODE_KEY, context.getShoppingItemDto().getSkuCode(),
								PARENT_SKU_CODE_KEY, determineParentSkuCode(context)
						)))));
		final ProductSku productSku = mock(ProductSku.class);
		when(productSkuLookup.findBySkuCode(SKU_CODE)).thenReturn(productSku);
		when(productSkuLookup.findBySkuCode(SKU_CODE_2)).thenReturn(productSku);
		when(beanFactory.getBean(ContextIdNames.SHOPPING_ITEM_DTO_VALIDATION_CONTEXT))
				.then((Answer<ShoppingItemDtoValidationContext>) invocationOnMock -> new ShoppingItemDtoValidationContextImpl());

		when(parentShoppingItem.getSkuGuid()).thenReturn(PARENT_SKU_CODE);
		rootShoppingItemDto = new ShoppingItemDto(SKU_CODE, 1);
		childShoppingItemDto = new ShoppingItemDto(SKU_CODE_2, 1);
		rootShoppingItemDto.addConstituent(childShoppingItemDto);
	}

	private String determineParentSkuCode(final ShoppingItemDtoValidationContext context) {
		Object parentShoppingItem = context.getParentShoppingItem();
		if (parentShoppingItem == null) {
			return "null";
		} else if (parentShoppingItem instanceof ShoppingItemDto) {
			return ((ShoppingItemDto) parentShoppingItem).getSkuCode();
		} else if (parentShoppingItem instanceof ShoppingItem) {
			return ((ShoppingItem) parentShoppingItem).getSkuGuid();
		}
		return "unknown";
	}

	@Test
	public void testValidateAllShoppingItemDtoConstituents() {
		// Given
		final ShoppingItemDtoValidationContext context = createShoppingItemDtoValidationContext();

		// When
		Collection<StructuredErrorMessage> errors = addOrUpdateShoppingItemDtoToCartValidationService.validate(context);

		// Then
		assertThat(errors)
				.extracting(StructuredErrorMessage::getData)
				.extracting(SKU_CODE_KEY, PARENT_SKU_CODE_KEY)
				.containsExactlyInAnyOrder(tuple(SKU_CODE, PARENT_SKU_CODE), tuple(SKU_CODE_2, SKU_CODE));
	}

	@Test
	public void testValidateWithUnselectedConstituent() {
		// Given
		childShoppingItemDto.setSelected(false);
		final ShoppingItemDtoValidationContext context = createShoppingItemDtoValidationContext();

		// When
		Collection<StructuredErrorMessage> errors = addOrUpdateShoppingItemDtoToCartValidationService.validate(context);

		// Then
		assertThat(errors)
				.extracting(StructuredErrorMessage::getData)
				.extracting(SKU_CODE_KEY, PARENT_SKU_CODE_KEY)
				.containsExactlyInAnyOrder(tuple(SKU_CODE, PARENT_SKU_CODE));
	}

	@Test
	public void testValidateWithInvalidConstituent() {
		// Given
		childShoppingItemDto.setSkuCode(INVALID_SKU_CODE);
		final ShoppingItemDtoValidationContext context = createShoppingItemDtoValidationContext();

		// When
		Collection<StructuredErrorMessage> errors = addOrUpdateShoppingItemDtoToCartValidationService.validate(context);

		// Then
		assertThat(errors)
				.extracting(StructuredErrorMessage::getData)
				.extracting(SKU_CODE_KEY, PARENT_SKU_CODE_KEY)
				.containsExactlyInAnyOrder(tuple(SKU_CODE, PARENT_SKU_CODE));
	}

	private ShoppingItemDtoValidationContext createShoppingItemDtoValidationContext() {
		final ShoppingItemDtoValidationContext context = new ShoppingItemDtoValidationContextImpl();
		context.setParentShoppingItem(parentShoppingItem);
		context.setShoppingItemDto(rootShoppingItemDto);
		context.setShoppingCart(shoppingCart);
		return context;
	}
}