/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.domain.builder.checkout;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.domain.catalog.AvailabilityCriteria;
import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerAddress;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.payment.PaymentGateway;
import com.elasticpath.domain.payment.impl.PaymentGatewayImpl;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.store.Store;
import com.elasticpath.sellingchannel.director.CartDirector;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.service.catalog.ProductSkuService;
import com.elasticpath.service.payment.PaymentGatewayService;
import com.elasticpath.service.store.StoreService;
import com.elasticpath.test.persister.TestDataPersisterFactory;
import com.elasticpath.test.persister.testscenarios.SimpleStoreScenario;
import com.elasticpath.test.util.Utils;

/**
 * Builder for shopping carts to facilitate testing of checkout.
 */
public class CheckoutTestCartBuilder {
	private static final String PAYMENT_GATEWAY_PLUGIN_TEST_DOUBLE = "paymentGatewayPluginTestDouble";
	private static final String PAYMENT_GATEWAY_EXTERNAL_AUTH_NULL = "paymentGatewayExternalAuthNull";
	private static final String PAYMENT_GATEWAY_NULL = "paymentGatewayNull";

	private Store store;

	private Customer customer;

	private CustomerAddress address;

	private CustomerSession customerSession;

	private SimpleStoreScenario scenario;

	private final List<ShoppingItemDto> shoppingItemDtos = new ArrayList<>();

	private final List<GiftCertificate> giftCertificates = new ArrayList<>();

	@Autowired
	private TestDataPersisterFactory persisterFactory;

	@Autowired
	private CartDirector cartDirector;

	@Autowired
	private PaymentGatewayService paymentGatewayService;

	@Autowired
	private StoreService storeService;

	@Autowired
	private ProductService productService;

	@Autowired
	private ProductSkuService productSkuService;

	/**
	 * Select the scenario to use with the builder.
	 *
	 * @param scenario the scenario
	 * @return the checkout test cart builder
	 */
	public CheckoutTestCartBuilder withScenario(final SimpleStoreScenario scenario) {
		this.scenario = scenario;
		store = scenario.getStore();

		address = persisterFactory.getStoreTestPersister().createCustomerAddress("Bond", "James", "1234 Pine Street", "", "Vancouver", "CA", "BC",
				"V6J5G4", "891312345007");

		return this;
	}

	/**
	 * Set the customer session.
	 * @param customerSession the session.
	 * @return the checkout test cart builder
	 */
	public CheckoutTestCartBuilder withCustomerSession(final CustomerSession customerSession) {
		this.customerSession = customerSession;
		return this;
	}

	/**
	 * Set customer.
	 *
	 * @param customer the customer.
	 * @return the checkout test cart builder
	 */
	public CheckoutTestCartBuilder withCustomer(final Customer customer) {
		this.customer = customer;
		return this;
	}

	/**
	 * Add a physical product to the cart.
	 *
	 * @return the checkout test cart builder
	 */
	public CheckoutTestCartBuilder withPhysicalProduct() {
		final Product physicalProduct = persisterFactory.getCatalogTestPersister().persistDefaultShippableProducts(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse()).get(0);
		return withProductSku(physicalProduct.getDefaultSku());
	}

	/**
	 * Add an electronic product to the cart.
	 *
	 * @return the checkout test cart builder
	 */
	public CheckoutTestCartBuilder withElectronicProduct() {
		final Product electronicProduct = persisterFactory.getCatalogTestPersister()
				.persistDefaultNonShippableProducts(scenario.getCatalog(),
													scenario.getCategory(),
													scenario.getWarehouse()).get(0);
		return withProductSku(electronicProduct.getDefaultSku());
	}

	/**
	 * Add a free electronic product to the cart.
	 *
	 * @return the checkout test cart builder
	 */
	public CheckoutTestCartBuilder withFreeElectronicProduct() {
		final Product freeElectronicProduct = persisterFactory.getCatalogTestPersister()
				.persistNonShippablePersistedProductWithSku(scenario.getCatalog(),
															scenario.getCategory(),
															scenario.getWarehouse(),
															BigDecimal.ZERO,
															"Free Electronic Product",
															"Free Electronic Product");
		return withProductSku(freeElectronicProduct.getDefaultSku());
	}

	/**
	 * Add a Gift Certificate product to the cart.
	 *
	 * @return the checkout test cart builder
	 */
	public CheckoutTestCartBuilder withGiftCertificateProduct() {
		return withGiftCertificateProduct(customer.getFullName(),
										  "Gift Certificate Recipient",
										  "giftcertificate.recipient." + Math.random() + "@elasticpath.com");
	}

	/**
	 * Add a Gift Certificate product to the cart.
	 *
	 * @param recipientEmailAddress the email address of the gift certificate recipient
	 * @return the checkout test cart builder
	 */
	public CheckoutTestCartBuilder withGiftCertificateProduct(final String senderName, final String recipientName,
															  final String recipientEmailAddress) {
		final Product giftCertificateProduct = persisterFactory.getCatalogTestPersister()
				.persistProductWithSku(scenario.getCatalog(),
									   scenario.getCategory(),
									   scenario.getWarehouse(),
									   "Gift Certificates",
									   BigDecimal.TEN,
									   store.getDefaultCurrency(),
									   "Store",
									   "giftCertificate_" + System.currentTimeMillis() + "_" + Math.random(),
									   "Gift Certificate",
									   "hummingbird_" + System.currentTimeMillis() + "_" + Math.random(),
									   "NONE",
									   null,
									   false,
									   AvailabilityCriteria.ALWAYS_AVAILABLE,
									   0,
									   false,
									   null,
									   -1,
									   -1
				);

		final Map<String, String> itemFields = new HashMap<>();

		itemFields.put(GiftCertificate.KEY_MESSAGE, "Please enjoy this testing Gift Certificate!");
		itemFields.put(GiftCertificate.KEY_RECIPIENT_EMAIL, recipientEmailAddress);
		itemFields.put(GiftCertificate.KEY_RECIPIENT_NAME, recipientName);
		itemFields.put(GiftCertificate.KEY_SENDER_NAME, senderName);

		return withProductSku(giftCertificateProduct.getDefaultSku(), itemFields);
	}

	/**
	 * Adds a product to the cart.  The product will be persisted if necessary.
	 *
	 * @param product the product to add to the cart
	 * @return the checkout test cart builder
	 */
	public CheckoutTestCartBuilder withProduct(final Product product) {
		final Product productToAddToCart;

		if (product.isPersisted()) {
			productToAddToCart = product;
		} else {
			productToAddToCart = productService.saveOrUpdate(product);
		}

		return withProductSku(productToAddToCart.getDefaultSku());
	}

	/**
	 * Adds a product SKU to the cart.  The SKU will be persisted if necessary.
	 *
	 * @param productSku the product sku to add to the cart
	 * @return the checkout test cart builder
	 */
	public CheckoutTestCartBuilder withProductSku(final ProductSku productSku) {
		return withProductSku(productSku, null);
	}

	/**
	 * Adds a product SKU to the cart, along with associated Shopping Item Fields.  The SKU will be persisted if necessary.
	 *
	 * @param productSku the product sku to add to the cart
	 * @param itemFields the shopping item fields
	 * @return the checkout test cart builder
	 */
	public CheckoutTestCartBuilder withProductSku(ProductSku productSku, final Map<String, String> itemFields) {
		final ProductSku productSkuToAddToCart;

		if (productSku.isPersisted()) {
			productSkuToAddToCart = productSku;
		} else {
			productSkuToAddToCart = productSkuService.saveOrUpdate(productSku);
		}

		final ShoppingItemDto shoppingItemDto = new ShoppingItemDto(productSkuToAddToCart.getSkuCode(), 1);

		if (itemFields != null) {
			shoppingItemDto.setItemFields(itemFields);
		}

		shoppingItemDtos.add(shoppingItemDto);

		return this;
	}

	/**
	 * Add a gift certificate with the specified amount.
	 *
	 * @param amount the amount
	 * @return the checkout test cart builder
	 */
	public CheckoutTestCartBuilder withGiftCertificateAmount(final BigDecimal amount) {
		final GiftCertificate certificate = persisterFactory.getGiftCertificateTestPersister().persistGiftCertificate(scenario.getStore(),
				"bigGiftCertificateGuid", "bigGiftCertificateCode",
				store.getDefaultCurrency().getCurrencyCode(), amount, "recipientName", "senderName", "theme",
				customer);
		giftCertificates.add(certificate);
		return this;
	}

	/**
	 * Use invalid token payment gateway for checkout.
	 *
	 * @return the checkout test cart builder
	 */
	public CheckoutTestCartBuilder withInvalidPaymentTokenGateway() {
		addStorePaymentGateway(createAndPersistInvalidPaymentGateway());
		return this;
	}

	/**
	 * Use test double token payment gateway for checkout.
	 *
	 * @return the checkout test cart builder
	 */
	public CheckoutTestCartBuilder withTestDoubleGateway() {
		addStorePaymentGateway(createAndPersistTestDoubleGateway());
		return this;
	}

	/**
	 * Use test double token payment gateway for checkout.
	 *
	 * @return the checkout test cart builder
	 */
	public CheckoutTestCartBuilder withTestExternalAuthGateway() {
		addStorePaymentGateway(createAndPersistTestExternalAuthGateway());
		return this;
	}

	/**
	 * Use test double token payment gateway for checkout.
	 *
	 * @return the checkout test cart builder
	 */
	public CheckoutTestCartBuilder withTestGateway() {
		addStorePaymentGateway(createAndPersistTestGateway());
		return this;
	}

	/**
	 * Use gift certificate gateway for checkout.
	 *
	 * @return the checkout test cart builder
	 */
	public CheckoutTestCartBuilder withGiftCertificateGateway() {
		final PaymentGateway giftCertificateGateway = persisterFactory.getStoreTestPersister().persistGiftCertificatePaymentGateway();
		addStorePaymentGateway(giftCertificateGateway);
		return this;
	}

	/**
	 * Use submitted gateway for checkout.
	 *
	 * @param paymentGateway the payment gateway
	 * @return the checkout test cart builder
	 */
	public CheckoutTestCartBuilder withGateway(final PaymentGateway paymentGateway) {
		addStorePaymentGateway(paymentGateway);
		return this;
	}

	/**
	 * Clear the payment gateways off the store.
	 */
	public CheckoutTestCartBuilder clearPaymentGateways() {
		store.getPaymentGateways().clear();
		store = storeService.saveOrUpdate(store);
		return this;
	}

	/**
	 * Builds the shopping cart.
	 *
	 * @return the shopping cart
	 */
	public ShoppingCart build() {
		final ShoppingCart shoppingCart = persisterFactory.getOrderTestPersister().persistEmptyShoppingCart(address, address, customerSession,
																											scenario.getShippingOption(), store);
		for (final ShoppingItemDto dto : shoppingItemDtos) {
			cartDirector.addItemToCart(shoppingCart, dto);
		}

		for (final GiftCertificate giftCertificate: giftCertificates) {
			shoppingCart.applyGiftCertificate(giftCertificate);
		}

		return shoppingCart;
	}

	private void addStorePaymentGateway(final PaymentGateway gateway) {
		
		// Multiple Payment Gateways with the same PaymentGatewayType are not supported, so we check for and remove the duplicate payment
		// gateway before adding the new one.
		final PaymentGateway existingGateway = store.getPaymentGatewayMap().get(gateway.getPaymentGatewayType());
		if (existingGateway != null) {
			store.getPaymentGateways().remove(existingGateway);
		}
		
		store.getPaymentGateways().add(gateway);
		store = storeService.saveOrUpdate(store);
	}

	private PaymentGateway createAndPersistTestDoubleGateway() {
		final PaymentGateway paymentGateway = new PaymentGatewayImpl();
		paymentGateway.setType(PAYMENT_GATEWAY_PLUGIN_TEST_DOUBLE);
		paymentGateway.setName(Utils.uniqueCode(PAYMENT_GATEWAY_PLUGIN_TEST_DOUBLE));
		return paymentGatewayService.saveOrUpdate(paymentGateway);
	}

	private PaymentGateway createAndPersistTestExternalAuthGateway() {
		PaymentGateway paymentGateway = new PaymentGatewayImpl();
		paymentGateway.setType(PAYMENT_GATEWAY_EXTERNAL_AUTH_NULL);
		paymentGateway.setName(Utils.uniqueCode(PAYMENT_GATEWAY_EXTERNAL_AUTH_NULL));
		return paymentGatewayService.saveOrUpdate(paymentGateway);
	}

	private PaymentGateway createAndPersistTestGateway() {
		PaymentGateway paymentGateway = new PaymentGatewayImpl();
		paymentGateway.setType(PAYMENT_GATEWAY_NULL);
		paymentGateway.setName(Utils.uniqueCode(PAYMENT_GATEWAY_NULL));
		return paymentGatewayService.saveOrUpdate(paymentGateway);
	}

	private PaymentGateway createAndPersistInvalidPaymentGateway() {
		final PaymentGateway paymentGateway = new PaymentGatewayImpl();
		paymentGateway.setType("paymentGatewayCyberSourceToken");
		paymentGateway.setName(Utils.uniqueCode("CybersourceTokenPaymentGateway"));
		return paymentGatewayService.saveOrUpdate(paymentGateway);
	}
}
