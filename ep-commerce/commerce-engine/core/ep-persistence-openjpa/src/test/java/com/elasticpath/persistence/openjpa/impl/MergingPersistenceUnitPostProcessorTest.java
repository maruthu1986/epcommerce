/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.persistence.openjpa.impl;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

import static com.elasticpath.persistence.openjpa.impl.PostProcessorTestHelper.givenAPersistenceUnitWithJarFileUrls;
import static com.elasticpath.persistence.openjpa.impl.PostProcessorTestHelper.givenAPersistenceUnitWithManagedClassNames;
import static com.elasticpath.persistence.openjpa.impl.PostProcessorTestHelper.givenAPersistenceUnitWithMappingFiles;
import static com.elasticpath.persistence.openjpa.impl.PostProcessorTestHelper.whenTheProcessorIsCalled;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.junit.Test;
import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;

/**
 * Test the merging and exclusion functionality of the MergingPersistenceUnitPostProcessor.
 */
public class MergingPersistenceUnitPostProcessorTest {

	private final MergingPersistenceUnitPostProcessor processor = new MergingPersistenceUnitPostProcessor();

	/**
	 * Test merging the list of mapping file names, after processing the last PersistenceUnitInfo
	 * should contain the superset of all previous units.
	 */
	@Test
	public void testMergingMappingFileNames() {

		MutablePersistenceUnitInfo pui1 = givenAPersistenceUnitWithMappingFiles("a");
		MutablePersistenceUnitInfo pui2 = givenAPersistenceUnitWithMappingFiles("b");
		MutablePersistenceUnitInfo pui3 = givenAPersistenceUnitWithMappingFiles("c");

		whenTheProcessorIsCalled(processor, pui1, pui2, pui3);
		assertEquals(asList("a", "b", "c"), pui3.getMappingFileNames());
	}

	/**
	 * Test merging the list of class names, after processing the last PersistenceUnitInfo
	 * should contain the superset of all previous units.
	 */
	@Test
	public void testMergingManagedClassNames() {

		MutablePersistenceUnitInfo pui1 = givenAPersistenceUnitWithManagedClassNames("a");
		MutablePersistenceUnitInfo pui2 = givenAPersistenceUnitWithManagedClassNames("b");
		MutablePersistenceUnitInfo pui3 = givenAPersistenceUnitWithManagedClassNames("c");

		whenTheProcessorIsCalled(processor, pui1, pui2, pui3);
		assertEquals(asList("a", "b", "c"), pui3.getManagedClassNames());
	}

	/**
	 * Test merging the list of class names, after processing the last PersistenceUnitInfo
	 * should contain the superset of all previous units.
	 * @throws MalformedURLException if there is a problem with the test urls.
	 */
	@SuppressWarnings("PMD.AvoidDuplicateLiterals")
	@Test
	public void testMergingJarFileUrls() throws MalformedURLException {

		MutablePersistenceUnitInfo pui1 = givenAPersistenceUnitWithJarFileUrls(new URL("file:///jar1"));
		MutablePersistenceUnitInfo pui2 = givenAPersistenceUnitWithJarFileUrls(new URL("file:///jar2"));
		MutablePersistenceUnitInfo pui3 = givenAPersistenceUnitWithJarFileUrls(new URL("file:///jar3"));

		whenTheProcessorIsCalled(processor, pui1, pui2, pui3);
		ArrayList<URL> jarFileUrls = new ArrayList<>(pui3.getJarFileUrls());
		assertEquals(asList(new URL("file:///jar1"), new URL("file:///jar2"), new URL("file:///jar3")), jarFileUrls);
	}




}
