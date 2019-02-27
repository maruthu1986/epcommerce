/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.pricelistmanager.actions;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.csv.PriceListCsvExportNotificationListener;
import com.elasticpath.cmclient.core.csv.PriceListCsvExportNotificationListenerComposite;
import com.elasticpath.cmclient.core.util.FileSystemUtil;
import com.elasticpath.cmclient.pricelistmanager.controller.impl.PriceListEditorControllerImpl;
import com.elasticpath.common.dto.pricing.PriceListDescriptorDTO;

/**
 * Exports all of the pricing information for a single price list.
 * This implementation writes data out in a chunked manner so that
 * it can handle large price lists.
 */
public class CsvChunkedPriceListExportService implements PriceListExportService {

	private static final String JOB_EXECUTION_DATE = "com.elasticpath.etl.batch.csv.price.execution.date"; //$NON-NLS-1$
	private static final String PRICE_LIST_GUID = "com.elasticpath.etl.batch.csv.price.list.guid"; //$NON-NLS-1$
	private static final String PRICE_LIST_NAME = "com.elasticpath.etl.batch.csv.price.list.name"; //$NON-NLS-1$
	private static final String PRICE_LIST_CURRENCY_CODE = "com.elasticpath.etl.batch.csv.price.list.currency"; //$NON-NLS-1$
	private static final String PRICING_LOCALE = "com.elasticpath.etl.batch.csv.pricing.locale"; //$NON-NLS-1$
	private static final String PRICE_LIST_OUTPUT_FILE = "com.elasticpath.etl.batch.csv.price.list.output.file"; //$NON-NLS-1$
	private static final Logger LOG = Logger.getLogger(CsvChunkedPriceListExportService.class);
	private static final String EXTENSION_CSV = ".csv"; //$NON-NLS-1$

	private PriceListEditorControllerImpl plController;
	private final PriceListCsvExportNotificationListenerComposite listener = new PriceListCsvExportNotificationListenerComposite();
	private String csvFileName;

	@Override
	public void exportPrices(final String priceListGuid) {
		listener.notifyStart();

		// TODO: Replace the controller with simpler services.
		plController = new PriceListEditorControllerImpl(priceListGuid);
		listener.notifyLoadingData();
		PriceListDescriptorDTO priceListDescriptor = getPriceListDescriptor();

		JobLauncher jobLauncher = ServiceLocator.getService("jobLauncher"); //$NON-NLS-1$
		Job priceListCsvExportJob = ServiceLocator.getService("priceListCsvExportJob"); //$NON-NLS-1$
		Locale pricingLocale = getPricingLocale();
		String priceListName = priceListDescriptor.getName();
		String priceListCurrencyCode = priceListDescriptor.getCurrencyCode();

		LOG.debug(String.format("Exporting price list [%s] to csv file [%s]...", priceListName, getFileName())); //$NON-NLS-1$
		JobExecution jobExecution = null;
		try {
			listener.notifyTransformingData();
			listener.notifyExportingData();
			jobExecution = jobLauncher.run(priceListCsvExportJob,
					new JobParametersBuilder()
						.addString(JOB_EXECUTION_DATE, Long.toString(new Date().getTime()))
						.addString(PRICE_LIST_GUID, priceListGuid)
						.addString(PRICE_LIST_NAME, priceListName)
						.addString(PRICE_LIST_CURRENCY_CODE, priceListCurrencyCode)
						.addString(PRICING_LOCALE, pricingLocale.toString())
						.addString(PRICE_LIST_OUTPUT_FILE, getFileName())
						.toJobParameters());
			LOG.debug(String.format("Exported price list [%s] to csv file [%s] in [%s] ms", //$NON-NLS-1$
					priceListName, getFileName(), jobExecution.getEndTime().getTime() - jobExecution.getStartTime().getTime()));
		} catch (JobExecutionException e) {
			// Error reporting is handled in the parent action....
			listener.notifyError(e);
		} finally {
			if (jobExecution != null && !ExitStatus.COMPLETED.equals(jobExecution.getExitStatus())) {
				listener.notifyError(new JobExecutionException(
					String.format("Error occurred while exporting price list [%s] to csv file [%s]: Exit status [%s]", //$NON-NLS-1$
						priceListName, getFileName(), jobExecution.getExitStatus())));
				List<Throwable> failureExceptions = jobExecution.getFailureExceptions();
				for (Throwable error : failureExceptions) {
					listener.notifyError((Exception) error);
				}
			}

			listener.notifyDone();
			LOG.debug(String.format("Done exporting price list [%s] to csv [%s]", priceListName, getFileName())); //$NON-NLS-1$
		}
	}

	protected PriceListDescriptorDTO getPriceListDescriptor() {
		return plController.getPriceListDescriptor();
	}

	/**
	 * @return the locale of the pricing
	 */
	protected Locale getPricingLocale() {
		// TODO: Bug - losing the country here?
		// NOTE: The locale is actually not used by the CSV export whatsoever.
		// See PriceListServiceImpl.getBaseAmountsExt(...).
		return new Locale(plController.getCurrentLocale().getLanguage());
	}

	// TODO: Clean up these accessors.
	@Override
	public String getFileName() {
		if (csvFileName == null) {
			csvFileName = getDefaultCsvFileName(getPriceListDescriptor());
		}
		return csvFileName;
	}

	public void setOutputFileName(final String csvFileName) {
		this.csvFileName = csvFileName;
	}

	private String getDefaultCsvFileName(final PriceListDescriptorDTO priceListDescriptor) {

		StringBuilder stringBuilder = new StringBuilder(FileSystemUtil.getTempDirectory()).append(File.separator);
		String rawfileName = priceListDescriptor.getName().replace(" ", "_").replace("&", "_"); //$NON-NLS-1$

		stringBuilder.append(rawfileName)
				.append(" - ") //$NON-NLS-1$
				.append(UUID.randomUUID().toString())
				.append(EXTENSION_CSV);

		csvFileName = stringBuilder.toString();

		File csvFile = new File(csvFileName);
		return csvFile.getAbsolutePath();
	}

	@Override
	public void addListenter(final PriceListCsvExportNotificationListener listener) {
		this.listener.addListener(listener);
	}

	@Override
	public void removeListener(final PriceListCsvExportNotificationListener listener) {
		this.listener.removeListener(listener);
	}
}
