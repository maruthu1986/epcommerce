/*
 * Copyright © 2018 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.carts.lineitems.dependents.relationship;

import static org.assertj.core.api.Assertions.assertThat;

import io.reactivex.Observable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.definition.carts.DependentLineItemsIdentifier;
import com.elasticpath.rest.definition.carts.LineItemIdentifier;

/**
 * Test class for {@link DependentLineItemsRelationshipDefinition}.
 */
@RunWith(MockitoJUnitRunner.class)
public class DependentLineItemsRelationshipDefinitionTest {

	@Mock
	private LineItemIdentifier lineItemIdentifier;

	@InjectMocks
	private DependentLineItemsRelationshipDefinition relationshipDefinition;

	@Test
	public void verifyDependentItemsIdentifierReturned() {
		final DependentLineItemsIdentifier expectedIdentifier = DependentLineItemsIdentifier.builder()
				.withLineItem(lineItemIdentifier)
				.build();

		final Observable<DependentLineItemsIdentifier> dependentLineItemIdentifiersObservable = relationshipDefinition.onLinkTo();

		assertThat(dependentLineItemIdentifiersObservable.blockingIterable())
				.hasOnlyOneElementSatisfying(identifier ->
													 assertThat(identifier)
															 .isEqualTo(expectedIdentifier));
	}

}