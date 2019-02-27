/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cucumber.shoppingcart;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.util.List;
import javax.inject.Provider;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import cucumber.api.java.After;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.cucumber.ScenarioContextValueHolder;
import com.elasticpath.domain.catalog.AvailabilityCriteria;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.discounts.ShoppingCartDiscountItemContainer;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.store.Store;
import com.elasticpath.sellingchannel.director.CartDirector;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.service.catalog.ProductTypeService;
import com.elasticpath.test.persister.CatalogTestPersister;
import com.elasticpath.test.persister.TaxTestPersister;
import com.elasticpath.test.persister.TestDataPersisterFactory;
import com.elasticpath.test.persister.testscenarios.SimpleStoreScenario;

/**
 * Step definitions for {@link ShoppingCartDiscountItemContainer} features.
 */
public class ShoppingCartDiscountItemContainerStepDefinitions {

	@Autowired
	private ShoppingCartStepDefinitionsHelper shoppingCartStepDefinitionsHelper;

	@Autowired
	private CartDirector cartDirector;

	@Autowired
	private CatalogTestPersister catalogTestPersister;

	@Autowired
	private ScenarioContextValueHolder<Store> storeHolder;

	@Autowired
	private ScenarioContextValueHolder<SimpleStoreScenario> simpleStoreScenarioHolder;

	@Autowired
	private Provider<ShoppingCartDiscountItemContainer> discountItemContainerProvider;

	@Autowired
	private ProductSkuLookup productSkuLookup;

	@Autowired
	private ProductTypeService productTypeService;

	private List<ShoppingItem> sortedItems;

	/**
	 * Tear down.
	 */
	@After
	public void tearDown() {
		sortedItems = null;
	}

	/**
	 * Creates a new shopping item and adds it to the cart.
	 *
	 * @param shoppingItemId the ID of the shopping item
	 */
	@Given("^a non-discountable Cart Item (.+?)$")
	public void givenNonDiscountableShoppingItem(final String shoppingItemId) {

		final String productTypeName = "NonDiscountableProductType";
		final Store store = storeHolder.get();
		final SimpleStoreScenario scenario = simpleStoreScenarioHolder.get();

		final ProductType productType = catalogTestPersister.createSimpleProductType(
				productTypeName,
				false,
				scenario.getCatalog(),
				catalogTestPersister.getPersistedTaxCode(TaxTestPersister.TAX_CODE_GOODS));

		productType.setExcludedFromDiscount(true);

		productTypeService.add(productType);

		catalogTestPersister.persistProductWithSku(store.getCatalog(),
													scenario.getCategory(),
													scenario.getWarehouse(),
													BigDecimal.ONE,
													TestDataPersisterFactory.DEFAULT_CURRENCY,
													null,
													"Product_Code" + shoppingItemId,
													"Product_Name" + shoppingItemId,
													productTypeName,
													shoppingItemId,
													TaxTestPersister.TAX_CODE_GOODS,
													BigDecimal.ZERO,
													false,
													AvailabilityCriteria.ALWAYS_AVAILABLE,
													1);

		addShoppingItemToCart(shoppingItemId, BigDecimal.ONE, 1);
	}

	/**
	 * Creates a new shopping item and adds it to the cart.
	 *
	 * @param shoppingItemId the ID of the shopping item
	 * @param listPrice the list price of the shopping item
	 */
	@Given("^a Cart Item (.+?) with List Price \\$([0-9\\.]+)$")
	public void givenShoppingItem(final String shoppingItemId, final BigDecimal listPrice) {
		addShoppingItemToCart(shoppingItemId, listPrice, 1);
	}

	/**
	 * Creates a new shopping item and adds it to the cart.
	 *
	 * @param shoppingItemId the ID of the shopping item
	 * @param listPrice the list price of the shopping item
	 * @param discountAmount the discount amount of the shopping item
	 */
	@Given("^a Cart Item (.+?) with List Price \\$([0-9\\.]+) and cart discount \\$([0-9\\.]+)$")
	public void createShoppingItem(final String shoppingItemId, final BigDecimal listPrice, final BigDecimal discountAmount) {
		final ShoppingItem shoppingItem = addShoppingItemToCart(shoppingItemId, listPrice, 1);
		shoppingItem.applyDiscount(discountAmount, productSkuLookup);
	}

	/**
	 * Creates a new shopping item and adds it to the cart.
	 *
	 * @param shoppingItemId the ID of the shopping item
	 * @param listPrice the list price of the shopping item
	 * @param quantity the quantity to add
	 */
	@Given("^a Cart Item (.+?) with List Price \\$([0-9\\.]+) and quantity (\\d)$")
	public void createShoppingItem(final String shoppingItemId, final BigDecimal listPrice, final int quantity) {
		addShoppingItemToCart(shoppingItemId, listPrice, quantity);
	}

	/**
	 * Creates a new ShoppingItem instance and adds it to the shopping cart.
	 *
	 * @param shoppingItemId the ID of the shopping item
	 * @param listPrice the list price of the shopping item
	 * @param quantity the quantity to add
	 * @return the ShoppingItem added to the cart
	 */
	protected ShoppingItem addShoppingItemToCart(final String shoppingItemId, final BigDecimal listPrice, final int quantity) {
		final ShoppingItemDto dto = new ShoppingItemDto(shoppingItemId, quantity);

		if (productSkuLookup.findBySkuCode(shoppingItemId) == null) {
			final Store store = storeHolder.get();
			final SimpleStoreScenario scenario = simpleStoreScenarioHolder.get();
			catalogTestPersister.persistNonShippablePersistedProductWithSku(
					store.getCatalog(),
					scenario.getCategory(),
					scenario.getWarehouse(),
					listPrice,
					"Product_Name" + shoppingItemId,
					shoppingItemId);
		}

		final ShoppingCart shoppingCart = shoppingCartStepDefinitionsHelper.getShoppingCart();
		return cartDirector.addItemToCart(shoppingCart, dto);
	}

	/**
	 * Retrieves the sorted list of shopping items.
	 */
	@When("^the promotion engine evaluates the list of discountable shopping items$")
	public void getSortedListOfShoppingItems() {
		final ShoppingCartDiscountItemContainer discountItemContainer = discountItemContainerProvider.get();
		discountItemContainer.setShoppingCart(shoppingCartStepDefinitionsHelper.getShoppingCart());
		discountItemContainer.setCurrency(TestDataPersisterFactory.DEFAULT_CURRENCY);

		sortedItems = discountItemContainer.getItemsLowestToHighestPrice();
	}

	/**
	 * Verifies that the list of shopping items are sorted in the expected order.
	 *
	 * @param expectedShoppingItemIds the expected order
	 */
	@Then("^the items are sorted in the order of (.+?)$")
	public void verifyItemsSorted(final List<String> expectedShoppingItemIds) {
		final List<String> sortedShoppingItemSkuCodes = convertSortedItemsToItemIds();

		assertEquals(expectedShoppingItemIds, sortedShoppingItemSkuCodes);
	}

	/**
	 * Verifies that the list of shopping items does not contain the shopping item identified by the provided id.
	 *
	 * @param shoppingItemId the ID of the shopping item that should not exist in the list
	 */
	@Then("^the set of items does not include Shopping Item (.+?)$")
	public void verifySortedItemListDoesNotContain(final String shoppingItemId) {
		final List<String> sortedShoppingItemSkuCodes = convertSortedItemsToItemIds();

		assertThat(sortedShoppingItemSkuCodes, not(hasItem(shoppingItemId)));
	}

	private List<String> convertSortedItemsToItemIds() {
		return Lists.transform(sortedItems, new Function<ShoppingItem, String>() {
			@Override
			public String apply(final ShoppingItem input) {
				final ProductSku sku = productSkuLookup.findByGuid(input.getSkuGuid());
				return sku.getSkuCode();
			}
		});
	}

}
