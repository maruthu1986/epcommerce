/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.ui.framework.impl;

import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ImageHyperlink;

import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.ControlModificationListener;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpDateTimePicker;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpListViewer;
import com.elasticpath.cmclient.core.ui.framework.IEpTabFolder;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.core.ui.framework.IEpTreeViewer;

/**
 * Abstract class providing basic implementation of all 'add' methods.
 */
@SuppressWarnings({"PMD.TooManyMethods", "PMD.GodClass"})
public abstract class AbstractEpLayoutComposite implements IEpLayoutComposite {
	private final boolean equalWidthColumns;

	private final int numColumns;

	private Composite composite;

	private EpControlFactory epControlFactory;

	/**
	 * Creates the abstract EP layout composite.
	 *
	 * @param parentComposite   the parent composite
	 * @param numColumns        columns count
	 * @param equalWidthColumns sets equal width columns
	 */
	public AbstractEpLayoutComposite(final Composite parentComposite, final int numColumns, final boolean equalWidthColumns) {
		this.numColumns = numColumns;
		this.equalWidthColumns = equalWidthColumns;

		this.createComposite0(parentComposite, numColumns, equalWidthColumns);
	}

	/**
	 * @param numColumns
	 * @param equalWidthColumns
	 */
	private void createComposite0(final Composite parentComposite, final int numColumns, final boolean equalWidthColumns) {
		if (this.getEpControlFactory().isFormStyle()) {
			this.composite = this.getFormToolkit().createComposite(parentComposite, SWT.WRAP);
			this.composite.setLayout(this.newLayoutInstance(numColumns, equalWidthColumns));
		} else {
			this.composite = new Composite(parentComposite, SWT.WRAP);
			this.composite.setLayout(this.newLayoutInstance(numColumns, equalWidthColumns));
		}
	}

	/**
	 * Constructor for wrapping an existing composite.
	 *
	 * @param composite         the SWT composite
	 * @param numColumns        columns count
	 * @param equalWidthColumns sets whether the columns should be equal width
	 * @param data              the EP layout data
	 */
	public AbstractEpLayoutComposite(final Composite composite, final int numColumns, final boolean equalWidthColumns, final IEpLayoutData data) {
		this.numColumns = numColumns;
		this.equalWidthColumns = equalWidthColumns;
		this.composite = composite;

		this.composite.setLayout(this.newLayoutInstance(numColumns, equalWidthColumns));
		this.composite.setLayoutData(this.adaptEpLayoutData(data));
	}

	/**
	 * Sets the layout data of the current composite.
	 *
	 * @param data Eclipse layout data
	 */
	public void setLayoutData(final Object data) {
		this.getSwtComposite().setLayoutData(data);
	}

	/**
	 * Retrieve the underlying SWT Composite.
	 *
	 * @return the underlying SWT composite
	 */
	public Composite getSwtComposite() {
		return this.composite;
	}

	/**
	 * Adds a Scale.
	 *
	 * @param minimum       min value for Scale
	 * @param maximum       max value for Scale
	 * @param increment     step
	 * @param pageIncrement - page increment
	 * @param style         SWT.VERTICAL or SWT.HORIZONTAL
	 * @param epState       the EP UI state flag (can be EDITABLE, READ_ONLY, DISABLED)
	 * @param data          EP layout data
	 * @return <code>Scale</code>
	 */
	public Scale addScale(final int minimum,
		final int maximum,
		final int increment,
		final int pageIncrement,
		final int style,
		final EpState epState,
		final IEpLayoutData data) {
		final Scale scale = this.getEpControlFactory().createScale(
			this.getSwtComposite(),
			minimum,
			maximum,
			increment,
			pageIncrement,
			style,
			epState);
		scale.setLayoutData(this.adaptEpLayoutData(data));
		return scale;
	}


	/**
	 * Adds a push button.
	 *
	 * @param buttonLabel button text label
	 * @param epState     the EP UI state flag (can be EDITABLE, READ_ONLY, DISABLED)
	 * @param data        EP layout data
	 * @return Button
	 */
	public Button addPushButton(final String buttonLabel, final EpState epState, final IEpLayoutData data) {
		final Button button = this.getEpControlFactory().createButton(this.getSwtComposite(), buttonLabel, SWT.PUSH, epState);
		button.setLayoutData(this.adaptEpLayoutData(data));
		return button;
	}

	/**
	 * Creates new Expandable composite with a twistie if there is a title defined. If title is null then no twistie is created and the expanded
	 * state is meant to be changed programmatically using {@link ExpandableComposite#setExpanded(boolean)}
	 *
	 * @param numColumns        number of columns to be created for the EP layout composite
	 * @param equalWidthColumns should the columns be equal size
	 * @param title             the title label
	 * @param data              EP layout data
	 * @return IEpLayoutComposite
	 */
	public IEpLayoutComposite addExpandableComposite(final int numColumns, final boolean equalWidthColumns, final String title,
		final IEpLayoutData data) {
		final ExpandableComposite expComposite = getEpControlFactory().createExpandableComposite(getSwtComposite(), title);

		final IEpLayoutComposite clientComposite = newCompositeInstance(expComposite, numColumns, equalWidthColumns);
		expComposite.setLayoutData(adaptEpLayoutData(data));
		expComposite.setClient(clientComposite.getSwtComposite());
		expComposite.addExpansionListener(new ExpansionAdapter() {
			@Override
			public void expansionStateChanged(final ExpansionEvent event) {
				getSwtComposite().getParent().layout();
				// invoke resize event
				EpEventService.getInstance().fireResizeEvent();
			}
		});
		return clientComposite;
	}

	/**
	 * Gets the EP control factory.
	 *
	 * @return EPControlFactory
	 */
	public EpControlFactory getEpControlFactory() {
		if (this.epControlFactory == null) {
			this.epControlFactory = EpControlFactory.getInstance();
		}
		return this.epControlFactory;
	}

	/**
	 * Adds new push button.
	 *
	 * @param buttonLabel text label on button
	 * @param image       image to set to the button
	 * @param epState     the EP UI state flag (can be EDITABLE, READ_ONLY, DISABLED)
	 * @param data        EP layout data
	 * @return Button
	 */
	public Button addPushButton(final String buttonLabel, final Image image, final EpState epState, final IEpLayoutData data) {
		final Button button = this.addPushButton(buttonLabel, epState, data);
		button.setImage(image);
		return button;
	}

	/**
	 * Adds a checkbox button.
	 *
	 * @param checkBoxLabel the text label
	 * @param epState       the EP UI state flag (can be EDITABLE, READ_ONLY, DISABLED)
	 * @param data          the EP layout data
	 * @return Button
	 */
	public Button addCheckBoxButton(final String checkBoxLabel, final EpState epState, final IEpLayoutData data) {
		final Button button = this.getEpControlFactory().createButton(this.getSwtComposite(), checkBoxLabel, SWT.CHECK, epState);
		button.setLayoutData(this.adaptEpLayoutData(data));
		return button;
	}

	/**
	 * Adds new radio button with the specified label, or a blank label if the specified label is null.
	 *
	 * @param radioLabel the text label
	 * @param epState    the EP UI state flag (can be EDITABLE, READ_ONLY, DISABLED)
	 * @param data       the layout data. If data is <code>null</code> the default layout data for filling the cell will be used
	 * @return <code>Button</code>, Eclipse UI control
	 */
	public Button addRadioButton(final String radioLabel, final EpState epState, final IEpLayoutData data) {
		final Button button = this.getEpControlFactory().createButton(this.getSwtComposite(), radioLabel, SWT.RADIO, epState);
		button.setLayoutData(this.adaptEpLayoutData(data));
		return button;
	}

	/**
	 * Adds new push button.
	 *
	 * @param radioLabel the text label
	 * @param image      image to set to the button
	 * @param epState    the EP UI state flag (can be EDITABLE, READ_ONLY, DISABLED)
	 * @param data       the layout data. If data is <code>null</code> the default layout data for filling the cell will be used
	 * @return <code>Button</code>, Eclipse UI control
	 */
	public Button addRadioButton(final String radioLabel, final Image image, final EpState epState, final IEpLayoutData data) {
		final Button button = this.addRadioButton(radioLabel, epState, data);
		button.setLayoutData(this.adaptEpLayoutData(data));
		button.setImage(image);
		return button;
	}


	/**
	 * Adds a list.
	 *
	 * @param epState the EP UI state flag (can be EDITABLE, READ_ONLY, DISABLED)
	 * @param data    the EP layout data
	 * @return List
	 */
	public List addList(final EpState epState, final IEpLayoutData data) {
		final List list = this.getEpControlFactory().createList(this.getSwtComposite(), epState);
		list.setLayoutData(this.adaptEpLayoutData(data));
		return list;
	}

	/**
	 * Adds a group.
	 *
	 * @param groupLabel        the group label, or null if no label
	 * @param numColumns        columns count
	 * @param equalWidthColumns set equal width columns
	 * @param data              EP layout data
	 * @return EP layout composite
	 */
	public IEpLayoutComposite addGroup(final String groupLabel, final int numColumns, final boolean equalWidthColumns, final IEpLayoutData data) {
		final Composite group = this.getEpControlFactory().createGroup(this.getSwtComposite(), groupLabel);

		return this.newWrapperCompositeInstance(group, numColumns, equalWidthColumns, data);
	}

	/**
	 * Creates new EP composite instance.
	 *
	 * @param parentComposite   Eclipse parent composite for the new EP composite
	 * @param numColumns        columns count of new EP composite
	 * @param equalWidthColumns sets equal width columns
	 * @return EP layout composite
	 */
	protected abstract IEpLayoutComposite newCompositeInstance(Composite parentComposite, int numColumns, boolean equalWidthColumns);

	/**
	 * Method that should be implemented by the extending class. Should wrap the existing composite and its parent to an EP layout composite object.
	 *
	 * @param composite         the SWT composite to be wrapped
	 * @param numColumns        columns count
	 * @param equalWidthColumns sets equal width columns
	 * @param data              EP layout data
	 * @return EP layout composite
	 */
	protected abstract IEpLayoutComposite newWrapperCompositeInstance(Composite composite, int numColumns, boolean equalWidthColumns,
		IEpLayoutData data);

	/**
	 * Adds a label.
	 *
	 * @param labelText text to be displayed
	 * @param data      EP layout data
	 * @return Label
	 */
	public Label addLabel(final String labelText, final IEpLayoutData data) {
		return this.getEpControlFactory().createLabel(this.getSwtComposite(), labelText, SWT.NONE, this.adaptEpLayoutData(data));
	}

	@Override
	public Label addSkinnyLabel(final String text, final IEpLayoutData layoutData) {
		return this.getEpControlFactory().createSkinnyLabel(this.getSwtComposite(), text, this.adaptEpLayoutData(layoutData));
	}

	/**
	 * Adds a text field.
	 *
	 * @param epState the EP UI state flag (can be EDITABLE, READ_ONLY, DISABLED)
	 * @param data    EP layout data
	 * @return Text
	 */
	public Text addTextField(final EpState epState, final IEpLayoutData data) {
		final Text text = this.getEpControlFactory().createTextField(this.getSwtComposite(), SWT.NONE, epState);
		text.setLayoutData(this.adaptEpLayoutData(data));

		return text;
	}

	/**
	 * Adds a text area without scrolls.
	 *
	 * @param epState the EP UI state flag (can be EDITABLE, READ_ONLY, DISABLED)
	 * @param data    EP layout data
	 * @return Text
	 */
	public Text addTextArea(final EpState epState, final IEpLayoutData data) {
		final Text text = this.getEpControlFactory().createTextArea(this.getSwtComposite(), false, false, epState);
		text.setLayoutData(this.adaptEpLayoutData(data));

		return text;
	}

	/**
	 * Adds a text area and scrolls depending on the parameters.
	 *
	 * @param hasVerticalScroll   should the text area have vertical scroll
	 * @param hasHorizontalScroll should the text area have horizontal scroll
	 * @param epState             EpState
	 * @param data                EP layout data
	 * @return multi line Text
	 * @see Text
	 */
	public Text addTextArea(final boolean hasVerticalScroll, final boolean hasHorizontalScroll, final EpState epState, final IEpLayoutData data) {
		final Text text = this.getEpControlFactory().createTextArea(this.getSwtComposite(), hasVerticalScroll, hasHorizontalScroll, epState);
		text.setLayoutData(this.adaptEpLayoutData(data));

		return text;
	}

	/**
	 * Adds a password field that echos an obscured character.
	 *
	 * @param epState the EP UI state flag (can be EDITABLE, READ_ONLY, DISABLED)
	 * @param data    EP layout data
	 * @return a Password Text control
	 */
	public Text addPasswordField(final EpState epState, final IEpLayoutData data) {
		final Text passwordText = this.getEpControlFactory().createTextField(this.getSwtComposite(), SWT.PASSWORD | SWT.BORDER, epState);
		passwordText.setLayoutData(this.adaptEpLayoutData(data));
		return passwordText;
	}

	/**
	 * Adds a text hyperlink to this composite.
	 *
	 * @param text    the text of the hyperlink or null if none
	 * @param epState the EP UI state flag (can be EDITABLE, READ_ONLY, DISABLED)
	 * @param data    the EP layout data
	 * @return Hyperlink
	 * @see Hyperlink
	 * @see FormToolkit#createHyperlink(Composite, String, int)
	 */
	public Hyperlink addHyperLinkText(final String text, final EpState epState, final IEpLayoutData data) {
		final Hyperlink hyperlinkText = this.getEpControlFactory().createHyperLinkText(this.getSwtComposite(), text, epState);
		hyperlinkText.setLayoutData(this.adaptEpLayoutData(data));

		return hyperlinkText;
	}

	/**
	 * Adds an image hyperlink to this composite.
	 *
	 * @param image   the image to be displayed or null if none
	 * @param epState the EP UI state flag (can be EDITABLE, READ_ONLY, DISABLED)
	 * @param data    the EP layout data
	 * @return <code>Hyperlink</code>
	 * @see Hyperlink
	 * @see FormToolkit#createHyperlink(Composite, String, int)
	 */
	public ImageHyperlink addHyperLinkImage(final Image image, final EpState epState, final IEpLayoutData data) {
		final ImageHyperlink hyperlinkImage = this.getEpControlFactory().createHyperLinkImage(this.getSwtComposite(), image, epState);
		hyperlinkImage.setLayoutData(this.adaptEpLayoutData(data));

		return hyperlinkImage;
	}

	/**
	 * Adds a spinner text field.
	 *
	 * @param epState the EP UI state flag (can be EDITABLE, READ_ONLY, DISABLED)
	 * @param data    the EP layout data
	 * @return Spinner
	 * @see Spinner
	 */
	public Spinner addSpinnerField(final EpState epState, final IEpLayoutData data) {
		final Spinner spinner = this.getEpControlFactory().createSpinnerField(this.getSwtComposite(), epState);
		spinner.setLayoutData(this.adaptEpLayoutData(data));
		return spinner;
	}

	/**
	 * Adds a CheckboxTableViewer.
	 *
	 * @param epState     the EP UI state flag (can be EDITABLE, READ_ONLY, DISABLED)
	 * @param data        the EP layout data
	 * @param showHeaders if table headers should be shown.
	 * @param tableName name of the table
	 * @return CheckboxTableViewer
	 * @see CheckboxTableViewer
	 */
	public CheckboxTableViewer addCheckboxTableViewer(final EpState epState, final IEpLayoutData data, final boolean showHeaders,
		final String tableName) {
		final CheckboxTableViewer tableViewer = this.getEpControlFactory()
			.createCheckboxTableViewer(this.getSwtComposite(), epState, showHeaders, tableName);
		tableViewer.getTable().setLayoutData(this.adaptEpLayoutData(data));
		return tableViewer;
	}

	/**
	 * Adds a bold label.
	 *
	 * @param labelText the text to be displayed
	 * @param data      EP layout data
	 * @return Label
	 */
	public Label addLabelBold(final String labelText, final IEpLayoutData data) {
		return this.getEpControlFactory().createLabelBold(this.getSwtComposite(), labelText, SWT.BOLD, this.adaptEpLayoutData(data));
	}

	/**
	 * Adds a bold label, prepending it with an asterisk and suffixing it with a colon.
	 *
	 * @param labelText the text to be displayed
	 * @param epState   the EP UI state flag (can be EDITABLE, READ_ONLY, DISABLED)
	 * @param data      EP layout data
	 * @return Label
	 */
	public Label addLabelBoldRequired(final String labelText, final EpState epState, final IEpLayoutData data) {
		return this.getEpControlFactory().createLabelBoldRequired(this.getSwtComposite(), labelText, epState, this.adaptEpLayoutData(data));
	}

	/**
	 * Adds Image to current composite.
	 *
	 * @param image the image to add
	 * @param data  EP layout data
	 * @return Label
	 */
	public Label addImage(final Image image, final IEpLayoutData data) {
		final Label label = this.getEpControlFactory().createImage(this.getSwtComposite(), image);
		label.setLayoutData(this.adaptEpLayoutData(data));
		return label;

	}

	/**
	 * Adds an empty label component. Used for setting empty cell in a grid.
	 *
	 * @param data EP layout data
	 */
	public void addEmptyComponent(final IEpLayoutData data) {
		final Label label = this.getEpControlFactory().createEmptyComponent(this.getSwtComposite());
		label.setLayoutData(this.adaptEpLayoutData(data));
	}

	/**
	 * Adds a combo box.
	 *
	 * @param epState the EP UI state flag (can be EDITABLE, READ_ONLY, DISABLED)
	 * @param data    EP layout data
	 * @return CCombo
	 */
	public CCombo addComboBox(final EpState epState, final IEpLayoutData data) {
		final CCombo combo = this.getEpControlFactory().createComboBox(this.getSwtComposite(), SWT.DROP_DOWN | SWT.BORDER | SWT.READ_ONLY, epState);
		combo.setLayoutData(this.adaptEpLayoutData(data));
		return combo;
	}

	/**
	 * Adds horizontal separator line.
	 *
	 * @param data EP layout data
	 * @return <code>Label</code>
	 */
	public Label addHorizontalSeparator(final IEpLayoutData data) {
		final Label label = this.getEpControlFactory().createHorizontalSeparator(this.getSwtComposite());
		label.setLayoutData(this.adaptEpLayoutData(data));
		return label;
	}

	/**
	 * Adds a table representing a list component to the composite.
	 *
	 * @param listLabel      the list label
	 * @param multiSelection enables multi selection
	 * @param epState        the EP UI state flag (can be EDITABLE, READ_ONLY, DISABLED)
	 * @param data           EP layout data
	 * @return ListViewer
	 */
	public IEpListViewer addListViewer(final String listLabel, final boolean multiSelection, final EpState epState, final IEpLayoutData data) {
		return addListViewer(listLabel, multiSelection, epState, data, false);
	}

	/**
	 * Adds a table representing a list component to the composite.
	 *
	 * @param listLabel      the list label
	 * @param multiSelection enables multi selection
	 * @param epState        the EP UI state flag (can be EDITABLE, READ_ONLY, DISABLED)
	 * @param data           EP layout data
	 * @param required       if true, marks the list viewer label with asterisk
	 * @return ListViewer
	 */
	public IEpListViewer addListViewer(final String listLabel, final boolean multiSelection, final EpState epState, final IEpLayoutData data,
		final boolean required) {
		final IEpListViewer viewer = this.getEpControlFactory().createListViewer(this.getSwtComposite(), listLabel, multiSelection, epState,
			required);
		viewer.setLayoutData(this.adaptEpLayoutData(data));
		return viewer;
	}

	/**
	 * Adds a new TableViewer to the current composite.
	 *
	 * @param multiSelection true if multi-row selection mode has to be set
	 * @param epState        the EP UI state flag (can be EDITABLE, READ_ONLY, DISABLED)
	 * @param data           EP layout data. If data is <code>null</code> the default layout data for filling the cell will be used
	 * @param tableName name of the table
	 * @return JFace <code>TableViewer</code>
	 * @see IEpLayoutData
	 */
	public IEpTableViewer addTableViewer(final boolean multiSelection, final EpState epState, final IEpLayoutData data,
		final String tableName) {
		final IEpTableViewer viewer = this.getEpControlFactory().createTableViewer(this.getSwtComposite(), multiSelection, epState, tableName);
		viewer.setLayoutData(this.adaptEpLayoutData(data));
		return viewer;
	}

	/**
	 * Adds a new TableViewer to the current composite.
	 * <p>
	 * <br><i>NOTE: Use only with the predefined EP styles!</i>
	 *
	 * @param style   SWT style flags
	 * @param epState the EP UI state flag (can be EDITABLE, READ_ONLY, DISABLED)
	 * @param data    EP layout data. If data is <code>null</code> the default layout data for filling the cell will be used
	 * @param tableName name of the table
	 * @return JFace <code>TableViewer</code>
	 * @see IEpLayoutData
	 */
	public IEpTableViewer addTableViewer(final int style, final EpState epState, final IEpLayoutData data, final String tableName) {
		final IEpTableViewer viewer = this.getEpControlFactory().createTableViewer(this.getSwtComposite(), style, epState, tableName);
		viewer.setLayoutData(this.adaptEpLayoutData(data));
		return viewer;
	}

	@Override
	public IEpTableViewer addCheckboxTableViewer(final boolean showHeaders, final EpState epState, final IEpLayoutData data,
		final String tableName) {
		final IEpTableViewer viewer = new EpTableViewer(this.getEpControlFactory().
			createCheckboxTableViewer(this.getSwtComposite(), epState, showHeaders, tableName), epState == EpState.EDITABLE);
		viewer.setLayoutData(this.adaptEpLayoutData(data));
		return viewer;
	}

	/**
	 * Adds new tree viewer.
	 *
	 * @param multiSelection true if multi selection is supported
	 * @param epState        the EP UI state flag (can be EDITABLE, READ_ONLY, DISABLED)
	 * @param data           EP layout data
	 * @return IEpTreeViewer
	 * @see IEpTreeViewer
	 */
	public IEpTreeViewer addTreeViewer(final boolean multiSelection, final EpState epState, final IEpLayoutData data) {
		final IEpTreeViewer viewer = this.getEpControlFactory().createTreeViewer(this.getSwtComposite(), multiSelection, epState);
		viewer.setLayoutData(this.adaptEpLayoutData(data));
		return viewer;
	}

	/**
	 * Creates a new date/time picker text field. For displaying it there are two possibilities.
	 * <p>
	 * 1) Pass the <code>IEpDateTimePicker</code> interface as a selection listener parameter to a SWT UI component using addSelectionListener().<br>
	 * 2) Call the open method when the date/time picker should be displayed
	 *
	 * @param style   the style that should be a constant from the <code>IEpDateTimePicker</code> interface.
	 * @param epState the EP UI state flag (can be EDITABLE, READ_ONLY, DISABLED)
	 * @param data    EP layout data
	 * @return EP date/time picker
	 */
	public IEpDateTimePicker addDateTimeComponent(final int style, final EpState epState, final IEpLayoutData data) {
		final EpTextDateTimePicker dateTimePicker = (EpTextDateTimePicker) this.getEpControlFactory().createDateTimeComponent(
			this.getSwtComposite(), style, epState);
		dateTimePicker.getSwtText().getParent().setLayoutData(this.adaptEpLayoutData(data));
		return dateTimePicker;
	}

	@Override
	public IEpLayoutComposite addScrolledGridLayoutComposite(final int numColumns, final boolean equalWidthColumns,
															 final boolean grabHorizontalSpace, final IEpLayoutData data) {
		IEpLayoutComposite resultComposite = addScrolledGridLayoutComposite(numColumns, equalWidthColumns, grabHorizontalSpace);
		resultComposite.setLayoutData(data);
		return resultComposite;
	}

	@Override
	public IEpLayoutComposite addScrolledGridLayoutComposite(final int numColumns, final boolean equalWidthColumns, final IEpLayoutData data) {
		IEpLayoutComposite resultComposite = addScrolledGridLayoutComposite(numColumns, equalWidthColumns, true);
		resultComposite.setLayoutData(data);
		return resultComposite;
	}

	@Override
	public IEpLayoutComposite addScrolledGridLayoutComposite(final int numColumns, final boolean equalWidthColumns) {
		return addScrolledGridLayoutComposite(numColumns, equalWidthColumns, true);
	}

	@Override
	public IEpLayoutComposite addScrolledGridLayoutComposite(final int numColumns, final boolean equalWidthColumns,
		final boolean grabHorizontalSpace) {

		final ScrolledComposite scrolledComposite = new ScrolledComposite(this.getSwtComposite(), SWT.V_SCROLL | SWT.H_SCROLL);
		scrolledComposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, grabHorizontalSpace, true));
		scrolledComposite.setLayout(new GridLayout());

		scrolledComposite.setExpandVertical(true);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setRedraw(true);

		final IEpLayoutComposite resultComposite = CompositeFactory.createGridLayoutComposite(scrolledComposite, numColumns, equalWidthColumns);
		scrolledComposite.setContent(resultComposite.getSwtComposite());

		scrolledComposite.addControlListener(new ControlAdapter() {
			public void controlResized(final ControlEvent event) {
				Rectangle rect = scrolledComposite.getClientArea();
				scrolledComposite.setMinSize(resultComposite.getSwtComposite().computeSize(rect.width, SWT.DEFAULT));
			}
		});

		return resultComposite;
	}

	/**
	 * Sets the form style enabled/disabled.
	 *
	 * @param isFormStyle boolean
	 */
	public void setFormStyle(final boolean isFormStyle) {
		this.getEpControlFactory().setFormStyle(isFormStyle);
	}

	/**
	 * Gets the form-style value.
	 *
	 * @return true if form-style is enabled
	 */
	public boolean isFormStyle() {
		return this.getEpControlFactory().isFormStyle();
	}

	/**
	 * Adapts the EP layout data to the native Eclipse layout data. Currently used are GridData and TableWrapData
	 *
	 * @param data EP layout data
	 * @return the respective layout data object
	 * @see org.eclipse.ui.forms.widgets.TableWrapData
	 * @see org.eclipse.swt.layout.GridData
	 */
	protected abstract Object adaptEpLayoutData(IEpLayoutData data);

	/**
	 * Has to create new instance of the employed layout.
	 *
	 * @param numColumns        columns count
	 * @param equalWidthColumns should columns be equal width
	 * @return Layout implementation
	 */
	protected abstract Layout newLayoutInstance(final int numColumns, final boolean equalWidthColumns);

	// layout data methods

	/**
	 * Creates layout data.
	 *
	 * @param horizontalAlignment       alignment type
	 * @param verticalAlignment         alignment type
	 * @param grabExcessHorizontalSpace grabs excess horizontal space
	 * @param grabExcessVerticalSpace   grabs excess vertical space
	 * @param horizontalSpan            columns span
	 * @param verticalSpan              rows span
	 * @return EP layout data
	 */
	public IEpLayoutData createLayoutData(final int horizontalAlignment, final int verticalAlignment, final boolean grabExcessHorizontalSpace,
		final boolean grabExcessVerticalSpace, final int horizontalSpan, final int verticalSpan) {
		return new EpLayoutData(this, horizontalAlignment, verticalAlignment, grabExcessHorizontalSpace, grabExcessVerticalSpace, horizontalSpan,
			verticalSpan);
	}

	/**
	 * Creates layout data.
	 *
	 * @param horizontalAlignment       alignment type
	 * @param verticalAlignment         alignment type
	 * @param grabExcessHorizontalSpace grabs excess horizontal space
	 * @param grabExcessVerticalSpace   grabs excess vertical space
	 * @return EP layout data
	 */
	public IEpLayoutData createLayoutData(final int horizontalAlignment, final int verticalAlignment, final boolean grabExcessHorizontalSpace,
		final boolean grabExcessVerticalSpace) {
		return new EpLayoutData(this, horizontalAlignment, verticalAlignment, grabExcessHorizontalSpace, grabExcessVerticalSpace);
	}

	/**
	 * Creates layout data.
	 *
	 * @param horizontalAlignment alignment type
	 * @param verticalAlignment   alignment type
	 * @return EP layout data
	 */
	public IEpLayoutData createLayoutData(final int horizontalAlignment, final int verticalAlignment) {
		return new EpLayoutData(this, horizontalAlignment, verticalAlignment);
	}

	/**
	 * Creates default EP layout data.
	 *
	 * @return EP layout data
	 */
	public IEpLayoutData createLayoutData() {
		return new EpLayoutData(this);
	}

	/**
	 * @return the equalWidthColumns
	 */
	protected boolean isEqualWidthColumns() {
		return this.equalWidthColumns;
	}

	/**
	 * @return the numColumns
	 */
	protected int getNumColumns() {
		return this.numColumns;
	}

	/**
	 * Adds tab folder to the current composite. <br>
	 * <i>Note: Available only within the IEpLayoutComposite interface. TODO: if needed move it to the EpControlFactory</i>
	 *
	 * @param data EP layout data
	 * @return wrapping interface of CTabFolder
	 */
	public IEpTabFolder addTabFolder(final IEpLayoutData data) {
		return new EpTabFolder(this, data);
	}

	/**
	 * Creates new native SWT composite.
	 *
	 * @param parent the parent composite of the one to be created
	 * @return Eclipse Composite
	 */
	protected Composite newSwtComposite(final Composite parent) {
		Composite newComposite;
		if (this.isFormStyle()) {
			newComposite = this.getFormToolkit().createComposite(parent, SWT.WRAP);
		} else {
			newComposite = new Composite(parent, SWT.WRAP);
		}
		return newComposite;
	}

	/**
	 * @return the parentComposite
	 */
	// private Composite getParentComposite() {
	// return this.parentComposite;
	// }

	/**
	 * Gets the FormToolkit instance.
	 *
	 * @return FormToolkit
	 */
	public FormToolkit getFormToolkit() {
		return this.getEpControlFactory().getFormToolkit();
	}

	/**
	 * Sets the control modification listener used by the data binding.
	 *
	 * @param controlModificationListener the listener
	 */
	public void setControlModificationListener(final ControlModificationListener controlModificationListener) {
		this.getEpControlFactory().setControlModificationListener(controlModificationListener);
	}

}
