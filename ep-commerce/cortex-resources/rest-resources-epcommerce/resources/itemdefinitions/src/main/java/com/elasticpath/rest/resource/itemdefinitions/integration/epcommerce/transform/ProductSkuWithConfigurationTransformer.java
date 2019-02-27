/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemdefinitions.integration.epcommerce.transform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.rest.definition.base.DetailsEntity;
import com.elasticpath.rest.definition.itemdefinitions.ItemDefinitionEntity;
import com.elasticpath.rest.resource.integration.epcommerce.transform.AttributeValueTransformer;
import com.elasticpath.rest.resource.itemdefinitions.integration.epcommerce.core.domain.wrapper.ProductSkuWithConfiguration;
import com.elasticpath.rest.resource.transform.AbstractDomainTransformer;
import com.elasticpath.rest.util.collection.CollectionUtil;

/**
 * Transforms a {@link ProductSkuWithConfiguration} to {@link ItemDefinitionEntity} and vice versa.
 */
@Singleton
@Named("productSkuWithConfigurationTransformer")
public class ProductSkuWithConfigurationTransformer extends AbstractDomainTransformer<ProductSkuWithConfiguration, ItemDefinitionEntity> {

	private final AttributeValueTransformer attributeValueTransformer;

	/**
	 * Instantiates a new item definition domain transformer.
	 *
	 * @param attributeValueTransformer the attribute value transformer
	 */
	@Inject
	ProductSkuWithConfigurationTransformer(
			@Named("attributeValueTransformer")
			final AttributeValueTransformer attributeValueTransformer) {

		this.attributeValueTransformer = attributeValueTransformer;
	}

	@Override
	public ProductSkuWithConfiguration transformToDomain(final ItemDefinitionEntity resourceEntity, final Locale locale) {
		throw new UnsupportedOperationException("This operation is not implemented.");
	}

	@Override
	public ItemDefinitionEntity transformToEntity(final ProductSkuWithConfiguration productSkuWithConfiguration, final Locale locale) {
		ProductSku productSku = productSkuWithConfiguration.getProductSku();
		Product product = productSku.getProduct();
		Collection<AttributeValue> skuAttributeValues = productSku.getFullAttributeValues(locale);
		Collection<AttributeValue> productAttributesValues = product.getFullAttributeValues(locale);
		Collection<AttributeValue> attributeValues = new ArrayList<>(skuAttributeValues.size() + productAttributesValues.size());
		attributeValues.addAll(skuAttributeValues);
		attributeValues.addAll(productAttributesValues);

		Collection<DetailsEntity> attributes = createDetailsEntities(attributeValues, locale);
		return ItemDefinitionEntity.builder()
				.withItemId(productSkuWithConfiguration.getConfigurationCode())
				.withDisplayName(product.getDisplayName(locale))
				.withDetails(attributes)
				.build();
	}

	private Collection<DetailsEntity> createDetailsEntities(final Collection<AttributeValue> attributeValues, final Locale locale) {
		//returns null to void out the attributes field of the entity
		if (CollectionUtil.isEmpty(attributeValues)) {
			return null;
		}

		Collection<DetailsEntity> attributes = new ArrayList<>(attributeValues.size());

		for (AttributeValue attributeValue : attributeValues) {
			DetailsEntity detailsEntity = attributeValueTransformer.transformToEntity(attributeValue, locale);
			if (detailsEntity != null) {
				attributes.add(detailsEntity);
			}
		}

		return attributes;
	}
}
