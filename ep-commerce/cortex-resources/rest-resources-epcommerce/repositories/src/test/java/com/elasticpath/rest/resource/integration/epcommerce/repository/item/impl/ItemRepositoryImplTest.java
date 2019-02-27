/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.item.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Set;

import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.sku.ProductSkuRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.impl.ReactiveAdapterImpl;
import com.elasticpath.rest.test.AssertExecutionResult;
import com.elasticpath.service.catalog.BundleIdentifier;
import com.elasticpath.service.catalog.ProductSkuLookup;

/**
 * Contains tests for ItemRepository operations.
 */
@RunWith(MockitoJUnitRunner.class)
public class ItemRepositoryImplTest {
	private static final String PRODUCT_CODE = "product code";
	private static final String SKU_CODE = "sku code";

	@Mock
	private ProductSkuRepository mockProductSkuRepository;
	@Mock
	private ProductSkuLookup mockProductSkuLookup;
	@Mock
	private BundleIdentifier mockBundleIdentifier;

	private ItemRepositoryImpl itemRepository;

	@InjectMocks
	private ReactiveAdapterImpl reactiveAdapter;

	private final Product product = new ProductImpl();
	private final ProductSku productSku = createProductSku(SKU_CODE, product);
	private final ProductSku productSkuWithNoProduct = createProductSku("sku code with no product");

	@Before
	public void setUp() {
		product.setCode(PRODUCT_CODE);
		itemRepository = new ItemRepositoryImpl(mockProductSkuRepository, mockProductSkuLookup, mockBundleIdentifier, reactiveAdapter);
	}

	@Test
	public void ensureThatItemIdGeneratedForSkuCanBeUsedToRetrieveIt() {
		ExecutionResult<String> itemIdResult = itemRepository.getItemIdForSku(productSku);

		assertEquals(ResourceStatus.READ_OK, itemIdResult.getResourceStatus());
		String itemId = itemIdResult.getData();
		assertNotNull(itemId);

		when(mockProductSkuRepository.getProductSkuWithAttributesByCode(productSku.getSkuCode()))
				.thenReturn(ExecutionResultFactory.createReadOK(productSku));
		ExecutionResult<ProductSku> skuForItemIdResult = itemRepository.getSkuForItemId(itemId);

		assertEquals(ResourceStatus.READ_OK, skuForItemIdResult.getResourceStatus());
		assertEquals(productSku, skuForItemIdResult.getData());

	}

	@Test(expected = AssertionError.class)
	public void ensureThatGetSkuForNullItemIdThrowsAssertionError() {
		itemRepository.getSkuForItemId(null);
	}

	@Test
	public void ensureIsItemBundleReturnsResultsFromService() {
		when(mockProductSkuRepository.getProductSkuWithAttributesByCode(productSku.getSkuCode()))
				.thenReturn(ExecutionResultFactory.createReadOK(productSku));
		when(mockBundleIdentifier.isBundle(any(Product.class))).thenReturn(true);

		ExecutionResult<Boolean> isItemBundleResult = itemRepository.isItemBundle(generateItemId(productSku));

		assertEquals(ResourceStatus.READ_OK, isItemBundleResult.getResourceStatus());
		assertTrue(isItemBundleResult.getData());
	}

	@Test(expected = AssertionError.class)
	public void ensureIsItemBundleThrowsErrorWhenNullProductOnSku() {
		when(mockProductSkuRepository.getProductSkuWithAttributesByCode(productSkuWithNoProduct.getSkuCode()))
				.thenReturn(ExecutionResultFactory.createReadOK(productSkuWithNoProduct));
		itemRepository.isItemBundle(generateItemId(productSkuWithNoProduct));
	}


	@Test
	public void ensureSkuForSkuGuidReturnsSuccessfully() {
		String skuGuid = "testSkuGuid";
		ProductSku mockProductSku = mock(ProductSku.class);
		mockFindProductSkuBySkuGuid(mockProductSku, skuGuid);

		ExecutionResult<ProductSku> productSkuResult = itemRepository.getSkuForSkuGuid(skuGuid);

		AssertExecutionResult.assertExecutionResult(productSkuResult)
				.isSuccessful()
				.data(mockProductSku);
	}

	@Test
	public void ensureSkuForSkuGuidReturnsFailureWhenNull() {
		String skuGuid = "testSkuGuid";
		when(mockProductSkuRepository.getProductSkuWithAttributesByGuid(skuGuid))
				.thenReturn(ExecutionResultFactory.<ProductSku>createNotFound());

		ExecutionResult<ProductSku> productSkuResult = itemRepository.getSkuForSkuGuid(skuGuid);

		AssertExecutionResult.assertExecutionResult(productSkuResult)
				.isFailure()
				.resourceStatus(ResourceStatus.NOT_FOUND);
	}

	@Test
	public void ensureAsProductBundleReturnsABundle() {
		ProductBundle mockProductBundle = mock(ProductBundle.class);
		when(mockBundleIdentifier.asProductBundle(product)).thenReturn(mockProductBundle);

		ProductBundle productBundle = itemRepository.asProductBundle(product);

		assertEquals(mockProductBundle, productBundle);
	}

	@Test(expected = AssertionError.class)
	public void ensureAsProductBundleReturnsErrorIfProductIsNull() {
		itemRepository.asProductBundle(null);
	}

	@Test
	public void givenValidItemIdReturnSkuOptions() {
		ProductType productType = mock(ProductType.class);
		Set<SkuOption> skuOptions = Sets.newHashSet();
		productSku.getProduct().setProductType(productType);

		ExecutionResult<String> itemIdResult = itemRepository.getItemIdForSku(productSku);

		when(mockProductSkuLookup.findBySkuCode(SKU_CODE)).thenReturn(productSku);
		when(productType.getSkuOptions()).thenReturn(skuOptions);

		ExecutionResult<Set<SkuOption>> result = itemRepository.getSkuOptionsForItemId(itemIdResult.getData());
		assertTrue(result.isSuccessful());
		assertEquals(skuOptions, result.getData());
	}

	@Test
	public void givenProductSkuLookupFailureReturnServerError() {
		ExecutionResult<String> itemIdResult = itemRepository.getItemIdForSku(productSku);

		when(mockProductSkuLookup.findBySkuCode(SKU_CODE)).thenThrow(new EpServiceException("Exception"));

		ExecutionResult<Set<SkuOption>> result = itemRepository.getSkuOptionsForItemId(itemIdResult.getData());
		assertTrue(result.isFailure());
	}

	private void mockFindProductSkuBySkuGuid(final ProductSku mockProductSku, final String skuGuid) {
		when(mockProductSkuRepository.getProductSkuWithAttributesByGuid(skuGuid))
				.thenReturn(ExecutionResultFactory.createReadOK(mockProductSku));
	}

	private ProductSku createProductSku(final String skuCode) {
		ProductSku productSku = new ProductSkuImpl();
		productSku.setSkuCode(skuCode);
		return productSku;
	}

	private ProductSku createProductSku(final String skuCode, final Product product) {
		ProductSku productSku = createProductSku(skuCode);
		productSku.setProduct(product);
		return productSku;
	}

	private String generateItemId(final ProductSku productSku) {
		return itemRepository.getItemIdForSku(productSku).getData();
	}
}
