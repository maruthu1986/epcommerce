/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.ui.framework;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationListener;
import org.eclipse.jface.viewers.ColumnViewerEditorDeactivationEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.elasticpath.cmclient.core.CmClientResources;
import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.helpers.EPTestUtilFactory;
import com.elasticpath.cmclient.core.helpers.TooltipService;
import com.elasticpath.cmclient.core.ui.framework.impl.EpListViewer;
import com.elasticpath.cmclient.core.ui.framework.impl.EpRequiredLabel;
import com.elasticpath.cmclient.core.ui.framework.impl.EpTableViewer;
import com.elasticpath.cmclient.core.ui.framework.impl.EpTextDateTimePicker;
import com.elasticpath.cmclient.core.ui.framework.impl.EpTreeViewer;
import com.elasticpath.cmclient.core.viewers.AssetTableViewer;

/**
 * Factory for creating EP styled controls.
 */
@SuppressWarnings({"PMD.TooManyMethods", "PMD.GodClass", "PMD.ExcessiveClassLength", "PMD.ExcessiveImports"})
public final class EpControlFactory {

	private static final int HORIZONTAL_INDENT = 25;
	private static final String EMPTY_STRING = "";  //$NON-NLS-1$
	private static final String WRAPPER = "wrapper";
	private FormToolkit formToolkit;

	private ControlModificationListener controlModificationListener;

	/**
	 * Used to specify Test IDs based on the name of previously added element.
	 */
	private String lastLabelText = EMPTY_STRING;

	/**
	 * By default the style of the widgets is provided by the form toolkit.
	 */
	private boolean formStyle = true;

	/**
	 * Represents the supported UI states for the supported widgets.
	 */
	public enum EpState {
		/**
		 * Edit-mode state.
		 */
		EDITABLE,
		/**
		 * Read-only state.
		 */
		READ_ONLY,
		/**
		 * Disabled state.
		 */
		DISABLED
	}

	/**
	 * Creates an INSTANCE of this class.
	 */
	private EpControlFactory() {
		super();
	}

	/**
	 * Returns an INSTANCE of this class.
	 * @return instance of EP control factory
	 */
	public static EpControlFactory getInstance() {
		//return CmSingletonUtil.getSessionInstance(EpControlFactory.class);
		//TODO return a single instance of this factory class
		return new EpControlFactory();
	}

	/**
	 * Creates an INSTANCE of the factory with a specific control modification listener.
	 *
	 * @param controlModificationListener control modification listener
	 */
	public EpControlFactory(final ControlModificationListener controlModificationListener) {
		super();
		this.controlModificationListener = controlModificationListener;
	}

	/**
	 * Creates a new button with the specified style.
	 *
	 * @param parentComposite the parent SWT composite
	 * @param buttonLabel     the label of the button
	 * @param style           the style bits. See the reference for details
	 * @param epState         the UI state of the widget
	 * @return Button
	 * @see Button
	 */
	public Button createButton(final Composite parentComposite, final String buttonLabel, final int style, final EpState epState) {
		Button button;
		if (isFormStyle()) {
			button = getFormToolkit().createButton(parentComposite, EMPTY_STRING, style);
		} else {
			button = new Button(parentComposite, style);
		}
		changeEpState(button, epState);
		button.setText(buttonLabel);
		this.addModificationListener(button);
		setWidgetId(button, buttonLabel);
		return button;
	}


	/**
	 * Creates a new Scale with specified style.
	 *
	 * @param parentComposite the parent SWT composite
	 * @param minimum         min value of Scale
	 * @param maximum         max value of Scale
	 * @param increment       step.
	 * @param pageIncrement   page increment.
	 * @param style           the style bits. See the reference for details
	 * @param epState         the UI state of the widget
	 * @return Scale
	 * @see Scale
	 */
	public Scale createScale(final Composite parentComposite,
		final int minimum,
		final int maximum,
		final int increment,
		final int pageIncrement,
		final int style,
		final EpState epState) {
		Scale scale;
		scale = new Scale(parentComposite, style);
		scale.setMinimum(minimum);
		scale.setMaximum(maximum);
		scale.setIncrement(increment);
		scale.setPageIncrement(pageIncrement);
		changeEpState(scale, epState);
		setWidgetId(scale);
		return scale;
	}


	/**
	 * Creates a list.
	 *
	 * @param parentComposite the parent SWT composite
	 * @param epState         the UI state of the widget
	 * @return List
	 */
	public List createList(final Composite parentComposite, final EpState epState) {
		final int style = SWT.V_SCROLL | SWT.BORDER | SWT.H_SCROLL;
		final List list = new List(parentComposite, style);
		if (isFormStyle()) {
			getFormToolkit().adapt(list, true, true);
		}
		changeEpState(list, epState);
		setWidgetId(list);
		return list;
	}

	/**
	 * Creates a group composite with title and border if text is provided.
	 * Otherwise creates a Composite with borders.
	 *
	 * @param parentComposite the parent SWT composite
	 * @param groupText      the label of the group
	 * @return Group
	 * @see Group
	 */
	public Composite createGroup(final Composite parentComposite, final String groupText) {
		if (StringUtils.isBlank(groupText)) {
			//Group places grey box to hold the text, which is redundant because text is Empty, return Composite with the borders.
			return new Composite(parentComposite, SWT.BORDER);
		}

		final Group group = new Group(parentComposite, SWT.NONE);

		group.setText(groupText);
		setWidgetId(group, groupText);

		if (isFormStyle()) {
			getFormToolkit().adapt(group);
		}
		return group;
	}

	/**
	 * Creates expandable composite.
	 *
	 * @param parentComposite parent composite
	 * @param titleLabel      title
	 * @return {@link ExpandableComposite}
	 */
	public ExpandableComposite createExpandableComposite(final Composite parentComposite, final String titleLabel) {
		ExpandableComposite expComposite;
		int expansionStyle = 0;
		if (titleLabel == null) {
			expansionStyle |= ExpandableComposite.NO_TITLE;
		} else {
			expansionStyle |= ExpandableComposite.TWISTIE;
		}
		if (isFormStyle()) {
			expComposite = getFormToolkit().createExpandableComposite(parentComposite, expansionStyle);
		} else {
			expComposite = new ExpandableComposite(parentComposite, SWT.NONE, expansionStyle);
		}
		if (titleLabel != null) {
			expComposite.setText(titleLabel);
		}
		setWidgetId(expComposite, titleLabel);
		return expComposite;
	}

	/**
	 * Creates a hyperlink.
	 *
	 * @param parent     the parent SWT composite
	 * @param swtStyle   style
	 * @param linkText   text of the link
	 * @param layoutData layout data
	 * @return HyperLink
	 */
	public Hyperlink createHyperlink(final Composite parent, final int swtStyle, final String linkText, final GridData layoutData) {
		final Hyperlink link = new Hyperlink(parent, swtStyle);
		link.setText(linkText);
		link.setLayoutData(layoutData);
		link.setBackground(parent.getBackground());

		setWidgetId(link, linkText);
		return link;
	}

	/**
	 * Creates a text field.
	 *
	 * @param parentComposite the parent SWT composite
	 * @param style           style
	 * @param epState         the UI state of the widget
	 * @return Text
	 */
	public Text createTextField(final Composite parentComposite, final int style, final EpState epState) {
		Text text;
		if (isFormStyle()) {
			text = getFormToolkit().createText(parentComposite, EMPTY_STRING, style);
		} else {
			text = new Text(parentComposite, style);
		}
		this.addModificationListener(text);
		changeEpState(text, epState);
		setWidgetId(text);
		return text;
	}

	/**
	 * Creates a text area.
	 *
	 * @param parentComposite the parent SWT composite
	 * @param hasVScroll      should the text area have vertical scroll
	 * @param hasHScroll      should the text area have horizontal scroll
	 * @param epState         the UI state of the widget
	 * @return Text
	 */
	public Text createTextArea(final Composite parentComposite, final boolean hasVScroll, final boolean hasHScroll, final EpState epState) {
		int style = SWT.MULTI | SWT.WRAP | SWT.BORDER;
		if (hasVScroll) {
			style |= SWT.V_SCROLL;
		}
		if (hasHScroll) {
			style |= SWT.H_SCROLL;
		}
		return createTextField(parentComposite, style, epState);
	}

	/**
	 * Changes the control look and feel depending on the epState.<br>
	 * Supports the following SWT widgets: Button, Text, Spinner, Hyperlink, ImageHyperlink, CCombo
	 *
	 * @param control the widget to be changed
	 * @param epState the EpState value
	 */
	public static void changeEpState(final Control control, final EpState epState) {
		if (control == null
			|| control.isDisposed()
			|| epState == null) {
			return;
		}

		if (control instanceof Text) {
			changeTextState((Text) control, epState);
			return;
		}

		if (control instanceof Table) {
			changeTableState((Table) control, epState);
			return;
		}

		boolean enabled = epState == EpState.EDITABLE;
		if (control instanceof Hyperlink || control instanceof Label) {
			Color color;
			if (enabled) {
				color = CmClientResources.getForegroundColor();
			} else {
				color = CmClientResources.getInactiveForegroundColor();
			}
			control.setForeground(color);
		}

		control.setEnabled(enabled);
		control.redraw();
	}

	/**
	 * Changes the control look and feel depending on the epState.<br>
	 * Supports the following SWT widgets: Composite
	 *
	 * @param composite the widget to be changed
	 * @param state     the EpState value
	 */
	public static void changeEpStateForComposite(final Composite composite, final EpState state) {
		changeEpState(composite, state);

		if (composite.isDisposed()) {
			return;
		}

		for (Control control : composite.getChildren()) {
			if (control instanceof Composite) {
				changeEpStateForComposite((Composite) control, state);
			} else {
				changeEpState(control, state);
			}
		}
	}

	/**
	 * Support for check box tables. Sets the check boxes as grayed in case the table should be disabled.
	 *
	 * @param control the table widget to change
	 * @param epState the EpState value
	 */
	private static void changeTableState(final Table control, final EpState epState) {
		if (epState == EpState.READ_ONLY || epState == EpState.DISABLED) {
			for (TableItem child : control.getItems()) {
				child.setGrayed(true);
			}
		} else if (epState == EpState.EDITABLE) {
			for (TableItem child : control.getItems()) {
				child.setGrayed(false);
			}
		}
		control.redraw();
	}

	/**
	 * Changes the control look and edit state of a text control.
	 *
	 * @param control the Text widget to be changed
	 * @param epState the EpState value
	 */
	public static void changeTextState(final Text control, final EpState epState) {
		if (epState == EpState.READ_ONLY) {
			control.setEditable(false);
			control.setBackground(CmClientResources.getColor(CmClientResources.COLOR_GREY));
		} else if (epState == EpState.EDITABLE) {
			control.setEnabled(true);
			control.setEditable(true);
			control.setBackground(CmClientResources.getBackgroundColor());
		} else if (epState == EpState.DISABLED) {
			control.setEnabled(false);
		}
		control.redraw();
	}

	/**
	 * Creates a spinner text field.
	 *
	 * @param parentComposite the parent SWT composite
	 * @param epState         the UI state of the widget
	 * @return Spinner
	 * @see Spinner
	 */
	public Spinner createSpinnerField(final Composite parentComposite, final EpState epState) {
		int style = SWT.BORDER;
		final Spinner spinner = new Spinner(parentComposite, style);
		if (isFormStyle()) {
			getFormToolkit().adapt(spinner, true, true);
		}
		changeEpState(spinner, epState);
		this.addModificationListener(spinner);
		setWidgetId(spinner);
		return spinner;
	}

	/**
	 * Creates a CheckboxTableViewer.
	 *
	 * @param parentComposite the parent SWT composite
	 * @param epState         the UI state of the widget
	 * @param showHeaders     if table headers should be shown.
	 * @param tableName       name of the table
	 * @return CheckboxTableViewer
	 * @see CheckboxTableViewer
	 */
	public CheckboxTableViewer createCheckboxTableViewer(final Composite parentComposite, final EpState epState, final boolean showHeaders,
		final String tableName) {
		CheckboxTableViewer viewer;
		Table table;
		final int style = SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.CHECK;
		if (isFormStyle()) {
			int borderStyle = getFormToolkit().getBorderStyle();
			if (!showHeaders) {
				getFormToolkit().setBorderStyle(SWT.NONE);
			}
			table = getFormToolkit().createTable(parentComposite, style);
			if (!showHeaders) {
				getFormToolkit().setBorderStyle(borderStyle);
			}
			viewer = new CheckboxTableViewer(table);
		} else {
			viewer = CheckboxTableViewer.newCheckList(parentComposite, style);
			table = viewer.getTable();
		}
		table.setLinesVisible(epState == EpState.EDITABLE);
		viewer.setUseHashlookup(false);
		table.setHeaderVisible(showHeaders);
		setWidgetId(table, tableName);
		addModificationListener(viewer);
		return viewer;
	}

	/**
	 * Creates a text hyperlink to this composite.
	 *
	 * @param parentComposite the parent SWT composite
	 * @param text            the text of the hyperlink or null if none
	 * @param epState         the UI state of the widget
	 * @return Hyperlink
	 * @see Hyperlink
	 * @see FormToolkit#createHyperlink(Composite, String, int)
	 */
	public Hyperlink createHyperLinkText(final Composite parentComposite, final String text, final EpState epState) {
		final Hyperlink hyperlinkText = getFormToolkit().createHyperlink(parentComposite, text, SWT.NONE);
		changeEpState(hyperlinkText, epState);
		setWidgetId(hyperlinkText, text);
		return hyperlinkText;
	}

	/**
	 * Creates an image hyperlink to this composite.
	 *
	 * @param parentComposite the parent SWT composite
	 * @param image           the image to be displayed or null if none
	 * @param epState         the UI state of the widget
	 * @return <code>Hyperlink</code>
	 * @see Hyperlink
	 * @see FormToolkit#createHyperlink(Composite, String, int)
	 */
	public ImageHyperlink createHyperLinkImage(final Composite parentComposite, final Image image, final EpState epState) {
		final ImageHyperlink hyperlinkImage = getFormToolkit().createImageHyperlink(parentComposite, SWT.NONE);
		hyperlinkImage.setImage(image);
		changeEpState(hyperlinkImage, epState);
		setWidgetId(hyperlinkImage);
		return hyperlinkImage;
	}

	private void addModificationListener(final Spinner spinner) {
		spinner.addModifyListener((ModifyListener) event -> EpControlFactory.this.notifyModificationListener());
	}


	/**
	 * Set a listener that will be notified of all modifications to controls on this composite.
	 *
	 * @param controlModificationListener an implementor of <code>ControlModificationListener</code>.
	 */
	public void setControlModificationListener(final ControlModificationListener controlModificationListener) {
		this.controlModificationListener = controlModificationListener;
	}

	/**
	 * Creates a modification listener to a Combo.
	 *
	 * @param combo the combo to listen to
	 */
	private void addModificationListener(final CCombo combo) {
		combo.addModifyListener(new ModifyListener() {

			/**
			 * Notifies the editor that the model has changed.
			 *
			 * @param modifyEvent the event
			 */
			public void modifyText(final ModifyEvent modifyEvent) {
				EpControlFactory.this.notifyModificationListener();
			}
		});
	}

	/**
	 * Creates a modification listener to a Button.
	 *
	 * @param button the button to listen to
	 */
	private void addModificationListener(final Button button) {
		button.addSelectionListener(new SelectionListener() {
			/**
			 * Not used.
			 *
			 * @param event not used
			 */
			public void widgetDefaultSelected(final SelectionEvent event) {
				// Do nothing
			}

			/**
			 * Called when the checkbox is clicked.
			 *
			 * @param event the selection event
			 */
			public void widgetSelected(final SelectionEvent event) {
				EpControlFactory.this.notifyModificationListener();
			}
		});
	}

	/**
	 * Creates a modification listener to a Text.
	 *
	 * @param text the text to listen to
	 */
	private void addModificationListener(final Text text) {
		text.addModifyListener(new ModifyListener() {

			/**
			 * Notifies the listener that the control has been modified.
			 *
			 * @param modifyEvent the event
			 */
			public void modifyText(final ModifyEvent modifyEvent) {
				EpControlFactory.this.notifyModificationListener();
			}
		});
	}

	private void notifyModificationListener() {
		if (controlModificationListener != null) {
			controlModificationListener.controlModified();
		}
	}

	/**
	 * Gets the Eclipse FormToolkit.
	 *
	 * @return the formToolkit the toolkit
	 */
	public FormToolkit getFormToolkit() {
		if (formToolkit == null) {
			formToolkit = createFormToolkit();
		}
		return formToolkit;
	}

	/**
	 * Creates a label.
	 *
	 * @param parentComposite the parent SWT composite
	 * @param labelText       text to be displayed
	 * @param style           style
	 * @param data layout data
	 * @return Label
	 */
	public Label createLabel(final Composite parentComposite, final String labelText, final int style, final Object data) {
		Composite labelCell = createLabelCell(parentComposite, style, data);
		Label label = getFormToolkit().createLabel(labelCell, labelText, style);
		setLabelData(label, data);

		cleanLabel(label);
		saveWidgetIdAssociatedToLabel(label, labelText);
		return label;
	}

	/**
	 * Creates a skinny label.
	 *
	 * @param parentComposite the parent SWT composite
	 * @param labelText       text to be displayed
	 * @param data            layout data
	 * @return Label
	 */
	public Label createSkinnyLabel(final Composite parentComposite, final String labelText, final Object data) {
		Label label = getFormToolkit().createLabel(parentComposite, labelText, SWT.WRAP);
		setLabelData(label, data);
		return label;
	}

	/**
	 * Creates a new bold Label with a ':' at the end.
	 *
	 * @param parentComposite the parent SWT composite
	 * @param labelText       the text to be displayed
	 * @param epState         the EP UI state flag (can be EDITABLE, READ_ONLY, DISABLED). If editable, then the text
	 *                        will be preceded by an asterisk.
	 * @param data layout data
	 * @return Label
	 * @see Label
	 */
	public Label createLabelBoldRequired(final Composite parentComposite, final String labelText, final EpState epState, final Object data) {
		Composite labelCell = createLabelCell(parentComposite, SWT.NONE, data);
		final Label label = new EpRequiredLabel(labelCell, SWT.BOLD);
		setLabelData(label, data);

		if (isFormStyle()) {
			getFormToolkit().adapt(label, false, false);
		}
		label.setText(labelText + ':');
		changeEpState(label, epState);

		cleanLabel(label);
		saveWidgetIdAssociatedToLabel(label, labelText);
		return label;
	}

	/**
	 * Sets the label data safely, considering its compatibility with the parents layout.
	 * Layout Data will be set to the wrapper if such exists and label will be centralized within the wrapper
	 */
	private void setLabelData(final Label label, final Object data) {
		label.setLayoutData(data);

		boolean hasWrapper = label.getParent().getData(WRAPPER) != null;

		if (hasWrapper) {
			label.setLayoutData(
				new GridData(SWT.BEGINNING, SWT.FILL, true, true)
			);
		}
	}


	/**
	 * Create an extra composite that will act as boundary for the Label.
	 * No wrapper will be create if label stands on its own line
	 */
	private Composite createLabelCell(final Composite parentComposite, final int style, final Object data) {
		//If label data will be different from parent's data then conversion problem will occur
		//Do not set wrapper if data is not GridData
		if (!(data instanceof GridData)) {
			return parentComposite;
		}

		if (needWrapper(parentComposite)) {
			Composite labelCell = new Composite(parentComposite, style);
			labelCell.setLayout(new GridLayout());
			labelCell.setLayoutData(data);
			labelCell.setData(WRAPPER, EMPTY_STRING);

			return labelCell;
		}

		//Wrapper is not needed, return the same composite, nothing was performed
		return parentComposite;
	}

	/**
	 * Recursive function.
	 * Searches for the FormLayout going to the parent and so on.
	 */
	private boolean needWrapper(final Composite parentComposite) {
		Object layout = parentComposite.getLayout();

		//Wrapper is needed (Label and Texts are positioned on the same line)
		if (layout instanceof FormLayout) {
			return true;
		}

		Composite parent = parentComposite.getParent();
		//Reached highest level of the composite hierarchy, or cannot go further --> didn't find form layout, hence do not need wrapper
		if (parentComposite instanceof CTabFolder || parentComposite instanceof Shell || parent == null) {
			return false;
		}

		return needWrapper(parent);
	}

	/**
	 * Creates a new bold Label with ':' at the end.
	 *
	 * @param parentComposite the parent SWT composite
	 * @param message         the text to be displayed
	 * @param style           SWT style
	 * @param data layout data
	 * @return Label
	 */
	public Label createLabelBold(final Composite parentComposite, final String message, final int style, final Object data) {
		final Label label = createLabel(parentComposite, message + ':', style, data);
		saveWidgetIdAssociatedToLabel(label, message);
		return label;
	}

	/**
	 * Creates Image to current composite.
	 *
	 * @param parentComposite the parent SWT composite
	 * @param image           the image to add
	 * @return Label
	 */
	public Label createImage(final Composite parentComposite, final Image image) {
		Label label;
		if (isFormStyle()) {
			label = getFormToolkit().createLabel(parentComposite, EMPTY_STRING, SWT.NONE); //$NON-NLS-1$
		} else {
			label = new Label(parentComposite, SWT.NONE);
		}

		label.setImage(image);
		showToolTipWithoutWidgetId(label);
		return label;
	}

	/**
	 * Creates an empty label component. Used for setting empty cell in a grid.
	 *
	 * @param parentComposite the parent SWT composite
	 * @return Label
	 */
	public Label createEmptyComponent(final Composite parentComposite) {
		Label label;
		if (isFormStyle()) {
			label = getFormToolkit().createLabel(parentComposite, EMPTY_STRING);
		} else {
			label = new Label(parentComposite, SWT.NONE);
		}
		label.setVisible(false);
		showToolTipWithoutWidgetId(label);
		return label;
	}

	/**
	 * Creates a combo box.
	 *
	 * @param parentComposite the parent SWT composite
	 * @param style           SWT style
	 * @param epState         the UI state of the widget
	 * @return CCombo
	 */
	public CCombo createComboBox(final Composite parentComposite, final int style, final EpState epState) {
		final CCombo combo = createComboBox(parentComposite, style);
		changeEpState(combo, epState);
		return combo;
	}

	/**
	 * Creates a combo box.
	 *
	 * @param parentComposite the parent SWT composite
	 * @param style           style
	 * @return CCombo
	 */
	public CCombo createComboBox(final Composite parentComposite, final int style) {
		final CCombo combo = new CCombo(parentComposite, style);
		if (isFormStyle()) {
			getFormToolkit().adapt(combo);
		}
		this.addModificationListener(combo);
		setWidgetId(combo);

		return combo;
	}

	/**
	 * Creates horizontal separator line.
	 *
	 * @param parentComposite the parent SWT composite
	 * @return <code>Label</code>
	 */
	public Label createHorizontalSeparator(final Composite parentComposite) {
		Label label;
		if (isFormStyle()) {
			label = getFormToolkit().createLabel(parentComposite, EMPTY_STRING, SWT.SEPARATOR | SWT.HORIZONTAL);
		} else {
			label = new Label(parentComposite, SWT.SEPARATOR | SWT.HORIZONTAL);
		}
		//TODO Maybe add ids to style separators?
		return label;
	}

	/**
	 * Creates a TableViewer wrapped in EP interface.
	 *
	 * @param parentComposite the parent SWT composite
	 * @param multiSelection  multi-row selection flag
	 * @param epState         the UI state of the widget
	 * @param tableName name of the table
	 * @return IEpTableViewer implementation
	 */
	public IEpTableViewer createTableViewer(final Composite parentComposite, final boolean multiSelection, final EpState epState,
		final String tableName) {
		final TableViewer viewer = createTableViewer0(parentComposite, multiSelection, false, epState, tableName);
		addModificationListener(viewer);

		return new EpTableViewer(viewer, epState == EpState.EDITABLE);
	}

	/**
	 * Creates a TableViewer wrapped in EP interface.
	 * Use only in accordance with the predefined EP styles.
	 *
	 * @param parentComposite the parent SWT composite
	 * @param style           the SWT style
	 * @param epState         the UI state of the widget
	 * @param tableName name of the table
	 * @return IEpTableViewer implementation
	 */
	public IEpTableViewer createTableViewer(final Composite parentComposite, final int style, final EpState epState, final String tableName) {
		final TableViewer viewer = createTableViewer1(parentComposite, false, epState, style, tableName);
		addModificationListener(viewer);

		return new EpTableViewer(viewer, epState == EpState.EDITABLE);
	}

	/**
	 * Creates a AssetTableViewer wrapped in EP interface.
	 *
	 * @param parentComposite the parent SWT composite
	 * @param multiSelection  multi-row selection flag
	 * @param epState         the UI state of the widget
	 * @return IEpTableViewer implementation
	 */
	public IEpTableViewer createTableViewerForAssetMgr(final Composite parentComposite, final boolean multiSelection, final EpState epState) {
		final AssetTableViewer viewer = createTableViewerForAssetMgr(parentComposite, multiSelection, false, epState);
		addModificationListener(viewer);

		return new EpTableViewer(viewer, epState == EpState.EDITABLE);
	}

	private void addModificationListener(final TableViewer viewer) {
		viewer.getColumnViewerEditor().addEditorActivationListener(new ColumnViewerEditorActivationListener() {
			@Override
			public void afterEditorActivated(final ColumnViewerEditorActivationEvent event) {
				notifyModificationListener(); // if an editor has been activated then probably there is a change
			}

			@Override
			public void afterEditorDeactivated(final ColumnViewerEditorDeactivationEvent event) {

				// empty
			}

			@Override
			public void beforeEditorActivated(final ColumnViewerEditorActivationEvent event) {
				// empty
			}

			@Override
			public void beforeEditorDeactivated(final ColumnViewerEditorDeactivationEvent event) {
				// empty
			}
		});
	}

	/**
	 * Creates a table representing a list component to the composite.
	 *
	 * @param parentComposite the parent SWT composite
	 * @param listLabel       the list label
	 * @param multiSelection  enables multi selection
	 * @param epState         the UI state of the widget
	 * @return ListViewer
	 */
	public IEpListViewer createListViewer(final Composite parentComposite, final String listLabel, final boolean multiSelection,
		final EpState epState) {
		return createListViewer(parentComposite, listLabel, multiSelection, epState, false);
	}

	/**
	 * Creates a table representing a list component to the composite.
	 *
	 * @param parentComposite the parent SWT composite
	 * @param listLabel       the list label
	 * @param multiSelection  enables multi selection
	 * @param epState         the UI state of the widget
	 * @param required        if true, marks the list viewer label with asterisk
	 * @return ListViewer
	 */
	public IEpListViewer createListViewer(final Composite parentComposite, final String listLabel, final boolean multiSelection,
		final EpState epState, final boolean required) {
		// creates additional composite in order to resize properly the only table column
		final Composite parent = createComposite(parentComposite);
		parent.setLayout(new GridLayout());
		createListViewerLabel(parent, listLabel, epState, required);
		final TableViewer tableViewer = createTableViewer0(parent, multiSelection, true, EpState.READ_ONLY, listLabel);
		tableViewer.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		return new EpListViewer(tableViewer, parent, epState == EpState.EDITABLE);
	}


	private void createListViewerLabel(final Composite parent, final String listLabel,
		final EpState epState, final boolean required) {
		if (required) {
			createLabelBoldRequired(parent, listLabel, epState, null);
		} else {
			createLabelBold(parent, listLabel, SWT.BOLD, null);
		}
	}

	/**
	 * Internal creation of the TableViewer.
	 *
	 * @param parent         the parent SWT composite
	 * @param multiSelection
	 * @param listMode
	 * @param epState
	 * @param tableName
	 * @return TableViewer INSTANCE
	 */
	private TableViewer createTableViewer0(final Composite parent, final boolean multiSelection, final boolean listMode, final EpState epState,
		final String tableName) {
		int style = SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION;
		if (multiSelection) {
			style |= SWT.MULTI;
		}
		return createTableViewer1(parent, listMode, epState, style, tableName);
	}

	/**
	 * @param parent
	 * @param listMode
	 * @param epState
	 * @param style
	 * @param tableName
	 * @return
	 */
	private TableViewer createTableViewer1(final Composite parent, final boolean listMode, final EpState epState, final int style,
		final String tableName) {
		TableViewer viewer;
		Table table;
		if (isFormStyle()) {

			table = getFormToolkit().createTable(parent, style);
			viewer = new TableViewer(table);
		} else {
			viewer = new TableViewer(parent, style);
			table = viewer.getTable();
		}
		table.setLinesVisible(epState == EpState.EDITABLE && !listMode);
		table.setHeaderVisible(!listMode);
		viewer.setUseHashlookup(false);

		changeEpState(table, epState);
		//Table names can be used as a ids, they are not translated
		setWidgetId(table, tableName);

		return viewer;
	}

	private AssetTableViewer createTableViewerForAssetMgr(final Composite parent, final boolean multiSelection, final boolean listMode,
		final EpState epState) {
		AssetTableViewer viewer;
		Table table;
		int style = SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION;
		if (multiSelection) {
			style |= SWT.MULTI;
		}
		if (isFormStyle()) {
			table = getFormToolkit().createTable(parent, style);
			viewer = new AssetTableViewer(table);
		} else {
			viewer = new AssetTableViewer(parent, style);

			table = viewer.getTable();
		}
		table.setLinesVisible(epState == EpState.EDITABLE && !listMode);
		table.setHeaderVisible(!listMode);
		viewer.setUseHashlookup(false);

		changeEpState(table, epState);

		return viewer;
	}

	/**
	 * Creates new tree viewer.
	 *
	 * @param parentComposite the parent composite
	 * @param multiSelection  enables multi-selection mode
	 * @param epState         the UI state of the widget
	 * @return IEpTreeViewer
	 */
	public IEpTreeViewer createTreeViewer(final Composite parentComposite, final boolean multiSelection, final EpState epState) {
		int style = SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION;
		if (multiSelection) {
			style |= SWT.MULTI;
		}
		Tree tree;
		TreeViewer treeViewer;
		if (isFormStyle()) {

			tree = getFormToolkit().createTree(parentComposite, style);
			treeViewer = new TreeViewer(tree);
		} else {
			treeViewer = new TreeViewer(parentComposite, style);
			tree = treeViewer.getTree();
		}

		tree.setHeaderVisible(true);
		tree.setLinesVisible(epState == EpState.EDITABLE);
		return new EpTreeViewer(treeViewer);
	}

	/**
	 * Creates a new date/time picker text field. For displaying it there are two possibilities.
	 * <p>
	 * 1) Pass the <code>IEpDateTimePicker</code> interface as a selection listener parameter to a SWT UI component using addSelectionListener().<br>
	 * 2) Call the open method when the date/time picker should be displayed
	 *
	 * @param parentComposite the parent SWT composite
	 * @param style           the style that should be a constant from the <code>IEpDateTimePicker</code> interface.
	 * @param epState         the UI state of the widget
	 * @return EP date/time picker
	 */
	public IEpDateTimePicker createDateTimeComponent(final Composite parentComposite, final int style, final EpState epState) {
		final Composite dateComposite = createComposite(parentComposite);
		final GridLayout gridLayout = new GridLayout(3, false);
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		gridLayout.horizontalSpacing = 2;
		gridLayout.verticalSpacing = 0;

		dateComposite.setLayout(gridLayout);

		final EpTextDateTimePicker dateTimePicker = new EpTextDateTimePicker(dateComposite, this, style, epState);
		dateTimePicker.getSwtText().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		final ImageHyperlink dateInvokerHyperlink = getFormToolkit().createImageHyperlink(dateComposite, SWT.NONE);
		dateInvokerHyperlink.setImage(CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_DATE_PICKER));
		dateInvokerHyperlink.setToolTipText(CoreMessages.get().AbstractEpLayoutComposite_DateSelectTooltip);
		dateInvokerHyperlink.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(final HyperlinkEvent event) {
				if (event.getSource() instanceof Control && dateTimePicker.getSwtText().getEditable()) {
					dateTimePicker.open((Control) event.getSource());
				}
			}
		});
		dateTimePicker.setInvokerControl(dateInvokerHyperlink);
		dateTimePicker.setEnabled(epState == EpState.EDITABLE);
		this.addModificationListener(dateTimePicker.getSwtText());
		//TODO Convert to localized
		setWidgetId(dateTimePicker.getSwtText());
		return dateTimePicker;
	}

	/**
	 * Create ImageHyperlink.
	 *
	 * @param parent            parent
	 * @param style             SWT style
	 * @param image             image
	 * @param text              text label
	 * @param tooltip           tooltip
	 * @param listener          listener
	 * @return ImageHyperlink
	 */
	public ImageHyperlink createImageHyperlink(final Composite parent, final int style,
		final Image image, final String text, final String tooltip,
		final IHyperlinkListener listener) {

		GridData gridData = new GridData(GridData.BEGINNING, GridData.CENTER, false, false);
		gridData.verticalIndent = 2;
		gridData.horizontalIndent = HORIZONTAL_INDENT;

		ImageHyperlink link = new ImageHyperlink(parent, style);
		link.setImage(image);
		if (text != null) {
			link.setText(text);
		}
		if (tooltip != null && !tooltip.equals(EMPTY_STRING)) {
			link.setToolTipText(tooltip);
		}
		link.setLayoutData(gridData);
		link.setBackground(parent.getBackground());
		if (listener != null) {
			link.addHyperlinkListener(listener);
		}
		//TODO Convert to localized
		setWidgetId(link, tooltip);
		return link;
	}

	/**
	 * Creates a Tool Item in the ToolBar.
	 *
	 * @param parent  toolbar
	 * @param index   index of new toolItem
	 * @param control control
	 * @param width   widths of toolItem
	 * @return ToolItem
	 */
	public ToolItem createToolItem(final ToolBar parent, final int index, final Control control, final int width) {
		ToolItem toolItem = new ToolItem(parent, SWT.SEPARATOR, index);
		toolItem.setControl(control);
		toolItem.setWidth(width);
		//TODO Convert to localized
		setWidgetId(toolItem, toolItem.getText());
		return toolItem;
	}

	/**
	 * @param parentComposite parent composite
	 * @return composite
	 */
	private Composite createComposite(final Composite parentComposite) {
		Composite newComposite;
		if (isFormStyle()) {
			newComposite = getFormToolkit().createComposite(parentComposite);
		} else {
			newComposite = new Composite(parentComposite, SWT.NONE);
		}
		setWidgetId(newComposite);
		return newComposite;
	}

	/**
	 * @param menu  parent menu
	 * @param style SWT style
	 * @param text  text
	 * @return menu item
	 */
	public MenuItem createMenuItem(final Menu menu, final int style, final String text) {
		MenuItem menuItem = new MenuItem(menu, style);
		menuItem.setText(text);
		setWidgetId(menuItem, text);
		return menuItem;
	}

	/**
	 * @param menu     parent menu
	 * @param style    SWT style
	 * @param text     text
	 * @param listener selection listener
	 * @return menu item
	 */
	public MenuItem createMenuItem(final Menu menu, final int style, final String text, final SelectionListener listener) {
		MenuItem menuItem = createMenuItem(menu, style, text);
		menuItem.addSelectionListener(listener);
		return menuItem;
	}

	private void cleanLabel(final Label label) {
		label.setForeground(null);
		label.setBackground(null);
		label.setFont(null);
	}

	/**
	 * Creates ImageHyperlink.
	 *
	 * @param toolkit      form toolkit which will create control
	 * @param parent       parent composite
	 * @param style        SWT style
	 * @param image        image
	 * @param text         link text
	 * @param mouseAdapter mouse listener
	 * @return image hyperlink
	 */
	public ImageHyperlink formToolkitCreateImageHyperlink(final FormToolkit toolkit, final Composite parent, final int style,
		final Image image, final String text, final MouseAdapter mouseAdapter) {

		ImageHyperlink link = toolkit.createImageHyperlink(parent, style);
		link.setImage(image);
		link.setText(text);
		link.addMouseListener(mouseAdapter);
		link.setUnderlined(false);

		EPTestUtilFactory.getInstance().getTestIdUtil().setId(link, text);

		return link;
	}

	/**
	 * Sets the form style enabled/disabled.
	 *
	 * @param isFormStyle boolean
	 */
	public void setFormStyle(final boolean isFormStyle) {
		formStyle = isFormStyle;
	}

	/**
	 * Gets the form-style value.
	 *
	 * @return true if form-style is enabled
	 */
	public boolean isFormStyle() {
		return formStyle;
	}

	/**
	 * Use this if you want use the text of the label created immediately before
	 * your widget as the widget id.
	 *
	 * @param widget the widget to set an id for.
	 */
	private void setWidgetId(final Widget widget) {
		setWidgetId(widget, lastLabelText);
		lastLabelText = EMPTY_STRING;
	}

	/**
	 * Sets the widgetId to the given value. If widget is a label, stores the text to use on the textbox
	 *  @param widget         the widget to set the id for.
	 * @param widgetId       the widget id.
	 */
	private void setWidgetId(final Widget widget, final String widgetId) {
		EPTestUtilFactory.getInstance().getTestIdUtil().setId(widget, widgetId);
	}

	private void saveWidgetIdAssociatedToLabel(final Label label, final String message) {
		showToolTipWithoutWidgetId(label);
		lastLabelText = message;
	}

	private void showToolTipWithoutWidgetId(final Label label) {
		TooltipService.showTooltip(label, EMPTY_STRING);
	}

	/**
	 * Create Managed Form with proper FormToolkit.
	 *
	 * @param parent composite that will be used to create scrolled form
	 * @return ManageForm
	 */
	public ManagedForm createManagedForm(final Composite parent) {
		FormToolkit toolkit = createFormToolkit();
		ScrolledForm scrolledForm = toolkit.createScrolledForm(parent);

		return new EpManagedForm(toolkit, scrolledForm);
	}

	/**
	 * This class is required for the toolkit to be disposed. No listener can be attached for the dispose event that is why have to extend it.
	 * Out goal is to provide our own FormColors for the toolkit.
	 * When we pass our own formToolkit it is expected that we will take care of it and dispose it.
	 */
	protected class EpManagedForm extends ManagedForm {
		/**
		 * Constructor that accepts custom toolkit.
		 *
		 * @param toolkit toolkit that creates controls
		 * @param form    scrolled form
		 */
		EpManagedForm(final FormToolkit toolkit, final ScrolledForm form) {
			super(toolkit, form);
		}

		@Override
		public void dispose() {
			//Dispose the toolkit, note: it is a safe disposal
			this.getToolkit().dispose();
			super.dispose();
		}
	}

	/**
	 * Create a toolkit with EpFormColors.
	 *
	 * @return form toolkit
	 */
	public FormToolkit createFormToolkit() {
		return new EpFormToolkit();
	}
}
