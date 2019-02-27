/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.csv;

import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.LocaleUtils;
import org.apache.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.database.AbstractPagingItemReader;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.common.dto.pricing.BaseAmountDTO;
import com.elasticpath.common.pricing.service.BaseAmountFilterExt;
import com.elasticpath.common.pricing.service.PriceListService;
import com.elasticpath.commons.constants.ContextIdNames;

/**
 * Adapt PriceListService to the paging ItemReader interface.
 */
public class ChunkedBaseAmountDTOItemReader extends AbstractPagingItemReader<BaseAmountDTO> implements StepExecutionListener {

	private static final Logger LOG = Logger.getLogger(ChunkedBaseAmountDTOItemReader.class);
	private PriceListService priceListService;
	private BaseAmountFilterExt filter;
	private String priceListGuid;
	private Locale pricingLocale;

	public void setPriceListService(final PriceListService priceListService) {
		this.priceListService = priceListService;
	}

	@Override
	public void beforeStep(final StepExecution stepExecution) {
		if (priceListGuid == null) {
			throw new IllegalStateException("Batch pipeline did not provide required price list guid parameter"); //$NON-NLS-1$
		}

		if (pricingLocale == null) {
			throw new IllegalArgumentException("Batch pipeline did not provide required pricing locale parameter"); //$NON-NLS-1$
		}

		filter =  ServiceLocator.getService(ContextIdNames.BASE_AMOUNT_FILTER_EXT);
		// TODO: Locale is not required by the filter or query.  It's only included for legacy reasons...
		filter.setLocale(pricingLocale);
		filter.setPriceListDescriptorGuid(priceListGuid);
	}

	@Override
	public ExitStatus afterStep(final StepExecution stepExecution) {
		return ExitStatus.COMPLETED;
	}

	@Override
	protected void doReadPage() {
		int currentPageIndex = getPage();
		int pageSize = getPageSize();
		int startIndex = currentPageIndex * pageSize;
		filter.setStartIndex(startIndex);
		filter.setLimit(pageSize);
		LOG.debug(String.format("Reading chunk [%s] to [%s]...", startIndex, startIndex + pageSize - 1)); //$NON-NLS-1$
		results = (List<BaseAmountDTO>) priceListService.getBaseAmountsExt(filter);
		LOG.debug(String.format("Completed reading chunk [%s] to [%s]", startIndex, startIndex + pageSize - 1)); //$NON-NLS-1$
	}

	@Override
	protected void doJumpToPage(final int itemIndex) {
		// no-op.  default index calcs are correct and doReadPage will work afterwards.
	}

	public void setPriceListGuid(final String priceListGuid) {
		this.priceListGuid = priceListGuid;
	}

	/**
	 * @param pricingLocaleString The locale string.
	 */
	public void setPricingLocale(final String pricingLocaleString) {
		try {
			this.pricingLocale = LocaleUtils.toLocale(pricingLocaleString);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(
					String.format("Batch pipeline had invalid value [%s] for required pricing locale parameter", //$NON-NLS-1$
							pricingLocaleString), e);
		}
	}
}
