package com.elasticpath.selenium.editor;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.common.AbstractPageObject;
import com.elasticpath.selenium.util.Constants;

/**
 * Inventory editor.
 */
public class InventoryEditor extends AbstractPageObject {
	private static final Logger LOGGER = Logger.getLogger(InventoryEditor.class);
	private static final String ACTIVE_EDITOR_PANE = "div[pane-location='editor-pane'] div[active-editor='true'] ";
	private static final String ADJUSTMENT_COMBO_CSS = ACTIVE_EDITOR_PANE + "div[widget-id='Adjustment'][widget-type='CCombo']";
	private static final String QUANTITY_INPUT_CSS = "div[widget-id='Quantity'] input";
	private static final String QUANTITY_ON_HAND_CSS =
			"div[automation-id='com.elasticpath.cmclient.warehouse.WarehouseMessages.Inventory_QuantityOnHand'] + div > div";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public InventoryEditor(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Select adjustment.
	 *
	 * @param adjustmentType the adjustment type.
	 */
	public void selectAdjustment(final String adjustmentType) {
		sleep(Constants.SLEEP_HALFSECOND_IN_MILLIS);
		assertThat(selectComboBoxItem(ADJUSTMENT_COMBO_CSS, adjustmentType))
				.as("Unable to find adjustment type - " + adjustmentType)
				.isTrue();
		sleep(Constants.SLEEP_HALFSECOND_IN_MILLIS);
	}

	/**
	 * Enters quantity.
	 *
	 * @param quantity String
	 */
	public void enterQuantity(final String quantity) {
		clearAndType(QUANTITY_INPUT_CSS, quantity);
	}

	/**
	 * Returns quantity on hand.
	 *
	 * @return the quantity on hand.
	 */
	public int getQuantityOnHand() {
		getWaitDriver().waitForElementToBeNotStale(QUANTITY_ON_HAND_CSS);
		return Integer.parseInt(getWaitDriver().waitForElementToBeVisible(By.cssSelector(QUANTITY_ON_HAND_CSS)).getText());
	}

	/**
	 * Verifies quantity update.
	 *
	 * @param updatedQuantity the updated quantity
	 */
	public void verifyQuantityUpdate(final int updatedQuantity) {
		getWaitDriver().waitForElementToBeNotStale(QUANTITY_ON_HAND_CSS);
		LOGGER.info("---------- waitForTextInElement - expected value: " + updatedQuantity);
		LOGGER.info("---------- waitForTextInElement - actual value  : "
				+ getDriver().findElement(By.cssSelector(QUANTITY_ON_HAND_CSS)).getText());
		getWaitDriver().waitForTextInElement(QUANTITY_ON_HAND_CSS, String.valueOf(updatedQuantity));
	}

	/**
	 * Verifies quantity on hand.
	 *
	 * @param expectedQuantityOnHand String
	 */
	public void verifyQuantityOnHand(final int expectedQuantityOnHand) {
		assertThat(getQuantityOnHand())
				.as("Quantity on hand validation failed")
				.isEqualTo(expectedQuantityOnHand);
	}

}
