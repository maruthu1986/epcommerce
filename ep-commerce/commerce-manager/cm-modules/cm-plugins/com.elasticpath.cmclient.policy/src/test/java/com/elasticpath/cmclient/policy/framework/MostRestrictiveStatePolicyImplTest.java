/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.policy.framework;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.policy.StatePolicyTestBase;
import com.elasticpath.cmclient.policy.StatePolicy;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.domain.catalog.Product;

/**
 * Test that most restrictive state policy acts as expected.
 */
public class MostRestrictiveStatePolicyImplTest extends StatePolicyTestBase {

	@Mock
	private StatePolicy editableStatePolicy;

	@Mock
	private StatePolicy readonlyStatePolicy;

	@Mock
	private StatePolicy disabledStatePolicy;

	@Mock
	private Product dependentObject;
	
	private PolicyActionContainer actionContainer;
	
	
	/**
	 * Set up required for all tests.
	 * 
	 */
	@Before
	public void setUp() {
		super.setUp();
		actionContainer = new PolicyActionContainer("container"); //$NON-NLS-1$
		when(editableStatePolicy.determineState(actionContainer)).thenReturn(EpState.EDITABLE);
		when(readonlyStatePolicy.determineState(actionContainer)).thenReturn(EpState.READ_ONLY);
		when(disabledStatePolicy.determineState(actionContainer)).thenReturn(EpState.DISABLED);
	}
	
	/**
	 * Ensure that the init method delegates to the policies.
	 */
	@Test
	public void testInitDelegates() {
		final Collection<StatePolicy> delegatePolicies = new HashSet<>();
		delegatePolicies.add(editableStatePolicy);
		delegatePolicies.add(readonlyStatePolicy);
		delegatePolicies.add(disabledStatePolicy);
		StatePolicy restrictiveStatePolicy = new MostRestrictiveStatePolicyImpl(delegatePolicies);

		restrictiveStatePolicy.init(dependentObject);
		verify(editableStatePolicy).init(dependentObject);
		verify(readonlyStatePolicy).init(dependentObject);
		verify(disabledStatePolicy).init(dependentObject);

	}
	
	/**
	 * Test that the state is EDITABLE when all delegate policies are EDITABLE.
	 */
	@Test
	public void testDetermineStateWhenAllEditable() {
		final StatePolicy editableStatePolicy2 = mock(StatePolicy.class, "editable2"); //$NON-NLS-1$
		final Collection<StatePolicy> delegatePolicies = new HashSet<>();
		delegatePolicies.add(editableStatePolicy);
		delegatePolicies.add(editableStatePolicy2);
		when(editableStatePolicy2.determineState(actionContainer)).thenReturn(EpState.EDITABLE);

		StatePolicy restrictiveStatePolicy = new MostRestrictiveStatePolicyImpl(delegatePolicies);
		EpState result = restrictiveStatePolicy.determineState(actionContainer);
		assertEquals("The state should be EDITABLE", EpState.EDITABLE, result); //$NON-NLS-1$
		verify(editableStatePolicy2).determineState(actionContainer);
	}

	/**
	 * Test that the state is READ_ONLY when the delegate policies are 
	 * EDITABLE and READ_ONLY.
	 */
	@Test
	public void testDetermineStateWhenEditableAndReadOnly() {
		final Collection<StatePolicy> delegatePolicies = new HashSet<>();
		delegatePolicies.add(editableStatePolicy);
		delegatePolicies.add(readonlyStatePolicy);

		StatePolicy restrictiveStatePolicy = new MostRestrictiveStatePolicyImpl(delegatePolicies);
		EpState result = restrictiveStatePolicy.determineState(actionContainer);
		assertEquals("The state should be READ_ONLY", EpState.READ_ONLY, result); //$NON-NLS-1$
	}
	
	/**
	 * Test that the state is DISABLED when the delegate policies contain one 
	 * that returns DISABLED.
	 */
	@Test
	public void testDetermineStateWhenOneDisabled() {
		final Collection<StatePolicy> delegatePolicies = new HashSet<>();
		delegatePolicies.add(editableStatePolicy);
		delegatePolicies.add(disabledStatePolicy);
		delegatePolicies.add(readonlyStatePolicy);

		StatePolicy restrictiveStatePolicy = new MostRestrictiveStatePolicyImpl(delegatePolicies);
		EpState result = restrictiveStatePolicy.determineState(actionContainer);
		assertEquals("The state should be DISABLED", EpState.DISABLED, result); //$NON-NLS-1$
	}


}
