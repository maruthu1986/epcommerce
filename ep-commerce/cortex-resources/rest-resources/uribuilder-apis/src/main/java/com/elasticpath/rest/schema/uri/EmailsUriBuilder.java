/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.schema.uri;

/**
 * Builder of email URIs.
 */
public interface EmailsUriBuilder extends ScopedUriBuilder<EmailsUriBuilder> {

	/**
	 * Sets the email ID.
	 *
	 * @param emailId the email id
	 * @return the builder
	 */
	EmailsUriBuilder setEmailId(String emailId);
}
