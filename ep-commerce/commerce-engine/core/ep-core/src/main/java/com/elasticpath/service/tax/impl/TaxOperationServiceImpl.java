/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.service.tax.impl;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Map;
import java.util.function.Function;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderAddress;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.OrderShipmentStatus;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.OrderStatus;
import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.domain.shipping.ShipmentType;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.ShoppingItemPricingSnapshot;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.money.Money;
import com.elasticpath.plugin.tax.builder.TaxOperationContextBuilder;
import com.elasticpath.plugin.tax.builder.TaxOverrideContextBuilder;
import com.elasticpath.plugin.tax.common.TaxItemObjectType;
import com.elasticpath.plugin.tax.common.TaxJournalType;
import com.elasticpath.plugin.tax.common.TaxTransactionType;
import com.elasticpath.plugin.tax.domain.TaxAddress;
import com.elasticpath.plugin.tax.domain.TaxDocument;
import com.elasticpath.plugin.tax.domain.TaxDocumentId;
import com.elasticpath.plugin.tax.domain.TaxExemption;
import com.elasticpath.plugin.tax.domain.TaxOperationContext;
import com.elasticpath.plugin.tax.domain.impl.MutableTaxDocument;
import com.elasticpath.plugin.tax.domain.impl.StringTaxDocumentId;
import com.elasticpath.plugin.tax.manager.TaxManager;
import com.elasticpath.service.store.StoreService;
import com.elasticpath.service.tax.TaxCalculationResult;
import com.elasticpath.service.tax.TaxCalculationService;
import com.elasticpath.service.tax.TaxDocumentModificationContext;
import com.elasticpath.service.tax.TaxDocumentModificationItem;
import com.elasticpath.service.tax.TaxDocumentModificationType;
import com.elasticpath.service.tax.TaxDocumentService;
import com.elasticpath.service.tax.TaxOperationService;
import com.elasticpath.service.tax.adapter.TaxAddressAdapter;

/**
 * Default implementation of {@link TaxOperationService}.
 */
public class TaxOperationServiceImpl implements TaxOperationService {

	private TaxCalculationService taxCalculationService;

	private TaxManager taxManager;

	private TaxDocumentService taxDocumentService;

	private TaxAddressAdapter addressAdapter;

	private BeanFactory beanFactory;

	private Function<OrderSku, ShoppingItemPricingSnapshot> orderSkuToPricingSnapshotFunction;

	@Override
	public void deleteDocument(final TaxDocument taxDocument) {

		getTaxManager().deleteDocument(taxDocument, TaxOperationContextBuilder
				.newBuilder()
				.withTaxJournalType(TaxJournalType.PURCHASE)
				.withTaxDocumentId(taxDocument.getDocumentId())
				.build());
	}

	@Override
	public TaxCalculationResult calculateTaxes(final OrderShipment orderShipment) {

		return calculateTaxes(orderShipment, TaxOperationContextBuilder
				.newBuilder()
				.withCurrency(orderShipment.getOrder().getCurrency())
				.withCustomerCode(orderShipment.getOrder().getCustomer().getUserId())
				.withTaxJournalType(TaxJournalType.PURCHASE)
				.withTaxDocumentId(orderShipment.getTaxDocumentId())
				.withTaxExemption(orderShipment.getOrder().getTaxExemption())
				.withCustomerBusinessNumber(orderShipment.getOrder().getCustomer().getBusinessNumber())
				.build());
	}

	@Override
	public TaxCalculationResult calculateTaxes(final OrderShipment orderShipment, final TaxOperationContext taxOperationContext) {
		StoreService storeService = beanFactory.getBean(ContextIdNames.STORE_SERVICE);
		Warehouse defaultWarehouse = storeService.findStoreWithCode(orderShipment.getOrder().getStoreCode()).getWarehouse();
		TaxAddress originAddress = null;
		if (defaultWarehouse != null) {
			originAddress = getAddressAdapter().toTaxAddress(defaultWarehouse.getAddress());
		}

		final Map<? extends ShoppingItem, ShoppingItemPricingSnapshot> shipmentOrderSkuPricingMap = orderShipment.getShipmentOrderSkus().stream()
				.collect(toMap(identity(),
							   getOrderSkuToPricingSnapshotFunction()));

		return getTaxCalculationService().calculateTaxes(orderShipment.getOrder().getStoreCode(),
				getAddressAdapter().toTaxAddress(buildTaxAddress(orderShipment)),
				originAddress,
				getShippingCostMoney(orderShipment),
				shipmentOrderSkuPricingMap,
				orderShipment.getSubtotalDiscountMoney(),
				taxOperationContext);
	}

	@Override
	public void commitDocument(final TaxDocument taxDocument, final OrderShipment orderShipment) throws EpServiceException {

		commitDocument(taxDocument, orderShipment, TaxTransactionType.ORDER);
	}

	private void commitDocument(final TaxDocument taxDocument, final OrderShipment orderShipment, final TaxTransactionType transactionType)
			throws EpServiceException {

		getTaxManager().commitDocument(taxDocument, TaxOperationContextBuilder
				.newBuilder()
				.withCurrency(orderShipment.getOrder().getCurrency())
				.withCustomerCode(orderShipment.getOrder().getCustomer().getUserId())
				.withTaxJournalType(TaxJournalType.PURCHASE)
				.withTaxDocumentId(orderShipment.getTaxDocumentId())
				.withOrderNumber(orderShipment.getOrder().getOrderNumber())
				.withTaxTransactionType(transactionType)
				.withTaxItemObjectType(TaxItemObjectType.ORDER_SKU)
				.withShippingItemReferenceId(orderShipment.getShipmentNumber())
				.withTaxExemption(orderShipment.getOrder().getTaxExemption())
				.withCustomerBusinessNumber(orderShipment.getOrder().getCustomer().getBusinessNumber())
				.build());

	}

	@Override
	public void reverseTaxes(final Order order) throws EpServiceException {

		if (OrderStatus.CANCELLED.equals(order.getStatus())) {

			for (OrderShipment orderShipment : order.getAllShipments()) {
				reverseTaxes(orderShipment, buildTaxAddress(orderShipment));
			}
			return;
		}
		throw new EpServiceException("Order is not cancelled" + order.getOrderNumber());
	}

	@Override
	public void reverseTaxes(final OrderShipment orderShipment, final OrderAddress destinationAddress) throws EpServiceException {
		final Order order = orderShipment.getOrder();

		StoreService storeService = getBeanFactory().getBean(ContextIdNames.STORE_SERVICE);
		Warehouse defaultWarehouse = storeService.findStoreWithCode(order.getStoreCode()).getWarehouse();
		TaxAddress originAddress = null;
		if (defaultWarehouse != null) {
			originAddress = getAddressAdapter().toTaxAddress(defaultWarehouse.getAddress());
		}

		if (OrderShipmentStatus.CANCELLED.equals(orderShipment.getShipmentStatus())) {

			reverseTaxDocumentTaxes(orderShipment.getTaxDocumentId(),
					getAddressAdapter().toTaxAddress(destinationAddress),
					originAddress,
					order.getCustomer().getUserId(),
					order.getOrderNumber(),
					order.getCurrency(),
					TaxTransactionType.ORDER_CANCEL,
					order.getTaxExemption(),
					orderShipment.getShipmentNumber(),
					order.getCustomer().getBusinessNumber());
			return;
		}
		throw new EpServiceException("Ordershipment is not cancellable" + orderShipment.getShipmentNumber());
	}

	@SuppressWarnings({"PMD.ExcessiveParameterList", "checkstyle:parameternumber"})
	private void reverseTaxDocumentTaxes(final TaxDocumentId taxDocumentId,
			final TaxAddress destinationAddress,
			final TaxAddress originAddress,
			final String customerCode,
			final String orderNumber,
			final Currency currency,
			final TaxTransactionType transactionType,
			final TaxExemption taxExemption,
			final String orderShipmentNumber,
			final String customerBusinessNumber) {

		TaxDocument orderShipmentTaxDocument = getTaxDocumentService().buildTaxDocument(taxDocumentId,
				destinationAddress, originAddress, TaxJournalType.PURCHASE);

		// if there is no such tax document in the EP tax journal history, then skip reversing
		if (orderShipmentTaxDocument == null) {
			return;
		}
		// change the tax document type to return for commit
		((MutableTaxDocument) orderShipmentTaxDocument).setJournalType(TaxJournalType.REVERSAL);

		TaxOperationContext taxOperationContext = TaxOperationContextBuilder
				.newBuilder()
				.withCurrency(currency)
				.withCustomerCode(customerCode)
				.withTaxJournalType(TaxJournalType.REVERSAL)
				.withTaxDocumentId(taxDocumentId)
				.withTaxExemption(taxExemption)
				.withOrderNumber(orderNumber)
				.withTaxTransactionType(transactionType)
				.withTaxItemObjectType(TaxItemObjectType.ORDER_SKU)
				.withShippingItemReferenceId(orderShipmentNumber)
				.withTaxOverrideContext(
						TaxOverrideContextBuilder
								.newBuilder()
								.withTaxOverrideDocumentId(taxDocumentId.toString())
								.withTaxOverrideJournalType(TaxJournalType.PURCHASE)
								.build())
				.withCustomerBusinessNumber(customerBusinessNumber)
				.build();

		getTaxManager().commitDocument(orderShipmentTaxDocument, taxOperationContext);
	}
	// CHECKSTYLE:ON

	@Override
	public void updateTaxes(final Order order, final TaxDocumentModificationContext taxDocumentModificationContext) {
		StoreService storeService = getBeanFactory().getBean(ContextIdNames.STORE_SERVICE);
		Warehouse defaultWarehouse = storeService.findStoreWithCode(order.getStoreCode()).getWarehouse();

		for (TaxDocumentModificationItem item : taxDocumentModificationContext.get(TaxDocumentModificationType.UPDATE)) {

			OrderShipment orderShipment = order.getShipment(item.getTaxDocumentReferenceId());

			TaxDocumentId previousTaxDocumentId = StringTaxDocumentId.fromString(item.getPreviousTaxDocumentId());
			TaxAddress destinationAddress = getAddressAdapter().toTaxAddress(item.getPreviousAddress());
			TaxAddress originAddress = null;
			if (defaultWarehouse != null) {
				originAddress = getAddressAdapter().toTaxAddress(defaultWarehouse.getAddress());
			}

			reverseTaxDocumentTaxes(previousTaxDocumentId,
					destinationAddress,
					originAddress,
					order.getCustomer().getUserId(),
					order.getOrderNumber(),
					order.getCurrency(),
					TaxTransactionType.ORDER_CHANGE,
					order.getTaxExemption(),
					orderShipment.getShipmentNumber(),
					order.getCustomer().getBusinessNumber());

			commitDocument(orderShipment.calculateTaxes().getTaxDocument(), orderShipment, TaxTransactionType.ORDER_CHANGE);
		}

		for (TaxDocumentModificationItem item : taxDocumentModificationContext.get(TaxDocumentModificationType.CANCEL)) {

			OrderShipment orderShipment = order.getShipment(item.getTaxDocumentReferenceId());
			TaxDocumentId previousTaxDocumentId = StringTaxDocumentId.fromString(item.getPreviousTaxDocumentId());
			TaxAddress destinationAddress = getAddressAdapter().toTaxAddress(item.getPreviousAddress());
			TaxAddress originAddress = null;
			if (defaultWarehouse != null) {
				originAddress = getAddressAdapter().toTaxAddress(defaultWarehouse.getAddress());
			}

			reverseTaxDocumentTaxes(previousTaxDocumentId,
					destinationAddress,
					originAddress,
					order.getCustomer().getUserId(),
					order.getOrderNumber(),
					order.getCurrency(),
					TaxTransactionType.ORDER_CHANGE,
					order.getTaxExemption(),
					orderShipment.getShipmentNumber(),
					order.getCustomer().getBusinessNumber());

		}

		for (TaxDocumentModificationItem item : taxDocumentModificationContext.get(TaxDocumentModificationType.NEW)) {

			OrderShipment orderShipment = order.getShipment(item.getTaxDocumentReferenceId());
			commitDocument(calculateTaxes(orderShipment).getTaxDocument(), orderShipment, TaxTransactionType.ORDER_CHANGE);
		}
	}

	private Money getShippingCostMoney(final OrderShipment orderShipment) {

		if (orderShipment.getOrderShipmentType().equals(ShipmentType.PHYSICAL)) {
			return ((PhysicalOrderShipment) orderShipment).getShippingCostMoney();
		}

		return getMoneyZero(orderShipment.getOrder().getCurrency());
	}

	private OrderAddress buildTaxAddress(final OrderShipment orderShipment) {

		if (orderShipment.getOrderShipmentType().equals(ShipmentType.PHYSICAL)) {
			return ((PhysicalOrderShipment) orderShipment).getShipmentAddress();
		}

		return orderShipment.getOrder().getBillingAddress();
	}

	private Money getMoneyZero(final Currency currency) {
		return Money.valueOf(BigDecimal.ZERO, currency);
	}

	public TaxManager getTaxManager() {
		return taxManager;
	}

	public void setTaxManager(final TaxManager taxManager) {
		this.taxManager = taxManager;
	}

	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	public TaxCalculationService getTaxCalculationService() {
		return taxCalculationService;
	}

	public void setTaxCalculationService(final TaxCalculationService taxCalculationService) {
		this.taxCalculationService = taxCalculationService;
	}

	public TaxDocumentService getTaxDocumentService() {
		return taxDocumentService;
	}

	public void setTaxDocumentService(final TaxDocumentService taxDocumentService) {
		this.taxDocumentService = taxDocumentService;
	}

	public TaxAddressAdapter getAddressAdapter() {
		return addressAdapter;
	}

	public void setAddressAdapter(final TaxAddressAdapter addressAdapter) {
		this.addressAdapter = addressAdapter;
	}

	protected Function<OrderSku, ShoppingItemPricingSnapshot> getOrderSkuToPricingSnapshotFunction() {
		return this.orderSkuToPricingSnapshotFunction;
	}

	public void setOrderSkuToPricingSnapshotFunction(final Function<OrderSku, ShoppingItemPricingSnapshot> orderSkuToPricingSnapshotFunction) {
		this.orderSkuToPricingSnapshotFunction = orderSkuToPricingSnapshotFunction;
	}

}
