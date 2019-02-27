/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.importexport.importer.importers.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.common.dto.datapolicy.DataPointDTO;
import com.elasticpath.common.dto.datapolicy.DataPolicyDTO;
import com.elasticpath.domain.datapolicy.DataPolicy;
import com.elasticpath.domain.datapolicy.DataPolicyState;
import com.elasticpath.domain.datapolicy.RetentionType;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.exception.runtime.ImportRuntimeException;
import com.elasticpath.importexport.importer.configuration.ImportConfiguration;
import com.elasticpath.importexport.importer.context.ImportContext;
import com.elasticpath.importexport.importer.importers.SavingStrategy;
import com.elasticpath.importexport.importer.types.ImportStrategyType;
import com.elasticpath.service.datapolicy.DataPolicyService;

/**
 * Test for <code>DataPolicyImporterImpl</code>.
 */
@RunWith(MockitoJUnitRunner.class)
public class DataPolicyImporterImplTest {

	private static final String GUID1 = "GUID1";
	private static final String GUID2 = "GUID2";
	private static final String GUID3 = "GUID3";
	private static final int RETENTION_PERIOD_IN_DAYS = 100;

	private DataPolicyImporterImpl dataPolicyImporterImpl;

	@Mock
	private DomainAdapter<DataPolicy, DataPolicyDTO> domainAdapter;

	@Mock
	private DataPolicyService mockDataPolicyService;

	private SavingStrategy<DataPolicy, DataPolicyDTO> mockSavingStrategy;

	/**
	 * SetUps the test.
	 */
	@Before
	public void setUp() {
		dataPolicyImporterImpl = new DataPolicyImporterImpl();
		dataPolicyImporterImpl.setStatusHolder(new ImportStatusHolder());
		dataPolicyImporterImpl.setDataPolicyService(mockDataPolicyService);
		dataPolicyImporterImpl.setDataPolicyAdapter(domainAdapter);

		mockSavingStrategy = AbstractSavingStrategy.createStrategy(ImportStrategyType.INSERT, new SavingManager<DataPolicy>() {
			@Override
			public void save(final DataPolicy persistable) {
				// do nothing
			}

			@Override
			public DataPolicy update(final DataPolicy persistable) {
				return null;
			}
		});
	}

	/**
	 * Check an import with non-initialized importer.
	 */
	public void testExecuteNonInitializedImport() {
		assertThatThrownBy(() -> dataPolicyImporterImpl.executeImport(createDataPolicyDTO()))
				.isInstanceOf(ImportRuntimeException.class)
				.hasMessageStartingWith("IE-30501");
	}

	/**
	 * Check an import of data policies.
	 */
	@Test
	public void testExecuteImport() {
		ImportConfiguration importConfiguration = new ImportConfiguration();
		importConfiguration.setImporterConfigurationMap(new HashMap<>());

		when(mockDataPolicyService.findByGuid(GUID1)).thenReturn(mock(DataPolicy.class));

		dataPolicyImporterImpl.initialize(new ImportContext(importConfiguration), mockSavingStrategy);
		dataPolicyImporterImpl.executeImport(createDataPolicyDTO());

		assertThat(DataPolicyDTO.ROOT_ELEMENT)
				.isEqualTo(dataPolicyImporterImpl.getImportedObjectName());
		assertThat(dataPolicyImporterImpl.getSavingStrategy())
				.isNotNull();
	}

	/**
	 * Test method for {@link DataPolicyImporterImpl#findPersistentObject(DataPolicyDTO)}.
	 */
	@Test
	public void testFindPersistentObjectDataPolicyDTO() {
		final DataPolicy dataPolicy = mock(DataPolicy.class);

		when(mockDataPolicyService.findByGuid(GUID1)).thenReturn(dataPolicy);

		assertThat(dataPolicy)
				.isEqualTo(dataPolicyImporterImpl.findPersistentObject(createDataPolicyDTO()));
	}


	/**
	 * Test method for {@link com.elasticpath.importexport.importer.importers.impl.DataPolicyImporterImpl#getDomainAdapter()}.
	 */
	@Test
	public void testGetDomainAdapter() {
		assertThat(domainAdapter)
				.isEqualTo(dataPolicyImporterImpl.getDomainAdapter());
	}

	/**
	 * Test method for
	 * {@link com.elasticpath.importexport.importer.importers.impl.DataPolicyImporterImpl
	 * #getDtoGuid{@link com.elasticpath.importexport.common.dto.datapolicy.DataPoliciesDTO})}.
	 */
	@Test
	public void testGetDtoGuidDataPolicyDTO() {
		final DataPolicyDTO dto = createDataPolicyDTO();

		assertThat(GUID1)
				.isEqualTo(dataPolicyImporterImpl.getDtoGuid(dto));
	}

	/**
	 * Test method for {@link com.elasticpath.importexport.importer.importers.impl.DataPolicyImporterImpl#getImportedObjectName()}.
	 */
	@Test
	public void testGetImportedObjectName() {
		assertThat(DataPolicyDTO.ROOT_ELEMENT)
				.isEqualTo(dataPolicyImporterImpl.getImportedObjectName());
	}

	/**
	 * The import classes should at least contain the DTO class we are operating on.
	 */
	@Test
	public void testDtoClass() {
		assertThat(DataPolicyDTO.class)
				.isEqualTo(dataPolicyImporterImpl.getDtoClass());
	}

	/**
	 * The auxiliary JAXB class list must not be null (can be empty).
	 */
	@Test
	public void testAuxiliaryJaxbClasses() {
		assertThat(dataPolicyImporterImpl.getAuxiliaryJaxbClasses())
				.isNotNull();
	}

	private DataPolicyDTO createDataPolicyDTO() {
		final DataPolicyDTO dataPolicyDTO = new DataPolicyDTO();
		dataPolicyDTO.setGuid(GUID1);
		dataPolicyDTO.setPolicyName(GUID1);
		dataPolicyDTO.setDescription(String.format("Description for %s", GUID1));
		dataPolicyDTO.setReferenceKey(String.format("Reference Key for %s", GUID1));
		dataPolicyDTO.setRetentionPeriodInDays(RETENTION_PERIOD_IN_DAYS);
		dataPolicyDTO.setState(DataPolicyState.DRAFT.getName());
		dataPolicyDTO.setStartDate(new Date());
		dataPolicyDTO.setEndDate(new Date());
		dataPolicyDTO.setRetentionType(RetentionType.FROM_CREATION_DATE.getName());
		dataPolicyDTO.setSegments(new HashSet<>(Arrays.asList("US", "EU", "CA")));
		dataPolicyDTO.setActivities(new HashSet<>());

		final List<DataPointDTO> dataPointDTOs = new ArrayList<>();
		for (String dataPointString : Arrays.asList(GUID2, GUID3)) {
			DataPointDTO dataPointDTO = new DataPointDTO();
			dataPointDTO.setDescriptionKey(dataPointString);
			dataPointDTO.setDataKey("key_" + dataPointString);
			dataPointDTO.setDataLocation("location_" + dataPointString);
			dataPointDTO.setGuid("guid_" + dataPointString);
			dataPointDTO.setRemovable(true);

			dataPointDTOs.add(dataPointDTO);
		}
		dataPolicyDTO.getDataPoints().addAll(dataPointDTOs);

		return dataPolicyDTO;
	}
}
