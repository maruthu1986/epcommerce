/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemselections.integration.epcommerce.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.domain.catalogview.impl.StoreProductImpl;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.product.StoreProductRepository;
import com.elasticpath.rest.resource.itemselections.integration.ItemSelectionOptionValuesDto;
import com.elasticpath.rest.resource.itemselections.integration.epcommerce.transform.SkuOptionValueSelectionTransformer;
import com.elasticpath.rest.resource.itemselections.integration.epcommerce.wrapper.SkuOptionValueSelectionWrapper;
import com.elasticpath.service.catalog.MultiSkuProductConfigurationService;

/**
 * Test class for {@link ItemSelectionLookupStrategyImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ItemSelectionLookupStrategyImplTest {

	private static final String UNSELECTED_OPTION_KEY = "unselectedOptionKey";
	private static final String OPERATION_SHOULD_BE_SUCCESSFUL = "Operation should be successful.";
	private static final String OPTION_VALUE_KEY = "optionValueKey";
	private static final String PRODUCT_GUID = "product_guid";
	private static final String OPTION_CODE = "option_code";
	private static final String ITEM_ID = "configuration_code";
	private static final String STORE_CODE = "store_code";

	private final ProductSku productSku = new ProductSkuImpl();
	private final Product product = new ProductImpl();
	private StoreProduct storeProduct;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private SkuOptionValue mockSkuOptionValue;
	@Mock
	private StoreProductRepository mockStoreProductRepository;
	@Mock
	private ItemRepository mockItemRepository;
	@Mock
	private MultiSkuProductConfigurationService mockMultiSkuProductConfigurationService;
	@Mock
	private SkuOptionValueSelectionTransformer mockSkuOptionValueSelectionTransformer;

	@InjectMocks
	private ItemSelectionLookupStrategyImpl lookupStrategy;

	/**
	 * Test find selected option value for option.
	 */
	@Test
	public void testFindSelectedOptionValueForOption() {
		productSku.setOptionValueMap(Collections.singletonMap(OPTION_CODE, mockSkuOptionValue));
		mockGetProductSku(ExecutionResultFactory.createReadOK(productSku));

		when(mockSkuOptionValue.getOptionValueKey()).thenReturn(OPTION_VALUE_KEY);

		ExecutionResult<String> result = lookupStrategy.findSelectedOptionValueForOption(STORE_CODE, ITEM_ID, OPTION_CODE);

		assertTrue(OPERATION_SHOULD_BE_SUCCESSFUL, result.isSuccessful());
		assertEquals("Option value key does not match expected value.", OPTION_VALUE_KEY, result.getData());
	}

	/**
	 * Test find selected option value for option when {@link ProductSku} not found.
	 */
	@Test
	public void testFindSelectedOptionValueForOptionWhenProductSkuNotFound() {
		ExecutionResult<ProductSku> lookupResult = ExecutionResultFactory.createNotFound();
		mockGetProductSku(lookupResult);
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		lookupStrategy.findSelectedOptionValueForOption(STORE_CODE, ITEM_ID, OPTION_CODE);
	}

	/**
	 * Test find option value selections.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testFindOptionValueSelections() {
		Collection<SkuOptionValue> selectableSkuOptionValues = Collections.emptyList();
		ItemSelectionOptionValuesDto dto = ResourceTypeFactory.createResourceEntity(ItemSelectionOptionValuesDto.class);
		storeProduct = mockSetUpProduct();
		mockGetProductSku(ExecutionResultFactory.createReadOK(productSku));
		mockFindStoreProduct(ExecutionResultFactory.createReadOK(storeProduct));

		when(mockMultiSkuProductConfigurationService.getAvailableOptionValuesForOption((any(StoreProduct.class)), (any(String.class)),
				(any(Collection.class)))).thenReturn(selectableSkuOptionValues);
		when(mockSkuOptionValueSelectionTransformer.transformToEntity(any(SkuOptionValueSelectionWrapper.class))).thenReturn(dto);

		ExecutionResult<ItemSelectionOptionValuesDto> result = lookupStrategy.findOptionValueSelections(STORE_CODE, ITEM_ID, OPTION_CODE);

		assertTrue(OPERATION_SHOULD_BE_SUCCESSFUL, result.isSuccessful());
		assertEquals("Result dto does not match expected value.", dto, result.getData());
	}

	/**
	 * Test find option value selections with selected option values.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testFindOptionValueSelectionsWithSelectedOptionValues() {
		Collection<SkuOptionValue> selectableSkuOptionValues = Collections.emptyList();
		ItemSelectionOptionValuesDto dto = ResourceTypeFactory.createResourceEntity(ItemSelectionOptionValuesDto.class);
		Map<String, SkuOptionValue> selectableOptionValuesMap = new HashMap<>();
		selectableOptionValuesMap.put(UNSELECTED_OPTION_KEY, mockSkuOptionValue);
		productSku.setOptionValueMap(selectableOptionValuesMap);

		storeProduct = mockSetUpProduct();
		mockGetProductSku(ExecutionResultFactory.createReadOK(productSku));
		mockFindStoreProduct(ExecutionResultFactory.createReadOK(storeProduct));
		when(mockSkuOptionValue.getSkuOption().getOptionKey()).thenReturn(UNSELECTED_OPTION_KEY);
		when(mockMultiSkuProductConfigurationService.getAvailableOptionValuesForOption((any(StoreProduct.class)), (any(String.class)),
				(any(Collection.class)))).thenReturn(selectableSkuOptionValues);
		when(mockSkuOptionValueSelectionTransformer.transformToEntity(any(SkuOptionValueSelectionWrapper.class))).thenReturn(dto);

		ExecutionResult<ItemSelectionOptionValuesDto> result = lookupStrategy.findOptionValueSelections(STORE_CODE, ITEM_ID, OPTION_CODE);

		assertTrue(OPERATION_SHOULD_BE_SUCCESSFUL, result.isSuccessful());
		assertEquals("Result dto does not match expected value.", dto, result.getData());
	}

	/**
	 * Test find option value selections when sku not found.
	 */
	@Test
	public void testFindOptionValueSelectionsWhenSkuNotFound() {
		ExecutionResult<ProductSku> lookupResult = ExecutionResultFactory.createNotFound();
		mockGetProductSku(lookupResult);
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		lookupStrategy.findOptionValueSelections(STORE_CODE, ITEM_ID, OPTION_CODE);

	}

	/**
	 * Test find option value selections when store product not found.
	 */
	@Test
	public void testFindOptionValueSelectionsWhenStoreProductNotFound() {
		ExecutionResult<StoreProduct> lookupResult = ExecutionResultFactory.createNotFound();
		mockSetUpProduct();
		mockGetProductSku(ExecutionResultFactory.createReadOK(productSku));
		mockFindStoreProduct(lookupResult);
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		lookupStrategy.findOptionValueSelections(STORE_CODE, ITEM_ID, OPTION_CODE);
	}

	@Test
	public void testFindOptionValueSelectionsWhenSelectedNotSelectable() {
		Collection<SkuOptionValue> selectableSkuOptionValues = new ArrayList<>();
		selectableSkuOptionValues.add(mockSkuOptionValue);

		ItemSelectionOptionValuesDto dto = ResourceTypeFactory.createResourceEntity(ItemSelectionOptionValuesDto.class);
		Map<String, SkuOptionValue> selectableOptionValuesMap = new HashMap<>();
		final SkuOptionValue selectedSkuOptionValue = mock(SkuOptionValue.class, RETURNS_DEEP_STUBS);
		selectableOptionValuesMap.put(UNSELECTED_OPTION_KEY, mockSkuOptionValue);
		selectableOptionValuesMap.put(OPTION_CODE, selectedSkuOptionValue);

		productSku.setOptionValueMap(selectableOptionValuesMap);

		storeProduct = mockSetUpProduct();
		mockGetProductSku(ExecutionResultFactory.createReadOK(productSku));
		mockFindStoreProduct(ExecutionResultFactory.createReadOK(storeProduct));
		when(mockSkuOptionValue.getSkuOption().getOptionKey()).thenReturn(UNSELECTED_OPTION_KEY);
		when(selectedSkuOptionValue.getSkuOption().getOptionKey()).thenReturn(OPTION_CODE);
		when(mockMultiSkuProductConfigurationService.getAvailableOptionValuesForOption((any(StoreProduct.class)), (any(String.class)),
			(any(Collection.class)))).thenReturn(selectableSkuOptionValues);
		when(mockSkuOptionValueSelectionTransformer.transformToEntity(any(SkuOptionValueSelectionWrapper.class))).thenReturn(dto);

		ExecutionResult<ItemSelectionOptionValuesDto> result = lookupStrategy.findOptionValueSelections(STORE_CODE, ITEM_ID, OPTION_CODE);

		assertTrue(OPERATION_SHOULD_BE_SUCCESSFUL, result.isSuccessful());
		assertEquals("Result dto does not match expected value.", dto, result.getData());
		assertThat("The selected option value should have been added", selectableSkuOptionValues, hasItem(selectedSkuOptionValue));

	}

	private StoreProduct mockSetUpProduct() {
		product.setGuid(PRODUCT_GUID);
		productSku.setProduct(product);
		storeProduct = new StoreProductImpl(product);

		return storeProduct;
	}

	private void mockGetProductSku(final ExecutionResult<ProductSku> executionResult) {
		when(mockItemRepository.getSkuForItemId(ITEM_ID)).thenReturn(executionResult);
	}

	private void mockFindStoreProduct(final ExecutionResult<StoreProduct> executionResult) {
		when(mockStoreProductRepository
				.findDisplayableStoreProductWithAttributesByProductGuid(STORE_CODE, product.getGuid())).thenReturn(executionResult);
	}
}
