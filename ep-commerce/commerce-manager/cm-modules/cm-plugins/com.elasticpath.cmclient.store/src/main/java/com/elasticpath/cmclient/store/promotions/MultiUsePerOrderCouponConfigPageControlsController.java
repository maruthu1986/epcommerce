/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.store.promotions;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;

import com.elasticpath.cmclient.core.ui.framework.EpControlFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;

/**
 * A Specialized CouponConfig controller that manages the extra needs of the MultiUsePerOrder checkbox.
 * One of those extra needs is that the multi-use checkbox should only be enabled when the rule
 * has an action to discount an item (c.f. shipping or subtotal).
 */
public class MultiUsePerOrderCouponConfigPageControlsController extends CouponConfigPageControlsController {

	private boolean hasRuleWithCartItemAction;
	private Control affectedControl;

	private final Map<Control, EpState> managedChildControls = new HashMap<>();
	
	/**
	 * Constructor.
	 *
	 * @param modelUpdateStrategy the model update strategy
	 */
	public MultiUsePerOrderCouponConfigPageControlsController(final CouponConfigPageModelUpdateStrategy modelUpdateStrategy) {
		super(modelUpdateStrategy);
	}
	
	
	@Override
	protected void changeButtonState(final Button button, final boolean state) {
		button.setSelection(state);
		changeChildrenControlsState(button, state);
	}
	
	@Override
	protected void changeChildrenControlsState(final Control parentControl, final boolean state) {
		EpState epState;

		for (Control childControl : getControlGroups().get(parentControl)) {
			if (getModifiedState(childControl, state)) {
				epState = EpState.EDITABLE;
			} else {
				epState = EpState.DISABLED;
			}
			
			// We keep a cache so that applyStatePolicyState knows
			// what was decided here.
			// We use a map because it will automatically keep the latest state.
			managedChildControls.put(childControl, epState);
			EpControlFactory.changeEpState(childControl, epState);
		}
	}
	
	private boolean getModifiedState(final Control control, final boolean state) {
		// The control should only be enabled if the rule has an action which modifies a cart item (i.e. not shipping or sub total).
		boolean result = state;
		if (affectedControl != null && affectedControl.equals(control)) {
			result = hasRuleWithCartItemAction && state;
		}
		return result;
	}
	
	/**
	 * Set Flag that a CartItem Action does exist.
	 * Used to call back changes in the availability of actions.
	 *
	 * @param hasRuleWithCartItemAction the Flag.
	 */
	public void setHasRuleACartItemAction(final boolean hasRuleWithCartItemAction) {
		this.hasRuleWithCartItemAction = hasRuleWithCartItemAction;
	}
	
	/**
	 * Set the control that is affected by the presence of the CartItem Action.
	 * This allows tests against a nested set of controls.  Required because this control does not
	 * neatly fit into the screen organisational pattern - being shared by 2 sets of radio buttons but
	 * driven by one selection in each set.
	 *
	 * @param affectedControl the target control.
	 */
	public void setAffectedControl(final Control affectedControl) {
		this.affectedControl = affectedControl;
	}
	
	/**
	 * Applies the state determined by the StatePolicy to the controls
	 * managed by this controller. Assumes that applyStatePolicy has already 
	 * been called on all the control as normal. If the statePolicyState
	 * is not editable then no changes will be made (see previous assumption).
	 * If the statePolicyState is editable then the enable state is determined
	 * by the normal behaviour of this controller.
	 * 
	 * @param statePolicyState The the state from the state policy.
	 */
	public void applyStatePolicyState(final EpState statePolicyState) {
		// During a save or a new page load, 
		// applyStatePolicy is called after createControls and populateControls.
		// The result of determineState comes from the permissions and change set
		// policies. If either of those policies decide that the controls should be 
		// disabled then the controls should be disabled regardless of the 
		// {private/public}CouponsSettingController.
		// However, if the policies decide that the state should be editable then
		// the enabled/disabled state should be decided by the {private/public}CouponsSettingController.
		if (statePolicyState.equals(EpState.EDITABLE)) {			
			for (Map.Entry<Control, EpState> mapEntry : managedChildControls.entrySet()) {				
				EpControlFactory.changeEpState(mapEntry.getKey(), mapEntry.getValue());							
			}
		}				
	}
}
