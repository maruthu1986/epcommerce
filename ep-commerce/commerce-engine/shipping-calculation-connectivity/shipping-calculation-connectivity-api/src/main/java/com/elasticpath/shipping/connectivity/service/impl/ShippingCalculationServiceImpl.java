/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.shipping.connectivity.service.impl;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.commons.lang.ObjectUtils;
import org.apache.log4j.Logger;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.shipping.connectivity.dto.PricedShippableItem;
import com.elasticpath.shipping.connectivity.dto.PricedShippableItemContainer;
import com.elasticpath.shipping.connectivity.dto.ShippableItem;
import com.elasticpath.shipping.connectivity.dto.ShippableItemContainer;
import com.elasticpath.shipping.connectivity.dto.ShippingAddress;
import com.elasticpath.shipping.connectivity.dto.ShippingCalculationResult;
import com.elasticpath.shipping.connectivity.dto.ShippingCalculationResult.ErrorInformation;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;
import com.elasticpath.shipping.connectivity.dto.builder.ShippingCalculationResultBuilder;
import com.elasticpath.shipping.connectivity.dto.impl.ShippingCalculationResultErrorInformationImpl;
import com.elasticpath.shipping.connectivity.service.ShippingCalculationService;
import com.elasticpath.shipping.connectivity.service.selector.PricedShippingCalculationPluginSelector;
import com.elasticpath.shipping.connectivity.service.selector.UnpricedShippingCalculationPluginSelector;
import com.elasticpath.shipping.connectivity.spi.ShippingCalculationPlugin;
import com.elasticpath.shipping.connectivity.spi.capability.ShippingCalculationCapability;
import com.elasticpath.shipping.connectivity.spi.capability.ShippingCostCalculationCapability;
import com.elasticpath.shipping.connectivity.spi.capability.ShippingOptionListAllCapability;
import com.elasticpath.shipping.connectivity.spi.capability.ShippingOptionListCapability;
import com.elasticpath.shipping.connectivity.spi.capability.ShippingOptionListPerDestinationCapability;

/**
 * Default implementation of {@link ShippingCalculationService}.
 */
public class ShippingCalculationServiceImpl implements ShippingCalculationService {
	private static final Logger LOG = Logger.getLogger(ShippingCalculationServiceImpl.class);

	/**
	 * Error code for when no shipping calculation provider is matched.
	 */
	public static final String NO_PROVIDER_MATCHED_ERROR_CODE = "NO_PROVIDER_MATCHED_ERROR_CODE";

	/**
	 * Error code for when no shipping calculation provider capability is matched.
	 */
	public static final String NO_PROVIDER_CAPABILITY_MATCHED_ERROR_CODE = "NO_PROVIDER_CAPABILITY_MATCHED_ERROR_CODE";

	/**
	 * Error message for when no shipping calculation provider capability is matched.
	 */
	public static final String NO_PROVIDER_CAPABILITY_MATCHED_ERROR_MSG = "Plugin [%s] doesn't support capability [%s].";

	/**
	 * Error code for when the shipping calculation provider throws an exception when called.
	 */
	public static final String PROVIDER_THREW_EXCEPTION_ERROR_CODE = "PROVIDER_THREW_EXCEPTION_ERROR_CODE";

	private UnpricedShippingCalculationPluginSelector unpricedShippingCalculationPluginSelector;
	private PricedShippingCalculationPluginSelector pricedShippingCalculationPluginSelector;

	private Supplier<ShippingCalculationResultBuilder> shippingCalculationResultBuilderSupplier;

	@Override
	public ShippingCalculationResult getUnpricedShippingOptions(final ShippableItemContainer<?> unpricedShippableItemContainer) {
		final ShippingCalculationResult result;

		requireNonNull(unpricedShippableItemContainer, "Unpriced shipping options cannot be requested with a null shippable item container.");

		// If no shipping address is set on the shippable item container then we return an empty result since no shipping options are available
		// until the shipping address has been specified, and callers should not need to have to inspect the shippable item container before passing
		// it in as that should be opaque to them. This is different to the other methods below that pass in a specific shipping address.
		final ShippingAddress destination = unpricedShippableItemContainer.getDestinationAddress();
		if (destination == null) {
			result = createEmptyResult();
		} else {

			if (LOG.isDebugEnabled()) {
				LOG.debug(format("Getting unpriced shipping calculation provider for store [%s] and destination [%s]...",
						unpricedShippableItemContainer.getStoreCode(), ObjectUtils.toString(destination, "null")));
			}

			final Collection<? extends ShippableItem> shippableItems = unpricedShippableItemContainer.getShippableItems();
			final String storeCode = unpricedShippableItemContainer.getStoreCode();

			final ShippingCalculationPlugin unpricedProvider = getUnpricedShippingCalculationPlugin(shippableItems, destination, storeCode);
			return getShippingOptions(unpricedProvider, ShippingOptionListCapability.class,
					capability -> capability.getUnpricedShippingOptions(unpricedShippableItemContainer));

		}

		return result;
	}

	@Override
	public ShippingCalculationResult getPricedShippingOptions(final PricedShippableItemContainer<?> pricedShippableItemContainer) {
		final Collection<? extends PricedShippableItem> shippableItems = pricedShippableItemContainer.getShippableItems();
		final ShippingAddress destination = pricedShippableItemContainer.getDestinationAddress();
		final String storeCode = pricedShippableItemContainer.getStoreCode();

		requireNonNull(destination, "Shipping options cannot be requested with a null destination address.");

		if (LOG.isDebugEnabled()) {
			LOG.debug(format("Getting priced shipping calculation provider for store [%s] and destination [%s]...",
					storeCode, destination.toString()));
		}

		final ShippingCalculationPlugin pricedProvider = getPricedShippingCalculationPlugin(shippableItems, destination, storeCode);
		return getShippingOptions(pricedProvider, ShippingCostCalculationCapability.class,
				capability -> capability.getPricedShippingOptions(pricedShippableItemContainer));
	}

	@Override
	public ShippingCalculationResult getAllShippingOptions(final String storeCode, final Locale locale) {
		final ShippingCalculationPlugin unpricedProvider = getUnpricedShippingCalculationPlugin(null, null, storeCode);
		return getShippingOptions(unpricedProvider, ShippingOptionListAllCapability.class,
				capability -> capability.getAllShippingOptions(storeCode, locale));
	}

	@Override
	public ShippingCalculationResult getUnpricedShippingOptions(final ShippingAddress destAddress,
																final String storeCode,
																final Locale locale) {
		final ShippingCalculationPlugin unpricedProvider = getUnpricedShippingCalculationPlugin(null, destAddress, storeCode);
		return getShippingOptions(unpricedProvider,
				ShippingOptionListPerDestinationCapability.class,
				capability -> capability.getUnpricedShippingOptions(destAddress, storeCode, locale));
	}

	/**
	 * Returns {@link ShippingCalculationResult} from given generic shippingCalculationPlugin and shippingOptionsFunction.
	 * If shippingCalculationPlugin is null, result is built with {@link ShippingCalculationResultBuilder}.
	 *
	 * @param shippingCalculationPlugin shipping calculation provider
	 * @param capabilityClass           shipping calculation provider capability class
	 * @param shippingOptionsFunction   shipping option function
	 * @param <P>                       shipping calculation provider type
	 * @param <C>                       shipping calculation provider capability class type
	 * @return ShippingCalculationResult
	 */
	private <P extends ShippingCalculationPlugin, C extends ShippingCalculationCapability> ShippingCalculationResult getShippingOptions(
			final P shippingCalculationPlugin,
			final Class<C> capabilityClass,
			final Function<C, List<ShippingOption>> shippingOptionsFunction) {

		ShippingCalculationResult result;

		if (shippingCalculationPlugin == null) {
			// No provider found so return a result indicating that
			result = getShippingCalculationResultBuilderSupplier().get()
					.withErrorInformation(createNoProviderFoundErrorInformation())
					.build();
		} else {
			LOG.debug(format("Using [%s] shipping calculation provider", shippingCalculationPlugin));
			if (shippingCalculationPlugin.hasCapability(capabilityClass)) {
				C capability = shippingCalculationPlugin.getCapability(capabilityClass);
				try {
					final List<ShippingOption> resultShippingOptionList = shippingOptionsFunction.apply(capability);
					result = getShippingCalculationResultBuilderSupplier().get()
							.withShippingOptions(resultShippingOptionList)
							.build();
				} catch (final Exception e) {
					result = createResultWhenProviderThrowsException(e);
				}
				logShippingCalculationResult(result);

			} else {
				return getShippingCalculationResultBuilderSupplier().get()
						.withErrorInformation(new ShippingCalculationResultErrorInformationImpl(
								NO_PROVIDER_CAPABILITY_MATCHED_ERROR_CODE,
								format(NO_PROVIDER_CAPABILITY_MATCHED_ERROR_MSG,
										shippingCalculationPlugin.getName(),
										capabilityClass.getSimpleName())))
						.build();
			}
		}

		// A sanity check ot make sure that the function passed in respects the method contract of never returning null.
		requireNonNull(result, "The ShippingCalculationResult returned should never be null");

		return result;
	}

	/**
	 * Factory method returning an empty {@link ShippingCalculationResult} object.
	 *
	 * @return an empty {@link ShippingCalculationResult} object.
	 */
	protected ShippingCalculationResult createEmptyResult() {
		return getShippingCalculationResultBuilderSupplier().get()
				.withShippingOptions(Collections.emptyList())
				.build();
	}

	/**
	 * Factory method returning a {@link ErrorInformation} object for when no shipping calculation provider has been matched.
	 *
	 * @return a {@link ErrorInformation} object with {@link #NO_PROVIDER_MATCHED_ERROR_CODE} error code.
	 */
	protected ErrorInformation createNoProviderFoundErrorInformation() {
		return new ShippingCalculationResultErrorInformationImpl(NO_PROVIDER_MATCHED_ERROR_CODE);
	}

	/**
	 * Factory method returning a {@link ShippingCalculationResult} object with no shipping options and an error indicating that the provider
	 * threw an exception when called.
	 *
	 * @param exceptionThrownByProvider the exception thrown by provider.
	 * @return a {@link ShippingCalculationResult} object.
	 */
	protected ShippingCalculationResult createResultWhenProviderThrowsException(final Exception exceptionThrownByProvider) {
		return getShippingCalculationResultBuilderSupplier().get()
				.withErrorInformation(createErrorInformationFromProviderException(exceptionThrownByProvider))
				.build();
	}

	/**
	 * Factory method returning a {@link ErrorInformation} object for when the shipping calculation provider throws an exception.
	 *
	 * @param exceptionThrownByProvider the exception thrown by provider.
	 * @return a {@link ErrorInformation} object with {@link #NO_PROVIDER_MATCHED_ERROR_CODE} error code.
	 */
	protected ErrorInformation createErrorInformationFromProviderException(final Exception exceptionThrownByProvider) {
		return new ShippingCalculationResultErrorInformationImpl(PROVIDER_THREW_EXCEPTION_ERROR_CODE, exceptionThrownByProvider);
	}

	/**
	 * Logs the shipping calculation result, delegates to {@link #logSuccessfulShippingCalculationResult(ShippingCalculationResult)} and
	 * {@link #logUnsuccessfulShippingCalculationResult(ShippingCalculationResult)} appropriately.
	 *
	 * @param result the result to log.
	 */
	protected void logShippingCalculationResult(final ShippingCalculationResult result) {
		if (result.isSuccessful()) {
			logSuccessfulShippingCalculationResult(result);
		} else {
			logUnsuccessfulShippingCalculationResult(result);
		}
	}

	/**
	 * Logs a successful shipping calculation result.
	 *
	 * @param result the result to log.
	 */
	protected void logSuccessfulShippingCalculationResult(final ShippingCalculationResult result) {
		final List<ShippingOption> availableShippingOptions = result.getAvailableShippingOptions();

		if (availableShippingOptions == null) {
			throw new EpServiceException(format("A null available shipping options list was returned from a result marked as successful. "
							+ "This is incorrect, all successful responses should have a list returned even if it's empty. Result: %s",
					result.toString()));
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug(format("Shipping result options: %s", availableShippingOptions.stream()
					.map(option -> option.getCode() + " (" + option.getDisplayName(Locale.getDefault()).orElse(null) + ")")
					.collect(Collectors.toList())));
		}
	}

	/**
	 * Logs an unsuccessful shipping calculation result.
	 *
	 * @param result the result to log.
	 */
	protected void logUnsuccessfulShippingCalculationResult(final ShippingCalculationResult result) {
		final Optional<ErrorInformation> errorInformation = result.getErrorInformation();

		if (errorInformation.isPresent()) {
			LOG.warn(format("Unsuccessful ShippingCalculationResult: Error Information: %s", errorInformation.get()));
		} else {
			LOG.warn(format("Unsuccessful ShippingCalculationResult, result: %s", result));
		}
	}

	/**
	 * Returns {@link ShippingCalculationPlugin} from given variables.
	 *
	 * @param shippableItems shippable items
	 * @param destination    destination address
	 * @param storeCode      store code
	 * @return unpriced shipping calculation provider
	 */
	protected ShippingCalculationPlugin getUnpricedShippingCalculationPlugin(final Collection<? extends ShippableItem> shippableItems,
																			 final ShippingAddress destination,
																			 final String storeCode) {
		final ShippingCalculationPlugin result = getUnpricedShippingCalculationPluginSelector()
				.getUnpricedShippingCalculationPlugin(shippableItems, destination, storeCode);

		if (result == null) {
			LOG.error(format("No unpriced shipping calculation provider found matching: Address: %s; Store: %s; Shippable Items: %s",
					destination, storeCode, shippableItems));
		}

		return result;
	}

	/**
	 * Returns {@link ShippingCalculationPlugin} from given variables.
	 *
	 * @param shippableItems shippable items
	 * @param destination    destination address
	 * @param storeCode      store code
	 * @return priced shipping calculation provider
	 */
	protected ShippingCalculationPlugin getPricedShippingCalculationPlugin(final Collection<? extends PricedShippableItem> shippableItems,
																		   final ShippingAddress destination,
																		   final String storeCode) {
		final ShippingCalculationPlugin result = getPricedShippingCalculationPluginSelector()
				.getPricedShippingCalculationPlugin(shippableItems, destination, storeCode);

		if (result == null) {
			LOG.error(format("No priced shipping calculation provider found matching: Address: %s; Store: %s; Shippable Items: %s",
					destination, storeCode, shippableItems));
		}

		return result;
	}

	protected UnpricedShippingCalculationPluginSelector getUnpricedShippingCalculationPluginSelector() {
		return this.unpricedShippingCalculationPluginSelector;
	}

	public void setUnpricedShippingCalculationPluginSelector(
			final UnpricedShippingCalculationPluginSelector unpricedShippingCalculationPluginSelector) {
		this.unpricedShippingCalculationPluginSelector = unpricedShippingCalculationPluginSelector;
	}

	protected PricedShippingCalculationPluginSelector getPricedShippingCalculationPluginSelector() {
		return this.pricedShippingCalculationPluginSelector;
	}

	public void setPricedShippingCalculationPluginSelector(
			final PricedShippingCalculationPluginSelector pricedShippingCalculationPluginSelector) {
		this.pricedShippingCalculationPluginSelector = pricedShippingCalculationPluginSelector;
	}

	protected Supplier<ShippingCalculationResultBuilder> getShippingCalculationResultBuilderSupplier() {
		return this.shippingCalculationResultBuilderSupplier;
	}

	public void setShippingCalculationResultBuilderSupplier(
			final Supplier<ShippingCalculationResultBuilder> shippingCalculationResultBuilderSupplier) {
		this.shippingCalculationResultBuilderSupplier = shippingCalculationResultBuilderSupplier;
	}
}
