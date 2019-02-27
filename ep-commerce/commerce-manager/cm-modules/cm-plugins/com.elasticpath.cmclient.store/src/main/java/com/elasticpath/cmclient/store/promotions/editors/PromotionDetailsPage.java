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
 * Represents the UI of the promotion details page.
 */
public class PromotionDetailsPage extends AbstractPolicyAwareEditorPage {

	private static final String PART_ID = PromotionDetailsPage.class.getName();
	
	private static final int NUM_FORM_COLUMNS = 1;
	
	private final boolean catalogPromotion;
	
	/**
	 * Constructor.
	 * 
	 * @param editor <code>FormEditor</code>
	 * @param catalogPromotion whether the create the page for a catalog promotion
	 */
	public PromotionDetailsPage(final AbstractCmClientFormEditor editor, final boolean catalogPromotion) {
		super(editor, PART_ID, PromotionsMessages.get().PromotionDetailsPage_Title);
		this.catalogPromotion = catalogPromotion;
	}

	@Override
	protected void addEditorSections(final AbstractCmClientFormEditor editor, final IManagedForm managedForm) {
		PolicyActionContainer promotionDetailsContainer = addPolicyActionContainer("promotionDetails"); //$NON-NLS-1$
		
		addPart(promotionDetailsContainer, managedForm, new PromotionDetailsOverviewPart(this, editor, catalogPromotion));
		addPart(promotionDetailsContainer, managedForm, new PromotionRulesSection(this, editor, catalogPromotion));

		getCustomPageData().put("catalogPromotion", this.catalogPromotion);
		addExtensionEditorSections(editor, managedForm, StorePlugin.PLUGIN_ID, this.getClass().getSimpleName());
	}

	@Override
	protected int getFormColumnsCount() {
		return NUM_FORM_COLUMNS;
	}

	@Override
	protected String getFormTitle() {
		return PromotionsMessages.get().PromotionDetailsPage_Form_Title;
	}

	@Override
	protected void addToolbarActions(final IToolBarManager toolBarManager) {
		// no toolbar actions
	}
}
