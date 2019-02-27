package com.elasticpath.rest.ws.fixture.alias;

/**
 * AuthenticationTypeEnum enumerates all types of authentication.
 */
public enum AuthenticationTypeEnum {

	/**
	 * auth match type: header.
	 */
	HEADER_AUTHENTICATION("headerAuth"),

	/**
	 * auth match type: OAuth2.
	 */
	OAUTH2_AUTHENTICATION("OAuth2");

	private String name;

	/**
	 * Private Constructor.
	 *
	 * @param name name
	 */
	AuthenticationTypeEnum(final String name) {
		this.name = name;
	}

	/**
	 * Get name of this authentication type.
	 *
	 * @return the name of the authentication type
	 */
	public String getName() {
		return name;
	}
}
