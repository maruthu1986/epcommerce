/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.store.promotions.wizard;

import org.eclipse.swt.widgets.Widget;

import com.elasticpath.cmclient.store.promotions.AbstractCouponConfigWidgetFactory;
import com.elasticpath.cmclient.store.promotions.CouponConfigPageModel;

/**
 * An implementation of the abstract factory to create widgets for coupon config wizard page.
 */
public class CouponConfigWizardPageWidgetFactory extends AbstractCouponConfigWidgetFactory {

	private final CouponConfigWizardPage wizardPage;

	/**
	 * The constructor.
	 * 
	 * @param wizardPage the {@link CouponConfigWizardPage}.
	 * @param model the coupon config model.
	 */
	public CouponConfigWizardPageWidgetFactory(final CouponConfigWizardPage wizardPage, final CouponConfigPageModel model) {
		super(model);
		this.wizardPage = wizardPage;
	}

	@Override
	protected void setFinishStatus(final boolean status, final String errorMessages) {
		wizardPage.setPageComplete(status);
		wizardPage.setErrorMessage(errorMessages);
	}

	@Override
	public void widgetChanged(final Widget widget) {
		// do nothing
	}
}
