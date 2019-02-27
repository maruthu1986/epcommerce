/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.policy.ui;

import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ImageHyperlink;

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
import com.elasticpath.cmclient.policy.StateChangeTarget;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;

/**
 * A layout composite wrapper that takes a policy target container.
 */
@SuppressWarnings({ "PMD.TooManyMethods", "PMD.GodClass" })
public class PolicyTargetLayoutComposite implements IPolicyTargetLayoutComposite {
	
	private final IEpLayoutComposite composite;
	
	
	/**
	 * Wrap the given composite.
	 * 
	 * @param composite the composite to wrap.
	 */
	public PolicyTargetLayoutComposite(final IEpLayoutComposite composite) {
		super();
		this.composite = composite;
	}

	/**
	 * Add the given control to the given container.
	 * 
	 * @param control the control to add
	 * @param container the container for the control
	 */
	protected void addControlToContainer(final Control control, final PolicyActionContainer container) {
		final StateChangeTarget target = new ControlStateChangeTargetImpl(control);
		container.addTarget(target);
		control.addDisposeListener(new DisposeListener() {
			/**
			 * Ensure the control is removed from the container when it is disposed.
			 * @param event the disposed event 
			 */
			@Override
			public void widgetDisposed(final DisposeEvent event) {
				container.removeTarget(target);				
			}
		});
	}
	
	@Override
	public Button addCheckBoxButton(final String checkBoxLabel, final IEpLayoutData data, final PolicyActionContainer container) {
		Button button = composite.addCheckBoxButton(checkBoxLabel, EpState.READ_ONLY, data);
		addControlToContainer(button, container);
		return button;
	}

	@Override
	public IEpTableViewer addCheckboxTableViewer(final boolean showHeaders, final IEpLayoutData data, final PolicyActionContainer container,
		final String tableName) {
		IEpTableViewer tableViewer = composite.addCheckboxTableViewer(showHeaders, EpState.READ_ONLY, data, tableName);
		addControlToContainer(tableViewer.getSwtTable(), container);
		return tableViewer;
	}

	@Override
	public CheckboxTableViewer addCheckboxTableViewer(final IEpLayoutData data, final boolean showHeaders, final PolicyActionContainer container,
		final String tableName) {
		CheckboxTableViewer checkBoxTableViewer = composite.addCheckboxTableViewer(EpState.READ_ONLY, data, showHeaders, tableName);
		addControlToContainer(checkBoxTableViewer.getTable(), container);
		return checkBoxTableViewer; 
		
	}

	@Override
	public CCombo addComboBox(final IEpLayoutData data, final PolicyActionContainer container) {
		CCombo combo = composite.addComboBox(EpState.READ_ONLY, data);
		addControlToContainer(combo, container);
		return combo;
	}
	
	@Override
	public ComboViewer addComboViewer(final IEpLayoutData data, final PolicyActionContainer container) {
		CCombo comboBox = addComboBox(data, container);
		return new ComboViewer(comboBox);
	}

	@Override
	public IEpDateTimePicker addDateTimeComponent(final int style, final IEpLayoutData data, final PolicyActionContainer container) {
		IEpDateTimePicker dateTimePicker = composite.addDateTimeComponent(style, EpState.READ_ONLY, data);
		addControlToContainer(dateTimePicker.getSwtText(), container);
		return dateTimePicker;
	}

	@Override
	public void addEmptyComponent(final IEpLayoutData data, final PolicyActionContainer container) {
		composite.addEmptyComponent(data);
	}

	@Override
	public IPolicyTargetLayoutComposite addExpandableComposite(final int numColumns, final boolean equalWidthColumns, final String title, 
			final IEpLayoutData data, final PolicyActionContainer container) {
		IEpLayoutComposite newComposite = composite.addExpandableComposite(numColumns, equalWidthColumns, title, data);
		return PolicyTargetCompositeFactory.wrapLayoutComposite(newComposite);
	}

	@Override
	public IPolicyTargetLayoutComposite addGridLayoutComposite(final int numColumns, final boolean equalWidthColumns, final IEpLayoutData data,
					final PolicyActionContainer container) {
		IEpLayoutComposite newComposite = composite.addGridLayoutComposite(numColumns, equalWidthColumns, data);
		return PolicyTargetCompositeFactory.wrapLayoutComposite(newComposite);
	}

	@Override
	public IPolicyTargetLayoutComposite addScrolledGridLayoutComposite(final int numColumns, final boolean equalWidthColumns,
					final IEpLayoutData data, final PolicyActionContainer container) {
		IEpLayoutComposite newComposite = composite.addScrolledGridLayoutComposite(numColumns, equalWidthColumns, data);
		return PolicyTargetCompositeFactory.wrapLayoutComposite(newComposite);
	}

	@Override
	public IPolicyTargetLayoutComposite addGridLayoutSection(final int numColumns, final String title, final int style, final IEpLayoutData data, 
			final PolicyActionContainer container) {
		IEpLayoutComposite newComposite = composite.addGridLayoutSection(numColumns, title, style, data);
		return PolicyTargetCompositeFactory.wrapLayoutComposite(newComposite);
	}

	@Override
	public IPolicyTargetLayoutComposite addGridLayoutSection(final int numColumns, final String title, final String description, final int style, 
			final IEpLayoutData data, final PolicyActionContainer container) {
		IEpLayoutComposite newComposite = composite.addGridLayoutSection(numColumns, title, description, style, data);
		return PolicyTargetCompositeFactory.wrapLayoutComposite(newComposite);
	}

	@Override
	public IPolicyTargetLayoutComposite addGroup(final String groupLabel, final int numColumns, final boolean equalWidthColumns, 
			final IEpLayoutData data, final PolicyActionContainer container) {
		IEpLayoutComposite newComposite = composite.addGroup(groupLabel, numColumns, equalWidthColumns, data);
		return PolicyTargetCompositeFactory.wrapLayoutComposite(newComposite);
	}

	@Override
	public Label addHorizontalSeparator(final IEpLayoutData data, final PolicyActionContainer container) {
		Label label = composite.addHorizontalSeparator(data);
		addControlToContainer(label, container);
		return label;
	}

	@Override
	public ImageHyperlink addHyperLinkImage(final Image image, final IEpLayoutData data, final PolicyActionContainer container) {
		ImageHyperlink control = composite.addHyperLinkImage(image, EpState.READ_ONLY, data);
		addControlToContainer(control, container);
		return control;
	}

	@Override
	public Hyperlink addHyperLinkText(final String text, final IEpLayoutData data, final PolicyActionContainer container) {
		Hyperlink control = composite.addHyperLinkText(text, EpState.READ_ONLY, data);
		addControlToContainer(control, container);
		return control;
	}

	@Override
	public Label addImage(final Image image, final IEpLayoutData data, final PolicyActionContainer container) {
		Label control = composite.addImage(image, data);
		addControlToContainer(control, container);
		return control;
	}

	@Override
	public Label addLabel(final String labelText, final IEpLayoutData data, final PolicyActionContainer container) {
		Label control = composite.addLabel(labelText, data);
		addControlToContainer(control, container);
		return control;
	}

	@Override
	public Label addLabelBold(final String labelText, final IEpLayoutData data, final PolicyActionContainer container) {
		Label control = composite.addLabelBold(labelText, data);
		addControlToContainer(control, container);
		return control;
	}

	@Override
	public Label addLabelBoldRequired(final String labelText, final IEpLayoutData data, final PolicyActionContainer container) {
		Label control = composite.addLabelBoldRequired(labelText, EpState.READ_ONLY, data);
		addControlToContainer(control, container);
		return control;
	}

	@Override
	public List addList(final IEpLayoutData data, final PolicyActionContainer container) {
		List control = composite.addList(EpState.READ_ONLY, data);
		addControlToContainer(control, container);
		return control;
	}

	@Override
	public IEpListViewer addListViewer(final String listLabel, final boolean multiSelection, final IEpLayoutData data, 
			final PolicyActionContainer container) {
		IEpListViewer listViewer = composite.addListViewer(listLabel, multiSelection, EpState.READ_ONLY, data);
		addControlToContainer(listViewer.getSwtTable(), container);
		return listViewer;
	}

	@Override
	public Text addPasswordField(final IEpLayoutData data, final PolicyActionContainer container) {
		Text control = composite.addPasswordField(EpState.READ_ONLY, data);
		addControlToContainer(control, container);
		return control;
	}

	@Override
	public Button addPushButton(final String buttonLabel, final IEpLayoutData data, final PolicyActionContainer container) {
		Button control = composite.addPushButton(buttonLabel, EpState.READ_ONLY, data);
		addControlToContainer(control, container);
		return control;
	}

	@Override
	public Button addPushButton(final String buttonLabel, final Image image, final IEpLayoutData data, final PolicyActionContainer container) {
		Button control = composite.addPushButton(buttonLabel, image, EpState.READ_ONLY, data);
		addControlToContainer(control, container);
		return control;
	}

	@Override
	public Button addRadioButton(final String radioLabel, final IEpLayoutData data, final PolicyActionContainer container) {
		Button control = composite.addRadioButton(radioLabel, EpState.READ_ONLY, data);
		addControlToContainer(control, container);
		return control;
	}

	@Override
	public Button addRadioButton(final String radioLabel, final Image image, final IEpLayoutData data, final PolicyActionContainer container) {
		Button control = composite.addRadioButton(radioLabel, image, EpState.READ_ONLY, data);
		addControlToContainer(control, container);
		return control;
	}

	@Override
	public Spinner addSpinnerField(final IEpLayoutData data, final PolicyActionContainer container) {
		Spinner control = composite.addSpinnerField(EpState.READ_ONLY, data);
		addControlToContainer(control, container);
		return control;
	}

	@Override
	public IEpTabFolder addTabFolder(final IEpLayoutData data, final PolicyActionContainer container) {
		IEpTabFolder tabFolder = composite.addTabFolder(data);
		addControlToContainer(tabFolder.getSwtTabFolder(), container);
		return tabFolder;
	}

	@Override
	public IEpTableViewer addTableViewer(final boolean multiSelection, final IEpLayoutData data, final PolicyActionContainer container,
		final String tableName) {
		IEpTableViewer tableViewer = composite.addTableViewer(multiSelection, EpState.READ_ONLY, data, tableName);
		addControlToContainer(tableViewer.getSwtTable(), container);
		return tableViewer;
	}

	@Override
	public IEpTableViewer addTableViewer(final int style, final IEpLayoutData data, final PolicyActionContainer container, final String tableName) {
		IEpTableViewer tableViewer = composite.addTableViewer(style, EpState.READ_ONLY, data, tableName);
		addControlToContainer(tableViewer.getSwtTable(), container);
		return tableViewer;
	}

	@Override
	public IPolicyTargetLayoutComposite addTableWrapLayoutComposite(final int numColumns, final boolean equalWidthColumns, final IEpLayoutData data,
			final PolicyActionContainer container) {
		IEpLayoutComposite newComposite = composite.addTableWrapLayoutComposite(numColumns, equalWidthColumns, data);
		return PolicyTargetCompositeFactory.wrapLayoutComposite(newComposite);
	}

	@Override
	public IPolicyTargetLayoutComposite addTableWrapLayoutSection(final int numColumns, final String title, final int style, 
			final IEpLayoutData data, final PolicyActionContainer container) {
		IEpLayoutComposite newComposite = composite.addTableWrapLayoutSection(numColumns, title, style, data);
		return PolicyTargetCompositeFactory.wrapLayoutComposite(newComposite);
	}

	@Override
	public IPolicyTargetLayoutComposite addTableWrapLayoutSection(final int numColumns, final String title, final String description, 
			final int style, final IEpLayoutData data, final PolicyActionContainer container) {
		IEpLayoutComposite newComposite = composite.addTableWrapLayoutSection(numColumns, title, description, style, data);
		return PolicyTargetCompositeFactory.wrapLayoutComposite(newComposite);
	}

	@Override
	public Text addTextArea(final IEpLayoutData data, final PolicyActionContainer container) {
		Text control = composite.addTextArea(EpState.READ_ONLY, data);
		addControlToContainer(control, container);
		return control;
	}

	@Override
	public Text addTextArea(final boolean hasVerticalScroll, final boolean hasHorizontalScroll, final IEpLayoutData data, 
			final PolicyActionContainer container) {
		Text control = composite.addTextArea(hasVerticalScroll, hasHorizontalScroll, EpState.READ_ONLY, data);
		addControlToContainer(control, container);
		return control;
	}

	@Override
	public Text addTextField(final IEpLayoutData data, final PolicyActionContainer container) {
		Text control = composite.addTextField(EpState.READ_ONLY, data);
		addControlToContainer(control, container);
		return control;
	}

	@Override
	public IEpTreeViewer addTreeViewer(final boolean multiSelection, final IEpLayoutData data, final PolicyActionContainer container) {
		IEpTreeViewer treeViewer = composite.addTreeViewer(multiSelection, EpState.READ_ONLY, data);
		addControlToContainer(treeViewer.getSwtTree(), container);
		return treeViewer;
	}

	@Override
	public IEpLayoutData createLayoutData(final int horizontalAlignment, final int verticalAlignment, final boolean grabExcessHorizontalSpace,
			final boolean grabExcessVerticalSpace, final int horizontalSpan, final int verticalSpan) {
		return composite.createLayoutData(horizontalAlignment, verticalAlignment, grabExcessHorizontalSpace, grabExcessVerticalSpace, 
				horizontalSpan, verticalSpan);
	}

	@Override
	public IEpLayoutData createLayoutData(final int horizontalAlignment, final int verticalAlignment, final boolean grabExcessHSpace, 
			final boolean grabExcessVSpace) {
		return composite.createLayoutData(horizontalAlignment, verticalAlignment, grabExcessHSpace, grabExcessVSpace);
	}

	@Override
	public IEpLayoutData createLayoutData(final int horizontalAlignment, final int verticalAlignment) {
		return composite.createLayoutData(horizontalAlignment, verticalAlignment);
	}

	@Override
	public IEpLayoutData createLayoutData() {
		return composite.createLayoutData();
	}

	@Override
	public EpControlFactory getEpControlFactory() {
		return composite.getEpControlFactory();
	}

	@Override
	public FormToolkit getFormToolkit() {
		return composite.getFormToolkit();
	}

	@Override
	public Composite getSwtComposite() {
		return composite.getSwtComposite();
	}

	@Override
	public boolean isFormStyle() {
		return composite.isFormStyle();
	}

	@Override
	public void setControlModificationListener(final ControlModificationListener controlModificationListener) {
		composite.setControlModificationListener(controlModificationListener);
	}

	@Override
	public void setFormStyle(final boolean isFormStyle) {
		composite.setFormStyle(isFormStyle);
	}

	@Override
	public void setLayoutData(final Object data) {
		composite.setLayoutData(data);
	}

	@Override
	public IEpLayoutComposite getLayoutComposite() {
		return composite;
	}

	@Override
	public Scale addScale(final int minimum, final int maximum, final int increment, 
			final int pageIncrement, final int style, final IEpLayoutData data, final PolicyActionContainer container) {
		Scale scale = composite.addScale(minimum, maximum, increment, pageIncrement, style, EpState.READ_ONLY, data);
		addControlToContainer(scale, container);
		return scale;
	}

}
