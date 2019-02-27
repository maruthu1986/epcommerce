/*
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.persistence.openjpa.impl;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.openjpa.datacache.DataCache;
import org.apache.openjpa.kernel.Broker;
import org.apache.openjpa.persistence.InvalidStateException;
import org.apache.openjpa.persistence.JPAFacadeHelper;
import org.apache.openjpa.persistence.OpenJPAEntityManager;
import org.apache.openjpa.persistence.OpenJPAEntityManagerFactory;
import org.apache.openjpa.persistence.OpenJPAPersistence;
import org.apache.openjpa.persistence.OpenJPAQuery;
import org.apache.openjpa.persistence.QueryResultCache;
import org.apache.openjpa.persistence.StoreCache;
import org.apache.openjpa.persistence.StoreCacheImpl;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.PlatformTransactionManager;

import com.elasticpath.persistence.api.ChangeType;
import com.elasticpath.persistence.api.EpPersistenceException;
import com.elasticpath.persistence.api.FlushMode;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.persistence.api.PersistenceEngineOperationListener;
import com.elasticpath.persistence.api.PersistenceSession;
import com.elasticpath.persistence.api.PersistenceSessionFactory;
import com.elasticpath.persistence.openjpa.JpaPersistenceEngineInternal;
import com.elasticpath.persistence.openjpa.JpaPersistenceSession;
import com.elasticpath.persistence.openjpa.PersistenceInterceptor;
import com.elasticpath.persistence.openjpa.QueryParameterEscaper;

/**
 * The JPA implementation of <code>PersistenceEngine</code>.
 */
@SuppressWarnings({ "unchecked", "PMD.TooManyMethods", "PMD.ExcessiveClassLength", "PMD.GodClass" })
public class JpaPersistenceEngineImpl implements JpaPersistenceEngineInternal {
	private static final String LOG_NAMED_QUERY = "Querying datastore with named query: ";

	private static final Logger LOG = Logger.getLogger(JpaPersistenceEngineImpl.class);

	private static final String CAUGHT_AN_EXCEPTION = "Caught an exception";
	private static final int EXPRESSION_ESTIMATE_LENGTH = 8;
	private static final int MAX_ALLOW_EXPRESSIONS_IN_QUERY = 900;
	private static final String NAMED_PARAMETER_PREFIX = ":";

	private EntityManager entityManager;
	private PersistenceSessionFactory sessionFactory;
	private PlatformTransactionManager txManager;
	private List<PersistenceEngineOperationListener> operationListeners = Collections.emptyList();

	private final QueryParameterEscaper paramEscaper = new QueryParameterEscaperImpl();

	/**
	 * Update/delete all objects according to the given query.
	 *
	 * @param sql the HQL query
	 * @param parameters the prameters to be used with the criteria
	 * @return the number of instances updated/deleted
	 * @throws EpPersistenceException - in case of persistence errors
	 */
	@Override
	public int bulkUpdate(final String sql, final Object... parameters) throws EpPersistenceException {
		int result;
		OpenJPAQuery<?> query = OpenJPAPersistence.cast(getEntityManager().createQuery(sql));
		ChangeType changeType = getChangeTypeFor(query);
		fireBeginBulkOperationEvent(query, parameters, changeType);

		try {
			if (parameters != null) {
				setQueryParameters(query, parameters);
			}

			result = query.executeUpdate();
		} catch (final DataAccessException e) {
			throw new EpPersistenceException(CAUGHT_AN_EXCEPTION, e);
		} finally {
			fireEndBulkOperationEvent(changeType);
		}
		return result;
	}

	/**
	 * Clear the cache.
	 */
	@Override
	public void clear() {
		entityManager.clear();
	}

	/**
	 * Delete the given persistent instance.
	 *
	 * @param object the instance to delete
	 * @throws EpPersistenceException - in case of persistence errors
	 */
	@Override
	public void delete(final Persistable object) throws EpPersistenceException {
		try {

			fireBeginSingleOperationEvent(object, ChangeType.DELETE);

			Object persistObject = entityManager.getReference(object.getClass(), object.getUidPk());
			entityManager.remove(persistObject);

			fireEndSingleOperationEvent(object, ChangeType.DELETE);
		} catch (final PersistenceException e) {
			throw new EpPersistenceException(CAUGHT_AN_EXCEPTION, e);
		}
	}

	/**
	 * Execute the bulk update with the specified query.
	 *
	 * @param sql the JPQL statement
	 * @return the number of entities effective by the operation.
	 * @throws EpPersistenceException - in case of persistence errors
	 */
	public int executeSessionUpdate(final String sql) throws EpPersistenceException {
		return bulkUpdate(sql);
	}

	/**
	 * Flush the cache.
	 */
	@Override
	public void flush() {
		entityManager.flush();
	}

	/**
	 * Get a persistent instance with the given id. Return null if no matching record exists.
	 *
	 * @param <T> the type of the object
	 * @param persistenceClass the persistent class of the given id.
	 * @param uidPk the persistent instance id.
	 * @return the persistent instance
	 * @throws EpPersistenceException - in case of persistence errors
	 */
	@Override
	public <T extends Persistable> T get(final Class<T> persistenceClass, final long uidPk) throws EpPersistenceException {
		try {
			return entityManager.find(persistenceClass, Long.valueOf(uidPk));
		} catch (final DataAccessException e) {
			throw new EpPersistenceException(CAUGHT_AN_EXCEPTION, e);
		}
	}

	/**
	 * Returns JPA Entity Manager.
	 *
	 * @return JpaSession wrapper of JPA EntityManager
	 */
	@Override
	public PersistenceSession getPersistenceSession() {
		return getSessionFactory().createPersistenceSession();
	}

	/**
	 * Returns a shared session.
	 *
	 * @return a session
	 */
	@Override
	public PersistenceSession getSharedPersistenceSession() {
		return new ListeningPersistenceSessionImpl(entityManager, txManager, true, this);
	}

	/**
	 * Initialize the given object.
	 *
	 * @param object the object to initialize.
	 */
	@Override
	public void initialize(final Object object) {
		entityManager.refresh(object);
	}

	/**
	 * Load a persistent instance with the given id. Throw an unrecoverable exception if there is no matching database row.
	 *
	 * @param <T> the type of the object
	 * @param persistenceClass the persistent class of the given id.
	 * @param uidPk The persistent instance id.
	 * @return the persistent instance
	 * @throws EpPersistenceException - in case of persistence errors
	 */
	@Override
	public <T extends Persistable> T load(final Class<T> persistenceClass, final long uidPk) throws EpPersistenceException {
		try {
			return entityManager.find(persistenceClass, Long.valueOf(uidPk));
		} catch (final DataAccessException e) {
			throw new EpPersistenceException(CAUGHT_AN_EXCEPTION, e);
		}
	}

	/**
	 * Load a persistent instance with the given id. Throw an unrecoverable exception if there is
	 * no matching database row. This method will create a new session (EntityManager) to execute
	 * the query, and close the new session when completed.
	 *
	 * @param <T> the type of the object
	 * @param persistenceClass the persistent class of the given id.
	 * @param uidPk the persistent instance id.
	 * @return the persistent instance
	 * @throws EpPersistenceException in case of persistence errors
	 */
	@Override
	public <T extends Persistable> T loadWithNewSession(final Class<T> persistenceClass, final long uidPk) {
		EntityManager newEntityManager = null;
		try {
			PersistenceSession newSession = sessionFactory.createPersistenceSession();
			newEntityManager = ((JpaPersistenceSession) newSession).getEntityManager();
			return newEntityManager.find(persistenceClass, uidPk);
		} catch (final DataAccessException e) {
			throw new EpPersistenceException(CAUGHT_AN_EXCEPTION, e);
		} finally {
			if (newEntityManager != null) {
				newEntityManager.close();
			}
		}
	}

	/**
	 * Merge the given persistent instance.
	 *
	 * @param <T> the type of the object
	 * @param object the instance to merge
	 * @throws EpPersistenceException - in case of persistence errors
	 * @return the merged object
	 */
	@Override
	public <T extends Persistable> T merge(final T object) throws EpPersistenceException {
		try {
			fireBeginSingleOperationEvent(object, ChangeType.UPDATE);

			if (object instanceof PersistenceInterceptor) {
				((PersistenceInterceptor) object).executeBeforePersistAction();
			}
			T result = entityManager.merge(object);

			fireEndSingleOperationEvent(object, ChangeType.UPDATE);

			return result;
		} catch (final DataAccessException e) {
			throw new EpPersistenceException(CAUGHT_AN_EXCEPTION, e);
		}
	}

	/**
	 * Retrieve a list of persistent instances with the given list of queries.
	 * <p>
	 * Note: all queries must return the same type of data. Their results will be merged into the list returned by this method.
	 *
	 * @param <T> the object's type to retrieve
	 * @param queries the list of queries
	 * @param parameters the parameters to be used with the given query
	 * @return a list of persistent instances.
	 * @throws EpPersistenceException - in case of persistence errors
	 */
	@Override
	public <T> List<T> retrieve(final List<String> queries, final Object... parameters) throws EpPersistenceException {
		final List<T> result = new ArrayList<>();
		for (String query : queries) {
			final List<T> objects = retrieve(query, parameters);
			result.addAll(objects);
		}

		return result;
	}

	/**
	 * Retrieve a list of persistent instances with the specified query and bounds, namely, the maximum number of rows to retrieve and / or the first
	 * row to retrieve. This help to support pagination.
	 *
	 * @param <T> the object's type to retrieve
	 * @param queryStr the HQL query string to be executed
	 * @param firstResult the first row to retrieve
	 * @param maxResults the maximum number of rows to retrieve
	 * @return a list of persistent instances
	 * @throws EpPersistenceException in case of persistence errors
	 */
	@Override
	public <T> List<T> retrieve(final String queryStr, final int firstResult, final int maxResults) throws EpPersistenceException {
		try {
			Query query = entityManager.createQuery(queryStr);
			query.setFirstResult(firstResult);
			query.setMaxResults(maxResults);
			query.setHint("openjpa.hint.OracleSelectHint", "/*+ first_rows(" + maxResults + ") */");
			return getResults(query);
		} catch (final DataAccessException e) {
			throw new EpPersistenceException(CAUGHT_AN_EXCEPTION, e);
		}
	}

	/**
	 * Retrieve a list of persistent instances with the specified query.
	 *
	 * @param <T> the object's type to retrieve
	 * @param query the HQL query
	 * @param parameters the parameters to be used with the criteria
	 * @return a list of persistent instances
	 * @throws EpPersistenceException in case of persistence errors
	 */
	@Override
	public <T> List<T> retrieve(final String query, final Object... parameters) throws EpPersistenceException {
		try {
			Query newQuery = entityManager.createQuery(query);
			setQueryParameters(newQuery, parameters);
			return getResults(newQuery);
		} catch (final DataAccessException e) {
			throw new EpPersistenceException(CAUGHT_AN_EXCEPTION, e);
		}
	}

	@Override
	public <T> List<T> retrieveWithNamedParameters(final String query, final Map<String, ?> parameters) throws EpPersistenceException {
		try {
			Query newQuery = entityManager.createQuery(query);
			for (final Entry<String, ?> entry : parameters.entrySet()) {
				newQuery.setParameter(entry.getKey(), entry.getValue());
			}

			return getResults(newQuery);
		} catch (final DataAccessException e) {
			throw new EpPersistenceException(CAUGHT_AN_EXCEPTION, e);
		}
	}

	/**
	 * Retrieve a list of persistent instances with the specified query.
	 *
	 * @param <T> the object's type to retrieve
	 * @param <E> the type of values used in the query
	 * @param query the JPQL query
	 * @param values the collection of values
	 * @param listParameterName the name of the parameter for the list values
	 * @param parameters the parameters to be used with the criteria
	 * @param firstResult the first row to retrieve
	 * @param maxResults the maximum number of rows to retrieve
	 * @return a list of persistent instances.
	 * @throws EpPersistenceException - in case of persistence errors
	 */
	@Override
	public <T, E> List<T> retrieveWithList(final String query, final String listParameterName,
										   final Collection<E> values, final Object[] parameters, final int firstResult, final int maxResults)
		throws EpPersistenceException {
		try {
			OpenJPAQuery<T> jpaQuery = OpenJPAPersistence.cast(entityManager.createQuery(query));
			String valuesList = getInParameterValues(values);
			if (StringUtils.isEmpty(valuesList)) {
				valuesList = "''";
			}
			Query newQuery = insertListIntoQuery(jpaQuery, listParameterName, valuesList);
			newQuery.setFirstResult(firstResult);
			newQuery.setMaxResults(maxResults);
			newQuery.setHint("openjpa.hint.OracleSelectHint", "/*+ first_rows(" + maxResults + ") */");
			setQueryParameters(newQuery, parameters);
			return getResults(newQuery);
		} catch (final DataAccessException e) {
			throw new EpPersistenceException(CAUGHT_AN_EXCEPTION, e);
		}
	}

	/**
	 * Retrieve a list of persistent instances with the specified query. This method will create a new
	 * session (EntityManager) to excute the query, and close the new session when completed.
	 *
	 * @param <T> the object's type to retrieve
	 * @param query the HQL query
	 * @param parameters the prameters to be used with the criteria
	 * @return a list of persistent instances.
	 * @throws EpPersistenceException - in case of persistence errors
	 */
	@Override
	public <T> List<T> retrieveWithNewSession(final String query, final Object... parameters) throws EpPersistenceException {

		EntityManager newEntityManager = null;
		try {
			PersistenceSession newSession = sessionFactory.createPersistenceSession();
			newEntityManager = ((JpaPersistenceSession) newSession).getEntityManager();
			Query newQuery = newEntityManager.createQuery(query);
			setQueryParameters(newQuery, parameters);
			return getResults(newQuery);
		} catch (final DataAccessException e) {
			throw new EpPersistenceException(CAUGHT_AN_EXCEPTION, e);
		} finally {
			if (newEntityManager != null) {
				newEntityManager.close();
			}
		}
	}

	/**
	 * Retrieve a list of persistent instances with the specified query. This method will create a new
	 * session (EntityManager) to excute the query, and close the new session when completed.
	 *
	 * @param <T> the object's type to retrieve
	 * @param <E> the type of values used in the query
	 * @param query the HQL query
	 * @param values the collection of values
	 * @param listParameterName the name of the parameter for the list values
	 * @param parameters the parameters to be used with the criteria
	 * @return a list of persistent instances.
	 * @throws EpPersistenceException - in case of persistence errors
	 */
	@Override
	public <T, E> List<T> retrieveWithListWithNewSession(final String query, final String listParameterName, final Collection<E> values,
														 final Object... parameters) throws EpPersistenceException {
		EntityManager newEntityManager = null;
		try {
			PersistenceSession newSession = sessionFactory.createPersistenceSession();
			newEntityManager = ((JpaPersistenceSession) newSession).getEntityManager();

			int parameterCount = 0;
			if (parameters != null) {
				parameterCount = parameters.length;
			}

			List<String> listParameters = splitCollection(values, parameterCount);
			final List<T> result = new ArrayList<>();
			for (String listParameter : listParameters) {
				OpenJPAQuery<T> jpaQuery = OpenJPAPersistence.cast(newEntityManager.createQuery(query));
				Query newQuery = insertListIntoQuery(jpaQuery, listParameterName, listParameter);
				setQueryParameters(newQuery, parameters);
				result.addAll(JpaPersistenceEngineImpl.<T>getResults(newQuery));
			}
			return result;
		} catch (final DataAccessException e) {
			throw new EpPersistenceException(CAUGHT_AN_EXCEPTION, e);
		} finally {
			if (newEntityManager != null) {
				newEntityManager.close();
			}
		}
	}

	/**
	 * Retrieve a list of persistent instances with the specified query.
	 *
	 * @param <T> the object's type to retrieve
	 * @param query the JPQL query
	 * @param parameters the prameters to be used with the criteria
	 * @param cacheQuery set it to <code>true</code> to cache the query result
	 * @return a list of persistent instances.
	 * @throws EpPersistenceException - in case of persistence errors
	 */
	@Override
	public <T> List<T> retrieve(final String query, final Object[] parameters, final boolean cacheQuery) throws EpPersistenceException {
		try {
			List<T> result;
			if (cacheQuery) {
				Query newQuery = entityManager.createQuery(query);
				OpenJPAQuery<T> jpaQuery = OpenJPAPersistence.cast(newQuery);
				jpaQuery.getFetchPlan().setQueryResultCacheEnabled(true);
				setQueryParameters(jpaQuery, parameters);
				result = getResults(jpaQuery);
			} else {
				result = retrieve(query, parameters);
			}
			return result;
		} catch (final DataAccessException e) {
			throw new EpPersistenceException(CAUGHT_AN_EXCEPTION, e);
		}
	}

	/**
	 * Retrieve a list of persistent instances with the specified query and bounds, namely, the maximum number of rows to retrieve and / or the first
	 * row to retrieve. This help to support pagination.
	 *
	 * @param <T> the object's type to retrieve
	 * @param queryStr the HQL query string to be executed
	 * @param firstResult the first row to retrieve
	 * @param maxResults the maximum number of rows to retrieve
	 * @param parameters the parameters to be used with the criteria
	 * @return a list of persistent instances
	 * @throws EpPersistenceException in case of persistence errors
	 */
	@Override
	public <T> List<T> retrieve(final String queryStr, final Object[] parameters, final int firstResult, final int maxResults)
			throws EpPersistenceException {
		try {
			Query query = entityManager.createQuery(queryStr);
			query.setFirstResult(firstResult);
			query.setMaxResults(maxResults);
			query.setHint("openjpa.hint.OracleSelectHint", "/*+ first_rows(" + maxResults + ") */");
			setQueryParameters(query, parameters);
			return getResults(query);
		} catch (final DataAccessException e) {
			throw new EpPersistenceException(CAUGHT_AN_EXCEPTION, e);
		}
	}

	/**
	 * Retrieve a list of persistent instances with the specified named query.
	 *
	 * @param <T> the object's type to retrieve
	 * @param queryName the named query
	 * @param parameters the parameters to be used with the given query
	 * @return a list of persistent instances
	 * @throws EpPersistenceException in case of persistence errors
	 */
	@Override
	public <T> List<T> retrieveByNamedQuery(final String queryName, final Object... parameters) throws EpPersistenceException {
		if (LOG.isDebugEnabled()) {
			LOG.debug(LOG_NAMED_QUERY + queryName);
		}
		try {
			Query query = entityManager.createNamedQuery(queryName);
			setQueryParameters(query, parameters);
			return getResults(query);
		} catch (final DataAccessException e) {
			throw new EpPersistenceException(CAUGHT_AN_EXCEPTION, e);
		} catch (final InvalidStateException ex) {
			throw new EpPersistenceException(ex.getMessage(), ex);
		}
	}

	@Override
	public <T> List<T> retrieveByNamedQuery(final String queryName, final FlushMode flushMode, final Object... parameters)
			throws EpPersistenceException {
		if (LOG.isDebugEnabled()) {
			LOG.debug(LOG_NAMED_QUERY + queryName);
		}
		try {
			Query query = entityManager.createNamedQuery(queryName);
			setQueryParameters(query, parameters);
			query.setFlushMode(toFlushModeType(flushMode));

			return getResults(query);
		} catch (final DataAccessException e) {
			throw new EpPersistenceException(CAUGHT_AN_EXCEPTION, e);
		} catch (final InvalidStateException ex) {
			throw new EpPersistenceException(ex.getMessage(), ex);
		}
	}

	/**
	 * Retrieves a collection of persistent instances with the specified named query. Parameter
	 * names should <i>not</i> be numerical.
	 *
	 * @param <T> the object's type to retrieve
	 * @param queryName the named query
	 * @param parameters the named list of parameters to be used with the given query
	 * @return a collection of persistable instances
	 * @throws EpPersistenceException in case of persistence errors
	 */
	@Override
	public <T> List<T> retrieveByNamedQuery(final String queryName, final Map<String, Object> parameters) throws EpPersistenceException {
		LOG.debug(LOG_NAMED_QUERY + queryName);
		try {
			final Query query = entityManager.createNamedQuery(queryName);
			setQueryParameters(query, parameters);
			return getResults(query);
		} catch (DataAccessException e) {
			throw new EpPersistenceException(CAUGHT_AN_EXCEPTION, e);
		} catch (InvalidStateException e) {
			throw new EpPersistenceException(e.getMessage(), e);
		}
	}

	/**
	 * Retrieve a list of persistent instances with the specified named query.
	 *
	 * @param <T> the object's type to retrieve
	 * @param queryName the named query
	 * @param firstResult the first row to retrieve
	 * @param maxResults the maximum number of rows to retrieve
	 * @return a list of persistent instances
	 * @throws EpPersistenceException in case of persistence errors
	 */
	@Override
	public <T> List<T> retrieveByNamedQuery(final String queryName, final int firstResult, final int maxResults) throws EpPersistenceException {
		if (LOG.isDebugEnabled()) {
			LOG.debug(LOG_NAMED_QUERY + queryName);
		}
		try {
			Query query = entityManager.createNamedQuery(queryName);
			query.setFirstResult(firstResult);
			query.setMaxResults(maxResults);
			return getResults(query);
		} catch (final DataAccessException e) {
			throw new EpPersistenceException(CAUGHT_AN_EXCEPTION, e);
		}

	}

	/**
	 * Retrieve a list of persistent instances with the specified named query.
	 *
	 * @param <T> the object's type to retrieve
	 * @param queryName the named query
	 * @param parameters the parameters to be used with the given query
	 * @param firstResult the first row to retrieve
	 * @param maxResults the maximum number of rows to retrieve.
	 * @return a list of persistent instances
	 * @throws EpPersistenceException in case of persistence errors
	 */
	@Override
	public <T> List<T> retrieveByNamedQuery(final String queryName, final Object[] parameters, final int firstResult, final int maxResults)
						throws EpPersistenceException {
		if (LOG.isDebugEnabled()) {
			LOG.debug(LOG_NAMED_QUERY + queryName);
		}
		try {
			Query query = entityManager.createNamedQuery(queryName);
			query.setFirstResult(firstResult);
			query.setMaxResults(maxResults);
			setQueryParameters(query, parameters);
			return getResults(query);
		} catch (final DataAccessException e) {
			throw new EpPersistenceException(CAUGHT_AN_EXCEPTION, e);
		}

	}

	/**
	 * Persist the given instance.
	 *
	 * @param object the instance to save.
	 * @throws EpPersistenceException - in case of persistence errors
	 */
	@Override
	public void save(final Persistable object) throws EpPersistenceException {
		try {
			fireBeginSingleOperationEvent(object, ChangeType.CREATE);

			if (object instanceof PersistenceInterceptor) {
				((PersistenceInterceptor) object).executeBeforePersistAction();
			}
			entityManager.persist(object);

		} catch (final DataAccessException e) {
			throw new EpPersistenceException(CAUGHT_AN_EXCEPTION, e);
		} finally {
			fireEndSingleOperationEvent(object, ChangeType.CREATE);
		}
	}

	private static <T> List<T> getResults(final Query query) {
		return new ArrayList<>((List<T>) query.getResultList());
	}

	/**
	 * Begin a single persistence operation. This will add a listener to the broker
	 * if one has not already been added.
	 *
	 * @param object the subject of the operation
	 * @param changeType the type of operation being performed
	 */
	@Override
	public void fireBeginSingleOperationEvent(final Persistable object, final ChangeType changeType) {
		for (PersistenceEngineOperationListener listener : getPersistenceEngineOperationListeners()) {
			listener.beginSingleOperation(object, changeType);
		}
	}

	/**
	 * End a single persistence operation. This will remove any listeners added to
	 * the broker by fireBeginSingleOperationEvent().
	 *
	 * @param object the object that was the subject of the operation
	 * @param changeType the type of operation being performed
	 */
	@Override
	public void fireEndSingleOperationEvent(final Persistable object, final ChangeType changeType) {
		for (PersistenceEngineOperationListener listener : getPersistenceEngineOperationListeners()) {
			listener.endSingleOperation(object, changeType);
		}
	}

	/**
	 * Begin a bulk persistence operation. This will add a listener to the broker
	 * if one has not already been added.
	 *
	 * @param query the query being executed
	 * @param parameters parameters to the query
	 * @param changeType the type of operation being performed
	 */
	void fireBeginBulkOperationEvent(final OpenJPAQuery<?> query, final Object[] parameters, final ChangeType changeType) {
		fireBeginBulkOperationEvent(null, query, parameters, changeType);
	}

	/**
	 * Begin a bulk persistence operation. This will add a listener to the broker
	 * if one has not already been added.
	 *
	 * @param queryName the name of the query or null;
	 * @param query the query being executed
	 * @param parameters parameters to the query
	 * @param changeType the type of operation being performed
	 */
	void fireBeginBulkOperationEvent(final String queryName, final OpenJPAQuery<?> query, final Object[] parameters, final ChangeType changeType) {
		for (PersistenceEngineOperationListener listener : getPersistenceEngineOperationListeners()) {
			listener.beginBulkOperation(queryName, query.getQueryString(), Arrays.toString(parameters), changeType);
		}
	}

	/**
	 * End a bulk persistence operation. This will remove any listeners added to
	 * the broker by beginBulkOperation().
	 *
	 * @param changeType the type of operation being performed
	 */
	@SuppressWarnings("PMD.UnusedFormalParameter")
	void fireEndBulkOperationEvent(final ChangeType changeType) {
		for (PersistenceEngineOperationListener listener : getPersistenceEngineOperationListeners()) {
			listener.endBulkOperation();
		}
	}

	/**
	 * Save the persistable instance if it's new or merge the persistent instance if it exists.
	 *
	 * @param <T> the type of the object
	 * @param object the instance to save or merge
	 * @throws EpPersistenceException - in case of persistence errors
	 * @return the merged object if it is merged, or the persisted object for save action
	 */
	@Override
	public <T extends Persistable> T saveOrMerge(final T object) throws EpPersistenceException {
		if (object.isPersisted()) {
			return merge(object);
		}

		save(object);
		return object;
	}

	/**
	 * Save the persistable instance if it's new or update the persistent instance if it exists.
	 *
	 * @param <T> the type of the object
	 * @param object the instance to save or update
	 * @throws EpPersistenceException - in case of persistence errors
	 * @return the merged object if it is merged, or the persisted object for save action
	 */
	@Override
	public <T extends Persistable> T saveOrUpdate(final T object) throws EpPersistenceException {
		return saveOrMerge(object);
	}

	/**
	 * Update the given persistent instance.  If you update an object, you must use the returned object from the update operation.
	 * @param <T> the type of the object
	 * @param object the instance to update
	 * @return the instance that the state was merged to
	 * @throws EpPersistenceException - in case of persistence errors
	 */
	@Override
	public <T extends Persistable> T update(final T object) throws EpPersistenceException {
		try {
			fireBeginSingleOperationEvent(object, ChangeType.UPDATE);

			if (object instanceof PersistenceInterceptor) {
				((PersistenceInterceptor) object).executeBeforePersistAction();
			}
			T result = entityManager.merge(object);

			fireEndSingleOperationEvent(object, ChangeType.UPDATE);

			return result;
		} catch (final DataAccessException e) {
			throw new EpPersistenceException(CAUGHT_AN_EXCEPTION, e);
		}
	}

	/**
	 * Get the Entity Manager.
	 *
	 * @return the EntityManager
	 */
	@Override
	public EntityManager getEntityManager() {
		return entityManager;
	}

	/**
	 * Set the Entity Magager.
	 *
	 * @param entityManager the EntityManager to use
	 */
	public void setEntityManager(final EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	/**
	 * Set the parameters into the query.
	 * @param query the query
	 * @param parameters the parameters
	 */
	protected void setQueryParameters(final Query query, final Object[] parameters) {
		if (parameters == null) {
			return;
		}

		for (int i = 0; i < parameters.length; i++) {
			query.setParameter(i + 1, parameters[i]);
		}
	}

	private void setQueryParameters(final Query query, final Map<String, Object> parameters) {
		for (Entry<String, Object> entry : parameters.entrySet()) {
			query.setParameter(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * Escapes parameters that are passed to the query. Now it escapes strings only, but other types can be easily added.
	 * @param <T> parameter type
	 * @param parameter parameter
	 * @return escaped parameter
	 */
	protected <T> T escapeParameter(final T parameter) {
		if (parameter instanceof String) {
			return (T) paramEscaper.escapeStringParameter((String) parameter);
		}
		return parameter;
	}

	private OpenJPAQuery<?> getQueryForNamedQuery(final String queryName) {
		OpenJPAQuery<?> query;
		try {
			query = OpenJPAPersistence.cast(getEntityManager().createNamedQuery(queryName));
		} catch (final DataAccessException e) {
			throw new EpPersistenceException("Exception was thrown when create named query.", e);
		}
		return query;
	}

	/**
	 * Execute an update or delete named query.
	 *
	 * @param queryName the named query
	 * @param parameters the parameters to be used with the given query
	 * @return the number of entities updated or deleted
	 * @throws EpPersistenceException - in case of persistence errors
	 */
	@Override
	public int executeNamedQuery(final String queryName, final Object... parameters) throws EpPersistenceException {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Executing named query: " + queryName);
		}

		OpenJPAQuery<?> query = getQueryForNamedQuery(queryName);
		ChangeType changeType = getChangeTypeFor(query);
		fireBeginBulkOperationEvent(queryName, query, parameters, changeType);

		try {
			//Query query = entityManager.createNamedQuery(queryName);
			setQueryParameters(query, parameters);
			return query.executeUpdate();
		} catch (final DataAccessException e) {
			throw new EpPersistenceException(CAUGHT_AN_EXCEPTION, e);
		} finally {
			fireEndBulkOperationEvent(changeType);
		}

	}

	@Override
	public int executeQuery(final String dynamicQuery, final Object... parameters) throws EpPersistenceException {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Executing dynamic query: " + dynamicQuery);
		}

		OpenJPAQuery<?> query = OpenJPAPersistence.cast(getEntityManager().createQuery(dynamicQuery));
		ChangeType changeType = getChangeTypeFor(query);
		fireBeginBulkOperationEvent("Dynamic Query", query, parameters, changeType);

		try {
			setQueryParameters(query, parameters);
			return query.executeUpdate();
		} catch (final DataAccessException e) {
			throw new EpPersistenceException(CAUGHT_AN_EXCEPTION, e);
		} finally {
			fireEndBulkOperationEvent(changeType);
		}
	}

	/**
	 * Execute dynamic update/delete query with list parameter and optional other parameters.
	 *
	 * @param dynamicQuery he query to be executed.
	 * @param listParameterName the name of the parameter for the list values
	 * @param values the collection of values
	 * @param parameters the parameters to be used with the given query
	 * @param <E> the type of values used in the query
	 * @return the number of entities updated or deleted
	 * @throws EpPersistenceException - in case of persistence errors
	 */
	public <E> int executeQueryWithList(final String dynamicQuery, final String listParameterName,
		final Collection<E> values, final Object... parameters) throws EpPersistenceException {

		if (LOG.isDebugEnabled()) {
			LOG.debug("executing dynamic query with list: " + dynamicQuery + "==" + listParameterName);
		}
		List<String> listParameters = splitCollection(values, 0);
		int result = 0;
		for (String listParameter : listParameters) {
			OpenJPAQuery<?> query = OpenJPAPersistence.cast(getEntityManager().createQuery(dynamicQuery));
			Query newQuery = insertListIntoQuery(query, listParameterName, listParameter);
			setQueryParameters(newQuery, parameters);

			ChangeType changeType = getChangeTypeFor(query);
			fireBeginBulkOperationEvent("Dynamic Query", query, parameters, changeType);
			try {
				result += newQuery.executeUpdate();
			} finally {
				fireEndBulkOperationEvent(changeType);
			}
		}
		return result;
	}

	/**
	 * Retrieve a list of persistent instances with the specified named query.
	 *
	 * @param <T> the object's type to retrieve
	 * @param <E> the type of values used in the query
	 * @param queryName the named query
	 * @param parameterValuesMap the map of name - list values
	 * @return a list of persistent instances
	 * @throws EpPersistenceException in case of persistence errors
	 */
	@Override
	public <T, E> List<T> retrieveByNamedQueryWithList(final String queryName,
													   final Map<String, Collection<E>> parameterValuesMap) throws EpPersistenceException {
		if (LOG.isDebugEnabled()) {
			LOG.debug(LOG_NAMED_QUERY + queryName);
		}
		OpenJPAQuery<T> query = OpenJPAPersistence.cast(entityManager).createNamedQuery(queryName);
		for (final Entry<String, Collection<E>> entry : parameterValuesMap.entrySet()) {
			final Collection<E> values = entry.getValue();
			if (values == null || values.isEmpty()) {
				return Collections.emptyList();
			}
			final String parameterValue = getInParameterValues(values);
			query = OpenJPAPersistence.cast(
						insertListIntoQuery(query, entry.getKey(), parameterValue)
					);
		}
		return getResults(query);
	}

	/**
	 * Convert collection of values into string representation.
	 * Do not use this method for <code>Date</code> types.
	 * @param collection of values
	 * @return comma delimited list of values
	 */
	private String getInParameterValues(final Collection<?> collection) {
		StringBuilder result = new StringBuilder();
		for (Object item : collection) {
			boolean isString = item instanceof String;
			if (isString) {
				result.append('\'');
			}
			result.append(item);
			if (isString) {
				result.append('\'');
			}
			result.append(',');
		}
		return StringUtils.chop(result.toString());
	}

	/**
	 * Retrieve a list of persistent instances with the specified named query.
	 *
	 * @param <T> the object's type to retrieve
	 * @param <E> the type of values used in the query
	 * @param queryName the named query
	 * @param listParameterName the name of the parameter for the list values
	 * @param values the collection of values
	 * @param parameters the parameters to be used with the given query
	 * @return a list of persistent instances
	 * @throws EpPersistenceException in case of persistence errors
	 */
	@Override
	public <T, E> List<T> retrieveByNamedQueryWithList(final String queryName, final String listParameterName,
													   final Collection<E> values, final Object... parameters) throws EpPersistenceException {
		if (LOG.isDebugEnabled()) {
			LOG.debug(LOG_NAMED_QUERY + queryName);
		}

		int parameterCount = 0;
		if (parameters != null) {
			parameterCount = parameters.length;
		}

		List<String> listParameters = splitCollection(values, parameterCount);
		final List<T> result = new ArrayList<>();
		for (String listParameter : listParameters) {
			OpenJPAQuery<T> query = OpenJPAPersistence.cast(entityManager).createNamedQuery(queryName);
			Query newQuery = insertListIntoQuery(query, listParameterName, listParameter);
			setQueryParameters(newQuery, parameters);
			result.addAll(JpaPersistenceEngineImpl.<T>getResults(newQuery));
		}
		return result;
	}


	/**
	 * Retrieve a list of object array with the specified named query.
	 * Array of object represent single row from JPA reporting query.
	 * Object types in array corresponds to persistent instance fileds, that
	 * retrieved by named query.
	 *
	 * @param queryName the named query
	 * @param listParameterName the name of the parameter for the list values
	 * @param values the collection of values
	 * @param parameters the parameters to be used with the given query
	 * @return a list of persistent instances
	 * @throws EpPersistenceException in case of persistence errors
	 */
	@Override
	public List<Object[]> retrievePartByNamedQueryWithList(final String queryName, final String listParameterName,
														   final Collection<?> values, final Object... parameters) throws EpPersistenceException {
		if (LOG.isDebugEnabled()) {
			LOG.debug(LOG_NAMED_QUERY + queryName);
		}

		int parameterCount = 0;
		if (parameters != null) {
			parameterCount = parameters.length;
		}

		List<String> listParameters = splitCollection(values, parameterCount);
		final List<Object[]> result = new ArrayList<>();
		for (String listParameter : listParameters) {
			OpenJPAQuery<Object[]> query = OpenJPAPersistence.cast(entityManager).createNamedQuery(queryName);
			Query newQuery = insertListIntoQuery(query, listParameterName, listParameter);
			setQueryParameters(newQuery, parameters);
			result.addAll(JpaPersistenceEngineImpl.<Object[]>getResults(newQuery));
		}
		return result;
	}



	/**
	 * Retrieve a list of persistent instances with the specified named query.
	 *
	 * @param <T> the object's type to retrieve
	 * @param queryName the named query
	 * @param listParameterName name of the list parameter
	 * @param values collection of values for the list parameter
	 * @param parameters the parameters to be used with the given query
	 * @param firstResult the first row to retrieve
	 * @param maxResults the maximum number of rows to retrieve
	 * @return a list of persistent instances
	 * @throws EpPersistenceException in case of persistence errors
	 */
	@Override
	public <T> List<T> retrieveByNamedQueryWithList(final String queryName, final String listParameterName, final Collection<?> values,
													final Object[] parameters, final int firstResult, final int maxResults)
			throws EpPersistenceException {
		try {
			OpenJPAQuery<T> query = OpenJPAPersistence.cast(entityManager).createNamedQuery(queryName);
			String valuesList = getInParameterValues(values);
			if (StringUtils.isEmpty(valuesList)) {
				valuesList = "''";
			}
			Query newQuery = insertListIntoQuery(query, listParameterName, valuesList);
			newQuery.setFirstResult(firstResult);
			newQuery.setMaxResults(maxResults);
			setQueryParameters(newQuery, parameters);
			return getResults(newQuery);
		} catch (final DataAccessException e) {
			throw new EpPersistenceException(CAUGHT_AN_EXCEPTION, e);
		}
	}

	/**
	 * Execute an update or delete named query.
	 *
	 * @param <E> the type of values used in the query
	 * @param queryName the named query
	 * @param listParameterName the name of the parameter for the list values
	 * @param values the collection of values
	 * @param parameters the parameters to be used with the given query
	 * @return the number of entities updated or deleted
	 * @throws EpPersistenceException - in case of persistence errors
	 */
	@Override
	public <E> int executeNamedQueryWithList(final String queryName, final String listParameterName, final Collection<E> values,
											 final Object... parameters) throws EpPersistenceException {
		if (LOG.isDebugEnabled()) {
			LOG.debug("executing named query: " + queryName);
		}
		List<String> listParameters = splitCollection(values, 0);
		int result = 0;
		for (String listParameter : listParameters) {
			OpenJPAQuery<Integer> query = OpenJPAPersistence.cast(entityManager).createNamedQuery(queryName);
			Query newQuery = insertListIntoQuery(query, listParameterName, listParameter);
			setQueryParameters(newQuery, parameters);

			ChangeType changeType = getChangeTypeFor(query);
			fireBeginBulkOperationEvent(queryName, query, parameters, changeType);
			try {
				result += newQuery.executeUpdate();
			} finally {
				fireEndBulkOperationEvent(changeType);
			}
		}
		return result;
	}


	/**
	 * Insert parameter names into query.
	 * @param query the query
	 * @param listParameterName the list of parameter name
	 * @param listParameter the list of parameter
	 * @return the query
	 */
	protected Query insertListIntoQuery(final OpenJPAQuery<?> query, final String listParameterName, final String listParameter) {
		StringBuilder queryString = new StringBuilder(query.getQueryString());
		String stringToReplace = NAMED_PARAMETER_PREFIX + listParameterName;
		if (queryString.indexOf(stringToReplace) == -1) {
			throw new IllegalArgumentException("Parameter " + listParameterName + " does not exist as a named parameter in ["
												+ query.getQueryString() + "]");
		}
		queryString.replace(queryString.indexOf(stringToReplace), queryString.indexOf(stringToReplace) + stringToReplace.length(), listParameter);
		return entityManager.createQuery(queryString.toString());
	}

	/**
	 * Private method to split a collection of values into a list of strings of suitable length.
	 *
	 * @param <T> the type of object
	 * @param values the collection of values
	 * @param parameterCount the number of other parameters to consider
	 * @return the list of value strings
	 */
	protected <T> List<String> splitCollection(final Collection<T> values, final int parameterCount) {
		// Try to get the most appropriate buffer size to avoid buffer expanding.
		int bufferSize;
		if (values.size() > MAX_ALLOW_EXPRESSIONS_IN_QUERY - parameterCount) {
			bufferSize = MAX_ALLOW_EXPRESSIONS_IN_QUERY * EXPRESSION_ESTIMATE_LENGTH;
		} else {
			bufferSize = values.size() * EXPRESSION_ESTIMATE_LENGTH;
		}
		final StringBuilder sbf = new StringBuilder(bufferSize);

		final List<String> result = new ArrayList<>();
		int cursor = 0;
		for (T value : values) {
			cursor++;
			if (value instanceof String) {
				value = escapeParameter(value);
				sbf.append('\'').append(value).append("\',");
			} else {
				sbf.append(value).append(',');
			}
			if (cursor >= MAX_ALLOW_EXPRESSIONS_IN_QUERY) {
				sbf.deleteCharAt(sbf.length() - 1);
				result.add(sbf.toString());
				sbf.delete(0, sbf.length());
				cursor = 0;
			}
		}

		if (sbf.length() > 0) {
			sbf.deleteCharAt(sbf.length() - 1);
			result.add(sbf.toString());
		}

		return result;
	}

	/**
	 * Get the persistence session factory.
	 * @return an instance of <code>PersistenceSessionFactory</code>
	 */
	@Override
	public PersistenceSessionFactory getSessionFactory() {
		return sessionFactory;
	}

	/**
	 * Set the persistence session factory.
	 * @param sessionFactory the factory
	 */
	public void setSessionFactory(final PersistenceSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	/**
	 * Get a direct connection.
	 * @return the connection
	 */
	@Override
	public Connection getConnection() {
		EntityManager newEntityManager = ((JpaPersistenceSession) sessionFactory.createPersistenceSession()).getEntityManager();
		return (Connection) OpenJPAPersistence.cast(newEntityManager).getConnection();
	}

	/**
	 * Output some debug information on a persistable object.
	 * @param name - the name of the object
	 * @param object - the persistable object to debug
	 */
	@Override
	public void debugObject(final String name, final Persistable object) {
		StringBuilder debugMessage = new StringBuilder();
		OpenJPAEntityManager openjpaEM = OpenJPAPersistence.cast(entityManager);
		debugMessage.append("Status for ");
		debugMessage.append(name);
		debugMessage.append(": dirty=");
		debugMessage.append(openjpaEM.isDirty(object));
		debugMessage.append(", detached=");
		debugMessage.append(openjpaEM.isDetached(object));
		debugMessage.append(", persistent=");
		debugMessage.append(openjpaEM.isPersistent(object));
		debugMessage.append(", transactional=");
		debugMessage.append(openjpaEM.isTransactional(object));
		debugMessage.append(", lockMode=");
		debugMessage.append(openjpaEM.getLockMode(object));
		LOG.debug(debugMessage.toString());
	}

	/**
	 * Sets the transaction manager.
	 *
	 * @param txManager the transaction manager
	 */
	public void setTransactionManager(final PlatformTransactionManager txManager) {
		this.txManager = txManager;
	}

	@Override
	public void evictObjectFromCache(final Persistable object) {
		OpenJPAEntityManagerFactory oemf = getOpenJPAEntityManagerFactory();
		StoreCache cache = oemf.getStoreCache();
		cache.evict(object.getClass(), object.getUidPk());
	}

	/**
	 * Clear the JPA data cache and query results cache completely.
	 */
	@Override
	public void clearCache() {
		clearDataCache();
		clearQueryResultCache();
	}

	/**
	 * Indicate whether data caching is enabled or not.
	 *
	 * @return true if the data cache is enabled
	 */
	@Override
	public boolean isCacheEnabled() {
		OpenJPAEntityManagerFactory oemf = getOpenJPAEntityManagerFactory();
		StoreCache cache = oemf.getStoreCache();
		if (cache == null) {
			return false;
		}
		if (cache instanceof StoreCacheImpl) {
			DataCache dataCache = ((StoreCacheImpl) cache).getDelegate();
			if (dataCache == null) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Indicate that the current transaction is a large transaction. This will
	 * tell JPA to more aggressively invalidate the cache.
	 *
	 * @param largeTransaction true to indicate a large transaction
	 */
	@Override
	public void setLargeTransaction(final boolean largeTransaction) {
		OpenJPAEntityManager oem = OpenJPAPersistence.cast(entityManager);
		oem.setTrackChangesByType(largeTransaction);
	}

	/**
	 * Clear the Query Result Cache.
	 */
	protected void clearQueryResultCache() {
		OpenJPAEntityManagerFactory oemf = getOpenJPAEntityManagerFactory();
		QueryResultCache qcache = oemf.getQueryResultCache();
		qcache.evictAll();
	}

	/**
	 * Evict all items from the OpenJPA Data Cache.
	 */
	protected void clearDataCache() {
		OpenJPAEntityManagerFactory oemf = getOpenJPAEntityManagerFactory();
		StoreCache cache = oemf.getStoreCache();
		cache.evictAll();
	}

	private OpenJPAEntityManagerFactory getOpenJPAEntityManagerFactory() {
		EntityManagerFactory emf = ((JpaSessionFactoryImpl) sessionFactory).getEntityManagerFactory();
		return OpenJPAPersistence.cast(emf);
	}

	/**
	 * Get the OpenJPA Entity Manager.
	 *
	 * @return an <code>OpenJPAEntityManager</code> instance
	 */
	protected OpenJPAEntityManager getOpenJPAEntityManager() {
		return OpenJPAPersistence.cast(getEntityManager());
	}

	/**
	 * Get the OpenJPA Broker.
	 *
	 * @return the broker
	 */
	@Override
	public Broker getBroker() {
		OpenJPAEntityManager openjpaEM = getOpenJPAEntityManager();
		return JPAFacadeHelper.toBroker(openjpaEM);
	}

	private ChangeType getChangeTypeFor(final OpenJPAQuery<?> query) {
		ChangeType changeType = null;
		switch (query.getOperation()) {
		case DELETE :
			changeType = ChangeType.DELETE;
			break;
		case UPDATE :
			changeType = ChangeType.UPDATE;
			break;
		default:
			//Ignore
		}
		return changeType;
	}

	@Override
	public <T> T detach(final T object) {
		final OpenJPAEntityManager entityManager = getOpenJPAEntityManager();
		if (entityManager.isDetached(object)) {
			return object;
		}
		return entityManager.detachCopy(object);
	}

	private FlushModeType toFlushModeType(final FlushMode flushMode) {
		if (flushMode == FlushMode.AUTO) {
			return FlushModeType.AUTO;
		} else if (flushMode == FlushMode.COMMIT) {
			return FlushModeType.COMMIT;
		}

		throw new EpPersistenceException("Unknown flush mode: " + flushMode);
	}

	protected List<PersistenceEngineOperationListener> getPersistenceEngineOperationListeners() {
		return Collections.unmodifiableList(operationListeners);
	}

	@Override
	public void setPersistenceEngineOperationListeners(final List<PersistenceEngineOperationListener> persistenceEngineListeners) {
		this.operationListeners = persistenceEngineListeners;
	}

	@Override
	public void addPersistenceEngineOperationListener(final PersistenceEngineOperationListener listener) {
		List<PersistenceEngineOperationListener> listenerCopy =
			new ArrayList<>(getPersistenceEngineOperationListeners());
		listenerCopy.add(listener);

		setPersistenceEngineOperationListeners(listenerCopy);
	}

	@Override
	public void removePersistenceEngineOperationListener(final PersistenceEngineOperationListener listener) {
		List<PersistenceEngineOperationListener> listenerCopy =
			new ArrayList<>(getPersistenceEngineOperationListeners());
		listenerCopy.remove(listener);

		setPersistenceEngineOperationListeners(listenerCopy);
	}
}
