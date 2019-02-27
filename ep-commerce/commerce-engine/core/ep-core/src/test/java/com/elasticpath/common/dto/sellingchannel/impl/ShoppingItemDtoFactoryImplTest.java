/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.common.dto.sellingchannel.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.ItemConfiguration;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.BundleConstituentImpl;
import com.elasticpath.domain.catalog.impl.ItemConfigurationImpl;
import com.elasticpath.domain.catalog.impl.ProductBundleImpl;
import com.elasticpath.domain.catalog.impl.ProductConstituentImpl;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.catalog.impl.ProductSkuConstituentImpl;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.catalog.impl.SelectionRuleImpl;
import com.elasticpath.domain.misc.impl.RandomGuidImpl;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.service.catalog.impl.BundleIdentifierImpl;

/**
 * Tests for ShoppingItemDtoFactoryImpl.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShoppingItemDtoFactoryImplTest {
	private static final String PROD_A = "ProdA";
	private static final String AAA = "AAA";
	private static final String BBB = "BBB";
	private static final String SKU_A = "SkuA";
	private static final String SKU_B = "SkuB";
	private static final String SKU_C = "SkuC";
	private static final String SKU_D = "SkuD";

	private static final String BUNDLE_CODE = "BUNDLE_CODE";
	private static final String PRODUCT_CODE = "PRODUCT_CODE";

	@Mock
	private BeanFactory beanFactory;

	@Mock
	private ProductSkuLookup productSkuLookup;

	@InjectMocks
	private ShoppingItemDtoFactoryImpl factory;

	/**
	 * Set up required before each test.
	 */
	@Before
	public void setUp() {
		given(beanFactory.getBean("productConstituent")).will(invocationOnMock -> new ProductConstituentImpl());
		given(beanFactory.getBean("productSkuConstituent")).will(invocationOnMock -> new ProductSkuConstituentImpl());

		factory.setBundleIdentifier(new BundleIdentifierImpl());
	}

	/**
	 * Test that when a ShoppingItemDto is created from a ProductBundle, the
	 * DTO has the same number of constituents at all levels and they are in the same order.
	 * All skus must be marked as not selected.
	 */
	@Test
	public void testCreateShoppingItemDtoAddsMissingConstituents() {
		//Create a bundle with two items and a select 1 rule
		final String selectedSkuGuid = "selectedSku";
		final String nonSelectedSkuGuid = "nonSelectedSku";
		final String bundleSkuGuid = "bundleSku";

		final Product selectedProduct = createProductWithSkuCode(PRODUCT_CODE, selectedSkuGuid);
		final Product nonSelectedProduct = createProductWithSkuCode(PRODUCT_CODE, nonSelectedSkuGuid);

		final BundleConstituent selectedBundleItem = createBundleConstituentFrom(selectedProduct, 1);
		final BundleConstituent nonSelectedBundleItem = createBundleConstituentFrom(nonSelectedProduct.getDefaultSku(), 1);

		final ProductBundle bundle = createProductBundleWithSkuCode(BUNDLE_CODE, bundleSkuGuid);
		bundle.setSelectionRule(new SelectionRuleImpl(1));
		bundle.addConstituent(selectedBundleItem);
		bundle.addConstituent(nonSelectedBundleItem);

		//Assemble the ShoppingItemDto and check it.
		ShoppingItemDto dto = factory.createDto(bundle, 1);
		assertEquals("DTO should have the same number of constituents as the Bundle",
				dto.getConstituents().size(), bundle.getConstituents().size());
		assertEquals("DTO's first sku should be the same as the bundle's first sku.",
				selectedSkuGuid, dto.getConstituents().get(0).getSkuCode());
		assertEquals("DTO's second sku should be the same as the bundle's second sku.",
				nonSelectedSkuGuid, dto.getConstituents().get(1).getSkuCode());
		assertFalse("DTO's first sku should not be selected",
				dto.getConstituents().get(0).isSelected());
		assertFalse("DTO's second sku should not be selected",
				dto.getConstituents().get(1).isSelected());
		assertFalse(dto.getConstituents().get(0).isProductSkuConstituent());
		assertTrue(dto.getConstituents().get(1).isProductSkuConstituent());

		// Assert that a second dto created from the same bundle is equal to the first.
		ShoppingItemDto dto2 = factory.createDto(bundle, 1);
		assertEquals(dto, dto2);
		assertEquals(dto.hashCode(), dto2.hashCode());
	}

	/**
	 * Tests that a {@code ShoppingItemDto} can be created from a {@code ProductBundle}.
	 * A simple parent->child->grandchild is tested to check recursion.
	 */
	@Test
	public void testCreateShoppingItemDto() {
		//Create grandChild product/sku
		Product grandChildProduct = createProductWithSkuCode("ProdC", BBB);

		//Create child product(Bundle)/sku
		ProductBundle childProduct = createProductBundleWithSkuCode("ProdB", AAA);
		childProduct.addConstituent(createBundleConstituentFrom(grandChildProduct, 1));

		//Create root bundle/sku
		ProductBundle productBundle = createProductBundleWithSkuCode(PROD_A, SKU_B);
		productBundle.addConstituent(createBundleConstituentFrom(childProduct, 1));

		ShoppingItemDto shoppingItemDto = factory.createDto(productBundle, 1);
		assertEquals(SKU_B, shoppingItemDto.getSkuCode());
		assertEquals(PROD_A, shoppingItemDto.getProductCode());
		assertEquals(1, shoppingItemDto.getQuantity());
		assertEquals(0, shoppingItemDto.getShoppingItemUidPk());

		assertEquals(1, shoppingItemDto.getConstituents().size());
		ShoppingItemDto actualChild = shoppingItemDto.getConstituents().get(0);
		assertEquals(AAA, actualChild.getSkuCode());
		assertEquals(1, actualChild.getConstituents().size());
		assertEquals(BBB, actualChild.getConstituents().get(0).getSkuCode());

		// Assert that a second shoppingItemDto created from the same bundle is equal to the first.
		ShoppingItemDto shoppingItemDto2 = factory.createDto(productBundle, 1);
		assertEquals(shoppingItemDto, shoppingItemDto2);
		assertEquals(shoppingItemDto.hashCode(), shoppingItemDto2.hashCode());
	}

	private String createBundleConstituentGuid(final Product product) {
		return product.getCode() + "_CG";
	}

	private BundleConstituent createBundleConstituentFrom(final Product product, final int quantity) {
		final BundleConstituentImpl bundleConstituent = new BundleConstituentImpl() {
			private static final long serialVersionUID = 1;

			@Override
			protected <T> T getBean(final String beanName) {
				return beanFactory.getBean(beanName);
			}
		};
		bundleConstituent.initialize();
		bundleConstituent.setConstituent(product);
		bundleConstituent.setQuantity(quantity);
		return bundleConstituent;
	}

	private BundleConstituent createBundleConstituentFrom(final ProductSku productSku, final int quantity) {
		final BundleConstituentImpl bundleConstituent = new BundleConstituentImpl() {
			private static final long serialVersionUID = 1;

			@Override
			protected <T> T getBean(final String beanName) {
				return beanFactory.getBean(beanName);
			}
		};
		bundleConstituent.initialize();
		bundleConstituent.setConstituent(productSku);
		bundleConstituent.setQuantity(quantity);
		return bundleConstituent;
	}

	/**
	 * Tests that an ItemConfiguration for a simple Product creates a ShoppingItemDto that is equal to the existing method.
	 */
	@Test
	public void testItemConfigurationForProduct() {
		final String selectedSkuGuid = "selectedSku";
		final Product selectedProduct = createProductWithSkuCode(PRODUCT_CODE, selectedSkuGuid);

		ItemConfigurationImpl itemConfiguration =
				new ItemConfigurationImpl(selectedSkuGuid, new HashMap<>(), true, null);

		ShoppingItemDto dtoFromProduct = factory.createDto(selectedProduct, 1);
		ShoppingItemDto dtoFromItemConfiguration = factory.createDto(selectedProduct, 1, itemConfiguration);

		assertTrue(dtoFromItemConfiguration.isSelected());
		assertEquals(dtoFromProduct, dtoFromItemConfiguration);
		assertEquals(dtoFromProduct.hashCode(), dtoFromItemConfiguration.hashCode());
	}

	/**
	 * Tests that an ItemConfiguration for a simple ProductBundle creates a ShoppingItemDto.
	 */
	@Test
	public void testItemConfigurationForProductBundle() {
		// childProductSkuC and childProductSkuD assembly.
		Product childProductCD = createProductWithSkuCode("ProdCD", SKU_C, SKU_D);

		// childProductSkuB assembly.
		ProductBundle childProductB = createProductBundleWithSkuCode("ProdB", SKU_B);

		// rootSku assembly.
		ProductBundle rootBundle = createProductBundleWithSkuCode(PROD_A, SKU_A);
		rootBundle.addConstituent(createBundleConstituentFrom(childProductB, 1));
		rootBundle.addConstituent(createBundleConstituentFrom(childProductCD, 1));

		// expected ShoppingItemDto assembly.
		ShoppingItemDto expectedDtoC = new ShoppingItemDto(SKU_D, 1);
		expectedDtoC.setProductCode("ProdCD");
		expectedDtoC.setSelected(false);

		ShoppingItemDto expectedDtoB = new ShoppingItemDto(SKU_B, 1);
		expectedDtoB.setProductCode("ProdB");
		expectedDtoB.setSelected(true);

		ShoppingItemDto expectedDtoA = new ShoppingItemDto(SKU_A, 1);
		expectedDtoA.setProductCode(PROD_A);
		expectedDtoA.setSelected(true);
		expectedDtoA.addConstituent(expectedDtoB);
		expectedDtoA.addConstituent(expectedDtoC);

		// ItemConfiguration assembly.
		ItemConfiguration itemConfigurationCD =
				new ItemConfigurationImpl(SKU_D, new HashMap<>(), false, createBundleConstituentGuid(childProductCD));
		ItemConfiguration itemConfigurationB =
				new ItemConfigurationImpl(SKU_B, new HashMap<>(), true, createBundleConstituentGuid(childProductB));

		Map<String, ItemConfiguration> childrenMap = new HashMap<>();
		childrenMap.put(createBundleConstituentGuid(childProductCD), itemConfigurationCD);
		childrenMap.put(createBundleConstituentGuid(childProductB), itemConfigurationB);
		ItemConfigurationImpl itemConfigurationA = new ItemConfigurationImpl(SKU_A, childrenMap, true, createBundleConstituentGuid(rootBundle));

		// Give the ItemConfiguration to the factory and get back the generated ShoppingItemDto.
		ShoppingItemDto dtoFromItemConfiguration = factory.createDto(rootBundle, 1, itemConfigurationA);
		assertEquals(expectedDtoA, dtoFromItemConfiguration);
		assertEquals(expectedDtoA.hashCode(), dtoFromItemConfiguration.hashCode());
	}

	private Product createProductWithSkuCode(final String productCode, final String... skuCodes) {
		final Product product = new ProductImpl();
		product.setCode(productCode);

		for (String skuCode : skuCodes) {
			final ProductSku sku = new ProductSkuImpl();
			sku.initialize();
			sku.setSkuCode(skuCode);
			product.addOrUpdateSku(sku);
			givenProductSkuLookupWillFindSku(sku);
		}

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
			given(productSkuLookup.findBySkuCode(sku.getSkuCode())).willReturn(sku);
		}
	}

}
