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
import com.elasticpath.cmclient.admin.taxes.dialogs.TaxJurisdictionDialog;
import com.elasticpath.domain.tax.TaxCategory;
import com.elasticpath.domain.tax.TaxJurisdiction;

/**
 * Delete tax category action.
 */
public class DeleteTaxCategoryAction extends Action {

	/** The logger. */
	private static final Logger LOG = Logger.getLogger(DeleteTaxCategoryAction.class);

	/** TaxJurisdiction dialog. */
	private final TaxJurisdictionDialog taxJurisdictionsDialog;

	/** Tax Jurisdiction. */
	private final TaxJurisdiction taxJurisdiction;

	/**
	 * The constructor.
	 * 
	 * @param taxJurisdictionsDialog the tax jurisdictions dialog.
	 * @param taxJurisdiction Tax Jurisdiction.
	 * @param text the tool tip text.
	 * @param imageDescriptor the image is shown at the title.
	 */
	public DeleteTaxCategoryAction(final TaxJurisdictionDialog taxJurisdictionsDialog, final TaxJurisdiction taxJurisdiction, final String text,
			final ImageDescriptor imageDescriptor) {
		super(text, imageDescriptor);
		this.taxJurisdictionsDialog = taxJurisdictionsDialog;
		this.taxJurisdiction = taxJurisdiction;
	}

	@Override
	public void run() {
		LOG.debug("Create Tax Category action called."); //$NON-NLS-1$

		TaxCategory selectedTaxCategory = taxJurisdictionsDialog.getSelectedTaxCategory();

		boolean confirmed = MessageDialog.openConfirm(taxJurisdictionsDialog.getShell(), TaxesMessages.get().ConfirmDeleteTaxCategoryMsgBoxTitle,

				NLS.bind(TaxesMessages.get().ConfirmDeleteTaxCategoryMsgBoxText,
				selectedTaxCategory.getName()));

		if (confirmed) {
			taxJurisdiction.getTaxCategorySet().remove(selectedTaxCategory);			
			taxJurisdictionsDialog.refreshTaxCategory();
		}

	}
}
