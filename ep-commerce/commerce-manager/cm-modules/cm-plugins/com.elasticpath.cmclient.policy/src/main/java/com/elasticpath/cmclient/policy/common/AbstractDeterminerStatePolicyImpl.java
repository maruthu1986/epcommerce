/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.policy.common;

import java.util.Map;

import com.elasticpath.cmclient.core.helpers.ChangeSetHelper;
import com.elasticpath.cmclient.core.helpers.extenders.PluginHelper;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.policy.StateDeterminer;

/**
 * An abstract implementation of <code>StatePolicy</code> which uses a StateDeterminer returned
 * from a map keyed by container name to determine the state.
 * 
 * This allows an extensible determination of state for different target containers.
 */
public abstract class AbstractDeterminerStatePolicyImpl extends AbstractStatePolicyImpl {

	/**
	 * Determine the UI state by delegating to a determiner for the given target container.
	 * 
	 * @param targetContainer a set of policy targets
	 * @return the <code>EpState</code> determined by the policy.
	 */
	@Override
	public EpState determineState(final PolicyActionContainer targetContainer) {
		StateDeterminer determiner = getDeterminer(targetContainer.getName());
		return determiner.determineState(targetContainer);
	}

	/**
	 * Get the state determiner for the given container name.
	 * 
	 * @param containerName the name of the container
	 * @return a <code>StateDeterminer</code>
	 */
	protected StateDeterminer getDeterminer(final String containerName) {
		return getDeterminerFromMapOrExtensions(containerName);
	}

	/**
	 * Gets the state determiner for the given container name using the changeSetHelper.
	 *
	 * @param containerName   the container name.
	 * @param changeSetHelper the Change set Helper.
	 * @return a <code>StateDeterminer</code>
	 */
	protected StateDeterminer getDeterminer(final String containerName, final ChangeSetHelper changeSetHelper) {
		if (!changeSetHelper.isChangeSetsEnabled()) {
			return getDefaultDeterminer();
		}
		return getDeterminerFromMapOrExtensions(containerName);
	}

	private StateDeterminer getDeterminerFromMapOrExtensions(final String containerName) {
		StateDeterminer stateDeterminer = getDeterminerMap().get(containerName);
		if (stateDeterminer == null) {
			stateDeterminer = getExtensionDeterminer(containerName);
			if (stateDeterminer == null) {
				return getDefaultDeterminer();
			}
		}

		return stateDeterminer;
	}

	/**
	 * Searches for extension Determiners registered with the plugin.
	 *
	 * @param containerName The container name.
	 * @return An extension determiner, if found. Null otherwise.
	 */
	protected StateDeterminer getExtensionDeterminer(final String containerName) {
		Map<String, Object> extDeterminers = PluginHelper.findDeterminers(getPluginId(), this.getClass().getSimpleName());
		return (StateDeterminer) extDeterminers.get(containerName);
	}


	/**
	 * Gets the Determiner's corresponding plugin, to identify where to find the extensions.
	 * @return The plugin ID of the table.
	 */
	protected abstract String getPluginId();

	/**
	 * Gets the default determiner when no action container is specified by the {@link #getDeterminerMap()} method.
	 * 
	 * @return a determiner
	 */
	protected abstract StateDeterminer getDefaultDeterminer();

	/**
	 * Map container names to determiners.
	 * 
	 * @return a map of container name to <code>StateDeterminer</code>
	 */
	protected abstract Map<String, StateDeterminer> getDeterminerMap();

	/**
	 * A default determiner that resolves everything as an editable control.
	 */
	public static class DefaultEditableDeterminer implements StateDeterminer {

		/**
		 * Always returns {@link EpState#EDITABLE}.
		 * 
		 * @param targetContainer the target container
		 * @return {@link EpState#EDITABLE}
		 */
		@Override
		public EpState determineState(final PolicyActionContainer targetContainer) {
			return EpState.EDITABLE;
		}
	}
	
	/**
	 * A default determiner that resolves everything as a read-only control.
	 */
	public static class DefaultReadOnlyDeterminer implements StateDeterminer {

		/**
		 * Always returns {@link EpState#READ_ONLY}.
		 * 
		 * @param targetContainer the target container
		 * @return {@link EpState#READ_ONLY}
		 */
		@Override
		public EpState determineState(final PolicyActionContainer targetContainer) {
			return EpState.READ_ONLY;
		}
	}

}
