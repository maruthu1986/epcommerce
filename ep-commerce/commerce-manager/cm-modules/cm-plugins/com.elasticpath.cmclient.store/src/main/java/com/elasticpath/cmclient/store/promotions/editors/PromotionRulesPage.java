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
public class PromotionRulesPage extends AbstractPolicyAwareEditorPage {

	private static final String PART_ID = PromotionRulesPage.class.getName();
	
	private static final int NUM_FORM_COLUMNS = 1;
	
	private final boolean catalogPromotion;
	
	/**
	 * Constructor.
	 * 
	 * @param editor <code>FormEditor</code>
	 * @param catalogPromotion whether to create the page for a catalog promotion
	 */
	public PromotionRulesPage(final AbstractCmClientFormEditor editor, final boolean catalogPromotion) {
		super(editor, PART_ID, PromotionsMessages.get().PromotionRulesPage_Title);
		this.catalogPromotion = catalogPromotion;
	}

	@Override
	protected void addEditorSections(final AbstractCmClientFormEditor editor, final IManagedForm managedForm) {
		PolicyActionContainer container = addPolicyActionContainer("section"); //$NON-NLS-1$
		
		// Create the sections
		addPart(container, managedForm, new PromotionRulesDefinitionPart(this, editor, catalogPromotion));
		getCustomPageData().put("catalogPromotion", this.catalogPromotion);
		addExtensionEditorSections(editor, managedForm, StorePlugin.PLUGIN_ID, this.getClass().getSimpleName());
	}

	@Override
	protected int getFormColumnsCount() {
		return NUM_FORM_COLUMNS;
	}

	@Override
	protected String getFormTitle() {
		return PromotionsMessages.get().PromotionRulesPage_Form_Title;
	}

	@Override
	protected void addToolbarActions(final IToolBarManager toolBarManager) {
		// no toolbar actions
	}
}
