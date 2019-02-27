/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemdefinitions.integration.epcommerce.transform;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeMultiValueType;
import com.elasticpath.domain.attribute.AttributeType;
import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.attribute.impl.AttributeImpl;
import com.elasticpath.domain.attribute.impl.ProductAttributeValueImpl;
import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.ConstituentItem;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.jmock.MockeryFactory;
import com.elasticpath.rest.definition.base.DetailsEntity;
import com.elasticpath.rest.definition.itemdefinitions.ItemDefinitionComponentEntity;
import com.elasticpath.rest.resource.integration.epcommerce.transform.AttributeValueTransformer;
import com.elasticpath.rest.resource.itemdefinitions.integration.epcommerce.core.domain.wrapper.BundleConstituentWithAttributesWrapper;

/**
 * Test class for {@link BundleConstituentWithAttributesTransformer}.
 */
public class BundleConstituentWithAttributesTransformerTest {

	private static final String NAME = "NAME";
	private static final String KEY = "KEY";
	private static final double EXPECTED_DOUBLE = 7.40;
	private static final String EXPECTED_DOUBLE_STRING_VALUE = "7.40";
	private static final Integer QUANTITY = 3;
	private static final String EXPECTED_PRODUCT_NAME = "name";
	private static final String EXPECTED_STANDALONE_ID = "id13";

	@Rule
	public final JUnitRuleMockery context = MockeryFactory.newRuleInstance();

	private final AttributeValueTransformer mockAttributeValueTransformer = context.mock(AttributeValueTransformer.class);
	private final BundleConstituentWithAttributesTransformer transformer =
			new BundleConstituentWithAttributesTransformer(mockAttributeValueTransformer);
	private final Product product = context.mock(Product.class);

	/**
	 * Test transform to domain.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void testTransformToDomain() {
		transformer.transformToDomain(null);
	}

	/**
	 * Tests the transform to entity with no attribute values.
	 */
	@Test
	public void testTransformToEntityWithNoAttributeValues() {
		ItemDefinitionComponentEntity expectedEntity = createItemDefinitionComponentEntity(EXPECTED_PRODUCT_NAME,
				QUANTITY,
				EXPECTED_STANDALONE_ID,
				Collections.<DetailsEntity>emptySet());

		BundleConstituentWithAttributesWrapper bundleConstituentWithAttributesWrapper =
				createMockBundleConstituentWithAttributesWrapper(new HashSet<AttributeValue>());

		ItemDefinitionComponentEntity actualEntity = transformer.transformToEntity(bundleConstituentWithAttributesWrapper, Locale.ENGLISH);

		assertItemDefinitionComponentEntityEquals(expectedEntity, actualEntity);
	}

	/**
	 * Test transform to entity with a valid attribute value.
	 */
	@Test
	public void testTransformToEntityWithValidAttributeValue() {
		BigDecimal expectedBigDecimal = BigDecimal.valueOf(EXPECTED_DOUBLE);
		final DetailsEntity expectedDetailsEntity = createDetailsEntity(KEY, expectedBigDecimal, NAME, EXPECTED_DOUBLE_STRING_VALUE);

		ItemDefinitionComponentEntity expectedEntity = createItemDefinitionComponentEntity(EXPECTED_PRODUCT_NAME,
				QUANTITY,
				EXPECTED_STANDALONE_ID,
				Collections.singletonList(expectedDetailsEntity));

		final AttributeValue attributeValue = createAttributeValue(NAME, KEY, false, AttributeType.DECIMAL, expectedBigDecimal);

		BundleConstituentWithAttributesWrapper bundleConstituentWithAttributesWrapper =
				createMockBundleConstituentWithAttributesWrapper(Collections.singleton(attributeValue));

		shouldTransformToEntityWithResult(attributeValue, expectedDetailsEntity);

		ItemDefinitionComponentEntity actualEntity = transformer.transformToEntity(bundleConstituentWithAttributesWrapper, Locale.ENGLISH);

		assertItemDefinitionComponentEntityEquals(expectedEntity, actualEntity);
	}

	/**
	 * Test transform to entity with no valid attribute values.
	 */
	@Test
	public void testTransformToEntityWithNoValidAttributeValues() {
		ItemDefinitionComponentEntity expectedEntity = createItemDefinitionComponentEntity(EXPECTED_PRODUCT_NAME,
				QUANTITY,
				EXPECTED_STANDALONE_ID,
				Collections.<DetailsEntity>emptyList());

		final AttributeValue attributeValue = createAttributeValue(NAME, KEY, false, AttributeType.DECIMAL, null);

		BundleConstituentWithAttributesWrapper bundleConstituentWithAttributesWrapper =
				createMockBundleConstituentWithAttributesWrapper(Collections.singleton(attributeValue));

		shouldTransformToEntityWithResult(attributeValue, null);

		ItemDefinitionComponentEntity actualEntity = transformer.transformToEntity(bundleConstituentWithAttributesWrapper, Locale.ENGLISH);

		assertItemDefinitionComponentEntityEquals(expectedEntity, actualEntity);
	}

	private void shouldTransformToEntityWithResult(final AttributeValue attributeValue, final DetailsEntity detailsEntity) {
		context.checking(new Expectations() {
			{
				oneOf(mockAttributeValueTransformer).transformToEntity(attributeValue, Locale.ENGLISH);
				will(returnValue(detailsEntity));
			}
		});
	}

	private BundleConstituentWithAttributesWrapper createMockBundleConstituentWithAttributesWrapper(
			final Collection<AttributeValue> attributeValues) {

		BundleConstituentWithAttributesWrapper wrapper = new BundleConstituentWithAttributesWrapper();

		final BundleConstituent bundleConstituent = context.mock(BundleConstituent.class);
		final ConstituentItem constituentItem = context.mock(ConstituentItem.class);

		context.checking(new Expectations() {
			{
				oneOf(bundleConstituent).getQuantity();
				will(returnValue(QUANTITY));

				allowing(bundleConstituent).getConstituent();
				will(returnValue(constituentItem));

				oneOf(constituentItem).getProduct();
				will(returnValue(product));

				oneOf(product).getDisplayName(Locale.ENGLISH);
				will(returnValue(EXPECTED_PRODUCT_NAME));
			}
		});

		wrapper.setAttributes(attributeValues);
		wrapper.setBundleConstituent(bundleConstituent);
		wrapper.setStandaloneItemId(EXPECTED_STANDALONE_ID);

		return wrapper;
	}

	private ItemDefinitionComponentEntity createItemDefinitionComponentEntity(final String displayName,
			final int quantity,
			final String itemId,
			final Collection<DetailsEntity> detailsEntities) {

		ItemDefinitionComponentEntity entity = ItemDefinitionComponentEntity.builder()
				.withDisplayName(displayName)
				.withQuantity(quantity)
				.withStandaloneItemId(itemId)
				.withDetails(detailsEntities)
				.build();

		return entity;
	}

	private DetailsEntity createDetailsEntity(final String name, final Object value, final String displayName, final String displayValue) {

		return DetailsEntity.builder()
				.withName(name)
				.withValue(value)
				.withDisplayName(displayName)
				.withDisplayValue(displayValue)
				.build();
	}

	private AttributeValue createAttributeValue(final String attributeName,
			final String attributeKey,
			final boolean attributeMultiValueEnabled,
			final AttributeType attributeValueAttributeType,
			final Object attributeValueValue) {

		Attribute attribute = createAttribute(attributeName, attributeKey, attributeMultiValueEnabled);
		return createAttributeValue(attributeValueAttributeType, attributeValueValue, attribute);
	}

	private Attribute createAttribute(final String name, final String key, final boolean multiValueEnabled) {
		Attribute attribute = new AttributeImpl();

		attribute.setName(name);
		attribute.setKey(key);
		attribute.setMultiValueType(AttributeMultiValueType.createAttributeMultiValueType(String.valueOf(multiValueEnabled)));

		return attribute;
	}

	private AttributeValue createAttributeValue(final AttributeType attributeType, final Object expectedValue, final Attribute attribute) {
		AttributeValue attributeValue = new ProductAttributeValueImpl();

		attributeValue.setAttributeType(attributeType);
		attributeValue.setAttribute(attribute);
		attributeValue.setValue(expectedValue);

		return attributeValue;
	}

	private void assertItemDefinitionComponentEntityEquals(final ItemDefinitionComponentEntity expected, final ItemDefinitionComponentEntity actual) {
		assertEquals("The item details should be the same.", expected.getDetails(), actual.getDetails());
		assertEquals("The item quantity should be the same.", QUANTITY, actual.getQuantity());
		assertEquals("The item product name should be the same.", EXPECTED_PRODUCT_NAME, actual.getDisplayName());
		assertEquals("The standalone item correlation ID should be the same.", EXPECTED_STANDALONE_ID, actual.getStandaloneItemId());
	}
}
