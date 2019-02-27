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

import com.elasticpath.common.dto.datapolicy.CustomerConsentDTO;
import com.elasticpath.domain.datapolicy.CustomerConsent;
import com.elasticpath.domain.datapolicy.impl.CustomerConsentImpl;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.exporter.configuration.ExportConfiguration;
import com.elasticpath.importexport.exporter.configuration.ExporterConfiguration;
import com.elasticpath.importexport.exporter.configuration.search.SearchConfiguration;
import com.elasticpath.importexport.exporter.context.DependencyRegistry;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.importexport.exporter.search.ImportExportSearcher;
import com.elasticpath.service.datapolicy.CustomerConsentService;

/**
 * Tests <code>CustomerConsentExporterImpl</code>.
 */
@RunWith(MockitoJUnitRunner.class)
public class CustomerConsentExporterImplTest {

	@InjectMocks
	private CustomerConsentExporterImpl customerConsentExporterImpl;

	@Mock
	private ImportExportSearcher importExportSearcher;

	@Mock
	private CustomerConsentService customerConsentService;

	@Mock
	private DomainAdapter<CustomerConsent, CustomerConsentDTO> customerConsentAdapter;

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

		final CustomerConsent customerConsent1 = new CustomerConsentImpl();
		customerConsent1.setGuid(GUID1);
		final CustomerConsent customerConsent2 = new CustomerConsentImpl();
		customerConsent2.setGuid(GUID2);
		final CustomerConsent customerConsent3 = new CustomerConsentImpl();
		customerConsent3.setGuid(GUID3);
		final List<CustomerConsent> customerConsentList = Arrays.asList(customerConsent1, customerConsent2, customerConsent3);
		final List<String> foundGuids = Arrays.asList(GUID1, GUID2, GUID3);

		when(customerConsentService.list()).thenReturn(customerConsentList);

		exportContext = new ExportContext(new ExportConfiguration(), new SearchConfiguration());
		customerConsentExporterImpl.initialize(exportContext);

		assertThat(CollectionUtils.isEqualCollection(foundGuids, customerConsentExporterImpl.getListExportableIDs()))
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

		final CustomerConsent customerConsent1 = mock(CustomerConsent.class, "customerConsent-1");
		final CustomerConsent customerConsent2 = mock(CustomerConsent.class, "customerConsent-2");

		final List<String> guidList = Arrays.asList("GUID1", "GUID1-DNE");
		final List<CustomerConsent> customerConsentList = Arrays.asList(customerConsent1, customerConsent2);

		when(customerConsentService.findByGuids(guidList)).thenReturn(customerConsentList);
		List<CustomerConsent> results = customerConsentExporterImpl.findByIDs(guidList);

		assertThat(CollectionUtils.isEqualCollection(results, customerConsentList))
				.as("Missing returned customer consent")
				.isTrue();

		assertThat(results)
				.as("Extra customer consent returned")
				.hasSize(2);
	}


	/**
	 * Test method for {@link CustomerConsentExporterImpl#getListExportableIDs()}.
	 *
	 * @throws ConfigurationException not expected exception.
	 */
	@Test
	public void testGetListExportableIDs() throws ConfigurationException {
		ExportConfiguration exportConfiguration = new ExportConfiguration();
		SearchConfiguration searchConfiguration = new SearchConfiguration();

		when(customerConsentService.list()).thenReturn(Collections.emptyList());

		customerConsentExporterImpl.initialize(new ExportContext(exportConfiguration, searchConfiguration));
		customerConsentExporterImpl.getContext()
				.setDependencyRegistry(new DependencyRegistry(Arrays.asList(new Class<?>[]{CustomerConsent.class})));

		ExporterConfiguration exporterConfiguration = new ExporterConfiguration();
		exportConfiguration.setExporterConfiguration(exporterConfiguration);

		customerConsentExporterImpl.getContext().getDependencyRegistry().addGuidDependency(CustomerConsent.class, GUID1);
		customerConsentExporterImpl.getContext().getDependencyRegistry().addGuidDependency(CustomerConsent.class, GUID2);
		customerConsentExporterImpl.getContext().getDependencyRegistry().addGuidDependency(CustomerConsent.class, GUID3);

		final List<String> listExportableIDs = customerConsentExporterImpl.getListExportableIDs();

		assertThat(CollectionUtils.isEqualCollection(listExportableIDs, Arrays.asList(GUID1, GUID2, GUID3)))
				.as("Missing returned customer consent")
				.isTrue();

		assertThat(listExportableIDs)
				.as("Extra customer consent returned")
				.hasSize(THREE);
	}

	/**
	 * Ensures the proper {@link JobType} is returned.
	 */
	@Test
	public void testJobType() {
		assertThat(JobType.CUSTOMER_CONSENT)
				.as("Incorrect job type returned.")
				.isEqualTo(customerConsentExporterImpl.getJobType());
	}

	/**
	 * Tests getDependentClasses.
	 */
	@Test
	public void testDependantClasses() {
		assertThat(new Class<?>[]{CustomerConsent.class})
				.as("Incorrect dependent classes returned.")
				.isEqualTo(customerConsentExporterImpl.getDependentClasses());
	}

	/**
	 * Tests getDomainAdapter.
	 */
	@Test
	public void testGetDomainAdapter() {
		assertThat(customerConsentAdapter)
				.as("Incorrect domain adapter returned.")
				.isEqualTo(customerConsentExporterImpl.getDomainAdapter());
	}

	/**
	 * Tests getImportExportSearcher.
	 */
	@Test
	public void testImportExportSearcher() {
		assertThat(importExportSearcher)
				.as("Incorrect export searcher returned.")
				.isEqualTo(customerConsentExporterImpl.getImportExportSearcher());
	}
}
