/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemselections.integration.epcommerce.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;


import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.domain.skuconfiguration.impl.SkuOptionImpl;
import com.elasticpath.domain.skuconfiguration.impl.SkuOptionValueImpl;
import com.elasticpath.jmock.MockeryFactory;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.product.StoreProductRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.product.option.SkuOptionRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.sku.ProductSkuRepository;
import com.elasticpath.service.catalog.MultiSkuProductConfigurationService;

/**
 * Tests the {@link ItemSelectionWriterStrategyImpl}.
 */
public class ItemSelectionWriterStrategyImplTest {

	private static final String OPERATION_SHOULD_BE_SUCCESSFUL = "Operation should be successful.";
	private static final String OTHER_OPTION_CODE = "other_option_code";
	private static final String NEW_ITEM_ID = "new_item_id";
	private static final String NEW_SKU_CODE = "new_sku_code";
	private static final String NEW_SKU_GUID = "new_sku_guid";
	private static final String VALUE_CODE = "value_code";
	private static final String OPTION_CODE = "option_code";
	private static final String ITEM_ID = "item_id";
	private static final String STORE_CODE = "store_code";

	@Rule
	public final JUnitRuleMockery context = MockeryFactory.newRuleInstance();
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private ProductSku mockProductSku;
	@Mock
	private ItemRepository mockItemRepository;
	@Mock
	private MultiSkuProductConfigurationService mockMultiSkuProductConfigurationService;
	@Mock
	private ProductSkuRepository mockProductSkuRepository;
	@Mock
	private SkuOptionRepository mockSkuOptionRepository;
	@Mock
	private StoreProductRepository mockStoreProductRepository;
	@Mock
	private StoreProduct storeProduct;

	private final ProductSku newSku = new ProductSkuImpl();
	private final SkuOptionValue newOptionValue = new SkuOptionValueImpl();

	private ItemSelectionWriterStrategyImpl strategy;

	@Before
	public void setUp() {
		strategy = new ItemSelectionWriterStrategyImpl(mockItemRepository, mockMultiSkuProductConfigurationService,
			mockProductSkuRepository, mockSkuOptionRepository, mockStoreProductRepository);
	}

	@Test
	public void testSaveConfigurationSuccessfully() {
		newSku.setSkuCode(NEW_SKU_CODE);

		mockGetProductWithAttributesByGuid(ExecutionResultFactory.createReadOK(newSku));
		mockFindSkuOptionValueByKey(ExecutionResultFactory.createReadOK(newOptionValue));
		mockFindStoreProduct(ExecutionResultFactory.createReadOK(storeProduct));
		mockProductSkuLookupAndSetup();
		mockGetItemIdForSku(ExecutionResultFactory.createReadOK(NEW_ITEM_ID));
		mockGetSkuForItemId(ExecutionResultFactory.createReadOK(mockProductSku));

		ExecutionResult<String> result = strategy.saveItemConfiguration(STORE_CODE, ITEM_ID, OPTION_CODE, VALUE_CODE);

		assertTrue(OPERATION_SHOULD_BE_SUCCESSFUL, result.isSuccessful());
		assertEquals("Result configuration id does not match expected value.", NEW_ITEM_ID, result.getData());
	}

	@Test
	public void testSaveConfigurationWhenSkuOptionNotFound() {
		newSku.setSkuCode(NEW_SKU_CODE);

		mockProductSkuLookupAndSetup();
		mockFindStoreProduct(ExecutionResultFactory.createReadOK(storeProduct));
		mockGetProductWithAttributesByGuid(ExecutionResultFactory.createReadOK(newSku));
		mockGetSkuForItemId(ExecutionResultFactory.createReadOK(mockProductSku));
		mockFindSkuOptionValueByKey(ExecutionResultFactory.createNotFound());

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		strategy.saveItemConfiguration(STORE_CODE, ITEM_ID, OPTION_CODE, VALUE_CODE);

	}

	@Test
	public void testSaveConfigurationWhenProductSkuNotFound() {
		mockGetSkuForItemId(ExecutionResultFactory.createNotFound());
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		strategy.saveItemConfiguration(STORE_CODE, ITEM_ID, OPTION_CODE, VALUE_CODE);
	}

	@Test
	public void testSaveItemConfigurationWhenStoreProductNotFound() {
		mockProductSkuLookupAndSetup();
		mockGetSkuForItemId(ExecutionResultFactory.createReadOK(mockProductSku));
		mockFindStoreProduct(ExecutionResultFactory.createNotFound());

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		strategy.saveItemConfiguration(STORE_CODE, ITEM_ID, OPTION_CODE, VALUE_CODE);
	}

	@Test
	public void testSaveItemConfigurationWhenCannotFindMatchingSelectionSkus() {
		final SkuOptionValue oldOptionValue = createMockSkuOptionValue("oldOptionValue", OPTION_CODE);

		mockFindSkuOptionValueByKey(ExecutionResultFactory.createReadOK(oldOptionValue));
		mockFindStoreProduct(ExecutionResultFactory.createReadOK(storeProduct));
		mockGetSkuForItemId(ExecutionResultFactory.createReadOK(mockProductSku));
		mockProductSkuLookupAndSetupWithEmptySkuList(oldOptionValue);

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		strategy.saveItemConfiguration(STORE_CODE, ITEM_ID, OPTION_CODE, VALUE_CODE);
	}

	@Test
	public void testSaveItemConfigurationWhenNewProductSkuNotFound() {
		mockFindStoreProduct(ExecutionResultFactory.createReadOK(storeProduct));
		mockGetSkuForItemId(ExecutionResultFactory.createReadOK(mockProductSku));
		mockFindSkuOptionValueByKey(ExecutionResultFactory.createReadOK(newOptionValue));
		mockProductSkuLookupAndSetup();
		mockGetProductWithAttributesByGuid(ExecutionResultFactory.createNotFound());
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		strategy.saveItemConfiguration(STORE_CODE, ITEM_ID, OPTION_CODE, VALUE_CODE);
	}

	private SkuOptionValue createMockSkuOptionValue(final String mockName, final String optionKey) {
		final SkuOptionValue optionValue = context.mock(SkuOptionValue.class, mockName);
		final SkuOption skuOption = new SkuOptionImpl();
		skuOption.setOptionKey(optionKey);

		context.checking(new Expectations() {
			{
				allowing(optionValue).getSkuOption();
				will(returnValue(skuOption));
			}
		});
		return optionValue;
	}

	private void mockProductSkuLookupAndSetup() {
		final SkuOptionValue oldOptionValue = createMockSkuOptionValue("oldOptionValue", OPTION_CODE);
		final SkuOptionValue otherOptionValue = createMockSkuOptionValue("otherOptionValue", OTHER_OPTION_CODE);
		final Collection<SkuOptionValue> oldSkuOptionValues = Arrays.asList(oldOptionValue, otherOptionValue);
		final Collection<SkuOptionValue> newSkuOptionValues = Arrays.asList(otherOptionValue, newOptionValue);
		final Collection<String> matchingSkuGuids = Collections.singleton(NEW_SKU_GUID);

		context.checking(new Expectations() {
			{
				allowing(mockProductSku).getProduct();
				allowing(mockProductSku).getOptionValues();
				will(returnValue(oldSkuOptionValues));

				allowing(mockMultiSkuProductConfigurationService).findSkuGuidsMatchingSelectedOptions(storeProduct, newSkuOptionValues);
				will(returnValue(matchingSkuGuids));
			}
		});
	}

	private void mockProductSkuLookupAndSetupWithEmptySkuList(final SkuOptionValue oldOptionValue) {
		final SkuOptionValue otherOptionValue = createMockSkuOptionValue("otherOptionValue", OTHER_OPTION_CODE);
		final Collection<SkuOptionValue> oldSkuOptionValues = Arrays.asList(otherOptionValue, oldOptionValue);
		final Collection<String> matchingSkuGuids = Collections.emptyList();

		context.checking(new Expectations() {
			{
				allowing(mockProductSku).getProduct();
				allowing(mockProductSku).getOptionValues();
				will(returnValue(oldSkuOptionValues));

				allowing(mockMultiSkuProductConfigurationService).findSkuGuidsMatchingSelectedOptions(storeProduct, oldSkuOptionValues);
				will(returnValue(matchingSkuGuids));
			}
		});
	}

	private <T> void mockGetProductWithAttributesByGuid(final ExecutionResult<T> executionResult) {
		context.checking(new Expectations() {
			{
				allowing(mockProductSkuRepository).getProductSkuWithAttributesByGuid(NEW_SKU_GUID);
				will(returnValue(executionResult));
			}
		});
	}

	private <T> void mockGetSkuForItemId(final ExecutionResult<T> executionResult) {
		context.checking(new Expectations() {
			{
				allowing(mockItemRepository).getSkuForItemId(ITEM_ID);
				will(returnValue(executionResult));
			}
		});
	}

	private <T> void mockGetItemIdForSku(final ExecutionResult<T> executionResult) {
		context.checking(new Expectations() {
			{
				allowing(mockItemRepository).getItemIdForSku(newSku);
				will(returnValue(executionResult));
			}
		});
	}

	private <T> void mockFindSkuOptionValueByKey(final ExecutionResult<T> executionResult) {
		context.checking(new Expectations() {
			{
				allowing(mockSkuOptionRepository).findSkuOptionValueByKey(OPTION_CODE, VALUE_CODE);
				will(returnValue(executionResult));
			}
		});
	}

	private <T> void mockFindStoreProduct(final ExecutionResult<T> executionResult) {
		context.checking(new Expectations() {
			{
				allowing(mockStoreProductRepository)
						.findDisplayableStoreProductWithAttributesByProductGuid(with(any(String.class)), with(any(String.class)));
				will(returnValue(executionResult));
			}
		});
	}
}
