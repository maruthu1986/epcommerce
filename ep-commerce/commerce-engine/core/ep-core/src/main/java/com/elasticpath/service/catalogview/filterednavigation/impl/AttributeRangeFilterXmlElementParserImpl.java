/**
 * Copyright (c) Elastic Path Software Inc., 2008-2014
 */
package com.elasticpath.service.catalogview.filterednavigation.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import com.google.common.base.Strings;
import org.apache.log4j.Logger;
import org.jdom.Element;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalogview.AttributeFilter;
import com.elasticpath.domain.catalogview.AttributeRangeFilter;
import com.elasticpath.domain.catalogview.RangeFilter;
import com.elasticpath.persistence.api.EpPersistenceException;
import com.elasticpath.service.catalogview.filterednavigation.FilteredNavigationConfiguration;

/**
 * Parses the XML representation of the filtered navigation configuration; creates {@link AttributeRangeFilter} defined by the configuration and
 * populates a given FilteredNavigationConfiguration object.
 */
public class AttributeRangeFilterXmlElementParserImpl extends AbstractRangeFilterXmlElementParserImpl implements Serializable {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;

	private static final Logger LOG = Logger.getLogger(AttributeRangeFilterXmlElementParserImpl.class);

	private static final String ATTRIBUTE_KEY = "key";

	@Override
	public void parse(final Element sectionElement, final FilteredNavigationConfiguration config) {

		if (sectionElement == null) {
			return;
		}

		final String attributeKey = sectionElement.getAttributeValue(ATTRIBUTE_KEY);
		if (config.getAllAttributeRanges().get(attributeKey) != null) {
			throw new EpPersistenceException("Attribute range has been defined multiple times:" + attributeKey);
		}
		AttributeRangeFilter rootFilter = getFilterFactory().getFilterBean(ContextIdNames.ATTRIBUTE_RANGE_FILTER);
		rootFilter.setId(attributeKey);
		rootFilter.setAttributeKey(attributeKey);
		// If attribute not found in database, just ignore it.
		if (rootFilter.getAttribute() == null) {
			LOG.error("Attribute not found: " + attributeKey);
			return;
		}

		final String localized = sectionElement.getAttributeValue(LOCALIZED);
		rootFilter.setLocalized(TRUE.equals(localized));
		config.getAllAttributeRanges().put(rootFilter.getId(), rootFilter);
		config.getAllAttributesMap().put(attributeKey, rootFilter.getAttribute());

		final List<Element> subRangeNodes = getChildren(sectionElement);
		if (subRangeNodes != null && !subRangeNodes.isEmpty()) {
			for (Element childNode : subRangeNodes) {
				rootFilter.addChild(parseAttributeRangeNode(childNode, config, rootFilter));
			}
		}
	}

	private AttributeRangeFilter parseAttributeRangeNode(final Element rangeNode, final FilteredNavigationConfiguration config,
			final AttributeRangeFilter parentAttributeRangeFilter) {
		if (!rangeNode.getName().equals(RANGE)) {
			throw new EpPersistenceException("Not an range node:" + rangeNode.getName());
		}

		final AttributeRangeFilter attributeRangeFilter = createChildAttributeRangeFilter(rangeNode, parentAttributeRangeFilter);

		addRangeNodesToFilter(rangeNode, config, attributeRangeFilter);

		addBottomLevelRangesToConfig(config, attributeRangeFilter);

		config.getAllAttributeRanges().put(attributeRangeFilter.getId(), attributeRangeFilter);

		return attributeRangeFilter;
	}

	private void addBottomLevelRangesToConfig(final FilteredNavigationConfiguration config, final AttributeRangeFilter attributeRangeFilter) {

		if (attributeRangeFilter.getChildren().isEmpty()) {
			// This is the leaf range.
			final SortedMap<AttributeRangeFilter, AttributeRangeFilter> bottomLevelAttributeRanges = config
					.getBottomLevelAttributeRanges(attributeRangeFilter.getAttributeKey());
			final AttributeRangeFilter filter = bottomLevelAttributeRanges.get(attributeRangeFilter);
			if (filter != null) {
				throw new EpPersistenceException("Attribute range has overlap with another range :" + filter.getId());
			}
			bottomLevelAttributeRanges.put(attributeRangeFilter, attributeRangeFilter);
		}
	}

	private void addRangeNodesToFilter(final Element rangeNode, final FilteredNavigationConfiguration config,
			final AttributeRangeFilter attributeRangeFilter) {

		for (Element childNode : getChildren(rangeNode)) {
			if (childNode.getName().equals(DISPLAY_INFO)) {
				parseDisplayInfo(attributeRangeFilter, childNode);
			} else if (childNode.getName().equals(RANGE)) {
				attributeRangeFilter.addChild(parseAttributeRangeNode(childNode, config, attributeRangeFilter));
			}
		}
	}

	private AttributeRangeFilter createChildAttributeRangeFilter(final Element rangeNode, final AttributeRangeFilter parentAttributeRangeFilter) {
		final AttributeRangeFilter attributeRangeFilter = getFilterFactory().getFilterBean(ContextIdNames.ATTRIBUTE_RANGE_FILTER);

		Map<String, Object> filterProperties = new HashMap<>();
		attributeRangeFilter.setLocalized(parentAttributeRangeFilter.isLocalized());
		final String lowerValue = rangeNode.getAttributeValue(LOWER_VALUE);
		final String upperValue = rangeNode.getAttributeValue(UPPER_VALUE);

		setFilterPropertyValues(parentAttributeRangeFilter, filterProperties, lowerValue, upperValue);

		String seoId = rangeNode.getAttributeValue(SEO_ID);
		if (seoId == null || seoId.length() < 0) {
			throw new EpPersistenceException("Range should have seoId defined for: " + lowerValue + " - " + upperValue);
		}
		filterProperties.put(AttributeFilter.ATTRIBUTE_VALUES_ALIAS_PROPERTY, seoId);
		attributeRangeFilter.initialize(filterProperties);

		return attributeRangeFilter;
	}

	private void setFilterPropertyValues(final AttributeRangeFilter parent, final Map<String, Object> filterProperties, final String lowerValue,
			final String upperValue) {
		filterProperties.put(AttributeFilter.ATTRIBUTE_PROPERTY, parent.getAttribute());

		if (isNonEmptyString(lowerValue)) {
			filterProperties.put(RangeFilter.LOWER_VALUE_PROPERTY, lowerValue);
		}
		if (isNonEmptyString(upperValue)) {
			filterProperties.put(RangeFilter.UPPER_VALUE_PROPERTY, upperValue);
		}
	}

	private boolean isNonEmptyString(final String rangeValue) {
		return !Strings.nullToEmpty(rangeValue).trim().isEmpty();
	}
}
