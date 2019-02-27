/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
/**
 * 
 */
package com.elasticpath.cmclient.store.promotions.editors;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.ExpandableComposite;

import com.elasticpath.cmclient.conditionbuilder.editor.ModelWrapper;
import com.elasticpath.cmclient.conditionbuilder.editor.TimeSectionPart;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareEditorPage;
import com.elasticpath.cmclient.store.StorePlugin;
import com.elasticpath.cmclient.store.promotions.PromotionsMessages;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.sellingcontext.SellingContext;
import com.elasticpath.tags.domain.TagDictionary;

/**
 * A promotion time page.
 *
 */
public class PromotionTimePage extends AbstractPolicyAwareEditorPage {

	private static final String PART_ID = PromotionTimePage.class.getName();
	
	/**
	 * Default constructor.
	 * @param editor an editor
	 * @param isLocaleDependent is locale dependent page
	 */
	public PromotionTimePage(final AbstractCmClientFormEditor editor, final boolean isLocaleDependent) {
		super(editor, PART_ID, PromotionsMessages.get().PromotionTimePage_Title, isLocaleDependent);
	}

	@Override
	protected void addEditorSections(final AbstractCmClientFormEditor editor, final IManagedForm managedForm) {

		PolicyActionContainer policyContainer = addPolicyActionContainer("promotionTimeRange"); //$NON-NLS-1$
		
		ModelWrapper<SellingContext> modelWrapper =
				() -> ((Rule) editor.getModel()).getSellingContext();
		addPart(policyContainer, managedForm, 
				new TimeSectionPart(this, editor, ExpandableComposite.EXPANDED, 
						TagDictionary.DICTIONARY_TIME_GUID, modelWrapper));
		addExtensionEditorSections(editor, managedForm, StorePlugin.PLUGIN_ID, this.getClass().getSimpleName());

	}

	@Override
	protected void addToolbarActions(final IToolBarManager toolBarManager) {
		// empty
	}

	@Override
	protected int getFormColumnsCount() {
		return 1;
	}

	@Override
	protected String getFormTitle() {
		return PromotionsMessages.get().PromotionTimePage_Title;
	}

}
