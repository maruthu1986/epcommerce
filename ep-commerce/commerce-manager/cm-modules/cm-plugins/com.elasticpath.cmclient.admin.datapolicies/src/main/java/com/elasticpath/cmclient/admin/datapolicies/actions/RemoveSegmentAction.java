/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.cmclient.admin.datapolicies.actions;

import java.util.function.Supplier;

import org.eclipse.jface.action.Action;

import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.domain.datapolicy.DataPolicy;

/**
 * Remove segment action.
 */
public class RemoveSegmentAction extends Action {

	private final AbstractCmClientFormEditor editor;
	private final IEpTableViewer segmentsTableViewer;
	private final DataPolicy dataPolicy;
	private final Supplier<String> selectedSegmentSupplier;

	/**
	 * Constructor.
	 *
	 * @param segmentsTableViewer     table viewer.
	 * @param editor                  parent editor.
	 * @param dataPolicy              data policy to remove segments for.
	 * @param selectedSegmentSupplier selected segment supplier.
	 * @param text                    action text.
	 */
	public RemoveSegmentAction(final IEpTableViewer segmentsTableViewer,
							   final AbstractCmClientFormEditor editor,
							   final DataPolicy dataPolicy,
							   final Supplier<String> selectedSegmentSupplier,
							   final String text) {
		super(text);
		this.segmentsTableViewer = segmentsTableViewer;
		this.editor = editor;
		this.dataPolicy = dataPolicy;
		this.selectedSegmentSupplier = selectedSegmentSupplier;
	}

	@Override
	public void run() {
		String selectedSegment = selectedSegmentSupplier.get();
		if (selectedSegment != null) {
			dataPolicy.getSegments().remove(selectedSegment);
			segmentsTableViewer.getSwtTableViewer().refresh();
			editor.controlModified();
		}
	}
}
