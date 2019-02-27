/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.policy.ui;

import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;

/**
 *  A factory class for wrapping grid or table wrap layout composites to be policy targets.
 */
public final class PolicyTargetCompositeFactory {

	private PolicyTargetCompositeFactory() {
		super();
	}

	/**
	 * Wrap a layout composite in a <code>PolicyTarget</code> facade.
	 * 
	 * @param composite the <code>IEpLayoutComposite</code> to wrap
	 * @return a policy target aware layout composite
	 */
	public static IPolicyTargetLayoutComposite wrapLayoutComposite(final IEpLayoutComposite composite) {
		return new PolicyTargetLayoutComposite(composite);
	}

}
