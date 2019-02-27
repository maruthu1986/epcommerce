/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.emails.uri.impl;

import javax.inject.Provider;

import org.osgi.service.component.annotations.Component;

import com.elasticpath.rest.schema.uri.EmailFormUriBuilder;
import com.elasticpath.rest.schema.uri.EmailFormUriBuilderFactory;

/**
 * A factory for {@link EmailFormUriBuilder}.
 *
 * @deprecated remove once dependent resources are converted to Helix.
 */
@Deprecated
@Component(service = EmailFormUriBuilderFactory.class)
public class EmailFormUriBuilderFactoryImpl implements Provider<EmailFormUriBuilder>, EmailFormUriBuilderFactory {

	@Override
	public EmailFormUriBuilder get() {
		return new EmailFormUriBuilderImpl();
	}

}
