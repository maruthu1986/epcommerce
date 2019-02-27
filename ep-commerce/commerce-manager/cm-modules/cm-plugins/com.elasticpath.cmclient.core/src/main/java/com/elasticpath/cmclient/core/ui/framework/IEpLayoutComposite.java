/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.ui.framework;

import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ImageHyperlink;

import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;

/**
 * This interface serves as an abstraction to a native Eclipse composite.
 */
@SuppressWarnings("PMD.TooManyMethods")
public interface IEpLayoutComposite {

	/**
	 * Adds new grid layout form section to this composite.
	 * 
	 * @param numColumns number of grid columns
	 * @param title the section title, null if none
	 * @param style the style. see ExpandableComposite for constants 
	 * @param data the layout data
	 * @return {@link IEpLayoutComposite}
	 * @see org.eclipse.ui.forms.widgets.ExpandableComposite
	 */
	IEpLayoutComposite addGridLayoutSection(int numColumns, String title, int style, IEpLayoutData data);

	/**
	 * Adds new grid layout form section to this composite.
	 * 
	 * @param numColumns number of grid columns
	 * @param title the section title, null if none
	 * @param description the section description
	 * @param style the style. see ExpandableComposite for constants 
	 * @param data the layout data
	 * @return {@link IEpLayoutComposite}
	 * @see org.eclipse.ui.forms.widgets.ExpandableComposite
	 */
	IEpLayoutComposite addGridLayoutSection(int numColumns, String title, String description, int style, IEpLayoutData data);

	/**
	 *
	 * Adds new table wrap layout form section to this composite.
	 * 
	 * @param numColumns number of grid columns
	 * @param title the section title, null if none
	 * @param style the style. see ExpandableComposite for constants 
	 * @param data the layout data
	 * @return {@link IEpLayoutComposite}
	 * @see org.eclipse.ui.forms.widgets.ExpandableComposite
	 */
	IEpLayoutComposite addTableWrapLayoutSection(int numColumns, String title, int style, IEpLayoutData data);
	
	/**
	 * Adds new table wrap layout form section to this composite.
	 * 
	 * @param numColumns number of grid columns
	 * @param title the section title, null if none
	 * @param description the section description
	 * @param style the style. see ExpandableComposite for constants 
	 * @param data the layout data
	 * @return {@link IEpLayoutComposite}
	 * @see org.eclipse.ui.forms.widgets.ExpandableComposite
	 */
	IEpLayoutComposite addTableWrapLayoutSection(int numColumns, String title, String description, int style, IEpLayoutData data);

	/**
	 * Adds new composite with a <code>GridLayout</code> layout to the current composite.
	 * 
	 * @param numColumns number of columns to be created in the grid
	 * @param equalWidthColumns specifies whether the columns are with equal width
	 * @param data the layout data specifying the way the new composite will be placed in the current composite. If data is <code>null</code> the
	 *            default layout data for filling the cell will be used
	 * @return instance of <code>IEpLayoutComposite</code>
	 */
	IEpLayoutComposite addGridLayoutComposite(int numColumns, boolean equalWidthColumns, IEpLayoutData data);

	/**
	 * Adds new scrolled composite with a <code>GridLayout</code> layout to the current composite.
	 *
	 * @param numColumns number of columns to be created in the grid
	 * @param equalWidthColumns specifies whether the columns are with equal width
	 * @return instance of <code>IEpLayoutComposite</code>
	 */
	IEpLayoutComposite addScrolledGridLayoutComposite(int numColumns, boolean equalWidthColumns);

	/**
	 * Adds new scrolled composite with a <code>GridLayout</code> layout to the current composite.
	 *
	 * @param numColumns          number of columns to be created in the grid
	 * @param equalWidthColumns   specifies whether the columns are with equal width
	 * @param grabHorizontalSpace specifies if scrolled composite will grab horizontal space
	 * @return instance of <code>IEpLayoutComposite</code>
	 */
	IEpLayoutComposite addScrolledGridLayoutComposite(int numColumns, boolean equalWidthColumns, boolean grabHorizontalSpace);

	/**
	 * Adds new scrolled composite with a <code>GridLayout</code> layout to the current composite.
	 *
	 * @param numColumns          number of columns to be created in the grid
	 * @param equalWidthColumns   specifies whether the columns are with equal width
	 * @param grabHorizontalSpace specifies if scrolled composite will grab horizontal space
	 * @param data the layout data specifying the way the new composite will be placed in the current composite. If data is <code>null</code> the
	 *            default layout data for filling the cell will be used
	 * @return instance of <code>IEpLayoutComposite</code>
	 */
	IEpLayoutComposite addScrolledGridLayoutComposite(int numColumns, boolean equalWidthColumns,
													  boolean grabHorizontalSpace, IEpLayoutData data);

	/**
	 * Adds new scrolled composite with a <code>GridLayout</code> layout to the current composite.
	 *
	 * @param numColumns number of columns to be created in the grid
	 * @param equalWidthColumns specifies whether the columns are with equal width
	 * @param data the layout data specifying the way the new composite will be placed in the current composite. If data is <code>null</code> the
	 *            default layout data for filling the cell will be used
	 * @return instance of <code>IEpLayoutComposite</code>
	 */
	IEpLayoutComposite addScrolledGridLayoutComposite(int numColumns, boolean equalWidthColumns, IEpLayoutData data);

	/**
	 * Adds new composite with a <code>TableWrapLayout</code> layout to the current composite.
	 * 
	 * @param numColumns number of columns to be created in the grid
	 * @param equalWidthColumns specifies whether the columns are with equal width. If data is <code>null</code> the default layout data for
	 *            filling the cell will be used
	 * @param data the layout data specifying the way the new composite is placed in the current composite
	 * @return instance of <code>IEpLayoutComposite</code>
	 */
	IEpLayoutComposite addTableWrapLayoutComposite(int numColumns, boolean equalWidthColumns, IEpLayoutData data);

	/**
	 * Creates new Expandable composite with a twistie if there is a title defined. If title is null then no twistie is created and the expanded
	 * state is meant to be changed programmatically using {@link org.eclipse.ui.forms.widgets.ExpandableComposite#setExpanded(boolean)}
	 * 
	 * @param numColumns number of columns to be created for the EP layout composite
	 * @param equalWidthColumns should the columns be equal size
	 * @param title the title label
	 * @param data EP layout data
	 * @return IEpLayoutComposite
	 */
	IEpLayoutComposite addExpandableComposite(int numColumns, boolean equalWidthColumns, String title, IEpLayoutData data);

	/**
	 * Retrieve the underlying SWT Composite.
	 * 
	 * @return the underlying SWT composite
	 */
	Composite getSwtComposite();

	/**
	 * Adds new text field to the composite.
	 *
	 * @param epState the EP UI state flag (can be EDITABLE, READ_ONLY, DISABLED)
	 * @param data the layout data. If data is <code>null</code> the default layout data for filling the cell will be used
	 * @return <code>Text</code> UI control
	 */
	Text addTextField(EpState epState, IEpLayoutData data);

	/**
	 * Adds a password field that echos an obscured character.
	 * 
	 * @param epState the EP UI state flag (can be EDITABLE, READ_ONLY, DISABLED)
	 * @param data EP layout data
	 * @return a Password Text control
	 */
	Text addPasswordField(EpState epState, IEpLayoutData data);

	/**
	 * Adds a text area.
	 * 
	 * @param epState the EP UI state flag (can be EDITABLE, READ_ONLY, DISABLED)
	 * @param data the EP layout data
	 * @return <code>Text</code>
	 */
	Text addTextArea(EpState epState, IEpLayoutData data);

	/**
	 * Adds a text area with configurable horizontal and vertical scrolls.
	 * 
	 * @param hasVerticalScroll should the text have vertical scroll
	 * @param hasHorizontalScroll should the text have horizontal scroll
	 * @param epState the EP UI state flag (can be EDITABLE, READ_ONLY, DISABLED)
	 * @param data the EP layout data
	 * @return <code>Text</code>
	 */
	Text addTextArea(boolean hasVerticalScroll, boolean hasHorizontalScroll, EpState epState, IEpLayoutData data);
	
	/**
	 * Adds a Scale. 
	 * 
	 * @param minimum  min value for Scale
	 * @param maximum max value for Scale
	 * @param increment step
	 * @param pageIncrement page (devider) increment
	 * @param style SWT.VERTICAL or SWT.HORIZONTAL
	 * @param epState the EP UI state flag (can be EDITABLE, READ_ONLY, DISABLED)
	 * @param data EP layout data 
	 * @return <code>Scale</code>
	 */
	Scale addScale(
			int minimum, 
			int maximum, 
			int increment, 
			int pageIncrement,
			int style, 
			EpState epState, 
			IEpLayoutData data);

	/**
	 * Adds new push button to the composite.
	 * 
	 * @param buttonLabel the label of the button
	 * @param epState the EP UI state flag (can be EDITABLE, READ_ONLY, DISABLED)
	 * @param data the layout data. If data is <code>null</code> the default layout data for filling the cell will be used
	 * @return <code>Button</code>, Eclipse UI control
	 */
	Button addPushButton(String buttonLabel, EpState epState, IEpLayoutData data);

	/**
	 * Adds new checkbox with the specified label.
	 * 
	 * @param checkBoxLabel the text label
	 * @param epState the EP UI state flag (can be EDITABLE, READ_ONLY, DISABLED)
	 * @param data the layout data. If data is <code>null</code> the default layout data for filling the cell will be used
	 * @return <code>Button</code>, Eclipse UI control
	 */
	Button addCheckBoxButton(String checkBoxLabel, EpState epState, IEpLayoutData data);

	/**
	 * Adds new radio button with the specified label.
	 * 
	 * @param radioLabel the text label
	 * @param epState the EP UI state flag (can be EDITABLE, READ_ONLY, DISABLED)
	 * @param data the layout data. If data is <code>null</code> the default layout data for filling the cell will be used
	 * @return <code>Button</code>, Eclipse UI control
	 */
	Button addRadioButton(String radioLabel, EpState epState, IEpLayoutData data);

	/**
	 * Adds new radio button with the specified label and image.
	 * 
	 * @param radioLabel the text label
	 * @param image the image to be set to the button
	 * @param epState the EP UI state flag (can be EDITABLE, READ_ONLY, DISABLED)
	 * @param data the layout data. If data is <code>null</code> the default layout data for filling the cell will be used
	 * @return <code>Button</code>, Eclipse UI control
	 */
	Button addRadioButton(String radioLabel, Image image, EpState epState, IEpLayoutData data);

	/**
	 * Adds new list.
	 * 
	 * @param epState the EP UI state flag (can be EDITABLE, READ_ONLY, DISABLED)
	 * @param data the layout data. If data is <code>null</code> the default layout data for filling the cell will be used
	 * @return <code>List</code>, Eclipse UI control
	 */
	List addList(EpState epState, IEpLayoutData data);

	/**
	 * Adds new label to the composite.
	 * 
	 * @param labelText the text label
	 * @param data the layout data. If data is <code>null</code> the default layout data for filling the cell will be used
	 * @return <code>Label</code>
	 */
	Label addLabel(String labelText, IEpLayoutData data);

	/**
	 * Adds new label in bold font.
	 * 
	 * @param labelText the text label
	 * @param data the layout data. If data is <code>null</code> the default layout data for filling the cell will be used
	 * @return <code>Label</code>
	 */
	Label addLabelBold(String labelText, IEpLayoutData data);

	/**
	 * Adds new label in bold font, and prepends it with an asterisk.
	 * 
	 * @param labelText the text label
	 * @param epState the EP UI state flag (can be EDITABLE, READ_ONLY, DISABLED)
	 * @param data the layout data. If data is <code>null</code> the default layout data for filling the cell will be used
	 * @return <code>Label</code>
	 */
	Label addLabelBoldRequired(String labelText, EpState epState, IEpLayoutData data);

	/**
	 * Adds a list to the composite.
	 * 
	 * @param listLabel the label of the created list
	 * @param multiSelection enables multi selection if true
	 * @param epState the EP UI state flag (can be EDITABLE, READ_ONLY, DISABLED). If EDITABLE EditingSupport should be set in order this mode to be
	 *            valid.
	 * @param data EP layout data
	 * @return IEpListViewer
	 * @see IEpListViewer
	 */
	IEpListViewer addListViewer(String listLabel, boolean multiSelection, EpState epState, IEpLayoutData data);

	/**
	 * Adds a list to the composite.
	 * 
	 * @param listLabel the label of the created list
	 * @param multiSelection enables multi selection if true
	 * @param epState the EP UI state flag (can be EDITABLE, READ_ONLY, DISABLED). If EDITABLE EditingSupport should be set in order this mode to be
	 *            valid.
	 * @param data EP layout data
	 * @param required if true, marks the list viewer label with asterisk
	 * @return IEpListViewer
	 * @see IEpListViewer
	 */
	IEpListViewer addListViewer(String listLabel, boolean multiSelection, EpState epState, IEpLayoutData data, boolean required);
	
	/**
	 * Adds new UI group to the composite.
	 * 
	 * @param groupLabel the text label to be used by the group
	 * @param numColumns the number of columns
	 * @param equalWidthColumns sets whether the columns should be the same width
	 * @param data the layout data. If data is <code>null</code> the default layout data for filling the cell will be used
	 * @return instance of <code>IEpLayoutComposite</code>
	 */
	IEpLayoutComposite addGroup(String groupLabel, int numColumns, boolean equalWidthColumns, IEpLayoutData data);

	/**
	 * Adds a horizontal separator to the composite.
	 * 
	 * @param data the layout data. If data is <code>null</code> the default layout data for filling the cell will be used
	 * @return <code>Label</code> representing the separator
	 */
	Label addHorizontalSeparator(IEpLayoutData data);

	/**
	 * Adds a text hyperlink to this composite.
	 * 
	 * @param text the text of the hyperlink or null if none
	 * @param epState the EP UI state flag (can be EDITABLE, READ_ONLY, DISABLED)
	 * @param data the EP layout data
	 * @return Hyperlink
	 * @see Hyperlink
	 * @see FormToolkit#createHyperlink(Composite, String, int)
	 */
	Hyperlink addHyperLinkText(String text, EpState epState, IEpLayoutData data);

	/**
	 * Adds an image hyperlink to this composite.
	 * 
	 * @param image the image to be displayed or null if none
	 * @param epState the EP UI state flag (can be EDITABLE, READ_ONLY, DISABLED)
	 * @param data the EP layout data
	 * @return Hyperlink
	 * @see ImageHyperlink
	 * @see FormToolkit#createImageHyperlink(Composite, int)
	 */
	ImageHyperlink addHyperLinkImage(Image image, EpState epState, IEpLayoutData data);

	/**
	 * Creates a new date/time picker text field and a button next to it on the right.
	 * <p>
	 * For displaying the calendar/time chooser there are two possibilities.<br>
	 * 1) Pass the <code>IEpDateTimePicker</code> interface as a selection listener parameter to a SWT UI component using addSelectionListener().<br>
	 * 2) Call the open method when the date/time picker should be displayed
	 * 
	 * @param style the style that should be a constant from the <code>IEpDateTimePicker</code> interface.
	 * @param epState the EP UI state flag (can be EDITABLE, READ_ONLY, DISABLED)
	 * @param data EP layout data
	 * @return EP date/time picker
	 */
	IEpDateTimePicker addDateTimeComponent(int style, EpState epState, IEpLayoutData data);

	/**
	 * Adds new combobox to the composite.
	 * 
	 * @param epState the EP UI state flag (can be EDITABLE, READ_ONLY, DISABLED)
	 * @param data the layout data. If data is <code>null</code> the default layout data for filling the cell will be used
	 * @return instance of the Eclipse <code>Combo</code>
	 */
	CCombo addComboBox(EpState epState, IEpLayoutData data);

	/**
	 * Sets the form style to be enabled/disabled for this composite.
	 * 
	 * @param isFormStyle boolean defining the form style to enabled/disabled for this composite
	 */
	void setFormStyle(boolean isFormStyle);

	// layout data
	/**
	 * Creates a layout data according to the parameters specified.
	 * 
	 * @param horizontalAlignment how control will be positioned horizontally within a cell
	 * @param verticalAlignment how control will be positioned vertically within a cell
	 * @param grabExcessHorizontalSpace whether cell will be made wide enough to fit the remaining horizontal space
	 * @param grabExcessVerticalSpace whether cell will be made high enough to fit the remaining vertical space
	 * @param horizontalSpan the number of column cells that the control will take up
	 * @param verticalSpan the number of row cells that the control will take up
	 * @return instance of <code>IEpLayoutData</code>
	 */
	IEpLayoutData createLayoutData(int horizontalAlignment, int verticalAlignment, boolean grabExcessHorizontalSpace,
			boolean grabExcessVerticalSpace, int horizontalSpan, int verticalSpan);

	/**
	 * Creates a layout data for a control that spans 1 column and 1 row.
	 * <p>
	 * The same as
	 * <p>
	 * <code>
	 * IEpLayoutComposite.createLayoutData(horizontalAlignment, vericalAlignment, grabExcessHorizontalSpace, grabExcessVerticalSpace, 1, 1);
	 * </code>
	 * 
	 * @param horizontalAlignment how control will be positioned horizontally within a cell
	 * @param verticalAlignment how control will be positioned vertically within a cell
	 * @param grabExcessHSpace whether cell will be made wide enough to fit the remaining horizontal space
	 * @param grabExcessVSpace whether cell will be made high enough to fit the remaining vertical space
	 * @return instance of <code>IEpLayoutData</code>
	 */
	IEpLayoutData createLayoutData(int horizontalAlignment, int verticalAlignment, boolean grabExcessHSpace, boolean grabExcessVSpace);

	/**
	 * Creates a layout data for a control that does not grab the excess horizontal/vertical space available and spans 1 column and 1 row.
	 * <p>
	 * The same as
	 * <p>
	 * <code>IEpLayoutComposite.createLayoutData(horizontalAlignment, vericalAlignment, false, false, 1, 1);</code>
	 * 
	 * @param horizontalAlignment how control will be positioned horizontally within a cell
	 * @param verticalAlignment how control will be positioned vertically within a cell
	 * @return instance of <code>IEpLayoutData</code>
	 */
	IEpLayoutData createLayoutData(int horizontalAlignment, int verticalAlignment);

	/**
	 * Creates the default layout data which makes the control to fill horizontally and vertically the cell.
	 * <p>
	 * The same as
	 * <p>
	 * <code>IEpLayoutComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, false, false, 1, 1);</code>
	 * 
	 * @return instance of <code>IEpLayoutData</code>
	 */
	IEpLayoutData createLayoutData();

	/**
	 * Sets the layout data for this composite. Depends on the parent composite layout.
	 * 
	 * @param data the layout data
	 */
	void setLayoutData(Object data);

	/**
	 * Used to check if the form style has been set. By default it is true.
	 * 
	 * @return true if the form style is used
	 */
	boolean isFormStyle();

	/**
	 * Adds a new TableViewer. 
	 * 
	 * @param multiSelection true if multi-row selection mode has to be set
	 * @param epState the EP UI state flag (can be EDITABLE, READ_ONLY, DISABLED)
	 * @param data EP layout data. If data is <code>null</code> the default layout data for filling the cell will be used
	 * @param tableName name of the table
	 * @return JFace <code>TableViewer</code>
	 * @see IEpLayoutData
	 */
	IEpTableViewer addTableViewer(boolean multiSelection, EpState epState, IEpLayoutData data, String tableName);

	/**
	 * Adds a new TableViewer to the current composite.
	 * 
	 * <br><i>NOTE: Use only with the predefined EP styles!</i>
	 * 
	 * @param style SWT style flags
	 * @param epState the EP UI state flag (can be EDITABLE, READ_ONLY, DISABLED)
	 * @param data EP layout data. If data is <code>null</code> the default layout data for filling the cell will be used
	 * @param tableName name of the table
	 * @return JFace <code>TableViewer</code>
	 * @see IEpLayoutData
	 */
	IEpTableViewer addTableViewer(int style, EpState epState, IEpLayoutData data, String tableName);

	/**
	 * Adds a new CheckboxTableViewer. 
	 * 
	 * @param showHeaders shows/hides table headers
	 * @param epState the EP UI state flag (can be EDITABLE, READ_ONLY, DISABLED)
	 * @param data EP layout data. If data is <code>null</code> the default layout data for filling the cell will be used
	 * @param tableName name of the table
	 * @return JFace <code>CheckboxTableViewer</code>
	 * @see IEpLayoutData
	 */
	IEpTableViewer addCheckboxTableViewer(boolean showHeaders, EpState epState, IEpLayoutData data, String tableName);

	/**
	 * Adds new tree viewer.
	 * 
	 * @param multiSelection true if multi selection is supported
	 * @param epState the EP UI state flag (can be EDITABLE, READ_ONLY, DISABLED)
	 * @param data EP layout data
	 * @return IEpTreeViewer
	 * @see IEpTreeViewer
	 */
	IEpTreeViewer addTreeViewer(boolean multiSelection, EpState epState, IEpLayoutData data);

	/**
	 * Adds a push button with an image to the composite.
	 * 
	 * @param buttonLabel the text label
	 * @param image the image to be set to the button
	 * @param epState the EP UI state flag (can be EDITABLE, READ_ONLY, DISABLED)
	 * @param data the layout data. If data is <code>null</code> the default layout data for filling the cell will be used
	 * @return <code>Button</code>, Eclipse UI control
	 */
	Button addPushButton(String buttonLabel, Image image, EpState epState, IEpLayoutData data);

	/**
	 * Adds a tab folder to the composite.
	 * 
	 * @param data EP layout data
	 * @return EP tab folder object
	 * @see IEpTabFolder
	 */
	IEpTabFolder addTabFolder(IEpLayoutData data);

	/**
	 * Set a listener that will be notified of all modifications to controls on this composite.
	 * 
	 * @param controlModificationListener an implementor of <code>ControlModificationListener</code>.
	 */
	void setControlModificationListener(ControlModificationListener controlModificationListener);

	/**
	 * Adds an image to the next cell in the grid of the composite.
	 * 
	 * @param image the Eclipse <code>Image</code>
	 * @param data EP layout data. If data is <code>null</code> the default layout data for filling the cell will be used
	 * @return <code>Label</code> where the image resides
	 */
	Label addImage(Image image, IEpLayoutData data);

	/**
	 * Adds an empty component to the next cell in the grid of this composite.
	 * 
	 * @param data EP grid layout data. If data is <code>null</code> the default layout data for filling the cell will be used
	 */
	void addEmptyComponent(IEpLayoutData data);

	/**
	 * Gets the FormToolkit used by the layout composite.
	 * 
	 * @return <code>FormToolkit</code>
	 */
	FormToolkit getFormToolkit();

	/**
	 * Adds a spinner text to this composite.
	 * 
	 * @param epState the EP UI state flag (can be EDITABLE, READ_ONLY, DISABLED)
	 * @param data the EP layout data
	 * @return Spinner
	 * @see Spinner
	 */
	Spinner addSpinnerField(EpState epState, IEpLayoutData data);

	/**
	 * Adds addCheckboxTableViewer to this composite.
	 * 
	 * @param epState the EP UI state flag (can be EDITABLE, READ_ONLY, DISABLED)
	 * @param data the EP layout data
	 * @param showHeaders if table headers should be shown.
	 * @param tableName name of the table
	 * @return CheckboxTableViewer
	 * @see CheckboxTableViewer
	 */
	CheckboxTableViewer addCheckboxTableViewer(EpState epState, IEpLayoutData data, boolean showHeaders, String tableName);

	/**
	 * Gets the instance of the EpControlFactory used by this EP composite.
	 * 
	 * @return EpControlFactory
	 */
	EpControlFactory getEpControlFactory();

	/**
	 * Adds a label without wrapper composite.
	 *
	 * @param layoutData the layout data
	 * @param text       the label text
	 * @return the label
	 */
	Label addSkinnyLabel(String text, IEpLayoutData layoutData);
}
