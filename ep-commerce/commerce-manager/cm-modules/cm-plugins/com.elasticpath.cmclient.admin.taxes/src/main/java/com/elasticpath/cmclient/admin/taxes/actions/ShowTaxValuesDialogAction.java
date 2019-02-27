/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.admin.taxes.actions;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.IAction;

import com.elasticpath.cmclient.admin.taxes.TaxesImageRegistry;
import com.elasticpath.cmclient.admin.taxes.TaxesMessages;
import com.elasticpath.cmclient.admin.taxes.TaxesPlugin;
import com.elasticpath.cmclient.admin.taxes.dialogs.ManageTaxValuesDialog;
import com.elasticpath.cmclient.core.actions.AbstractAuthorizedShowViewAction;

/**
 * Opens tax values dialog.
 */
public class ShowTaxValuesDialogAction extends AbstractAuthorizedShowViewAction {
	private static final Logger LOG = Logger.getLogger(ShowTaxValuesDialogAction.class);

	@Override
	public void run(final IAction action) {
		if (isAuthorized()) {
			final ManageTaxValuesDialog manageTaxValuesDialog = new ManageTaxValuesDialog(getWindow().getShell(),
					TaxesMessages.get().ManageTaxValuesTitleDialog, TaxesMessages.get().ManageTaxValuesMessageDialog, TaxesImageRegistry
							.getImage(TaxesImageRegistry.IMAGE_TAX_VALUE));
			manageTaxValuesDialog.open();
		} else {
			LOG.info("Authorization Error"); //$NON-NLS-1$
		}
	}

	@Override
	public boolean isAuthorized() {
		return TaxesPlugin.isAuthorized();
	}

	@Override
	protected String getViewId() {
		return null;
	}

}
