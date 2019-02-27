/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.changeset.policy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.Test;

import org.mockito.Mock;

import com.elasticpath.cmclient.ChangeSetTestBase;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;

/**
 * Tests the ChangesetMemberCategoryCatalogStatePolicy.
 */
@SuppressWarnings({ "restriction" })
public class ChangeSetMemberCategoryCatalogStatePolicyImplTest extends ChangeSetTestBase {


	@Mock
	private Catalog catalog;

	@Mock
	private Category category;


	/**
	 * Container state should be editable if catalog is a master catalog.
	 */
	@Test
	public void testDetermineContainerStateIsEditableIfCatalogIsMaster() {
		when(catalog.isMaster()).thenReturn(true);

		PolicyActionContainer policyActionContainer = new PolicyActionContainer("test_container"); //$NON-NLS-1$

		ChangeSetMemberCategoryCatalogStatePolicyImpl policy = new ChangeSetMemberCategoryCatalogStatePolicyImpl();
		policy.init(catalog);
		assertEquals("The container state should be editable.", EpState.EDITABLE, policy.determineContainerState(policyActionContainer));
	}

	/**
	 * When given a category, the ChangeSetMemberCategoryCatalogStatePolicyImpl should use the catalog as the dependent object.
	 */
	@Test
	public void testInitUsesCatalogWhenGivenACategory() {
		when(category.getCatalog()).thenReturn(catalog);

		ChangeSetMemberCategoryCatalogStatePolicyImpl policy = new ChangeSetMemberCategoryCatalogStatePolicyImpl();
		policy.init(category);
		assertTrue("The dependent object should be a catalog.", policy.getDependentObject() instanceof Catalog);
	}
}
