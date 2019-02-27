/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.shipmentdetails.destinationinfo;

import static org.mockito.Mockito.when;

import static com.elasticpath.rest.resource.integration.epcommerce.repository.ErrorCheckPredicate.createErrorCheckPredicate;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.shipmentdetails.ShipmentDetailsUtil.createShipmentDetailsId;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.definition.addresses.AddressEntity;
import com.elasticpath.rest.definition.addresses.AddressIdentifier;
import com.elasticpath.rest.definition.addresses.AddressesIdentifier;
import com.elasticpath.rest.definition.shipmentdetails.DestinationInfoIdentifier;
import com.elasticpath.rest.definition.shipmentdetails.DestinationInfoSelectorChoiceIdentifier;
import com.elasticpath.rest.definition.shipmentdetails.DestinationInfoSelectorIdentifier;
import com.elasticpath.rest.id.type.CompositeIdentifier;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.addresses.repositories.AddressEntityRepositoryImpl;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipmentdetails.ShipmentDetailsConstants;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipmentdetails.ShipmentDetailsServiceImpl;
import com.elasticpath.rest.selector.ChoiceStatus;

/**
 * Test for {@link DestinationInfoSelectorRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class DestinationInfoSelectorRepositoryImplTest {

	private static final String ORDER_ID = "orderId";
	private static final String SCOPE = "scope";
	private static final String ADDRESS_ID = "addressId";
	private static final String SELECTED_ID = "addressId";
	private static final String NOT_SELECTED_ID = "notSelected";
	private static final int NUM_OF_ADDRESSES = 2;

	private final AddressesIdentifier addressesIdentifier = AddressesIdentifier.builder()
			.withScope(StringIdentifier.of(SCOPE))
			.build();

	private final AddressIdentifier addressIdentifier = AddressIdentifier.builder()
			.withAddressId(StringIdentifier.of(ADDRESS_ID))
			.withAddresses(addressesIdentifier)
			.build();

	private final DestinationInfoIdentifier destinationInfoIdentifier = DestinationInfoIdentifier.builder()
			.withShipmentDetailsId(CompositeIdentifier.of(createShipmentDetailsId(ORDER_ID, ShipmentDetailsConstants.SHIPMENT_TYPE)))
			.withScope(StringIdentifier.of(SCOPE))
			.build();

	private final DestinationInfoSelectorIdentifier selectorIdentifier = DestinationInfoSelectorIdentifier.builder()
			.withDestinationInfo(destinationInfoIdentifier)
			.build();

	private final DestinationInfoSelectorChoiceIdentifier selectorChoiceIdentifier = DestinationInfoSelectorChoiceIdentifier.builder()
			.withDestinationInfoSelector(selectorIdentifier)
			.withAddress(addressIdentifier)
			.build();

	@Mock
	private AddressEntity addressEntity;

	@InjectMocks
	private DestinationInfoSelectorRepositoryImpl<DestinationInfoSelectorIdentifier, DestinationInfoSelectorChoiceIdentifier> repository;

	@Mock
	private Repository<AddressEntity, AddressIdentifier> addressRepository;

	@Mock
	private DestinationInfoService destinationInfoService;

	@Test
	public void verifyGetChoiceReturnsChoosableChoiceWhenSelectedAddressDoesNotExist() {
		when(addressRepository.findOne(addressIdentifier)).thenReturn(Single.just(addressEntity));
		when(destinationInfoService.getSelectedAddressGuidIfShippable(SCOPE, ORDER_ID)).thenReturn(Maybe.empty());

		repository.getChoice(selectorChoiceIdentifier)
				.test()
				.assertNoErrors()
				.assertValue(choice -> choice.getStatus() == ChoiceStatus.CHOOSABLE);
	}

	@Test
	public void verifyGetChoiceReturnsChoosableChoiceWhenAddressIsNotSelected() {
		when(addressRepository.findOne(addressIdentifier)).thenReturn(Single.just(addressEntity));
		when(destinationInfoService.getSelectedAddressGuidIfShippable(SCOPE, ORDER_ID)).thenReturn(Maybe.just(NOT_SELECTED_ID));

		repository.getChoice(selectorChoiceIdentifier)
				.test()
				.assertNoErrors()
				.assertValue(choice -> choice.getStatus() == ChoiceStatus.CHOOSABLE);
	}

	@Test
	public void verifyGetChoiceReturnsChosenChoiceWhenAddressIsSelected() {
		when(addressRepository.findOne(addressIdentifier)).thenReturn(Single.just(addressEntity));
		when(destinationInfoService.getSelectedAddressGuidIfShippable(SCOPE, ORDER_ID)).thenReturn(Maybe.just(SELECTED_ID));

		repository.getChoice(selectorChoiceIdentifier)
				.test()
				.assertNoErrors()
				.assertValue(choice -> choice.getStatus() == ChoiceStatus.CHOSEN);
	}

	@Test
	public void verifyGetChoiceReturnsErrorWhenThereAreNoShippableItems() {
		when(addressRepository.findOne(addressIdentifier)).thenReturn(Single.just(addressEntity));
		when(destinationInfoService.getSelectedAddressGuidIfShippable(SCOPE, ORDER_ID))
				.thenReturn(Maybe.error(ResourceOperationFailure.notFound(ShipmentDetailsServiceImpl.COULD_NOT_FIND_SHIPMENT)));

		repository.getChoice(selectorChoiceIdentifier)
				.test()
				.assertError(createErrorCheckPredicate(ShipmentDetailsServiceImpl.COULD_NOT_FIND_SHIPMENT, ResourceStatus.NOT_FOUND));
	}

	@Test
	public void verifyGetChoiceReturnsNotFoundWhenAddressCannotBeFound() {
		when(addressRepository.findOne(addressIdentifier))
				.thenReturn(Single.error(ResourceOperationFailure.notFound(AddressEntityRepositoryImpl.ADDRESS_NOT_FOUND)));

		repository.getChoice(selectorChoiceIdentifier)
				.test()
				.assertError(createErrorCheckPredicate(AddressEntityRepositoryImpl.ADDRESS_NOT_FOUND, ResourceStatus.NOT_FOUND));
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

		when(destinationInfoService.getSelectedAddressGuidIfShippable(SCOPE, ORDER_ID)).thenReturn(Maybe.empty());
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

		when(destinationInfoService.getSelectedAddressGuidIfShippable(SCOPE, ORDER_ID)).thenReturn(Maybe.just(SELECTED_ID));
		when(addressRepository.findAll(StringIdentifier.of(SCOPE))).thenReturn(Observable.fromIterable(addressIdentifiers));

		repository.getChoices(selectorIdentifier)
				.test()
				.assertNoErrors()
				.assertValueCount(NUM_OF_ADDRESSES)
				.assertValueAt(0, selectorChoice -> selectorChoice.getStatus() == ChoiceStatus.CHOOSABLE)
				.assertValueAt(1, selectorChoice -> selectorChoice.getStatus() == ChoiceStatus.CHOSEN);
	}

	@Test
	public void verifyGetChoicesReturnsErrorWhenNoShippableItemExists() {
		List<AddressIdentifier> addressIdentifiers = new ArrayList<>(NUM_OF_ADDRESSES);

		addressIdentifiers.add(AddressIdentifier.builder()
				.withAddresses(addressesIdentifier)
				.withAddressId(StringIdentifier.of(NOT_SELECTED_ID))
				.build());

		addressIdentifiers.add(addressIdentifier);

		when(destinationInfoService.getSelectedAddressGuidIfShippable(SCOPE, ORDER_ID))
				.thenReturn(Maybe.error(ResourceOperationFailure.notFound(ShipmentDetailsServiceImpl.COULD_NOT_FIND_SHIPMENT)));
		when(addressRepository.findAll(StringIdentifier.of(SCOPE))).thenReturn(Observable.fromIterable(addressIdentifiers));

		repository.getChoices(selectorIdentifier)
				.test()
				.assertError(createErrorCheckPredicate(ShipmentDetailsServiceImpl.COULD_NOT_FIND_SHIPMENT, ResourceStatus.NOT_FOUND));
	}
}
