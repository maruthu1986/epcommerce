/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.catalog.wizards.product.create;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;

import com.elasticpath.cmclient.pricelistmanager.controller.impl.PriceListEditorControllerImpl;
import com.elasticpath.cmclient.pricelistmanager.controller.listeners.PriceListModelChangedListener;
import com.elasticpath.common.dto.pricing.BaseAmountDTO;
import com.elasticpath.common.pricing.service.BaseAmountFilterExt;
import com.elasticpath.domain.catalog.Product;

/**
 * A pricing editor controller used when the product we are creating prices for doesn't yet exist
 * in the system.
 */
public class NewProductPriceListEditorControllerImpl extends PriceListEditorControllerImpl {

	private final List<PriceListModelChangedListener> modelChangeListeners = new ArrayList<>();

	/** If we create a product we should pass it here because it can't be read from DB obviously. **/
	private Product managedProduct;
	
	/**
	 * Creates an instance to manage prices for a new product.
	 */
	public NewProductPriceListEditorControllerImpl() {
		// New product doesn't have a guid yet.
		super(StringUtils.EMPTY);
	}
	
	@Override
	public void deleteBaseAmountDTO(final BaseAmountDTO baseAmountDTO) {
		super.deleteBaseAmountDTO(baseAmountDTO);
		fireModelChangedEvent();
	}

	@Override
	public void addBaseAmountDTO(final BaseAmountDTO newBaseAmountDTO) {
		super.addBaseAmountDTO(newBaseAmountDTO);
		newBaseAmountDTO.setObjectGuid(managedProduct.getCode());
		fireModelChangedEvent();
	}

	@Override
	public void updateBaseAmountDTO(final BaseAmountDTO oldBaseAmountDTO, final BaseAmountDTO newBaseAmountDTO) {
		super.updateBaseAmountDTO(oldBaseAmountDTO, newBaseAmountDTO);
		fireModelChangedEvent();
	}

	/**
	 * @param listener listener
	 */
	public void addModelChangedListener(final PriceListModelChangedListener listener) {
		modelChangeListeners.add(listener);
	}


	private void fireModelChangedEvent() {
		for (PriceListModelChangedListener listener : modelChangeListeners) {
			listener.notifyPriceListModelChanged();
		}
	}
	
	@Override
	protected boolean serverSideDuplicatesExist(final BaseAmountDTO baseAmountDTO) {
		if (!isValidBaseAmountDTO(baseAmountDTO)) {
			return false;
		}
		final Collection<BaseAmountDTO> alreadyPersistent = getPersistentBaseAmounts(baseAmountDTO);

		if (baseAmountDTO.getListValue() != null) { // optimisation switch to prevent unnecessary server calls
			// New base amount dtos created in a create product wizard (for example) have no persistent object guid (yet)
			// so look for duplicates against the managed product.
			if (managedProductUnsaved()) {
				retainBaseAmountsWithProductGuid(alreadyPersistent, managedProduct.getCode());
			}

			return isNotSameFirstElement(baseAmountDTO, alreadyPersistent);
		}
		return false;
	}
	

	private boolean managedProductUnsaved() {
		return managedProduct != null;
	}
	
	private void retainBaseAmountsWithProductGuid(final Collection<BaseAmountDTO> alreadyPersistent, final String code) {
		Predicate hasSameGuid = obj -> code.equals(((BaseAmountDTO) obj).getObjectGuid());
		CollectionUtils.filter(alreadyPersistent, hasSameGuid);
	}

	/**
	 *
	 * @param productCodeWasEdited productCodeWasEdited
	 */
	public void reloadBaseAmountsForManagedProduct(final boolean productCodeWasEdited) {
		if (managedProductUnsaved()) {						
			getModel().getRawBaseAmounts().clear();
			BaseAmountFilterExt filter = getBaseAmountsFilter();
			filter.setObjectGuid(managedProduct.getGuid());
			Collection<BaseAmountDTO> dtos = this.getPriceListService().getBaseAmountsExt(filter, true);			
			getModel().getRawBaseAmounts().addAll(dtos);
			if (productCodeWasEdited && !dtos.isEmpty()) { //if product code was changed and we have prices to preload from DB
																					//reset all user entered prices
				getModel().getChangeSet().getRemovalList().clear();
				getModel().getChangeSet().getAdditionList().clear();
				getModel().getChangeSet().getUpdateList().clear();
				return;
			}
			if (productCodeWasEdited) { //need to update all base amounts with a new object guid
				updateBaseAmountsObjectsGuids(getModel().getChangeSet().getRemovalList());
				updateBaseAmountsObjectsGuids(getModel().getChangeSet().getAdditionList());
				updateBaseAmountsObjectsGuids(getModel().getChangeSet().getUpdateList());
			}
		}
	}

	private void updateBaseAmountsObjectsGuids(final List<BaseAmountDTO> dtoList) {
		for (BaseAmountDTO baDto : dtoList) {
			baDto.setObjectGuid(managedProduct.getCode());
		}
		
	}

	public void setManagedProduct(final Product managedProduct) {
		this.managedProduct = managedProduct;
	}

	public Product getManagedProduct() {
		return managedProduct;
	}
	
	@Override
	protected Product getNewlyCreatedProduct() {
		return getManagedProduct();
	}
}
