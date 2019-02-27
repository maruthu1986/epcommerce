/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.reporting.giftcertificatesummary.service.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;

import com.elasticpath.cmclient.core.LoginManager;
import com.elasticpath.cmclient.reporting.ReportType;
import com.elasticpath.cmclient.reporting.giftcertificatesummary.GiftCertificateSummaryMessages;
import com.elasticpath.cmclient.reporting.giftcertificatesummary.GiftCertificateSummaryReportSection;
import com.elasticpath.cmclient.reporting.giftcertificatesummary.parameters.GiftCertificateSummaryParameters;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.service.catalog.GiftCertificateService;

/**
* The service is responsible for building a Gift certificate details prepared JPA query and a
* corresponding parameter list, then sending the query to the server-side service.
*/
public class GiftCertificateSummaryServiceImpl {
	
	private GiftCertificateSummaryParameters parameter;
	
	
	/**
	 * Object array hold following fields:
	 * Date of purchase [Data];
	 * GC number [String];
	 * Recipient [String];
	 * Sender [String];
	 * Currency [String];
	 * Original amount [BigDecimal];
	 * Outstanding amount [BigDecimal].
	 * 
	 * @return List of described arrays to use in reporting layout.
	 */
	public List<Object[]> giftCertificateData() {		
		
		final String currencyCode;
		if (getParameter().getCurrencies() == null) {
			currencyCode = null;
		} else {
			currencyCode = getParameter().getCurrencies()[0];
		}
		
		return getGiftCertificateService().getGiftCertificateSummaryData(
				storeUidPksAsList(getParameter().getStoreUidPkList()),
				currencyCode,
				getParameter().getStartDate(), 
				getParameter().getEndDate());
	}
	
	private List<Long> storeUidPksAsList(final long[] uidPks) {
		if (ArrayUtils.isEmpty(uidPks)) {
			return Collections.emptyList();
		}
		final List<Long> list = new LinkedList<Long>();
		for (long uidPk : uidPks) {
			list.add(uidPk);
		}
		return list;
	}
	
	
	/** 
	 * @return {@link GiftCertificateDetailsParameters}, that used for create JPA query parameters.
	 */
	public GiftCertificateSummaryParameters getParameter() {
		if (parameter == null) {
			loadParameters();
		}
		return parameter;
	}

	private void loadParameters() {
		Map<String, Object> params = getGiftCertificateDetailsReportParameters();
		parameter = (GiftCertificateSummaryParameters) params.get(GiftCertificateSummaryReportSection.PARAMETER_PARAMETERS);
	}


	private GiftCertificateService getGiftCertificateService() {
		return LoginManager.getInstance().getBean(ContextIdNames.GIFT_CERTIFICATE_SERVICE);
	}
	
	/**
	 * @return parameters for the CustomerRegistrationReport
	 */
	Map<String, Object> getGiftCertificateDetailsReportParameters() {
		Map<String, Object> params = new HashMap<String, Object>();
		for (ReportType reportType : ReportTypeManager.getInstance().getReportTypes()) {
			if (reportType.getName().equalsIgnoreCase(
					GiftCertificateSummaryMessages.report)) {
				params = reportType.getReport().getParameters();
			}
		}
		return params;
	}

}
