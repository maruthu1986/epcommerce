/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.order.impl;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PostLoad;
import javax.persistence.PostUpdate;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.openjpa.persistence.DataCache;
import org.apache.openjpa.persistence.ElementDependent;
import org.apache.openjpa.persistence.FetchAttribute;
import org.apache.openjpa.persistence.FetchGroup;
import org.apache.openjpa.persistence.FetchGroups;
import org.apache.openjpa.persistence.jdbc.ElementForeignKey;
import org.apache.openjpa.persistence.jdbc.ElementJoinColumn;
import org.apache.openjpa.persistence.jdbc.ForeignKey;

import com.elasticpath.domain.RecalculableObject;
import com.elasticpath.domain.catalog.DigitalAsset;
import com.elasticpath.domain.catalog.impl.DigitalAssetImpl;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.impl.AbstractItemData;
import com.elasticpath.domain.impl.AbstractShoppingItemImpl;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.shoppingcart.PriceCalculator;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.ShoppingItemRecurringPrice;
import com.elasticpath.domain.shoppingcart.ShoppingItemTaxSnapshot;
import com.elasticpath.domain.shoppingcart.TaxPriceCalculator;
import com.elasticpath.domain.shoppingcart.impl.AbstractPriceCalculatorImpl;
import com.elasticpath.domain.shoppingcart.impl.ShoppingItemRecurringPriceImpl;
import com.elasticpath.money.Money;
import com.elasticpath.persistence.support.FetchGroupConstants;
import com.elasticpath.service.catalog.ProductSkuLookup;


/**
 * Represents a quantity of SKUs in an Order.<br/>
 *
 * NOTE that the presence of the {@code DatabaseLastModifiedDate} means that whenever this object is saved or updated to the database
 * the lastModifiedDate will be set by the {@code LastModifiedPersistenceEngineImpl} if that class in configured in Spring.
 */
@Entity
@Table(name = OrderSkuImpl.TABLE_NAME)
@FetchGroups({
	@FetchGroup(
			name = FetchGroupConstants.ORDER_DEFAULT,
			attributes = {
					@FetchAttribute(name = "childOrderSkus", recursionDepth = -1),
					@FetchAttribute(name = "parent", recursionDepth = -1),
					@FetchAttribute(name = "displayName"),
					@FetchAttribute(name = "displaySkuOptions"),
					@FetchAttribute(name = "image"),
					@FetchAttribute(name = "skuCode"),
					@FetchAttribute(name = "allocatedQuantityInternal"),
					@FetchAttribute(name = "recurringPrices"),
					@FetchAttribute(name = "unitPriceInternal")
			},
			postLoad = true
	),
	@FetchGroup(name = FetchGroupConstants.ORDER_INDEX, attributes = {
		@FetchAttribute(name = "shipment"),
		@FetchAttribute(name = "skuCode"),
		@FetchAttribute(name = "recurringPrices")
	}),
	@FetchGroup(name = FetchGroupConstants.ORDER_SEARCH, attributes = {
			@FetchAttribute(name = "skuCode"),
			@FetchAttribute(name = "displayName"),
			@FetchAttribute(name = "recurringPrices")
	})
})
@DataCache(enabled = false)
@SuppressWarnings({ "PMD.TooManyFields", "PMD.ExcessiveClassLength", "PMD.ExcessiveImports", "PMD.GodClass" })
public class OrderSkuImpl extends AbstractShoppingItemImpl implements OrderSku, RecalculableObject {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 7000000002L;

	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TORDERSKU";

	private static final String FK_COLUMN_NAME = "ORDERSKU_UID";

	private static final int DEFAULT_NUM_FRACTIONAL_DIGITS = 2;

	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

	private OrderShipment shipment;

	private Date createdDate;

	private CmUser lastModifiedBy;

	private String skuCode;

	private int returnableQuantity;

	private BigDecimal amount;

	private BigDecimal unitPrice;

	private String displayName;

	private String displaySkuOptions;

	private String image;

	private int weight;

	private DigitalAsset digitalAsset;

	private String taxCode;

	private int allocatedQuantity;

	private long uidPk;

	private boolean recalculationEnabled;

	private int changedQuantityAllocated;

	private int preOrBackOrderQuantity;

	private Map<String, AbstractItemData> fieldValues = new HashMap<>();

	private List<OrderSku> childOrderSkus = new ArrayList<>();

	private OrderSku parent;

	private Set<ShoppingItemRecurringPrice> recurringPrices = new HashSet<>();

	/**
	 * Internal accessor used by JPA.
	 *
	 * @return the set of dependent order skus.
	 */
	@OneToMany(targetEntity = OrderSkuImpl.class, cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, mappedBy = "parent")
	@OrderBy("ordering")
	protected List<OrderSku> getChildOrderSkus() {
		return this.childOrderSkus;
	}

	/**
	 * Internal accessor used by JPA.
	 *
	 * @param orderSkus the set of dependent order skus.
	 */
	protected void setChildOrderSkus(final List<OrderSku> orderSkus) {
		this.childOrderSkus = orderSkus;
	}

	/**
	 * @return the list of order skus as shopping items
	 */
	@Transient
	@Override
	protected List<ShoppingItem> getChildItemsInternal() {
		List<ShoppingItem> items = new ArrayList<>();
		for (OrderSku orderSku : getChildOrderSkus()) {
			items.add(orderSku);
		}
		return items;
	}

	/**
	 *
	 * @param childItems the set of dependent cart items.
	 */
	@Transient
	@Override
	protected void setChildItemsInternal(final List<ShoppingItem> childItems) {
		this.getChildOrderSkus().clear();
		if (childItems != null) {
			for (ShoppingItem item : childItems) {
				OrderSkuImpl orderSku = (OrderSkuImpl) item;
				orderSku.setParent(this);
				getChildOrderSkus().add(orderSku);
			}
		}
	}

	@Override
	public void addChildItem(final ShoppingItem newChildItem) {
		OrderSkuImpl orderSku = (OrderSkuImpl) newChildItem;
		orderSku.setParent(this);
		getChildOrderSkus().add(orderSku);
	}

	@Override
	public void removeChildItem(final ShoppingItem childItem) {
		getChildOrderSkus().remove(childItem);
	}

	@Transient
	@Override
	public List<ShoppingItem> getBundleItems(final ProductSkuLookup productSkuLookup) {
		return Collections.unmodifiableList(getChildItemsInternal());
	}

	@Transient
	@Override
	public void setBundleItems(final List<ShoppingItem> bundleItems) {
		setChildItemsInternal(bundleItems);
	}

	/**
	 * Sets the {@code OrderItemData} - for JPA.
	 * @param itemData the cart item data
	 */
	protected void setItemData(final Map<String, AbstractItemData> itemData) {
		this.fieldValues = itemData;
	}

	@Override
	protected AbstractItemData createItemData(final String name, final String value) {
		return new OrderItemData(name, value);
	}

	/**
	 * Internal JPA method to get Item Data.
	 * @return the item data
	 */
	@Override
	@OneToMany(targetEntity = OrderItemData.class, cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@MapKey(name = "key")
	@ElementJoinColumn(name = FK_COLUMN_NAME, nullable = false)
	@ElementForeignKey
	@ElementDependent
	protected Map<String, AbstractItemData> getItemData() {
		return this.fieldValues;
	}

	@Transient
	@Override
	public Map<String, String> getFields() {
		Map<String, String> fields = new HashMap<>();
		for (String key : getItemData().keySet()) {
			fields.put(key, getItemData().get(key).getValue());
		}
		return Collections.unmodifiableMap(fields);
	}

	/**
	 * Get the date that this order was created on.
	 *
	 * @return the created date
	 */
	@Override
	@Basic
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_DATE", nullable = false)
	public Date getCreatedDate() {
		if (this.createdDate == null) {
			createdDate = new Date();
		}
		return this.createdDate;
	}

	/**
	 * Set the date that the order is created.
	 *
	 * @param createdDate the start date
	 */
	@Override
	public void setCreatedDate(final Date createdDate) {
		this.createdDate = createdDate;
	}

	/**
	 * Get the CM user who last modifed this order sku.
	 *
	 * @return the CM user
	 */
	@Override
	@Transient
	public CmUser getLastModifiedBy() {
		return this.lastModifiedBy;
	}

	/**
	 * Set the CM User who last modified this order sku.
	 *
	 * @param modifiedBy the CM user
	 */
	@Override
	public void setLastModifiedBy(final CmUser modifiedBy) {
		this.lastModifiedBy = modifiedBy;
	}

	/**
	 * Get the productSku code.
	 *
	 * @return the productSku code
	 */
	@Override
	@Basic
	@Column(name = "SKUCODE")
	public String getSkuCode() {
		return this.skuCode;
	}

	@Override
	public void setSkuCode(final String code) {
		this.skuCode = code;
	}

	/**
	 * Set the quantity of this item as a primitive int. Required by Spring Validator.
	 *
	 * @param quantity the quantity
	 */
	@Override
	public void setQuantity(final int quantity) {
		super.setQuantity(quantity);
		recalculate();
	}

	/**
	 * Get the amount for this sku (Price * Quantity).
	 *
	 * @return the amount
	 * @deprecated Use getInvoiceItemAmount() instead.
	 */
	@Basic
	@Column(name = "AMOUNT", scale = DECIMAL_PRECISION, precision = DECIMAL_SCALE)
	@Deprecated
	protected BigDecimal getAmount() {
		return this.amount;
	}

	/**
	 * Set the amount for this sku (Price * Quantity).
	 *
	 * @param amount the amount
	 */
	protected void setAmount(final BigDecimal amount) {
		this.amount = amount;
	}

	@Transient
	@Override
	@Deprecated
	public Money getTotal() {
		return makeMoney(getInvoiceItemAmount());
	}

	/**
	 * Get the unit price for this sku.
	 *
	 * @return the price
	 */
	@Basic
	@Column(name = "UNIT_PRICE", scale = DECIMAL_PRECISION, precision = DECIMAL_SCALE)
	protected BigDecimal getUnitPriceInternal() {
		return unitPrice;
	}

	/**
	 * Set the unit price for this sku.
	 *
	 * @param price the price
	 */
	protected void setUnitPriceInternal(final BigDecimal price) {
		unitPrice = price;
	}

	/**
	 * Get the unit price for this sku.
	 *
	 * @return the price
	 */
	@Override
	@Transient
	public BigDecimal getUnitPrice() {
		return getUnitPriceInternal();
	}

	/**
	 * Get the unit price as a <code>Money</code> object.
	 *
	 * @return a <code>Money</code> object representing the unit price
	 * @deprecated Call getUnitPriceCalc().getMoney() instead.
	 */
	@Override
	@Transient
	@Deprecated
	public Money getUnitPriceMoney() {
		return makeMoney(getUnitPrice());
	}

	/**
	 * Set the unit price for this sku.
	 *
	 * @param price the price
	 */
	@Override
	@Transient
	public void setUnitPrice(final BigDecimal price) {
		setUnitPriceInternal(price);
		recalculate();
	}

	/**
	 *
	 * @param discount the discount to set
	 */
	@Override
	@Transient
	public void setDiscountBigDecimal(final BigDecimal discount) {
		setDiscountInternal(discount);
		recalculate();
	}

	/**
	 * @return the BigDecimal discount amount
	 */
	@Override
	@Transient
	public BigDecimal getDiscountBigDecimal() {
		return getDiscountInternal();
	}

	/**
	 * Get the product's display name.
	 *
	 * @return the product's display name.
	 */
	@Override
	@Basic
	@Column(name = "DISPLAY_NAME")
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * Set the product's display name.
	 *
	 * @param displayName the product's display name
	 */
	@Override
	public void setDisplayName(final String displayName) {
		this.displayName = displayName;
	}

	/**
	 * Get the product's option values for display.
	 *
	 * @return the product's option values for display.
	 */
	@Override
	@Basic
	@Column(name = "DISPLAY_SKU_OPTIONS")
	public String getDisplaySkuOptions() {
		return this.displaySkuOptions;
	}

	/**
	 * Set the product's option values for display.
	 *
	 * @param displaySkuOptions the product's option values for display
	 */
	@Override
	public void setDisplaySkuOptions(final String displaySkuOptions) {
		this.displaySkuOptions = displaySkuOptions;
	}

	/**
	 * Get the product's image path.
	 *
	 * @return the product's image path.
	 */
	@Override
	@Basic
	@Column(name = "IMAGE")
	public String getImage() {
		return this.image;
	}

	/**
	 * Set the product's image path.
	 *
	 * @param image the product's image path
	 */
	@Override
	public void setImage(final String image) {
		this.image = image;
	}

	/**
	 * Returns the shipping weight.
	 *
	 * @return the shipping weight.
	 */
	@Override
	@Basic
	@Column(name = "WEIGHT")
	public int getWeight() {
		return this.weight;
	}

	/**
	 * Sets the shipping weight.
	 *
	 * @param weight the shipping weight to set.
	 */
	@Override
	public void setWeight(final int weight) {
		this.weight = weight;
	}

	/**
	 * Calculates the <code>Money</code> savings if the price has a discount.
	 *
	 * @return the price savings as a <code>Money</code>
	 */
	@Override
	@Transient
	public Money getDollarSavingsMoney() {
		return makeMoney(getSavings());
	}

	/**
	 * Calculates the <code>BigDecimal</code> savings if any.
	 *
	 * @return the price savings as a <code>BigDecimal</code>
	 */
	@Override
	@Transient
	public BigDecimal getSavings() {
		BigDecimal savings = BigDecimal.ZERO;
		if (getListUnitPrice() != null) {
			final BigDecimal goodsItemTotal = getListUnitPrice().getRawAmount().multiply(new BigDecimal(getQuantity()));
			if (goodsItemTotal.compareTo(getInvoiceItemAmount()) > 0) {
				savings = goodsItemTotal.subtract(getInvoiceItemAmount());
			} else {
				savings = BigDecimal.ZERO;
			}
		}
		return savings;
	}

	/**
	 * Gets the digital asset belong to this order SKU.
	 *
	 * @return the digital asset belong to this order SKU
	 */
	@Override
	@ManyToOne(targetEntity = DigitalAssetImpl.class, fetch = FetchType.EAGER)
	@JoinColumn(name = "DIGITAL_ASSET_UID")
	public DigitalAsset getDigitalAsset() {
		return this.digitalAsset;
	}

	/**
	 * Sets the digital asset.
	 *
	 * @param digitalAsset the digital asset
	 */
	@Override
	public void setDigitalAsset(final DigitalAsset digitalAsset) {
		this.digitalAsset = digitalAsset;
	}

	/**
	 * Gets the tax code for this order SKU.
	 *
	 * @return the tax code for this order SKU.
	 */
	@Override
	@Basic
	@Column(name = "TAXCODE", nullable = false)
	public String getTaxCode() {
		return this.taxCode;
	}

	/**
	 * Sets the tax code for this order SKU.
	 *
	 * @param taxCode the tax code for this order SKU.
	 */
	@Override
	public void setTaxCode(final String taxCode) {
		this.taxCode = taxCode;
	}

	/**
	 * Gets the unique identifier for this domain model object.
	 *
	 * @return the unique identifier.
	 */
	@Override
	@Id
	@Column(name = "UIDPK")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@TableGenerator(name = TABLE_NAME, table = "JPA_GENERATED_KEYS", pkColumnName = "ID",
			valueColumnName = "LAST_VALUE", pkColumnValue = TABLE_NAME, allocationSize = HIGH_CONCURRENCY_ALLOCATION_SIZE)
	public long getUidPk() {
		return this.uidPk;
	}

	/**
	 * Sets the unique identifier for this domain model object.
	 *
	 * @param uidPk the new unique identifier.
	 */
	@Override
	public void setUidPk(final long uidPk) {
		this.uidPk = uidPk;
	}

	@Override
	@Transient
	public int getReturnableQuantity() {
		return returnableQuantity;
	}

	@Override
	public void setReturnableQuantity(final int returnableQuantity) {
		this.returnableQuantity = returnableQuantity;
	}

	@Override
	public void copyFrom(final OrderSku orderSku, final ProductSkuLookup productSkuLookup, final ShoppingItemTaxSnapshot taxSnapshot) {
		copyFrom(orderSku, productSkuLookup, taxSnapshot, true);
	}

	@Override
	public void copyFrom(final OrderSku orderSku, final ProductSkuLookup productSkuLookup,
						final ShoppingItemTaxSnapshot taxSnapshot, final boolean setPricingInfo) {
		if (orderSku.isBundle(productSkuLookup)) {
			throw new IllegalArgumentException();
		}
		if (orderSku.getParent() != null) {
			orderSku.getParent().addChildItem(this);
		}
		this.setAllocatedQuantity(orderSku.getAllocatedQuantity());
		this.setCreatedDate(orderSku.getCreatedDate());
		this.setDigitalAsset(orderSku.getDigitalAsset());
		this.setDisplayName(orderSku.getDisplayName());
		this.setDisplaySkuOptions(orderSku.getDisplaySkuOptions());
		this.setImage(orderSku.getImage());
		this.setLastModifiedBy(orderSku.getLastModifiedBy());
		this.setLastModifiedDate(orderSku.getLastModifiedDate());
		this.setDiscountBigDecimal(taxSnapshot.getPricingSnapshot().getDiscount().getRawAmount());
		this.setSkuGuid(orderSku.getSkuGuid());
		this.setSkuCode(orderSku.getSkuCode());
		// sets quantity, currency and prices
		if (setPricingInfo) {
			this.setPrice(orderSku.getQuantity(), taxSnapshot.getPricingSnapshot().getPrice());
		}
		this.setTaxAmount(taxSnapshot.getTaxAmount());
		if (setPricingInfo) {
			this.setUnitPrice(orderSku.getUnitPrice());
		}
		this.setWeight(orderSku.getWeight());
		this.setTaxCode(orderSku.getTaxCode());
		copyDataFieldsFrom(orderSku);
	}

	private void copyDataFieldsFrom(final OrderSku orderSku) {
		for (String key : orderSku.getFields().keySet()) {
			this.setFieldValue(key, orderSku.getFieldValue(key));
		}
	}

	/**
	 * @return the shipment that this order sku is part of
	 */
	@Override
	@ManyToOne(targetEntity = AbstractOrderShipmentImpl.class, fetch = FetchType.EAGER,
				cascade = { CascadeType.REFRESH, CascadeType.MERGE })
	@JoinColumn(name = "ORDER_SHIPMENT_UID")
	@ForeignKey(name = "TORDERSKU_IBFK_1")
	public OrderShipment getShipment() {
		return shipment;
	}

	/**
	 * @param shipment the shipment to set
	 */
	@Override
	public void setShipment(final OrderShipment shipment) {
		this.shipment = shipment;
	}

	/**
	 * Recalculate taxes and totals when modifications are made to the shipment.
	 */
	private void recalculate() {
		if (isRecalculationEnabled()) {
			BigDecimal amount = getInvoiceItemAmount();
			BigDecimal oldAmount = getAmount();
			setAmount(amount);
			firePropertyChange("amount", oldAmount, amount); //$NON-NLS-1$
		}
	}

	@Override
	@Transient
	@Deprecated
	public BigDecimal getInvoiceItemAmount() {
		BigDecimal amount = BigDecimal.ZERO;
		if (getUnitPrice() != null) {
			amount = getUnitPrice().multiply(new BigDecimal(getQuantity()));
			final BigDecimal discount = getDiscountInternal();
			if (discount != null) {
				if (amount.compareTo(discount) < 0) {
					amount = BigDecimal.ZERO;
				} else {
					amount = amount.subtract(discount);
				}
			}
		}
		return amount;
	}

	@Override
	@PostLoad
	@PostUpdate
	public void enableRecalculation() {
		recalculationEnabled = true;
		recalculate();
	}

	@Override
	@PreUpdate
	public void disableRecalculation() {
		recalculationEnabled = false;
	}

	/**
	 * @return true if recalculation is enabled and necessary data is loaded, otherwise false
	 */
	@Transient
	protected boolean isRecalculationEnabled() {
		return recalculationEnabled;
	}

	/**
	 * JPA accessor for the allocated quantity.
	 *
	 * @return the allocated quantity
	 */
	@Basic
	@Column(name = "ALLOCATED_QUANTITY")
	protected int getAllocatedQuantityInternal() {
		return this.allocatedQuantity;
	}

	/**
	 * JPA accessor for setting the allocated quantity.
	 *
	 * @param qty the quantity to allocate.
	 */
	protected void setAllocatedQuantityInternal(final int qty) {
		this.allocatedQuantity = qty;
	}

	@Override
	@Transient
	public int getAllocatedQuantity() {
		return this.getAllocatedQuantityInternal();
	}

	@Override
	public void setAllocatedQuantity(final int qty) {
		int oldQty = this.allocatedQuantity;
		this.setAllocatedQuantityInternal(qty);
		firePropertyChange("orderInventoryAllocation", oldQty, qty);
	}

	/**
	 *
	 *
	 * @return
	 */
	@Override
	@Transient
	public boolean isAllocated() {
		return this.getQuantity() <= this.getAllocatedQuantity() + this.getChangedQuantityAllocated();
	}

	/**
	 * Get the changedQuantityAllocated. changedQuantityAllocated is the amount of allocated quantity increased/decreased based on quantityAllocated
	 *
	 * @return <CODE>changedQuantityAllocated</CODE>
	 */
	@Override
	@Transient
	public int getChangedQuantityAllocated() {
		return this.changedQuantityAllocated;
	}

	/**
	 * Set the changedQuantityAllocated. changedQuantityAllocated is the amount of allocated quantity increased/decreased based on quantityAllocated
	 *
	 * @param changedQuantityAllocated the amount of allocated quantity increased/decreased upon quanittyAllocated
	 */
	@Override
	public void setChangedQuantityAllocated(final int changedQuantityAllocated) {
		this.changedQuantityAllocated = changedQuantityAllocated;
	}

	/**
	 * Used for splitting shipment. get preOrBackOrderQuantity from this order sku
	 *
	 * @return preOrBackOrderQuantity
	 */
	@Override
	@Transient
	public int getPreOrBackOrderQuantity() {
		return this.preOrBackOrderQuantity;
	}

	/**
	 * Used for splitting shipment. set preOrBackOrderQuantity for this order sku
	 *
	 * @param preOrBackOrderQuantity preOrBackOrderQuantity
	 */
	@Override
	public void setPreOrBackOrderQuantity(final int preOrBackOrderQuantity) {
		this.preOrBackOrderQuantity = preOrBackOrderQuantity;
	}

	@Override
	@SuppressWarnings("PMD.UselessOverridingMethod")
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof OrderSkuImpl)) {
			return false;
		}
		return super.equals(obj);
	}

	@Override
	public void addPropertyChangeListener(final PropertyChangeListener listener) {
		addPropertyChangeListener(listener, true);
	}

	@Override
	public void addPropertyChangeListener(final PropertyChangeListener listener, final boolean replace) {
		if (replace) {
			propertyChangeSupport.removePropertyChangeListener(listener);
		}
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	@Override
	public void addPropertyChangeListener(final String propertyName, final PropertyChangeListener listener) {
		addPropertyChangeListener(propertyName, listener, true);
	}

	@Override
	public void addPropertyChangeListener(final String propertyName, final PropertyChangeListener listener, final boolean replace) {
		if (replace) {
			propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
		}
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	@Override
	public void removePropertyChangeListener(final PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	@Override
	public void removePropertyChangeListener(final String propertyName, final PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
	}

	/**
	 * Notify listeners of a property change.
	 *
	 * @param propertyName the name of the property that is being changed
	 * @param oldValue the old value
	 * @param newValue the new value
	 */
	protected void firePropertyChange(final String propertyName, final Object oldValue, final Object newValue) {
		propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
	}

	/**
	 *  @return the parent if this is a dependent item, otherwise null
	*/
	@Override
	@ManyToOne(targetEntity = OrderSkuImpl.class, cascade = { CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST },
													fetch = FetchType.LAZY)
	@JoinTable(name = "TORDERSKUPARENT",
				joinColumns = @JoinColumn(name = "CHILD_UID", referencedColumnName = "UIDPK", nullable = false),
				inverseJoinColumns = @JoinColumn(name = "PARENT_UID", referencedColumnName = "UIDPK", nullable = false))
	public OrderSku getParent() {
		return this.parent;
	}

	/**
	 * @param parent the parent OrderSku
	*/
	protected void setParent(final OrderSku parent) {
		this.parent = parent;
	}

	/**
	 *  @return the root if this is a dependent item, otherwise null
	*/
	@Override
	@Transient
	public OrderSku getRoot() {
		OrderSku orderSku = getParent();
		while (orderSku != null && orderSku.getParent() != null) {
			orderSku = orderSku.getParent();
		}
		return orderSku;
	}

	/**
	 * Get the property change support object.
     *
	 * @return the propertyChangeSupport
	 */
	@Transient
	public PropertyChangeSupport getPropertyChangeSupport() {
		return propertyChangeSupport;
	}

	/**
	 *	Set the property change support object.
	 *
	 * @param propertyChangeSupport the propertyChangeSupport to set
	 */
	public void setPropertyChangeSupport(final PropertyChangeSupport propertyChangeSupport) {
		this.propertyChangeSupport = propertyChangeSupport;
	}

	@Override
	@OneToMany(targetEntity = ShoppingItemRecurringPriceImpl.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@ElementJoinColumn(name = FK_COLUMN_NAME, nullable = true)
	@ElementForeignKey
	@ElementDependent
	protected Set<ShoppingItemRecurringPrice> getRecurringPrices() {
		if (recurringPrices == null) {
			recurringPrices = new HashSet<>();
		}
		return recurringPrices;
	}

	@Override
	protected void setRecurringPrices(final Set<ShoppingItemRecurringPrice> recurringPrices) {
		this.recurringPrices = recurringPrices;
	}

	@Override
	@Transient
	public PriceCalculator getPriceCalc() {
		return new PriceCalculatorImpl(false);
	}

	@Override
	@Transient
	public TaxPriceCalculator getTaxPriceCalculator() {
		return new PriceCalculatorImpl(true);
	}

	/**
	 * Price calculator implementation used by getPriceCalc().
	 */
	private class PriceCalculatorImpl extends AbstractPriceCalculatorImpl implements TaxPriceCalculator {

		private final boolean taxAware;

		/**
		 * Default constructor.
		 */
		PriceCalculatorImpl(final boolean taxAware) {
			super();
			this.taxAware = taxAware;
		}

		@Override
		public BigDecimal getAmount() {
			// We intentionally use getUnitPrice instead of findLowestUnitPrice because we want to use the persisted value.
			BigDecimal amount = getUnitPrice();

			if (amount == null) {
				return null;
			}

			if (isUnitPriceOnly()) {
				if (isIncludeCartDiscounts() && getQuantity() > 0 && getDiscount() != null) {
					BigDecimal quantity = BigDecimal.valueOf(getQuantity());
					BigDecimal discountAmount = getDiscount().getAmount();
					BigDecimal discount = discountAmount.divide(quantity, DEFAULT_NUM_FRACTIONAL_DIGITS, RoundingMode.HALF_EVEN)
						.setScale(2, RoundingMode.HALF_EVEN);
					amount = amount.subtract(discount);
				}

				if (taxAware) {
					if (getShipment().isInclusiveTax()) {
						amount = amount.subtract(getUnitTax());
					} else {
						amount = amount.add(getUnitTax());
					}
				}

			} else {
				BigDecimal quantity = BigDecimal.valueOf(getQuantity());
				amount = amount.multiply(quantity);

				if (isIncludeCartDiscounts() && getDiscount() != null) {
					amount = amount.subtract(getDiscount().getAmount());
				}

				if (taxAware) {
					if (getShipment().isInclusiveTax()) {
						amount = amount.subtract(getTaxAmount());
					} else {
						amount = amount.add(getTaxAmount());
					}
				}

			}

			if (amount != null && amount.compareTo(BigDecimal.ZERO) < 0) {
				amount = BigDecimal.ZERO;
			}

			return amount;
		}

		@Override
		public Money getMoney() {
			return makeMoney(getAmount());
		}
	}

}
