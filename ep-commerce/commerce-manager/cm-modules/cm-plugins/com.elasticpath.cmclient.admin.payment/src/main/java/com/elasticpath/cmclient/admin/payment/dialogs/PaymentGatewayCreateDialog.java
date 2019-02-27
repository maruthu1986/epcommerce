/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.admin.payment.dialogs;

import java.util.Properties;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

import com.elasticpath.cmclient.admin.payment.AdminPaymentMessages;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.payment.PaymentGateway;
import com.elasticpath.service.payment.PaymentGatewayService;

/**
 * Create payment gateway dialog.
 */
class PaymentGatewayCreateDialog extends AbstractPaymentGatewayDialog {

	/**
	 * Holds currently selected gateway's index in order to make that gateway back in checkbox in the UI in case if user discards properties changes.
	 */
	private int currentGatewayIndex;

	/** Gateway which will be created in this create dialog. */
	private PaymentGateway paymentGateway;

	/**
	 * The create dialog constructor. Doesn't need gateway parameter. The gateway will be created at the time user clicks save button, because
	 * gateway's implementations is unknown up front.
	 * 
	 * @param parentShell the parent shell
	 * @param image this create dialog image
	 * @param title this create dialog title
	 */
	protected PaymentGatewayCreateDialog(final Shell parentShell, final Image image, final String title) {
		super(parentShell, image, title);
	}

	@Override
	public PaymentGateway getPaymentGateway() {
		return paymentGateway;
	}

	@Override
	protected void createEpDialogContent(final IEpLayoutComposite dialogComposite) {
		super.createEpDialogContent(dialogComposite);
		getGatewayImplCombo().addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(final SelectionEvent event) {
				LOG.debug("widgetDefaultSelected: " + event); //$NON-NLS-1$
			}

			/**
			 * Forces gateway's property table to be updated when user changes gateway implementation in UI.
			 * 
			 * @param event SelectionEvent
			 */
			@Override
			public void widgetSelected(final SelectionEvent event) {

				/**
				 * Update property table for the specified gateway implementation.
				 */
				if (updatePropertiesTable(getGatewayImplCombo().getText())) {
					currentGatewayIndex = getGatewayImplCombo().getSelectionIndex();
				} else {
					/**
					 * select previously used implementation if user do not want to discard properties' changes
					 */
					getGatewayImplCombo().select(currentGatewayIndex);
				}
			}
		});
	}

	@Override
	protected void populateControls() {
		PaymentGatewayService gatewayService = ServiceLocator.getService(
				ContextIdNames.PAYMENT_GATEWAY_SERVICE);
		for (String implName : gatewayService.getSupportedPaymentGateways()) {
			getGatewayImplCombo().add(implName);
		}
		getGatewayImplCombo().select(0);
		currentGatewayIndex = 0;
		updatePropertiesTable(getGatewayImplCombo().getText());
	}

	/**
	 * Gets gateway contextId using UI implementation name. Creates gateway using that contextId. Gets default properties for that gateway. Questions
	 * user to reject previously modified properties.
	 * 
	 * @param implName implementation name seen in UI
	 * @return true if gateway's properties either were not modified or user want to reject modifications.
	 */
	protected boolean updatePropertiesTable(final String implName) {

		if (isGatewayPropertiesModified()
				&& !MessageDialog.openQuestion(getShell(), AdminPaymentMessages.get().PaymentGatewayPropertiesModifiedTitle,
						AdminPaymentMessages.get().PaymentGatewayPropertiesModifiedText)) {
			return false;
		}
		PaymentGatewayService gatewayService = ServiceLocator.getService(
				ContextIdNames.PAYMENT_GATEWAY_SERVICE);
		Properties paymentDefProps = gatewayService.getPaymentGatewayDefaultProperties(implName);

		/** sets gateway properties to be modified */
		setGatewayProperties(paymentDefProps);
		return true;
	}

	@Override
	protected boolean prepareForSave() {
		/** Check if gateway's name is correct */
		boolean isPaymentReadyForSave = isGatewayNameNotExist(getGatewayNewName());

		if (isPaymentReadyForSave) {
			PaymentGatewayService gatewayService = ServiceLocator.getService(
					ContextIdNames.PAYMENT_GATEWAY_SERVICE);
			String paymentGatewayType = getGatewayImplCombo().getText();
			paymentGateway = gatewayService.addPaymentGateway(getGatewayNewName(), paymentGatewayType, getGatewayProperties());
		} else {
			paymentGateway = null;
		}

		return isPaymentReadyForSave;
	}

}
