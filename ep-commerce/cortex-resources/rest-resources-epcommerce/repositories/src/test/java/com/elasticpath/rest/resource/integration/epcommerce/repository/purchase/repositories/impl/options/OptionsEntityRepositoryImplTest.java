/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.purchase.repositories.impl.options;

import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.PURCHASE_ID;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.SCOPE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import io.reactivex.Single;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemOptionIdentifier;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemOptionsIdentifier;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.IdentifierTestFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.order.OrderRepository;

/**
 * Test for the  {@link OptionsEntityRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class OptionsEntityRepositoryImplTest {

	private static final String LINE_ITEM_ID = "line item id";
	private static final String CODE_1 = "code 1";
	private static final String CODE_2 = "code 2";
	private final List<String> guids = ImmutableList.of(LINE_ITEM_ID);

	@InjectMocks
	private OptionsEntityRepositoryImpl<PurchaseLineItemOptionsIdentifier, PurchaseLineItemOptionIdentifier> repository;
	@Mock
	private OrderRepository orderRepository;

	private final PurchaseLineItemOptionsIdentifier identifier = PurchaseLineItemOptionsIdentifier.builder()
			.withPurchaseLineItem(IdentifierTestFactory.buildPurchaseLineItemIdentifier(SCOPE, PURCHASE_ID, guids))
			.build();
	@Mock
	private ProductSku productSku;

	@Before
	public void setUp() {
		when(orderRepository.findProductSku(any(), any(), any())).thenReturn(Single.just(productSku));
	}

	@Test
	public void testGetOneElement() {
		Set<String> valueCodes = new HashSet<>();
		valueCodes.add(CODE_1);
		when(productSku.getOptionValueCodes()).thenReturn(valueCodes);

		PurchaseLineItemOptionIdentifier firstOption = PurchaseLineItemOptionIdentifier.builder()
				.withOptionId(StringIdentifier.of(CODE_1))
				.withPurchaseLineItemOptions(identifier)
				.build();

		repository.getElements(identifier)
				.test()
				.assertValueSequence(ImmutableList.of(firstOption));
	}

	@Test
	public void testGetSeveralElements() {
		Set<String> valueCodes = new HashSet<>();
		valueCodes.add(CODE_1);
		valueCodes.add(CODE_2);
		when(productSku.getOptionValueCodes()).thenReturn(valueCodes);

		PurchaseLineItemOptionIdentifier firstOption = PurchaseLineItemOptionIdentifier.builder()
				.withOptionId(StringIdentifier.of(CODE_1))
				.withPurchaseLineItemOptions(identifier)
				.build();

		PurchaseLineItemOptionIdentifier secondOption = PurchaseLineItemOptionIdentifier.builder()
				.withOptionId(StringIdentifier.of(CODE_2))
				.withPurchaseLineItemOptions(identifier)
				.build();

		repository.getElements(identifier)
				.test()
				.assertValueSequence(ImmutableList.of(firstOption, secondOption));
	}

	@Test
	public void testOptionsNotFound() {
		when(productSku.getOptionValueCodes()).thenReturn(new HashSet<>());

		repository.getElements(identifier)
				.test()
				.assertError(ResourceOperationFailure.notFound(OptionsEntityRepositoryImpl.OPTIONS_NOT_FOUND));
	}


}
