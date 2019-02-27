/**
 * Copyright (c) Elastic Path Software Inc., 2012
 */
package com.elasticpath.service.query.impl;

import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.elasticpath.persistence.api.LoadTuner;
import com.elasticpath.service.query.MutableQueryCriteria;
import com.elasticpath.service.query.Relation;
import com.elasticpath.service.query.ResultType;

/**
 * Encapsulation of common elements created when constructing a query.
 * @param <T> the type class to query upon
 */
public class QueryCriteriaImpl<T> implements MutableQueryCriteria<T> {

	private static final long serialVersionUID = 1L;

	private Class<T> queryClass;
	private ResultType resultType;
	private Date modifiedAfter;
	private Date startDate;
	private Date endDate;
	private LoadTuner loadTuner;

	private final Set<Relation<?>> relations = new HashSet<>();

	@Override
	public Class<T> getQueryClass() {
		return this.queryClass;
	}

	@Override
	public ResultType getResultType() {
		return this.resultType;
	}

	@Override
	public Date getModifiedAfter() {
		return modifiedAfter;
	}

	@Override
	public Date getStartDate() {
		return startDate;
	}

	@Override
	public Date getEndDate() {
		return endDate;
	}

	@Override
	public void setQueryClass(final Class<T> queryClass) {
		this.queryClass = queryClass;
	}

	@Override
	public void setResultType(final ResultType resultType) {
		this.resultType = resultType;
	}

	@Override
	public void setModifiedAfter(final Date modifiedAfter) {
		this.modifiedAfter = modifiedAfter;
	}

	@Override
	public void setStartDate(final Date startDate) {
		this.startDate = startDate;
	}

	@Override
	public void setEndDate(final Date endDate) {
		this.endDate = endDate;
	}

	@Override
	public void setLoadTuner(final LoadTuner loadTuner) {
		this.loadTuner = loadTuner;
	}

	@Override
	public LoadTuner getLoadTuner() {
		return loadTuner;
	}

	@Override
	public Set<Relation<?>> getRelations() {
		return relations;
	}

	@Override
	public void addRelation(final Relation<?> relation) {
		relations.add(relation);
	}

	@Override
	public int hashCode() {
		return Objects.hash(endDate, loadTuner, modifiedAfter, queryClass.getName(), relations, resultType, startDate);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		QueryCriteriaImpl<T> other = (QueryCriteriaImpl<T>) obj;
		return Objects.equals(endDate, other.endDate)
			&& Objects.equals(loadTuner, other.loadTuner)
			&& Objects.equals(modifiedAfter, other.modifiedAfter)
			&& Objects.equals(queryClass.getName(), other.queryClass.getName())
			&& Objects.equals(relations, other.relations)
			&& Objects.equals(resultType, other.resultType)
			&& Objects.equals(startDate, other.startDate);
	}

}
