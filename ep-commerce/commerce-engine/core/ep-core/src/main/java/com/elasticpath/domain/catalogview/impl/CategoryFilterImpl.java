/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.domain.catalogview.impl;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.elasticpath.commons.constants.CatalogViewConstants;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.constants.SeoConstants;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.LocaleDependantFields;
import com.elasticpath.domain.catalogview.CategoryFilter;
import com.elasticpath.domain.catalogview.EpCatalogViewRequestBindException;
import com.elasticpath.service.catalog.CategoryLookup;

/**
 * Represents a filter on category.
 */
public class CategoryFilterImpl extends AbstractFilterImpl<CategoryFilter> implements CategoryFilter {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;

	private static final Logger LOG = Logger.getLogger(CategoryFilterImpl.class);

	private Category category;
	private Catalog catalog;

	/**
	 * Returns the display name of the filter with the given locale.
	 *
	 * @param locale the locale
	 * @return the display name of the filter with the given locale.
	 */
	@Override
	public String getDisplayName(final Locale locale) {
		if (this.category == null) {
			throw new EpDomainException("Not initalized.");
		}
		return this.category.getDisplayName(locale);
	}

	/**
	 * Returns the hash code.
	 *
	 * @return the hash code
	 */
	@Override
	public int hashCode() {
		return getId().hashCode();
	}

	/**
	 * Returns <code>true</code> if this filter equals to the given object.
	 *
	 * @param object the object to compare
	 * @return <code>true</code> if this filter equals to the given object.
	 */
	@Override
	public boolean equals(final Object object) {
		if (!(object instanceof CategoryFilter)) {
			return false;
		}
		return getId().equals(((CategoryFilter) object).getId());
	}

	/**
	 * Compares this object with the specified filter for ordering.
	 *
	 * @param other the given object
	 * @return a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the specified object.
	 */
	@Override
	public int compareTo(final CategoryFilter other) {
		return Comparator.comparing(CategoryFilter::getCategory)
			.compare(this, other);
	}

	/**
	 * Returns the first available (thus best) SEO name of the category in the given locale.
	 * Priorities are the category SEO URL, display name followed by the category GUID if neither of
	 * the first two are available. This SEO name will also be used during the SEO URL validation and
	 * therefore should conform to the structure used by the SeoUrlBuilder.
	 *
	 * @param locale the locale
	 * @return the SEO url of the filter with the given locale.
	 */
	@Override
	@SuppressWarnings("PMD.ConfusingTernary")
	public String getSeoName(final Locale locale) {
		// return this.getDisplayName(locale).toLowerCase();
		// final String displayName = this.getDisplayName(locale).toLowerCase();

		// [TCH-83] Category SEO URL validation fails.
		//The logic here should is the same with the logic in SeoUrlBuilderImpl.findMostPreferableSeoName()
		String seoName = "";
		LocaleDependantFields ldf = this.getCategory().getLocaleDependantFieldsWithoutFallBack(locale);
		if (!StringUtils.isEmpty(ldf.getUrl())) {
			seoName = ldf.getUrl();

		} else if (!StringUtils.isEmpty(ldf.getDisplayName())) {
			seoName = ldf.getDisplayName();
			seoName = getUtility().escapeName2UrlFriendly(seoName, locale);

		} else {
			seoName = getUtility().escapeName2UrlFriendly(getCategory().getGuid(), locale);
		}

		return seoName;
	}

	/**
	 * This method is not used.
	 *
	 * @return the SEO identifier of the filter with the given locale.
	 */
	@Override
	public String getSeoId() {
		if (this.category == null) {
			return SeoConstants.CATEGORY_PREFIX + CatalogViewConstants.BRAND_FILTER_OTHERS;
		}
		return SeoConstants.CATEGORY_PREFIX + this.category.getCode();
	}

	/**
	 * Get the category.
	 *
	 * @return the category
	 */
	@Override
	public Category getCategory() {
		return category;
	}

	/**
	 * Set the category.
	 *
	 * @param category the category to set
	 */
	@Override
	public void setCategory(final Category category) {
		this.category = category;
	}

	/**
	 * Gets the {@link Catalog}.
	 *
	 * @return the {@link Catalog}
	 */
	@Override
	public Catalog getCatalog() {
		return catalog;
	}

	/**
	 * Sets the {@link Catalog}.
	 *
	 * @param catalog the {@link Catalog}
	 */
	@Override
	public void setCatalog(final Catalog catalog) {
		this.catalog = catalog;
	}

	/**
	 * Initializes the filter with a category code / catalog combination to
	 * uniquely identify the category.
	 *
	 * @param categoryCode the category code
	 * @param catalog the catalog in which the filter applies
	 * @throws EpCatalogViewRequestBindException if the UID is invalid
	 */
	@Override
	public void initializeWithCode(final String categoryCode, final Catalog catalog) throws EpCatalogViewRequestBindException {
		LOG.debug("Initializing category filter from Category Code and Catalog Code");
		final CategoryLookup categoryLookup = getBean(ContextIdNames.CATEGORY_LOOKUP);
		final Category category = categoryLookup.findByCategoryCodeAndCatalog(categoryCode, catalog);
		if (category == null) {
			throw new EpCatalogViewRequestBindException(
					String.format("Invalid category Code / Catalog combo %1$s %2$s", categoryCode, catalog));
		}
		this.category = category;
		setId(SeoConstants.CATEGORY_PREFIX + categoryCode);
	}

	@Override
	public void initialize(final Map<String, Object> properties) {
		this.setCategory((Category) properties.get("category"));
		this.setId(getSeoId());
	}

	@Override
	public Map<String, Object> parseFilterString(final String filterIdStr) {
		if (!filterIdStr.startsWith(SeoConstants.CATEGORY_PREFIX)) {
			throw new EpCatalogViewRequestBindException("Invalid category filter id:" + filterIdStr);
		}

		final String categoryIdStr = filterIdStr.substring(filterIdStr.indexOf(SeoConstants.CATEGORY_PREFIX)
				+ SeoConstants.CATEGORY_PREFIX.length());

		final CategoryLookup categoryLookup = getBean(ContextIdNames.CATEGORY_LOOKUP);
		Category category = categoryLookup.findByCategoryCodeAndCatalog(categoryIdStr, catalog);
		if (category == null) {
			throw new EpCatalogViewRequestBindException("Invalid category filter id:" + filterIdStr);
		}
		Map<String, Object> tokenMap = new HashMap<>();
		tokenMap.put("category", category);
		return tokenMap;
	}

}
