/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.shipmentdetails.destinationinfo;

import java.util.Map;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.repository.Repository;
import com.elasticpath.repository.SelectorRepository;
import com.elasticpath.rest.definition.addresses.AddressEntity;
import com.elasticpath.rest.definition.addresses.AddressIdentifier;
import com.elasticpath.rest.definition.shipmentdetails.DestinationInfoIdentifier;
import com.elasticpath.rest.definition.shipmentdetails.DestinationInfoSelectorChoiceIdentifier;
import com.elasticpath.rest.definition.shipmentdetails.DestinationInfoSelectorIdentifier;
import com.elasticpath.rest.id.IdentifierPart;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipmentdetails.ShipmentDetailsConstants;
import com.elasticpath.rest.selector.Choice;
import com.elasticpath.rest.selector.ChoiceStatus;
import com.elasticpath.rest.selector.SelectResult;
import com.elasticpath.rest.selector.SelectStatus;
import com.elasticpath.rest.selector.SelectorChoice;

/**
 * Destination info selector.
 *
 * @param <SI>	extends DestinationInfoSelectorIdentifier
 * @param <CI>	extends DestinationInfoSelectorChoiceIdentifier
 */
@Component
public class DestinationInfoSelectorRepositoryImpl<SI extends DestinationInfoSelectorIdentifier, CI extends DestinationInfoSelectorChoiceIdentifier>
		implements SelectorRepository<DestinationInfoSelectorIdentifier, DestinationInfoSelectorChoiceIdentifier> {

	private Repository<AddressEntity, AddressIdentifier> addressRepository;
	private DestinationInfoService destinationInfoService;
	private CartOrderRepository cartOrderRepository;

	@Override
	public Observable<SelectorChoice> getChoices(final DestinationInfoSelectorIdentifier destinationInfoSelectorIdentifier) {
		DestinationInfoIdentifier destinationInfoIdentifier = destinationInfoSelectorIdentifier.getDestinationInfo();
		IdentifierPart<String> scope = destinationInfoIdentifier.getScope();
		String orderId = destinationInfoIdentifier.getShipmentDetailsId().getValue().get(ShipmentDetailsConstants.ORDER_ID);
		return destinationInfoService.getSelectedAddressGuidIfShippable(scope.getValue(), orderId)
				.flatMapObservable(selectedAddress -> getChoices(destinationInfoSelectorIdentifier, scope, selectedAddress))
				.switchIfEmpty(getChoices(destinationInfoSelectorIdentifier, scope, ""));
	}

	private Observable<SelectorChoice> getChoices(final DestinationInfoSelectorIdentifier destinationInfoSelectorIdentifier,
												  final IdentifierPart<String> scope, final String selectedAddress) {
		return addressRepository.findAll(scope)
				.map(addressIdentifier -> buildSelectorChoice(destinationInfoSelectorIdentifier, addressIdentifier,
						getChoiceStatus(addressIdentifier.getAddressId().getValue(), selectedAddress)));
	}

	private SelectorChoice buildSelectorChoice(final DestinationInfoSelectorIdentifier destinationInfoSelectorIdentifier,
											   final AddressIdentifier addressIdentifier,
											   final ChoiceStatus choiceStatus) {
		return SelectorChoice.builder()
				.withChoice(buildDestinationInfoSelectorChoiceIdentifier(destinationInfoSelectorIdentifier, addressIdentifier))
				.withStatus(choiceStatus)
				.build();
	}

	private DestinationInfoSelectorChoiceIdentifier buildDestinationInfoSelectorChoiceIdentifier(
			final DestinationInfoSelectorIdentifier destinationInfoSelectorIdentifier,
			final AddressIdentifier addressIdentifier) {
		return DestinationInfoSelectorChoiceIdentifier.builder()
				.withAddress(addressIdentifier)
				.withDestinationInfoSelector(destinationInfoSelectorIdentifier)
				.build();
	}

	private ChoiceStatus getChoiceStatus(final String addressId, final String selectedAddressId) {
		return selectedAddressId.equals(addressId) ? ChoiceStatus.CHOSEN : ChoiceStatus.CHOOSABLE;
	}

	@Override
	public Single<Choice> getChoice(final DestinationInfoSelectorChoiceIdentifier selectorChoiceId) {
		DestinationInfoIdentifier destinationInfoIdentifier = selectorChoiceId.getDestinationInfoSelector()
				.getDestinationInfo();
		Map<String, String> shipmentDetailsId = destinationInfoIdentifier.getShipmentDetailsId().getValue();
		String orderId = shipmentDetailsId.get(ShipmentDetailsConstants.ORDER_ID);
		String scope = destinationInfoIdentifier.getScope().getValue();
		AddressIdentifier addressIdentifier = selectorChoiceId.getAddress();
		return addressRepository.findOne(addressIdentifier)
				.flatMapMaybe(addressEntity -> buildChoiceIfAddressExists(selectorChoiceId, orderId, scope, addressIdentifier))
				.toSingle();
	}

	private Maybe<Choice> buildChoiceIfAddressExists(final DestinationInfoSelectorChoiceIdentifier selectorChoiceId, final String orderId,
													 final String scope, final AddressIdentifier addressIdentifier) {
		return destinationInfoService.getSelectedAddressGuidIfShippable(scope, orderId)
				.map(selectedAddress -> buildChoice(selectorChoiceId, addressIdentifier, selectedAddress))
				.switchIfEmpty(Maybe.just(buildChoice(selectorChoiceId, addressIdentifier, "")));
	}


	private Choice buildChoice(final DestinationInfoSelectorChoiceIdentifier destinationInfoSelectorChoiceIdentifier,
							   final AddressIdentifier addressIdentifier,
							   final String selectedAddressId) {
		String addressId = addressIdentifier.getAddressId().getValue();
		boolean isSelected = selectedAddressId.equals(addressId);
		ChoiceStatus choiceStatus = isSelected ? ChoiceStatus.CHOSEN : ChoiceStatus.CHOOSABLE;
		return buildChoice(destinationInfoSelectorChoiceIdentifier, addressIdentifier, isSelected, choiceStatus);
	}

	private Choice buildChoice(final DestinationInfoSelectorChoiceIdentifier destinationInfoSelectorChoiceIdentifier, final AddressIdentifier
			addressIdentifier, final boolean isSelected, final ChoiceStatus choiceStatus) {
		Choice.Builder choiceBuilder = Choice.builder()
				.withDescription(addressIdentifier)
				.withStatus(choiceStatus);
		if (!isSelected) {
			choiceBuilder.withAction(destinationInfoSelectorChoiceIdentifier);
		}
		return choiceBuilder.build();
	}

	@Override
	public Single<SelectResult<DestinationInfoSelectorIdentifier>> selectChoice(final DestinationInfoSelectorChoiceIdentifier
																						destinationInfoSelectorChoiceIdentifier) {
		DestinationInfoIdentifier destinationInfoIdentifier = destinationInfoSelectorChoiceIdentifier.getDestinationInfoSelector()
				.getDestinationInfo();
		String scope = destinationInfoIdentifier.getScope().getValue();
		String orderId = destinationInfoIdentifier.getShipmentDetailsId().getValue().get(ShipmentDetailsConstants.ORDER_ID);
		String addressId = destinationInfoSelectorChoiceIdentifier.getAddress().getAddressId().getValue();
		return destinationInfoService.validateOrderIsShippable(scope, orderId)
				.flatMap(validatedId -> cartOrderRepository.updateShippingAddressOnCartOrderAsSingle(addressId, orderId, scope))
				.map(isAddressUpdated -> {
					SelectStatus status = isAddressUpdated ? SelectStatus.SELECTED : SelectStatus.EXISTING;
					return SelectResult.<DestinationInfoSelectorIdentifier>builder()
							.withIdentifier(destinationInfoSelectorChoiceIdentifier.getDestinationInfoSelector())
							.withStatus(status)
							.build();
				});
	}

	@Reference(target = "(name=addressEntityRepositoryImpl)")
	public void setAddressRepository(final Repository<AddressEntity, AddressIdentifier> addressRepository) {
		this.addressRepository = addressRepository;
	}

	@Reference
	public void setDestinationInfoService(final DestinationInfoService destinationInfoService) {
		this.destinationInfoService = destinationInfoService;
	}

	@Reference
	public void setCartOrderRepository(final CartOrderRepository cartOrderRepository) {
		this.cartOrderRepository = cartOrderRepository;
	}
}
