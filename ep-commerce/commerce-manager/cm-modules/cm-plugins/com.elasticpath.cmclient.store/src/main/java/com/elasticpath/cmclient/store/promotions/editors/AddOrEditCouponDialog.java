/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.store.promotions.editors;

import java.util.Collection;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.custom.CCombo;

import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.promotions.CouponCollectionModel;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.store.promotions.CouponEditingPopupDialog;
import com.elasticpath.cmclient.store.promotions.PromotionsMessages;
import com.elasticpath.common.dto.CouponModelDto;

/**
 * Add or edit coupon dialog for coupon editor.
 */
public class AddOrEditCouponDialog extends CouponEditingPopupDialog {

	private CCombo statusField;
	private final boolean isEditMode;


	/**
	 * The constructor.
	 * 
	 * @param couponModelDtos coupon model dtos.
	 * @param collectionModel the collection model.
	 * @param isEditMode the Edit Mode. 
	 */
	public AddOrEditCouponDialog(final Collection<CouponModelDto> couponModelDtos, 
								final CouponCollectionModel collectionModel,
								final boolean isEditMode) {
		super(couponModelDtos, collectionModel, isEditMode);
		this.isEditMode = isEditMode;
	}

	@Override
	protected void createEpDialogContent(final IEpLayoutComposite dialogComposite) {
		if (!isEditMode) {
			super.createEpDialogContent(dialogComposite);
		}

		final IEpLayoutData labelData = dialogComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.CENTER);
		final IEpLayoutData fieldData = dialogComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.CENTER, true, false, 2, 1);

		dialogComposite.addLabelBoldRequired(PromotionsMessages.get().CouponSingleEditorDialog_Status, EpState.EDITABLE, labelData);
		this.statusField = dialogComposite.addGridLayoutComposite(1, false, fieldData).addComboBox(EpState.EDITABLE, fieldData);
	}
	
	@Override
	protected void populateControls() {
		if (!isEditMode) {
			super.populateControls();
		}

		this.statusField.add(PromotionsMessages.get().CouponSingleEditorDialog_Status_In_Use);
		this.statusField.add(PromotionsMessages.get().CouponSingleEditorDialog_Status_Suspended);

		populateStatusField();

		if (isEditMode) {
			setMessage(PromotionsMessages.get().CouponEditPopupDialogDescription);
		} else {
			setMessage(PromotionsMessages.get().CouponAddPopupDialogDescription);
		}
	}

	/**
	 * Sets the initial state of the status field.
	 */
	protected void populateStatusField() {
		this.statusField.select(0);

		if (!isSingleEditing()) {
			// Because, for multi-select, we select in-use as the default,
			// we need to force the model to match. Otherwise the binding
			// does not get fired and the model does not get updated.
			setCouponModelDtosSuspended(false);
			return;
		}

		if (getCouponModelDtos().iterator().next().isSuspended()) {
			this.statusField.select(1);
		}
	}

	@Override
	protected void bindControls() {
		EpControlBindingProvider.getInstance().bind(getDataBindingContext(), this.statusField, null, null, createUpdateValueStrategy(), false);
		if (!isEditMode) {
			super.bindControls();
		}
	}

	/**
	 * Creates the update value strategy.
	 * 
	 * @return {@link ObservableUpdateValueStrategy}.
	 */
	protected ObservableUpdateValueStrategy createUpdateValueStrategy() {
		return new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object value) {
				Integer select = (Integer) value;
				if (select == 0) {
					setCouponModelDtosSuspended(false);
				}

				if (select == 1) {
					setCouponModelDtosSuspended(true);
				}

				return Status.OK_STATUS;
			}
		};
	}
	
	private void setCouponModelDtosSuspended(final boolean status) {
		for (CouponModelDto dto : getCouponModelDtos()) {
			dto.setSuspended(status);
		}
	}
}
