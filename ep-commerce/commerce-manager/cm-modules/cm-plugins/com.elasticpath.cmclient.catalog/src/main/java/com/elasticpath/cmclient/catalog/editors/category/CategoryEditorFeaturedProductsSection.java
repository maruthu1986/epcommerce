/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.editors.category;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.ui.dialog.ProductFinderDialog;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareEditorPageSectionPart;
import com.elasticpath.cmclient.policy.ui.IPolicyTargetLayoutComposite;
import com.elasticpath.cmclient.policy.ui.PolicyTargetCompositeFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.service.catalog.CategoryService;
import com.elasticpath.service.catalog.ProductLookup;
import com.elasticpath.service.catalog.ProductService;

/**
 * This class implements the section of the Category editor that displays featured products.
 */
@SuppressWarnings({ "PMD.CyclomaticComplexity", "PMD.GodClass" })
public class CategoryEditorFeaturedProductsSection extends AbstractPolicyAwareEditorPageSectionPart implements SelectionListener,
		ISelectionChangedListener {

	private static final int PRODUCT_CODE_WIDTH = 150;

	private static final int NAME_WIDTH = 300;

	private static final int MAX_ITEM_TO_SCROLL = 10;
	private static final String PRODUCT_SECTION_TABLE = "Product Section"; //$NON-NLS-1$

	private IPolicyTargetLayoutComposite mainComposite;

	private TableViewer tableViewer;

	private Button upButton;

	private Button downButton;

	private Button addButton;

	private Button removeButton;

	private List<Product> featuredProducts;

	private final List<Long> deletedFeaturedProductUids = new ArrayList<>();

	private final List<Product> productsToAddToCategory = new ArrayList<>();

	private final PolicyActionContainer editableControls;

	/**
	 * Constructor.
	 *
	 * @param editor the editor where the detail section will be placed
	 * @param formPage the Eclipse form page
	 */
	public CategoryEditorFeaturedProductsSection(final FormPage formPage, final AbstractCmClientFormEditor editor) {
		super(formPage, editor, ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED);
		editableControls = addPolicyActionContainer("categorySummaryEditableControls"); //$NON-NLS-1$
	}

	@Override
	protected void createControls(final Composite parent, final FormToolkit toolkit) {
		mainComposite = PolicyTargetCompositeFactory.wrapLayoutComposite(
				CompositeFactory.createTableWrapLayoutComposite(parent, 2, false));

		this.featuredProducts = getFeaturedProductsInCategory(getCategory().getUidPk());

		final IEpLayoutData tableViewerData = this.mainComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true, 1, 1);
		final IEpTableViewer epTableViewer = this.mainComposite.addTableViewer(true, tableViewerData, editableControls,
			PRODUCT_SECTION_TABLE);

		epTableViewer.addTableColumn(CatalogMessages.get().ProductMerchandisingAssociationSection_Product_Code, PRODUCT_CODE_WIDTH);
		epTableViewer.addTableColumn(CatalogMessages.get().ProductMerchandisingAssociationSection_Proudct_Name, NAME_WIDTH);
		epTableViewer.getSwtTableViewer().addSelectionChangedListener(this);
		epTableViewer.setContentProvider(new ArrayContentProvider());
		epTableViewer.setLabelProvider(new ViewLabelProvider());
		epTableViewer.setInput(this.featuredProducts.toArray());

		this.tableViewer = epTableViewer.getSwtTableViewer();

		final IEpLayoutData buttonCompositeData = this.mainComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false);

		final IPolicyTargetLayoutComposite buttonComposite =
			this.mainComposite.addTableWrapLayoutComposite(1, false, buttonCompositeData, editableControls);

		this.createButtons(buttonComposite);
	}

	private void createButtons(final IPolicyTargetLayoutComposite epComposite) {
		this.addButton = epComposite.addPushButton(CatalogMessages.get().CategoryFeaturedProductsSection_Add, CoreImageRegistry
				.getImage(CoreImageRegistry.IMAGE_ADD), null, editableControls);
		this.addButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				openSelectProductDialog(event);
			}
		});

		this.removeButton = epComposite.addPushButton(CatalogMessages.get().CategoryFeaturedProductsSection_Remove, CoreImageRegistry
				.getImage(CoreImageRegistry.IMAGE_REMOVE), null, editableControls);
		this.removeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				final Product selectedProduct = getSelectedProduct();

				final String removeProductMsg =
					NLS.bind(CatalogMessages.get().CategoryFeaturedDialog_RemoveMsg,
					new Object[]{selectedProduct.getDisplayName(CorePlugin.getDefault().getDefaultLocale())});

				final boolean answerYes = MessageDialog.openConfirm(getManagedForm().getForm().getShell(),
						CatalogMessages.get().CategoryFeaturedDialog_RemoveTitle, removeProductMsg);
				if (answerYes) {
					CategoryEditorFeaturedProductsSection.this.featuredProducts.remove(selectedProduct);
					CategoryEditorFeaturedProductsSection.this.productsToAddToCategory.remove(selectedProduct);
					CategoryEditorFeaturedProductsSection.this.deletedFeaturedProductUids.add(selectedProduct.getUidPk());
					markDirty();

					refreshTableViewer();
					refreshButtons();
				}
			}
		});

		// add spacer
		epComposite.addEmptyComponent(null, editableControls);

		this.upButton = epComposite.addPushButton(CatalogMessages.get().CategoryFeaturedProductsSection_Move_Up, CoreImageRegistry
				.getImage(CoreImageRegistry.IMAGE_UP_ARROW), null, editableControls);
		this.upButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				final int selectionIndex = CategoryEditorFeaturedProductsSection.this.tableViewer.getTable().getSelectionIndex();

				final Product product1 = getSelectedProduct();
				final Product product2 = (Product) CategoryEditorFeaturedProductsSection.this.tableViewer.getElementAt(selectionIndex - 1);

				if (product1 == null || product2 == null) {
					return;
				}
				switchOrdering(product1, product2);
				markDirty();
				refreshTableViewer();
				refreshButtons();
			}
		});

		this.downButton = epComposite.addPushButton(CatalogMessages.get().CategoryFeaturedProductsSection_Move_Down, CoreImageRegistry
				.getImage(CoreImageRegistry.IMAGE_DOWN_ARROW), null, editableControls);
		this.downButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				final int selectionIndex = CategoryEditorFeaturedProductsSection.this.tableViewer.getTable().getSelectionIndex();

				final Product product1 = getSelectedProduct();
				final Product product2 = (Product) CategoryEditorFeaturedProductsSection.this.tableViewer.getElementAt(selectionIndex + 1);

				if (product1 == null || product2 == null) {
					return;
				}
				switchOrdering(product1, product2);
				markDirty();
				refreshTableViewer();
				refreshButtons();
			}
		});

		// add spacer
		epComposite.addEmptyComponent(null, editableControls);
		disableButtons();
	}

	/**
	 * Returns the <code>List</code> of <code>Product</code> objects that are featured in the category specified by the given <code>long</code>
	 * category uid.
	 *
	 * @param categoryUid the category uid
	 * @return the <code>List</code> of <code>Product</code> objects that are featured in the category specified by the given <code>long</code>
	 *         category uid
	 */
	private List<Product> getFeaturedProductsInCategory(final long categoryUid) {
		final CategoryService categoryService = ServiceLocator.getService(ContextIdNames.CATEGORY_SERVICE);
		final List<Object[]> sortedObjectPairs = categoryService.getFeaturedProductsList(categoryUid);
		final List<Product> sortedProducts = new ArrayList<>();

		for (final Object[] currObjectPair : sortedObjectPairs) {
			sortedProducts.add((Product) currObjectPair[0]);
		}

		return sortedProducts;
	}

	/**
	 * Refresh the table viewer.
	 */
	private void refreshTableViewer() {
		this.tableViewer.setInput(this.featuredProducts.toArray());
		this.tableViewer.refresh();
		if (this.tableViewer.getTable().getItemCount() < MAX_ITEM_TO_SCROLL) {
			this.mainComposite.getSwtComposite().layout();
		}
		getEditor().controlModified();
	}

	/**
	 * Return the selected <code>Product</code> object.
	 *
	 * @return the selected <code>Product</code> object
	 */
	private Product getSelectedProduct() {
		final ISelection selection = this.tableViewer.getSelection();
		return (Product) ((IStructuredSelection) selection).getFirstElement();
	}

	/**
	 * Opens a <code>ProductFinderDialog</code> that searches products belonging to the model category.
	 *
	 * @param event the <code>SelectionEvent</code>
	 */
	private void openSelectProductDialog(final SelectionEvent event) {
		final Category category = (getCategory());
		final ProductFinderDialog productFindDialog = new ProductFinderDialog(event.display.getActiveShell(),
				category.getUidPk(), category.getCatalog(), true);
		final int result = productFindDialog.open();
		if (result != Window.OK) {
			return;
		}
		final Product selectedProduct = (Product) productFindDialog.getSelectedObject();

		if (selectedProduct != null) {
			// check if product is already featured
			if (featuredProducts.contains(selectedProduct)) {
				final String addProductWarningMsg =
					NLS.bind(CatalogMessages.get().CategoryFeaturedDialog_AddWarningMsg,
					new Object[]{selectedProduct.getDisplayName(CorePlugin.getDefault().getDefaultLocale())});
				MessageDialog.openWarning(getManagedForm().getForm().getShell(), CatalogMessages.get().CategoryFeaturedDialog_AddWarningTitle,
						addProductWarningMsg);
			} else {
				final ProductLookup productLookup = ServiceLocator.getService(
						ContextIdNames.PRODUCT_LOOKUP);
				final Product featuredProduct = productLookup.findByUid(selectedProduct.getUidPk());
				featuredProducts.add(featuredProduct);
				
				// Check if we are featuring a product that is not in this category (i.e. the product is in one of this category's
				// sub-categories).
				if (!isProductInCategory(selectedProduct, category)) {
					productsToAddToCategory.add(featuredProduct);
				}
				
				markDirty();
				refreshTableViewer();
			}
		}
	}
	
	/**
	 * Returns true if the given Category contains the given Product; false otherwise.
	 *
	 * @param product the product to check.
	 * @param category the category to check.
	 * @return true if the given Category contains the given Product; false otherwise.
	 */
	private boolean isProductInCategory(final Product product, final Category category) {
		for (final Category currCategory : product.getCategories()) {
			if (currCategory.getCode().equals(category.getCode())) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Swap the featured product order of the two given <code>Product</code> objects in the list.
	 * 
	 * @param product1 product one
	 * @param product2 product two
	 */
	private void switchOrdering(final Product product1, final Product product2) {
		final int product1Index = this.featuredProducts.indexOf(product1);
		final int product2Index = this.featuredProducts.indexOf(product2);

		this.featuredProducts.set(product1Index, product2);
		this.featuredProducts.set(product2Index, product1);
	}

	@Override
	public void commit(final boolean onSave) {
		super.commit(onSave);

		final ProductService productService = ServiceLocator.getService(ContextIdNames.PRODUCT_SERVICE);

		// "Un-feature" any products that were removed from the featured product list
		for (final Long currDeletedProductUid : this.deletedFeaturedProductUids) {
			productService.resetProductCategoryFeatured(currDeletedProductUid, getCategory().getUidPk());
		}
		
		// Any products chosen to be featured that are not already in this category need to be added first, before they are featured.
		for (final Product currProductToAdd : this.productsToAddToCategory) {
			currProductToAdd.addCategory(getCategory());
			productService.saveOrUpdate(currProductToAdd);
		}
		// Update the featured products list in the database. Do this by first removing all the featured products in this category and then adding
		// them back in the correct order
		for (final Product currFeaturedProduct : this.featuredProducts) {
			// reset the featured order value to 0
			productService.resetProductCategoryFeatured(currFeaturedProduct.getUidPk(), getCategory().getUidPk());
		}
		for (final Product currFeaturedProduct : this.featuredProducts) {
			// set the order value to the list index
			productService.setProductCategoryFeatured(currFeaturedProduct.getUidPk(), getCategory().getUidPk());
		}
		
		this.deletedFeaturedProductUids.clear();
		this.productsToAddToCategory.clear();
	}

	private Category getCategory() {
		return (Category) getModel();
	}

	/**
	 * Provides the labels for the Featured Product table.
	 */
	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {

		/**
		 * Get the column image.
		 * 
		 * @param element not used
		 * @param columnIndex the column to create an image for
		 * @return the image
		 */
		@Override
		public Image getColumnImage(final Object element, final int columnIndex) {
			return null;
		}
		
		@Override
		public String getColumnText(final Object element, final int columnIndex) {
			final Product product = (Product) element;

			switch (columnIndex) {
			case 0:
				return product.getCode();
			case 1:
				return product.getDisplayName(CorePlugin.getDefault().getDefaultLocale());
			default:
				return CatalogMessages.get().Product_NotAvailable;
			}
		}

		@Override
		public String getText(final Object element) {
			final int stringLength = 6;
			String featuredProductOrder = String.valueOf(featuredProducts.indexOf(element));
			// the string comparison here is not very smart (e.g. "11" comes before "9") so we need to append each string with 0s (e.g. "11" comes
			// after "09")
			while (featuredProductOrder.length() < stringLength) {
				featuredProductOrder = "0".concat(featuredProductOrder); //$NON-NLS-1$
			}
			return featuredProductOrder;
		}
	}

	@Override
	public void widgetDefaultSelected(final SelectionEvent event) {
		// Not used
	}

	@Override
	public void widgetSelected(final SelectionEvent event) {
		this.refreshButtons();
	}

	@Override
	protected void bindControls(final DataBindingContext bindingContext) {
		// Empty
	}

	@Override
	protected void populateControls() {
		// Empty
	}

	@Override
	public void selectionChanged(final SelectionChangedEvent event) {
		refreshLayout();
	}

	private void refreshButtons() {
		final int selectionIndex = this.tableViewer.getTable().getSelectionIndex();

		disableButtons();

		if (isAuthorized() && (selectionIndex != -1)) {  // && EpState.EDITABLE == epState
			if (selectionIndex == 0) {
				this.downButton.setEnabled(true);
				this.removeButton.setEnabled(true);
			} else if (selectionIndex == this.tableViewer.getTable().getItemCount() - 1) {
				this.upButton.setEnabled(true);
				this.removeButton.setEnabled(true);
			} else {
				enableButtons();
			}
		}
	}
	
	private boolean isAuthorized() {
		return (getStatePolicy() != null && EpState.EDITABLE.equals(getStatePolicy().determineState(editableControls)));
	}

	private void disableButtons() {
		this.upButton.setEnabled(false);
		this.downButton.setEnabled(false);
		this.removeButton.setEnabled(false);
	}

	private void enableButtons() {
		this.upButton.setEnabled(true);
		this.downButton.setEnabled(true);
		this.removeButton.setEnabled(true);
	}

	@Override
	public void refreshLayout() {
		if (!mainComposite.getSwtComposite().isDisposed()) {
			mainComposite.getSwtComposite().getParent().layout();
		}
		refreshButtons();
	}
}