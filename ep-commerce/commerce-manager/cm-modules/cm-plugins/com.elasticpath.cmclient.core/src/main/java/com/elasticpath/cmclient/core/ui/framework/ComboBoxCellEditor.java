/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.ui.framework;

import java.text.MessageFormat;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;

/**
 * A cell editor that presents a list of items in a combo box. The cell editor's value is the zero-based index of the selected item.
 * <p>
 */
public class ComboBoxCellEditor extends CellEditor {

	private static final int DEFAULT_MIN_STRING_LENGTH = 10;

	private static final int DEFAULT_MIN_WIDTH = 60;

	private final EpControlFactory controlFactory = EpControlFactory.getInstance();

	/**
	 * The list of items to present in the combo box.
	 */
	private String[] items;

	/**
	 * The zero-based index of the selected item.
	 */
	private int selection;

	/**
	 * The custom combo box control.
	 */
	private CCombo comboBox;

	/**
	 * Default ComboBoxCellEditor style.
	 */
	private static final int DEFAULT_STYLE = SWT.NONE;

	/**
	 * Creates a new cell editor with no control and no st of choices. Initially, the cell editor has no cell validator.
	 *
	 * @see CellEditor#setStyle
	 * @see CellEditor#create
	 * @see ComboBoxCellEditor#setItems
	 * @see CellEditor#dispose
	 */
	public ComboBoxCellEditor() {
		setStyle(DEFAULT_STYLE);
	}

	/**
	 * Creates a new cell editor with a combo containing the given list of choices and parented under the given control. The cell editor value is the
	 * zero-based index of the selected item. Initially, the cell editor has no cell validator and the first item in the list is selected.
	 *
	 * @param parent the parent control
	 * @param items  the list of strings for the combo box
	 */
	public ComboBoxCellEditor(final Composite parent, final String[] items) {
		this(parent, items, DEFAULT_STYLE);
	}

	/**
	 * Creates a new cell editor with a combo containing the given list of choices and parented under the given control. The cell editor value is the
	 * zero-based index of the selected item. Initially, the cell editor has no cell validator and the first item in the list is selected.
	 *
	 * @param parent the parent control
	 * @param items  the list of strings for the combo box
	 * @param style  the style bits
	 */
	public ComboBoxCellEditor(final Composite parent, final String[] items, final int style) {
		super(parent, style);
		setItems(items);
	}

	/**
	 * Sets the list of choices for the combo box.
	 *
	 * @param items the list of choices for the combo box
	 */
	public final void setItems(final String[] items) {
		Assert.isNotNull(items);
		this.items = new String[items.length];
		System.arraycopy(items, 0, this.items, 0, items.length);
		populateComboBoxItems();
	}

	@Override
	protected CCombo createControl(final Composite parent) {

		comboBox = controlFactory.createComboBox(parent, SWT.DROP_DOWN | SWT.BORDER | SWT.READ_ONLY, EpControlFactory.EpState.READ_ONLY);

		populateComboBoxItems();

		comboBox.addKeyListener(new KeyAdapter() {
			public void keyPressed(final KeyEvent event) {
				keyReleaseOccured(event);
			}
		});

		comboBox.addSelectionListener(new SelectionAdapter() {
			public void widgetDefaultSelected(final SelectionEvent event) {
				applyEditorValueAndDeactivate();
			}

			public void widgetSelected(final SelectionEvent event) {
				selection = comboBox.getSelectionIndex();
				applyEditorValue();
			}
		});

		comboBox.addTraverseListener(new TraverseListener() {
			public void keyTraversed(final TraverseEvent event) {
				if (event.detail == SWT.TRAVERSE_ESCAPE || event.detail == SWT.TRAVERSE_RETURN) {
					event.doit = false;
				}
			}
		});

		comboBox.addFocusListener(new FocusAdapter() {
			public void focusLost(final FocusEvent event) {
				ComboBoxCellEditor.this.focusLost();
			}
		});
		return comboBox;
	}

	/**
	 * The <code>ComboBoxCellEditor</code> implementation of this <code>CellEditor</code> framework method returns the zero-based index of the
	 * current selection.
	 *
	 * @return the zero-based index of the current selection wrapped as an <code>Integer</code>
	 */
	protected Object doGetValue() {
		return selection;
	}

	@Override
	protected void doSetFocus() {
		comboBox.setFocus();
	}

	/**
	 * The <code>ComboBoxCellEditor</code> implementation of this <code>CellEditor</code> framework method sets the minimum width of the cell. The
	 * minimum width is 10 characters if <code>comboBox</code> is not <code>null</code> or <code>disposed</code> else it is 60 pixels to make sure
	 * the arrow button and some text is visible. The list of CCombo will be wide enough to show its longest item.
	 *
	 * @return the layout data
	 */
	public LayoutData getLayoutData() {
		LayoutData layoutData = super.getLayoutData();
		if ((comboBox == null) || comboBox.isDisposed()) {
			layoutData.minimumWidth = DEFAULT_MIN_WIDTH;
		} else {
			// make the comboBox 10 characters wide
			final GC gContext = new GC(comboBox);
			layoutData.minimumWidth = (gContext.getFontMetrics().getAverageCharWidth() * DEFAULT_MIN_STRING_LENGTH) + DEFAULT_MIN_STRING_LENGTH;
			gContext.dispose();
		}
		return layoutData;
	}

	/**
	 * The <code>ComboBoxCellEditor</code> implementation of this <code>CellEditor</code> framework method accepts a zero-based index of a selection.
	 *
	 * @param value the zero-based index of the selection wrapped as an <code>Integer</code>
	 */
	protected void doSetValue(final Object value) {
		Assert.isTrue(comboBox != null && (value instanceof Integer));
		selection = (Integer) value;
		comboBox.select(selection);
	}

	/**
	 * Updates the list of choices for the combo box for the current control.
	 */
	private void populateComboBoxItems() {
		if (comboBox != null && items != null) {
			comboBox.removeAll();
			for (int i = 0; i < items.length; i++) {
				comboBox.add(items[i], i);
			}

			setValueValid(true);
			selection = 0;
		}
	}


	/**
	 * Applies the currently selected value and deactivates the cell editor.
	 */
	private void applyEditorValue() {
		// must set the selection before getting value
		selection = comboBox.getSelectionIndex();
		Object newValue = doGetValue();
		markDirty();
		boolean isValid = isCorrect(newValue);
		setValueValid(isValid);

		if (!isValid) {
			// Only format if the 'index' is valid
			if (items.length > 0 && selection >= 0 && selection < items.length) {
				// try to insert the current value into the error message.
				setErrorMessage(MessageFormat.format(getErrorMessage(), items[selection]));
			} else {
				// Since we don't have a valid index, assume we're using an 'edit'
				// combo so format using its text value
				setErrorMessage(MessageFormat.format(getErrorMessage(), comboBox.getText()));
			}
		}

		fireApplyEditorValue();
	}


	/**
	 * Applies the editor value and deactivates.
	 */
	protected void applyEditorValueAndDeactivate() {
		applyEditorValue();
		deactivate();
	}

	@Override
	protected void focusLost() {
		if (isActivated()) {
			applyEditorValueAndDeactivate();
		}
	}

	@Override
	protected void keyReleaseOccured(final KeyEvent keyEvent) {
		if (keyEvent.character == '\u001b') { // Escape character
			fireCancelEditor();
		} else if (keyEvent.character == '\t') { // tab key
			applyEditorValueAndDeactivate();
		}
	}
}
