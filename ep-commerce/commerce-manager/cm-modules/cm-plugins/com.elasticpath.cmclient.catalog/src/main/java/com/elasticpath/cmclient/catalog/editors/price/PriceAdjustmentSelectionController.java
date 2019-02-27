/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.catalog.editors.price;

import java.beans.PropertyChangeEvent;
import java.util.Collection;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.elasticpath.cmclient.catalog.CatalogImageRegistry;
import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.catalog.editors.price.model.PriceAdjustmentModel;
import com.elasticpath.cmclient.catalog.editors.price.model.PriceAdjustmentModelRoot;
import com.elasticpath.cmclient.catalog.editors.price.model.PriceAdjustmentSummaryCalculator;
import com.elasticpath.cmclient.catalog.helpers.EventManager;
import com.elasticpath.domain.catalog.ProductBundle;

/**
 * A selection controller based on user selection and selection rule.
 */
public class PriceAdjustmentSelectionController {
	private final ProductBundle productBundle;

	/**
	 * Constructors.
	 *
	 * @param productBundle the controller for a specific bundle.
	 */
	public PriceAdjustmentSelectionController(final ProductBundle productBundle) {
		this.productBundle = productBundle;
	}

	private void deselectAll(final Collection<PriceAdjustmentModel> children) {
		for (PriceAdjustmentModel model : children) {
			model.setSelected(false);
			deselectAll(model.getChildren());
		}
	}

	/**
	 * Gets selection image based on the {@link PriceAdjustmentModel}.
	 *
	 * @param priceAdjustmentModel {@link PriceAdjustmentModel}.
	 * @return {@link Image}.
	 */
	public Image getSelectionImage(final PriceAdjustmentModel priceAdjustmentModel) {
		int selectionParameter = priceAdjustmentModel.getParent().getSelectionParameter();

		if (selectionParameter == 0) {
			return CatalogImageRegistry.getImage(CatalogImageRegistry.BUNDLE_ITEM_SELECTED);
		}

		boolean singleSelection = (selectionParameter == 1);
		boolean isSelected = priceAdjustmentModel.isSelected();

		if (singleSelection && isSelected) {
			return CatalogImageRegistry.getImage(CatalogImageRegistry.BUNDLE_ITEM_RADIO_ON);
		}

		if (singleSelection && !isSelected) {
			return CatalogImageRegistry.getImage(CatalogImageRegistry.BUNDLE_ITEM_RADIO_OFF);
		}

		if (!singleSelection && isSelected) {
			return CatalogImageRegistry.getImage(CatalogImageRegistry.BUNDLE_ITEM_CHECKBOX_ON);
		}

		if (!singleSelection && !isSelected) {
			return CatalogImageRegistry.getImage(CatalogImageRegistry.BUNDLE_ITEM_CHECKBOX_OFF);
		}

		return null;
	}

	private boolean isMultipleSelections(final PriceAdjustmentModel parent) {
		return parent.getSelectionParameter() > 1;
	}

	private boolean isSingleSelection(final PriceAdjustmentModel parent) {
		return parent.getSelectionParameter() == 1;
	}

	private boolean isValidSelection(final PriceAdjustmentModel model) {
		int alreadySelected = 0;
		for (PriceAdjustmentModel childModel : model.getParent().getChildren()) {
			if (childModel.isSelected()) {
				alreadySelected++;
			}
		}
		if (alreadySelected + 1 > model.getParent().getSelectionParameter() && !model.isSelected()) {
			MessageDialog.openWarning(Display.getDefault().getActiveShell(),
				CatalogMessages.get().ProductBundlePriceAdjustmentSelectionWarningDialogTitle,
					NLS.bind(CatalogMessages.get().ProductBundlePriceAdjustmentSelectionWarningMessage,
					alreadySelected));
			return false;
		}

		return true;
	}

	/**
	 * Selects a tree item and sets proper image.
	 * 
	 * @param treeItem {@link TreeItem}.
	 */
	public void select(final TreeItem treeItem) {
		PriceAdjustmentModel model = (PriceAdjustmentModel) treeItem.getData();
		PriceAdjustmentModel parent = model.getParent();

		if (!parent.isSelected()) {
			return;
		}

		if (isSingleSelection(parent)) {
			deselectAll(parent.getChildren());
		}

		if (isMultipleSelections(parent)) {
			if (isValidSelection(model)) {
				model.setSelected(!model.isSelected());
			}
		} else {
			model.setSelected(true);
		}
		setChildDefaultStates(model.getChildren(), model.getSelectionParameter(), model.isSelected());

		updateImages(treeItem);

		EventManager.getInstance().fireEvent(productBundle,
				new PropertyChangeEvent(this, PriceAdjustmentSummaryCalculator.PRICE_CHANGED_PROPERTY, null, treeItem.getParent().getData()));
	}

	private void setChildDefaultStates(final Collection<PriceAdjustmentModel> children, final int parentSelectionRule, final boolean parentSelected) {
		int selectedCount = 0;
		for (PriceAdjustmentModel model : children) {
			if (parentSelectionRule == 0) {
				model.setSelected(parentSelected);
				setChildDefaultStates(model.getChildren(), model.getSelectionParameter(), parentSelected);
			}

			if (parentSelectionRule == 1 && selectedCount < 1) {
				model.setSelected(parentSelected);
				setChildDefaultStates(model.getChildren(), model.getSelectionParameter(), parentSelected);
			}

			if (parentSelectionRule > 1 && selectedCount < parentSelectionRule) {
				model.setSelected(parentSelected);
				setChildDefaultStates(model.getChildren(), model.getSelectionParameter(), parentSelected);
			}

			selectedCount++;
		}
	}

	private void setImages(final TreeItem item) {
		PriceAdjustmentModel model = (PriceAdjustmentModel) item.getData();
		item.setImage(getSelectionImage(model));
		for (TreeItem child : item.getItems()) {
			setImages(child);
		}
	}

	/**
	 * Sets initial states for {@link PriceAdjustmentModelRoot}.
	 * 
	 * @param root {@link PriceAdjustmentModelRoot}
	 */
	public void setInitialStates(final PriceAdjustmentModelRoot root) {
		root.setSelected(true);
		setChildDefaultStates(root.getChildren(), root.getSelectionParameter(), true);
	}

	private void updateImages(final TreeItem treeItem) {
		Tree tree = treeItem.getParent();
		tree.setRedraw(false);
		for (TreeItem item : tree.getItems()) {
			setImages(item);
		}
		tree.setRedraw(true);
	}
}
