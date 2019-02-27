package com.elasticpath.selenium.editor;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.common.AbstractPageObject;

/**
 * Cart Promotion Details pane.
 */
public class CartPromotionEditor extends AbstractPageObject {

	/**
	 * Editor Pane CSS.
	 */
	public static final String EDITOR_PANE_PARENT_CSS = "div[pane-location='editor-pane'] div[active-editor='true'] ";
	/**
	 * Page Object Id CSS.
	 */
	public static final String CART_PROMOTION_EDITOR_PAGE_OBJECT_ID = "div[automation-id*='com.elasticpath.cmclient.store.promotions.editors']";
	private static final String STATE_INPUT_CSS =
			"div[automation-id='com.elasticpath.cmclient.store.promotions.PromotionsMessages.PromoStoreRules_State'] > input";
	private static final String ENABLE_ICON_CSS = "div[widget-type='Button'] > div[style*='1882de9d.png']";
	private static final String TAB_CSS = "div[widget-id='%s'][seeable='true']";
	private static final String COUPON_TABLE_PARENT_CSS = EDITOR_PANE_PARENT_CSS + "div[widget-id='Coupon Usage'] ";
	private static final String COUPON_COLUMN_CSS = COUPON_TABLE_PARENT_CSS + "div[column-id='%s']";
	private static final String EDIT_COUPON_CSS = "div[widget-id='Edit...']";
	private static final String COUPON_CODE_EDITOR_PARENT_CSS = "div[widget-id='Coupon Code Editor'][widget-type='Shell'] ";
	private static final String COUPON_STATUS_COMBO_CSS = COUPON_CODE_EDITOR_PARENT_CSS + "div[widget-type='CCombo']";
	private static final String ADD_SINGLE_COUPON_PARENT_CSS = "div[automation-id*='com.elasticpath.cmclient.store.promotions.PromotionsMessages"
			+ ".CouponSingleEditorDialog_Title'][widget-type='Shell'] ";
	private static final String ADD_SINGLE_COUPON_OK_BUTTON_CSS = ADD_SINGLE_COUPON_PARENT_CSS
			+ "div[automation-id='com.elasticpath.cmclient.core.CoreMessages.AbstractEpDialog_ButtonOK']";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public CartPromotionEditor(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Clicks to un-check Enable in Store box.
	 */
	public void disableCartPromotion() {
		click(getWaitDriver().waitForElementToBeClickable(By.cssSelector(ENABLE_ICON_CSS)));
	}

	/**
	 * Verifies promotion state.
	 *
	 * @param state the state.
	 */
	public void verifyPromoState(final String state) {
		assertThat(getWaitDriver().waitForElementToBeVisible(By.cssSelector(STATE_INPUT_CSS)).getAttribute("value"))
				.as("Cart promotion state validation failed")
				.isEqualTo(state);
	}

	/**
	 * Suspend coupon code.
	 *
	 * @param couponCode status
	 */
	public void suspendCartPromotionCoupon(final String couponCode) {
		String cssSelector = String.format(TAB_CSS, "Coupon Codes");
		resizeWindow(cssSelector);
		click(getWaitDriver().waitForElementToBeClickable(By.cssSelector(cssSelector)));
		verifyAndSelectCouponCode(couponCode);
		clickButton(EDIT_COUPON_CSS, "Edit");
		selectStatus("Suspended");
		clickButton(ADD_SINGLE_COUPON_OK_BUTTON_CSS, "OK");
		waitTillElementDisappears(By.cssSelector(ADD_SINGLE_COUPON_PARENT_CSS));
		click(getWaitDriver().waitForElementToBeClickable(By.cssSelector(String.format(TAB_CSS, "Summary"))));
	}

	/**
	 * Verifies and select coupon code.
	 *
	 * @param couponCode cart promotion
	 */
	public void verifyAndSelectCouponCode(final String couponCode) {
		assertThat(selectItemInEditorPane(COUPON_TABLE_PARENT_CSS, COUPON_COLUMN_CSS, couponCode, "Code"))
				.as("Unable to find coupon column value - " + "Code")
				.isTrue();
	}

	/**
	 * Selects status for coupon in combo box.
	 *
	 * @param couponStatus cart promotion
	 */
	public void selectStatus(final String couponStatus) {
		assertThat(selectComboBoxItem(COUPON_STATUS_COMBO_CSS, couponStatus))
				.as("Unable to find coupon status - " + couponStatus)
				.isTrue();
	}

}
