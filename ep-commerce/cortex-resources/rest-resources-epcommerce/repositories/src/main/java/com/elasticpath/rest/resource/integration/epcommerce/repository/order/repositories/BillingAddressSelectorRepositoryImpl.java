/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.order.repositories;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.repository.Repository;
import com.elasticpath.repository.SelectorRepository;
import com.elasticpath.rest.definition.addresses.AddressEntity;
import com.elasticpath.rest.definition.addresses.AddressIdentifier;
import com.elasticpath.rest.definition.orders.BillingaddressInfoIdentifier;
import com.elasticpath.rest.definition.orders.BillingaddressInfoSelectorChoiceIdentifier;
import com.elasticpath.rest.definition.orders.BillingaddressInfoSelectorIdentifier;
import com.elasticpath.rest.definition.orders.OrderIdentifier;
import com.elasticpath.rest.id.IdentifierPart;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.selector.Choice;
import com.elasticpath.rest.selector.ChoiceStatus;
import com.elasticpath.rest.selector.SelectResult;
import com.elasticpath.rest.selector.SelectStatus;
import com.elasticpath.rest.selector.SelectorChoice;

/**
 * Implementation of a repository for selecting a billing address.
 * 
 * @param <SI>  the selector identifier type
 * @param <CI> the choice identifier type
 */
@Component
public class BillingAddressSelectorRepositoryImpl<SI extends BillingaddressInfoSelectorIdentifier, CI extends 
		BillingaddressInfoSelectorChoiceIdentifier>
		implements SelectorRepository<BillingaddressInfoSelectorIdentifier, BillingaddressInfoSelectorChoiceIdentifier> {

	private CartOrderRepository cartOrderRepository;

	private Repository<AddressEntity, AddressIdentifier> repository;

	@Override
	public Single<Choice> getChoice(final BillingaddressInfoSelectorChoiceIdentifier selectorChoiceId) {
		BillingaddressInfoIdentifier billingAddressInfo = selectorChoiceId.getBillingaddressInfoSelector()
				.getBillingaddressInfo();
		AddressIdentifier addressIdentifier = selectorChoiceId.getAddress();
		String addressId = addressIdentifier.getAddressId().getValue();

		return getSelectedAddress(billingAddressInfo)
				.map(selectedAddress -> buildChoice(selectorChoiceId, getChoiceStatus(addressId, selectedAddress.getGuid())))
				.switchIfEmpty(Maybe.just(buildChoice(selectorChoiceId, ChoiceStatus.CHOOSABLE)))
				.toSingle();
	}

	private ChoiceStatus getChoiceStatus(final String addressId, final String selectedAddress) {
		return selectedAddress.equals(addressId) ? ChoiceStatus.CHOSEN : ChoiceStatus.CHOOSABLE;
	}

	@Override
	public Observable<SelectorChoice> getChoices(final BillingaddressInfoSelectorIdentifier selectorIdentifier) {
		BillingaddressInfoIdentifier billingAddressInfo = selectorIdentifier.getBillingaddressInfo();
		IdentifierPart<String> scope = billingAddressInfo.getOrder().getScope();

		return getSelectedAddress(billingAddressInfo)
				.flatMapObservable(selectedAddress -> getChoicesWithSelectedAddress(selectorIdentifier, scope, selectedAddress.getGuid()))
				.switchIfEmpty(getChoicesWithSelectedAddress(selectorIdentifier, scope, ""));
	}

	private Observable<SelectorChoice> getChoicesWithSelectedAddress(final BillingaddressInfoSelectorIdentifier billingaddressInfoSelectorIdentifier,
																	 final IdentifierPart<String> scope, final String selectedAddress) {
		return repository.findAll(scope)
				.map(addressIdentifier -> buildSelectorChoice(billingaddressInfoSelectorIdentifier, addressIdentifier,
						getChoiceStatus(addressIdentifier.getAddressId().getValue(), selectedAddress)));
	}

	@Override
	public Single<SelectResult<BillingaddressInfoSelectorIdentifier>> selectChoice(final BillingaddressInfoSelectorChoiceIdentifier
																						   billingaddressInfoSelectorChoiceIdentifier) {
		OrderIdentifier orderIdentifier = billingaddressInfoSelectorChoiceIdentifier.getBillingaddressInfoSelector().getBillingaddressInfo()
				.getOrder();
		String addressId = billingaddressInfoSelectorChoiceIdentifier.getAddress().getAddressId().getValue();
		String scope = orderIdentifier.getScope().getValue();
		String orderId = orderIdentifier.getOrderId().getValue();
		return cartOrderRepository.findByGuidAsSingle(scope, orderId)
				.flatMap(cartOrder -> setBillingAddress(cartOrder, addressId))
				.map(selectStatus -> SelectResult.<BillingaddressInfoSelectorIdentifier>builder()
						.withIdentifier(billingaddressInfoSelectorChoiceIdentifier.getBillingaddressInfoSelector())
						.withStatus(selectStatus)
						.build());
	}

	/**
	 * Set the order's billing address.
	 *
	 * @param cartOrder cartOrder
	 * @param addressId addressId
	 * @return CartOrder
	 */
	protected Single<SelectStatus> setBillingAddress(final CartOrder cartOrder, final String addressId) {
		if (addressId.equals(cartOrder.getBillingAddressGuid())) {
			return Single.just(SelectStatus.EXISTING);
		} else {
			cartOrder.setBillingAddressGuid(addressId);
			return cartOrderRepository.saveCartOrderAsSingle(cartOrder)
					.flatMap(savedCartOrder -> Single.just(SelectStatus.SELECTED));
		}
	}

	/**
	 * Get the currently selected address.
	 *
	 * @param billingaddressInfo billingaddressInfo
	 * @return currently selected address
	 */
	protected Maybe<Address> getSelectedAddress(final BillingaddressInfoIdentifier billingaddressInfo) {
		OrderIdentifier orderIdentifier = billingaddressInfo.getOrder();
		String scope = orderIdentifier.getScope().getValue();
		String orderId = orderIdentifier.getOrderId().getValue();

		return cartOrderRepository.findByGuidAsSingle(scope, orderId)
				.flatMapMaybe(cartOrder -> cartOrderRepository.getBillingAddress(cartOrder));
	}

	/**
	 * Build the choice.
	 *
	 * @param billingaddressInfoSelectorChoiceIdentifier billingaddressInfoSelectorChoiceIdentifier
	 * @param choiceStatus                               choiceStatus
	 * @return the choice
	 */
	protected Choice buildChoice(
			final BillingaddressInfoSelectorChoiceIdentifier billingaddressInfoSelectorChoiceIdentifier,
			final ChoiceStatus choiceStatus) {
		return Choice.builder()
				.withDescription(billingaddressInfoSelectorChoiceIdentifier.getAddress())
				.withAction(billingaddressInfoSelectorChoiceIdentifier)
				.withStatus(choiceStatus)
				.build();
	}

	/**
	 * Build the selector choice.
	 *
	 * @param billingaddressInfoSelectorIdentifier billingaddressInfoSelectorIdentifier
	 * @param addressIdentifier                    addressIdentifier
	 * @param choiceStatus                         choiceStatus
	 * @return the selector choice
	 */
	protected SelectorChoice buildSelectorChoice(
			final BillingaddressInfoSelectorIdentifier billingaddressInfoSelectorIdentifier,
			final AddressIdentifier addressIdentifier,
			final ChoiceStatus choiceStatus) {
		return SelectorChoice.builder()
				.withChoice(buildBillingAddressChoiceIdentifier(billingaddressInfoSelectorIdentifier, addressIdentifier))
				.withStatus(choiceStatus)
				.build();
	}

	/**
	 * Build the billing address choice identifier.
	 *
	 * @param billingaddressInfoSelectorIdentifier billingaddressInfoSelectorIdentifier
	 * @param addressIdentifier                    addressIdentifier
	 * @return the billing address choice identifier
	 */
	protected BillingaddressInfoSelectorChoiceIdentifier buildBillingAddressChoiceIdentifier(
			final BillingaddressInfoSelectorIdentifier billingaddressInfoSelectorIdentifier, final AddressIdentifier addressIdentifier) {
		return BillingaddressInfoSelectorChoiceIdentifier.builder()
				.withAddress(addressIdentifier)
				.withBillingaddressInfoSelector(billingaddressInfoSelectorIdentifier)
				.build();
	}

	@Reference
	public void setCartOrderRepository(final CartOrderRepository cartOrderRepository) {
		this.cartOrderRepository = cartOrderRepository;
	}

	@Reference(target = "(name=addressEntityRepositoryImpl)")
	public void setRepository(final Repository<AddressEntity, AddressIdentifier> repository) {
		this.repository = repository;
	}
}
