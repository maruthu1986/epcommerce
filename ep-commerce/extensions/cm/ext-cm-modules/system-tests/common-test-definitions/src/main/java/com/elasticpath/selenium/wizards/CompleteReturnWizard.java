package com.elasticpath.selenium.wizards;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Complete Return Wizard.
 */
public class CompleteReturnWizard extends AbstractWizard {

	private static final String COMPLETE_RETURN_PARENT_CSS = "div[automation-id='com.elasticpath.cmclient.fulfillment.FulfillmentMessages"
			+ ".ReturnWizard_Complete_Title'] ";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public CompleteReturnWizard(final WebDriver driver) {
		super(driver);
		setWizardType("Complete Return");
		setWizardStepCss("Step %s of 2 ");
	}

	/**
	 * Completes return.
	 */
	public void completeReturn() {
		clickNextInDialog();
		waitForWizardStep("2");
		clickFinish();
		waitTillElementDisappears(By.cssSelector(COMPLETE_RETURN_PARENT_CSS));
	}
}