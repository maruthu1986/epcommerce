/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemdefinitions.integration.epcommerce.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import junit.framework.TestCase;
import org.apache.commons.lang3.StringUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.domain.skuconfiguration.impl.SkuOptionValueImpl;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.itemdefinitions.ItemDefinitionOptionEntity;
import com.elasticpath.rest.definition.itemdefinitions.ItemDefinitionOptionValueEntity;
import com.elasticpath.rest.identity.Subject;
import com.elasticpath.rest.identity.TestSubjectFactory;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;
import com.elasticpath.rest.resource.itemdefinitions.integration.epcommerce.transform.SkuOptionTransformer;
import com.elasticpath.rest.resource.itemdefinitions.integration.epcommerce.transform.SkuOptionValueTransformer;
import com.elasticpath.rest.util.collection.CollectionUtil;

/**
 * Test class for {@link ItemDefinitionOptionLookupStrategyImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ItemDefinitionOptionLookupStrategyImplTest extends TestCase {

	private static final String OPTION_CODE = "option_code";
	private static final String OPTION_GUID = "option_guid";
	private static final String VALUE_CODE = "value_code";
	private static final String PRODUCT_GUID = "product_guid";
	private static final String ITEM_ID = "item_id";
	private static final String INVALID_ITEM_ID = "invalid_item_id";
	private static final String STORE_CODE = "store_code";
	private static final String USERID = "userid";
	private static final Locale LOCALE = Locale.CANADA;

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	@Mock
	private ResourceOperationContext mockResourceOperationContext;
	@Mock
	private ItemRepository mockItemRepository;
	@Mock
	private SkuOptionTransformer mockSkuOptionTransformer;
	@Mock
	private SkuOptionValueTransformer mockSkuOptionValueTransformer;

	@InjectMocks
	private ItemDefinitionOptionLookupStrategyImpl strategy;

	/**
	 * Initialise common mock classes.
	 */
	@Before
	public void setUp() {
		Subject subject = TestSubjectFactory.createWithScopeAndUserIdAndLocale(STORE_CODE, USERID, LOCALE);
		when(mockResourceOperationContext.getSubject()).thenReturn(subject);
	}


	/**
	 * Test get option IDs for an item definition.
	 */
	@Test
	public void testReadOptionIdsForItemDefinition() {
		SkuOption mockSkuOption = mock(SkuOption.class);

		when(mockItemRepository.getSkuOptionsForItemId(ITEM_ID))
				.thenReturn(ExecutionResultFactory.createReadOK(Collections.singleton(mockSkuOption)));
		when(mockSkuOption.getGuid()).thenReturn(OPTION_GUID);

		ExecutionResult<Collection<String>> result = strategy.findOptionIds(STORE_CODE, ITEM_ID);

		assertTrue(result.isSuccessful());
		assertTrue(CollectionUtil.containsOnly(Collections.singletonList(OPTION_GUID), result.getData()));
	}

	/**
	 * Test get option IDs for an item definition when no options are found.
	 */
	@Test
	public void testReadOptionIdsForItemDefinitionWhenNoOptionsFound() {
		when(mockItemRepository.getSkuOptionsForItemId(ITEM_ID))
				.thenReturn(ExecutionResultFactory.createReadOK(Collections.emptySet()));

		ExecutionResult<Collection<String>> result = strategy.findOptionIds(STORE_CODE, ITEM_ID);

		assertTrue(result.isSuccessful());
		assertTrue(result.getData().isEmpty());
	}


	/**
	 * Test read item definition option.
	 */
	@Test
	public void testReadItemDefinitionOption() {
		ProductSku productSku = createProductSku();
		SkuOptionValue skuOptionValue = new SkuOptionValueImpl();
		Map<String, SkuOptionValue> optionValueMap = new HashMap<>();
		optionValueMap.put(OPTION_CODE, skuOptionValue);
		productSku.setOptionValueMap(optionValueMap);
		ItemDefinitionOptionEntity itemDefinitionOptionEntity = ItemDefinitionOptionEntity.builder().build();

		mockItemRepositoryGetProductSkuResult(ExecutionResultFactory.createReadOK(productSku));
		when(mockSkuOptionTransformer.transformToEntity(skuOptionValue, LOCALE)).thenReturn(itemDefinitionOptionEntity);

		ExecutionResult<ItemDefinitionOptionEntity> optionResult = strategy.findOption(STORE_CODE, ITEM_ID, OPTION_CODE);

		assertTrue(optionResult.isSuccessful());
		assertEquals(itemDefinitionOptionEntity, optionResult.getData());
	}

	/**
	 * Test read item definition option when SKU not found.
	 */
	@Test
	public void testReadItemDefinitionOptionWhenSkuNotFound() {
		mockItemRepositoryGetProductSkuResult(ExecutionResultFactory.createNotFound(StringUtils.EMPTY));

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		strategy.findOption(STORE_CODE, ITEM_ID, OPTION_CODE);
	}

	/**
	 * Test read item definition option when option not found.
	 */
	@Test
	public void testReadItemDefinitionOptionWhenOptionNotFound() {
		ProductSku productSku = new ProductSkuImpl();
		Map<String, SkuOptionValue> optionValueMap = new HashMap<>();
		productSku.setOptionValueMap(optionValueMap);

		mockItemRepositoryGetProductSkuResult(ExecutionResultFactory.createReadOK(productSku));

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		strategy.findOption(STORE_CODE, ITEM_ID, OPTION_CODE);
	}

	/**
	 * Test read option value.
	 */
	@Test
	public void testReadOptionValue() {
		ProductSku mockProductSku = mock(ProductSku.class);
		Product mockProduct = mock(Product.class);
		ProductType mockProductType = mock(ProductType.class);
		SkuOption mockSkuOption = mock(SkuOption.class);
		SkuOptionValue mockSkuOptionValue = mock(SkuOptionValue.class);
		ItemDefinitionOptionValueEntity optionValueDto = ItemDefinitionOptionValueEntity.builder()
				.withName("name")
				.withDisplayName("display name")
				.build();

		mockItemRepositoryGetProductSkuResult(ExecutionResultFactory.createReadOK(mockProductSku));
		when(mockProductSku.getProduct()).thenReturn(mockProduct);
		when(mockProduct.getProductType()).thenReturn(mockProductType);
		when(mockProductType.getSkuOptions()).thenReturn(Collections.singleton(mockSkuOption));
		when(mockSkuOption.getOptionKey()).thenReturn(OPTION_CODE);
		when(mockSkuOption.getOptionValue(VALUE_CODE)).thenReturn(mockSkuOptionValue);
		when(mockSkuOptionValueTransformer.transformToEntity(mockSkuOptionValue, LOCALE)).thenReturn(optionValueDto);

		ExecutionResult<ItemDefinitionOptionValueEntity> optionValueResult =
				strategy.findOptionValue(STORE_CODE, ITEM_ID, OPTION_CODE, VALUE_CODE);

		assertTrue(optionValueResult.isSuccessful());
		assertEquals(optionValueDto, optionValueResult.getData());
	}

	/**
	 * Test read option value when SKU not found.
	 */
	@Test
	public void testReadOptionValueWhenSkuNotFound() {
		mockItemRepositoryGetProductSkuResult(ExecutionResultFactory.createNotFound(StringUtils.EMPTY));

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		strategy.findOptionValue(STORE_CODE, ITEM_ID, OPTION_CODE, VALUE_CODE);
	}

	/**
	 * test find options ids when sku code not found.
	 */
	@Test
	public void testFindOptionsIdsWhenSkuCodeResultNotFound() {
		when(mockItemRepository.getSkuOptionsForItemId(INVALID_ITEM_ID))
				.thenReturn(ExecutionResultFactory.createNotFound());

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		strategy.findOptionIds(STORE_CODE, INVALID_ITEM_ID);
	}

	/**
	 * Test find option value when dto is null.
	 */
	@Test
	public void testFindOptionValueWhenDtoIsNull() {
		ProductSku mockProductSku = mock(ProductSku.class);
		Product mockProduct = mock(Product.class);
		ProductType mockProductType = mock(ProductType.class);
		SkuOption mockSkuOption = mock(SkuOption.class);
		SkuOptionValue mockSkuOptionValue = mock(SkuOptionValue.class);

		mockItemRepositoryGetProductSkuResult(ExecutionResultFactory.createReadOK(mockProductSku));
		when(mockProductSku.getProduct()).thenReturn(mockProduct);
		when(mockProduct.getProductType()).thenReturn(mockProductType);
		when(mockProductType.getSkuOptions()).thenReturn(Collections.singleton(mockSkuOption));
		when(mockSkuOption.getOptionKey()).thenReturn(OPTION_CODE);
		when(mockSkuOption.getOptionValue(VALUE_CODE)).thenReturn(mockSkuOptionValue);
		when(mockSkuOptionValueTransformer.transformToEntity(mockSkuOptionValue, LOCALE)).thenReturn(null);

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		strategy.findOptionValue(STORE_CODE, ITEM_ID, OPTION_CODE, VALUE_CODE);
	}

	private ProductSku createProductSku() {
		ProductSku productSku = new ProductSkuImpl();
		Product product = new ProductImpl();
		product.setGuid(PRODUCT_GUID);
		productSku.setProduct(product);
		return productSku;
	}

	private void mockItemRepositoryGetProductSkuResult(final ExecutionResult<ProductSku> result) {
		when(mockItemRepository.getSkuForItemId(ITEM_ID)).thenReturn(result);
	}
}
