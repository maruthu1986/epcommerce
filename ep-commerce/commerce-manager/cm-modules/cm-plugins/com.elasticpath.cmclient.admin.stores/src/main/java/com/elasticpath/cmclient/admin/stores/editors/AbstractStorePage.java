/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.admin.stores.editors;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.actions.ActionDelegate;

import com.elasticpath.cmclient.admin.stores.actions.ChangeStoreStateAction;
import com.elasticpath.cmclient.core.editors.AbstractCmClientEditorPage;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.util.PullDownDelegateUtil;

/**
 * The abstract Store Editor Page, provide the unified action bar functionality.
 */
@SuppressWarnings("PMD.AbstractClassWithoutAbstractMethod")
public abstract class AbstractStorePage extends AbstractCmClientEditorPage {
	
	private final boolean editable;
	
	/**
	 * Creates AbstractStorePage instance.
	 * 
	 * @param editor the parent editor.
	 * @param partId the ID of the part.
	 * @param title the title of the page.
	 * @param editable whether the store page should be rendered as editable for the current user. Typically this boolean's
	 * value is determined by the user's permission on the store being opened in the store editor.
	 */
	public AbstractStorePage(final AbstractCmClientFormEditor editor, final String partId, final String title, final boolean editable) {
		super(editor, partId, title);
		this.editable = editable;
	}

	@Override
	protected void addToolbarActions(final IToolBarManager toolBarManager) {
		final StoreEditor storeEditor = (StoreEditor) getEditor();
		final Action changeStoreStateAction = new ChangeStoreStateAction(storeEditor, new ChangeStoreStateActionDelegate());
		changeStoreStateAction.setEnabled(isEditable());
		addToolbarActionItem(toolBarManager, changeStoreStateAction);
	}

	/**
	 * Action delegate for the ChangeStoreStateAction.
	 * */
	class ChangeStoreStateActionDelegate extends ActionDelegate {
		@Override
		public void runWithEvent(final IAction action, final Event event) {
			PullDownDelegateUtil.runWithEvent(event);
		}
	}

	/**
	 * Create toolbar action item.
	 * @param toolBarManager the tool bar manager
	 * @param action to be added
	 */
	private void addToolbarActionItem(final IToolBarManager toolBarManager, final Action action) {
		final ActionContributionItem result = new ActionContributionItem(action);
		result.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		toolBarManager.add(result);
	}
	
	/**
	 * @return true if the current user is authorized to edit the current store, false if not.
	 */
	protected boolean isEditable() {
		return editable;
	}

}
