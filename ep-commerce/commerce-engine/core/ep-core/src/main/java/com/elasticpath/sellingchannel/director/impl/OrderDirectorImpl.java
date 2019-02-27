/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.sellingchannel.director.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import com.elasticpath.common.dto.OrderItemDto;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.inventory.InventoryDto;
import com.elasticpath.money.Money;
import com.elasticpath.sellingchannel.director.OrderDirector;
import com.elasticpath.sellingchannel.director.OrderItemAssembler;

/**
 * Default implementation of {@link OrderDirector}.
 */
public class OrderDirectorImpl implements OrderDirector {

	private OrderItemAssembler orderItemAssembler;

	@Override
	public List<OrderItemDto> createOrderItemDtoList(final OrderShipment shipment) {

		final List<OrderItemDto> orderItemDtoList = new ArrayList<>();
		final List<OrderSku> shipmentSkus = new ArrayList<>();

		for (final OrderSku orderSku : shipment.getShipmentOrderSkus()) {
			// is this a bundle constituent, or a regular product?
			final OrderSku rootBundleSku = orderSku.getRoot();
			if (rootBundleSku == null) {  // not a bundle constituent
				shipmentSkus.add(orderSku);
			} else if (!shipmentSkus.contains(rootBundleSku)) {
				shipmentSkus.add(rootBundleSku);
			}
		}

		for (final OrderSku orderSku : shipmentSkus) {
			final OrderItemDto orderItemDto = orderItemAssembler.createOrderItemDto(orderSku, shipment);
			orderItemDto.setItemFields(orderSku.getFields());
			orderItemDtoList.add(orderItemDto);
			setPricesAndInventories(orderItemDto);
		}

		return orderItemDtoList;
	}

	/**
	 *
	 * @param assembler The order item assembler.
	 */
	public void setOrderItemAssembler(final OrderItemAssembler assembler) {
		orderItemAssembler = assembler;
	}

	private void setPricesAndInventories(final OrderItemDto root) {
		if (root.getChildren().isEmpty()) {
			return;
		}

		// clear the price values
		final Currency currency = root.getUnitPrice().getCurrency();  // all the currencies better be the same!
		root.setListPrice(Money.valueOf(BigDecimal.ZERO, currency));
		root.setUnitPrice(Money.valueOf(BigDecimal.ZERO, currency));
		root.setTotal(Money.valueOf(BigDecimal.ZERO, currency));
		root.setDollarSavings(Money.valueOf(BigDecimal.ZERO, currency));

		// aggregate the children to sum their prices into the root
		aggregateChildPricesAndInventories(root, root);
		if (root.getQuantity() > 0) {
			final int scale = 10;
			final BigDecimal qty = BigDecimal.valueOf(root.getQuantity());
			BigDecimal tmpPrice = root.getListPrice().getRawAmount();
			BigDecimal price  = tmpPrice.divide(qty, scale, BigDecimal.ROUND_HALF_UP);
			root.setListPrice(Money.valueOf(price, currency));

			tmpPrice = root.getUnitPrice().getRawAmount();
			price  = tmpPrice.divide(qty, scale, BigDecimal.ROUND_HALF_UP);
			root.setUnitPrice(Money.valueOf(price, currency));
		}
		root.setUnitLessThanList(root.getListPrice().compareTo(root.getUnitPrice()) < 0);
	}

	private void aggregateChildPricesAndInventories(final OrderItemDto root, final OrderItemDto orderItemDto) {
		for (final OrderItemDto child : orderItemDto.getChildren()) {
			if (child.getChildren().isEmpty()  && !child.isBundle()) {
				final BigDecimal childQty = new BigDecimal(child.getQuantity());
				Money sum = root.getListPrice().add(child.getListPrice().multiply(childQty));
				root.setListPrice(sum);

				sum = root.getUnitPrice().add(child.getUnitPrice().multiply(childQty));
				root.setUnitPrice(sum);

				sum = root.getTotal().add(child.getTotal());
				root.setTotal(sum);

				sum = root.getDollarSavings().add(child.getDollarSavings());
				root.setDollarSavings(sum);

				// setting up the worse case inventory
				final InventoryDto worseInventory = InventoryWorseCaseUtil.getWorse(root.getInventory(), child.getInventory());
				root.setInventory(worseInventory);

			} else if (child.isBundle()) {
				aggregateChildPricesAndInventories(root, child);
			}
		}
	}

}
