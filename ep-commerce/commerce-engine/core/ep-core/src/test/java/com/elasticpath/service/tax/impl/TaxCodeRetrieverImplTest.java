/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.service.tax.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.catalog.impl.ProductTypeImpl;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.domain.tax.impl.TaxCodeImpl;


/**
 * Tests for {@link TaxCodeRetrieverImpl}.
 */
public class TaxCodeRetrieverImplTest {

	private static final String TAX_CODE_TYPE_STRING = "GOODS";
	private static final String TAX_CODE_PRODUCT_STRING = "CLOTHES";
	private static final String TAX_CODE_SKU_STRING = "JORTS";

	private TaxCode productTypeTaxCode;
	private TaxCode productTaxCode;
	private TaxCode productSkuTaxCode;

	private ProductSku productSku;
	private Product product;
	private ProductType productType;
	private TaxCodeRetrieverImpl taxCodeRetrieverImpl;

	@Before
	public void setUp() {
		prepareTaxCodes();
		prepareDomainObjects();

		taxCodeRetrieverImpl = new TaxCodeRetrieverImpl();
	}

	private void prepareTaxCodes() {
		productTypeTaxCode = new TaxCodeImpl();
		productTypeTaxCode.setGuid("arbitaryGuid1");
		productTypeTaxCode.setCode(TAX_CODE_TYPE_STRING);
		productTaxCode = new TaxCodeImpl();
		productTaxCode.setCode(TAX_CODE_PRODUCT_STRING);
		productTaxCode.setGuid("arbitaryGuid2");
		productSkuTaxCode = new TaxCodeImpl();
		productSkuTaxCode.setCode(TAX_CODE_SKU_STRING);
		productSkuTaxCode.setGuid("arbitaryGuid3");
	}

	private void prepareDomainObjects() {
		productType = new ProductTypeImpl();
		productType.setTaxCode(productTypeTaxCode);

		product = new ProductImpl();
		product.setTaxCodeOverride(productTaxCode);
		product.setProductType(productType);

		productSku = new ProductSkuImpl();
		productSku.setTaxCodeOverride(productSkuTaxCode);
		productSku.setProduct(product);
		
	}

	@Test
	public void testGetEffectiveTaxCodeFromProductSku() {
		TaxCode effectiveTaxCode = taxCodeRetrieverImpl.getEffectiveTaxCode(productSku);

		assertEquals(productSkuTaxCode, effectiveTaxCode);
	}

	@Test
	public void testGetEffectiveTaxCodeFromProductSkuFallsBackToProduct() {
		productSku.setTaxCodeOverride(null);

		TaxCode effectiveTaxCode = taxCodeRetrieverImpl.getEffectiveTaxCode(productSku);

		assertEquals(productTaxCode, effectiveTaxCode);
	}

	@Test
	public void testGetEffectiveTaxCodeFromProductSkuFallsBackToProductTypeIfNoProductOverride() {
		productSku.setTaxCodeOverride(null);
		product.setTaxCodeOverride(null);

		TaxCode effectiveTaxCode = taxCodeRetrieverImpl.getEffectiveTaxCode(productSku);

		assertEquals(productTypeTaxCode, effectiveTaxCode);
	}

	@Test
	public void testGetEffectiveTaxCodeNull() {
		productSku.setTaxCodeOverride(null);
		product.setTaxCodeOverride(null);
		productType.setTaxCode(null);

		TaxCode effectiveTaxCode = taxCodeRetrieverImpl.getEffectiveTaxCode(productSku);

		assertEquals(null, effectiveTaxCode);
	}

	public void testGetEffectiveTaxCodeFromProduct() {
		TaxCode effectiveTaxCode = taxCodeRetrieverImpl.getEffectiveTaxCode(product);

		assertEquals("The product's tax code override should have been returned", productTaxCode, effectiveTaxCode);
	}

	@Test
	public void testGetEffectiveTaxCodeFromProductFallsBackToProductType() {
		product.setTaxCodeOverride(null);

		TaxCode effectiveTaxCode = taxCodeRetrieverImpl.getEffectiveTaxCode(product);
		assertEquals("The tax code from the product type should have been returned", productTypeTaxCode, effectiveTaxCode);
	}

}
