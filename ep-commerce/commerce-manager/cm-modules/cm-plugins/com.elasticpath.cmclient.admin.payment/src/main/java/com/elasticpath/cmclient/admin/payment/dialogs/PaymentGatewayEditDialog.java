/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.admin.payment.dialogs;

import java.util.Properties;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

import com.elasticpath.domain.payment.PaymentGateway;

/**
 * Edit payment gateway dialog.
 */
class PaymentGatewayEditDialog extends AbstractPaymentGatewayDialog {

	private final PaymentGateway paymentGateway;

	/**
	 * The constructor.
	 * 
	 * @param parentShell the parent shell.
	 * @param paymentGateway Payment Gateway to be edited.
	 * @param image the edit dialog image.
	 * @param title the edit dialog title.
	 */
	protected PaymentGatewayEditDialog(final Shell parentShell, final PaymentGateway paymentGateway, final Image image, final String title) {
		super(parentShell, image, title);
		this.paymentGateway = paymentGateway;
	}

	@Override
	public PaymentGateway getPaymentGateway() {
		return paymentGateway;
	}

	@Override
	
	// ---- DOCpopulateControls
	protected void populateControls() {
		getGatewayNameText().setText(paymentGateway.getName());
		getGatewayImplCombo().add(paymentGateway.getType());
		getGatewayImplCombo().select(0);
		getGatewayImplCombo().setEnabled(false);
		Properties props = paymentGateway.buildProperties();
		setGatewayProperties(props);
	}
	// ---- DOCpopulateControls

	@Override

    // ---- DOCprepareForSave
	protected boolean prepareForSave() {
		if (!setGatewayName(paymentGateway)) {
			return false;
		}
		paymentGateway.setProperties(getGatewayProperties());
		return true;
	}
	// ---- DOCprepareForSave
}
