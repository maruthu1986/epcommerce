/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.tools.sync.client.controller.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;

import com.elasticpath.tools.sync.beanfactory.ContextInitializer;
import com.elasticpath.tools.sync.beanfactory.ContextInitializerFactory;
import com.elasticpath.tools.sync.beanfactory.SyncBeanFactoryMutator;
import com.elasticpath.tools.sync.client.SyncJobConfiguration;
import com.elasticpath.tools.sync.client.SyncToolConfiguration;
import com.elasticpath.tools.sync.client.controller.SyncToolController;
import com.elasticpath.tools.sync.configuration.ConnectionConfiguration;
import com.elasticpath.tools.sync.exception.SyncToolRuntimeException;
import com.elasticpath.tools.sync.processing.SerializableObjectListener;
import com.elasticpath.tools.sync.processing.SyncJobObjectProcessor;
import com.elasticpath.tools.sync.target.result.Summary;
import com.elasticpath.tools.sync.target.result.impl.SummaryImpl;

/**
 * This abstract implementation of a {@link SyncToolController} outlines the
 * basic steps of performing a system synchronization - source, target or both.
 */
public abstract class AbstractSyncController implements SyncToolController {

	private static final Logger LOG = Logger.getLogger(AbstractSyncController.class);

	private SystemConfig sourceConfig;

	private SystemConfig targetConfig;

	private SyncToolConfiguration syncToolConfiguration;

	private ContextInitializerFactory contextInitializerFactory;

	private SyncBeanFactoryMutator syncBeanFactory;

	private AbstractObjectEventDistributor objectEventDistributor;

	/**
	 * A system configuration interface for triggering initialization on demand.
	 */
	public interface SystemConfig {

		/**
		 * Initializes a system configuration.
		 */
		void initSystem();

		/**
		 * Destroy system.
		 */
		void destroySystem();
	}

	/**
	 * Loads a TransactionJob instance by using a listener.
	 * <p>It can be loaded from database or read from file, or magically created</p>
	 *
	 * @param listener the object listener
	 * @param syncJobConfiguration the configuration for this sync job
	 * @throws SyncToolRuntimeException if an error occurs while loading
	 */
	protected abstract void loadTransactionJob(SerializableObjectListener listener, SyncJobConfiguration syncJobConfiguration)
			throws SyncToolRuntimeException;

	@Override
	public void startUp() {
		initConfig(getSourceConfig(), getTargetConfig());
	}

	@Override
	@SuppressWarnings("PMD.AvoidSynchronizedAtMethodLevel")
	public synchronized Summary synchronize(final SyncJobConfiguration jobConfiguration) {
		synchronizationToBeStarted(jobConfiguration);

		final Summary summary = new SummaryImpl();

		final AbstractObjectEventDistributor eventDistributor = getObjectEventDistributor();

		// set the properties of the distributor
		eventDistributor.startJob();
		eventDistributor.setObjectProcessor(getObjectProcessor());
		eventDistributor.setSummary(summary);

		try {
			loadTransactionJob(eventDistributor, jobConfiguration);
		} catch (SyncToolRuntimeException exc) {
			eventDistributor.handleException(exc, summary, null);
			return summary;
		} catch (Exception exc) {
			LOG.error("Error while performing synchronization.", exc);
			eventDistributor.handleException(exc, summary, null);
			return summary;
		} finally {
			try {
				eventDistributor.finished();
			} catch (Exception exc) {
				eventDistributor.handleException(exc, summary, null);
				LOG.error("Error while finishing up the synchronization process.", exc);
			}
		}

		this.synchronizationCompleted(jobConfiguration);

		return summary;
	}

	@Override
	public void shutDown() {
		destroyConfig(getSourceConfig(), getTargetConfig());
	}

	/**
	 * A callback method for the event of a starting synchronization.
	 *
	 * @param jobConfiguration the job configuration being started
	 */
	protected void synchronizationToBeStarted(final SyncJobConfiguration jobConfiguration) {
		LOG.debug("Begin synchronization...");
	}

	/**
	 * Initialise config.
	 *
	 * @param sourceSystem source system configuration
	 * @param targetSystem target system configuration
	 */
	protected abstract void initConfig(SystemConfig sourceSystem, SystemConfig targetSystem);

	/**
	 * Destroy config.
	 *
	 * @param sourceConfig the source config
	 * @param targetConfig the target config
	 */
	protected abstract void destroyConfig(SystemConfig sourceConfig, SystemConfig targetConfig);

	/**
	 * Gets the object processor reponsible for properly handling the objects.
	 *
	 * @return the object processor to be used
	 */
	protected abstract SyncJobObjectProcessor getObjectProcessor();

	/**
	 * A callback method for when the synchronization completes.
	 *
	 * @param jobConfiguration the job configuration being completed
	 */
	protected void synchronizationCompleted(final SyncJobConfiguration jobConfiguration) {
		LOG.debug("Synchronization completed.");
	}

	/**
	 * Gets the source {@link SystemConfig} currently in use, creating one if no config is currently in use.
	 *
	 * @return the source {@link SystemConfig} currently in use; never {@code null}.
	 */
	protected SystemConfig getSourceConfig() {
		if (this.sourceConfig == null) {
			this.sourceConfig = new SourceSystemConfig();
		}
		return this.sourceConfig;
	}

	protected void setSourceConfig(final SystemConfig sourceConfig) {
		this.sourceConfig = sourceConfig;
	}

	/**
	 * Gets the target {@link SystemConfig} currently in use, creating one if no config is currently in use.
	 *
	 * @return the target {@link SystemConfig} currently in use; never {@code null}.
	 */
	protected SystemConfig getTargetConfig() {
		if (this.targetConfig == null) {
			this.targetConfig = new TargetSystemConfig();
		}
		return this.targetConfig;
	}

	protected void setTargetConfig(final SystemConfig targetConfig) {
		this.targetConfig = targetConfig;
	}

	protected SyncToolConfiguration getSyncToolConfiguration() {
		return syncToolConfiguration;
	}

	public void setSyncToolConfiguration(final SyncToolConfiguration syncToolConfiguration) {
		this.syncToolConfiguration = syncToolConfiguration;
	}

	protected ContextInitializerFactory getContextInitializerFactory() {
		return contextInitializerFactory;
	}

	public void setContextInitializerFactory(final ContextInitializerFactory contextInitializerFactory) {
		this.contextInitializerFactory = contextInitializerFactory;
	}

	protected SyncBeanFactoryMutator getSyncBeanFactory() {
		return syncBeanFactory;
	}

	public void setSyncBeanFactory(final SyncBeanFactoryMutator syncBeanFactory) {
		this.syncBeanFactory = syncBeanFactory;
	}

	protected AbstractObjectEventDistributor getObjectEventDistributor() {
		return objectEventDistributor;
	}

	public void setObjectEventDistributor(final AbstractObjectEventDistributor objectEventDistributor) {
		this.objectEventDistributor = objectEventDistributor;
	}

	/** SystemConfig for source. */
	private class SourceSystemConfig implements SystemConfig {
		private BeanFactory beanFactory;
		private ContextInitializer initializer;

		@Override
		public void initSystem() {
			ConnectionConfiguration sourceSystemConnectionConfig = getSyncToolConfiguration().getSourceConfig();
			String destinationType = "source";
			String connectionType = sourceSystemConnectionConfig.getType();
			ContextInitializer sourceContextInitializer = contextInitializerFactory.create(connectionType, destinationType);
			BeanFactory sourceBeanFactory = sourceContextInitializer.initializeContext(sourceSystemConnectionConfig);
			initializer = sourceContextInitializer;
			beanFactory = sourceBeanFactory;
			getSyncBeanFactory().setSourceBeanFactory(sourceBeanFactory);
		}

		@Override
		public void destroySystem() {
			initializer.destroyContext(beanFactory);
		}
	}

	/** SystemConfig for target. */
	private class TargetSystemConfig implements SystemConfig {
		private BeanFactory beanFactory;

		private ContextInitializer initializer;

		@Override
		public void initSystem() {
			ConnectionConfiguration targetSystemConnectionConfig = getSyncToolConfiguration().getTargetConfig();
			String destinationType = "target";
			String connectionType = targetSystemConnectionConfig.getType();
			ContextInitializer targetContextInitializer = contextInitializerFactory.create(connectionType, destinationType);
			BeanFactory targetBeanFactory = targetContextInitializer.initializeContext(targetSystemConnectionConfig);
			initializer = targetContextInitializer;
			beanFactory = targetBeanFactory;
			getSyncBeanFactory().setTargetBeanFactory(targetBeanFactory);

		}
		@Override
		public void destroySystem() {
			// This may be null in the case of an export
			if (initializer != null) {
				initializer.destroyContext(beanFactory);
			}
		}

	}
}
