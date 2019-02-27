/**
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shoppingcart.validation.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.common.pricing.service.PriceLookupFacade;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.ConstituentItem;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.service.shoppingcart.validation.ProductSkuValidationContext;
import com.elasticpath.service.shoppingcart.validation.ProductSkuValidator;

/**
 * Delegates the validation of auto-selectable bundle constituents.
 */
public class AutoSelectableBundleConstituentDelegateValidatorImpl
	extends AbstractAggregateValidator<ProductSkuValidationContext, ProductSkuValidationContext>
		implements ProductSkuValidator {

	private BeanFactory beanFactory;
	private PriceLookupFacade priceLookupFacade;

	@Override
	public Collection<StructuredErrorMessage> validate(final ProductSkuValidationContext context) {
		return getFilteredConstituents(context).stream()
				.map(item -> buildConstituentContext(context, item))
				.map(super::validate)
				.flatMap(Collection::stream)
				.collect(Collectors.toList());
	}

	private Collection<ConstituentItem> getFilteredConstituents(final ProductSkuValidationContext context) {
		if (context.getProductSku().getProduct() instanceof ProductBundle) {
			ProductBundle bundle = (ProductBundle) context.getProductSku().getProduct();

			return bundle.getConstituents().stream()
					.filter(bundle::isConstituentAutoSelectable)
					.map(BundleConstituent::getConstituent)
					.collect(Collectors.toList());
		}
		return Collections.emptyList();
	}

	/**
	 * Creates a new validation context for the validation of constituent.
	 * @param context the original context.
	 * @param item the constituent item.
	 * @return the new validation context.
	 */
	protected ProductSkuValidationContext buildConstituentContext(final ProductSkuValidationContext context, final ConstituentItem item) {
		ProductSkuValidationContext constituentContext = beanFactory.getBean(ContextIdNames.PRODUCT_SKU_VALIDATION_CONTEXT);
		constituentContext.setProductSku(item.getProductSku());
		constituentContext.setParentProductSku(context.getProductSku());
		constituentContext.setShopper(context.getShopper());
		constituentContext.setStore(context.getStore());
		constituentContext.setPromotedPrice(priceLookupFacade.getPromotedPriceForSku(item.getProductSku(), context.getStore(), context.getShopper()));
		return constituentContext;
	}

	protected BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	protected PriceLookupFacade getPriceLookupFacade() {
		return priceLookupFacade;
	}

	public void setPriceLookupFacade(final PriceLookupFacade priceLookupFacade) {
		this.priceLookupFacade = priceLookupFacade;
	}
}
