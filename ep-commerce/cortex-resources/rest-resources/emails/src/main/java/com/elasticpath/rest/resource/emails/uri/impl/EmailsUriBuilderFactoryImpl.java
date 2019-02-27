/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.emails.uri.impl;

import javax.inject.Provider;

import org.osgi.service.component.annotations.Component;

import com.elasticpath.rest.schema.uri.EmailsUriBuilder;
import com.elasticpath.rest.schema.uri.EmailsUriBuilderFactory;

/**
 * Factory for {@link EmailsUriBuilder}.
 *
 * @deprecated remove once dependent resources are converted to Helix.
 */
@Deprecated
@Component(service = EmailsUriBuilderFactory.class)
public class EmailsUriBuilderFactoryImpl implements Provider<EmailsUriBuilder>, EmailsUriBuilderFactory {

	@Override
	public EmailsUriBuilder get() {
		return new EmailsUriBuilderImpl();
	}
}
