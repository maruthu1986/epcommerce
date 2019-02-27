/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.editors.attribute;

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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;

import com.elasticpath.cmclient.catalog.CatalogMessages;
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
import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.catalog.Category;

/**
 * Attributes UI controls abstraction.
 */
public class AttributesViewPart implements SelectionListener,
		ISelectionChangedListener, IAttributeChangedListener {

	private static final String ATTRIBUTES_VIEW_TABLE = "Attributes View"; //$NON-NLS-1$
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
	private IEpLayoutComposite mainComposite;

	private Button resetButton;

	private final EpState rolePermission;

	private final Object model;

	private final IToolBarManager toolbarManager;

	private final ICellEditorDialogService dialogService;

	private IEpLayoutComposite buttonsComposite;

	private ControlModificationListener controlModificationListener;

	/**
	 * Constructs the object.
	 *
	 * @param model
	 *            the object model
	 * @param rolePermission
	 *            the role permissions
	 * @param toolbarManager
	 *            the toolbar manager
	 */
	public AttributesViewPart(final Object model, final EpState rolePermission,
			final IToolBarManager toolbarManager) {
		this.rolePermission = rolePermission;
		this.model = model;
		this.toolbarManager = toolbarManager;
		this.dialogService = new DialogService();
	}

	/**
	 * Creates UI controls.
	 *
	 * @param mainComposite
	 *            the main composite
	 */
	public void createControls(final IEpLayoutComposite mainComposite) {
		this.mainComposite = mainComposite;
		// layout for the table area
		final IEpLayoutData tableLayoutData = mainComposite.createLayoutData(
				IEpLayoutData.FILL, IEpLayoutData.FILL, true, true);
		this.attributesTableViewer = this.mainComposite.addTableViewer(false,
			rolePermission, tableLayoutData, ATTRIBUTES_VIEW_TABLE);

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
						200);

		final AttributesLabelProviderUtil labelProviderUtil = new AttributesLabelProviderUtil(
				rolePermission);
		labelProviderUtil.setNameColumnLabel(nameColumn);
		labelProviderUtil.setTypeColumnLabel(typeColumn);
		labelProviderUtil.setRequiredColumnLabel(requiredColumn);
		labelProviderUtil.setMultiLanguageColumnLabel(multiLanguageColumn);
		labelProviderUtil.setValueColumnLabel(valueColumn);

		if (rolePermission == EpState.EDITABLE) {
			// add EditSupport to the attribute value column
			AttributeEditingSupport editorSupport = new AttributeEditingSupport(this.attributesTableViewer, this.getModel(), dialogService);
			editorSupport.addAttributeChangedListener(this);
			valueColumn.setEditingSupport(editorSupport);

		}

		buttonsComposite = mainComposite.addGridLayoutComposite(1, false, null);

		// create edit button for invoking the editing
		final Image editImage = CoreImageRegistry
				.getImage(CoreImageRegistry.IMAGE_EDIT);
		editButton = buttonsComposite.addPushButton(
				CatalogMessages.get().AttributePage_ButtonEdit, editImage,
				rolePermission, mainComposite.createLayoutData(
						IEpLayoutData.FILL, IEpLayoutData.BEGINNING));
		editButton.addSelectionListener(this);

		resetButton = buttonsComposite.addPushButton(
				CatalogMessages.get().AttributePage_ButtonReset, CoreImageRegistry
						.getImage(CoreImageRegistry.IMAGE_X), rolePermission,
				mainComposite.createLayoutData(IEpLayoutData.FILL,
						IEpLayoutData.BEGINNING));
		resetButton.addSelectionListener(this);

		editButton.setEnabled(false);
		resetButton.setEnabled(false);

		this.attributesTableViewer
				.setContentProvider(new ArrayContentProvider());

		if (toolbarManager != null) {
			this.createActions(toolbarManager);
		}

		attributesTableViewer.getSwtTableViewer().addSelectionChangedListener(
				this);
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
		if (rolePermission == EpState.EDITABLE) {
			editButton.setEnabled(true);
			resetButton.setEnabled(true);
		}
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
	 *
	 * @return true if table input is set
	 */
	public boolean isInitialized() {
		return attributesTableViewer.getSwtTableViewer().getInput() != null;
	}
}
