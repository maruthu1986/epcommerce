/*
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.service.catalog.impl;

import java.util.ArrayList;
import java.util.List;

import com.elasticpath.persistence.api.Entity;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.search.query.ProductAssociationSearchCriteria;

/**
 * Builds a ProductAssociationQuery from a ProductAssociationSearchCriteria.
 */
class ProductAssociationQueryBuilder {

	/**
	 * Builds the search query to use.
	 *
	 * @param criteria the search criteria
	 * @return a ProductAssociationQuery containing the query and the list of parameters to be used
	 */
	public ProductAssociationQuery buildSearchQuery(final ProductAssociationSearchCriteria criteria) {
		criteria.optimize();

		StringBuilder queryBuffer = new StringBuilder("SELECT pa FROM ProductAssociationImpl pa");
		appendForCatalogOnly(criteria, queryBuffer);
		List<Object> parameterList = buildWhereClause(criteria, queryBuffer);

		// order associations in the result by 'ordering' number in an ascending way (1, 2, 3, ...)
		queryBuffer.append(" ORDER BY pa.ordering ASC");

		return new ProductAssociationQuery(queryBuffer.toString(), parameterList);
	}

	/**
	 * Builds the query to find the count based on the search criteria.
	 *
	 * @param criteria the search criteria
	 * @return a ProductAssociationQuery containing the query and the list of parameters to be used
	 */
	public ProductAssociationQuery buildCountQuery(final ProductAssociationSearchCriteria criteria) {
		criteria.optimize();

		StringBuilder queryBuffer = new StringBuilder("SELECT COUNT(pa.uidPk) FROM ProductAssociationImpl pa");
		appendForCatalogOnly(criteria, queryBuffer);
		List<Object> parameterList = buildWhereClause(criteria, queryBuffer);

		return new ProductAssociationQuery(queryBuffer.toString(), parameterList);
	}

	private void appendForCatalogOnly(final ProductAssociationSearchCriteria criteria, final StringBuilder queryBuffer) {
		if (criteria.isWithinCatalogOnly()) {
			queryBuffer.append(", IN(pa.sourceProduct.productCategories) spc, IN(pa.targetProduct.productCategories) tpc");
		}
	}

	private List<Object> buildWhereClause(final ProductAssociationSearchCriteria criteria, final StringBuilder queryBuffer) {
		List<Object> parameterList = new ArrayList<>();
		boolean beginWhereClause = false;
		queryBuffer.append(" WHERE ");
		if (criteria.isWithinCatalogOnly()) {
			queryBuffer.append("tpc.category.catalog = pa.catalog AND spc.category.catalog = pa.catalog");
			beginWhereClause = true;
		}

		addParameter(queryBuffer, "pa.associationType", "=", criteria.getAssociationType(), parameterList, beginWhereClause);
		addParameter(queryBuffer, "pa.sourceProduct", "=", criteria.getSourceProduct(), parameterList, beginWhereClause);
		addParameter(queryBuffer, "pa.sourceProduct.code", "=", criteria.getSourceProductCode(), parameterList, beginWhereClause);
		addParameter(queryBuffer, "pa.targetProduct", "=", criteria.getTargetProduct(), parameterList, beginWhereClause);
		addParameter(queryBuffer, "pa.targetProduct.code", "=", criteria.getTargetProductCode(), parameterList, beginWhereClause);
		addParameter(queryBuffer, "pa.targetProduct.hidden", "=", criteria.isHidden(), parameterList, beginWhereClause);
		addParameter(queryBuffer, "pa.targetProduct.notSoldSeparately", "=", criteria.isNotSoldSeparately(), parameterList, beginWhereClause);
		addParameter(queryBuffer, "pa.catalog.code", "=", criteria.getCatalogCode(), parameterList, beginWhereClause);

		if (criteria.getStartDateBefore() != null) {
			boolean isNotFirstParameterClause = !parameterList.isEmpty();
			if (isNotFirstParameterClause || beginWhereClause) {
				queryBuffer.append(" AND ");
			}
			parameterList.add(criteria.getStartDateBefore());
			queryBuffer.append("(pa.startDateInternal IS NULL OR pa.startDateInternal <= ?");
			queryBuffer.append(parameterList.size());
			queryBuffer.append(')');
		}

		if (criteria.getEndDateAfter() != null) {
			boolean isNotFirstParameterClause = !parameterList.isEmpty();
			if (isNotFirstParameterClause || beginWhereClause) {
				queryBuffer.append(" AND ");
			}
			parameterList.add(criteria.getEndDateAfter());
			queryBuffer.append("(pa.endDateInternal IS NULL OR pa.endDateInternal > ?");
			queryBuffer.append(parameterList.size());
			queryBuffer.append(')');
		}
		return parameterList;
	}

	private void addParameter(final StringBuilder queryBuffer, final String paramName, final String operator, final Object object,
								final List<Object> parameterList, final boolean beginWhereClause) {
		if (object == null) {
			return;
		}
		boolean isNotFirstParameterClause = !parameterList.isEmpty();
		if (isNotFirstParameterClause || beginWhereClause) {
			queryBuffer.append(" AND ");
		}

		String addProperty = "";
		Object objectParam = object;
		if (object instanceof Persistable) {
			objectParam = ((Entity) object).getUidPk();
			addProperty = ".uidPk";
		}

		parameterList.add(objectParam);
		int index = parameterList.size();
		queryBuffer.append(paramName).append(addProperty).append(' ').append(operator).append(" ?").append(index);
	}
}
