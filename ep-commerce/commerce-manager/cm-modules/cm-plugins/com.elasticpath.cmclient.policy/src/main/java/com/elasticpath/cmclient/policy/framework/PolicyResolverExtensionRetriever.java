/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.policy.framework;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;

import com.elasticpath.cmclient.policy.PolicyPlugin;
import com.elasticpath.cmclient.policy.StatePolicyResolver;

/**
 * Retrieve state policy contributions from the plugin extension point.
 */
public final class PolicyResolverExtensionRetriever {

	private static final Logger LOG = Logger.getLogger(PolicyResolverExtensionRetriever.class);
	
	private static final String EXTENSION_STATE_POLICY_RESOLVER = "statePolicyResolver"; //$NON-NLS-1$

	private static final String ATTR_RESOLVER_CLASS = "resolverClass"; //$NON-NLS-1$
	
	private static StatePolicyResolver resolver;
	
	/**
	 * Constructor.
	 */
	private PolicyResolverExtensionRetriever() {
		super();
	}
	/**
	 * Get the state policy resolver.
	 * 
	 * @return A <code>StatePolicyResolver</code>
	 */
	public static StatePolicyResolver getPolicyResolver() {
		LOG.debug("Getting Policy Resolvers");
		if (resolver != null) {
			return resolver;
		}
		IExtension[] extensions = Platform.getExtensionRegistry().getExtensionPoint(
				PolicyPlugin.PLUGIN_ID, EXTENSION_STATE_POLICY_RESOLVER).getExtensions();
		for (IExtension extension : extensions) {
			LOG.debug("Resolver checking Extension: " + extension);
			for (IConfigurationElement configElement : extension.getConfigurationElements()) {
				try {
					resolver = (StatePolicyResolver) configElement.createExecutableExtension(ATTR_RESOLVER_CLASS);
					LOG.debug("Logger found resolver : " + resolver);
					
				} catch (CoreException e) {
					LOG.error("Failed to instantiate state policy resolver: " //$NON-NLS-1$
							+ configElement.getAttribute(ATTR_RESOLVER_CLASS) + " in plugin: " //$NON-NLS-1$
							+ configElement.getDeclaringExtension().getNamespaceIdentifier(), e);
				}
			}
		}
		if (resolver == null) {
			LOG.warn("RESOLVERS ARE NULL");
		}
		LOG.debug("Resolver : " + resolver);
		return resolver;
	}

}
