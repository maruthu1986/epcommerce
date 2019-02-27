package com.elasticpath.selenium.toolbars;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.wizards.CreateCartPromotionWizard;
import com.elasticpath.selenium.wizards.CreateCatalogPromotionWizard;

/**
 * Promotions Shipping Toolbar.
 */
public class PromotionsShippingActionToolbar extends AbstractToolbar {

	private static final String APPEARANCE_ID_CSS = "div[appearance-id='toolbar-button']";

	private static final String CREATE_CATALOG_PROMOTION_BUTTON_CSS = APPEARANCE_ID_CSS
			+ "[widget-id='Create Catalog Promotion']";
	private static final String CREATE_CART_PROMOTION_BUTTON_CSS = APPEARANCE_ID_CSS
			+ "[widget-id='Create Shopping Cart Promotion']";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public PromotionsShippingActionToolbar(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Clicks Create Catalog Promotion icon.
	 *
	 * @return CreateCatalogPromotionWizard
	 */
	public CreateCatalogPromotionWizard clickCreateCatalogPromotionButton() {
		clickButton(CREATE_CATALOG_PROMOTION_BUTTON_CSS, "Create Catalog Promotion");
		return new CreateCatalogPromotionWizard(getDriver());
	}

	/**
	 * Clicks Create Cart Promotion icon.
	 *
	 * @return CreateCartPromotionWizard
	 */
	public CreateCartPromotionWizard clickCreateCartPromotionButton() {
		clickButton(CREATE_CART_PROMOTION_BUTTON_CSS, "Create Shopping Cart Promotion");
		return new CreateCartPromotionWizard(getDriver());
	}

	/**
	 * Verifies Create Catalog Promotion button is present.
	 */
	public void verifyCreateCatalogPromotionButtonIsPresent() {
		getWaitDriver().waitForElementToBeInteractable(CREATE_CATALOG_PROMOTION_BUTTON_CSS);
		assertThat(isElementPresent(By.cssSelector(CREATE_CATALOG_PROMOTION_BUTTON_CSS)))
				.as("Unable to find create catalog promotion button")
				.isTrue();
	}

}
