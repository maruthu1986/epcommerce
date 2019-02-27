/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.persistence.openjpa.impl;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.persistence.Query;

import org.apache.openjpa.persistence.FetchPlan;
import org.apache.openjpa.persistence.OpenJPAPersistence;
import org.apache.openjpa.persistence.OpenJPAQuery;

import com.elasticpath.persistence.api.EpPersistenceException;
import com.elasticpath.persistence.openjpa.JpaQuery;

/**
 * The JPA implementation of a query in ElasticPath. It is a wrap of <code>javax.persistence.Query</code>.
 *
 * @param <T> the expected type of elements returned by the query
 */
public class JpaQueryImpl<T> implements JpaQuery<T> {

	private final OpenJPAQuery<T> query;

	/**
	 * The default constructor.
	 *
	 * @param query the JPA query
	 */
	@SuppressWarnings("unchecked")
	public JpaQueryImpl(final Query query) {
		super();
		this.query = OpenJPAPersistence.cast(query);
	}

	@Override
	public void setParameter(final int position, final Object val) throws EpPersistenceException {
		this.query.setParameter(position, val);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> list() throws EpPersistenceException {
		return this.query.getResultList();
	}

	@Override
	public void setFetchSize(final int fetchSize) {
		query.getFetchPlan().setFetchBatchSize(fetchSize);
	}

	@Override
	public void setFirstResult(final int startPosition) {
		this.query.setFirstResult(startPosition);
	}

	@Override
	public void setMaxResults(final int maxResults) {
		this.query.setMaxResults(maxResults);
	}

	/**
	 * Add a fetch group to use for this query.
	 *
	 * @param group the name of a fetch group to add
	 */
	public void addFetchGroup(final String group) {
		query.getFetchPlan().addFetchGroup(group);
	}

	/**
	 * Clears the set of fetch group names to use when loading data.
	 */
	public void clearFetchGroups() {
		query.getFetchPlan().clearFetchGroups();
	}

	/**
	 * Remove the given fetch group.
	 *
	 * @param group the group to remove
	 */
	public void removeFetchGroup(final String group) {
		query.getFetchPlan().removeFetchGroup(group);
	}

	@Override
	public void setFetchGroups(final Set<String> groups) {
		FetchPlan fetchPlan = query.getFetchPlan();
		fetchPlan.clearFetchGroups();

		if (groups != null) {
			for (String fetchGroup : groups) {
				fetchPlan.addFetchGroup(fetchGroup);
			}
		}
	}

	@Override
	public void setFetchGroupFields(final Collection<String> fields) {
		FetchPlan fetchPlan = query.getFetchPlan();
		fetchPlan.clearFields();
		fetchPlan.addFields(fields);
	}

	@Override
	public int executeUpdate() {
		return query.executeUpdate();
	}

	@Override
	public Query getJpaQuery() {
		return query;
	}
}
