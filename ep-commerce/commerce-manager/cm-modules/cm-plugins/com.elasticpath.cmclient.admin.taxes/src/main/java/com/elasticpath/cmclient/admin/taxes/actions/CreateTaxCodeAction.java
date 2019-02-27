/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.admin.taxes.actions;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import com.elasticpath.cmclient.admin.taxes.dialogs.TaxCodeDialog;
import com.elasticpath.cmclient.admin.taxes.views.TaxCodeListView;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.service.tax.TaxCodeService;

/**
 * Create tax code action.
 */
public class CreateTaxCodeAction extends Action {

	/** The logger. */
	private static final Logger LOG = Logger.getLogger(CreateTaxCodeAction.class);

	private final TaxCodeListView listView;

	/**
	 * The constructor.
	 * 
	 * @param listView the tax code list view
	 * @param text the action's text
	 * @param imageDescriptor the action's image
	 */
	public CreateTaxCodeAction(final TaxCodeListView listView, final String text, final ImageDescriptor imageDescriptor) {
		super(text, imageDescriptor);
		this.listView = listView;
	}

	@Override
	public void run() {
		LOG.debug("Create Tax Code action called."); //$NON-NLS-1$

		final TaxCode taxCode = ServiceLocator.getService(ContextIdNames.TAX_CODE);

		final boolean dialogOk = TaxCodeDialog.openCreateDialog(listView.getSite().getShell(), taxCode);
		if (dialogOk) {
			final TaxCodeService taxCodeService = ServiceLocator.getService(
					ContextIdNames.TAX_CODE_SERVICE);
			taxCodeService.add(taxCode);
			listView.refreshViewerInput();
		}
	}
}