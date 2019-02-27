/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.emails.uri.impl;

import com.elasticpath.rest.resource.dispatch.operator.annotation.Form;
import com.elasticpath.rest.schema.uri.EmailFormUriBuilder;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Build the URI for the email form.
 *
 * @deprecated remove once dependent resources are converted to Helix.
 */
@Deprecated
public class EmailFormUriBuilderImpl implements EmailFormUriBuilder {

	private static final String RESOURCE_SERVER_NAME = "emails";

	private String scope;


	@Override
	public EmailFormUriBuilderImpl setScope(final String scope) {
		this.scope = scope;
		return this;
	}

	@Override
	public String build() {
		assert scope != null : "scope required.";
		return URIUtil.format(RESOURCE_SERVER_NAME, scope, Form.URI_PART);
	}
}
