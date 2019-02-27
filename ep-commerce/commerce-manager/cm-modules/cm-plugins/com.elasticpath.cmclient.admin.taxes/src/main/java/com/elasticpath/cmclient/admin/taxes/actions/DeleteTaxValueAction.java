/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.admin.taxes.actions;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;

import com.elasticpath.cmclient.admin.taxes.TaxesMessages;
import com.elasticpath.cmclient.admin.taxes.dialogs.ManageTaxValuesDialog;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.tax.TaxCategory;
import com.elasticpath.domain.tax.TaxRegion;
import com.elasticpath.service.tax.TaxJurisdictionService;

/**
 * Delete Tax Value action.
 */
public class DeleteTaxValueAction extends Action {

	/** The logger. */
	private static final Logger LOG = Logger.getLogger(DeleteTaxValueAction.class);

	/** Manage Tax Value Dialog. */
	private final ManageTaxValuesDialog manageTaxValueDialog;

	/**
	 * The constructor.
	 *
	 * @param manageTaxValueDialog the ManageTaxValueDialog.
	 * @param text the tool tip text.
	 * @param imageDescriptor the image is shown at the title.
	 */
	public DeleteTaxValueAction(final ManageTaxValuesDialog manageTaxValueDialog, final String text, final ImageDescriptor imageDescriptor) {
		super(text, imageDescriptor);
		this.manageTaxValueDialog = manageTaxValueDialog;
	}

	@Override
	public void run() {
		LOG.debug("Delete Tax Value Action called."); //$NON-NLS-1$

		TaxRegion taxRegion = manageTaxValueDialog.getSelectedTaxRegion();
		TaxCategory taxCategory = manageTaxValueDialog.getSelectedTaxCategory();

		boolean confirmed = MessageDialog.openConfirm(manageTaxValueDialog.getShell(), TaxesMessages.get().ConfirmDeleteTaxValueMsgBoxTitle,

				NLS.bind(TaxesMessages.get().ConfirmDeleteTaxValueMsgBoxText,
				taxRegion.getRegionName()));

		if (confirmed) {
			taxCategory.removeTaxRegion(taxRegion);

			TaxJurisdictionService taxJurisdictionService = ServiceLocator.getService(
					ContextIdNames.TAX_JURISDICTION_SERVICE);
			taxJurisdictionService.update(manageTaxValueDialog.getSelectedTaxJurisdiction());
			manageTaxValueDialog.refreshTaxRegions();
		}
	}
}
