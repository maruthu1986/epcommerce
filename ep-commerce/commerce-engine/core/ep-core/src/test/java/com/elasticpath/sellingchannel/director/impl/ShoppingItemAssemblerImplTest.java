/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.sellingchannel.director.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.common.dto.sellingchannel.impl.ShoppingItemDtoFactoryImpl;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.catalog.impl.BundleConstituentImpl;
import com.elasticpath.domain.catalog.impl.CatalogImpl;
import com.elasticpath.domain.catalog.impl.PriceImpl;
import com.elasticpath.domain.catalog.impl.ProductBundleImpl;
import com.elasticpath.domain.catalog.impl.ProductConstituentImpl;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.catalog.impl.ProductTypeImpl;
import com.elasticpath.domain.catalog.impl.SelectionRuleImpl;
import com.elasticpath.domain.misc.impl.RandomGuidImpl;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.impl.ShoppingItemImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.money.Money;
import com.elasticpath.sellingchannel.ShoppingItemFactory;
import com.elasticpath.service.catalog.ProductLookup;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.service.catalog.impl.BundleIdentifierImpl;

/**
 * Verifies the behaviour of the ShoppingItemAssembler.
 */
@SuppressWarnings({"PMD.TooManyMethods", "PMD.GodClass"})
@RunWith(MockitoJUnitRunner.class)
public class ShoppingItemAssemblerImplTest {

	private static final String SELECTED_SKU = "selectedSku";

	private static final int SHOPPING_ITEM_UIDPK = 12334;

	private static final String BBB = "BBB";

	private static final String AAA = "AAA";

	private static final String BUNDLE_CODE_1 = "BUNDLE_CODE_1";

	private static final String BUNDLE_SKU_CODE_1 = "BUNDLE_SKU_1";

	private static final String BUNDLE_CODE_2 = "BUNDLE_CODE_2";

	private static final String BUNDLE_SKU_CODE_2 = "BUNDLE_SKU_2";

	private static final String PRODUCT_CODE_1 = "PRODUCT_CODE_1";

	private static final String PRODUCT_SKU_CODE_1 = "PRODUCT_SKU_CODE_1";

	private static final String PRODUCT_CODE_2 = "PRODUCT_CODE_2";

	private static final String PRODUCT_SKU_CODE_2 = "PRODUCT_SKU_CODE_2";

	private static final String PRODUCT_CODE_3 = "PRODUCT_CODE_3";

	private static final String PRODUCT_SKU_CODE_3 = "PRODUCT_SKU_CODE_3";

	private static final String SKU_B = "SkuB";

	private static final String CURRENCY_CAD = "CAD";

	@Mock
	private BeanFactory beanFactory;

	@Mock
	private ProductLookup productLookup;

	@Mock
	private ProductSkuLookup productSkuLookup;

	@Mock
	private ShoppingItemFactory shoppingItemFactory;

	@InjectMocks
	private ShoppingItemAssemblerImpl assembler;

	/**
	 * Set up required before each test.
	 */
	@Before
	public void setUp() {
		given(beanFactory.getBean("productConstituent")).will(invocationOnMock -> new ProductConstituentImpl());

		// Yes, this is horrible, but I can only refactor this test so far before I kill myself.
		ShoppingItemDtoFactoryImpl shoppingItemDtoFactory = new ShoppingItemDtoFactoryImpl();
		shoppingItemDtoFactory.setProductSkuLookup(productSkuLookup);
		shoppingItemDtoFactory.setBundleIdentifier(new BundleIdentifierImpl());
		assembler.setShoppingItemDtoFactory(shoppingItemDtoFactory);
	}

	/**
	 * Tests that called createShoppingItem will find the sku, find the price and create the shopping item.
	 */
	@Test
	public void testCreateShoppingItem() {
		final ProductSku sku = new ProductSkuImpl();
		final Product product = new ProductImpl();
		sku.setProduct(product);

		final Catalog catalog = new CatalogImpl();
		final Store store = new StoreImpl();
		store.setCatalog(catalog);

		final ShoppingItem shoppingItem = new ShoppingItemImpl();
		shoppingItem.setGuid("GUID");

		given(productSkuLookup.findBySkuCode(anyString())).willReturn(sku);
		given(shoppingItemFactory.createShoppingItem(sku, null, 2, 0, Collections.emptyMap())).willReturn(shoppingItem);

		final ShoppingItemDto shoppingItemDto = new ShoppingItemDto(SKU_B, 2);
		shoppingItemDto.setGuid("GUID");
		shoppingItemDto.setSelected(true);

		final ShoppingItem actualShoppingItem = assembler.createShoppingItem(shoppingItemDto);

		verify(shoppingItemFactory).createShoppingItem(sku, null, 2, 0, Collections.emptyMap());

		assertEquals("The cartItem from the delegate should equal the cart item from the factory", shoppingItem, actualShoppingItem);

	}

	private ShoppingItemDto createDtoWithThreeConstituentsAllSelected() {
		final ShoppingItemDto rootDto = new ShoppingItemDto(BUNDLE_SKU_CODE_1, 1);
		rootDto.addConstituent(createConstituentDto(PRODUCT_SKU_CODE_1, true));
		rootDto.addConstituent(createConstituentDto(PRODUCT_SKU_CODE_2, true));
		rootDto.addConstituent(createConstituentDto(PRODUCT_SKU_CODE_3, true));
		return rootDto;
	}

	private ShoppingItemDto createConstituentDto(final String skuCode, final boolean selected) {
		final ShoppingItemDto dto = new ShoppingItemDto(skuCode, 1);
		dto.setSelected(selected);
		return dto;
	}

	private BundleConstituent createBundleConstituentFrom(final Product product, final int quantity, final int ordering) {
		given(shoppingItemFactory.createShoppingItem(product.getDefaultSku(), null, quantity, ordering, Collections.emptyMap()))
				.willReturn(createFakeShoppingItem(product.getDefaultSku().getGuid()));
		given(productLookup.findByGuid(product.getCode())).willReturn(product);
		final BundleConstituentImpl constituentBundle = new BundleConstituentImpl() {
			private static final long serialVersionUID = 1;

			@Override
			protected <T> T getBean(final String beanName) {
				return beanFactory.getBean(beanName);
			}
		};
		constituentBundle.setConstituent(product);
		constituentBundle.setQuantity(quantity);
		return constituentBundle;
	}

	private ProductBundle createBundleWithThreeConstituents() {
		final ProductBundle rootBundle = createProductBundleWithSkuCode(BUNDLE_CODE_1, BUNDLE_SKU_CODE_1);
		rootBundle.addConstituent(createBundleConstituentFrom(createProductWithSkuCode(PRODUCT_CODE_1, PRODUCT_SKU_CODE_1), 1, 0));
		rootBundle.addConstituent(createBundleConstituentFrom(createProductWithSkuCode(PRODUCT_CODE_2, PRODUCT_SKU_CODE_2), 1, 1));
		rootBundle.addConstituent(createBundleConstituentFrom(createProductWithSkuCode(PRODUCT_CODE_3, PRODUCT_SKU_CODE_3), 1, 2));
		return rootBundle;
	}

	/**
	 * Tests that a {@code ShoppingItemDto} with 3 direct children will have a ShoppingItem created for each child, with a single ShoppingItem
	 * parent.
	 */
	@Test
	public void testTraverseAllChildren() {
		final ShoppingItemDto rootShoppingItemDto = new ShoppingItemDto("sku", 1);
		final ShoppingItemDto childDto1 = new ShoppingItemDto(PRODUCT_SKU_CODE_1, 2);
		final ShoppingItemDto childDto2 = new ShoppingItemDto(PRODUCT_SKU_CODE_2, 3);
		final ShoppingItemDto childDto3 = new ShoppingItemDto(PRODUCT_SKU_CODE_3, 4);

		rootShoppingItemDto.addConstituent(childDto1);
		rootShoppingItemDto.addConstituent(childDto2);
		rootShoppingItemDto.addConstituent(childDto3);

		final ProductBundle bundle = mock(ProductBundle.class);

		final List<BundleConstituent> bundleConstituents = new ArrayList<>();

		final Product productA = createProductWithSkuCode(PRODUCT_CODE_1, PRODUCT_SKU_CODE_1);
		final BundleConstituent const1 = createBundleConstituentFrom(productA, 1, 0);
		bundleConstituents.add(const1);

		final Product productB = createProductWithSkuCode(PRODUCT_CODE_2, PRODUCT_SKU_CODE_2);
		final BundleConstituent const2 = createBundleConstituentFrom(productB, 1, 1);
		bundleConstituents.add(const2);

		final Product productC = createProductWithSkuCode(PRODUCT_CODE_3, PRODUCT_SKU_CODE_3);
		final BundleConstituent const3 = createBundleConstituentFrom(productC, 1, 2);
		bundleConstituents.add(const3);

		given(bundle.getConstituents()).willReturn(bundleConstituents);

		final ShoppingItem parent = new ShoppingItemImpl();
		assembler.createShoppingItemTree(bundle, rootShoppingItemDto, parent, 1);

		verify(shoppingItemFactory).createShoppingItem(productA.getDefaultSku(), null, 1, 0, Collections.emptyMap());
		verify(shoppingItemFactory).createShoppingItem(productB.getDefaultSku(), null, 1, 1, Collections.emptyMap());
		verify(shoppingItemFactory).createShoppingItem(productC.getDefaultSku(), null, 1, 2, Collections.emptyMap());
	}

	/**
	 * Tests that a {@code ShoppingItem} with a child and a grandchild will have a ShoppingItem for both the child and the grandchild when the
	 * children are created.
	 */
	@Test
	public void testTraverseGrandChildren() {
		//
		// Create ShoppingItemDto Tree
		//
		final ShoppingItemDto rootShoppingItemDto = new ShoppingItemDto(BUNDLE_SKU_CODE_1, 1);

		final ShoppingItemDto childShoppingItemDto = new ShoppingItemDto(BUNDLE_SKU_CODE_2, 2);
		childShoppingItemDto.setSelected(true);
		rootShoppingItemDto.addConstituent(childShoppingItemDto);

		final ShoppingItemDto grandChildShoppingItemDto = new ShoppingItemDto(PRODUCT_SKU_CODE_1, 3);
		grandChildShoppingItemDto.setSelected(true);
		childShoppingItemDto.addConstituent(grandChildShoppingItemDto);
		//
		// Create ProductBundle Tree
		//
		final ProductBundle rootBundle = createProductBundleWithSkuCode(BUNDLE_CODE_1, BUNDLE_SKU_CODE_1);
		rootBundle.setSelectionRule(new SelectionRuleImpl(0));
		final ProductBundle childBundle = createProductBundleWithSkuCode(BUNDLE_CODE_2, BUNDLE_SKU_CODE_2);
		childBundle.setSelectionRule(new SelectionRuleImpl(0));
		final Product grandChildProduct = createProductWithSkuCode(PRODUCT_CODE_1, PRODUCT_SKU_CODE_1);
		rootBundle.addConstituent(createBundleConstituentFrom(childBundle, 1, 0));
		childBundle.addConstituent(createBundleConstituentFrom(grandChildProduct, 1, 0));

		//
		// Create Assembler implementation for testing
		//
		// The root ShoppingItem should be passed in, so it won't have to be created
		final ShoppingItem parent = new ShoppingItemImpl();
		assembler.createShoppingItemTree(rootBundle, rootShoppingItemDto, parent, 1);

		verify(shoppingItemFactory).createShoppingItem(childBundle.getDefaultSku(), null, 1, 0, Collections.emptyMap());
		verify(shoppingItemFactory).createShoppingItem(grandChildProduct.getDefaultSku(), null, 1, 0, Collections.emptyMap());
	}

	/**
	 * Tests that if a product has multiple skus, the product doesn't have its skus loaded, and a sku code is specified, then the product will be
	 * loaded with its skus from the product service.
	 */
	@Test
	public void testProcessNodeMultipleSku() {
		final ProductSku productSku = new ProductSkuImpl();
		productSku.setGuid(AAA);

		final ProductType productType = new ProductTypeImpl();
		productType.setMultiSku(true);

		final String productCode = "PRODUCT";
		final Product multiSkuProductWithoutSkus = new ProductImpl();
		multiSkuProductWithoutSkus.setCode(productCode);
		multiSkuProductWithoutSkus.setProductType(productType);

		final Product multiSkuProductWithSkus = new ProductImpl();
		multiSkuProductWithSkus.setCode(productCode);
		productSku.setProduct(multiSkuProductWithSkus);
		multiSkuProductWithSkus.setProductType(productType);

		given(productLookup.findByGuid(multiSkuProductWithoutSkus.getCode())).willReturn(multiSkuProductWithSkus);

		assembler.getSkuFromProduct(multiSkuProductWithoutSkus, AAA);
	}

	/**
	 * Test that when a ShoppingItemDto is created from a ShoppingItem, any applicable Bundle items that are not in the ShoppingItem are added to the
	 * ShoppingItemDto so that the DTO's structure mirrors the Bundle's structure.
	 */
	@Test
	public void testBundleWithSkuConstituent() {
		// Create a bundle with two items and a select 1 rule
		final String selectedSkuGuid = SELECTED_SKU;
		final String nonSelectedSkuGuid = "nonSelectedSku";
		final String bundleSkuGuid = "bundleSku";
		final Product selectedProduct = new ProductImpl();
		final ProductSku selectedSku = new ProductSkuImpl();
		selectedSku.setGuid(selectedSkuGuid);
		selectedSku.setSkuCode(selectedSkuGuid);
		selectedProduct.addOrUpdateSku(selectedSku);

		final Product nonSelectedProduct = new ProductImpl();
		final ProductSku nonSelectedSku = new ProductSkuImpl();
		nonSelectedSku.setGuid(nonSelectedSkuGuid);
		nonSelectedSku.setSkuCode(nonSelectedSkuGuid);
		nonSelectedProduct.addOrUpdateSku(nonSelectedSku);

		final BundleConstituent selectedBundleItem = createBundleConstituentFrom(selectedProduct, 1, 0);
		final BundleConstituent nonSelectedBundleItem = createBundleConstituentFrom(nonSelectedProduct, 1, 1);

		final ProductBundle bundle = new ProductBundleImpl();
		bundle.setSelectionRule(new SelectionRuleImpl(1));
		final ProductSku bundleSku = new ProductSkuImpl();
		bundleSku.setSkuCode(bundleSkuGuid);
		bundleSku.setGuid(bundleSkuGuid);
		bundle.addOrUpdateSku(bundleSku);
		bundle.addConstituent(selectedBundleItem);
		bundle.addConstituent(nonSelectedBundleItem);

		givenProductSkuLookupWillFindSku(selectedSku, nonSelectedSku, bundleSku);

		// Create a corresponding ShoppingItem
		final ShoppingItem shoppingItem = new TestShoppingItemImpl();
		shoppingItem.setSkuGuid(bundleSku.getGuid());
		final ShoppingItem nestedShoppingItem = new TestShoppingItemImpl();
		nestedShoppingItem.setSkuGuid(selectedSku.getGuid());
		nestedShoppingItem.setBundleConstituent(true);
		shoppingItem.addChild(nestedShoppingItem);

		// Assemble the ShoppingItemDto and check it.
		final ShoppingItemDto dto = assembler.assembleShoppingItemDtoFrom(shoppingItem);
		assertEquals("DTO should have the same number of constituents as the Bundle", dto.getConstituents().size(), bundle.getConstituents().size());
		assertEquals("DTO's first sku should be the same as the bundle's first sku.", selectedSkuGuid, dto.getConstituents().get(0).getSkuCode());
		assertEquals("DTO's second sku should be the same as the bundle's second sku.", nonSelectedSkuGuid, dto.getConstituents().get(1)
				.getSkuCode());
		assertTrue("DTO's first sku should be selected", dto.getConstituents().get(0).isSelected());
		assertFalse("DTO's second sku should not be selected", dto.getConstituents().get(1).isSelected());
	}

	/**
	 * Test that when a ShoppingItemDto is configured from a ShoppingItem, the prices and the UIDPK are copied over (recursively through the tree).
	 * Also check that any items in the ShoppingItem are marked as "selected" on the ShoppingItemDto. A simple parent->child->grandchild is tested to
	 * check recursion.
	 */
	@Test
	public void testConfigureShoppingItemDto() {
		// Create grandChild product/sku
		final ProductSku grandChildProductSku = new ProductSkuImpl();
		final Product grandChildProduct = new ProductImpl();
		grandChildProduct.setCode("ProdC");
		grandChildProductSku.setSkuCode(BBB);
		grandChildProductSku.setGuid(BBB);
		grandChildProduct.addOrUpdateSku(grandChildProductSku);
		// Create child product(Bundle)/sku
		final ProductSku childProductSku = new ProductSkuImpl();
		final ProductBundle childProduct = new ProductBundleImpl();
		childProduct.setCode("ProdB");
		childProductSku.setSkuCode(AAA);
		childProductSku.setGuid(AAA);
		childProduct.addOrUpdateSku(childProductSku);
		final BundleConstituent childConstituent = createBundleConstituentFrom(grandChildProduct, 1, 0);
		childProduct.addConstituent(childConstituent);
		// Create root bundle/sku
		final ProductSku rootSku = new ProductSkuImpl();
		rootSku.setSkuCode(SKU_B);
		rootSku.setGuid(SKU_B);
		final ProductBundle productBundle = new ProductBundleImpl();
		productBundle.setCode("ProdA");
		productBundle.addOrUpdateSku(rootSku);
		productBundle.addConstituent(createBundleConstituentFrom(childProduct, 1, 0));

		// Create shopping item tree
		final ShoppingItem rootItem = new TestShoppingItemImpl();
		rootItem.setSkuGuid(rootSku.getGuid());
		rootItem.setUidPk(SHOPPING_ITEM_UIDPK);
		rootItem.setGuid("Root");

		final ShoppingItem childItem = new TestShoppingItemImpl();
		childItem.setSkuGuid(childProductSku.getGuid());
		childItem.setUidPk(SHOPPING_ITEM_UIDPK + 1);
		childItem.setGuid("Child");
		childItem.setBundleConstituent(true);

		final ShoppingItem grandChildItem = new TestShoppingItemImpl();
		grandChildItem.setSkuGuid(grandChildProductSku.getGuid());
		grandChildItem.setUidPk(SHOPPING_ITEM_UIDPK + 2);
		grandChildItem.setGuid("Grandchild");
		grandChildItem.setBundleConstituent(true);

		childItem.addChildItem(grandChildItem);
		rootItem.addChildItem(childItem);

		final Price price = mock(Price.class);
		final Money money = Money.valueOf(BigDecimal.ONE, Currency.getInstance(CURRENCY_CAD));

		givenProductSkuLookupWillFindSku(rootSku, childProductSku, grandChildProductSku);

		given(price.getCurrency()).willReturn(Currency.getInstance(CURRENCY_CAD));
		given(price.getListPrice(1)).willReturn(money);
		given(price.getSalePrice(1)).willReturn(money);
		given(price.getComputedPrice(1)).willReturn(money);
		given(price.getPricingScheme()).willReturn(null);

		rootItem.setPrice(1, price);

		final ShoppingItemDto shoppingItemDto = assembler.assembleShoppingItemDtoFrom(rootItem);
		assertEquals(SKU_B, shoppingItemDto.getSkuCode());
		assertEquals("ProdA", shoppingItemDto.getProductCode());
		assertEquals(1, shoppingItemDto.getQuantity());
		assertEquals(SHOPPING_ITEM_UIDPK, shoppingItemDto.getShoppingItemUidPk());
		assertEquals("Expected GUID in ShoppingItemDto to match ShoppingItem", "Root", shoppingItemDto.getGuid());

		assertEquals("Expected one child constituent in root ShoppingItemDto", 1, shoppingItemDto.getConstituents().size());
		final ShoppingItemDto actualChild = shoppingItemDto.getConstituents().get(0);
		assertEquals(AAA, actualChild.getSkuCode());
		assertEquals("Expected GUID in ShoppingItemDto to match ShoppingItem", "Child", actualChild.getGuid());

		assertEquals("Expected one grandchild constituent in child ShoppingItemDto", 1, actualChild.getConstituents().size());
		final ShoppingItemDto actualGrandchild = actualChild.getConstituents().get(0);
		assertEquals(BBB, actualGrandchild.getSkuCode());
		assertEquals("Expected GUID in ShoppingItemDto to match ShoppingItem", "Grandchild", actualGrandchild.getGuid());
	}

	/**
	 * Tests that no constituents are added to the cart with the bundle when the selection rule is 1 but there is > 1 bundle constituents defined.
	 */
	@Test
	public void testCreateShoppingItemTreeWhenNestedBundleHasNoSelection() {
		final ProductBundle productBundle = createBundleWithThreeConstituents();
		productBundle.setSelectionRule(new SelectionRuleImpl(1));
		final ProductBundle nestedProductBundle = createBundleWithThreeConstituents();
		nestedProductBundle.setSelectionRule(new SelectionRuleImpl(1));

		productBundle.addConstituent(createBundleConstituentFrom(nestedProductBundle, 1, 0));

		final ShoppingItemDto shoppingItemDto = createDtoWithThreeConstituentsAllSelected();
		shoppingItemDto.getConstituents().get(2).setSelected(false);
		shoppingItemDto.getConstituents().get(1).setSelected(false);

		final ShoppingItemDto nestedShoppingItemDto = createDtoWithThreeConstituentsAllSelected();
		nestedShoppingItemDto.setSelected(false);
		nestedShoppingItemDto.getConstituents().get(0).setSelected(false);
		nestedShoppingItemDto.getConstituents().get(1).setSelected(false);
		nestedShoppingItemDto.getConstituents().get(2).setSelected(false);
		shoppingItemDto.addConstituent(nestedShoppingItemDto);

		final ShoppingItemImpl parent = new ShoppingItemImpl() {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isBundle(final ProductSkuLookup productSkuLookup) {
				return true;
			}
		};
		assembler.createShoppingItemTree(productBundle, shoppingItemDto, parent, 1);
		assertEquals(1, parent.getBundleItems(productSkuLookup).size());
	}

	/**
	 * Test that the {@code ShoppingItem} created from the {@code ShoppingItemDto} only contains the skus that are flagged as "selected" on the DTO.
	 */
	@Test
	public void testShoppingItemContainsOnlySelectedSkusFromDto() {
		// Create a bundle with two items and a select 1 rule
		final Product selectedProduct = createProductWithSkuCode(PRODUCT_CODE_1, PRODUCT_SKU_CODE_1);
		final ProductSku selectedSku = selectedProduct.getDefaultSku();
		final Product nonSelectedProduct = createProductWithSkuCode(PRODUCT_CODE_2, PRODUCT_SKU_CODE_2);

		final BundleConstituent selectedBundleItem = createBundleConstituentFrom(selectedProduct, 1, 0);
		final BundleConstituent nonSelectedBundleItem = createBundleConstituentFrom(nonSelectedProduct, 1, 1);

		final ProductBundle bundle = createProductBundleWithSkuCode(BUNDLE_CODE_1, BUNDLE_SKU_CODE_1);
		final ProductSku bundleSku = bundle.getDefaultSku();
		bundle.addConstituent(selectedBundleItem);
		bundle.addConstituent(nonSelectedBundleItem);

		// Create a DTO matching the bundle, with 1 item selected
		final ShoppingItemDto rootDto = new ShoppingItemDto(BUNDLE_SKU_CODE_1, 1);
		final ShoppingItemDto selectedDto = new ShoppingItemDto(PRODUCT_SKU_CODE_1, 1);
		selectedDto.setSelected(true);
		final ShoppingItemDto nonSelectedDto = new ShoppingItemDto(PRODUCT_SKU_CODE_2, 1);
		nonSelectedDto.setSelected(false);
		rootDto.addConstituent(selectedDto);
		rootDto.addConstituent(nonSelectedDto);

		given(shoppingItemFactory.createShoppingItem(bundle.getDefaultSku(), null, 1, 0, Collections.emptyMap()))
				.willReturn(createFakeShoppingItem(bundle.getDefaultSku().getGuid()));

		// assemble the ShoppingItem
		assembler.createShoppingItem(rootDto);

		// ShoppingItemFactory should only be called TWICE!
		verify(shoppingItemFactory).createShoppingItem(bundleSku, null, 1, 0, Collections.emptyMap());
		verify(shoppingItemFactory).createShoppingItem(selectedSku, null, 1, 0, Collections.emptyMap());
	}

	/**
	 * Test that when a simple multi-sku product (non-bundle) is in a shoppingItem by itself, the ShoppingItemDto that was created with the product's
	 * default sku is updated to the sku specified in the shopping item.
	 */
	@Test
	public void testDtoUpdatedToShoppingItemSkuForMultiSkuProducts() {
		final String defaultSkuCode = "defaultSku";

		// Create a multisku product
		final ProductSku defaultSku = new ProductSkuImpl();
		defaultSku.initialize();
		defaultSku.setSkuCode(defaultSkuCode);
		final ProductSku selectedSku = new ProductSkuImpl();
		selectedSku.initialize();
		selectedSku.setSkuCode(SELECTED_SKU);
		final Product product = new ProductImpl();
		product.addOrUpdateSku(defaultSku);
		product.addOrUpdateSku(selectedSku);
		product.setDefaultSku(defaultSku);

		// Create a shoppingItem for the multisku product's default sku
		final ShoppingItem shoppingItem = new ShoppingItemImpl();
		shoppingItem.setSkuGuid(selectedSku.getGuid());
		final ShoppingItemDto dto = new ShoppingItemDto(null, 1);
		dto.setSkuCode(defaultSkuCode);

		given(productSkuLookup.findByGuid(selectedSku.getGuid())).willReturn(selectedSku);

		// configure the dto, and check that it's been updated.
		String skuCode = assembler.configureShoppingItemDtoFromShoppingItem(dto, shoppingItem).getSkuCode();
		assertEquals("The DTO should have been updated to reflect the actual sku specified in the ShoppingItem, rather than the"
				+ "multi-sku product's default sku", selectedSku.getSkuCode(), skuCode);
	}

	/**
	 * Tests if it can find the appropriate DTO from the DTO list. And make sure that only appropriate DTO is selected.
	 */
	@Test
	public void testAssemblingShoppingItemFormBeanWhenOneOfTheTwoSameMultiSkuConstituentsIsSelected() {
		// given
		final ProductBundle bundle = createProductBundleWithSkuCode(BUNDLE_CODE_1, BUNDLE_SKU_CODE_1);
		final Product product = createProductWithSkuCode(PRODUCT_CODE_1, PRODUCT_SKU_CODE_1);
		final ProductSku secondSku = new ProductSkuImpl();
		secondSku.setSkuCode(PRODUCT_SKU_CODE_2);
		secondSku.setGuid(PRODUCT_SKU_CODE_2);
		product.addOrUpdateSku(secondSku);

		givenProductSkuLookupWillFindSku(secondSku);

		bundle.addConstituent(createBundleConstituentFrom(product, 1, 0));
		bundle.addConstituent(createBundleConstituentFrom(product, 1, 1));

		final ShoppingItem root = createFakeShoppingItem(bundle.getDefaultSku().getGuid());

		final ShoppingItem child1 = createFakeShoppingItem(product.getSkuByCode(PRODUCT_SKU_CODE_2).getGuid());
		child1.setBundleConstituent(true);
		root.addChildItem(child1);

		given(productSkuLookup.findByGuid(root.getSkuGuid())).willReturn(bundle.getDefaultSku());
		given(productSkuLookup.findByGuid(child1.getSkuGuid())).willReturn(product.getDefaultSku());

		// test
		final ShoppingItemDto rootDto = assembler.assembleShoppingItemDtoFrom(root);
		assertEquals(2, rootDto.getConstituents().size());
		assertTrue(rootDto.getConstituents().get(0).isSelected());
		assertFalse(rootDto.getConstituents().get(1).isSelected());
	}

	/**
	 * Tests assembling ShoppingItemFormBeans from ShoppingItem when shopping item has same constituents.
	 */
	@Test
	public void testAssemblingShoppingItemFormBeanFromShoppingItemWithTwoSameConstituents() {
		// given
		final ProductBundle bundle = createProductBundleWithSkuCode(BUNDLE_CODE_1, BUNDLE_SKU_CODE_1);
		final Product product = createProductWithSkuCode(PRODUCT_CODE_1, PRODUCT_SKU_CODE_1);
		bundle.addConstituent(createBundleConstituentFrom(product, 1, 0));
		bundle.addConstituent(createBundleConstituentFrom(product, 1, 1));

		final ShoppingItem root = createFakeShoppingItem(bundle.getDefaultSku().getGuid());

		final ShoppingItem child1 = createFakeShoppingItem(product.getDefaultSku().getGuid());
		child1.setOrdering(0);
		final ShoppingItem child2 = createFakeShoppingItem(product.getDefaultSku().getGuid());
		child2.setOrdering(1);

		root.addChildItem(child1);
		root.addChildItem(child2);

		// test
		final ShoppingItemDto rootDto = assembler.assembleShoppingItemDtoFrom(root);
		assertEquals(2, rootDto.getConstituents().size());
		assertTrue(rootDto.getConstituents().get(0).isSelected());
		assertTrue(rootDto.getConstituents().get(1).isSelected());
	}

	private ShoppingItem createFakeShoppingItem(final String skuGuid) {
		ShoppingItem shoppingItem = new ShoppingItemImpl();
		shoppingItem.setSkuGuid(skuGuid);
		return shoppingItem;
	}

	/**
	 * Tests creating shopping item from a bundle which has two of same constituents.
	 */
	@Test
	public void testCreatingShoppingItemFromBundleWithTwoSameConstituents() {
		// given
		final ProductBundle bundle = createProductBundleWithSkuCode(BUNDLE_CODE_1, BUNDLE_SKU_CODE_1);
		final Product product1 = createProductWithSkuCode(PRODUCT_CODE_1, PRODUCT_SKU_CODE_1);
		final Product product2 = createProductWithSkuCode(PRODUCT_CODE_1, PRODUCT_SKU_CODE_1);
		bundle.addConstituent(createBundleConstituentFrom(product1, 1, 0));
		bundle.addConstituent(createBundleConstituentFrom(product2, 1, 1));

		final ShoppingItemDto bundleDto = new ShoppingItemDto(BUNDLE_SKU_CODE_1, 1);
		final ShoppingItemDto childDto1 = new ShoppingItemDto(PRODUCT_SKU_CODE_1, 1);
		final ShoppingItemDto childDto2 = new ShoppingItemDto(PRODUCT_SKU_CODE_1, 1);

		bundleDto.addConstituent(childDto1);
		bundleDto.addConstituent(childDto2);

		// test
		final ShoppingItemImpl shoppingItem = new ShoppingItemImpl() {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isBundle(final ProductSkuLookup productSkuLookup) {
				return true;
			}
		};

		assembler.createShoppingItemTree(bundle, bundleDto, shoppingItem, 1);
		assertEquals(2, shoppingItem.getBundleItems(productSkuLookup).size());
		assertEquals(product1.getDefaultSku().getGuid(), shoppingItem.getBundleItems(productSkuLookup).get(0).getSkuGuid());
		assertEquals(product2.getDefaultSku().getGuid(), shoppingItem.getBundleItems(productSkuLookup).get(1).getSkuGuid());
	}

	private Product createProductWithSkuCode(final String productCode, final String skuCode) {
		final Product product = new ProductImpl();
		product.setCode(productCode);

		final ProductSku sku = new ProductSkuImpl();
		sku.initialize();
		sku.setSkuCode(skuCode);
		product.addOrUpdateSku(sku);

		givenProductSkuLookupWillFindSku(sku);
		return product;
	}

	private ProductBundle createProductBundleWithSkuCode(final String bundleCode, final String skuCode) {
		final ProductBundle bundle = new ProductBundleImpl();
		bundle.setCode(bundleCode);

		final ProductSku sku = new ProductSkuImpl();
		sku.setGuid(new RandomGuidImpl().toString());
		sku.setSkuCode(skuCode);
		bundle.addOrUpdateSku(sku);

		givenProductSkuLookupWillFindSku(sku);
		return bundle;
	}

	protected void givenProductSkuLookupWillFindSku(final ProductSku... skus) {
		for (ProductSku sku : skus) {
			given(productSkuLookup.findByGuid(sku.getGuid())).willReturn(sku);
			given(productSkuLookup.findBySkuCode(sku.getSkuCode())).willReturn(sku);
		}
	}

	/**
	 * Test shopping item object overrides getPrice and makeMoney.
	 */
	class TestShoppingItemImpl extends ShoppingItemImpl {
		private static final long serialVersionUID = 1L;

		@Override
		protected Money makeMoney(final BigDecimal amount) {
			if (amount == null) {
				return null;
			}
			return Money.valueOf(amount, getCurrency());
		}


		@Override
		public Price getPrice() {
			final Price price = new PriceImpl();
			price.setCurrency(getCurrency());
			return price;
		}
	}

}
