/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.persistence.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.persistence.api.PersistenceSession;
import com.elasticpath.persistence.api.Query;

/**
 * Test class for {@link CustomerAuthenticationDaoImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class CmUserAuthenticationDaoImplTest {

	private static final String FIND_BY_USERNAME_NAMED_QUERY = "CMUSER_FIND_BY_USERNAME";
	private static final String USERNAME = "cm.user.id";

	@Mock
	private PersistenceEngine persistenceEngine;

	@Mock
	private PersistenceSession persistenceSession;

	@InjectMocks
	private CmUserAuthenticationDaoImpl dao;

	@Before
	public void setUp() {
		when(persistenceEngine.getPersistenceSession()).thenReturn(persistenceSession);
	}

	@Test
	public void verifyLoadUserByUsernameReturnsUserWhenFound() throws Exception {
		final CmUser cmUser = mock(CmUser.class);

		givenUsernameReturnsCmUsers(cmUser);

		final UserDetails actual = dao.loadUserByUsername(USERNAME);

		assertThat(actual).isSameAs(cmUser);
	}

	@Test(expected = UsernameNotFoundException.class)
	public void verifyLoadUserByUsernameThrowsWhenNoSuchUser() throws Exception {
		givenUsernameReturnsCmUsers(); // no CmUser method param = no users returned

		dao.loadUserByUsername(USERNAME);
	}

	@Test(expected = EpServiceException.class)
	public void verifyLoadUserByUsernameThrowsWhenMultipleUsers() throws Exception {
		givenUsernameReturnsCmUsers(mock(CmUser.class), mock(CmUser.class));

		dao.loadUserByUsername(USERNAME);
	}

	@Test(expected = EpServiceException.class)
	public void verifyLoadUserByUsernameThrowsWhenUsernameNull() throws Exception {
		dao.loadUserByUsername(null);
	}

	private void givenUsernameReturnsCmUsers(final CmUser... cmUsers) {
		final Query<CmUser> query =
				when(mock(Query.class).list())
						.thenReturn(Arrays.asList(cmUsers))
						.getMock();

		given(persistenceSession.<CmUser>createNamedQuery(FIND_BY_USERNAME_NAMED_QUERY))
				.willReturn(query);
	}

}