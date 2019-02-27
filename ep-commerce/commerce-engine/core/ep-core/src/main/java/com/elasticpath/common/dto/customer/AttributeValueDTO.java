/**
 * Copyright (c) Elastic Path Software Inc., 2011
 */
package com.elasticpath.common.dto.customer;

import java.util.Date;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

import com.elasticpath.common.dto.Dto;

/**
 * Similar to a PropertyDTO, but also contains the type associated with the key-value.
 */
@XmlRootElement(name = AttributeValueDTO.ROOT_ELEMENT)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { })
public class AttributeValueDTO implements Dto {

	/** XML root element name. */
	public static final String ROOT_ELEMENT = "attribute_value";

	private static final long serialVersionUID = 1L;

	@XmlAttribute(required = true)
	private String key;

	@XmlAttribute(required = true)
	private String type;

	@XmlAttribute(name = "creation_date")
	private Date creationDate;

	@XmlValue
	private String value;

	public String getKey() {
		return key;
	}

	public void setKey(final String key) {
		this.key = key;
	}

	public String getType() {
		return type;
	}

	public void setType(final String type) {
		this.type = type;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(final Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getValue() {
		return value;
	}

	public void setValue(final String value) {
		this.value = value;
	}

	@Override
	public int hashCode() {
		return Objects.hash(key, type, creationDate, value);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		
		AttributeValueDTO other = (AttributeValueDTO) obj;
		
		return Objects.equals(key, other.key)
			&& Objects.equals(type, other.type)
			&& Objects.equals(creationDate, other.creationDate)
			&& Objects.equals(value, other.value);
	}

}
