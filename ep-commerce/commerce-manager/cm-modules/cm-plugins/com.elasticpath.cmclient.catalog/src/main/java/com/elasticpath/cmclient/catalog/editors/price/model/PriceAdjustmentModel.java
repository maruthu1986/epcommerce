/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.catalog.editors.price.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Observable;

import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;

/**  
 * Model for price adjustment view. The model is a tree with PriceAdjustmentModelRoot at the
 * root.  Combines the product, price, and price adjustment for display. 
 */
public class PriceAdjustmentModel extends Observable {
	private BigDecimal priceAdjustment;

	private BundleConstituent bundleConstituent;

	private final List<PriceAdjustmentModel> children = new ArrayList<>();

	private PriceAdjustmentModel parent;

	private BigDecimal price;

	private Product product;

	private Integer quantity;

	private boolean selected;

	private int selectionParameter;

	/**  */
	protected PriceAdjustmentModel() {
		// do nothing
	}

	/**
	 * Constructor.
	 * 
	 * @param bundleConstituent bundleConstituent
	 * @param price price
	 * @param priceAdjustment price adjustment
	 * @param selectionParameter selection parameter
	 */
	public PriceAdjustmentModel(final BundleConstituent bundleConstituent, final BigDecimal price, final BigDecimal priceAdjustment,
			final int selectionParameter) {
		this.product = bundleConstituent.getConstituent().getProduct();
		this.bundleConstituent = bundleConstituent;
		this.quantity = bundleConstituent.getQuantity();
		this.price = price;
		this.priceAdjustment = priceAdjustment;
		this.selectionParameter = selectionParameter;
	}

	/**
	 * Use to add child models, the represent constituent of the bundle represented by this node. 
	 * 
	 * @param child child
	 */
	public void addChild(final PriceAdjustmentModel child) {
		children.add(child);
		child.setParent(this);
	}

	/**
	 * @return the price adjustment
	 */
	public BigDecimal getPriceAdjustment() {
		return priceAdjustment;
	}

	/**
	 * @return the bundleConstituentGuid
	 */
	public BundleConstituent getBundleConstituent() {
		return bundleConstituent;
	}

	/**
	 * @return a list children.
	 */
	public Collection<PriceAdjustmentModel> getChildren() {
		return children;
	}

	/**
	 * @return the parent
	 */
	public PriceAdjustmentModel getParent() {
		return parent;
	}

	/**
	 * @return the price
	 */
	public BigDecimal getPrice() {
		return price;
	}

	/**
	 * @return the product
	 */
	public Product getProduct() {
		return product;
	}

	/**
	 * @return quantity of the product on bundle
	 */
	public int getQuantity() {
		return this.quantity;
	}

	/**
	 * @return the selectionParameter
	 */
	public int getSelectionParameter() {
		return selectionParameter;
	}

	/**
	 * @return true if and only if this Product is a calculated ProductBundle.
	 */
	public boolean isProductACalculatedBundle() {
		if (getProduct() instanceof ProductBundle) {
			return ((ProductBundle) getProduct()).isCalculated();
		}
		return false;
	}
	
	/**
	 * @return true if and only if this Product is inside a bundle.
	 */
	public boolean isConstituentItem() {
		return getParent() instanceof PriceAdjustmentModelRoot;
	}

	/**
	 * @return the selected
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * @param adjustment the price adjustment to set
	 */
	public void setPriceAdjustment(final BigDecimal adjustment) {
		this.priceAdjustment = adjustment;
		this.setChanged();
		this.notifyObservers(this);
	}

	/**
	 * @param bundleConstituent the BundleConstituent to set
	 */
	protected void setBundleConstituent(final BundleConstituent bundleConstituent) {
		this.bundleConstituent = bundleConstituent;
	}

	private void setParent(final PriceAdjustmentModel parent) {
		this.parent = parent;
	}

	/**
	 * @param price the price to set
	 */
	protected void setPrice(final BigDecimal price) {
		this.price = price;
	}

	/**
	 * @param product the product to set
	 */
	protected void setProduct(final Product product) {
		this.product = product;
	}

	/**
	 * @param quantity the quantity to set
	 */
	protected void setQuantity(final Integer quantity) {
		this.quantity = quantity;
	}

	/**
	 * @param selected the selected to set
	 */
	public void setSelected(final boolean selected) {
		this.selected = selected;
	}

	/**
	 * @param selectionParameter the selectionParameter to set
	 */
	protected void setSelectionParameter(final int selectionParameter) {
		this.selectionParameter = selectionParameter;
	}
}