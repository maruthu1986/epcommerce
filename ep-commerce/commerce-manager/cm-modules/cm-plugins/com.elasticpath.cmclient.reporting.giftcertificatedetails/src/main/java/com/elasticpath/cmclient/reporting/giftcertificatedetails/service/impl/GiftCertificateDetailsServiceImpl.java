/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.reporting.giftcertificatedetails.service.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elasticpath.cmclient.core.LoginManager;
import com.elasticpath.cmclient.reporting.ReportType;
import com.elasticpath.cmclient.reporting.giftcertificatedetails.GiftCertificateDetailsMessages;
import com.elasticpath.cmclient.reporting.giftcertificatedetails.GiftCertificateDetailsReportSection;
import com.elasticpath.cmclient.reporting.giftcertificatedetails.parameters.GiftCertificateDetailsParameters;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.service.catalog.GiftCertificateService;

/**
* The service is responsible for building a Gift certificate details prepared JPA query and a
* corresponding parameter list, then sending the query to the server-side service.
*/
public class GiftCertificateDetailsServiceImpl {
	
	private GiftCertificateDetailsParameters parameter;
	
	
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
		
		return getGiftCertificateService().getGiftCertificateDetailData(
				getParameter().getStoreUidPk(), 
				getParameter().getStartDate(), 
				getParameter().getEndDate(), 
				Arrays.asList(getParameter().getCurrencies()));

	}
	
	
	/** 
	 * @return {@link GiftCertificateDetailsParameters}, that used for create JPA query parameters.
	 */
	public GiftCertificateDetailsParameters getParameter() {
		if (parameter == null) {
			loadParameters();
		}
		return parameter;
	}

	private void loadParameters() {
		Map<String, Object> params = getGiftCertificateDetailsReportParameters();
		parameter = new GiftCertificateDetailsParameters();
		parameter.setStoreUidPk((Long) params.get(GiftCertificateDetailsReportSection.PARAMETER_STORE_UIDPK));
		parameter.setStoreName((String) params.get(GiftCertificateDetailsReportSection.PARAMETER_STORE));
		parameter.setStartDate((Date) params.get(GiftCertificateDetailsReportSection.PARAMETER_START_DATE));
		parameter.setEndDate((Date) params.get(GiftCertificateDetailsReportSection.PARAMETER_END_DATE));
		parameter.setCurrencies((String[]) params.get(GiftCertificateDetailsReportSection.PARAMETER_CURRENCIES));
	}


	/** 
	 * @param parameter {@link GiftCertificateDetailsParameters}, that used for create JPA query parameters.
	 */
	public void setParameter(final GiftCertificateDetailsParameters parameter) {
		this.parameter = parameter;
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
					GiftCertificateDetailsMessages.report)) {
				params = reportType.getReport().getParameters();
			}
		}
		return params;
	}

}
