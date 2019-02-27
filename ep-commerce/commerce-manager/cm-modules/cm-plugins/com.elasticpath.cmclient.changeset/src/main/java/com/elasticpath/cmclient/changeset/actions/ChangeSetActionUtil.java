/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.changeset.actions;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.dialogs.IDialogLabelKeys;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.ISaveablePart2;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;

import com.elasticpath.cmclient.catalog.editors.ViewSynchronizer;
import com.elasticpath.cmclient.changeset.ChangeSetMessages;
import com.elasticpath.cmclient.changeset.ChangeSetPlugin;
import com.elasticpath.cmclient.changeset.editors.support.ChangeSetInfoEditorPage;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.policy.PolicyPlugin;
import com.elasticpath.cmclient.policy.StatePolicyGovernable;
import com.elasticpath.cmclient.policy.StatePolicyTarget;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareFormEditor;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.changeset.ChangeSet;
import com.elasticpath.service.changeset.ChangeSetService;
/**
 * The utility class of change set actions.
 */
public class ChangeSetActionUtil {
	
	/**
	 * Saves and reloads all the editors.
	 * 
	 * @param changeSet the change set that might hold any open editors
	 * @param workbenchWindow the work bench window  
	 * @return whether the save was successful
	 */
	public boolean saveAndReloadEditors(final ChangeSet changeSet, final IWorkbenchWindow workbenchWindow) {
		if (changeSet == null) {
			return true;
		}
		
		final IWorkbenchPage activePage = workbenchWindow.getActivePage();
		final IEditorReference[] openEditorReferences = activePage.getEditorReferences();

		// check for open editors and ask the user to save them
		for (IEditorReference editorReference : openEditorReferences) {
			final AbstractCmClientFormEditor cmClientFormEditor = (AbstractCmClientFormEditor) editorReference.getEditor(false);

			ViewSynchronizer viewSynchronizer = (ViewSynchronizer) cmClientFormEditor.getAdapter(ViewSynchronizer.class);
			if (viewSynchronizer != null && !viewSynchronizer.saveOrReload()) {
				return false;
			} 

			// only try to save the editor if the object is part of the change set being switched
			if (isSaveNeeded(cmClientFormEditor, changeSet)) {
				int choice = getUserResponse();
				if (choice == ISaveablePart2.YES) {
					activePage.saveEditor(cmClientFormEditor, false);
				} else if (choice == ISaveablePart2.NO) {
					cmClientFormEditor.reload();
				} else if (choice == ISaveablePart2.CANCEL) {
					return false;
				}
			}
		}
		return true;
	}
	
	
	/**
	 * Check if we need to save the editor. Will return true if the object is part of the change set,
	 * the editor is dirty and the editor needs to be saved on close. 
	 * @param editorPart the editor to save
	 * @param changeSet the change that might hold any open editors
	 * @return true if we should try to save.
	 */
	protected boolean isSaveNeeded(final AbstractCmClientFormEditor editorPart, final ChangeSet changeSet) {
		return (isObjectPartOfChangeSet(editorPart.getDependentObject(), changeSet)
				|| isChangeSetEditor(editorPart.getDependentObject(), changeSet))
			&& editorPart.isDirty() && editorPart.isSaveOnCloseNeeded();
	}

	/**
	 * Pop a dialog to get the user response to saving. 
	 * @return the ISaveablePart2 constant for  the user's choice {YES|NO|CANCEL}
	 */
	protected int getUserResponse() {
		int[] result = new int[] { ISaveablePart2.YES, ISaveablePart2.NO, ISaveablePart2.CANCEL};
		String[] buttonLabels = new String[] { 
				JFaceResources.getString(IDialogLabelKeys.YES_LABEL_KEY),
				JFaceResources.getString(IDialogLabelKeys.NO_LABEL_KEY),
				JFaceResources.getString(IDialogLabelKeys.CANCEL_LABEL_KEY) };
		MessageDialog dialog = new MessageDialog(null,
				ChangeSetMessages.get().ChangeSetSwitchAction_SaveChangetSetDialogTitle, null,
				ChangeSetMessages.get().ChangeSetSwitchAction_SaveChangetSetDialogText,
				MessageDialog.QUESTION,
				buttonLabels, 0); // default yes
		
		return result[dialog.open()];
	}
	

    
    /**
	 * Checks whether an object is a part of a change set.
	 * 
	 * @param object the object to check
	 * @param changeSet the change set
	 * @return true if the object is a part of the change set
	 */
	public boolean isObjectPartOfChangeSet(final Object object, final ChangeSet changeSet) {
		if (changeSet == null) {
			return false;
		}
		ChangeSetService changeSetService = getChangeSetService();
		return changeSetService.getStatus(object).isMember(changeSet.getGuid());
	}
	
	private boolean isChangeSetEditor(final Object dependentObject, final ChangeSet changeSet) {
		if (changeSet == null) {
			return false;
		}
		if (dependentObject instanceof ChangeSet) {
			return changeSet.equals(dependentObject);
		}
		return false;
	}

	/**
	 * Get the change set service bean.
	 * @return the change set service bean.
	 */
	protected ChangeSetService getChangeSetService() {
		return ServiceLocator.getService(ContextIdNames.CHANGESET_SERVICE);
	}

	/**
	 * Applies the policy to all the components being part of any of the given change sets.
	 * 
	 * @param previousChangeSet the change set that we switch from
	 * @param newActiveChangeSet the change set that the new active change set
	 * @param activePage the active page containing the components
	 */
	public void applyStatePolicyToComponents(final ChangeSet previousChangeSet, final ChangeSet newActiveChangeSet, final IWorkbenchPage activePage) {

		final IEditorReference[] openEditorReferences = activePage.getEditorReferences();

		// refresh the editor states by applying the policy
		for (IEditorReference editorReference : openEditorReferences) {
			final IEditorPart editorPart = editorReference.getEditor(false);
			final AbstractCmClientFormEditor cmClientFormEditor = (AbstractCmClientFormEditor) editorPart;
			//FIXME: different pages in one editor may depend on different object 
			//It should be fixed in BB-1303
			if (isObjectPartOfChangeSet(cmClientFormEditor.getDependentObject(), previousChangeSet)
					|| isObjectPartOfChangeSet(cmClientFormEditor.getDependentObject(), newActiveChangeSet)) {
				AbstractPolicyAwareFormEditor policyEditor = (AbstractPolicyAwareFormEditor) cmClientFormEditor;
				policyEditor.applyStatePolicy();
			}
		}
		
		//refresh the target if the dependent object is related to the change set
		//It may applyStatePolicy on an editor more than once if it has a policy dependent.
		//It should be fixed in BB-1303
		for (StatePolicyTarget target : PolicyPlugin.getDefault().getRegisteredStatePolicyTargets()) {
			Set<Object> dependents = flatPolicyDependent(target);
			for (Object dependent : dependents) { 
				if (isObjectPartOfChangeSet(dependent, previousChangeSet)
						|| isObjectPartOfChangeSet(dependent, newActiveChangeSet)) {
					target.applyStatePolicy(PolicyPlugin.getDefault().getStatePolicy(target.getTargetIdentifier()));
					break;
				}
			}
		}
	}
	

	private Set<Object> flatPolicyDependent(final StatePolicyGovernable statePolicyGovernable) {
		Set<Object> dependents = new HashSet<>();
		Map<String, PolicyActionContainer> policyActionContainers = statePolicyGovernable.getPolicyActionContainers();
		for (PolicyActionContainer targetContainer : policyActionContainers.values()) {
			Object policyDependent = targetContainer.getPolicyDependent();
			if (policyDependent != null) {
				if (policyDependent instanceof Collection< ? >) {
					dependents.addAll((Collection< ? >) policyDependent);
				} else {
					dependents.add(policyDependent);
				}
			}
			for (StatePolicyGovernable governable : targetContainer.getDelegates()) {
				dependents.addAll(flatPolicyDependent(governable));
			}
		}
		return dependents;
	}
	
	/**
	 * Refreshes the currently active change set.
	 */
	public void refreshActiveChangeSet() {
		ChangeSetPlugin.getDefault().setActiveChangeSet(ChangeSetPlugin.getDefault().getActiveChangeSet());
	}

	/**
	 * Refresh the change set info page of the given editor.
	 *
	 * @param activeEditor the editor whose change set info page should be refreshed
	 */
	public void refreshChangeSetInfoPage(final AbstractCmClientFormEditor activeEditor) {
		activeEditor.refreshPage(ChangeSetInfoEditorPage.PART_ID);
	}

	/**
	 * Refresh the change set info pages of any open editors.
	 *
	 * @param activePage the active page containing the editors
	 */
	public void refreshEditorChangeSetInfoPages(final IWorkbenchPage activePage) {
		final IEditorReference[] openEditorReferences = activePage.getEditorReferences();
		for (IEditorReference editorReference : openEditorReferences) {
			final IEditorPart editorPart = editorReference.getEditor(false);
			if (editorPart instanceof AbstractCmClientFormEditor) {
				final AbstractCmClientFormEditor cmClientFormEditor = (AbstractCmClientFormEditor) editorPart;
				refreshChangeSetInfoPage(cmClientFormEditor);
			}
		}
	}
		
}
