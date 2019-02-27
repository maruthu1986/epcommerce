/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.service.order.impl;

import java.math.BigDecimal;
import java.util.List;

import com.elasticpath.commons.handlers.order.OrderShipmentHandler;
import com.elasticpath.commons.handlers.order.OrderShipmentHandlerFactory;
import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.domain.order.OrderReturnSku;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.service.order.OrderReturnInvalidException;
import com.elasticpath.service.order.OrderReturnValidator;

/**
 * Composite validator.
 */
public class CompositeOrderReturnValidatorImpl implements OrderReturnValidator {
	
	private List<OrderReturnValidator> validators;
	
	/**
	 * Iterates through all validators and triggers them.
	 * 
	 * @param orderReturn the order return
	 * @param orderShipment the order shipment
	 * @throws OrderReturnInvalidException on validation error
	 */
	@Override
	public void validate(final OrderReturn orderReturn, final OrderShipment orderShipment) throws OrderReturnInvalidException {
		for (OrderReturnValidator validator : getValidators()) {
			validator.validate(orderReturn, orderShipment);
		}
	}

	/**
	 * @return the validators list
	 */
	protected List<OrderReturnValidator> getValidators() {
		return validators;
	}

	/**
	 *
	 * @param validators the validators to use
	 */
	public void setValidators(final List<OrderReturnValidator> validators) {
		this.validators = validators;
	}

	/**
	 * Order return quantity validator.
	 */
	public static class OrderReturnQuantityValidator implements OrderReturnValidator {

		/**
		 * Checks to see if the quantity passed in is valid.
		 *
		 * @param orderReturn the order return
		 * @param orderShipment the order shipment
		 */
		@Override
		public void validate(final OrderReturn orderReturn, final OrderShipment orderShipment) {
			for (OrderReturnSku orderReturnSku : orderReturn.getOrderReturnSkus()) {
				if (orderReturnSku.getQuantity() <= 0) {
					throw new OrderReturnInvalidException("Quantity must be a positive integer");
				}
			}
		}
	}

	/**
	 * Order return returnable quantity validator.
	 */
	public static class OrderReturnReturnableQuantityValidator implements OrderReturnValidator {

		/**
		 * Check if Returnable Quantity is valid.
		 *
		 * @param orderReturn the order return
		 * @param orderShipment the order shipment
		 */
		@Override
		public void validate(final OrderReturn orderReturn, final OrderShipment orderShipment) {
			for (OrderReturnSku orderReturnSku : orderReturn.getOrderReturnSkus()) {
				if (orderReturnSku.getQuantity() > orderReturnSku.getOrderSku().getReturnableQuantity()) {
					throw new OrderReturnInvalidException("Return quantity cannot be more than order SKU returnable quantity");
				}
			}
		}
	}
	
	/**
	 * Order return less restock amount validator.
	 */
	public static class OrderReturnRestockAmountValidator implements OrderReturnValidator {

		/**
		 * Determines if the restock amount is valid. <br>
		 * By default is is valid if it is not less than 0.
		 * 
		 * @param orderReturn the order return
		 * @param orderShipment the order shipment
		 * @throws OrderReturnInvalidException on validation error
		 */
		@Override
		public void validate(final OrderReturn orderReturn, final OrderShipment orderShipment) throws OrderReturnInvalidException {
			BigDecimal restockAmount = orderReturn.getLessRestockAmount();
			if (restockAmount == null || restockAmount.compareTo(BigDecimal.ZERO) >= 0) {
				return;
			}
			throw new OrderReturnInvalidException("Restock amount must be greater than zero");
			
		}
	}
	
	/**
	 * Order return shipping cost validator.
	 */
	public static class OrderReturnShippingCostValidator implements OrderReturnValidator {
		private OrderShipmentHandlerFactory orderShipmentHandlerFactory;
		
		/**
		 * Determines if the shipping cost is valid. By default is is valid if it is not less than 0.
		 *
		 * @param orderReturn the order return
		 * @param orderShipment the order shipment
		 * @throws OrderReturnInvalidException on validation error
		 */
		@Override
		public void validate(final OrderReturn orderReturn, final OrderShipment orderShipment) throws OrderReturnInvalidException {
				OrderShipmentHandler shipmentHandler = getOrderShipmentHandlerFactory().getOrderShipmentHandler(
															orderShipment.getOrderShipmentType());
				
				BigDecimal shippingCost = orderReturn.getShippingCost();
				BigDecimal maxAllowedShippingCost = shipmentHandler.calculateShippingCost(orderShipment);

				if ((shippingCost == null) || ((shippingCost.compareTo(BigDecimal.ZERO) >= 0)
					&& (shippingCost.compareTo(maxAllowedShippingCost) <= 0))) {
					return;
				}
				throw new OrderReturnInvalidException("Refunded shipping cost cannot be more than originally charged");
			}

		protected OrderShipmentHandlerFactory getOrderShipmentHandlerFactory() {
			return orderShipmentHandlerFactory;
		}

		public void setOrderShipmentHandlerFactory(final OrderShipmentHandlerFactory orderShipmentHandlerFactory) {
			this.orderShipmentHandlerFactory = orderShipmentHandlerFactory;
		}
	}
	
	/**
	 * Order return SKUs validator.
	 */
	public static class OrderReturnSkuValidator implements OrderReturnValidator {
		
		/**
		 * @param orderReturn order return
		 * @param orderShipment order shipment
		 */
		@Override
		public void validate(final OrderReturn orderReturn, final OrderShipment orderShipment) {
			for (OrderReturnSku returnSku : orderReturn.getOrderReturnSkus()) {
				if (returnSku.getOrderSku() == null) {
					throw new OrderReturnInvalidException("Order SKU reference must exist on order return SKU");
				} else if (!orderShipment.getShipmentOrderSkus().contains(returnSku.getOrderSku())) {
					throw new OrderReturnInvalidException("Order SKU reference does not match any of the order shipment SKUs");
				}
			}
		}
	}
}
