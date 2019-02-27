/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.cucumber.orderreturn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.cucumber.ScenarioContextValueHolder;
import com.elasticpath.cucumber.shoppingcart.ShoppingCartStepDefinitionsHelper;
import com.elasticpath.domain.event.EventOriginatorHelper;
import com.elasticpath.domain.misc.PropertyBased;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.domain.order.OrderReturnSku;
import com.elasticpath.domain.order.OrderReturnStatus;
import com.elasticpath.domain.order.OrderReturnType;
import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.domain.order.impl.OrderReturnImpl;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.persistence.support.FetchGroupConstants;
import com.elasticpath.sellingchannel.director.CartDirector;
import com.elasticpath.service.order.ReturnAndExchangeService;
import com.elasticpath.service.order.ReturnExchangeType;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;

/**
 * Help class for {@link OrderReturnStepDefinitions}.
 */
@SuppressWarnings("deprecation")
public class OrderReturnStepDefinitionsHelper {
		
	@Inject
	@Named("orderHolder")
	private ScenarioContextValueHolder<Order> orderHolder;
	
	@Inject
	@Named("orderReturnHolder")
	private ScenarioContextValueHolder<OrderReturn> orderReturnHolder;
	
	@Inject
	@Named("shippingOptionHolder")
	private ScenarioContextValueHolder<ShippingOption> shippingOptionHolder;
	
	@Autowired
	private ShoppingCartStepDefinitionsHelper shoppingCartStepDefinitionsHelper;
	
	@Autowired
	private ReturnAndExchangeService returnAndExchangeService;
	
	@Autowired
	@Qualifier("coreBeanFactory")
	private BeanFactory coreBeanFactory;
	
	@Autowired
	private CartDirector cartDirector;

	/**
	 * Creates an order return and save it to the current test context. 
	 *
	 * @param itemDtos the return items
	 */
	public void createShipmentReturn(final List<ShoppingItemDto> itemDtos) {
		
		setUpProperties();
		
		Order order = orderHolder.get();
		
		OrderReturn orderReturn = createOrderShipmentReturn(order, order.getPhysicalShipments().get(0), itemDtos, OrderReturnType.RETURN);
		orderReturn.getOrder().setModifiedBy(getEventOriginatorHelper().getSystemOriginator());
		
		orderReturnHolder.set(returnAndExchangeService.createShipmentReturn(orderReturn, 
																			ReturnExchangeType.PHYSICAL_RETURN_REQUIRED, 
																			order.getPhysicalShipments().get(0), 
																			orderReturn.getOrder().getModifiedBy()));		
	}
	
	/**
	 * Cancels the order return of the current test context.
	 */
	public void cancelReturn() {
		
		OrderReturn orderReturn = orderReturnHolder.get();
		
		OrderReturn retrievedOrderReturn = returnAndExchangeService.get(orderReturn.getUidPk(), getTuner());
		retrievedOrderReturn.getOrder().setModifiedBy(getEventOriginatorHelper().getSystemOriginator());
		
		orderReturnHolder.set(returnAndExchangeService.cancelReturnExchange(retrievedOrderReturn));
	}
	
	/**
	 * Modifies order return items.
	 * 
	 * @param itemDtos the order return items
	 */
	public void editReturnItems(final List<ShoppingItemDto> itemDtos) {

		OrderReturn orderReturn = orderReturnHolder.get();
		OrderReturn retrievedOrderReturn = returnAndExchangeService.get(orderReturn.getUidPk(), getTuner());
		
		for (ShoppingItemDto dto : itemDtos) {
			for (OrderReturnSku orderReturnSku : retrievedOrderReturn.getOrderReturnSkus()) {
				if (orderReturnSku.getOrderSku().getSkuCode().equals(dto.getSkuCode())) {
					orderReturnSku.setQuantity(dto.getQuantity());
					orderReturnSku.setReturnAmount(orderReturnSku.getAmountMoney().getAmount());
				}
			}
		}
		
		retrievedOrderReturn.recalculateOrderReturn();
		retrievedOrderReturn.getOrder().setModifiedBy(getEventOriginatorHelper().getSystemOriginator());
		
		orderReturnHolder.set(returnAndExchangeService.editReturn(retrievedOrderReturn));
	}
	
	/**
	 * Creates order return with a given collection of items.
	 *
	 * @param itemDtos the returning items
	 */
	public void createExchangeReturningItems(final List<ShoppingItemDto> itemDtos) {
		
		setUpProperties();
		
		Order order = orderHolder.get();
		orderReturnHolder.set(createOrderShipmentReturn(order, order.getPhysicalShipments().get(0), itemDtos, OrderReturnType.EXCHANGE));
		
	}
	
	/**
	 * Creates order return with exchange order. 
	 *
	 * @param itemDtos the exchanging items
	 */
	public void createExchange(final List<ShoppingItemDto> itemDtos) {
		
		OrderReturn orderReturn = orderReturnHolder.get();
		List<ShoppingItem> shoppingItems = new ArrayList<>();
		
		for (ShoppingItemDto shoppingItemDto : itemDtos) {
			shoppingItems.add(cartDirector.addItemToCart(
											shoppingCartStepDefinitionsHelper.getEmptyShoppingCart(), 
											shoppingItemDto));
		}
		
		returnAndExchangeService.populateShoppingCart(
											orderReturn, 
											shoppingItems, 
											shippingOptionHolder.get(),
											orderReturn.getOrderReturnAddress());
		
		orderReturn.getOrder().setModifiedBy(getEventOriginatorHelper().getSystemOriginator());
		
		OrderReturn exchangeReturn = returnAndExchangeService.createExchange(
				orderReturn, 
				ReturnExchangeType.ORIGINAL_PAYMENT, 
				getPaymentByCardHolderName(orderReturn.getOrder(), OrderPayment.CAPTURE_TRANSACTION));
		exchangeReturn.recalculateOrderReturn();
		
		orderReturnHolder.set(exchangeReturn);
	}
	
	private FetchGroupLoadTuner getTuner() {
		
		FetchGroupLoadTuner tuner = (FetchGroupLoadTuner) coreBeanFactory.getBean(ContextIdNames.FETCH_GROUP_LOAD_TUNER);
		tuner.addFetchGroup(FetchGroupConstants.ORDER_INDEX, 
			FetchGroupConstants.ORDER_NOTES, 
			FetchGroupConstants.ALL);
		
		return tuner;
	}
	
	/**
	 * getEventOriginatorHelper.
	 * @return EventOriginatorHelper
	 */
	private EventOriginatorHelper getEventOriginatorHelper() {
		return coreBeanFactory.getBean(ContextIdNames.EVENT_ORIGINATOR_HELPER);
	}
	
	private void setUpProperties() {
		final PropertyBased propertyBased = (PropertyBased) coreBeanFactory.getBean(ContextIdNames.ORDER_RETURN_SKU_REASON);

		Map<String, Properties> propertiesMap = new HashMap<>();

		Properties value = new Properties();
		value.put("OrderReturnSkuReason_UnwantedGift", "Unwanted Gift");
		value.put("OrderReturnSkuReason_IncorrectItem", "Incorrect Item");
		value.put("OrderReturnSkuReason_Faulty", "Faulty");
		propertiesMap.put("orderReturnSkuReason.properties", value);

		propertyBased.setPropertiesMap(propertiesMap);
	}
	
	private OrderReturn createOrderShipmentReturn(final Order order,
													final PhysicalOrderShipment phShipment,
													final List<ShoppingItemDto> itemDtos,
													final OrderReturnType type) {
		
		OrderReturn orderReturn = new OrderReturnImpl();
		
		orderReturn.populateOrderReturn(order, phShipment, type);
		orderReturn.setOrderReturnAddress(phShipment.getShipmentAddress());
		
		
		for (OrderReturnSku orderReturnSku : orderReturn.getOrderReturnSkus()) {
			orderReturnSku.setReturnReason("Faulty");
	
			orderReturnSku.setQuantity(getQuantity(orderReturnSku.getOrderSku().getSkuCode(), itemDtos));
			
			orderReturnSku.setReturnAmount(orderReturnSku.getAmountMoney().getAmount());
		}
		
		orderReturn.setShippingCost(phShipment.getShippingCost());
		orderReturn.recalculateOrderReturn();
		orderReturn.setReturnStatus(OrderReturnStatus.AWAITING_STOCK_RETURN);
		
		return orderReturn;
	}

	private int getQuantity(final String skuCode, final List<ShoppingItemDto> itemDtos) {
		
		for (ShoppingItemDto itemDto : itemDtos) {
			if (StringUtils.equals(skuCode, itemDto.getSkuCode())) {
				return itemDto.getQuantity();
			}
		}
		
		return 0;
	}
	
	private OrderPayment getPaymentByCardHolderName(final Order order, final String transactionType) {
		for (OrderPayment orderPayment : order.getOrderPayments()) {
			if (StringUtils.equals(orderPayment.getTransactionType(), transactionType)) {
				return orderPayment;
			}
		}
		
		return null;
	}
}
