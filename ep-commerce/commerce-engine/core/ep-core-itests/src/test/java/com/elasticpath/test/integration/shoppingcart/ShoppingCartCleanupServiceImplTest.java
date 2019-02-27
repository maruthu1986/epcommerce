/**
 * Copyright (c) Elastic Path Software Inc., 2011
 */
package com.elasticpath.test.integration.shoppingcart;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;

import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartMemento;
import com.elasticpath.domain.shoppingcart.ShoppingCartMementoHolder;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.WishList;
import com.elasticpath.domain.shoppingcart.impl.ShoppingCartStatus;
import com.elasticpath.domain.store.Store;
import com.elasticpath.sellingchannel.director.CartDirectorService;
import com.elasticpath.service.customer.CustomerSessionService;
import com.elasticpath.service.misc.impl.MockTimeServiceImpl;
import com.elasticpath.service.shopper.ShopperService;
import com.elasticpath.service.shoppingcart.ShoppingCartCleanupService;
import com.elasticpath.service.shoppingcart.ShoppingCartService;
import com.elasticpath.service.shoppingcart.ShoppingItemService;
import com.elasticpath.service.shoppingcart.WishListService;
import com.elasticpath.test.integration.BasicSpringContextTest;
import com.elasticpath.test.integration.DirtiesDatabase;
import com.elasticpath.test.persister.testscenarios.AbstractScenario;
import com.elasticpath.test.persister.testscenarios.ProductsScenario;
import com.elasticpath.test.persister.testscenarios.SimpleStoreScenario;
import com.elasticpath.test.util.Utils;

/**
 * Integration testing when dealing with shopping cart abandonment issues.
 */
@ContextConfiguration(locations="/integration-mock-time-service.xml", inheritLocations = true)
public class ShoppingCartCleanupServiceImplTest extends BasicSpringContextTest {

	private static final int PRODUCT_AGE = 120;
	private static final int EXPECTED_MAX_CART_HISTORY = 60;

	private static final int EXPECTED_MAX_RESULTS = 1000;

	private static final int EXPECTED_TWO_CARTS_REMOVED = 2;

	private static final String SKU_CODE = "0349B004";

	@Autowired private ShoppingCartCleanupService shoppingCartCleanupService;

	@Autowired private ShoppingCartService shoppingCartService;

	@Autowired private ShopperService shopperService;

	@Autowired private CustomerSessionService customerSessionService;

	private SimpleStoreScenario storeScenario;

	@Autowired private CartDirectorService cartDirectorService;

	@Autowired private ShoppingItemService shoppingItemService;

	@Autowired private WishListService wishListService;

	@Autowired @Qualifier(ContextIdNames.TIME_SERVICE)
	private MockTimeServiceImpl mockTimeService;

	private Date priorDate;
	private Date productCreationDate;

	/**
	 * Get a reference to TestApplicationContext for use within the test. <br>
	 * Setup scenarios.
	 * @throws Exception on exception
	 */
	@Before
	@SuppressWarnings("unchecked")
	public void setUp() throws Exception {
		productCreationDate = getAdjustedDate(new Date(), PRODUCT_AGE);
		priorDate = getAdjustedDate(new Date(), EXPECTED_MAX_CART_HISTORY);

		mockTimeService.setCurrentTime(productCreationDate);
		Map<Class<? extends AbstractScenario>, AbstractScenario> scenarios =
				getTac().useScenarios((List) Arrays.asList(SimpleStoreScenario.class, ProductsScenario.class));
		storeScenario = (SimpleStoreScenario) scenarios.get(SimpleStoreScenario.class);
		mockTimeService.setCurrentTime(new Date());
	}

	/**
	 * Test that the max history applied to a date removes the expected number of shopping carts.
	 */
	@DirtiesDatabase
	@Test
	public void testCandidateRemovalByDateBoundaries() {
		Date afterRemovalDate = new Date();
		Date equalsBoundaryDate = getAdjustedDate(afterRemovalDate, EXPECTED_MAX_CART_HISTORY);
		Date beforeRemovalDate = getAdjustedDate(afterRemovalDate, EXPECTED_MAX_CART_HISTORY + 1);

		// create shopping carts around the max history date boundaries
		Shopper shopperAfterRemovalDate = addShoppingCart(afterRemovalDate);
		Shopper shopperEqualsRemovalDate = addShoppingCart(equalsBoundaryDate);
		Shopper shopperBeforeRemovalDate = addShoppingCart(beforeRemovalDate);

		int removed = shoppingCartCleanupService.deleteAbandonedShoppingCarts(equalsBoundaryDate, EXPECTED_MAX_RESULTS);
		assertEquals("There should have been two shopping carts removed.", EXPECTED_TWO_CARTS_REMOVED, removed);

		ShoppingCart updatedCartAfterRemovalDate = shoppingCartService.findOrCreateByShopper(shopperAfterRemovalDate);
		assertEquals("The non-candidate shopping cart should still exist in database.", updatedCartAfterRemovalDate.getGuid(),
				shopperAfterRemovalDate.getCurrentShoppingCart().getGuid());

		ShoppingCart updatedCartEqualsRemovalDate = shoppingCartService.findOrCreateByShopper(shopperEqualsRemovalDate);
		assertFalse("The candidate shopping cart which is equals to the removal date should not exist in database. A new cart was created.",
				updatedCartEqualsRemovalDate.getGuid().equals(shopperEqualsRemovalDate.getCurrentShoppingCart().getGuid()));

		ShoppingCart updatedCartBeforeRemovalDate = shoppingCartService.findOrCreateByShopper(shopperBeforeRemovalDate);
		assertFalse("The candidate shopping cart which is before the removal date should not exist in database. A new cart was created.",
				updatedCartBeforeRemovalDate.getGuid().equals(shopperBeforeRemovalDate.getCurrentShoppingCart().getGuid()));
	}

	/**
	 * Test that the the batch size limits the number of abandoned carts.
	 */
	@DirtiesDatabase
	@Test
	public void testCandidateRemovalByBatchSizeAlsoNoticeRemovesOldestAbandonedCartsFirst() {
		Date afterRemovalDate = new Date();
		Date equalsBoundaryDate = getAdjustedDate(afterRemovalDate, EXPECTED_MAX_CART_HISTORY);
		Date beforeRemovalDate = getAdjustedDate(afterRemovalDate, EXPECTED_MAX_CART_HISTORY + 1);

		// create shopping carts around the max history date boundaries
		Shopper shopperAfterRemovalDate = addShoppingCart(afterRemovalDate);
		Shopper shopperEqualsRemovalDate = addShoppingCart(equalsBoundaryDate);
		Shopper shopperBeforeRemovalDate = addShoppingCart(beforeRemovalDate);

		int removed = shoppingCartCleanupService.deleteAbandonedShoppingCarts(equalsBoundaryDate, 1);
		assertEquals("There should have been one shopping cart removed.", 1, removed);

		ShoppingCart updatedCartAfterRemovalDate = shoppingCartService.findOrCreateByShopper(shopperAfterRemovalDate);
		assertEquals("The non-candidate shopping cart should still exist in database.", updatedCartAfterRemovalDate.getGuid(),
				shopperAfterRemovalDate.getCurrentShoppingCart().getGuid());

		ShoppingCart updatedCartEqualsRemovalDate = shoppingCartService.findOrCreateByShopper(shopperEqualsRemovalDate);
		assertEquals("The candidate shopping cart which is equals to the removal date should still exist in database.",
				updatedCartEqualsRemovalDate.getGuid(), shopperEqualsRemovalDate.getCurrentShoppingCart().getGuid());

		ShoppingCart updatedCartBeforeRemovalDate = shoppingCartService.findOrCreateByShopper(shopperBeforeRemovalDate);
		assertFalse("The candidate shopping cart which is before the removal date should not exist in database. A new cart was created.",
				updatedCartBeforeRemovalDate.getGuid().equals(shopperBeforeRemovalDate.getCurrentShoppingCart().getGuid()));
	}

	/**
	 * Test that if an abandoned shopping cart is removed, that it cascades to remove dependent shopping cart items as well.
	 */
	@DirtiesDatabase
	@Test
	public void testCandidateRemovalRemovesShoppingItems() {
		Date cartCreationTime = priorDate;

		mockTimeService.setCurrentTime(cartCreationTime);
		final Shopper shopper = shopperService.createAndSaveShopper(storeScenario.getStore().getCode());
		ShoppingCart shoppingCart = createShoppingCart(shopper);
		ShoppingItemDto dto = new ShoppingItemDto(SKU_CODE, 1);
		ShoppingItem shoppingItem = cartDirectorService.addItemToCart(shoppingCart, dto);

		mockTimeService.setCurrentTime(new Date());  // Just for clarity here
		int removed = shoppingCartCleanupService.deleteAbandonedShoppingCarts(new Date(cartCreationTime.getTime() + 1000), 100);
		assertEquals("There should have been one shopping cart removed.", 1, removed);

		ShoppingItem actualShoppingItem = shoppingItemService.findByGuid(shoppingItem.getGuid(), null);
		assertNull("This shopping item should have been cascade deleted when the shopping cart was removed.", actualShoppingItem);
	}

	/**
	 * Test that if an abandoned shopping cart is removed, that wish list items can still be accessed by shopper.
	 */
	@DirtiesDatabase
	@Test
	public void testCandidateRemovalDoesNotAdverselyAffectShopperWishLists() {
		Date afterRemovalDate = new Date();
		Date equalsBoundaryDate = getAdjustedDate(afterRemovalDate, EXPECTED_MAX_CART_HISTORY);

		Shopper shopperEqualsRemovalDate = addShoppingCart(equalsBoundaryDate);
		final Store store = storeScenario.getStore();
		ShoppingItem shoppingItem = cartDirectorService.addSkuToWishList(SKU_CODE, shopperEqualsRemovalDate, store);

		int removed = shoppingCartCleanupService.deleteAbandonedShoppingCarts(equalsBoundaryDate, 1);
		assertEquals("There should have been one shopping cart removed.", 1, removed);

		WishList actualWishList = wishListService.findOrCreateWishListByShopper(shopperEqualsRemovalDate);
		assertFalse("This favorites should not be null.", actualWishList == null);

		assertEquals("This favorites should have the same amount of items as previously held.", actualWishList.getAllItems().size(), 1);

		assertEquals("This favorites should contain the same product sku.",
				actualWishList.getAllItems().get(0).getSkuGuid(),
				shoppingItem.getSkuGuid());
	}

	/**
	 * Test that the the batch size limits the number of inactive carts.
	 */
	@DirtiesDatabase
	@Test
	public void testShouldRemovePreDefinedNumberOfInactiveCarts() {
		Shopper shopperWithActiveCart = createShoppingCartWithStatus(ShoppingCartStatus.ACTIVE);
		Shopper shopperWithInactiveCart = createShoppingCartWithStatus(ShoppingCartStatus.INACTIVE);

		int removed = shoppingCartCleanupService.deleteInactiveShoppingCarts(1);
		assertEquals("There should have been one shopping cart removed.", 1, removed);

		boolean inactiveCartExists = shoppingCartService.shoppingCartExists(shopperWithInactiveCart.getCurrentShoppingCart().getGuid());
		assertFalse("Inactive cart must not exist.", inactiveCartExists);

		boolean activeCartExists = shoppingCartService.shoppingCartExists(shopperWithActiveCart.getCurrentShoppingCart().getGuid());
		assertTrue("Active cart must exist.", activeCartExists);
	}

	/**
	 * Test that if an inactive shopping cart is removed, that it cascades to remove dependent shopping cart items as well.
	 */
	@DirtiesDatabase
	@Test
	public void testShouldRemoveInactiveCartsAndShoppingItems() {

		Shopper shopperWithInactiveCart = createShoppingCartWithStatus(ShoppingCartStatus.INACTIVE);
		ShoppingItemDto dto = new ShoppingItemDto(SKU_CODE, 1);
		ShoppingItem shoppingItem = cartDirectorService.addItemToCart(shopperWithInactiveCart.getCurrentShoppingCart(), dto);

		int removed = shoppingCartCleanupService.deleteInactiveShoppingCarts(1);
		assertEquals("There should have been one shopping cart removed.", 1, removed);

		boolean inactiveCartExists = shoppingCartService.shoppingCartExists(shopperWithInactiveCart.getCurrentShoppingCart().getGuid());
		assertFalse("Inactive cart must not exist.", inactiveCartExists);

		ShoppingItem actualShoppingItem = shoppingItemService.findByGuid(shoppingItem.getGuid(), null);
		assertNull("This shopping item should have been cascade deleted when the shopping cart was removed.", actualShoppingItem);
	}

	/**
	 * Test that if an inactive shopping cart is removed, that wish list items can still be accessed by shopper.
	 */
	@DirtiesDatabase
	@Test
	public void testShopperWishListsShouldNotBeAffectedByRemovalOfInactiveCarts() {

		Shopper shopperWithInactiveCart = createShoppingCartWithStatus(ShoppingCartStatus.INACTIVE);

		final Store store = storeScenario.getStore();
		ShoppingItem shoppingItem = cartDirectorService.addSkuToWishList(SKU_CODE, shopperWithInactiveCart, store);

		int removed = shoppingCartCleanupService.deleteInactiveShoppingCarts(1);
		assertEquals("There should have been one shopping cart removed.", 1, removed);

		WishList actualWishList = wishListService.findOrCreateWishListByShopper(shopperWithInactiveCart);
		assertNotNull("This favorites should not be null.", actualWishList);

		assertEquals("This favorites should have the same amount of items as previously held.", actualWishList.getAllItems().size(), 1);

		assertEquals("This favorites should contain the same product sku.",
			actualWishList.getAllItems().get(0).getSkuGuid(),
			shoppingItem.getSkuGuid());
	}

	private Date getAdjustedDate(final Date now, final int adjustment) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(now);
		calendar.add(Calendar.DAY_OF_YEAR, - adjustment);
		return calendar.getTime();
	}

	private Shopper addShoppingCart(final Date lastModifiedDate) {
		mockTimeService.setCurrentTime(lastModifiedDate);

		try {
			final Shopper shopper = shopperService.createAndSaveShopper(storeScenario.getStore().getCode());
			ShoppingCart cart = createShoppingCart(shopper);
			ShoppingCart updatedCart = shoppingCartService.saveOrUpdate(cart);
			shopper.setCurrentShoppingCart(updatedCart);

			return shopper;
		} finally {
			mockTimeService.setCurrentTime(new Date());
		}
	}

	private Shopper createShoppingCartWithStatus(final ShoppingCartStatus cartStatus) {
		final Shopper shopper = shopperService.createAndSaveShopper(storeScenario.getStore().getCode());
		ShoppingCart cart = createShoppingCart(shopper, cartStatus);
		ShoppingCart updatedCart = shoppingCartService.saveOrUpdate(cart);
		shopper.setCurrentShoppingCart(updatedCart);

		return shopper;
	}

	/**
	 * Create a non-persistent {@link ShoppingCart} tied to the default store.
	 *
	 * @return the shopping cart
	 */
	private ShoppingCart createShoppingCart(final Shopper shopper) {

		return createShoppingCart(shopper, ShoppingCartStatus.ACTIVE);
	}

	private ShoppingCart createShoppingCart(final Shopper shopper, final ShoppingCartStatus cartStatus) {
		Currency currency = Currency.getInstance("USD");

		CustomerSession customerSession = customerSessionService.createWithShopper(shopper);
		customerSession.setCurrency(currency);
		customerSession.setLocale(Locale.ENGLISH);
		customerSession = customerSessionService
				.initializeCustomerSessionForPricing(customerSession, storeScenario.getStore().getCode(), currency);

		final ShoppingCart shoppingCart = shoppingCartService.findOrCreateByCustomerSession(customerSession);
		shoppingCart.setStore(storeScenario.getStore());

		final ShoppingCartMemento memento = ((ShoppingCartMementoHolder)shoppingCart).getShoppingCartMemento();
		memento.setGuid(Utils.uniqueCode("CART-"));
		memento.setStatus(cartStatus);

		shopper.setCurrentShoppingCart(shoppingCart);
		return shoppingCart;
	}

}
