/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.store.promotions;

import org.eclipse.jface.dialogs.IDialogLabelKeys;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.JFaceResources;

import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;

import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.common.dto.CouponModelDto;

/**
 * Dialog shown when removing base amounts.
 *
 */
public class CouponRemoveDialog extends MessageDialog {

	private static final String[] BUTTONS = {
			JFaceResources.getString(IDialogLabelKeys.OK_LABEL_KEY),
			JFaceResources.getString(IDialogLabelKeys.CANCEL_LABEL_KEY) };

	private final CouponModelDto couponUsageModel;

	/**
	 * Dialog for confirmation to remove a BaseAmount entry.
	 *
	 * @param parentShell the parent shell
	 * @param couponModelDto the coupon usage model to consider deleting
	 */
	public CouponRemoveDialog(final Shell parentShell, final CouponModelDto couponModelDto) {
		super(parentShell, PromotionsMessages.get().Coupon_Delete_Title, null,
				PromotionsMessages.get().Coupon_Delete_Message, QUESTION, BUTTONS, 0);
		this.couponUsageModel = couponModelDto;
	}


	@Override
	protected void configureShell(final Shell newShell) {
		super.configureShell(newShell);
		newShell.setImage(CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_REMOVE));
	}

	@Override
	public int open() {
		message =
			NLS.bind(message,
			couponUsageModel.getCouponCode());
		return super.open();
	}
}
