/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.shoppingcart.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
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

import com.elasticpath.domain.DatabaseCreationDate;
import com.elasticpath.domain.impl.AbstractItemData;
import com.elasticpath.domain.impl.AbstractShoppingItemImpl;
import com.elasticpath.domain.shoppingcart.ExchangeItem;
import com.elasticpath.domain.shoppingcart.PriceCalculator;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.ShoppingItemRecurringPrice;
import com.elasticpath.domain.shoppingcart.TaxPriceCalculator;
import com.elasticpath.money.Money;
import com.elasticpath.persistence.support.FetchGroupConstants;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.service.shoppingcart.impl.TaxHandlingEnum;

/**
 * A {@code ShoppingItem} represents a quantity of SKUs in a shopping cart.<br/>
 * 
 * NOTE that the presence of the {@code DatabaseLastModifiedDate} means that whenever this object is saved or updated to the database
 * the lastModifiedDate will be set by the {@code LastModifiedPersistenceEngineImpl} if that class in configured in Spring. 
 */
@Entity
@Table(name =  ShoppingItemImpl.TABLE_NAME)
@DataCache(enabled = false)
@FetchGroups({
	@FetchGroup(
			name = FetchGroupConstants.SHOPPING_ITEM_CHILD_ITEMS, 
			attributes = {
					@FetchAttribute(name = "childItemsInternal", recursionDepth = -1)
			}
	),
	@FetchGroup(
			name = FetchGroupConstants.ORDER_DEFAULT, 
			attributes = {
					@FetchAttribute(name = "recurringPrices")
			}
	)
})
@SuppressWarnings("PMD.GodClass")
public class ShoppingItemImpl extends AbstractShoppingItemImpl implements CartItem, ExchangeItem, DatabaseCreationDate {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 7000000002L;

	private static final int SCALE = 10;
	
	private static final String FK_COLUMN_NAME = "CARTITEM_UID";

	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TCARTITEM";

	private long uidPk;

	private List<ShoppingItem> childItems = new ArrayList<>();
	
	private Map<String, AbstractItemData> fieldValues = new HashMap<>();
	
	private Long cartUid;

	private Set<ShoppingItemRecurringPrice> recurringPrices = new HashSet<>();
	
	private boolean taxInclusive;

	private Date creationDate;

	/**
	 * Internal JPA method to get Item Data.
	 * @return the item data
	 */
	@OneToMany(targetEntity = ShoppingItemData.class, cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@MapKey(name = "key")
	@ElementJoinColumn(name = FK_COLUMN_NAME, nullable = false)
	@ElementForeignKey
	@ElementDependent
	@Override
	protected Map<String, AbstractItemData> getItemData() {
		return this.fieldValues;
	}
	
	/**
	 * Sets the {@code ShoppingItemData} - for JPA.
	 * @param itemData the cart item data
	 */
	protected void setItemData(final Map<String, AbstractItemData> itemData) {
		this.fieldValues = itemData;
	}

	@Override
	protected AbstractItemData createItemData(final String name, final String value) {
		return new ShoppingItemData(name, value);
	}

	/**
	 * Internal accessor used by JPA.
	 * 
	 * @return the set of dependent cart items.
	 */
	@OneToMany(targetEntity = ShoppingItemImpl.class, cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@ElementJoinColumn(name = "PARENT_ITEM_UID")
	@ElementForeignKey
	@ElementDependent
	@OrderBy("ordering")
	@Override
	protected List<ShoppingItem> getChildItemsInternal() { // the set should be of CartItem implementors
		return this.childItems;
	}

	/**
	 * Internal accessor used by JPA.
	 * 
	 * @param childItems the set of dependent cart items.
	 */
	@Override
	protected void setChildItemsInternal(final List<ShoppingItem> childItems) {
		this.childItems = childItems;
	}
	
	
	/**
	 * Gets the cart uid.
	 * 
	 * @return the cart uid
	 */
	@Override
	@Transient
	public Long getCartUid() {
		return cartUid;
	}
	
	/**
	 * Sets the cart uid.
	 * 
	 * @param cartUid the cart uid
	 */
	@Override
	@Transient
	public void setCartUid(final Long cartUid) {
		this.cartUid = cartUid;
	}

	@Transient
	@Override
	public List<ShoppingItem> getBundleItems(final ProductSkuLookup productSkuLookup) {
		return getChildren().stream()
				.filter(ShoppingItem::isBundleConstituent)
				.collect(Collectors.toList());
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
	@TableGenerator(name = TABLE_NAME, table = "JPA_GENERATED_KEYS", pkColumnName = "ID", valueColumnName = "LAST_VALUE", pkColumnValue = TABLE_NAME)
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

	/**
	 * Return the hash code.
	 * 
	 * @return the hash code
	 */
	@Override
	@SuppressWarnings("PMD.UselessOverridingMethod")
	public int hashCode() {
		return super.hashCode();
	}

	/**
	 * Return{@code true} if the given object is a {@code ShoppingItemImpl} and is logically equal.
	 * 
	 * @param obj the object to compare
	 * @return <code>true</code> if the given object is equal
	 */
	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof ShoppingItemImpl)) {
			return false;
		}
		return super.equals(obj);
	}

	/**
	 * Get the recurring prices.
	 * @return the recurring prices
	 */
	@OneToMany(targetEntity = ShoppingItemRecurringPriceImpl.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@ElementJoinColumn(name = FK_COLUMN_NAME, nullable = true)
	@ElementForeignKey
	@ElementDependent
	@Override
	protected Set<ShoppingItemRecurringPrice> getRecurringPrices() {
		return recurringPrices;
	}

	@Override
	protected void setRecurringPrices(final Set<ShoppingItemRecurringPrice> recurringPrices) {
		this.recurringPrices = recurringPrices;
	}
	/**
	 * Is the site tax inclusive or not?  This property will be set whenever a cart item is created from a cart.
	 * @return true if site is tax inclusive, false if it is tax exclusive
	 */
	@Transient
	protected boolean isTaxInclusive() {
		return taxInclusive;
	}

	@Override
	public void setTaxInclusive(final boolean isTaxInclusive) {
		this.taxInclusive = isTaxInclusive;
	}

	/**
	 * Price calculator implementation.
	 */
	private class PriceCalculatorImpl extends AbstractPriceCalculatorImpl implements TaxPriceCalculator {

		private final boolean taxAware;

		/**
		 * Initialize a PriceCalculator.
		 */
		PriceCalculatorImpl(final boolean taxAware) {
			super();
			this.taxAware = taxAware;
		}
		
		@Override
		public BigDecimal getAmount() {
			BigDecimal amount = findLowestUnitPrice();
			
			if (amount == null) {
				return null;
			}

			if (isUnitPriceOnly()) {

				// subtract non-coupon discount
				if (isIncludeCartDiscounts() && getQuantity() > 0 && getDiscount() != null) {
					BigDecimal quantity = BigDecimal.valueOf(getQuantity());
					BigDecimal nonCouponDiscountPerUnit = getDiscount().getAmount().divide(quantity, SCALE, BigDecimal.ROUND_HALF_UP);
					amount = amount.subtract(nonCouponDiscountPerUnit);
				}

				if (taxAware) {
					amount = addOrSustractTaxForPrice(amount, getTaxHandling(), getUnitTax());
				}

			} else {
				BigDecimal quantity = BigDecimal.valueOf(getQuantity());
				amount = amount.multiply(quantity);

				if (isIncludeCartDiscounts() && getDiscount() != null) {
					amount = amount.subtract(getDiscount().getAmount());
				}

				if (taxAware) {
					amount = addOrSustractTaxForPrice(amount, getTaxHandling(), getTaxAmount());
				}

			}

			if (amount.compareTo(BigDecimal.ZERO) < 0) {
				amount = BigDecimal.ZERO;
			}
			
			return roundAmountBasedOnTaxType(amount);
		}

		private TaxHandlingEnum getTaxHandling() {
			if (isTaxInclusive()) {
				return TaxHandlingEnum.EXCLUDE;
			} else {
				return TaxHandlingEnum.INCLUDE;
			}
		}

		@Override
		public Money getMoney() {
			return makeMoney(getAmount());
		}
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

	@Override
	@Basic
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATION_DATE", nullable = false)
	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(final Date creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * Round the Amount, based on the tax type. 
	 * @param amount - the values that needs rounding
	 * @return the value after rounding
	 */
	private BigDecimal roundAmountBasedOnTaxType(final BigDecimal amount) {
		// If the state uses tax inclusive pricing, they likely also require the use of ROUND_HALF_EVEN instead of ROUND_HALF_UP.
		if (taxInclusive) {
			return amount.setScale(getCurrency().getDefaultFractionDigits(), BigDecimal.ROUND_HALF_EVEN);
		} 
		
		return amount.setScale(getCurrency().getDefaultFractionDigits(), BigDecimal.ROUND_HALF_UP);
	}

	/**
	 * Determine the price of item based on the taxes handling property and taxes type.
	 *
	 * @param amount - the price of the item
	 * @param itemTaxHandlingValue - how taxes are handled for this item
	 * @param taxAmount - the amount paid towards taxes for this item
	 * @return - the price of the item after taxes logic
	 */
	private BigDecimal addOrSustractTaxForPrice(final BigDecimal amount, final TaxHandlingEnum itemTaxHandlingValue, final BigDecimal taxAmount) {

		if (itemTaxHandlingValue != TaxHandlingEnum.USE_SITE_DEFAULTS) {
			// Tax inclusive pricing, but without tax.
			if (taxInclusive && itemTaxHandlingValue == TaxHandlingEnum.EXCLUDE) {
				return amount.subtract(taxAmount);
			}
			// Tax exclusive pricing, but with tax.
			if (!taxInclusive && itemTaxHandlingValue == TaxHandlingEnum.INCLUDE) {
				return amount.add(taxAmount);
			}
		}

		return amount;
	}
}
