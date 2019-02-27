package com.elasticpath.selenium.domainobjects;

/**
 * Shipping Service Level class.
 */
public class ShippingServiceLevel {
	private String shippingServiceLevelCode;
	private String store;
	private String shippingRegion;
	private String carrier;
	private String name;
	private String propertyValue;

	public String getShippingServiceLevelCode() {
		return shippingServiceLevelCode;
	}

	public void setShippingServiceLevelCode(final String shippingServiceLevelCode) {
		this.shippingServiceLevelCode = shippingServiceLevelCode;
	}

	public String getStore() {
		return store;
	}

	public void setStore(final String store) {
		this.store = store;
	}

	public String getShippingRegion() {
		return shippingRegion;
	}

	public void setShippingRegion(final String shippingRegion) {
		this.shippingRegion = shippingRegion;
	}

	public String getCarrier() {
		return carrier;
	}

	public void setCarrier(final String carrier) {
		this.carrier = carrier;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getPropertyValue() {
		return propertyValue;
	}

	public void setPropertyValue(final String propertyValue) {
		this.propertyValue = propertyValue;
	}
}
