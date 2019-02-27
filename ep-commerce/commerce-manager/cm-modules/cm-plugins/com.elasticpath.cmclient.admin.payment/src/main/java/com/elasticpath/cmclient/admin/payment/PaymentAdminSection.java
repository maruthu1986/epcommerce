/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.admin.payment;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.elasticpath.cmclient.admin.AbstractAdminSection;
import com.elasticpath.cmclient.admin.payment.views.PaymentListView;

/**
 * Payment admin section.
 */
public class PaymentAdminSection extends AbstractAdminSection {

	@Override
	public void createItems(final FormToolkit toolkit, final Composite parent, final IWorkbenchPartSite site) {
		createItem(toolkit, parent, site, PaymentListView.VIEW_ID, 
				AdminPaymentMessages.get().PaymentAdminItemCompositeFactory_PaymentAdmin,
				AdminPaymentImageRegistry.getImage(AdminPaymentImageRegistry.IMAGE_PAYMENT_GATEWAY));
	}

	@Override
	public boolean isAuthorized() {
		return AdminPaymentPlugin.isAuthorized();
	}
}
