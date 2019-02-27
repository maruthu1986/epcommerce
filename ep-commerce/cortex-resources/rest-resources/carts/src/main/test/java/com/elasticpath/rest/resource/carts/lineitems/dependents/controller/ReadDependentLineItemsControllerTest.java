/*
 * Copyright © 2018 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.carts.lineitems.dependents.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.reactivex.Observable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.definition.carts.DependentLineItemsIdentifier;
import com.elasticpath.rest.definition.carts.LineItemIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.carts.lineitems.DependentLineItemRepository;

/**
 * Test class for {@link ReadDependentLineItemsPrototype}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ReadDependentLineItemsControllerTest {

	@Mock
	private DependentLineItemsIdentifier dependentlineitemsIdentifier;

	@Mock
	private DependentLineItemRepository dependentLineItemsRepository;

	@InjectMocks
	private ReadDependentLineItemsPrototype controller;

	@Test
	public void verifyEmptyCollectionReturnedWhenNoDependentItems() {
		final LineItemIdentifier lineItemIdentifier = dependentlineitemsIdentifier.getLineItem();

		when(dependentLineItemsRepository.getElements(lineItemIdentifier))
				.thenReturn(Observable.empty());

		final Observable<LineItemIdentifier> dependentLineItemIdentifiersObservable = controller.onRead();

		assertThat(dependentLineItemIdentifiersObservable.blockingIterable())
				.isEmpty();
	}

	@Test
	public void verifyCollectionContainsAllDependentLineItems() {
		final LineItemIdentifier parentLineItemIdentifier = dependentlineitemsIdentifier.getLineItem();

		final LineItemIdentifier dependentLineItemIdentifier1 = mock(LineItemIdentifier.class);
		final LineItemIdentifier dependentLineItemIdentifier2 = mock(LineItemIdentifier.class);

		when(dependentLineItemsRepository.getElements(parentLineItemIdentifier))
				.thenReturn(Observable.fromArray(dependentLineItemIdentifier1, dependentLineItemIdentifier2));

		final Observable<LineItemIdentifier> dependentLineItemIdentifiersObservable = controller.onRead();

		assertThat(dependentLineItemIdentifiersObservable.blockingIterable())
				.containsExactlyInAnyOrder(dependentLineItemIdentifier1, dependentLineItemIdentifier2);
	}

}