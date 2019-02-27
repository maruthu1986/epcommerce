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
import com.elasticpath.cmclient.admin.taxes.dialogs.TaxCodeDialog;
import com.elasticpath.cmclient.admin.taxes.views.TaxCodeListView;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.service.tax.TaxCodeService;

/**
 * Edit tax code action.
 */
public class EditTaxCodeAction extends Action {

	/** The logger. */
	private static final Logger LOG = Logger.getLogger(EditTaxCodeAction.class);

	private final TaxCodeListView listView;

	/**
	 * The Constructor.
	 *
	 * @param listView the customer list view
	 * @param text the action's text
	 * @param imageDescriptor the action's image
	 */
	public EditTaxCodeAction(final TaxCodeListView listView, final String text, final ImageDescriptor imageDescriptor) {
		super(text, imageDescriptor);
		this.listView = listView;
	}

	@Override
	public void run() {
		LOG.debug("Edit Tax Code Action called."); //$NON-NLS-1$

		final TaxCodeService taxCodeService = ServiceLocator.getService(ContextIdNames.TAX_CODE_SERVICE);
		final TaxCode taxCode = listView.getSelectedTaxCode();
		final TaxCode taxCodeToEdit = taxCodeService.findByCode(taxCode.getCode());
		if (taxCodeToEdit == null) {
			MessageDialog.openInformation(listView.getSite().getShell(), TaxesMessages.get().TaxCodeNoLongerExists,

					NLS.bind(TaxesMessages.get().EditTaxCode,
					taxCode.getCode()));
			listView.refreshViewerInput();
			return;
		}

		final boolean dialogOk = TaxCodeDialog.openEditDialog(listView.getSite().getShell(), taxCodeToEdit);
		if (dialogOk) {
			taxCodeService.update(taxCodeToEdit);
			listView.refreshViewerInput();
		}
	}
}
