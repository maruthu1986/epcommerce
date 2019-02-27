/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
/*

 */

package com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.impl;

import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static com.elasticpath.rest.resource.integration.epcommerce.repository.ErrorCheckPredicate.createErrorCheckPredicate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableSet;
import io.reactivex.Single;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.cartmodifier.CartItemModifierField;
import com.elasticpath.domain.cartmodifier.CartItemModifierFieldOption;
import com.elasticpath.domain.cartmodifier.CartItemModifierGroup;
import com.elasticpath.domain.cartmodifier.impl.CartItemModifierFieldImpl;
import com.elasticpath.domain.cartmodifier.impl.CartItemModifierFieldOptionImpl;
import com.elasticpath.domain.cartmodifier.impl.CartItemModifierGroupImpl;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.ShoppingCartRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.order.OrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.order.impl.OrderRepositoryImpl;
import com.elasticpath.rest.resource.integration.epcommerce.repository.sku.ProductSkuRepository;
import com.elasticpath.service.cartitemmodifier.CartItemModifierService;

/**
 * Tests for {@link CartItemModifiersRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class CartItemModifiersRepositoryImplTest {

	private static final String GROUP_CODE = "groupCode";
	private static final String FIELD_CODE1 = "fieldCode1";
	private static final String FIELD_CODE2 = "fieldCode2";
	private static final String FIELD_CODE3 = "fieldCode3";
	private static final String FIELD_CODE4 = "fieldCode4";
	private static final String FIELD_CODE5 = "fieldCode5";
	private static final int FIELD_ORDERING_1 = 1;
	private static final int FIELD_ORDERING_2 = 2;
	private static final int FIELD_ORDERING_3 = 3;
	private static final int FIELD_ORDERING_4 = 4;
	private static final int FIELD_ORDERING_5 = 5;
	private static final String OPTION_VALUE1 = "option1";
	private static final String OPTION_VALUE2 = "option2";
	private static final int REQUIRED_FIELDS_COUNT = 3;
	private static final String SKU_GUID = "testSkuGuid";
	private static final String FIELD_VALUE_1 = "testFieldValue1";
	private static final String SHOPPING_ITEM_GUID = "testShoppingItemGuid";

	private static final String STORE_CODE = "testStoreCode";

	private static final String PURCHASE_GUID = "testPurchaseGuid";

	private static final String PURCHASE_LINE_ITEM_GUID = "testPurchaseLineItemGuid";

	private static final String CART_GUID = "testcartGUID";

	private CartItemModifierGroup cartItemModifierGroup;

	private CartItemModifierField field1;

	private CartItemModifierField field2;

	@Mock
	private CartItemModifierService cartItemModifierService;

	@Mock
	private ShoppingCartRepository shoppingCartRepository;

	@Mock
	private ProductSkuRepository productSkuRepository;

	@Mock
	private OrderRepository orderRepository;

	@InjectMocks
	private CartItemModifiersRepositoryImpl cartItemModifiersRepository;

	@Mock
	private ShoppingCart shoppingCart;

	@Mock
	private ShoppingItem shoppingItem;

	@Mock
	private ProductSku productSku;

	@Mock
	private Product product;

	@Mock
	private ProductType productType;

	@Mock
	private Order order;

	@Mock
	private OrderSku orderSku;

	private final List<CartItemModifierField> cartItemModifierFields = buildFields();

	private final Map<String, String> shoppingItemData = new HashMap<>();

	@Before
	public void setUp() {

		//given
		cartItemModifierGroup = buildCartItemModifierGroup();
		when(cartItemModifierService.findCartItemModifierGroupByCode(GROUP_CODE)).thenReturn(cartItemModifierGroup);

		when(product.getProductType()).thenReturn(productType);
		when(cartItemModifierService.findCartItemModifierFieldsByProductType(productType)).thenReturn(cartItemModifierFields);

		given(productSkuRepository.getProductSkuWithAttributesByGuid(SKU_GUID)).willReturn(ExecutionResultFactory.createReadOK(productSku));
		given(productSku.getProduct()).willReturn(product);
		given(product.getProductType()).willReturn(productType);
		given(productType.getCartItemModifierGroups()).willReturn(ImmutableSet.of(cartItemModifierGroup));
	}

	@Test(expected = NullPointerException.class)
	public void testFindCartItemModifierGroupByCodeWhenCodeIsNull() {

		// when
		cartItemModifiersRepository.findCartItemModifierGroupByCode(null);
	}

	@Test
	public void testFindCartItemModifierGroupByCodeWhenValidCode() {

		// when
		ExecutionResult<CartItemModifierGroup> group = cartItemModifiersRepository.findCartItemModifierGroupByCode(GROUP_CODE);

		// verify
		assertNotNull(group);
		assertEquals(cartItemModifierGroup, group.getData());
		assertEquals(ResourceStatus.READ_OK, group.getResourceStatus());
	}

	@Test
	public void testFindCartItemModifierGroupByCodeWhenInvalidCode() {

		//given
		String invalidCode = "invalidCode";
		// when
		ExecutionResult<CartItemModifierGroup> group = cartItemModifiersRepository.findCartItemModifierGroupByCode(invalidCode);

		// verify
		assertNotNull(group);
		assertNull(group.getData());
		assertEquals(ResourceStatus.NOT_FOUND, group.getResourceStatus());
	}

	@Test(expected = NullPointerException.class)
	public void testFindCartItemModifierFieldByWhenAnyParamIsNull() {
		//given
		String cartItemModifierFieldCode = null;
		String cartItemModifierGroupCode = "code";
		// when
		cartItemModifiersRepository.findCartItemModifierFieldBy(cartItemModifierFieldCode, cartItemModifierGroupCode);
	}

	@Test
	public void testFindCartItemModifierFieldBy() {

		// when
		ExecutionResult<CartItemModifierField> field = cartItemModifiersRepository.findCartItemModifierFieldBy(FIELD_CODE1, GROUP_CODE);
		// verify
		assertNotNull(field);
		assertEquals(FIELD_CODE1, field.getData().getCode());
		assertEquals(ResourceStatus.READ_OK, field.getResourceStatus());
	}

	@Test
	public void testFindCartItemModifierFieldByWithInvalidFieldCode() {

		String invalidCode = "invalidCode";

		// when
		ExecutionResult<CartItemModifierField> field = cartItemModifiersRepository.findCartItemModifierFieldBy(invalidCode, GROUP_CODE);
		// verify
		assertNotNull(field);
		assertNull(field.getData());
		assertEquals(ResourceStatus.NOT_FOUND, field.getResourceStatus());
	}

	@Test(expected = NullPointerException.class)
	public void testFindCartItemModifierFieldOptionByWhenAnyParamIsNull() {
		//given
		String cartItemModifierFieldCode = null;
		String cartItemModifierGroupCode = "code";
		// when
		cartItemModifiersRepository.findCartItemModifierFieldOptionBy(OPTION_VALUE1, cartItemModifierFieldCode, cartItemModifierGroupCode);
	}

	@Test
	public void testFindCartItemModifierFieldOptionBy() {

		// when
		ExecutionResult<CartItemModifierFieldOption> option = cartItemModifiersRepository.findCartItemModifierFieldOptionBy(OPTION_VALUE1,
				FIELD_CODE1, GROUP_CODE);
		// verify
		assertNotNull(option);
		assertEquals(OPTION_VALUE1, option.getData().getValue());
		assertEquals(ResourceStatus.READ_OK, option.getResourceStatus());
	}

	@Test
	public void testFindCartItemModifierFieldOptionByWithInvalidOptionValue() {

		String invalidOptionValue = "invalidValue";
		// when
		ExecutionResult<CartItemModifierFieldOption> option = cartItemModifiersRepository.findCartItemModifierFieldOptionBy(invalidOptionValue,
				FIELD_CODE1, GROUP_CODE);
		// verify
		assertNotNull(option);
		assertNull(OPTION_VALUE1, option.getData());
		assertEquals(ResourceStatus.NOT_FOUND, option.getResourceStatus());
	}

	private CartItemModifierGroup buildCartItemModifierGroup() {
		CartItemModifierGroup group = new CartItemModifierGroupImpl();
		group.initialize();
		group.setCode(GROUP_CODE);
		field1 = new CartItemModifierFieldImpl();
		field1.initialize();
		field1.setCode(FIELD_CODE1);
		field1.setOrdering(0);
		field2 = new CartItemModifierFieldImpl();
		field2.initialize();
		field2.setCode(FIELD_CODE2);
		field1.setOrdering(1);
		group.addCartItemModifierField(field1);
		group.addCartItemModifierField(field2);

		CartItemModifierFieldOption option1 = new CartItemModifierFieldOptionImpl();
		option1.setValue(OPTION_VALUE1);
		option1.setOrdering(0);
		CartItemModifierFieldOption option2 = new CartItemModifierFieldOptionImpl();
		option2.setValue(OPTION_VALUE2);
		option2.setOrdering(1);

		field1.addCartItemModifierFieldOption(option1);
		field1.addCartItemModifierFieldOption(option2);

		return group;
	}

	@Test
	public void testFindCartItemModifierValuesContainsOnlyValidModifiers() {
		given(shoppingCartRepository.getShoppingCart(CART_GUID))
				.willReturn(Single.just(shoppingCart));
		given(shoppingCart.getCartItemByGuid(SHOPPING_ITEM_GUID))
				.willReturn(shoppingItem);
		given(shoppingItem.getSkuGuid())
				.willReturn(SKU_GUID);
		given(shoppingItem.getFields())
				.willReturn(shoppingItemData);

		given(productSkuRepository.getProductSkuWithAttributesByGuidAsSingle(SKU_GUID))
				.willReturn(Single.just(productSku));
		given(productSku.getProduct())
				.willReturn(product);
		given(product.getProductType())
				.willReturn(productType);
		given(productType.getCartItemModifierGroups())
				.willReturn(ImmutableSet.of(cartItemModifierGroup));

		shoppingItemData.put(FIELD_CODE1, FIELD_VALUE_1);

		cartItemModifiersRepository.findCartItemModifierValues(CART_GUID, SHOPPING_ITEM_GUID)
				.test()
				.assertNoErrors()
				.assertValue(shoppingData -> shoppingData.get(field1).equals(FIELD_VALUE_1) && shoppingData.get(field2).equals(StringUtils.EMPTY));
	}

	@Test
	public void testFindCartItemModifierValuesWithNoCartFoundFailure() {
		given(shoppingCartRepository.getShoppingCart(CART_GUID))
				.willReturn(Single.error(ResourceOperationFailure.notFound(ShoppingCartRepositoryImpl.DEFAULT_CART_NOT_FOUND)));

		cartItemModifiersRepository.findCartItemModifierValues(CART_GUID, SHOPPING_ITEM_GUID)
				.test()
				.assertError(createErrorCheckPredicate(ShoppingCartRepositoryImpl.DEFAULT_CART_NOT_FOUND, ResourceStatus.NOT_FOUND));
	}

	@Test
	public void testFindPurchaseItemModifierValuesContainsOnlyValidModifiers() {
		given(orderRepository.findByGuidAsSingle(STORE_CODE, PURCHASE_GUID))
				.willReturn(Single.just(order));
		given(order.getOrderSkuByGuid(PURCHASE_LINE_ITEM_GUID))
				.willReturn(orderSku);
		given(orderSku.getSkuGuid())
				.willReturn(SKU_GUID);
		given(orderSku.getFields())
				.willReturn(shoppingItemData);

		given(productSkuRepository.getProductSkuWithAttributesByGuidAsSingle(SKU_GUID))
				.willReturn(Single.just(productSku));
		given(productSku.getProduct())
				.willReturn(product);
		given(product.getProductType())
				.willReturn(productType);
		given(productType.getCartItemModifierGroups())
				.willReturn(ImmutableSet.of(cartItemModifierGroup));
		shoppingItemData.put(FIELD_CODE1, FIELD_VALUE_1);

		cartItemModifiersRepository.findPurchaseItemModifierValues(STORE_CODE, PURCHASE_GUID, PURCHASE_LINE_ITEM_GUID)
				.test()
				.assertNoErrors()
				.assertValue(shoppingData -> shoppingData.get(field1).equals(FIELD_VALUE_1) && shoppingData.get(field2).equals(StringUtils.EMPTY));
	}

	@Test
	public void testFindPurchaseItemModifierValuesWithNoOrderFoundFailure() {
		given(orderRepository.findByGuidAsSingle(STORE_CODE, PURCHASE_GUID))
				.willReturn(Single.error(ResourceOperationFailure.notFound(OrderRepositoryImpl.PURCHASE_NOT_FOUND)));

		cartItemModifiersRepository.findPurchaseItemModifierValues(STORE_CODE, PURCHASE_GUID, PURCHASE_LINE_ITEM_GUID)
				.test()
				.assertError(createErrorCheckPredicate(OrderRepositoryImpl.PURCHASE_NOT_FOUND, ResourceStatus.NOT_FOUND));
	}

	@Test
	public void testFindCartItemModifiersByProduct() {

		// when
		List<CartItemModifierField> list = cartItemModifiersRepository.findCartItemModifiersByProduct(product);

		// verify
		assertEquals(list, cartItemModifierFields);
	}

	@Test
	public void testFindMissingRequiredFieldCodesByShoppingItem() {

		// given
		final ShoppingItem mockShoppingItem = mock(ShoppingItem.class);
		final Map<String, String> itemMap = new HashMap<>();
		itemMap.put(FIELD_CODE1, FIELD_VALUE_1);
		itemMap.put(FIELD_CODE3, StringUtils.EMPTY);
		itemMap.put(FIELD_CODE4, null);
		when(mockShoppingItem.getSkuGuid()).thenReturn(SKU_GUID);
		when(mockShoppingItem.getFields()).thenReturn(itemMap);

		// when
		List<String> missedRequiredFieldCodes = cartItemModifiersRepository.findMissingRequiredFieldCodesByShoppingItem(mockShoppingItem);

		// verify
		assertEquals(REQUIRED_FIELDS_COUNT, missedRequiredFieldCodes.size());
		assertThat(missedRequiredFieldCodes, hasItems(FIELD_CODE3, FIELD_CODE4, FIELD_CODE5));

	}

	private CartItemModifierField buildField(final String fieldCode, final int ordering, final boolean required) {

		CartItemModifierField field = new CartItemModifierFieldImpl();

		field.initialize();
		field.setCode(fieldCode);
		field.setOrdering(ordering);
		field.setRequired(required);

		return field;

	}

	private List<CartItemModifierField> buildFields() {

		List<CartItemModifierField> fields = new ArrayList<>();

		fields.add(buildField(FIELD_CODE1, FIELD_ORDERING_1, true));
		fields.add(buildField(FIELD_CODE2, FIELD_ORDERING_2, false));
		fields.add(buildField(FIELD_CODE3, FIELD_ORDERING_3, true));
		fields.add(buildField(FIELD_CODE4, FIELD_ORDERING_4, true));
		fields.add(buildField(FIELD_CODE5, FIELD_ORDERING_5, true));

		return fields;
	}
}
