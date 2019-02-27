/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.importexport.common.dto.cmimportjob;

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.elasticpath.common.dto.Dto;

/**
 * Contains mapping between XML and TagCondition domain object. Designed for JAXB.
 */
@XmlRootElement(name = CmImportMappingDTO.ROOT_ELEMENT)
@XmlAccessorType(XmlAccessType.NONE)
public class CmImportMappingDTO implements Dto {

	private static final long serialVersionUID = 1L;

	/**
	 * The name of root element in xml representation.
	 */
	public static final String ROOT_ELEMENT = "mapping";
	
	@XmlElement(name = "col_number", required = true)
	private int colNumber;

	@XmlElement(name = "import_field_name", required = true)
	private String importFieldName;

	public int getColNumber() {
		return colNumber;
	}

	public void setColNumber(final int colNumber) {
		this.colNumber = colNumber;
	}

	public String getImportFieldName() {
		return importFieldName;
	}

	public void setImportFieldName(final String importFieldName) {
		this.importFieldName = importFieldName;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(colNumber, importFieldName);
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
		
		CmImportMappingDTO other = (CmImportMappingDTO) obj;
		
		return Objects.equals(colNumber, other.colNumber)
			&& Objects.equals(importFieldName, other.importFieldName);
	}

}
