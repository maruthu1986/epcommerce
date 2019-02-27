/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.store.promotions.editors;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.ui.forms.IManagedForm;

import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareEditorPage;
import com.elasticpath.cmclient.store.StorePlugin;
import com.elasticpath.cmclient.store.promotions.CouponConfigPageModel;
import com.elasticpath.cmclient.store.promotions.CouponEditorValidatorImpl;
import com.elasticpath.cmclient.store.promotions.CouponPageModel;
import com.elasticpath.cmclient.store.promotions.PromotionsMessages;

/**
 * Represents the UI of the coupon editor page.
 */
public class CouponEditorPage extends AbstractPolicyAwareEditorPage {

	/** .*/
	public static final String COUPON_EDITOR = "couponEditor"; //$NON-NLS-1$

	private static final String PART_ID = CouponEditorPage.class.getName();
	
	private static final int NUM_FORM_COLUMNS = 1;
	
	private static final int HORIZONTAL_SPACING = 15;

	private CouponPageModel model;

	/**
	 * Constructor.
	 * 
	 * @param editor <code>FormEditor</code>
	 */
	public CouponEditorPage(final ShoppingCartPromotionsEditor editor) {
		super(editor, PART_ID, PromotionsMessages.get().CouponEditorPage_Title);
	}

	@Override
	protected void addEditorSections(final AbstractCmClientFormEditor editor, final IManagedForm managedForm) {
		final PolicyActionContainer container = addPolicyActionContainer(COUPON_EDITOR); 

		CouponConfigPageModel couponConfigPageModel = ((ShoppingCartPromotionsEditor) editor).getCouponConfigPageModel();
		
		model = new CouponPageModel();
		CouponEditorValidatorImpl validator = new CouponEditorValidatorImpl();
		validator.setModel(model);
		couponConfigPageModel.getCouponUsageCollectionModel().setCouponValidator(validator);
		validator.setRuleCode(couponConfigPageModel.getCouponConfig().getRuleCode());
		
		addPart(container, managedForm, new CouponEditorPart(this, editor, model, couponConfigPageModel));
		addExtensionEditorSections(editor, managedForm, StorePlugin.PLUGIN_ID, this.getClass().getSimpleName());
	}

	@Override
	protected int getFormColumnsCount() {
		return NUM_FORM_COLUMNS;
	}

	@Override
	protected String getFormTitle() {
		return PromotionsMessages.get().CouponEditorPage_Form_Title;
	}

	@Override
	protected void addToolbarActions(final IToolBarManager toolBarManager) {
		// no toolbar actions
	}

	/**
	 * @return the model which represents the data modified by this page.
	 */
	public CouponPageModel getModel() {
		return model;
	}
	
	@Override
	protected Layout getLayout() {
		GridLayout layout = new GridLayout(getFormColumnsCount(), true);
		layout.marginLeft = LEFT_MARGIN;
		layout.marginRight = RIGHT_MARGIN;
		layout.horizontalSpacing = HORIZONTAL_SPACING;

		return layout;

	}
}
