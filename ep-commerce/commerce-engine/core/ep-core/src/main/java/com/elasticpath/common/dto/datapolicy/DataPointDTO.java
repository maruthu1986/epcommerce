/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.common.dto.datapolicy;

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.elasticpath.common.dto.Dto;

/**
 * Data Transfer Object for {@link com.elasticpath.domain.datapolicy.DataPoint}.
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = {})
public class DataPointDTO implements Dto {

	/**
	 * Root element name for {@link com.elasticpath.domain.datapolicy.DataPoint}.
	 */
	public static final String ROOT_ELEMENT = "data_point";

	private static final long serialVersionUID = 5000000001L;

	@XmlAttribute(required = true)
	private String guid;

	@XmlElement(name = "name", required = true)
	private String name;

	@XmlElement(name = "data_location", required = true)
	private String dataLocation;

	@XmlElement(name = "data_key", required = true)
	private String dataKey;

	@XmlElement(name = "description_key", required = true)
	private String descriptionKey;

	@XmlElement(name = "removable", required = true)
	private boolean removable;

	public String getGuid() {
		return guid;
	}

	public void setGuid(final String guid) {
		this.guid = guid;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getDataLocation() {
		return dataLocation;
	}

	public void setDataLocation(final String dataLocation) {
		this.dataLocation = dataLocation;
	}

	public String getDataKey() {
		return dataKey;
	}

	public void setDataKey(final String dataKey) {
		this.dataKey = dataKey;
	}

	public String getDescriptionKey() {
		return descriptionKey;
	}

	public void setDescriptionKey(final String descriptionKey) {
		this.descriptionKey = descriptionKey;
	}

	public boolean isRemovable() {
		return removable;
	}

	public void setRemovable(final boolean removable) {
		this.removable = removable;
	}

	@Override
	public int hashCode() {
		return Objects.hash(guid);
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

		DataPointDTO other = (DataPointDTO) obj;

		return Objects.equals(guid, other.guid);
	}
}
