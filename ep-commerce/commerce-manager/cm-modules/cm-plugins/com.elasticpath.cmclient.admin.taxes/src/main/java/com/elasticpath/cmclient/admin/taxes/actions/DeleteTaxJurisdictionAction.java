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
import com.elasticpath.cmclient.admin.taxes.views.TaxJurisdictionsListView;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.misc.Geography;
import com.elasticpath.domain.tax.TaxJurisdiction;
import com.elasticpath.service.tax.TaxJurisdictionService;

/**
 * Delete Tax Jurisdiction implementation.
 */
public class DeleteTaxJurisdictionAction extends Action {

	/** The logger. */
	private static final Logger LOG = Logger.getLogger(DeleteTaxJurisdictionAction.class);

	/** TaxJurisdictionsListView list view. */
	private final TaxJurisdictionsListView listView;

	/**
	 * The constructor.
	 *
	 * @param listView the tax jurisdictions list view.
	 * @param text the tool tip text.
	 * @param imageDescriptor the image is shown at the title.
	 */
	public DeleteTaxJurisdictionAction(final TaxJurisdictionsListView listView, final String text, final ImageDescriptor imageDescriptor) {
		super(text, imageDescriptor);
		this.listView = listView;
	}

	@Override
	public void run() {
		LOG.debug("Delete Tax Jurisdiction Action called."); //$NON-NLS-1$

		TaxJurisdiction selectedJurisdiction = listView.getSelectedTaxJurisdiction();

		TaxJurisdictionService jurisdictionService = ServiceLocator.getService(
				ContextIdNames.TAX_JURISDICTION_SERVICE);

		Geography geography = ServiceLocator.getService(ContextIdNames.GEOGRAPHY);
		String countryName = geography.getCountryDisplayName(selectedJurisdiction.getRegionCode(), CorePlugin.getDefault().getDefaultLocale());

		if (jurisdictionService.getTaxJurisdictionsInUse().contains(selectedJurisdiction.getUidPk())) {
			MessageDialog.openInformation(listView.getSite().getShell(), TaxesMessages.get().TaxJurisdictionInUseTitle,
				NLS.bind(TaxesMessages.get().TaxJurisdictionInUseMessage,
				countryName));
			return;
		}

		if (jurisdictionService.get(selectedJurisdiction.getUidPk()) == null) {
			MessageDialog.openInformation(listView.getSite().getShell(), TaxesMessages.get().NoLongerExistTaxJurisdictionMsgBoxTitle,

					NLS.bind(TaxesMessages.get().NoLongerExistTaxJurisdictionMsgBoxText,
					selectedJurisdiction.getRegionCode()));
			listView.refreshViewerInput();
			return;
		}


		boolean confirmed = MessageDialog.openConfirm(listView.getSite().getShell(), TaxesMessages.get().ConfirmDeleteTaxJurisdictionMsgBoxTitle,

				NLS.bind(TaxesMessages.get().ConfirmDeleteTaxJurisdictionMsgBoxText,
				countryName));

		if (confirmed) {
			jurisdictionService.remove(selectedJurisdiction);
			listView.refreshViewerInput();
		}
	}
}
