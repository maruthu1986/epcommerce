package com.elasticpath.selenium.wizards;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Payment Authorization Wizard.
 */
public class PaymentAuthorizationWizard extends AbstractWizard {

	private static final String PAYMENT_AUTHORIZATION_PARENT_CSS = "div[widget-id='Payment Authorization'][widget-type='Shell'] ";
	private static final String PAYMENT_SOURCE_COMBO_CSS = PAYMENT_AUTHORIZATION_PARENT_CSS + "div[widget-id='Payment "
			+ "Source'][widget-type='CCombo']";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public PaymentAuthorizationWizard(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Selects payment source in combo box.
	 *
	 * @param paymentSource the payment source
	 */
	public void selectPaymentSource(final String paymentSource) {
		getWaitDriver().waitForElementToBeVisible(By.cssSelector(PAYMENT_SOURCE_COMBO_CSS));
		assertThat(selectComboBoxItem(PAYMENT_SOURCE_COMBO_CSS, paymentSource))
				.as("Unable to find payment source - " + paymentSource)
				.isTrue();
	}

	/**
	 * Completes Payment Authorization.
	 *
	 * @param paymentSource the payment source
	 */
	public void completePaymentAuthorization(final String paymentSource) {
		selectPaymentSource(paymentSource);
		clickNextInDialog();
		clickFinish();
		waitTillElementDisappears(By.cssSelector(PAYMENT_AUTHORIZATION_PARENT_CSS));
	}

}