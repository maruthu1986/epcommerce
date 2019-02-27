/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.domain.advancedsearch;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents available types for advanced query. 
 */
public enum AdvancedQueryType {
	
	/**
	 * Represents product query.
	 */
	PRODUCT("Product"),
	
	/**
	 * Represents category query.
	 */
	CATEGORY("Category"),
	
	/**
	 * Represents catalog query.
	 */
	CATALOG("Catalog");
	
	private String propertyKey = "";

	/*
	 * The map of AdvancedQueryType available in the system.
	 * String is the property key and AdvancedQueryType is the value.
	 */
	private static Map<String, AdvancedQueryType> queryTypeMap = new HashMap<>();

	/*
	 * Fill the map with Advanced Query Types registered in this enumeration.
	 */
	static {
		for (AdvancedQueryType queryType : AdvancedQueryType.values()) {
			queryTypeMap.put(queryType.getPropertyKey(), queryType);
		}
	}

	/**
	 * Constructor.
	 *
	 * @param propertyKey the property key.
	 */
	AdvancedQueryType(final String propertyKey) {
		this.propertyKey = propertyKey;
	}

	/**
	 * Get the localization property key.
	 *
	 * @return the localized property key
	 */
	public String getPropertyKey() {
		return this.propertyKey;
	}

	/**
	 * Finds AdvancedQueryType in the map by its property key.
	 * 
	 * @param key property key
	 * @return AdvancedQueryType
	 */
	public static AdvancedQueryType getQueryTypeKey(final String key) {
		return queryTypeMap.get(key);
	}
}
