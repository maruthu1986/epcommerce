package com.elaticpath.selenium.example;

import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.navigations.CatalogManagement;
import com.elasticpath.selenium.resultspane.CatalogProductListingPane;

/**
 * Example catalog management.
 */
public class ExampleCatalogManagement extends CatalogManagement {

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public ExampleCatalogManagement(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Double click category.
	 *
	 * @param categoryName the category name.
	 * @return the pane.
	 */
	public CatalogProductListingPane doubleClickTheCategory(final String categoryName) {
		return doubleClickCategory(categoryName);
	}

}
