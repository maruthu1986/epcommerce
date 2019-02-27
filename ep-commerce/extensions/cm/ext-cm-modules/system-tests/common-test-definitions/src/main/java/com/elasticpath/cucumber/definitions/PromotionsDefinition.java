package com.elasticpath.cucumber.definitions;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import com.elasticpath.selenium.editor.CartPromotionEditor;
import com.elasticpath.selenium.editor.CatalogPromotionEditor;
import com.elasticpath.selenium.navigations.PromotionsShipping;
import com.elasticpath.selenium.resultspane.PromotionSearchResultPane;
import com.elasticpath.selenium.setup.SetUp;
import com.elasticpath.selenium.toolbars.PromotionsShippingActionToolbar;
import com.elasticpath.selenium.util.Constants;
import com.elasticpath.selenium.util.Utility;
import com.elasticpath.selenium.wizards.CreateCartPromotionWizard;
import com.elasticpath.selenium.wizards.CreateCatalogPromotionWizard;

/**
 * Promotions Shipping step definitions.
 */
public class PromotionsDefinition {

	private static final String DASH = " - ";
	private static final int SLEEP_TIME = 500;
	private final PromotionsShipping promotionsShipping;
	private final PromotionsShippingActionToolbar promotionsShippingActionToolbar;
	private PromotionSearchResultPane promotionSearchResultPane;
	private CreateCatalogPromotionWizard createCatalogPromotionWizard;
	private CreateCartPromotionWizard createCartPromotionWizard;
	private CatalogPromotionEditor catalogPromotionEditor;
	private CartPromotionEditor cartPromotionEditor;
	private static String catalogPromoName = "";
	private static String cartPromoName = "";
	private static String cartCouponCode = "";
	private static String cartCouponEmail = "";
	private static String cartCouponType = "coupon type";


	/**
	 * Constructor.
	 */
	public PromotionsDefinition() {
		promotionsShipping = new PromotionsShipping(SetUp.getDriver());
		promotionsShippingActionToolbar = new PromotionsShippingActionToolbar(SetUp.getDriver());
	}

	/**
	 * Click Promotion Search Button.
	 */
	@When("^I click Search button in Promotion tab$")
	public void clickPromotionSearchButton() {
		promotionSearchResultPane = promotionsShipping.clickPromotionSearchButton();
	}

	/**
	 * Verify Promotion Search Results.
	 *
	 * @param promotionList The promotion list.
	 */
	@Then("^Promotion Search Results should contain following promotions?$")
	public void verifyPromotionSearchResult(final List<String> promotionList) {
		for (String promotionName : promotionList) {
			promotionSearchResultPane.verifyPromotionExists(promotionName);
		}
	}

	/**
	 * Create catalog promotion.
	 *
	 * @param catalogPromoMap the catalog promotion map.
	 */
	@When("^I create catalog promotion with following values$")
	public void createCatalogPromotion(final Map<String, String> catalogPromoMap) {
		String uuidString = Utility.getRandomUUID();
		catalogPromoName = catalogPromoMap.get("name") + DASH + uuidString;
		createCatalogPromotionWizard = promotionsShippingActionToolbar.clickCreateCatalogPromotionButton();
		createCatalogPromotionWizard.selectCatalog(catalogPromoMap.get("catalog"));
		createCatalogPromotionWizard.enterPromotionName(catalogPromoName);
		createCatalogPromotionWizard.enterPromotionDisplayName(catalogPromoMap.get("display name") + DASH + uuidString);
		createCatalogPromotionWizard.enterEnableDateTime("Mar 17, 2017 2:39 PM");
		createCatalogPromotionWizard.clickNextInDialog();
		createCatalogPromotionWizard.openConditionMenu();
		createCatalogPromotionWizard.selectConditionMenuItem(catalogPromoMap.get("condition menu item"));
		createCatalogPromotionWizard.openDiscountMenuAndSelectMenuItem(catalogPromoMap.get("discount menu item"));
		createCatalogPromotionWizard.selectDiscountSubMenuItem(catalogPromoMap.get("discount sub menu item"));
		createCatalogPromotionWizard.enterDiscountValue(catalogPromoMap.get("discount value"));
		createCatalogPromotionWizard.clickFinish();
	}

	/**
	 * Verify New catalog promotion.
	 */
	@And("^newly created catalog promotion exists$")
	public void verifyNewCatalogPromotion() {
		searchForPromotionByName(catalogPromoName);
	}

	/**
	 * Disable new catalog promotion.
	 */
	@And("^I edit and disable newly created catalog promotion$")
	public void disableNewCatalogPromotion() {
		catalogPromotionEditor = promotionSearchResultPane.openCatalogPromotionEditor();
		catalogPromotionEditor.enterCurrentExpirationDateTime();
		promotionsShippingActionToolbar.saveAll();
	}

	/**
	 * Verify Catalog promotion state.
	 *
	 * @param state the state.
	 */
	@And("^catalog promotion state should be (.+)$")
	public void verifyCatalogPromotionState(final String state) {
		catalogPromotionEditor.verifyPromoState(state);
	}

	/**
	 * Create cart promotion.
	 *
	 * @param cartPromoMap the cart promotion map.
	 */
	@When("^I create cart promotion with following values$")
	public void createCartPromotion(final Map<String, String> cartPromoMap) {
		String uuidString = Utility.getRandomUUID();
		cartPromoName = cartPromoMap.get("name") + DASH + uuidString;

		createCartPromotionWizard = promotionsShippingActionToolbar.clickCreateCartPromotionButton();
		createCartPromotionWizard.selectStore(cartPromoMap.get("store"));
		createCartPromotionWizard.enterPromotionName(cartPromoName);
		createCartPromotionWizard.enterPromotionDisplayName(cartPromoMap.get("display name") + DASH + uuidString);
		createCartPromotionWizard.clickNextInDialog();
		createCartPromotionWizard.clickNextInDialog();
		createCartPromotionWizard.clickNextInDialog();
		createCartPromotionWizard.openConditionMenu();
		createCartPromotionWizard.selectConditionMenuItem(cartPromoMap.get("condition menu item"));
		createCartPromotionWizard.openDiscountMenuAndSelectMenuItem(cartPromoMap.get("discount menu item"));
		createCartPromotionWizard.selectDiscountSubMenuItem(cartPromoMap.get("discount sub menu item"));
		createCartPromotionWizard.enterDiscountValue(cartPromoMap.get("discount value"));
		createCartPromotionWizard.clickFinish();
	}

	/**
	 * Create coupon cart promotion.
	 *
	 * @param cartPromoMap the coupon cart promotion map.
	 */
	@When("^I create coupon cart promotion with following values$")
	public void createCouponCartPromotion(final Map<String, String> cartPromoMap) {
		String uuidString = Utility.getRandomUUID();
		cartPromoName = cartPromoMap.get("name") + DASH + uuidString;
		cartCouponCode = cartPromoMap.get("coupon code") + " " + uuidString;
		cartCouponEmail = cartPromoMap.get("coupon email") + "_" + uuidString;
		createCartPromotionWizard = promotionsShippingActionToolbar.clickCreateCartPromotionButton();
		createCartPromotionWizard.selectStore(cartPromoMap.get("store"));
		createCartPromotionWizard.enterPromotionName(cartPromoName);
		createCartPromotionWizard.enterPromotionDisplayName(cartPromoMap.get("display name") + DASH + uuidString);
		createCartPromotionWizard.clickNextInDialog();
		createCartPromotionWizard.clickNextInDialog();
		createCartPromotionWizard.clickNextInDialog();
		createCartPromotionWizard.openConditionMenu();
		createCartPromotionWizard.selectConditionMenuItem(cartPromoMap.get("condition menu item"));
		createCartPromotionWizard.openDiscountMenuAndSelectMenuItem(cartPromoMap.get("discount menu item"));
		createCartPromotionWizard.selectDiscountSubMenuItem(cartPromoMap.get("discount sub menu item"));
		createCartPromotionWizard.enterDiscountValue(cartPromoMap.get("discount value"));
		createCartPromotionWizard.clickNextInDialog();
		if ("public".equalsIgnoreCase(cartPromoMap.get(cartCouponType))) {
			createCartPromotionWizard.clickPromotionActivatedByPublicCouponButton(cartCouponCode);
		} else if ("private".equalsIgnoreCase(cartPromoMap.get(cartCouponType))) {
			createCartPromotionWizard.clickPromotionActivatedByPrivateCouponButton(cartCouponCode, cartCouponEmail);
		} else {
			assertThat(("public".equalsIgnoreCase(cartPromoMap.get(cartCouponType)))
					|| ("private".equalsIgnoreCase(cartPromoMap.get(cartCouponType))))
					.as("Invalid coupon type entered - " + (cartPromoMap.get(cartCouponType)))
					.isTrue();
		}
		createCartPromotionWizard.clickFinish();
	}

	/**
	 * Verify new cart promotion.
	 */
	@And("^I verify newly created cart promotion exists$")
	public void verifyNewCartPromotion() {
		searchForPromotionByName(cartPromoName);
	}

	/**
	 * Disable new cart promotion.
	 */
	@And("^I disable newly created cart promotion$")
	public void disableNewCartPromotion() {
		cartPromotionEditor = promotionSearchResultPane.openCartPromotionEditor();
		cartPromotionEditor.disableCartPromotion();
		promotionsShippingActionToolbar.saveAll();
	}

	/**
	 * Verify cart promotion state.
	 *
	 * @param state the state.
	 */
	@And("^cart promotion state should be (.+)$")
	public void verifyCartPromotionState(final String state) {
		promotionsShippingActionToolbar.clickReloadActiveEditor();
		cartPromotionEditor.verifyPromoState(state);
	}

	/**
	 * Is promotion in list.
	 *
	 * @param promoName the promotion name.
	 */
	public void searchForPromotionByName(final String promoName) {
		promotionsShipping.enterPromotionName(promoName);
		promotionSearchResultPane = promotionsShipping.clickPromotionSearchButton();
		boolean isPromotionInList = promotionSearchResultPane.isPromotionInList(promoName);

		int index = 0;
		while (!isPromotionInList && index < Constants.UUID_END_INDEX) {
			promotionSearchResultPane.sleep(SLEEP_TIME);
			promotionsShipping.clickPromotionSearchButton();
			isPromotionInList = promotionSearchResultPane.isPromotionInList(promoName);
			index++;
		}

		assertThat(isPromotionInList)
				.as("Promotion does not exist in search result - " + promoName)
				.isTrue();
	}

	/**
	 * Verify create catalog promotion button is present.
	 */
	@And("^I can view Create Catalog Promotion button")
	public void verifyCreateCatalogPromotionButtonIsPresent() {
		promotionsShippingActionToolbar.verifyCreateCatalogPromotionButtonIsPresent();
	}

	/**
	 * Selects new promotion.
	 */
	@And("^I select the newly created promotion")
	public void selectNewPromotion() {
		promotionSearchResultPane.selectPromotion(cartPromoName);
	}

	/**
	 * Returns catalog promotion name.
	 *
	 * @return catalogPromoName
	 */
	public static String getCatalogPromoName() {
		return catalogPromoName;
	}

	/**
	 * Returns cart promotion name.
	 *
	 * @return cartPromoName
	 */
	public static String getCartPromoName() {
		return cartPromoName;
	}

	/**
	 * Suspend newly created coupon code and disable cart promotion.
	 */
	@And("^I suspend newly created coupon and disable cart promotion$")
	public void suspendNewCartPromotionCoupon() {
		cartPromotionEditor = promotionSearchResultPane.openCartPromotionEditor();
		cartPromotionEditor.disableCartPromotion();
		cartPromotionEditor.suspendCartPromotionCoupon(cartCouponCode);
		promotionsShippingActionToolbar.saveAll();
	}
}
