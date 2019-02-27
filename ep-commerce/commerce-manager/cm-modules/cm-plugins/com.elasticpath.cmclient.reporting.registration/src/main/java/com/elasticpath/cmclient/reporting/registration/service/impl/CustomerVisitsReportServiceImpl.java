/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.reporting.registration.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.elasticpath.cmclient.core.LoginManager;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.reporting.ReportService;

/**
 * Creates a CustomerVisitsReport for the dashboard.
 * FIXME: Not currently used
 *
 */
public class CustomerVisitsReportServiceImpl {

	private final ReportService reportService = 
		LoginManager.getInstance().getBean(ContextIdNames.REPORT_SERVICE);
	
	/**
	 * Criteria for counting registrations.
	 */
	public static final String REGI_COUNT_BASE = "select count(c) from CustomerImpl as c"; //$NON-NLS-1$

	/**
	 * Criteria for counting visits.
	 */
	public static final String VISIT_COUNT_BASE = "select count(cs) from CustomerSessionImpl cs"; //$NON-NLS-1$
	
	/**
	 * List of counts registered users, returning visits and new visits between the
	 * start and end dates.
	 *
	 * @param startDate the start date of the report
	 * @param endDate the end date of the report
	 * @return a list containing # registered users, # return visits and # new visits
	 */
	public List<Object> registrationVisitsReport(final Date startDate, final Date endDate) {
		
		List<Object> reportResults = new ArrayList<Object>();

		String query = getRegistrationCountQuery(startDate, endDate);

		ArrayList<Date> params = new ArrayList<Date>();
		if (startDate != null) {
			params.add(startDate);
		}
		if (endDate != null) {
			params.add(endDate);
		}

		// calculate number of registrations
		List<Object[]> dbResults = reportService.execute(query, params.toArray());
		if (dbResults.isEmpty()) {
			reportResults.add(Integer.valueOf(0));
		} else {
			reportResults.add(dbResults.get(0));
		}

		// calculate visits
		query = getReturnVisitsCountQuery(startDate, endDate);
		dbResults = reportService.execute(query, params.toArray());
		if (dbResults.isEmpty()) {
			reportResults.add(Integer.valueOf(0));
		} else {
			reportResults.add(dbResults.get(0));
		}

		query = getNewVisitsCountQuery(startDate, endDate);
		dbResults = reportService.execute(query, params.toArray());
		if (dbResults.isEmpty()) {
			reportResults.add(Integer.valueOf(0));
		} else {
			reportResults.add(dbResults.get(0));
		}

		return reportResults;
	}
	
	/**
	 * Query for calculating aggregate sum of registrations.
	 * 
	 * @param startDate
	 *            start date.
	 * @param endDate
	 *            end date.
	 * @return query Query in HQL format
	 */
	private String getRegistrationCountQuery(final Date startDate,
			final Date endDate) {
		StringBuffer query = new StringBuffer(REGI_COUNT_BASE);
		int numOfParameter = 0;
		if (startDate == null) {
			if (endDate != null) {
				// use < instead of <= to eliminate the duplicate count Sunday
				// (this work, last week) count problem
				numOfParameter++;
				query.append(" where c.creationDate < ?").append(numOfParameter); //$NON-NLS-1$
			}
		} else {
			numOfParameter++;
			query.append(" where c.creationDate >= ?").append(numOfParameter); //$NON-NLS-1$
			if (endDate != null) {
				numOfParameter++;
				query.append(" and c.creationDate < ?").append(numOfParameter); //$NON-NLS-1$
			}
		}
		return query.toString();
	}
	
	/**
	 * Query for calculating aggregate sum of return visits.
	 * 
	 * @param startDate
	 *            start date.
	 * @param endDate
	 *            end date.
	 * @return query Query in HQL format
	 */
	private String getReturnVisitsCountQuery(final Date startDate,
			final Date endDate) {
		final StringBuffer query = new StringBuffer(59);
		query.append(VISIT_COUNT_BASE);
		int numOfParameter = 0;
		if (startDate == null) {
			if (endDate != null) {
				numOfParameter++;
				query.append("where cs.lastAccessedDate <= ?").append(numOfParameter); //$NON-NLS-1$
			}
		} else {
			numOfParameter++;
			query.append(" where cs.lastAccessedDate >= ?").append(numOfParameter); //$NON-NLS-1$
			if (endDate != null) {
				numOfParameter++;
				query.append(" and cs.lastAccessedDate <= ?").append(numOfParameter); //$NON-NLS-1$
			}
		}
		if (query.length() > VISIT_COUNT_BASE.length()) {
			query.append(" and"); //$NON-NLS-1$
		} else {
			query.append(" where"); //$NON-NLS-1$
		}
		query.append(" cs.customer is not null"); //$NON-NLS-1$
		return query.toString();
	}

	/**
	 * Query for calculating aggregate sum of new visits.
	 * 
	 * @param startDate
	 *            start date.
	 * @param endDate
	 *            end date.
	 * @return query Query in HQL format
	 */
	private String getNewVisitsCountQuery(final Date startDate,
			final Date endDate) {
		final StringBuffer query = new StringBuffer(55);
		query.append(VISIT_COUNT_BASE);
		int numOfParameter = 0;
		if (startDate == null) {
			if (endDate != null) {
				numOfParameter++;
				query.append("where cs.lastAccessedDate <= ?").append(numOfParameter); //$NON-NLS-1$
			}
		} else {
			numOfParameter++;
			query.append(" where cs.lastAccessedDate >= ?").append(numOfParameter); //$NON-NLS-1$
			if (endDate != null) {
				numOfParameter++;
				query.append(" and cs.lastAccessedDate <= ?").append(numOfParameter); //$NON-NLS-1$
			}
		}
		if (query.length() > VISIT_COUNT_BASE.length()) {
			query.append(" and"); //$NON-NLS-1$
		} else {
			query.append(" where"); //$NON-NLS-1$
		}
		query.append(" cs.customer is null"); //$NON-NLS-1$
		return query.toString();
	}

	/**
	 * Generic load method for all persistable domain models.
	 * 
	 * @param uid the persisted instance uid
	 * @return the persisted instance if exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	public Object getObject(final long uid) throws EpServiceException {
		throw new UnsupportedOperationException();
	}
	
}
