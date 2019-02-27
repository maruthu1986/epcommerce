/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemdefinitions.integration.epcommerce.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.apache.commons.lang3.StringUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.ConstituentItem;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.itemdefinitions.ItemDefinitionComponentEntity;
import com.elasticpath.rest.identity.Subject;
import com.elasticpath.rest.identity.TestSubjectFactory;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;
import com.elasticpath.rest.resource.itemdefinitions.integration.epcommerce.core.domain.wrapper.BundleConstituentWithAttributesWrapper;
import com.elasticpath.rest.resource.itemdefinitions.integration.epcommerce.transform.BundleConstituentWithAttributesTransformer;
import com.elasticpath.rest.util.collection.CollectionUtil;

/**
 * Test class for {@link ItemDefinitionComponentLookupStrategyImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ItemDefinitionComponentLookupStrategyImplTest {

	private static final String THE_RESULT_SHOULD_BE_SUCCESSFUL = "The result should be successful";
	private static final String ITEM_ID = "item_id";
	private static final String BUNDLE_CONSTITUENT_GUID = "bundle_constituent_guid";
	private static final String STANDALONE_CONSTITUENT_ITEM_ID = "standalone constituent item id";
	private static final String STORECODE = "store";
	private static final String USERID = "userid";
	private static final Locale TEST_LOCALE = Locale.CANADA;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private ProductSku mockProductSku;
	@Mock
	private Product mockProduct;
	@Mock
	private ProductBundle mockBundle;
	@Mock
	private BundleConstituent mockConstituent;
	@Mock
	private ConstituentItem mockConstituentItem;
	@Mock
	private BundleConstituentWithAttributesTransformer mockBundleConstituentTransformer;
	@Mock
	private ItemRepository mockItemRepository;
	@Mock
	private ResourceOperationContext mockResourceOperationContext;

	@InjectMocks
	private ItemDefinitionComponentLookupStrategyImpl strategy;

	/**
	 * Test find component id's when product sku result is successful.
	 */
	@Test
	public void testFindComponentIdsWhenProductSkuResultSuccessful() {
		mockSetUpProductAndProductBundle();
		mockItemConfigProductSku(ExecutionResultFactory.createReadOK(mockProductSku));
		when(mockBundle.getConstituents()).thenReturn(Collections.singletonList(mockConstituent));
		when(mockConstituent.getGuid()).thenReturn(BUNDLE_CONSTITUENT_GUID);
		when(mockItemRepository.asProductBundle(mockProduct)).thenReturn(mockBundle);

		ExecutionResult<Collection<String>> result = strategy.findComponentIds(STORECODE, ITEM_ID);
		assertTrue(THE_RESULT_SHOULD_BE_SUCCESSFUL, result.isSuccessful());
		assertTrue("The result should be the BUNDLE_CONSTITUENT_GUID as expected",
			CollectionUtil.containsOnly(Collections.singleton(BUNDLE_CONSTITUENT_GUID), result.getData()));
	}

	/**
	 * Test get component ids when no item is found.
	 */
	@Test
	public void testGetComponentIdsWhenNoItemFound() {
		when(mockItemRepository.getSkuForItemId(ITEM_ID))
				.thenReturn(ExecutionResultFactory.createNotFound(StringUtils.EMPTY));

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		strategy.findComponentIds(STORECODE, ITEM_ID);
	}

	/**
	 * Tests Find Component by Id When Nested Bundle Constituent Successful.
	 */
	@Test
	public void testFindComponentByIdWhenNestedBundleConstituentSuccessful() {
		final BundleConstituent mockBundleConstituent = mock(BundleConstituent.class);
		final ProductBundle mockProductBundle = mock(ProductBundle.class);
		final Product mockConstituentProduct = mock(Product.class);
		final ProductSku mockConstituentSku = mock(ProductSku.class);

		mockItemConfigProductSku(ExecutionResultFactory.createReadOK(mockProductSku));
		mockShopperExpectations();
		mockBundleTransformer();

		when(mockProductSku.getProduct()).thenReturn(mockProduct);
		when(mockItemRepository.asProductBundle(mockProduct)).thenReturn(mockBundle);
		when(mockBundle.getConstituents()).thenReturn(Collections.singletonList(mockBundleConstituent));
		when(mockBundleConstituent.getGuid()).thenReturn("Guid");
		when(mockBundleConstituent.getConstituent()).thenReturn(mockConstituentItem);
		when(mockConstituentItem.isBundle()).thenReturn(true);
		when(mockConstituentItem.getProduct()).thenReturn(mockConstituentProduct);
		when(mockItemRepository.asProductBundle(mockConstituentProduct)).thenReturn(mockProductBundle);
		when(mockProductBundle.getConstituents()).thenReturn(Collections.singletonList(mockConstituent));
		when(mockConstituent.getGuid()).thenReturn(BUNDLE_CONSTITUENT_GUID);
		when(mockConstituent.getConstituent()).thenReturn(mockConstituentItem);
		when(mockConstituentItem.getProductSku()).thenReturn(mockConstituentSku);
		when(mockItemRepository.getItemIdForSku(mockConstituentSku))
				.thenReturn(ExecutionResultFactory.createReadOK(STANDALONE_CONSTITUENT_ITEM_ID));
		when(mockConstituentProduct.getFullAttributeValues(Locale.CANADA)).thenReturn(null);
		when(mockConstituentItem.isProductSku()).thenReturn(false);

		ExecutionResult<ItemDefinitionComponentEntity> result = strategy.findComponentById(STORECODE, ITEM_ID,
				BUNDLE_CONSTITUENT_GUID);

		assertTrue(THE_RESULT_SHOULD_BE_SUCCESSFUL, result.isSuccessful());
	}

	@Test
	public void testFindComponentByIdWhenItemConfigResultSuccessful() {
		final Product mockConstituentProduct = mock(Product.class);
		final AttributeValue mockAttribute = mock(AttributeValue.class);
		final List<AttributeValue> shoppingItemConsituentList = Collections.singletonList(mockAttribute);

		mockShopperExpectations();
		mockSetUpProductAndProductBundle();
		mockBundleTransformer();
		mockItemConfigProductSku(ExecutionResultFactory.createReadOK(mockProductSku));
		mockItemConfigIdForProductAndSku(ExecutionResultFactory.createReadOK(ITEM_ID));
		when(mockConstituent.getConstituent()).thenReturn(mockConstituentItem);
		when(mockConstituentItem.getProduct()).thenReturn(mockConstituentProduct);
		when(mockConstituentItem.getProductSku()).thenReturn(mockProductSku);
		when(mockConstituentItem.isProductSku()).thenReturn(true);
		when(mockBundle.getConstituents()).thenReturn(Collections.singletonList(mockConstituent));
		when(mockConstituent.getGuid()).thenReturn(BUNDLE_CONSTITUENT_GUID);
		when(mockConstituentProduct.getFullAttributeValues(Locale.CANADA)).thenReturn(shoppingItemConsituentList);
		when(mockProductSku.getFullAttributeValues(Locale.CANADA)).thenReturn(Collections.emptyList());

		ExecutionResult<ItemDefinitionComponentEntity> result = strategy.findComponentById(STORECODE, ITEM_ID,
				BUNDLE_CONSTITUENT_GUID);

		assertTrue(THE_RESULT_SHOULD_BE_SUCCESSFUL, result.isSuccessful());
	}

	@Test
	public void testFindComponentByIdWhenBundleConstituentNotFound() {
		mockShopperExpectations();
		mockSetUpProductAndProductBundle();
		mockItemConfigProductSku(ExecutionResultFactory.createReadOK(mockProductSku));

		when(mockBundle.getConstituents()).thenReturn(Collections.emptyList());

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		strategy.findComponentById(STORECODE, ITEM_ID, BUNDLE_CONSTITUENT_GUID);
	}

	@Test
	public void testFindComponentByIdWhenProductSkuNotFound() {
		when(mockItemRepository.getSkuForItemId(ITEM_ID)).thenReturn(ExecutionResultFactory.createNotFound());

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		strategy.findComponentById(STORECODE, ITEM_ID, BUNDLE_CONSTITUENT_GUID);
	}

	@Test
	public void testHasComponentsWhenItemBundleExists() {
		when(mockItemRepository.isItemBundle(ITEM_ID)).thenReturn(ExecutionResultFactory.createReadOK(true));

		ExecutionResult<Boolean> result = strategy.hasComponents(STORECODE, ITEM_ID);

		assertTrue(THE_RESULT_SHOULD_BE_SUCCESSFUL, result.isSuccessful());
	}

	private void mockShopperExpectations() {
		Subject subject = TestSubjectFactory.createWithScopeAndUserIdAndLocale(STORECODE, USERID, TEST_LOCALE);
		when(mockResourceOperationContext.getSubject()).thenReturn(subject);
	}

	private void mockSetUpProductAndProductBundle() {
		when(mockProductSku.getProduct()).thenReturn(mockProduct);
		when(mockProductSku.getProduct()).thenReturn(mockProduct);
		when(mockProductSku.getProduct()).thenReturn(mockProduct);
		when(mockItemRepository.asProductBundle(mockProduct)).thenReturn(mockBundle);
	}

	private void mockItemConfigProductSku(final ExecutionResult<ProductSku> executionResult) {
		when(mockItemRepository.getSkuForItemId(ITEM_ID)).thenReturn(executionResult);
	}

	private void mockItemConfigIdForProductAndSku(final ExecutionResult<String> executionResult) {
		when(mockItemRepository.getItemIdForSku(mockProductSku)).thenReturn(executionResult);
	}
	private void mockBundleTransformer() {
		final ItemDefinitionComponentEntity entity = ItemDefinitionComponentEntity.builder().build();
		when(mockBundleConstituentTransformer.transformToEntity(any(BundleConstituentWithAttributesWrapper.class), any(Locale.class)))
				.thenReturn(entity);
	}
}
