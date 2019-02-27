/*
 * Copyright (c) Elastic Path Software Inc., 2005
 */
package com.elasticpath.commons.util.impl;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.constants.GlobalConstants;
import com.elasticpath.commons.constants.SeoConstants;
import com.elasticpath.commons.util.PasswordGenerator;
import com.elasticpath.commons.util.Utility;
import com.elasticpath.domain.store.CreditCardType;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.store.StoreService;
import com.elasticpath.settings.provider.SettingValueProvider;

/**
 * The default implementation of <code>Utility</code>.
 */
public class UtilityImpl implements Utility {

	private static final long serialVersionUID = 5000000001L;

	private static final int MONTHS_IN_YEAR = 12;

	private static final int NUM_EXPIRY_YEARS = 18;

	private static final Map<String, String> MONTH_MAP;

	private static final Map<String, String> YEAR_MAP;

	private static final Map<Character, Character> WESTERN_TO_ENGLISH_MAP = new HashMap<>();

	private BeanFactory beanFactory;
	private SettingValueProvider<String> defaultDateFormatPatternProvider;

	static {
		MONTH_MAP = new LinkedHashMap<>();
		for (int monthNumber = 1; monthNumber <= MONTHS_IN_YEAR; monthNumber++) {
			String monthString = String.format("%02d", monthNumber);
			MONTH_MAP.put(monthString, monthString);
		}

		YEAR_MAP = new LinkedHashMap<>();
		final GregorianCalendar calendar = new GregorianCalendar();
		final int currentYear = calendar.get(Calendar.YEAR);
		for (int yearNumber = 0; yearNumber <= NUM_EXPIRY_YEARS; yearNumber++) {
			final String yearString = String.valueOf(currentYear + yearNumber);
			YEAR_MAP.put(yearString, yearString);
		}

		for (char[] chars : SeoConstants.WESTERN_TO_ENGLISH_ARRAY) {
			WESTERN_TO_ENGLISH_MAP.put(chars[0], chars[1]);
		}
	}

	@Override
	@Deprecated
	public String escapeName2UrlFriendly(final String name) {
		return escapeName2UrlFriendly(name, Locale.getDefault());
	}

	@Override
	public String escapeName2UrlFriendly(final String name, final Locale locale) {
		// Currently, we only allow alphabetic and numbers in a seo url.
		// In some cases, you might need to use URLEncoder to encode url-nonfriendly characters.

		// convert the western European characters to English
		StringBuilder builder = new StringBuilder(name.trim());
		for (int x = 0; x < builder.length(); x++) {
			char character = builder.charAt(x);
			Character replaceChar = WESTERN_TO_ENGLISH_MAP.get(character);
			if (replaceChar == null) {
				continue;
			}
			builder.setCharAt(x, replaceChar);
		}

		return builder.toString().toLowerCase(locale).replaceAll("[^\\p{Alnum}]+", "-");
	}

	/**
	 * Returns the system-default date formatting string and locale.
	 * @return the system-default date formatting string and locale
	 */
	@Override
	public LocalizedDateFormat getDefaultLocalizedDateFormat() {
		return new LocalizedDateFormat(getDefaultDateFormatPattern(), getDefaultDateFormatLocale());
	}

	/**
	 * Default pattern to be used, when one is not send.
	 * @return the default date format
	 * @throws com.elasticpath.base.exception.EpServiceException if the locale date format setting cannot be retrieved for any reason
	 */
	protected String getDefaultDateFormatPattern() {
		return getDefaultDateFormatPatternProvider().get();
	}

	/**
	 * Locale to be used by methods that are not sending one.
	 *
	 * @return {@link Locale} to be used in the {@link java.text.DateFormat}
	 */
	protected Locale getDefaultDateFormatLocale() {
		return Locale.getDefault();
	}

	@Override
	public boolean checkShortTextMaxLength(final String value) {
		if (value == null) {
			return true;
		}
		return value.length() <= GlobalConstants.SHORT_TEXT_MAX_LENGTH;
	}

	@Override
	public boolean checkLongTextMaxLength(final String value) {
		if (value == null) {
			return true;
		}
		return value.length() <= GlobalConstants.LONG_TEXT_MAX_LENGTH;
	}

	@Override
	public boolean isValidGuidStr(final String string) {
		if (string == null) {
			return false;
		}
		return string.matches("^[\\p{Alnum}\\-_]+$");
	}

	@Override
	public boolean isValidZipPostalCode(final String zipPostalCode) {
		if (zipPostalCode == null) {
			return false;
		}
		return zipPostalCode.matches("^\\p{Alnum}[\\p{Alnum}\\s]+\\p{Alnum}$");
	}

	@Override
	public Map<String, String> getMonthMap() {
		return MONTH_MAP;
	}

	@Override
	public Map<String, String> getYearMap() {
		return YEAR_MAP;
	}

	@Override
	public Map<String, String> getStoreCreditCardTypesMap(final String storeCode) {
		StoreService storeService = getBeanFactory().getBean(ContextIdNames.STORE_SERVICE);
		Store store = storeService.findStoreWithCode(storeCode);
		Set<CreditCardType> cardTypes = store.getCreditCardTypes();
		Map<String, String> supportedCardMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		for (CreditCardType card : cardTypes) {
			supportedCardMap.put(card.getCreditCardType(), card.getCreditCardType());
		}
		return supportedCardMap;
	}

	@Override
	@Deprecated
	public Map<String, String> getAllCreditCardTypesMap() {

		Map<String, String> allCreditCardsMap = new LinkedHashMap<>();

		StoreService storeService = getBeanFactory().getBean(ContextIdNames.STORE_SERVICE);
		List<Store> stores = storeService.findAllCompleteStores();

		for (Store store : stores) {
			allCreditCardsMap.putAll(getStoreCreditCardTypesMap(store.getCode()));
		}
		return allCreditCardsMap;
	}

	@Override
	public String getRandomStringWithLength(final int length) {
		final PasswordGenerator passwordGenerator = getBeanFactory().getBean("passwordGenerator");
		return passwordGenerator.getPassword().substring(0, length);
	}

	protected BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	protected SettingValueProvider<String> getDefaultDateFormatPatternProvider() {
		return defaultDateFormatPatternProvider;
	}

	public void setDefaultDateFormatPatternProvider(final SettingValueProvider<String> defaultDateFormatPatternProvider) {
		this.defaultDateFormatPatternProvider = defaultDateFormatPatternProvider;
	}

}
