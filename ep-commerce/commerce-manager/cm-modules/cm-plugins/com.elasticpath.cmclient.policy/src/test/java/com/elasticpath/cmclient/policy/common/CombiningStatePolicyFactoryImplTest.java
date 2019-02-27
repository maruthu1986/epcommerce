/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.policy.common;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.elasticpath.cmclient.policy.StatePolicyTestBase;
import com.elasticpath.cmclient.policy.StatePolicy;
import com.elasticpath.cmclient.policy.StatePolicyResolver;

/**
 * Test that <code>CombiningStatePolicyFactoryImpl</code> creates state policies correctly.
 */
public class CombiningStatePolicyFactoryImplTest extends StatePolicyTestBase {

	private static final String TEST_TARGET_ID = "testTargetId"; //$NON-NLS-1$

	private CombiningStatePolicyFactoryImpl combiningStatePolicyFactoryImpl;

	@Mock
	private StatePolicyResolver mockResolver;
	
	private Collection<StatePolicyContribution> contributions;
	
	private StatePolicyContribution targetContribution1;
	
	private StatePolicyContribution targetContribution2;
	
	private StatePolicyContribution otherContribution;

	@Mock
	private StatePolicy resolvedStatePolicy;
	
	
	/**
	 * Set up required for all tests.
	 * 
	 */
	@Before
	public void setUp() {
		super.setUp();
		StatePolicy targetStatePolicy1 = mock(StatePolicy.class, "statePolicy1"); //$NON-NLS-1$
		StatePolicy targetStatePolicy2 = mock(StatePolicy.class, "statePolicy2"); //$NON-NLS-1$
		StatePolicy otherStatePolicy = mock(StatePolicy.class, "statePolicy3"); //$NON-NLS-1$

		targetContribution1 = newStatePolicyContribution(targetStatePolicy1, TEST_TARGET_ID, 1);
		targetContribution2 = newStatePolicyContribution(targetStatePolicy2, TEST_TARGET_ID, 1);
		otherContribution = newStatePolicyContribution(otherStatePolicy, "otherId", 1); //$NON-NLS-1$
		contributions = new HashSet<>();
		contributions.add(targetContribution1);
		contributions.add(targetContribution2);
		contributions.add(otherContribution);

		combiningStatePolicyFactoryImpl = new CombiningStatePolicyFactoryImpl(contributions, mockResolver);
	}

	/**
	 */
	private StatePolicyContribution newStatePolicyContribution(final StatePolicy statePolicy, final String targetId, final int priority) {
		return new StatePolicyContribution(null) {
			@Override
			public int getPriority() {
				return priority;
			}
			@Override
			public StatePolicy getStatePolicy() {
				return statePolicy;
			}
			@Override
			public String getTargetId() {
				return targetId;
			}
		};
	}

	/**
	 * Test getting a state policy.
	 */
	@Test
	public void testGetStatePolicy() {
		final Collection<StatePolicyContribution> resultContributions = new HashSet<>();
		resultContributions.add(targetContribution1);
		resultContributions.add(targetContribution2);
		when(mockResolver.resolvePolicy(resultContributions)).thenReturn(resolvedStatePolicy);
		StatePolicy result = combiningStatePolicyFactoryImpl.getStatePolicy(TEST_TARGET_ID);
		assertEquals("resultant state policy should be the one returned from the resolver", resolvedStatePolicy,
				result); //$NON-NLS-1$
		verify(mockResolver).resolvePolicy(resultContributions);
	}

	/**
	 * Test getting contributions for a target gives the correct collection.
	 */
	@Test
	public void testGetContributionsForTarget() {
		Collection<StatePolicyContribution> combined = combiningStatePolicyFactoryImpl.getContributionsForTarget(TEST_TARGET_ID);
		assertTrue("result should contain first contribution", combined.contains(targetContribution1)); //$NON-NLS-1$
		assertTrue("result should contain second contribution", combined.contains(targetContribution2)); //$NON-NLS-1$
		assertFalse("result should not contain third contribution", combined.contains(otherContribution));  //$NON-NLS-1$
	}
}
