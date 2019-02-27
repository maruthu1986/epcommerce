/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.weaving;

import java.util.ArrayList;
import java.util.List;


import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.hooks.weaving.WeavingHook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Bundle activator for registering {@link com.elasticpath.rest.resource.integration.epcommerce.repository.weaving.AspectJOSGiWeavingHook}.
 */
public class Activator implements BundleActivator {

	private final List<ServiceRegistration<?>> regs = new ArrayList<>();
	private static final Logger LOG = LoggerFactory.getLogger(Activator.class);

	@Override
	public void start(final BundleContext context) throws Exception {
		addHook(context, new AspectJOSGiWeavingHook());
	}

	@Override
	public void stop(final BundleContext context) throws Exception {
		regs.forEach(ServiceRegistration::unregister);
		regs.clear();
	}

	private void addHook(final BundleContext context, final WeavingHook hook) {
		LOG.info("Registering WeavingHook {}", hook);
		regs.add(context.registerService(WeavingHook.class.getName(), hook, null));
	}

}
