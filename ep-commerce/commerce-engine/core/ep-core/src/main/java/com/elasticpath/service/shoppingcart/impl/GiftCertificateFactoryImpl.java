/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
/**
 * 
 */
package com.elasticpath.service.shoppingcart.impl;

import java.util.Currency;
import java.util.Date;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.util.GiftCertificateCodeGenerator;
import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.ShoppingItemPricingSnapshot;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.catalog.GiftCertificateService;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.service.shoppingcart.GiftCertificateFactory;

/**
 * Creates GiftCertificates. 
 * This implementation uses the GiftCertificateCodeGenerator to generate unique codes.
 */
public class GiftCertificateFactoryImpl implements GiftCertificateFactory {

	private BeanFactory beanFactory;
	private GiftCertificateService giftCertificateService;
	private GiftCertificateCodeGenerator giftCertificateCodeGenerator;
	private ProductSkuLookup productSkuLookup;

	@Override
	public GiftCertificate createGiftCertificate(final ShoppingItem shoppingItem,
													final ShoppingItemPricingSnapshot pricingSnapshot,
													final Customer customer,
													final Store store,
													final Currency currency) {
		final ProductSku giftCertificateSku = getProductSkuLookup().findByGuid(shoppingItem.getSkuGuid());

		final GiftCertificate giftCertificate = getBeanFactory().getBean(ContextIdNames.GIFT_CERTIFICATE);
		giftCertificate.setGiftCertificateCode(generateUniqueGiftCertificateCode(store.getUidPk()));
		giftCertificate.setPurchaser(customer);
		giftCertificate.setCreationDate(new Date());
		giftCertificate.setCurrencyCode(currency.getCurrencyCode());
		giftCertificate.setPurchaseAmount(pricingSnapshot.getPriceCalc().withCartDiscounts().getAmount());
		giftCertificate.setRecipientEmail(shoppingItem.getFieldValue(GiftCertificate.KEY_RECIPIENT_EMAIL));
		giftCertificate.setRecipientName(shoppingItem.getFieldValue(GiftCertificate.KEY_RECIPIENT_NAME));
		giftCertificate.setSenderName(shoppingItem.getFieldValue(GiftCertificate.KEY_SENDER_NAME));
		giftCertificate.setStore(store);
		giftCertificate.setTheme(giftCertificateSku.getSkuCode());
		giftCertificate.setMessage(shoppingItem.getFieldValue(GiftCertificate.KEY_MESSAGE));

		return giftCertificate;
	}

	/**
	 * Generates a gift certificate code that is guaranteed to be unique within the {@code Store} having
	 * the given UIDPK.
	 * @param storeUidPk the store's UID
	 * @return the unique code
	 */
	String generateUniqueGiftCertificateCode(final long storeUidPk) {
		String giftCertificateCode = generateCode();
		while (getGiftCertificateService().isGiftCertificateCodeExist(giftCertificateCode, storeUidPk)) {
			giftCertificateCode = generateCode();
		}
		return giftCertificateCode;
	}
	
	private String generateCode() {
		return getGiftCertificateCodeGenerator().generateCode();
	}

	/**
	 * @param beanFactory the beanFactory to set
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * @return the beanFactory
	 */
	public BeanFactory getBeanFactory() {
		return beanFactory;
	}
	
	/**
	 * @param giftCertificateCodeGenerator the giftCertificateCodeGenerator to set
	 */
	public void setGiftCertificateCodeGenerator(final GiftCertificateCodeGenerator giftCertificateCodeGenerator) {
		this.giftCertificateCodeGenerator = giftCertificateCodeGenerator;
	}

	/**
	 * @return the giftCertificateCodeGenerator
	 */
	public GiftCertificateCodeGenerator getGiftCertificateCodeGenerator() {
		return giftCertificateCodeGenerator;
	}

	/**
	 * @param giftCertificateService the giftCertificateService to set
	 */
	public void setGiftCertificateService(final GiftCertificateService giftCertificateService) {
		this.giftCertificateService = giftCertificateService;
	}

	/**
	 * @return the giftCertificateService
	 */
	public GiftCertificateService getGiftCertificateService() {
		return giftCertificateService;
	}

	protected ProductSkuLookup getProductSkuLookup() {
		return productSkuLookup;
	}

	public void setProductSkuLookup(final ProductSkuLookup productSkuLookup) {
		this.productSkuLookup = productSkuLookup;
	}
}
