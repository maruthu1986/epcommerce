/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.policy.ui;

import java.util.Collection;

import org.osgi.framework.BundleContext;

import com.elasticpath.cmclient.core.ui.AbstractEpUIPlugin;
import com.elasticpath.cmclient.policy.PolicyPlugin;
import com.elasticpath.cmclient.policy.StatePolicyFactory;
import com.elasticpath.cmclient.policy.StatePolicyResolver;
import com.elasticpath.cmclient.policy.common.CombiningStatePolicyFactoryImpl;
import com.elasticpath.cmclient.policy.common.StatePolicyContribution;
import com.elasticpath.cmclient.policy.common.StatePolicyExtensionRetriever;
import com.elasticpath.cmclient.policy.framework.PolicyResolverExtensionRetriever;

/**
 * Adds <code>StatePolicy</code> awareness to the eclipse <code>AbstractUIPlugin</code>.
 */
public abstract class AbstractPolicyAwareUIPlugin extends AbstractEpUIPlugin {

	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);

		PolicyPlugin.putStatePolicyRegistrationInQueue(getPluginId(), createStatePolicyFactory());
	}

	/**
	 * Creates a state policy factory.
	 *
	 * @return state policy factory
	 */
	protected StatePolicyFactory createStatePolicyFactory() {
		return new CombiningStatePolicyFactoryImpl(getPolicyContributions(), getStatePolicyResolver());
	}

	/**
	 * Get the state policy contributions from the extension point.
	 *
	 * @return a collection of <code>StatePolicyContribution</code>
	 */
	protected Collection<StatePolicyContribution> getPolicyContributions() {
		return new StatePolicyExtensionRetriever(getPluginId()).getPolicies();
	}

	/**
	 * Get the state policy resolver for this plugin.
	 * Returns Default policy resolver. Subclasses may choose to override
	 *
	 * @return a <code>StatePolicyResolver</code>
	 */
	protected StatePolicyResolver getStatePolicyResolver() {
		return PolicyResolverExtensionRetriever.getPolicyResolver();
	}

	/**
	 * Getter for the Plugin Id.
	 * @return plugin id
	 */
	protected abstract String getPluginId();
}
