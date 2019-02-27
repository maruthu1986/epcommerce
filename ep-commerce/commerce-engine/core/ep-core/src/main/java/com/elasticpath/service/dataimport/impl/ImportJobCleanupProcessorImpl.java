/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.service.dataimport.impl;

import java.io.File;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.commons.util.AssetRepository;
import com.elasticpath.domain.dataimport.ImportAction;
import com.elasticpath.domain.dataimport.ImportJobState;
import com.elasticpath.domain.dataimport.ImportJobStatus;
import com.elasticpath.domain.dataimport.ImportNotification;
import com.elasticpath.domain.dataimport.ImportNotificationState;
import com.elasticpath.service.dataimport.ImportJobCleanupProcessor;
import com.elasticpath.service.dataimport.StaleImportNotificationProcessor;
import com.elasticpath.service.dataimport.dao.ImportJobStatusDao;
import com.elasticpath.service.dataimport.dao.ImportNotificationDao;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.settings.provider.SettingValueProvider;

/**
 * The default implementation of {@link ImportJobCleanupProcessor}.
 *
 * Import jobs remain in the database after successful processing. This processor is responsible for
 * calculating the age of the import job data and removing it from the database, based on an maximum age
 * setting defined.
 */
public class ImportJobCleanupProcessorImpl implements ImportJobCleanupProcessor {

	/**
	 * The logger.
	 */
	private static final Logger LOG = Logger.getLogger(ImportJobCleanupProcessorImpl.class);

	private static final long MILLI = 1000;
	private static final long ONE_DAY_MILLIS = 24 * 60 * 60 * MILLI;

	private ImportNotificationDao importNotificationDao;

	private ImportJobStatusDao importJobStatusDao;

	private TimeService timeService;

	private StaleImportNotificationProcessor staleImportNotificationProcessor;

	private AssetRepository assetRepository;

	private SettingValueProvider<Integer> maximumImportJobAgeDaysProvider;

	private SettingValueProvider<Integer> staleImportJobStatusThresholdMinsProvider;

	@Override
	public int cleanupImportJobData() {
		final long startTime = System.currentTimeMillis();
		LOG.debug("Start cleanup import job quartz job at: " + new Date(startTime));

		int importJobsAffected = 0;

		final int importJobMaxAgeSeconds = getMaximumImportJobAgeDaysProvider().get();

		// get a list of all processed launch notifications (success or failure of the process is not applicable) 
		List<ImportNotification> notifications = 
			importNotificationDao.findByActionAndState(ImportAction.LAUNCH_IMPORT, ImportNotificationState.PROCESSED);

		for (ImportNotification notification : notifications) {
			importJobsAffected += processNotifications(importJobMaxAgeSeconds, notification);
		}

		// also delete old import job attempts that failed validation or were cancelled by the user before a launch
		// notification (clicking Cancel instead of Finish button in wizard), thus stuck in QUEUED_FOR_VALIDATION state
		List<ImportJobStatus> importJobStatusList = importJobStatusDao.findByState(ImportJobState.QUEUED_FOR_VALIDATION);

		for (ImportJobStatus importJobStatus : importJobStatusList) {
			importJobsAffected += processValidationStates(importJobMaxAgeSeconds, importJobStatus);
		}

		LOG.debug("Import jobs cleaned up: " + importJobsAffected);
		LOG.debug("Cleanup import job quartz job completed in (ms): " + (System.currentTimeMillis() - startTime));

		return importJobsAffected;
	}

	private int processValidationStates(final int importJobMaxAgeSeconds, final ImportJobStatus importJobStatus) {
		Date importTime = importJobStatus.getEndTime();
		if (importTime == null) {
			// let's not assume that the end time has always been set
			importTime = importJobStatus.getStartTime();
		}

		if (importTime == null) {
			throw new EpSystemException("No start or end time set for import job status with process ID: "
					+ importJobStatus.getProcessId());
		}

		final boolean maxAgeExceeded = isMaxAgeExceeded(importJobMaxAgeSeconds, importTime.getTime());

		int importJobsAffected = 0;

		if (maxAgeExceeded) {
			// bump up affected jobs counter
			importJobsAffected++;

			importJobStatusDao.remove(importJobStatus);
		}

		return importJobsAffected;
	}

	private int processNotifications(final int importJobMaxAgeSeconds, final ImportNotification notification) {
		int importJobsAffected = 0;

		// the import job has remained in the system too long if the
		// current time is past the maximum age of the notification
		boolean maxAgeExceeded = isMaxAgeExceeded(importJobMaxAgeSeconds, notification.getDateCreated().getTime());

		if (maxAgeExceeded) {
			// bump up affected jobs counter
			importJobsAffected++;

			// delete the import job process status data, cascading the deletes over it's bad rows and fault tables
			ImportJobStatus importJobStatus = importJobStatusDao.findByProcessId(notification.getProcessId());
			importJobStatusDao.remove(importJobStatus);

			// delete all launch notifications associated with this import job
			List<ImportNotification> importNotifications =
				importNotificationDao.findByProcessId(notification.getProcessId(), ImportAction.LAUNCH_IMPORT);
			for (ImportNotification importNotification : importNotifications) {
				importNotificationDao.remove(importNotification);
			}

			// delete all cancel notifications associated with this import job
			importNotifications = importNotificationDao.findByProcessId(notification.getProcessId(), ImportAction.CANCEL_IMPORT);
			for (ImportNotification importNotification : importNotifications) {
				importNotificationDao.remove(importNotification);
			}
		}

		return importJobsAffected;
	}

	private boolean isMaxAgeExceeded(final int importJobMaxAgeInSeconds, final long dateCreated) {
		long now = timeService.getCurrentTime().getTime();
		long maxAge = dateCreated + importJobMaxAgeInSeconds * MILLI;
		return now > maxAge;
	}

	/**
	 *
	 * @return the importNotificationDao
	 */
	public ImportNotificationDao getImportNotificationDao() {
		return importNotificationDao;
	}

	/**
	 *
	 * @param importNotificationDao the importNotificationDao to set
	 */
	public void setImportNotificationDao(final ImportNotificationDao importNotificationDao) {
		this.importNotificationDao = importNotificationDao;
	}

	/**
	 *
	 * @return the importJobStatusDao
	 */
	protected ImportJobStatusDao getImportJobStatusDao() {
		return importJobStatusDao;
	}

	/**
	 *
	 * @param importJobStatusDao the importJobStatusDao to set
	 */
	public void setImportJobStatusDao(final ImportJobStatusDao importJobStatusDao) {
		this.importJobStatusDao = importJobStatusDao;
	}

	/**
	 * @return the timeService
	 */
	public TimeService getTimeService() {
		return timeService;
	}

	/**
	 * @param timeService the timeService to set
	 */
	public void setTimeService(final TimeService timeService) {
		this.timeService = timeService;
	}

	@Override
	public void processStaleImportJobs() {
		final long startTime = System.currentTimeMillis();
		LOG.debug("Start process stale import job quartz job at: " + new Date(startTime));
		LOG.debug("Starting up the stale import job clean up process.");

		// check whether a processor is stuck
		List<ImportNotification> jobsInProcess = getImportNotificationDao().findByActionAndState(
				ImportAction.LAUNCH_IMPORT, ImportNotificationState.IN_PROCESS);

		for (ImportNotification importNotification : jobsInProcess) {
			ImportJobStatus status = getImportJobStatusDao().findByProcessId(importNotification.getProcessId());
			// check whether the last updated date is earlier than the limit we have, based on current time
			Date cutOutTime = DateUtils.addMinutes(getTimeService().getCurrentTime(), -getStaleImportJobTimeoutInMinutes());
			if (status.getLastModifiedDate().compareTo(cutOutTime) < 0) {
				LOG.debug("Found a stale import job. Its import notification: " + importNotification);
				try {
					getStaleImportNotificationProcessor().process(importNotification);
				} catch (Exception exc) {
					LOG.error("Could not process a stale import notification.", exc);
				}
			}
		}

		LOG.debug("Process stale import job quartz job completed in (ms): " + (System.currentTimeMillis() - startTime));
	}

	/**
	 * Sets the stale import notification processor.
	 *
	 * @param staleImportNotificationProcessor the processor
	 */
	public void setStaleImportNotificationProcessor(final StaleImportNotificationProcessor staleImportNotificationProcessor) {
		this.staleImportNotificationProcessor = staleImportNotificationProcessor;
	}

	/**
	 * Gets the timeout.
	 *
	 * @return stale import job timeout in minutes
	 */
	protected int getStaleImportJobTimeoutInMinutes() {
		return getStaleImportJobStatusThresholdMinsProvider().get();
	}

	/**
	 *
	 * @return the staleImportNotificationProcessor
	 */
	protected StaleImportNotificationProcessor getStaleImportNotificationProcessor() {
		return staleImportNotificationProcessor;
	}

	@Override
	public int cleanupStaleImportCSVFiles() {
		LOG.debug("Running job for cleaning stale CSV import files ....");

		//the job runs every day at midnight

		Date yesterday = new Date(Calendar.getInstance().getTimeInMillis() - ONE_DAY_MILLIS);

		String importAssetPath = getAssetRepository().getImportAssetPath();
		File importAssetDir = new File(importAssetPath);

		Collection<File> csvFiles = FileUtils.listFiles(importAssetDir, new String[]{"csv"}, true);

		int csvFilesCount = csvFiles.size();
		int numOfDeletedFiles = 0;

		boolean isFileDeleted;
		for (File csvFile : csvFiles) {
			//delete CSV files 1 or more days older
			if (FileUtils.isFileOlder(csvFile, yesterday)) {
				if (csvFile.getParentFile().equals(importAssetDir)) {
					isFileDeleted = FileUtils.deleteQuietly(csvFile);
				} else {
					isFileDeleted = FileUtils.deleteQuietly(csvFile.getParentFile());
				}

				if (isFileDeleted) {
					numOfDeletedFiles++;
				} else {
					LOG.warn("File [" + csvFile.getAbsolutePath() + "] couldn't be deleted");
				}
			}
		}

		if (numOfDeletedFiles > 0) {
			csvFiles.clear();

			LOG.info("Deleted [" + numOfDeletedFiles + "] of [" + csvFilesCount + "] found CSV files.");
		}

		return numOfDeletedFiles;
	}

	/**
	 * @return the assetRepository
	 */
	protected AssetRepository getAssetRepository() {
		return assetRepository;
	}

	/**
	 * @param assetRepository the assetRepository to set
	 */
	public void setAssetRepository(final AssetRepository assetRepository) {
		this.assetRepository = assetRepository;
	}

	public void setMaximumImportJobAgeDaysProvider(final SettingValueProvider<Integer> maximumImportJobAgeDaysProvider) {
		this.maximumImportJobAgeDaysProvider = maximumImportJobAgeDaysProvider;
	}

	protected SettingValueProvider<Integer> getMaximumImportJobAgeDaysProvider() {
		return maximumImportJobAgeDaysProvider;
	}

	public void setStaleImportJobStatusThresholdMinsProvider(final SettingValueProvider<Integer> staleImportJobStatusThresholdMinsProvider) {
		this.staleImportJobStatusThresholdMinsProvider = staleImportJobStatusThresholdMinsProvider;
	}

	protected SettingValueProvider<Integer> getStaleImportJobStatusThresholdMinsProvider() {
		return staleImportJobStatusThresholdMinsProvider;
	}

}
