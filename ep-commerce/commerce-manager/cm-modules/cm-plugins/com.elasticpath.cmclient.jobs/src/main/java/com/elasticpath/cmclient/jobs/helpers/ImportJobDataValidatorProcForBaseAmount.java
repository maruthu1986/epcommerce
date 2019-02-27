/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
/**
 * 
 */
package com.elasticpath.cmclient.jobs.helpers;

import java.lang.reflect.InvocationTargetException;
import java.util.Currency;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.osgi.util.NLS;

import com.elasticpath.cmclient.core.LoginManager;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.jobs.JobsMessages;
import com.elasticpath.common.dto.pricing.PriceListDescriptorDTO;
import com.elasticpath.common.pricing.service.PriceListService;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.dataimport.ImportJobRequest;
import com.elasticpath.service.dataimport.ImportService;

/**
 * A validator process for BaseAmount.
 *
 */
public class ImportJobDataValidatorProcForBaseAmount extends ImportJobDataValidatorProc {

	private boolean headerValid;
	private String validationMessage;

	private final ImportService importService =
		ServiceLocator.getService(ContextIdNames.IMPORT_SERVICE);

	private final PriceListService plService =
		ServiceLocator.getService(ContextIdNames.PRICE_LIST_CLIENT_SERVICE);

	private static final int LIST_PRICE_INDEX = 4;
	private static final int SALE_PRICE_INDEX = 5;

	/**
	 * A default constructor with message and import job request in parameters.
	 * @param message a message
	 * @param request a request object
	 */
	public ImportJobDataValidatorProcForBaseAmount(final String message, final ImportJobRequest request) {
		super(message, request);
	}

	@Override
	public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		try {
			monitor.beginTask(getMessage(), IProgressMonitor.UNKNOWN);

			if (monitor.isCanceled()) {
				throw new OperationCanceledException();
			}

			doCsvValidate();
			monitor.worked(1);

			if (csvValidationFaults().isEmpty() && doHeaderValidate(getRequest())) {
				if (monitor.isCanceled()) {
					throw new OperationCanceledException();
				}
				doMappingsValidate();
				monitor.worked(1);
			}
		} finally {
			monitor.done();
		}
	}

	/**
	 * Validate header.
	 * @param importJobRequest a request object for testing
	 * @return a result of validation
	 */
	protected boolean doHeaderValidate(final ImportJobRequest importJobRequest) {

		this.validationMessage = ""; //$NON-NLS-1$

		try {
			this.headerValid = true;
			// get header row
			List<List<String>> previewData = importService.getPreviewData(importJobRequest.getImportJob(), 1);

			// get list price or sale price, if list price header is empty
			List<String> header = previewData.get(0);
			String listPriceHeader = header.get(LIST_PRICE_INDEX).substring("listPrice_".length()); //$NON-NLS-1$
			String salePriceHeader = header.get(SALE_PRICE_INDEX).substring("salePrice_".length()); //$NON-NLS-1$

			if (!listPriceHeader.equals(salePriceHeader)) {
				String message =

						NLS.bind(JobsMessages.get().ConfigurePriceListImportJobPage_Msg_DifferentNames,
						listPriceHeader, salePriceHeader);
				throw new CsvHeaderException(message);
			}

			// extract price list name from template like "listPrice_PriceListName_CAD" or "salePrice_PriceListName_CAD"
			String priceListName;
			String priceListCurrency;
			String[] priceHeaderItems = listPriceHeader.split("_"); //$NON-NLS-1$

			if (priceHeaderItems.length == 2 && priceHeaderItems[0].trim().length() != 0 && priceHeaderItems[1].trim().length() != 0) {
				priceListName = priceHeaderItems[0];
				priceListCurrency = priceHeaderItems[1];
			} else {
				String message =
					NLS.bind(JobsMessages.get().ConfigurePriceListImportJobPage_Msg_WrongFormat,
					listPriceHeader);
				throw new CsvHeaderException(message);
			}
			// get price list GUID from name
			PriceListDescriptorDTO priceListDescriptorDTO = this.plService.getPriceListDescriptorByName(priceListName);

			if (priceListDescriptorDTO == null) {
				String message =

						NLS.bind(JobsMessages.get().ConfigurePriceListImportJobPage_ErrorDialog_Message,
						priceListName, priceListName);
				throw new CsvHeaderException(message);
			}

			validateUserHasPermissionOnPriceList(priceListDescriptorDTO);

			validateCurrency(priceListCurrency, priceListDescriptorDTO);

			// set the guid to the import job
			importJobRequest.getImportJob().setDependentPriceListGuid(priceListDescriptorDTO.getGuid());

		} catch (Exception exception) {
			headerValid = false;
			this.validationMessage = exception.getMessage();
		}
		return headerValid;
	}

	private void validateUserHasPermissionOnPriceList(final PriceListDescriptorDTO priceListDescriptorDTO)
			throws CsvHeaderException {
		CmUser cmUser = LoginManager.getCmUser();
		if (!cmUser.isAllPriceListsAccess() && !cmUser.getPriceLists().contains(priceListDescriptorDTO.getGuid())) {
			String message =

					NLS.bind(JobsMessages.get().ConfigurePriceListImportJobPage_No_Permission_Message,
					priceListDescriptorDTO.getName());
			throw new CsvHeaderException(message);
		}
	}

	/**
	 * @param priceListCurrency a parsed price list currency
	 * @param priceListDescriptorDTO price list descriptor DTO currency
	 * @throws CsvHeaderException exception
	 */
	private void validateCurrency(final String priceListCurrency, final PriceListDescriptorDTO priceListDescriptorDTO) throws CsvHeaderException {
		Currency parcedCurrency;
		Currency priceListDTOCurrency;
		try {
			parcedCurrency = Currency.getInstance(priceListCurrency);
			priceListDTOCurrency = Currency.getInstance(priceListDescriptorDTO.getCurrencyCode());
		} catch (Exception e) {
			String message =

					NLS.bind(JobsMessages.get().ConfigurePriceListImportJobPage_Msg_WrongCurrencyFormat,
					priceListCurrency, priceListDescriptorDTO.getCurrencyCode());
			throw new CsvHeaderException(message, e);
		}
		if (!parcedCurrency.equals(priceListDTOCurrency)) {
			String message =

					NLS.bind(JobsMessages.get().ConfigurePriceListImportJobPage_Msg_WrongCurrency,
					priceListCurrency, priceListDescriptorDTO.getCurrencyCode());
			throw new CsvHeaderException(message);
		}
	}

	/**
	 * @return the validationMessage
	 */
	public String getValidationMessage() {
		return validationMessage;
	}

	/**
	 * @return the headerValid
	 */
	public boolean isHeaderValid() {
		return headerValid;
	}
}
