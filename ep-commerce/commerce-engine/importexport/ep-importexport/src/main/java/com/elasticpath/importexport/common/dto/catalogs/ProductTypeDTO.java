/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.importexport.common.dto.catalogs;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * The implementation of the <code>Dto</code> interface that contains data of CatalogProductType object.
 * <p>
 * This implementation designed for JAXB to working with xml representation of data
 */
@XmlAccessorType(XmlAccessType.NONE)
public class ProductTypeDTO extends CategoryTypeDTO {

	private static final long serialVersionUID = 1L;

	@XmlElement(name = "defaulttaxcode", required = true)
	private String defaultTaxCode;

	@XmlElement(name = "multisku")
	private MultiSkuDTO multiSku;

	@XmlElement(name = "nodiscount")
	private Boolean nodiscount;

	@XmlElementWrapper(name = "assignedcartitemmodifiergroups")
	@XmlElement(name = "code")
	private List<String> assignedCartItemModifierGroups;

	/**
	 * Gets the defaultTaxCode.
	 *
	 * @return the defaultTaxCode
	 */
	public String getDefaultTaxCode() {
		return defaultTaxCode;
	}

	/**
	 * Sets the defaultTaxCode.
	 *
	 * @param defaultTaxCode the defaultTaxCode to set
	 */
	public void setDefaultTaxCode(final String defaultTaxCode) {
		this.defaultTaxCode = defaultTaxCode;
	}

	/**
	 * Gets the multiSku.
	 *
	 * @return the multiSku
	 */
	public MultiSkuDTO getMultiSku() {
		return multiSku;
	}

	/**
	 * Sets the multiSku.
	 *
	 * @param multiSku the multiSku to set
	 */
	public void setMultiSku(final MultiSkuDTO multiSku) {
		this.multiSku = multiSku;
	}
	/**
	 * Returns if discount is applied to the type .
	 *
	 * @return <code>true</code> if discount can be applied to the type, <code>false</code> otherwise
	 */
	public Boolean getNoDiscount() {
		return nodiscount;
	}

	/**
	 * Sets if discount is applied to the type .
	 *
	 * @param nodiscount <code>true</code> if discount can be applied to the type, <code>false</code> otherwise
	 */
	public void setNoDiscount(final Boolean nodiscount) {
		this.nodiscount = nodiscount;
	}

	/**
	 * Gets the AssignedCartItemModifierGroups.
	 *
	 * @return the AssignedCartItemModifierGroups
	 * */
	public List<String> getAssignedCartItemModifierGroups() {
		return assignedCartItemModifierGroups;
	}

	/**
	 * Set the AssignedCartItemModifierGroups.
	 *
	 * @param assignedCartItemModifierGroups the AssignedCartItemModifierGroups
	 * */
	public void setAssignedCartItemModifierGroups(final List<String> assignedCartItemModifierGroups) {
		this.assignedCartItemModifierGroups = assignedCartItemModifierGroups;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
			.append("defaultTaxCode", getDefaultTaxCode())
			.append("multiSku", getMultiSku())
			.append("noDiscount", getNoDiscount())
			.append("assignedcartitemmodifiergroups", getAssignedCartItemModifierGroups())
			.toString();
	}
}
