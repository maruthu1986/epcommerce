/**
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.base.common.dto;

/**
 * Holds information on how to resolve an error.
 */
public class StructuredErrorResolution {

	private final Class<?> domain;
	private final String guid;

	/**
	 * Creates a new Structured Error resolution.
	 * @param domain the domain class.
	 * @param guid the guid of the domain object.
	 */
	public StructuredErrorResolution(final Class<?> domain, final String guid) {
		this.domain = domain;
		this.guid = guid;
	}

	public Class<?> getDomain() {
		return domain;
	}

	public String getGuid() {
		return guid;
	}
}
