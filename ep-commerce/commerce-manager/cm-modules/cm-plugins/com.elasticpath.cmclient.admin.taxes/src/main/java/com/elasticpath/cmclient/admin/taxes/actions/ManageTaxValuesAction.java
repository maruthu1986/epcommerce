/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.admin.taxes.actions;

import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.ui.IWorkbenchPartSite;

import com.elasticpath.cmclient.admin.taxes.TaxesImageRegistry;
import com.elasticpath.cmclient.admin.taxes.TaxesMessages;
import com.elasticpath.cmclient.admin.taxes.dialogs.ManageTaxValuesDialog;

/**
 * The Manage Tax Values Action.
 */
public class ManageTaxValuesAction extends MouseAdapter {

	private final IWorkbenchPartSite site;

	/**
	 * The constructor.
	 * 
	 * @param site the site
	 */
	public ManageTaxValuesAction(final IWorkbenchPartSite site) {
		this.site = site;
	}

	@Override
	public void mouseDown(final MouseEvent event) {
		ManageTaxValuesDialog manageTaxValuesDialog = new ManageTaxValuesDialog(site.getShell(), TaxesMessages.get().ManageTaxValuesTitleDialog,
				TaxesMessages.get().ManageTaxValuesMessageDialog, TaxesImageRegistry.getImage(TaxesImageRegistry.IMAGE_TAX_VALUE));
		manageTaxValuesDialog.open();
	}
}
