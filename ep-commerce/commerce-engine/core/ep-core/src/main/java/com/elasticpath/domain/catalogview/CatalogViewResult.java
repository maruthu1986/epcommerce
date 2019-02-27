/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.domain.catalogview;

import java.util.List;
import java.util.Map;

import com.elasticpath.domain.EpDomain;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.service.catalog.CategoryLookup;

/**
 * Represents a catalog view result.
 */
public interface CatalogViewResult extends EpDomain {

	/**
	 * Returns a list of products as the search result. The list has been sorted based on the
	 * sorter specified in <code>CatalogViewRequest</code>.
	 *
	 * @return a list of products as the search result.
	 */
	List<StoreProduct> getProducts();

	/**
	 * Sets a list of products as the search result.
	 *
	 * @param products a list of products as the search result.
	 */
	void setProducts(List<StoreProduct> products);

	/**
	 * Returns the catalog view request which generated this search result.
	 *
	 * @return the catalog view request
	 */
	CatalogViewRequest getCatalogViewRequest();

	/**
	 * Sets the catalog view request which generated this search result.
	 *
	 * @param catalogViewRequest the catalog view request
	 */
	void setCatalogViewRequest(CatalogViewRequest catalogViewRequest);

	/**
	 * Returns a list of filter options on price range.
	 *
	 * @return a list of filter options on price range.
	 */
	List<FilterOption<PriceFilter>> getPriceFilterOptions();

	/**
	 * Sets the list of filter options on price range.
	 *
	 * @param priceFilterOptions the list of filter options on price range
	 */
	void setPriceFilterOptions(List<FilterOption<PriceFilter>> priceFilterOptions);

	/**
	 * Collapses the list of price filter options such that options that are a subset of another
	 * filter option will be removed (counts added to the option that contains that set).
	 */
	void collapsePriceFilterOptions();

	/**
	 * Returns a list of filter options on brand.
	 *
	 * @return a list of filter options on brand.
	 */
	List<FilterOption<BrandFilter>> getBrandFilterOptions();

	/**
	 * Gets a list of filter options on brand.
	 *
	 * @param brandFilterOptions a list of filter options on brand
	 */
	void setBrandFilterOptions(List<FilterOption<BrandFilter>> brandFilterOptions);

	/**
	 * Replicate the data to this <code>CatalogViewResult</code> from the given
	 * <code>CatalogViewResult</code>.
	 *
	 * @param catalogViewResult the <code>CatalogViewResult</code> to be replicated
	 */
	void replicateData(CatalogViewResult catalogViewResult);

	/**
	 * Returns a list of featured products.
	 *
	 * @return a list of featured products
	 */
	List<StoreProduct> getFeaturedProducts();

	/**
	 * Sets a list of featured products.
	 *
	 * @param featuredProducts a list of featured products
	 */
	void setFeaturedProducts(List<StoreProduct> featuredProducts);

	/**
	 * Sets the category.
	 *
	 * @param category the category to set
	 */
	void setCategory(Category category);

	/**
	 * Returns the category.
	 *
	 * @return the category.
	 */
	Category getCategory();

	/**
	 * Sets the category's list of available child categories.
	 *
	 * @param categories the list of available child categories
	 */
	void setAvailableChildCategories(List<Category> categories);

	/**
	 * Returns the list of the category's available child categories.
	 * @return the list of available child categories
	 */
	List<Category> getAvailableChildCategories();

	/**
	 * Returns the categories used to navigate from the root to the current category as a list.
	 * @return the list of categories used to navigate from the root to the current category
	 */
	List<Category> getCategoryPath();

	/**
	 * Sets the category's category path.
	 * @param categoryPath the category's category path
	 */
	void setCategoryPath(List<Category> categoryPath);

	/**
	 * Sets whether a category match was made and should navigate to the category specified by
	 * <code>getCategory()</code>.
	 *
	 * @param categoryMatch whether a category match was made
	 */
	void setCategoryMatch(boolean categoryMatch);

	/**
	 * Gets whether a category match was made and should navigate to the category specified by
	 * <code>getCategory()</code>.
	 *
	 * @return whether a category match was made
	 */
	boolean isCategoryMatch();

	/**
	 * Returns a list of filter options on category.
	 *
	 * @return a list of filter options on category.
	 */
	List<FilterOption<CategoryFilter>> getCategoryFilterOptions();

	/**
	 * Sets the list of filter options on category.
	 *
	 * @param categoryFilterOptions a list of filter options on category
	 */
	void setCategoryFilterOptions(List<FilterOption<CategoryFilter>> categoryFilterOptions);

	/**
	 * Collapses the list of category filter options such that options that are a subset of
	 * another filter option will be removed (counts added to the option that contains that set).
	 * Optionally remove filter options for this category as well. This category is given by
	 * {@link #getCategory()}.
	 *
	 * @param categoryLookup a category lookup
	 * @param removeThisCategory whether to remove options from this category
	 */
	void collapseCategoryFilterOptions(CategoryLookup categoryLookup, boolean removeThisCategory);

	/**
	 * Gets the map of attribute value filter options.
	 *
	 * @return a map of filter value options
	 */
	Map<Attribute, List<FilterOption<AttributeValueFilter>>> getAttributeValueFilterOptions();

	/**
	 * Sets the map of attribute value filter options.
	 *
	 * @param attributeValueFilterOptions map of attribute value filter options
	 */
	void setAttributeValueFilterOptions(Map<Attribute, List<FilterOption<AttributeValueFilter>>> attributeValueFilterOptions);

	/**
	 * Gets the map of attribute range filter options.
	 *
	 * @return the map of attribute range filter options
	 */
	Map<Attribute, List<FilterOption<AttributeRangeFilter>>> getAttributeRangeFilterOptions();

	/**
	 * Sets the map of attribute value filter options.
	 *
	 * @param attributeRangeFilterOptions map of attribute value filter options
	 */
	void setAttributeRangeFilterOptions(Map<Attribute, List<FilterOption<AttributeRangeFilter>>> attributeRangeFilterOptions);

	/**
	 * Collapses the list of attribute range filter options. This is only value for attribute
	 * range filter options whereby the the range is contained entirely within another range.
	 */
	void collapseAttributeRangeFilterOptions();

	/**
	 * Returns the number of hits for this result. The number of hits is the actual number of
	 * products returned from the result which may or may not be the same as the number of items
	 * in.
	 *
	 * @return the number of hits for this result
	 */
	int getResultsCount();

	/**
	 * Sets the number of hits for this result. The number of hits is the actual number of
	 * products returned from the result which may or may not be the same as the number of items
	 * in.
	 *
	 * @param hits the number of hits for this result
	 */
	void setResultsCount(int hits);
}
