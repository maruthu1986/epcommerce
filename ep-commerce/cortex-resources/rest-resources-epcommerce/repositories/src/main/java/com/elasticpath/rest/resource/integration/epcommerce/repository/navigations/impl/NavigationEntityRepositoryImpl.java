/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.navigations.impl;

import java.util.List;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.Single;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.domain.attribute.AttributeGroup;
import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.attribute.AttributeValueGroup;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.base.DetailsEntity;
import com.elasticpath.rest.definition.navigations.NavigationEntity;
import com.elasticpath.rest.definition.navigations.NavigationIdentifier;
import com.elasticpath.rest.identity.util.SubjectUtil;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.category.CategoryRepository;
import com.elasticpath.rest.resource.integration.epcommerce.transform.AttributeValueTransformer;

/**
 * Repository that creates navigation entity given navigation identifier.
 * @param <E> Navigation entity
 * @param <I> Navigation identifier
 */
@Component
public class NavigationEntityRepositoryImpl<E extends NavigationEntity, I extends NavigationIdentifier>
		implements Repository<NavigationEntity, NavigationIdentifier> {

	private AttributeValueTransformer attributeValueTransformer;
	private CategoryRepository categoryRepository;
	private ResourceOperationContext resourceOperationContext;

	@Override
	public Single<NavigationEntity> findOne(final NavigationIdentifier identifier) {
		String storeCode = identifier.getNavigations().getScope().getValue();
		String categoryGuid = identifier.getNodeId().getValue();
		Locale locale = SubjectUtil.getLocale(resourceOperationContext.getSubject());

		return categoryRepository.findByStoreAndCategoryCode(storeCode, categoryGuid)
				.flatMap(category -> getAttributeValues(category, locale)
						.flatMap(attributeValue -> getDetailsEntity(attributeValue, locale))
						.toList()
						.map(listOfDetails -> buildNavigationEntity(categoryGuid, locale, category, listOfDetails))
				);
	}

	/**
	 * Get attribute values for the category.
	 *
	 * @param category category
	 * @param locale locale
	 * @return observable of the attribute values
	 */
	protected Observable<AttributeValue> getAttributeValues(final Category category, final Locale locale) {
		AttributeGroup attributeGroup = category.getCategoryType().getAttributeGroup();
		AttributeValueGroup attributeValueGroup = category.getAttributeValueGroup();
		List<AttributeValue> attributeValues = attributeValueGroup.getAttributeValues(attributeGroup, locale);
		if (attributeValues == null) {
			return Observable.empty();
		}
		return Observable.fromIterable(attributeValues);
	}

	/**
	 * Get details entity for the attribute value.
	 *
	 * @param attributeValue attribute value
	 * @param locale locale
	 * @return details entity
	 */
	private Observable<DetailsEntity> getDetailsEntity(final AttributeValue attributeValue, final Locale locale) {
		DetailsEntity detailsEntity = attributeValueTransformer.transformToEntity(attributeValue, locale);
		if (detailsEntity == null) {
			return Observable.empty();
		}
		return Observable.just(detailsEntity);
	}

	/**
	 * Construct navigation entity.
	 *
	 * @param categoryGuid guid
	 * @param locale locale
	 * @param category category
	 * @param details list of entity details
	 * @return navigation entity
	 */
	protected NavigationEntity buildNavigationEntity(final String categoryGuid, final Locale locale,
													 final Category category, final List<DetailsEntity> details) {
		NavigationEntity.Builder builder = NavigationEntity.builder()
				.withDisplayName(category.getDisplayName(locale))
				.withName(category.getCode())
				.withNodeId(categoryGuid);
		//Include details field only if there are any
		if (!details.isEmpty()) {
			builder.withDetails(details);
		}
		return builder.build();
	}

	@Reference
	public void setAttributeValueTransformer(final AttributeValueTransformer attributeValueTransformer) {
		this.attributeValueTransformer = attributeValueTransformer;
	}

	@Reference
	public void setCategoryRepository(final CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}

	@Reference
	public void setResourceOperationContext(final ResourceOperationContext resourceOperationContext) {
		this.resourceOperationContext = resourceOperationContext;
	}
}
