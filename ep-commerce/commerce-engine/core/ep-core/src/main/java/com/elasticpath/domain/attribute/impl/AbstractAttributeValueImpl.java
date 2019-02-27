/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.attribute.impl;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.apache.openjpa.persistence.FetchAttribute;
import org.apache.openjpa.persistence.FetchGroup;
import org.apache.openjpa.persistence.FetchGroups;
import org.apache.openjpa.persistence.jdbc.ForeignKey;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.constants.GlobalConstants;
import com.elasticpath.commons.constants.ImportConstants;
import com.elasticpath.commons.exception.EpBindException;
import com.elasticpath.commons.util.Utility;
import com.elasticpath.commons.util.impl.ConverterUtils;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeMultiValueType;
import com.elasticpath.domain.attribute.AttributeType;
import com.elasticpath.domain.attribute.AttributeValueWithType;
import com.elasticpath.domain.impl.AbstractLegacyPersistenceImpl;
import com.elasticpath.persistence.support.FetchGroupConstants;

/**
 * The default implementation of <code>AttributeValue</code>.
 */
@MappedSuperclass
@FetchGroups({
		@FetchGroup(name = FetchGroupConstants.ATTRIBUTE_VALUES, attributes = { @FetchAttribute(name = "localizedAttributeKey"),
				@FetchAttribute(name = "shortTextValue"), @FetchAttribute(name = "integerValue"), @FetchAttribute(name = "longTextValue"),
				@FetchAttribute(name = "decimalValue"), @FetchAttribute(name = "booleanValue"), @FetchAttribute(name = "dateValue"),
				@FetchAttribute(name = "attribute"), @FetchAttribute(name = "attributeTypeId") }),
		@FetchGroup(name = FetchGroupConstants.CATEGORY_ATTRIBUTES, fetchGroups = { FetchGroupConstants.ATTRIBUTE_VALUES })
}
)
@SuppressWarnings("PMD.GodClass")
public abstract class AbstractAttributeValueImpl extends AbstractLegacyPersistenceImpl implements AttributeValueWithType {

	private static final long serialVersionUID = 5000000001L;

	private String localizedAttributeKey;

	private String shortTextValue;

	private Integer integerValue;

	private String longTextValue;

	private BigDecimal decimalValue;

	private Boolean booleanValue;

	private Date dateValue;

	private Attribute attribute;

	private int attributeTypeId;
	
	@Override
	@Basic
	@Column(name = "SHORT_TEXT_VALUE")
	public String getShortTextValue() {
		return shortTextValue;
	}

	@Override
	public void setShortTextValue(final String shortTextValue) {
		this.shortTextValue = shortTextValue;
	}

	@Override
	@Lob
	@Column(name = "LONG_TEXT_VALUE", length = GlobalConstants.LONG_TEXT_MAX_LENGTH)
	public String getLongTextValue() {
		return longTextValue;
	}

	@Override
	public void setLongTextValue(final String longTextValue) {
		this.longTextValue = longTextValue;
	}

	@Override
	@Basic
	@Column(name = "INTEGER_VALUE")
	public Integer getIntegerValue() {
		return integerValue;
	}

	@Override
	public void setIntegerValue(final Integer integerValue) {
		this.integerValue = integerValue;
	}

	@Override
	@Basic
	@Column(name = "DECIMAL_VALUE")
	public BigDecimal getDecimalValue() {
		return decimalValue;
	}

	@Override
	public void setDecimalValue(final BigDecimal decimalValue) {
		this.decimalValue = decimalValue;
	}

	@Override
	@Basic
	@Column(name = "BOOLEAN_VALUE")
	public Boolean getBooleanValue() {
		return booleanValue;
	}

	@Override
	public void setBooleanValue(final Boolean booleanValue) {
		this.booleanValue = booleanValue;
	}

	@Override
	@Basic
	@Column(name = "DATE_VALUE")
	public Date getDateValue() {
		return dateValue;
	}

	@Override
	public void setDateValue(final Date dateValue) {
		this.dateValue = dateValue;
	}

	@Override
	@ManyToOne(targetEntity = AttributeImpl.class, cascade = { CascadeType.REFRESH, CascadeType.MERGE })
	@JoinColumn(name = "ATTRIBUTE_UID")
	@ForeignKey
	public Attribute getAttribute() {
		return attribute;
	}

	@Override
	public void setAttribute(final Attribute attribute) {
		this.attribute = attribute;
	}

	@Override
	@Transient
	public String getStringValue() {
		switch (getAttributeType().getTypeId()) {
		case AttributeType.SHORT_TEXT_TYPE_ID:
			if (getAttribute().isMultiValueEnabled()) {
				return buildShortTextMultiValues(getShortTextMultiValues(), 
												getAttribute().getMultiValueType());
			}
			return getShortTextValue();
		case AttributeType.LONG_TEXT_TYPE_ID:
			return getLongTextValue();
		case AttributeType.INTEGER_TYPE_ID:
			return Objects.toString(getIntegerValue(), null);
		case AttributeType.DECIMAL_TYPE_ID:
			return Objects.toString(getDecimalValue(), GlobalConstants.NULL_VALUE);
		case AttributeType.BOOLEAN_TYPE_ID:
			return Objects.toString(getBooleanValue(), null);
		case AttributeType.IMAGE_TYPE_ID:
			return getShortTextValue();
		case AttributeType.FILE_TYPE_ID:
			return getShortTextValue();
		case AttributeType.DATE_TYPE_ID:
			return ConverterUtils.date2String(getDateValue(), getUtilityBean().getDefaultLocalizedDateFormat());
		case AttributeType.DATETIME_TYPE_ID:
			return ConverterUtils.date2String(getDateValue(), getUtilityBean().getDefaultLocalizedDateFormat());
		default:
			throw new UnsupportedOperationException();
		}
	}

	@Override
	@Transient
	public Object getValue() {
		switch (getAttributeType().getTypeId()) {
		case AttributeType.SHORT_TEXT_TYPE_ID:
			return getStringValue();
		case AttributeType.LONG_TEXT_TYPE_ID:
			return getLongTextValue();
		case AttributeType.INTEGER_TYPE_ID:
			return getIntegerValue();
		case AttributeType.DECIMAL_TYPE_ID:
			return getDecimalValue();
		case AttributeType.BOOLEAN_TYPE_ID:
			return getBooleanValue();
		case AttributeType.IMAGE_TYPE_ID:
			return getShortTextValue();
		case AttributeType.FILE_TYPE_ID:
			return getShortTextValue();
		case AttributeType.DATE_TYPE_ID:
			return getDateValue();
		case AttributeType.DATETIME_TYPE_ID:
			return getDateValue();
		default:
			throw new UnsupportedOperationException();
		}
	}

	@Override
	public void setValue(final Object value) {
		switch (getAttributeType().getTypeId()) {
		case AttributeType.SHORT_TEXT_TYPE_ID:
			setStringValue((String) value);
			break;
		case AttributeType.LONG_TEXT_TYPE_ID:
			setLongTextValue((String) value);
			break;
		case AttributeType.INTEGER_TYPE_ID:
			setIntegerValue((Integer) value);
			break;
		case AttributeType.DECIMAL_TYPE_ID:
			setDecimalValue((BigDecimal) value);
			break;
		case AttributeType.BOOLEAN_TYPE_ID:
			setBooleanValue((Boolean) value);
			break;
		case AttributeType.IMAGE_TYPE_ID:
			setStringValue((String) value);
			break;
		case AttributeType.FILE_TYPE_ID:
			setStringValue((String) value);
			break;
		case AttributeType.DATE_TYPE_ID:
			setDateValue((Date) value);
			break;
		case AttributeType.DATETIME_TYPE_ID:
			setDateValue((Date) value);
			break;
		default:
			throw new UnsupportedOperationException();
		}
	}

	@SuppressWarnings("fallthrough")
	@Override
	public void setStringValue(final String stringValue) throws EpBindException {
		switch (getAttributeType().getTypeId()) {
		case AttributeType.SHORT_TEXT_TYPE_ID:
			if (getAttribute().isMultiValueEnabled()) {
				setShortTextMultiValues(stringValue);
			} else {
				setShortTextValue(stringValue);
			}
			break;
		case AttributeType.LONG_TEXT_TYPE_ID:
			setLongTextValue(stringValue);
			break;
		case AttributeType.INTEGER_TYPE_ID:
			setIntegerValue(ConverterUtils.string2Int(stringValue));
			break;
		case AttributeType.DECIMAL_TYPE_ID:
			setDecimalValue(ConverterUtils.string2BigDecimal(stringValue));
			break;
		case AttributeType.BOOLEAN_TYPE_ID:
			setBooleanValue(ConverterUtils.string2Boolean(stringValue));
			break;
		case AttributeType.IMAGE_TYPE_ID:
			setShortTextValue(stringValue);
			break;
		case AttributeType.FILE_TYPE_ID:
			setShortTextValue(stringValue);
			break;
		case AttributeType.DATE_TYPE_ID:
		case AttributeType.DATETIME_TYPE_ID:
			setDateValue(ConverterUtils.string2Date(stringValue, getUtilityBean().getDefaultLocalizedDateFormat()));
			break;
		default:
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * Sets multi-valued short text attributes.<br>
	 * This implementation splits the CSV-encoded string on {@link ImportConstants#SHORT_TEXT_MULTI_VALUE_SEPARATOR}.
	 * The result is stored in a longtextvalue.
	 * 
	 * @param delimitedShortTextValues the delimited string of short text values
	 */
	protected void setShortTextMultiValues(final String delimitedShortTextValues) {
		if (delimitedShortTextValues == null) {
			setShortTextMultiValues(Collections.<String>emptyList());
			return;
		}
		
		List<String> values = getAttribute().getMultiValueType().getEncoder().decodeStringToList(delimitedShortTextValues, 
				ImportConstants.SHORT_TEXT_MULTI_VALUE_SEPARATOR_CHAR);
		setShortTextMultiValues(values);
	}

	@Override
	@Transient
	public AttributeType getAttributeType() {
		try {
			return AttributeType.valueOf(getAttributeTypeId());
		} catch (IllegalArgumentException iae) {
			return null;
		}
	}

	@Override
	public void setAttributeType(final AttributeType attributeType) {
		if (attributeType == null) {
			setAttributeTypeId(0);
		} else {
			setAttributeTypeId(attributeType.getTypeId());
		}
	}

	@Override
	public void setLocalizedAttributeKey(final String localizedAttributeKey) {
		this.localizedAttributeKey = localizedAttributeKey;
	}

	@Override
	@Basic
	@Column(name = "LOCALIZED_ATTRIBUTE_KEY")
	public String getLocalizedAttributeKey() {
		return localizedAttributeKey;
	}

	@Override
	@Transient
	public List<String> getShortTextMultiValues() {
		if (AttributeType.SHORT_TEXT.equals(getAttributeType()) && getAttribute().isMultiValueEnabled()) {
			return parseShortTextMultiValues(getLongTextValue(), getAttribute().getMultiValueType());
		}
		return Collections.emptyList();
	}

	/**
	 * Parse the multi values for short text type from a delimited string.<br>
	 * This implementation assumes the given string is CSV-encoded.
	 * 
	 * @param shortTextValue the string value which contains the multi-value for short text.
	 * @param multiValueType the multi value type
	 * @return the list of shortText value
	 */
	public static List<String> parseShortTextMultiValues(final String shortTextValue,
			final AttributeMultiValueType multiValueType) {
		return multiValueType.getEncoder().decodeStringToList(shortTextValue,
				ImportConstants.SHORT_TEXT_MULTI_VALUE_SEPARATOR_CHAR);
	}

	@Override
	public void setShortTextMultiValues(final List<String> shortTextMultiValues) {
		if (AttributeType.SHORT_TEXT.equals(getAttributeType()) && getAttribute().isMultiValueEnabled()) {
			setLongTextValue(buildShortTextMultiValues(shortTextMultiValues,
														getAttribute().getMultiValueType()));
		}
	}

	/**
	 * Build up the multi values for short text. <br>
	 * Encode a list of strings to a CSV-encoded string using SHORT_TEXT_MULTI_VALUE_SEPARATOR.
	 * 
	 * @param shortTextMultiValues the list of multi-value for short text
	 * @param multiValueType the multi value type
	 * @return the storage format of the multi-value for short text.
	 */
	public static String buildShortTextMultiValues(final List<String> shortTextMultiValues,
			final AttributeMultiValueType multiValueType) {
		if (shortTextMultiValues == null) {
			return "";
		}

		return multiValueType.getEncoder().encodeString(shortTextMultiValues, 
				ImportConstants.SHORT_TEXT_MULTI_VALUE_SEPARATOR_CHAR);
	}

	/**
	 * Get the attribute type Id.
	 *
	 * @return the Id of the attribute type
	 */
	@Basic
	@Column(name = "ATTRIBUTE_TYPE")
	protected int getAttributeTypeId() {
		return attributeTypeId;
	}

	/**
	 * Set the attribute type id.
	 *
	 * @param attributeTypeId the id of the attribute type
	 */
	protected void setAttributeTypeId(final int attributeTypeId) {
		this.attributeTypeId = attributeTypeId;
	}

	@Override
	@SuppressWarnings({"PMD.MissingBreakInSwitch", "fallthrough"})
	public int compareTo(final AttributeValueWithType other) {
		if (!getAttributeType().equals(other.getAttributeType())) {
			throw new EpDomainException("Must have the same attribute type!");
		}
		switch (getAttributeType().getTypeId()) {
		case AttributeType.LONG_TEXT_TYPE_ID:
		case AttributeType.SHORT_TEXT_TYPE_ID:
			return getStringValue().compareTo(other.getStringValue());
		case AttributeType.DECIMAL_TYPE_ID:
			return getDecimalValue().compareTo(other.getDecimalValue());
		case AttributeType.INTEGER_TYPE_ID:
			return getIntegerValue().compareTo(other.getIntegerValue());
		case AttributeType.DATE_TYPE_ID:
		case AttributeType.DATETIME_TYPE_ID:
			return getDateValue().compareTo(other.getDateValue());
		default:
			throw new EpDomainException("Not implemented");
		}
	}

	@Override
	public boolean isDefined() {
		if (getValue() == null) {
			return false;
		}
		return !"".equals(getStringValue().trim());
	}

	/**
	 * Implements equals semantics.<br>
	 * This class more than likely would be extended to add functionality that would not effect the equals method in comparisons, and as such would
	 * act as an entity type. In this case, content is not crucial in the equals comparison. Using instanceof within the equals method enables
	 * comparison in the extended classes where the equals method can be reused without violating symmetry conditions. If getClass() was used in the
	 * comparison this could potentially cause equality failure when we do not expect it. If when extending additional fields are included in the
	 * equals method, then the equals needs to be overridden to maintain symmetry.
	 *
	 * @param obj the other object to compare
	 * @return true if equal
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof AbstractAttributeValueImpl)) {
			return false;
		}
		AbstractAttributeValueImpl other = (AbstractAttributeValueImpl) obj;
		return Objects.equals(integerValue, other.integerValue)
			&& Objects.equals(decimalValue, other.decimalValue)
			&& Objects.equals(booleanValue, other.booleanValue)
			&& Objects.equals(dateValue, other.dateValue)
			&& Objects.equals(attribute, other.attribute)
			&& Objects.equals(shortTextValue, other.shortTextValue)
			&& Objects.equals(longTextValue, other.longTextValue)
			&& Objects.equals(localizedAttributeKey, other.localizedAttributeKey)
			&& Objects.equals(attributeTypeId, other.attributeTypeId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(integerValue, decimalValue, booleanValue, dateValue, attribute, shortTextValue, longTextValue,
			localizedAttributeKey, attributeTypeId);
	}

	@Override
	public String toString() {
		if (getAttributeType() != null) {
			return getStringValue();
		}
		return "";
	}

	@Transient
	private Utility getUtilityBean() {
		return getBean(ContextIdNames.UTILITY);
	}
}
