/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.importexport.common.dto.catalogs;

import javax.xml.bind.annotation.XmlEnumValue;

import com.elasticpath.domain.attribute.AttributeMultiValueType;

/**
 * The attribute multi-value type type. Used in {@link com.elasticpath.importexport.common.dto.catalogs.AttributeDTO}
 */
public enum AttributeMultiValueTypeType {

	/** Not multi-value. */
	@XmlEnumValue("false")
	FALSE {
		@Override
		public AttributeMultiValueType type() {
			return AttributeMultiValueType.SINGLE_VALUE;
		}
	},

	/** Legacy multi-value. */
	@XmlEnumValue("true")
	TRUE {
		@Override
		public AttributeMultiValueType type() {
			return AttributeMultiValueType.LEGACY;
		}
	},

	/** RFC 4180 compliant multi-value. */
	@XmlEnumValue("rfc4180")
	RFC4180 {
		@Override
		public AttributeMultiValueType type() {
			return AttributeMultiValueType.RFC_4180;
		}
	};

	/**
	 * Returns AttributeMultiValueType for the current type.
	 *
	 * @return AttributeMultiValueType
	 */
	public abstract AttributeMultiValueType type();

	/**
	 * Converts AttributeMultiValueType to AttributeMultiValueTypeType.
	 *
	 * @param type the AttributeMultiValueType
	 * @return the AttributeMultiValueTypeType
	 */
	public static AttributeMultiValueTypeType valueOf(final AttributeMultiValueType type) {
		return valueOf(type.getTypeName().toUpperCase());
	}
}
