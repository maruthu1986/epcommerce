/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.store.promotions.editors;

import org.eclipse.swt.widgets.Widget;

import com.elasticpath.cmclient.store.promotions.AbstractCouponConfigWidgetFactory;
import com.elasticpath.cmclient.store.promotions.CouponConfigPageModel;

/**
 * An implementation of the abstract widget factory to provide widgets creation for coupon config editor part.
 */
public class CouponConfigEditorWidgetFactory extends AbstractCouponConfigWidgetFactory {

	private final CouponConfigEditorPart couponConfigEditorPart;

	/**
	 * The constructor.
	 * 
	 * @param couponConfigEditorPart the coupon config editor part.
	 * @param couponConfig the coupon config model.
	 */
	public CouponConfigEditorWidgetFactory(final CouponConfigEditorPart couponConfigEditorPart, final CouponConfigPageModel couponConfig) {
		super(couponConfig);
		this.couponConfigEditorPart = couponConfigEditorPart;
	}

	@Override
	protected void setFinishStatus(final boolean status, final String errorMessages) {
		// do nothing
	}

	@Override
	public void widgetChanged(final Widget widget) {
		couponConfigEditorPart.markDirty();
		couponConfigEditorPart.getEditor().controlModified();
	}

}
