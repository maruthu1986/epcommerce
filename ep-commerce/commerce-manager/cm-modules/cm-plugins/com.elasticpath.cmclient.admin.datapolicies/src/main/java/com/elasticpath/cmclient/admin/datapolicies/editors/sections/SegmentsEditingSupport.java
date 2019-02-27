/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.cmclient.admin.datapolicies.editors.sections;

import java.util.function.BiConsumer;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Composite;

import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;

/**
 * Segments edition support. Uses inline text cell editor to edit table rows.
 */
public class SegmentsEditingSupport extends EditingSupport {

	private final IEpTableViewer segmentsTableViewer;
	private final BiConsumer<String, String> segmentModifiedConsumer;

	/**
	 * Constructor.
	 *
	 * @param segmentsTableViewer table viewer.
	 * @param segmentModifiedConsumer segment modification consumer.
	 */
	public SegmentsEditingSupport(final IEpTableViewer segmentsTableViewer,
								  final BiConsumer<String, String> segmentModifiedConsumer) {
		super(segmentsTableViewer.getSwtTableViewer());
		this.segmentsTableViewer = segmentsTableViewer;
		this.segmentModifiedConsumer = segmentModifiedConsumer;
	}

	@Override
	protected boolean canEdit(final Object element) {
		return true;
	}

	@Override
	protected CellEditor getCellEditor(final Object element) {
		return new TextCellEditor((Composite) segmentsTableViewer.getSwtTableViewer().getControl());
	}

	@Override
	protected Object getValue(final Object element) {
		return element;
	}

	@Override
	protected void setValue(final Object element, final Object value) {
		final String oldValue = (String) element;
		final String newValue = (String) value;
		segmentModifiedConsumer.accept(oldValue, newValue);
	}
}
