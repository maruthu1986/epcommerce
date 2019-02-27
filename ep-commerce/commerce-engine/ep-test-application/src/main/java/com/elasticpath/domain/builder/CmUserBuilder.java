/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.domain.builder;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.cmuser.UserRole;
import com.elasticpath.service.cmuser.UserRoleService;

public class CmUserBuilder implements DomainObjectBuilder<CmUser> {

	@Autowired
	private BeanFactory beanFactory;

	@Autowired
	private UserRoleService userRoleService;

	private String userName;

	private String email;

	private String password;

	private String firstName;

	private String lastName;

	private Date creationDate;

	private Set<String> cmUserRoles = new HashSet<>();

	public static final String SUPERUSER_ROLE = "SUPERUSER";

	public static final String CMUSER_ROLE = "CMUSER";

	public static final String WSUSER_ROLE = "WSUSER";

	public CmUserBuilder withUserName(final String userName) {
		this.userName = userName;
		return this;
	}

	public CmUserBuilder withEmail(final String email) {
		this.email = email;
		return this;
	}

	public CmUserBuilder withFirstName(final String firstName) {
		this.firstName = firstName;
		return this;
	}

	public CmUserBuilder withLastName(final String lastName) {
		this.lastName = lastName;
		return this;
	}

	public CmUserBuilder withPassword(final String password) {
		this.password = password;
		return this;
	}

	public CmUserBuilder withCreationDate(final Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}

	public CmUserBuilder withRole(final String roleName) {
		cmUserRoles.add(roleName);
		return this;
	}

	@Override
	public CmUser build() {

		final CmUser cmUser = beanFactory.getBean(ContextIdNames.CMUSER);
		cmUser.initialize();

		cmUser.setEmail((String) ObjectUtils.defaultIfNull(email, "john.smith@elasticpath.com"));
		cmUser.setUserName((String) ObjectUtils.defaultIfNull(userName, getDefaultUserName()));
		cmUser.setFirstName((String) ObjectUtils.defaultIfNull(firstName, "James"));
		cmUser.setLastName((String) ObjectUtils.defaultIfNull(lastName, "Bond"));
		cmUser.setPassword((String) ObjectUtils.defaultIfNull(password, "password1"));
		cmUser.setCreationDate((Date) ObjectUtils.defaultIfNull(creationDate, new Date()));

		for (final String currentUserRoleName : cmUserRoles) {
			if (currentUserRoleName != null) {
				// check to see if the currentUserRoleName exists and persist it first if not
				// then add it to the current CmUser
				final UserRole existingRole = userRoleService.findByName(currentUserRoleName);
				if (existingRole == null) {
					final UserRole userRole = beanFactory.getBean(ContextIdNames.USER_ROLE);
					userRole.initialize();
					userRole.setName(currentUserRoleName);
					userRoleService.add(userRole);
					cmUser.addUserRole(userRole);
				} else {
					cmUser.addUserRole(existingRole);
				}
			}
		}

		return cmUser;
	}

	private String getDefaultUserName() {
		return "cmUser_" + System.currentTimeMillis() + "_" + Math.random();
	}
}
