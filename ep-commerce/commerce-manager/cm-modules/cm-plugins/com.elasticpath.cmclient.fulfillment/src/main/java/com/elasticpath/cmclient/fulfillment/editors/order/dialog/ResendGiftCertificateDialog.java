/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.fulfillment.editors.order.dialog;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.helpers.LocalProductSkuLookup;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.dialog.AbstractEpDialog;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.fulfillment.FulfillmentImageRegistry;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.cmclient.fulfillment.FulfillmentPermissions;
import com.elasticpath.cmclient.fulfillment.FulfillmentPlugin;
import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.service.catalog.ProductSkuLookup;

/**
 * A dialog for re-sending a gift certificate to the customer.
 */
public class ResendGiftCertificateDialog extends AbstractEpDialog {

	private Text recipientText;
	private final OrderSku giftCert;
	private ProductSkuLookup productSkuLookup;

	private String recipientEmail;
	
	/**
	 * Constructs a new instance.
	 * 
	 * @param parentShell the parent shell
	 * @param giftCertificate the gift certificate instance
	 */
	public ResendGiftCertificateDialog(final Shell parentShell, final OrderSku giftCertificate) {
		super(parentShell, 2, false);
		this.giftCert = giftCertificate;
		this.recipientEmail = giftCertificate.getFieldValue(GiftCertificate.KEY_RECIPIENT_EMAIL);
	}

	@Override
	protected void bindControls() {
		final DataBindingContext context = new DataBindingContext();
		EpControlBindingProvider.getInstance().bind(context, recipientText, 
				this, "recipientEmail", EpValidatorFactory.EMAIL_REQUIRED, null, true); //$NON-NLS-1$
	}

	@Override
	protected void createEpDialogContent(final IEpLayoutComposite dialogComposite) {
		final EpState epState = getRecipientTextState();
		
		dialogComposite.addLabelBoldRequired(FulfillmentMessages.get().ResendGiftCertDialog_Recipient, epState, null);
		recipientText = dialogComposite.addTextField(epState, null);
		
		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
		recipientText.setLayoutData(layoutData);
	}

	private EpState getRecipientTextState() {
		EpState epState = EpState.DISABLED;
		if (isAuthorized()) {
			epState = EpState.EDITABLE;
		}
		return epState;
	}

	private boolean isAuthorized() {
		return AuthorizationService.getInstance().isAuthorizedWithPermission(FulfillmentPermissions.ORDER_EDIT)
				&& AuthorizationService.getInstance().isAuthorizedWithPermission(FulfillmentPermissions.EDIT_GIFT_CERTIFICATE_RECIPIENT)
				&& AuthorizationService.getInstance().isAuthorizedForCatalog(
					getProductSkuLookup().findByGuid(giftCert.getSkuGuid()).getProduct().getMasterCatalog());
	}

	
	@Override
	protected void createButtonsForButtonBar(final Composite parent) {
		createEpOkButton(parent, FulfillmentMessages.get().ResendGiftCertDialog_Send, null);
		createEpCancelButton(parent);
	}
	
	@Override
	protected String getInitialMessage() {
		return FulfillmentMessages.get().ResendGiftCertDialog_InitialMessage;
	}

	@Override
	protected String getTitle() {
		return FulfillmentMessages.get().ResendGiftCertDialog_Title;
	}

	@Override
	protected Image getWindowImage() {
		return FulfillmentImageRegistry.getImage(FulfillmentImageRegistry.ICON_GIFT_CERTIFICATE_RESEND);
	}

	@Override
	protected String getWindowTitle() {
		return FulfillmentMessages.get().ResendGiftCertDialog_WindowTitle;
	}

	@Override
	protected String getPluginId() {
		return FulfillmentPlugin.PLUGIN_ID;
	}

	@Override
	public Object getModel() {
		return giftCert;
	}

	@Override
	protected void populateControls() {
		recipientText.setText(recipientEmail);
	}

	/**
	 * @return recipient email
	 */
	public String getRecipientEmail() {
		return recipientEmail;
	}

	/**
	 * Sets the recipient email.
	 * @param recipientEmail recipient email 
	 */
	public void setRecipientEmail(final String recipientEmail) {
		this.recipientEmail = recipientEmail;
	}
	
	/**
	 * Lazy loads a ProductSkuLookup.
	 *
	 * @return a product sku reader.
	 */
	protected ProductSkuLookup getProductSkuLookup() {
		if (productSkuLookup == null) {
			productSkuLookup = new LocalProductSkuLookup();
		}
		
		return productSkuLookup;
	}
}
