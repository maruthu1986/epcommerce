/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.cmclient.fulfillment.wizards;

import java.math.BigDecimal;
import java.util.Set;

import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.domain.order.OrderReturnSku;
import com.elasticpath.domain.order.OrderReturnStatus;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.PhysicalOrderShipment;

/**
 * 
 * Encapsulates the logic around order return validation.
 */
public class OrderReturnWizardValidator {
	
	/**
	 * Checks whether shipping cost is valid by comparing the return shipping cost
	 * to remaining shipping cost available for return.
	 *
	 * @param returnShippingCost - the return shipping cost.
	 * @param remainingShippingCost - the shipping cost available for return.
	 * @return true if the value for shipping cost is valid
	 */
	public boolean isShippingCostValid(final BigDecimal returnShippingCost, final BigDecimal remainingShippingCost) {
		return returnShippingCost.compareTo(remainingShippingCost) <= 0;
	}
	
	/**
	 * Checks whether shipment total is valid by comparing the return shipment total
	 * to remaining shipment total.
	 *
	 * @param returnShipmentTotal - the return shipment total.
	 * @param remainingShipmentTotal - the shipment total available for return.
	 * @return true if the value for shipment total is valid
	 */
	public boolean isShipmentTotalValid(final BigDecimal returnShipmentTotal, final BigDecimal remainingShipmentTotal) {
		return returnShipmentTotal.compareTo(remainingShipmentTotal) <= 0;
	}
	
	/**
	 * Checks whether or not the return skus quantity is valid.
	 *
	 * @param returnSkus - the order return skus
	 * @return true if one of the returnSkus quantity is greater than 0
	 */
	public boolean isQuantityToReturnValid(final Set<OrderReturnSku> returnSkus) {
		for (OrderReturnSku currentOrderReturnSku : returnSkus) {
			if (currentOrderReturnSku.getQuantity() > 0) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Retrieves the shipping cost left for returning for this order shipment.
	 * @param orderReturn The OrderReturn to populate data for.
	 * @param orderShipment The OrderShipment to base information for.
	 * @return The shipping cost left for returning for the shipment.
	 */
	public BigDecimal calculateShippingCostLeftForReturn(final OrderReturn orderReturn, final OrderShipment orderShipment) {
		
		BigDecimal alreadyReturnedShippingCost = BigDecimal.ZERO;
		Set<OrderReturn> orderReturnSet = orderReturn.getOrder().getReturns();
		
		for (OrderReturn returnedOrder : orderReturnSet) {
			if (returnedOrder.getReturnStatus().equals(OrderReturnStatus.CANCELLED)) {
				continue;
			}
			if (orderReturn.getRmaCode() != null && orderReturn.getRmaCode().equals(returnedOrder.getRmaCode())) {
				continue;
			}
			if (!returnedOrder.getOrderShipmentForReturn().getShipmentNumber().equals(orderShipment.getShipmentNumber())) {
				continue;
			}
			
			alreadyReturnedShippingCost = alreadyReturnedShippingCost.add(returnedOrder.getShippingCost());
		}
		
		return ((PhysicalOrderShipment) orderShipment).getShippingCost().subtract(alreadyReturnedShippingCost);
	}
	
	/**
	 * Retrieves the shipment total left for returning for this order shipment.
	 * @param orderReturn The OrderReturn to populate data for.
	 * @param orderShipment The OrderShipment to base information for.
	 * @return The shipment total left for returning for the shipment.
	 */
	public BigDecimal calculateShipmentTotalLeftForReturn(final OrderReturn orderReturn, final OrderShipment orderShipment) {
		
		BigDecimal alreadyReturnedShipmentTotal = BigDecimal.ZERO;
		Set<OrderReturn> orderReturnSet = orderReturn.getOrder().getReturns();
		
		for (OrderReturn returnedOrder : orderReturnSet) {
			if (returnedOrder.getReturnStatus().equals(OrderReturnStatus.CANCELLED)) {
				continue;
			}
			if (orderReturn.getRmaCode() != null && orderReturn.getRmaCode().equals(returnedOrder.getRmaCode())) {
				continue;
			}
			if (!returnedOrder.getOrderShipmentForReturn().getShipmentNumber().equals(orderShipment.getShipmentNumber())) {
				continue;
			}
			returnedOrder.recalculateOrderReturn();
			alreadyReturnedShipmentTotal = alreadyReturnedShipmentTotal.add(returnedOrder.getReturnTotal());
		}
		
		return orderShipment.getTotal().subtract(alreadyReturnedShipmentTotal);
	}
}