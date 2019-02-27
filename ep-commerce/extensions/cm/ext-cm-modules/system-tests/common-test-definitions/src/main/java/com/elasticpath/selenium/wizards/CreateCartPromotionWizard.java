package com.elasticpath.selenium.wizards;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Create Cart Promotion Wizard.
 */
public class CreateCartPromotionWizard extends AbstractWizard {

	private static final String CREATE_SHOPPING_CART_PROMOTION_PARENT_CSS
			= "div[automation-id='com.elasticpath.cmclient.store.promotions.PromotionsMessages.CreatePromotionsWizard_Title'] ";
	private static final String CREATE_COUPON_PARENT_CSS
			= "div[automation-id*='com.elasticpath.cmclient.store.promotions.PromotionsMessages";
	private static final String STORE_COMBO_CSS = CREATE_SHOPPING_CART_PROMOTION_PARENT_CSS + "div[widget-id='Store'][widget-type='CCombo']";
	private static final String NAME_INPUT_CSS = CREATE_SHOPPING_CART_PROMOTION_PARENT_CSS + "div[widget-id='Promotion Name'] > input";
	private static final String DISPLAY_NAME_INPUT_CSS = CREATE_SHOPPING_CART_PROMOTION_PARENT_CSS
			+ "div[widget-id=''][widget-type='Text'] > input";
	private static final String CONDITION_ICON_CSS
			= CREATE_SHOPPING_CART_PROMOTION_PARENT_CSS + "div[widget-id='of these conditions are true'][widget-type='ImageHyperlink']";
	private static final String CONDITION_MENU_ITEM_CSS = "div[widget-id*='%s']";
	private static final String DISCOUNT_MENU_ITEM_CSS = "div[widget-id='%s']";
	private static final String DISCOUNT_SUB_MENU_ITEM_CSS = "div[widget-id='%s']";
	private static final String DISCOUNT_ICON_CSS
			= CREATE_SHOPPING_CART_PROMOTION_PARENT_CSS + "div[widget-id='They get'][widget-type='ImageHyperlink']";
	private static final String DISCOUNT_VALUE_INPUT_CSS = CREATE_SHOPPING_CART_PROMOTION_PARENT_CSS + "div[widget-id*='Get'] > input";
	private static final String PUBLIC_COUPON_RADIO_BUTTON_CSS = CREATE_COUPON_PARENT_CSS
			+ ".CouponConfigPageActivatedByPublicCoupons'] > div[style*='.png']";
	private static final String PRIVATE_COUPON_RADIO_BUTTON_CSS = CREATE_COUPON_PARENT_CSS
			+ ".CouponConfigPageActivatedByPrivateCoupons'] > div[style*='.png']";
	private static final String MANAGE_COUPON_BUTTON_CSS =
			"div[automation-id='com.elasticpath.cmclient.store.promotions.PromotionsMessages.CouponConfigPageManageCouponCodes'][seeable='true']";
	private static final String ADD_COUPON_CSS = "div[widget-id='Add']";
	private static final String COUPON_CODE_INPUT_CSS = CREATE_COUPON_PARENT_CSS
			+ ".CouponSingleEditorDialog_Title'] " + "div[widget-id='Coupon Code'] > input";
	private static final String COUPON_EMAIL_INPUT_CSS = CREATE_COUPON_PARENT_CSS
			+ ".CouponSingleEditorDialog_Title'] " + "div[widget-id='Email Address'] > input";
	private static final String ADD_SINGLE_COUPON_PARENT_CSS = CREATE_COUPON_PARENT_CSS
			+ ".CouponSingleEditorDialog_Title'][widget-type='Shell'] ";
	private static final String ADD_SINGLE_COUPON_OK_BUTTON_CSS = ADD_SINGLE_COUPON_PARENT_CSS
			+ "div[automation-id='com.elasticpath.cmclient.core.CoreMessages.AbstractEpDialog_ButtonOK']";
	private static final String COUPON_PARENT_CSS = CREATE_COUPON_PARENT_CSS
			+ ".CouponEditorDialog_WindowTitle'][widget-type='Shell'] ";
	private static final String COUPON_OK_BUTTON_CSS = COUPON_PARENT_CSS
			+ "div[automation-id='com.elasticpath.cmclient.core.CoreMessages.AbstractEpDialog_ButtonOK']";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public CreateCartPromotionWizard(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Selects store in combo box.
	 *
	 * @param storeName the store name.
	 */
	public void selectStore(final String storeName) {
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(STORE_COMBO_CSS));
		assertThat(selectComboBoxItem(STORE_COMBO_CSS, storeName))
				.as("Unable to find store - " + storeName)
				.isTrue();
	}

	/**
	 * Inputs promotion name.
	 *
	 * @param promotionName the promotion name.
	 */
	public void enterPromotionName(final String promotionName) {
		clearAndType(NAME_INPUT_CSS, promotionName);
	}

	/**
	 * Inputs promotion display name.
	 *
	 * @param displayName the display name.
	 */
	public void enterPromotionDisplayName(final String displayName) {
		clearAndType(DISPLAY_NAME_INPUT_CSS, displayName);
	}

	/**
	 * Opens condition menu.
	 */
	public void openConditionMenu() {
		click(getWaitDriver().waitForElementToBeClickable(By.cssSelector(CONDITION_ICON_CSS)));
	}

	/**
	 * Selects condition's menu item.
	 *
	 * @param conditionMenuItem the condition menu item.
	 */
	public void selectConditionMenuItem(final String conditionMenuItem) {
		click(getWaitDriver().waitForElementToBeClickable(By.cssSelector(String.format(CONDITION_MENU_ITEM_CSS, conditionMenuItem))));
	}

	/**
	 * Opens discount menu and selects menu item.
	 *
	 * @param menuItem the menu item.
	 */
	public void openDiscountMenuAndSelectMenuItem(final String menuItem) {
		click(getWaitDriver().waitForElementToBeClickable(By.cssSelector(DISCOUNT_ICON_CSS)));
		scrollDownWithDownArrowKey(getDriver().findElement(By.cssSelector(CREATE_SHOPPING_CART_PROMOTION_PARENT_CSS)), 1);
		click(getWaitDriver().waitForElementToBeClickable(By.cssSelector(String.format(DISCOUNT_MENU_ITEM_CSS, menuItem))));
	}

	/**
	 * Selects discount sub-menu item.
	 *
	 * @param subMenuItem the subMenu item.
	 */
	public void selectDiscountSubMenuItem(final String subMenuItem) {
		click(getWaitDriver().waitForElementToBeClickable(By.cssSelector(String.format(DISCOUNT_SUB_MENU_ITEM_CSS, subMenuItem))));
	}

	/**
	 * Inputs discount value.
	 *
	 * @param discountValue the discount value.
	 */
	public void enterDiscountValue(final String discountValue) {
		clearAndType(DISCOUNT_VALUE_INPUT_CSS, discountValue);
	}

	/**
	 * Click Promotion is activated by public coupon button to add coupon.
	 *
	 * @param couponCode cart promotion
	 */
	public void clickPromotionActivatedByPublicCouponButton(final String couponCode) {
		click(getWaitDriver().waitForElementToBeClickable(By.cssSelector(PUBLIC_COUPON_RADIO_BUTTON_CSS)));
		addCouponCode(couponCode);
		closeCouponCodeDialogs();
	}

	/**
	 * Click Promotion is activated by private coupon button to add coupon code and email.
	 *
	 * @param couponCode  cart promotion
	 * @param couponEmail cart promotion
	 */
	public void clickPromotionActivatedByPrivateCouponButton(final String couponCode, final String couponEmail) {
		click(getWaitDriver().waitForElementToBeClickable(By.cssSelector(PRIVATE_COUPON_RADIO_BUTTON_CSS)));
		addCouponCode(couponCode);
		clearAndType(COUPON_EMAIL_INPUT_CSS, couponEmail + "@elasticpath.com");
		closeCouponCodeDialogs();
	}

	/**
	 * Adds coupon code.
	 *
	 * @param couponCode the coupon code
	 */
	private void addCouponCode(final String couponCode) {
		clickButton(MANAGE_COUPON_BUTTON_CSS, "Manage Coupon Code");
		clickButton(ADD_COUPON_CSS, "Add");
		clearAndType(COUPON_CODE_INPUT_CSS, couponCode);
	}

	/**
	 * Closes coupon code dialogs.
	 */
	private void closeCouponCodeDialogs() {
		clickButton(ADD_SINGLE_COUPON_OK_BUTTON_CSS, "OK");
		waitTillElementDisappears(By.cssSelector(ADD_SINGLE_COUPON_PARENT_CSS));
		clickButton(COUPON_OK_BUTTON_CSS, "OK");
	}
}