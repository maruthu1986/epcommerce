/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.cmclient.store.policy;

import java.util.HashMap;
import java.util.Map;

import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.policy.StateDeterminer;
import com.elasticpath.cmclient.policy.common.AbstractDeterminerStatePolicyImpl;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.store.StorePlugin;
import com.elasticpath.persistence.api.Persistable;

/**
 * State policy for figuring out the state of some controls on the dcd wizard.
 */
public class DcdWizardStatePolicy extends AbstractDeterminerStatePolicyImpl {

	private final PersistentObjectStateDeterminer persistentObjectStateDeterminer = new PersistentObjectStateDeterminer();

	@Override
	public void init(final Object dependentObject) {
		persistentObjectStateDeterminer.setPersistentObject((Persistable) dependentObject);
	}

	@Override
	protected Map<String, StateDeterminer> getDeterminerMap() {
		Map<String, StateDeterminer> determiners = new HashMap<>();
		determiners.put("dcdWizardNamePageNameControls", persistentObjectStateDeterminer); //$NON-NLS-1$
		return determiners;
	}

	/**
	 * Determines the state of the controls depending on the dependent object state.
	 */
	public static class PersistentObjectStateDeterminer implements StateDeterminer {

		private Persistable persistentObject;

		/**
		 * @param persistentObject the object
		 */
		public void setPersistentObject(final Persistable persistentObject) {
			this.persistentObject = persistentObject;
		}

		@Override
		public EpState determineState(final PolicyActionContainer targetContainer) {
			if (persistentObject == null || persistentObject.isPersisted()) {
				return EpState.READ_ONLY;
			}
			return EpState.EDITABLE;
		}
	}

	/**
	 * A determiner that sets a container as always editable.
	 */
	public class AlwaysEditableStateDeterminer implements StateDeterminer {

		@Override
		public EpState determineState(final PolicyActionContainer targetContainer) {
			return EpState.EDITABLE;
		}
	}

	@Override
	protected String getPluginId() {
		return StorePlugin.PLUGIN_ID;
	}

	@Override
	protected StateDeterminer getDefaultDeterminer() {
		return new DefaultEditableDeterminer();
	}
}
