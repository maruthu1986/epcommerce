/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.service.search.solr.query;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermRangeQuery;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.exception.EpUnsupportedOperationException;
import com.elasticpath.domain.misc.SearchConfig;
import com.elasticpath.service.catalog.CatalogService;
import com.elasticpath.service.catalog.CategoryLookup;
import com.elasticpath.service.catalog.CategoryService;
import com.elasticpath.service.search.query.EpEmptySearchCriteriaException;
import com.elasticpath.service.search.query.ProductSearchCriteria;
import com.elasticpath.service.search.query.SearchCriteria;
import com.elasticpath.service.search.query.SortOrder;
import com.elasticpath.service.search.query.StandardSortBy;
import com.elasticpath.service.search.solr.SolrIndexConstants;
import com.elasticpath.service.search.solr.SolrQueryFactory;

/**
 * A query compose for products search.
 */
@SuppressWarnings("PMD.GodClass")
public class ProductQueryComposerImpl extends AbstractQueryComposerImpl {

	private CategoryLookup categoryLookup;
	private CategoryService categoryService;

	private CatalogService catalogService;

	private SolrQueryFactory solrQueryFactory;

	@Override
	protected boolean isValidSearchCriteria(final SearchCriteria searchCriteria) {
		return searchCriteria instanceof ProductSearchCriteria;
	}

	@Override


	public Query composeQueryInternal(final SearchCriteria searchCriteria, final SearchConfig searchConfig) {
		if (searchCriteria.getLocale() == null) {
			throw new EpServiceException("Locale not set on product search criteria");
		}

		final ProductSearchCriteria productSearchCriteria = (ProductSearchCriteria) searchCriteria;
		final BooleanQuery booleanQuery = new BooleanQuery();
		boolean hasSomeCriteria = false;

		if (productSearchCriteria.getCatalogSearchableLocales().isEmpty()) {
			hasSomeCriteria |= addSplitFieldToQuery(SolrIndexConstants.PRODUCT_NAME,
					productSearchCriteria.getProductName(), productSearchCriteria.getLocale(),
					searchConfig, booleanQuery, Occur.MUST, true);
		} else {
			hasSomeCriteria |= addSplitFieldToQueryWithMultipleLocales(SolrIndexConstants.PRODUCT_NAME,
					productSearchCriteria.getProductName(), productSearchCriteria.getCatalogSearchableLocales(),
					searchConfig, booleanQuery, Occur.MUST, true);
		}

		hasSomeCriteria |= addWholeFieldToQuery(SolrIndexConstants.PRODUCT_SKU_CODE, productSearchCriteria.getProductSku(), null,
				searchConfig, booleanQuery, Occur.MUST, true);

		hasSomeCriteria |= addFuzzyInvariableTerms(productSearchCriteria, booleanQuery, searchConfig);

		if (!hasSomeCriteria) {
			throw new EpEmptySearchCriteriaException("Empty search criteria is not allowed!");
		}

		return booleanQuery;
	}

	@Override
	public Query composeFuzzyQueryInternal(final SearchCriteria searchCriteria, final SearchConfig searchConfig) {
		if (searchCriteria.getLocale() == null) {
			throw new EpServiceException("Locale not set on product search criteria");
		}

		final ProductSearchCriteria productSearchCriteria = (ProductSearchCriteria) searchCriteria;
		final BooleanQuery booleanQuery = new BooleanQuery();
		boolean hasSomeCriteria = false;

		if (productSearchCriteria.getCatalogSearchableLocales().isEmpty()) {
			hasSomeCriteria |= addSplitFuzzyFieldToQuery(SolrIndexConstants.PRODUCT_NAME,
					productSearchCriteria.getProductName(), searchCriteria.getLocale(),
					searchConfig, booleanQuery, Occur.MUST, true);
		} else {
			hasSomeCriteria |= addSplitFuzzyFieldToQueryWithMultipleLocales(SolrIndexConstants.PRODUCT_NAME,
					productSearchCriteria.getProductName(), productSearchCriteria.getCatalogSearchableLocales(),
					searchConfig, booleanQuery, Occur.MUST, true);
		}

		hasSomeCriteria |= addWholeFuzzyFieldToQuery(SolrIndexConstants.PRODUCT_SKU_CODE, productSearchCriteria.getProductSku(), null,
				searchConfig, booleanQuery, Occur.MUST, true);

		hasSomeCriteria |= addFuzzyInvariableTerms(productSearchCriteria, booleanQuery, searchConfig);

		if (!hasSomeCriteria) {
			throw new EpEmptySearchCriteriaException("Empty search criteria is not allowed!");
		}

		return booleanQuery;
	}


	/**
	 * Add the invariable search terms to the product index query.
	 * @param productSearchCriteria the product search criteria
	 * @param booleanQuery the query being composed
	 * @param searchConfig the search configuration
	 * @return true if any fields were added to the query, false if not
	 */
	protected boolean addFuzzyInvariableTerms(final ProductSearchCriteria productSearchCriteria, final BooleanQuery booleanQuery,
			final SearchConfig searchConfig) {
		boolean hasSomeCriteria = false;

		hasSomeCriteria |= addWholeFieldToQuery(SolrIndexConstants.PRODUCT_CODE, productSearchCriteria.getProductCode(), null,
				searchConfig, booleanQuery, Occur.MUST, true);
		hasSomeCriteria |= addWholeFieldToQuery(SolrIndexConstants.BRAND_CODE, productSearchCriteria.getBrandCode(), null,
				searchConfig, booleanQuery, Occur.MUST, true);

		hasSomeCriteria |= addWholeFieldToQuery(SolrIndexConstants.CATALOG_CODE, productSearchCriteria.getCatalogCodes(),
				null, searchConfig, booleanQuery, Occur.MUST, true);

		hasSomeCriteria |= addWholeFieldToQuery(SolrIndexConstants.OBJECT_UID, productSearchCriteria.getFilteredUids(), null,
				searchConfig, booleanQuery, Occur.MUST_NOT, false);

		if (productSearchCriteria.isDisplayableOnly()) {
			if (StringUtils.isBlank(productSearchCriteria.getStoreCode())) {
				throw new EpUnsupportedOperationException("StoreCode must be defined to include displayable products");
			}
			hasSomeCriteria |= addWholeFieldToQuery(getIndexUtility().createDisplayableFieldName(SolrIndexConstants.DISPLAYABLE,
					productSearchCriteria.getStoreCode()), String.valueOf(true), null, searchConfig, booleanQuery, Occur.MUST,
					false);
		}

		if (productSearchCriteria.getProductUid() != null && productSearchCriteria.getProductUid() > 0) {
			hasSomeCriteria |= addWholeFieldToQuery(SolrIndexConstants.OBJECT_UID, String.valueOf(productSearchCriteria
					.getProductUid()), null, searchConfig, booleanQuery, Occur.MUST, false);
		}

		hasSomeCriteria |= addFeaturedFieldsToQuery(productSearchCriteria, booleanQuery, searchConfig);
		hasSomeCriteria |= addTermsForCategories(productSearchCriteria, booleanQuery, searchConfig);
		hasSomeCriteria |= addTermForActiveOnly(productSearchCriteria, booleanQuery);
		hasSomeCriteria |= addTermForInActiveOnly(productSearchCriteria, booleanQuery, searchConfig);

		return hasSomeCriteria;
	}

	/**
	 * <p>Adds the FEATURED and FEATURED_* fields to the query if the product search criteria specify
	 * that the caller wants only featured products.</p>
	 *
	 * @param productSearchCriteria the product search criteria
	 * @param booleanQuery the query object
	 * @param searchConfig the search configuration object
	 * @return true if any fields were added to the query, false if not
	 */
	protected boolean addFeaturedFieldsToQuery(
			final ProductSearchCriteria productSearchCriteria, final BooleanQuery booleanQuery, final SearchConfig searchConfig) {
		boolean hasSomeCriteria = false;
		if (productSearchCriteria.isOnlyFeaturedProducts()) {
			hasSomeCriteria |= addWholeFieldToQuery(SolrIndexConstants.FEATURED, String.valueOf(productSearchCriteria
					.isOnlyFeaturedProducts()), null, searchConfig, booleanQuery, Occur.MUST, true);

			// this really only makes sense with a category UID
			if (productSearchCriteria.isFeaturedOnlyInCategory() && productSearchCriteria.getCategoryUid() != null
					&& productSearchCriteria.getCategoryUid() > 0) {
				addWholeFieldToQuery(getIndexUtility().createFeaturedField(productSearchCriteria.getCategoryUid()),
						String.valueOf(0), null, searchConfig, booleanQuery, Occur.MUST_NOT, false);
			}
		}
		return hasSomeCriteria;
	}

	/**
	 * Add query for active products.
	 * @param productSearchCriteria search criteria
	 * @param booleanQuery boolean query
	 * @return return true if flag isActive activated
	 */
	protected boolean addTermForActiveOnly(final ProductSearchCriteria productSearchCriteria, final BooleanQuery booleanQuery) {
		boolean hasSomeCriteria = false;

		if (productSearchCriteria.isActiveOnly()) {
			hasSomeCriteria = true;
			// only query for products currently active
			Date now = new Date();
			BooleanQuery dateRangeQuery = getSolrQueryFactory().createTermsForStartEndDateRange(now);
			booleanQuery.add(dateRangeQuery, Occur.MUST);
		}
		return hasSomeCriteria;

	}

	private boolean addTermForInActiveOnly(final ProductSearchCriteria productSearchCriteria, final BooleanQuery booleanQuery,
			final SearchConfig searchConfig) {
		boolean hasSomeCriteria = false;

		if (productSearchCriteria.isInActiveOnly()) {
			final BooleanQuery innerQuery = new BooleanQuery();
			hasSomeCriteria = true;
			final String nowAnalyzed = getAnalyzer().analyze(new Date());

			// start date is in the future
			final Query futureStartDateQuery = TermRangeQuery.newStringRange(SolrIndexConstants.START_DATE, nowAnalyzed, null,
					true, true);
			futureStartDateQuery.setBoost(searchConfig.getBoostValue(SolrIndexConstants.START_DATE));
			innerQuery.add(futureStartDateQuery, Occur.SHOULD);

			// OR end date in the past
			final Query pastEndDateQuery = TermRangeQuery.newStringRange(SolrIndexConstants.END_DATE, null, nowAnalyzed, true, true);
			pastEndDateQuery.setBoost(searchConfig.getBoostValue(SolrIndexConstants.END_DATE));
			innerQuery.add(pastEndDateQuery, Occur.SHOULD);

			booleanQuery.add(innerQuery, Occur.MUST);
		}
		return hasSomeCriteria;
	}

	/**
	 * Adds the Category and Ancestor Category product search criteria to the search query, if they are specified.
	 * This implementation calls {@link #getCategoryCodeFromProductSearchCriteria(ProductSearchCriteria)}.
	 * @param productSearchCriteria the product search criteria
	 * @param booleanQuery the search query
	 * @param searchConfig the search configuration
	 * @return true if any category search terms were specified that resulted in a new query field being added, false if not
	 */
	protected boolean addTermsForCategories(final ProductSearchCriteria productSearchCriteria, final BooleanQuery booleanQuery,
			final SearchConfig searchConfig) {
		boolean hasSomeCriteria = false;

		Set<String> ancestorCategoryCodes = getAncestorCategoryCodesFromProductSearchCritiera(productSearchCriteria);
		String categoryCode = getCategoryCodeFromProductSearchCriteria(productSearchCriteria);
		final String catalogCode = productSearchCriteria.getCatalogCode();

		if (productSearchCriteria.isOnlySearchMasterCategory()) {
			final String masterCategoryFieldName = getIndexUtility()
					.createProductCategoryFieldName(
							SolrIndexConstants.MASTER_PRODUCT_CATEGORY,
							productSearchCriteria
									.getMasterCategoryCatalogCode());
			hasSomeCriteria |= addWholeFieldToQuery(masterCategoryFieldName, productSearchCriteria.getMasterCategoryCode(), null,
					searchConfig, booleanQuery, Occur.MUST, true);
		} else if (productSearchCriteria.isOnlyWithinDirectCategory()) {
			if (categoryCode != null) {
				final String categoryFieldName = getIndexUtility().createProductCategoryFieldName(SolrIndexConstants.PRODUCT_CATEGORY, catalogCode);
				hasSomeCriteria |= addWholeFieldToQuery(categoryFieldName, categoryCode, null,
						searchConfig, booleanQuery, Occur.MUST, true);
			}
			hasSomeCriteria |= addWholeFieldToQuery(SolrIndexConstants.PARENT_CATEGORY_CODES, ancestorCategoryCodes, null, searchConfig,
					booleanQuery, Occur.MUST, true);
		} else {
			final BooleanQuery innerQuery = new BooleanQuery();
			if (categoryCode != null) {
				final String categoryFieldName = getIndexUtility().createProductCategoryFieldName(SolrIndexConstants.PRODUCT_CATEGORY, catalogCode);
				hasSomeCriteria |= addWholeFieldToQuery(categoryFieldName, categoryCode, null,
						searchConfig, innerQuery, Occur.SHOULD, true);
			}
			hasSomeCriteria |= addWholeFieldToQuery(SolrIndexConstants.PARENT_CATEGORY_CODES, ancestorCategoryCodes, null, searchConfig,
					innerQuery, Occur.SHOULD, true);

			booleanQuery.add(innerQuery, Occur.MUST);
		}
		return hasSomeCriteria;
	}

	/**
	 * Retrieves the set of ancestor category codes from the Product Search Criteria.
	 * @param productSearchCriteria the product search criteria
	 * @return the set of ancestor category codes specified in the search criteria, or null if none were specified.
	 */
	protected Set<String> getAncestorCategoryCodesFromProductSearchCritiera(final ProductSearchCriteria productSearchCriteria) {
		Set<String> ancestorCategoryCodes = null;
		Set<Long> ancestorCategoryUids = productSearchCriteria.getAncestorCategoryUids();
		if (ancestorCategoryUids != null && !ancestorCategoryUids.isEmpty()) {
			ancestorCategoryCodes = new HashSet<>();
			for (Long categoryUid : ancestorCategoryUids) {
				ancestorCategoryCodes.add(findCategoryCodeByUid(categoryUid));
			}
		}
		return ancestorCategoryCodes;
	}

	/**
	 * Retrieves the category code from the product search criteria.
	 * @param productSearchCriteria the product search criteria
	 * @return the code of the category specified in the search criteria, or null if one was not specified
	 */
	protected String getCategoryCodeFromProductSearchCriteria(final ProductSearchCriteria productSearchCriteria) {
		Long directCategoryUid = productSearchCriteria.getDirectCategoryUid();
		if (directCategoryUid != null && directCategoryUid > 0) {
			return findCategoryCodeByUid(directCategoryUid);
		}
		return null;
	}

	private String findCategoryCodeByUid(final long directCategoryUid) {
		return getCategoryService().findCodeByUid(directCategoryUid);
	}

	@Override
	protected void fillSortFieldMap(final Map<String, SortOrder> sortFieldMap, final SearchCriteria searchCriteria, final SearchConfig searchConfig) {
		final String sortField = resolveSortField(searchCriteria);
		if (StringUtils.isNotEmpty(sortField)) {
			sortFieldMap.put(sortField, searchCriteria.getSortingOrder());
		}
	}

	private String resolveSortField(final SearchCriteria searchCriteria) {
		switch (searchCriteria.getSortingType().getOrdinal()) {
		case StandardSortBy.PRODUCT_START_DATE_ORDINAL:
			return SolrIndexConstants.START_DATE;
		case StandardSortBy.PRODUCT_END_DATE_ORDINAL:
			return SolrIndexConstants.END_DATE;
		case StandardSortBy.BRAND_NAME_ORDINAL:
			return SolrIndexConstants.SORT_BRAND_NAME_EXACT;
		case StandardSortBy.PRODUCT_DEFAULT_CATEGORY_NAME_ORDINAL:
			return SolrIndexConstants.SORT_PRODUCT_DEFAULT_CATEGORY_NAME_EXACT;
		case StandardSortBy.PRODUCT_CODE_ORDINAL:
			return SolrIndexConstants.PRODUCT_CODE;
		case StandardSortBy.PRODUCT_NAME_NON_LC_ORDINAL:
			return SolrIndexConstants.PRODUCT_NAME_NON_LC;
		case StandardSortBy.PRODUCT_DISPLAY_SKU_CODE_EXACT_ORDINAL:
			return SolrIndexConstants.PRODUCT_DISPLAY_SKU_CODE_EXACT;
		case StandardSortBy.PRODUCT_TYPE_NAME_ORDINAL:
			return SolrIndexConstants.PRODUCT_TYPE_NAME_EXACT;
		default:
			return null;
		}
	}

	/**
	 * @return SolrIndexConstants.PRODUCT_CODE
	 */
	@Override
	protected String getBusinessCodeField() {
		return SolrIndexConstants.PRODUCT_CODE;
	}

	/**
	 * Set the category service.
	 * @param categoryLookup the category service
	 */
	public void setCategoryLookup(final CategoryLookup categoryLookup) {
		this.categoryLookup = categoryLookup;
	}

	/**
	 * Gets the category service.
	 * @return the category service
	 */
	protected CategoryLookup getCategoryLookup() {
		return this.categoryLookup;
	}

	/**
	 * Sets the catalog service.
	 * @param catalogService the catalog service
	 */
	public void setCatalogService(final CatalogService catalogService) {
		this.catalogService = catalogService;
	}

	/**
	 * Gets the catalog service.
	 * @return the catalog service
	 */
	protected CatalogService getCatalogService() {
		return this.catalogService;
	}

	/**
	 * @param solrQueryFactory the solrQueryFactory to set
	 */
	public void setSolrQueryFactory(final SolrQueryFactory solrQueryFactory) {
		this.solrQueryFactory = solrQueryFactory;
	}

	/**
	 * @return the solrQueryFactory
	 */
	public SolrQueryFactory getSolrQueryFactory() {
		return solrQueryFactory;
	}

	protected CategoryService getCategoryService() {
		return categoryService;
	}

	public void setCategoryService(final CategoryService categoryService) {
		this.categoryService = categoryService;
	}
}
