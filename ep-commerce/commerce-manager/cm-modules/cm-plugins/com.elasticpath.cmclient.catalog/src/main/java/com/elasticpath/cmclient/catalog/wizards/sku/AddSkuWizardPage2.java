/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.catalog.wizards.sku;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
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
import com.elasticpath.cmclient.catalog.editors.attribute.ICellEditorDialogService;
import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.comparator.AttributeValueComparatorByNameIgnoreCase;
import com.elasticpath.cmclient.core.helpers.IValueRetriever;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTableColumn;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.core.wizard.AbstractEPWizardPage;
import com.elasticpath.domain.attribute.AttributeGroup;
import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;

/**
 * The Store configuration wizard page.
 */
public class AddSkuWizardPage2 extends AbstractEPWizardPage<Product> implements SelectionListener {

	private static final int TABLE_HIGH_HINT = 450;

	private static final int TABLE_WIDTH_HINT = 650;
	private static final String ATTRIBUTES_TABLE = "Attributes"; //$NON-NLS-1$

	private final String title;

	private final ProductSku productSku;

	private final Product product;

	private final ICellEditorDialogService dialogService;

	/**
	 * the table viewer for the attribute info table.
	 */
	private IEpTableViewer attributesTableViewer;
	
	private Button editButton;

	private Button clearButton;

	private Label attributeImageLabel;
	
//	private static final Logger LOG = Logger.getLogger(AddSkuWizardPage2.class);
	
		

	/**
	 * The Constructor.
	 *  @param pageName the page name
	 * @param title the page title
	 * @param databindingContext the data binding context
	 * @param product the <code>Product</code> will have this new sku
	 * @param productSku the <code>ProductSku</code>
	 */
	protected AddSkuWizardPage2(final String pageName, final String title,
								final DataBindingContext databindingContext, final Product product, final ProductSku productSku) {
		super(2, false, pageName, databindingContext);
		this.title = title;
		setMessage(CatalogMessages.get().AddSkuWizardPage2_Msg);
		this.product = product;
		this.productSku = productSku;
		this.dialogService = new DialogService();
	}

	@SuppressWarnings("PMD.ExcessiveMethodLength")
	@Override
	protected void createEpPageContent(final IEpLayoutComposite mainComposite) {
		
			// layout for the table area
		final IEpLayoutData tableLayoutData = mainComposite.createLayoutData(
				IEpLayoutData.FILL, IEpLayoutData.FILL, true, true);

		// ---- DOCcreateEpPageContent

		this.attributesTableViewer = mainComposite.addTableViewer(false, EpState.EDITABLE, tableLayoutData, ATTRIBUTES_TABLE);
		final GridData tableLayoutData2 = (GridData) attributesTableViewer.getSwtTable().getLayoutData();
		tableLayoutData2.heightHint = TABLE_HIGH_HINT;
		tableLayoutData2.widthHint = TABLE_WIDTH_HINT;
		
		//getSection().setLayoutData(getLayoutData());

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
		
		// The attribute required column content of the attribute table
		final IEpTableColumn requiredColumn = this.attributesTableViewer
				.addTableColumn(
						CatalogMessages.get().ProductEditorAttributeSection_TableColumnTitle_Required,
						60);
		
		// The attribute required column content of the attribute table
		final IEpTableColumn multiLanguageColumn = this.attributesTableViewer
				.addTableColumn(
						CatalogMessages.get().ProductEditorAttributeSection_TableColumnTitle_MLang,
						90);

		// The attribute value column content of the attribute table
		final IEpTableColumn valueColumn = this.attributesTableViewer
				.addTableColumn(
						CatalogMessages.get().ProductEditorAttributeSection_TableColumnTitle_Value,
						200);
		
		final AttributesLabelProviderUtil labelProviderUtil = new AttributesLabelProviderUtil(EpState.EDITABLE);
		labelProviderUtil.setNameColumnLabel(nameColumn);
		labelProviderUtil.setTypeColumnLabel(typeColumn);
		labelProviderUtil.setRequiredColumnLabel(requiredColumn);
		labelProviderUtil.setMultiLanguageColumnLabel(multiLanguageColumn);
		labelProviderUtil.setValueColumnLabel(valueColumn);

		// add EditSupport to the attribute value column
		valueColumn.setEditingSupport(new AttributeEditingSupport(this.attributesTableViewer, this.getProductSku(), dialogService));
		
		// ---- DOCcreateEpPageContent
		
		final IEpLayoutComposite buttonsComposite = mainComposite.addGridLayoutComposite(1, false, null);

		// create edit button for invoking the editing
		final Image editImage = CoreImageRegistry
				.getImage(CoreImageRegistry.IMAGE_EDIT);
		editButton = buttonsComposite.addPushButton(CatalogMessages.get().AttributePage_ButtonEdit, editImage,
				EpState.EDITABLE, mainComposite.createLayoutData(
						IEpLayoutData.FILL, IEpLayoutData.BEGINNING));
		editButton.addSelectionListener(this);

		clearButton = buttonsComposite.addPushButton(
				CatalogMessages.get().AttributePage_ButtonReset, CoreImageRegistry
						.getImage(CoreImageRegistry.IMAGE_X),
						EpState.EDITABLE, mainComposite.createLayoutData(
						IEpLayoutData.FILL, IEpLayoutData.BEGINNING));
		clearButton.addSelectionListener(this);
		
		Locale locale = getLocaleFromCatalog();
		
		if (isEmptyAttributeValueGroup(locale)) {
			editButton.setEnabled(false);
			clearButton.setEnabled(false);
		}
		
		this.attributeImageLabel = buttonsComposite.addImage(CatalogImageRegistry.IMAGE_NOT_AVAILABLE.createImage(), null);
		attributeImageLabel.setVisible(false);

		this.attributesTableViewer.setContentProvider(new ArrayContentProvider());
							
		this.attributesTableViewer.setInput(getInput(locale));
		
		editButton.setEnabled(false);
		clearButton.setEnabled(false);
		this.attributesTableViewer.getSwtTableViewer().addSelectionChangedListener(event -> {
			final IStructuredSelection strSelection = (IStructuredSelection) event.getSelection();
			Object firstSelection = strSelection.getFirstElement();
			editButton.setEnabled(firstSelection != null);
			clearButton.setEnabled(firstSelection != null);
		});
		
		this.setControl(mainComposite.getSwtComposite());
	}

	/** 
	 * Gets the locale from the master catalog. If no master catalog found, uses the Default locale.
	 * @return the locale.
	 */
	private Locale getLocaleFromCatalog() {
		for (Catalog catalog : this.product.getCatalogs()) {
			if (catalog.isMaster()) {
				return catalog.getDefaultLocale();
			}
		}
		return CorePlugin.getDefault().getDefaultLocale();
	}
	
	
	/**
	 * To the data input for the attribute table.
	 * 
	 * @param locale the selected locale.
	 * @return the element array for the attribute table.
	 */
	public Object getInput(final Locale locale) {
		final AttributeGroup attributeGroup = product.getProductType().getSkuAttributeGroup();
		final List<AttributeValue> attributeValueList = productSku.getAttributeValueGroup().getFullAttributeValues(attributeGroup, locale);
		final List<AttributeValue> newList = new ArrayList<>();

		for (final AttributeValue attribute : attributeValueList) {
				newList.add(attribute);
		}

		Collections.sort(newList, new AttributeValueComparatorByNameIgnoreCase());
		
		return newList.toArray();
	}
	
	/**
	 * Return whether we have attribute values in the group.
	 * @param locale is the locale
	 * @return whether we have attribute values
	 */
	protected boolean isEmptyAttributeValueGroup(final Locale locale) {
		final AttributeGroup attributeGroup = product.getProductType().getSkuAttributeGroup();
		final List<AttributeValue> attributeValueList = productSku.getAttributeValueGroup().getFullAttributeValues(attributeGroup, locale);
		return attributeValueList.isEmpty();
	}
	
	
	@Override
	public void widgetDefaultSelected(final SelectionEvent event) {
		// not used
	}

	@Override
	public void widgetSelected(final SelectionEvent event) {
		if (event.getSource() == editButton) {
			dialogService.createEditorDialog();
		}
		if (event.getSource() == clearButton) {
			clearAttributeValue();
		}
	}

	/**
	 * Clear attribute value.
	 */
	protected void clearAttributeValue() {
		final IStructuredSelection selection = (IStructuredSelection) attributesTableViewer.getSwtTableViewer().getSelection();
		final AttributeValue attribute = (AttributeValue) selection
				.getFirstElement();
		
		if (attribute == null) {
			MessageDialog.openWarning(this.getShell(), CatalogMessages.get().AddSkuWizard_CannotClearAttributeValue,
					CatalogMessages.get().AddSkuWizard_CannotClearAttributeValue);
		} else {			
			attribute.setValue(null);
			attributesTableViewer.getSwtTableViewer().refresh();
		}
	}

	/**
	 * Dialog Service opens a Dialog to edit the value of the specific cell.
	 */
	protected class DialogService implements ICellEditorDialogService {
		@Override
		public void createEditorDialog() {
			final IStructuredSelection selection = (IStructuredSelection) attributesTableViewer.getSwtTableViewer().getSelection();
			final AttributeValue attribute = (AttributeValue) selection.getFirstElement();

			if (attribute == null) {
				MessageDialog.openWarning(AddSkuWizardPage2.this.getShell(), CatalogMessages.get().AddSkuWizard_CannotEditAttributeValue,
					CatalogMessages.get().AddSkuWizard_CannotEditAttributeValue);
			} else {
				final Shell shell = AddSkuWizardPage2.this.getShell();
				Window dialog = AttributeEditingSupport.getEditorDialog(attribute, shell);
				final int result = dialog.open();
				if (result == Window.OK) {
					final IValueRetriever retriever = (IValueRetriever) dialog;
					attribute.setValue(retriever.getValue());
					AddSkuWizardPage2.this.productSku.getAttributeValueMap().put(attribute.getLocalizedAttributeKey(), attribute);
					attributesTableViewer.getSwtTableViewer().refresh();
				}
			}
		}
	}
		
	@Override
	public void populateControls() {
		//getButton(IDialogConstants.OK_ID).setEnabled(false);
	}	
	
	@Override
	protected void bindControls() {
		//Empty
	}

	@Override
	protected String getTitlePage() {
		return title;
	}

	/**
	 * Return the product sku.
	 * @return product sku
	 */
	public ProductSku getProductSku() {
		return productSku;
	}

	/**
	 * @param attributeValue attributeValue
	 */
	public void attributeValueChanged(final AttributeValue attributeValue) {
		//this method does not need to do anything as there are no control modification listeners to modify
	}
}
