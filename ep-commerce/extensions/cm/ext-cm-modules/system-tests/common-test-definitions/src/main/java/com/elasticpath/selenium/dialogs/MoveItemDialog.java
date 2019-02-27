package com.elasticpath.selenium.dialogs;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Move Item Dialog.
 */
public class MoveItemDialog extends AbstractDialog {

	/**
	 * CSS selector used to identify the dialog.
	 */
	public static final String MOVE_ITEM_PARENT_CSS = "div[widget-id='Move Item'][widget-type='Shell'] ";
	private static final String ADDRESS_COMBO_CSS = MOVE_ITEM_PARENT_CSS + "div[widget-id='Address'][widget-type='CCombo']";
	private static final String SHIPMENT_METHOD_COMBO_CSS = MOVE_ITEM_PARENT_CSS + "div[widget-id='Shipment Method'][widget-type='CCombo']";


	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public MoveItemDialog(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Selects shipping address.
	 *
	 * @param shippingAddress the shipping address
	 */
	public void selectAddress(final String shippingAddress) {
		getWaitDriver().waitForElementToBeVisible(By.cssSelector(ADDRESS_COMBO_CSS));
		assertThat(selectComboBoxItem(ADDRESS_COMBO_CSS, shippingAddress))
				.as("Unable to find shipping address - " + shippingAddress)
				.isTrue();
	}

	/**
	 * Selects shipment method.
	 *
	 * @param shipmentMethod the shipment method
	 */
	public void selectShipmentMethod(final String shipmentMethod) {
		assertThat(selectComboBoxItem(SHIPMENT_METHOD_COMBO_CSS, shipmentMethod))
				.as("Unable to find shipment method - " + shipmentMethod)
				.isTrue();
	}

	/**
	 * Moves item to new shipment.
	 *
	 * @param shippingAddress the shipping address
	 * @param shipmentMethod  the shipment method
	 */
	public void moveItem(final String shippingAddress, final String shipmentMethod) {
		selectAddress(shippingAddress);
		selectShipmentMethod(shipmentMethod);
		clickOK();
		waitTillElementDisappears(By.cssSelector(MOVE_ITEM_PARENT_CSS));
	}

}
