/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.service.search.solr.query;

import java.util.Date;

import com.google.common.base.Strings;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermRangeQuery;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.domain.misc.SearchConfig;
import com.elasticpath.service.search.query.CategorySearchCriteria;
import com.elasticpath.service.search.query.EpEmptySearchCriteriaException;
import com.elasticpath.service.search.query.SearchCriteria;
import com.elasticpath.service.search.solr.SolrIndexConstants;

/**
 * A query compose for category search.
 */
public class CategoryQueryComposerImpl extends AbstractQueryComposerImpl {

	@Override
	protected boolean isValidSearchCriteria(final SearchCriteria searchCriteria) {
		return searchCriteria instanceof CategorySearchCriteria;
	}

	@Override
	public Query composeQueryInternal(final SearchCriteria searchCriteria, final SearchConfig searchConfig) {
		if (searchCriteria.getLocale() == null) {
			throw new EpServiceException("Locale not set on category search criteria");
		}

		final CategorySearchCriteria categorySearchCriteria = (CategorySearchCriteria) searchCriteria;
		final BooleanQuery booleanQuery = new BooleanQuery();
		boolean hasSomeCriteria = false;

		hasSomeCriteria |= addWholeFieldToQuery(SolrIndexConstants.CATEGORY_CODE, categorySearchCriteria.getCategoryCode(), null,
				searchConfig, booleanQuery, Occur.MUST, true);

		if (searchWithExactNameAndNoCatalogDefinedLocales(categorySearchCriteria)) {
			hasSomeCriteria |= addWholeFieldToQuery(SolrIndexConstants.CATEGORY_NAME_EXACT, categorySearchCriteria.getCategoryName(),
					searchCriteria.getLocale(), searchConfig, booleanQuery, Occur.MUST, true);
		} else if (searchWithNotExactNameAndNoCatalogDefinedLocales(categorySearchCriteria)) {
			hasSomeCriteria |= addSplitFieldToQuery(SolrIndexConstants.CATEGORY_NAME, categorySearchCriteria.getCategoryName(),
					searchCriteria.getLocale(), searchConfig, booleanQuery, Occur.MUST, true);
		} else if (searchWithExactNameAndCatalogDefinedLocales(categorySearchCriteria)) {
			hasSomeCriteria |= addWholeFieldToQueryWithMultipleLocales(SolrIndexConstants.CATEGORY_NAME_EXACT, categorySearchCriteria
					.getCategoryName(),
					categorySearchCriteria.getCatalogSearchableLocales(), searchConfig, booleanQuery, Occur.MUST, true);
		} else {
			hasSomeCriteria |= addSplitFieldToQueryWithMultipleLocales(SolrIndexConstants.CATEGORY_NAME, categorySearchCriteria.getCategoryName(),
					categorySearchCriteria.getCatalogSearchableLocales(), searchConfig, booleanQuery, Occur.MUST, true);
		}

		hasSomeCriteria |= addFuzzyInvariableTerms(categorySearchCriteria, booleanQuery, searchConfig);

		if (!hasSomeCriteria) {
			throw new EpEmptySearchCriteriaException("Empty search criteria is not allowed!");
		}

		return booleanQuery;
	}

	private boolean searchWithExactNameAndCatalogDefinedLocales(final CategorySearchCriteria categorySearchCriteria) {
		return categorySearchCriteria.isCategoryNameExact() && !categorySearchCriteria.getCatalogSearchableLocales().isEmpty();
	}

	private boolean searchWithNotExactNameAndNoCatalogDefinedLocales(final CategorySearchCriteria categorySearchCriteria) {
		return !categorySearchCriteria.isCategoryNameExact() && categorySearchCriteria.getCatalogSearchableLocales().isEmpty();
	}

	private boolean searchWithExactNameAndNoCatalogDefinedLocales(final CategorySearchCriteria categorySearchCriteria) {
		return categorySearchCriteria.isCategoryNameExact() && categorySearchCriteria.getCatalogSearchableLocales().isEmpty();
	}

	@Override
	public Query composeFuzzyQueryInternal(final SearchCriteria searchCriteria, final SearchConfig searchConfig) {
		if (searchCriteria.getLocale() == null) {
			throw new EpServiceException("Locale not set on category search criteria");
		}
		final CategorySearchCriteria categorySearchCriteria = (CategorySearchCriteria) searchCriteria;
		final BooleanQuery booleanQuery = new BooleanQuery();
		boolean hasSomeCriteria = false;

		hasSomeCriteria |= addWholeFuzzyFieldToQuery(SolrIndexConstants.CATEGORY_CODE, categorySearchCriteria.getCategoryCode(), null,
				searchConfig, booleanQuery, Occur.MUST, true);

		if (searchWithExactNameAndNoCatalogDefinedLocales(categorySearchCriteria)) {
			hasSomeCriteria |= addWholeFuzzyFieldToQuery(SolrIndexConstants.CATEGORY_NAME_EXACT, categorySearchCriteria.getCategoryName(),
					searchCriteria.getLocale(), searchConfig, booleanQuery, Occur.MUST, true);
		} else if (searchWithNotExactNameAndNoCatalogDefinedLocales(categorySearchCriteria)) {
			hasSomeCriteria |= addSplitFuzzyFieldToQuery(SolrIndexConstants.CATEGORY_NAME, categorySearchCriteria.getCategoryName(),
					searchCriteria.getLocale(), searchConfig, booleanQuery, Occur.MUST, true);
		} else if (searchWithExactNameAndCatalogDefinedLocales(categorySearchCriteria)) {
			hasSomeCriteria |= addWholeFuzzyFieldToQueryWithMultipleLocales(SolrIndexConstants.CATEGORY_NAME_EXACT, categorySearchCriteria
					.getCategoryName(),
					categorySearchCriteria.getCatalogSearchableLocales(), searchConfig, booleanQuery, Occur.MUST, true);
		} else {
			hasSomeCriteria |= addSplitFuzzyFieldToQueryWithMultipleLocales(SolrIndexConstants.CATEGORY_NAME, categorySearchCriteria
					.getCategoryName(),
					categorySearchCriteria.getCatalogSearchableLocales(), searchConfig, booleanQuery, Occur.MUST, true);
		}



		hasSomeCriteria |= addFuzzyInvariableTerms(categorySearchCriteria, booleanQuery, searchConfig);

		if (!hasSomeCriteria) {
			throw new EpEmptySearchCriteriaException("Empty search criteria is not allowed!");
		}

		return booleanQuery;
	}

	private boolean addFuzzyInvariableTerms(final CategorySearchCriteria categorySearchCriteria, final BooleanQuery booleanQuery,
			final SearchConfig searchConfig) {
		boolean hasSomeCriteria = false;

		hasSomeCriteria |= addTermForActiveOnly(categorySearchCriteria, booleanQuery, searchConfig);
		hasSomeCriteria |= addTermForInActiveOnly(categorySearchCriteria, booleanQuery, searchConfig);
		hasSomeCriteria |= addWholeFieldToQuery(SolrIndexConstants.OBJECT_UID, categorySearchCriteria.getFilteredUids(), null,
				searchConfig, booleanQuery, Occur.MUST_NOT, false);

		if (categorySearchCriteria.isDisplayableOnly()) {
			hasSomeCriteria |= addWholeFieldToQuery(SolrIndexConstants.DISPLAYABLE, String.valueOf(true), null, searchConfig,
					booleanQuery, Occur.MUST, false);
		}

		if (!Strings.isNullOrEmpty(categorySearchCriteria.getAncestorCode())) {
			hasSomeCriteria |= addWholeFieldToQuery(SolrIndexConstants.PARENT_CATEGORY_CODES, String.valueOf(categorySearchCriteria
					.getAncestorCode()), null, searchConfig, booleanQuery, Occur.MUST, true);
		}

		if (categorySearchCriteria.getCatalogCodes() != null) {
			hasSomeCriteria |= addWholeFieldToQuery(SolrIndexConstants.CATALOG_CODE, categorySearchCriteria
					.getCatalogCodes(), null, searchConfig, booleanQuery, Occur.MUST, false);
		}

		if (categorySearchCriteria.isLinked() != null) {
			hasSomeCriteria |= addWholeFieldToQuery(SolrIndexConstants.CATEGORY_LINKED, String.valueOf(categorySearchCriteria.isLinked()), null,
					searchConfig, booleanQuery, Occur.MUST, false);
		}

		return hasSomeCriteria;
	}

	private boolean addTermForActiveOnly(final CategorySearchCriteria categorySearchCriteria, final BooleanQuery booleanQuery,
			final SearchConfig searchConfig) {
		boolean hasSomeCriteria = false;

		if (categorySearchCriteria.isActiveOnly()) {
			hasSomeCriteria = true;
			final String nowAnalyzed = getAnalyzer().analyze(new Date());

			// start date is in the past
			final Query startDateQuery = TermRangeQuery.newStringRange(SolrIndexConstants.START_DATE, null, nowAnalyzed, true, true);
			startDateQuery.setBoost(searchConfig.getBoostValue(SolrIndexConstants.START_DATE));
			booleanQuery.add(startDateQuery, Occur.MUST);

			// AND end date is NOT in the past
			final Query endDateQuery = TermRangeQuery.newStringRange(SolrIndexConstants.END_DATE, null, nowAnalyzed, true, true);
			endDateQuery.setBoost(searchConfig.getBoostValue(SolrIndexConstants.END_DATE));
			booleanQuery.add(endDateQuery, Occur.MUST_NOT);
		}
		return hasSomeCriteria;
	}

	private boolean addTermForInActiveOnly(final CategorySearchCriteria categorySearchCriteria, final BooleanQuery booleanQuery,
			final SearchConfig searchConfig) {
		boolean hasSomeCriteria = false;

		if (categorySearchCriteria.isInActiveOnly()) {
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
}