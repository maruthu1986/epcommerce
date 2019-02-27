/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.cmclient.admin.datapolicies.actions;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;

import com.elasticpath.cmclient.admin.datapolicies.AdminDataPoliciesMessages;
import com.elasticpath.cmclient.admin.datapolicies.editors.DataPolicyEditor;
import com.elasticpath.cmclient.admin.datapolicies.editors.DataPolicyEditorInput;
import com.elasticpath.cmclient.admin.datapolicies.views.DataPolicyListView;
import com.elasticpath.domain.datapolicy.DataPolicy;

/**
 * Action to create a new data policy.
 */
public class CreateDataPolicyAction extends Action {

	private static final Logger LOG = Logger.getLogger(CreateDataPolicyAction.class);

	private final DataPolicyListView listView;

	/**
	 * Constructor.
	 *
	 * @param listView        the customer list view
	 * @param text            the action's text
	 * @param imageDescriptor the action's image
	 */
	public CreateDataPolicyAction(final DataPolicyListView listView,
								  final String text,
								  final ImageDescriptor imageDescriptor) {
		super(text, imageDescriptor);
		this.listView = listView;
	}

	@Override
	public void run() {
		final IEditorInput editorInput = new DataPolicyEditorInput(
				AdminDataPoliciesMessages.get().DataPolicyEditor_NewSegmentName, 0, DataPolicy.class);
		try {
			listView.getSite().getWorkbenchWindow().getActivePage().openEditor(editorInput, DataPolicyEditor.ID_EDITOR);
		} catch (PartInitException e) {
			LOG.error(e.getMessage());
		}
	}
}
