/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.policy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

import com.elasticpath.cmclient.core.CmSingletonUtil;
import com.elasticpath.cmclient.policy.ui.StatePolicyListener;

/**
 * The activator class controls the plug-in life cycle.
 */
public class PolicyPlugin extends Plugin {
	private static final Logger LOG = Logger.getLogger(PolicyPlugin.class);
	/**
	 * The plug-in ID.
	 */
	public static final String PLUGIN_ID = "com.elasticpath.cmclient.policy"; //$NON-NLS-1$

	private static List<Runnable> deferredRegistration = new ArrayList<>();

	private final Map<String, StatePolicyFactory> factoryMap = new HashMap<>();
	private final StatePolicyTargetListener statePolicyListener = new StatePolicyListener();
	private final Collection<StatePolicyTarget> targets = new HashSet<>();

	@Override
	public void stop(final BundleContext context) throws Exception {
		super.stop(context);
		LOG.debug("Deregister state policy factories");
		factoryMap.clear();
	}

	/**
	 * Returns the shared instance.
	 * 
	 * @return the shared instance
	 */
	public static PolicyPlugin getDefault() {
		return CmSingletonUtil.getApplicationInstance(PolicyPlugin.class);
	}
	
	/**
	 * Register a state policy factory for a plugin.
	 * All of the statePolicy extensions (of that plugin) will be converted to contributions, which will be contained in factory
	 * 
	 * @param pluginId the ID of the plugin to register a factory for.
	 * @param factory a <code>StatePolicyFactory</code> for the identified plugin
	 */
	public static void putStatePolicyRegistrationInQueue(final String pluginId, final StatePolicyFactory factory) {
		//Registration is deferred as we cannot use SingletonUtil.getUniqueInstance() yet.
		//This runnables will be executed once factoryMap will be referenced.
		deferredRegistration.add(() -> {
			LOG.debug("Register StatePolicyFactory for " + pluginId);
			PolicyPlugin.getDefault().factoryMap.put(pluginId, factory);
		});
	}

	/**
	 * Get the collection of policy factories contributed by each plugin.
	 * 
	 * @return a collection of <code>StatePolicyFactory</code>
	 */
	public Collection<StatePolicyFactory> getStatePolicyFactories() {
		performFactoryRegistration();

		//Factory map must be populated now
		return factoryMap.values();
	}

	private void performFactoryRegistration() {
		//Perform registration if such exist
		for (Runnable registrationOperation : deferredRegistration) {
			registrationOperation.run();
		}
		deferredRegistration.clear();
	}

	/**
	 * Register a state policy target that the listener should be aware of.
	 * 
	 * @param target a <code>StatePolicyTarget</code>
	 */
	public void registerStatePolicyTarget(final StatePolicyTarget target) {
		target.addGovernableListener(statePolicyListener);
		targets.add(target);
	}
	
	/**
	 * unregister State Policy Target.
	 * 
	 * @param target the state policy target
	 */
	public void unregisterStatePolicyTarget(final StatePolicyTarget target) {
		target.removeGovernableListener(statePolicyListener);
		targets.remove(target);
	}
	
	/**
	 * Get the registered state policy targets.
	 * 
	 * @return the collection of registered state policy target
	 */
	public Collection<StatePolicyTarget> getRegisteredStatePolicyTargets() {
		return targets;
	}
	
	/**
	 * Get the state policy for the given target by iterating through
	 * the collection of policies.
	 * 
	 * @param targetId the identified policy target.
	 * @return a <code>StatePolicy</code> for the identified target.
	 */
	public StatePolicy getStatePolicy(final String targetId) {
		LOG.debug("Get State Policy");
		for (StatePolicyFactory factory : PolicyPlugin.getDefault().getStatePolicyFactories()) {
			StatePolicy policy = factory.getStatePolicy(targetId);
			if (policy != null) {
				return policy;
			}
		}
		return null;
	}
}
