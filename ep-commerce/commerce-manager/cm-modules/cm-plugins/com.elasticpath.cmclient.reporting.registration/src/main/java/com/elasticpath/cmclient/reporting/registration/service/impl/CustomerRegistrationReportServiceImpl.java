/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.reporting.registration.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.elasticpath.cmclient.core.LoginManager;
import com.elasticpath.cmclient.reporting.ReportType;
import com.elasticpath.cmclient.reporting.registration.CustomerRegistrationReportMessages;
import com.elasticpath.cmclient.reporting.registration.CustomerRegistrationReportSection;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.persistence.openjpa.support.JpqlQueryBuilder;
import com.elasticpath.persistence.openjpa.support.JpqlQueryBuilderWhereGroup;
import com.elasticpath.persistence.openjpa.support.JpqlQueryBuilderWhereGroup.JpqlMatchType;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.reporting.ReportService;

/**
 * Local CustomerReportService. This service is a wrapper for calling the
 * (possibly remote) ReportService, because BIRT's javascript engine does not
 * handle Spring proxy beans well.
 * The service is responsible for building a CustomerRegistration prepared JPA query and a
 * corresponding parameter list, then sending the query to the server-side service.
 */
@SuppressWarnings({"PMD.GodClass"})
public class CustomerRegistrationReportServiceImpl {

	private static final int UID_COLUMN = 1;
	private static final int DATE_COLUMN = 0;
	private static final String FALSE = "false"; //$NON-NLS-1$
	private static final String CP_BE_NOTIFIED = "CP_BE_NOTIFIED"; //$NON-NLS-1$
	private static final String CP_FIRST_NAME = "CP_FIRST_NAME"; //$NON-NLS-1$
	private static final String CP_LAST_NAME = "CP_LAST_NAME"; //$NON-NLS-1$
	private static final String CP_EMAIL = "CP_EMAIL"; //$NON-NLS-1$
	private static final String CP_PHONE = "CP_PHONE"; //$NON-NLS-1$
	private static final String CP_ANONYMOUS_CUST = "CP_ANONYMOUS_CUST"; //$NON-NLS-1$
	private static final String CP_PREF_CURR = "CP_PREF_CURR"; //$NON-NLS-1$
	private static final String CP_HTML_EMAIL = "CP_HTML_EMAIL"; //$NON-NLS-1$
	private static final String CP_PREF_LOCALE = "CP_PREF_LOCALE"; //$NON-NLS-1$

	private static final int FNAME = 2;
	private static final int LNAME = 3;
	private static final int EMAIL = 4;
	private static final int PHONE = 5;
	private static final int ANNON = 6;
	private static final int LOCALE = 7;
	private static final int CURRENCY = 8;
	private static final int RECEIVE_HTML_EMAIL = 9;
	private static final int NEWSLETTER = 10;

	private ReportService getReportService() {
		return LoginManager.getInstance().getBean(ContextIdNames.REPORT_SERVICE);
	}

	/**
	 * List registered users with create date between start and end dates.
	 *
	 * @return a list of registered users.
	 */
	public List<Object[]> registrationReport() {
		Map<String, Object> params = getCustomerRegistrationReportParameters();
		JpqlQueryBuilder builder = buildQuery(params);
		List<Object[]> queryResult = getReportService().execute(builder.toString(), builder.getParameterList().toArray());
		return flattenListFromDB(queryResult);
	}

	/**
	 * Constructs a {@link JpqlQueryBuilder} which corresponds to the given parameters.
	 *
	 * @param params the raw report parameters
	 * @return the prepare statement query string
	 */
	protected JpqlQueryBuilder buildQuery(final Map<String, Object> params) {
		JpqlQueryBuilder queryBuilder = new JpqlQueryBuilder("CustomerImpl", "c", //$NON-NLS-1$ //$NON-NLS-2$
				"c.creationDate, c.uidPk, cp.localizedAttributeKey, cp.shortTextValue, cp.booleanValue");  //$NON-NLS-1$
		queryBuilder.appendOrderBy("c.creationDate", false); //$NON-NLS-1$
		queryBuilder.appendInnerJoin("c.profileValueMap", "cp");  //$NON-NLS-1$//$NON-NLS-2$

		if (!isAnonymousRegistrationIncluded(params)) {
			queryBuilder.appendInnerJoin("c.profileValueMap", "cp2"); //$NON-NLS-1$ //$NON-NLS-2$
			JpqlQueryBuilderWhereGroup whereGroup = queryBuilder.getDefaultWhereGroup();
			whereGroup.appendWhereEquals("cp2.booleanValue", false); //$NON-NLS-1$
			whereGroup.appendWhereEquals("cp2.localizedAttributeKey", CP_ANONYMOUS_CUST); //$NON-NLS-1$

			whereGroup.appendWhereInCollection("cp.localizedAttributeKey", Arrays.asList(CP_FIRST_NAME, //$NON-NLS-1$
					CP_LAST_NAME, CP_EMAIL, CP_ANONYMOUS_CUST, CP_PHONE, CP_PREF_LOCALE, CP_PREF_CURR, CP_HTML_EMAIL, CP_BE_NOTIFIED));
		}

		applyQueryFilters(queryBuilder, params);
		return queryBuilder;
	}

	private void applyQueryFilters(final JpqlQueryBuilder queryBuilder, final Map<String, Object> params) {
		JpqlQueryBuilderWhereGroup whereGroup = queryBuilder.getDefaultWhereGroup();
		if (params.get(CustomerRegistrationReportSection.PARAMETER_STORE) != null) {
			queryBuilder.appendInnerJoin("StoreImpl", "s", "c.storeCode = s.code"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			whereGroup.appendWhereInCollection("s.name", //$NON-NLS-1$
					(Collection<?>) params.get(CustomerRegistrationReportSection.PARAMETER_STORE));
		}

		if (params.get(CustomerRegistrationReportSection.PARAMETER_START_DATE) != null) {
			whereGroup.appendWhere("c.creationDate", ">=", //$NON-NLS-1$ //$NON-NLS-2$
					params.get(CustomerRegistrationReportSection.PARAMETER_START_DATE), JpqlMatchType.AS_IS);
		}

		if (params.get(CustomerRegistrationReportSection.PARAMETER_END_DATE) != null) {
			whereGroup.appendWhere("c.creationDate", "<=", //$NON-NLS-1$ //$NON-NLS-2$
					params.get(CustomerRegistrationReportSection.PARAMETER_END_DATE), JpqlMatchType.AS_IS);
		}
	}

	/**
	 * Determine whether anonymous registrations are to be included in the report.
	 * @param params the report parameters
	 * @return true if anonymous registrations are to be included in the report
	 */
	private boolean isAnonymousRegistrationIncluded(final Map<String, Object> params) {
		Object anonymousRegistrations = params.get(CustomerRegistrationReportSection.PARAMETER_ANONYMOUS_REGISTRATION);
		boolean anonymous = false;
		if (anonymousRegistrations != null) {
			if (!(anonymousRegistrations instanceof Boolean)) {
				throw new EpServiceException("AnonymousRegistration parameter must be an instance of java.lang.Boolean."); //$NON-NLS-1$
			}

			if ((Boolean) anonymousRegistrations) {
				anonymous = true;
			}
		}
		return anonymous;
	}

	private Date getDateParam(final String paramKey, final Map<String, Object> params) {
		Object date = params.get(paramKey);
		if (date == null) {
			return null;
		}
		try {
			return (Date) date;
		} catch (ClassCastException cce) {
			throw new EpServiceException("Parameter " + paramKey  //$NON-NLS-1$
					+ "is of an incorrect type. Expecting java.util.Date.", cce); //$NON-NLS-1$
		}
	}

	/**
	 * @return parameters for the CustomerRegistrationReport
	 */
	Map<String, Object> getCustomerRegistrationReportParameters() {
		Map<String, Object> params = new HashMap<String, Object>();
		for (ReportType reportType : ReportTypeManager.getInstance().getReportTypes()) {
			if (reportType.getName().equalsIgnoreCase(
					CustomerRegistrationReportMessages.report)) {
				params = reportType.getReport().getParameters();
			}
		}
		return params;
	}

	/**
	 * Create the parameter list to be sent in with the prepared statement.
	 * @param params the raw report parameters
	 * @return the parameter list
	 */
	protected List<Object> createParameterList(final Map<String, Object> params) {
		List<Object> parameterList = new ArrayList<Object>();

		Date startDate = getDateParam(CustomerRegistrationReportSection.PARAMETER_START_DATE, params);
		if (startDate != null) {
			parameterList.add(startDate);
		}
		Date endDate = getDateParam(CustomerRegistrationReportSection.PARAMETER_END_DATE, params);
		if (endDate != null) {
			parameterList.add(endDate);
		}
		return parameterList;
	}

	/**
	 * Gets list from DB and creates data that can be displayed from report.
	 * This method became necessary because of schema changes relating to
	 * customer profile. We expect the data be be well-formed; ie, coming in the
	 * same order as in the junit test case. FIXME: Too complex - Simplify and
	 * stop suppressing warnings.
	 *
	 * CustomerRegistration records are assumed to be returned as a collection of rows related to customers.
	 * One customer will likely have several rows in the returned dataset, where each row details a different
	 * field in the customer's profile.
	 * The column headings are as follows:
	 * Customer CreationDate,
	 * Customer UIDPK,
	 * CustomerProfile AttributeKey (e.g. CP_EMAIL),
	 * CustomerProfile ShortTextValue, (e.g. some.customer@somedomain.com)
	 * CustomerProfile booleanValue (often null, may be T/F in cases where ShortTextValue would be null).
	 *
	 * This method needs to flatten the returned records into a table where each customer record has a single row.
	 *
	 * @param listFromDB list from db.
	 * @return list for display in report.
	 */
	@SuppressWarnings("PMD.CyclomaticComplexity")
	private List<Object[]> flattenListFromDB(final List<Object[]> listFromDB) {
		if (listFromDB == null || listFromDB.isEmpty()) {
			return new ArrayList<Object[]>();
		}
		Map<Object, Object[]> flattenedRecords = new HashMap<Object, Object[]>();

		Object[] row = null;
		Object[] currentRow = null;
		String emptyString = ""; //$NON-NLS-1$
		for (int i = 0; i < listFromDB.size(); i++) {
			row = listFromDB.get(i);
			if (flattenedRecords.get(row[UID_COLUMN]) == null) {
				currentRow = new Object[] { row[DATE_COLUMN], row[UID_COLUMN], emptyString, emptyString, emptyString,
					emptyString, emptyString, emptyString, emptyString,
					CustomerRegistrationReportMessages.no_as_literal,
					CustomerRegistrationReportMessages.no_as_literal };
				flattenedRecords.put(row[UID_COLUMN], currentRow);
			} else {
				currentRow = flattenedRecords.get(row[UID_COLUMN]);
			}

			final int fieldName = 2;
			final int fieldValue = 3;
			final int booleanValue = 4;

			if (row[fieldName].equals(CP_FIRST_NAME) && row[fieldValue] != null) {
				currentRow[FNAME] = row[fieldValue];

			} else if (row[fieldName].equals(CP_LAST_NAME) && row[fieldValue] != null) {
				currentRow[LNAME] = row[fieldValue];

			} else if (row[fieldName].equals(CP_EMAIL) && row[fieldValue] != null) {
				currentRow[EMAIL] = row[fieldValue];

			} else if (row[fieldName].equals(CP_PHONE) && row[fieldValue] != null) {
				currentRow[PHONE] = row[fieldValue];

			} else if (row[fieldName].equals(CP_ANONYMOUS_CUST) && row[booleanValue] != null) {
				if (String.valueOf(row[booleanValue]) == FALSE) {
					currentRow[ANNON] = CustomerRegistrationReportMessages.registered;
				} else {
					currentRow[ANNON] = CustomerRegistrationReportMessages.guest;
				}

			} else if (row[fieldName].equals(CP_PREF_CURR) && row[fieldValue] != null) {
				currentRow[CURRENCY] = row[fieldValue];
			} else if (row[fieldName].equals(CP_BE_NOTIFIED) && row[booleanValue] != null) {
				if (String.valueOf(row[booleanValue]) == FALSE) {
					currentRow[NEWSLETTER] = CustomerRegistrationReportMessages.no_as_literal;
				} else {
					currentRow[NEWSLETTER] = CustomerRegistrationReportMessages.yes_as_literal;
				}

			} else if (row[fieldName].equals(CP_HTML_EMAIL) && row[booleanValue] != null) {
				if (String.valueOf(row[booleanValue]) == FALSE) {
					currentRow[RECEIVE_HTML_EMAIL] = CustomerRegistrationReportMessages.no_as_literal;
				} else {
					currentRow[RECEIVE_HTML_EMAIL] = CustomerRegistrationReportMessages.yes_as_literal;
				}

			} else if (row[fieldName].equals(CP_PREF_LOCALE) && row[fieldValue] != null) {
				currentRow[LOCALE] = new Locale((String) row[fieldValue]).getDisplayName();
			}
		}

		return new ArrayList<Object[]>(flattenedRecords.values());
	}
}
