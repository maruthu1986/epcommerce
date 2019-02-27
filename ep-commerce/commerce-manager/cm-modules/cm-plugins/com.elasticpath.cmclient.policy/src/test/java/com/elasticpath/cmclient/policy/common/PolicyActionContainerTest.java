/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.policy.common;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.cmclient.policy.StatePolicyTestBase;
import com.elasticpath.cmclient.policy.StateChangeTarget;
import com.elasticpath.cmclient.policy.StatePolicyDelegate;
import com.elasticpath.domain.catalog.Product;

/**
 * Test adding and removing governables and targets from an action container.
 */
public class PolicyActionContainerTest extends StatePolicyTestBase {

	private PolicyActionContainer container;
	
	/**
	 * Set up required for all tests.
	 * 
	 */
	@Before
	public void setUp() {
		super.setUp();
		container = new PolicyActionContainer("container"); //$NON-NLS-1$
	}

	/**
	 * Test adding and removing a target.
	 */
	@Test
	public void testAddAndRemoveTarget() {
		StateChangeTarget mockTarget = mock(StateChangeTarget.class);
		container.addTarget(mockTarget);
		Collection<StateChangeTarget> result = container.getTargets();
		assertEquals("There should be one target in the container", 1, result.size()); //$NON-NLS-1$
		assertTrue("The expected target should be in the collection", result.contains(mockTarget)); //$NON-NLS-1$
		
		container.removeTarget(mockTarget);
		assertTrue("The container should be empty of targets", container.getTargets().isEmpty()); //$NON-NLS-1$
	}
	
	/**
	 * Test adding and removing a governable.
	 */
	@Test
	public void testAddAndRemoveGovernable() {
		StatePolicyDelegate mockGovernable = mock(StatePolicyDelegate.class);
		container.addDelegate(mockGovernable);
		Collection<StatePolicyDelegate> result = container.getDelegates();
		assertEquals("There should be one governable in the container", 1, result.size()); //$NON-NLS-1$
		assertTrue("The expected governable should be in the collection", result.contains(mockGovernable)); //$NON-NLS-1$
		
		container.removeDelegate(mockGovernable);
		assertTrue("The container should be empty of governables", container.getDelegates().isEmpty()); //$NON-NLS-1$
	}
	
	/**
	 * Test that the governables collection is unmodifiable.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void testGovernablesAreUnmodifiable() {
		StatePolicyDelegate mockGovernable = mock(StatePolicyDelegate.class);
		container.addDelegate(mockGovernable);
		
		container.getDelegates().clear();
	}

	/**
	 * Test that the targets collection is unmodifiable.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void testTargetsAreUnmodifiable() {
		StateChangeTarget mockTarget = mock(StateChangeTarget.class);
		container.addTarget(mockTarget);
		
		container.getTargets().clear();
	}
	
	/**
	 * Test the name is the one given on the constructor.
	 */
	@Test
	public void testGetName() {
		assertEquals("The name returned should be what we gave to the constructor", "container", container.getName()); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	/**
	 * Test the accessor methods for the policy dependent object.
	 */
	@Test
	public void testDependentAccessors() {
		Object policyDependent = mock(Product.class);
		container.setPolicyDependent(policyDependent);
		assertEquals("We should get back the object that was set", policyDependent, container.getPolicyDependent()); //$NON-NLS-1$
	}
}

