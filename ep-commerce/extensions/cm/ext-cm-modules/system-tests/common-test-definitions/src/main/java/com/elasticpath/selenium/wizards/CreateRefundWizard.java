package com.elasticpath.selenium.wizards;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Create Refund Wizard.
 */
public class CreateRefundWizard extends AbstractWizard {

	private static final String CREATE_REFUND_PARENT_CSS = "div[widget-id='Create Refund'][widget-type='Shell'] ";
	private static final String REFUND_AMOUNT_INPUT_CSS = CREATE_REFUND_PARENT_CSS + "div[widget-id='%s'][widget-type='Text'] input";
	private static final String REFUND_NOTE_INPUT_CSS = CREATE_REFUND_PARENT_CSS
			+ "div[automation-id='com.elasticpath.cmclient.fulfillment.FulfillmentMessages.RefundWizard_RefundNote'][widget-type='Text'] textarea";
	private static final String PAYMENT_SOURCE_COMBO_CSS = CREATE_REFUND_PARENT_CSS + "div[widget-id='Payment Source'][widget-type='CCombo']";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public CreateRefundWizard(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Inputs refund amount.
	 *
	 * @param currencyCode the currency code
	 * @param refundAmount the refund amount
	 */
	public void enterRefundAmount(final String currencyCode, final String refundAmount) {
		clearAndType(String.format(REFUND_AMOUNT_INPUT_CSS, currencyCode), refundAmount);

	}

	/**
	 * Inputs refund note.
	 *
	 * @param refundNote the refund note
	 */
	public void enterRefundNote(final String refundNote) {
		clearAndType(REFUND_NOTE_INPUT_CSS, refundNote);
	}

	/**
	 * Selects payment source in combo box.
	 *
	 * @param paymentSource the payment source
	 */
	public void selectPaymentSource(final String paymentSource) {
		assertThat(selectComboBoxItem(PAYMENT_SOURCE_COMBO_CSS, paymentSource))
				.as("Unable to find payment source - " + paymentSource)
				.isTrue();
	}

	/**
	 * Creates refund.
	 *
	 * @param refundMap map of refund item info
	 */
	public void createRefund(final Map<String, String> refundMap) {
		enterRefundAmount(refundMap.get("Currency Code"), refundMap.get("Refund Amount"));
		enterRefundNote(refundMap.get("Refund Note"));
		selectPaymentSource(refundMap.get("Payment Source"));
		clickNextInDialog();
		clickFinish();
		waitTillElementDisappears(By.cssSelector(CREATE_REFUND_PARENT_CSS));
	}


}