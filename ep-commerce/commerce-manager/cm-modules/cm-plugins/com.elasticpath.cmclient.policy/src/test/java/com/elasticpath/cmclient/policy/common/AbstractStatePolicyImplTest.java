/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.policy.common;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.policy.StatePolicyTestBase;
import com.elasticpath.cmclient.policy.StateChangeTarget;
import com.elasticpath.cmclient.policy.StatePolicy;
import com.elasticpath.cmclient.policy.StatePolicyDelegate;

/**
 * Test that <code>AbstractStatePolicyImpl</code> behaves as expected.
 */
public class AbstractStatePolicyImplTest extends StatePolicyTestBase {

	private static final String TEST_CONTAINER = "testContainer"; //$NON-NLS-1$
	private StatePolicy abstractStatePolicy;

	/**
	 * Set up objects required for every test.
	 * 
	 */
	@Before
	public void setUp() {
		super.setUp();
		abstractStatePolicy = new AbstractStatePolicyImpl() {

			@Override
			public EpState determineState(final PolicyActionContainer targetContainer) {
				if (TEST_CONTAINER.equals(targetContainer.getName())) {
					return EpState.EDITABLE;
				}
				return EpState.READ_ONLY;
			}

			@Override
			public void init(final Object dependentObject) {
				// Not required
			}
		};
	}

	/**
	 * Test that the apply method calls apply on the governables and sets the right state
	 * for state change targets.
	 */
	@Test
	public void testApply() {
		final PolicyActionContainer namedTestContainer = new PolicyActionContainer(TEST_CONTAINER);
		final PolicyActionContainer otherTestContainer = new PolicyActionContainer("other"); //$NON-NLS-1$
		final StatePolicyDelegate governable = mock(StatePolicyDelegate.class);
		final StateChangeTarget target = mock(StateChangeTarget.class);
		final StateChangeTarget otherTarget = mock(StateChangeTarget.class);

		namedTestContainer.addDelegate(governable);
		namedTestContainer.addTarget(target);
		abstractStatePolicy.apply(namedTestContainer);
		
		otherTestContainer.addTarget(otherTarget);
		abstractStatePolicy.apply(otherTestContainer);

		verify(governable).applyStatePolicy(abstractStatePolicy);
		verify(target).setState(EpState.EDITABLE);
		verify(otherTarget).setState(EpState.READ_ONLY);
	}

}
