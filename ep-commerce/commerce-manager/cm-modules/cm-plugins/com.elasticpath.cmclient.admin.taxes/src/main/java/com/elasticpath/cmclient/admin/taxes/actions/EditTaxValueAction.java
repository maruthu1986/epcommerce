/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.admin.taxes.actions;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import com.elasticpath.cmclient.admin.taxes.dialogs.ManageTaxValuesDialog;
import com.elasticpath.cmclient.admin.taxes.dialogs.TaxValueDialog;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.tax.TaxCategory;
import com.elasticpath.domain.tax.TaxRegion;
import com.elasticpath.service.tax.TaxJurisdictionService;

/**
 * Edit Tax Value action.
 */
public class EditTaxValueAction extends Action {

	/** The logger. */
	private static final Logger LOG = Logger.getLogger(EditTaxValueAction.class);

	/** Manage Tax Value Dialog. */
	private final ManageTaxValuesDialog manageTaxValueDialog;

	/**
	 * The constructor.
	 * 
	 * @param manageTaxValueDialog the ManageTaxValueDialog.
	 * @param text the tool tip text.
	 * @param imageDescriptor the image is shown at the title.
	 */
	public EditTaxValueAction(final ManageTaxValuesDialog manageTaxValueDialog, final String text, final ImageDescriptor imageDescriptor) {
		super(text, imageDescriptor);
		this.manageTaxValueDialog = manageTaxValueDialog;
	}

	@Override
	public void run() {
		LOG.debug("Edit Tax Value Action called."); //$NON-NLS-1$

		TaxRegion taxRegion = manageTaxValueDialog.getSelectedTaxRegion();
		TaxCategory taxCategory = manageTaxValueDialog.getSelectedTaxCategory();
		boolean dialogOk = TaxValueDialog.openEditDialog(manageTaxValueDialog.getShell(), taxCategory, taxRegion, taxCategory.getFieldMatchType()
				.getName());

		if (dialogOk) {
			TaxJurisdictionService taxJurisdictionService = ServiceLocator.getService(
					ContextIdNames.TAX_JURISDICTION_SERVICE);
			taxJurisdictionService.update(manageTaxValueDialog.getSelectedTaxJurisdiction());
			manageTaxValueDialog.refreshTaxRegions();
		}
	}
}