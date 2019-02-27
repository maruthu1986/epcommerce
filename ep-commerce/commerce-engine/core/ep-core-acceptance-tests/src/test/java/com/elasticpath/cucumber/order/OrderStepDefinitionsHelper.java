/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.cucumber.order;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.cucumber.CucumberConstants;
import com.elasticpath.cucumber.ScenarioContextValueHolder;
import com.elasticpath.cucumber.shoppingcart.ShoppingCartStepDefinitionsHelper;
import com.elasticpath.domain.RecalculableObject;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.PriceTier;
import com.elasticpath.domain.customer.CustomerAddress;
import com.elasticpath.domain.event.EventOriginatorHelper;
import com.elasticpath.domain.order.ElectronicOrderShipment;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderAddress;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.OrderShipmentStatus;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.OrderStatus;
import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.domain.order.impl.PhysicalOrderShipmentImpl;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingCartTaxSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.ShoppingItemPricingSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingItemTaxSnapshot;
import com.elasticpath.sellingchannel.director.CartDirector;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.service.order.OrderService;
import com.elasticpath.service.shipping.transformers.PricedShippableItemContainerFromOrderShipmentTransformer;
import com.elasticpath.service.shoppingcart.OrderSkuFactory;
import com.elasticpath.service.shoppingcart.PricingSnapshotService;
import com.elasticpath.service.shoppingcart.TaxSnapshotService;
import com.elasticpath.service.tax.TaxDocumentModificationContext;
import com.elasticpath.service.tax.TaxDocumentModificationType;
import com.elasticpath.shipping.connectivity.dto.PricedShippableItem;
import com.elasticpath.shipping.connectivity.dto.PricedShippableItemContainer;
import com.elasticpath.shipping.connectivity.dto.ShippingCalculationResult;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;
import com.elasticpath.shipping.connectivity.service.ShippingCalculationService;

/**
 * Help class for {@link OrderStepDefinitions}.
 */
public class OrderStepDefinitionsHelper {

	@Inject
	@Named("orderHolder")
	private ScenarioContextValueHolder<Order> orderHolder;

	@Autowired
	private ShoppingCartStepDefinitionsHelper shoppingCartStepDefinitionsHelper;

	@Autowired
	private OrderService orderService;

	@Autowired
	private OrderSkuFactory orderSkuFactory;

	@Autowired
	@Qualifier("coreBeanFactory")
	private BeanFactory coreBeanFactory;

	@Autowired
	private ShippingCalculationService shippingCalculationService;

	@Autowired
	private CartDirector cartDirector;

	@Autowired
	private ProductSkuLookup productSkuLookup;

	@Autowired
	private PricingSnapshotService pricingSnapshotService;

	@Autowired
	private TaxSnapshotService taxSnapshotService;

	@Autowired
	private PricedShippableItemContainerFromOrderShipmentTransformer<PricedShippableItem> pricedShippableItemContainerTransformer;

	/**
	 * Releases order physical shipments, and complete order of the current test context.
	 */
	public void completeOrder() {

		Order order = orderHolder.get();
		Order completedOrder = null;

		for (OrderShipment orderShipment : order.getPhysicalShipments()) {
			orderShipment = orderService.processReleaseShipment(orderShipment);
			String shipmentNumber = orderShipment.getShipmentNumber();

			completedOrder = orderService.completeShipment(shipmentNumber, "trackingNumber" + shipmentNumber,
					true, null, false, getEventOriginatorHelper().getSystemOriginator());
		}
		assertEquals(OrderStatus.COMPLETED, completedOrder.getStatus());
		orderHolder.set(completedOrder);
	}

	/**
	 * Cancels  the order physical shipments of the current test context.
	 */
	public void cancelOrderShipments() {

		Order order = orderHolder.get();

		for (PhysicalOrderShipment phShipment : order.getPhysicalShipments()) {
			phShipment = orderService.cancelOrderShipment(phShipment);
		}

		orderHolder.set(order);
	}

	/**
	 * Cancels  the order.
	 */
	public void cancelOrder() {

		orderHolder.set(orderService.cancelOrder(orderHolder.get()));
	}

	/**
	 * Adds items to a physical order shipment.
	 *
	 * @param itemDtos the shopping item dtos
	 */
	public void addItemsToPhysicalShipment(final List<ShoppingItemDto> itemDtos) {

		Order order = orderHolder.get();
		List<ShoppingItem> shoppingItems = new ArrayList<>();

		final ShoppingCart shoppingCart = shoppingCartStepDefinitionsHelper.getEmptyShoppingCart();
		for (ShoppingItemDto shoppingItemDto : itemDtos) {
			shoppingItems.add(cartDirector.addItemToCart(shoppingCart, shoppingItemDto));
		}

		final ShoppingCartPricingSnapshot pricingSnapshot = pricingSnapshotService.getPricingSnapshotForCart(shoppingCart);
		final ShoppingCartTaxSnapshot taxSnapshot = taxSnapshotService.getTaxSnapshotForCart(shoppingCart, pricingSnapshot);

		Collection<OrderSku> orderSkus = orderSkuFactory.createOrderSkus(shoppingItems, taxSnapshot, order.getLocale());

		PhysicalOrderShipment phShipment = order.getPhysicalShipments().get(0);

		for (OrderSku orderSku : orderSkus) {
			phShipment.addShipmentOrderSku(orderSku);
		}

		updateOrder(order, phShipment);
	}

	/**
	 * Removes items from a physical order shipment.
	 *
	 * @param itemDtos the shopping item dtos
	 */
	public void removeItemsFromPhysicalShipment(final List<ShoppingItemDto> itemDtos) {

		Order order = orderHolder.get();

		PhysicalOrderShipment phShipment = order.getPhysicalShipments().get(0);
		List<OrderSku> removedOrderSkus = new ArrayList<>();

		for (ShoppingItemDto dto : itemDtos) {
			String skuCode = dto.getSkuCode();

			for (OrderSku orderSku : phShipment.getShipmentOrderSkus()) {
				if (StringUtils.equals(orderSku.getSkuCode(), skuCode)) {
					removedOrderSkus.add(orderSku);
				}
			}
		}

		for (OrderSku orderSku : removedOrderSkus) {
			phShipment.removeShipmentOrderSku(orderSku, productSkuLookup);
		}

		updateOrder(order, phShipment);
	}

	/**
	 * Changes item quantities of a physical order shipment.
	 *
	 * @param itemDtos the shopping item dtos
	 */
	public void changeItemQuantitiesOfPhysicalShipment(final List<ShoppingItemDto> itemDtos) {

		Order order = orderHolder.get();

		PhysicalOrderShipment phShipment = order.getPhysicalShipments().get(0);

		for (ShoppingItemDto dto : itemDtos) {
			String skuCode = dto.getSkuCode();

			for (OrderSku orderSku : phShipment.getShipmentOrderSkus()) {
				if (StringUtils.equals(orderSku.getSkuCode(), skuCode)) {
					orderSku.setQuantity(dto.getQuantity());
				}
			}
		}

		updateOrder(order, phShipment);

	}

	/**
	 * Changes item prices of a physical order shipment.
	 *
	 * @param itemProperties data of items
	 */
	public void changeItemPricesOfPhysicalShipment(final List<Map<String, String>> itemProperties) {

		Order order = orderHolder.get();
		PhysicalOrderShipment phShipment = order.getPhysicalShipments().get(0);

		for (Map<String, String> properties : itemProperties) {

			String skuCode = properties.get(CucumberConstants.FIELD_SKU_CODE);
			BigDecimal newPrice = new BigDecimal(properties.get(CucumberConstants.FIELD_PRICE));

			for (OrderSku orderSku : phShipment.getShipmentOrderSkus()) {
				if (StringUtils.equals(orderSku.getSkuCode(), skuCode)) {
					updatePrice(orderSku, newPrice);
				}
			}
		}

		updateOrder(order, phShipment);
	}

	/**
	 * Changes item discounts of a physical order shipment.
	 *
	 * @param itemProperties data of items
	 */
	public void changeItemDiscountsOfPhysicalShipment(final List<Map<String, String>> itemProperties) {

		Order order = orderHolder.get();
		PhysicalOrderShipment phShipment = order.getPhysicalShipments().get(0);

		for (Map<String, String> properties : itemProperties) {

			String skuCode = properties.get(CucumberConstants.FIELD_SKU_CODE);
			BigDecimal newDiscount = new BigDecimal(properties.get(CucumberConstants.FIELD_DISCOUNT));

			for (OrderSku orderSku : phShipment.getShipmentOrderSkus()) {
				if (StringUtils.equals(orderSku.getSkuCode(), skuCode)) {
					((RecalculableObject) orderSku).enableRecalculation();
					orderSku.setDiscountBigDecimal(newDiscount);
				}
			}
		}

		updateOrder(order, phShipment);

	}

	/**
	 * Changes shipping address of a physical order shipment.
	 *
	 * @param customerAddress the shipping address
	 */
	public void changeShippingAddressOfPhysicalShipment(final CustomerAddress customerAddress) {

		if (customerAddress == null) {
			return;
		}

		Order order = orderHolder.get();
		PhysicalOrderShipment phShipment = order.getPhysicalShipments().get(0);

		final OrderAddress newShipmentAddress = coreBeanFactory.getBean(ContextIdNames.ORDER_ADDRESS);
		newShipmentAddress.init(customerAddress);
		phShipment.setShipmentAddress(newShipmentAddress);

		updateOrder(order, phShipment);
	}

	/**
	 * Changes delivery option of a physical order shipment.
	 *
	 * @param deliveryOption the delivery option
	 */
	@SuppressWarnings("unchecked")
	public void changeShippingMethodOfOrderPhysicalShipment(final String deliveryOption) {

		final Order order = orderHolder.get();
		final PhysicalOrderShipment phShipment = order.getPhysicalShipments().get(0);

		final PricedShippableItemContainer<PricedShippableItem> pricedShippableItemContainer
				= pricedShippableItemContainerTransformer.apply(phShipment);

		final ShippingCalculationResult shippingOptionResult = shippingCalculationService.getPricedShippingOptions(pricedShippableItemContainer);

		if (!shippingOptionResult.isSuccessful()) {
			throw new EpServiceException("Unable to get available shipping options so cannot change shipping option on order.");
		}

		final List<ShippingOption> options = shippingOptionResult.getAvailableShippingOptions();
		options.stream()
				.filter(opt -> opt.getCode().equals(deliveryOption))
				.findFirst()
				.ifPresent(opt -> populateShippingOption(phShipment, opt, order.getLocale()));

		updateOrder(order, phShipment);
	}

	/**
	 * Populates {@link PhysicalOrderShipment} with shippingOptionCode,shippingOptionName and ShippingCost.
	 *
	 * @param shipment       the shipment need to be populated.
	 * @param shippingOption the shipping option contains shipping option info.
	 * @param locale         the locale.
	 */
	private void populateShippingOption(final PhysicalOrderShipment shipment, final ShippingOption shippingOption, final Locale locale) {
		shipment.setShippingOptionCode(shippingOption.getCode());
		shipment.setShippingOptionName(shippingOption.getDisplayName(locale).orElse(null));
		shippingOption.getShippingCost().ifPresent(shippingCost -> shipment.setShippingCost(shippingCost.getAmount()));
	}

	/**
	 * Moves items from an existing physical shipment to a new physical shipment.
	 *
	 * @param itemDtos the shopping item dtos
	 */
	public void moveItemsToNewShipmentFromOrderPhysicalShipment(final List<ShoppingItemDto> itemDtos) {

		Order order = orderHolder.get();
		PhysicalOrderShipment phShipment = order.getPhysicalShipments().get(0);

		// create new order shipment
		PhysicalOrderShipmentImpl newPhysicalShipment = new PhysicalOrderShipmentImpl();
		newPhysicalShipment.enableRecalculation();
		newPhysicalShipment.setCreatedDate(new Date());
		newPhysicalShipment.setLastModifiedDate(new Date());
		newPhysicalShipment.setOrder(order);
		newPhysicalShipment.setStatus(OrderShipmentStatus.INVENTORY_ASSIGNED);
		newPhysicalShipment.initialize();

		for (ShoppingItemDto dto : itemDtos) {
			String skuCode = dto.getSkuCode();

			for (OrderSku orderSku : phShipment.getShipmentOrderSkus()) {
				if (StringUtils.equals(orderSku.getSkuCode(), skuCode)) {
					OrderSku movedOrderSku = coreBeanFactory.getBean(ContextIdNames.ORDER_SKU);
					final ShoppingItemPricingSnapshot pricingSnapshot = pricingSnapshotService.getPricingSnapshotForOrderSku(orderSku);
					final ShoppingItemTaxSnapshot taxSnapshot = taxSnapshotService.getTaxSnapshotForOrderSku(orderSku, pricingSnapshot);
					movedOrderSku.copyFrom(orderSku, productSkuLookup, taxSnapshot, true);
					movedOrderSku.setQuantity(dto.getQuantity());
					newPhysicalShipment.addShipmentOrderSku(movedOrderSku);

					if (orderSku.getQuantity() - dto.getQuantity() <= 0) {
						phShipment.removeShipmentOrderSku(orderSku, productSkuLookup);
					} else {
						orderSku.setQuantity(orderSku.getQuantity() - dto.getQuantity());
					}
				}
			}
		}

		newPhysicalShipment.setShippingOptionCode(phShipment.getShippingOptionCode());
		newPhysicalShipment.setShippingCost(phShipment.getShippingCost());
		newPhysicalShipment.setShipmentAddress(phShipment.getShipmentAddress());

		// add new order shipment to the order
		order.addShipment(newPhysicalShipment);

		TaxDocumentModificationContext taxDocumentModificationContext = new TaxDocumentModificationContext();
		taxDocumentModificationContext.add(phShipment,
				buildOrderShipmentAddress(order).get(phShipment.getShipmentNumber()),
				TaxDocumentModificationType.UPDATE);

		taxDocumentModificationContext.add(newPhysicalShipment,
				null,
				TaxDocumentModificationType.NEW);

		updateOrderTaxes(order, taxDocumentModificationContext);
	}

	private void updateOrder(final Order order, final PhysicalOrderShipment phShipment) {
		TaxDocumentModificationContext taxDocumentModificationContext = new TaxDocumentModificationContext();
		taxDocumentModificationContext.add(phShipment,
				buildOrderShipmentAddress(order).get(phShipment.getShipmentNumber()),
				TaxDocumentModificationType.UPDATE);

		updateOrderTaxes(order, taxDocumentModificationContext);
	}

	private void updateOrderTaxes(final Order order, final TaxDocumentModificationContext taxDocumentModificationContext) {
		// the order shipment has new item added
		Order updatedOrder = orderService.update(order, taxDocumentModificationContext);
		updatedOrder.setModifiedBy(getEventOriginatorHelper().getSystemOriginator());

		orderHolder.set(updatedOrder);
	}

	/**
	 * Remembers the original order shipment tax address, in case any ordershipment changes, the tax can be recorded correctly.
	 */
	private Map<String, OrderAddress> buildOrderShipmentAddress(final Order order) {

		Map<String, OrderAddress> addresses = new HashMap<>();

		for (PhysicalOrderShipment orderShipment : order.getPhysicalShipments()) {
			addresses.put(orderShipment.getShipmentNumber(), orderShipment.getShipmentAddress());
		}

		for (ElectronicOrderShipment orderShipment : order.getElectronicShipments()) {
			addresses.put(orderShipment.getShipmentNumber(), order.getBillingAddress());
		}

		return addresses;
	}

	/**
	 * getEventOriginatorHelper.
	 *
	 * @return EventOriginatorHelper
	 */
	private EventOriginatorHelper getEventOriginatorHelper() {
		return coreBeanFactory.getBean(ContextIdNames.EVENT_ORIGINATOR_HELPER);
	}

	@SuppressWarnings("deprecation")
	private void updatePrice(final OrderSku orderSku, final BigDecimal invoicePrice) {

		PriceTier tier = coreBeanFactory.getBean(ContextIdNames.PRICE_TIER);
		Price price = coreBeanFactory.getBean(ContextIdNames.PRICE);

		price.setCurrency(orderSku.getCurrency());

		final ShoppingItemPricingSnapshot pricingSnapshot = pricingSnapshotService.getPricingSnapshotForOrderSku(orderSku);
		tier.setListPrice(pricingSnapshot.getListUnitPrice().getAmount());
		tier.setSalePrice(getSalePrice(pricingSnapshot));
		tier.setComputedPriceIfLower(invoicePrice);
		tier.setMinQty(0);
		price.addOrUpdatePriceTier(tier);
		orderSku.setPrice(orderSku.getQuantity(), price);

		final BigDecimal unitPrice = pricingSnapshot.getPriceCalc().forUnitPrice().getAmount();
		if (invoicePrice == null && unitPrice != null) {
			orderSku.setUnitPrice(unitPrice);
		} else {
			orderSku.setUnitPrice(invoicePrice);
		}
		((RecalculableObject) orderSku).enableRecalculation();
	}

	private BigDecimal getSalePrice(final ShoppingItemPricingSnapshot pricingSnapshot) {

		if (pricingSnapshot.getSaleUnitPrice() == null) {
			return null;
		}

		return pricingSnapshot.getSaleUnitPrice().getAmount();
	}

}
