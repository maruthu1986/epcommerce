/*
 * Copyright © 2018 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.carts.lineitems.dependents.relationship;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.definition.carts.LineItemIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.carts.lineitems.DependentLineItemRepository;

/**
 * Test class for {@link LineItemToParentLineItemRelationshipDefinition}.
 */
@RunWith(MockitoJUnitRunner.class)
public class LineItemToParentLineItemRelationshipDefinitionTest {

	@Mock
	private DependentLineItemRepository repository;

	@Mock
	private LineItemIdentifier lineItemIdentifier;

	@InjectMocks
	private LineItemToParentLineItemRelationshipDefinition relationshipDefinition;

	@Test
	public void verifyEmptyObserableReturnedWhenLineItemIsNotDependent() {
		when(repository.findParent(lineItemIdentifier))
				.thenReturn(Maybe.empty());

		final Observable<LineItemIdentifier> parentLineItemIdentifierObservable = relationshipDefinition.onLinkTo();

		assertThat(parentLineItemIdentifierObservable.blockingIterable())
				.isEmpty();
	}

	@Test
	public void verifyParentLinkReturnedWhenLineItemIsDependent() {
		final LineItemIdentifier expectedParentLineItemIdentifier = mock(LineItemIdentifier.class);

		when(repository.findParent(lineItemIdentifier))
				.thenReturn(Maybe.just(expectedParentLineItemIdentifier));

		final Observable<LineItemIdentifier> parentLineItemIdentifierObservable = relationshipDefinition.onLinkTo();

		assertThat(parentLineItemIdentifierObservable.blockingIterable())
				.hasOnlyOneElementSatisfying(identifier ->
													 assertThat(identifier)
															 .isEqualTo(expectedParentLineItemIdentifier));
	}

}