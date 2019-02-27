/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.shipping.impl;

import java.util.Locale;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.google.common.annotations.VisibleForTesting;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.identity.util.SubjectUtil;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipping.ShippingOptionRepository;
import com.elasticpath.service.shipping.ShippingOptionService;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;

/**
 * Implementation of ShippingOptionRepository.
 */
@Singleton
@Named("shippingOptionRepository")
public class ShippingOptionRepositoryImpl implements ShippingOptionRepository {

	/**
	 * Error message when shipping option are not in sync.
	 */
	@VisibleForTesting
	public static final String SHIPPING_OPTION_SELECTION_OUT_OF_SYNC =
			"Cart order shipping option and shopping cart selected shipping option are no longer in sync.";
	/**
	 * Error message when shipping option not found.
	 */
	static final String SHIPPING_OPTION_NOT_FOUND = "Shipping option not found.";

	/**
	 * Error message when shipping options not found.
	 */
	@VisibleForTesting
	public static final String SHIPPING_OPTIONS_NOT_FOUND = "Shipping options not found.";

	/**
	 * Error message when shipping address not found.
	 */
	private static final String NO_SHIPPING_ADDRESS_FOUND = "No shipping address found.";

	private final CartOrderRepository cartOrderRepository;
	private final ShippingOptionService shippingOptionService;
	private final ResourceOperationContext resourceOperationContext;

	/**
	 * Constructor.
	 *
	 * @param cartOrderRepository      the cartOrderRepository.
	 * @param shippingOptionService    the shippingOptionService.
	 * @param resourceOperationContext the resourceOperationContext.
	 */
	@Inject
	public ShippingOptionRepositoryImpl(
			@Named("cartOrderRepository") final CartOrderRepository cartOrderRepository,
			@Named("shippingOptionService") final ShippingOptionService shippingOptionService,
			@Named("resourceOperationContext") final ResourceOperationContext resourceOperationContext) {

		this.cartOrderRepository = cartOrderRepository;
		this.shippingOptionService = shippingOptionService;
		this.resourceOperationContext = resourceOperationContext;

	}

	@Override
	public Observable<String> findShippingOptionCodesForShipment(final String storeCode, final Map<String, String> shipmentDetailsId) {
		return findShippingAddressByStoreCodeAndShipmentDetails(storeCode, shipmentDetailsId)
				.flatMapObservable(shippingAddress -> getShippingOptions(storeCode, shippingAddress))
				.map(ShippingOption::getCode);
	}

	@Override
	public Single<ShippingOption> findByCode(final String storeCode, final Map<String, String> shipmentDetailsId,
											 final String shippingOptionCode) {
		return findShippingAddressByStoreCodeAndShipmentDetails(storeCode, shipmentDetailsId)
				.flatMap(shippingAddress -> getShippingOption(shippingOptionCode, getShippingOptions(storeCode, shippingAddress)));
	}

	@Override
	public Single<ShippingOption> getShippingOption(final String shippingOptionCode,
													final Observable<ShippingOption> shippingOptions) {
		return shippingOptions
				.filter(shippingOption -> shippingOptionCode.equals(shippingOption.getCode()))
				.firstOrError()
				.onErrorResumeNext(Single.error(ResourceOperationFailure.notFound(SHIPPING_OPTION_NOT_FOUND)));
	}

	@Override
	public Maybe<String> getSelectedShippingOptionCodeForShipmentDetails(final String storeCode, final Map<String, String> shipmentDetailsId) {
		return cartOrderRepository.findByShipmentDetailsId(storeCode, shipmentDetailsId)
				.flatMapMaybe(cartOrder -> cartOrderRepository.getShippingAddress(cartOrder)
						.flatMap(shippingAddress -> getSelectedShipmentIdFromCartOrder(storeCode, shippingAddress, cartOrder)));
	}

	private Maybe<String> getSelectedShipmentIdFromCartOrder(final String storeCode, final Address shippingAddress, final CartOrder cartOrder) {
		return getSelectedShippingOptionCode(cartOrder)
				.flatMap(selectedShippingOptionCode -> getShippingOptionCodeFromSelectedId(storeCode, shippingAddress, selectedShippingOptionCode));
	}

	private Maybe<String> getShippingOptionCodeFromSelectedId(final String storeCode, final Address shippingAddress,
															   final String selectedShippingOptionCode) {
		return getShippingOption(selectedShippingOptionCode, getShippingOptions(storeCode, shippingAddress))
						.map(ShippingOption::getCode)
				.onErrorResumeNext(Single.error(ResourceOperationFailure.serverError(SHIPPING_OPTION_SELECTION_OUT_OF_SYNC)))
				.toMaybe();
	}

	private Maybe<String> getSelectedShippingOptionCode(final CartOrder cartOrder) {
		return cartOrder.getShippingOptionCode() == null ? Maybe.empty() : Maybe.just(cartOrder.getShippingOptionCode());
	}

	private Observable<ShippingOption> getShippingOptions(final String storeCode, final Address shippingAddress) {
		final Locale locale = SubjectUtil.getLocale(resourceOperationContext.getSubject());

		return Observable.fromIterable(shippingOptionService.getShippingOptions(
				shippingAddress,
				storeCode,
				locale).getAvailableShippingOptions())
				.onErrorResumeNext(Observable.error(ResourceOperationFailure.notFound(SHIPPING_OPTIONS_NOT_FOUND)));
	}

	private Single<Address> findShippingAddressByStoreCodeAndShipmentDetails(final String storeCode,
																			 final Map<String, String> shipmentDetailsId) {
		return cartOrderRepository.findByShipmentDetailsId(storeCode, shipmentDetailsId)
				.flatMap(cartOrder -> cartOrderRepository.getShippingAddress(cartOrder)
						.switchIfEmpty(Maybe.error(ResourceOperationFailure.notFound(NO_SHIPPING_ADDRESS_FOUND)))
						.toSingle());
	}
}
