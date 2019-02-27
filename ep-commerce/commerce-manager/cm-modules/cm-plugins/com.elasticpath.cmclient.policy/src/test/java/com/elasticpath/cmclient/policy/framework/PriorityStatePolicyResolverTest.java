/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.policy.framework;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.policy.StatePolicyTestBase;
import com.elasticpath.cmclient.policy.StatePolicy;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.common.StatePolicyContribution;

/**
 * Test that resolving state policies based on the priority field works
 * as expected.
 */
public class PriorityStatePolicyResolverTest extends StatePolicyTestBase {

	private static final String TARGET_ID = "target"; //$NON-NLS-1$

	private static final int LOW_PRIORITY = 10;
	private static final int MEDIUM_PRIORITY = 20;
	private static final int HIGH_PRIORITY = 30;
	
	private StatePolicyContribution lowPriorityContribution;
	private StatePolicyContribution mediumPriorityContribution;
	private StatePolicyContribution highPriorityContribution;

	@Mock
	private StatePolicy lowPriorityStatePolicy;

	@Mock
	private StatePolicy mediumPriorityStatePolicy;

	@Mock
	private StatePolicy highPriorityStatePolicy;

	private PriorityStatePolicyResolver resolver;

	/**
	 * Set up required for all tests.
	 * 
	 */
	@Before
	public void setUp() {
		super.setUp();
		lowPriorityContribution = newStatePolicyContribution(lowPriorityStatePolicy, TARGET_ID, LOW_PRIORITY);
		mediumPriorityContribution = newStatePolicyContribution(mediumPriorityStatePolicy, TARGET_ID, MEDIUM_PRIORITY);
		highPriorityContribution = newStatePolicyContribution(highPriorityStatePolicy, TARGET_ID, HIGH_PRIORITY);

		resolver = new PriorityStatePolicyResolver();
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
	 * If one policy has a higher priority than the others it should be
	 * the only one that wins.
	 */
	@Test
	public void testGetWinningPoliciesWithOneHighestPriority() {
		Collection<StatePolicyContribution> contributions = new HashSet<>();
		contributions.add(lowPriorityContribution);
		contributions.add(mediumPriorityContribution);
		contributions.add(highPriorityContribution);

		List<StatePolicy> winningPolicies = resolver.getWinningPolicies(contributions);
		assertEquals("There should be only one winner", 1, winningPolicies.size()); //$NON-NLS-1$
		assertTrue("The policy should be the high priority one", winningPolicies.contains(highPriorityStatePolicy));
		//$NON-NLS-1$

	}

	/**
	 * When several policies have an equal, highest priority they
	 * should all be equal winners.
	 */
	@Test
	public void testGetWinningPoliciesWithSeveralHighest() {
		final StatePolicy highPriorityStatePolicy2 = mock(StatePolicy.class, "high2"); //$NON-NLS-1$
		final StatePolicyContribution highPriorityContribution2 = newStatePolicyContribution(highPriorityStatePolicy2, TARGET_ID, HIGH_PRIORITY);

		Collection<StatePolicyContribution> contributions = new HashSet<>();
		contributions.add(lowPriorityContribution);
		contributions.add(highPriorityContribution);
		contributions.add(mediumPriorityContribution);
		contributions.add(highPriorityContribution2);

		List<StatePolicy> winningPolicies = resolver.getWinningPolicies(contributions);
		assertEquals("There should be two winners", 2, winningPolicies.size()); //$NON-NLS-1$
		assertTrue("The list should be contain the 1st high priority policy", winningPolicies.contains
				(highPriorityStatePolicy)); //$NON-NLS-1$
		assertTrue("The list should be contain the 2nd high priority policy", winningPolicies.contains
				(highPriorityStatePolicy2)); //$NON-NLS-1$

	}

	/**
	 * Ensure the sort returns policies sorted highest to lowest.
	 */
	@Test
	public void testSortPolicies() {
		Collection<StatePolicyContribution> contributions = new HashSet<>();
		contributions.add(mediumPriorityContribution);
		contributions.add(lowPriorityContribution);
		contributions.add(highPriorityContribution);
		List<StatePolicyContribution> sortedList = resolver.sortPoliciesByPriority(contributions);
		assertEquals("There should still be 3 items in the list", contributions.size(), sortedList.size());
		//$NON-NLS-1$
		assertEquals("First item in list is high priority contribution", highPriorityContribution, sortedList.get(0));
		//$NON-NLS-1$
		assertEquals("Second item in list is medium priority contribution", mediumPriorityContribution, sortedList.get
				(1)); //$NON-NLS-1$
		assertEquals("Third item in list is low priority contribution", lowPriorityContribution, sortedList.get(2));
		//$NON-NLS-1$
	}

	/**
	 * Ensure nothing breaks when we have no policies.
	 */
	@Test
	public void testResolvePolicyWhenEmpty() {
		Collection<StatePolicyContribution> contributions = new HashSet<>();
		StatePolicy resultPolicy = resolver.resolvePolicy(contributions);
		assertNotNull("Even with no contributions we should get a resultant policy", resultPolicy); //$NON-NLS-1$
	}

	/**
	 * Test there is only one policy used in the resolved combination when there is
	 * only one winning policy.
	 */
	@Test
	public void testResolvePolicyWithOneWinner() {
		Collection<StatePolicyContribution> contributions = new HashSet<>();
		contributions.add(lowPriorityContribution);
		contributions.add(mediumPriorityContribution);
		contributions.add(highPriorityContribution);
		final PolicyActionContainer container = new PolicyActionContainer("container"); //$NON-NLS-1$

		// Only the highest priority policy should get called.
		when(highPriorityStatePolicy.determineState(container)).thenReturn(EpState.READ_ONLY);

		StatePolicy resultPolicy = resolver.resolvePolicy(contributions);
		EpState resultState = resultPolicy.determineState(container);
	
		assertEquals("The result state should be from our policy", EpState.READ_ONLY, resultState); //$NON-NLS-1$
		verify(highPriorityStatePolicy).determineState(container);
		
	}

	/**
	 * Test there are two policies used in the resultant combination when there are 
	 * two winners.
	 */
	@Test
	public void testResolvePolicyWithTwoWinners() {
		final StatePolicy highPriorityStatePolicy2 = mock(StatePolicy.class, "high2"); //$NON-NLS-1$
		final StatePolicyContribution highPriorityContribution2 = newStatePolicyContribution(highPriorityStatePolicy2,
				TARGET_ID, HIGH_PRIORITY);

		Collection<StatePolicyContribution> contributions = new HashSet<>();
		contributions.add(lowPriorityContribution);
		contributions.add(highPriorityContribution);
		contributions.add(mediumPriorityContribution);
		contributions.add(highPriorityContribution2);

		final PolicyActionContainer container = new PolicyActionContainer("container"); //$NON-NLS-1$
		
		// Only the highest priority policy should get called.
		when(highPriorityStatePolicy.determineState(container)).thenReturn(EpState.READ_ONLY);
		when(highPriorityStatePolicy2.determineState(container)).thenReturn(EpState.READ_ONLY);

		StatePolicy resultPolicy = resolver.resolvePolicy(contributions);
		EpState resultState = resultPolicy.determineState(container);
	
		assertEquals("The result state should be from our policy", EpState.READ_ONLY, resultState); //$NON-NLS-1$
		verify(highPriorityStatePolicy).determineState(container);
		verify(highPriorityStatePolicy2).determineState(container);
		
	}

}
