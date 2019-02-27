/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.wizards.category;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.elasticpath.cmclient.catalog.CatalogImageRegistry;
import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.catalog.editors.attribute.AttributeEditingSupport;
import com.elasticpath.cmclient.catalog.editors.attribute.AttributesLabelProviderUtil;
import com.elasticpath.cmclient.catalog.editors.attribute.IAttributeChangedListener;
import com.elasticpath.cmclient.catalog.editors.attribute.ICellEditorDialogService;
import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.comparator.AttributeValueComparatorByNameIgnoreCase;
import com.elasticpath.cmclient.core.helpers.IValueRetriever;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTableColumn;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.core.wizard.AbstractEPWizardPage;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeGroup;
import com.elasticpath.domain.attribute.AttributeGroupAttribute;
import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.CategoryType;

/**
 * The create category details wizard page.
 */
@SuppressWarnings({ "PMD.PrematureDeclaration" })
public class CreateCategoryWizardAttributesPage extends AbstractEPWizardPage<Category> 
	implements SelectionListener, ISelectionChangedListener, IAttributeChangedListener {

	private static final String ATTRIBUTES_TABLE = "Attributes"; //$NON-NLS-1$

	private Button editButton;

	private Button resetButton;

	private CategoryType lastSelectedCategoryType;

	/**
	 * the table viewer for the attribute info table.
	 */
	private IEpTableViewer attributesTableViewer;

	private final ICellEditorDialogService dialogService;

	private Label attributeImageLabel;

	/**
	 * Constructor.
	 * 
	 * @param pageName the page name
	 */
	protected CreateCategoryWizardAttributesPage(final String pageName) {
		super(2, false, pageName, new DataBindingContext());
		this.setDescription(CatalogMessages.get().CreateCategoryWizardAttributesPage_Description);
		this.setTitle(CatalogMessages.get().CreateCategoryWizardAttributesPage_Title);
		// This disables the wizard's "Finish" button until the user reaches this page
		this.setPageComplete(false);
		dialogService = new DialogService();
	}
	
	@Override
	public void setVisible(final boolean visible) {
		super.setVisible(visible);

		// Enable the wizard's "Finish" button
		this.setPageComplete(true);

		// The data in the attribute table is dependent on the selection of the category-type combo box on the first wizard page; every time this
		// wizard page is loaded, check whether the selection has changed - if yes, repopulate the table's attributes
		if ((visible) && (!this.getModel().getCategoryType().equals(this.lastSelectedCategoryType))) {
			this.lastSelectedCategoryType = this.getModel().getCategoryType();
			// refresh the table viewer
			this.attributesTableViewer.setInput(this.getInput());
		}
	}

	@Override
	public void createEpPageContent(final IEpLayoutComposite mainComposite) {
		// Layout for the table area
		final IEpLayoutData tableLayoutData = mainComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true);
		this.attributesTableViewer = mainComposite.addTableViewer(false, EpState.EDITABLE, tableLayoutData, ATTRIBUTES_TABLE);

		// The name column content of the attribute table
		final IEpTableColumn nameColumn = this.attributesTableViewer.addTableColumn(
				CatalogMessages.get().ProductEditorAttributeSection_TableColumnTitle_Name, 200);
		// The attribute type column content of the attribute table
		final IEpTableColumn typeColumn = this.attributesTableViewer.addTableColumn(
				CatalogMessages.get().ProductEditorAttributeSection_TableColumnTitle_Type, 120);
		// The required column content of the attribute table
		final IEpTableColumn requiredColumn = this.attributesTableViewer.addTableColumn(
				CatalogMessages.get().ProductEditorAttributeSection_TableColumnTitle_Required, 60);
		// The multi-language column content of the attribute table
		final IEpTableColumn multiLanguageColumn = this.attributesTableViewer.addTableColumn(
				CatalogMessages.get().ProductEditorAttributeSection_TableColumnTitle_MLang, 90);
		// The attribute value column content of the attribute table
		final IEpTableColumn valueColumn = this.attributesTableViewer.addTableColumn(
				CatalogMessages.get().ProductEditorAttributeSection_TableColumnTitle_Value, 200);

		final AttributesLabelProviderUtil labelProviderUtil = new AttributesLabelProviderUtil(EpState.EDITABLE);
		labelProviderUtil.setNameColumnLabel(nameColumn);
		labelProviderUtil.setTypeColumnLabel(typeColumn);
		labelProviderUtil.setRequiredColumnLabel(requiredColumn);
		labelProviderUtil.setMultiLanguageColumnLabel(multiLanguageColumn);
		labelProviderUtil.setValueColumnLabel(valueColumn);

		//Creates a pop up window that will be opened to edit this column
		valueColumn.setEditingSupport(new AttributeEditingSupport(this.attributesTableViewer, this.getModel(), dialogService));

		final IEpLayoutComposite buttonsComposite = mainComposite.addGridLayoutComposite(1, false, null);

		// Create edit button for invoking the editing
		final Image editImage = CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_EDIT);
		this.editButton = buttonsComposite.addPushButton(CatalogMessages.get().AttributePage_ButtonEdit, editImage, EpState.EDITABLE, mainComposite
				.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.BEGINNING));
		this.editButton.addSelectionListener(this);

		this.resetButton = buttonsComposite.addPushButton(CatalogMessages.get().AttributePage_ButtonReset, CoreImageRegistry
				.getImage(CoreImageRegistry.IMAGE_X), EpState.EDITABLE, mainComposite.createLayoutData(IEpLayoutData.FILL,
				IEpLayoutData.BEGINNING));
		this.resetButton.addSelectionListener(this);

		this.editButton.setEnabled(false);
		this.resetButton.setEnabled(false);
		
		this.attributeImageLabel = buttonsComposite.addImage(CatalogImageRegistry.IMAGE_NOT_AVAILABLE.createImage(), null);
		attributeImageLabel.setVisible(false);

		this.attributesTableViewer.setContentProvider(new ArrayContentProvider());
		this.attributesTableViewer.setInput(this.getInput());

		// Set the modification listener *after* setting the control values so that controls aren't considered to be modified when the initial value
		// is set.
		// this.mainComposite.setControlModificationListener(this.controlModificationListener);
		this.attributesTableViewer.getSwtTableViewer().addSelectionChangedListener(this);
		

		/* MUST be called */
		this.setControl(mainComposite.getSwtComposite());
	}

	/**
	 * To the data input for the attribute table.
	 * 
	 * @return the element array for the attribute table.
	 */
	private Object getInput() {
		final ArrayList<AttributeValue> inputObjects = new ArrayList<>();

		final CategoryType categoryType = this.getModel().getCategoryType();
		// When the wizard first pops up, no category type will be selected - just return null
		if (categoryType == null) {
			return null;
		}

		// Get the AttributeGroup associated with this CategoryType
		final AttributeGroup attributeGroup = categoryType.getAttributeGroup();
		if ((attributeGroup == null) || (attributeGroup.getAttributeGroupAttributes().isEmpty())) {
			// no attributes
			return null;
		}

		// Get the set of AttributeGroupAttribute associated with this AttributeGroup
		final Set<AttributeGroupAttribute> attributeGroupAttributes = attributeGroup.getAttributeGroupAttributes();

		// Iterate through the set of AttributeGroupAttributes and create the list of AttributeValues to be used for input
		for (final AttributeGroupAttribute currAttributeGroupAttribute : attributeGroupAttributes) {
			Locale attributeValueLocale = null;
			Attribute attribute = currAttributeGroupAttribute.getAttribute();
			if (attribute.isLocaleDependant()) {
				attributeValueLocale = getModel().getCatalog().getDefaultLocale();
			}
			getModel().getAttributeValueGroup().setAttributeValue(attribute, attributeValueLocale, null);
			AttributeValue newAttributeValue = getModel().getAttributeValueGroup().getAttributeValue(
					attribute.getKey(), attributeValueLocale);
			// add it to the table viewer input
			inputObjects.add(newAttributeValue);
		}
		
		Collections.sort(inputObjects, new AttributeValueComparatorByNameIgnoreCase());
		
		return inputObjects.toArray();
	}

	@Override
	public void selectionChanged(final SelectionChangedEvent event) {
		this.editButton.setEnabled(true);
		this.resetButton.setEnabled(true);
	}

	@Override
	public void widgetDefaultSelected(final SelectionEvent event) {
		// do nothing here
	}

	@Override
	public void widgetSelected(final SelectionEvent event) {
		if (event.getSource() == this.editButton) {
			dialogService.createEditorDialog();
		}
		if (event.getSource() == this.resetButton) {
			final IStructuredSelection selection = (IStructuredSelection) attributesTableViewer.getSwtTableViewer().getSelection();
			final AttributeValue attr = (AttributeValue) selection.getFirstElement();
			attr.setValue(null);
			this.attributesTableViewer.getSwtTableViewer().refresh();
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
			final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			Window dialog = AttributeEditingSupport.getEditorDialog(attr, shell);

			final int result = dialog.open();
			if (result == Window.OK) {
				final IValueRetriever retriever = (IValueRetriever) dialog;
				attr.setValue(retriever.getValue());
				attributesTableViewer.getSwtTableViewer().refresh();
			}
		}
	}
	
	@Override
	public void attributeValueChanged(final AttributeValue attributeValue) {
		//this method does not need to do anything as there are no control modification listeners to modify
	}
	
	@Override
	protected void bindControls() {
		// not used
	}

	@Override
	protected void populateControls() {
		this.lastSelectedCategoryType = this.getModel().getCategoryType();
	}
}