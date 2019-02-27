/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.persistence.openjpa.impl;

import java.util.List;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.sql.DataSource;

import org.apache.openjpa.persistence.OpenJPAEntityManagerFactorySPI;
import org.apache.openjpa.persistence.OpenJPAPersistence;
import org.springframework.core.io.ResourceLoader;
import org.springframework.instrument.classloading.LoadTimeWeaver;
import org.springframework.jdbc.datasource.lookup.SingleDataSourceLookup;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.persistenceunit.DefaultPersistenceUnitManager;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitPostProcessor;

/**
 * A {@link LocalContainerEntityManagerFactoryBean} that allows a configurable persistence unit manager.
 */
public class ConfigurableLocalContainerEntityManagerFactoryBean extends LocalContainerEntityManagerFactoryBean {

	private static final long serialVersionUID = 5000000001L;

	private DefaultPersistenceUnitManager persistenceUnitManager;
	private List<Object> lifecycleListeners;
	private boolean propertiesSet;

	/**
	 * Sets the persistence unit manager.
	 *
	 * @param persistenceUnitManager the new persistence unit manager
	 */
	public void setPersistenceUnitManager(final DefaultPersistenceUnitManager persistenceUnitManager) {
		super.setPersistenceUnitManager(persistenceUnitManager);
		this.persistenceUnitManager = persistenceUnitManager;
	}

	@Override
	public void setPersistenceXmlLocation(final String persistenceXmlLocation) {
		super.setPersistenceXmlLocation(persistenceXmlLocation);
		persistenceUnitManager.setDefaultPersistenceUnitRootLocation(persistenceXmlLocation);
	}

	@Override
	public void setPersistenceUnitName(final String persistenceUnitName) {
		super.setPersistenceUnitName(persistenceUnitName);
		persistenceUnitManager.setDefaultPersistenceUnitName(persistenceUnitName);
	}

	@Override
	public void setPackagesToScan(final String... packagesToScan) {
		super.setPackagesToScan(packagesToScan);
		persistenceUnitManager.setPackagesToScan(packagesToScan);
	}

	@Override
	public void setMappingResources(final String... mappingResources) {
		super.setMappingResources(mappingResources);
		persistenceUnitManager.setMappingResources(mappingResources);
	}

	@Override
	public void setDataSource(final DataSource dataSource) {
		super.setDataSource(dataSource);
		persistenceUnitManager.setDefaultDataSource(dataSource);
		persistenceUnitManager.setDataSourceLookup(new SingleDataSourceLookup(dataSource));
	}

	@Override
	public void setPersistenceUnitPostProcessors(final PersistenceUnitPostProcessor... postProcessors) {
		super.setPersistenceUnitPostProcessors(postProcessors);
		persistenceUnitManager.setPersistenceUnitPostProcessors(postProcessors);
	}

	@Override
	public void setLoadTimeWeaver(final LoadTimeWeaver loadTimeWeaver) {
		super.setLoadTimeWeaver(loadTimeWeaver);
		persistenceUnitManager.setLoadTimeWeaver(loadTimeWeaver);
	}

	@Override
	public void setResourceLoader(final ResourceLoader resourceLoader) {
		super.setResourceLoader(resourceLoader);
		persistenceUnitManager.setResourceLoader(resourceLoader);
	}

	public List<Object> getLifecycleListeners() {
		return lifecycleListeners;
	}

	/**
	 * Sets the lifecycle listeners that will be added to each EntityManager created by the factory created by this factory.
	 * The types allowed are any of the OpenJPA lifecycle listener interfaces in org.apache.openjpa.event.
	 *
	 * @param lifecycleListeners the lifecycle listeners that will be added to EntityManagers on creation
	 */
	public void setLifecycleListeners(final List<Object> lifecycleListeners) {
		this.lifecycleListeners = lifecycleListeners;
	}

	@Override
	protected EntityManagerFactory createNativeEntityManagerFactory() throws PersistenceException {
		if (!propertiesSet) {
			persistenceUnitManager.afterPropertiesSet();
			propertiesSet = true;
		}

		EntityManagerFactory emf = super.createNativeEntityManagerFactory();
		OpenJPAEntityManagerFactorySPI spi = (OpenJPAEntityManagerFactorySPI) OpenJPAPersistence.cast(emf);
		for (Object listener : getLifecycleListeners()) {
			spi.addLifecycleListener(listener, (Class []) null);
		}

		return emf;
	}

}
