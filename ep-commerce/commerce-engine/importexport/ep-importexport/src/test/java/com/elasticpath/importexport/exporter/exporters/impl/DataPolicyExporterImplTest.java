/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.importexport.exporter.exporters.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.common.dto.datapolicy.DataPolicyDTO;
import com.elasticpath.domain.datapolicy.DataPolicy;
import com.elasticpath.domain.datapolicy.impl.DataPolicyImpl;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.exporter.configuration.ExportConfiguration;
import com.elasticpath.importexport.exporter.configuration.ExporterConfiguration;
import com.elasticpath.importexport.exporter.configuration.search.SearchConfiguration;
import com.elasticpath.importexport.exporter.context.DependencyRegistry;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.importexport.exporter.search.ImportExportSearcher;
import com.elasticpath.service.datapolicy.DataPolicyService;

/**
 * Tests <code>DataPolicyExporterImpl</code>.
 */
@RunWith(MockitoJUnitRunner.class)
public class DataPolicyExporterImplTest {

	@InjectMocks
	private DataPolicyExporterImpl dataPolicyExporterImpl;

	@Mock
	private ImportExportSearcher importExportSearcher;

	@Mock
	private DataPolicyService dataPolicyService;

	@Mock
	private DomainAdapter<DataPolicy, DataPolicyDTO> dataPolicyAdapter;

	private ExportContext exportContext;

	private static final String GUID1 = "GUID1";
	private static final String GUID2 = "GUID2";
	private static final String GUID3 = "GUID3";
	private static final int THREE = 3;

	/**
	 * All guids found during initialization should be exportable.
	 *
	 * @throws ConfigurationException in case of errors
	 */
	@Test
	public void testInitialize() throws ConfigurationException {

		final DataPolicy dataPolicy1 = new DataPolicyImpl();
		dataPolicy1.setGuid(GUID1);
		final DataPolicy dataPolicy2 = new DataPolicyImpl();
		dataPolicy2.setGuid(GUID2);
		final DataPolicy dataPolicy3 = new DataPolicyImpl();
		dataPolicy3.setGuid(GUID3);
		final List<DataPolicy> dataPolicyList = Arrays.asList(dataPolicy1, dataPolicy2, dataPolicy3);
		final List<String> foundGuids = Arrays.asList(GUID1, GUID2, GUID3);

		when(dataPolicyService.list()).thenReturn(dataPolicyList);

		exportContext = new ExportContext(new ExportConfiguration(), new SearchConfiguration());
		dataPolicyExporterImpl.initialize(exportContext);

		assertThat(CollectionUtils.isEqualCollection(foundGuids, dataPolicyExporterImpl.getListExportableIDs()))
				.isTrue();
	}

	/**
	 * Searching for a guids should return all guids regardless whether they were found during initialization.
	 *
	 * @throws ConfigurationException in case of errors
	 */
	@Test
	public void testFindByGuid() throws ConfigurationException {
		testInitialize();

		final DataPolicy dataPolicy1 = mock(DataPolicy.class, "dataPolicy-1");
		final DataPolicy dataPolicy2 = mock(DataPolicy.class, "dataPolicy-2");

		final List<String> guidList = Arrays.asList("GUID1", "GUID1-DNE");
		final List<DataPolicy> dataPolicyList = Arrays.asList(dataPolicy1, dataPolicy2);

		when(dataPolicyService.findByGuids(guidList)).thenReturn(dataPolicyList);
		List<DataPolicy> results = dataPolicyExporterImpl.findByIDs(guidList);

		assertThat(CollectionUtils.isEqualCollection(results, dataPolicyList))
				.as("Missing returned data policy")
				.isTrue();

		assertThat(results)
				.as("Extra data policy returned")
				.hasSize(2);
	}


	/**
	 * Test method for {@link DataPolicyExporterImpl#getListExportableIDs()}.
	 *
	 * @throws ConfigurationException not expected exception.
	 */
	@Test
	public void testGetListExportableIDs() throws ConfigurationException {
		ExportConfiguration exportConfiguration = new ExportConfiguration();
		SearchConfiguration searchConfiguration = new SearchConfiguration();

		when(dataPolicyService.list()).thenReturn(Collections.emptyList());

		dataPolicyExporterImpl.initialize(new ExportContext(exportConfiguration, searchConfiguration));
		dataPolicyExporterImpl.getContext()
				.setDependencyRegistry(new DependencyRegistry(Arrays.asList(new Class<?>[]{DataPolicy.class})));

		ExporterConfiguration exporterConfiguration = new ExporterConfiguration();
		exportConfiguration.setExporterConfiguration(exporterConfiguration);

		dataPolicyExporterImpl.getContext().getDependencyRegistry().addGuidDependency(DataPolicy.class, GUID1);
		dataPolicyExporterImpl.getContext().getDependencyRegistry().addGuidDependency(DataPolicy.class, GUID2);
		dataPolicyExporterImpl.getContext().getDependencyRegistry().addGuidDependency(DataPolicy.class, GUID3);

		final List<String> listExportableIDs = dataPolicyExporterImpl.getListExportableIDs();

		assertThat(CollectionUtils.isEqualCollection(listExportableIDs, Arrays.asList(GUID1, GUID2, GUID3)))
				.as("Missing returned data policy")
				.isTrue();

		assertThat(listExportableIDs)
				.as("Extra data policy returned")
				.hasSize(THREE);
	}

	/**
	 * Ensures the proper {@link JobType} is returned.
	 */
	@Test
	public void testJobType() {
		assertThat(JobType.DATA_POLICY)
				.as("Incorrect job type returned.")
				.isEqualTo(dataPolicyExporterImpl.getJobType());
	}

	/**
	 * Tests getDependentClasses.
	 */
	@Test
	public void testDependantClasses() {
		assertThat(new Class<?>[]{DataPolicy.class})
				.as("Incorrect dependent classes returned.")
				.isEqualTo(dataPolicyExporterImpl.getDependentClasses());
	}

	/**
	 * Tests getDomainAdapter.
	 */
	@Test
	public void testGetDomainAdapter() {
		assertThat(dataPolicyAdapter)
				.as("Incorrect domain adapter returned.")
				.isEqualTo(dataPolicyExporterImpl.getDomainAdapter());
	}

	/**
	 * Tests getImportExportSearcher.
	 */
	@Test
	public void testImportExportSearcher() {
		assertThat(importExportSearcher)
				.as("Incorrect export searcher returned.")
				.isEqualTo(dataPolicyExporterImpl.getImportExportSearcher());
	}
}
