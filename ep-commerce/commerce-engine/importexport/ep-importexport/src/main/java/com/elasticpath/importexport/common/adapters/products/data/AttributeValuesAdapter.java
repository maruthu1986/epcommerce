/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.importexport.common.adapters.products.data;

import static com.elasticpath.importexport.common.comparators.ExportComparators.DISPLAY_VALUE_COMPARATOR;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import org.apache.log4j.Logger;

import com.elasticpath.common.dto.DisplayValue;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.attribute.AttributeValueGroup;
import com.elasticpath.domain.attribute.impl.AbstractAttributeValueImpl;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.importexport.common.adapters.AbstractDomainAdapterImpl;
import com.elasticpath.importexport.common.dto.products.AttributeValuesDTO;
import com.elasticpath.importexport.common.exception.runtime.PopulationRollbackException;
import com.elasticpath.importexport.common.exception.runtime.PopulationRuntimeException;
import com.elasticpath.importexport.common.util.LocalizedAttributeKeyLocaleTranslator;
import com.elasticpath.importexport.common.util.Message;
import com.elasticpath.validation.service.ValidatorUtils;

/**
 * The implementation of <code>DomainAdapter</code> interface. It is responsible for data transformation between
 * <code>Collection&lt;AttributeValue></code> and <code>AttributeValuesDTO</code> objects.
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public class AttributeValuesAdapter extends AbstractDomainAdapterImpl<Collection<AttributeValue>, AttributeValuesDTO> {

	private static final String SEPARATOR = "_";

	private AttributeValueGroup attributeValueGroup;

	private ValidatorUtils validatorUtils;

	private LocalizedAttributeKeyLocaleTranslator localizedAttributeKeyLocaleTranslator;

	private static final Logger LOG = Logger.getLogger(AttributeValuesAdapter.class);

	/**
	 *
	 *
	 * @throws PopulationRollbackException if attributeValueGroup is not initialized
	 */
	@Override
	public void populateDomain(final AttributeValuesDTO attributesDTO, final Collection<AttributeValue> attributeValueCollection) {
		sanityCheck();

		String attributeKey = attributesDTO.getKey();
		Attribute attribute = getCachingService().findAttribiteByKey(attributeKey);
		if (attribute == null) {
			// In the ProductAdapter the product code is added to the parameters.
			throw new PopulationRollbackException("IE-10309", attributeKey);
		}

		final ListMultimap<Locale, String> attributesMap = ArrayListMultimap.create();
		for (final DisplayValue displayValue : attributesDTO.getValues()) {
			final Locale locale = getLocalizedAttributeKeyLocaleTranslator()
				.convertLocaleStringToLocale(displayValue.getLanguage());

			attributesMap.put(locale, displayValue.getValue());
		}

		for (final Locale locale : attributesMap.keySet()) {
			try {
				if (locale != null && !isLocaleSupportedByCatalog(attribute.getCatalog(), locale)) {
					continue;
				}

				final AttributeValue attributeValue = populateAttributeValue(attribute, attributeKey, locale, attributesMap.get(locale));

				getValidatorUtils().validateAttributeValue(attributeValue);

				attributeValueCollection.add(attributeValue);
			} catch (IllegalArgumentException exception) {
				throw new PopulationRuntimeException("IE-10306", exception, String.valueOf(locale), attributesMap.get(locale).toString());
			}
		}
	}

	private AttributeValue populateAttributeValue(final Attribute attribute,
		final String attributeKey,
		final Locale locale,
		final List<String> values) {

		AttributeValue attributeValue = attributeValueGroup.getAttributeValue(attributeKey, locale);
		if (attributeValue == null) {
			StringBuilder localizedKey = new StringBuilder(attributeKey);
			if (locale != null) {
				localizedKey = localizedKey.append(SEPARATOR).append(locale);
			}

			attributeValue = attributeValueGroup.getAttributeValueFactory().createAttributeValue(attribute, localizedKey.toString());
		}

		String value = null;

		if (attribute.isMultiValueEnabled()) {
			value = AbstractAttributeValueImpl.buildShortTextMultiValues(values, attribute.getMultiValueType());
		} else if (!values.isEmpty()) {
			value = values.get(0);
		}

		if (value != null) {
			attributeValue.setStringValue(value);
		}

		return attributeValue;
	}

	/**
	 * @return True if Catalog is not null and Locale is not supported.
	 */
	private boolean isLocaleSupportedByCatalog(final Catalog catalog, final Locale locale) {
		if (catalog != null && !catalog.getSupportedLocales().contains(locale)) {
			LOG.warn(new Message("IE-10000", locale.toString()));
			return false;
		}
		return true;
	}

	/**
	 *
	 *
	 * @throws PopulationRollbackException if attributeValueGroup is not initialized
	 */
	@Override
	public void populateDTO(final Collection<AttributeValue> attributeValueCollection, final AttributeValuesDTO attributeValuesDTO) {
		List<DisplayValue> displayValueList = new ArrayList<>();
		for (AttributeValue attributeValue : attributeValueCollection) {
			final Attribute attribute = attributeValue.getAttribute();
			attributeValuesDTO.setKey(attribute.getKey());

			String language = null;

			if (attribute.isLocaleDependant()) {
				final String localizedKey = attributeValue.getLocalizedAttributeKey();
				language = getLocalizedAttributeKeyLocaleTranslator().getLanguageTagFromLocalizedKeyName(localizedKey);
			}

			String stringValue = null;
			if (attributeValue.getValue() != null) {
				stringValue = attributeValue.getStringValue();
			}
			if (attribute.isMultiValueEnabled()) {
				List<String> valueList = AbstractAttributeValueImpl.
					parseShortTextMultiValues(stringValue, attribute.getMultiValueType());
				if (valueList != null) {
					for (String value : valueList) {
						displayValueList.add(new DisplayValue(language, value));
					}
				}
			} else {
				displayValueList.add(new DisplayValue(language, stringValue));
			}
		}
		Collections.sort(displayValueList, DISPLAY_VALUE_COMPARATOR);
		attributeValuesDTO.setValues(displayValueList);
	}

	private void sanityCheck() {
		if (attributeValueGroup == null) {
			throw new PopulationRollbackException("IE-10310");
		}
	}

	/**
	 * Sets the attributeValueGroup.
	 *
	 * @param attributeValueGroup the attributeValueGroup to set
	 */
	public void setAttributeValueGroup(final AttributeValueGroup attributeValueGroup) {
		this.attributeValueGroup = attributeValueGroup;
	}

	protected ValidatorUtils getValidatorUtils() {
		return validatorUtils;
	}

	public void setValidatorUtils(final ValidatorUtils validatorUtils) {
		this.validatorUtils = validatorUtils;
	}

	protected LocalizedAttributeKeyLocaleTranslator getLocalizedAttributeKeyLocaleTranslator() {
		return localizedAttributeKeyLocaleTranslator;
	}

	public void setLocalizedAttributeKeyLocaleTranslator(final LocalizedAttributeKeyLocaleTranslator localizedAttributeKeyLocaleTranslator) {
		this.localizedAttributeKeyLocaleTranslator = localizedAttributeKeyLocaleTranslator;
	}

}
