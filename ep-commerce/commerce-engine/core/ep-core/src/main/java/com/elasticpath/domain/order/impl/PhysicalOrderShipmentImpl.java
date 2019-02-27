/*
 * Copyright (c) Elastic Path Software Inc., 2007-2014
 */
package com.elasticpath.domain.order.impl;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.log4j.Logger;
import org.apache.openjpa.persistence.DataCache;
import org.apache.openjpa.persistence.FetchAttribute;
import org.apache.openjpa.persistence.FetchGroup;
import org.apache.openjpa.persistence.FetchGroups;
import org.apache.openjpa.persistence.jdbc.ForeignKey;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.order.OrderAddress;
import com.elasticpath.domain.order.OrderShipmentStatus;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.domain.shipping.ShipmentType;
import com.elasticpath.domain.shoppingcart.ShoppingItemPricingSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingItemTaxSnapshot;
import com.elasticpath.money.Money;
import com.elasticpath.persistence.support.FetchGroupConstants;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.service.shoppingcart.PricingSnapshotService;
import com.elasticpath.service.shoppingcart.TaxSnapshotService;
import com.elasticpath.service.tax.TaxCalculationResult;
import com.elasticpath.service.tax.TaxOperationService;

/**
 * <code>PhysicalOrderShipmentImpl</code> represents a customer's order shipment for shippable goods.
 */
@Entity
@DiscriminatorValue(ShipmentType.PHYSICAL_STRING)
@FetchGroups({
		@FetchGroup(name = FetchGroupConstants.ORDER_INDEX, attributes = { @FetchAttribute(name = "shipmentAddressInternal") }),
		@FetchGroup(name = FetchGroupConstants.ORDER_DEFAULT, attributes = { @FetchAttribute(name = "shipmentAddressInternal") },
				fetchGroups = { FetchGroupConstants.DEFAULT }, postLoad = true) })
@DataCache(enabled = false)
@SuppressWarnings("PMD.GodClass")
public class PhysicalOrderShipmentImpl extends AbstractOrderShipmentImpl implements PhysicalOrderShipment {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;

	private static final Logger LOG = Logger.getLogger(PhysicalOrderShipmentImpl.class);

	private String carrierCode;
	private String carrierName;
	private String shippingOptionCode;
	private String shippingOptionName;
	private String trackingCode;
	private BigDecimal shippingCost = BigDecimal.ZERO.setScale(DECIMAL_SCALE);
	private BigDecimal beforeTaxShippingCost;
	private BigDecimal shippingTax;
	private OrderAddress shipmentAddress;
	private BigDecimal shippingSubtotal;

	private final Set<OrderSku> shipmentRemovedOrderSkus = new HashSet<>();

	/**
	 * Must be implemented by subclasses to return their type. (e.g. electronic or physical)
	 *
	 * @return the type of the order shipment subclass.
	 */
	@Override
	@Transient
	public ShipmentType getOrderShipmentType() {
		return ShipmentType.PHYSICAL;
	}

	/**
	 * Internal method for persistence layer use to get the shipping cost.
	 *
	 * @return the shipping cost in <code>BigDecimal</code>.
	 */
	@Basic
	@Column(name = "SHIPPING_COST", precision = DECIMAL_PRECISION, scale = DECIMAL_SCALE)
	protected BigDecimal getShippingCostInternal() {
		return shippingCost;
	}

	/**
	 * Internal method for the persistence layer to set the shipping cost.
	 *
	 * @param shippingCost the shipping cost
	 */
	protected void setShippingCostInternal(final BigDecimal shippingCost) {
		this.shippingCost = shippingCost;
	}

	@Override
	@Transient
	public BigDecimal getShippingCost() {
		final BigDecimal shippingCostInt = getShippingCostInternal();
		if (shippingCostInt == null) {
			return BigDecimal.ZERO;
		}
		return shippingCostInt;
	}

	@Override
	public void setShippingCost(final BigDecimal shippingCost) {
		BigDecimal oldShippingCost = getShippingCost();
		setShippingCostInternal(shippingCost);
		if (isRecalculationEnabled()) {
			firePropertyChange("shippingCost", oldShippingCost, shippingCost); //$NON-NLS-1$
			recalculate();
		}
	}

	@Override
	@Transient
	public Money getShippingCostMoney() {
		return Money.valueOf(getShippingCost(), getOrder().getCurrency());
	}

	@Override
	@Transient
	public Money getBeforeTaxShippingCostMoney() {
		return Money.valueOf(getBeforeTaxShippingCost(), getOrder().getCurrency());
	}

	@Override
	@Basic
	@Column(name = "BEFORE_TAX_SHIPPING_COST", precision = DECIMAL_PRECISION, scale = DECIMAL_SCALE)
	public BigDecimal getBeforeTaxShippingCost() {
		return beforeTaxShippingCost;
	}

	@Override
	public void setBeforeTaxShippingCost(final BigDecimal beforeTaxShippingCost) {
		this.beforeTaxShippingCost = beforeTaxShippingCost;
	}

	@Override
	@Basic
	@Column(name = "SHIPPING_TAX", precision = DECIMAL_PRECISION, scale = DECIMAL_SCALE)
	public BigDecimal getShippingTax() {
		return shippingTax;
	}

	@Override
	public void setShippingSubtotal(final BigDecimal shippingSubtotal) {
		this.shippingSubtotal = shippingSubtotal;
	}

	@Override
	@Basic
	@Column(name = "SHIPPING_SUBTOTAL", precision = DECIMAL_PRECISION, scale = DECIMAL_SCALE)
	public BigDecimal getShippingSubtotal() {
		return shippingSubtotal;
	}

	/**
	 * Set the shipping tax in <code>BigDecimal</code>.
	 *
	 * @param shippingTax the shipping tax
	 */
	public void setShippingTax(final BigDecimal shippingTax) {
		BigDecimal oldShippingTax = this.shippingTax;
		this.shippingTax = shippingTax;
		firePropertyChange("shippingTax", oldShippingTax, shippingTax); //$NON-NLS-1$
	}

	/**
	 * Get the shipment address corresponding to this shipment.
	 *
	 * @return the shipment address Uid
	 */
	@ManyToOne(targetEntity = OrderAddressImpl.class, cascade = { CascadeType.ALL })
	@JoinColumn(name = "ORDER_ADDRESS_UID")
	@ForeignKey(name = "TORDERSHIPMENT_IBFK_1")
	protected OrderAddress getShipmentAddressInternal() {
		return shipmentAddress;
	}

	/**
	 * Set the shipping address corresponding to this shipment.
	 *
	 * @param shipmentAddress the Uid of the corresponding shipment address.
	 */
	protected void setShipmentAddressInternal(final OrderAddress shipmentAddress) {
		this.shipmentAddress = shipmentAddress;
	}

	@Override
	@Transient
	public OrderAddress getShipmentAddress() {
		return getShipmentAddressInternal();
	}

	@Override
	public void setShipmentAddress(final OrderAddress shipmentAddress) {
		setShipmentAddressInternal(shipmentAddress);
		recalculate();
	}

	@Override
	@Basic
	@Column(name = "CARRIER_CODE")
	public String getCarrierCode() {
		return carrierCode;
	}

	@Override
	public void setCarrierCode(final String carrierCode) {
		this.carrierCode = carrierCode;
	}

	@Override
	@Basic
	@Column(name = "CARRIER_NAME")
	public String getCarrierName() {
		return carrierName;
	}

	@Override
	public void setCarrierName(final String carrierName) {
		this.carrierName = carrierName;
	}

	@Basic
	@Column(name = "SHIPPING_OPTION_CODE")
	public String getShippingOptionCode() {
		return shippingOptionCode;
	}

	public void setShippingOptionCode(final String shippingOptionCode) {
		this.shippingOptionCode = shippingOptionCode;
	}

	@Basic
	@Column(name = "SHIPPING_OPTION_NAME")
	public String getShippingOptionName() {
		return shippingOptionName;
	}

	public void setShippingOptionName(final String shippingOptionName) {
		this.shippingOptionName = shippingOptionName;
	}

	@Override
	@Basic
	@Column(name = "TRACKING_CODE")
	public String getTrackingCode() {
		return trackingCode;
	}

	@Override
	public void setTrackingCode(final String trackingCode) {
		this.trackingCode = trackingCode;
	}

	/**
	 * Updates all calculated values in the linked shipment (taxes, subtotals, etc.).
	 */
	@Override
	protected void recalculate() {
		if (isRecalculationEnabled()) {
			synchronized (this) {
				recalculateTaxesUsingLiveTaxRates();
			}
		}
	}

	/**
	 * Recalculate the tax amounts using the tax calculation service. <br>
	 * If this is an existing order it is possible that the tax rates have changed since the order was placed, which will change the tax values and
	 * the total prices for this order. <br>
	 * Thus, this method should only be called after a change which affects the price of the order.
	 */
	private void recalculateTaxesUsingLiveTaxRates() {
		if (isTaxRecalculationRequired()) {
			final TaxCalculationResult taxResult = calculateTaxes();

			taxResult.applyTaxes(getShipmentOrderSkus());

			setInclusiveTax(taxResult.isTaxInclusive());
			setItemTax(taxResult.getTotalItemTax().getAmount());
			setShippingTax(taxResult.getShippingTax().getAmount());

			updateTaxValues(taxResult);

			final Money beforeTaxShippingCostMoney = taxResult.getBeforeTaxShippingCost();
			final BigDecimal beforeTaxShippingCost = beforeTaxShippingCostMoney.getAmount();
			setBeforeTaxShippingCost(beforeTaxShippingCost);

			final BigDecimal itemSubtotal = taxResult.getSubtotal().getAmount();
			setItemSubtotal(itemSubtotal);
		}
		recalculateTransientDerivedValues();
	}

	/**
	 * This method recalculates values which are derived from persistent values. <br>
	 * It does the recalculation from data held by this object and does not go outside to get other information.
	 */
	@Override
	protected void recalculateTransientDerivedValues() {
		if (isRecalculationEnabled()) {
			// They are here rather than in the getters so we can fire a property change on when the setter is called.
			setSubtotal(getItemSubtotal());
			if (isInclusiveTax()) {
				setTotal(getSubtotal().subtract(getSubtotalDiscount()).add(getShippingCost()));
			} else {
				setTotal(getSubtotal().subtract(getSubtotalDiscount()).add(getShippingCost()).add(getItemTax()).add(getShippingTax()));
			}
			Money totalBeforeTaxMoney = Money.valueOf(
					getItemSubTotalBeforeTaxMoney().getAmount().add(getBeforeTaxShippingCost()).subtract(getSubtotalDiscount()),
					getOrder().getCurrency());
			setTotalBeforeTaxMoney(totalBeforeTaxMoney);

		}
	}

	/**
	 * Calculate the taxes for the order shipment.
	 *
	 * @return the tax result
	 */
	@Override
	@Transient
	public TaxCalculationResult calculateTaxes() {
		TaxOperationService taxOperationService = getBean(ContextIdNames.TAX_OPERATION_SERVICE);

		return taxOperationService.calculateTaxes(this);
	}

	@Override
	@Transient
	public Money getTotalTaxMoney() {
		BigDecimal totalTax = BigDecimal.ZERO.setScale(2);
		if (getItemTax() != null) {
			totalTax = totalTax.add(getItemTax());
		}
		if (getShippingTax() != null) {
			totalTax = totalTax.add(getShippingTax());
		}

		return Money.valueOf(totalTax, getOrder().getCurrency());
	}

	@Override
	@Transient
	public Set<OrderSku> getShipmentRemovedOrderSku() {
		return shipmentRemovedOrderSkus;
	}

	/**
	 * Remove shipment order sku from the shipment, and add it to removedShipmentOrderSku set.
	 *
	 * @param orderSku orderSku to be deleted
	 * @param productSkuLookup a product sku lookup
	 */
	@Override
	public void removeShipmentOrderSku(final OrderSku orderSku, final ProductSkuLookup productSkuLookup) {
		super.removeShipmentOrderSku(orderSku, productSkuLookup);
		OrderSku removedOrderSku = getBean(ContextIdNames.ORDER_SKU);

		final PricingSnapshotService pricingSnapshotService = getBean(ContextIdNames.PRICING_SNAPSHOT_SERVICE);
		final TaxSnapshotService taxSnapshotService = getBean(ContextIdNames.TAX_SNAPSHOT_SERVICE);
		ShoppingItemPricingSnapshot pricingSnapshotForOrderSku = pricingSnapshotService.getPricingSnapshotForOrderSku(orderSku);
		ShoppingItemTaxSnapshot taxSnapshotForOrderSku = taxSnapshotService.getTaxSnapshotForOrderSku(orderSku, pricingSnapshotForOrderSku);
		removedOrderSku.copyFrom(orderSku, productSkuLookup, taxSnapshotForOrderSku, false);
		shipmentRemovedOrderSkus.add(removedOrderSku);
	}

	@Override
	@Transient
	public Money getShippingTaxMoney() {
		return Money.valueOf(getShippingTax(), getOrder().getCurrency());
	}

	@SuppressWarnings("fallthrough")
	@Override
	@Transient
	public boolean isCancellable() {
		boolean cancellable = true;

		switch (getShipmentStatus().getOrdinal()) {
		case OrderShipmentStatus.CANCELLED_ORDINAL:
		case OrderShipmentStatus.SHIPPED_ORDINAL:
			cancellable = false;
			break;
		case OrderShipmentStatus.AWAITING_INVENTORY_ORDINAL:
		case OrderShipmentStatus.INVENTORY_ASSIGNED_ORDINAL:
		case OrderShipmentStatus.ONHOLD_ORDINAL:
		case OrderShipmentStatus.RELEASED_ORDINAL:
			cancellable = true;
			break;

		default:
			throw new EpSystemException("Error: unhandled shipment status: " + getShipmentStatus());

		}
		if (LOG.isDebugEnabled() && !cancellable) {
			LOG.debug("Cannot cancel orderShipment because it is already " + getShipmentStatus());
		}
		return cancellable;
	}

	/**
	 * Implements equals semantics.<br>
	 * This class more than likely would be extended to add functionality that would not effect the equals method in comparisons, and as such would
	 * act as an entity type. In this case, content is not crucial in the equals comparison. Using instanceof within the equals method enables
	 * comparison in the extended classes where the equals method can be reused without violating symmetry conditions. If getClass() was used in the
	 * comparison this could potentially cause equality failure when we do not expect it. If when extending additional fields are included in the
	 * equals method, then the equals needs to be overridden to maintain symmetry.
	 *
	 * @param obj the other object to compare
	 * @return true if equal
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof PhysicalOrderShipmentImpl)) {
			return false;
		}

		final PhysicalOrderShipmentImpl other = (PhysicalOrderShipmentImpl) obj;
		return Objects.equals(getOrderShipmentType(), other.getOrderShipmentType())
			&& super.equals(obj);
	}

	@Override
	public int hashCode() {
		return Objects.hash(getOrderShipmentType(), super.hashCode());
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
			.append("carrierCode", getCarrierCode())
			.append("carrierName", getCarrierName())
			.append("shippingOptionCode", getShippingOptionCode())
			.append("shippingOptionName", getShippingOptionName())
			.append("trackingCode", getTrackingCode())
			.append("shippingCost", getShippingCost())
			.append("beforeTaxShippingCost", getBeforeTaxShippingCost())
			.append("shippingTax", getShippingTax())
			.append("shipmentAddress", getShipmentAddress())
			.appendSuper(super.toString())
			.toString();
	}

	/**
	 * Returns a product sku lookup, retrieved from the global bean factory.  Sorry.
	 * @return a product sku lookup
	 */
	@Transient
	protected ProductSkuLookup getProductSkuLookup() {
		return getBean(ContextIdNames.PRODUCT_SKU_LOOKUP);
	}
}
