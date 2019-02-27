/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.catalog.editors.sku;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.service.catalog.ProductSkuService;

import org.eclipse.rap.rwt.testfixture.TestContext;

/**
 * Tests sku code validator for product creation wizard. 
 */
@SuppressWarnings({ "restriction" })
public class ProductSkuCodeValidatorTest {

	@Rule
	public final MockitoRule rule = MockitoJUnit.rule();

	@Rule
	public TestContext context = new TestContext();

	@Mock
	private ProductSkuService service;

	private final ProductSku sku = new ProductSkuImpl();
	private static final String SKUCODE = "skucode"; //$NON-NLS-1$
	
	private final ProductSkuCodeValidator productSkuCodeValidator = new ProductSkuCodeValidator(true, sku) {
		@Override
		protected ProductSkuService getProductSkuService() {
			return service;
		}
	};
	
	/**
	 * Init.
	 */
	@Before
	public void init() {
		Product prod = new ProductImpl();
		prod.setUidPk(1L);
		sku.setProduct(prod);
	}

	/**
	 * This test checks that if a given sku code already exists, validation fails. Status ERROR should be returned.
	 */
	@Test
	public void testSkuCodeExists() {
		final List<String> skuCodes = Collections.singletonList(SKUCODE);

		when(service.skuExists(anyCollection(), eq(1L))).thenReturn(skuCodes);

		IStatus status = productSkuCodeValidator.validate(SKUCODE);
		
		assertEquals("Validation should fail because sku code exists", IStatus.ERROR, status.getCode()); //$NON-NLS-1$
		assertEquals("Status error message is not valid", "SKU code skucode already exists.", status.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$

	}

	/**
	 * If there's no sku code in the database, validation should be successful. Status OK should be returned.
	 */
	@Test
	public void testSkuCodeDoesntExist() {
		when(service.skuExists(anyCollection(), eq(1L))).thenReturn(Collections.emptyList());

		IStatus status = productSkuCodeValidator.validate(SKUCODE);

		assertEquals("Validation should be successful because sku code doesn't exist", IStatus.OK, status.getCode()); //$NON-NLS-1$
	}
	
	
}
