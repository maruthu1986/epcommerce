/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cucumber.tax;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Currency;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.cucumber.ScenarioContextValueHolder;
import com.elasticpath.cucumber.shoppingcart.ShoppingCartStepDefinitionsHelper;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.ShoppingItemPricingSnapshot;
import com.elasticpath.domain.store.Store;
import com.elasticpath.money.Money;
import com.elasticpath.plugin.tax.builder.TaxExemptionBuilder;
import com.elasticpath.plugin.tax.builder.TaxOperationContextBuilder;
import com.elasticpath.plugin.tax.common.TaxJournalType;
import com.elasticpath.plugin.tax.domain.TaxAddress;
import com.elasticpath.plugin.tax.domain.TaxExemption;
import com.elasticpath.plugin.tax.domain.TaxOperationContext;
import com.elasticpath.plugin.tax.domain.TaxableItemContainer;
import com.elasticpath.plugin.tax.domain.impl.StringTaxDocumentId;
import com.elasticpath.service.customer.CustomerService;
import com.elasticpath.service.shoppingcart.PricingSnapshotService;
import com.elasticpath.service.shoppingcart.ShoppingItemToPricingSnapshotFunction;
import com.elasticpath.service.tax.TaxCalculationService;
import com.elasticpath.service.tax.adapter.TaxAddressAdapter;
import com.elasticpath.test.persister.TestApplicationContext;

/**
 * B2BTaxStepDefinitionsHelper.
 */
public class B2BTaxStepDefinitionsHelper {
	@Inject
	@Named("customerHolder")
	private ScenarioContextValueHolder<Customer> customerHolder;

	@Inject
	@Named("storeHolder")
	private ScenarioContextValueHolder<Store> storeHolder;

	@Inject
	@Named("taxableItemContainerHolder")
	private ScenarioContextValueHolder<TaxableItemContainer> taxableItemContainerHolder;

	@Autowired
	private TestApplicationContext tac;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private ShoppingCartStepDefinitionsHelper shoppingCartStepDefinitionsHelper;

	@Autowired
	private TaxCalculationService taxCalculationService;

	@Autowired
	private TaxAddressAdapter taxAddressAdapter;

	@Autowired
	private PricingSnapshotService pricingSnapshotService;

	/**
	 * Create a basic customer.
	 */
	public void setupCustomer() {
		customerHolder.set(tac.getPersistersFactory().getStoreTestPersister().createDefaultCustomer(storeHolder.get()));
	}

	/**
	 * Set the given business number onto the customer.
	 *
	 * @param businessNumber the business number to set
	 */
	public void setBusinessNumberOnCustomer(final String businessNumber) {
		if (customerHolder.get() == null) {
			setupCustomer();
		}

		Customer customer = customerService.load(customerHolder.get().getUidPk());
		customer.setBusinessNumber(businessNumber);

		customerHolder.set(customerService.update(customer));
	}

	/**
	 * Set the given tax exemption id on the customer.
	 *
	 * @param taxExemptionId the tax exemption to set
	 */
	public void setCustomerTaxExemptionIdOnCustomer(final String taxExemptionId) {
		if (customerHolder.get() == null) {
			setupCustomer();
		}

		Customer customer = customerService.load(customerHolder.get().getUidPk());
		customer.setTaxExemptionId(taxExemptionId);

		customerHolder.set(customerService.update(customer));
	}

	/**
	 * Verify that the TaxOperationContext contains the expected business number.
	 *
	 * @param businessNumber the expected business number
	 */
	public void verifyTaxOperationContextBusinessNumber(final String businessNumber) {
		assertEquals("Business number did not match", businessNumber,
					taxableItemContainerHolder.get().getTaxOperationContext().getCustomerBusinessNumber());
	}

	/**
	 * Verify that the TaxOperationContext contains the expected Tax Exemption Id.
	 *
	 * @param taxExemptionId the expected tax exemption id
	 */
	public void verifyTaxOperationContextTaxExemptionId(final String taxExemptionId) {
		assertEquals("Tax Exemption Id did not match", taxExemptionId,
					taxableItemContainerHolder.get().getTaxOperationContext().getTaxExemption().getExemptionId());
	}

	/**
	 * Request that taxes are calculated.
	 */
	public void requestTaxCalculation() {
		final Customer customer = customerHolder.get();

		final ShoppingCart shoppingCart = shoppingCartStepDefinitionsHelper.getShoppingCart();
		shoppingCart.getShopper().setCustomer(customer);

		final Store store = storeHolder.get();
		final Currency currency = store.getDefaultCurrency();
		final Money zeroDollars = Money.valueOf(BigDecimal.ZERO, currency);

		final String storeCode = store.getCode();
		final TaxAddress destinationAddress = taxAddressAdapter.toTaxAddress(shoppingCart.getShippingAddress());
		final TaxAddress originAddress = taxAddressAdapter.toTaxAddress(store.getWarehouse().getAddress());

		final ShoppingCartPricingSnapshot cartPricingSnapshot = pricingSnapshotService.getPricingSnapshotForCart(shoppingCart);
		final Collection<ShoppingItem> shoppingItems = shoppingCart.getAllShoppingItems();

		final Map<? extends ShoppingItem, ShoppingItemPricingSnapshot> shoppingItemPricingSnapshotMap = shoppingItems.stream()
				.collect(Collectors.toMap(
						Function.identity(),
						new ShoppingItemToPricingSnapshotFunction(cartPricingSnapshot)
				));

		final TaxOperationContext taxOperationContext  = TaxOperationContextBuilder
				.newBuilder()
				.withCurrency(currency)
				.withTaxDocumentId(StringTaxDocumentId.fromString(shoppingCart.getGuid()))
				.withCustomerCode(shoppingCart.getGuid())
				.withTaxJournalType(TaxJournalType.PURCHASE)
				.withTaxExemption(shoppingCart.getTaxExemption())
				.withCustomerBusinessNumber(customer.getBusinessNumber())
				.build();

		taxCalculationService.calculateTaxes(storeCode,
													destinationAddress,
													originAddress,
													zeroDollars,
													shoppingItemPricingSnapshotMap,
													zeroDollars,
													taxOperationContext);
	}

	/**
	 * Set the tax exemption id on the shopping cart.
	 *
	 * @param taxExemptionId the tax exemption id to set
	 */
	public void setTaxExemptionOnCart(final String taxExemptionId) {
		TaxExemption taxExemption = TaxExemptionBuilder.newBuilder().withTaxExemptionId(taxExemptionId).build();
		ShoppingCart shoppingCart = shoppingCartStepDefinitionsHelper.getShoppingCart();
		shoppingCart.setTaxExemption(taxExemption);
	}

}
