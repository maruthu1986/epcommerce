/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.persistence.openjpa.impl;

import java.util.EnumSet;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.apache.log4j.Logger;
import org.apache.openjpa.persistence.AutoDetachType;
import org.apache.openjpa.persistence.OpenJPAEntityManager;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.elasticpath.persistence.api.PersistenceSessionFilterSupport;

/**
 * This service exposes methods designed to help construct OpenSessionInView style filters.
 *
 * This code is adapted from spring's org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter,
 * and only works with spring managed sessions.
 */
public class JpaPersistenceSessionFilterSupportImpl implements PersistenceSessionFilterSupport {
	private static final Logger LOG = Logger.getLogger(JpaPersistenceSessionFilterSupportImpl.class);

	private EntityManagerFactory entityManagerFactory;
	private final ThreadLocal<Integer> participationCounter = new ThreadLocal<Integer>() {
		@Override
		protected Integer initialValue() {
			return 0;
		}
	};

	@Override
	public void openSharedSession() {
		if (TransactionSynchronizationManager.hasResource(entityManagerFactory)) {
			// Do not modify the EntityManager: just increment the participation counter.
			participationCounter.set(participationCounter.get() + 1);
		} else {
			LOG.debug("Opening EntityManager in JpaPersistenceSessionFilterSupportWithTxImpl");
			try {
				EntityManager entityManager = getEntityManagerFactory().createEntityManager();
				entityManager = enableExtendedPersistenceContext(entityManager);
				TransactionSynchronizationManager.bindResource(entityManagerFactory, new EntityManagerHolder(entityManager));
			} catch (Exception ex) {
				throw new DataAccessResourceFailureException("Could not create JPA EntityManager", ex);
			}
		}
	}

	/**
	 * By default, when running in a container, JPA entity manager factories will dole out EntityManagers
	 * that using a transactional persistence context.  In our case, we want an extended persistence context
	 * because we want the L1 cache to be used even when no tx is active.
	 *
	 * @param entityManager the Entity Manager to convert to an extended persistence context
	 * @return the entity manager
	 */
	protected EntityManager enableExtendedPersistenceContext(final EntityManager entityManager) {
		OpenJPAEntityManager oem = (OpenJPAEntityManager) entityManager;
		EnumSet<AutoDetachType> detachTypes = EnumSet.copyOf(oem.getAutoDetach());
		detachTypes.remove(AutoDetachType.COMMIT);
		detachTypes.remove(AutoDetachType.NON_TRANSACTIONAL_READ);
		oem.setAutoDetach(detachTypes);

		return oem;
	}

	@Override
	public void closeSharedSession() {
		if (participationCounter.get() > 0) {
			participationCounter.set(participationCounter.get() - 1);
			return;
		}

		EntityManagerHolder emHolder = (EntityManagerHolder)
				TransactionSynchronizationManager.unbindResource(entityManagerFactory);
		LOG.debug("Closing JPA EntityManager in JpaPersistenceSessionFilterSupportWithTxImpl");
		EntityManagerFactoryUtils.closeEntityManager(emHolder.getEntityManager());
	}

	protected EntityManagerFactory getEntityManagerFactory() {
		return entityManagerFactory;
	}

	public void setEntityManagerFactory(final EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
	}
}
