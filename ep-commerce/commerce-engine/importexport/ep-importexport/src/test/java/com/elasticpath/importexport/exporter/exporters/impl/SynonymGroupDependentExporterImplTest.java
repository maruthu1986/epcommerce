/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.importexport.exporter.exporters.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.search.SynonymGroup;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.exporter.configuration.ExportConfiguration;
import com.elasticpath.importexport.exporter.configuration.search.SearchConfiguration;
import com.elasticpath.importexport.exporter.context.DependencyRegistry;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.importexport.exporter.exporters.DependentExporterFilter;
import com.elasticpath.service.search.SynonymGroupService;

/**
 * Test for {@link SynonymGroupDependentExporterImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("PMD.NonStaticInitializer")
public class SynonymGroupDependentExporterImplTest {
	private final SynonymGroupDependentExporterImpl synonymGroupExporter = new SynonymGroupDependentExporterImpl();
	@Mock
	private SynonymGroupService synonymGroupService;
	@Mock
	private DependentExporterFilter dependentExporterFilter;
	private ExportContext exportContext;
	private static final long CATALOG_UID = 14441;

	/**
	 * Test initialization.
	 *
	 * @throws ConfigurationException in case of errors
	 */
	@Before
	public void setUp() throws ConfigurationException {
		synonymGroupExporter.setSynonymGroupService(synonymGroupService);

		exportContext = new ExportContext(new ExportConfiguration(), new SearchConfiguration());
		synonymGroupExporter.initialize(exportContext, dependentExporterFilter);
	}

	/**
	 * Tests finding dependent objects when the dependent object should be filtered.
	 */
	@Test
	public void testFindDependentObjectsFiltered() {
		SynonymGroup synonymGroup1 = mock(SynonymGroup.class, "synonymGroup-1");
		SynonymGroup synonymGroup2 = mock(SynonymGroup.class, "synonymGroup-2");
		final List<SynonymGroup> synonymGroupList = Arrays.asList(synonymGroup1, synonymGroup2);

		when(dependentExporterFilter.isFiltered(CATALOG_UID)).thenReturn(true);
		when(synonymGroupService.findAllSynonymGroupForCatalog(CATALOG_UID)).thenReturn(synonymGroupList);

		assertThat(synonymGroupExporter.findDependentObjects(CATALOG_UID))
				.isEqualTo(synonymGroupList);

		verify(dependentExporterFilter, times(1)).isFiltered(CATALOG_UID);
		verify(synonymGroupService, times(1)).findAllSynonymGroupForCatalog(CATALOG_UID);
	}

	/**
	 * Tests finding dependent objects when the dependent object should be filtered.
	 */
	@Test
	public void testFindDependentObjectsNotFiltered() {
		DependencyRegistry registry = new DependencyRegistry(Collections.singletonList(SynonymGroup.class));
		exportContext.setDependencyRegistry(registry);

		final String synonymGroup1Guid = "18acbfb8-de23-48e0-9230-ae7eb491b38e";
		final String synonymGroup2Guid = "ea231cbf-5c8b-47df-a880-564828ae69c6";
		final String synonymGroup3Guid = "1aa4922b-7a0b-43ad-8aef-670d63f24ae9";
		registry.addGuidDependency(SynonymGroup.class, synonymGroup1Guid);
		registry.addGuidDependencies(SynonymGroup.class, new TreeSet<>(Arrays.asList(synonymGroup2Guid, synonymGroup3Guid)));

		when(dependentExporterFilter.isFiltered(CATALOG_UID)).thenReturn(false);

		List<SynonymGroup> result = synonymGroupExporter.findDependentObjects(CATALOG_UID);

		assertThat(result)
				.size()
				.as("SynonymGroups returned")
				.isEqualTo(0);

		verify(dependentExporterFilter, times(1)).isFiltered(CATALOG_UID);
	}
}
