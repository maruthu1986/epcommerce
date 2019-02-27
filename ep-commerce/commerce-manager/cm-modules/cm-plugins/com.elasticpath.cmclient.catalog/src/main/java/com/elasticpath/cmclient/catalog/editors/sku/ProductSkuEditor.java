/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.editors.sku;

import java.util.Collection;
import java.util.Locale;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.catalog.CatalogPlugin;
import com.elasticpath.cmclient.catalog.editors.model.ProductModelController;
import com.elasticpath.cmclient.catalog.editors.product.ProductPricePage;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.EpUiException;
import com.elasticpath.cmclient.core.dto.catalog.ProductSkuModel;
import com.elasticpath.cmclient.core.event.ChangeSetMemberSelectionProvider;
import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.cmclient.core.service.CatalogEventService;
import com.elasticpath.cmclient.core.ui.TableSelectionProvider;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareFormEditor;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.pricing.BaseAmount;
import com.elasticpath.domain.pricing.exceptions.DuplicateBaseAmountException;

/**
 * Implements a multi-page editor for displaying and editing product SKUs.
 *
 */
public class ProductSkuEditor extends AbstractPolicyAwareFormEditor implements ChangeSetMemberSelectionProvider {

	/**
	 * Editor ID.
	 */
	public static final String PART_ID = ProductSkuEditor.class.getName();

	private ProductSkuModel productSkuModel;

	private final ProductModelController productSkuEditorController;

	private final TableSelectionProvider baseAmountTableSelectionProvider;

	private ProductPricePage productPricePage;

	/**
	 * The constructor.
	 */
	public ProductSkuEditor() {
		productSkuEditorController = new ProductModelController();
		baseAmountTableSelectionProvider = new TableSelectionProvider();
	}

	@Override
	public void initEditor(final IEditorSite site, final IEditorInput input) throws PartInitException {
		String skuCode = getEditorInput().getAdapter(String.class);
		this.productSkuModel = productSkuEditorController.buildProductSkuEditorModel(skuCode);

		// add the base amount table listener to the selection service as a selection provider
		// a selected base amount can then be added to a change set, see AddToChangeSetActionDelegate
		site.setSelectionProvider(baseAmountTableSelectionProvider);
	}

	@Override
	public ProductSkuModel getModel() {
		return productSkuModel;
	}

	@Override
	public Object getDependentObject() {
		return getProductSku();
	}

	/**
	 * Retrieves the product SKU to be displayed/edited from persistent storage.
	 *
	 * @return the <code>ProductSku</code>
	 */

	private ProductSku getProductSku() {
		if (productSkuModel == null) {
			reloadModel();
		}
		return productSkuModel.getProductSku();
	}

	@Override
	protected void addPages() {

		PolicyActionContainer pageContainer = addPolicyActionContainer("skueditor"); //$NON-NLS-1$

		try {
				addPage(new ProductSkuDetailsPage(this, true, true), pageContainer);
				productPricePage = new ProductPricePage("productPrice", this, baseAmountTableSelectionProvider); //$NON-NLS-1$
			addPage(productPricePage, pageContainer);
				addPage(new SkuAttributePage(this), pageContainer);

				getCustomData().put("pageContainer", pageContainer);
				getCustomData().put("baseAmountTableSelectionProvider", baseAmountTableSelectionProvider);
				addExtensionPages(getClass().getSimpleName(), CatalogPlugin.PLUGIN_ID);
			} catch (final PartInitException e) {
				throw new EpUiException(e);
			}
	}

	@Override
	protected void saveModel(final IProgressMonitor monitor) {
		monitor.beginTask(CatalogMessages.get().ProductSkuEditor_Save_StatusBarMsg, 2);
		try {
			productSkuModel = productSkuEditorController.saveOrUpdate(productSkuModel);
			if (productPricePage != null) {
				productPricePage.saveModel();
			}
			refreshEditorPages();
			final ItemChangeEvent<ProductSku> event = new ItemChangeEvent<>(this, getProductSku());
			monitor.worked(1);
			CatalogEventService.getInstance().notifyProductSkuChanged(event);
		} catch (DuplicateBaseAmountException dbae) {
				BaseAmount baseAmount = dbae.getBaseAmount();
			MessageDialog.openError(Display.getCurrent().getActiveShell(),
				CatalogMessages.get().ProductSaveDuplicateBaseAmountErrorTitle,

					NLS.bind(CatalogMessages.get().ProductSaveDuplicateBaseAmountErrorMsg,
					new Object[]{baseAmount.getObjectType(), baseAmount.getObjectGuid(), baseAmount.getQuantity().toString()}));
				monitor.setCanceled(true);
				return;
		} finally {
			monitor.done();
		}
	}

	@Override
	public void reloadModel() {
		productSkuModel = productSkuEditorController.buildProductSkuEditorModel(productSkuModel.getProductSku().getGuid());
	}

	@Override
	public Collection<Locale> getSupportedLocales() {
		return getProductSku().getProduct().getProductType().getCatalog().getSupportedLocales();
	}

	@Override
	public Locale getDefaultLocale() {
		return getProductSku().getProduct().getProductType().getCatalog().getDefaultLocale();
	}

	@Override
	protected String getSaveOnCloseMessage() {
		return
			NLS.bind(CatalogMessages.get().ProductSkuEditor_OnSavePrompt,
			getEditorName());
	}

	@Override
	public String getEditorName() {
		return getProductSku().getSkuCode();
	}
	@Override
	public String getEditorToolTip() {
		final Locale locale = CorePlugin.getDefault().getDefaultLocale();
		final String tooltip;
		if (getProductSku().getProduct().getProductType().isMultiSku()) {
			tooltip =
				NLS.bind(CatalogMessages.get().ProductSkuEditor_MultiSku_Tooltip,
				new Object[]{
				getProductSku().getSkuCode(),
				getProductSku().getProduct().getDisplayName(locale),
				getProductSku().getDisplayName(locale)});
		} else {
			tooltip =
				NLS.bind(CatalogMessages.get().ProductSkuEditor_SingleSku_Tooltip,
				new Object[]{
				getProductSku().getSkuCode(),
				getProductSku().getProduct().getDisplayName(locale)});
		}
		return tooltip;
	}
	
	

	/**
	 * Gets the SKU GUID.
	 * 
	 * @return the SKU GUID
	 */
	protected String getProductSkuGuid() {
		return getEditorInput().getAdapter(String.class);
	}

	@Override
	public String getTargetIdentifier() {
		return "skuEditor"; //$NON-NLS-1$
	}
	
	@Override
	public Object resolveObjectMember(final Object changeSetObjectSelection) {
		return changeSetObjectSelection;
	}

}
