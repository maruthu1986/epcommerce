package com.elasticpath.rest.ws.fixture.alias

import com.elasticpath.rest.ws.client.FluentRelosClient
import static com.elasticpath.cortex.dce.SharedConstants.DEFAULT_SCOPE
import static com.elasticpath.cortex.dce.SharedConstants.DEFAULT_SCOPE_TEST_USER
import static com.elasticpath.cortex.dce.SharedConstants.PASSWORD

class OAuth2Authentication extends Authentication {

	public OAuth2Authentication(final FluentRelosClient client) {
		super(client);
	}

	def authenticate(def userName, def password, def scope, def role) {
		oauthAuthentication(userName, password, scope, role)
	}

	def authAsAPublicUser(def scope) {
		oauthPublicAuthentication(scope)
	}

	def authAsRegisteredUser() {
		authRegisteredUserByName(DEFAULT_SCOPE, DEFAULT_SCOPE_TEST_USER)
	}

	def authRegisteredUserByName(def scope, def userId) {
		oauthRegisteredAuthentication(userId, PASSWORD, scope)
	}

	def roleTransitionToRegisteredUser() {
		roleTransitionToRegisteredUserByName(DEFAULT_SCOPE, DEFAULT_SCOPE_TEST_USER)
	}

	def roleTransitionToRegisteredUserByName(def scope, def userName) {
		oauthRoleTransitionAuthentication(userName, PASSWORD, scope)
	}

	def invalidateAuthentication() {
		oauthInvalidateAuthentication()
	}
}
