/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.catalog;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.attribute.AttributeValueGroup;
import com.elasticpath.domain.misc.Orderable;
import com.elasticpath.persistence.api.Entity;

/**
 * A <code>Category</code> represents a collection of related <code>Product</code>s. A
 * <code>Category</code> is likely to have one or more products in it.
 */
public interface Category extends Comparable<Category>, Entity, ObjectWithLocaleDependantFields,
		CatalogObject, Orderable {

	/** Legacy Category guid delimiter.  This is a throwback to when categories used categoryCode|catalogCode as a compound guid. */
	String CATEGORY_LEGACY_GUID_DELIMITER = "|";

	/**
	 * Get the guid of the parent of this category. Returns null if this category doesn't have a parent.
	 *
	 * @return the guid of the parent category (or null no parent)
	 */
	String getParentGuid();

	/**
	 * Set the parent category's guid.
	 *
	 * @param parentGuid the new parent guid
	 */
	void setParentGuid(String parentGuid);

	/**
	 * Set the parent category.  This is a convenience method that delegates to setParentGuid(), however
	 * using it does hide the implementation of how the relationship to the parent is stored.
	 *
	 * @param newParent the new parent category
	 */
	void setParent(Category newParent);

	/**
	 * Returns true if this category has a parent (i.e. is not a root node)
	 *
	 * @return true if this category has a parent, false if this category is a root node
	 */
	boolean hasParent();

	/**
	 * Get the start date that this category will become available to customers.
	 *
	 * @return the start date
	 */
	Date getStartDate();

	/**
	 * Set the start date that this category will become valid.
	 *
	 * @param startDate the start date
	 */
	void setStartDate(Date startDate);

	/**
	 * Get the end date. After the end date, the category will change to unavailable to customers.
	 *
	 * @return the end date
	 */
	Date getEndDate();

	/**
	 * Set the end date.
	 *
	 * @param endDate the end date
	 */
	void setEndDate(Date endDate);

	/**
	 * Get the attribute value group.
	 *
	 * @return the domain model's <code>AttributeValueGroup</code>
	 */
	AttributeValueGroup getAttributeValueGroup();

	/**
	 * Set the attribute value group.
	 *
	 * @param attributeValueGroup the <code>AttributeValueGroup</code>
	 */
	void setAttributeValueGroup(AttributeValueGroup attributeValueGroup);

	/**
	 * Returns <code>true</code> if the category is available.
	 *
	 * @return <code>true</code> if the category is available.
	 */
	boolean isAvailable();

	/**
	 * Returns the category type.
	 *
	 * @return the category type
	 */
	CategoryType getCategoryType();

	/**
	 * Set the <code>CategoryType</code>.
	 *
	 * @param categoryType the <code>CategoryType</code>
	 */
	void setCategoryType(CategoryType categoryType);

	/**
	 * Returns true if the product should not be displayed (e.g. in its category or as a search result).
	 *
	 * @return true if the product should not be displayed
	 */
	boolean isHidden();

	/**
	 * Set to true if the product should not be displayed.
	 *
	 * @param hidden true if the product should not be displayed
	 */
	void setHidden(boolean hidden);

	/**
	 * Sets the top sellers for this category.
	 *
	 * @param topSellers the top sellers for this category
	 */
	void setTopSellers(Set<TopSeller> topSellers);

	/**
	 * Returns the top sellers for this category.
	 *
	 * @return top sellers for this category
	 */
	Set<TopSeller> getTopSellers();

	/**
	 * Returns the category code.
	 * @return the category code
	 */
	String getCode();

	/**
	 * Sets the category code.
	 * @param code the category code
	 */
	void setCode(String code);

	/**
	 * Return the compound category guid based on category code and appropriate catalog code.
	 *
	 * @return the compound guid.
	 */
	String getCompoundGuid();

	/**
	 * Returns the date when the category was last modified.
	 *
	 * @return the date when the category was last modified
	 */
	Date getLastModifiedDate();

	/**
	 * Get the indicator of whether this is a virtual category.
	 * @return true if this is a virtual category
	 */
	boolean isVirtual();

	/**
	 * Set the indicator of whether this is a virtual category.
	 * @param virtual true if the category is virtual
	 */
	void setVirtual(boolean virtual);

	/**
	 * Returns true if this category is linked (i.e. derived from a master category); false if it is a master category.
	 *
	 * @return true if this category is linked (i.e. derived from a master category); false if it is a master category
	 */
	boolean isLinked();

	/**
	 * Get the master category this virtual category is derived from (null if this category is a master).
	 *
	 * @return the master category.
	 */
	Category getMasterCategory();

	/**
	 * Set the master category this virtual category is derived from (null if this a master category).
	 *
	 * @param masterCategory the master category
	 */
	void setMasterCategory(Category masterCategory);

	/**
	 * Set the attribute value map.
	 *
	 * @param attributeValueMap the map
	 */
	void setAttributeValueMap(Map<String, AttributeValue> attributeValueMap);

	/**
	 * Get the attribute value map.
	 *
	 * @return the map
	 */
	Map<String, AttributeValue> getAttributeValueMap();

	/**
	 * Get the indicator of whether or not this category has been included.
	 *
	 * @return true if this category has been included
	 */
	boolean isIncluded();

	/**
	 * Set the indicator of whether or not this category has been included.
	 *
	 * @param include true if this category has been included
	 */
	void setIncluded(boolean include);
}
