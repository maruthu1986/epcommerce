/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.cmclient.admin.datapolicies.actions;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Text;

import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.domain.datapolicy.DataPolicy;

/**
 * Add segment action.
 */
public class AddSegmentAction extends Action {

	private final IEpTableViewer segmentsTableViewer;
	private final Text segmentNameTextField;

	private final DataPolicy dataPolicy;
	private final AbstractCmClientFormEditor editor;


	/**
	 * Constructor.
	 *
	 * @param segmentsTableViewer  table viewer.
	 * @param segmentNameTextField name text field.
	 * @param editor               editor.
	 * @param dataPolicy           data policy.
	 * @param text                 text.
	 * @param imageDescriptor      image descriptor.
	 */
	public AddSegmentAction(final IEpTableViewer segmentsTableViewer,
							final Text segmentNameTextField,
							final AbstractCmClientFormEditor editor,
							final DataPolicy dataPolicy,
							final String text,
							final ImageDescriptor imageDescriptor) {
		super(text, imageDescriptor);
		this.segmentsTableViewer = segmentsTableViewer;
		this.segmentNameTextField = segmentNameTextField;
		this.dataPolicy = dataPolicy;
		this.editor = editor;
	}

	@Override
	public void run() {
		String text = segmentNameTextField.getText();
		if (!StringUtils.isEmpty(text)) {
			dataPolicy.getSegments().add(text);
			segmentsTableViewer.getSwtTableViewer().refresh();
			editor.controlModified();
			segmentNameTextField.setText("");
		}
	}
}
