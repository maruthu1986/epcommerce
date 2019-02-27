/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.admin.taxes.actions;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import com.elasticpath.cmclient.admin.taxes.dialogs.TaxJurisdictionDialog;
import com.elasticpath.cmclient.admin.taxes.views.TaxJurisdictionsListView;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.tax.TaxJurisdiction;
import com.elasticpath.service.tax.TaxJurisdictionService;

/**
 * Create Tax Jurisdiction action.
 */
public class CreateTaxJurisdictionAction extends Action {

	/** The logger. */
	private static final Logger LOG = Logger.getLogger(CreateTaxJurisdictionAction.class);

	/** TaxJurisdictionsListView list view. */
	private final TaxJurisdictionsListView listView;

	/**
	 * The constructor.
	 * 
	 * @param listView the tax jurisdictions list view.
	 * @param text the tool tip text.
	 * @param imageDescriptor the image is shown at the title.
	 */
	public CreateTaxJurisdictionAction(final TaxJurisdictionsListView listView, final String text, final ImageDescriptor imageDescriptor) {
		super(text, imageDescriptor);
		this.listView = listView;
	}

	@Override
	public void run() {
		LOG.debug("Create Tax Jurisdiction Action called."); //$NON-NLS-1$

		TaxJurisdiction taxJurisdiction = ServiceLocator.getService(ContextIdNames.TAX_JURISDICTION);

		boolean dialogOk = TaxJurisdictionDialog.openCreateDialog(listView.getSite().getShell(), taxJurisdiction);

		if (dialogOk) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Persisting tax Jurisdiction: " + taxJurisdiction.getRegionCode()); //$NON-NLS-1$
			}
			TaxJurisdictionService taxJurisdictionService = ServiceLocator.getService(
					ContextIdNames.TAX_JURISDICTION_SERVICE);
			taxJurisdictionService.add(taxJurisdiction);
			listView.refreshViewerInput();
		}

	}
}
