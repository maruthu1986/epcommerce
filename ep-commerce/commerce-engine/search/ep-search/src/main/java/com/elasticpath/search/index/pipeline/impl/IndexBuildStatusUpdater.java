/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.search.index.pipeline.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.elasticpath.domain.search.IndexBuildStatus;
import com.elasticpath.persistence.dao.IndexBuildStatusDao;
import com.elasticpath.service.search.IndexType;

/**
 * Utility that manages the updating of IndexBuildStatus objects in the context of a multi-threaded indexing environmnent.
 * 
 * During Indexing there will be a single IndexBuildStatus instance for each IndexType. These are shared between indexer threads and used to update
 * the statistics for a particular index job as it runs. The intention of this class is to prevent issues that might occur in this multi-threaded
 * environment, particularly around IndexBuildStatus objects becoming stale. There was a particularly nasty openjpa error occurring previously.
 * 
 * IndexBuildStatus instances get passed to this class, they are put on a queue, there is a worker thread that periodically checks this queue
 * for any objects to update. We manage the copying 
 * of data from the passed in IndexBuildStatus ourselves, after an update we always save back to the internal map.
 * 
 */
public class IndexBuildStatusUpdater {

	private static final Logger LOG = Logger.getLogger(IndexBuildStatusUpdater.class);
	
	private static final long POLLING_INTERVAL_IN_MILLISECONDS = 500L;
	
	private static final long MAX_TERMINATION_TIME_IN_SECONDS = 30L;
	
	private final BlockingQueue<IndexBuildStatus> waitingQueue = new LinkedBlockingDeque<>();
	
	private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
	
	private IndexBuildStatusDao indexBuildStatusDao;

	/**
	 * Add the <code>IndexBuildStatus</code> to the waiting queue to be processed later!
	 * @param buildStatus the status update to be processed
	 */
	public void enqueue(final IndexBuildStatus buildStatus) {
		waitingQueue.add(buildStatus);		
	}
	
	/**
	 * Initializes a background process that manages the retrieval of <code>IndexBuildStatus</code> from 
	 * internal <code>Queue</code> and passes it to the internal <code>IndexBuildStatusDao</code>.
	 */
	public void initialize() {
		executorService.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				if (!waitingQueue.isEmpty()) {
					LinkedList<IndexBuildStatus> processingQueue = new LinkedList<>();
					waitingQueue.drainTo(processingQueue);
					if (LOG.isDebugEnabled()) {
						LOG.debug(String.format("Processing: %s. Waiting: %s", processingQueue.size(), waitingQueue.size()));
					}
					saveOrUpdateMostRecentStatusUpdatesPerType(processingQueue);
				}
			}
		}, 0, POLLING_INTERVAL_IN_MILLISECONDS, TimeUnit.MILLISECONDS);
	}
	
	private void saveOrUpdateMostRecentStatusUpdatesPerType(final Queue<IndexBuildStatus> batchOfStatusUpdates) {
		Map<IndexType, IndexBuildStatus> statusesByType = new HashMap<>();
		//process to a Map so we have unique IndexBuildStatus by IndexType
		//as every IndexBuildStatus by type will point to the same instance anyway
		//so we only want to hit the database once for each.
		for (IndexBuildStatus indexBuildStatus : batchOfStatusUpdates) {
			if (indexBuildStatus != null && indexBuildStatus.getIndexType() != null) {
				statusesByType.put(indexBuildStatus.getIndexType(), indexBuildStatus);
			}
		}
		
		for (IndexBuildStatus indexBuildStatus : statusesByType.values()) {
			prepareInstanceForSaveOrUpdate(indexBuildStatus); 
		}
	}
	
	/**
	 * We always want to get a fresh instance from the db and do a soft copy of the data
	 * from the passed in <code>IndexBuildStatus</code> as we dont want OpenJpa to get in a twist
	 * in a multi threaded environment.
	 * 
	 * @param buildStatus buildStatus
	 * @return an updated Entity
	 */
	private void prepareInstanceForSaveOrUpdate(final IndexBuildStatus indexBuildStatus) {
		
		IndexBuildStatus freshInstance = indexBuildStatusDao.get(indexBuildStatus.getIndexType());
		
		if (freshInstance == null) {
			freshInstance = indexBuildStatus;
		} else {
			freshInstance.setLastBuildDate(indexBuildStatus.getLastBuildDate());
			freshInstance.setIndexStatus(indexBuildStatus.getIndexStatus());
			freshInstance.setLastModifiedDate(indexBuildStatus.getLastModifiedDate());
			freshInstance.setOperationStartDate(indexBuildStatus.getOperationStartDate());
			freshInstance.setProcessedRecords(indexBuildStatus.getProcessedRecords());
			freshInstance.setTotalRecords(indexBuildStatus.getTotalRecords());		
			freshInstance.setIndexType(indexBuildStatus.getIndexType());		
		}
		try {
			indexBuildStatusDao.saveOrUpdate(freshInstance);
		} catch (Exception e) {
			LOG.error("could not update IndexBuildStatus for type: " 
				+ freshInstance.getIndexType() + " uidPk:" + freshInstance.getUidPk(), e);
		}
	}

	/**
	 * Indicates that the internal thread executor is eligible to stop.
	 */
	public void shutdown() {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Shutting down IndexBuildStatusUpdater");
		}
		executorService.shutdown();
		try {
			if (!executorService.awaitTermination(MAX_TERMINATION_TIME_IN_SECONDS, TimeUnit.SECONDS)) {
				executorService.shutdownNow(); // Cancel currently executing tasks
			}
		} catch (InterruptedException ie) {
			// (Re-)Cancel if current thread also interrupted
			executorService.shutdownNow();
			// Preserve interrupt status
			Thread.currentThread().interrupt();
		}
	}
	
	/**
	 * Sets the  <code>IndexBuildStatusDao</code>.
	 * @param indexBuildStatusDao indexBuildStatusDao
	 */
	public void setIndexBuildStatusDao(final IndexBuildStatusDao indexBuildStatusDao) {
		this.indexBuildStatusDao = indexBuildStatusDao;
	}
}
