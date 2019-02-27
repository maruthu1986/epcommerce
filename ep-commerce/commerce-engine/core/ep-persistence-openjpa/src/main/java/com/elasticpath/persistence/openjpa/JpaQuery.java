/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.persistence.openjpa;

/**
 * JPA specific Query methods.
 *
 * @param <T> The type of result expected from the query
 */
public interface JpaQuery<T> extends com.elasticpath.persistence.api.Query<T> {
	/**
	 * Returns the underlying JPA query.
	 * @return the underlying JPA query
	 */
	javax.persistence.Query getJpaQuery();
}
