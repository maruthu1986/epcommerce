/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.service.search.query;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.cmuser.UserStatus;

/**
 * Test <code>UserSearchCriteria</code>.
 */
public class UserSearchCriteriaTest {


	private UserSearchCriteria userSearchCriteria;

	@Before
	public void setUp() throws Exception {
		this.userSearchCriteria = new UserSearchCriteria();
	}

	/**
	 * Test method for 'com.elasticpath.service.search.query.UserSearchCriteria.getUserRole()'.
	 */
	@Test
	public void testGetUserRole() {
		assertNull(this.userSearchCriteria.getUserRoleName());
	}
	
	/**
	 * Test method for 'com.elasticpath.service.search.query.UserSearchCriteria.getUserStatus()'.
	 */
	@Test
	public void testGetUserState() {
		assertNull(this.userSearchCriteria.getUserStatus());
	}

	/**
	 * Test method for 'com.elasticpath.service.search.query.UserSearchCriteria.setUserStatus'.
	 */
	@Test
	public void testSetUserStatus() {
		final UserStatus userStatus = UserStatus.ENABLED;
		this.userSearchCriteria.setUserStatus(userStatus);
		assertSame(userStatus, this.userSearchCriteria.getUserStatus());
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.UserSearchCriteria.clear()'.
	 */
	@Test
	public void testClear() {
		assertTrue(this.userSearchCriteria.isEmpty());
		userSearchCriteria.setUserStatus(UserStatus.ENABLED);
		userSearchCriteria.setCatalogCode("Cat01");
		userSearchCriteria.setStoreCode("Store01");
		userSearchCriteria.setEmail("user@server.com");
		userSearchCriteria.setFirstName("Peter");
		userSearchCriteria.setLastName("Jensen");
		userSearchCriteria.setUserName("Peter Jensen");
		assertFalse(this.userSearchCriteria.isEmpty());

		this.userSearchCriteria.clear();
		assertTrue(this.userSearchCriteria.isEmpty());
	}
}
