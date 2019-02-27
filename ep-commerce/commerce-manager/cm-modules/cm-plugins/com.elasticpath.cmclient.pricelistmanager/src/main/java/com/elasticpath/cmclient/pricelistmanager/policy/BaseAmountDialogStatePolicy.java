/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.cmclient.pricelistmanager.policy;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.policy.common.AbstractStatePolicyImpl;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;

/**
 * The default policy for the base amount dialog.
 */
public class BaseAmountDialogStatePolicy extends AbstractStatePolicyImpl {

	@Override
	public EpState determineState(final PolicyActionContainer targetContainer) {
		// if it is edit mode disable the object data controls
		if (StringUtils.equals(targetContainer.getName(), "objectDataContainer") && targetContainer.getPolicyDependent() != null) { //$NON-NLS-1$
			return EpState.READ_ONLY;
		}
		return EpState.EDITABLE;
	}

	@Override
	public void init(final Object dependentObject) {
		// no operation
	}

}
