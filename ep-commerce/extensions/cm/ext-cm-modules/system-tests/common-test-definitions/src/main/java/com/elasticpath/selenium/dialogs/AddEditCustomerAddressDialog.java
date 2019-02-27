package com.elasticpath.selenium.dialogs;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Add Customer Address Dialog.
 */
public class AddEditCustomerAddressDialog extends AbstractDialog {
	/**
	 * CSS selector used to identify the dialog.
	 */
	public static final String CUSTOMER_ADDRESS_PARENT_CSS = "div[automation-id*='com.elasticpath.cmclient.fulfillment.FulfillmentMessages"
			+ ".AddressDialog_'][widget-type='Shell'] ";
	private static final String CUSTOMER_ADDRESS_FIRST_NAME_CSS = CUSTOMER_ADDRESS_PARENT_CSS + "div[widget-id='First Name'] input";
	private static final String CUSTOMER_ADDRESS_LAST_NAME_CSS = CUSTOMER_ADDRESS_PARENT_CSS + "div[widget-id='Last Name'] input";
	private static final String CUSTOMER_ADDRESS_ADDRESS_LINE_1_CSS = CUSTOMER_ADDRESS_PARENT_CSS + "div[widget-id='Address Line1'] input";
	private static final String CUSTOMER_ADDRESS_CITY_CSS = CUSTOMER_ADDRESS_PARENT_CSS + "div[widget-id='City'] input";
	private static final String CUSTOMER_ADDRESS_STATE_COMBO_CSS = CUSTOMER_ADDRESS_PARENT_CSS
			+ "div[widget-id='State/Province/Region'][widget-type='CCombo']";
	private static final String CUSTOMER_ADDRESS_ZIP_CSS = CUSTOMER_ADDRESS_PARENT_CSS + "div[widget-id='Zip/Postal Code'] input";
	private static final String CUSTOMER_ADDRESS_COUNTRY_COMBO_CSS = CUSTOMER_ADDRESS_PARENT_CSS + "div[widget-id='Country'][widget-type='CCombo']";
	private static final String CUSTOMER_ADDRESS_PHONE_CSS = CUSTOMER_ADDRESS_PARENT_CSS + "div[widget-id='Phone Number'] input";
	private static final String SAVE_BUTTON_CSS = CUSTOMER_ADDRESS_PARENT_CSS
			+ "div[automation-id='com.elasticpath.cmclient.core.CoreMessages.AbstractEpDialog_ButtonSave']";


	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public AddEditCustomerAddressDialog(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Clicks save.
	 */
	public void clickSave() {
		clickButton(SAVE_BUTTON_CSS, "Save");
		waitTillElementDisappears(By.cssSelector(CUSTOMER_ADDRESS_PARENT_CSS));
	}

	/**
	 * Enter customer address first name.
	 *
	 * @param firstName String
	 */
	public void enterFirstName(final String firstName) {
		clearAndType(CUSTOMER_ADDRESS_FIRST_NAME_CSS, firstName);
	}

	/**
	 * Enter customer address last name.
	 *
	 * @param lastName String
	 */
	public void enterLastName(final String lastName) {
		clearAndType(CUSTOMER_ADDRESS_LAST_NAME_CSS, lastName);
	}

	/**
	 * Enter customer address line 1.
	 *
	 * @param addressLine1 String
	 */
	public void enterAddressLine1(final String addressLine1) {
		clearAndType(CUSTOMER_ADDRESS_ADDRESS_LINE_1_CSS, addressLine1);
	}

	/**
	 * Enter customer address city.
	 *
	 * @param city String
	 */
	public void enterCity(final String city) {
		clearAndType(CUSTOMER_ADDRESS_CITY_CSS, city);
	}

	/**
	 * Enter customer address zip.
	 *
	 * @param zip String
	 */
	public void enterZip(final String zip) {
		clearAndType(CUSTOMER_ADDRESS_ZIP_CSS, zip);
	}

	/**
	 * Enter customer address phone number.
	 *
	 * @param phone String
	 */
	public void enterPhone(final String phone) {
		clearAndType(CUSTOMER_ADDRESS_PHONE_CSS, phone);
	}

	/**
	 * Selects state in combo box.
	 *
	 * @param state String
	 */
	public void selectState(final String state) {
		assertThat(selectComboBoxItem(CUSTOMER_ADDRESS_STATE_COMBO_CSS, state))
				.as("Unable to find state - " + state)
				.isTrue();
	}

	/**
	 * Selects country in combo box.
	 *
	 * @param country String
	 */
	public void selectCountry(final String country) {
		assertThat(selectComboBoxItem(CUSTOMER_ADDRESS_COUNTRY_COMBO_CSS, country))
				.as("Unable to find country - " + country)
				.isTrue();
	}
}