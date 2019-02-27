/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.policy.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.helpers.ChangeSetHelper;

/**
 * Retrieve state policy contributions from the plug-in extension point.
 * This class does also a verification of whether the policy contributions come 
 * from the change set plug-in and if that is true and the change sets feature is disabled, 
 * the state policy contributions from the change set plug-in are disregarded
 */
@SuppressWarnings({ "PMD.ExcessiveClassLength", "PMD.TooManyMethods", "PMD.PrematureDeclaration" })
public class StatePolicyExtensionRetriever {
	
	private static final String EXTENSION_NAME = "statePolicy"; //$NON-NLS-1$
	
	private Collection<StatePolicyContribution> policies;
	
	private final String pluginId;

	private final ChangeSetHelper changeSetHelper = ServiceLocator.getService(ChangeSetHelper.BEAN_ID);


	/**
	 * Create an extension retriever for the identified plugin.
	 * 
	 * @param pluginId the ID of the plugin holding the extensions.
	 */
	public StatePolicyExtensionRetriever(final String pluginId) {
		super();
		this.pluginId = pluginId;
	}

	/**
	 * Get the collection of state policy contributions.
	 * 
	 * @return a collection of <code>StatePolicyContribution</code> objects.
	 */
	public Collection<StatePolicyContribution> getPolicies() {
		if (policies != null) {
			return policies;
		}
		policies = new ArrayList<>();
		IExtension[] extensions = Platform.getExtensionRegistry().getExtensionPoint(pluginId, EXTENSION_NAME).getExtensions();
		for (IExtension extension : extensions) {
			if (isExtensionValid(extension)) {
				for (IConfigurationElement configElement : extension.getConfigurationElements()) {
					StatePolicyContribution policy = parseItem(configElement);
					if (policy != null) {
						policies.add(policy);
					}
				}
			}
		}
		return Collections.unmodifiableCollection(policies);
	}
	
	/**
	 * Determine whether the given extension is valid for inclusion.
	 * 
	 * @param extension an extension
	 * @return true if the extension is valid.
	 */
	protected boolean isExtensionValid(final IExtension extension) {
		if ("com.elasticpath.cmclient.changeset".equals(extension.getContributor().getName())) { //$NON-NLS-1$
			return changeSetHelper.isChangeSetsEnabled();
		}
		return true;
	}
	
	/**
	 * Parse an extension configuration element into a state policy contribution.
	 * 
	 * @param configElement the extension configuration element
	 * @return a <code>StatePolicyContribution</code> object.
	 */
	protected StatePolicyContribution parseItem(final IConfigurationElement configElement) {
		return new StatePolicyContribution(configElement);
	}

}
