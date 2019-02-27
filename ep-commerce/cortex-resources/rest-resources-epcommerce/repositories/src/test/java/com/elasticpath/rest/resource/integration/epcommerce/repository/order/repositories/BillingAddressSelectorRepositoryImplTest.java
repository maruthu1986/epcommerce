/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.order.repositories;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.addresses.AddressEntity;
import com.elasticpath.rest.definition.addresses.AddressIdentifier;
import com.elasticpath.rest.definition.addresses.AddressesIdentifier;
import com.elasticpath.rest.definition.orders.BillingaddressInfoIdentifier;
import com.elasticpath.rest.definition.orders.BillingaddressInfoSelectorChoiceIdentifier;
import com.elasticpath.rest.definition.orders.BillingaddressInfoSelectorIdentifier;
import com.elasticpath.rest.definition.orders.OrderIdentifier;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.selector.ChoiceStatus;

/**
 * Test for {@link BillingAddressSelectorRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class BillingAddressSelectorRepositoryImplTest {

	private static final String SCOPE = "scope";
	private static final String ADDRESS_ID = "addressId";
	private static final String ORDER_ID = "orderId";
	private static final String SELECTED_ID = "addressId";
	private static final String NOT_SELECTED_ID = "notSelected";
	private static final int NUM_OF_ADDRESSES = 2;

	private final OrderIdentifier orderIdentifier = OrderIdentifier.builder()
			.withScope(StringIdentifier.of(SCOPE))
			.withOrderId(StringIdentifier.of(ORDER_ID))
			.build();

	private final BillingaddressInfoIdentifier infoIdentifier = BillingaddressInfoIdentifier.builder()
			.withOrder(orderIdentifier)
			.build();

	private final BillingaddressInfoSelectorIdentifier selectorIdentifier = BillingaddressInfoSelectorIdentifier.builder()
			.withBillingaddressInfo(infoIdentifier)
			.build();

	private final AddressesIdentifier addressesIdentifier = AddressesIdentifier.builder()
			.withScope(StringIdentifier.of(SCOPE))
			.build();

	private final AddressIdentifier addressIdentifier = AddressIdentifier.builder()
			.withAddresses(addressesIdentifier)
			.withAddressId(StringIdentifier.of(ADDRESS_ID))
			.build();

	private final BillingaddressInfoSelectorChoiceIdentifier selectorChoiceIdentifier = BillingaddressInfoSelectorChoiceIdentifier.builder()
			.withAddress(addressIdentifier)
			.withBillingaddressInfoSelector(selectorIdentifier)
			.build();

	@Mock
	private CartOrder cartOrder;

	@Mock
	private Address selectedAddress;

	@InjectMocks
	private BillingAddressSelectorRepositoryImpl<BillingaddressInfoSelectorIdentifier, BillingaddressInfoSelectorChoiceIdentifier> repository;

	@Mock
	private CartOrderRepository cartOrderRepository;

	@Mock
	private Repository<AddressEntity, AddressIdentifier> addressRepository;

	@Before
	public void setUp() {
		when(cartOrderRepository.findByGuidAsSingle(SCOPE, ORDER_ID)).thenReturn(Single.just(cartOrder));
	}

	@Test
	public void verifyGetChoiceReturnsChoosableChoiceWhenSelectedAddressDoesNotExist() {
		when(cartOrderRepository.getBillingAddress(cartOrder)).thenReturn(Maybe.empty());

		repository.getChoice(selectorChoiceIdentifier)
				.test()
				.assertNoErrors()
				.assertValue(choice -> choice.getStatus() == ChoiceStatus.CHOOSABLE);
	}

	@Test
	public void verifyGetChoiceReturnsChoosableChoiceWhenAddressIsNotSelected() {
		when(cartOrderRepository.getBillingAddress(cartOrder)).thenReturn(Maybe.just(selectedAddress));
		when(selectedAddress.getGuid()).thenReturn(NOT_SELECTED_ID);

		repository.getChoice(selectorChoiceIdentifier)
				.test()
				.assertNoErrors()
				.assertValue(choice -> choice.getStatus() == ChoiceStatus.CHOOSABLE);
	}

	@Test
	public void verifyGetChoiceReturnsChosenChoiceWhenAddressIsSelected() {
		when(cartOrderRepository.getBillingAddress(cartOrder)).thenReturn(Maybe.just(selectedAddress));
		when(selectedAddress.getGuid()).thenReturn(SELECTED_ID);

		repository.getChoice(selectorChoiceIdentifier)
				.test()
				.assertNoErrors()
				.assertValue(choice -> choice.getStatus() == ChoiceStatus.CHOSEN);
	}

	@Test
	public void verifyGetChoicesReturnsListOfChoosableChoicesWhenSelectedAddressDoesNotExist() {
		List<AddressIdentifier> addressIdentifiers = new ArrayList<>(NUM_OF_ADDRESSES);
		for (int i = 0; i < NUM_OF_ADDRESSES; i++) {
			addressIdentifiers.add(AddressIdentifier.builder()
					.withAddresses(addressesIdentifier)
					.withAddressId(StringIdentifier.of(String.valueOf(i)))
					.build());
		}

		when(cartOrderRepository.getBillingAddress(cartOrder)).thenReturn(Maybe.empty());
		when(addressRepository.findAll(StringIdentifier.of(SCOPE))).thenReturn(Observable.fromIterable(addressIdentifiers));

		repository.getChoices(selectorIdentifier)
				.test()
				.assertNoErrors()
				.assertValueCount(NUM_OF_ADDRESSES)
				.assertValueAt(0, selectorChoice -> selectorChoice.getStatus() == ChoiceStatus.CHOOSABLE)
				.assertValueAt(1, selectorChoice -> selectorChoice.getStatus() == ChoiceStatus.CHOOSABLE);
	}

	@Test
	public void verifyGetChoicesReturnsListOfChoicesWhenSelectedAddressExists() {
		List<AddressIdentifier> addressIdentifiers = new ArrayList<>(NUM_OF_ADDRESSES);

		addressIdentifiers.add(AddressIdentifier.builder()
				.withAddresses(addressesIdentifier)
				.withAddressId(StringIdentifier.of(NOT_SELECTED_ID))
				.build());

		addressIdentifiers.add(addressIdentifier);

		when(cartOrderRepository.getBillingAddress(cartOrder)).thenReturn(Maybe.just(selectedAddress));
		when(addressRepository.findAll(StringIdentifier.of(SCOPE))).thenReturn(Observable.fromIterable(addressIdentifiers));
		when(selectedAddress.getGuid()).thenReturn(SELECTED_ID);

		repository.getChoices(selectorIdentifier)
				.test()
				.assertNoErrors()
				.assertValueCount(NUM_OF_ADDRESSES)
				.assertValueAt(0, selectorChoice -> selectorChoice.getStatus() == ChoiceStatus.CHOOSABLE)
				.assertValueAt(1, selectorChoice -> selectorChoice.getStatus() == ChoiceStatus.CHOSEN);
	}

	@Test
	public void verifySelectChoiceIsComplete() {
		when(cartOrderRepository.saveCartOrderAsSingle(cartOrder)).thenReturn(Single.just(cartOrder));

		repository.selectChoice(selectorChoiceIdentifier)
				.test()
				.assertComplete()
				.assertNoErrors();
	}
}
