/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.tools.sync.client.controller.impl;

import net.sf.ehcache.CacheManager;
import org.apache.log4j.Logger;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.tools.sync.client.SyncJobConfiguration;
import com.elasticpath.tools.sync.exception.SyncToolConfigurationException;
import com.elasticpath.tools.sync.job.DomainSorter;
import com.elasticpath.tools.sync.job.JobEntry;
import com.elasticpath.tools.sync.job.SourceObjectCache;
import com.elasticpath.tools.sync.job.TransactionJob;
import com.elasticpath.tools.sync.job.TransactionJobBuilder;
import com.elasticpath.tools.sync.job.TransactionJobUnit;
import com.elasticpath.tools.sync.job.dao.TransactionJobDao;
import com.elasticpath.tools.sync.job.dao.TransactionJobDaoFactory;
import com.elasticpath.tools.sync.job.descriptor.JobDescriptor;
import com.elasticpath.tools.sync.job.descriptor.SourceSyncRequestAdapter;
import com.elasticpath.tools.sync.job.descriptor.TransactionJobDescriptorEntry;
import com.elasticpath.tools.sync.job.descriptor.dao.JobDescriptorDao;
import com.elasticpath.tools.sync.job.descriptor.dao.JobDescriptorDaoFactory;
import com.elasticpath.tools.sync.processing.SerializableObject;
import com.elasticpath.tools.sync.processing.SerializableObjectListener;
import com.elasticpath.tools.sync.processing.SyncJobObjectProcessor;

/**
 * This controller is used to do synchronization between two live systems. It performs the following steps: <li>load data from the source system
 * (usually database) <li>process the data <li>send data to the target system
 */
public class FullController extends AbstractSyncController {

	private static final Logger LOG = Logger.getLogger(FullController.class);

	private static final String SOURCE_SYNC_REQ_ADAPTER = "changeSetAdapter";

	private SyncJobObjectProcessor objectProcessor;

	private JobDescriptorDaoFactory jobDescriptorDaoFactory;

	private TransactionJobDaoFactory transactionJobDaoFactory;

	private DomainSorter domainSorter;

	/**
	 * Builds TransactionJob.
	 * <p>
	 * Template method :
	 * <ul>
	 * <li>Builds {@link JobDescriptor} from source using <code>adapterParam</code></li>
	 * <li>Saves {@link JobDescriptor}</li>
	 * <li>Builds {@link TransactionJob} using pre-built {@link JobDescriptor}</li>
	 * <li>Saves {@link TransactionJob}</li>
	 * </ul>
	 * </p>
	 *
	 * @param objectListener the object listener to be used to notify for new available objects
	 * @param syncJobConfiguration the job configuration being synchronized
	 */
	@Override
	protected void loadTransactionJob(final SerializableObjectListener objectListener, final SyncJobConfiguration syncJobConfiguration) {
		//Clear the cache before the run, incase running in webapp (persistent) environment.
		clearCache();

		//perform initial lookups to build JobDescriptor
		final JobDescriptor jobDescriptor = buildJobDescriptor(syncJobConfiguration);

		final PersistenceEngine sourcePersistenceEngine = getSyncBeanFactory().getSourceBean(ContextIdNames.PERSISTENCE_ENGINE);
		sourcePersistenceEngine.clearCache();

		final SourceObjectCache sourceObjectCache = getSyncBeanFactory().getSourceBean("sourceObjectCache");

		getJobDescriptorDao(syncJobConfiguration).save(jobDescriptor);

		final TransactionJob transactionJob = buildTransactionJob(jobDescriptor);

		for (final TransactionJobUnit jobUnit : transactionJob.getTransactionJobUnits()) {
			domainSorter.sort(jobUnit.getJobDescriptorEntries());
		}


		if (sourceObjectCache.supportsPreloading()) {
			for (final TransactionJobUnit jobUnit : transactionJob.getTransactionJobUnits()) {
				for (final TransactionJobDescriptorEntry entry : jobUnit.getJobDescriptorEntries()) {
					//Load from source system to a temporary mem location instead of disk.
					//used later (in super.synchronize) to push to target system
					sourceObjectCache.load(entry.getGuid(), entry.getType());
				}
			}
		}

		getTransactionJobDao(syncJobConfiguration).save(transactionJob);

		// notify the listener of the new objects
		for (final SerializableObject object : transactionJob) {
			//process the object, take it from src object Cache and put it in target.
			objectListener.processObject(object);
			if (object instanceof JobEntry) {
				final JobEntry entry = (JobEntry) object;
				sourceObjectCache.remove(entry.getGuid(), entry.getType());
			}
		}
	}

	/**
	 * Clears the caches, useful for running multiple DST runs in a row without restarting/clearing the context.
	 */
	protected void clearCache() {
		CacheManager cacheManager = getSyncBeanFactory().getSourceBean("epEhcacheManager");
		cacheManager.clearAll();
	}

	/**
	 * Builds JobDescriptor from source using <code>adapterParam</code>.
	 *
	 * @return {@link JobDescriptor} instance
	 * @param syncJobConfiguration the job configuration being synchronized
	 */
	protected JobDescriptor buildJobDescriptor(final SyncJobConfiguration syncJobConfiguration) {
		final SourceSyncRequestAdapter sourceSyncRequestAdapter = getSourceSyncRequestAdapter();

		final String adapterParameter = syncJobConfiguration.getAdapterParameter();
		LOG.info("Building Job Descriptor for : " + adapterParameter);

		final JobDescriptor jobDescriptor = sourceSyncRequestAdapter.buildJobDescriptor(adapterParameter);

		LOG.info("Job Descriptor has been built");

		return jobDescriptor;
	}

	/**
	 * @return {@link SourceSyncRequestAdapter} instance
	 */
	protected SourceSyncRequestAdapter getSourceSyncRequestAdapter() {
		final SourceSyncRequestAdapter sourceSyncRequestAdapter = getSyncBeanFactory().getSourceBean(SOURCE_SYNC_REQ_ADAPTER);

		if (sourceSyncRequestAdapter == null) {
			throw new SyncToolConfigurationException("Unable to get source sync request adapter for type: " + SOURCE_SYNC_REQ_ADAPTER);
		}

		LOG.info("Using Adapter : " + SOURCE_SYNC_REQ_ADAPTER);

		return sourceSyncRequestAdapter;
	}

	/**
	 * Builds TransactionJob using pre builded {@link JobDescriptor}.
	 *
	 * @param jobDescriptor the {@link JobDescriptor} which was built using {@link #buildJobDescriptor}.
	 * @return TransactionJob instance.
	 */
	protected TransactionJob buildTransactionJob(final JobDescriptor jobDescriptor) {
		if (jobDescriptor == null) {
			throw new SyncToolConfigurationException("Unable to build job unit. Job Descriptor wan't provided");
		}

		LOG.debug("Building job unit...");
		final TransactionJobBuilder transactionJobBuilder = getTransactionJobFromBean();

		final TransactionJob transactionJob = transactionJobBuilder.build(jobDescriptor, false);

		LOG.debug("Job Unit has been built");

		return transactionJob;
	}

	protected TransactionJobBuilder getTransactionJobFromBean() {
		return getSyncBeanFactory().getSourceBean("cachingTransactionJobBuilder");
	}


	/**
	 * Creates a new {@link JobDescriptorDao} instance.
	 *
	 * @param syncJobConfiguration the job configuration being synchronized
	 * @return the JobDescriptorDao
	 */
	protected JobDescriptorDao getJobDescriptorDao(final SyncJobConfiguration syncJobConfiguration) {
		return getJobDescriptorDaoFactory().createJobDescriptorDao(syncJobConfiguration);
	}

	/**
	 * Creates a new {@link TransactionJobDao} instance.
	 *
	 * @param syncJobConfiguration the job configuration being synchronized
	 * @return the TransactionJobDao
	 */
	protected TransactionJobDao getTransactionJobDao(final SyncJobConfiguration syncJobConfiguration) {
		return getTransactionJobDaoFactory().createTransactionJobDao(syncJobConfiguration);
	}

	/**
	 * Sets the {@link DomainSorter} instance to use.
	 *
	 * @param domainSorter {@link DomainSorter} instance to use
	 */
	public void setDomainSorter(final DomainSorter domainSorter) {
		this.domainSorter = domainSorter;
	}

	/**
	 * @return the object processor instance
	 */
	@Override
	protected SyncJobObjectProcessor getObjectProcessor() {
		return objectProcessor;
	}

	/**
	 * @param objectProcessor the objectProcessor to set
	 */
	public void setObjectProcessor(final SyncJobObjectProcessor objectProcessor) {
		this.objectProcessor = objectProcessor;
	}

	protected JobDescriptorDaoFactory getJobDescriptorDaoFactory() {
		return jobDescriptorDaoFactory;
	}

	public void setJobDescriptorDaoFactory(final JobDescriptorDaoFactory jobDescriptorDaoFactory) {
		this.jobDescriptorDaoFactory = jobDescriptorDaoFactory;
	}

	protected TransactionJobDaoFactory getTransactionJobDaoFactory() {
		return transactionJobDaoFactory;
	}

	public void setTransactionJobDaoFactory(final TransactionJobDaoFactory transactionJobDaoFactory) {
		this.transactionJobDaoFactory = transactionJobDaoFactory;
	}

	/**
	 * Initializes both source and target systems.
	 *
	 * @param sourceSystem the source system configuration
	 * @param targetSystem the target system configuration
	 */
	@Override
	protected void initConfig(final SystemConfig sourceSystem, final SystemConfig targetSystem) {
		sourceSystem.initSystem();
		targetSystem.initSystem();
	}

	/**
	 * Calls destroy cleanup on both source and target systems.
	 *
	 * @param sourceSystem the source system
	 * @param targetSystem the target system
	 */
	@Override
	protected void destroyConfig(final SystemConfig sourceSystem, final SystemConfig targetSystem) {
		sourceSystem.destroySystem();
		targetSystem.destroySystem();
	}

}
