/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.emails.uri.impl;

import com.elasticpath.rest.schema.uri.EmailsUriBuilder;
import com.elasticpath.rest.uri.URIUtil;


/**
 * Builds the URI pointing to the specific email.
 *
 * @deprecated remove once dependent resources are converted to Helix.
 */
@Deprecated
public final class EmailsUriBuilderImpl implements EmailsUriBuilder {

	private static final String RESOURCE_SERVER_NAME = "emails";

	private String scope;
	private String emailId;


	@Override
	public EmailsUriBuilderImpl setScope(final String scope) {
		this.scope = scope;
		return this;
	}

	@Override
	public EmailsUriBuilderImpl setEmailId(final String emailId) {
		this.emailId = emailId;
		return this;
	}

	@Override
	public String build() {
		assert scope != null : "scope required.";
		assert emailId != null : "emailId required.";
		return URIUtil.format(RESOURCE_SERVER_NAME, scope, emailId);
	}
}
