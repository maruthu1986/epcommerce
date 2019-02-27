/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.transform.impl;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;

import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeType;
import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.attribute.AttributeValueWithType;
import com.elasticpath.persistence.api.AbstractPersistableImpl;
import com.elasticpath.rest.definition.base.DetailsEntity;
import com.elasticpath.rest.resource.integration.epcommerce.transform.AttributeValueTransformer;
import com.elasticpath.rest.resource.transform.AbstractDomainTransformer;
import com.elasticpath.rest.util.date.DateUtil;

/**
 * Transforms a {@link AttributeValue} into a {@link DetailsEntity}, and vice-versa.
 */
@Component(property = AbstractDomainTransformer.DS_SERVICE_RANKING)
public class AttributeValueTransformerImpl extends
		AbstractDomainTransformer<AttributeValue, DetailsEntity> implements AttributeValueTransformer {

	private static final Collection<AttributeType> UNSUPPORTED_ATTRIBUTE_TYPES = Arrays.asList(AttributeType.FILE, AttributeType.IMAGE);

	@Override
	public AttributeValue transformToDomain(final DetailsEntity resourceEntity, final Locale locale) {
		throw new UnsupportedOperationException("This operation is not implemented.");
	}

	@Override
	public DetailsEntity transformToEntity(final AttributeValue attributeValue, final Locale locale) {
		final DetailsEntity detailsEntity;

		if (UNSUPPORTED_ATTRIBUTE_TYPES.contains(attributeValue.getAttributeType())) {
			detailsEntity = null;
		} else {
			Attribute domainAttribute = attributeValue.getAttribute();

			Object valueObject = determineValueObject(attributeValue);

			if (valueObject == null) {
				detailsEntity = null;
			} else {
				detailsEntity = DetailsEntity.builder()
						.withName(domainAttribute.getKey())
						.withValue(convertToProperValue(valueObject))
						.withDisplayName(domainAttribute.getName())
						.withDisplayValue(convertRawValueToDisplayValue(valueObject, attributeValue.getAttributeType(), locale)).build();

			}
		}

		return detailsEntity;
	}

	private Object determineValueObject(final AttributeValue attributeValue) {
		final Object valueObject;

		Attribute domainAttribute = attributeValue.getAttribute();
		if (domainAttribute.isMultiValueEnabled()) {
			valueObject = ((AttributeValueWithType) attributeValue).getShortTextMultiValues();
		} else {
			valueObject = attributeValue.getValue();
		}

		return valueObject;
	}

	@SuppressWarnings("unchecked")
	private static String convertRawValueToDisplayValue(final Object rawValue, final AttributeType attributeType, final Locale locale) {
		String displayValue;

		if (rawValue instanceof Collection) {
			Collection<String> valueCollection = (Collection<String>) rawValue;
			displayValue = StringUtils.join(valueCollection, ", ");
		} else if (rawValue instanceof Boolean) {
			if (Boolean.class.cast(rawValue)) {
				displayValue = "True";
			} else {
				displayValue = "False";
			}
		} else if (rawValue instanceof Date) {
			Date dateValue = Date.class.cast(rawValue);
			if (attributeType.equals(AttributeType.DATETIME)) {
				displayValue = DateUtil.formatDateTime(dateValue, locale);
			} else {
				displayValue = DateUtil.formatDate(dateValue, locale);
			}
		} else if (rawValue instanceof BigDecimal) {
			BigDecimal value = BigDecimal.class.cast(rawValue);
			// this is hard coded to the scale from the Persistence object we use to retrieve decimals from the database.
			value = value.setScale(AbstractPersistableImpl.DECIMAL_SCALE);
			displayValue = value.toPlainString();
		} else {
			displayValue = rawValue.toString();
		}
		return displayValue;
	}

	private static Object convertToProperValue(final Object rawValue) {
		Object value;

		if (rawValue instanceof Date) {
			value = Date.class.cast(rawValue).getTime();
		} else {
			value = rawValue;
		}

		return value;
	}
}
