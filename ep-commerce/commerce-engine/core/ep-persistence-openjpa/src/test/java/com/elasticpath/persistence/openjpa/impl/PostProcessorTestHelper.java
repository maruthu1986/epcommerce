/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.persistence.openjpa.impl;

import static java.util.Arrays.asList;

import java.net.URL;

import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitPostProcessor;

/**
 * Helper methods for Post Processor tests.
 */
public final class PostProcessorTestHelper {

	/** The name of the persistence unit. */
	public static final String PERSISTENCE_UNIT_NAME = "pu";

	private PostProcessorTestHelper() {
		// Ensure class can not be instantiated
	}

	/**
	 * A persistence unit with the given mapping files.
	 *
	 * @param mappingFiles a collection of mapping files
	 * @return a persistence unit
	 */
	public static MutablePersistenceUnitInfo givenAPersistenceUnitWithMappingFiles(final String... mappingFiles) {
		MutablePersistenceUnitInfo pui = givenAPersistenceUnit();
		pui.getMappingFileNames().addAll(asList(mappingFiles));
		return pui;
	}

	/**
	 * A persistence unit with the given managed class names.
	 *
	 * @param classNames a collection of managed class name
	 * @return a persistence unit
	 */
	public static MutablePersistenceUnitInfo givenAPersistenceUnitWithManagedClassNames(final String... classNames) {
		MutablePersistenceUnitInfo pui = givenAPersistenceUnit();
		pui.getManagedClassNames().addAll(asList(classNames));
		return pui;
	}

	/**
	 * A persistence unit with the given Jar File URLs.
	 *
	 * @param jarFileUrls a collection of Jar File URLs
	 * @return a persistence unit
	 */
	public static MutablePersistenceUnitInfo givenAPersistenceUnitWithJarFileUrls(final URL... jarFileUrls) {
		MutablePersistenceUnitInfo pui = givenAPersistenceUnit();
		pui.getJarFileUrls().addAll(asList(jarFileUrls));
		return pui;
	}

	/**
	 * Calls processing of the given persistence units.
	 *
	 * @param processor the processor to call
	 * @param puis one or more persistence unit infos
	 */
	public static void whenTheProcessorIsCalled(final PersistenceUnitPostProcessor processor, final MutablePersistenceUnitInfo... puis) {
		for (MutablePersistenceUnitInfo pui : puis) {
			processor.postProcessPersistenceUnitInfo(pui);
		}
	}

	/**
	 * A persistence unit.
	 *
	 * @return a persistence unit.
	 */
	public static MutablePersistenceUnitInfo givenAPersistenceUnit() {
		MutablePersistenceUnitInfo pui = new MutablePersistenceUnitInfo();
		pui.setPersistenceUnitName(PERSISTENCE_UNIT_NAME);
		return pui;
	}

}