/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.changeset.policy;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.elasticpath.cmclient.ChangeSetTestBase;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;

/**
 * Test ChangeSetMemberContainerDependentObjectStatePolicyImpl - the object passed
 * to init() is ignored, rather it is retrieved from the target container.
 */
public class ChangeSetMemberContainerDependentObjectStatePolicyImplTest extends ChangeSetTestBase {

	private static final String CONTAINER_NAME = "container"; //$NON-NLS-1$

	/**
	 * Null dependent object make things EDITABLE.
	 */
	@Test
	public void testDetermineStateNull() {
		ChangeSetMemberContainerDependentObjectStatePolicyImpl policy = new ChangeSetMemberContainerDependentObjectStatePolicyImpl();
		PolicyActionContainer container = new PolicyActionContainer(CONTAINER_NAME); 
		assertEquals(EpState.EDITABLE, policy.determineState(container));
	}
	
	
	/**
	 * Objects not in the current change set must be READ_ONLY.
	 */
	@Test
	public void testDetermineStateNotInCurrentChangeSet() {
		ChangeSetMemberContainerDependentObjectStatePolicyImpl policy = new ChangeSetMemberContainerDependentObjectStatePolicyImpl() {
			@Override
			protected boolean currentChangeSetChangeable() {
				return true;
			}
			@Override
			protected boolean isMemberOfCurrentChangeSet(final Object object) {
				return false;
			}
		};
		PolicyActionContainer container = new PolicyActionContainer(CONTAINER_NAME); 
		container.setPolicyDependent(new Object());
		assertEquals(EpState.READ_ONLY, policy.determineState(container));
	}
	
	/**
	 * Objects not in the current change set must be READ_ONLY - especially if the change set cannot be modified.
	 */
	@Test
	public void testDetermineStateNotInCurrentChangeSetWhichIsNotModifiable() {
		ChangeSetMemberContainerDependentObjectStatePolicyImpl policy = new ChangeSetMemberContainerDependentObjectStatePolicyImpl() {
			@Override
			protected boolean currentChangeSetChangeable() {
				return false;
			}
			@Override
			protected boolean isMemberOfCurrentChangeSet(final Object object) {
				return false;
			}
		};
		PolicyActionContainer container = new PolicyActionContainer(CONTAINER_NAME); 
		container.setPolicyDependent(new Object());
		assertEquals(EpState.READ_ONLY, policy.determineState(container));
	}
	
	
	/**
	 * Objects in the current change set must be READ_ONLY if the change set is not modifiable.
	 */
	@Test
	public void testDetermineStateInCurrentChangeSetButTheChangeSetIsNotModifiable() {
		ChangeSetMemberContainerDependentObjectStatePolicyImpl policy = new ChangeSetMemberContainerDependentObjectStatePolicyImpl() {
			@Override
			protected boolean currentChangeSetChangeable() {
				return false;
			}
			@Override
			protected boolean isMemberOfCurrentChangeSet(final Object object) {
				return true;
			}
		};
		PolicyActionContainer container = new PolicyActionContainer(CONTAINER_NAME); 
		container.setPolicyDependent(new Object());
		assertEquals(EpState.READ_ONLY, policy.determineState(container));
	}

	/**
	 * Objects must be EDITABLE if they are in the current change set and that change set is modifiable.
	 */
	@Test
	public void testDetermineStateInCurrentChangeSetAndThatChangeSetIsModifiable() {
		ChangeSetMemberContainerDependentObjectStatePolicyImpl policy = new ChangeSetMemberContainerDependentObjectStatePolicyImpl() {
			@Override
			protected boolean currentChangeSetChangeable() {
				return true;
			}
			@Override
			protected boolean isMemberOfCurrentChangeSet(final Object object) {
				return true;
			}
		};
		PolicyActionContainer container = new PolicyActionContainer(CONTAINER_NAME); 
		container.setPolicyDependent(new Object());
		assertEquals(EpState.EDITABLE, policy.determineState(container));
	}	
	
}
