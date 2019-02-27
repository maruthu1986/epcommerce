/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.reporting.promotiondetails.service.impl;

import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elasticpath.cmclient.core.LoginManager;
import com.elasticpath.cmclient.reporting.ReportType;
import com.elasticpath.cmclient.reporting.ReportTypeManager;
import com.elasticpath.cmclient.reporting.promotiondetails.PromotionDetailsMessages;
import com.elasticpath.cmclient.reporting.promotiondetails.parameters.PromotionDetailsParameters;
import com.elasticpath.service.rules.ReportingRuleService;

/**
 * The service is responsible for building a Promotion Details prepared JPA query and a corresponding parameter list, then sending the query to the
 * server-side service.
 */
public class PromotionDetailsServiceImpl {

	private PromotionDetailsParameters parameter;

	private final ReportingRuleService reportingRuleService = LoginManager.getInstance().getBean("reportingRuleService"); //$NON-NLS-1$

	/**
	 * Object array hold following fields: Promotion Name [String]; Coupon Code [String]; Store [String]; Start Date [Data]; End Date [Data]; Number
	 * of orders [int]; Currency [String]; Total Revenue [BigDecimal]; % of total orders [BigDecimal] (from 0.00 to 1.00).
	 * 
	 * @return List of described arrays to use in reporting layout.
	 */
	public List<Object[]> getData() {
		final PromotionDetailsParameters params = getParameter();


		Currency currency = null;
		if (params.getCurrenyCode() != null) {
			currency = Currency.getInstance(params.getCurrenyCode().trim());
		}
		return reportingRuleService.getPromotionDetailsData(params.getStoreUidPk(), currency, params
				.getStartDate(), params.getEndDate(), params.getPromotionCode(), params.getCouponCode());
	}

	/**
	 * @return {@link GiftCertificateDetailsParameters}, that used for create JPA query parameters.
	 */
	public PromotionDetailsParameters getParameter() {
		if (parameter == null) {
			loadParameters();
		}
		return parameter;
	}

	private void loadParameters() {
		Map<String, Object> params = getPromotionDetailsReportParameters();
		parameter = (PromotionDetailsParameters) params.get(PromotionDetailsParameters.DETAILS_PARAMETERS);
	}

	/**
	 * @return parameters for the CustomerRegistrationReport
	 */
	private Map<String, Object> getPromotionDetailsReportParameters() {
		Map<String, Object> params = new HashMap<String, Object>();
		for (ReportType reportType : ReportTypeManager.getInstance().getReportTypes()) {
			if (reportType.getName().equalsIgnoreCase(PromotionDetailsMessages.report)) {
				params = reportType.getReport().getParameters();
			}
		}
		return params;
	}
}
