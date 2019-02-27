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
 * Test the ChangesetMemberStatePolicy.
 */
public class ChangesetMemberStatePolicyImplTest extends ChangeSetTestBase {

	/**
	 * Null objects must be READ_ONLY.
	 */
	@Test
	public void testDetermineStateNull() {
		ChangesetMemberStatePolicyImpl policy = new ChangesetMemberStatePolicyImpl();
		assertEquals(EpState.READ_ONLY, policy.determineState(null));
	}
	
	
	/**
	 * Objects not in the current change set must be READ_ONLY.
	 */
	@Test
	public void testDetermineStateNotInCurrentChangeSet() {
		ChangesetMemberStatePolicyImpl policy = new ChangesetMemberStatePolicyImpl() {
			@Override
			protected boolean currentChangeSetChangeable() {
				return true;
			}
			@Override
			protected boolean isMemberOfCurrentChangeSet(final Object object) {
				return false;
			}
		};
		policy.init(new Object());
		assertEquals(EpState.READ_ONLY, policy.determineState(null));
	}
	
	/**
	 * Objects not in the current change set must be READ_ONLY - especially if the change set cannot be modified.
	 */
	@Test
	public void testDetermineStateNotInCurrentChangeSetWhichIsNotModifiable() {
		ChangesetMemberStatePolicyImpl policy = new ChangesetMemberStatePolicyImpl() {
			@Override
			protected boolean currentChangeSetChangeable() {
				return false;
			}
			@Override
			protected boolean isMemberOfCurrentChangeSet(final Object object) {
				return false;
			}
		};
		policy.init(new Object());
		assertEquals(EpState.READ_ONLY, policy.determineState(null));
	}
	
	
	/**
	 * Objects in the current change set must be READ_ONLY if the change set is not modifiable.
	 */
	@Test
	public void testDetermineStateInCurrentChangeSetButTheChangeSetIsNotModifiable() {
		ChangesetMemberStatePolicyImpl policy = new ChangesetMemberStatePolicyImpl() {
			@Override
			protected boolean currentChangeSetChangeable() {
				return false;
			}
			@Override
			protected boolean isMemberOfCurrentChangeSet(final Object object) {
				return true;
			}
		};
		policy.init(new Object());
		assertEquals(EpState.READ_ONLY, policy.determineState(null));
	}

	/**
	 * Objects must be EDITABLE if they are in the current change set and that change set is modifiable.
	 */
	@Test
	public void testDetermineStateInCurrentChangeSetAndThatChangeSetIsModifiable() {
		PolicyActionContainer policyActionContainer = new PolicyActionContainer("test_container"); //$NON-NLS-1$
		
		ChangesetMemberStatePolicyImpl policy = new ChangesetMemberStatePolicyImpl() {
			@Override
			protected boolean currentChangeSetChangeable() {
				return true;
			}
			@Override
			protected boolean isMemberOfCurrentChangeSet(final Object object) {
				return true;
			}
		};
		policy.init(new Object());
		assertEquals(EpState.EDITABLE, policy.determineState(policyActionContainer));
	}	
	
}
