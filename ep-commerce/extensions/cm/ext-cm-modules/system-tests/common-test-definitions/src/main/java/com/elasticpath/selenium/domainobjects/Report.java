package com.elasticpath.selenium.domainobjects;


import java.util.ArrayList;
import java.util.List;

/**
 * Report options.
 */
public class Report {

	private String reportType;
	private String store;
	private String currency;
	private String orderStatus;
	private List<String> orderStatusList;


	public String getReportType() {
		return reportType;
	}

	public void setReportType(final String reportType) {
		this.reportType = reportType;
	}

	public String getStore() {
		return store;
	}

	public void setStore(final String store) {
		this.store = store;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(final String currency) {
		this.currency = currency;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(final String orderStatus) {
		this.orderStatus = orderStatus;
	}

	/**
	 * Returns list of order status.
	 *
	 * @return orderStatusList the order status list
	 */
	public List<String> getOrderStatusList() {
		orderStatusList = new ArrayList<>();
		if (orderStatus != null) {
			String[] orderStatusArray = orderStatus.split(",");
			for (String orderStatus : orderStatusArray) {
				orderStatusList.add(orderStatus.trim());
			}
		}
		return orderStatusList;
	}
}
