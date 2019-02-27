/**
 * Copyright (c) Elastic Path Software Inc., 2008-2014
 */
package com.elasticpath.service.catalogview.filterednavigation.impl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import org.jdom.Element;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalogview.PriceFilter;
import com.elasticpath.domain.catalogview.RangeFilter;
import com.elasticpath.persistence.api.EpPersistenceException;
import com.elasticpath.service.catalogview.filterednavigation.FilteredNavigationConfiguration;

/**
 * Parses the XML representation of the filtered navigation configuration; creates {@link PriceFilter} 
 * defined by the configuration and populates a given FilteredNavigationConfiguration object.
 */
public class PriceFilterXmlElementParserImpl extends AbstractRangeFilterXmlElementParserImpl implements Serializable {
	
	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	@Override
	public void parse(final Element sectionElement, final FilteredNavigationConfiguration config) {
		
		if (sectionElement == null) {
			return;
		}
		
		final String currencyCode = sectionElement.getAttributeValue("currency");
		// final Currency currency = Currency.getInstance(currencyCode);
		if (config.getAllPriceRanges().get(currencyCode) != null) {
			throw new EpPersistenceException("Price range has been defined multiple times:" + currencyCode);
		}
		PriceFilter rootFilter = getFilterFactory().getFilterBean(ContextIdNames.PRICE_FILTER);
		rootFilter.setId(currencyCode);
		rootFilter.setCurrency(Currency.getInstance(currencyCode));
		final String localized = sectionElement.getAttributeValue(LOCALIZED);
		rootFilter.setLocalized(TRUE.equals(localized));
		config.getAllPriceRanges().put(rootFilter.getId(), rootFilter);

		final List<Element> subRangeNodes = getChildren(sectionElement);
		if (subRangeNodes != null && !subRangeNodes.isEmpty()) {
			for (Element childNode : subRangeNodes) {
				rootFilter.addChild(parsePriceRangeNode(childNode, config, rootFilter));
			}
		}

	}

	private PriceFilter parsePriceRangeNode(final Element rangeNode,
			final FilteredNavigationConfiguration config, final PriceFilter parent) {
		if (!rangeNode.getName().equals(RANGE)) {
			throw new EpPersistenceException("Not an range node:" + rangeNode.getName());
		}

		final PriceFilter priceFilter = getFilterFactory().getFilterBean(ContextIdNames.PRICE_FILTER);
		final Map<String, Object> filterProperties = new HashMap<>();

		filterProperties.put(PriceFilter.CURRENCY_PROPERTY, parent.getCurrency());

		priceFilter.setLocalized(parent.isLocalized());

		final BigDecimal lowerValue = constructBigDecimalValue(rangeNode.getAttributeValue(LOWER_VALUE));
		final BigDecimal upperValue = constructBigDecimalValue(rangeNode.getAttributeValue(UPPER_VALUE));

		filterProperties.put(RangeFilter.LOWER_VALUE_PROPERTY, lowerValue);
		filterProperties.put(RangeFilter.UPPER_VALUE_PROPERTY, upperValue);


		String seoId = rangeNode.getAttributeValue(SEO_ID);
		if (seoId == null || seoId.length() < 0) {
			throw new EpPersistenceException("Range should have seoId defined for: " + lowerValue + " - " + upperValue);
		}
		filterProperties.put(PriceFilter.ALIAS_PROPERTY, seoId);

		priceFilter.initialize(filterProperties);

		final List<Element> subNodes = getChildren(rangeNode);
		if (subNodes != null && !subNodes.isEmpty()) {
			for (Element childNode : subNodes) {
				if (childNode.getName().equals(DISPLAY_INFO)) {
					parseDisplayInfo(priceFilter, childNode);
				} else if (childNode.getName().equals(RANGE)) {
					priceFilter.addChild(parsePriceRangeNode(childNode, config, priceFilter));
				}
			}
		}
		if (priceFilter.getChildren() == null || priceFilter.getChildren().isEmpty()) {
			// This is the leaf range.
			final SortedMap<PriceFilter, PriceFilter> bottomLevelPriceRanges = config.getBottomLevelPriceRanges(priceFilter.getCurrency());
			if (bottomLevelPriceRanges.get(priceFilter) != null) {
				final PriceFilter overLapPriceFilter = bottomLevelPriceRanges.get(priceFilter);
				throw new EpPersistenceException("Price range has overlap with another price range :"
						+ overLapPriceFilter.getId());
			}
			bottomLevelPriceRanges.put(priceFilter, priceFilter);
		}
		config.getAllPriceRanges().put(priceFilter.getId(), priceFilter);

		return priceFilter;
	}
	
	private BigDecimal constructBigDecimalValue(final String attributeValue) {
		try {
			return new BigDecimal(attributeValue);
		} catch (NumberFormatException e) {
			return null;
		}
	}
}
