/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.actions;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.elasticpath.cmclient.catalog.CanDeleteObjectResult;
import com.elasticpath.cmclient.catalog.CatalogImageRegistry;
import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.catalog.editors.model.ProductModelController;
import com.elasticpath.cmclient.catalog.editors.product.ProductEditor;
import com.elasticpath.cmclient.catalog.editors.sku.ProductSkuEditor;
import com.elasticpath.cmclient.catalog.views.AbstractProductListView;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.EpUiException;
import com.elasticpath.cmclient.core.dto.catalog.ProductModel;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.util.EditorUtil;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;

/**
 * Begin the process for deleting a product.
 */
public class DeleteProductAction extends AbstractCatalogViewAction implements ISelectionChangedListener {

	private static final Logger LOG = Logger.getLogger(DeleteProductAction.class);

	private final ProductModelController productModelController = new ProductModelController();

	private ProductModel selectedProductModel;

	private AbstractProductListView view;

	/**
	 * Constructor.
	 */
	public DeleteProductAction() {
		super(CatalogMessages.get().DeleteProductAction, CatalogImageRegistry.PRODUCT_DELETE);
		setToolTipText(CatalogMessages.get().DeleteProductAction);
		setEnabled(false);
	}

	/**
	 * Constructor.
	 *
	 * @param view the view this action is used by
	 */
	public DeleteProductAction(final AbstractProductListView view) {
		this();
		view.getViewer().addSelectionChangedListener(this);
		this.view = view;
	}

	@Override
	public void run() {
		LOG.debug("DeleteProduct Action called."); //$NON-NLS-1$

		final Product product = selectedProductModel.getProduct();

		boolean hasOpenEditors = checkForOpenEditorsAndDisplayWarning(product);

		if (!hasOpenEditors) {
			final CanDeleteObjectResult result = productModelController.canDelete(selectedProductModel);
			if (result.canDelete()) {
				final boolean answerYes =
					MessageDialog.openConfirm(null, CatalogMessages.get().DeleteProduct_MsgBox_Title,

							NLS.bind(CatalogMessages.get().DeleteProduct_MsgBox_Content,
							new Object[]{product.getCode(), product.getDisplayName(CorePlugin.getDefault().getDefaultLocale())}));

				if (answerYes) {

					productModelController.delete(selectedProductModel);

					view.refreshViewerInput();
					setEnabled(false);
				}
			} else {

				switch (result.getReason()) {
				case ProductModelController.CANNOT_DELETE_PRODUCT_PART_OF_BUNDLE:
					MessageDialog.openWarning(null,
							CatalogMessages.get().DeleteProduct_CanNotRemove,

							NLS.bind(CatalogMessages.get().DeleteProduct_CanNotRemoveBundleMsg,
							new Object[]{product.getCode(), result.getMessage()}));
					break;
				case ProductModelController.CANNOT_DELETE_PRODUCT_SKU_IS_IN_USE:
					MessageDialog.openWarning(null,
							CatalogMessages.get().DeleteProduct_CanNotRemove,

							NLS.bind(CatalogMessages.get().DeleteProduct_CanNotRemoveProductInOrder,
							new Object[]{product.getCode()}));
					break;
				default:
					MessageDialog.openWarning(null,
							CatalogMessages.get().DeleteProduct_CanNotRemove,

							NLS.bind(CatalogMessages.get().DeleteProduct_CanNotRemoveShippingMsg,
							new Object[]{product.getCode()}));
					break;
				}
			}
		}
	}

	private boolean checkForOpenEditorsAndDisplayWarning(final Product product) {
		final IWorkbenchPage workbenchPage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

		for (IEditorReference editorRef : workbenchPage.getEditorReferences()) {

			try {
				if (EditorUtil.isSameEditor(editorRef, ProductEditor.PART_ID) && EditorUtil.isSameEntity(product, editorRef)) {
					MessageDialog.openWarning(null,
							CatalogMessages.get().DeleteProduct_CanNotRemove,

							NLS.bind(CatalogMessages.get().DeleteProduct_CloseEditor,
							new Object[]{product.getCode(), product.getDisplayName(CorePlugin.getDefault().getDefaultLocale())}));
							return true;
					}

				if (product.hasMultipleSkus()) {
					for (ProductSku selectedSku : product.getProductSkus().values()) {
						if (EditorUtil.isSameEditor(editorRef, ProductSkuEditor.PART_ID)
								&& EditorUtil.isSameEntity(selectedSku, editorRef)) {
							MessageDialog.openWarning(null,
								CatalogMessages.get().ProductEditorMultiSkuSection_CanNotRemove,

									NLS.bind(CatalogMessages.get().ProductEditorMultiSkuSection_CloseEditor,
									new Object[]{selectedSku.getSkuCode(),
									selectedSku.getDisplayName(CorePlugin.getDefault().getDefaultLocale())}));
								return true;

							}
					}
				}

			} catch (PartInitException e) {
				LOG.error(e.getStackTrace());
				throw new EpUiException("Could not get product editor input", e); //$NON-NLS-1$
			}
		}
		return false;
	}

	@Override
	protected void pageSelectionChanged(final ISelection selection) {
		if (!(selection instanceof IStructuredSelection)) {
			return;
		}

		final IStructuredSelection structuredSelection = (IStructuredSelection) selection;
		final Object selectedObject = structuredSelection.getFirstElement();

		if (selectedObject instanceof ProductModel) {
			this.selectedProductModel = (ProductModel) selectedObject;
			this.setEnabled(true);
		} else {
			this.setEnabled(false);
		}
	}

	@Override
	protected boolean isAuthorized() {
		if (selectedProductModel == null) {
			return false;
		}
		return (EpState.EDITABLE == view.getStatePolicy().determineState(getDefaultContainer()));
	}

	@Override
	public void selectionChanged(final SelectionChangedEvent event) {
		pageSelectionChanged(event.getSelection());
	}

	@Override
	public String getTargetIdentifier() {
		return "deleteProductAction"; //$NON-NLS-1$
	}
}
