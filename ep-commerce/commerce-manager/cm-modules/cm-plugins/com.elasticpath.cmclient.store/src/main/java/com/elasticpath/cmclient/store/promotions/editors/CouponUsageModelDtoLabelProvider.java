/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.store.promotions.editors;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.elasticpath.cmclient.store.promotions.PromotionsMessages;
import com.elasticpath.common.dto.CouponUsageModelDto;

/**
 * Provides the element text for each cell in the table for the {@code CouponUsageModelDto}.
 */
public class CouponUsageModelDtoLabelProvider extends LabelProvider implements ITableLabelProvider {
	
	private static final int COUPON_CODE_COLUMN = 0;

	private static final int EMAIL_COLUMN = 1;
	
	private static final int STATUS_COLUMN = 2;

	@Override
	public Image getColumnImage(final Object element, final int columnIndex) {
		return null;
	}

	@Override
	public String getColumnText(final Object element, final int columnIndex) {
		CouponUsageModelDto model = (CouponUsageModelDto) element;
		switch (columnIndex) {		
			case COUPON_CODE_COLUMN:
				return model.getCouponCode();
			case EMAIL_COLUMN:
				return model.getEmailAddress();
			case STATUS_COLUMN:
				if (model.isSuspended()) {
					return PromotionsMessages.get().CouponEditorPart_Table_Suspended;
				}
				return PromotionsMessages.get().CouponEditorPart_Table_InUse;
			default: 
				return PromotionsMessages.get().CouponEditorPart_Table_EmptyString;
		}
	}
}
