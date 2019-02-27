/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.importexport.common.adapters.products.data;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.common.dto.DisplayValue;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeMultiValueType;
import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.attribute.AttributeValueFactory;
import com.elasticpath.domain.attribute.AttributeValueGroup;
import com.elasticpath.importexport.common.caching.CachingService;
import com.elasticpath.importexport.common.dto.products.AttributeGroupDTO;
import com.elasticpath.importexport.common.dto.products.AttributeValuesDTO;
import com.elasticpath.importexport.common.util.LocalizedAttributeKeyLocaleTranslator;
import com.elasticpath.validation.service.ValidatorUtils;

/**
 * Verify that AttributeGroupAdapterTest populates catalog domain object from DTO properly and vice versa.
 * Nested adapters should be tested separately.
 */
@RunWith(MockitoJUnitRunner.class)
public class AttributeGroupAdapterTest {

	private static final String KEY = "key";

	private static final String KEY_EN = KEY + "_en";

	private static final DisplayValue DISPLAY_VALUE = new DisplayValue("en", "value1");

	private static final Locale LOCALE = new Locale("en");

	private final AttributeGroupAdapter adapterUnderTest = new AttributeGroupAdapter();

	@Mock
	private CachingService cachingService;
	@Mock
	private Attribute attribute;
	@Mock
	private ValidatorUtils validatorUtils;
	@Mock
	private LocalizedAttributeKeyLocaleTranslator localizedAttributeKeyLocaleTranslator;
	@Mock
	private AttributeValueGroup attributeValueGroup;
	@Mock
	private AttributeValue attributeValue;
	/**
	 * Setup required for each test.
	 *
	 * @throws Exception the exception
	 */
	@Before
	public void setUp() throws Exception {

		when(cachingService.findAttribiteByKey(KEY)).thenReturn(attribute);

		//can't be a mock because must populate AttributeValuesDTO in AttributeGroupAdapter.populateDTO
		AttributeValuesAdapter attributeValuesAdapter = new AttributeValuesAdapter();
		attributeValuesAdapter.setCachingService(cachingService);
		attributeValuesAdapter.setValidatorUtils(validatorUtils);
		attributeValuesAdapter.setLocalizedAttributeKeyLocaleTranslator(localizedAttributeKeyLocaleTranslator);

		adapterUnderTest.setAttributeValuesAdapter(attributeValuesAdapter);
	}

	/**
	 * Check that all required fields for Dto object are being set during domain population.
	 */
	@Test
	public void shouldSetAllRequiredFieldsWhilePopulatingDTOFromDomain() {
		Map<String, AttributeValue> attributeValueMap = new HashMap<>();
		attributeValueMap.put(KEY, attributeValue);

		//mock method calls
		mockCommonMethodCalls(attributeValueMap, true);
		when(attribute.getKey()).thenReturn(KEY);
		when(attribute.getMultiValueType()).thenReturn(AttributeMultiValueType.LEGACY);
		when(attribute.isLocaleDependant()).thenReturn(true);
		when(attributeValue.getAttribute()).thenReturn(attribute);
		when(attributeValue.getValue()).thenReturn("Attribute value");
		when(localizedAttributeKeyLocaleTranslator.getLanguageTagFromLocalizedKeyName(KEY_EN)).thenReturn(LOCALE.toLanguageTag());

		//real method invocation
		AttributeGroupDTO expectedDto = new AttributeGroupDTO();
		adapterUnderTest.populateDTO(attributeValueGroup, expectedDto);

		AttributeValuesDTO expectedAttributeValuesDTO = expectedDto.getAttributeValues().get(0);

		assertEquals("Keys must be the same", KEY, expectedAttributeValuesDTO.getKey());
		assertEquals("Languages must be the same", DISPLAY_VALUE.getLanguage(), expectedAttributeValuesDTO.getValues().get(0).getLanguage());
		assertEquals("Values must be the same", DISPLAY_VALUE.getValue(), expectedAttributeValuesDTO.getValues().get(0).getValue());
		assertThat("AttributeValuesDTO must have 1 DisplayValue", expectedAttributeValuesDTO.getValues(), hasSize(1));
		assertThat("AttributeGroupDTO must have 1 AttributeValuesDTO", expectedDto.getAttributeValues(), hasSize(1));

		verify(attributeValueGroup).getAttributeValueMap();
		verify(attribute, atLeast(1)).getKey();
		verify(attribute).getMultiValueType();
		verify(attribute).isMultiValueEnabled();
		verify(attribute).isLocaleDependant();
		verify(attributeValue).getStringValue();
		verify(attributeValue).getLocalizedAttributeKey();
		verify(localizedAttributeKeyLocaleTranslator).getLanguageTagFromLocalizedKeyName(KEY_EN);
	}

	/**
	 * Check that all required fields for domain object are being set during domain population.
	 */
	@Test
	public void testPopulateDomain() {
		// prepare DTO :
		AttributeValuesDTO attributeValuesDTO = Mockito.mock(AttributeValuesDTO.class);
		when(attributeValuesDTO.getKey()).thenReturn(KEY);
		when(attributeValuesDTO.getValues()).thenReturn(Arrays.asList(DISPLAY_VALUE));

		AttributeGroupDTO dto = Mockito.mock(AttributeGroupDTO.class);
		when(dto.getAttributeValues()).thenReturn(Arrays.asList(attributeValuesDTO));

		// prepare Domain
		final Map<String, AttributeValue> attributeValueMap = new HashMap<>();
		final AttributeValueFactory attributeValueFactory = Mockito.mock(AttributeValueFactory.class);

		//mock method calls
		mockCommonMethodCalls(attributeValueMap, false);

		when(attributeValueGroup.getAttributeValue(KEY, LOCALE)).thenReturn(null);
		when(attributeValueGroup.getAttributeValueFactory()).thenReturn(attributeValueFactory);
		when(attributeValueFactory.createAttributeValue(attribute, KEY_EN)).thenReturn(attributeValue);
		when(attribute.getCatalog()).thenReturn(null);
		when(localizedAttributeKeyLocaleTranslator.convertLocaleStringToLocale(LOCALE.toString())).thenReturn(LOCALE);

		//real method invocation
		adapterUnderTest.populateDomain(dto, attributeValueGroup);

		assertThat("Attribute value map must have entry: " + KEY_EN + ":" + attributeValue, attributeValueMap, hasEntry(KEY_EN, attributeValue));
		assertEquals(1, attributeValueMap.size());

		verify(attributeValueGroup).getAttributeValueMap();
		verify(attributeValueGroup).getAttributeValue(KEY, LOCALE);
		verify(attributeValueFactory).createAttributeValue(attribute, KEY_EN);
		verify(attribute).isMultiValueEnabled();
		verify(attribute).getCatalog();
		verify(attributeValue).setStringValue(DISPLAY_VALUE.getValue());
		verify(attributeValue).getLocalizedAttributeKey();
		verify(localizedAttributeKeyLocaleTranslator).convertLocaleStringToLocale(LOCALE.toString());
		verify(cachingService).findAttribiteByKey(KEY);
	}

	private void mockCommonMethodCalls(final Map<String, AttributeValue> attributeValueMap, final boolean isMultiValueEnabled) {
		when(attributeValueGroup.getAttributeValueMap()).thenReturn(attributeValueMap);
		when(attribute.isMultiValueEnabled()).thenReturn(isMultiValueEnabled);
		when(attributeValue.getLocalizedAttributeKey()).thenReturn(KEY_EN);
		when(attributeValue.getStringValue()).thenReturn(DISPLAY_VALUE.getValue());
	}

}