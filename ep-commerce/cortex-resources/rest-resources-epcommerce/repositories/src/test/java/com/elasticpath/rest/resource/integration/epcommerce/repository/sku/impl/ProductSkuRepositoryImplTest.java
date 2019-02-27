/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.sku.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.service.catalog.ProductSkuLookup;

@RunWith(MockitoJUnitRunner.class)
public class ProductSkuRepositoryImplTest {
	private static final String SKU_CODE = "SKU123";
	private static final String SKU_GUID = "a845e2e3-b6b1-4bbb-b846-44542ea8f3f0";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private ProductSkuLookup productSkuLookup;

	@InjectMocks
	private ProductSkuRepositoryImpl repository;

	@Test
	public void testGetProductSkuWithAttributesByCode() {
		final ProductSku mockProductSku = mock(ProductSku.class);
		when(productSkuLookup.findBySkuCode(SKU_CODE)).thenReturn(mockProductSku);

		ExecutionResult<ProductSku> result = repository.getProductSkuWithAttributesByCode(SKU_CODE);

		assertTrue("The operation should have been successful", result.isSuccessful());
		assertEquals("The result should be the product sku returned by the core service", mockProductSku, result.getData());
	}

	@Test
	public void testGetProductSkuWithAttributesByCodeNotFound() {
		when(productSkuLookup.findBySkuCode(SKU_CODE)).thenReturn(null);

		ExecutionResult<ProductSku> result = repository.getProductSkuWithAttributesByCode(SKU_CODE);

		assertTrue("The operation should have failed", result.isFailure());
		assertEquals("The status should be NOT FOUND", ResourceStatus.NOT_FOUND, result.getResourceStatus());
	}

	@Test
	public void testGetProductSkuWithAttributesByGuid() {
		final ProductSku mockProductSku = mock(ProductSku.class);
		when(productSkuLookup.findByGuid(SKU_GUID)).thenReturn(mockProductSku);

		ExecutionResult<ProductSku> result = repository.getProductSkuWithAttributesByGuid(SKU_GUID);

		assertThat(result.isSuccessful()).isTrue();
		assertThat(result.getData()).isEqualTo(mockProductSku);
	}

	@Test
	public void testGetProductSkuWithAttributesByGuidNotFound() {
		ExecutionResult<ProductSku> result = repository.getProductSkuWithAttributesByGuid(SKU_GUID);

		assertThat(result.isFailure()).isTrue();
		assertThat(result.getResourceStatus()).isEqualTo(ResourceStatus.NOT_FOUND);
	}

	@Test
	public void testIsProductBundleGuidNotFound() {
		when(productSkuLookup.findByGuid(SKU_GUID)).thenReturn(null);

		ExecutionResult<Boolean> result = repository.isProductBundle(SKU_GUID);

		assertThat(result.isFailure()).isTrue();
		assertThat(result.getResourceStatus()).isEqualTo(ResourceStatus.NOT_FOUND);
	}

	@Test
	public void testIsProductBundle() {
		ProductSku mockProductSku = mock(ProductSku.class);
		when(mockProductSku.getProduct()).thenReturn(mock(ProductBundle.class));
		when(productSkuLookup.findByGuid(SKU_GUID)).thenReturn(mockProductSku);

		ExecutionResult<Boolean> result = repository.isProductBundle(SKU_GUID);

		assertThat(result.isSuccessful()).isTrue();
		assertThat(result.getData()).isEqualTo(true);
	}

	@Test
	public void testIsNotAProductBundle() {
		ProductSku mockProductSku = mock(ProductSku.class);
		when(productSkuLookup.findByGuid(SKU_GUID)).thenReturn(mockProductSku);

		ExecutionResult<Boolean> result = repository.isProductBundle(SKU_GUID);

		assertThat(result.isSuccessful()).isTrue();
		assertThat(result.getData()).isEqualTo(false);
	}

	@Test
	public void shouldFailOnInvalidItemIdWhenCheckingIfProductSkuExists() {

		ExecutionResult<Boolean> result = repository.isProductSkuExist("Invalid_Item_Id=");

		assertThat(result.isFailure()).isTrue();
		assertThat(result.getErrorMessage()).isEqualTo("Item not found");
	}

	@Test
	public void shouldSucceedOnValidItemIdWhenCheckingIfProductSkuExists() {
		String skuCode = "sony_bt_sku";
		when(productSkuLookup.isProductSkuExist(skuCode))
			.thenReturn(Boolean.TRUE);

		ExecutionResult<Boolean> result = repository.isProductSkuExist("qgqvhk3tn5xhsx3corpxg23v=");

		assertThat(result.isSuccessful()).isTrue();
		verify(productSkuLookup).isProductSkuExist(skuCode);
	}

	@Test
	public void shouldFailOnLookupCallFailureWhenCheckingIfProductSkuExists() {

		when(productSkuLookup.isProductSkuExist("sony_bt_sku"))
			.thenThrow(new EpServiceException("Failure"));

		thrown.expect(EpServiceException.class);
		thrown.expectMessage("Failure");

		repository.isProductSkuExist("qgqvhk3tn5xhsx3corpxg23v=");
	}
}
