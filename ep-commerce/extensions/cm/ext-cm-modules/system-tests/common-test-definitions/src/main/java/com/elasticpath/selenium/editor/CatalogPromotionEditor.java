package com.elasticpath.selenium.editor;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.common.AbstractPageObject;

/**
 * Catalog Promotion Details pane.
 */
public class CatalogPromotionEditor extends AbstractPageObject {
	/**
	 * Page Object Id CSS.
	 */
	public static final String ACTIVE_EDITOR_PARENT_CSS = "div[active-editor='true'][seeable='true'] ";
	private static final String OVERVIEW_LABEL_CSS = ACTIVE_EDITOR_PARENT_CSS + "div[widget-id='Overview'][appearance-id='label-wrapper']";
	private static final String EXPIRATION_PARENT_CSS = ACTIVE_EDITOR_PARENT_CSS + "div[widget-id='Expiration Date/Time'] ";
	private static final String EXPIRATION_CALENDAR_ICON_CSS = EXPIRATION_PARENT_CSS + "div[style*='.png']";
	private static final String CALENDAR_OK_BUTTON_XPATH = "//div[text() = 'OK']";
	private static final String STATE_INPUT_CSS = ACTIVE_EDITOR_PARENT_CSS
			+ "div[automation-id='com.elasticpath.cmclient.store.promotions.PromotionsMessages.PromoStoreRules_State'] > input";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public CatalogPromotionEditor(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Enters current expiration date/time.
	 */
	public void enterCurrentExpirationDateTime() {
		click(getWaitDriver().waitForElementToBeClickable(By.cssSelector(OVERVIEW_LABEL_CSS)));
		scrollDownWithDownArrowKey(getDriver().findElement(By.cssSelector(ACTIVE_EDITOR_PARENT_CSS)), 1);
		scrollWidgetIntoView(EXPIRATION_PARENT_CSS);
		click(getWaitDriver().waitForElementToBeClickable(By.cssSelector(EXPIRATION_CALENDAR_ICON_CSS)));
		click(getWaitDriver().waitForElementToBeClickable(By.xpath(CALENDAR_OK_BUTTON_XPATH)));
	}

	/**
	 * Verifies promotion state.
	 *
	 * @param state the state.
	 */
	public void verifyPromoState(final String state) {
		assertThat(getDriver().findElement(By.cssSelector(STATE_INPUT_CSS)).getAttribute("value"))
				.as("Catalog promotion state validation failed")
				.isEqualTo(state);
	}

	/**
	 * Enters current expiration date/time.
	 */
	public void disableCartPromotion() {
		click(getDriver().findElement(By.cssSelector(EXPIRATION_CALENDAR_ICON_CSS)));
		click(getWaitDriver().waitForElementToBeClickable(By.xpath(CALENDAR_OK_BUTTON_XPATH)));
	}
}
