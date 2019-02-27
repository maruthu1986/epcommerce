/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.store.promotions.editors;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.forms.IManagedForm;

import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareEditorPage;
import com.elasticpath.cmclient.store.StorePlugin;
import com.elasticpath.cmclient.store.promotions.PromotionsMessages;

/**
 * Represents the UI of the promotion rules page.
 */
public class CouponConfigEditorPage extends AbstractPolicyAwareEditorPage {

	/** . */
	public static final String COUPON_CONFIG_EDITOR_PAGE = "promotionCoupons"; //$NON-NLS-1$

	private static final String PART_ID = CouponConfigEditorPage.class.getName();
	
	private static final int NUM_FORM_COLUMNS = 1;
	
	private CouponConfigEditorPart part;

	/**
	 * Constructor.
	 * 
	 * @param editor <code>FormEditor</code>
	 */
	public CouponConfigEditorPage(final ShoppingCartPromotionsEditor editor) {
		super(editor, PART_ID, PromotionsMessages.get().PromotionCouponsPage_Title);
	}
	
	@Override
	public void setActive(final boolean active) {
		super.setActive(active);
		if (active) {
			part.notifyRefresh();
		}
	}
	
	@Override
	protected void addEditorSections(final AbstractCmClientFormEditor editor, final IManagedForm managedForm) {
		final PolicyActionContainer container = addPolicyActionContainer(COUPON_CONFIG_EDITOR_PAGE);
		// Create the sections
		part =	new CouponConfigEditorPart(this, (ShoppingCartPromotionsEditor) editor);
		addPart(container, managedForm, part);
		addExtensionEditorSections(editor, managedForm, StorePlugin.PLUGIN_ID, this.getClass().getSimpleName());

	}

	@Override
	protected int getFormColumnsCount() {
		return NUM_FORM_COLUMNS;
	}

	@Override
	protected String getFormTitle() {
		return PromotionsMessages.get().PromotionCouponsPage_Form_Title;
	}

	@Override
	protected void addToolbarActions(final IToolBarManager toolBarManager) {
		// no toolbar actions
	}
}
