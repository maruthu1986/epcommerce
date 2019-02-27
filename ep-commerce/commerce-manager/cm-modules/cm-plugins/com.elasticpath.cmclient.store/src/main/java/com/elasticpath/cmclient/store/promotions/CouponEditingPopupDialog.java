/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.store.promotions;

import java.util.Collection;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.EpDialogSupport;
import com.elasticpath.cmclient.core.promotions.CouponCollectionModel;
import com.elasticpath.cmclient.core.promotions.ValidationState;
import com.elasticpath.cmclient.core.ui.dialog.AbstractEpDialog;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.CompoundValidator;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.store.StorePlugin;
import com.elasticpath.common.dto.CouponModelDto;
import com.elasticpath.common.dto.CouponUsageModelDto;

/**
 * Add/edit base amount Dialog.
 */
@SuppressWarnings({"PMD.TooManyFields" })
public class CouponEditingPopupDialog extends AbstractEpDialog {

	private static final int LAYOUT_COLUMN_NUMBER = 3;

	/**
	 * Getter for data binding context.
	 *
	 * @return the dataBindingContext
	 */
	public DataBindingContext getDataBindingContext() {
		return dataBindingContext;
	}

	private Text couponCodeField;

	private Text emailField;

	/** The data binding context. */
	private final DataBindingContext dataBindingContext;

	private final String windowTitle = PromotionsMessages.get().CouponSingleEditorDialog_Title;

	private final String dialogTitle = PromotionsMessages.get().CouponSingleEditorDialog_Title;

	private final CouponCollectionModel model;

	private final Collection<CouponModelDto> couponModelDtos;

	private final boolean singleEditing;

	private boolean isEditMode;


	/**
	 * Getter for coupon model dtos.
	 *
	 * @return the couponModelDtos
	 */
	public Collection<CouponModelDto> getCouponModelDtos() {
		return couponModelDtos;
	}

	/**
	 * Whether this dialog is editing single coupon or multiple coupons.
	 *
	 * @return true if it's editing single coupon.
	 */
	protected boolean isSingleEditing() {
		return singleEditing;
	}

	/**
	 * Constructs the Base Amount dialog.
	 * @param couponModelDtos The coupons to update.
	 * @param model The model
	 * @param isEditMode Is the dialog in edit mode.
	 */
	public CouponEditingPopupDialog(final Collection<CouponModelDto> couponModelDtos, final CouponCollectionModel model, final boolean isEditMode) {
		super(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), LAYOUT_COLUMN_NUMBER, false);

		Assert.isTrue(!couponModelDtos.isEmpty(), "Should have at least one coupon model dto"); //$NON-NLS-1$
		this.couponModelDtos = couponModelDtos;

		this.dataBindingContext = new DataBindingContext();
		this.model = model;
		this.singleEditing = couponModelDtos.size() == 1;
		this.isEditMode = isEditMode;
	}

	/**
	 * Constructs the Base Amount dialog.
	 * @param couponModelDtos The coupons to update.
	 * @param model The model
	 */
	public CouponEditingPopupDialog(final Collection<CouponModelDto> couponModelDtos, final CouponCollectionModel model) {
		this(couponModelDtos, model, false);
	}

	@Override
	protected String getPluginId() {
		return StorePlugin.PLUGIN_ID;
	}

	@Override
	public Object getModel() {
		return this.couponModelDtos;
	}

	@Override
	protected void populateControls() {
		if (!isSingleEditing()) {
			return;
		}

		CouponModelDto couponModelDto = getFirstCouponModelDto();
		if (couponModelDto.getCouponCode() != null) {
			this.couponCodeField.setText(couponModelDto.getCouponCode());
		}

		if (isEmailNeeded() && ((CouponUsageModelDto) couponModelDto).getEmailAddress() != null) {
			this.emailField.setText(((CouponUsageModelDto) couponModelDto).getEmailAddress());
		}
	}

	private boolean isEmailNeeded() {
		return getFirstCouponModelDto() instanceof CouponUsageModelDto;
	}

	/**
	 * Gets the first coupon model dto.
	 *
	 * @return the first coupon model dto.
	 */
	protected CouponModelDto getFirstCouponModelDto() {
		return couponModelDtos.iterator().next();
	}

	@Override
	protected void createButtonsForButtonBar(final Composite parent) {
		createEpButtonsForButtonsBar(ButtonsBarType.OK, parent);
	}

	@Override
	protected void createEpDialogContent(final IEpLayoutComposite dialogComposite) {

		final IEpLayoutData labelData = dialogComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL);
		final IEpLayoutData fieldData = dialogComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.CENTER, true, false, 2, 1);

		dialogComposite.addLabelBoldRequired(PromotionsMessages.get().CouponSingleEditorDialog_CouponCode, EpState.EDITABLE, labelData);
		this.couponCodeField = dialogComposite.addTextField(EpState.EDITABLE, fieldData);

		if (isEmailNeeded()) {
			dialogComposite.addLabelBoldRequired(PromotionsMessages.get().Coupon_Email, EpState.EDITABLE, labelData);
			this.emailField = dialogComposite.addTextField(EpState.EDITABLE, fieldData);
		}
	}

	@Override
	protected String getInitialMessage() {
		return null;
	}

	@Override
	protected String getTitle() {
		return dialogTitle;
	}

	@Override
	protected String getWindowTitle() {
		return windowTitle;
	}

	@Override
	protected Image getWindowImage() {
		return CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_ADD_NOTE);
	}

	@Override
	protected void bindControls() {
		if (!singleEditing) {
			return;
		}

		// For public coupons the coupon code needs to be unique.
		// For private coupons (isEmailNeeded) the coupon code can, and will, be shared
		// by many email addresses. However, the tuple of code and email address must
		// be unique. This check is handled in okPressed.

		CouponModelDto couponModelDto = getFirstCouponModelDto();

		if (isEmailNeeded()) {
			EpControlBindingProvider.getInstance().bind(dataBindingContext, this.couponCodeField, couponModelDto,
					"couponCode", //$NON-NLS-1$
					EpValidatorFactory.STRING_255_REQUIRED,
					null, true);
			EpControlBindingProvider.getInstance().bind(dataBindingContext, this.emailField, couponModelDto,
				"emailAddress", //$NON-NLS-1$
					new CompoundValidator(
							new IValidator[] {
									EpValidatorFactory.STRING_255_REQUIRED,
									EpValidatorFactory.EMAIL
							}
					),
				null, true);
		} else {
			EpControlBindingProvider.getInstance().bind(dataBindingContext, this.couponCodeField, couponModelDto,
					"couponCode", //$NON-NLS-1$
					new CompoundValidator(
							new IValidator[] {
									EpValidatorFactory.STRING_255_REQUIRED,
									new DuplicateCouponCodeValidator()
							}
					), null, true);
		}

		EpDialogSupport.create(this, dataBindingContext);
	}

	/**
	 * Checks that coupon code is not a duplicate.
	 */
	private class DuplicateCouponCodeValidator implements IValidator {

		@Override
		public IStatus validate(final Object value) {
			updateButtons();

			return Status.OK_STATUS;
		}
	}

	/**
	 * Called when the ok button is pressed. Validates that the couponCode, emailAddress
	 * pair is unique.
	 */
	@Override
	protected void okPressed() {
		if (!isEditMode) {
			ValidationState validationResult;
			if (isEmailNeeded()) {
				validationResult = model.getCouponValidator().isValid(couponCodeField.getText(), emailField.getText());
				if (!validationResult.isValid()) {
					String message;
					switch (validationResult.getReason()) {
					case SAME_PROMO_DUPLICATE:
						message = samePromoDuplicate();
						break;
					case OTHER_PROMO_DUPLICATE: /* intentional fall through */
					default:
						message =
							NLS.bind(PromotionsMessages.get().CouponSingleEditorDialog_DuplicateCouponWithCode,
							couponCodeField.getText());
						break;
					}
					MessageDialog.openError(Display.getDefault().getActiveShell(), CoreMessages.get().ApplicationWorkbenchAdvisor_Error_Title,
							message);
					return; // Keep this dialog box open.
				}
			} else {
				validationResult = model.getCouponValidator().isValid(couponCodeField.getText());
				if (!validationResult.isValid()) {
					String message =
						NLS.bind(PromotionsMessages.get().CouponSingleEditorDialog_DuplicateCouponWithCode,
						couponCodeField.getText());
					MessageDialog.openError(Display.getDefault().getActiveShell(), CoreMessages.get().ApplicationWorkbenchAdvisor_Error_Title,
							message);
					return; // Keep this dialog box open.
				}
			}
		}
		super.okPressed();
	}

	private String samePromoDuplicate() {
		String message;
		if (emailField.getText() == null || emailField.getText().trim().length() == 0) {
			message =
				NLS.bind(PromotionsMessages.get().CouponSingleEditorDialog_DuplicateCouponAndNullEmail,
				couponCodeField.getText(), emailField.getText());
		} else {
			message =
				NLS.bind(PromotionsMessages.get().CouponSingleEditorDialog_DuplicateCouponAndEmail,
				couponCodeField.getText(), emailField.getText());
		}
		return message;
	}
}
