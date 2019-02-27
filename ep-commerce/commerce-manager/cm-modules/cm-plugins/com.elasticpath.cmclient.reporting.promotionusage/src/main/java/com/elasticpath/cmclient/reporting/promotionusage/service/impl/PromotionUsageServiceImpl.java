/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.reporting.promotionusage.service.impl;

import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.helpers.extenders.PluginHelper;
import com.elasticpath.cmclient.reporting.ReportType;
import com.elasticpath.cmclient.reporting.ReportTypeManager;
import com.elasticpath.cmclient.reporting.ReportingPlugin;
import com.elasticpath.cmclient.reporting.promotionusage.PromotionUsageMessages;
import com.elasticpath.cmclient.reporting.promotionusage.PromotionUsageReportPlugin;
import com.elasticpath.cmclient.reporting.promotionusage.PromotionUsageReportSection;
import com.elasticpath.cmclient.reporting.promotionusage.parameters.PromotionUsageParameters;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.service.rules.RuleService;

/**
* The service is responsible for building a Promotion Usage prepared JPA query and a
* corresponding parameter list, then sending the query to the server-side service.
*/
public class PromotionUsageServiceImpl {

	private static final Logger LOG = Logger.getLogger(PromotionUsageServiceImpl.class.getName());

	private static final int CURRENCY_IDX = 0;
	private static final int RULENAME_IDX = 3;
	
	private PromotionUsageParameters parameter;
	private static final String ATTR_NAME = "name";
	private static final String ATTR_TYPE_REPORT = "reports";

	/**
	 * Object array holding the following fields:
	 *   Currency [String];
	 *   Store [String];
	 *   Rule UidPk [long]
	 *   Promotion Name [String];
	 *   Coupon Usage Type [String];
	 *   Start Date [Data];
	 *   End Date [Data];
	 *   Rule Set Scenario [int] - 1 = Cart, 2 = Catalog
	 *   Total Revenue [BigDecimal];
	 *   Number of orders [BigDecimal];
	 *   Percent of total orders [BigDecimal] (from 0.00 to 1.00).
	 * 
	 * @return List of described arrays to use in reporting layout.
	 */
	public List<Object[]> getData() {

		final PromotionUsageParameters params = getParameter();
		final Currency currency = null; // all currencies

		try {
			final List<Object[]> queryResults = getRuleService().getPromotionUsageData(
					storeUidPkAsList(params.getStoreUidPk()), currency,
					params.getStartDate(), params.getEndDate(), params.isOnlyPromotionsWithCouponCodes());

			// Replace Currency object with string code
			queryResults.forEach(rawObject -> {
				if (rawObject[CURRENCY_IDX] instanceof Currency) {
					rawObject[CURRENCY_IDX] = ((Currency) rawObject[CURRENCY_IDX]).getCurrencyCode();
				} else {
					rawObject[CURRENCY_IDX] = PromotionUsageMessages.get().noCurrency;
				}
			});

			// Sort by currency and rule name
			Collections.sort(queryResults, (rawObject1, rawObject2) -> {
				int comparison = ((String) rawObject1[CURRENCY_IDX]).compareTo((String) rawObject2[CURRENCY_IDX]);
				if (comparison == 0) {
					comparison = ((String) rawObject1[RULENAME_IDX]).compareToIgnoreCase((String) rawObject2[RULENAME_IDX]);
				}
				return comparison;
			});

			return queryResults;

		} catch (Exception ex) {
			LOG.error("Error executing report queries", ex);
			throw ex;
		}
	}

	private List<Long> storeUidPkAsList(final long uidPk) {
		if (uidPk == 0) {
			return Collections.emptyList();
		}
		final List<Long> list = new LinkedList<>();
		list.add(uidPk);
		return list;
	}
	
	private RuleService getRuleService() {
		return ServiceLocator.getService(ContextIdNames.RULE_SERVICE);
	}
	
	
	/** 
	 * @return {@link PromotionUsageParameters}, that used for create JPA query parameters.
	 */
	public PromotionUsageParameters getParameter() {
		if (parameter == null) {
			loadParameters();
		}
		return parameter;
	}

	private void loadParameters() {
		Map<String, Object> params = getGiftCertificateDetailsReportParameters();
		parameter = (PromotionUsageParameters) params.get(PromotionUsageReportSection.PARAMETER_PARAMETERS);
	}
	
	/**
	 * @return parameters for the CustomerRegistrationReport
	 */
	Map<String, Object> getGiftCertificateDetailsReportParameters() {
		Map<String, Object> params = new HashMap<>();
		for (ReportType reportType : ReportTypeManager.getInstance().getReportTypes()) {
			if (reportType.getName().equalsIgnoreCase(
					PluginHelper.getPluginAttribute(ReportingPlugin.PLUGIN_ID,  PromotionUsageReportPlugin.PLUGIN_ID, ATTR_TYPE_REPORT, ATTR_NAME))) {
				params = reportType.getReport().getParameters();
			}
		}
		return params;
	}


}
