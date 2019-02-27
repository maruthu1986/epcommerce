/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.importexport.common.adapters.settings;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.importexport.common.dto.settings.DefaultValueDTO;
import com.elasticpath.importexport.common.dto.settings.MetadataDTO;
import com.elasticpath.importexport.common.dto.settings.SettingDTO;
import com.elasticpath.settings.domain.SettingDefinition;
import com.elasticpath.settings.domain.SettingMetadata;
import com.elasticpath.settings.domain.impl.SettingDefinitionImpl;
import com.elasticpath.settings.domain.impl.SettingMetadataImpl;

/**
 * Tests population of DTO and domain objects by <code>SettingDefinitionAdapter</code>.
 */
@RunWith(MockitoJUnitRunner.class)
public class SettingDefinitionAdapterTest {

	private static final String DEFAULT_VALUE_VALUE = "/assets";

	private static final String DEFAULT_VALUE_TYPE = "String";

	private static final int MAXIMUM_OVERRIDES = 1;

	private static final String DESCRIPTION = "The path to the location of global assets.";

	private static final String NAMESPACE = "COMMERCE/SYSTEM/ASSETS/assetLocation";

	private static final String VALUE = "true";

	private static final String ADDITIONAL_METADATA = "additionalMetadata";

	private static final String AVAILABLE_TO_MARKETING = "availableToMarketing";

	private final SettingDefinitionAdapter settingDefinitionAdapter = new SettingDefinitionAdapter();

	@Mock
	private SettingDefinition settingDefinition;
	@Mock
	private SettingMetadata settingMetadata;
	@Mock
	private BeanFactory mockBeanFactory;

	/**
	 * Sets Up Test Case.
	 */
	@Before
	public void setUp() {
		settingDefinitionAdapter.setBeanFactory(mockBeanFactory);

		when(settingDefinition.getPath()).thenReturn(NAMESPACE);
		when(settingDefinition.getDescription()).thenReturn(DESCRIPTION);
		when(settingDefinition.getMaxOverrideValues()).thenReturn(MAXIMUM_OVERRIDES);
		when(settingDefinition.getValueType()).thenReturn(DEFAULT_VALUE_TYPE);

		when(settingMetadata.getKey()).thenReturn(AVAILABLE_TO_MARKETING);
		when(settingMetadata.getValue()).thenReturn(VALUE);
	}

	/**
	 * Tests creation and population of <code>DefaultValueDTO</code> object as a part of <code>SettingDTO</code> state.
	 */
	@Test
	public void testCreateDefaultValueDTO() {
		when(settingDefinition.getDefaultValue()).thenReturn("storeassets");

		DefaultValueDTO defaultValueDTO = settingDefinitionAdapter.createDefaultValueDTO(settingDefinition);

		assertThat(defaultValueDTO.getValue()).isEqualTo("storeassets");
		assertThat(defaultValueDTO.getType()).isEqualTo(DEFAULT_VALUE_TYPE);

		verify(settingDefinition, times(1)).getValueType();
		verify(settingDefinition, times(1)).getDefaultValue();
	}

	/**
	 * Tests creation and population of <code>MetadataDTO</code> object as a part of <code>SettingDTO</code> state.
	 */
	@Test
	public void testCreateMetadataDTO() {
		MetadataDTO metadataDto = settingDefinitionAdapter.createMetadataDTO(settingMetadata);

		assertThat(metadataDto.getKey()).isEqualTo(AVAILABLE_TO_MARKETING);
		assertThat(metadataDto.getValue()).isEqualTo(VALUE);

		verify(settingMetadata, times(1)).getKey();
		verify(settingMetadata, times(1)).getValue();
	}

	/**
	 * Checks that list of <code>MetadataDTO</code> objects is objects is created properly.
	 */
	@Test
	public void testCreateMetadataDTOList() {
		final SettingMetadata settingMetadata1 = new SettingMetadataImpl();
		settingMetadata1.setKey("KEY1");
		final SettingMetadata settingMetadata2 = new SettingMetadataImpl();
		settingMetadata2.setKey("KEY2");
		final Map<String, SettingMetadata> metadata = new HashMap<>();
		metadata.put(AVAILABLE_TO_MARKETING, settingMetadata1);
		metadata.put(ADDITIONAL_METADATA, settingMetadata2);

		when(settingDefinition.getMetadata()).thenReturn(metadata);

		final List<MetadataDTO> metadataDtoList = settingDefinitionAdapter.createMetadataDTOList(settingDefinition);

		assertThat(metadataDtoList).size().isEqualTo(2);

		verify(settingDefinition, times(1)).getMetadata();
	}

	/**
	 * Tests population of <code>SettingDTO</code> in the assumption that all other methods are implemented correctly.
	 */
	@Test
	public void testPopulateDTO() {
		final SettingDefinitionAdapter settingDefinitionAdapter = new SettingDefinitionAdapter() {
			@Override
			List<MetadataDTO> createMetadataDTOList(final SettingDefinition source) {
				return null;
			}

			@Override
			DefaultValueDTO createDefaultValueDTO(final SettingDefinition source) {
				return null;
			}
		};

		final SettingDTO settingDto = new SettingDTO();

		settingDefinitionAdapter.populateDTO(settingDefinition, settingDto);

		assertThat(settingDto.getNameSpace()).isEqualTo(NAMESPACE);
		assertThat(settingDto.getDescription()).isEqualTo(DESCRIPTION);
		assertThat(settingDto.getMaximumOverrides()).isEqualTo(MAXIMUM_OVERRIDES);

		verify(settingDefinition, times(1)).getPath();
		verify(settingDefinition, times(1)).getDescription();
		verify(settingDefinition, times(1)).getMaxOverrideValues();
	}

	/**
	 * Tests populateDomain.
	 */
	@Test
	public void testPopulateDomain() {
		// Expectation
		final SettingDefinitionAdapter settingDefinitionAdapter = new SettingDefinitionAdapter() {
			@Override
			Map<String, SettingMetadata> createSettingMetadataMap(final List<MetadataDTO> metadataValues) {
				return Collections.emptyMap();
			}
		};

		// Test Data
		SettingDTO settingDTO = new SettingDTO();
		settingDTO.setNameSpace(NAMESPACE);
		settingDTO.setDescription(DESCRIPTION);
		settingDTO.setMaximumOverrides(MAXIMUM_OVERRIDES);
		settingDTO.setDefaultValue(createDefaultValue(DEFAULT_VALUE_TYPE, DEFAULT_VALUE_VALUE));
		settingDTO.setMetadataValues(Collections.<MetadataDTO>emptyList());

		// Result
		settingDefinitionAdapter.populateDomain(settingDTO, settingDefinition);

		verify(settingDefinition, times(1)).setPath(NAMESPACE);
		verify(settingDefinition, times(1)).setDescription(DESCRIPTION);
		verify(settingDefinition, times(1)).setMaxOverrideValues(MAXIMUM_OVERRIDES);
		verify(settingDefinition, times(1)).setValueType(DEFAULT_VALUE_TYPE);
		verify(settingDefinition, times(1)).setDefaultValue(DEFAULT_VALUE_VALUE);
		verify(settingDefinition, atLeast(1)).getMetadata();
		verify(settingDefinition, times(1)).setMetadata(Collections.emptyMap());
	}

	private DefaultValueDTO createDefaultValue(final String type, final String value) {
		DefaultValueDTO defaultValueDTO = new DefaultValueDTO();
		defaultValueDTO.setType(type);
		defaultValueDTO.setValue(value);

		return defaultValueDTO;
	}

	/**
	 * Test createSettingMetadataMap.
	 */
	@Test
	public void testCreateSettingMetadataMap() {
		final SettingDefinitionAdapter settingDefinitionAdapter = new SettingDefinitionAdapter() {
			@Override
			SettingMetadata createSettingMetaData(final MetadataDTO metadataDTO) {
				return settingMetadata;
			}
		};

		// Test Data
		final List<MetadataDTO> metadataList = new ArrayList<>();
		metadataList.add(createMetaDataDTO(AVAILABLE_TO_MARKETING, VALUE));

		// Result
		final Map<String, SettingMetadata> map = settingDefinitionAdapter.createSettingMetadataMap(metadataList);

		// Check Result
		assertThat(map).size().isEqualTo(1);
		assertThat(map.get(AVAILABLE_TO_MARKETING)).isEqualTo(settingMetadata);
	}

	/**
	 * Tests createSettingMetaData.
	 */
	@Test
	public void testCreateSettingMetaData() {
		MetadataDTO metadataDTO = createMetaDataDTO(AVAILABLE_TO_MARKETING, VALUE);

		when(mockBeanFactory.getBean(ContextIdNames.SETTING_METADATA)).thenReturn(settingMetadata);
		assertThat(settingDefinitionAdapter.createSettingMetaData(metadataDTO)).isEqualTo(settingMetadata);
		verify(settingMetadata, times(1)).setKey(AVAILABLE_TO_MARKETING);
		verify(settingMetadata, times(1)).setValue(VALUE);
		verify(mockBeanFactory, times(1)).getBean(ContextIdNames.SETTING_METADATA);
	}

	private MetadataDTO createMetaDataDTO(final String key, final String value) {
		MetadataDTO metadataDTO = new MetadataDTO();

		metadataDTO.setKey(key);
		metadataDTO.setValue(value);

		return metadataDTO;
	}

	/**
	 * Tests createDtoObject.
	 */
	@Test
	public void testCreateDtoObject() {
		assertThat(settingDefinitionAdapter.createDtoObject()).isInstanceOf(SettingDTO.class);
	}

	/**
	 * Tests createDomainObject.
	 */
	@Test
	public void testCreateDomainObject() {
		when(mockBeanFactory.getBean(ContextIdNames.SETTING_DEFINITION)).thenReturn(new SettingDefinitionImpl());

		assertThat(settingDefinitionAdapter.createDomainObject()).isInstanceOf(SettingDefinitionImpl.class);

		verify(mockBeanFactory, times(1)).getBean(ContextIdNames.SETTING_DEFINITION);
	}
}
