/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.shipping;

import java.util.Map;

import com.elasticpath.persistence.api.Entity;
import com.elasticpath.shipping.connectivity.dto.ShippingAddress;

/**
 * A ShippingRegion represents a region that will be associated with one or more shipping services.
 * For now, it is composed of country and a subcountry, i.e. CA(country) and BC(subcountry).
 */
public interface ShippingRegion extends Entity, Comparable<ShippingRegion> {
	/**
	 * Get the shipping region name.
	 * @return the parameter name
	 */
	String getName();

	/**
	 * Set the shipping region name.
	 * @param name the parameter name
	 */
	void setName(String name);

	/**
	 * Get the Map of regions associated with this shipping region.
	 * The entry of the regionMap is countryCode -> <code>Region</code>.
	 * @return the map of regions associated with this shipping region.
	 */
	Map<String, Region> getRegionMap();

	/**
	 * Set the Map of regions associated with this shipping region.
	 * @param regionMap the map of regions to be associated with this shipping region.
	 */
	void setRegionMap(Map<String, Region> regionMap);

	/**
	 * Check if the given shippingAddress is in the range of this <code>ShippingRegion</code>.
	 * @param shippingAddress the shippingAddress to be evaluated.
	 * @return status of whether the given shippingAddress is in the range of this <code>ShippingRegion</code>.
	 */
	boolean isInShippingRegion(ShippingAddress shippingAddress);

}
