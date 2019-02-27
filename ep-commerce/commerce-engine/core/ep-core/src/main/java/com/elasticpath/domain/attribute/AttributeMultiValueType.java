/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.domain.attribute;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.util.csv.CsvStringEncoder;
import com.elasticpath.commons.util.csv.impl.LegacyCsvStringEncoderImpl;
import com.elasticpath.commons.util.csv.impl.Rfc4180CsvStringEncoderImpl;
import com.elasticpath.commons.util.extenum.AbstractExtensibleEnum;

/**
 * Enum for multi-valued type representation of attributes. *
 */
public class AttributeMultiValueType extends AbstractExtensibleEnum<AttributeMultiValueType> {

	private static final long serialVersionUID = 1L;
	
	private static final int SINGLE_VALUE_ORDINAL = 0;
	private static final int LEGACY_ORDINAL = 1;
	private static final int RFC_4180_ORDINAL = 2;
	

	/** Is a single value type. **/
	public static final AttributeMultiValueType SINGLE_VALUE = new AttributeMultiValueType(SINGLE_VALUE_ORDINAL, "false",
			AttributeMultiValueType.class);
	/** Is a multi-value type in legacy encoding. **/
	public static final AttributeMultiValueType LEGACY = new AttributeMultiValueType(LEGACY_ORDINAL, "true",
			AttributeMultiValueType.class);
	/** Is a multi-value type in rfc-compliant encoding. **/
	public static final AttributeMultiValueType RFC_4180 = new AttributeMultiValueType(RFC_4180_ORDINAL, "rfc4180",
			AttributeMultiValueType.class);
	
	/**
	 * Constructor for creating AttributeMultiValueTypes.
	 * @param ordinal the int representation on the database
	 * @param name the string representation on the exported XML
	 * @param klass this class
	 */
	protected AttributeMultiValueType(final int ordinal, final String name,
			final Class<AttributeMultiValueType> klass) {
		super(ordinal, name, klass);
	}

	/**
	 * Gets the Enum Type.
	 * @return Attribute multi value type class
	 */
	@Override
	protected Class<AttributeMultiValueType> getEnumType() {
		return AttributeMultiValueType.class;
	}
	
	/**
	 * Creates an AttributeMultiValueType from the int representation.
	 * @param ordinal int representation of the type
	 * @return AttributeMultiValueType object
	 */
	public static AttributeMultiValueType createAttributeMultiValueType(final int ordinal) {
		return AttributeMultiValueType.valueOf(ordinal, AttributeMultiValueType.class);
	}
	
	/**
	 * Creates an AttributeMultiValueType from the string representation.
	 * @param name string representation of the type
	 * @return AttributeMultiValueType object
	 */
	public static AttributeMultiValueType createAttributeMultiValueType(final String name) {
		return AttributeMultiValueType.valueOf(name, AttributeMultiValueType.class);
	}

	/**
	 * Gets the encoder for this multi-value type.
	 * 
	 * @return encoder the encoder
	 */
	public CsvStringEncoder getEncoder() {
		CsvStringEncoder encoder = null;
		if (LEGACY.equals(this)) {
			encoder = new LegacyCsvStringEncoderImpl();
		} else if (RFC_4180.equals(this)) {
			encoder = new Rfc4180CsvStringEncoderImpl();
		} else {
			throw new EpServiceException("The attribute is not in valid multi-value encoding format.");
		}
		return encoder;
	}
	
	/**
	 * Returns the type name in lowercase.
	 * @return type name in lowercase
	 */
	public String getTypeName() {
		return this.getName().toLowerCase();
	}
}

