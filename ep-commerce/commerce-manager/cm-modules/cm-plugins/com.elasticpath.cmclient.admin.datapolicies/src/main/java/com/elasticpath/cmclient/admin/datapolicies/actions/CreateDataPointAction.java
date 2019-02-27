/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.cmclient.admin.datapolicies.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TableViewer;

import com.elasticpath.cmclient.admin.datapolicies.dialogs.DataPointDialog;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;

/**
 * Create data point action.
 */
public class CreateDataPointAction extends Action {

	private final AbstractCmClientFormEditor editor;
	private final TableViewer tableView;

	/**
	 * Constructor.
	 *
	 * @param editor          editor.
	 * @param tableView       table view to add created data point to.
	 * @param text            text.
	 * @param imageDescriptor image descriptor.
	 */
	public CreateDataPointAction(final AbstractCmClientFormEditor editor,
								 final TableViewer tableView,
								 final String text,
								 final ImageDescriptor imageDescriptor) {
		super(text, imageDescriptor);
		this.editor = editor;
		this.tableView = tableView;
	}

	@Override
	public void run() {
		DataPointDialog.openCreateDialog(editor.getSite().getShell(), tableView);
	}
}
