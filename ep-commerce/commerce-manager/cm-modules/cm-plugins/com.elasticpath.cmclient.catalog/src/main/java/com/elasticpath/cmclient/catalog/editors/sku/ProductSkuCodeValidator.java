/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.catalog.editors.sku;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.catalog.CatalogPlugin;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.service.catalog.ProductSkuService;

/**
 * Validates product sku code input. The sku code should not exist in the database in order to pass the validation.
 */
public class ProductSkuCodeValidator implements IValidator {

	private final boolean checkCodeOnKeyStroke;

	private boolean validateSku;

	private final ProductSku productSku;

	/**
	 * Constructor.
	 *
	 * @param checkCodeOnKeyStroke check sku code on each key stroke flag
	 * @param productSku product sku
	 */
	public ProductSkuCodeValidator(final boolean checkCodeOnKeyStroke, final ProductSku productSku) {
		this.checkCodeOnKeyStroke = checkCodeOnKeyStroke;
		this.productSku = productSku;
	}

	@Override
	public IStatus validate(final Object value) {

			if (!isCheckCodeOnKeyStroke() && !isValidateSku()) {
				return Status.OK_STATUS;
			}

			final List<String> skuCodes = new LinkedList<>();
			skuCodes.add(value.toString());
			final ProductSkuService productSkuService = getProductSkuService();
			final List<String> existingSkuCodes = productSkuService.skuExists(skuCodes, productSku.getProduct().getUidPk());

			if (!existingSkuCodes.isEmpty()) {
				StringBuffer strBuffer = new StringBuffer();
				for (String skuCode : existingSkuCodes) {
					strBuffer = strBuffer.append(skuCode).append(", "); //$NON-NLS-1$
				}
				strBuffer = strBuffer.delete(strBuffer.length() - 2, strBuffer.length());
				return new Status(IStatus.ERROR, CatalogPlugin.PLUGIN_ID, IStatus.ERROR,
					NLS.bind(CatalogMessages.get().ProductEditor_Save_Error_SkuExists,
					strBuffer), null);
			}

			// only check this once (on the save)
			setValidateSku(false);
			return Status.OK_STATUS;
			}

	/**
	 * @return product sku service
	 */
	protected ProductSkuService getProductSkuService() {
		return (ProductSkuService) ServiceLocator.getService(ContextIdNames.PRODUCT_SKU_SERVICE);
	}
	
	/**
	 * @return validate SKU
	 */
	public boolean isValidateSku() {
		return validateSku;
	}
	
	/**
	 * 
	 * @return true if need to check sku code during user input.
	 */
	public boolean isCheckCodeOnKeyStroke() {
		return checkCodeOnKeyStroke;
	}	
	
	/**
	 * Sets the validate sku.
	 * @param validateSku validate sku to set
	 */
	public void setValidateSku(final boolean validateSku) {
		this.validateSku = validateSku;
	}		
}