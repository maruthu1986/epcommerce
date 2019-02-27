package com.elasticpath.selenium.editor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.common.AbstractPageObject;
import com.elasticpath.selenium.dialogs.AddEditCustomerAddressDialog;
import com.elasticpath.selenium.dialogs.ConfirmDialog;
import com.elasticpath.selenium.dialogs.EditItemDetailsDialog;
import com.elasticpath.selenium.dialogs.MoveItemDialog;
import com.elasticpath.selenium.dialogs.SelectASkuDialog;
import com.elasticpath.selenium.util.Constants;
import com.elasticpath.selenium.wizards.CompleteReturnWizard;
import com.elasticpath.selenium.wizards.CreateExchangeWizard;
import com.elasticpath.selenium.wizards.CreateRefundWizard;
import com.elasticpath.selenium.wizards.CreateReturnWizard;


/**
 * Order Editor.
 */
@SuppressWarnings({"PMD.TooManyMethods"})
public class OrderEditor extends AbstractPageObject {

	//TODO: the pane-location disappears if re-login to an idled CM. Re-check again when PB-3174 fixed.
	/**
	 * Page Object Id.
	 */
	public static final String EDITOR_PANE_PARENT_CSS = "div[pane-location='editor-pane'] div[active-editor='true'] ";
	private static final String CANCEL_ORDER_BUTTON_CSS = EDITOR_PANE_PARENT_CSS + "div[widget-id='Cancel Order'][seeable='true']";
	private static final String EDITOR_BUTTON_CSS = EDITOR_PANE_PARENT_CSS + "div[widget-id='%s'][seeable='true']";
	private static final String CREATE_REFUND_BUTTON_CSS = EDITOR_PANE_PARENT_CSS + "div[widget-id='Create Refund'][seeable='true']";
	private static final String ORDER_STATUS_INPUT_CSS = EDITOR_PANE_PARENT_CSS + "div[widget-id='Order Status'] > input";
	private static final String SHIPMENT_STATUS_INPUT_CSS = EDITOR_PANE_PARENT_CSS + "div[widget-id='Status'] > input";
	private static final String TAB_CSS = "div[widget-id='%s'][seeable='true']";
	private static final String PAYMENT_TABLE_PARENT_CSS = EDITOR_PANE_PARENT_CSS + "div[widget-id='Order Payments History Table'] ";
	private static final String COLUMN_ID_CSS = "div[column-id='%s']";
	private static final String PAYMENT_COLUMN_CSS = PAYMENT_TABLE_PARENT_CSS + COLUMN_ID_CSS;
	private static final String SKU_TABLE_PARENT_CSS = EDITOR_PANE_PARENT_CSS + "div[widget-id='Sku Table'] ";
	private static final String SKU_COLUMN_CSS = SKU_TABLE_PARENT_CSS + COLUMN_ID_CSS;
	private static final String EXCHANGE_ORDER_NUMBER_INPUT_CSS = EDITOR_PANE_PARENT_CSS + "div[widget-id='Exchange Order #'] > input";
	private static final String ORDER_NUMBER_INPUT_CSS = EDITOR_PANE_PARENT_CSS + "div[widget-id='Order #'] > input";
	private static final String EXTERNAL_ORDER_NUMBER_INPUT_CSS = EDITOR_PANE_PARENT_CSS + "div[widget-id='External Order #'] > input";
	private static final String ORDER_DETAIL_SHIPMENT_TABLE_PARENT_CSS = EDITOR_PANE_PARENT_CSS + "div[widget-id*='Shipment Table']"
			+ "[widget-type='Table'][seeable='true'] ";
	private static final String ORDER_DETAIL_SHIPMENT_TABLE_COLUMN_CSS = ORDER_DETAIL_SHIPMENT_TABLE_PARENT_CSS + "div[column-id='%s']";
	private static final String ATTRIBUTE_VALUE = "value";
	private static final String EMAIL_ADDRESS_CSS = EDITOR_PANE_PARENT_CSS + "div[widget-type='Hyperlink'][seeable='true']";
	private static final String FULFILLMENT_MESSAGE = "div[automation-id='com.elasticpath.cmclient.fulfillment.FulfillmentMessages";
	private static final String EDITOR_TITLE_LABEL = "[appearance-id='label-wrapper']";
	private static final String ORDER_SUMMARY_TITLE = FULFILLMENT_MESSAGE + ".OrderSummaryPage_Form_Title'][seeable='true']"
			+ EDITOR_TITLE_LABEL;
	private static final String ORDER_DETAILS_TITLE = FULFILLMENT_MESSAGE + ".OrderDetailPage_Form_Title'][seeable='true']"
			+ EDITOR_TITLE_LABEL;
	private static final String ORDER_PAYMENTS_TITLE = FULFILLMENT_MESSAGE + ".OrderPaymentsPage_Form_Title'][seeable='true']"
			+ EDITOR_TITLE_LABEL;
	private static final String RETURNS_AND_EXCHANGES_TITLE = FULFILLMENT_MESSAGE + ".OrderReturnsPage_Title'][seeable='true']"
			+ EDITOR_TITLE_LABEL;
	private static final String ITEM_DETAIL_BUTTON_CSS = "div[automation-id='com.elasticpath.cmclient.fulfillment.FulfillmentMessages"
			+ ".ShipmentSection_EditItemAttributesButton'][seeable='true']";
	private static final String RETURNED_SKU_TABLE_PARENT_CSS = EDITOR_PANE_PARENT_CSS + "div[widget-id='Sku Table'] ";
	private static final String RETURNED_SKU_COLUMN_CSS = RETURNED_SKU_TABLE_PARENT_CSS + COLUMN_ID_CSS;
	private static final int SLEEP_TIME = 500;
	private static final String SKU_CODE_COLUMN_NAME = "SKU Code";
	private static final String UNLOCK_ORDER_CSS = "div[automation-id='com.elasticpath.cmclient.fulfillment.FulfillmentMessages"
			+ ".OrderActionUnlockOrder'][seeable='true']";
	private static final String SHIPMENT_DISCOUNT_VALUE_CSS = "div[widget-id='Less Shipment Discount:'][widget-type='Text'] "
			+ "+ div[widget-type='Text'] > input";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public OrderEditor(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Clicks Cancel Order button.
	 *
	 * @return the confirm dialog
	 */
	public ConfirmDialog clickCancelOrderButton() {
		clickButton(CANCEL_ORDER_BUTTON_CSS, "Cancel");
		return new ConfirmDialog(getDriver());
	}

	/**
	 * Clicks the complete return button.
	 *
	 * @return the complete return wizard
	 */
	public CompleteReturnWizard clickCompleteReturnButton() {
		clickEditorButton("Complete Return...");
		return new CompleteReturnWizard(getDriver());
	}

	/**
	 * Checks if Editor button in view port.
	 *
	 * @param buttonWidgetId String the widget id
	 * @return boolean
	 */
	public boolean isEditorButtonInViewport(final String buttonWidgetId) {
		getWaitDriver().waitForElementToBeNotStale(String.format(EDITOR_BUTTON_CSS, buttonWidgetId));
		scrollWidgetIntoView(String.format(EDITOR_BUTTON_CSS, buttonWidgetId));
		return isElementInViewport(String.format(EDITOR_BUTTON_CSS, buttonWidgetId));
	}

	/**
	 * Clicks button.
	 *
	 * @param buttonWidgetId String the widget id
	 */
	public void clickEditorButton(final String buttonWidgetId) {
		scrollWidgetIntoView(String.format(EDITOR_BUTTON_CSS, buttonWidgetId));
		getWaitDriver().waitForElementToBeInteractable(String.format(EDITOR_BUTTON_CSS, buttonWidgetId));
		clickButton(String.format(EDITOR_BUTTON_CSS, buttonWidgetId), buttonWidgetId);
	}

	/**
	 * Clicks Cancel Shipment button.
	 *
	 * @return the confirm dialog
	 */
	public ConfirmDialog clickCancelShipmentButton() {
		clickEditorButton("Cancel Shipment");
		return new ConfirmDialog(getDriver());
	}

	/**
	 * Clicks Create Return button.
	 *
	 * @return CreateReturnWizard
	 */
	public CreateReturnWizard clickCreateReturnButton() {
		clickEditorButton("Create Return ");
		return new CreateReturnWizard(getDriver());
	}

	/**
	 * Checks if Create Return button in view port.
	 *
	 * @return boolean
	 */
	public boolean isCreateReturnButtonInViewport() {
		return isEditorButtonInViewport("Create Return ");
	}

	/**
	 * Clicks Create Exchange button.
	 *
	 * @return CreateExchangeWizard
	 */
	public CreateExchangeWizard clickCreateExchangeButton() {
		clickEditorButton("Create Exchange");
		return new CreateExchangeWizard(getDriver());
	}

	/**
	 * Clicks Release Shipment button.
	 */
	public void clickReleaseShipmentButton() {
		String buttonText = "Release Shipment";
		scrollWidgetIntoView(String.format(EDITOR_BUTTON_CSS, buttonText));
		clickButton(String.format(EDITOR_BUTTON_CSS, buttonText), buttonText);
		new ConfirmDialog(getDriver()).clickOKButton("FulfillmentMessages.ShipmentSection_ReleaseShipmentConfirm");
	}

	/**
	 * Checks if Release Shipment button in view port.
	 *
	 * @return boolean
	 */
	public boolean isReleaseShipmentButtonInViewport() {
		return isEditorButtonInViewport("Release Shipment");
	}

	/**
	 * Clicks Create Refund button.
	 *
	 * @return CreateRefundWizard
	 */
	public CreateRefundWizard clickCreateRefundButton() {
		clickButton(CREATE_REFUND_BUTTON_CSS, "Create Refund");
		return new CreateRefundWizard(getDriver());
	}

	/**
	 * Clicks Open Exchange Order button.
	 */
	public void clickOpenExchangeOrderButton() {
		clickEditorButton("Open Exchange Order...");
	}

	/**
	 * Clicks Move Item button.
	 *
	 * @return MoveItemDialog
	 */
	public MoveItemDialog clickMoveItemButton() {
		clickEditorButton("Move Item...");
		return new MoveItemDialog(getDriver());
	}

	/**
	 * Clicks Add Item button.
	 *
	 * @return SelectASkuDialog
	 */
	public SelectASkuDialog clickAddItemButton() {
		clickEditorButton("Add Item...");
		return new SelectASkuDialog(getDriver());
	}

	/**
	 * Clicks Remove Item button.
	 */
	public void clickRemoveItemButton() {
		clickEditorButton("Remove Item...");
		new ConfirmDialog(getDriver()).clickOKButton("FulfillmentMessages.ShipmentSection_RemoveItemConfirm");
	}

	/**
	 * Verifies Order Status.
	 *
	 * @param expectedOrderStatus the expected order status.
	 */
	public void verifyOrderStatus(final String expectedOrderStatus) {
		assertThat(getWaitDriver().waitForElementToBeVisible(By.cssSelector(ORDER_STATUS_INPUT_CSS)).getAttribute(ATTRIBUTE_VALUE))
				.as("Order Status validation failed")
				.isEqualTo(expectedOrderStatus);
	}

	/**
	 * Verifies Shipment Status.
	 *
	 * @param expectedShipmentStatus the expected shipment status.
	 */
	public void verifyShipmentStatus(final String expectedShipmentStatus) {
		getWaitDriver().waitForTextInInput(By.cssSelector(SHIPMENT_STATUS_INPUT_CSS), expectedShipmentStatus);
		assertThat(getDriver().findElement(By.cssSelector(SHIPMENT_STATUS_INPUT_CSS)).getAttribute(ATTRIBUTE_VALUE))
				.as("Shipment status validation failed")
				.isEqualTo(expectedShipmentStatus);
	}

	/**
	 * Clicks to select tab.
	 *
	 * @param tabName the tab name.
	 */
	public void clickTab(final String tabName) {
		String cssSelector = String.format(TAB_CSS, tabName);
		resizeWindow(cssSelector);
		click(getWaitDriver().waitForElementToBeClickable(By.cssSelector(cssSelector)));
		switch (tabName) {
			case "Summary":
				getWaitDriver().waitForElementToBeVisible(By.cssSelector(ORDER_SUMMARY_TITLE));
				break;
			case "Details":
				getWaitDriver().waitForElementToBeVisible(By.cssSelector(ORDER_DETAILS_TITLE));
				break;
			case "Payments":
				getWaitDriver().waitForElementToBeVisible(By.cssSelector(ORDER_PAYMENTS_TITLE));
				break;
			case "Returns and Exchanges":
				getWaitDriver().waitForElementToBeVisible(By.cssSelector(RETURNS_AND_EXCHANGES_TITLE));
				break;
			default:
				fail("Editor is not visible for tab - " + tabName);
				return;
		}
	}

	/**
	 * Verifies Transaction Type.
	 *
	 * @param transactionType the transaction type.
	 */
	public void verifyTransactionType(final String transactionType) {
		verifyPaymentHistoryColumnValue(transactionType, "Transaction Type");
	}

	/**
	 * Verifies Payment History column value.
	 *
	 * @param columnValue the column value.
	 * @param columnName  the column number.
	 */
	public void verifyPaymentHistoryColumnValue(final String columnValue, final String columnName) {
		sleep(SLEEP_TIME);
		assertThat(selectItemInEditorPane(PAYMENT_TABLE_PARENT_CSS, PAYMENT_COLUMN_CSS, columnValue, columnName))
				.as("Unable to find payment history column value - " + columnValue)
				.isTrue();
	}

	/**
	 * Verifies returned sku code.
	 *
	 * @param skuCode the sku code
	 */
	public void verifyReturnSkuCode(final String skuCode) {
		sleep(SLEEP_TIME);
		assertThat(selectItemInDialog(SKU_TABLE_PARENT_CSS, SKU_COLUMN_CSS, skuCode, SKU_CODE_COLUMN_NAME))
				.as("Unable to find sku - " + skuCode)
				.isTrue();
	}

	/**
	 * Verifies exchange order number is present.
	 *
	 * @return exchangeOrderNumber String
	 */
	public String verifyExchangeOrderNumberIsPresent() {
		String exchangeOrderNumber = getDriver().findElement(By.cssSelector(EXCHANGE_ORDER_NUMBER_INPUT_CSS)).getAttribute(ATTRIBUTE_VALUE);
		assertThat(Integer.parseInt(exchangeOrderNumber) > 0)
				.as("Unable to find exchange order number")
				.isTrue();
		return exchangeOrderNumber;
	}


	/**
	 * Verifies original and exchange order numbers.
	 *
	 * @param originalOrderNumber String
	 * @param exchangeOrderNumber String
	 */
	public void verifyOriginalAndExchangeOrderNumbers(final String originalOrderNumber, final String exchangeOrderNumber) {
		assertThat(getDriver().findElement(By.cssSelector(ORDER_NUMBER_INPUT_CSS)).getAttribute(ATTRIBUTE_VALUE))
				.as("Exchange order number validation failed")
				.isEqualTo(exchangeOrderNumber);

		assertThat(getDriver().findElement(By.cssSelector(EXTERNAL_ORDER_NUMBER_INPUT_CSS)).getAttribute(ATTRIBUTE_VALUE))
				.as("Exchange order number validation failed")
				.isEqualTo(originalOrderNumber);
	}

	/**
	 * Verifies and select order sku code.
	 *
	 * @param skuCode the sku code
	 */
	public void verifyAndSelectOrderSkuCode(final String skuCode) {
		sleep(SLEEP_TIME);
		assertThat(selectItemInDialog(ORDER_DETAIL_SHIPMENT_TABLE_PARENT_CSS, ORDER_DETAIL_SHIPMENT_TABLE_COLUMN_CSS, skuCode, SKU_CODE_COLUMN_NAME))
				.as("Unable to find order sku - " + skuCode)
				.isTrue();
	}

	/**
	 * Verifies sku is not in the list.
	 *
	 * @param skuCode the sku code
	 */
	public void verifySkuCodeIsNotInList(final String skuCode) {
		setWebDriverImplicitWait(Constants.IMPLICIT_WAIT_FOR_ELEMENT_NOT_EXISTS);
		assertThat(verifyItemIsNotInDialog(ORDER_DETAIL_SHIPMENT_TABLE_PARENT_CSS, ORDER_DETAIL_SHIPMENT_TABLE_COLUMN_CSS, skuCode,
				SKU_CODE_COLUMN_NAME))
				.as("Sku is still in the list - " + skuCode)
				.isFalse();
		setWebDriverImplicitWaitToDefault();
	}


	/**
	 * Verifies shipment number.
	 *
	 * @param shipmentNumber String
	 */
	public void verifyShipmentNumber(final String shipmentNumber) {
		assertThat(getDriver().findElement(By.cssSelector(EDITOR_PANE_PARENT_CSS)).getText().contains(shipmentNumber))
				.as("Unable to find shipment number - " + shipmentNumber)
				.isTrue();
	}

	/**
	 * Returns customer's email address.
	 *
	 * @return cusomer's email
	 */
	public String getCustomerEmail() {
		return getDriver().findElement(By.cssSelector(EMAIL_ADDRESS_CSS)).getText();
	}

	/**
	 * Clicks Open Customer Profile button.
	 *
	 * @return CustomerEditor
	 */
	public CustomerEditor clickOpenCustomerProfileButton() {
		clickEditorButton("Open Customer Profile...");
		getWaitDriver().waitForElementToBeVisible(By.cssSelector("div[widget-id='Basic Profile'][seeable='true']"));
		return new CustomerEditor(getDriver());
	}

	/**
	 * Opens Item Detail dialog.
	 *
	 * @param skuCode sku code.
	 * @return EditItemDetailsDialog.
	 */
	public EditItemDetailsDialog clickItemDetailButton(final String skuCode) {
		getWaitDriver().waitForElementToBeInteractable(ORDER_DETAIL_SHIPMENT_TABLE_PARENT_CSS);
		assertThat(selectItemInDialog(ORDER_DETAIL_SHIPMENT_TABLE_PARENT_CSS, ORDER_DETAIL_SHIPMENT_TABLE_COLUMN_CSS, skuCode, SKU_CODE_COLUMN_NAME))
				.as("Unable to find order sku - " + skuCode)
				.isTrue();
		clickButton(ITEM_DETAIL_BUTTON_CSS, "Item Detail", EditItemDetailsDialog.EDIT_ITEM_DIALOG_PARENT_CSS);
		return new EditItemDetailsDialog((getDriver()));
	}

	/**
	 * * Verifies Returned sku table  column value.
	 *
	 * @param columnValue the column value.
	 * @param columnName  the column number.
	 */
	public void verifyReturnedSkuColumnValue(final String columnValue, final String columnName) {
		sleep(SLEEP_TIME);
		assertThat(selectItemInEditorPane(RETURNED_SKU_TABLE_PARENT_CSS, RETURNED_SKU_COLUMN_CSS, columnValue, columnName))
				.as("Unable to find returned sku column value - " + columnValue)
				.isTrue();
	}

	/**
	 * Clicks Edit Address... button.
	 *
	 * @return EditAddressDialog
	 */
	public AddEditCustomerAddressDialog clickEditAddressButton() {
		clickEditorButton("Edit Shipping Address...");
		return new AddEditCustomerAddressDialog(getDriver());
	}

	/**
	 * Verify UnLock Order button is not enabled.
	 */
	public void verifyUnlockOrderIsNotEnabled() {
		assertThat(isButtonEnabled(UNLOCK_ORDER_CSS))
				.as("Order is still locked after saving.")
				.isFalse();
	}

	/**
	 * Verifies Shipment Discount.
	 *
	 * @param shipmentDiscount the expected discount value.
	 */
	public void verifyShipmentDiscountValue(final String shipmentDiscount) {
		assertThat(getWaitDriver().waitForElementToBeVisible(By.cssSelector(SHIPMENT_DISCOUNT_VALUE_CSS)).getAttribute(ATTRIBUTE_VALUE))
				.as("Expected Shipment Discount value does not match.")
				.isEqualTo(shipmentDiscount);
	}
}
