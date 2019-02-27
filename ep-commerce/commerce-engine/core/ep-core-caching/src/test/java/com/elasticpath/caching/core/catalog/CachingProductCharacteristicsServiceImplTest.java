/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.caching.core.catalog;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductCharacteristics;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.service.catalog.ProductCharacteristicsService;

/**
 * Test that {@link CachingProductCharacteristicsServiceImpl} behaves as expected.
 */
public class CachingProductCharacteristicsServiceImplTest {

	private static final String RESULTS_SHOULD_MATCH_DELEGATE_RESULTS = "The service should return the results returned from the delegate";
	private static final String SKU_CODE = "SKU1";
	
	private ProductCharacteristicsService service;
	@Mock private ProductCharacteristicsService delegate;
	@Mock private Ehcache cache;
	
	/**
	 * Setup required for each test.
	 */
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		
		service = new CachingProductCharacteristicsServiceImpl();
		
		((CachingProductCharacteristicsServiceImpl) service).setDelegateService(delegate);
		((CachingProductCharacteristicsServiceImpl) service).setCache(cache);
	}
	
	/**
	 * Test that get product characteristics by product delegates to the delegate.
	 */
	@Test
	public void testGetProductCharacteristicsProduct() {
		final Product product = mock(Product.class);
		final ProductCharacteristics productCharacteristics = mock(ProductCharacteristics.class);
		
		when(delegate.getProductCharacteristics(product)).thenReturn(productCharacteristics);
	
		ProductCharacteristics result = service.getProductCharacteristics(product);
		
		assertEquals(RESULTS_SHOULD_MATCH_DELEGATE_RESULTS, productCharacteristics, result);
		verifyZeroInteractions(cache);
	}

	/**
	 * Test the behaviour of get product characteristics product sku delegates to the delegate.
	 */
	@Test
	public void testGetProductCharacteristicsProductSku() {
		final ProductSku sku = mock(ProductSku.class);
		final ProductCharacteristics productCharacteristics = mock(ProductCharacteristics.class);
		when(delegate.getProductCharacteristics(sku)).thenReturn(productCharacteristics);
		
		ProductCharacteristics result = service.getProductCharacteristics(sku);
		
		assertEquals(RESULTS_SHOULD_MATCH_DELEGATE_RESULTS, productCharacteristics, result);
		verifyZeroInteractions(cache);
	}

	/**
	 * Test that get product characteristics for sku code looks in the cache.
	 */
	@Test
	public void testGetProductCharacteristicsForSkuCodeInCache() {
		final ProductCharacteristics productCharacteristics = mock(ProductCharacteristics.class);
		final Element element = new Element(SKU_CODE, productCharacteristics);
		when(cache.get(SKU_CODE)).thenReturn(element);
		
		ProductCharacteristics result = service.getProductCharacteristicsForSkuCode(SKU_CODE);
		assertEquals("The service should return the results returned from the cache", productCharacteristics, result);
		verifyZeroInteractions(delegate);
	}
	
	/**
	 * Test that get product characteristics for sku code when the cache has expired calls the delegate.
	 */
	@Test
	public void testGetProductCharacteristicsForSkuCodeExpired() {
		final ProductCharacteristics productCharacteristics = mock(ProductCharacteristics.class);
		final Element element = new Element(SKU_CODE, productCharacteristics) {
			private static final long serialVersionUID = 1L;
			
			@Override
			public boolean isExpired() {
				return true;
			};
		};
		when(delegate.getProductCharacteristicsForSkuCode(SKU_CODE)).thenReturn(productCharacteristics);
		when(cache.get(SKU_CODE)).thenReturn(element);
		
		ProductCharacteristics result = service.getProductCharacteristicsForSkuCode(SKU_CODE);
		assertEquals(RESULTS_SHOULD_MATCH_DELEGATE_RESULTS, productCharacteristics, result);
		verify(cache).put(element);
	}

	/**
	 * Test that get product characteristics for sku code when the value is not cached calls the delegate.
	 */
	@Test
	public void testGetProductCharacteristicsForSkuCodeNotCached() {
		final ProductCharacteristics productCharacteristics = mock(ProductCharacteristics.class);
		final Element element = new Element(SKU_CODE, productCharacteristics);
		when(delegate.getProductCharacteristicsForSkuCode(SKU_CODE)).thenReturn(productCharacteristics);
		when(cache.get(SKU_CODE)).thenReturn(null);

		ProductCharacteristics result = service.getProductCharacteristicsForSkuCode(SKU_CODE);
		assertEquals(RESULTS_SHOULD_MATCH_DELEGATE_RESULTS, productCharacteristics, result);
		verify(cache).put(element);
	}

}
