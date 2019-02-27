/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.totals.uri.impl;

import javax.inject.Provider;

import org.osgi.service.component.annotations.Component;

import com.elasticpath.rest.schema.uri.TotalsUriBuilder;
import com.elasticpath.rest.schema.uri.TotalsUriBuilderFactory;

/**
 * A factory for creating TotalsUriBuilder objects.
 *
 * @deprecated remove once dependent resources are converted to Helix.
 */
@Deprecated
@Component(service = TotalsUriBuilderFactory.class)
public final class TotalsUriBuilderFactoryImpl implements Provider<TotalsUriBuilder>, TotalsUriBuilderFactory {

	@Override
	public TotalsUriBuilder get() {
		return new TotalsUriBuilderImpl();
	}
}
