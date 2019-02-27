/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.sellingchannel.director.impl;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.internal.stubbing.answers.ReturnsArgumentAt;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.common.dto.sellingchannel.ShoppingItemDtoFactory;
import com.elasticpath.common.pricing.service.PriceLookupFacade;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.PriceImpl;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.misc.impl.RandomGuidImpl;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.impl.CartItem;
import com.elasticpath.domain.shoppingcart.impl.ShoppingItemImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.money.Money;
import com.elasticpath.sellingchannel.director.ShoppingItemAssembler;
import com.elasticpath.service.catalog.ProductAssociationService;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.service.shoppingcart.validation.AddOrUpdateShoppingItemDtoToCartValidationService;

/**
 * Tests the {@code CartDirectorImpl} in isolation.
 */
@SuppressWarnings("serial")
@RunWith(MockitoJUnitRunner.class)
public class CartDirectorImplTest {

	private static final String SHOPPING_ITEM_GUID = "shoppingItemGuid";
	private static final String SKU_CODE = "skuCode";
	private static final String SKU_123 = "123";
	private static final String SKU_A = "skuA";
	private static final String SKU_GUID = "testSkuGuid";
	private static final String SKU_GUID_2 = "testSkuGuid2";
	private static final long CART_ITEM_UID = 1;
	private static final Currency CURRENCY_CAD = Currency.getInstance("CAD");
	private static final int SHOPPING_ITEM_ORDERING = 5;
	private static final int QUANTITY = 3;
	private static final String DEPENDENT_SKU_CODE = "testADependentSkuCode";

	@Mock
	private ProductSkuLookup productSkuLookup;
	@Mock
	private ShoppingItemAssembler shoppingItemAssembler;

	@Spy
	private CartDirectorImpl cartDirector;

	@Mock
	private ProductSku productSku;
	@Mock
	private ProductSku productSku2;
	@Mock
	private ShoppingCart shoppingCart;
	@Mock
	private CartItem shoppingItemAlreadyInCart;
	@Mock
	private ShoppingItem addedShoppingItem;
	@Mock
	private Store store;
	@Mock
	private PriceLookupFacade priceLookupFacade;
	@Mock
	private ProductAssociationService productAssociationService;
	@Mock
	private ShoppingItemDtoFactory shoppingItemDtoFactory;
	@SuppressWarnings("PMD.UnusedPrivateField")
	@Mock private AddOrUpdateShoppingItemDtoToCartValidationService validationService;

	private final List<ShoppingItem> itemsInCart = new ArrayList<>();
	private Integer changeQuantityArgument;

	@Before
	public void setUp() {
		cartDirector.setProductSkuLookup(productSkuLookup);
		cartDirector.setShoppingItemAssembler(shoppingItemAssembler);
		cartDirector.setPriceLookupFacade(priceLookupFacade);
		cartDirector.setProductAssociationService(productAssociationService);
		cartDirector.setShoppingItemDtoFactory(shoppingItemDtoFactory);
		cartDirector.setValidationService(validationService);

		// Configure class under test to ignore irrelevant code branches
		doNothing().when(cartDirector).priceShoppingItemWithAdjustments(any(ShoppingCart.class), any(ShoppingItem.class));

		doAnswer((Answer<ShoppingItem>) invocationOnMock -> {
			changeQuantityArgument = (Integer) invocationOnMock.getArguments()[1];
			return (ShoppingItem) invocationOnMock.getArguments()[0];
		}).when(cartDirector).changeQuantityForCartItem(any(ShoppingItem.class), anyInt(), any(ShoppingCart.class));

		// Dependency and parameter mocking
		itemsInCart.add(shoppingItemAlreadyInCart);
		given(shoppingCart.getAllShoppingItems()).willReturn(itemsInCart);
		given(shoppingCart.getCartItemsBySkuGuid(SKU_GUID)).willReturn(itemsInCart);
		given(shoppingCart.getStore()).willReturn(store);

		given(shoppingItemAlreadyInCart.getQuantity()).willReturn(1);
		given(addedShoppingItem.getQuantity()).willReturn(1);

		given(shoppingItemAlreadyInCart.getSkuGuid()).willReturn(SKU_GUID);
		given(addedShoppingItem.getSkuGuid()).willReturn(SKU_GUID);

		given(productSkuLookup.findByGuid(SKU_GUID)).willReturn(productSku);
		given(productSku.getSkuCode()).willReturn(SKU_CODE);

		given(shoppingItemAlreadyInCart.getChildren()).willReturn(emptyList());

		given(shoppingCart.addShoppingCartItem(any(ShoppingItem.class))).willAnswer(new ReturnsArgumentAt(0));
		given(productAssociationService.findDependentItemsForSku(eq(store), any(ProductSku.class))).willReturn(emptyList());
	}

	/**
	 * Tests that the adding an item to a cart which already has an item with that sku and the product is not configurable
	 * will add to the quantity.
	 */
	@Test
	public void quantityUpdatedWhenSkuAlreadyInCartAndNotConfigurable() {
		cartDirector.addToCart(addedShoppingItem, shoppingCart, null);
		verify(cartDirector).changeQuantityForCartItem(shoppingItemAlreadyInCart, 2, shoppingCart);
	}

	/**
	 * Tests that the adding an item to a cart which already has an item with that sku and the product *is* configurable
	 * will add the new cart item.
	 */
	@Test
	public void testAddToCartSkuAlreadyInCartConfigurable() {
		final ProductSku productSku = createProductSku(SKU_123);

		when(addedShoppingItem.getSkuGuid()).thenReturn(productSku.getGuid());
		when(addedShoppingItem.getQuantity()).thenReturn(1);

		verify(cartDirector, never()).addDependentItemsForParentItem(shoppingCart, addedShoppingItem);
		cartDirector.addToCart(addedShoppingItem, shoppingCart, null);
		verify(shoppingCart).addShoppingCartItem(addedShoppingItem);
	}

	/**
	 * Tests that updateCartItem() finds the existing cart item and sets sku, price and quantity.
	 */
	@Test
	public void testUpdateCartItem() {
		final long itemUid = 5;
		final ShoppingItemDto dto = new ShoppingItemDto(SKU_A, 1);
		final CartItem updatedShoppingItem = mock(CartItem.class, "updated");

		when(shoppingItemAlreadyInCart.getGuid()).thenReturn(SHOPPING_ITEM_GUID);
		when(shoppingItemAssembler.createShoppingItem(dto)).thenReturn(updatedShoppingItem);

		doReturn(shoppingItemAlreadyInCart).when(shoppingCart).getCartItemById(itemUid);

		assertThat(updatedShoppingItem).isSameAs(cartDirector.updateCartItem(shoppingCart, itemUid, dto));

		verify(shoppingItemAssembler).createShoppingItem(dto);
		verify(updatedShoppingItem).setGuid(SHOPPING_ITEM_GUID);
	}

	/**
	 * Tests that the adding dependent items of given parent item to a cart.
	 */
	@Test
	public void testAddToCartWithDependentSku() {

		final ProductSku parentProductSkuAdded = mock(ProductSku.class, RETURNS_DEEP_STUBS);
		final ProductSku dependentProductSkuToAdd = mock(ProductSku.class, RETURNS_DEEP_STUBS);
		final ShoppingItemDto dependentShoppingItemDto = mock(ShoppingItemDto.class);
		final ShoppingItem dependentShoppingItem = mock(ShoppingItem.class);

		given(addedShoppingItem.getSkuGuid()).willReturn(SKU_GUID);
		given(productSkuLookup.findByGuid(SKU_GUID)).willReturn(parentProductSkuAdded);
		given(parentProductSkuAdded.getProduct().isNotSoldSeparately()).willReturn(false);
		given(productAssociationService.findDependentItemsForSku(store, parentProductSkuAdded)).willReturn(singletonList(dependentProductSkuToAdd));
		given(dependentProductSkuToAdd.getSkuCode()).willReturn(DEPENDENT_SKU_CODE);
		given(dependentProductSkuToAdd.getProduct().getMinOrderQty()).willReturn(1);
		given(shoppingItemDtoFactory.createDto(DEPENDENT_SKU_CODE, 1)).willReturn(dependentShoppingItemDto);
		given(shoppingItemAssembler.createShoppingItem(dependentShoppingItemDto)).willReturn(dependentShoppingItem);

		cartDirector.addDependentItemsForParentItem(shoppingCart, addedShoppingItem);

		verify(addedShoppingItem).addChildItem(dependentShoppingItem);
	}

	/**
	 * Test that the pricing functor does its job.
	 */
	@Test
	public void testPriceShoppingItemsWithTraverser() {
		final Price unitPrice = new PriceImpl();
		unitPrice.setListPrice(Money.valueOf(BigDecimal.ONE, CURRENCY_CAD));
		final PriceLookupFacade priceLookupFacade = mock(PriceLookupFacade.class);

		when(addedShoppingItem.getQuantity()).thenReturn(QUANTITY);
		when(priceLookupFacade.getShoppingItemPrice(addedShoppingItem, null, null)).thenReturn(unitPrice);

		cartDirector.setPriceLookupFacade(priceLookupFacade);
		cartDirector.setProductSkuLookup(productSkuLookup);

		cartDirector.priceShoppingItem(addedShoppingItem, null, null);
		verify(addedShoppingItem).setPrice(QUANTITY, unitPrice);
	}

	/**
	 * Test that the pricing functor does its job and prices child items.
	 */
	@Test
	public void testPriceChildShoppingItems() {
		final ShoppingItemImpl shoppingItemChild = new ShoppingItemImpl();
		final ShoppingItemImpl shoppingItemParent = new ShoppingItemImpl() {
			@Override
			public boolean isBundle(final ProductSkuLookup productSkuLookup) {
				return true;
			}
		};
		shoppingItemParent.addChild(shoppingItemChild);

		final ProductSkuImpl productSku = new ProductSkuImpl();
		productSku.setGuid(new RandomGuidImpl().toString());
		shoppingItemChild.setSkuGuid(productSku.getGuid());
		shoppingItemChild.setQuantity(QUANTITY);
		final ProductSkuImpl parentSku = new ProductSkuImpl();
		parentSku.setGuid(new RandomGuidImpl().toString());
		shoppingItemParent.setSkuGuid(parentSku.getGuid());
		shoppingItemParent.setQuantity(QUANTITY);

		final Price unitPrice = mock(Price.class);
		final Money twoBucks = Money.valueOf("2", CURRENCY_CAD);

		when(productSkuLookup.findByGuid(parentSku.getGuid())).thenReturn(parentSku);
		when(productSkuLookup.findByGuid(productSku.getGuid())).thenReturn(productSku);
		when(priceLookupFacade.getShoppingItemPrice(shoppingItemParent, null, null)).thenReturn(unitPrice);
		when(unitPrice.getCurrency()).thenReturn(CURRENCY_CAD);
		when(unitPrice.getListPrice(QUANTITY)).thenReturn(twoBucks);
		when(unitPrice.getSalePrice(QUANTITY)).thenReturn(twoBucks);
		when(unitPrice.getComputedPrice(QUANTITY)).thenReturn(twoBucks);

		cartDirector.priceShoppingItem(shoppingItemParent, null, null);

		assertThat(shoppingItemChild.getPrice()).isEqualTo(unitPrice);
		assertThat(shoppingItemParent.getPrice()).isEqualTo(unitPrice);
	}

	/**
	 * Test that the pricing functor does its job and prices child items, and director throws Exception if a null is found.
	 */
	@Test
	public void testPriceChildShoppingItemChecksNullPrice() {
		final ShoppingItemImpl shoppingItemChild = new ShoppingItemImpl();
		final ShoppingItemImpl shoppingItemParent = new ShoppingItemImpl() {
			@Override
			public boolean isBundle(final ProductSkuLookup productSkuLookup) {
				return true;
			}
		};
		shoppingItemParent.addChild(shoppingItemChild);

		final ProductSkuImpl productSku = new ProductSkuImpl();
		productSku.setGuid(new RandomGuidImpl().toString());
		shoppingItemChild.setSkuGuid(productSku.getGuid());
		shoppingItemChild.setQuantity(QUANTITY);
		final ProductSkuImpl parentSku = new ProductSkuImpl();
		parentSku.setGuid(new RandomGuidImpl().toString());
		shoppingItemParent.setSkuGuid(parentSku.getGuid());
		shoppingItemParent.setQuantity(QUANTITY);

		final Price unitPrice = mock(Price.class);
		final Money twoBucks = Money.valueOf("2", CURRENCY_CAD);

		when(productSkuLookup.findByGuid(parentSku.getGuid())).thenReturn(parentSku);
		when(productSkuLookup.findByGuid(productSku.getGuid())).thenReturn(productSku);
		when(priceLookupFacade.getShoppingItemPrice(shoppingItemParent, null, null)).thenReturn(unitPrice);
		given(unitPrice.getCurrency()).willReturn(CURRENCY_CAD);
		given(unitPrice.getSalePrice(QUANTITY)).willReturn(twoBucks);
		given(unitPrice.getComputedPrice(QUANTITY)).willReturn(twoBucks);

		cartDirector.priceShoppingItem(shoppingItemParent, null, null);
	}

	/**
	 * Tests {@link CartDirectorImpl#retainShoppingItemIdentity(ShoppingItem, ShoppingItem)} for a non-configurable shopping item.
	 */
	@Test
	public void testRetainShoppingItemIdentity() {
		final ShoppingItem existingItem = mock(ShoppingItem.class, "existing");
		final ShoppingItem newItem = mock(ShoppingItem.class, "new");

		when(existingItem.getGuid()).thenReturn(SHOPPING_ITEM_GUID);

		cartDirector.retainShoppingItemIdentity(existingItem, newItem);

		verify(newItem).setGuid(SHOPPING_ITEM_GUID);
	}

	/**
	 * Tests that if a shopping cart contains an item with no price tiers associated.
	 */
	@Test
	public void testRemoveShoppingItemThatHasNoPriceTiersAssociated() {
		String skuCode = "code";
		final ShoppingItem item = new ShoppingItemImpl() {
			@Override
			public Price getPrice() {
				return new PriceImpl();
			}
		};
		final ProductSku sku = createProductSku(skuCode);
		item.setSkuGuid(sku.getGuid());
	}

	@Test
	public void verifyClearItemsClearsItems() {
		cartDirector.clearItems(shoppingCart);
		verify(shoppingCart).clearItems();
	}

	@Test
	public void testAddToCartWithDifferentCartItemDataAddsLineItem() {
		givenItemHasFieldWithValue(addedShoppingItem, "Key1", "Key1MatchingValue");
		givenItemHasFieldWithValue(shoppingItemAlreadyInCart, "Key1", "Key1MatchingValue");
		givenItemHasFieldWithValue(addedShoppingItem, "Key2", "Key2MismatchedValue1");
		givenItemHasFieldWithValue(shoppingItemAlreadyInCart, "Key2", "Key2MismatchedValue2");

		cartDirector.addToCart(addedShoppingItem, shoppingCart, null);

		verify(cartDirector).addDependentItemsForParentItem(shoppingCart, addedShoppingItem);
		verify(shoppingCart).addShoppingCartItem(addedShoppingItem);
		verifyQuantityNotChanged();
	}

	@Test
	public void testAddToCartWithSameCartItemDataIncreasesQuantity() {
		givenItemHasFieldWithValue(addedShoppingItem, "Key3", "Key3MatchingValue");
		givenItemHasFieldWithValue(shoppingItemAlreadyInCart, "Key3", "Key3MatchingValue");
		givenItemHasFieldWithValue(addedShoppingItem, "Key4", "Key4MatchingValue");
		givenItemHasFieldWithValue(shoppingItemAlreadyInCart, "Key4", "Key4MatchingValue");

		cartDirector.addToCart(addedShoppingItem, shoppingCart, null);

		verify(cartDirector, never()).addDependentItemsForParentItem(shoppingCart, addedShoppingItem);
		verify(shoppingCart, never()).addShoppingCartItem(addedShoppingItem);
		verifyQuantityChanged(2);
	}

	@Test
	public void testAddToCartWithNoCartItemDataIncreasesQuantity() {
		cartDirector.addToCart(addedShoppingItem, shoppingCart, null);

		verify(cartDirector, never()).addDependentItemsForParentItem(shoppingCart, addedShoppingItem);
		verify(shoppingCart, never()).addShoppingCartItem(addedShoppingItem);
		verifyQuantityChanged(2);
	}

	@Test
	public void testAddToCartWithDifferentSkuCodeAndSameCartItemDataAddsLineItem() {
		givenItemHasFieldWithValue(addedShoppingItem, "Key5", "Key5MatchingValue");
		givenItemHasFieldWithValue(shoppingItemAlreadyInCart, "Key5", "Key5MatchingValue");
		givenItemHasFieldWithValue(addedShoppingItem, "Key6", "Key6MatchingValue");
		givenItemHasFieldWithValue(shoppingItemAlreadyInCart, "Key6", "Key6MatchingValue");
		given(addedShoppingItem.getSkuGuid()).willReturn(SKU_GUID_2);
		given(productSkuLookup.findByGuid(SKU_GUID_2)).willReturn(productSku2);

		cartDirector.addToCart(addedShoppingItem, shoppingCart, null);

		verify(cartDirector).addDependentItemsForParentItem(shoppingCart, addedShoppingItem);
		verify(shoppingCart).addShoppingCartItem(addedShoppingItem);
		verifyQuantityNotChanged();
	}

	@Test
	public void testUpdateCartItemRetainsCartItemData() {
		ShoppingItemDto shoppingItemDto = new ShoppingItemDto(SKU_CODE, 1);
		ShoppingItem newShoppingItem = mock(ShoppingItem.class);

		given(newShoppingItem.getSkuGuid()).willReturn(SKU_GUID);
		given(shoppingItemAssembler.createShoppingItem(shoppingItemDto)).willReturn(newShoppingItem);
		given(shoppingCart.getCartItemById(CART_ITEM_UID)).willReturn(shoppingItemAlreadyInCart);

		cartDirector.updateCartItem(shoppingCart, CART_ITEM_UID, shoppingItemDto);

		assertThat(shoppingItemAlreadyInCart.getFields())
				.as("Fields should have been copied over.")
				.isEqualTo(shoppingItemDto.getItemFields());
	}

	@Test
	public void testUpdateCartItemRetainsGuidAndOrdering() {
		ShoppingItemDto shoppingItemDto = new ShoppingItemDto(SKU_CODE, 1);
		givenItemHasFieldWithValue(addedShoppingItem, "newKey", "newValue");

		given(addedShoppingItem.getSkuGuid()).willReturn(SKU_GUID);
		given(shoppingItemAssembler.createShoppingItem(shoppingItemDto)).willReturn(addedShoppingItem);
		given(shoppingCart.getCartItemById(CART_ITEM_UID)).willReturn(shoppingItemAlreadyInCart);

		String shoppingItemGuid = "testShoppingItemGuid";
		given(shoppingItemAlreadyInCart.getGuid()).willReturn(shoppingItemGuid);
		given(shoppingItemAlreadyInCart.getOrdering()).willReturn(SHOPPING_ITEM_ORDERING);

		cartDirector.updateCartItem(shoppingCart, CART_ITEM_UID, shoppingItemDto);

		verify(addedShoppingItem).setGuid(shoppingItemGuid);
		verify(addedShoppingItem).setOrdering(SHOPPING_ITEM_ORDERING);
	}

	private void givenItemHasFieldWithValue(final ShoppingItem mockShoppingItem, final String key, final String value) {
		Map<String, String> fieldsMap = mockShoppingItem.getFields();

		if (fieldsMap == null || fieldsMap.isEmpty()) {
			fieldsMap = new HashMap<>();
			given(mockShoppingItem.getFields()).willReturn(fieldsMap);
		}

		fieldsMap.put(key, value);
	}

	private void verifyQuantityChanged(final Integer targetQuantity) {
		assertThat(targetQuantity)
				.as("Quantity should have been changed to %s", targetQuantity)
				.isEqualTo(changeQuantityArgument);
	}

	private void verifyQuantityNotChanged() {
		assertThat(changeQuantityArgument)
				.as("Cart item quantity change should not have been called.")
				.isNull();
	}


	private ProductSku createProductSku(final String skuCode) {
		final Product product = new ProductImpl();
		product.initialize();

		return createProductSku(skuCode, product);
	}

	private ProductSku createProductSku(final String skuCode, final Product product) {
		final ProductSku sku = new ProductSkuImpl();
		sku.initialize();
		sku.setSkuCode(skuCode);
		sku.setProduct(product);

		given(productSkuLookup.findByGuid(sku.getGuid())).willReturn(sku);

		return sku;
	}
}
