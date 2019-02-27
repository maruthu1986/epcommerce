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
import com.elasticpath.cmclient.admin.taxes.views.TaxCodeListView;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.service.tax.TaxCodeService;

/**
 * Delete tax code action.
 */
public class DeleteTaxCodeAction extends Action {

	/** The logger. */
	private static final Logger LOG = Logger.getLogger(DeleteTaxCodeAction.class);

	private final TaxCodeListView listView;

	/**
	 * The constructor.
	 *
	 * @param listView the tax code list view
	 * @param text the action's text
	 * @param imageDescriptor the action's image
	 */
	public DeleteTaxCodeAction(final TaxCodeListView listView, final String text, final ImageDescriptor imageDescriptor) {
		super(text, imageDescriptor);
		this.setToolTipText(TaxesMessages.get().DeleteTaxCode);
		this.setText(TaxesMessages.get().DeleteTaxCode);
		this.listView = listView;
	}

	@Override
	public void run() {
		LOG.debug("Delete Tax Code action called."); //$NON-NLS-1$

		final TaxCodeService taxCodeService = ServiceLocator.getService(ContextIdNames.TAX_CODE_SERVICE);

		final TaxCode taxCode = listView.getSelectedTaxCode();

		if (taxCodeService.getTaxCodesInUse().contains(taxCode.getCode())) {
			MessageDialog.openInformation(listView.getSite().getShell(), TaxesMessages.get().TaxCodeInUseTitle,
				NLS.bind(TaxesMessages.get().TaxCodeInUseMessage,
				taxCode.getCode()));
			return;
		}

		final boolean confirmed = MessageDialog.openConfirm(listView.getSite().getShell(), TaxesMessages.get().DeleteTaxCodeTitle,
			NLS.bind(TaxesMessages.get().DeleteTaxCodeText,
			taxCode.getCode()));
		if (confirmed) {
			taxCodeService.remove(taxCode);
			listView.refreshViewerInput();
		}
	}
}