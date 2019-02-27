/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.service.catalogview.impl;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.solr.client.solrj.util.ClientUtils;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalogview.CatalogViewRequest;
import com.elasticpath.domain.catalogview.search.SearchRequest;
import com.elasticpath.domain.search.Synonym;
import com.elasticpath.domain.search.SynonymGroup;
import com.elasticpath.service.catalog.CatalogService;
import com.elasticpath.service.catalog.CategoryService;
import com.elasticpath.service.catalogview.SearchCriteriaFactory;
import com.elasticpath.service.catalogview.StoreConfig;
import com.elasticpath.service.search.SynonymGroupService;
import com.elasticpath.service.search.query.CategorySearchCriteria;
import com.elasticpath.service.search.query.KeywordSearchCriteria;
import com.elasticpath.service.search.query.ProductSearchCriteria;
import com.elasticpath.service.search.solr.SolrIndexConstants;

/**
 * 
 * The default implementation for {@link SearchCriteriaFactory} which provides methods to create search criteria.
 */
public class SearchCriteriaFactoryImpl implements SearchCriteriaFactory {

	private static final boolean ENABLE_SYNONYM_GROUPS = System.getProperty(SolrIndexConstants.SOLR_ENABLE_SYNONYM_GROUPS) != null;

	private SynonymGroupService synonymGroupService;

	private CatalogService catalogService;

	private StoreConfig storeConfig;

	private CategoryService categoryService;

	private BeanFactory beanFactory;

	@Override
	public CategorySearchCriteria createCategorySearchCriteria(final CatalogViewRequest catalogViewRequest) {

		final CategorySearchCriteria searchCriteria = beanFactory.getBean(ContextIdNames.CATEGORY_SEARCH_CRITERIA);

		
		searchCriteria.setCategoryName(extractKeywords(catalogViewRequest));
		
		

		if (catalogViewRequest.getCategoryUid() > 0) {
			String code = getCategoryService().findCodeByUid(catalogViewRequest.getCategoryUid());
			searchCriteria.setAncestorCode(code);
		}

		searchCriteria.setLocale(catalogViewRequest.getLocale());
		searchCriteria.setCategoryNameExact(true);
		searchCriteria.setDisplayableOnly(true);
		searchCriteria.setCatalogCodes(Collections.singleton(getStoreConfig().getStore().getCatalog().getCode()));
		return searchCriteria;
	}

	@Override
	public ProductSearchCriteria createProductSearchCriteria(final CatalogViewRequest searchRequest) {
		return beanFactory.getBean(ContextIdNames.PRODUCT_SEARCH_CRITERIA);
	}

	@Override
	public KeywordSearchCriteria createKeywordProductCategorySearchCriteria(final CatalogViewRequest catalogViewRequest) {
		final KeywordSearchCriteria searchCriteria = beanFactory.getBean(ContextIdNames.KEYWORD_SEARCH_CRITERIA);

		String keywords = extractKeywords(catalogViewRequest);
		final Catalog catalog = getStoreConfig().getStore().getCatalog();

		if (ENABLE_SYNONYM_GROUPS) {
			keywords = addSynonyms(keywords, catalog);
		}

		// Set the store code for price filters
		searchCriteria.setStoreCode(getStoreConfig().getStoreCode());

		searchCriteria.setKeyword(keywords);
		if (catalogViewRequest.getCategoryUid() > 0) {
			searchCriteria.setCategoryUid(catalogViewRequest.getCategoryUid());
		}

		searchCriteria.setCatalogCode(catalog.getCode());
		searchCriteria.setFuzzySearchDisabled(false);
		return searchCriteria;
	}
	
	private String extractKeywords(final CatalogViewRequest catalogViewRequest) {
		if (catalogViewRequest instanceof SearchRequest)  {
			return ClientUtils.escapeQueryChars(((SearchRequest) catalogViewRequest).getKeyWords());
		} 
		return null;
	}

	/**
	 * Adds synonyms to the given set of keywords.
	 *
	 * @param keywords the keywords used in the search
	 * @param catalog the catalog
	 * @return the keywords with synonyms added
	 */
	String addSynonyms(final String keywords, final Catalog catalog) {
		String result = keywords;
		Collection<SynonymGroup> synonymGroups = new HashSet<>();
		synonymGroups.addAll(synonymGroupService.findAllSynonymGroupForCatalog(catalog.getUidPk()));
		for (Catalog master : catalogService.findMastersUsedByVirtualCatalog(catalog.getCode())) {
			synonymGroups.addAll(synonymGroupService.findAllSynonymGroupForCatalog(master.getUidPk()));
		}
		try {
			result = findSynonyms(synonymGroups, keywords); // NOPMD: There is no string concatenation here.
		} catch (IOException e) {
			throw new EpServiceException("Error while adding synonyms.", e);
		}
		return result;
	}

	/**
	 * Runs through the given synonym groups to find if there are any synonyms configured for the given keywords and, if so, adds the synonyms to the
	 * string of keywords. This implementation splits up the keyword string using the tokenizer specified in the the Lucene Analyzer that is
	 * configured through Spring to be the Synonym Analyzer.
	 * 
	 * @param synonymGroups groups of synonym configurations
	 * @param keywords the keyword string to which synonyms should be appended
	 * @return the modified keyword string
	 * @throws IOException if there is a problem tokenizing the keyword string
	 */
	String findSynonyms(final Collection<SynonymGroup> synonymGroups, final String keywords) throws IOException {
		if (synonymGroups == null || synonymGroups.isEmpty() || keywords == null) {
			return keywords;
		}
		final StringBuilder synonymStr = new StringBuilder();
		synonymStr.append(keywords);

		final Analyzer analyzer = beanFactory.getBean(ContextIdNames.SYNONYM_ANALYZER);
		final TokenStream tokenStream = analyzer.tokenStream(null, new StringReader(keywords));

		CharTermAttribute termAtt = tokenStream.addAttribute(CharTermAttribute.class);
		while (tokenStream.incrementToken()) {
			if (termAtt.length() <= 0) {
				continue;
			}
			final String term = termAtt.toString();
			for (SynonymGroup synonymGroup : synonymGroups) {
				if (term.equalsIgnoreCase(synonymGroup.getConceptTerm())) {
					for (Synonym synonym : synonymGroup.getSynonyms()) {
						String escSynonym = ClientUtils.escapeQueryChars(" " + synonym.getSynonym().trim());
						synonymStr.append(escSynonym);
					}
				}
			}
		}
		return synonymStr.toString();
	}

	/**
	 * @return the {@link SynonymGroupService} instance to use
	 */
	protected SynonymGroupService getSynonymGroupService() {
		return synonymGroupService;
	}

	/**
	 * Sets the {@link SynonymGroupService} instance to use.
	 * 
	 * @param synonymGroupService the {@link SynonymGroupService} instance to use
	 */
	public void setSynonymGroupService(final SynonymGroupService synonymGroupService) {
		this.synonymGroupService = synonymGroupService;
	}

	/**
	 * Sets the {@link CatalogService} instance to use.
	 * 
	 * @param catalogService the {@link CatalogService} instance to use
	 */
	public void setCatalogService(final CatalogService catalogService) {
		this.catalogService = catalogService;
	}

	/**
	 * Sets the store configuration to be used as a context for searching.
	 * 
	 * @param storeConfig the store configuration.
	 */
	public void setStoreConfig(final StoreConfig storeConfig) {
		this.storeConfig = storeConfig;
	}

	/**
	 * Returns the store configuration that provides the context for the catalog to view.
	 * 
	 * @return the store configuration.
	 */
	protected StoreConfig getStoreConfig() {
		return storeConfig;
	}

	/**
	 * Gets the {@link CategoryService} instance.
	 * 
	 * @return the {@link CategoryService} instance
	 */
	protected CategoryService getCategoryService() {
		return categoryService;
	}

	/**
	 * Sets the {@link CategoryService} instance to use.
	 * 
	 * @param categoryService the {@link CategoryService instance to use
	 */
	public void setCategoryService(final CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
}
