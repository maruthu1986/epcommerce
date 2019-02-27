package com.elasticpath.selenium.wizards;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Create Return Wizard.
 */
public class CreateReturnWizard extends AbstractWizard {

	private static final String CREATE_RETURN_PARENT_CSS
			= "div[automation-id='com.elasticpath.cmclient.fulfillment.FulfillmentMessages.ReturnWizard_Create_Title'] ";
	private static final String ELECTRONIC_RETURN_PARENT_TABLE_CSS
			= CREATE_RETURN_PARENT_CSS + "div[widget-id='Return Electronic Order Table'][widget-type='Table'] ";
	private static final String ELECTRONIC_RETURN_COLUMN_CSS = ELECTRONIC_RETURN_PARENT_TABLE_CSS + "div[row-id='%s'] div[column-id='0']";
	private static final String ELECTRONIC_RETURN_COLUMN_INPUT_CSS = ELECTRONIC_RETURN_PARENT_TABLE_CSS + " input:not([readonly])";
	private static final String PHYSICAL_RETURN_PARENT_TABLE_CSS
			= CREATE_RETURN_PARENT_CSS + "div[widget-id='Return Subject Page Table'][widget-type='Table'] ";
	private static final String PHYSICAL_RETURN_COLUMN_CSS = PHYSICAL_RETURN_PARENT_TABLE_CSS + "div[row-id='%s'] div[column-id='0']";
	private static final String PHYSICAL_RETURN_COLUMN_INPUT_CSS = PHYSICAL_RETURN_PARENT_TABLE_CSS + " input:not([readonly])";
	//TODO need widget-id
	private static final String REQUIRED_RETURN_CHECKBOX_CSS = CREATE_RETURN_PARENT_CSS + "div[appearance-id='check-box']";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public CreateReturnWizard(final WebDriver driver) {
		super(driver);
		setWizardType("Create Return");
		setWizardStepCss("Step %s of 3 ");
	}

	/**
	 * Enters digital return quantity.
	 *
	 * @param quantity int
	 * @param skuCode  String
	 */
	public void enterDigitalReturnQuantity(final int quantity, final String skuCode) {
		click(getWaitDriver().waitForElementToBeClickable(By.cssSelector(String.format(ELECTRONIC_RETURN_COLUMN_CSS, skuCode))));
		clearAndType(ELECTRONIC_RETURN_COLUMN_INPUT_CSS, String.valueOf(quantity));
	}

	/**
	 * Creates digital return.
	 *
	 * @param quantity int
	 * @param skuCode  String
	 */
	public void createDigitalReturn(final int quantity, final String skuCode) {
		enterDigitalReturnQuantity(quantity, skuCode);
		clickNextInDialog();
		waitForWizardStep("2");
		clickNextInDialog();
		waitForWizardStep("3");
		clickFinish();
		waitTillElementDisappears(By.cssSelector(CREATE_RETURN_PARENT_CSS));
	}

	/**
	 * Enters physical return quantity.
	 *
	 * @param quantity int
	 * @param skuCode  String
	 */
	public void enterPhysicalReturnQuantity(final int quantity, final String skuCode) {
		click(getDriver().findElement(By.cssSelector(String.format(PHYSICAL_RETURN_COLUMN_CSS, skuCode))));
		clearAndType(PHYSICAL_RETURN_COLUMN_INPUT_CSS, String.valueOf(quantity));
	}

	/**
	 * Creates physical return.
	 *
	 * @param quantity int
	 * @param skuCode  String
	 */
	public void createPhysicalReturn(final int quantity, final String skuCode) {
		enterPhysicalReturnQuantity(quantity, skuCode);
		clickNextInDialog();
		//removing as we want physical returned to be required
		//clickRequiredReturnCheckbox();
		clickNextInDialog();
		clickFinish();
		waitTillElementDisappears(By.cssSelector(CREATE_RETURN_PARENT_CSS));
	}

	//TODO need isChecked method for checkBox

	/**
	 * Clicks Physical Return Required Before Refund checkbox.
	 */
	public void clickRequiredReturnCheckbox() {
		click(getWaitDriver().waitForElementToBeClickable(By.cssSelector(REQUIRED_RETURN_CHECKBOX_CSS)));
	}

}