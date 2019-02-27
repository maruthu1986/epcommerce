package com.elasticpath.rest.ws.fixture.alias

import com.elasticpath.rest.ws.client.StopTestException
import org.apache.commons.lang3.StringUtils

class AuthenticationFactory {

	static Class getAuthentication(String authType) {
		if (isOAuth2(authType)) {
			return OAuth2Authentication;
		} else if (isHeaderAuth(authType)) {
			return HeaderAuthentication;
		}

		throw new StopTestException("Invalid Cortex authentication type: " + authType + ".");
	}

	private static boolean isOAuth2(String authType) {
		StringUtils.equals(authType, AuthenticationTypeEnum.OAUTH2_AUTHENTICATION.getName())
	}

	private static boolean isHeaderAuth(String authType) {
		StringUtils.equals(authType, AuthenticationTypeEnum.HEADER_AUTHENTICATION.getName())
	}
}
