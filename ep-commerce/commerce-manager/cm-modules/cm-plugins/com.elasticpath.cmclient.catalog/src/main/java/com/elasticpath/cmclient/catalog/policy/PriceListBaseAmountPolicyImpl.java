/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.catalog.policy;

import java.util.HashMap;
import java.util.Map;

import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.policy.StateDeterminer;
import com.elasticpath.cmclient.policy.common.AbstractStatePolicyImpl;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.common.dto.pricing.BaseAmountDTO;

/**
 * 
 */
public class PriceListBaseAmountPolicyImpl extends AbstractStatePolicyImpl {
	
	private final Map<String, StateDeterminer> determinerMap = new HashMap<>();
	
	@Override
	public void init(final Object dependentObject) {
		// no-op
	}

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
		StateDeterminer determiner = getDeterminerMap().get(containerName);
		if (determiner == null) {
			determiner = new DefaultAuthorizationDeterminer();
		}
		return determiner;
	}
	
	/**
	 * Map container names to determiners.
	 * 
	 * @return a map of container name to <code>StateDeterminer</code>
	 */
	protected Map<String, StateDeterminer> getDeterminerMap() {
		if (determinerMap.isEmpty()) {
			determinerMap.put("editBaseAmountButtonContainer", new BaseAmountSelectedDeterminer()); //$NON-NLS-1$
			determinerMap.put("removeBaseAmountButtonContainer", new BaseAmountSelectedDeterminer()); //$NON-NLS-1$
		}
		return determinerMap;
	}
	
	/**
	 * Default state determiner.
	 */
	public class DefaultAuthorizationDeterminer implements StateDeterminer {
		
		/**
		 * Determine the state based on authorization.
		 * @param targetContainer the target container
		 * @return the <code>EpState</code> determined by the authorization.
		 */
		@Override
		public EpState determineState(final PolicyActionContainer targetContainer) {
			return EpState.EDITABLE;
		}
	}
	
	/**
	 * Default state determiner.
	 */
	public class BaseAmountSelectedDeterminer implements StateDeterminer {
		
		/**
		 * Determine the state based on the base amount selection.
		 * 
		 * @param targetContainer the target container
		 * @return the <code>EpState</code> determined
		 */
		@Override
		public EpState determineState(final PolicyActionContainer targetContainer) {
			Object dependentObject = targetContainer.getPolicyDependent();
			
			if (!(dependentObject instanceof BaseAmountDTO)) {
				// base amount must be selected
				return EpState.READ_ONLY;
			}
			
			return EpState.EDITABLE;
		}
	}
	
	

}
