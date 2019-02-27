/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.cmclient.core.dialog.value.cell;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.common.dto.pricing.DisplayPriceDTO;
import com.elasticpath.commons.util.Pair;

/**
 * Cell editor. Table used as combo box, that allow to: 
 * 1. select one of the multiple values from particular row;
 * 2. select particular row.
 */
@SuppressWarnings({ "PMD.GodClass" })
public class AdvancedTableCellEditor extends DialogCellEditor {
	
	private static final Logger LOG = Logger.getLogger(AdvancedTableCellEditor.class);
	
	private final IStructuredContentProvider contentProvider;
	
	private final ITableLabelProvider labelProvider;
	
	private final List<Pair<String, Integer>> columns;
	
	private int [] allowedColumnsForselection;
	
	private final boolean rowResult;
	
	private int maxRows = 2;
	
	private final TableValuesProvider valuesProvider;

	/**
	 * Construct the cell editor. AdvancedTableCellEditor allow to use table as
	 * value provider for cell in other table.
	 * @param parent parent control 
	 * @param contentProvider content provider for table
	 * @param labelProvider label provider
	 * @param columns columns
	 * @param valuesProvider provider for item that will be displayed in the drop down
	 * @param allowedColumnsForselection optional, allowed columns for selection, shall be non null if 
	 * result shall be a selected item, otherwise result will be a selected cell text 
	 */
	public AdvancedTableCellEditor(
			final Composite parent,
			final IStructuredContentProvider contentProvider,
			final ITableLabelProvider labelProvider,
			final List<Pair<String, Integer>> columns,
			final TableValuesProvider valuesProvider,
			final int [] allowedColumnsForselection
			) {
		
		super(parent);
		
		this.contentProvider = contentProvider; 
		this.labelProvider = labelProvider; 
				
		this.valuesProvider = valuesProvider;
		
		this.columns = columns;
		
		if (allowedColumnsForselection != null) {
			this.allowedColumnsForselection = allowedColumnsForselection.clone();
		}
		
		this.rowResult = allowedColumnsForselection == null;
	}
	
	/**
	 * Set maximum rows in table to show. 
	 * @param maxRows shows how many rows will be shown without scrolling
	 */
	public void setMaxRows(final int maxRows) {
		this.maxRows = maxRows;
	}


	/** 
	 * Create and open dialog with table. 
	 * @param parent the parent control.
	 * @return result of user selection.
	 * */
	protected Object openDialogBox(final Control parent) {
		
		TableDialog myDialog = new TableDialog(parent.getShell());		
		
		return myDialog.open();
	}
	
	/**
	 * Dialog with table viewer as content for cell.
	 */
	private class TableDialog extends Dialog {
		
		//TODO get system metrics for vertical scroller width
		//For Windows its easy, not sure for other op systems.

		private static final int VERTICAL_SCROLLER_SPACE = 32;
		private static final int EMPTY_HEIGHT = 39;
		private static final String EDITING_TABLE = "Editing Table";

		/**
		 * The view's TableViewer.
		 */
		private TableViewer viewer;
		private Table table;
		private final Color bgColor;
		private final Color fgColor;
		private final Color bgColorSelected;
		private final Color fgColorSelected;
		private Object result;
		
		/**
		 * Constructor . 
		 * @param parent parent shell
		 */
		TableDialog(final Shell parent) {
			super(parent, SWT.NO_TRIM | SWT.ON_TOP);
			
			// get system colors
			bgColor = parent.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND);
			fgColor = parent.getDisplay().getSystemColor(SWT.COLOR_LIST_FOREGROUND);
			bgColorSelected = parent.getDisplay().getSystemColor(SWT.COLOR_LIST_SELECTION);
			fgColorSelected = parent.getDisplay().getSystemColor(SWT.COLOR_LIST_SELECTION_TEXT);
			
		}		
		
		/**
		 * Calculate width of controls.
		 * @return width of control
		 */
		private int getWidth() {			
			int width = VERTICAL_SCROLLER_SPACE;
//			if (items.length > maxRows) {
//				width = VERTICAL_SCROLLER_SPACE;				
//			} else {
//				width = VERTICAL_SCROLLER_SPACE / 2;
//			}			
			for (Pair<String, Integer> pair : columns) {				
				width += pair.getSecond();
			}
			return width;
		}

		/**
		 * Calculate height of control via font metrics.
		 * @param shell the shell for get graphics context
		 * @param itemsCount number of items that will be displayed in the drop down
		 * @return height of control
		 */
		private int getHeight(final Shell shell, final int itemsCount) {
			
			if (itemsCount == 0) {
				return EMPTY_HEIGHT;
			}
			
			final GC graphicsContext = new GC(shell);
			final FontMetrics fontMetrics = graphicsContext.getFontMetrics();
			final int fontHeigth = fontMetrics.getHeight();
			final int height = fontHeigth * (Math.min(itemsCount, maxRows) + 2) + (EMPTY_HEIGHT / 2);
			graphicsContext.dispose();
			return height;
		}
		
		
		
		/**
		 * Open dialog.
		 * @return result of dialog execution or null if focus was lost or ESC was pressed.
		 */
		public Object open() {
			final Shell parent = getParent();	
			final Shell shell = new Shell(parent, SWT.FLAT | SWT.NO_TRIM | SWT.ON_TOP /* | SWT.APPLICATION_MODAL*/);

			final int itemsCount = createContents(shell);

			final int height = getHeight(shell, itemsCount);
			final int width = getWidth();
			final Point point = parent.getShell().getDisplay().getCursorLocation();
			shell.setLocation(point.x - width, point.y);
			
			LOG.info("Open table drop box dim[w:" + width //$NON-NLS-1$
					+ " x h:" + height + "] for items: " + itemsCount);  //$NON-NLS-1$//$NON-NLS-2$
			shell.setSize(width, height);		
			
			shell.open();
			Display display = parent.getDisplay();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
			return result;
		}

		
		/**
		 * In case if cell editor configured for cell text result perform the 
		 * check is given column can be selected.
		 * @param columnIdx given column index
		 * @param rowResult is cell editor configured for row result.
		 * @return true in case if cell selection allowed or cell editor configured for row result.
		 */
		private boolean allowedColumn(final int columnIdx, final boolean rowResult) {
			if (!rowResult) {
				for (int allowedColumnIdx : allowedColumnsForselection) {
					if (allowedColumnIdx == columnIdx) {
						return true;
					}
				}
				return false;
			}
			return true; 
		}
		
		
		/**
		 * @param shell the shell on which to draw table.
		 * @return number of element in the table
		 */
		private int createContents(final Shell shell) {
			GridLayout gridLayout = new GridLayout(1, true);
			gridLayout.marginHeight = 0;
			gridLayout.marginWidth = 0;
			gridLayout.verticalSpacing = 0;
			shell.setLayout(gridLayout);
			final IEpLayoutComposite layoutComposite = CompositeFactory.createGridLayoutComposite(shell, 1, true);			
		    
			final IEpLayoutData layoutData = layoutComposite.createLayoutData(
					IEpLayoutData.FILL, 
					IEpLayoutData.BEGINNING, 
					true, 
					true);
			
			layoutComposite.setLayoutData(layoutData.getSwtLayoutData());
			
			
			gridLayout = (GridLayout) layoutComposite.getSwtComposite().getLayout();
			gridLayout.marginHeight = 0;
			gridLayout.marginWidth = 0;
			gridLayout.verticalSpacing = 0;
			
			
			final IEpTableViewer epTableViewer = layoutComposite.addTableViewer(
				false,
				EpState.EDITABLE,
				layoutComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true),
				EDITING_TABLE);
			
			
			viewer = epTableViewer.getSwtTableViewer();		
			table = viewer.getTable();
			table.setLinesVisible(true);
			addListeners(shell);			
			
			for (Pair<String, Integer> pair : columns) {
				epTableViewer.addTableColumn(pair.getFirst(), pair.getSecond());
			}		
			
			epTableViewer.setContentProvider(contentProvider);
			epTableViewer.setLabelProvider(labelProvider);
			
			final Object[] items = valuesProvider.getValues(getValue());
			epTableViewer.setInput(items);	
			
			if (items != null) {
				return items.length;
			}
			
			return 0;
			
		}

		/**
		 * Add listeners to table.
		 * @param shell the Shell
		 */
		private void addListeners(final Shell shell) {
			
			addTableFocusListener(shell);
			
			addTableMouseListener(shell);
		//TODO-RAP-M1 No Mouse Move events.
		// see https://eclipse.org/rap/developers-guide/devguide.php?topic=key-and-mouse-events.html
		}

		private void addTableMouseListener(final Shell shell) {
			table.addMouseListener(new MouseListener() {

				public void mouseDoubleClick(final MouseEvent event) {
					// Nothing to do
				}

				public void mouseDown(final MouseEvent event) {
					// Nothing to do
				}

				public void mouseUp(final MouseEvent event) {
					final Point point = new Point(event.x, event.y);
					int index = table.getTopIndex();
					while (index < table.getItemCount()) {
						TableItem itemRow = table.getItem(index);
						for (int i = 0; i < table.getColumnCount(); i++) {
							final Rectangle rect = itemRow.getBounds(i);
							if (rect.contains(point) && allowedColumn(i, rowResult)) {
								if (rowResult) {
									result = itemRow.getData();
								} else {
									result = itemRow.getText(i);
								}
								shell.close();
								return;
							}
						}
						index++;
					}
				}
				
			});
		}

		private void addTableFocusListener(final Shell shell) {
			table.addFocusListener(new FocusListener() {

				public void focusGained(final FocusEvent arg0) {
					// Nothing to do
				}

				public void focusLost(final FocusEvent arg0) {
					// Works when control lost focus
					shell.close();
				}
				
			});
		}
	}
	
	private Text textControl;
	
	@Override
	protected void doSetFocus() {
		// just for overwriting parent functionality
	}

	@Override
	protected void updateContents(final Object value) {
		if (textControl == null || value == null) {
			return;
		}
		String text = ""; //$NON-NLS-1$
		if (value instanceof String) {
			text = (String) value;
		} else if (value instanceof DisplayPriceDTO) {
			DisplayPriceDTO dto = (DisplayPriceDTO) value;
			if (dto.getSalePrice() == null) {
				text = dto.getListPrice().toString();
			} else {
				text = dto.getSalePrice().toString();

			}
		} else {
			text = value.toString();
		}
		textControl.setText(text);
	}


	@Override
	protected Control createContents(final Composite cell) {
		textControl = EpControlFactory.getInstance().createTextField(cell, SWT.LEFT, EpState.EDITABLE);
		textControl.setBackground(cell.getBackground());
		return textControl;
	}

	/**
	 * @return price text control
	 */
	public Text getTextControl() {
		return this.textControl;
	}
}
