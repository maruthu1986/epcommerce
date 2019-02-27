package com.elasticpath.selenium.dialogs;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Complete Shipment Dialog.
 */
public class CompleteShipmentDialog extends AbstractDialog {

	/**
	 * CSS selector used to identify the dialog.
	 */
	public static final String COMPLETE_SHIPMENT_PARENT_CSS = "div[widget-id='Complete Shipment'][widget-type='Shell'] ";
	private static final String SHIPMENT_ID_INPUT_CSS = COMPLETE_SHIPMENT_PARENT_CSS + "div[widget-id='Shipment ID'] input";
	private static final String VALIDATE_BUTTON_CSS = COMPLETE_SHIPMENT_PARENT_CSS + "div[widget-id='Validate'][widget-type='Button']";
	private static final String COMPLETE_BUTTON_CSS = COMPLETE_SHIPMENT_PARENT_CSS + "div[widget-id='Complete'][widget-type='Button']";


	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public CompleteShipmentDialog(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Inputs shipment id.
	 *
	 * @param shipmentId the code.
	 */
	public void enterShipmentId(final String shipmentId) {
		clearAndType(SHIPMENT_ID_INPUT_CSS, shipmentId);
	}

	/**
	 * Clicks validate button.
	 */
	public void clickValidateButton() {
		clickButton(VALIDATE_BUTTON_CSS, "Validate");
	}

	/**
	 * Clicks complete button.
	 */
	public void clickCompleteButton() {
		clickButton(COMPLETE_BUTTON_CSS, "Complete");
		waitTillElementDisappears(By.cssSelector(COMPLETE_SHIPMENT_PARENT_CSS));
	}

	/**
	 * Completes shipment.
	 *
	 * @param shipmentId the code
	 */
	public void completeShipment(final String shipmentId) {
		enterShipmentId(shipmentId);
		clickValidateButton();
		clickCompleteButton();
	}

}
