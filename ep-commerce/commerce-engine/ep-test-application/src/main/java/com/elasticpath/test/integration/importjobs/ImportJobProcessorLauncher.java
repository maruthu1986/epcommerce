/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.test.integration.importjobs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.domain.dataimport.ImportJobStatus;
import com.elasticpath.service.dataimport.ImportJobProcessor;
import com.elasticpath.service.dataimport.ImportService;

/**
 * A utility class for launching an import job processor.
 */
public class ImportJobProcessorLauncher {
	protected static final Logger LOG = Logger.getLogger(ImportJobProcessorLauncher.class);


	private static final int MAX_THREADS_COUNT = 2;

	private static final int TWO_MINUTES = 120;
	private static final int REFRESH_SLEEP_TIME = 100;

	private final int timeoutInSeconds;
	private final ImportJobProcessor importJobProcessor;
	private final ImportService importService;
	private ExecutorService executor;
	private Thread importJobProcessThread;

	/**
	 * Constructs a new launcher with default timeout of 2 minutes.
	 * 
	 * @param beanFactory the bean factory to use
	 */
	public ImportJobProcessorLauncher(final BeanFactory beanFactory) {
		this(beanFactory, TWO_MINUTES);
	}
	
	/**
	 * Constructor.
	 * 
	 * @param beanFactory the bean factory
	 * @param timeoutInSeconds the timeout in seconds
	 */
	public ImportJobProcessorLauncher(final BeanFactory beanFactory, final int timeoutInSeconds) {
		this.timeoutInSeconds = timeoutInSeconds;
		importJobProcessor = beanFactory.getBean("importJobProcessor");
		importService = beanFactory.getBean("importService");
		// max 2 threads expected
		executor = Executors.newFixedThreadPool(MAX_THREADS_COUNT);
	}
	
	/**
	 * Launches the import job processor with a timeout for the import set to 2 minutes.
	 * 
	 * @param status the import job status right after
	 * @return the last import job status
	 */
	public ImportJobStatus launchAndWaitToFinish(final ImportJobStatus status) {
		Collection<Callable<Object>> tasks = new ArrayList<>();
		tasks.add(new ImportJobRunner());
		tasks.add(new ImportJobMonitor(status));
		try {
			// invoke all tasks at once. If the timeout is reached 
			// the task(s) blocking the thread will be cancelled.
			List<Future<Object>> futures = executor.invokeAll(tasks, timeoutInSeconds, TimeUnit.SECONDS);
			long cancelledTaskCount = futures.stream().filter(f -> f.isCancelled()).count();
			if (cancelledTaskCount > 0) {
				LOG.error("Number of cancelled tasks" + cancelledTaskCount);
			}

		} catch (InterruptedException exc) {
			//log the exception!
			LOG.error("Exception in import task: ", exc);
		} finally {
			close();
		}

		return importService.getImportJobStatus(status.getProcessId());
	}
	
	/**
	 * Launches a job in a thread and returns.
	 */
	public void launch() {
		importJobProcessThread = new Thread(new ImportJobRunner());
		importJobProcessThread.start();
	}
	
	/**
	 * The import job runner launching an import job.
	 */
	private class ImportJobRunner implements Runnable, Callable<Object> {
		
		@Override
		public Object call() throws Exception {
			importJobProcessor.launchImportJob();
			return null;
		}
		
		@Override
		public void run() {
			try {
				call();
			} catch (Exception exception) {
				// interested
				LOG.error("Exception: ", exception);
			}	
		}
	}

	/**
	 * A monitor to wait for an import job to finish.
	 */
	private class ImportJobMonitor implements Callable<Object> {
		private final ImportJobStatus status;
		
		ImportJobMonitor(final ImportJobStatus status) {
			this.status = status;
		}
		
		@Override
		public Object call() {
			ImportJobStatus freshStatus = status;
			while (!freshStatus.isFinished()) {
				freshStatus = importService.getImportJobStatus(freshStatus.getProcessId());
				try {
					Thread.sleep(REFRESH_SLEEP_TIME);
				} catch (InterruptedException e) {
					//do nothing, for now.
				}
			}
			return null;
		}
	}

	/**
	 * Interrupts the import process.
	 * 
	 * @return true if the thread was stopped
	 */
	public boolean interruptProcess() {
		// uses the stop method in order to make sure that the thread is killed
		// using interrupt does not do the same job
		importJobProcessThread.stop();
		
		return importJobProcessThread.isAlive();
	}

	/**
	 * Clean up the threads/executors so appropriate garbage collection can be done.
	 */
	public void close() {
		if (executor != null) {
			executor.shutdown();
			executor = null;
		}
		if (importJobProcessThread != null && importJobProcessThread.isAlive()) {
			importJobProcessThread.stop();
			importJobProcessThread = null;
		}
	}
}
