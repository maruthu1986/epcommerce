/*
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.cmclient.store.targetedselling.dynamiccontent.wizard.parameters;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.cmclient.core.dialog.value.support.AbstractDialogEditingSupport;
import com.elasticpath.cmclient.core.dialog.value.support.DialogValueLabelProvider;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.core.util.DateTimeUtilFactory;
import com.elasticpath.cmclient.store.targetedselling.TargetedSellingMessages;
import com.elasticpath.commons.constants.ValueTypeEnum;
import com.elasticpath.domain.contentspace.DynamicContent;
import com.elasticpath.domain.contentspace.ParameterValue;

/**
 * Editing support for <code>ParameterValue</code>.
 */
@SuppressWarnings({ "PMD.CyclomaticComplexity", "PMD.GodClass", "PMD.PrematureDeclaration" })
public class ParametersEditingSupport extends 
	AbstractDialogEditingSupport<DynamicContent, ParameterValue> {
	
	/**
	 * Max length for value.
	 */
	public static final int MAX_VALUE_LENGTH = 4000;

	private Locale locale;
	/**
	 * Constructor.
	 * 
	 * @param parametersTableViewer the EP table viewer
	 * @param dynamicContent the DynamicContent that contains wrapper with edited properties
	 * @param locale the locale of the edited properties
	 */
	public ParametersEditingSupport(final IEpTableViewer parametersTableViewer, 
			final DynamicContent dynamicContent, final Locale locale) {
		super(parametersTableViewer, dynamicContent, 
				new DialogValueLabelProvider() {
					@Override
					public String getLabelText() {
						return TargetedSellingMessages.get().DynamicContentAttribute_Edit;
					}

					@Override
					public boolean isLabelBold() {
						return false;
					}
					
				}, new SimpleParameterEditingSupportDialogFactory(), new SimpleParameterEditingSupportCellEditorFactory());
		this.locale = locale;

	}
	
	/**
	 * check type of parameter value is valid.
	 * @param value the value to extract value from
	 * @return true if value or it's type is null
	 */
	private boolean isNullTypeParameterValue(final ParameterValue value) {
		return value == null || value.getParameter() == null || value.getParameter().getType() == null;
	}

	@Override
	protected ValueTypeEnum getTypeOfValue(final ParameterValue value) {

		if (isNullTypeParameterValue(value)) {
			return ValueTypeEnum.StringShort;
		}
		return value.getParameter().getType();

	}

	@Override
	protected Object extractValueFromElement(final ParameterValue value) {
		if (!isNullTypeParameterValue(value)) {

			final String exValue = value.getValue(this.getLocaleStringValue());

			String str = StringUtils.EMPTY;
			Map<ValueTypeEnum, String> elements = new HashMap<>();
			elements.put(ValueTypeEnum.StringShort, str);
			elements.put(ValueTypeEnum.StringLong, str);
			elements.put(ValueTypeEnum.File, str);
			elements.put(ValueTypeEnum.Image, str);
			elements.put(ValueTypeEnum.Url, str);
			elements.put(ValueTypeEnum.HTML, str);

			if (StringUtils.isEmpty(exValue)) {
				return elements.get(value.getParameter().getType());
			}


			switch (value.getParameter().getType()) { // NOPMD
				case Boolean:
					return Boolean.valueOf(exValue);
				case Date:
					try {
						return DateTimeUtilFactory.getDateUtil().parseDate(exValue);
					} catch (ParseException e) {
						return new Date();
					}
				case Datetime:
					try {
						return DateTimeUtilFactory.getDateUtil().parseDateTime(exValue);
					} catch (ParseException e) {
						return new Date();
					}
				case Decimal:
					try {
						return BigDecimal.valueOf(Double.valueOf(exValue));
					} catch (NumberFormatException e) {
						return null;
					}
				case Integer:
					try {
						return Integer.valueOf(exValue);
					} catch (NumberFormatException e) {
						return null;
					}
				case StringShortMultiValue:

					return parseParameterShortTextMultiValues(exValue);

				case Product:
				case Category:
				case StringShort:
				case StringLong:
				case File:
				case Image:
				case Url:
				default:
					return exValue;
			}
		}
		return null;
	}

	@Override
	public boolean setValueToElement(final ParameterValue element, final Object value) {

		if (!isNullTypeParameterValue(element)) {

			final String localeKey = this.getLocaleStringValue();

			// check value has changed
			if (!hasValueChanged(value, element)) {
				return false;
			}

			// check required
			if (element.getParameter().isRequired()
					&& (value == null || StringUtils.EMPTY.equals(value))) {
				return false;
			}

			if (value == null) {

				// if null it is set regardless of type
				element.setValue(null, localeKey);

			} else {
				// do type sensitive setting
				switch (element.getParameter().getType()) {
					case Boolean:
					case Integer:
						element.setValue(String.valueOf(value), localeKey);
						break;

					case Decimal:
						element.setValue(value.toString(), localeKey);
						break;

					case Date:
						final String stringDateValue = DateTimeUtilFactory.getDateUtil().formatAsDate((Date) value);
						element.setValue(stringDateValue, localeKey);
						break;

					case Datetime:
						final String stringDateTimeValue = DateTimeUtilFactory.getDateUtil().formatAsDateTime((Date) value);
						element.setValue(stringDateTimeValue, localeKey);
						break;

					case StringShortMultiValue:
						final String stringMultiValue = compileParameterShortTextMultiValues((List<String>) value);
						element.setValue(truncateString(stringMultiValue, MAX_VALUE_LENGTH), localeKey);
						break;
					case Product:
						element.setValue((String) value, localeKey);
						break;
					case Category:
						element.setValue((String) value, localeKey);
						break;
					case File:
					case Image:
						final int maxFilePathChars = 255;
						element.setValue(resolvePath((String) value, maxFilePathChars), localeKey);
						break;
					case StringShort:
					case StringLong:
					case Url:
					case HTML:
					default:
						element.setValue(truncateString((String) value, MAX_VALUE_LENGTH), localeKey);
						break;
				}
			}

		}
		return true;
	}
	
	/**
	 * used to truncate string since <code>ParameterValue</code> value is only MAX_VALUE_LENGTH characters.
	 * @param value the value from dialog
	 * @param maxLength the max length of value
	 * @return truncated (if necessary) value
	 */
	private static String truncateString(final String value, final int maxLength) {
		// limit short text less than MAX_VALUE_LENGTH chars. the chars after MAX_VALUE_LENGTHth
		// char will be ignored.
		if (isTooLongString(value, maxLength)) {
			return value.substring(0, maxLength - 1);
		} 
		return value;
	}
	
	/**
	 * check if this string is longer than maximum number of characters allowed.
	 * @param value the string value
	 * @param maxLength the maximum allowed characters
	 * @return true if string is too long, false otherwise
	 */
	private static boolean isTooLongString(final String value, final int maxLength) {
		return value != null && value.length() > maxLength;
	}
	
	/**
	 * Resolves the path to file. Due to different outcome of the path on *nix and
	 * windows this method checks if an extra "/" needs to be removed from path.
	 * Also it prevents the users from entering paths longer than limit of characters
	 * to prevent database insert crash.
	 * @param path the path to file
	 * @param maxLength the maximum number of characters allowed in path
	 * @return resolved path or Error message is path is too long
	 */
	private static String resolvePath(final String path, final int maxLength) {
		if (path == null) {
			return null;
		}
		if (isTooLongString(path, maxLength)) {
			return "Error: Path too long."; //$NON-NLS-1$
		}
		if (path.startsWith("/")) { //$NON-NLS-1$
			return path.substring(1);
		}
		return path;
	}

	/**
	 * Determines if the parameter value has changed.
	 * 
	 * @param value new value
	 * @param parameterValue parameter value to check if it has changed
	 * @return boolean depending on whether it has changed
	 */
	private boolean hasValueChanged(final Object value, final ParameterValue parameterValue) {
		
		if (value == null || StringUtils.isBlank(value.toString())) {
			return false;
		}
		
		if (parameterValue.getValue(getLocaleStringValue()) == null) {
			return true;
		}

		return !value.equals(extractValueFromElement(parameterValue));
	}

	@Override
	public void updateModel(final ParameterValue parameterValue) {
		if (getModel() != null) {			
			getModel().getParameterValues().add(parameterValue);
		}
	}

	/**
	 * Returns selected locale.
	 *
	 * @return Locale - the selected locale
	 */
	public Locale getLocale() {
		return locale;
	}
	
	/**
	 * Returns current locale as string to be used with locale dependent fields.
	 * @return string locale key
	 */
	private String getLocaleStringValue() {
		return this.getLocale().toString();
	}

	/**
	 * Sets the selected locale.
	 *
	 * @param locale - selected locale
	 */
	public void setLocale(final Locale locale) {
		this.locale = locale;
	}

	@Override
	protected boolean isRequiredElement(final ParameterValue value) {
		return value.getParameter().isRequired();
	}
	
}