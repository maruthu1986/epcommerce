/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.relos.rs.subject.attribute.epcommerce;

import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import com.google.common.annotations.VisibleForTesting;

import org.apache.commons.lang3.StringUtils;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.elasticpath.domain.store.Store;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.identity.attribute.CurrencySubjectAttribute;
import com.elasticpath.rest.identity.attribute.SubjectAttribute;
import com.elasticpath.rest.relos.rs.subject.attribute.lookup.SubjectAttributeLookupStrategy;
import com.elasticpath.rest.relos.rs.subject.util.SubjectHeadersUtil;
import com.elasticpath.rest.resource.integration.epcommerce.repository.store.StoreRepository;
import com.elasticpath.rest.util.collection.CollectionUtil;

/**
 * Lookup for currency subject attribute.
 */
@Component(property = Constants.SERVICE_RANKING + ":Integer=100")
public class CurrencySubjectAttributeLookupStrategy implements SubjectAttributeLookupStrategy {

	private static final Logger LOG = LoggerFactory.getLogger(CurrencySubjectAttributeLookupStrategy.class);


	@Reference
	private StoreRepository storeRepository;


	@Override
	public Iterable<SubjectAttribute> from(final HttpServletRequest request, final Map<String, String> existingAttributeHeaders) {
		Optional<Store> storeOptional = findStore(request);
		if (!storeOptional.isPresent()) {
			return Collections.emptyList();
		}

		Store store = storeOptional.get();
		Currency currency;

		String currencyString = existingAttributeHeaders.get(CurrencySubjectAttribute.TYPE);
		if (StringUtils.isNotEmpty(currencyString)) {
			currency = findBestSupportedCurrency(createCurrency(currencyString), store);
		} else {
			currency = store.getDefaultCurrency();
		}

		return Collections.singleton(new CurrencySubjectAttribute(CurrencySubjectAttribute.TYPE, currency));
	}

	private Optional<Store> findStore(final HttpServletRequest request) {
		String scope = CollectionUtil.first(SubjectHeadersUtil.getUserScopesFromRequest(request));
		if (scope != null) {
			ExecutionResult<Store> storeResult = storeRepository.findStore(scope);
			if (storeResult.isSuccessful()) {
				return Optional.of(storeResult.getData());
			}
		}
		return Optional.empty();
	}

	private Currency createCurrency(final String currencyString) {
		try {
			return Currency.getInstance(currencyString);
		} catch (Exception e) {
			LOG.debug("unknown currency code: {}", currencyString);
		}
		return null;
	}

	/**
	 * Find the best currency for this session given the request headers and Store supported currencies.
	 *
	 * @param currency the currency from the request
	 * @param store the store
	 * @return the appropriate currency
	 */
	@VisibleForTesting
	Currency findBestSupportedCurrency(final Currency currency, final Store store) {

		if (currency != null) {
			Collection<Currency> supportedCurrencies = store.getSupportedCurrencies();

			if (supportedCurrencies.contains(currency)) {
				return currency;
			}
		}
		return store.getDefaultCurrency();
	}
}
