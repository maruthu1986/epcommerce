/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.store.promotions;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;

import com.elasticpath.cmclient.core.ui.framework.EpControlFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;

/**
 * A controller that control the state of the radio buttons selection.
 */
public class CouponConfigPageControlsController {
	private final Map<Control, Set<Control>> controlGroups = new LinkedHashMap<>();

	private Button currentSelectedButton;

	private final CouponConfigPageModelUpdateStrategy modelUpdateStrategy;

	/**
	 * Constructor.
	 * 
	 * @param modelUpdateStrategy {@link CouponConfigPageModelUpdateStrategy}.
	 */
	public CouponConfigPageControlsController(final CouponConfigPageModelUpdateStrategy modelUpdateStrategy) {
		this.modelUpdateStrategy = modelUpdateStrategy;
	}

	/**
	 * Adds child control to a certain button.
	 * 
	 * @param parentControl parent radio button.
	 * @param childControl child control.
	 */
	public void addDependentControl(final Control parentControl, final Control childControl) {
		Set<Control> childControls = controlGroups.computeIfAbsent(parentControl, key -> new HashSet<>());

		if (childControl != null) {
			childControls.add(childControl);
		}
	}
	
	/**
	 * Gets all the parent controls in this controller.
	 * 
	 * @return a collection of parent controls.
	 */
	public Collection<Control> getParentControls() {
		return controlGroups.keySet();
	}

	/**
	 * Gets the current selection.
	 * 
	 * @return gets the current selected radio button.
	 */
	public Button getSelection() {
		return currentSelectedButton;
	}

	/**
	 * Selects the parent button and set its state. It will set the reverse state to other buttons in the group.
	 * 
	 * @param button parent button.
	 * @param state the state.
	 */
	public void select(final Button button, final boolean state) {
		if (!controlGroups.containsKey(button)) {
			return;
		}

		updateView(button, state);
		modelUpdateStrategy.updateModel(button);
	}

	private void updateView(final Button button, final boolean state) {
		currentSelectedButton = button;
		// setting sibling radio button control states first
		if (isRadioButton(button)) {
			boolean siblingState = !state;
			Set<Button> siblingRadioButtons = getSiblingRadioButtons(button);
			for (Button siblingButton : siblingRadioButtons) {
				changeButtonState(siblingButton, siblingState);
			}
		}
		
		// setting the right state for current selected controls
		changeButtonState(button, state);
	}


	/**
	 * change the button state.
	 *
	 * @param button to use.
	 * @param state to change to.
	 */
	protected void changeButtonState(final Button button, final boolean state) {
		button.setSelection(state);
		changeChildrenControlsState(button, state);
	}

	private Set<Button> getSiblingRadioButtons(final Button radioButton) {
		Set<Button> siblingRadioButtons = new HashSet<>();
		for (Control control : controlGroups.keySet()) {
			if (!control.equals(radioButton) && control instanceof Button && isRadioButton((Button) control)) {
				siblingRadioButtons.add((Button) control);
			}
		}

		return siblingRadioButtons;
	}

	private boolean isRadioButton(final Button button) {
		return (button.getStyle() & SWT.RADIO) != 0;
	}

	/**
	 * Change Enabled state of child widgets.
	 *
	 * @param parentControl the parent - usually a radio button.
	 * @param state the enable/disable state flag.
	 */
	protected void changeChildrenControlsState(final Control parentControl, final boolean state) {
		EpState epState = EpState.DISABLED;
		if (state) {
			epState = EpState.EDITABLE;
		}

		for (Control childControl : controlGroups.get(parentControl)) {
			EpControlFactory.changeEpState(childControl, epState);
		}
	}
	
	/**
	 * Get the control groups for inheriting classes.
	 *
	 * @return the control groups.
	 */
	protected Map<Control, Set<Control>> getControlGroups() {
		return controlGroups;
	}

}
