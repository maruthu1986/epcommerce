/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.service.catalogview.filterednavigation.impl;

import java.util.Currency;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.catalogview.AttributeKeywordFilter;
import com.elasticpath.domain.catalogview.AttributeRangeFilter;
import com.elasticpath.domain.catalogview.AttributeValueFilter;
import com.elasticpath.domain.catalogview.PriceFilter;
import com.elasticpath.service.catalogview.filterednavigation.FilteredNavigationConfiguration;

/**
 * Implements the methods relevant to filtered navigation configuration.
 */
public class FilteredNavigationConfigurationImpl implements FilteredNavigationConfiguration {

	private final Map<String, PriceFilter> allPriceRanges = new HashMap<>();

	private final Map<Currency, SortedMap<PriceFilter, PriceFilter>> bottomLevelPriceRangesMap =
		new HashMap<>();

	private final Map<String, AttributeRangeFilter> allAttributeRanges = new HashMap<>();

	private final Map<String, SortedMap<AttributeRangeFilter, AttributeRangeFilter>> bottomLevelAttributeRangesMap =
		new HashMap<>();

	private final Map<String, AttributeValueFilter> allAttributeSimpleValues = new HashMap<>();

	private final Map<String, AttributeKeywordFilter> allAttributeKeywords = new HashMap<>();
	
	private final Map<String, SortedMap<String, AttributeValueFilter>> attributeSimpleValuesMap =
		new HashMap<>();

	private final Map<String, Attribute> allAttributesMap = new HashMap<>();

	private final Set<String> allBrandCodes = new HashSet<>();
	
	/**
	 * Returns all defined price ranges as a <code>Map</code>.
	 * <p>
	 * The key will be like : "price-between-USD-90-and-100".
	 * <p>
	 * And the value will be a <code>PriceFilter</code>.
	 * 
	 * @return all defined price ranges as a <code>Map</code>.
	 */
	@Override
	public Map<String, PriceFilter> getAllPriceRanges() {
		return this.allPriceRanges;
	}
	
	/**
	 * Clears all price range information, including bottom level price ranges.
	 */
	@Override
	public void clearAllPriceRanges() {
		this.allPriceRanges.clear();
		this.bottomLevelPriceRangesMap.clear();
	}

	/**
	 * Returns price ranges of the given currency, which are defined at bottom level of the
	 * price-range-tree. This is a lookup map for a <code>Product</code> to decide which price
	 * range it should belongs to.
	 * <p>
	 * The key and the value will be the same <code>PriceFilter</code>.
	 * 
	 * @param currency the currency
	 * @return price ranges of the given currency, which are defined at bottom level
	 */
	@Override
	public SortedMap<PriceFilter, PriceFilter> getBottomLevelPriceRanges(final Currency currency) {
		SortedMap<PriceFilter, PriceFilter> result = this.bottomLevelPriceRangesMap.get(currency);

		if (result == null) {
			result = new TreeMap<>();
			this.bottomLevelPriceRangesMap.put(currency, result);
		}
		return result;
	}


	/**
	 * Returns all defined attribute ranges as a <code>Map</code>.
	 * <p>
	 * The attribute code will be used as the map key .
	 * <p>
	 * And the value will be a <code>AttributeRangeFilter</code>.
	 * 
	 * @return all defined attribute ranges as a <code>Map</code>.
	 */
	@Override
	public Map<String, AttributeRangeFilter> getAllAttributeRanges() {
		return this.allAttributeRanges;
	}

	/**
	 * Clears all attribute range information, including bottom level attribute ranges.
	 */
	@Override
	public void clearAllAttributeRanges() {
		this.allAttributeRanges.clear();
		this.bottomLevelAttributeRangesMap.clear();
	}

	/**
	 * Returns attribute ranges of the given attributeCode, which are defined at bottom level of the attribute-range-tree. This is a lookup map for a
	 * <code>Product</code> to decide which attribute range it should belongs to.
	 * <p>
	 * The key and the value will be the same <code>AttributeRangeFilter</code>.
	 * 
	 * @param attributeCode the attributeCode
	 * @return attribute ranges of the given attribute code, which are defined at bottom level
	 */
	@Override
	public SortedMap<AttributeRangeFilter, AttributeRangeFilter> getBottomLevelAttributeRanges(final String attributeCode) {
		SortedMap<AttributeRangeFilter, AttributeRangeFilter> result = this.bottomLevelAttributeRangesMap.get(attributeCode);

		if (result == null) {
			result = new TreeMap<>();
			this.bottomLevelAttributeRangesMap.put(attributeCode, result);
		}
		return result;
	}

	/**
	 * Returns all defined attribute simple value filters as a <code>Map</code>.
	 * <p>
	 * The filter id will be used as the map key .
	 * <p>
	 * And the value will be a <code>AttributeFilter</code>.
	 * 
	 * @return all defined attribute filters as a <code>Map</code>.
	 */
	@Override
	public Map<String, AttributeValueFilter> getAllAttributeSimpleValues() {
		return allAttributeSimpleValues;
	}

	/**
	 * Clears all attribute simple value information.
	 */
	@Override
	public void clearAllAttributeSimpleValues() {
		this.allAttributeSimpleValues.clear();
	}

	/**
	 * Returns attribute values of the given attributeCode, which are defined at the attribute section. The key will be the attribute value and the
	 * value will be the <code>AttributeValueFilter</code>.
	 * 
	 * @param attributeCode the attributeCode
	 * @return attribute ranges of the given attribute code, which are defined at bottom level
	 */
	@Override
	public SortedMap<String, AttributeValueFilter> getAttributeSimpleValuesMap(final String attributeCode) {
		SortedMap<String, AttributeValueFilter> result = this.attributeSimpleValuesMap.get(attributeCode);

		if (result == null) {
			result = new TreeMap<>();
			this.attributeSimpleValuesMap.put(attributeCode, result);
		}
		return result;
	}

	@Override
	public Map<String, AttributeKeywordFilter> getAllAttributeKeywords() {
		return allAttributeKeywords;
	}
	
	@Override
	public void clearAllAttributeKeywords() {
		this.allAttributeKeywords.clear();
	}
	
	/**
	 * This map contains all the attributes defined in the filtered navigation configuration.
	 * The key will be the attribute code, and the value will be the <code>Attribute</code>.
	 * 
	 * @return All the attributes.
	 */
	@Override
	public Map<String, Attribute> getAllAttributesMap() {
		return this.allAttributesMap;
	}
	
	@Override
	public Set<String> getAllSimpleAttributeKeys() {
		return attributeSimpleValuesMap.keySet();
	}
	
	@Override
	public Set<String> getAllBrandCodes() {
		return this.allBrandCodes;
	}

	@Override
	public void clearAllBrandCodes() {
		this.allBrandCodes.clear();
	}
}
