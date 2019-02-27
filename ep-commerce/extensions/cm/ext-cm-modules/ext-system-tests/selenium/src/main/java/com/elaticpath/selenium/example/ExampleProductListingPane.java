package com.elaticpath.selenium.example;

import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.resultspane.CatalogProductListingPane;

/**
 * Example catalog product listing pane.
 */
public class ExampleProductListingPane extends CatalogProductListingPane {

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public ExampleProductListingPane(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Closes Product Listing pane.
	 */
	public void closeProductListingPane() {
		closePane("Product Listing");
	}

}
