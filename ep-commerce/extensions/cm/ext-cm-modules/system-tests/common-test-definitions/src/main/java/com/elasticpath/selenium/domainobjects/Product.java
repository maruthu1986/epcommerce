package com.elasticpath.selenium.domainobjects;

import java.util.ArrayList;
import java.util.List;

/**
 * Product class.
 */
@SuppressWarnings("PMD.TooManyFields")
public class Product {

	private String catalog;
	private String category;
	private String productName;
	private String productCode;
	private String productType;
	private String taxCode;
	private String brand;
	private String storeVisible;
	private String availability;
	private String attrShortTextMulti;
	private String attrInteger;
	private String attrDecimal;
	private String attrShortTextMultiValue;
	private String attrIntegerValue;
	private String attrDecimalValue;
	private String shippableType;
	private String priceList;
	private String listPrice;
	private String skuCodeList;
	private String skuOption;
	private String bundlePricing;
	private String bundleProductSKUList;
	private String bundleSelectionRule;
	private String bundleSelectionRuleValue;

	public String getBundleSelectionRule() {
		return bundleSelectionRule;
	}

	public void setBundleSelectionRule(final String bundleSelectionRule) {
		this.bundleSelectionRule = bundleSelectionRule;
	}

	public String getBundleSelectionRuleValue() {
		return bundleSelectionRuleValue;
	}

	public void setBundleSelectionRuleValue(final String bundleSelectionRuleValue) {
		this.bundleSelectionRuleValue = bundleSelectionRuleValue;
	}

	public String getBundleProductSKUCodes() {
		return bundleProductSKUList;
	}

	public void setBundleProductSKUCodes(final String bundleProductSKUList) {
		this.bundleProductSKUList = bundleProductSKUList;
	}

	/**
	 * Gets Bundle Product Codes List.
	 *
	 * @return productSkuCodeList.
	 */
	public List<String> getBundleProductSKUList() {
		List<String> productSkuCodeList = new ArrayList<>();
		if (bundleProductSKUList == null) {
			return null;
		} else {
			String[] skuCodes = bundleProductSKUList.split(",");
			for (String product : skuCodes) {
				productSkuCodeList.add(product);
			}
			return productSkuCodeList;
		}
	}

	public String getBundlePricing() {
		return bundlePricing;
	}

	public void setBundlePricing(final String bundlePricing) {
		this.bundlePricing = bundlePricing;
	}


	public String getSkuCode() {
		return skuCode;
	}

	public void setSkuCode(final String skuCode) {
		this.skuCode = skuCode;
	}

	private String skuCode;

	public String getProductSKUCodes() {
		return skuCodeList;
	}

	public void setProductSKUCodes(final String codesList) {
		this.skuCodeList = codesList;
	}

	/**
	 * Gets SKU Codes List.
	 *
	 * @return productSkuCodeList.
	 */
	public List<String> getSKUCodeList() {
		List<String> productSkuCodeList = new ArrayList<>();
		if (skuCodeList == null) {
			return null;
		} else {
			String[] skuCodes = skuCodeList.split(",");
			for (String product : skuCodes) {
				productSkuCodeList.add(product);
			}
			return productSkuCodeList;
		}
	}

	public String getStoreVisible() {
		return storeVisible;
	}

	public void setStoreVisible(final String storeVisible) {
		this.storeVisible = storeVisible;
	}

	public String getAttrShortTextMulti() {
		return attrShortTextMulti;
	}

	public void setAttrShortTextMulti(final String attrShortTextMulti) {
		this.attrShortTextMulti = attrShortTextMulti;
	}

	public String getAttrInteger() {
		return attrInteger;
	}

	public void setAttrInteger(final String attrInteger) {
		this.attrInteger = attrInteger;
	}

	public String getAttrDecimal() {
		return attrDecimal;
	}

	public void setAttrDecimal(final String attrDecimal) {
		this.attrDecimal = attrDecimal;
	}

	public String getCatalog() {
		return catalog;
	}

	public void setCatalog(final String catalog) {
		this.catalog = catalog;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(final String category) {
		this.category = category;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(final String productName) {
		this.productName = productName;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(final String productType) {
		this.productType = productType;
	}

	public String getTaxCode() {
		return taxCode;
	}

	public void setTaxCode(final String taxCode) {
		this.taxCode = taxCode;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(final String brand) {
		this.brand = brand;
	}

	public String getAvailability() {
		return availability;
	}

	public void setAvailability(final String availability) {
		this.availability = availability;
	}

	public String getAttrShortTextMultiValue() {
		return attrShortTextMultiValue;
	}

	public void setAttrShortTextMultiValue(final String attrShortTextMultiValue) {
		this.attrShortTextMultiValue = attrShortTextMultiValue;
	}

	public String getAttrIntegerValue() {
		return attrIntegerValue;
	}

	public void setAttrIntegerValue(final String attrIntegerValue) {
		this.attrIntegerValue = attrIntegerValue;
	}

	public String getAttrDecimalValue() {
		return attrDecimalValue;
	}

	public void setAttrDecimalValue(final String attrDecimalValue) {
		this.attrDecimalValue = attrDecimalValue;
	}

	public String getShippableType() {
		return shippableType;
	}

	public void setShippableType(final String shippableType) {
		this.shippableType = shippableType;
	}

	public String getPriceList() {
		return priceList;
	}

	public void setPriceList(final String priceList) {
		this.priceList = priceList;
	}

	public String getListPrice() {
		return listPrice;
	}

	public void setListPrice(final String listPrice) {
		this.listPrice = listPrice;
	}

	public String getSKUOption() {
		return skuOption;
	}

	public void setSKUOption(final String skuOption) {
		this.skuOption = skuOption;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(final String productCode) {
		this.productCode = productCode;
	}
}
