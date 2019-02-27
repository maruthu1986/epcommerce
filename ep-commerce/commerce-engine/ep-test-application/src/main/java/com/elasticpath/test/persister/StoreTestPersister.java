/*
 * Copyright (c) Elastic Path Software Inc., 2016
 */

package com.elasticpath.test.persister;

import static com.elasticpath.commons.constants.EpShippingContextIdNames.SHIPPING_OPTION_TRANSFORMER;
import static com.elasticpath.commons.constants.EpShippingContextIdNames.SHIPPING_REGION;
import static com.elasticpath.commons.constants.EpShippingContextIdNames.SHIPPING_REGION_SERVICE;
import static com.elasticpath.commons.constants.EpShippingContextIdNames.SHIPPING_SERVICE_LEVEL;
import static com.elasticpath.commons.constants.EpShippingContextIdNames.SHIPPING_SERVICE_LEVEL_SERVICE;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.DefaultValueRemovalForbiddenException;
import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.cmuser.impl.CmUserImpl;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerAddress;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.customer.PaymentToken;
import com.elasticpath.domain.customer.impl.PaymentTokenImpl;
import com.elasticpath.domain.factory.TestCustomerSessionFactoryForTestApplication;
import com.elasticpath.domain.factory.TestShopperFactoryForTestApplication;
import com.elasticpath.domain.misc.impl.RandomGuidImpl;
import com.elasticpath.domain.payment.PaymentGateway;
import com.elasticpath.domain.payment.impl.PaymentGatewayImpl;
import com.elasticpath.domain.shipping.ShippingCostCalculationMethod;
import com.elasticpath.domain.shipping.ShippingCostCalculationParameter;
import com.elasticpath.domain.shipping.ShippingCostCalculationParametersEnum;
import com.elasticpath.domain.shipping.ShippingRegion;
import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.domain.shipping.impl.FixedPriceMethodImpl;
import com.elasticpath.domain.shipping.impl.ShippingCostCalculationParameterImpl;
import com.elasticpath.domain.shipping.impl.ShippingRegionImpl;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.StoreState;
import com.elasticpath.domain.store.StoreType;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.domain.store.WarehouseAddress;
import com.elasticpath.domain.store.impl.WarehouseAddressImpl;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.money.Money;
import com.elasticpath.service.cmuser.CmUserService;
import com.elasticpath.service.customer.CustomerService;
import com.elasticpath.service.customer.CustomerSessionService;
import com.elasticpath.service.payment.PaymentGatewayService;
import com.elasticpath.service.shipping.ShippingOptionTransformer;
import com.elasticpath.service.shipping.ShippingRegionService;
import com.elasticpath.service.shipping.ShippingServiceLevelService;
import com.elasticpath.service.shopper.ShopperService;
import com.elasticpath.service.shoppingcart.ShoppingCartService;
import com.elasticpath.service.store.StoreService;
import com.elasticpath.service.store.WarehouseService;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;
import com.elasticpath.test.util.Utils;

/**
 * Persister allows to create and save into database store dependent domain objects.
 */
@SuppressWarnings({ "PMD.ExcessiveParameterList", "PMD.GodClass" })
public class StoreTestPersister {

	private static final int RANDOMIZER_SEED = 1000;

	private static final String USA = "USA";

	private static final int WAREHOUSE_PACK_DELAY = 10;

	private static final String CANADA_POST = "Canada Post";

	private static final String STORE_CODE = "Test Store";

	/**
	 * The default shipping region name to use in tests.
	 */
	public static final String DEFAULT_SHIPPING_REGION_NAME = "Canada";

	private final BeanFactory beanFactory;

	private final WarehouseService warehouseService;

	private final CustomerService customerService;

	private final CustomerSessionService customerSessionService;

	private final ShippingServiceLevelService shippingServiceLevelService;

	private final ShippingOptionTransformer shippingOptionTransformer;

	private final ShopperService shopperService;

	private final ShoppingCartService shoppingCartService;

	private final StoreService storeService;

	private final PaymentGatewayService paymentGatewayService;

	private final ShippingRegionService regionService;

	private final GiftCertificateTestPersister giftCertificateTestPersister;

	private final List<Currency> shippingServiceLevelCurrencies = new ArrayList<>();

	/**
	 * Constructor initializes necessary services and beanFactory.
	 *
	 * @param beanFactory the ElasticPath bean factory
	 */
	public StoreTestPersister(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
		paymentGatewayService = beanFactory.getBean(ContextIdNames.PAYMENT_GATEWAY_SERVICE);
		regionService = beanFactory.getBean(SHIPPING_REGION_SERVICE);
		warehouseService = beanFactory.getBean(ContextIdNames.WAREHOUSE_SERVICE);
		customerService = beanFactory.getBean(ContextIdNames.CUSTOMER_SERVICE);
		customerSessionService = beanFactory.getBean("customerSessionService");
		shopperService = beanFactory.getBean("shopperService");
		shoppingCartService = beanFactory.getBean(ContextIdNames.SHOPPING_CART_SERVICE);
		shippingServiceLevelService = beanFactory.getBean(SHIPPING_SERVICE_LEVEL_SERVICE);
		shippingOptionTransformer = beanFactory.getBean(SHIPPING_OPTION_TRANSFORMER);
		storeService = beanFactory.getBean(ContextIdNames.STORE_SERVICE);

		giftCertificateTestPersister = new GiftCertificateTestPersister(beanFactory);
		//use these to set up cost calculations for shipping service levels
		shippingServiceLevelCurrencies.add(Currency.getInstance("USD"));
		shippingServiceLevelCurrencies.add(Currency.getInstance("CAD"));
		shippingServiceLevelCurrencies.add(Currency.getInstance("GBP"));
	}

	/**
	 * Persists the default store as defined in this class.
	 *
	 * @param catalog   the catalog to associate with the default test store
	 * @param warehouse the warehouse to associate with the default test store
	 * @return The persisted store
	 */
	public Store persistDefaultStore(final Catalog catalog, final Warehouse warehouse) {
		return persistStore(catalog, warehouse, Utils.uniqueCode(STORE_CODE), TestDataPersisterFactory.DEFAULT_CURRENCY_CODE);
	}

	/**
	 * Create a default persisted warehouse.
	 *
	 * @return the warehouse
	 */
	public Warehouse persistDefaultWarehouse() {
		return persistWarehouse(Utils.uniqueCode("warehouse"), "TEST_WAREHOUSE", WAREHOUSE_PACK_DELAY, "Vancouver", "CA", "Boulvard1", "BC", "123");
	}

	/**
	 * Create a persisted warehouse.
	 *
	 * @param warehouseCode      the warehouse code
	 * @param warehouseName      the warehouse name
	 * @param warehousePickDelay the warehouse pick delay
	 * @param city               the city
	 * @param country            the country
	 * @param street1            the street
	 * @param subcountry         the subcountry
	 * @param zip                the zip
	 * @return the warehouse
	 */
	public Warehouse persistWarehouse(final String warehouseCode, final String warehouseName, final int warehousePickDelay, final String city,
			final String country, final String street1, final String subcountry, final String zip) {
		final Warehouse warehouse = beanFactory.getBean(ContextIdNames.WAREHOUSE);
		final WarehouseAddress wAddress = new WarehouseAddressImpl();
		wAddress.setCity(city);
		wAddress.setCountry(country);
		wAddress.setStreet1(street1);
		wAddress.setSubCountry(subcountry);
		wAddress.setZipOrPostalCode(zip);
		warehouse.setAddress(wAddress);
		warehouse.setName(warehouseName);
		warehouse.setCode(warehouseCode);
		warehouse.setPickDelay(warehousePickDelay);
		return warehouseService.saveOrUpdate(warehouse);
	}

	/**
	 * Create a pack of <b>Customers</b>.
	 * <li><b>James Bond</b> <<u>james@bond.com</u>>
	 * <ul>
	 * Country: <b>Canada</b>, State: <b>BC</b>, City: <b>Vancouver</b>, line1: <b>1234 Pine Street</b>, Postal code: <b>8241237</b>, Phone:
	 * <b>891312345007</b></br> Country: <b>Canada</b>, State: <b>ON</b>, City: <b>Toronto</b>, line1: <b>1234 Main Street</b>, Postal code:
	 * <b>8723451</b>, Phone: <b>912348724938</b>
	 * </ul>
	 * </li>
	 * <li><b>John Smith</b> <<u>john@smith.com</u>>
	 * <ul>
	 * Country: <b>USA</b>, State: <b>WA</b>, City: <b>Washington</b>, line1: <b>73 Oak Street</b>, Postal code: <b>832901</b>, Phone:
	 * <b>782390129353</b></br> Country: <b>USA</b>, State: <b>NY</b>, City: <b>New York</b>, line1: <b>11 Peace Street</b>, Postal code:
	 * <b>818923</b>, Phone: <b>728930174927</b>
	 * </ul>
	 * </li>
	 *
	 * @param store Store in which customer is registered
	 */
	public void persistDefaultCustomers(final Store store) {
		final CustomerAddress address1 = createCustomerAddress("Bond", "James", "1234 Pine Street", "", "Vancouver", "CA", "BC", "V6J5G4",
				"891312345007");
		final CustomerAddress address2 = createCustomerAddress("Bond", "James", "28 Main Street", "", "Toronto", "CA", "ON", "K6J5G4",
				"912348724938");
		final PaymentToken paymentToken1 = createCustomerPaymentToken("testPaymentToken1");
		persistCustomerSessionWithAssociatedEntities(persistCustomer(null, store, "james@bond.com", paymentToken1, address1, address2));

		final CustomerAddress address3 = createCustomerAddress("John", "Smith", "73 Oak Street", "", "Washington", "US", "WA", "832901",
				"782390129353");
		final CustomerAddress address4 = createCustomerAddress("John", "Smith", "11 Peace Street", "", "New York", "US", "NY", "818923",
				"728930174927");
		final PaymentToken paymentToken2 = createCustomerPaymentToken("testPaymentToken2");
		persistCustomerSessionWithAssociatedEntities(persistCustomer(null, store, "john@smith.com", paymentToken2, address3, address4));
	}

	/**
	 * <p>Persist default shipping service levels.</p>
	 * <p>
	 * <b>Shipping Regions</b> available in <b>Canada</b>: [CA (AB, BC, MB, NB, NL, NT, NS, NU, ON, PE, QC, SK, YT) ] </br></br>Available
	 * <b>Shipping Service Levels</b> with the fixed price calculation method:
	 * <li>Display name: <b>2 Business Days (Canada Post - Regular Parcel)</b>, carrier: <b>Canada Post</b>, price: <b>7.00</b></li>
	 * <li>Display name: <b>1 Business Day (Canada Post - Xpresspost)</b>, carrier: <b>Canada Post</b>, price: <b>9.30</b></li>
	 * <li>Display name: <b>Next Business Day (Canada Post - Priority Courier)</b>, carrier: <b>Canada Post</b>, price: <b>16.00</b></li>
	 *
	 * @param store the store
	 */
	public void persistDefaultShippingServiceLevels(final Store store) {
		persistShippingServiceLevelFixedPriceCalcMethod(store, DEFAULT_SHIPPING_REGION_NAME, "2 Business Days (Canada Post - Regular Parcel)",
				CANADA_POST, "7.00", null);
		persistShippingServiceLevelFixedPriceCalcMethod(store, DEFAULT_SHIPPING_REGION_NAME, "1 Business Day (Canada Post - Xpresspost)",
				CANADA_POST, "9.30", null);
		persistShippingServiceLevelFixedPriceCalcMethod(store, DEFAULT_SHIPPING_REGION_NAME, "Next Business Day (Canada Post - Priority Courier)",
				CANADA_POST, "16.00", null);
	}

	/**
	 * Persist the default shipping option.
	 *
	 * @param store the store
	 * @return the shipping option
	 */
	public ShippingOption persistDefaultShippingOption(final Store store) {
		final ShippingServiceLevel shippingServiceLevel = persistDefaultShippingServiceLevel(store);
		// fixed shipping price.
		return shippingOptionTransformer.transform(shippingServiceLevel,
				() -> Money.valueOf(new BigDecimal("7.00"), Currency.getInstance(Locale.US)), store.getDefaultLocale());
	}

	/**
	 * Persist the default shipping service level with given locale.
	 *
	 * @param store the store
	 * @return the shipping service level
	 */
	public ShippingServiceLevel persistDefaultShippingServiceLevel(final Store store) {
		return persistShippingServiceLevelFixedPriceCalcMethod(store, DEFAULT_SHIPPING_REGION_NAME,
				"2 Business Days (Canada Post - Regular Parcel)", CANADA_POST, "7.00", null);
	}

	/**
	 * Persist a customer with the given email.
	 *
	 * @param store    the store customer to be registered in
	 * @param email    unique email within the store
	 * @param password customer password
	 * @return persisted customer
	 */
	public Customer persistCustomer(final Store store, final String email, final String password) {
		final Customer customer = beanFactory.getBean(ContextIdNames.CUSTOMER);
		customer.setUserId(Utils.uniqueCode("id"));
		customer.setFirstName("Test");
		customer.setLastName("Test");
		customer.setCreationDate(new Date());
		customer.setLastEditDate(new Date());
		customer.setStatus(Customer.STATUS_ACTIVE);
		customer.setAnonymous(false);
		customer.setGuid(new RandomGuidImpl().toString());
		customer.setEmail(email);
		customer.setStoreCode(store.getCode());
		customer.setClearTextPassword(password);
		return customerService.add(customer);
	}

	/**
	 * Persist a customer with the given email.
	 *
	 * @param store             the store customer to be registered in
	 * @param email             unique email within the store
	 * @param customerAddresses array of available customer addresses
	 * @param paymentToken the payment token.
	 * @return persisted customer
	 */
	public Customer persistCustomer(final String guid, final Store store, final String email, final PaymentToken paymentToken,
									final CustomerAddress... customerAddresses) {
		final Customer customer = beanFactory.getBean(ContextIdNames.CUSTOMER);
		customer.setFirstName("Test");
		customer.setLastName("Test");
		customer.setUserId(Utils.uniqueCode("id"));
		customer.setCreationDate(new Date());
		customer.setLastEditDate(new Date());
		customer.setStatus(Customer.STATUS_ACTIVE);
		customer.setAnonymous(false);
		customer.setGuid(StringUtils.isNotEmpty(guid) ? guid : new RandomGuidImpl().toString());
		customer.setEmail(email);
		customer.setStoreCode(store.getCode());
		customer.setPreferredBillingAddress(customerAddresses[0]);
		for (final CustomerAddress address : customerAddresses) {
			customer.addAddress(address);
		}

		customer.getPaymentMethods().setDefault(createCustomerPaymentToken("test"));
		return customerService.add(customer);
	}
	/**
	 * Create customer payment token payment token.
	 *
	 * @param tokenValue the token value
	 * @return the payment token
	 */
	public PaymentToken createCustomerPaymentToken(final String tokenValue) {
		PaymentTokenImpl.TokenBuilder tokenBuilder = new PaymentTokenImpl.TokenBuilder();
		return tokenBuilder
				.withDisplayValue(tokenValue)
				.withValue(tokenValue)
				.build();
	}

	/**
	 * Creates Customer Address without saving it into database.
	 *
	 * @param lastName  the last name
	 * @param firstName the first name
	 * @param street1   the street line 1
	 * @param street2   the street line 2
	 * @param city      the city
	 * @param country   the country
	 * @param state     the state
	 * @param zip       the zip
	 * @param phone     the phone number
	 * @return the customer address
	 */
	public CustomerAddress createCustomerAddress(final String lastName, final String firstName, final String street1, final String street2,
			final String city, final String country, final String state, final String zip, final String phone) {

		final CustomerAddress customerAddress = beanFactory.getBean(ContextIdNames.CUSTOMER_ADDRESS);
		customerAddress.setFirstName(firstName);
		customerAddress.setLastName(lastName);
		customerAddress.setStreet1(street1);
		customerAddress.setStreet2(street2);
		customerAddress.setCity(city);
		customerAddress.setCountry(country);
		customerAddress.setSubCountry(state);
		customerAddress.setZipOrPostalCode(zip);
		customerAddress.setPhoneNumber(phone);
		return customerAddress;
	}

	/**
	 * Persist the Customer Session.
	 *
	 * @param customer the customer corresponding to the session
	 * @return persisted customerSession
	 */
	public CustomerSession persistCustomerSessionWithAssociatedEntities(final Customer customer) {
		Currency currency = Currency.getInstance(Locale.US);

		final Shopper shopper = TestShopperFactoryForTestApplication.getInstance().createNewShopperWithMemento();
		shopper.setCustomer(customer);
		shopper.setStoreCode(customer.getStoreCode());
		shopperService.save(shopper);
		ShoppingCart shoppingCart = shoppingCartService.findOrCreateByShopper(shopper);

		CustomerSession session = TestCustomerSessionFactoryForTestApplication.getInstance().createNewCustomerSessionWithContext(shopper);
		session.setShopper(shopper);
		session.setCreationDate(new Date());
		session.setCurrency(currency);
		session.setLastAccessedDate(new Date());
		session.setGuid(Utils.uniqueCode("session"));
		session.setLocale(Locale.US);
		customerSessionService.add(session);
		session = customerSessionService.initializeCustomerSessionForPricing(session, customer.getStoreCode(), currency);

		shoppingCart.setCustomerSession(session);

		ShoppingCart persistedShoppingCart = shoppingCartService.saveOrUpdate(shoppingCart);
		shopper.setCurrentShoppingCart(persistedShoppingCart);
		shopperService.save(shopper);

		shopper.updateTransientDataWith(session);
		return session;
	}

	/**
	 * Persist the default CM user.
	 *
	 * @return the persisted CM user
	 */
	public CmUser persistDefaultCmUser() {
		final CmUser cmUser = new CmUserImpl();
		cmUser.setUserName(Utils.uniqueCode("user"));
		cmUser.setEmail(Utils.uniqueEmailAddress("mail"));
		cmUser.setPassword("password");
		cmUser.setEnabled(true);
		cmUser.initialize();
		final CmUserService cmUserService = beanFactory.getBean(ContextIdNames.CMUSER_SERVICE);
		return cmUserService.update(cmUser);
	}

	/**
	 * Get the instance of CmUser which name is "cmuser".
	 * This method avoids generating a mocked cmUser in database for tests.
	 *
	 * @return the instance of CmUser
	 */
	public CmUser getCmUser() {
		final CmUserService cmUserService = beanFactory.getBean(ContextIdNames.CMUSER_SERVICE);
		return cmUserService.findByUserName("cmuser");
	}

	/**
	 * Create shipping cost calculation method with fixed price for shipping service level.
	 * Creates three <code>ShippingCostCalculationParameter</code> on the returned
	 * <code>ShippingCostCalculationMethod</code>, one for each currency USD, GBP and CAD
	 * in <code>shippingServiceLevelCurrencies</code>, to
	 * the satisfy any calling tests that need one or the other.
	 *
	 * @param shippingPriceValue how does the shipping with the fixed price costs
	 * @return shipping cost calculation method
	 */
	public ShippingCostCalculationMethod persistShippingCostFixedPriceCalculationMethod(final String shippingPriceValue) {
		final ShippingCostCalculationMethod method = new FixedPriceMethodImpl();

		final Set<ShippingCostCalculationParameter> params = new HashSet<>();

		for (Currency currency : shippingServiceLevelCurrencies) {
			final ShippingCostCalculationParameterImpl param = new ShippingCostCalculationParameterImpl();
			param.setKey(ShippingCostCalculationParametersEnum.FIXED_PRICE.getKey());
			param.setValue(shippingPriceValue);
			param.setCurrency(currency);
			params.add(param);
		}
		method.setParameters(params);
		return method;
	}

	/**
	 * Create shipping cost calculation method of the specified type, with the specified set of properties.
	 * Creates three <code>ShippingCostCalculationParameter</code> on the returned
	 * <code>ShippingCostCalculationMethod</code>, one for each currency USD, GBP and CAD
	 * in <code>shippingServiceLevelCurrencies</code>, to
	 * the satisfy any calling tests that need one or the other.
	 *
	 * @param methodType calculation method type
	 * @param properties the method's properties
	 * @return in-memory instance of ShippingCostCalculationMethod
	 */
	public ShippingCostCalculationMethod createShippingCostCalculationMethod(final String methodType, final Properties properties) {
		final List<ShippingCostCalculationMethod> allMethods = shippingServiceLevelService.getAllShippingCostCalculationMethods();
		ShippingCostCalculationMethod method = null;
		for (final ShippingCostCalculationMethod shippingCostCalculationMethod : allMethods) {
			if (shippingCostCalculationMethod.getType().equals(methodType)) {
				method = shippingCostCalculationMethod;
				break;
			}
		}
		if (method == null) {
			throw new IllegalStateException("Can't find shipping cost calculation method of the type: " + methodType);
		}

		final Set<ShippingCostCalculationParameter> params = new HashSet<>();
		for (Currency currency : shippingServiceLevelCurrencies) {
			for (final String key : method.getParameterKeys()) {
				final ShippingCostCalculationParameterImpl param = new ShippingCostCalculationParameterImpl();
				param.setKey(key);
				param.setValue((String) properties.get(key));
				param.setCurrency(currency);
				params.add(param);
			}
		}
		method.setParameters(params);

		return method;
	}

	/**
	 * Create persisted shipping service level without any calculation method..
	 *
	 * @param store              the store which provides customers with persisted shipping service level
	 * @param shippingRegionName the name of existed shipping region
	 * @param displayNames       the display names for the store's locales
	 * @param carrier            the carrier String name
	 * @param methodType         shipping cost calculation method
	 * @param properties         properties of the shipping service level
	 * @param code               shipping service level code
	 * @param enabled            if shipping service level is enabled or not
	 * @return persisted shipping service level
	 */
	public ShippingServiceLevel persistShippingServiceLevel(final Store store, final String shippingRegionName,
															final Map<Locale, String> displayNames, final String carrier, final String methodType,
															final Properties properties, final String code, final boolean enabled) {
		final ShippingRegionService shippingRegionService = beanFactory.getBean(SHIPPING_REGION_SERVICE);
		final ShippingRegion shippingRegion = shippingRegionService.findByName(shippingRegionName);
		final ShippingServiceLevel level = beanFactory.getBean(SHIPPING_SERVICE_LEVEL);
		level.setGuid(Utils.uniqueCode("service_level"));
		level.setCarrier(carrier);
		if (code == null) {
			level.setCode(Utils.uniqueCode("SSLCODE"));
		} else {
			level.setCode(code);
		}

		level.setStore(store);
		level.setShippingRegion(shippingRegion);

		for (final Map.Entry<Locale, String> localeEntry : displayNames.entrySet()) {
			level.getLocalizedProperties().setValue(ShippingServiceLevel.LOCALIZED_PROPERTY_NAME, localeEntry.getKey(), localeEntry.getValue());
		}

		level.setShippingCostCalculationMethod(createShippingCostCalculationMethod(methodType, properties));
		level.setEnabled(enabled);
		return shippingServiceLevelService.update(level);
	}

	/**
	 * Create persisted shipping service level for fixed price method only. Code is autogenerated.
	 *
	 * @param store              the store which provides customers with persisted shipping serivce level
	 * @param shippingRegionName the name of existed shipping region
	 * @param displayName        the display name for the store's default locale
	 * @param carrier            the carrier String name
	 * @param shippingPriceValue price value for the default fixed price calculation method.
	 * @return persisted shipping service level
	 */
	public ShippingServiceLevel persistShippingServiceLevelFixedPriceCalcMethod(final Store store, final String shippingRegionName,
																				final String displayName, final String carrier, final String shippingPriceValue) {
		return persistShippingServiceLevelFixedPriceCalcMethod(store, shippingRegionName, displayName, carrier, shippingPriceValue, null);
	}

	/**
	 * Create persisted shipping service level for fixed price method only.
	 *
	 * @param store              the store which provides customers with persisted shipping serivce level
	 * @param shippingRegionName the name of existed shipping region
	 * @param displayName        the display name for the store's default locale
	 * @param carrier            the carrier String name
	 * @param shippingPriceValue price value for the default fixed price calculation method.
	 * @param code               shipping service level code
	 * @return persisted shipping service level
	 */
	public ShippingServiceLevel persistShippingServiceLevelFixedPriceCalcMethod(final Store store, final String shippingRegionName,
																				final String displayName, final String carrier, final String shippingPriceValue, final String code) {

		final ShippingRegionService shippingRegionService = beanFactory.getBean(SHIPPING_REGION_SERVICE);
		final ShippingRegion shippingRegion = shippingRegionService.findByName(shippingRegionName);
		final ShippingServiceLevel level = beanFactory.getBean(SHIPPING_SERVICE_LEVEL);
		level.setGuid(Utils.uniqueCode("service_level"));
		level.setCarrier(carrier);
		if (code == null) {
			level.setCode(Utils.uniqueCode("SSLCODE"));
		} else {
			level.setCode(code);
		}
		level.setStore(store);
		level.setShippingCostCalculationMethod(persistShippingCostFixedPriceCalculationMethod(shippingPriceValue));
		level.setShippingRegion(shippingRegion);
		level.getLocalizedProperties().setValue(ShippingServiceLevel.LOCALIZED_PROPERTY_NAME, store.getDefaultLocale(), displayName);
		level.setEnabled(true);

		return shippingServiceLevelService.add(level);
	}

	/**
	 * Deletes shipping service level identified by code.
	 *
	 * @param code ssl code
	 * @return true if the ssl was removed
	 */
	public boolean deleteShippingService(final String code) {
		final ShippingServiceLevel shippingServiceLevel = shippingServiceLevelService.findByCode(code);
		if (shippingServiceLevel == null) {
			return false;
		}
		shippingServiceLevelService.remove(shippingServiceLevel);
		return true;
	}

	/**
	 * Persist the store.
	 *
	 * @param catalog      the catalog
	 * @param warehouse    the warehouse
	 * @param storeCode    the store code
	 * @param currencyCode the currency code
	 * @return the persisted store
	 */
	public Store persistStore(final Catalog catalog, final Warehouse warehouse, final String storeCode, final String currencyCode) {
		final Store store = persistStore(catalog, warehouse, storeCode, currencyCode, USA, USA,
				Arrays.asList(TestDataPersisterFactory.DEFAULT_LOCALE), "Email Sender", "tests@beanFactory.com",
				Utils.uniqueCode("storename"),
				TimeZone.getDefault(), "storeurl", "email@test.com", "", "UTF-8", true, true, true, true);
		final Set<PaymentGateway> paymentGateways = new HashSet<>();
		paymentGateways.add(persistDefaultPaymentGateway());
		updateStorePaymentGateways(store, paymentGateways);
		return store;

	}

	/**
	 * Creates a persisted store with given catalog, warehouse, store code
	 * store name, and currency code.
	 *
	 * @param catalog      the catalog to set
	 * @param warehouse    the warehouse to set
	 * @param storeCode    the store code to set
	 * @param storeName    the store name to set
	 * @param currencyCode the currency code to set
	 * @return the store if save was successful
	 */
	public Store persistStore(final Catalog catalog, final Warehouse warehouse, final String storeCode, final String storeName,
			final String currencyCode) {
		final Store store = persistStore(catalog, warehouse, storeCode, currencyCode, USA, USA,
				Arrays.asList(TestDataPersisterFactory.DEFAULT_LOCALE), "Email Sender", "tests@beanFactory.com", storeName, TimeZone.getDefault(),
				"storeurl", "email@test.com", "", "UTF-8", true, true, true, true);
		final Set<PaymentGateway> paymentGateways = new HashSet<>();
		paymentGateways.add(persistDefaultPaymentGateway());
		updateStorePaymentGateways(store, paymentGateways);
		return store;

	}

	/**
	 * Create a persisted store with the given catalog and warehouse.
	 * <p>
	 * Stores are in open state for tests.
	 *
	 * @param catalog                           the catalog to use for this store
	 * @param warehouse                         the warehouse to use for this store
	 * @param storeCode                         the store code
	 * @param currencyCode                      the currency code
	 * @param country                           the country
	 * @param subCountry                        the sub country
	 * @param locales                           the locales
	 * @param emailSenderName                   the email sender name
	 * @param emailSenderAddress                the email sender address
	 * @param storeName                         the store name
	 * @param timeZone                          the time zone
	 * @param storeUrl                          the store URL
	 * @param adminEmailAddress                 the admin email address
	 * @param description                       the description
	 * @param contentEncoding                   the content encoding
	 * @param creditCardCvv2Enabled             the ccv enabled flag
	 * @param displayOutOfStock                 the display out of stock flag
	 * @param savingCreditCardWithOrdersEnabled the saving credit card with orders enabled flag
	 * @param enabled                           the store enabled flag
	 * @return the persisted store
	 */
	public Store persistStore(final Catalog catalog, final Warehouse warehouse, final String storeCode,
			final String currencyCode, final String country, final String subCountry, final List<Locale> locales, final String emailSenderName,
			final String emailSenderAddress, final String storeName, final TimeZone timeZone, final String storeUrl,
			final String adminEmailAddress, final String description, final String contentEncoding,
			final boolean creditCardCvv2Enabled, final boolean displayOutOfStock, final boolean savingCreditCardWithOrdersEnabled,
			final boolean enabled) {

		final Store store = beanFactory.getBean("store");
		store.setCatalog(catalog);
		final List<Warehouse> warehouses = new ArrayList<>();
		warehouses.add(warehouse);
		store.setWarehouses(warehouses);
		store.setCode(storeCode);
		store.setCountry(country);
		store.setSubCountry(subCountry);
		store.setDefaultLocale(locales.get(0));
		store.setDefaultCurrency(Currency.getInstance(currencyCode));
		store.setEmailSenderAddress(emailSenderAddress);
		store.setEmailSenderName(emailSenderName);
		store.setName(storeName);
		store.setStoreType(StoreType.B2C);
		store.setTimeZone(timeZone);
		store.setUrl(storeUrl);
		store.setStoreAdminEmailAddress(adminEmailAddress);
		store.setDescription(description);
		store.setContentEncoding(contentEncoding);
		store.setCreditCardCvv2Enabled(creditCardCvv2Enabled);
		store.setDisplayOutOfStock(displayOutOfStock);
		store.setStoreFullCreditCardsEnabled(savingCreditCardWithOrdersEnabled);
		store.setEnabled(enabled);
		final Set<PaymentGateway> paymentGateways = new HashSet<>();
		store.setPaymentGateways(paymentGateways);
		store.setStoreState(StoreState.OPEN);

		final Collection<Currency> supportedCurrencies = new ArrayList<>();
		supportedCurrencies.add(Currency.getInstance(currencyCode));
		try {
			store.setSupportedCurrencies(supportedCurrencies);
		} catch (final DefaultValueRemovalForbiddenException e) {
		}

		try {
			store.setSupportedLocales(locales);
		} catch (final DefaultValueRemovalForbiddenException e) {
		}
		return storeService.saveOrUpdate(store);
	}

	/**
	 * Updated the specified store with the associated tax codes and persists it to the database.
	 *
	 * @param store    to be updated with the set tax codes
	 * @param taxCodes to associate with the specified store
	 * @return the store that has been persisted
	 */
	public Store updateStoreTaxCodes(final Store store, final Set<TaxCode> taxCodes) {
		store.setTaxCodes(taxCodes);
		return storeService.saveOrUpdate(store);
	}

	/**
	 * Update the store Payment gateways.
	 *
	 * @param store           the store
	 * @param paymentGateways the payment gateways
	 * @return the updated store with the payment gateways
	 */
	public Store updateStorePaymentGateways(final Store store, final Set<PaymentGateway> paymentGateways) {
		store.setPaymentGateways(paymentGateways);
		return storeService.saveOrUpdate(store);
	}

	/**
	 * Update store currency.
	 *
	 * @param store        the store
	 * @param currencyCode the currency code
	 * @return the store with the currency code updated
	 */
	public Store updateStoreCurrency(final Store store, final String currencyCode) {
		store.setDefaultCurrency(Currency.getInstance(currencyCode));
		return storeService.saveOrUpdate(store);
	}

	/**
	 * Persists null payment gateway of the arbitrary name.
	 *
	 * @return PaymentGateway
	 */
	public PaymentGateway persistDefaultPaymentGateway() {
		final PaymentGateway paymentGateway = new PaymentGatewayImpl();
		paymentGateway.setType("paymentGatewayNull");
		paymentGateway.setName(Utils.uniqueCode("NullPaymentGateway"));

		return paymentGatewayService.saveOrUpdate(paymentGateway);
	}

	/**
	 * Persists null payment gateway of the arbitrary name.
	 *
	 * @return PaymentGateway
	 */
	public PaymentGateway persistGiftCertificatePaymentGateway() {
		final PaymentGateway paymentGateway = new PaymentGatewayImpl();
		paymentGateway.setType("paymentGatewayGiftCertificate");
		paymentGateway.setName("paymentGatewayGiftCertificate");

		return paymentGatewayService.saveOrUpdate(paymentGateway);
	}

	/**
	 * Create persisted payment gateway for CyberSource.
	 *
	 * @param propertiesMap the properties map
	 * @return persisted paymentGateway
	 */
	public PaymentGateway persistCyberSourcePaymentGateway(final Properties propertiesMap) {
		final PaymentGateway paymentgateway = new PaymentGatewayImpl();
		paymentgateway.setType("paymentGatewayCybersource");
		paymentgateway.setName(Utils.uniqueCode("CyberSource PaymentGateway"));
		paymentgateway.setProperties(propertiesMap);
		return paymentGatewayService.saveOrUpdate(paymentgateway);
	}

	/**
	 * Find the shipping region by its name.
	 *
	 * @param shippingRegionName the shipping region name
	 * @return shippingRegion instance
	 */
	public ShippingRegion getShippingRegion(final String shippingRegionName) {
		return regionService.findByName(shippingRegionName);
	}

	/**
	 * Create default persisted customer.
	 *
	 * @param store the store
	 * @return a customer
	 */
	public Customer createDefaultCustomer(final Store store) {
		final CustomerAddress address = createCustomerAddress(
				"Bond", "James", "1234 Pine Street", "", "Vancouver", "CA", "BC", "V6JT2N", "891312345007");
		return createCustomerWithAddress(null, store, address);
	}

	/**
	 * Create default persisted customer.
	 *
	 * @param store the store
	 * @return a customer
	 */
	public Customer createCustomerWithAddress(final String customerGuid, final Store store, final CustomerAddress customerAddress) {
		final PaymentToken paymentToken = createCustomerPaymentToken("");
		Customer customer = null;
		String randomEmail = null;
		//if there is already a customer with the 'random' email the persistCustomer call will fail,
		//so lets be sure the customer doesnt exist already.
		do {
			randomEmail = "test" + Math.round(Math.random() * RANDOMIZER_SEED) + "@elasticpath.com";
			customer = customerService.findByEmail(randomEmail, store.getCode());
		}
		while (customer != null);

		return persistCustomer(customerGuid, store, randomEmail, paymentToken, customerAddress);
	}

	public Customer getCustomerByGuid(final String customerGuid) {
		return customerService.findByGuid(customerGuid);
	}

	/**
	 * Create persisted gift certificate with specified <code>store</code> and purchase amount.
	 * GC code, theme, and currency are defaulted.
	 *
	 * @param store          in which this certificate can be used
	 * @param purchaseAmount the purchase amount of persisted certificate
	 * @return persisted gift certificate
	 * @deprecated Use {@link GiftCertificateTestPersister#persistGiftCertificate(Store, String, String, String,
	 * BigDecimal, String, String, String, Customer, String, Date, Date, String, String)}
	 */
	@Deprecated
	public GiftCertificate persistGiftCertificate(final Store store, final BigDecimal purchaseAmount) {
		return giftCertificateTestPersister.persistGiftCertificate(
				store, null, Utils.uniqueCode("GCATLEAST18CHARS"), "USD", purchaseAmount,
				null, null, "SomeTheme", null, null, new Date(), null, null, null);
	}

	/**
	 * Create persisted gift certificate with specified <code>store</code>, GC code,
	 * and purchase amount in specified currency.
	 * GC theme is defaulted.
	 *
	 * @param gcCode         the GC's Code
	 * @param store          the store in which the GC is purchased
	 * @param currencyCode   the GC's currency code
	 * @param purchaseAmount the purchase amount of persisted certificate
	 * @return persisted gift certificate
	 * @deprecated Use {@link GiftCertificateTestPersister#persistGiftCertificate(Store, String, String, String,
	 * BigDecimal, String, String, String, Customer, String, Date, Date, String, String)}
	 */
	@Deprecated
	public GiftCertificate persistGiftCertificate(final Store store, final String gcCode, final String currencyCode,
			final BigDecimal purchaseAmount) {
		return giftCertificateTestPersister.persistGiftCertificate(
				store, null, gcCode, currencyCode, purchaseAmount, null,
				null, "SomeTheme", null, null, new Date(), null, null, null);
	}

	/**
	 * Create persisted gift certificate with specified <code>store</code>, GC code,
	 * purchase amount in specified <code>currency</code>, and creation date.
	 * GC theme is defaulted.
	 *
	 * @param gcCode         the GC's Code
	 * @param store          the store in which the GC is purchased
	 * @param currencyCode   the GC's currency code
	 * @param purchaseAmount the purchase amount of persisted certificate
	 * @param creationDate   the date of GC creation
	 * @return persisted gift certificate
	 * @deprecated Use {@link GiftCertificateTestPersister#persistGiftCertificate(Store, String, String, String,
	 * BigDecimal, String, String, String, Customer, String, Date, Date, String, String)}
	 */
	@Deprecated
	public GiftCertificate persistGiftCertificate(final Store store, final String gcCode, final String currencyCode,
			final BigDecimal purchaseAmount, final Date creationDate) {
		return giftCertificateTestPersister.persistGiftCertificate(
				store, null, gcCode, currencyCode, purchaseAmount, null,
				null, "SomeTheme", null, null, creationDate, null, null, null);
	}

	/**
	 * Create persisted shipping region.
	 *
	 * @param regionName region name
	 * @param regionStr  region string
	 * @return persisted shipping region
	 */
	public ShippingRegion persistShippingRegion(final String regionName, final String regionStr) {
		final ShippingRegionService shippingRegionService = beanFactory.getBean(SHIPPING_REGION_SERVICE);
		final ShippingRegionImpl shippingRegion = beanFactory.getBean(SHIPPING_REGION);
		shippingRegion.setName(regionName);
		shippingRegion.setRegionStr(regionStr);

		return shippingRegionService.add(shippingRegion);
	}
}
