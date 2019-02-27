/*
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.fulfillment.editors.order.actions.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.helpers.LocalProductSkuLookup;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.fulfillment.FulfillmentImageRegistry;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.cmclient.fulfillment.FulfillmentPermissions;
import com.elasticpath.cmclient.fulfillment.editors.order.dialog.ResendGiftCertificateDialog;
import com.elasticpath.cmclient.fulfillment.editors.order.ui.ManagedModel;
import com.elasticpath.cmclient.fulfillment.editors.order.ui.ManagedModelFactory;
import com.elasticpath.cmclient.fulfillment.editors.order.ui.UiProperty;
import com.elasticpath.cmclient.fulfillment.editors.order.ui.impl.ManagedModelFactoryImpl;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.service.catalog.GiftCertificateService;
import com.elasticpath.service.catalog.ProductSkuLookup;

/**
 * This action contributes the 'Resend Gift Certificate' button and performs the resend action.
 */
public class GiftCertificateContributedAction extends AbstractContributedAction {

	private static final Logger LOG = Logger.getLogger(GiftCertificateContributedAction.class);

	private final ManagedModelFactory<String, String> managedModelFactory = new ManagedModelFactoryImpl();

	private ProductSkuLookup productSkuLookup;
	
	@Override
	protected void buildActionUiElements(final IEpLayoutComposite dialogComposite, final IEpLayoutData buttonData) {
		if (getButton() == null) {
			setButton(dialogComposite.addPushButton(FulfillmentMessages.get().ShipmenSection_GiftCertificateResend, FulfillmentImageRegistry
					.getImage(FulfillmentImageRegistry.ICON_GIFT_CERTIFICATE_RESEND), EpState.EDITABLE, buttonData));
		}
	}

	@Override
	protected void buildDefaultListener() {
		if (getButton() != null && getDefaultListener() == null) {
			setDefaultListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(final SelectionEvent event) {
					final ResendGiftCertificateDialog dialog = new ResendGiftCertificateDialog(null, getOrderSku());
					if (dialog.open() == IDialogConstants.OK_ID) {
						resendEmail(dialog.getRecipientEmail());
					}
				}
			});
			getButton().addSelectionListener(getDefaultListener());
		}
	}

	/**
	 * Resends the Gift Certificate email to the specified address.
	 * 
	 * @param recipientEmail the email address to resend the email to
	 */
	protected void resendEmail(final String recipientEmail) {
		try {
			final GiftCertificateService giftCertificateService = ServiceLocator.getService(ContextIdNames.GIFT_CERTIFICATE_SERVICE);
			giftCertificateService.resendGiftCertificate(recipientEmail, getOrder().getGuid(), getOrderSku().getGuid());

		} catch (final Exception e) {
			LOG.error("Error re-sending gift certificate to customer.", e); //$NON-NLS-1$
			showErrorDialog();
		}
	}

	/**
	 * Shows the error dialog.
	 */
	protected void showErrorDialog() {
		MessageDialog.openError(null, FulfillmentMessages.get().OrderDetailsErrorResendingGiftCert_Title,
				FulfillmentMessages.get().OrderDetailsErrorResendingGiftCert_Message);
	}

	@Override
	protected boolean canContributedActionApplied() {
		if (this.getOrderSku() == null) {
			return false;
		}
		
		ProductSku productSku = getProductSkuLookup().findByGuid(getOrderSku().getSkuGuid());
		ProductType productType = productSku.getProduct().getProductType();

		return productType.isGiftCertificate();
	}

	@Override
	public List<ManagedModel<String, String>> filterDisplayData(final List<ManagedModel<String, String>> displayData) {
		
		List<ManagedModel<String, String>> resultList = displayData;
		if (canContributedActionApplied()) {
			resultList = new ArrayList<>(displayData.size());

			for (final ManagedModel<String, String> managedRow : displayData) {
				if ("giftCertificate.code".equalsIgnoreCase(managedRow.getKey()) && !isAuthorized()) { //$NON-NLS-1$
					resultList.add(managedModelFactory.create(managedRow, UiProperty.MASK));
				} else if ("giftCertificate.recipientEmail".equalsIgnoreCase(managedRow.getKey()) && !isAuthorizedToSendEmail()) { //$NON-NLS-1$
					resultList.add(managedModelFactory.create(managedRow, UiProperty.READ_ONLY));
				} else {
					resultList.add(managedRow);
				}
			}
		}
		return resultList;
	}

	private boolean isAuthorized() {
		return AuthorizationService.getInstance().isAuthorizedWithPermission(FulfillmentPermissions.VIEW_FULL_CREDITCARD_NUMBER);
	}

	private boolean isAuthorizedToSendEmail() {
		return AuthorizationService.getInstance().isAuthorizedWithPermission(FulfillmentPermissions.ORDER_EDIT)
				&& AuthorizationService.getInstance().isAuthorizedWithPermission(FulfillmentPermissions.EDIT_GIFT_CERTIFICATE_RECIPIENT)
				&& AuthorizationService.getInstance().isAuthorizedForCatalog(
					getProductSkuLookup().findByGuid(getOrderSku().getSkuGuid()).getProduct().getMasterCatalog());
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
