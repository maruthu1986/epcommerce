/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.importexport.exporter.exporters.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.catalog.ProductAssociation;
import com.elasticpath.domain.catalog.impl.CatalogImpl;
import com.elasticpath.domain.catalog.impl.ProductAssociationImpl;
import com.elasticpath.importexport.common.adapters.associations.ProductAssociationAdapter;
import com.elasticpath.importexport.common.dto.productassociation.ProductAssociationDTO;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.summary.Summary;
import com.elasticpath.importexport.common.summary.impl.SummaryImpl;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.exporter.configuration.ExportConfiguration;
import com.elasticpath.importexport.exporter.configuration.search.SearchConfiguration;
import com.elasticpath.importexport.exporter.context.DependencyRegistry;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.service.catalog.ProductAssociationService;

/**
 * ProductAssociation exporter test.
 */
@RunWith(MockitoJUnitRunner.class)
public class ProductAssociationExporterImplTest {

	private static final String ASSOCIATION_GUID = "f9fdc802-8269-45dd-af95-ebe8a548ba41";

	private ProductAssociationExporterImpl productAssociationExporter;

	@Mock
	private ProductAssociationService mockProductAssociationService;


	/**
	 * Prepare for tests.
	 *
	 * @throws Exception in case of error happens
	 */
	@Before
	public void setUp() throws Exception {
		final ProductAssociation productAssociation = new ProductAssociationImpl();
		productAssociation.setCatalog(new CatalogImpl());

		when(mockProductAssociationService.findByGuid(any(), any())).thenReturn(productAssociation);

		productAssociationExporter = new ProductAssociationExporterImpl();
		productAssociationExporter.setProductAssociationService(mockProductAssociationService);
		productAssociationExporter.setProductAssociationAdapter(new MockProductAssociationAdapter());
	}

	/**
	 * Check that during initialization exporter prepares the list of UidPk for product associations to be exported.
	 */
	@Test
	public void testExporterInitialization() {
		final List<String> associationGuidList = new ArrayList<>();
		associationGuidList.add(ASSOCIATION_GUID);

		ExportConfiguration exportConfiguration = new ExportConfiguration();
		SearchConfiguration searchConfiguration = new SearchConfiguration();

		ExportContext exportContext = new ExportContext(exportConfiguration, searchConfiguration);
		exportContext.setSummary(new SummaryImpl());

		List<Class<?>> dependentClasses = new ArrayList<>();
		dependentClasses.add(ProductAssociation.class);
		DependencyRegistry dependencyRegistry = new DependencyRegistry(dependentClasses);

		dependencyRegistry.addGuidDependencies(ProductAssociation.class, new TreeSet<>(associationGuidList));
		exportContext.setDependencyRegistry(dependencyRegistry);

		try {
			productAssociationExporter.initialize(exportContext);
		} catch (ConfigurationException e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Check an export of product associations.
	 */
	@Test
	public void testProcessExport() {
		testExporterInitialization();
		productAssociationExporter.processExport(System.out);
		Summary summary = productAssociationExporter.getContext().getSummary();
		assertThat(summary.getCounters())
				.size()
				.isEqualTo(1);
		assertThat(summary.getCounters())
				.containsKey(JobType.PRODUCTASSOCIATION);
		assertThat(summary.getCounters().get(JobType.PRODUCTASSOCIATION))
				.isEqualTo(1);
		assertThat(summary.getFailures())
				.size()
				.isEqualTo(0);
		assertThat(summary.getStartDate()).isNotNull();
		assertThat(summary.getElapsedTime()).isNotNull();
		assertThat(summary.getElapsedTime().toString()).isNotNull();
	}

	/**
	 * Mock product association adapter.
	 */
	private class MockProductAssociationAdapter extends ProductAssociationAdapter {

		@Override
		public void populateDomain(final ProductAssociationDTO source, final ProductAssociation target) {
			//do nothing
		}

		@Override
		public void populateDTO(final ProductAssociation source, final ProductAssociationDTO target) {
			// do nothing
		}
	}
}
