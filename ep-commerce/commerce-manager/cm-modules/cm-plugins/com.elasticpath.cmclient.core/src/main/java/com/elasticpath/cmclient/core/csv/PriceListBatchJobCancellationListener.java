/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.csv;

import java.util.List;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.listener.ItemListenerSupport;

import com.elasticpath.common.dto.pricing.BaseAmountDTO;

/**
 * Allow job cancellation.
 */
public class PriceListBatchJobCancellationListener
	extends ItemListenerSupport<BaseAmountDTO, BaseAmountDTO>
	implements StepExecutionListener {

	private StepExecution stepExecution;

	@Override
	public void onReadError(final Exception exception) {
		// Notify the job that we wish to exit.
		stepExecution.setTerminateOnly();
	}

	@Override
	public void onWriteError(final Exception exception, final List<? extends BaseAmountDTO> item) {
		// Notify the job that we wish to exit.
		stepExecution.setTerminateOnly();
	}

	@Override
	public void beforeStep(final StepExecution stepExecution) {
		this.stepExecution = stepExecution;
	}

	@Override
	public ExitStatus afterStep(final StepExecution stepExecution) {
		if (stepExecution.isTerminateOnly()) {
			return ExitStatus.FAILED;
		} else {
			return ExitStatus.COMPLETED;
		}
	}
}
