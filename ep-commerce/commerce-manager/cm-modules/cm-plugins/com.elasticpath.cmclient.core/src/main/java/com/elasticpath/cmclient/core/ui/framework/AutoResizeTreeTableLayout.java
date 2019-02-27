/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.ui.framework;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * A layout technique to automatically resize the columns. The original source can be found at: http://www.korbel.tk/korbel/?p=24
 */
public class AutoResizeTreeTableLayout extends TableLayout implements ControlListener {

	private static final int FIFTEEN = 15;

	private final Tree tree;

	private final List<ColumnLayoutData> columns = new ArrayList<ColumnLayoutData>();

	private boolean autosizing;

	/**
	 * Constructs from any {@link Tree}.
	 * 
	 * @param tree a Tree
	 */
	public AutoResizeTreeTableLayout(final Tree tree) {
		this.tree = tree;
		tree.addControlListener(this);
	}

	/**
	 * Adds a column data.
	 * 
	 * @param data a {@link ColumnLayoutData}
	 */
	public void addColumnData(final ColumnLayoutData data) {
		columns.add(data);
		super.addColumnData(data);
	}

	/**
	 * When control is moved, this event can be caught.
	 * 
	 * @param event an {@link ControlEvent}
	 */
	public void controlMoved(final ControlEvent event) {
		// no implementation
	}

	/**
	 * When control is resized, this event can be caught.
	 * 
	 * @param event an {@link ControlEvent}
	 */
	public void controlResized(final ControlEvent event) {
		if (autosizing) {
			return;
		}
		autosizing = true;
		try {
			autoSizeColumns();
		} finally {
			autosizing = false;
		}
	}

	@SuppressWarnings({ "PMD.NPathComplexity" })
	private void autoSizeColumns() {
		int width = tree.getClientArea().width;
		if (width <= 1) {
			return;
		}

		TreeColumn[] treeColumns = tree.getColumns();
		int size = Math.min(columns.size(), treeColumns.length);
		int[] widths = new int[size];
		int fixedWidth = 0;
		int numberOfWeightColumns = 0;
		int totalWeight = 0;

		GC graphicContext = new GC(tree);
		// First do some calculations
		for (int i = 0; i < size; i++) {
			ColumnLayoutData col = columns.get(i);
			if (col instanceof ColumnPixelData) {
				int fixPixels = ((ColumnPixelData) col).width;
				if (fixPixels == 0) {
					Point measurement = graphicContext.textExtent(tree.getColumn(i).getText());
					int maxWidth = measurement.x;

					for (TreeItem treeItem : tree.getItems()) {
						if (treeItem.isDisposed()) {
							break;
						}
						
						measurement = graphicContext.textExtent(treeItem.getText(i));
						Integer[] depthArray = getDepths(treeItem, 1).toArray(new Integer[] {});
						Image image = treeItem.getImage();
						int imageWidth = 0;
						if (image != null) {
							imageWidth = depthArray[depthArray.length - 1] * (image.getBounds().width + 2);
						}
						maxWidth = Math.max(maxWidth, measurement.x + imageWidth);
					}
					widths[i] = maxWidth + FIFTEEN;
					fixedWidth += maxWidth + FIFTEEN;
				} else {
					widths[i] = fixPixels;
					fixedWidth += fixPixels;
				}
			} else if (col instanceof ColumnWeightData) {
				ColumnWeightData columnWeightData = (ColumnWeightData) col;
				numberOfWeightColumns++;
				int weight = columnWeightData.weight;
				totalWeight += weight;
			} else {
				throw new IllegalStateException("Unknown column layout data"); //$NON-NLS-1$
			}
		}
		graphicContext.dispose();

		// Do we have columns that have a weight?
		if (numberOfWeightColumns > 0) {
			// Now, distribute the rest
			// to the columns with weight.
			int rest = width - fixedWidth;
			int totalDistributed = 0;
			for (int i = 0; i < size; i++) {
				ColumnLayoutData col = columns.get(i);
				if (col instanceof ColumnWeightData) {
					ColumnWeightData columnWeightData = (ColumnWeightData) col;
					int weight = columnWeightData.weight;
					int pixels;
					if (totalWeight == 0) {
						pixels = 0;
					} else {
						pixels = weight * rest / totalWeight;
					}
					totalDistributed += pixels;
					widths[i] = pixels;
				}
			}

			// Distribute any remaining pixels
			// to columns with weight.
			int diff = rest - totalDistributed;
			for (int i = 0; diff > 0; i++) {
				if (i == size) {
					i = 0;
				}
				ColumnLayoutData col = columns.get(i);
				if (col instanceof ColumnWeightData) {
					++widths[i];
					--diff;
				}
			}
		}

		for (int i = 0; i < size; i++) {
			if (treeColumns[i].getWidth() != widths[i]) {
				treeColumns[i].setWidth(widths[i]);
			}
		}
	}

	private Set<Integer> getDepths(final TreeItem treeItem, final int level) {
		Set<Integer> result = new TreeSet<Integer>();
		result.add(level + 1);
		
		for (TreeItem child : treeItem.getItems()) {
			result.addAll(getDepths(child, level + 1));
		}
		
		return result;
	}
	
}
