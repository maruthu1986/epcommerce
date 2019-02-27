/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
/**
 * 
 */
package com.elasticpath.cmclient.store.targetedselling.conditionalexpression.editors;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.forms.IManagedForm;

import com.elasticpath.cmclient.core.editors.AbstractCmClientEditorPageSectionPart;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareEditorPage;
import com.elasticpath.cmclient.store.StorePlugin;
import com.elasticpath.cmclient.store.targetedselling.TargetedSellingMessages;
import com.elasticpath.tags.domain.TagDictionary;

/**
 * ConditionBuilderPage.
 * @author ynovikov
 *
 */
public class ConditionBuilderPage extends AbstractPolicyAwareEditorPage {
	
	/** Page ID. **/
	public static final String PAGE_ID = "ConditionBuilderPage"; //$NON-NLS-1$
	
	private AbstractCmClientEditorPageSectionPart part;
	/**
	 * Custom constructor.
	 * @param editor named condition editor
	 */
	public ConditionBuilderPage(final AbstractCmClientFormEditor editor) {
		super(editor, PAGE_ID, TargetedSellingMessages.get().ConditionBuilder);
	}

	@Override
	protected void addEditorSections(final AbstractCmClientFormEditor editor,
			final IManagedForm managedForm) {
		String tagDictionaryGUID = ((ConditionEditor) editor).getDependentObject().getTagDictionaryGuid();
		PolicyActionContainer container = addPolicyActionContainer("conditionBuilderPage"); //$NON-NLS-1$
		
		addPart(container, managedForm, getPart(tagDictionaryGUID, editor));
		addExtensionEditorSections(editor, managedForm, StorePlugin.PLUGIN_ID, this.getClass().getSimpleName());
	}

	private AbstractCmClientEditorPageSectionPart getPart(final String tagDictionaryGUID, final AbstractCmClientFormEditor editor) {
		if (part == null) {
			if (TagDictionary.DICTIONARY_SHOPPER_GUID.equalsIgnoreCase(tagDictionaryGUID)) {
				part = new ShopperConditionBuilderSection(this, editor);
			} else if (TagDictionary.DICTIONARY_TIME_GUID.equalsIgnoreCase(tagDictionaryGUID)) {
				part = new TimeConditionBuilderSection(this, editor);
			} 	else {
				part = new StoresConditionBuilderSection(this, editor);
			}
		}	
		return part;
	}
	
	@Override
	protected void addToolbarActions(final IToolBarManager toolBarManager) {
		// TODO Auto-generated method stub
	}

	@Override
	protected int getFormColumnsCount() {
		return 1;
	}

	@Override
	protected String getFormTitle() {
		return null;
	}

	@Override
	public void pageDisposed() {
		super.pageDisposed();
		this.part.dispose();
		this.part = null;
	}

}
