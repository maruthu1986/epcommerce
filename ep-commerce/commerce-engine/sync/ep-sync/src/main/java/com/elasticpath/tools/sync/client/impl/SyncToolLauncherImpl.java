/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.tools.sync.client.impl;

import org.apache.log4j.Logger;

import com.elasticpath.tools.sync.client.SyncJobConfiguration;
import com.elasticpath.tools.sync.client.SyncToolConfiguration;
import com.elasticpath.tools.sync.client.SyncToolLauncher;
import com.elasticpath.tools.sync.client.controller.SyncToolController;
import com.elasticpath.tools.sync.client.controller.SyncToolControllerFactory;
import com.elasticpath.tools.sync.exception.SyncToolConfigurationException;
import com.elasticpath.tools.sync.exception.SyncToolRuntimeException;
import com.elasticpath.tools.sync.target.result.Summary;

/**
 * Default implementation of {@link SyncToolLauncher}.
 */
public class SyncToolLauncherImpl implements SyncToolLauncher {

	private static final Logger LOG = Logger.getLogger(SyncToolLauncherImpl.class);

	private SyncToolConfiguration syncToolConfiguration;
	private SyncToolControllerFactory syncToolControllerFactory;

	private SyncToolController controller;

	// Use the startedUp flag to prevent startUp()/shutDown() methods being run whilst true
	private boolean startedUp;

	/**
	 * Initialises the launcher.
	 */
	public void startUp() {
		// Synchronize on this to prevent running if startUp(), processJob() or shutDown() are currently inflight; only one can be active at a time
		synchronized (this) {
			if (startedUp) {
				throw new SyncToolRuntimeException(
						"SyncToolLauncher has already started up and cannot be started up again before being first shutdown");
			}

			LOG.info("Starting up Data Sync Tool.");
			LOG.debug("Data Sync Tool Configuration: " + syncToolConfiguration);

			this.controller = getSyncToolControllerFactory().createController(syncToolConfiguration.getControllerType());
			this.controller.startUp();

			startedUp = true;

			LOG.info("Data Sync Tool completed startup.");
		}
	}

	@Override
	public Summary processJob(final SyncJobConfiguration jobConfiguration) throws SyncToolConfigurationException {
		// Synchronize on this to prevent running if startUp(), processJob() or shutDown() are currently inflight; only one can be active at a time
		synchronized (this) {
			LOG.info("Processing Data Sync Request for ChangeSet: " + jobConfiguration.getAdapterParameter());
			LOG.debug("Job Configuration: " + jobConfiguration);

			final Summary resultSummary = controller.synchronize(jobConfiguration);

			LOG.info("Data Sync Request processing complete. Successful: " + !resultSummary.hasErrors());

			return resultSummary;
		}
	}

	/**
	 * Shuts down the launcher after all processing has been completed.
	 */
	public void shutDown() {
		// Synchronize on this to prevent running if startUp(), processJob() or shutDown() are currently inflight; only one can be active at a time
		synchronized (this) {
			LOG.info("Shutting down Data Sync Tool including its resident Controller and Application Contexts.");

			this.controller.shutDown();
			this.controller = null;
		}
	}

	protected SyncToolControllerFactory getSyncToolControllerFactory() {
		return this.syncToolControllerFactory;
	}

	public void setSyncToolControllerFactory(final SyncToolControllerFactory syncToolControllerFactory) {
		this.syncToolControllerFactory = syncToolControllerFactory;
	}

	protected SyncToolConfiguration getSyncToolConfiguration() {
		return syncToolConfiguration;
	}

	public void setSyncToolConfiguration(final SyncToolConfiguration syncToolConfiguration) {
		this.syncToolConfiguration = syncToolConfiguration;
	}

}
