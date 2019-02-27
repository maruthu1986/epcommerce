/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemdefinitions.integration.epcommerce.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Locale;


import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.apache.commons.lang3.StringUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.itemdefinitions.ItemDefinitionEntity;
import com.elasticpath.rest.identity.Subject;
import com.elasticpath.rest.identity.TestSubjectFactory;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;
import com.elasticpath.rest.resource.itemdefinitions.integration.epcommerce.core.domain.wrapper.ProductSkuWithConfiguration;
import com.elasticpath.rest.resource.itemdefinitions.integration.epcommerce.transform.ProductSkuWithConfigurationTransformer;

/**
 * Test class for {@link ItemDefinitionLookupStrategyImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ItemDefinitionLookupStrategyImplTest {

	private static final String ITEM_DISPLAY_NAME = "item_display_name";
	private static final String SKU_CODE = "sku_code";
	private static final String PRODUCT_GUID = "product_guid";
	private static final String ITEM_ID = "item_id";
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
	private ProductSkuWithConfigurationTransformer mockProductSkuWithConfigurationTransformer;

	@InjectMocks
	private ItemDefinitionLookupStrategyImpl strategy;

	/**
	 * Initialise common mock classes.
	 */
	@Before
	public void setUp() {
		Subject subject = TestSubjectFactory.createWithScopeAndUserIdAndLocale(STORE_CODE, USERID, LOCALE);
		when(mockResourceOperationContext.getSubject()).thenReturn(subject);
	}

	/**
	 * Test read item definition.
	 */
	@Test
	public void testReadItemDefinition() {
		ProductSku productSku = createProductSku();
		ItemDefinitionEntity itemDefinitionEntity = ItemDefinitionEntity.builder()
				.withDisplayName(ITEM_DISPLAY_NAME)
				.build();
		ProductSkuWithConfiguration expectedProductSkuWithConfiguration = new ProductSkuWithConfiguration(productSku, ITEM_ID);

		mockItemRepositoryGetProductSkuResult(ExecutionResultFactory.createReadOK(productSku));
		when(mockProductSkuWithConfigurationTransformer.transformToEntity(expectedProductSkuWithConfiguration, LOCALE))
				.thenReturn(itemDefinitionEntity);

		ExecutionResult<ItemDefinitionEntity> result = strategy.find(STORE_CODE, ITEM_ID);

		assertTrue(result.isSuccessful());
		assertEquals(itemDefinitionEntity, result.getData());
	}

	/**
	 * Test read item definition when sku not found.
	 */
	@Test
	public void testReadItemDefinitionWhenSkuNotFound() {
		mockItemRepositoryGetProductSkuResult(ExecutionResultFactory.<ProductSku>createNotFound(StringUtils.EMPTY));

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		strategy.find(STORE_CODE, ITEM_ID);
	}

	private ProductSku createProductSku() {
		ProductSku productSku = new ProductSkuImpl();
		productSku.setSkuCode(SKU_CODE);
		Product product = new ProductImpl();
		product.setGuid(PRODUCT_GUID);
		productSku.setProduct(product);
		return productSku;
	}

	private void mockItemRepositoryGetProductSkuResult(final ExecutionResult<ProductSku> result) {
		when(mockItemRepository.getSkuForItemId(ITEM_ID)).thenReturn(result);
	}

}
