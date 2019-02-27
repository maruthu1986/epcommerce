/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.csv;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.file.FlatFileHeaderCallback;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.core.io.Resource;

import com.elasticpath.common.dto.pricing.BaseAmountDTO;

/**
 * A flat file writer with a configurable output (file) location.
 */
public class ConfigurableFlatFileItemWriter extends FlatFileItemWriter<BaseAmountDTO> implements StepExecutionListener {

	private static final Logger LOG = Logger.getLogger(ConfigurableFlatFileItemWriter.class);

	// Clone the resource (also stored in FlatFileItemWriter) here so that we can see it for validation and logging.
	private Resource fileResource;
	private String priceListName;
	private String priceListCurrency;

	@Override
	public void beforeStep(final StepExecution stepExecution) {
		if (fileResource == null) {
			throw new IllegalStateException("Batch pipeline did not provide required [resource] parameter"); //$NON-NLS-1$
		}

		if (priceListName == null) {
			throw new IllegalStateException("Batch pipeline did not provide required [priceListName] parameter"); //$NON-NLS-1$
		}

		if (priceListCurrency == null) {
			throw new IllegalStateException("Batch pipeline did not provide required [priceListCurrency] parameter"); //$NON-NLS-1$
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug(String.format("Writer configured with target file [%s], price list [%s], currency [%s]", //$NON-NLS-1$
					getFileName(), priceListName, priceListCurrency));
		}

		setHeaderCallback(new FlatFileHeaderCallback() {
			@Override
			public void writeHeader(final Writer writer) throws IOException {
				String priceListId = priceListName + "_" + priceListCurrency; //$NON-NLS-1$
				String listPriceHeaderName =  "listPrice_" + priceListId; //$NON-NLS-1$
				String salePriceHeaderName = "salePrice_" + priceListId; //$NON-NLS-1$
				String headerString
					= "\"type\",\"productName\",\"productCode\",\"skuCode\",\"skuConfiguration\",\"qty\",\"%s\",\"%s\""; //$NON-NLS-1$
				writer.write(String.format(headerString, listPriceHeaderName, salePriceHeaderName));
			}
		});
	}

	@Override
	public ExitStatus afterStep(final StepExecution stepExecution) {
		return ExitStatus.COMPLETED;
	}

	private String getFileName() {
		String fileName;
		try {
			fileName = getResource().getFile().getAbsolutePath();
		} catch (IOException e) {
			fileName = null;
		}
		return fileName;
	}

	protected Resource getResource() {
		return fileResource;
	}

	@Override
	public void setResource(final Resource resource) {
		super.setResource(resource);
		this.fileResource = resource;
	}

	@Override
	public void write(final List<? extends BaseAmountDTO> items) throws Exception {
		LOG.trace(String.format("Writing [%s] items...", items.size())); //$NON-NLS-1$
		super.write(items);
		LOG.trace(String.format("Completed writing [%s] items", items.size())); //$NON-NLS-1$
	}

	public void setPriceListName(final String priceListName) {
		this.priceListName = priceListName;
	}

	public void setPriceListCurrency(final String priceListCurrency) {
		this.priceListCurrency = priceListCurrency;
	}
}
