/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.validation;

import java.math.BigDecimal;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Currency;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.validator.EmailValidator;
import org.apache.log4j.Logger;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.custom.CCombo;

import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.ui.framework.IEpDateTimePicker;
import com.elasticpath.cmclient.core.util.DateTimeUtilFactory;


/**
 * This class defines simple validator classes that can be used
 * to validate user input. The validators are typically used with
 * the Data Binding mechanism.
 *
 * More complex validation code (such as email address validation)
 * should be implemented in separate files and declared here as constants.
 *
 * Some validators are grouped into <code>CompoundValidator</code>s in
 * which several validators must return an OK status for the overall
 * result of the validation to be OK.
 */
@SuppressWarnings({ "PMD.ExcessiveClassLength", "PMD.TooManyMethods", "PMD.GodClass", "PMD.PrematureDeclaration" })
public final class EpValidatorFactory {

	private static final int LIMIT_DAY = 31;

	private static final int LIMIT_MONTH = 12;

	private static final int LIMIT_YEAR = 2999;

	private static final Date UP_LIMIT_DATE;

	private static final String REG_EXPRESSION_STRING_MAXLEN_30 = "^([a-zA-Z_0-9]|[-]){0,30}$"; //$NON-NLS-1$

	private static final String REG_EXPRESSION_ALPHANUMERIC_REQUIRED = "^([a-zA-Z0-9])+$"; //$NON-NLS-1$

	private static final String REG_EXPRESSION_STRING = "^[A-Za-z_0-9]([\\w\\s]{0,}[A-Za-z_0-9])?$"; //$NON-NLS-1$

	private static final String LETTERS_AND_NUMBERS_REQUIRED_REG_EXPR = "(?=.*\\d)(?=.*([a-zA-Z])).*"; //$NON-NLS-1$

	private static final String NO_SPECIAL_CHARS_REG_EXPR =
		"[^!$%&@#<>`~';:,-/={}\\+\\*\\\\^\\(\\)\\[\\]\\{\\}\\|\\\"\\?]*"; //$NON-NLS-1$

	private static final String NO_SPECIAL_CHARS_EXCEPT_DASH_REG_EXPR =
		"[^!$%&@#<>`~';:,/={}\\+\\*\\\\^\\(\\)\\[\\]\\{\\}\\|\\\"\\?]*"; //$NON-NLS-1$

	private static final String NO_SPECIAL_CHARS_EXCEPT_APOSTROPHE_REG_EXPR =
		"[^!$%&@#<>~;:,/={}\\+\\*\\\\^\\(\\)\\[\\]\\{\\}\\|\\\"\\?]*"; //$NON-NLS-1$

	private static final String NO_SPECIAL_CHARS_EXCEPT_AT_SIGN_REG_EXPR =
		"[^!$%&#<>`~';:,/={}\\+\\*\\\\^\\(\\)\\[\\]\\{\\}\\|\\\"\\?]*"; //$NON-NLS-1$

	/**
	 * Phone numbers can be of length 7-50 composing of any numeric digits or symbols ()./+ and space.
	 * The same validation regex expression is used in the storefront validation.xml
	 */
	private static final String PHONE_REG_EXP = "^(([0-9]|\\s|-|\\)|\\(|\\+|\\.)){7,50}$"; //$NON-NLS-1$

	/**
	 * Phone numbers can be of length 3-50 composing of any numeric digits or symbols ()./+ and space.
	 * The same validation regex expression is used in the storefront validation.xml
	 */
	private static final String PHONE_PATTERN_REG_EXP = "^(([0-9]|\\s|-|\\)|\\(|\\+|\\.)){3,50}$"; //$NON-NLS-1$

	private static final String NUMBER_VALIDATION_ERROR_STRING = "Number to validate must be a String or Integer"; //$NON-NLS-1$

	/**
	 * The logger.
	 */
	private static final Logger LOG = Logger.getLogger(EpValidatorFactory.class);

	// limit to (-)200,000,000 as there are problems with validating the spinner values when Integer.MAX_VALUE is used
	private static final int MAX_INTEGER_VALUE = 200000000;

	private static final int MIN_INTEGER_VALUE = -200000000;

	/** Verifies that a value is a boolean true or false. */
	public static final IValidator BOOLEAN_REQUIRED = new IValidator() {

		public IStatus validate(final Object value) {
			String stringValue = (String) value;

			if (!StringUtils.isEmpty(stringValue)
					&& ("true".equals(stringValue) || "false".equals(stringValue))) { //$NON-NLS-1$ //$NON-NLS-2$

				return Status.OK_STATUS;
			}

			return new Status(
					IStatus.ERROR,
					CorePlugin.PLUGIN_ID,
					IStatus.ERROR,
					CoreMessages.get().EpValidatorFactory_Boolean,
					null);
		}
	};

	//private constructor.
	private EpValidatorFactory() {

	}

	/** Verifies that an email address is well-formatted. */
	public static final IValidator EMAIL = new IValidator() {

		public IStatus validate(final Object value) {
			String stringValue = (String) value;
			if (stringValue == null || stringValue.length() == 0) {
				return Status.OK_STATUS;
			}
			if (EmailValidator.getInstance().isValid(stringValue)) {
				return MAX_LENGTH_255.validate(value);
			}
			return new Status(
					IStatus.ERROR,
					CorePlugin.PLUGIN_ID,
					IStatus.ERROR,
					CoreMessages.get().EpValidatorFactory_Email,
					null);
		}
	};

	/** Checks that a String doesn't exceed 2000 characters. */
	public static final IValidator MAX_LENGTH_2000 = new MaxStringLengthValidator(2000);

	/** Checks that a String doesn't exceed 1024 characters. */
	public static final IValidator MAX_LENGTH_1024 = new MaxStringLengthValidator(1024);

	/** Checks that a String doesn't exceed 255 characters. */
	public static final IValidator MAX_LENGTH_255 = new MaxStringLengthValidator(255);

	/** Checks that a String doesn't exceed 65535 characters. */
	public static final IValidator MAX_LENGTH_65535 = new MaxStringLengthValidator(65535);

	/** Checks that a String doesn't exceed 65535*5 characters. */
	public static final IValidator MAX_LENGTH_65535X5 = new MaxStringLengthValidator(65535 * 5);

	/** Checks that a String doesn't exceed 100 characters. */
	public static final IValidator MAX_LENGTH_100 = new MaxStringLengthValidator(100);

	/** Checks that a String doesn't exceed 20 characters. */
	public static final IValidator MAX_LENGTH_20 = new MaxStringLengthValidator(20);

	/** Checks that a String doesn't exceed 20 characters. */
	public static final IValidator MAX_LENGTH_50 = new MaxStringLengthValidator(50);

	/** Checks that a String doesn't exceed 64 characters. */
	public static final IValidator MAX_LENGTH_64 = new MaxStringLengthValidator(64);

	/** Checks that a String doesn't exceed 5 characters. */
	public static final IValidator MAX_LENGTH_5 = new MaxStringLengthValidator(5);

	/** Checks that a String doesn't exceed 16 characters. */
	public static final IValidator MAX_LENGTH_16 = new MaxStringLengthValidator(16);

	/** Checks that a String has no spaces. */
	public static final IValidator NO_LEADING_TRAILING_SPACES = new IValidator() {

		public IStatus validate(final Object value) {
			String stringValue = (String) value;
			if (stringValue.startsWith(" ") || stringValue.endsWith(" ")) { //$NON-NLS-1$ //$NON-NLS-2$
				return new Status(
						IStatus.ERROR,
						CorePlugin.PLUGIN_ID,
						IStatus.ERROR,
						CoreMessages.get().EpValidatorFactory_LeadTrailSpace,
						null);
			}
			return Status.OK_STATUS;
		}
	};

	/** Checks that a String has no leading or trailing spaces (and tabs). */
	public static final IValidator NO_SPACES = new IValidator() {

		public IStatus validate(final Object value) {
			String stringValue = (String) value;
			if (stringValue.indexOf(' ') != -1
					|| stringValue.indexOf('\t') != -1) {
				return new Status(
						IStatus.ERROR,
						CorePlugin.PLUGIN_ID,
						IStatus.ERROR,
						CoreMessages.get().EpValidatorFactory_NoSpace,
						null);
			}
			return Status.OK_STATUS;
		}
	};

	/** Checks that at least one character is present. */
	public static final IValidator REQUIRED = new RequiredValidator(0);

	/** Checks that the combo box has an element selected by making sure that the selected index is not the first index. */
	public static final IValidator REQUIRED_COMBO_FIRST_ELEMENT_NOT_VALID = new RequiredValidator(1);

	/**
	 * Provides a 'required' field validator with custom error message.
	 * @param valueRequired error message for field validation, or default if empty
	 * @return validator, that checks that at least one character is present
	 */
	public static IValidator getRequiredFieldValidatorInstanceWithCustomMessage(final String valueRequired) {
		return new RequiredValidator(0, StringUtils.EMPTY, valueRequired);
	}

	/**
	 * Provides a 'required' combo validator with custom error message.
	 * @param index combo box index to begin validation from
	 * @param valueRequired error message for field validation, or default if empty
	 * @return validator, that checks that the combo box has an element selected by making sure that the selected index is not the first index
	 */
	public static IValidator getRequiredComboValidatorInstanceWithCustomMessage(final int index, final String valueRequired) {
		return new RequiredValidator(index, valueRequired, StringUtils.EMPTY);
	}

	/** Checks that the input is an integer. */
	private static class IntegerValidator implements IValidator {
		public IStatus validate(final Object value) {

			String stringValue = ""; //$NON-NLS-1$
			if (value instanceof String) {
				stringValue = (String) value;
			} else if (value instanceof Integer) {
				stringValue = ((Integer) value).toString();
			} else {
				LOG.error("INTEGER validator: " + value + " is not a supported object type."); //$NON-NLS-1$ //$NON-NLS-2$
				throw new IllegalArgumentException(NUMBER_VALIDATION_ERROR_STRING);
			}

			//Empty value should be allowed, if it is a must have value, use the REQUIRED with this.
			if ((stringValue == null) || (stringValue.length() == 0)) {
				return Status.OK_STATUS;
			}

			int intValue = 0;
			try {
				intValue = Integer.parseInt(stringValue);
			} catch (final NumberFormatException nfe) {

				return new Status(
						IStatus.ERROR,
						CorePlugin.PLUGIN_ID,
						IStatus.ERROR,
						CoreMessages.get().EpValidatorFactory_Integer,
						null);

			}

			return postValidate(intValue);
		}

		protected IStatus postValidate(final int intValue) {
			return Status.OK_STATUS;
		}
	}

	/** Checks that the input is a non negative integer. */
	private static class NonNegativeIntegerValidator extends IntegerValidator {
		private final int edge;
		private final String message;

		NonNegativeIntegerValidator(final int edge, final String message) {
			this.edge = edge;
			this.message = message;
		}

		@Override
		protected final IStatus postValidate(final int intValue) {
			if (intValue >= edge && intValue <= MAX_INTEGER_VALUE) {
				return super.postValidate(intValue);
			}

			return new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, IStatus.ERROR, message, null);
		}
	}

	/** Checks that the input is a non positive integer. */
	private static class NonPositiveIntegerValidator extends IntegerValidator {
		private final int edge;
		private final String message;

		NonPositiveIntegerValidator(final int edge, final String message) {
			this.edge = edge;
			this.message = message;
		}

		@Override
		protected final IStatus postValidate(final int intValue) {
			if (intValue >= MIN_INTEGER_VALUE && intValue <= edge) {
				return super.postValidate(intValue);
			}

			return new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, IStatus.ERROR, message, null);
		}

	}

	/** Checks that the input is an integer. */
	public static final IValidator INTEGER = new IntegerValidator();

	/** Checks that the input is a positive integer. */
	public static final IValidator POSITIVE_INTEGER = new NonNegativeIntegerValidator(1, CoreMessages.get().EpValidatorFactory_PositiveInt);

	/** Checks that the input is a negative integer. */
	public static final IValidator NEGATIVE_INTEGER = new NonPositiveIntegerValidator(-1, CoreMessages.get().EpValidatorFactory_NegativeInt);

	/** Checks that the input is a non positive integer. */
	public static final IValidator NON_POSITIVE_INTEGER =
			new NonPositiveIntegerValidator(0, CoreMessages.get().EpValidatorFactory_NonPositiveInt);

	/** Checks that the input is a non negative integer. */
	public static final IValidator NON_NEGATIVE_INTEGER =
			new NonNegativeIntegerValidator(0, CoreMessages.get().EpValidatorFactory_NonPositiveInt);

	/** Checks that the input is a positive integer. */
	public static final IValidator LONG = new IValidator() {

		public IStatus validate(final Object value) {

			String stringValue = ""; //$NON-NLS-1$
			if (value instanceof String) {
				stringValue = (String) value;
			} else if (value instanceof Long) {
				stringValue = ((Long) value).toString();
			} else {
				LOG.error("LONG validator: " + value + " is not of type String or Long.");  //$NON-NLS-1$//$NON-NLS-2$
				throw new IllegalArgumentException(NUMBER_VALIDATION_ERROR_STRING);
			}
			if (stringValue.length() == 0) {
				return Status.OK_STATUS; // value is not required and therefore status is ok for empty strings
			}
			try {
				Long.parseLong(stringValue);
			} catch (NumberFormatException nfe) {
				return new Status(
						IStatus.ERROR,
						CorePlugin.PLUGIN_ID,
						IStatus.ERROR,
						CoreMessages.get().EpValidatorFactory_Long,
						null);

			}

			return Status.OK_STATUS;
		}
	};

	/** Checks that the input is a positive integer, ignoring leading and trail spaces. */
	public static final IValidator LONG_IGNORE_SPACE = new IValidator() {

		public IStatus validate(final Object value) {

			String stringValue = ""; //$NON-NLS-1$
			if (value instanceof String) {
				stringValue = (String) value;
			} else if (value instanceof Long) {
				stringValue = ((Long) value).toString();
			} else {
				LOG.error("LONG validator: " + value + " is of type String or Long.");  //$NON-NLS-1$//$NON-NLS-2$
				throw new IllegalArgumentException(NUMBER_VALIDATION_ERROR_STRING);
			}
			if (stringValue.trim().length() == 0) {
				return Status.OK_STATUS; // value is not required and therefore status is ok for empty strings
			}
			try {
				Long.parseLong(stringValue.trim());
			} catch (NumberFormatException nfe) {
				return new Status(
						IStatus.ERROR,
						CorePlugin.PLUGIN_ID,
						IStatus.ERROR,
						CoreMessages.get().EpValidatorFactory_Long,
						null);

			}

			return Status.OK_STATUS;
		}
	};

	/**
	 * It's validator based on comparator.
	 */
	public static class BigDecimalValidatorForComparator  extends BigDecimalValidator {
		private final Comparator<BigDecimal> comparator;
		private final String message;

		/**
		 * Default constructor.
		 * @param comparator valid comparator for validated value. It implementation will compare with other model or ui object.
		 * @param message error message
		 */
		public BigDecimalValidatorForComparator(final Comparator<BigDecimal> comparator, final String message) {
			this.comparator = comparator;
			this.message = message;
		}
		@Override
		protected IStatus postValidate(final BigDecimal bigDecimal) {
			int result = comparator.compare(bigDecimal, null);
			if (result == 0 || result == 1) {
				return super.postValidate(bigDecimal);
			}
			return new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, IStatus.ERROR, message, null);
		}
	}

	/** Checks that the input is a big decimal. */
	public static final IValidator BIG_DECIMAL = new BigDecimalValidator();

	/** Checks that the input is big decimal greater than 0. */
	public static final IValidator NON_NEGATIVE_NON_ZERO_BIG_DECIMAL = new NonNegativeNonZeroBigDecimalValidator(
			CoreMessages.get().EpValidatorFactory_NonNegativeNonZeroBigDecimal);

	/** Checks that the input is a non negative big decimal. */
	public static final IValidator NON_NEGATIVE_BIG_DECIMAL = new NonNegativeBigDecimalValidator(
			CoreMessages.get().EpValidatorFactory_NonNegativeBigDecimal);

	/** Checks that the input is a non negative big decimal, with a maximum scale of 8. */
	public static final IValidator NON_NEGATIVE_HIGH_SCALE_BIG_DECIMAL = new NonNegativeBigDecimalValidator(
			CoreMessages.get().EpValidatorFactory_NonNegativeBigDecimal, 8);

	/** Checks that the input is a non positive big decimal. */
	public static final IValidator NON_POSITIVE_BIG_DECIMAL = new NonPositiveBigDecimalValidator(
			CoreMessages.get().EpValidatorFactory_NonPositiveBigDecimal);

	/** Checks that the input is a valid string  - may be blank or have leading or trailing spaces. */
	private static final class GenericStringPatternValidator implements IValidator {

		private final IValidator validator;
		private final String validationMessage;

		GenericStringPatternValidator(final IValidator validator, final String validationMessage) {
			this.validator = validator;
			this.validationMessage = validationMessage;
		}

		public IStatus validate(final Object value) {
			String stringValue = ((String) value).trim();
			if (StringUtils.isBlank(stringValue)) {
				return Status.OK_STATUS; // value is not required and therefore status is ok for empty strings
			}
			IStatus result = validator.validate(stringValue);
			if (!result.isOK()) {
				return new Status(
						IStatus.ERROR,
						CorePlugin.PLUGIN_ID,
						IStatus.ERROR,
						validationMessage,
						null);

			}
			return Status.OK_STATUS;
		}
	}

	/** Checks that the input is a phone number, can be from a blank text, but must parse into a number. */
	public static final IValidator PHONE_REQUIRED = new RegularExpressionValidator(
			PHONE_REG_EXP, CoreMessages.get().EpValidatorFactory_PhoneValid);

	/** Checks that the input is a phone number pattern, can be from a blank text, but must parse into a number. */
	public static final IValidator PHONE_PATTERN_REQUIRED = new RegularExpressionValidator(
			PHONE_PATTERN_REG_EXP, CoreMessages.get().EpValidatorFactory_PhoneValid);

	/** Checks that the input is a phone number - may be blank or have leading or trailing spaces. */
	public static final IValidator PHONE_IGNORE_SPACES = new GenericStringPatternValidator(
			PHONE_REQUIRED, CoreMessages.get().EpValidatorFactory_PhoneValid);

	/** Checks that the input is a phone number pattern - may be blank or have leading or trailing spaces. */
	public static final IValidator PHONE_PATTERN_IGNORE_SPACES = new GenericStringPatternValidator(
			PHONE_PATTERN_REQUIRED, CoreMessages.get().EpValidatorFactory_PhoneValid);

	/** Checks that the input is a fax number, can be from a blank text, but must parse into a number. */
	public static final IValidator FAX_REQUIRED = new RegularExpressionValidator(
			PHONE_REG_EXP, CoreMessages.get().EpValidatorFactory_FaxValid);

	/** Checks that the input is a fax  number - may be blank or have leading or trailing spaces. */
	public static final IValidator FAX_IGNORE_SPACES = new GenericStringPatternValidator(
			FAX_REQUIRED, CoreMessages.get().EpValidatorFactory_FaxValid);

	/**
	 * Checks if the date & time string conform to the Locale dependent format. Uses DateFormat.
	 */
	public static final IValidator DATE_TIME = new IValidator() {
		public IStatus validate(final Object value) {
			if (value instanceof String) {
				String stringValue = value.toString();
				if (stringValue.trim().length() == 0) {
					return Status.OK_STATUS; // no value entered => no check needed
				}
				final Status errorStatus = new Status(
						IStatus.ERROR,
						CorePlugin.PLUGIN_ID,
						IStatus.ERROR,
						CoreMessages.get().EpValidatorFactory_DateTime,
						null);

					ParsePosition parsePosition = new ParsePosition(0);
					Date date = DateTimeUtilFactory.getDateUtil().parseDateTime(stringValue, parsePosition);

				// check that stringValue is valid string representation of date, can not use formatter.parse(stringValue), because it parses only
					// the beginning of the string and then for example such string "Nov 9, 2007sdfafg" will be valid.
					final boolean isParsedSuccessfully = parsePosition.getIndex() == stringValue.length();
					if (!isParsedSuccessfully || date.compareTo(UP_LIMIT_DATE) > 0) {
						return errorStatus; // meaningful error message?
					}
					String parsedDate = DateTimeUtilFactory.getDateUtil().formatAsDateTime(date);
					if (!parsedDate.equalsIgnoreCase(stringValue)) {
						return errorStatus;
					}
				return Status.OK_STATUS;
			}
			LOG.error("DATE_TIME validator: " + value + " is not a String.");  //$NON-NLS-1$//$NON-NLS-2$
			throw new IllegalArgumentException("The date value to be validated must be String"); //$NON-NLS-1$
		}
	};

	/**
	 * Checks if the date conforms to the Locale dependent format. Uses DateFormat.
	 */
	public static final IValidator DATE = new IValidator() {

		public IStatus validate(final Object value) {
			if (value instanceof String) {
				String stringValue = value.toString();
				if (stringValue.length() == 0) {
					return Status.OK_STATUS; // no value entered => no check needed
				}

				final Status errorStatus =
						new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, IStatus.ERROR, CoreMessages.get().EpValidatorFactory_Date, null);

				ParsePosition parsePosition = new ParsePosition(0);
				Date date = DateTimeUtilFactory.getDateUtil().parseDate(stringValue, parsePosition);

				// check that stringValue is valid string representation of date, can not use formatter.parse(stringValue), because it parses only
				// the beginning of the string and then for example such string "Nov 9, 2007sdfafg" will be valid.
				final boolean isParsedSuccessfully = parsePosition.getIndex() == stringValue.length();
				if (!isParsedSuccessfully || date.compareTo(UP_LIMIT_DATE) > 0) {
					return errorStatus; // meaningful error message?
				}

				return Status.OK_STATUS;
			}
			LOG.error("DATE validator: " + value + " is not a String."); //$NON-NLS-1$//$NON-NLS-2$
			throw new IllegalArgumentException("The date value to be validated must be a String"); //$NON-NLS-1$
		}
	};


	/** Checks that the input is a valid percent value that is greater than 0 and no more than 100. */
	public static final IValidator PERCENTAGE = new IValidator() {
		private static final int MIN_LENGTH = 1;

		private static final int MIN_PERCENTAGE_EXCLUSIVE = 0;

		private static final int MAX_PERCENTAGE_INCLUSIVE = 100;

		public IStatus validate(final Object value) {
			String stringValue = ""; //$NON-NLS-1$
			if (value instanceof String) {
				stringValue = (String) value;
			} else {
				throw new IllegalArgumentException(NUMBER_VALIDATION_ERROR_STRING);
			}
			double doubleValue = 0;
			try {
				if (stringValue.length() < MIN_LENGTH) {
					return new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, IStatus.ERROR,
							CoreMessages.get().EpValidatorFactory_Percent, null);
				}
				if (!"".equals(stringValue)) { //$NON-NLS-1$
					doubleValue = Double.parseDouble(stringValue);
				}
			} catch (NumberFormatException nfe) {
				return new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, IStatus.ERROR,
						CoreMessages.get().EpValidatorFactory_Percent, null);

			}
			if (doubleValue <= MIN_PERCENTAGE_EXCLUSIVE || doubleValue > MAX_PERCENTAGE_INCLUSIVE) {
				return new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, IStatus.ERROR,
						CoreMessages.get().EpValidatorFactory_Percent, null);
			}
			return Status.OK_STATUS;
		}
	};

	/** Checks that the input is a valid month. */
	public static final IValidator MONTH = new IValidator() {
		private static final int MIN_LENGTH = 1;

		private static final int MONTH_MAX = 12;

		public IStatus validate(final Object value) {
			String stringValue = ""; //$NON-NLS-1$
			if (value instanceof String) {
				stringValue = (String) value;
			} else {
				throw new IllegalArgumentException(NUMBER_VALIDATION_ERROR_STRING);
			}
			long longValue = 0;
			try {
				if (stringValue.length() < MIN_LENGTH) {
					return new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, IStatus.ERROR,
							CoreMessages.get().EpValidatorFactory_Month, null);
				}
				if (!"".equals(stringValue)) { //$NON-NLS-1$
					longValue = Long.parseLong(stringValue);
				}
			} catch (NumberFormatException nfe) {
				return new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, IStatus.ERROR,
						CoreMessages.get().EpValidatorFactory_Month, null);

			}
			if (longValue <= 0 || longValue > MONTH_MAX) {
				return new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, IStatus.ERROR,
						CoreMessages.get().EpValidatorFactory_Month, null);
			}
			return Status.OK_STATUS;
		}
	};

	/** Checks that the input is a valid year before 2025. */
	abstract static class AbstractYearValidator implements IValidator {
		private static final int MIN_LENGTH = 4;

		private static final int YEAR_MAX = 2025;

		/**
		 * Checks that the input is a valid year between minYear and YEAR_MAX.
		 *
		 * @param value value to be validated.
		 * @param minYear minimal valid year.
		 * @return a status object indicating whether the validation succeeded
    	 *         {@link IStatus#isOK()} or not. Never null.
		 */
		public IStatus validate(final Object value, final int minYear) {
			String stringValue = ""; //$NON-NLS-1$
			if (value instanceof String) {
				stringValue = (String) value;
			} else {
				throw new IllegalArgumentException(NUMBER_VALIDATION_ERROR_STRING);
			}
			long longValue = 0;
			try {
				if (stringValue.length() < MIN_LENGTH) {
					return new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, IStatus.ERROR,
							CoreMessages.get().EpValidatorFactory_Year, null);
				}
				if (!"".equals(stringValue)) { //$NON-NLS-1$
					longValue = Long.parseLong(stringValue);
				}
			} catch (final NumberFormatException nfe) {
				return new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, IStatus.ERROR,
						CoreMessages.get().EpValidatorFactory_Year, null);

			}
			if (longValue < minYear || longValue > YEAR_MAX) {
				return new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, IStatus.ERROR,
						CoreMessages.get().EpValidatorFactory_Year, null);
			}
			return Status.OK_STATUS;
		}
	}

	/** Checks that the input is a valid year after the current one. */
	public static final IValidator YEAR = new AbstractYearValidator() {

		public IStatus validate(final Object value) {
			return validate(value, Calendar.getInstance().get(Calendar.YEAR));
		}
	};

	/** Checks that the input is a valid year after 1949. */
	public static final IValidator START_YEAR = new AbstractYearValidator() {

		private static final int YEAR_MIN = 1949;

		public IStatus validate(final Object value) {
			return validate(value, YEAR_MIN);
		}
	};

	/** Check the folder name.*/
	public static final IValidator FOLDER_NAME = new RegularExpressionValidator(REG_EXPRESSION_STRING_MAXLEN_30,
			CoreMessages.get().EpValidatorFactory_FolderName);

	/** Validate product name.*/
	public static final IValidator PRODUCT_NAME = new RegularExpressionValidator(REG_EXPRESSION_STRING_MAXLEN_30,
			CoreMessages.get().EpValidatorFactory_ProductName);

	/** Validate required product name.*/
	public static final IValidator PRODUCT_NAME_REQUIRED =  new CompoundValidator(PRODUCT_NAME, REQUIRED);

	/** Validates that string contains at least one letter and one digit. */
	public static final IValidator LETTER_AND_DIGIT_REQUIRED = new RegularExpressionValidator(LETTERS_AND_NUMBERS_REQUIRED_REG_EXPR,
			CoreMessages.get().EpValidatorFactory_LetterAndDigitRequired);

	/** Alphanumeric required .*/
	public static final IValidator ALPHANUMERIC_REQUIRED = new RegularExpressionValidator(REG_EXPRESSION_ALPHANUMERIC_REQUIRED,
			CoreMessages.get().EpValidatorFactory_ALPHANUMERIC_REQUIRED);

	/** Checks that a password is a minimum of 8 characters. */
	public static final IValidator PASSWORD_MIN_LENGTH_8 = new IValidator() {
		private static final int MIN_LENGTH = 8;

		public IStatus validate(final Object value) {
			String stringValue = (String) value;
			if (stringValue.length() < MIN_LENGTH) {
				return new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, IStatus.ERROR,
						CoreMessages.get().EpValidatorFactory_PasswordMinCharLength_8, null);
			}
			return Status.OK_STATUS;
		}
	};

	/** Compound Validator - required password. */
	public static final IValidator PASSWORD = new CompoundValidator(PASSWORD_MIN_LENGTH_8, REQUIRED, NO_SPACES);

	/** Compound Validator - for new password. */
	public static final IValidator NEW_PASSWORD =
			new CompoundValidator(PASSWORD_MIN_LENGTH_8, NO_SPACES, LETTER_AND_DIGIT_REQUIRED);

	/** Compound Validator - required email address. */
	public static final IValidator EMAIL_REQUIRED = new CompoundValidator(EMAIL, REQUIRED);

	/** Compound validator - validates 100-character string fields that are required and have no leading spaces. */
	public static final IValidator STRING_100_REQUIRED = new CompoundValidator(MAX_LENGTH_100, REQUIRED);

	/** Compound validator - validates 255-character string fields that are required and have no leading spaces. */
	public static final IValidator STRING_255_REQUIRED = new CompoundValidator(MAX_LENGTH_255, REQUIRED);

	/** Compound validator - validates 100-character string fields that are required and have no spaces within. */
	public static final IValidator STRING_100_NOSPACES_REQUIRED = new CompoundValidator(MAX_LENGTH_100, NO_SPACES, REQUIRED);

	/** Compound validator - validates 255-character string fields that are required and have no spaces within. */
	public static final IValidator STRING_255_NOSPACES_REQUIRED = new CompoundValidator(MAX_LENGTH_255, NO_SPACES, REQUIRED);

	/** Compount validator - validates 65,535-character string fields that are required. */
	public static final IValidator STRING_65535_REQUIRED = new CompoundValidator(MAX_LENGTH_65535, REQUIRED);

	/** Compount validator - validates 65535*5-character string fields that are required (especially for XML file uploads). */
	public static final IValidator STRING_65535X5_REQUIRED = new CompoundValidator(MAX_LENGTH_65535X5, REQUIRED);

	/** Compound Validator - requires date. */
	public static final IValidator DATE_REQUIRED = new CompoundValidator(DATE, REQUIRED);

	/** Compound Validator - requires date & time. */
	public static final IValidator DATE_TIME_REQUIRED = new CompoundValidator(DATE_TIME, REQUIRED);

	/** Compound Validator - requires price. */
	public static final IValidator PRICE_REQUIRED = new CompoundValidator(BIG_DECIMAL, REQUIRED);

	/** Compound Validator - requires decimal. */
	public static final IValidator BIG_DECIMAL_REQUIRED = new CompoundValidator(BIG_DECIMAL, REQUIRED);

	/** Compound Validator - requires non-negative decimal. */
	public static final IValidator NON_NEGATIVE_BIG_DECIMAL_REQUIRED =
			new CompoundValidator(NON_NEGATIVE_BIG_DECIMAL, REQUIRED);

	/** Compound Validator - requires non-negative high scale decimal. */
	public static final IValidator NON_NEGATIVE_HIGH_PRECISION_BIG_DECIMAL_REQUIRED =
			new CompoundValidator(NON_NEGATIVE_HIGH_SCALE_BIG_DECIMAL, REQUIRED);

	/** Compound Validator - requires non-negative non-zero decimal. */
	public static final IValidator NON_NEGATIVE_NON_ZERO_BIG_DECIMAL_REQUIRED =
			new CompoundValidator(NON_NEGATIVE_NON_ZERO_BIG_DECIMAL, REQUIRED);

	/** Compound Validator - folder name. */
	public static final IValidator FOLDER_NAME_REQUIRED = new CompoundValidator(FOLDER_NAME, REQUIRED);

	/** Compound Validator - Positive integer, required. */
	public static final IValidator POSITIVE_INTEGER_REQUIRED = new CompoundValidator(POSITIVE_INTEGER, REQUIRED);

	/** Compound Validator - Percentage, decimal, required. */
	public static final IValidator PERCENTAGE_REQUIRED = new CompoundValidator(PERCENTAGE, REQUIRED);

	/** validator for attribute key creation. */
	public static final IValidator ATTRIBUTE_KEY = new CompoundValidator(
			new RegularExpressionValidator(REG_EXPRESSION_STRING, CoreMessages.get().EpValidatorFactory_AttributeKey),
			MAX_LENGTH_255, REQUIRED, NO_SPACES);

	/** validator for attribute name creation. */
	public static final IValidator ATTRIBUTE_NAME = new CompoundValidator(
			new RegularExpressionValidator(REG_EXPRESSION_STRING, CoreMessages.get().EpValidatorFactory_AttributeName),
			MAX_LENGTH_255, REQUIRED);

	/** validator for non usage of special characters. */
	public static final IValidator NO_SPECIAL_CHARACTERS = new CompoundValidator(
			new RegularExpressionValidator(NO_SPECIAL_CHARS_REG_EXPR, CoreMessages.get().EpValidatorFactory_NoSpecialCharacters),
			MAX_LENGTH_255);

	/**
	 * validator for non usage of special characters.
	 */
	public static final IValidator NO_SPECIAL_CHARACTERS_EXCEPT_DASH = new CompoundValidator(
			new RegularExpressionValidator(NO_SPECIAL_CHARS_EXCEPT_DASH_REG_EXPR,
					CoreMessages.get().EpValidatorFactory_NoSpecialCharacters),
			MAX_LENGTH_255);

/**
	 * validator for non usage of special characters.
	 */
	public static final IValidator NO_SPECIAL_CHARACTERS_EXCEPT_APOSTROPHE = new CompoundValidator(
			new RegularExpressionValidator(NO_SPECIAL_CHARS_EXCEPT_APOSTROPHE_REG_EXPR,
					CoreMessages.get().EpValidatorFactory_NoSpecialCharacters),
			MAX_LENGTH_255);

	/**
	 * validator for non usage of special characters (except @ sign).
	 */
	public static final IValidator NO_SPECIAL_CHARACTERS_EXCEPT_AT_SIGN = new CompoundValidator(
			new RegularExpressionValidator(NO_SPECIAL_CHARS_EXCEPT_AT_SIGN_REG_EXPR,
					CoreMessages.get().EpValidatorFactory_NoSpecialCharacters),
			MAX_LENGTH_255);

	/** Validate codes.*/

	/**
	 * Validate product code.
	 */
	public static final IValidator PRODUCT_CODE_NOT_REQUIRED =
			new CompoundValidator(MAX_LENGTH_64, NO_SPACES, NO_SPECIAL_CHARACTERS_EXCEPT_DASH);

	/**
	 * Validate product code.
	 */
	public static final IValidator PRODUCT_CODE = new CompoundValidator(PRODUCT_CODE_NOT_REQUIRED, REQUIRED);

	/**
	 * Validate catalog code.
	 */
	public static final IValidator CATALOG_CODE = new CompoundValidator(MAX_LENGTH_64, REQUIRED, NO_SPACES, NO_SPECIAL_CHARACTERS);

	/**
	 * Validate category code.
	 */
	public static final IValidator CATEGORY_CODE = new CompoundValidator(MAX_LENGTH_64, REQUIRED, NO_SPACES, NO_SPECIAL_CHARACTERS);

	/**
	 * Validate SKU code.
	 */
	public static final IValidator SKU_CODE_NOT_REQURED = new CompoundValidator(MAX_LENGTH_64, NO_SPACES, NO_SPECIAL_CHARACTERS_EXCEPT_DASH);

	/**
	 * Validate SKU code.
	 */
	public static final IValidator SKU_CODE = new CompoundValidator(SKU_CODE_NOT_REQURED, REQUIRED);

	/**
	 * Validate warehouse code.
	 */
	public static final IValidator WAREHOUSE_CODE = new CompoundValidator(MAX_LENGTH_64, REQUIRED, NO_SPACES, NO_SPECIAL_CHARACTERS);

	/**
	 * Validate SKU option code.
	 */
	public static final IValidator SKU_OPTION_CODE = new CompoundValidator(MAX_LENGTH_64, REQUIRED, NO_SPACES, NO_SPECIAL_CHARACTERS);

	/**
	 * Validate SKU option code.
	 */
	public static final IValidator BRAND_CODE = new CompoundValidator(MAX_LENGTH_64, REQUIRED, NO_SPACES, NO_SPECIAL_CHARACTERS);

	/**
	 * Validate Cart Item Modifier Group Field Option value.
	 */
	public static final IValidator CARTITEM_MODIFIER_OPTION_VALUE =
			new CompoundValidator(MAX_LENGTH_64, REQUIRED, NO_SPACES, NO_SPECIAL_CHARACTERS);

	/**
	 * Validate Cart Item Modifier Group Field code.
	 */
	public static final IValidator CARTITEM_MODIFIER_FIELD_CODE = new CompoundValidator(MAX_LENGTH_64, REQUIRED, NO_SPACES);

	/**
	 * Validate Cart Item Modifier Group code.
	 */
	public static final IValidator CARTITEM_MODIFIER_GROUP_CODE = new CompoundValidator(MAX_LENGTH_64, REQUIRED, NO_SPACES);

	static {
		final Calendar calendar = Calendar.getInstance();
		calendar.set(LIMIT_YEAR, LIMIT_MONTH, LIMIT_DAY);
		UP_LIMIT_DATE = calendar.getTime();
	}

	/**
	 * A common validator for maximum string length.
	 */
	private static class MaxStringLengthValidator implements IValidator {
		private final int stringLength;

		MaxStringLengthValidator(final int stringLength) {
			this.stringLength = stringLength;
		}

		public IStatus validate(final Object value) {
			final String stringValue = (String) value;

			if (stringValue.length() > stringLength) {
				return new Status(IStatus.ERROR,
						CorePlugin.PLUGIN_ID,
						IStatus.ERROR,

						NLS.bind(CoreMessages.get().EpValidatorFactory_MaxCharLength,
						stringLength),
						null);
			}
			return Status.OK_STATUS;
		}
	}


	/**
	 * Creates a validator that verifies if a from date is after the to date (rounded to the nearest minute).
	 *
	 * @param fromDatePicker the from date picker
	 * @param toDatePicker the to date picker
	 * @return validator instance
	 */
	public static IValidator createToDateValidator(final IEpDateTimePicker fromDatePicker, final IEpDateTimePicker toDatePicker) {
		return createToAfterFromDateValidator(fromDatePicker, toDatePicker,
				CoreMessages.get().EpValidatorFactory_ToDateBeforeFromDate);
	}

	/**
	 * Creates a validator that verifies if a disable date is after the enable date (rounded to the nearest minute).
	 *
	 * @param enableDatePicker the enable date picker
	 * @param disableDatePicker the disable date picker
	 * @return validator instance
	 */
	public static IValidator createDisableDateValidator(final IEpDateTimePicker enableDatePicker, final IEpDateTimePicker disableDatePicker) {
		return createDisableDateValidator(enableDatePicker, disableDatePicker,
				CoreMessages.get().EpValidatorFactory_DisableDateBeforeStartDate);
	}

	/**
	 * Creates a validator that verifies if a start date is after the end date (rounded to the nearest minute).
	 *
	 * @param enableDatePicker the enable date picker
	 * @param disableDatePicker the disable date picker
	 * @param validationMessage validation message to show
	 * @return validator instance
	 */
	public static IValidator createDisableDateValidator(final IEpDateTimePicker enableDatePicker,
														final IEpDateTimePicker disableDatePicker,
														final String validationMessage) {
		return createToAfterFromDateValidator(enableDatePicker, disableDatePicker, validationMessage);
	}

	private static IValidator createToAfterFromDateValidator(final IEpDateTimePicker fromDatePicker,
			final IEpDateTimePicker toDatePicker, final String message) {
		return new IValidator() {
			public IStatus validate(final Object value) {
				if (toDatePicker.getDate() == null || fromDatePicker.getDate() == null) {
					return Status.OK_STATUS;
				}
				Date disableDateMinuteRounded = DateUtils.round(toDatePicker.getDate(), Calendar.MINUTE);
				Date enableDateMinuteRounded = DateUtils.round(fromDatePicker.getDate(), Calendar.MINUTE);
				if (!disableDateMinuteRounded.after(enableDateMinuteRounded)) {
					return new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, IStatus.ERROR, message, null);
				}
				return Status.OK_STATUS;
			}
		};
	}

	/**
	 * Creates a validator that verifies if is selected a cart item modifier field type.
	 *
	 * @param fieldTypeCombo Field Type control widget.
	 * @return validator instance
	 */
	public static IValidator createCComboFieldTypeSelectionValidator(final CCombo fieldTypeCombo) {
		return new IValidator() {
			public IStatus validate(final Object value) {
				if (fieldTypeCombo.getSelectionIndex() <= 0) {
					return new Status(IStatus.ERROR,
							CorePlugin.PLUGIN_ID,
							IStatus.ERROR,
							CoreMessages.get().EpValidatorFactory_NoCComboSTypeelecction,
							null);
				} else {
					return Status.OK_STATUS;
				}
			}
		};
	}


	/**
	 * Validator class for valid currency code.
	 */
	private static class CurrencyValidator implements IValidator {
		public IStatus validate(final Object value) {
			final String currencyCode = (String) value;

			try {
				Currency.getInstance(currencyCode);
			} catch (final Exception e) {
				return new Status(IStatus.ERROR,
						CorePlugin.PLUGIN_ID,
						IStatus.ERROR,

						NLS.bind(CoreMessages.get().EpValidatorFactory_CurrencyCode,
						currencyCode),
						null);
			}
			return Status.OK_STATUS;
		}
	}

	/**
	 * Predefined currency code validator.
	 */
	public static final IValidator CURRENCY_CODE = new CurrencyValidator();

}
