/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.ui.dialog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.eclipse.rap.rwt.testfixture.TestContext;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import com.elasticpath.common.dto.pricing.BaseAmountDTO;
import com.elasticpath.common.dto.pricing.PriceListDescriptorDTO;
import com.elasticpath.common.pricing.service.BaseAmountFilter;
import com.elasticpath.common.pricing.service.PriceListHelperService;
import com.elasticpath.common.pricing.service.impl.BaseAmountFilterExtImpl;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;

/**
 * Tests pricing info retrieving for Sku Finder Dialog so that single sku pricing overrides are
 * handled appropriately.
 */
@SuppressWarnings({"restriction" })
public class RetrievingPricingInfoTest {

	@Rule
	public final MockitoRule rule = MockitoJUnit.rule();

	@Rule
	public TestContext context = new TestContext();


	private static final String USD = "USD"; //$NON-NLS-1$
	private static final String BA_GUID = "BA_GUID"; //$NON-NLS-1$
	private SkuFinderDialog dialog;
	private BaseAmountFilter filter;
	private static final String SKU_OBJECT_GUID = "SKU1"; //$NON-NLS-1$

	@Mock
	private ProductSku sku;

	@Mock
	private Product product;

	private Map<PriceListDescriptorDTO, List<BaseAmountDTO>> serviceResult =
		new HashMap<PriceListDescriptorDTO, List<BaseAmountDTO>>(); 
	private final Map<PriceListDescriptorDTO, List<BaseAmountDTO>> skuPriceOverrideServiceEmptyResult = 
		new HashMap<PriceListDescriptorDTO, List<BaseAmountDTO>>();

	@Mock
	private PriceListHelperService service;

	private final String[] currencies = new String[]{null};

	/**
	 * Init method.
	 */
	@Before
	public void init() {
		dialog = new SkuFinderDialog(null, null, null, true);
		filter =new BaseAmountFilterExtImpl();
		serviceResult = populateResponseMap(BA_GUID);
		dialog.setPriceListHelperService(service);			
	}
	
	private Map<PriceListDescriptorDTO, List<BaseAmountDTO>> populateResponseMap(final String baseAmountGuid) {
		Map<PriceListDescriptorDTO, List<BaseAmountDTO>> responseMap = new HashMap<PriceListDescriptorDTO, List<BaseAmountDTO>>();
		PriceListDescriptorDTO pldDto = new PriceListDescriptorDTO();
		pldDto.setCurrencyCode(USD);
		List<BaseAmountDTO> baDtos = new ArrayList<BaseAmountDTO>();
		BaseAmountDTO baDto = new BaseAmountDTO();
		baDto.setGuid(baseAmountGuid); 		
		baDtos.add(baDto);
		responseMap.put(pldDto, baDtos);
		return responseMap;
	}

	/**
	 * Single sku product is passed => an overridden price for the default sku of this product should be retrieved 
	 * (the case when overridden price exists).
	 */
	@Test
	public void testResolveForSingleSkuPricingOverridesSingleSkuProductWithOverride() {

		when(sku.getGuid()).thenReturn(SKU_OBJECT_GUID);
		when(service.getPriceListMap(filter, currencies)).thenReturn(serviceResult);
		when(product.hasMultipleSkus()).thenReturn(false);
		when(product.getDefaultSku()).thenReturn(sku);

		Map<PriceListDescriptorDTO, List<BaseAmountDTO>> itemPricesMap = new HashMap<PriceListDescriptorDTO, List<BaseAmountDTO>>();
		itemPricesMap = dialog.resolveForSingleSkuPricingOverrides(product, filter, itemPricesMap);
		assertFalse(itemPricesMap.isEmpty()); //should return overridden price 

		verify(sku).getGuid();
		verify(service).getPriceListMap(filter, currencies);
		verify(product).hasMultipleSkus();

	}
	
	/**
	 * Single sku product is passed => no overridden price for the default sku of this product should be retrieved 
	 * (the case when overridden price doesn't exist).
	 */
	@Test
	public void testResolveForSingleSkuPricingOverridesSingleSkuProductWithoutOverride() {

		when(sku.getGuid()).thenReturn(SKU_OBJECT_GUID);
		when(service.getPriceListMap(filter, currencies)).thenReturn(skuPriceOverrideServiceEmptyResult);
		when(product.hasMultipleSkus()).thenReturn(false);
		when(product.getDefaultSku()).thenReturn(sku);

		Map<PriceListDescriptorDTO, List<BaseAmountDTO>> itemPricesMap2 = new HashMap<PriceListDescriptorDTO, List<BaseAmountDTO>>();
		itemPricesMap2 = dialog.resolveForSingleSkuPricingOverrides(product, filter, itemPricesMap2);
		assertTrue(itemPricesMap2.isEmpty()); //should return empty result (no overridden price was found) 

		verify(sku).getGuid();
		verify(service).getPriceListMap(filter, currencies);
		verify(product).hasMultipleSkus();

	}
	
	/**
	 * Multi sku product is passed => no result should be retrieved (an overridden price is checked only for single sku products).
	 */
	@Test
	public void testResolveForSingleSkuPricingOverridesMultiSkuProduct() {

		when(product.hasMultipleSkus()).thenReturn(true);

		Map<PriceListDescriptorDTO, List<BaseAmountDTO>> itemPricesMap3 = new HashMap<PriceListDescriptorDTO, List<BaseAmountDTO>>();
		itemPricesMap3 = dialog.resolveForSingleSkuPricingOverrides(product, filter, itemPricesMap3);
		assertTrue(itemPricesMap3.isEmpty()); //product is multi sku - should return empty result
		verify(product).hasMultipleSkus();
	}

	/**
	 * Something other than product is passed => no result should be retrieved (an overridden price is checked only for single sku products).
	 */
	@Test
	public void testResolveForSingleSkuPricingOverridesNotPassingAProduct() {
		Map<PriceListDescriptorDTO, List<BaseAmountDTO>> itemPricesMap4 = new HashMap<PriceListDescriptorDTO, List<BaseAmountDTO>>();
		itemPricesMap4 = dialog.resolveForSingleSkuPricingOverrides(sku, filter, itemPricesMap4);
		assertTrue(itemPricesMap4.isEmpty()); //other than product object is passed - should return empty result
	}

	/**
	 * Tests that if no price was retrieved for sku in it will be derived from the product. 
	 */
	@Test
	public void testFallbackToProductPriceIfSkuPriceDoesNotExist() {
		final Map<PriceListDescriptorDTO, List<BaseAmountDTO>> multiSkuServiceResult = populateResponseMap("FALLBACK_PRICE"); //$NON-NLS-1$

		when(sku.getProduct()).thenReturn(product);
		when(product.getGuid()).thenReturn("PROD1"); //$NON-NLS-1$
		when(service.getPriceListMap(filter, currencies)).thenReturn(multiSkuServiceResult);

		Map<PriceListDescriptorDTO, List<BaseAmountDTO>> itemPricesMap = new HashMap<PriceListDescriptorDTO, List<BaseAmountDTO>>();
		itemPricesMap = dialog.fallbackToProductPriceIfSkuPriceDoesNotExist(sku, filter, itemPricesMap);
		assertFalse(itemPricesMap.isEmpty());
		assertEquals(itemPricesMap.values().iterator().next().get(0).getGuid(), "FALLBACK_PRICE"); //$NON-NLS-1$

		verify(sku).getProduct();
		verify(product).getGuid();
		verify(service).getPriceListMap(filter, currencies);
	}

	/**
	 * Tests that if price was retrieved for sku in it will not be derived from the product. 
	 */
	@Test
	public void testNoFallbackToProductPriceIfSkuPriceDoesExist() {
		Map<PriceListDescriptorDTO, List<BaseAmountDTO>> itemPricesMap = new HashMap<PriceListDescriptorDTO, List<BaseAmountDTO>>();
		itemPricesMap = dialog.fallbackToProductPriceIfSkuPriceDoesNotExist(sku, filter, serviceResult);
		assertFalse(itemPricesMap.isEmpty());
		assertEquals(itemPricesMap.values().iterator().next().get(0).getGuid(), BA_GUID);
	}

	/**
	 * Tests that if price will not be derived from the product if any other object than sku is passed. 
	 */
	@Test
	public void testNoFallbackToProductPriceIfNonSkuObjectIsPassed() {
		Map<PriceListDescriptorDTO, List<BaseAmountDTO>> itemPricesMap = new HashMap<PriceListDescriptorDTO, List<BaseAmountDTO>>();
		itemPricesMap = dialog.fallbackToProductPriceIfSkuPriceDoesNotExist(product, filter, itemPricesMap);
		assertTrue(itemPricesMap.isEmpty());
	}

}
