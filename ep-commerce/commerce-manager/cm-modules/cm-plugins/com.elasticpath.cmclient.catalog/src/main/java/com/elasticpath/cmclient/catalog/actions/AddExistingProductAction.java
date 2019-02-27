/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.actions;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.elasticpath.cmclient.catalog.CatalogImageRegistry;
import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.cmclient.core.service.CatalogEventService;
import com.elasticpath.cmclient.core.ui.dialog.ProductFinderDialog;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.policy.StatePolicy;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.exception.EpUnsupportedOperationException;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.service.catalog.ProductService;

/**
 * This class is responsible for carrying out the action of adding existing products to virtual categories.
 */
public class AddExistingProductAction extends AbstractCatalogViewAction {

	private static final Logger LOG = Logger.getLogger(AddExistingProductAction.class);

	private Category category;

	private final ProductService productService;

	/**
	 * Constructs a create sub-category action.
	 */
	public AddExistingProductAction() {
		super(CatalogMessages.get().CatalogBrowseView_Action_AddExistingProduct, CatalogImageRegistry.PRODUCT_INCLUDE);
		setEnabled(false);
		this.productService = ServiceLocator.getService(ContextIdNames.PRODUCT_SERVICE);
	}

	@Override
	protected void pageSelectionChanged(final ISelection selection) {
		if (!(selection instanceof IStructuredSelection)) {
			setEnabled(false);
			return;
		}

		final Object selectedObject = ((IStructuredSelection) selection).getFirstElement();
		if (!(selectedObject instanceof Category)) {
			setEnabled(false);
			return;
		}
		this.category = (Category) selectedObject;
		setEnabled(canAddExistingProductToVirtualCategory((Category) selectedObject));
	}

	/**
	 * Determines whether an existing product can be added to the given category.
	 *
	 * @param category the category to which a product may be added
	 * @return true if the user is able to add an existing product to the given category
	 */
	boolean canAddExistingProductToVirtualCategory(final Category category) {
		// Add existing product is enabled for virtual categories that are NOT linked
		if (!category.isVirtual() || category.isLinked()) {
			return false;
		}

		boolean editable = false;

		final StatePolicy statePolicy = getStatePolicy();
		if (statePolicy != null && category != null && category.getCatalog() != null) {
			statePolicy.init(category);
			editable = (EpState.EDITABLE == statePolicy.determineState(getDefaultContainer()));
		}

		return editable;
	}

	@Override
	public void run() {
		final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();

		LOG.debug("AddExistingProductAction called."); //$NON-NLS-1$

		// Create and open a product finder dialog
		final ProductFinderDialog productFinderDialog = new ProductFinderDialog(shell, true, false);
		productFinderDialog.setErrorMessage(CatalogMessages.get().NoPermissionToAccessMasterCatalog);

		if (Window.OK == productFinderDialog.open()) {
			final Product selectedProduct = (Product) productFinderDialog.getSelectedObject();

			if (selectedProduct == null) {
				throw new EpUnsupportedOperationException("Product cannot be null"); //$NON-NLS-1$
			}

			// Add the selected product to the category
			selectedProduct.addCategory(this.category);
			productService.saveOrUpdate(selectedProduct);

			// Fire an event to refresh the browse list view
			final ItemChangeEvent<Category> event = new ItemChangeEvent<>(this, this.category, ItemChangeEvent.EventType.CHANGE);
			CatalogEventService.getInstance().notifyCategoryChanged(event);
		}
	}

	@Override
	protected boolean isAuthorized() {
		return true;
	}

	@Override
	public String getTargetIdentifier() {
		return "addExistingProductAction"; //$NON-NLS-1$
	}

}