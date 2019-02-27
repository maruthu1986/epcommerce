/*
 * Copyright (c) Elastic Path Software Inc., 2006
 *
 */
package com.elasticpath.test.jmock;

import static java.util.Arrays.asList;

import static com.elasticpath.domain.misc.impl.DisplayNameComparatorImplTest.LOCALE;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.jmock.Expectations;
import org.jmock.api.Action;
import org.jmock.api.Invocation;
import org.jmock.auto.Mock;
import org.junit.Before;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.catalog.AvailabilityCriteria;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.DefaultValueRemovalForbiddenException;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.PriceTier;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.catalog.impl.CatalogImpl;
import com.elasticpath.domain.catalog.impl.CatalogLocaleImpl;
import com.elasticpath.domain.catalog.impl.CategoryImpl;
import com.elasticpath.domain.catalog.impl.PriceImpl;
import com.elasticpath.domain.catalog.impl.PriceTierImpl;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.catalog.impl.ProductTypeImpl;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.customer.impl.CustomerAddressImpl;
import com.elasticpath.domain.customer.impl.CustomerImpl;
import com.elasticpath.domain.misc.impl.LocalizedPropertiesImpl;
import com.elasticpath.domain.misc.impl.RandomGuidImpl;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.impl.OrderSkuImpl;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.impl.ShoppingCartImpl;
import com.elasticpath.domain.shoppingcart.impl.ShoppingItemImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.domain.tax.impl.TaxCodeImpl;
import com.elasticpath.money.Money;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.shoppingcart.BundleApportioningCalculator;
import com.elasticpath.service.shoppingcart.ShoppingItemSubtotalCalculator;
import com.elasticpath.service.shoppingcart.impl.ItemPricing;
import com.elasticpath.service.shoppingcart.impl.OrderSkuFactoryImpl;
import com.elasticpath.service.tax.DiscountApportioningCalculator;
import com.elasticpath.service.tax.TaxCodeRetriever;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;
import com.elasticpath.shipping.connectivity.dto.impl.ShippingOptionImpl;
import com.elasticpath.test.factory.TestCustomerSessionFactory;
import com.elasticpath.test.factory.TestShopperFactory;
import com.elasticpath.test.factory.TestShoppingCartFactory;

/**
 * This class has the ability to create several interlinked catalog-related mock objects
 * used by several test cases, this class should not be over-used as it previously became
 * a dumping ground, instead please write more specific test cases that mock specifically
 * what theg test really needs.
 */
@SuppressWarnings({ "PMD.ExcessiveImports", "PMD.CouplingBetweenObjects", "PMD.GodClass" })
public abstract class AbstractCatalogDataTestCase extends AbstractEPServiceTestCase {

	/**
	 * GUID of the sku of cart item 1.
	 */
	protected static final String SKU_GUID_1 = "Sku1";

	/**
	 * GUID of the sku of cart item 2.
	 */
	private static final String SKU_GUID_2 = "Sku2";

	/**
	 * UID of the first product.
	 */
	protected static final long PRODUCT_UID_1 = 123;

	/**
	 * UID of the second product.
	 */
	private static final long PRODUCT_UID_2 = 456;

	/**
	 * Sales tax code associated with the product type of cart item 1.
	 */
	protected static final String SALES_TAX_CODE_BOOKS = "BOOKS";

	/**
	 * Sales tax code associated with the product type of cart item 2.
	 */
	protected static final String SALES_TAX_CODE_DVDS = "DVDS";

	/**
	 * The primary currency in use by this test.
	 */
	protected static final Currency CURRENCY = Currency.getInstance("CAD");

	/**
	 * The initially selected shipping option set by {@link #updateShippingOptions(ShoppingCartImpl)}.
	 */
	protected static final String SELECTED_SHIPPING_OPTION_CODE = "1000";

	private static final String CARTITEM_PRICE_5 = "5";

	private static final String CARTITEM_PRICE_10 = "10";

	private static final int CARTITEM_QTY_3 = 3;

	private static final int CARTITEM_QTY_5 = 5;

	@Mock private ProductService productService;
	@Mock private ProductSkuLookup productSkuLookup;
	@Mock private BundleApportioningCalculator bundleApportioningCalculator;
	@Mock private DiscountApportioningCalculator discountApportioningCalculator;
	@Mock private ShoppingItemSubtotalCalculator shoppingItemSubtotalCalculator;
	@Mock private TimeService timeService;

	private Catalog masterCatalog;

	private int productTypeMockCounter;

	private List<ProductSku> cartSkus;
	private final Map<String, ProductSku> productSkuLookupGuidToSkuMap = new HashMap<>();

	/**
	 * Sets up standard beans in the bean factory as well as the {@link com.elasticpath.service.shoppingcart.OrderSkuFactory}.
	 *
	 * @throws Exception if something goes wrong during set up.
	 */
	@Before
	public void setUp() throws Exception {
		super.setUp();

		stubGetBean(ContextIdNames.CATALOG_LOCALE, CatalogLocaleImpl.class);
		stubGetBean(ContextIdNames.LOCALIZED_PROPERTIES, LocalizedPropertiesImpl.class);
		stubGetBean(ContextIdNames.ORDER_SKU, OrderSkuImpl.class);
		stubGetBean(ContextIdNames.PRODUCT_SKU_LOOKUP, getProductSkuLookup());
		stubGetBean(ContextIdNames.SHOPPING_ITEM_SUBTOTAL_CALCULATOR, getShoppingItemSubtotalCalculator());
		stubGetBean(ContextIdNames.TIME_SERVICE, getTimeService());

		mockTimeService();
		mockOrderSkuFactory();
		mockProductSkuLookup();
	}

	@SuppressWarnings({"unchecked"})
	private void mockProductSkuLookup() {
		context.checking(new Expectations() {
			{
				allowing(productSkuLookup).findByGuid(with(any(String.class)));
				will(new Action() {
					@Override
					public void describeTo(final Description description) {
						description.appendText("Lookup productSku based on guid.");
					}

					@Override
					public Object invoke(final Invocation invocation) throws Throwable {
						return productSkuLookupGuidToSkuMap.get(invocation.getParameter(0));
					}
				});
				allowing(productSkuLookup).findByGuids(with(any(Collection.class)));
				will(new Action() {
					@Override
					public void describeTo(final Description description) {
						description.appendText("Lookup productSku based on guid.");
					}

					@Override
					public Object invoke(final Invocation invocation) throws Throwable {
						List<ProductSku> results = new ArrayList<>();
						for (String guid : (Collection<String>) invocation.getParameter(0)) {
							results.add(productSkuLookupGuidToSkuMap.get(guid));
						}
						return results;
					}
				});
			}
		});
	}

	protected List<ProductSku> getCartSkus() {
		return this.cartSkus;
	}
	/**
	 * A non-shippable ProductSku, the second one created by #addCartItemsTo().
	 * @return the a non-shippable ProductSku - the first one added to cart.
	 */
	protected ProductSku getNonShippableSku() {
		return getCartSkus().get(0);
	}

	/**
	 * A shippable ProductSku, created by #addCartItemsTo().
	 * @return the a shippable ProductSku - the second one added to cart.
	 */
	protected ProductSku getShippableSku() {
		return getCartSkus().get(1);
	}

	/**
	 * Returns the default mocked store.
	 *
	 * @return the default mocked store.
	 */
	@Override
	protected Store getMockedStore() {
		Store store = super.getMockedStore();
		store.setCatalog(this.getCatalog());
		return store;
	}

	/**
	 * Returns a new <code>ProductSku</code> instance.
	 *
	 * @return a new <code>ProductSku</code> instance.
	 */
	protected ProductSku getProductSku() {
		final ProductSku productSku = new ProductSkuImpl();
		String skuCode = new RandomGuidImpl().toString();
		productSku.setGuid(new RandomGuidImpl().toString());
		productSku.setSkuCode(skuCode);
		productSku.initialize();
		productSku.setProduct(this.getProduct());

		productSkuLookupGuidToSkuMap.put(productSku.getGuid(), productSku);
		return productSku;
	}


	/**
	 * Returns a new <code>Price</code> instance.
	 *
	 * @return a new <code>Price</code> instance.
	 */
	private Price getPrice() {
		// PriceImpl price = new PriceImpl();
		PriceImpl price = new PriceImpl();
		price.addOrUpdatePriceTier(getPriceTier());
		price.setCurrency(Currency.getInstance(Locale.CANADA));
		price.initialize();
		return price;
	}

	/**
	 * Returns a new <code>PriceTier</code> instance.
	 *
	 * @return a new <code>PriceTier</code> instance.
	 */
	private PriceTier getPriceTier() {
		PriceTier priceTier = new PriceTierImpl();
		priceTier.initialize();
		return priceTier;
	}


	/**
	 * Returns a new <code>Category</code> instance.
	 *
	 * @return a new <code>Category</code> instance.
	 */
	protected Category getCategory() {
		final Category category = new CategoryImpl();
		category.initialize();
		category.setCode(new RandomGuidImpl().toString());
		category.setCatalog(getCatalog());

		return category;
	}

	/**
	 * Returns a new <code>Product</code> instance.
	 *
	 * @return a new <code>Product</code> instance.
	 */
	protected Product getProduct() {
		final Product product = newProductImpl();
		product.initialize();
		product.setCode(new RandomGuidImpl().toString());
		final ProductType mock = context.mock(ProductType.class, "productType " + productTypeMockCounter++);
		final TaxCodeImpl taxCodeImpl = new TaxCodeImpl();
		taxCodeImpl.setCode(SALES_TAX_CODE_BOOKS);
		taxCodeImpl.setGuid(SALES_TAX_CODE_BOOKS);
		context.checking(new Expectations() {
			{
				allowing(mock).getTaxCode();
				will(returnValue(taxCodeImpl));

				allowing(mock).isExcludedFromDiscount();
				will(returnValue(Boolean.FALSE.booleanValue()));
			}
		});
		ProductType productType = mock;
		product.setProductType(productType);

		final Category category = getCategory();
		product.addCategory(category);
		product.setCategoryAsDefault(category);

		context.checking(new Expectations() {
			{
				allowing(getProductService()).isInCategory(product, category.getCompoundGuid());
				will(returnValue(true));

				allowing(getProductService()).isInCategory(with(equal(product)), with(any(String.class)));
				will(returnValue(false));
			}
		});

		return product;
	}

	/**
	 * Override to change behaviour of product.
	 * @return new instance of productImpl
	 */
	protected Product newProductImpl() {
		return new ProductImpl() {
			private static final long serialVersionUID = 5000000001L;

			@Override
			public String getDisplayName(final Locale locale) {
				return "Test Display Name";
			}
		};
	}

	/**
	 * Returns a standard shoppingCart, contains a customer, shipping/billing addresses, two
	 * items, a selected shipping option and etc.
	 *
	 * @return a standard shoppingCart.
	 */
	protected ShoppingCartImpl getShoppingCart() {
		// By default the items added to cart is $65.00 so we'll use that, but some tests override this, hence why we call a parameterized method.
		// Likewise the shippable subtotal by default is $50
		return getShoppingCart(new BigDecimal("50.00"), new BigDecimal("65.00"));
	}

	/**
	 * Returns a standard shoppingCart, contains a customer, shipping/billing addresses, two
	 * items, a selected shipping option and etc from given subtotal calculator supplier.
	 *
	 * @param shippableSubtotal the subtotal for all shippable items
	 * @param cartSubtotal the subtotal for all apportioned leaf items
	 * @return a standard shoppingCart.
	 */
	@SuppressWarnings("unchecked")
	protected ShoppingCartImpl getShoppingCart(final BigDecimal shippableSubtotal, final BigDecimal cartSubtotal) {
		final ShoppingCartImpl shoppingCart = createNewShoppingCart();

		addCartItemsTo(shoppingCart);

		// The cart now contains two cart items:
		// Item 1: 3qty x $5, shippable
		// Item 2: 5qty x $10, non-shippable

		// First set an expectation for just the shippable items in the cart
		mockSubtotalForShippableItems(shippableSubtotal);
		// Next set an expectation for the overall subtotal
		mockSubtotalCalculatorForAllCartItems(shoppingCart, cartSubtotal);

		updateShippingOptions(shoppingCart);

		return shoppingCart;
	}

	/**
	 * Mocks out the shippable subtotal for the given shopping cart.
	 *
	 * @param shippableSubtotal the subtotal for all shippable items
	 */
	protected void mockSubtotalForShippableItems(final BigDecimal shippableSubtotal) {
		context.checking(new Expectations() {
			{
				allowing(shoppingItemSubtotalCalculator).calculate(
						with(aStreamOfShippableShoppingItems()),
						with(any(ShoppingCartPricingSnapshot.class)),
						with(any(Currency.class))
				);
				will(returnValue(Money.valueOf(shippableSubtotal, CURRENCY)));
			}
		});
	}

	/**
	 * Mocks out the overall subtotal for the given shopping cart.
	 *
	 * @param shoppingCart the shopping cart.
	 * @param cartSubtotal the subtotal for all apportioned leaf items
	 */
	protected void mockSubtotalCalculatorForAllCartItems(final ShoppingCart shoppingCart, final BigDecimal cartSubtotal) {
		context.checking(new Expectations() {
			{
				allowing(shoppingItemSubtotalCalculator).calculate(
						with(shoppingCart.getApportionedLeafItems()),
						with(any(ShoppingCartPricingSnapshot.class)),
						with(any(Currency.class))
				);
				will(returnValue(Money.valueOf(cartSubtotal, CURRENCY)));
			}
		});
	}

	/**
	 * Populates available shipping options and selects one.
	 * @param shoppingCart shopping cart
	 */
	protected void updateShippingOptions(final ShoppingCartImpl shoppingCart) {
		// Shipping cost = $5 fixed + 10% of shippable subtotal value
		// = $5 + (10% of (5qty x $10))
		// = $5 + (10% of $50)
		// = $5 + $5
		// = $10

		final ShippingOption selectedShippingOption = createShippingOption(SELECTED_SHIPPING_OPTION_CODE, BigDecimal.TEN);
		shoppingCart.setSelectedShippingOption(selectedShippingOption);

		selectedShippingOption.getShippingCost().ifPresent(
				shippingCost -> shoppingCart.setShippingListPrice(selectedShippingOption.getCode(), shippingCost));
	}

	private ShoppingCartImpl createNewShoppingCart() {
		final Shopper shopper = TestShopperFactory.getInstance().createNewShopperWithMemento();
		final CustomerSession customerSession = createCustomerSessionForShopper(shopper);
		customerSession.setCurrency(CURRENCY);
		customerSession.setLocale(LOCALE);

		final ShoppingCartImpl shoppingCart = TestShoppingCartFactory.getInstance().createNewCartWithMemento(
				customerSession.getShopper(), getMockedStore());
		shoppingCart.setCustomerSession(customerSession);
		shoppingCart.setBillingAddress(getAddress());
		shoppingCart.setShippingAddress(getAddress());
		return shoppingCart;
	}

	private CustomerSession createCustomerSessionForShopper(final Shopper shopper) {
		final CustomerSession customerSession = TestCustomerSessionFactory.getInstance().createNewCustomerSessionWithContext(shopper);
		final Customer customer = createCustomer();
		customerSession.getShopper().setCustomer(customer);
		return customerSession;
	}

	private Customer createCustomer() {
		return new CustomerImpl() {
			private static final long serialVersionUID = 5085646833754066257L;

			@Override
			public String getEmail() {
				return "Joe.Doe@elasticpath.com";
			}
		};
	}

	/**
	 * Get two cart items - qty 3 of $5.00 each, and qty 5 of $10.00 each.
	 */
	@SuppressWarnings("unchecked")
	private List<ShoppingItem> addCartItemsTo(final ShoppingCart shoppingCart) {

		context.checking(new Expectations() {
			{
				allowing(bundleApportioningCalculator).apportion(with(any(ItemPricing.class)), with(any(Map.class)));
				will(returnValue(Collections.emptyMap()));
				allowing(discountApportioningCalculator).calculateApportionedAmounts(with(any(BigDecimal.class)), with(any(Map.class)));
				will(returnValue(Collections.emptyMap()));
			}
		});

		final ShoppingItem cartItem1 = new ShoppingItemImpl();
		final ProductSku nonShippableSku = getProductSku();
		populateNonShippableSku(cartItem1, nonShippableSku);

		shoppingCart.addCartItem(cartItem1);

		final ShoppingItem cartItem2 = new ShoppingItemImpl();
		final ProductSku shippableSku = getProductSku();
		populateShippableSku(cartItem2, shippableSku);

		shoppingCart.addCartItem(cartItem2);

		cartSkus = asList(nonShippableSku, shippableSku);

		return new ArrayList<>(asList(cartItem1, cartItem2));
	}

	private void populateNonShippableSku(final ShoppingItem cartItem, final ProductSku nonShippableSku) {
		cartItem.setGuid(new RandomGuidImpl().toString());
		cartItem.setUidPk(Calendar.getInstance().getTimeInMillis());
		nonShippableSku.initialize();
		nonShippableSku.setUidPk(Calendar.getInstance().getTimeInMillis());
		nonShippableSku.setSkuCode(SKU_GUID_1);
		nonShippableSku.setWeight(BigDecimal.ONE);
		nonShippableSku.setDigital(true);
		nonShippableSku.setShippable(false);

		productSkuLookupGuidToSkuMap.put(nonShippableSku.getGuid(), nonShippableSku);

		final Product product = getProduct();
		product.setUidPk(PRODUCT_UID_1);
		product.setProductSkus(new HashMap<>());

		final TaxCode taxCode = new TaxCodeImpl();
		taxCode.setCode(SALES_TAX_CODE_BOOKS);
		taxCode.setGuid(SALES_TAX_CODE_BOOKS);

		final ProductTypeImpl productType = new ProductTypeImpl();
		productType.setTaxCode(taxCode);
		product.setProductType(productType);
		product.setAvailabilityCriteria(AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK);
		nonShippableSku.setProduct(product);
		product.addOrUpdateSku(nonShippableSku);

		final Money price = Money.valueOf(new BigDecimal(CARTITEM_PRICE_5), CURRENCY);
		Price productSkuPrice = getPrice();

		productSkuPrice.setCurrency(CURRENCY);
		productSkuPrice.setListPrice(price);
		productSkuPrice.setSalePrice(price);

		cartItem.setSkuGuid(nonShippableSku.getGuid());
		cartItem.setPrice(CARTITEM_QTY_3, productSkuPrice);
	}

	private void populateShippableSku(final ShoppingItem cartItem, final ProductSku shippableSku) {
		cartItem.setUidPk(Calendar.getInstance().getTimeInMillis() / 2);
		cartItem.setGuid(new RandomGuidImpl().toString());

		shippableSku.initialize();
		shippableSku.setUidPk(Calendar.getInstance().getTimeInMillis());
		shippableSku.setSkuCode(SKU_GUID_2);
		shippableSku.setDigital(false);
		shippableSku.setShippable(true);
		productSkuLookupGuidToSkuMap.put(shippableSku.getGuid(), shippableSku);

		final Product product = getProduct();
		product.setUidPk(PRODUCT_UID_2);
		product.setProductSkus(new HashMap<>());
		product.setAvailabilityCriteria(AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK);

		final TaxCode taxCode = new TaxCodeImpl();
		taxCode.setCode(SALES_TAX_CODE_DVDS);
		taxCode.setGuid(SALES_TAX_CODE_DVDS);

		final ProductTypeImpl productType = new ProductTypeImpl();
		productType.setTaxCode(taxCode);
		product.setProductType(productType);
		shippableSku.setProduct(product);
		product.addOrUpdateSku(shippableSku);

		final Money price = Money.valueOf(new BigDecimal(CARTITEM_PRICE_10), CURRENCY);
		final Price productSkuPrice;

		productSkuPrice = getPrice();
		productSkuPrice.setCurrency(CURRENCY);
		productSkuPrice.setListPrice(price);
		productSkuPrice.setSalePrice(price);

		cartItem.setSkuGuid(shippableSku.getGuid());
		cartItem.setPrice(CARTITEM_QTY_5, productSkuPrice);
	}

	/**
	 * Returns a newly created address.
	 *
	 * @return a newly created address
	 */
	protected Address getAddress() {
		Address address = new CustomerAddressImpl();
		address.setFirstName("Joe");
		address.setLastName("Doe");
		address.setCountry("US");
		address.setStreet1("1295 Charleston Road");
		address.setCity("Mountain View");
		address.setSubCountry("CA");
		address.setZipOrPostalCode("94043");

		return address;
	}

	/**
	 * Returns a newly created {@link ShippingOption} with given shipping option code.
	 *
	 * @param code shipping option code
	 * @return newly created builder
	 */
	protected ShippingOption createShippingOption(final String code) {
		return createShippingOption(code, (Money) null);
	}

	/**
	 * Returns a newly created {@link ShippingOption} with given shipping option code and shipping cost.
	 *
	 * @param code shipping option code
	 * @param shippingCost the shipping cost must not be null
	 * @return newly created builder
	 */
	protected ShippingOption createShippingOption(final String code, final BigDecimal shippingCost) {
		return createShippingOption(code, Money.valueOf(shippingCost, CURRENCY));
	}

	/**
	 * Returns a newly created {@link ShippingOption} with given shipping option code and an optional shipping cost.
	 *
	 * @param code shipping option code
	 * @param shippingCost the shipping cost.
	 * @return newly created builder
	 */
	protected ShippingOption createShippingOption(final String code, final Money shippingCost) {

		final ShippingOptionImpl shippingOption = new ShippingOptionImpl();
		shippingOption.setCode(code);
		shippingOption.setCarrierCode("Fed Ex");

		if (shippingCost != null) {
			shippingOption.setShippingCost(shippingCost);
		}

		return shippingOption;
	}

	/**
	 * Gets the master catalog singleton.
	 *
	 * @return the master catalog singleton
	 */
	protected Catalog getCatalog() {
		if (masterCatalog == null) {
			masterCatalog = new CatalogImpl();
			masterCatalog.setMaster(true);
			masterCatalog.setCode("a master catalog code that no one would ever think of");
		}

		try {
			// Supported Currencies
			final Set<Currency> supportedCatalogCurrencies = new HashSet<>();
			supportedCatalogCurrencies.add(Currency.getInstance(Locale.CANADA));
			supportedCatalogCurrencies.add(Currency.getInstance(Locale.US));

			// Supported Locales
			final Set<Locale> supportedLocales = new HashSet<>();
			supportedLocales.add(Locale.CANADA);
			supportedLocales.add(Locale.CANADA_FRENCH); // en_fr
			supportedLocales.add(Locale.US); //us
			masterCatalog.setSupportedLocales(supportedLocales);
			masterCatalog.setDefaultLocale(Locale.CANADA);
		} catch (DefaultValueRemovalForbiddenException ex) {
			throw new EpDomainException("Default locale not set, so this shouldn't happen", ex);
		}

		return masterCatalog;
	}

	/**
	 * Mocks the {@link TimeService} interface to return the current local time.
	 */
	protected void mockTimeService() {
		context.checking(new Expectations() {
			{
				allowing(timeService).getCurrentTime();
				will(returnValue(new Date()));
			}
		});
	}

	/**
	 * Mocks the OrderSku factory with the {@link #SALES_TAX_CODE_BOOKS} tax code.
	 */
	protected void mockOrderSkuFactory() {
		final TaxCodeImpl taxCode = new TaxCodeImpl();
		taxCode.setCode(SALES_TAX_CODE_BOOKS);

		final TaxCodeRetriever taxCodeRetriever = context.mock(TaxCodeRetriever.class);
		context.checking(new Expectations() {
			{
				allowing(taxCodeRetriever).getEffectiveTaxCode(with(any(ProductSku.class)));
				will(returnValue(taxCode));
			}
		});

		OrderSkuFactoryImpl orderSkuFactory = new OrderSkuFactoryImpl() {
			@Override
			protected void copyFields(final ShoppingItem shoppingItem, final OrderSku orderSku, final Locale locale) {
				super.copyFields(shoppingItem, orderSku, locale);

				orderSku.setGuid("OrderSku-" + shoppingItem.getGuid());
			}
		};

		orderSkuFactory.setBeanFactory(getBeanFactory());
		orderSkuFactory.setTaxCodeRetriever(taxCodeRetriever);
		orderSkuFactory.setBundleApportioner(getBundleApportioningCalculator());
		orderSkuFactory.setDiscountApportioner(getDiscountApportioningCalculator());
		orderSkuFactory.setProductSkuLookup(getProductSkuLookup());
		orderSkuFactory.setTimeService(getTimeService());

		stubGetBean(ContextIdNames.ORDER_SKU_FACTORY, orderSkuFactory);
	}

	/**
	 * Mocks a ProductSku response from the productSkuLookup service for findByGuid and findByGuids calls.
	 *
	 * @param productSkuGuid the product sku guid
	 * @param productSku the product sku
	 */
	protected void mockProductSkuLookupByGuid(final String productSkuGuid, final ProductSku productSku) {
		productSkuLookupGuidToSkuMap.put(productSkuGuid, productSku);
	}

	/**
	 * A matcher that ensures that the given argument is a {code Stream<ShoppingItem>} in which all shopping items are shippable.
	 *
	 * @return a matcher
	 */
	public Matcher<Stream<? extends ShoppingItem>> aStreamOfShippableShoppingItems() {
		return new ShippableStreamMatcher();
	}

	protected ProductSkuLookup getProductSkuLookup() {
		return productSkuLookup;
	}

	protected BundleApportioningCalculator getBundleApportioningCalculator() {
		return bundleApportioningCalculator;
	}

	protected DiscountApportioningCalculator getDiscountApportioningCalculator() {
		return discountApportioningCalculator;
	}

	protected ProductService getProductService() {
		return productService;
	}

	protected ShoppingItemSubtotalCalculator getShoppingItemSubtotalCalculator() {
		return shoppingItemSubtotalCalculator;
	}

	protected TimeService getTimeService() {
		return this.timeService;
	}

	/**
	 * A {@link TypeSafeMatcher} implementation which only matches a Stream of {@link ShoppingItem}s if all elements in that stream are shippable.
	 */
	protected class ShippableStreamMatcher extends TypeSafeMatcher<Stream<? extends ShoppingItem>> {
		@Override
		protected boolean matchesSafely(final Stream<? extends ShoppingItem> items) {
			return items.noneMatch(item -> !item.isShippable(getProductSkuLookup()));
		}

		@Override
		public void describeTo(final Description description) {
			description.appendText("A stream of shippable shopping items");
		}
	};
}
