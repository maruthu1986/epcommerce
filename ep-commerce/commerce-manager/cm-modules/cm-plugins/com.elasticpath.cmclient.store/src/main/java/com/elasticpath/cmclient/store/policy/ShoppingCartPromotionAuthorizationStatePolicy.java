/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.store.policy;

import java.util.Arrays;
import java.util.Collection;

import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.policy.StateDeterminer;
import com.elasticpath.cmclient.policy.common.AbstractStatePolicyImpl;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.store.promotions.PromotionsPermissions;
import com.elasticpath.cmclient.store.promotions.editors.CouponConfigEditorPage;
import com.elasticpath.cmclient.store.promotions.editors.CouponEditorPage;
import com.elasticpath.cmclient.store.promotions.editors.CouponEditorPart;
import com.elasticpath.cmclient.store.promotions.wizard.CouponConfigWizardPage;
import com.elasticpath.domain.rules.Rule;

/**
 * A <code>StatePolicy</code> that will determine UI state for product related UI elements.
 * It will return editable based on the following criteria:
 * <ol>
 *   <li>The user has permission to the product's catalog</li>
 *   <li>The user has permission to edit products</li>
 * </ol>
 */
public class ShoppingCartPromotionAuthorizationStatePolicy extends AbstractStatePolicyImpl {

	private Rule rule;
		
	/**
	 * Construct an instance of this policy that ties to the product guid.
	 * 
	 * @param rule the <code>Rule</code> this policy applies to
	 */
	@Override
	public void init(final Object rule) {
		this.rule = (Rule) rule;
	}
	
	/**
	 * Determine the UI state of targets based on the following policy.
	 * 
	 * Controls are editable only if the user is authorized to edit products
	 * 
	 * @param targetContainer a set of policy targets
	 * @return the <code>EpState</code> determined by the policy.
	 */
	@Override
	public EpState determineState(final PolicyActionContainer targetContainer) {
		if (getReadOnlyContainerNames().contains(targetContainer.getName())) {
			return EpState.READ_ONLY;
		}
		
		if (getCouponDeterminerContainers().contains(targetContainer.getName())) {
			return new CouponManagementDeterminer().determineState(targetContainer);
		}
		
		StateDeterminer determiner = new StoreAuthorizationDeterminer();
		return determiner.determineState(targetContainer);
	}
	
	
	/**
	 * @return collection of coupon related containers
	 */
	protected Collection<String> getCouponDeterminerContainers() {
		return Arrays.asList(
				CouponConfigEditorPage.COUPON_CONFIG_EDITOR_PAGE,
				CouponConfigWizardPage.COUPON_WIZARD_CONFIG_PAGE,
				CouponEditorPage.COUPON_EDITOR,
				CouponEditorPart.OBJECTS_DISPLAY_SECTION,
				CouponEditorPart.OBJECTS_SECTION
				);
	}

	/**
	 * Get the collection of names of <code>PolicyTargetContainer</code> objects
	 * that should should always have a read only status.
	 * 
	 * @return the collection of names
	 */
	protected Collection<String> getReadOnlyContainerNames() {
		return Arrays.asList(
				"overviewDisplayControls",  //$NON-NLS-1$
				"overviewRuleDisplayControls",  //$NON-NLS-1$
				"rulesDisplayControls", //$NON-NLS-1$
				"promotionCoupons"); //$NON-NLS-1$
	}
	
	/**
	 * Default state determiner.
	 */
	public class StoreAuthorizationDeterminer implements StateDeterminer {
		/**
		 * Determine the state based on authorization.
		 * @param targetContainer the target container
		 * @return the <code>EpState</code> determined by the authorization.
		 */
		@Override
		public EpState determineState(final PolicyActionContainer targetContainer) {
			if (AuthorizationService.getInstance().isAuthorizedWithPermission(PromotionsPermissions.PROMOTION_MANAGE)
					&& AuthorizationService.getInstance().isAuthorizedForStore(rule.getStore())) {
				
				return EpState.EDITABLE;
			}
			return EpState.READ_ONLY;
		}
	}

	/**
	 * Coupon UI state determiner.
	 */
	public class CouponManagementDeterminer implements StateDeterminer {
		/**
		 * Determine the state based on authorization.
		 * @param targetContainer the target container
		 * @return the <code>EpState</code> determined by the authorization.
		 */
		@Override
		public EpState determineState(final PolicyActionContainer targetContainer) {
			if (AuthorizationService.getInstance().isAuthorizedWithPermission(PromotionsPermissions.COUPONS_MANAGE)) {
				return EpState.EDITABLE;
			}
			return EpState.READ_ONLY;
		}
	}
}
