/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.relos.rs.subject.attribute.epcommerce;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.net.HttpHeaders;

import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.domain.store.Store;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.identity.attribute.LocaleSubjectAttribute;
import com.elasticpath.rest.identity.attribute.SubjectAttribute;
import com.elasticpath.rest.relos.rs.subject.attribute.lookup.SubjectAttributeLookupStrategy;
import com.elasticpath.rest.relos.rs.subject.util.SubjectHeadersUtil;
import com.elasticpath.rest.resource.integration.epcommerce.repository.store.StoreRepository;
import com.elasticpath.rest.util.collection.CollectionUtil;

/**
 * Lookup for locale subject attribute.
 */
@Component(property = Constants.SERVICE_RANKING + ":Integer=100")
public class LocaleSubjectAttributeLookupStrategy implements SubjectAttributeLookupStrategy {

	private final Map<Locale, Collection<Locale>> localeLookups = new ConcurrentHashMap<>();


	@Reference
	private StoreRepository storeRepository;


	@Override
	public Iterable<SubjectAttribute> from(final HttpServletRequest request, final Map<String, String> existingAttributeHeaders) {
		Optional<Store> storeOptional = findStore(request);
		if (!storeOptional.isPresent()) {
			return Collections.emptyList();
		}

		Store store = storeOptional.get();
		Locale locale;

		String localeAttribute = existingAttributeHeaders.get(LocaleSubjectAttribute.TYPE);

		if (StringUtils.isNotEmpty(localeAttribute)) {
			locale = findBestSupportedLocale(Stream.of(Locale.forLanguageTag(localeAttribute)), store);
		// request.getLocales() always returns a value even if no header is present.
		} else if (StringUtils.isNotEmpty(request.getHeader(HttpHeaders.ACCEPT_LANGUAGE))) {
			Collection<Locale> requestLocales = Collections.list(request.getLocales());
			locale = findBestSupportedLocale(requestLocales.stream(), store);
		} else {
			locale = store.getDefaultLocale();
		}

		return Collections.singleton(new LocaleSubjectAttribute(LocaleSubjectAttribute.TYPE, locale));
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

	/**
	 * Test locales against Store support to find the best Locale to use. Defaults to store default
	 * if nothing lines up.
	 *
	 * @param locales the locales from the request
	 * @param store the store
	 * @return an appropriate locale
	 */
	@VisibleForTesting
	Locale findBestSupportedLocale(final Stream<Locale> locales, final Store store) {
		Collection<Locale> supportedLocales = store.getSupportedLocales();
		return locales
			.flatMap(locale -> localeLookups
				.computeIfAbsent(locale, LocaleUtils::localeLookupList)
				.stream())
			.filter(supportedLocales::contains)
			.findFirst()
			.orElse(store.getDefaultLocale());
	}
}
