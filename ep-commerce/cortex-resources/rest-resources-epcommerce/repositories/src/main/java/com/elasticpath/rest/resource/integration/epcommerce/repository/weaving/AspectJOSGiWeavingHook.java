/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.weaving;

import java.lang.instrument.ClassFileTransformer;

import org.aspectj.weaver.loadtime.ClassPreProcessorAgentAdapter;
import org.osgi.framework.hooks.weaving.WeavingHook;
import org.osgi.framework.hooks.weaving.WovenClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hook for AspectJ weaving.
 */
public class AspectJOSGiWeavingHook implements WeavingHook {

	private static final Logger LOG = LoggerFactory.getLogger(AspectJOSGiWeavingHook.class);
	private final ClassFileTransformer classFileTransformer = new ClassPreProcessorAgentAdapter();

	@Override
	public void weave(final WovenClass wovenClass) {

		final String wovenClassName = wovenClass.getClassName();

		try {
			if (shouldBeInstrumented(wovenClassName)) {
				LOG.trace("Instrumenting {}", wovenClassName);

				final ClassLoader loader = wovenClass.getBundleWiring().getClassLoader();

				final byte[] wovenBytes = classFileTransformer.transform(loader, wovenClassName, null, null, wovenClass.getBytes());

				wovenClass.setBytes(wovenBytes);
			}
		} catch (Exception e) {
			LOG.error("Error instrumenting " + wovenClass.getClassName(), e);
		}
	}

	//TODO this info should be derived from aop.xml
	private boolean shouldBeInstrumented(final String className) {
		return className.contains("integration.epcommerce.repository")
					&& className.endsWith("RepositoryImpl");
	}
}
