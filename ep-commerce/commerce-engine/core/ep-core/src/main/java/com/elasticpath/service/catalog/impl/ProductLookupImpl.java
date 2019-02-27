/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.service.catalog.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductLoadTuner;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.service.catalog.ProductLookup;
import com.elasticpath.service.misc.FetchPlanHelper;
import com.elasticpath.service.query.CriteriaBuilder;
import com.elasticpath.service.query.QueryResult;
import com.elasticpath.service.query.QueryService;
import com.elasticpath.service.query.ResultType;
import com.elasticpath.service.query.relations.ProductRelation;

/**
 * {@link com.elasticpath.service.catalog.ProductLookup} implementation that reads products directly from the persistence engine.
 */
public class ProductLookupImpl implements ProductLookup {

	private static final int BATCH_SIZE = 2000; //Batch size is 2000 to remain under the MSSQL 2100 parameter limit.
	private QueryService<Product> queryService;
	private FetchPlanHelper fetchPlanHelper;
	private ProductLoadTuner productLoadTuner;
	private PersistenceEngine persistenceEngine;


	@Override
	@SuppressWarnings("unchecked")
	public <P extends Product> P findByUid(final long uidpk) throws EpServiceException {
		getFetchPlanHelper().configureProductFetchPlan(getProductLoadTuner());
		try {
			return (P) getPersistenceEngine().get(ProductImpl.class, uidpk);
		} finally {
			getFetchPlanHelper().clearFetchPlan();
		}
	}

	@Override
	public <P extends Product> List<P> findByUids(final Collection<Long> uidPks) throws EpServiceException {
		List<P> products = new ArrayList<>();
		List<List<Long>> batchesOfUids = Lists.partition(new ArrayList<>(uidPks), BATCH_SIZE);
		for (List<Long> batchOfUids : batchesOfUids) {
			products.addAll(this.<P>findByUidsInternal(batchOfUids));
		}
		return products;
	}

	/**
	 * Internal implementation that finds products by uidPks.
	 *
	 * @param uidPks the products' primary keys
	 * @param <P> the genericized Product sub-class that this finder will return
	 * @return the products that match the given primary keys, otherwise an empty list
	 * @throws com.elasticpath.base.exception.EpServiceException - in case of any errors
	 */
	protected <P extends Product> List<P> findByUidsInternal(final Collection<Long> uidPks) throws EpServiceException {
		getFetchPlanHelper().configureProductFetchPlan(getProductLoadTuner());
		try {
			QueryResult<P> queryResult = getQueryService().query(productCriteria()
					.with(ProductRelation.having().uids(uidPks)).returning(ResultType.ENTITY));
			return queryResult.getResults();
		} finally {
			getFetchPlanHelper().clearFetchPlan();
		}
	}

	@Override
	public <P extends Product> P findByGuid(final String guid) throws EpServiceException {
		if (guid == null) {
			throw new EpServiceException("Cannot retrieve null code.");
		}

		getFetchPlanHelper().configureProductFetchPlan(getProductLoadTuner());
		try {
			QueryResult<P> queryResult = getQueryService().query(productCriteria()
					.with(ProductRelation.having().codes(guid)).returning(ResultType.ENTITY));
			return queryResult.getSingleResult();
		} finally {
			getFetchPlanHelper().clearFetchPlan();
		}

	}

	/**
	 * Get the criteria builder for a product.
	 *
	 * @return a product criteria builder
	 */
	protected CriteriaBuilder<Product> productCriteria() {
		return CriteriaBuilder.criteriaFor(Product.class);
	}


	protected QueryService<Product> getQueryService() {
		return queryService;
	}

	public void setQueryService(final QueryService<Product> queryService) {
		this.queryService = queryService;
	}

	protected FetchPlanHelper getFetchPlanHelper() {
		return fetchPlanHelper;
	}

	public void setFetchPlanHelper(final FetchPlanHelper fetchPlanHelper) {
		this.fetchPlanHelper = fetchPlanHelper;
	}

	protected ProductLoadTuner getProductLoadTuner() {
		return productLoadTuner;
	}

	public void setProductLoadTuner(final ProductLoadTuner productLoadTuner) {
		this.productLoadTuner = productLoadTuner;
	}

	protected PersistenceEngine getPersistenceEngine() {
		return persistenceEngine;
	}

	public void setPersistenceEngine(final PersistenceEngine persistenceEngine) {
		this.persistenceEngine = persistenceEngine;
	}
}
