/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.service.shoppingcart.actions.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.Currency;
import java.util.HashSet;
import java.util.Locale;
import java.util.UUID;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.catalog.impl.GiftCertificateImpl;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.catalog.impl.ProductTypeImpl;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.customer.impl.CustomerSessionImpl;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.impl.OrderSkuImpl;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItemPricingSnapshot;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.catalog.GiftCertificateService;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.service.shoppingcart.GiftCertificateFactory;
import com.elasticpath.service.shoppingcart.PricingSnapshotService;
import com.elasticpath.service.shoppingcart.actions.CheckoutActionContext;

/**
 * Test class for {@link CreateGiftCertificatesCheckoutAction}.
 */
public class CreateGiftCertificatesCheckoutActionTest {

	private static final Currency CURRENCY = Currency.getInstance(Locale.CANADA);

	private CreateGiftCertificatesCheckoutAction checkoutAction;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private final GiftCertificateFactory giftCertificateFactory = context.mock(GiftCertificateFactory.class);
	private final GiftCertificateService giftCertificateService = context.mock(GiftCertificateService.class);
	private final ProductSkuLookup productSkuLookup = context.mock(ProductSkuLookup.class);
	private final PricingSnapshotService pricingSnapshotService = context.mock(PricingSnapshotService.class);

	private final Customer customer = context.mock(Customer.class);
	private final Order order = context.mock(Order.class);
	private final CustomerSession customerSession = new CustomerSessionImpl();
	private final ShoppingCart shoppingCart = context.mock(ShoppingCart.class);
	private final Store store = context.mock(Store.class);

	private CheckoutActionContext checkoutContext;

	@Before
	public void setUp() {
		checkoutAction = new CreateGiftCertificatesCheckoutAction();
		checkoutAction.setGiftCertificateFactory(giftCertificateFactory);
		checkoutAction.setGiftCertificateService(giftCertificateService);
		checkoutAction.setProductSkuLookup(productSkuLookup);
		checkoutAction.setPricingSnapshotService(pricingSnapshotService);

		checkoutContext = new CheckoutActionContextImpl(shoppingCart, null, customerSession, null, false, false, null);
		checkoutContext.setOrder(order);

		context.checking(new Expectations() {
			{
				allowing(order).getGuid();
				will(returnValue("100000"));

				allowing(order).getCurrency();
				will(returnValue(CURRENCY));

				allowing(shoppingCart).getStore();
				will(returnValue(store));

				final Shopper shopper = context.mock(Shopper.class);

				allowing(shoppingCart).getShopper();
				will(returnValue(shopper));

				allowing(shopper).getCustomer();
				will(returnValue(customer));

				allowing(customer).getEmail();
				will(returnValue("sender@domain.com"));
			}
		});
	}

	@Test
	public void testExecuteAddsGiftCertificateCodeToShoppingItemData() throws Exception {
		final String giftCertificate1Guid = "giftCertificateSku1";
		final String giftCertificate2Guid = "giftCertificateSku2";

		final OrderSku giftCertificateOrderSku1 = createOrderSkuWithGuid("sku1", GiftCertificate.KEY_PRODUCT_TYPE);
		final OrderSku giftCertificateOrderSku2 = createOrderSkuWithGuid("sku2", GiftCertificate.KEY_PRODUCT_TYPE);
		final OrderSku nonGiftCertificateOrderSku = createOrderSkuWithGuid("nonGiftCertificateSku", "NonGiftCertificate");

		final OrderShipment shipment = context.mock(OrderShipment.class);

		givenOrderContainsOrderShipment(order, shipment);
		givenOrderShipmentContainsOrderSkus(shipment, giftCertificateOrderSku1, giftCertificateOrderSku2, nonGiftCertificateOrderSku);

		expectOrderSkuGeneratesGiftCertificate(giftCertificateOrderSku1, giftCertificate1Guid, "GIFTCERT001", order.getCurrency());
		expectOrderSkuGeneratesGiftCertificate(giftCertificateOrderSku2, giftCertificate2Guid, "GIFTCERT002", order.getCurrency());

		checkoutAction.execute(checkoutContext);

		assertEquals("Unexpected gift certificate GUID", giftCertificate1Guid, giftCertificateOrderSku1.getFieldValue(GiftCertificate.KEY_GUID));
		assertEquals("Unexpected gift certificate GUID", giftCertificate2Guid, giftCertificateOrderSku2.getFieldValue(GiftCertificate.KEY_GUID));
		assertNull("Non-Gift-Certificate OrderSkus should not contain a gift certificate GUID",
				nonGiftCertificateOrderSku.getFieldValue(GiftCertificate.KEY_GUID));
	}

	private OrderSku createOrderSkuWithGuid(final String orderSkuGuid, final String productTypeName) {
		final ProductType productType = new ProductTypeImpl();
		productType.setName(productTypeName);

		final Product product = new ProductImpl();
		product.setProductType(productType);

		final ProductSku productSku = new ProductSkuImpl();
		productSku.initialize();
		productSku.setProduct(product);

		final OrderSku orderSku = new OrderSkuImpl();
		orderSku.setGuid(orderSkuGuid);
		orderSku.setSkuGuid(productSku.getGuid());

		context.checking(new Expectations() {
			{
				allowing(productSkuLookup).findByGuid(productSku.getGuid());
				will(returnValue(productSku));
			}
		});

		return orderSku;
	}

	private void givenOrderShipmentContainsOrderSkus(final OrderShipment shipment, final OrderSku... giftCertificateOrderSkus) {
		context.checking(new Expectations() {
			{
				allowing(shipment).getShipmentOrderSkus();
				will(returnValue(new HashSet<>(Arrays.asList(giftCertificateOrderSkus))));
			}
		});
	}

	private void givenOrderContainsOrderShipment(final Order order, final OrderShipment... shipments) {
		context.checking(new Expectations() {
			{
				allowing(order).getAllShipments();
				will(returnValue(Arrays.asList(shipments)));
			}
		});
	}

	private void expectOrderSkuGeneratesGiftCertificate(final OrderSku giftCertificateOrderSku, final String giftCertificateGuid,
														final String giftCertificateCode, final Currency currency) {
		final GiftCertificate giftCertificate = new GiftCertificateImpl();
		giftCertificate.setGuid(giftCertificateGuid);
		giftCertificate.setGiftCertificateCode(giftCertificateCode);
		giftCertificate.setPurchaser(customer);

		context.checking(new Expectations() {
			{
				final ShoppingItemPricingSnapshot shoppingItemPricingSnapshot =
						context.mock(ShoppingItemPricingSnapshot.class, "Snapshot-" + UUID.randomUUID());

				oneOf(pricingSnapshotService).getPricingSnapshotForOrderSku(giftCertificateOrderSku);
				will(returnValue(shoppingItemPricingSnapshot));

				oneOf(giftCertificateFactory).createGiftCertificate(giftCertificateOrderSku, shoppingItemPricingSnapshot, customer, store, currency);
				will(returnValue(giftCertificate));

				allowing(giftCertificateService).add(giftCertificate);
				will(returnValue(giftCertificate));
			}
		});
	}

}
