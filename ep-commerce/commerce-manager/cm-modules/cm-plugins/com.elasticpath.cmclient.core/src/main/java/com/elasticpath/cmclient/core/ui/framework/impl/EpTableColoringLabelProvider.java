/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.ui.framework.impl;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import com.elasticpath.cmclient.core.CmClientResources;

/**
 * Internal implementation of a label provider. The idea is to provide common background behavior to all tables.
 */
class EpTableColoringLabelProvider extends LabelProvider implements ITableLabelProvider, IColorProvider {

	private final ITableLabelProvider originalLabelProvider;

	private int cellsCountIndex;

	private final int columnCount;

	/**
	 * Constructor.
	 * 
	 * @param labelProvider the original label provider
	 * @param columnCount the columns number
	 */
	EpTableColoringLabelProvider(final ITableLabelProvider labelProvider, final int columnCount) {
		this.originalLabelProvider = labelProvider;
		this.columnCount = columnCount;
	}

	/**
	 * Gets the column image.
	 * 
	 * @param element model element
	 * @param columnIndex column index
	 * @return Image
	 */
	public Image getColumnImage(final Object element, final int columnIndex) {
		return this.originalLabelProvider.getColumnImage(element, columnIndex);
	}

	/**
	 * Gets the column text.
	 * 
	 * @param element model element
	 * @param columnIndex column index
	 * @return String
	 */
	public String getColumnText(final Object element, final int columnIndex) {
		return this.originalLabelProvider.getColumnText(element, columnIndex);
	}

	/**
	 * Gets the background alternating colors.
	 * 
	 * @param element model element
	 * @return alternating null and Grey color for each line
	 */
	public Color getBackground(final Object element) {
		Color color = null;
		if (this.columnCount > 0) {
			final boolean useGreyColor = this.cellsCountIndex++ / this.columnCount % 2 == 0;

			if (useGreyColor) {
				color = CmClientResources.getColor(CmClientResources.COLOR_GREY);
			}
		}
		return color;
	}

	/**
	 * Returns null foreground color.
	 * 
	 * @param element model element
	 * @return null
	 */
	public Color getForeground(final Object element) {
		return null;
	}

}
