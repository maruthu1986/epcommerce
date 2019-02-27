/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.domain.dataimport.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.time.DateUtils;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.commons.util.AssetRepository;
import com.elasticpath.domain.dataimport.ImportAction;
import com.elasticpath.domain.dataimport.ImportJob;
import com.elasticpath.domain.dataimport.ImportJobState;
import com.elasticpath.domain.dataimport.ImportJobStatus;
import com.elasticpath.domain.dataimport.ImportNotification;
import com.elasticpath.domain.dataimport.ImportNotificationState;
import com.elasticpath.service.dataimport.StaleImportNotificationProcessor;
import com.elasticpath.service.dataimport.dao.ImportJobStatusDao;
import com.elasticpath.service.dataimport.dao.ImportNotificationDao;
import com.elasticpath.service.dataimport.impl.ImportJobCleanupProcessorImpl;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.settings.MalformedSettingValueException;
import com.elasticpath.settings.provider.SettingValueProvider;
import com.elasticpath.settings.test.support.SimpleSettingValueProvider;

/**
 * ImportJobCleanupProcessorImpl unit test class.
 */
public class ImportJobCleanupProcessorImplTest {

	private static final long ONE_MINUTE_MILLIS = 60 * 1000;
	private static final long ONE_DAY_MILLIS = 24 * 60 * ONE_MINUTE_MILLIS;
	private static final String CSV_FILE_SHOULD_NOT_EXIST_MSG = "CSV file should not exist";

	private ImportJobCleanupProcessorImpl importJobCleanupProcessor;

	@Rule
	public final JUnitRuleMockery mockery = new JUnitRuleMockery();

	private final ImportNotificationDao importNotificationDao = mockery.mock(ImportNotificationDao.class);
	private final ImportJobStatusDao importJobStatusDao = mockery.mock(ImportJobStatusDao.class);
	private final TimeService timeService = mockery.mock(TimeService.class);
	private final AssetRepository assetRepository = mockery.mock(AssetRepository.class);

	private File importAssetDir;

	/**
	 * Prepare for tests.
	 */
	@Before
	public void setUp() {
		importJobCleanupProcessor = new ImportJobCleanupProcessorImpl();
		importJobCleanupProcessor.setImportJobStatusDao(importJobStatusDao);
		importJobCleanupProcessor.setImportNotificationDao(importNotificationDao);
		importJobCleanupProcessor.setTimeService(timeService);
		importJobCleanupProcessor.setAssetRepository(assetRepository);

		importAssetDir = getImportAssetFolder();
	}

	/**
	 * Cleanup after tests.
	 */
	@After
	public void tearDown() {
		FileUtils.deleteQuietly(importAssetDir);
	}
	
	/**
	 * 
	 */
	@Test(expected = EpSystemException.class)
	public void testCleanupImportJobWithNullMaxAgeSetting() {
		ImportNotification launchImportNotification = getLaunchImportNotification(new Date(), "1");
		final List<ImportNotification> importNotifications = new ArrayList<>();
		importNotifications.add(launchImportNotification);

		final SettingValueProvider<Integer> exceptionThrowingProvider = new SettingValueProvider<Integer>() {
			@Override
			public Integer get() {
				throw new MalformedSettingValueException("Null setting values can't be converted to integers");
			}

			@Override
			public Integer get(final String context) {
				throw new MalformedSettingValueException("Null setting values can't be converted to integers");
			}
		};

		importJobCleanupProcessor.setMaximumImportJobAgeDaysProvider(exceptionThrowingProvider);

		importJobCleanupProcessor.cleanupImportJobData();
		fail("An EpSystemException must be thrown if no no max age setting is found.");
	}
	
	/**
	 * Create a very young notification that is not expired and does not get it's data deleted.
	 */
	@Test
	public void testCleanupImportJobWithNonExpiredNotification() {
		final int maximumImportJobAgeDaysProvider = 100000000;
		importJobCleanupProcessor.setMaximumImportJobAgeDaysProvider(new SimpleSettingValueProvider<>(maximumImportJobAgeDaysProvider));

		ImportNotification launchImportNotification = getLaunchImportNotification(new Date(), "processId-1");
		final List<ImportNotification> importNotifications = new ArrayList<>();
		importNotifications.add(launchImportNotification);
		
		final List<ImportJobState> importJobStates = new ArrayList<>();

		mockery.checking(new Expectations() { {
			oneOf(importNotificationDao).findByActionAndState(with(ImportAction.LAUNCH_IMPORT), with(ImportNotificationState.PROCESSED));
			will(returnValue(importNotifications));
			oneOf(timeService).getCurrentTime();
			will(returnValue(new Date()));
			oneOf(importJobStatusDao).findByState(with(ImportJobState.QUEUED_FOR_VALIDATION));
			will(returnValue(importJobStates));
		} });
		
		int importJobsAffected = importJobCleanupProcessor.cleanupImportJobData();
		assertEquals("No import job should be affected with no notifications found.", 0, importJobsAffected);
	}

	/**
	 * Create a very old notification to force expiration, and thus a cleanup of it's data. 
	 */
	@Test
	public void testCleanupImportJobWithExpiredNotification() {
		importJobCleanupProcessor.setMaximumImportJobAgeDaysProvider(new SimpleSettingValueProvider<>(1));
		
		final String processId = "processId-" + System.currentTimeMillis();
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, -1);
		
		final ImportNotification launchImportNotification = getLaunchImportNotification(cal.getTime(), processId);
		final List<ImportNotification> importNotifications = new ArrayList<>();
		importNotifications.add(launchImportNotification);
		
		final ImportJobStatus importJobStatus = new ImportJobStatusImpl();
		
		final ImportNotification cancelImportNotification = getCancelImportNotification();
		final List<ImportNotification> cancelImportNotifications = new ArrayList<>();
		cancelImportNotifications.add(cancelImportNotification);
		
		final List<ImportJobState> importJobStates = new ArrayList<>();
		
		mockery.checking(new Expectations() { {
			oneOf(importNotificationDao).findByActionAndState(with(ImportAction.LAUNCH_IMPORT), with(ImportNotificationState.PROCESSED));
			will(returnValue(importNotifications));
			oneOf(timeService).getCurrentTime();
			will(returnValue(new Date()));
			oneOf(importJobStatusDao).findByProcessId(with(processId));
			will(returnValue(importJobStatus));
			oneOf(importJobStatusDao).remove(importJobStatus);
			oneOf(importNotificationDao).findByProcessId(with(processId), with(ImportAction.LAUNCH_IMPORT));
			will(returnValue(importNotifications));
			oneOf(importNotificationDao).remove(launchImportNotification);
			oneOf(importNotificationDao).findByProcessId(with(processId), with(ImportAction.CANCEL_IMPORT));
			will(returnValue(cancelImportNotifications));
			oneOf(importNotificationDao).remove(cancelImportNotification);
			oneOf(importJobStatusDao).findByState(with(ImportJobState.QUEUED_FOR_VALIDATION));
			will(returnValue(importJobStates));
		} });
		
		int importJobsAffected = importJobCleanupProcessor.cleanupImportJobData();
		assertEquals("Exactly 1 import job should be affected.", 1, importJobsAffected);
	}
	
	/**
	 * Creates an launch import notification.
	 * 
	 * @param dateCreated the date to use as the creation
	 * @param processId the processId to assign to the new import notification
	 * @return an import notification
	 */
	protected ImportNotification getLaunchImportNotification(final Date dateCreated, final String processId) {
		ImportJob importJob = new ImportJobImpl();
		importJob.setCsvFileName("csv-filename.csv");
		
		ImportNotification notification = new ImportNotificationImpl() {
			private static final long serialVersionUID = -5086517190878593108L;

			@Override
			public Date getDateCreated() {
				return dateCreated;
			}
		};
		notification.setAction(ImportAction.LAUNCH_IMPORT);
		notification.setProcessId(processId);
		notification.setImportJob(importJob);
		
		return notification;
	}

	/**
	 * Creates an cancel import notification.
	 * 
	 * @return an import notification
	 */
	protected ImportNotification getCancelImportNotification() {
		ImportJob importJob = new ImportJobImpl();
		importJob.setCsvFileName("csv-filename.csv");
		
		ImportNotification notification = new ImportNotificationImpl();
		notification.setAction(ImportAction.CANCEL_IMPORT);
		notification.setImportJob(importJob);
		
		return notification;
	}

	/**
	 * Tests that having an import job status updated after the cut out time of 1 minute (defined by the setting)
	 * does not trigger a job for clean up.
	 */
	@Test
	public void testProcessStaleImportJob() {
		importJobCleanupProcessor.setStaleImportJobStatusThresholdMinsProvider(new SimpleSettingValueProvider<>(1));

		final Date currentTime = new Date();
		
		final String processId = "processId1";
		final ImportNotification launchImportNotification = getLaunchImportNotification(new Date(), processId);

		final ImportJobStatus importJobStatus = mockery.mock(ImportJobStatus.class);

		mockery.checking(new Expectations() { {
			oneOf(importNotificationDao).findByActionAndState(with(ImportAction.LAUNCH_IMPORT), with(ImportNotificationState.IN_PROCESS));
			will(returnValue(Arrays.asList(launchImportNotification)));
			oneOf(importJobStatusDao).findByProcessId(processId);
			will(returnValue(importJobStatus));
			oneOf(timeService).getCurrentTime();
			will(returnValue(currentTime));

			oneOf(importJobStatus).getLastModifiedDate();
			// the last modified date of a status is exactly the current time which is later
			// than one minute in the past (defined by the setting).
			will(returnValue(currentTime));
		} });

		importJobCleanupProcessor.processStaleImportJobs();
	}

	/**
	 * Tests that having an import job status updated after the cut out time of 1 minute (defined by the setting)
	 * does not trigger a job for clean up.
	 */
	@Test
	public void testProcessStaleImportJobTimeoutElapsed() {
		final StaleImportNotificationProcessor staleImportNotificationProcessor = 
			mockery.mock(StaleImportNotificationProcessor.class);
		
		importJobCleanupProcessor.setStaleImportNotificationProcessor(staleImportNotificationProcessor);
		importJobCleanupProcessor.setStaleImportJobStatusThresholdMinsProvider(new SimpleSettingValueProvider<>(1));

		// set the current time one minute in the past
		final Date currentTime = new Date();
		final ImportNotification launchImportNotification = getLaunchImportNotification(new Date(), "processId1");

		final ImportJobStatus importJobStatus = mockery.mock(ImportJobStatus.class);

		mockery.checking(new Expectations() { {
			oneOf(importNotificationDao).findByActionAndState(with(ImportAction.LAUNCH_IMPORT), with(ImportNotificationState.IN_PROCESS));
			will(returnValue(Arrays.asList(launchImportNotification)));
			oneOf(importJobStatusDao).findByProcessId("processId1");
			will(returnValue(importJobStatus));
			oneOf(timeService).getCurrentTime();
			will(returnValue(currentTime));

			oneOf(importJobStatus).getLastModifiedDate();
			// the last modified date of a status is 4 minutes before the calculated timeout (currentTime - 1)
			final int fiveMinutesBeforeNow = -5;
			will(returnValue(DateUtils.addMinutes(new Date(), fiveMinutesBeforeNow)));

			oneOf(staleImportNotificationProcessor).process(launchImportNotification);
		} });

		importJobCleanupProcessor.processStaleImportJobs();
	}

	@Test
	public void shouldNotRemoveNonCSVFiles() throws Exception {

		mockery.checking(new Expectations() { {
			oneOf(assetRepository).getImportAssetPath();
			will(returnValue(importAssetDir.getAbsolutePath()));
		}});

		File nonCsvFile = File.createTempFile("nonCsv", ".xls", importAssetDir);
		nonCsvFile.deleteOnExit();
		nonCsvFile.setLastModified(Calendar.getInstance().getTimeInMillis() - ONE_DAY_MILLIS - ONE_MINUTE_MILLIS);

		int actualNumberOfDeletedFiles = importJobCleanupProcessor.cleanupStaleImportCSVFiles();

		assertEquals("Non-CSV files must not be deleted", 0, actualNumberOfDeletedFiles);
		assertTrue("Non-CSV file must exist", nonCsvFile.exists());
	}

	@Test
	public void shouldRemoveCSVFilesOlderThanOneDay() throws Exception {

		mockery.checking(new Expectations() { {
				oneOf(assetRepository).getImportAssetPath();
				will(returnValue(importAssetDir.getAbsolutePath()));
		}});

		File csvFile1 = createDeletableTempFile(importAssetDir);
		File csvFile2 = createDeletableTempFile(importAssetDir);

		int actualNumberOfDeletedCSVFiles = importJobCleanupProcessor.cleanupStaleImportCSVFiles();

		assertEquals("All old CSV files must be deleted", 2, actualNumberOfDeletedCSVFiles);
		assertFalse(CSV_FILE_SHOULD_NOT_EXIST_MSG, csvFile1.exists());
		assertFalse(CSV_FILE_SHOULD_NOT_EXIST_MSG, csvFile2.exists());
	}

	//CM uploads files under ASSET_IMPORT_FOLDER/fileupload_XXXXXXXXXX dir
	@Test
	public void shouldRemoveCSVFilesOlderThanOneDayFromSubFolder() throws Exception {

		File assetDirWithSubFolder = new File(importAssetDir, "fileupload");
		assetDirWithSubFolder.mkdirs();
		assetDirWithSubFolder.deleteOnExit();

		mockery.checking(new Expectations() { {
			oneOf(assetRepository).getImportAssetPath();
			will(returnValue(importAssetDir.getAbsolutePath()));
		}});

		File csvFile1 = createDeletableTempFile(assetDirWithSubFolder);

		int actualNumberOfDeletedCSVFiles = importJobCleanupProcessor.cleanupStaleImportCSVFiles();

		assertEquals("All old CSV files must be deleted", 1, actualNumberOfDeletedCSVFiles);
		assertFalse(CSV_FILE_SHOULD_NOT_EXIST_MSG, csvFile1.exists());
	}

	private File createDeletableTempFile(final File importAssetDir) throws Exception {
		File deletableTempFile = File.createTempFile("test_", ".csv", importAssetDir);
		deletableTempFile.deleteOnExit();
		//ensure that file is a bit older than 1 day so the test has enough time to execute
		deletableTempFile.setLastModified(Calendar.getInstance().getTimeInMillis() - ONE_DAY_MILLIS - ONE_MINUTE_MILLIS);

		return deletableTempFile;
	}

	private File getImportAssetFolder() {
		File importAssetFolder = new File(getClass().getClassLoader().getResource(".").getPath(), "csvimport");
		importAssetFolder.mkdirs();

		return importAssetFolder;
	}

}
