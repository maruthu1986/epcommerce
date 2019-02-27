/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.admin.taxes;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.elasticpath.cmclient.admin.AbstractAdminSection;
import com.elasticpath.cmclient.admin.taxes.actions.ManageTaxValuesAction;
import com.elasticpath.cmclient.admin.taxes.views.TaxCodeListView;
import com.elasticpath.cmclient.admin.taxes.views.TaxJurisdictionsListView;

/**
 * Taxes admin section.
 */
public class TaxesAdminSection extends AbstractAdminSection {

	@Override
	public void createItems(final FormToolkit toolkit, final Composite parent, final IWorkbenchPartSite site) {
		createItem(toolkit, parent, site, TaxCodeListView.VIEW_ID, TaxesMessages.get().TaxesAdminSection_TaxCodes, TaxesImageRegistry
				.getImage(TaxesImageRegistry.IMAGE_TAX_CODE));
		createItem(toolkit, parent, site, TaxJurisdictionsListView.VIEW_ID, TaxesMessages.get().TaxesAdminSection_TaxJurisdictionAdmin,
				TaxesImageRegistry.getImage(TaxesImageRegistry.IMAGE_TAX_JURISDICTION_ADMIN_SECTION_ITEM));
		createItemDialog(toolkit, parent, TaxesMessages.get().TaxesAdminSection_ManageTaxValues, TaxesImageRegistry
				.getImage(TaxesImageRegistry.IMAGE_TAX_VALUE), new ManageTaxValuesAction(site));
	}

	@Override
	public boolean isAuthorized() {
		return TaxesPlugin.isAuthorized();
	}

}