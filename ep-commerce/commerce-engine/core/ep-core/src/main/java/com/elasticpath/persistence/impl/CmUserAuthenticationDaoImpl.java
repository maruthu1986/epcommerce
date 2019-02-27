/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.persistence.impl;

import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.persistence.api.PersistenceSession;
import com.elasticpath.persistence.api.Query;

/**
 * Default implementation of Spring Security's <code>UserDetailsService</code>, to integrate with Spring Security framework for authentication and
 * authorisation.
 */
public class CmUserAuthenticationDaoImpl implements UserDetailsService {

	private static final Logger LOG = Logger.getLogger(CmUserAuthenticationDaoImpl.class);

	private PersistenceEngine persistenceEngine;

	/**
	 * Locates the user based on the given userName of the <code>cmUser</code>.
	 *
	 * @param userName the userName presented to the <code>DaoAuthenticationProvider</code>. In commerce manager, it will be the userName.
	 * @return a fully populated cmUser (never <code>null</code>)
	 * @throws UsernameNotFoundException if the user could not be found or the user has no GrantedAuthority
	 * @throws DataAccessException if user could not be found for a repository-specific reason
	 */
	@Override
	public UserDetails loadUserByUsername(final String userName) throws UsernameNotFoundException, DataAccessException {
		if (userName == null) {
			throw new EpServiceException("Cannot retrieve null userName.");
		}
		PersistenceSession session = getPersistenceEngine().getPersistenceSession();
		Query<CmUser> query = session.createNamedQuery("CMUSER_FIND_BY_USERNAME");
		query.setParameter(1, userName);

		final List<CmUser> results = query.list();

		try {
			if (results.size() > 1) {
				throw new EpServiceException("Inconsistent data -- duplicate email address exist -- " + userName);
			}

			final Optional<CmUser> cmUser = results.stream().findFirst();

			return cmUser.orElseThrow(() -> new UsernameNotFoundException("No user found with username [" + userName + "]."));
		} finally {
			session.close();
		}
	}

	/**
	 * Sets the persistence engine.
	 *
	 * @param persistenceEngine the persistence engine to set.
	 */
	public void setPersistenceEngine(final PersistenceEngine persistenceEngine) {
		this.persistenceEngine = persistenceEngine;
		if (LOG.isDebugEnabled()) {
			LOG.debug("Persistence engine initialized ... " + persistenceEngine);
		}
	}

	/**
	 * Returns the persistence engine.
	 *
	 * @return the persistence engine.
	 */
	public PersistenceEngine getPersistenceEngine() {
		return this.persistenceEngine;
	}

}
