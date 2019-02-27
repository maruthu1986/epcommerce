/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.service.search.query;

import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductAssociationType;
import com.elasticpath.service.search.AbstractSearchCriteriaImpl;
import com.elasticpath.service.search.IndexType;

/**
 * A criteria for advanced product associations search.
 */
public class ProductAssociationSearchCriteria extends AbstractSearchCriteriaImpl {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;

	private Product sourceProduct;

	private Product targetProduct;

	private String sourceProductCode;

	private String targetProductCode;

	private ProductAssociationType associationType;

	private String catalogCode;

	private boolean withinCatalogOnly;

	private Date startDateBefore;

	private Date endDateAfter;

	private boolean hidden;

	private boolean notSoldSeparately;

	/**
	 * Returns The Source Product.
	 *
	 * @return the sourceProduct
	 */
	public Product getSourceProduct() {
		return sourceProduct;
	}

	/**
	 * Sets The Source Product.
	 *
	 * @param sourceProduct the sourceProduct to set
	 */
	public void setSourceProduct(final Product sourceProduct) {
		this.sourceProduct = sourceProduct;
	}

	/**
	 * Returns The Target Product.
	 *
	 * @return the targetProduct
	 */
	public Product getTargetProduct() {
		return targetProduct;
	}

	/**
	 * Sets The Target Product.
	 *
	 * @param targetProduct the targetProduct to set
	 */
	public void setTargetProduct(final Product targetProduct) {
		this.targetProduct = targetProduct;
	}

	/**
	 * Returns The Association Type.
	 *
	 * @return the associationType
	 */
	public ProductAssociationType getAssociationType() {
		return associationType;
	}

	/**
	 * Sets The Association Type.
	 *
	 * @param associationType the associationType to set
	 */
	public void setAssociationType(final ProductAssociationType associationType) {
		this.associationType = associationType;
	}

	/**
	 * Optimizes a search criteria by removing unnecessary information.
	 */
	@Override
	public void optimize() {
		if ((sourceProduct != null) || ((sourceProductCode != null) && (sourceProductCode.trim().length() < 1))) {
			sourceProductCode = null;
		}
		if ((targetProduct != null) || ((targetProductCode != null) && (targetProductCode.trim().length() < 1))) {
			targetProductCode = null;
		}
	}

	/**
	 * Returns the index type this criteria deals with.
	 *
	 * @return the index type this criteria deals with
	 */
	@Override
	public IndexType getIndexType() {
		return null;
	}

	/**
	 *
	 * @return the sourceProductCode
	 */
	public String getSourceProductCode() {
		return sourceProductCode;
	}

	/**
	 *
	 * @param sourceProductCode the sourceProductCode to set
	 */
	public void setSourceProductCode(final String sourceProductCode) {
		this.sourceProductCode = sourceProductCode;
	}

	/**
	 *
	 * @return the targetProductCode
	 */
	public String getTargetProductCode() {
		return targetProductCode;
	}

	/**
	 *
	 * @param targetProductCode the targetProductCode to set
	 */
	public void setTargetProductCode(final String targetProductCode) {
		this.targetProductCode = targetProductCode;
	}

	/**
	 *
	 * @param catalogCode the catalog code to set
	 */
	public void setCatalogCode(final String catalogCode) {
		this.catalogCode = catalogCode;
	}

	/**
	 *
	 * @return the catalog code
	 */
	public String getCatalogCode() {
		return this.catalogCode;
	}

	/**
	 *
	 * @return the withinCatalogOnly
	 */
	public boolean isWithinCatalogOnly() {
		return withinCatalogOnly;
	}

	/**
	 *
	 * @param withinCatalogOnly the withinCatalogOnly to set
	 */
	public void setWithinCatalogOnly(final boolean withinCatalogOnly) {
		this.withinCatalogOnly = withinCatalogOnly;
	}

	/**
	 * Gets the start date.
	 * @return The start date.
	 */
	public Date getStartDateBefore() {
		return startDateBefore;
	}

	/**
	 * Sets the start date.
	 * @param startDateBefore The start date.
	 */
	public void setStartDateBefore(final Date startDateBefore) {
		this.startDateBefore = startDateBefore;
	}

	/**
	 * Gets the end date.
	 * @return The end date.
	 */
	public Date getEndDateAfter() {
		return endDateAfter;
	}

	/**
	 * Sets the end date.
	 * @param endDateAfter The end date.
	 */
	public void setEndDateAfter(final Date endDateAfter) {
		this.endDateAfter = endDateAfter;
	}

	/**
	 * Gets the hidden flag.
	 * @return the hidden flag
	 */
	public boolean isHidden() {
		return hidden;
	}

	/**
	 * Sets the hidden flag.
	 * @param hidden new hidden flag
	 */
	public void setHidden(final boolean hidden) {
		this.hidden = hidden;
	}

	/**
	 * Gets not sold separately flag.
	 * @return not sold separately flag.
	 */
	public boolean isNotSoldSeparately() {
		return notSoldSeparately;
	}

	/**
	 * Sets not sold separately flag.
	 * @param notSoldSeparately new not sold separately flag.
	 */
	public void setNotSoldSeparately(final boolean notSoldSeparately) {
		this.notSoldSeparately = notSoldSeparately;
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}
}
