/**
 * Copyright (c) Elastic Path Software Inc., 2008-2014
 */
package com.elasticpath.service.catalogview.filterednavigation.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import org.apache.commons.lang.LocaleUtils;
import org.apache.log4j.Logger;
import org.jdom.Element;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalogview.AttributeFilter;
import com.elasticpath.domain.catalogview.AttributeValueFilter;
import com.elasticpath.persistence.api.EpPersistenceException;
import com.elasticpath.service.catalogview.filterednavigation.FilteredNavigationConfiguration;

/**
 * Parses the XML representation of the filtered navigation configuration; creates {@link AttributeValueFilter} 
 * defined by the configuration and populates a given FilteredNavigationConfiguration object.
 */
public class AttributeValueFilterXmlElementParserImpl extends AbstractFilterXmlElementParserImpl implements Serializable {
	
	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	private static final Logger LOG = Logger.getLogger(AttributeValueFilterXmlElementParserImpl.class);

	private static final String VALUE = "value";

	private static final String ATTRIBUTE_KEY = "key";

	private static final String ATTRIBUTE_SIMPLE_VALUE = "simple";

	@Override
	public void parse(final Element sectionElement, final FilteredNavigationConfiguration config) {
		
		if (sectionElement == null) {
			return;
		}
		
		final String attributeKey = sectionElement.getAttributeValue(ATTRIBUTE_KEY);
		if (config.getAllAttributeSimpleValues().get(attributeKey) != null) {
			throw new EpPersistenceException("Attribute has been defined multiple times:" + attributeKey);
		}
		AttributeValueFilter rootFilter = getFilterFactory().getFilterBean(ContextIdNames.ATTRIBUTE_FILTER);
		rootFilter.setId(attributeKey);
		rootFilter.setAttributeKey(attributeKey);
		// If attribute not found in database, just ignore it.
		if (rootFilter.getAttribute() == null) {
			LOG.error("Attribute not found: " + attributeKey);
			return;
		}

		final String localized = sectionElement.getAttributeValue(LOCALIZED);
		rootFilter.setLocalized(TRUE.equals(localized));
		config.getAllAttributeSimpleValues().put(rootFilter.getId(), rootFilter);
		config.getAllAttributesMap().put(attributeKey, rootFilter.getAttribute());
		
		@SuppressWarnings("unchecked")
		final List<Element> subNodes = sectionElement.getChildren();
		if (subNodes != null && !subNodes.isEmpty()) {
			for (Element childNode : subNodes) {
				parseAttributeSimpleNode(childNode, config, rootFilter);
			}
		}
		
	}

	private AttributeFilter<?> parseAttributeSimpleNode(final Element subNode, final FilteredNavigationConfiguration config,
			final AttributeFilter<?> parent) {
		if (!subNode.getName().equals(ATTRIBUTE_SIMPLE_VALUE)) {
			throw new EpPersistenceException("Not an attribute simple node:" + subNode.getName());
		}

		final AttributeValueFilter attributeFilter = getFilterFactory().getFilterBean(ContextIdNames.ATTRIBUTE_FILTER);
		Map<String, Object> filterProperties = new HashMap<>();
		attributeFilter.setLocalized(parent.isLocalized());
		filterProperties.put(AttributeFilter.ATTRIBUTE_PROPERTY, parent.getAttribute());

		String seoId = subNode.getAttributeValue(SEO_ID);
		if (seoId == null || seoId.length() < 0) {
			throw new EpPersistenceException("Attribute simple value should have seoId defined for: "
					+ attributeFilter.getAttributeKey());
		}

		filterProperties.put(AttributeFilter.ATTRIBUTE_VALUES_ALIAS_PROPERTY, seoId);
		String attributeValue = subNode.getAttributeValue(VALUE);
		filterProperties.put(AttributeValueFilter.ATTRIBUTE_VALUE_PROPERTY, attributeValue);

		attributeFilter.initialize(filterProperties);

		attributeFilter.setDisplayName(subNode.getAttributeValue("displayName"));

		String identicalMapKey = null;
		if (attributeFilter.isLocalized()) {
			String language = subNode.getAttributeValue(LANGUAGE);
			if (language == null || language.length() == 0) {
				throw new EpPersistenceException("Attribute node should have language defined since it is localized."
						+ attributeFilter.getId());
			}
			attributeFilter.setLocale(LocaleUtils.toLocale(language));
			identicalMapKey = String.valueOf(attributeFilter.getLocale()).concat(String.valueOf(attributeFilter.getAttributeValue()));
		} else {
			identicalMapKey = String.valueOf(attributeFilter.getAttributeValue());
		}

		final SortedMap<String, AttributeValueFilter> attributeValuesMap = config.getAttributeSimpleValuesMap(attributeFilter.getAttributeKey());
		final AttributeValueFilter overLapFilter = attributeValuesMap.get(String.valueOf(attributeFilter
				.getAttributeValue()));
		if (overLapFilter != null) {
			throw new EpPersistenceException("Attribute value has overlap with another value:" + overLapFilter.getId());
		}
		attributeValuesMap.put(identicalMapKey, attributeFilter);
		config.getAllAttributeSimpleValues().put(attributeFilter.getId(), attributeFilter);

		return attributeFilter;
	}
}
