/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.catalog.policy;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.elasticpath.cmclient.catalog.CatalogImageRegistry;
import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.catalog.editors.attribute.AttributeEditingSupport;
import com.elasticpath.cmclient.catalog.editors.attribute.AttributesLabelProviderUtil;
import com.elasticpath.cmclient.catalog.editors.attribute.IAttributeChangedListener;
import com.elasticpath.cmclient.catalog.editors.attribute.ICellEditorDialogService;
import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.dto.catalog.ProductModel;
import com.elasticpath.cmclient.core.dto.catalog.ProductSkuModel;
import com.elasticpath.cmclient.core.helpers.IValueRetriever;
import com.elasticpath.cmclient.core.ui.framework.ControlModificationListener;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTableColumn;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.policy.StatePolicy;
import com.elasticpath.cmclient.policy.common.DefaultStatePolicyDelegateImpl;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.IPolicyTargetLayoutComposite;
import com.elasticpath.cmclient.policy.ui.PolicyTargetCompositeFactory;
import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.catalog.Category;

/**
 * State policy aware Attributes UI controls abstraction.
 */
@SuppressWarnings({ "PMD.GodClass" })
public class PolicyAwareAttributesViewPart extends DefaultStatePolicyDelegateImpl
	implements SelectionListener, ISelectionChangedListener, IAttributeChangedListener {
	
	private static final int TABLE_HEIGHT = 10;

	private static final String ATTRIBUTES_TABLE = "Attributes"; //$NON-NLS-1$
	/**
	 * the table viewer for the attribute info table.
	 */
	private IEpTableViewer attributesTableViewer;

	/**
	 * the edit button to invoke the attribute value editing.
	 */
	private Button editButton;

	/**
	 * the main composite for attribute section on attribute page.
	 */
	private IPolicyTargetLayoutComposite mainComposite;

	private Button resetButton;

	private final Object model;

	private final IToolBarManager toolbarManager;

	private Label attributeImageLabel;

	private Image attributeImage;

	private IPolicyTargetLayoutComposite buttonsComposite;

	private ControlModificationListener controlModificationListener;

	private final PolicyActionContainer tableContainer;

	private final PolicyActionContainer buttonContainer;

	private final Map<String, IEpTableColumn> columns = new HashMap<>();

	private final ICellEditorDialogService dialogService;

	private StatePolicy statePolicy;

	/**
	 * Constructs the object.
	 *
	 * @param model
	 *            the object model
	 * @param toolbarManager
	 *            the toolbar manager
	 * @param buttonPolicyActionContainer
	 *            the policy action container to use for the buttons
	 * @param tableActionContainer
	 *            the policy action container to use to control in-line table editing
	 */
	public PolicyAwareAttributesViewPart(final Object model, final IToolBarManager toolbarManager, 
			final PolicyActionContainer buttonPolicyActionContainer,
			final PolicyActionContainer tableActionContainer) {
		this.model = model;
		this.toolbarManager = toolbarManager;
		this.buttonContainer = buttonPolicyActionContainer;
		this.tableContainer = tableActionContainer;
		getPolicyActionContainers().put(buttonContainer.getName(), buttonContainer);
		getPolicyActionContainers().put(tableContainer.getName(), tableContainer);
		dialogService = new DialogService();
	}

	/**
	 * Creates UI controls.
	 *
	 * @param parentComposite
	 *            the main composite
	 */
	public void createControls(final IEpLayoutComposite parentComposite) {
		this.mainComposite = PolicyTargetCompositeFactory.wrapLayoutComposite(parentComposite);

		// layout for the table area
		final IEpLayoutData tableLayoutData = mainComposite.createLayoutData(
				IEpLayoutData.FILL, IEpLayoutData.FILL, true, true);
		this.attributesTableViewer = this.mainComposite.getLayoutComposite().addTableViewer(false, EpState.EDITABLE, tableLayoutData,
			ATTRIBUTES_TABLE);
		((GridData) this.attributesTableViewer.getSwtTable().getLayoutData()).heightHint = TABLE_HEIGHT;

		createTableColumns();

		buttonsComposite = mainComposite.addGridLayoutComposite(1, false, tableLayoutData, tableContainer);

		// create edit button for invoking the editing
		final Image editImage = CoreImageRegistry
				.getImage(CoreImageRegistry.IMAGE_EDIT);
		editButton = buttonsComposite.addPushButton(
				CatalogMessages.get().AttributePage_ButtonEdit, editImage,
				mainComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.BEGINNING), buttonContainer);
		editButton.addSelectionListener(this);

		resetButton = buttonsComposite.addPushButton(
				CatalogMessages.get().AttributePage_ButtonReset, CoreImageRegistry
						.getImage(CoreImageRegistry.IMAGE_X), mainComposite.createLayoutData(IEpLayoutData.FILL,
						IEpLayoutData.BEGINNING), buttonContainer);
		resetButton.addSelectionListener(this);

		editButton.setEnabled(false);
		resetButton.setEnabled(false);

		this.attributeImageLabel = buttonsComposite.addImage(
				CatalogImageRegistry.IMAGE_NOT_AVAILABLE.createImage(), null, buttonContainer);
		attributeImageLabel.setVisible(false);

		this.attributesTableViewer
				.setContentProvider(new ArrayContentProvider());

		if (toolbarManager != null) {
			this.createActions(toolbarManager);
		}

		attributesTableViewer.getSwtTableViewer().addSelectionChangedListener(
				this);
	}

	/**
	 * Create the table columns.
	 */
	protected void createTableColumns() {
		// attributesTableViewer.getSwtTable().set
		// the name column content of the attribute table
		final IEpTableColumn nameColumn = this.attributesTableViewer
				.addTableColumn(
						CatalogMessages.get().ProductEditorAttributeSection_TableColumnTitle_Name,
						200);
		// The attribute type column content of the attribute table
		final IEpTableColumn typeColumn = this.attributesTableViewer
				.addTableColumn(
						CatalogMessages.get().ProductEditorAttributeSection_TableColumnTitle_Type,
						120);
		// The required column content of the attribute table
		final IEpTableColumn requiredColumn = this.attributesTableViewer
				.addTableColumn(
						CatalogMessages.get().ProductEditorAttributeSection_TableColumnTitle_Required,
						60);
		// The multi-language column content of the attribute table
		final IEpTableColumn multiLanguageColumn = this.attributesTableViewer
				.addTableColumn(
						CatalogMessages.get().ProductEditorAttributeSection_TableColumnTitle_MLang,
						90);

		// The attribute value column content of the attribute table
		final IEpTableColumn valueColumn = this.attributesTableViewer
				.addTableColumn(
						CatalogMessages.get().ProductEditorAttributeSection_TableColumnTitle_Value,
						300);

		columns.put("name", nameColumn); //$NON-NLS-1$
		columns.put("type", typeColumn); //$NON-NLS-1$
		columns.put("required", requiredColumn); //$NON-NLS-1$
		columns.put("multilanguage", multiLanguageColumn); //$NON-NLS-1$
		columns.put("value", valueColumn); //$NON-NLS-1$

		updateColumns(EpState.READ_ONLY);
	}

	/**
	 * Update the columns to ensure the state is displayed correctly.
	 *
	 * @param state the state that needs to be set.
	 */
	protected void updateColumns(final EpState state) {
		if (attributesTableViewer.getSwtTable().isDisposed()) {
			return;
		}
		final AttributesLabelProviderUtil labelProviderUtil = new AttributesLabelProviderUtil(state);

		labelProviderUtil.setNameColumnLabel(columns.get("name")); //$NON-NLS-1$
		labelProviderUtil.setTypeColumnLabel(columns.get("type")); //$NON-NLS-1$
		labelProviderUtil.setRequiredColumnLabel(columns.get("required")); //$NON-NLS-1$
		labelProviderUtil.setMultiLanguageColumnLabel(columns.get("multilanguage")); //$NON-NLS-1$

		IEpTableColumn valueColumn = columns.get("value"); //$NON-NLS-1$
		labelProviderUtil.setValueColumnLabel(valueColumn);

		this.attributesTableViewer.setEnableEditMode(state == EpState.EDITABLE);

		// add EditSupport to the attribute value column
		if (state == EpState.EDITABLE) {
			AttributeEditingSupport editorSupport = new AttributeEditingSupport(this.attributesTableViewer, this.getModel(), dialogService);
			editorSupport.addAttributeChangedListener(this);
			valueColumn.setEditingSupport(editorSupport);
		}

		attributesTableViewer.getSwtTableViewer().refresh();
	}

	/**
	 * Sets the control modification listener for this UI part.
	 *
	 * @param controlModificationListener
	 *            listener implementor
	 */
	public void setControlModificationListener(
			final ControlModificationListener controlModificationListener) {
		this.controlModificationListener = controlModificationListener;
	}

	/**
	 * Create action on the attribute page.
	 *
	 * @param toolBarManager
	 *            The tool bar manager passed in.
	 */
	protected void createActions(final IToolBarManager toolBarManager) {
		toolBarManager.add(new Separator());
		toolBarManager.update(true);
	}

	private Object getModel() {
		return model;
	}

	@Override
	public void widgetDefaultSelected(final SelectionEvent event) {
		// do nothing here
	}

	@Override
	public void widgetSelected(final SelectionEvent event) {
		if (event.getSource() == editButton) {
			dialogService.createEditorDialog();
		}
		if (event.getSource() == resetButton) {
			final IStructuredSelection selection = (IStructuredSelection) attributesTableViewer.getSwtTableViewer().getSelection();
			final AttributeValue attr = (AttributeValue) selection.getFirstElement();
			clearAttributeValue(attr);
			attr.setValue(null);
			attributesTableViewer.getSwtTableViewer().refresh();
			fireModified();
		}
	}

	/**
	 * Dialog Service opens a Dialog to edit the value of the specific cell.
	 */
	protected class DialogService implements ICellEditorDialogService {
		@Override
		public void createEditorDialog() {
			final IStructuredSelection selection = (IStructuredSelection) attributesTableViewer.getSwtTableViewer().getSelection();
			final AttributeValue attr = (AttributeValue) selection.getFirstElement();
			final Shell shell = mainComposite.getSwtComposite().getShell();
			Window dialog = AttributeEditingSupport.getEditorDialog(attr, shell);
			final int result = dialog.open();

			if (result == Window.OK) {
				final IValueRetriever retriever = (IValueRetriever) dialog;
				attr.setValue(retriever.getValue());
				addAttributeValueToModel(attr);
				attributesTableViewer.getSwtTableViewer().refresh();
				fireModified();
			}
		}

		/**
		 * Adds the given AttributeValue to the model's AttributeValueMap.
		 *
		 * @param attr the AttributeValue to add
		 */
		private void addAttributeValueToModel(final AttributeValue attr) {
			if (getModel() instanceof ProductModel) {
				((ProductModel) getModel()).getProduct().getAttributeValueMap().put(attr.getLocalizedAttributeKey(), attr);
			} else if (getModel() instanceof Category) {
				((Category) getModel()).getAttributeValueMap().put(attr.getLocalizedAttributeKey(), attr);
			} else if (getModel() instanceof ProductSkuModel) {
				((ProductSkuModel) getModel()).getProductSku().getAttributeValueMap().put(attr.getLocalizedAttributeKey(), attr);
			}
		}
	}

	/**
	 * Fire an event to notify the listeners that the control has been modified.
	 */
	private void fireModified() {
		//The control modification listener is null if the attributes view part
		//is created in a wizard, but if it's created in a editor it will be non-null
		if (controlModificationListener != null) {
			controlModificationListener.controlModified();
		}
	}

	private void clearAttributeValue(final AttributeValue attr) {
		Map<String, AttributeValue> attributeValueMap = null;
		if (getModel() instanceof ProductModel) {
			attributeValueMap = ((ProductModel) getModel()).getProduct().getAttributeValueMap();
		}
		if (getModel() instanceof Category) {
			attributeValueMap = ((Category) getModel()).getAttributeValueMap();
		}
		if (getModel() instanceof ProductSkuModel) {
			attributeValueMap = ((ProductSkuModel) getModel()).getProductSku().getAttributeValueMap();
		}
		if (attributeValueMap == null) {
			return;
		}
		Object attributeValueKey = null;
		for (Object key : attributeValueMap.keySet().toArray()) {
			if (attr.getUidPk() == attributeValueMap.get(key).getUidPk()) {
				attributeValueKey = key;
				break;
			}
		}
		attributeValueMap.remove(attributeValueKey);
	}

	/**
	 * Enable the edit buttons while the selection changed.
	 *
	 * @param event
	 *            selection changed event.
	 */
	@Override
	public void selectionChanged(final SelectionChangedEvent event) {
		final IStructuredSelection selection = (IStructuredSelection) attributesTableViewer
				.getSwtTableViewer().getSelection();

		setButtonState(!selection.isEmpty());
	}

	/**
	 * Sets the input of the table.
	 *
	 * @param input
	 *            the input object
	 */
	public void setInput(final AttributeValue[] input) {
		attributesTableViewer.setInput(input);
	}

	@Override
	public void attributeValueChanged(final AttributeValue attributeValue) {
		if (controlModificationListener != null) {
			controlModificationListener.controlModified();
		}
	}

	/**
	 * Dispose the system resource. The client should call this after using this
	 * view part.
	 */
	public void dispose() {
		if (attributeImage != null && !attributeImage.isDisposed()) {
			attributeImage.dispose();
		}
	}

	/**
	 *
	 * @return true if table input is set
	 */
	public boolean isInitialized() {
		return attributesTableViewer.getSwtTableViewer().getInput() != null;
	}

	@Override
	public void applyStatePolicy(final StatePolicy policy) {
		this.statePolicy = policy;
		super.applyStatePolicy(policy);
		updateColumns(policy.determineState(tableContainer));
		setButtonState(!attributesTableViewer.getSwtTableViewer().getSelection().isEmpty());
	}

	@Override
	public void refreshLayout() {
		if (!mainComposite.getSwtComposite().isDisposed()) {
			mainComposite.getSwtComposite().getParent().layout();
		}
	}

	/**
	 * Set the button state dependent on whether a row is selected or not.
	 *
	 * @param rowSelected true if a row is selected.
	 */
	protected void setButtonState(final boolean rowSelected) {
		// if state policy is null the button should be considered editable
		// otherwise check the state policy state
		boolean isEditable = statePolicy == null
						|| statePolicy.determineState(buttonContainer) == EpState.EDITABLE;
		final boolean buttonsEnabled = (isEditable && rowSelected);
		updateButtonState(editButton, buttonsEnabled);
		updateButtonState(resetButton, buttonsEnabled);
	}
	
	private void updateButtonState(final Button button, final boolean enabled) {
		if (!button.isDisposed()) {
			button.setEnabled(enabled);
		}
	}

}
