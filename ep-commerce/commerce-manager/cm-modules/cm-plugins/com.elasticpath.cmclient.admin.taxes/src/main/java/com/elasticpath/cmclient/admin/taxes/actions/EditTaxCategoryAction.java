/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.admin.taxes.actions;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import com.elasticpath.cmclient.admin.taxes.dialogs.TaxCategoryDialog;
import com.elasticpath.cmclient.admin.taxes.dialogs.TaxJurisdictionDialog;
import com.elasticpath.domain.tax.TaxCategory;
import com.elasticpath.domain.tax.TaxJurisdiction;

/**
 * Edit tax category action.
 */
public class EditTaxCategoryAction extends Action {

	/** The logger. */
	private static final Logger LOG = Logger.getLogger(EditTaxCategoryAction.class);

	/** Tax Jurisdiction dialog. */
	private final TaxJurisdictionDialog taxJurisdictionsDialog;

	/** Tax Jurisdiction. */
	private final TaxJurisdiction taxJurisdiction;

	/**
	 * The Constructor.
	 * 
	 * @param taxJurisdictionDialog the tax category table view
	 * @param taxJurisdiction the holder tax jurisdiction
	 * @param text the action's text
	 * @param imageDescriptor the action's image
	 */
	public EditTaxCategoryAction(final TaxJurisdictionDialog taxJurisdictionDialog, final TaxJurisdiction taxJurisdiction, final String text,
			final ImageDescriptor imageDescriptor) {
		super(text, imageDescriptor);
		this.taxJurisdictionsDialog = taxJurisdictionDialog;
		this.taxJurisdiction = taxJurisdiction;
	}

	@Override
	public void run() {
		LOG.debug("Edit Tax Category Action called."); //$NON-NLS-1$

		TaxCategory selectedTaxCategory = taxJurisdictionsDialog.getSelectedTaxCategory();

		final boolean dialogOk = TaxCategoryDialog.openEditDialog(taxJurisdictionsDialog.getShell(), selectedTaxCategory, taxJurisdiction);
		if (dialogOk) {
			taxJurisdictionsDialog.refreshTaxCategory();
		}
	}
}
