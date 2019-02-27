/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.pricelistmanager.editors.baseamountsection;

import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.FocusCellOwnerDrawHighlighter;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.jface.viewers.TableViewerFocusCellManager;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Shell;

/**
 * Adds usability features to <code>TableViewer</code> object.
 */
@SuppressWarnings({ "PMD.UseSingleton", "PMD.UseUtilityClass" })
public class TableViewerEnhancer {
	
	/**
	 * Adds traversal tabbing feature to the <code>TableViwer</code>. 
	 *
	 * @param tableViewer - {@link TableViewer}.
	 * @param addArrowNavigation - if true - it will be possible to navigate right-left through table.
	 * @param addMoveNextPrevRow - if true we will search up and down the table (not just in the row).
	 */
	public static void addTraversalTabbing(final TableViewer tableViewer, final boolean addArrowNavigation, final boolean addMoveNextPrevRow) {
		final ColumnViewerEditorActivationStrategy actSupport = new ColumnViewerEditorActivationStrategy(tableViewer) {
			@Override
			protected boolean isEditorActivationEvent(final ColumnViewerEditorActivationEvent event) {
				return event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL
						|| event.eventType == ColumnViewerEditorActivationEvent.MOUSE_CLICK_SELECTION
						|| event.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED && (event.keyCode == SWT.CR || event.character == ' ')
						|| event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC;
			}
		};

		//do not perform arrow navigation
		if (!addArrowNavigation) {
			int feature = ColumnViewerEditor.TABBING_HORIZONTAL | ColumnViewerEditor.KEYBOARD_ACTIVATION;
			if (addMoveNextPrevRow) {
				feature |= ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR;
			} else {
				feature |= ColumnViewerEditor.TABBING_CYCLE_IN_ROW;
			}
			TableViewerEditor.create(tableViewer, actSupport, feature);
			return;
		}
		
		
		final Shell shell = tableViewer.getTable().getShell();
		
		final FocusCellOwnerDrawHighlighter focusCellOwnerDrawHighlighter = new FocusCellOwnerDrawHighlighter(tableViewer) {
			protected Color getSelectedCellBackgroundColor(
					final ViewerCell cell) {
				return shell.getDisplay().getSystemColor(
						SWT.COLOR_LIST_SELECTION);
			}
			protected Color getSelectedCellForegroundColor(
					final ViewerCell cell) {
				return shell.getDisplay().getSystemColor(
						SWT.COLOR_WIDGET_FOREGROUND);
			}
		};

		TableViewerFocusCellManager focusCellManager = new TableViewerFocusCellManager(tableViewer, focusCellOwnerDrawHighlighter);
		
		TableViewerEditor.create(tableViewer, focusCellManager, actSupport, ColumnViewerEditor.TABBING_HORIZONTAL
				| ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR | ColumnViewerEditor.TABBING_VERTICAL | ColumnViewerEditor.KEYBOARD_ACTIVATION);
	}

}
