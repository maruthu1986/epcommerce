/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemdefinitions.integration.epcommerce.transform;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeMultiValueType;
import com.elasticpath.domain.attribute.AttributeType;
import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.attribute.impl.AttributeImpl;
import com.elasticpath.domain.attribute.impl.ProductAttributeValueImpl;
import com.elasticpath.domain.attribute.impl.SkuAttributeValueImpl;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.jmock.MockeryFactory;
import com.elasticpath.rest.definition.base.DetailsEntity;
import com.elasticpath.rest.definition.itemdefinitions.ItemDefinitionEntity;
import com.elasticpath.rest.resource.integration.epcommerce.transform.AttributeValueTransformer;
import com.elasticpath.rest.resource.itemdefinitions.integration.epcommerce.core.domain.wrapper.ProductSkuWithConfiguration;

/**
 * Test class for {@link ProductSkuWithConfigurationTransformer}.
 */
public class ProductSkuWithConfigurationTransformerTest {

	private static final String PROD_KEY = "PROD_KEY";
	private static final String PROD_NAME = "PROD_NAME";
	private static final String SKU_KEY = "SKU_KEY";
	private static final String SKU_NAME = "SKU_NAME";
	private static final String CONFIGURATION_CODE = "CONFIGURATION_CODE";
	private static final String DISPLAY_NAME = "DISPLAY_NAME";
	private static final Locale LOCALE_CA = Locale.CANADA;

	@Rule
	public final JUnitRuleMockery context = MockeryFactory.newRuleInstance();

	@Mock
	private AttributeValueTransformer mockAttributeValueTransformer;
	@Mock
	private ProductSku productSku;
	@Mock
	private Product product;

	private ProductSkuWithConfigurationTransformer productSkuWithConfigurationTransformer;


	@Before
	public void setUp() {
		productSkuWithConfigurationTransformer = new ProductSkuWithConfigurationTransformer(mockAttributeValueTransformer);
	}

	/**
	 * Test transform to domain.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void testTransformToDomain() {
		productSkuWithConfigurationTransformer.transformToDomain(null);
	}

	/**
	 * Test transform to entity with no attribute values.
	 */
	@Test
	public void testTransformToEntityWithNoAttributeValues() {
		ProductSkuWithConfiguration productSkuWrapper = createMockProductSkuWithConfiguration(DISPLAY_NAME,
				Collections.<AttributeValue>emptyList(),
				Collections.<AttributeValue>emptyList());

		ItemDefinitionEntity expectedItemDefinitionEntity = createItemDefinitionEntity(CONFIGURATION_CODE, DISPLAY_NAME, null);

		ItemDefinitionEntity itemDefinitionEntity = productSkuWithConfigurationTransformer.transformToEntity(productSkuWrapper, LOCALE_CA);

		assertEquals("The item definition dto should be the same.", expectedItemDefinitionEntity, itemDefinitionEntity);
	}

	/**
	 * Test transform from ProductSku to {@link ItemDefinitionEntity}.
	 */
	@Test
	public void testTransformToEntityWithValidSkuAndProductAttributeValues() {
		AttributeValue skuAttributeValue = createSkuAttributeValue(SKU_NAME, SKU_KEY, false, AttributeType.LONG_TEXT, "sku");
		AttributeValue productAttributeValue = createProductAttributeValue(PROD_NAME, PROD_KEY, false, AttributeType.LONG_TEXT, "product");

		ProductSkuWithConfiguration productSkuWrapper = createMockProductSkuWithConfiguration(DISPLAY_NAME,
				Collections.singletonList(skuAttributeValue),
				Collections.singletonList(productAttributeValue));

		DetailsEntity skuDetailsEntity = createDetailsEntity(SKU_KEY, "sku", DISPLAY_NAME, "sku");
		DetailsEntity productDetailsEntity = createDetailsEntity(PROD_KEY, "product", DISPLAY_NAME, "product");

		Collection<DetailsEntity> detailsEntities = createDetailsEntities(skuDetailsEntity, productDetailsEntity);

		ItemDefinitionEntity expectedItemDefinitionEntity = createItemDefinitionEntity(CONFIGURATION_CODE, DISPLAY_NAME, detailsEntities);

		shouldTransformToEntityWithResult(skuAttributeValue, skuDetailsEntity);
		shouldTransformToEntityWithResult(productAttributeValue, productDetailsEntity);

		ItemDefinitionEntity itemDefinitionEntity = productSkuWithConfigurationTransformer.transformToEntity(productSkuWrapper, LOCALE_CA);

		assertEquals("The item definition dto should be the same.", expectedItemDefinitionEntity, itemDefinitionEntity);
	}

	/**
	 * Test transform to entity with no valid attribute values.
	 */
	@Test
	public void testTransformToEntityWithNoValidAttributeValues() {
		AttributeValue skuAttributeValue = createSkuAttributeValue(SKU_NAME, SKU_KEY, false, AttributeType.DECIMAL, null);

		ProductSkuWithConfiguration productSkuWrapper = createMockProductSkuWithConfiguration(DISPLAY_NAME,
				Collections.singletonList(skuAttributeValue),
				Collections.<AttributeValue>emptyList());

		ItemDefinitionEntity expectedItemDefinitionEntity = createItemDefinitionEntity(CONFIGURATION_CODE,
				DISPLAY_NAME,
				Collections.<DetailsEntity>emptyList());

		shouldTransformToEntityWithResult(skuAttributeValue, null);

		ItemDefinitionEntity itemDefinitionEntity = productSkuWithConfigurationTransformer.transformToEntity(productSkuWrapper, LOCALE_CA);

		assertEquals("The item definition dto should be the same.", expectedItemDefinitionEntity, itemDefinitionEntity);
	}

	private void shouldTransformToEntityWithResult(final AttributeValue attributeValue, final DetailsEntity result) {
		context.checking(new Expectations() {
			{
				oneOf(mockAttributeValueTransformer).transformToEntity(attributeValue, LOCALE_CA);
				will(returnValue(result));
			}
		});
	}

	private ProductSkuWithConfiguration createMockProductSkuWithConfiguration(final String displayName,
			final Collection<AttributeValue> skuAttributeValues,
			final Collection<AttributeValue> productAttributeValues) {

		context.checking(new Expectations() {
			{
				oneOf(productSku).getProduct();
				will(returnValue(product));

				oneOf(productSku).getFullAttributeValues(LOCALE_CA);
				will(returnValue(skuAttributeValues));

				oneOf(product).getFullAttributeValues(LOCALE_CA);
				will(returnValue(productAttributeValues));

				oneOf(product).getDisplayName(LOCALE_CA);
				will(returnValue(displayName));
			}
		});

		return new ProductSkuWithConfiguration(productSku, CONFIGURATION_CODE);
	}

	private Collection<DetailsEntity> createDetailsEntities(final DetailsEntity... detailsEntities) {
		Collection<DetailsEntity> result = new ArrayList<>();
		Collections.addAll(result, detailsEntities);
		return result;
	}

	private ItemDefinitionEntity createItemDefinitionEntity(final String configurationCode,
			final String displayName,
			final Collection<DetailsEntity> attributes) {

		return ItemDefinitionEntity.builder()
				.withItemId(configurationCode)
				.withDisplayName(displayName)
				.withDetails(attributes)
				.build();
	}

	private DetailsEntity createDetailsEntity(final String name, final Object value, final String displayName, final String displayValue) {

		return DetailsEntity.builder()
				.withName(name)
				.withValue(value)
				.withDisplayName(displayName)
				.withDisplayValue(displayValue)
				.build();
	}

	private AttributeValue createProductAttributeValue(final String attributeName,
			final String attributeKey,
			final boolean attributeMultiValueEnabled,
			final AttributeType attributeValueAttributeType,
			final Object attributeValueValue) {

		Attribute attribute = createAttribute(attributeName, attributeKey, attributeMultiValueEnabled);
		return createProductAttributeValue(attributeValueAttributeType, attributeValueValue, attribute);
	}

	private AttributeValue createSkuAttributeValue(final String attributeName,
			final String attributeKey,
			final boolean attributeMultiValueEnabled,
			final AttributeType attributeValueAttributeType,
			final Object attributeValueValue) {

		Attribute attribute = createAttribute(attributeName, attributeKey, attributeMultiValueEnabled);
		return createSkuAttributeValue(attributeValueAttributeType, attributeValueValue, attribute);
	}

	private Attribute createAttribute(final String name, final String key, final boolean multiValueEnabled) {
		Attribute attribute = new AttributeImpl();

		attribute.setName(name);
		attribute.setKey(key);
		attribute.setMultiValueType(AttributeMultiValueType.createAttributeMultiValueType(String.valueOf(multiValueEnabled)));

		return attribute;
	}

	private AttributeValue createProductAttributeValue(final AttributeType attributeType, final Object expectedValue, final Attribute attribute) {
		AttributeValue attributeValue = new ProductAttributeValueImpl();

		attributeValue.setAttributeType(attributeType);
		attributeValue.setAttribute(attribute);
		attributeValue.setValue(expectedValue);

		return attributeValue;
	}

	private AttributeValue createSkuAttributeValue(final AttributeType attributeType, final Object expectedValue, final Attribute attribute) {
		AttributeValue attributeValue = new SkuAttributeValueImpl();

		attributeValue.setAttributeType(attributeType);
		attributeValue.setAttribute(attribute);
		attributeValue.setValue(expectedValue);

		return attributeValue;
	}
}
