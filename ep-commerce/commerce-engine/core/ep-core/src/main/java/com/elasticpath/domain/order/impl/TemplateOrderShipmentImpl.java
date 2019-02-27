/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.domain.order.impl;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.OrderShipmentStatus;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.OrderTaxValue;
import com.elasticpath.domain.shipping.ShipmentType;
import com.elasticpath.money.Money;
import com.elasticpath.plugin.tax.domain.TaxDocumentId;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.service.tax.TaxCalculationResult;

/**
 * POJO version of OrderShipmentImpl for use within an OrderPayment template object that is passed to the
 * CheckoutService.
 */
public class TemplateOrderShipmentImpl implements OrderShipment {
	private static final long serialVersionUID = 5000000101L;
	private String shipmentNumber;
	private final Set<OrderSku> shipmentOrderSkus = new LinkedHashSet<>();

	@Override
	public String getShipmentNumber() {
		return shipmentNumber;
	}

	@Override
	public void setShipmentNumber(final String shipmentNumber) {
		this.shipmentNumber = shipmentNumber;
	}

	@Override
	public Set<OrderSku> getShipmentOrderSkus() {
		return Collections.unmodifiableSet(shipmentOrderSkus);
	}

	@Override
	public void addShipmentOrderSku(final OrderSku shipmentOrderSku) {
		shipmentOrderSkus.add(shipmentOrderSku);
	}

	@Override
	public void removeShipmentOrderSku(final OrderSku shipmentOrderSku, final ProductSkuLookup productSkuLookup) {
		throw new UnsupportedOperationException();
	}

	@Override
	public long getUidPk() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setUidPk(final long uidPk) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isPersisted() {
		return false;
	}

	@Override
	public ShipmentType getOrderShipmentType() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Date getCreatedDate() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setCreatedDate(final Date createdDate) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Date getLastModifiedDate() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setLastModifiedDate(final Date lastModifiedDate) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Date getShipmentDate() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setShipmentDate(final Date shipmentDate) {
		throw new UnsupportedOperationException();
	}

	@Override
	public OrderShipmentStatus getShipmentStatus() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setStatus(final OrderShipmentStatus status) {
		throw new UnsupportedOperationException();
	}

	@Override
	public BigDecimal getItemSubtotal() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Money getItemSubtotalMoney() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isInclusiveTax() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setInclusiveTax(final boolean inclusiveTax) {
		throw new UnsupportedOperationException();
	}

	@Override
	public BigDecimal getSubtotalDiscount() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setSubtotalDiscount(final BigDecimal subtotalDiscount) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Money getSubtotalDiscountMoney() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasSubtotalDiscount() {
		throw new UnsupportedOperationException();
	}

	@Override
	public BigDecimal getItemTax() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Money getItemTaxMoney() {
		throw new UnsupportedOperationException();
	}

	@Override
	public BigDecimal getSubtotal() {
		throw new UnsupportedOperationException();
	}

	@Override
	public BigDecimal getTotal() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Money getTotalMoney() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Money getTotalBeforeTaxMoney() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Money getItemSubTotalBeforeTaxMoney() {
		throw new UnsupportedOperationException();
	}

	@Override
	public BigDecimal getItemSubTotalBeforeTax() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Order getOrder() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setOrder(final Order order) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<OrderTaxValue> getShipmentTaxes() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Money getTotalTaxMoney() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isCancellable() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isReadyForFundsCapture() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Money getSubtotalMoney() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Money getSubtotalBeforeTaxMoney() {
		throw new UnsupportedOperationException();
	}

	@Override
	public BigDecimal getSubtotalBeforeTax() {
		throw new UnsupportedOperationException();
	}

	@Override
	public TaxDocumentId getTaxDocumentId() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void resetTaxDocumentId() {
		throw new UnsupportedOperationException();
	}

	@Override
	public TaxCalculationResult calculateTaxes() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean isRefundable() {
		throw new UnsupportedOperationException();
	}
}