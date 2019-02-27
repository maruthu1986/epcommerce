/**
 * Copyright (c) Elastic Path Software Inc., 2010
 */
package com.elasticpath.persistence.openjpa.impl;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitPostProcessor;

/**
 * A persistence unit post processor which merges multiple persistence units with the
 * same name within the same classloader. Also {@see OverridingPersistenceUnitPostProcessor}
 * for excluding mapping files & classes and overriding/adding properties.
 */
public class MergingPersistenceUnitPostProcessor implements PersistenceUnitPostProcessor {

	private final ListMultimap<String, String> puiClasses = ArrayListMultimap.create();
	private final ListMultimap<String, URL> puiURLs = ArrayListMultimap.create();
	private final ListMultimap<String, String> puiMappings = ArrayListMultimap.create();
	private final Map<String, Properties> puiProperties = new HashMap<>();

	/**
	 * Post-process the given PersistenceUnitInfo to perform the merge.
	 *
	 * @param persistenceUnitInfo the persistence unit info
	 */
	@Override
	public void postProcessPersistenceUnitInfo(final MutablePersistenceUnitInfo persistenceUnitInfo) {
		mergeManagedClassNames(persistenceUnitInfo);
		mergeJarFileUrls(persistenceUnitInfo);
		mergeMappingFileNames(persistenceUnitInfo);
		mergeProperties(persistenceUnitInfo);
	}

	/**
	 * Merge the collection of managed class names from the given unit with any other managed class
	 * names from other files with the same unit name.
	 * 
	 * @param persistenceUnitInfo the persistence unit to merge
	 */
	protected void mergeManagedClassNames(final MutablePersistenceUnitInfo persistenceUnitInfo) {
		String persistenceUnitName = persistenceUnitInfo.getPersistenceUnitName();
		puiClasses.putAll(persistenceUnitName, persistenceUnitInfo.getManagedClassNames());
		persistenceUnitInfo.getManagedClassNames().clear();
		persistenceUnitInfo.getManagedClassNames().addAll(puiClasses.get(persistenceUnitName));
	}

	/**
	 * Merge the collection of jar file urls from the given unit with any other urls
	 * from other files with the same unit name.
	 * 
	 * @param persistenceUnitInfo the persistence unit to merge
	 */
	protected void mergeJarFileUrls(final MutablePersistenceUnitInfo persistenceUnitInfo) {
		String persistenceUnitName = persistenceUnitInfo.getPersistenceUnitName();
		puiURLs.putAll(persistenceUnitName, persistenceUnitInfo.getJarFileUrls());
		persistenceUnitInfo.getJarFileUrls().clear();
		persistenceUnitInfo.getJarFileUrls().addAll(puiURLs.get(persistenceUnitName));
	}

	/**
	 * Merge the collection of mapping file names from the given unit with any other mapping file
	 * names from other files with the same unit name.
	 * 
	 * @param persistenceUnitInfo the persistence unit to merge
	 */
	protected void mergeMappingFileNames(final MutablePersistenceUnitInfo persistenceUnitInfo) {
		String persistenceUnitName = persistenceUnitInfo.getPersistenceUnitName();
		puiMappings.putAll(persistenceUnitName, persistenceUnitInfo.getMappingFileNames());
		persistenceUnitInfo.getMappingFileNames().clear();
		persistenceUnitInfo.getMappingFileNames().addAll(puiMappings.get(persistenceUnitName));
	}


	/**
	 * Merge the properties from the given unit with any other properties
	 * from other files with the same unit name.
	 * 
	 * NOTE: The preferred way to add or change properties is to use
	 * {@link OverridingPersistenceUnitPostProcessor} in Spring configuration.
	 * 
	 * @param persistenceUnitInfo the persistence unit to merge
	 */
	private void mergeProperties(final MutablePersistenceUnitInfo persistenceUnitInfo) {
		Properties properties = puiProperties.get(persistenceUnitInfo.getPersistenceUnitName());
		if (properties == null) {
			properties = new Properties();
			puiProperties.put(persistenceUnitInfo.getPersistenceUnitName(), properties);
		}
		final Properties props = persistenceUnitInfo.getProperties();
		properties.putAll(props);
		persistenceUnitInfo.getProperties().putAll(properties);
	}

}

