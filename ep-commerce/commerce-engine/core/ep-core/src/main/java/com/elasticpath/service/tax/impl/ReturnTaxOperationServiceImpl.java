/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.service.tax.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.order.OrderAddress;
import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.domain.order.OrderReturnSku;
import com.elasticpath.domain.order.OrderReturnStatus;
import com.elasticpath.domain.order.OrderSku;
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
import com.elasticpath.plugin.tax.domain.TaxOperationContext;
import com.elasticpath.plugin.tax.domain.impl.MutableTaxDocument;
import com.elasticpath.plugin.tax.domain.impl.StringTaxDocumentId;
import com.elasticpath.plugin.tax.manager.TaxManager;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.service.shoppingcart.OrderSkuFactory;
import com.elasticpath.service.shoppingcart.PricingSnapshotService;
import com.elasticpath.service.store.StoreService;
import com.elasticpath.service.tax.ReturnTaxOperationService;
import com.elasticpath.service.tax.TaxCalculationResult;
import com.elasticpath.service.tax.TaxCalculationService;
import com.elasticpath.service.tax.TaxDocumentModificationContext;
import com.elasticpath.service.tax.TaxDocumentModificationItem;
import com.elasticpath.service.tax.TaxDocumentModificationType;
import com.elasticpath.service.tax.TaxDocumentService;
import com.elasticpath.service.tax.adapter.TaxAddressAdapter;

/**
 * Provides a default implementation of {@link ReturnTaxOperationService}.
 *
 */
public class ReturnTaxOperationServiceImpl implements ReturnTaxOperationService {

	private TaxCalculationService taxCalculationService;

	private TaxManager taxManager;

	private TaxDocumentService taxDocumentService;

	private TaxAddressAdapter addressAdapter;

	private BeanFactory beanFactory;

	private ProductSkuLookup productSkuLookup;

	private PricingSnapshotService pricingSnapshotService;

	@Override
	public TaxCalculationResult calculateTaxes(final OrderReturn orderReturn) {

		TaxDocumentId taxOverrideTaxDocumentId = orderReturn.getOrderShipmentForReturn().getTaxDocumentId();

		TaxOperationContext taxOperationContext = TaxOperationContextBuilder
				.newBuilder()
				.withCurrency(orderReturn.getCurrency())
				.withCustomerCode(orderReturn.getOrder().getCustomer().getUserId())
				.withTaxJournalType(TaxJournalType.REVERSAL)
				.withTaxDocumentId(orderReturn.getTaxDocumentId())
				.withTaxOverrideContext(
						TaxOverrideContextBuilder
								.newBuilder()
								.withTaxOverrideDocumentId(taxOverrideTaxDocumentId.toString())
								.withTaxOverrideJournalType(TaxJournalType.PURCHASE)
								.build())
				.withTaxExemption(orderReturn.getOrder().getTaxExemption())
				.withCustomerBusinessNumber(orderReturn.getOrder().getCustomer().getBusinessNumber())
				.build();

		StoreService storeService = getBeanFactory().getBean(ContextIdNames.STORE_SERVICE);
		Warehouse defaultWarehouse = storeService.findStoreWithCode(orderReturn.getOrder().getStoreCode()).getWarehouse();
		TaxAddress originAddress = null;
		if (defaultWarehouse != null) {
			originAddress = getAddressAdapter().toTaxAddress(defaultWarehouse.getAddress());
		}

		// calculate taxes based on the order shipment tax document allowing us to base the tax calculation on the tax document
		return getTaxCalculationService().calculateTaxes(
				orderReturn.getOrder().getStoreCode(),
				getAddressAdapter().toTaxAddress(orderReturn.getOrderReturnAddress()),
				originAddress,
				orderReturn.getShippingCostMoney(),
				getAsShoppingItemPricingSnapshotMap(orderReturn),
				getMoneyZero(orderReturn.getCurrency()),
				taxOperationContext);
	}

	@Override
	public void commitDocument(final TaxDocument taxDocument, final OrderReturn orderReturn) throws EpServiceException {

		commitDocument(taxDocument, orderReturn, TaxTransactionType.RETURN);
	}

	private void commitDocument(final TaxDocument taxDocument, final OrderReturn orderReturn, final TaxTransactionType transactionType)
			throws EpServiceException {

		TaxDocumentId taxOverrideTaxDocumentId = orderReturn.getOrderShipmentForReturn().getTaxDocumentId();

		TaxOperationContext taxOperationContext = TaxOperationContextBuilder
				.newBuilder()
				.withCurrency(orderReturn.getCurrency())
				.withCustomerCode(orderReturn.getOrder().getCustomer().getUserId())
				.withTaxJournalType(TaxJournalType.REVERSAL)
				.withTaxDocumentId(orderReturn.getTaxDocumentId())
				.withOrderNumber(orderReturn.getOrder().getOrderNumber())
				.withTaxTransactionType(transactionType)
				.withTaxItemObjectType(TaxItemObjectType.ORDER_RETURN_SKU)
				.withShippingItemReferenceId(orderReturn.getRmaCode())
				.withTaxOverrideContext(
						TaxOverrideContextBuilder
								.newBuilder()
								.withTaxOverrideDocumentId(taxOverrideTaxDocumentId.toString())
								.withTaxOverrideJournalType(TaxJournalType.PURCHASE)
								.build())
				.withTaxExemption(orderReturn.getOrder().getTaxExemption())
				.withCustomerBusinessNumber(orderReturn.getOrder().getCustomer().getBusinessNumber())
				.build();

		getTaxManager().commitDocument(taxDocument, taxOperationContext);
	}

	@Override
	public void reverseTaxes(final OrderReturn orderReturn, final OrderAddress address) throws EpServiceException {

		if (orderReturn.getReturnStatus() != OrderReturnStatus.CANCELLED) {
			throw new EpServiceException("OrderReturn is not cancellable" + orderReturn.getRmaCode());
		}
		TaxAddress destinationAddress = getAddressAdapter().toTaxAddress(address);
		reverseTaxDocumentTaxes(orderReturn, orderReturn.getTaxDocumentId(), destinationAddress, TaxTransactionType.RETURN_CANCEL);
	}

	@Override
	public void updateTaxes(final OrderReturn orderReturn, final TaxDocumentModificationContext taxDocumentModificationContext) {

		TaxDocumentModificationItem taxDocumentModificationItem = taxDocumentModificationContext.get(orderReturn.getRmaCode());
		if (taxDocumentModificationItem == null || taxDocumentModificationItem.getModificationType() != TaxDocumentModificationType.UPDATE) {
			return;
		}

		TaxDocumentId previousTaxDocumentId = StringTaxDocumentId.fromString(taxDocumentModificationItem.getPreviousTaxDocumentId());
		TaxAddress address = getAddressAdapter().toTaxAddress(taxDocumentModificationItem.getPreviousAddress());

		reverseTaxDocumentTaxes(orderReturn, previousTaxDocumentId, address, TaxTransactionType.RETURN_CHANGE);
		commitDocument(orderReturn.calculateTaxes().getTaxDocument(), orderReturn, TaxTransactionType.RETURN_CHANGE);
	}

	private void reverseTaxDocumentTaxes(final OrderReturn orderReturn, final TaxDocumentId reverseTaxDocumentId,
			final TaxAddress destinationAddress, final TaxTransactionType transactionType) {
		StoreService storeService = getBeanFactory().getBean(ContextIdNames.STORE_SERVICE);
		Warehouse defaultWarehouse = storeService.findStoreWithCode(orderReturn.getOrder().getStoreCode()).getWarehouse();
		TaxAddress originAddress = null;
		if (defaultWarehouse != null) {
			originAddress = getAddressAdapter().toTaxAddress(defaultWarehouse.getAddress());
		}

		TaxDocument taxDocument = getTaxDocumentService().buildTaxDocument(reverseTaxDocumentId,
				destinationAddress, originAddress, TaxJournalType.REVERSAL);

		// if there is no such tax document in the EP tax journal history, then skip reversing
		if (taxDocument == null) {
			return;
		}
		// change the tax document type to purchase for commit
		((MutableTaxDocument) taxDocument).setJournalType(TaxJournalType.PURCHASE);

		TaxOperationContext taxOperationContext = TaxOperationContextBuilder
				.newBuilder()
				.withCurrency(orderReturn.getCurrency())
				.withCustomerCode(orderReturn.getOrder().getCustomer().getUserId())
				.withTaxJournalType(TaxJournalType.PURCHASE)
				.withTaxDocumentId(reverseTaxDocumentId)
				.withOrderNumber(orderReturn.getOrder().getOrderNumber())
				.withTaxTransactionType(transactionType)
				.withTaxItemObjectType(TaxItemObjectType.ORDER_RETURN_SKU)
				.withShippingItemReferenceId(orderReturn.getRmaCode())
				.withTaxOverrideContext(
						TaxOverrideContextBuilder
								.newBuilder()
								.withTaxOverrideDocumentId(orderReturn.getOrderShipmentForReturn().getTaxDocumentId().toString())
								.withTaxOverrideJournalType(TaxJournalType.PURCHASE)
								.build())
				.withTaxExemption(orderReturn.getOrder().getTaxExemption())
				.withCustomerBusinessNumber(orderReturn.getOrder().getCustomer().getBusinessNumber())
				.build();

		getTaxManager().commitDocument(taxDocument, taxOperationContext);
	}


	private Money getMoneyZero(final Currency currency) {
		return Money.valueOf(BigDecimal.ZERO, currency);
	}

	private Map<? extends ShoppingItem, ShoppingItemPricingSnapshot> getAsShoppingItemPricingSnapshotMap(final OrderReturn orderReturn) {

		List<OrderReturnSku> orderReturnSkus = new ArrayList<>();
		orderReturnSkus.addAll(orderReturn.getOrderReturnSkus());

		final Map<ShoppingItem, ShoppingItemPricingSnapshot> shoppingItems = Maps.newHashMap();

		for (OrderReturnSku orderReturnSku : orderReturnSkus) {
			if (orderReturnSku.getQuantity() > 0) {
				orderReturnSku.setReturnAmount(orderReturnSku.getAmountMoney().getAmount());

				final OrderSku shoppingItem = convertToShoppingItem(orderReturnSku, orderReturn.getCurrency());
				final ShoppingItemPricingSnapshot itemPricingSnapshot = getPricingSnapshotService().getPricingSnapshotForOrderSku(shoppingItem);

				shoppingItems.put(shoppingItem, itemPricingSnapshot);
			}
		}

		return shoppingItems;
	}

	private OrderSku convertToShoppingItem(final OrderReturnSku orderReturnSku, final Currency currency) {
		final OrderSkuFactory orderSkuFactory = getBeanFactory().getBean(ContextIdNames.ORDER_SKU_FACTORY);
		final ProductSku productSku = getProductSkuLookup().findByGuid(orderReturnSku.getOrderSku().getSkuGuid());
		final OrderSku orderSku = orderSkuFactory.createOrderSku(
				productSku,
				convertToPrice(orderReturnSku, currency),
				1,
				orderReturnSku.getOrderSku().getOrdering(),
				orderReturnSku.getOrderSku().getFields());

		orderSku.setGuid(orderReturnSku.getGuid());

		return orderSku;
	}

	/**
	 * Takes the returnAmount from the OrderReturnSku object and populates a price based on it.
	 * @param orderReturnSku The OrderReturnSku instance to get the pricing information for
	 * @return A Price instance representing the returnAmount of the OrderReturnSku
	 */
	private Price convertToPrice(final OrderReturnSku orderReturnSku, final Currency currency) {
		Price price = getBeanFactory().getBean(ContextIdNames.PRICE);

		BigDecimal returnUnitPrice = orderReturnSku.getReturnAmount();

		Money listPrice = Money.valueOf(returnUnitPrice, currency);

		price.setListPrice(listPrice);
		price.setCurrency(currency);
		return price;
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

	protected ProductSkuLookup getProductSkuLookup() {
		return productSkuLookup;
	}

	public void setProductSkuLookup(final ProductSkuLookup productSkuLookup) {
		this.productSkuLookup = productSkuLookup;
	}

	public TaxAddressAdapter getAddressAdapter() {
		return addressAdapter;
	}

	public void setAddressAdapter(final TaxAddressAdapter addressAdapter) {
		this.addressAdapter = addressAdapter;
	}

	protected PricingSnapshotService getPricingSnapshotService() {
		return pricingSnapshotService;
	}

	public void setPricingSnapshotService(final PricingSnapshotService pricingSnapshotService) {
		this.pricingSnapshotService = pricingSnapshotService;
	}

}
