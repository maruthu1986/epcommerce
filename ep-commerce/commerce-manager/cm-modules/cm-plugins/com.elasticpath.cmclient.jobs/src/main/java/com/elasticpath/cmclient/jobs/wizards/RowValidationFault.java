/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.jobs.wizards;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.osgi.util.NLS;

import com.elasticpath.cmclient.jobs.JobsMessages;
import com.elasticpath.domain.dataimport.ImportFault;

/**
 * Row validation fault description.
 */
public class RowValidationFault {

	private final String rowNumber;

	private final String columnName;

	private final String data;

	private final String error;

	private static final int COLUMN_NAME_INDEX = 0;

	private static final int DATA_INDEX = 3;

	private static final int UNEXPECTED_ERROR = 5;

	private static Map<String, String> codesToMessages;

	private static final String[] CODES = new String[] { "import.csvFile.badRow.wrongColumns", //$NON-NLS-1$
			"import.csvFile.badRow.wrongGuid", //$NON-NLS-1$
//			"import.csvFile.badRow.badGuid", //$NON-NLS-1$ // TOBE ADDED: This is thrown on validation and is not handled by this class
			"import.csvFile.badRow.notNull", //$NON-NLS-1$
			"import.csvFile.badRow.tooLong", //$NON-NLS-1$
			"import.csvFile.badRow.bindError", //$NON-NLS-1$
			"import.unexpected.error", //$NON-NLS-1$
			"import.csvFile.emptyTitleNameNotAllow", //$NON-NLS-1$
			"import.csvFile.titleColumnCannotWrapMultipleLines", //$NON-NLS-1$
			"import.csvFile.badTitleName", //$NON-NLS-1$
			"import.csvFile.badRow.badValue", //$NON-NLS-1$
			"import.csvFile.badRow.salePriceExceedListPrice", //$NON-NLS-1$
			"import.csvFile.badRow.unavailableForChangeSet", //$NON-NLS-1$
			"import.csvFile.badRow.priceListUnavailableForChangeSet", //$NON-NLS-1$
			"import.csvFile.badRow.requiredDependentFieldsMissing" //$NON-NLS-1$
	};
	static {
		int index = 0;
		codesToMessages = new HashMap<String, String>();
		codesToMessages.put(CODES[index++], JobsMessages.get().RunWizard_WrongColumnsError);
		codesToMessages.put(CODES[index++], JobsMessages.get().RunWizard_WrongGuidError);
		codesToMessages.put(CODES[index++], JobsMessages.get().RunWizard_NotNullError);
		codesToMessages.put(CODES[index++], JobsMessages.get().RunWizard_TooLongError);
		codesToMessages.put(CODES[index++], JobsMessages.get().RunWizard_BindError);
		codesToMessages.put(CODES[index++], JobsMessages.get().RunWizard_UnexpectedError);
		codesToMessages.put(CODES[index++], JobsMessages.get().RunWizard_EmptyTitleNameNotAllowError);
		codesToMessages.put(CODES[index++], JobsMessages.get().RunWizard_MultipleLinesError);
		codesToMessages.put(CODES[index++], JobsMessages.get().RunWizard_BadTitleNameError);
		codesToMessages.put(CODES[index++], JobsMessages.get().RunWizard_BindError);
		codesToMessages.put(CODES[index++], JobsMessages.get().RunWizard_Error_SalePriceExceedListPrice);
		codesToMessages.put(CODES[index++], JobsMessages.get().RunWizard_ObjectUnavailableForChangeSet);
		codesToMessages.put(CODES[index++], JobsMessages.get().RunWizard_PriceListUnavailableForChangeSet);
		codesToMessages.put(CODES[index++], JobsMessages.get().RunWizard_RequiredDepdendentFieldsMissing);
	}

	/**
	 * Constructor.
	 *
	 * @param row row number
	 * @param fault fault
	 */
	public RowValidationFault(final int row, final ImportFault fault) {
		rowNumber = String.valueOf(row);
		String errorDescription = codesToMessages.get(fault.getCode());
		if (errorDescription == null) {
			errorDescription = codesToMessages.get(CODES[UNEXPECTED_ERROR]);
		}
		error =
			NLS.bind(errorDescription,
			fault.getArgs());
		columnName = (String) fault.getArgs()[COLUMN_NAME_INDEX];
		if (DATA_INDEX < fault.getArgs().length) {
			data = (String) fault.getArgs()[DATA_INDEX];
		} else {
			data = ""; //$NON-NLS-1$
		}
	}
	/**
	 * Constructor.
	 *
	 * @param row row number
	 * @param errorMessage error message
	 */
	public RowValidationFault(final int row, final String errorMessage) {
		rowNumber = String.valueOf(row);
		error = errorMessage;
		columnName = ""; //$NON-NLS-1$
		data = ""; //$NON-NLS-1$
	}

	/**
	 * Gets row number.
	 *
	 * @return rowNumber
	 */
	public String getRowNumber() {
		return rowNumber;
	}

	/**
	 * Sets row number.
	 *
	 * @return columnName
	 */
	public String getColumnName() {
		return columnName;
	}

	/**
	 * Gets data.
	 *
	 * @return data
	 */
	public String getData() {
		return data;
	}

	/**
	 * Gets error.
	 *
	 * @return error
	 */
	public String getError() {
		return error;
	}

}
