/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.importexport.common.dto.productassociation;

import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.elasticpath.common.dto.Dto;
import com.elasticpath.importexport.common.adapters.associations.ProductAssociationTypeAdapter;

/**
 * The implementation of the <code>ImportDto</code> interface that contains data of productassociation object. This implementation designed for JAXB
 * to work with XML representation of data
 */
@XmlRootElement(name = ProductAssociationDTO.ROOT_ELEMENT)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { })
public class ProductAssociationDTO implements Dto {

	private static final long serialVersionUID = 1L;

	/**
	 * The name of root element in XML representation of product association.
	 */
	public static final String ROOT_ELEMENT = "productassociation";

	@XmlAttribute(name = "catalog", required = true)
	private String catalogCode;

	@XmlJavaTypeAdapter(ProductAssociationTypeAdapter.class)
	@XmlElement(name = "type", required = true)
	private ProductAssociationTypeDTO productAssociationTypeDTO;

	@XmlElement(name = "enabledate", required = true)
	private Date startDate;

	@XmlElement(name = "disabledate")
	private Date endDate;

	@XmlElement(name = "sourceproductcode", required = true)
	private String sourceProductCode;

	@XmlElement(name = "targetproductcode", required = true)
	private String targetProductCode;

	@XmlElement(name = "defaultquantity", required = true)
	private int defaultQuantity;

	@XmlElement(name = "sourceproductdependent")
	private Boolean sourceProductDependent = Boolean.FALSE;

	@XmlElement(name = "ordering")
	private int ordering;

	/**
	 * Gets catalog code.
	 * 
	 * @return catalog code
	 */
	public String getCatalogCode() {
		return catalogCode;
	}

	/**
	 * Sets catalog code.
	 * 
	 * @param catalogCode catalog code
	 */
	public void setCatalogCode(final String catalogCode) {
		this.catalogCode = catalogCode;
	}

	/**
	 * Sets ProductAssociationTypeDTO.
	 * 
	 * @return the productAssociationTypeDTO
	 */
	public ProductAssociationTypeDTO getProductAssociationType() {
		return productAssociationTypeDTO;
	}

	/**
	 * Gets ProductAssociationTypeDTO.
	 * 
	 * @param productAssociationTypeDTO the productAssociationTypeDTO to set
	 */
	public void setProductAssociationType(final ProductAssociationTypeDTO productAssociationTypeDTO) {
		this.productAssociationTypeDTO = productAssociationTypeDTO;
	}

	/**
	 * Gets start date.
	 * 
	 * @return start date
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * Sets start date.
	 * 
	 * @param startDate start date
	 */
	public void setStartDate(final Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * Gets end date.
	 * 
	 * @return end date
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * Sets end date.
	 * 
	 * @param endDate end date
	 */
	public void setEndDate(final Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * Gets source product code.
	 * 
	 * @return source product code
	 */
	public String getSourceProductCode() {
		return sourceProductCode;
	}

	/**
	 * Sets source product code.
	 * 
	 * @param sourceProductCode source product code
	 */
	public void setSourceProductCode(final String sourceProductCode) {
		this.sourceProductCode = sourceProductCode;
	}

	/**
	 * Gets target product code.
	 * 
	 * @return target product code
	 */
	public String getTargetProductCode() {
		return targetProductCode;
	}

	/**
	 * Sets target product code.
	 * 
	 * @param targetProductCode target product code
	 */
	public void setTargetProductCode(final String targetProductCode) {
		this.targetProductCode = targetProductCode;
	}

	/**
	 * Gets default quantity.
	 * 
	 * @return default quantity
	 */
	public int getDefaultQuantity() {
		return defaultQuantity;
	}

	/**
	 * Sets default quantity.
	 * 
	 * @param defaultQuantity default quantity
	 */
	public void setDefaultQuantity(final int defaultQuantity) {
		this.defaultQuantity = defaultQuantity;
	}

	/**
	 * Gets if target is source product dependent.
	 * 
	 * @return true if target product is source product dependent.
	 */
	public Boolean isSourceProductDependent() {
		return sourceProductDependent;
	}

	/**
	 * Gets if target is source product dependent.
	 * 
	 * @param sourceProductDependent if target product is source product dependent.
	 */
	public void setSourceProductDependent(final Boolean sourceProductDependent) {
		this.sourceProductDependent = sourceProductDependent;
	}

	/**
	 * Gets ordering.
	 * 
	 * @return ordering
	 */
	public int getOrdering() {
		return ordering;
	}

	/**
	 * Sets ordering.
	 * 
	 * @param ordering ordering
	 */
	public void setOrdering(final int ordering) {
		this.ordering = ordering;
	}
}
