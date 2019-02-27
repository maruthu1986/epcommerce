/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.jobs.wizards;

import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.wizard.page.IBeforeFinishNotifier;
import com.elasticpath.cmclient.jobs.JobsMessages;
import com.elasticpath.common.dto.pricing.PriceListDescriptorDTO;
import com.elasticpath.common.pricing.service.PriceListService;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.dataimport.ImportDataType;
import com.elasticpath.domain.dataimport.ImportJob;
import com.elasticpath.service.dataimport.ImportService;

/**
 * Configuration page for information specific to a Price List import job.
 */
public class ConfigurePriceListImportJobPage extends AbstractConfigureImportJobPageWithDataTypeAndImportType implements IBeforeFinishNotifier {

	private static final int INT_3 = 3;
	private static final int LIST_PRICE_INDEX = 3;
	private static final int SALE_PRICE_INDEX = 4;

	private final PriceListService plService;
	private final ImportService importService;

	/**
	 * Constructor for a Price List import job.
	 * @param job the import job object
	 */
	public ConfigurePriceListImportJobPage(final ImportJob job) {
		super("ConfigureImportJobPage", JobsMessages.get().ImportJobWizard_ConfigureImportJob, //$NON-NLS-1$
				JobsMessages.get().ImportJobWizard_ConfigureImportJobDescription, job);
		plService = ServiceLocator.getService(ContextIdNames.PRICE_LIST_CLIENT_SERVICE);
		importService = ServiceLocator.getService(ContextIdNames.IMPORT_SERVICE);
	}

	@Override
	protected List<ImportDataType> getImportDataTypes() {
		return getImportService().getPriceListImportDataTypes();
	}

	@Override
	public boolean enableFinish() {

		// get header row
		List<List<String>> previewData = importService.getPreviewData(getImportJob(), 1);

		// get list price or sale price, if list price header is empty
		List<String> header = previewData.get(0);
		String priceHeader = header.get(LIST_PRICE_INDEX);
		if (priceHeader == null || priceHeader.trim().length() <= 0) {
			priceHeader = header.get(SALE_PRICE_INDEX);
		}
		// extract price list name from template like "listPrice_PriceListName_CAD" or "salePrice_PriceListName_CAD"
		String priceListName;
		String[] priceHeaderItems = priceHeader.split("_"); //$NON-NLS-1$
		if (priceHeaderItems.length == 0) {
			priceListName = priceHeader;
		} else if (priceHeaderItems.length == INT_3 || priceHeaderItems.length == 2) {
			priceListName = priceHeaderItems[1];
		} else {
			StringBuffer sBuffer = new StringBuffer();
			sBuffer.append(priceHeaderItems[1]);
			for (int i = 2; i < priceHeaderItems.length - 1; i++) {
				sBuffer.append('_');
				sBuffer.append(priceHeaderItems[i]);
			}
			priceListName = sBuffer.toString();
		}
		// get price list GUID from name
		PriceListDescriptorDTO priceListDescriptorDTO = this.plService.getPriceListDescriptorByName(priceListName);

		if (priceListDescriptorDTO == null) {
			String message =
				NLS.bind(JobsMessages.get().ConfigurePriceListImportJobPage_ErrorDialog_Message,
				priceListName, priceListName);
			MessageDialog.openError(getShell(), JobsMessages.get().ConfigurePriceListImportJobPage_ErrorDialog_Title, message);
		} else {
			// set the guid to the import job
			getImportJob().setDependentPriceListGuid(priceListDescriptorDTO.getGuid());
		}
		return priceListDescriptorDTO != null;
	}
}
