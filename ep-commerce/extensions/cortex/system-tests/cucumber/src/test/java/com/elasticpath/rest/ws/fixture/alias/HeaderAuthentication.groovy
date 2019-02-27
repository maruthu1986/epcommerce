package com.elasticpath.rest.ws.fixture.alias

import com.elasticpath.rest.ws.client.FluentRelosClient
import static com.elasticpath.cortex.dce.SharedConstants.DEFAULT_SCOPE
import static com.elasticpath.cortex.dce.SharedConstants.DEFAULT_TEST_USER_GUID

class HeaderAuthentication extends Authentication {


	def authenticate(def userName, def password, def scope, def role) {
		headerAuthentication(userName, password, scope, role)
	}

	def HeaderAuthentication(final FluentRelosClient client) {
		super(client);
	}

	def authAsAPublicUser(def scope) {
		throw new UnsupportedOperationException();
	}

	def authAsRegisteredUser() {
		headerRegisteredAuthentication(DEFAULT_TEST_USER_GUID, DEFAULT_SCOPE)
	}

	def authRegisteredUserByName(def scope, def userId) {
		headerRegisteredAuthentication(userId, scope)
	}

	def invalidateAuthentication() {
		headerInvalidateAuthentication()
	}

	def roleTransitionToRegisteredUser() {
		throw new UnsupportedOperationException();
	}

	def roleTransitionToRegisteredUserByName(def scope, def userName) {
		throw new UnsupportedOperationException();
	}
}
