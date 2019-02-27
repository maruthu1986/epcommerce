/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.service.impl;

import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Rule;
import org.junit.Test;

import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import com.elasticpath.service.changeset.BusinessObjectResolver;
import com.elasticpath.service.changeset.ChangeSetPolicyException;

/**
 * Test that resolving changeset objects locally works as expected.
 */
public class LocalResolvingChangeSetServiceImplTest {

	@Rule
	public final MockitoRule rule = MockitoJUnit.rule();
	
	/**
	 * Test that an unresolvable object simply doesn't have a change set.
	 */
	@Test
	public void testFindChangeSetWithUnresolvableObject() {
		
		final BusinessObjectResolver resolver = mock(BusinessObjectResolver.class);
		final Object obj = new Object();

		when(resolver.resolveObjectDescriptor(obj)).thenThrow(new ChangeSetPolicyException("")); //$NON-NLS-1$

		LocalResolvingChangeSetServiceImpl service = new LocalResolvingChangeSetServiceImpl();
		service.setBusinessObjectResolver(resolver);
		
		assertNull("An unresolvable object simply doesn't have a change set", service.findChangeSet(obj)); //$NON-NLS-1$
	}
	
}
