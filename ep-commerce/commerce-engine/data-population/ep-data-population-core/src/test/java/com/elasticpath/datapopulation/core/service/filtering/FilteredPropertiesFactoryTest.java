/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.datapopulation.core.service.filtering;


import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * Tests {@link FilteredPropertiesFactory} helper factory class.
 */
public class FilteredPropertiesFactoryTest {

	public static final String TEST_KEY_ONE = "test.key.one";
	public static final String TEST_KEY_ONE_REPEATED = TEST_KEY_ONE + ".repeated";
	public static final String TEST_KEY_TWO = "test.key.two";
	public static final String PROPERTY1_HAS_VALUE_STMT = "The 1st property should have a value";
	public static final String PROPERTY2_HAS_VALUE_STMT = "The 2nd property should have a value";
	public static final int EXPECTED = 6;
	public static final String CONFIG_DIRECTORY_TEST_LOADING_PROPERTIES_FILE = "configDirectory/testLoadingProperties.properties";
	public static final String PROPERTY3_HAS_VALUE_STMT = "The 3rd property should have a value";
	public static final String DUMMYVALUE_ONE = "dummyvalueOne";
	public static final String DUMMYVALUE_TWO = "dummyvalueTwo";
	public static final String DUMMYVALUE_THREE = "dummyvalueThree";

	@Test
	public void testCombineSourcesWithNoReplacementAndNoFiltering() throws IOException {
		Properties properties = new Properties();
		properties.setProperty(TEST_KEY_ONE_REPEATED, "test.value.one"); //Should not be replaced
		properties.setProperty(TEST_KEY_TWO, "test.value.two");

		Properties sourceProperties = new Properties();
		sourceProperties.setProperty("test.key.one.repeated", "test.value.three");

		FilteredPropertiesFactory filteredPropertiesFactory = new FilteredPropertiesFactory();
		filteredPropertiesFactory.setSourceProperties(sourceProperties);
		filteredPropertiesFactory.setPropertiesToFilter(properties);
		filteredPropertiesFactory.setIncludeSourceProperties(true);
		Properties results = filteredPropertiesFactory.getObject();

		assertThat(results.getProperty("test.key.one.repeated"))
				.as(PROPERTY1_HAS_VALUE_STMT)
				.isNotNull();
		assertThat(results.getProperty("test.key.one.repeated"))
				.as("The property test.key.one.repeated is not replaced by source property if the key is the same")
				.isEqualTo("test.value.one");
		assertThat(results.getProperty(TEST_KEY_TWO))
				.as(PROPERTY2_HAS_VALUE_STMT)
				.isNotNull();
	}

	@Test
	public void testNotIncludeSourceProperties() throws IOException {
		Properties properties = new Properties();
		properties.setProperty(TEST_KEY_ONE, "test.value.one");
		properties.setProperty(TEST_KEY_TWO, "test.value.two");

		Properties sourceProperties = new Properties();
		sourceProperties.setProperty("test.key.three", "test.value.three");

		FilteredPropertiesFactory filteredPropertiesFactory = new FilteredPropertiesFactory();
		filteredPropertiesFactory.setSourceProperties(sourceProperties);
		filteredPropertiesFactory.setPropertiesToFilter(properties);
		filteredPropertiesFactory.setIncludeSourceProperties(false);
		Properties results = filteredPropertiesFactory.getObject();

		assertThat(results.getProperty(TEST_KEY_ONE))
				.as(PROPERTY1_HAS_VALUE_STMT)
				.isNotNull();

		assertThat(results.getProperty(TEST_KEY_TWO))
				.as(PROPERTY2_HAS_VALUE_STMT)
				.isNotNull();

		assertThat(results.getProperty("test.key.three"))
				.as("The 3rd property should not have a value because include source is %s", filteredPropertiesFactory.isIncludeSourceProperties())
				.isNull();
	}

	@Test
	public void testReadSingleSourceProperties() throws IOException {
		List<Resource> resources = new ArrayList<Resource>();
		ClassPathResource res1 = new ClassPathResource(CONFIG_DIRECTORY_TEST_LOADING_PROPERTIES_FILE);
		resources.add(res1);

		FilteredPropertiesFactory filteredPropertiesFactory = new FilteredPropertiesFactory();
		filteredPropertiesFactory.setSourceLocations(resources);
		filteredPropertiesFactory.setIncludeSourceProperties(true);
		Properties results = filteredPropertiesFactory.getObject();


		assertThat(results.getProperty(TEST_KEY_ONE))
				.as(PROPERTY1_HAS_VALUE_STMT)
				.isNotNull();

		assertThat(results.getProperty(TEST_KEY_TWO))
				.as(PROPERTY2_HAS_VALUE_STMT)
				.isNotNull();

		assertThat(results.getProperty("test.${placeholder.three}.three"))
				.as(PROPERTY3_HAS_VALUE_STMT)
				.isNotNull();
	}

	@Test
	public void testReadMultiSourceProperties() throws IOException {
		List<Resource> resources = new ArrayList<Resource>();
		ClassPathResource res1 = new ClassPathResource(CONFIG_DIRECTORY_TEST_LOADING_PROPERTIES_FILE);
		resources.add(res1);
		ClassPathResource res2 = new ClassPathResource("configDirectory/data-population.properties");
		resources.add(res2);

		FilteredPropertiesFactory filteredPropertiesFactory = new FilteredPropertiesFactory();
		filteredPropertiesFactory.setSourceLocations(resources);
		filteredPropertiesFactory.setIncludeSourceProperties(true);
		Properties results = filteredPropertiesFactory.getObject();

		assertThat(results.getProperty(TEST_KEY_ONE))
				.as(PROPERTY1_HAS_VALUE_STMT)
				.isNotNull();

		assertThat(results.getProperty(TEST_KEY_TWO))
				.as(PROPERTY2_HAS_VALUE_STMT)
				.isNotNull();

		assertThat(results.getProperty("test.${placeholder.three}.three"))
				.as(PROPERTY3_HAS_VALUE_STMT)
				.isNotNull();

		assertThat(results.getProperty("liquibase.contexts"))
				.as("The property liquibase.contexts should have a value")
				.isNotNull();
	}

	@Test
	public void testFilterProperties() throws IOException {
		List<Resource> resources = new ArrayList<Resource>();
		ClassPathResource res1 = new ClassPathResource(CONFIG_DIRECTORY_TEST_LOADING_PROPERTIES_FILE);
		resources.add(res1);

		Properties sourceProperties = new Properties();
		sourceProperties.setProperty("placeholder.one", "dummyvalueOne");
		sourceProperties.setProperty("placeholder.two", DUMMYVALUE_TWO);
		sourceProperties.setProperty("placeholder.three", DUMMYVALUE_THREE);

		FilteredPropertiesFactory filteredPropertiesFactory = new FilteredPropertiesFactory();
		filteredPropertiesFactory.setSourceProperties(sourceProperties);
		filteredPropertiesFactory.setLocationsToFilter(resources);
		filteredPropertiesFactory.setIncludeSourceProperties(false);
		Properties results = filteredPropertiesFactory.getObject();

		assertThat(results.getProperty(TEST_KEY_ONE))
				.as("The filtered value of test.key.one is dummyValueOne")
				.isEqualTo(DUMMYVALUE_ONE);

		assertThat(results.getProperty(TEST_KEY_TWO))
				.as("The filtered value of test.key.two is dummyValueTwo")
				.isEqualTo(DUMMYVALUE_TWO);

		assertThat(results.getProperty("test.dummyvalueThree.three"))
				.as(PROPERTY3_HAS_VALUE_STMT)
				.isNotNull();
	}

	@Test
	public void testFilteringNestedProperties() throws IOException {
		Properties sourceProperties = new Properties();
		sourceProperties.setProperty("placeholder.one", "one");
		sourceProperties.setProperty("placeholder.three", DUMMYVALUE_THREE);

		Properties unfilteredProperties = new Properties();
		unfilteredProperties.setProperty("${placeholder.three}", "dummyValue${placeholder.${placeholder.one}}");

		FilteredPropertiesFactory filteredPropertiesFactory = new FilteredPropertiesFactory();
		filteredPropertiesFactory.setSourceProperties(sourceProperties);
		filteredPropertiesFactory.setPropertiesToFilter(unfilteredProperties);
		filteredPropertiesFactory.setIncludeSourceProperties(false);
		Properties results = filteredPropertiesFactory.getObject();

		assertThat(results.getProperty(DUMMYVALUE_THREE))
				.as("Nested placeholders are resolved")
				.isEqualTo("dummyValueone");
	}

	@Test
	public void testUseAllPropertiesConfigsForFiltering() throws IOException {
		List<Resource> resources = new ArrayList<Resource>();
		ClassPathResource res1 = new ClassPathResource(CONFIG_DIRECTORY_TEST_LOADING_PROPERTIES_FILE);
		ClassPathResource res2 = new ClassPathResource("configDirectory/testLoadingProperties2.properties");
		resources.add(res1);
		resources.add(res2);

		Properties sourceProperties = new Properties();
		sourceProperties.setProperty("placeholder.one", "one");
		sourceProperties.setProperty("placeholder.two", DUMMYVALUE_TWO);
		sourceProperties.setProperty("placeholder.three", DUMMYVALUE_THREE);

		List<Resource> sourceResources = new ArrayList<Resource>();
		ClassPathResource res3 = new ClassPathResource("configDirectory/data-population.properties");
		sourceResources.add(res3);

		Properties unfilteredProperties = new Properties();
		unfilteredProperties.setProperty("liquibase.contexts", "${liquibase.contexts}${placeholder.one}");
		unfilteredProperties.setProperty("${placeholder.three}", "dummyValue${placeholder.${placeholder.one}}");
		unfilteredProperties.setProperty("new.liquibase.contexts", "dummyValue${liquibase.contexts}");

		FilteredPropertiesFactory filteredPropertiesFactory = new FilteredPropertiesFactory();
		filteredPropertiesFactory.setSourceProperties(sourceProperties);
		filteredPropertiesFactory.setSourceLocations(sourceResources);
		filteredPropertiesFactory.setPropertiesToFilter(unfilteredProperties);
		filteredPropertiesFactory.setLocationsToFilter(resources);
		filteredPropertiesFactory.setIncludeSourceProperties(false);
		Properties results = filteredPropertiesFactory.getObject();

		assertThat(results.keySet())
				.size()
				.as("There are 6 resulting properties")
				.isEqualTo(EXPECTED);

		assertThat(results.getProperty(TEST_KEY_ONE))
				.as("The filtered value of test.key.one is dummyValueOne")
				.isEqualTo("new value");

		assertThat(results.getProperty(TEST_KEY_TWO))
				.as("The filtered value of test.key.two is dummyValueTwo")
				.isEqualTo(DUMMYVALUE_TWO);

		assertThat(results.getProperty("test.dummyvalueThree.three"))
				.as(PROPERTY3_HAS_VALUE_STMT)
				.isNotNull();

		assertThat(results.getProperty(DUMMYVALUE_THREE))
				.as("Nested placeholders are resolved")
				.isEqualTo("dummyValueone");

		assertThat(results.getProperty("new.liquibase.contexts"))
				.as("The property new.liquibase.contexts is dummyValuedefault,test-data")
				.isEqualTo("dummyValuedefault,test-data");

	}
}
