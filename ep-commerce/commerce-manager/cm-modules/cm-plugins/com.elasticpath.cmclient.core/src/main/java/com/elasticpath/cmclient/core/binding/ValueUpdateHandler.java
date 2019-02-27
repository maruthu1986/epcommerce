/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.binding;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.graphics.Point;

import com.elasticpath.cmclient.core.CmClientResources;
import com.elasticpath.cmclient.core.ui.framework.impl.EpTextDateTimePicker;

/**
 * This class handles the changes in control decoration status.
 */
public class ValueUpdateHandler implements UpdateValueStrategyListener {

	private static final int DATE_TIME_PICKER_IMAGE_WIDTH = 22;

	/**
	 * 
	 */
	private static final int ERROR_ICON_WIDTH = 25;

	private final ControlDecoration controlDecoration;

	private final boolean hideDecorationOnFirstValidation;

	private boolean firstValidationPass = true;

	private boolean resized;

	private int changedSize;

	/**
	 * Constructor.
	 * 
	 * @param controlDecoration the control decoration
	 * @param hideDecorationOnFirstValidation whether or not to hide decoration on first validation
	 */
	public ValueUpdateHandler(final ControlDecoration controlDecoration, final boolean hideDecorationOnFirstValidation) {
		this.controlDecoration = controlDecoration;
		this.hideDecorationOnFirstValidation = hideDecorationOnFirstValidation;
	}

	/**
	 * Called when input is validated on a control. This implementation updates the Control Decorators.
	 * 
	 * @param validationStatus the validation status
	 */
	public void inputValidated(final IStatus validationStatus) {
		if (validationStatus == Status.OK_STATUS || this.hideDecorationOnFirstValidation && this.firstValidationPass) {
			this.firstValidationPass = false;
			controlDecoration.hide();
			controlDecoration.setShowHover(false);
			// Set to default color
			controlDecoration.getControl().setBackground(CmClientResources.getBackgroundColor());
			if (resized) {
				final Point size = controlDecoration.getControl().getSize();
				if (changedSize == size.x) {
					size.x += ERROR_ICON_WIDTH;
					controlDecoration.getControl().setSize(size);
					resized = false;
				}
			}
		} else {
			if (!resized) {
				final Point size = controlDecoration.getControl().getSize();
				if (size.x > 0) {
					controlDecoration.setMarginWidth(0);
					size.x -= ERROR_ICON_WIDTH;
					controlDecoration.getControl().setSize(size);
					resized = true;
					changedSize = size.x;
				} else if (isDateComponent()) {
					// checks if the widget belongs to the date/time component and sets the margin
					// in order to show the error icon at the right of the date image.
					controlDecoration.setMarginWidth(DATE_TIME_PICKER_IMAGE_WIDTH);
				} else {
					// In the case where value of hideDecorationOnFirstValidation = false and the validation fails
					// the first time the size.x is 0 and is causing 2 error icons to be displayed when another error is triggered, 
					// before fixing the first one.
					resized = true;
				}

			}
			controlDecoration.show();
			controlDecoration.setShowHover(true);
			controlDecoration.setDescriptionText(validationStatus.getMessage());
			controlDecoration.getControl().setBackground(CmClientResources.getColor(CmClientResources.COLOR_FIELD_ERROR));
		}
	}
	/**
	 * @param control
	 * @return
	 */
	private boolean isDateComponent() {
		return EpTextDateTimePicker.DATE_TIME_COMPONENT_ID.equals(controlDecoration.getControl().getData());
	}
}