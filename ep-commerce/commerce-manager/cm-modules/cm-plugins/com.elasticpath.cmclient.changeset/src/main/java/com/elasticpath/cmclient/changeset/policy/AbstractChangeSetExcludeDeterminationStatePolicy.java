/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.changeset.policy;

import java.util.Collection;

import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.policy.common.AbstractStatePolicyImpl;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;

/**
 * Abstract class that provides a mechanism for excluding action container from being evaluated.
 * Useful in situations where one wants a constant policy for certain container(s).
 */
public abstract class AbstractChangeSetExcludeDeterminationStatePolicy extends AbstractStatePolicyImpl {

	/**
	 * The dependent object for this policy.
	 */
	private Object dependentObject;

	@Override
	public void init(final Object dependentObject) {
		this.dependentObject = dependentObject;
	}

	@Override
	public final EpState determineState(final PolicyActionContainer targetContainer) {
		if (targetContainer == null) {
			return EpState.READ_ONLY;
		}
		if (isContainerEditable(targetContainer)) {
			return EpState.EDITABLE;
		}
		if (isContainerReadOnly(targetContainer)) {
			return EpState.READ_ONLY;
		}

		return determineContainerState(targetContainer);
	}

	/**
	 * Determines this container state.
	 * 
	 * @param targetContainer the container
	 * @return the state
	 */
	protected abstract EpState determineContainerState(final PolicyActionContainer targetContainer);

	/**
	 * Checks whether a container is editable.
	 * 
	 * @param targetContainer the container to check out
	 * @return true if the container should be always editable
	 */
	private boolean isContainerEditable(final PolicyActionContainer targetContainer) {
		return getEditableContainerNames().contains(targetContainer.getName());
	}

	/**
	 * Checks whether a container is read only.
	 * 
	 * @param targetContainer the container to check out
	 * @return true if the container should be always read-only
	 */
	private boolean isContainerReadOnly(final PolicyActionContainer targetContainer) {
		return getReadOnlyContainerNames().contains(targetContainer.getName());
	}
	
	/**
	 * Get the collection of names of <code>PolicyTargetContainer</code> objects
	 * that should always have editable status.
	 * 
	 * @return the collection of names
	 */
	protected abstract Collection<String> getEditableContainerNames();
	
	/**
	 * Get the collection of names of <code>PolicyTargetContainer</code> objects
	 * that should always have readable status.
	 * 
	 * @return the collection of names
	 */
	protected abstract Collection<String> getReadOnlyContainerNames();

	
	/**
	 * @return the dependentObject
	 */
	protected Object getDependentObject() {
		return dependentObject;
	}
}
