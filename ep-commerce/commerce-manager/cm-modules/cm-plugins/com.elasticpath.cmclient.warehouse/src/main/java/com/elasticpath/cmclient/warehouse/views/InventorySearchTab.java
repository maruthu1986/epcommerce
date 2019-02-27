/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.warehouse.views;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.ImageHyperlink;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.CmClientResources;
import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.ui.dialog.SkuFinderDialog;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTabFolder;
import com.elasticpath.cmclient.warehouse.WarehouseImageRegistry;
import com.elasticpath.cmclient.warehouse.WarehouseMessages;
import com.elasticpath.cmclient.warehouse.actions.OpenInventoryEditorAction;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.AvailabilityCriteria;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.service.catalog.ProductSkuLookup;

/**
 * The inventory search tab in the search view.
 */
public class InventorySearchTab implements ITab {

	private static final int SEARCH_COLUMN_COUNT = 3;

	private static final int SKU_CODE_TEXT_LIMIT = 255;

	private final Text productSkuCodeText;

	private final ImageHyperlink selectSkuHyperlink;

	private final Button retrieveButton;

	private IEpLayoutComposite errorComposite;

	private Label errorMessageLabel;

	private ProductSku productSKU;

	/**
	 * Construct the customer search tab.
	 *
	 * @param tabFolder the tabFolder
	 * @param tabIndex the tab index
	 */
	public InventorySearchTab(final IEpTabFolder tabFolder, final int tabIndex) {
		final IEpLayoutComposite compositeTab = tabFolder.addTabItem(WarehouseMessages.get().SearchView_InventoryTab,
				WarehouseImageRegistry.getImage(WarehouseImageRegistry.IMAGE_INVENTORY), tabIndex, 1, false);

		IEpLayoutComposite searchGroup = compositeTab.addGroup(WarehouseMessages.get().SearchView_RetrieveSkuGroup, SEARCH_COLUMN_COUNT, false,
				compositeTab.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false));

		createErrorComposite(searchGroup);

		final IEpLayoutData labelData = searchGroup.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL);
		final IEpLayoutData fieldData = searchGroup.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false);

		searchGroup.addLabelBold(WarehouseMessages.get().SearchView_SkuCodeLabel, labelData);
		productSkuCodeText = searchGroup.addTextField(EpState.EDITABLE, fieldData);
		productSkuCodeText.setTextLimit(SKU_CODE_TEXT_LIMIT);
		selectSkuHyperlink = searchGroup.addHyperLinkImage(WarehouseImageRegistry.getImage(WarehouseImageRegistry.IMAGE_SELECT_SKU),
				EpState.EDITABLE, labelData);

		selectSkuHyperlink.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(final MouseEvent event) {
				// FIXME: use shell instead null
				SkuFinderDialog dialog = new SkuFinderDialog(null, null, false);
				if (dialog.open() == Window.OK) {
					Object selectedObject = dialog.getSelectedObject();
					if (selectedObject instanceof ProductSku) {
						productSkuCodeText.setText(((ProductSku) selectedObject).getSkuCode());
					} else if (selectedObject instanceof Product) {
						productSkuCodeText.setText(((Product) selectedObject).getDefaultSku().getSkuCode());
					}
				}
			}
		});

		retrieveButton = compositeTab.addPushButton(WarehouseMessages.get().InventorySearchView_RetrieveButton, WarehouseImageRegistry
				.getImage(WarehouseImageRegistry.IMAGE_RETRIEVE_INVENTORY), EpState.EDITABLE, compositeTab.createLayoutData(IEpLayoutData.END,
				IEpLayoutData.FILL));

		SelectionListener retrieveListener = new SelectionListener() {

			@Override
			public void widgetDefaultSelected(final SelectionEvent event) {
				widgetSelected(event);
			}

			@Override
			public void widgetSelected(final SelectionEvent event) {
				if (validateSku(productSkuCodeText.getText().trim())) {
					SearchView view = (SearchView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(
							SearchView.ID_SEARCH_VIEW);
					OpenInventoryEditorAction openInventoryEditorAction = new OpenInventoryEditorAction(productSKU, view.getSite());
					openInventoryEditorAction.run();
				}
			}

		};

		retrieveButton.addSelectionListener(retrieveListener);
		productSkuCodeText.addSelectionListener(retrieveListener);

	}

	private IEpLayoutComposite createErrorComposite(final IEpLayoutComposite searchGroup) {
		errorComposite = searchGroup.addGridLayoutComposite(2, false, searchGroup.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true,
				false, SEARCH_COLUMN_COUNT, 1));
		errorComposite.addImage(CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_ERROR_SMALL),
				errorComposite.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.FILL));
		errorMessageLabel = errorComposite.addLabelBold("", errorComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, //$NON-NLS-1$
				true, false));
		errorMessageLabel.setForeground(CmClientResources.getColor(CmClientResources.COLOR_RED));
		errorComposite.getSwtComposite().setVisible(false);
		return errorComposite;
	}

	private boolean validateSku(final String productSkuCode) {
		ProductSkuLookup productSkuLookup = ServiceLocator.getService(
				ContextIdNames.PRODUCT_SKU_LOOKUP);
		productSKU = productSkuLookup.findBySkuCode(productSkuCode);

//		long warehouseUid = WarehousePerspectiveFactory.getCurrentWarehouse().getUidPk();
		boolean validate = false;
		if (productSKU == null) {
			errorMessageLabel.setText(WarehouseMessages.get().SearchViewInventoryError_InvalidSkuCode);
		} else if (productSKU.getProduct().getAvailabilityCriteria() == AvailabilityCriteria.ALWAYS_AVAILABLE) {
			errorMessageLabel.setText(WarehouseMessages.get().SearchViewInventoryError_SkuAlwaysAvailable);
		} else {
			validate = true;
		}
		errorComposite.getSwtComposite().setVisible(!validate);
		return validate;
	}

	@Override
	public void setFocus() {
		productSkuCodeText.setFocus();
	}

	@Override
	public void tabActivated() {
		setFocus();
	}

	@Override
	public void refresh() {
		// do nothing
	}
}
