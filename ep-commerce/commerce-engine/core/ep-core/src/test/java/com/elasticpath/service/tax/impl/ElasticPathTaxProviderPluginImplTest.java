/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.service.tax.impl;

import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.WriterAppender;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.plugin.tax.builder.TaxOperationContextBuilder;
import com.elasticpath.plugin.tax.common.TaxContextIdNames;
import com.elasticpath.plugin.tax.common.TaxJournalType;
import com.elasticpath.plugin.tax.domain.TaxAddress;
import com.elasticpath.plugin.tax.domain.TaxExemption;
import com.elasticpath.plugin.tax.domain.TaxableItem;
import com.elasticpath.plugin.tax.domain.TaxableItemContainer;
import com.elasticpath.plugin.tax.domain.TaxedItem;
import com.elasticpath.plugin.tax.domain.TaxedItemContainer;
import com.elasticpath.plugin.tax.domain.impl.MutableTaxAddress;
import com.elasticpath.plugin.tax.domain.impl.MutableTaxableItemContainer;
import com.elasticpath.plugin.tax.domain.impl.MutableTaxedItem;
import com.elasticpath.plugin.tax.domain.impl.MutableTaxedItemContainer;
import com.elasticpath.plugin.tax.domain.impl.StringTaxDocumentId;
import com.elasticpath.plugin.tax.domain.impl.TaxExemptionImpl;
import com.elasticpath.plugin.tax.domain.impl.TaxableItemImpl;
import com.elasticpath.plugin.tax.rate.dto.MutableTaxRateDescriptor;
import com.elasticpath.plugin.tax.rate.dto.MutableTaxRateDescriptorResult;
import com.elasticpath.plugin.tax.rate.impl.TaxExclusiveRateApplier;
import com.elasticpath.plugin.tax.resolver.TaxRateDescriptorResolver;
import com.elasticpath.service.tax.calculator.impl.ElasticPathTaxCalculator;

/**
 * Tests the tax calculator provided by Elastic Path, {@link com.elasticpath.service.tax.calculator.impl.ElasticPathTaxCalculator }.
 */
public class ElasticPathTaxProviderPluginImplTest {

	private static final String LINE_ITEM_1 = "LineItem-1";
	private static final String LINE_DESCRIPTION_1 = "This is product 1";
	private static final double DOUBLE_1000 = 1000.00;
	private static final double DOUBLE_20 = 20.00;
	private static final double DELTA = 0.0001;
	private static final String TAX_EXEMPTION_ID = "4567828";
	private static final double GST_VALUE = 0.075;
	private static final double EXPECTED_TOTAL_TAX = 75.00;
	private static final double EXPECTED_SHIPPING_TAX = 1.50;

	private final ElasticPathTaxProviderPluginImpl epTaxPlugin = new ElasticPathTaxProviderPluginImpl();

	private final MutableTaxableItemContainer container = new MutableTaxableItemContainer();
	private final List<TaxableItem> itemList = new ArrayList<>();
	private final MutableTaxRateDescriptor gst = new MutableTaxRateDescriptor();
	private final MutableTaxRateDescriptorResult gstDescriptionResult = new MutableTaxRateDescriptorResult();

	/**
	 * Sets up the container.
	 */
	@Before
	public void setUp() {
		container.setCurrency(Currency.getInstance(Locale.US));
		container.setStoreCode("US_STORE");

		TaxableItem shipping = generateTaxableItem(DOUBLE_20, 1, "Shipping Cost", "Shipping Cost", TaxCode.TAX_CODE_SHIPPING);
		TaxableItem taxableItem = generateTaxableItem(DOUBLE_1000, 1, LINE_DESCRIPTION_1, LINE_ITEM_1, "GOODS");
		itemList.add(taxableItem);
		itemList.add(shipping);

		final BeanFactory beanFactory = mock(BeanFactory.class);
		ElasticPathTaxCalculator epTaxCalculator = new ElasticPathTaxCalculator();
		epTaxCalculator.setBeanFactory(beanFactory);

		epTaxPlugin.setBeanFactory(beanFactory);
		epTaxPlugin.setTaxCalculator(epTaxCalculator);

		gst.setTaxJurisdiction("CA");
		gst.setTaxRegion("BC");
		gst.setTaxRateApplier(new TaxExclusiveRateApplier());
		gst.setValue(new BigDecimal(GST_VALUE));
		gstDescriptionResult.setTaxInclusive(false);
		gstDescriptionResult.addTaxRateDescriptor(gst);

		TaxRateDescriptorResolver taxRateDescriptorResolver = mock(TaxRateDescriptorResolver.class);
		when(taxRateDescriptorResolver.findTaxRateDescriptors(isA(TaxableItem.class), isA(TaxableItemContainer.class))).
				thenReturn(gstDescriptionResult);

		when(beanFactory.getBean(TaxContextIdNames.MUTABLE_TAXED_ITEM_CONTAINER)).thenReturn(new MutableTaxedItemContainer());
		// thenAnswer returns a new Object (not the same instance) per call
		when(beanFactory.getBean(TaxContextIdNames.MUTABLE_TAXED_ITEM)).thenAnswer(new Answer<MutableTaxedItem>() {
			@Override
			public MutableTaxedItem answer(final InvocationOnMock invocationOnMock) throws Throwable {
				return new MutableTaxedItem();
			}
		});

		when(beanFactory.getBean(TaxContextIdNames.TAX_RATE_DESCRIPTOR_RESOLVER)).thenReturn(taxRateDescriptorResolver);
	}

	@Test
	public void testCalculateWithoutTaxExemption() {
		String documentId = "AK_58_item_and_shipping_docId" + System.currentTimeMillis();
		setupTaxContainerHelper(documentId, itemList, null, getValidUSAddress(), getValidUSAddress());

		TaxedItemContainer result = epTaxPlugin.calculateTaxes(container);
		assertNotNull(result);
		assertEquals("There should be an item and a shipping line", 2, result.getItems().size());

		TaxedItem taxedItem = result.getItems().get(0);
		assertEquals("The before tax total should be 1000", DOUBLE_1000, taxedItem.getPriceBeforeTax().doubleValue(), DELTA);
		assertEquals("The total tax should be 75.00", EXPECTED_TOTAL_TAX, taxedItem.getTotalTax().doubleValue(), DELTA);

		TaxedItem taxedShipping = result.getItems().get(1);
		assertEquals("The before tax total should be 20", DOUBLE_20, taxedShipping.getPriceBeforeTax().doubleValue(), DELTA);
		assertEquals("The shipping tax should be 1.50", EXPECTED_SHIPPING_TAX, taxedShipping.getTotalTax().doubleValue(), DELTA);
	}

	@Test
	public void testCalculateWithEmptyTaxExemptionCodeShouldPass() {
		String documentId = "AK_89_item_and_shipping_docId" + System.currentTimeMillis();
		TaxExemption taxExemption = new TaxExemptionImpl();
		taxExemption.setExemptionId("");

		setupTaxContainerHelper(documentId, itemList, taxExemption, getValidUSAddress(), getValidUSAddress());
		TaxedItemContainer result = epTaxPlugin.calculateTaxes(container);
		assertNotNull(result);
	}

	@Test
	public void testCalculateWithNullTaxExemptionCodeShouldPass() {
		String documentId = "AK_89_item_and_shipping_docId" + System.currentTimeMillis();
		TaxExemption taxExemption = new TaxExemptionImpl();
		taxExemption.setExemptionId(null);
		setupTaxContainerHelper(documentId, itemList, taxExemption, getValidUSAddress(), getValidUSAddress());
		TaxedItemContainer result = epTaxPlugin.calculateTaxes(container);
		assertNotNull(result);
	}

	@Test
	public void testCalculateWithTaxExemptionShouldLogError() {
		StringWriter logWriter = new StringWriter();
		Logger.getRootLogger().addAppender(new WriterAppender(new SimpleLayout(), logWriter));
		String documentId = "AK_89_item_and_shipping_docId" + System.currentTimeMillis();
		TaxExemption taxExemption = new TaxExemptionImpl();
		taxExemption.setExemptionId(TAX_EXEMPTION_ID);
		setupTaxContainerHelper(documentId, itemList, taxExemption, getValidUSAddress(), getValidUSAddress());

		epTaxPlugin.calculateTaxes(container);

		assertThat("Unsupported Tax Exemption should be logged", logWriter.toString(),
				startsWith("WARN - The tax provider does not provide tax exemption services."));
	}

	private void setupTaxContainerHelper(final String documentId,
			final List<TaxableItem> itemList,
			final TaxExemption taxExemption,
			final MutableTaxAddress destination,
			final MutableTaxAddress origin) {
		resetContainerAddresses(destination, origin);
		container.setTaxOperationContext(TaxOperationContextBuilder
				.newBuilder()
				.withCustomerCode("user_" + System.currentTimeMillis())
				.withTaxJournalType(TaxJournalType.PURCHASE)
				.withTaxDocumentId(StringTaxDocumentId.fromString(documentId))
				.withTaxExemption(taxExemption)
				.build());
		container.setItems(itemList);
	}

	/**
	 * Resets the container's addresses.
	 * @param destination destination address
	 * @param origin origin address
	 */
	private void resetContainerAddresses(final TaxAddress destination, final TaxAddress origin) {
		container.setDestinationAddress(destination);
		container.setOriginAddress(origin);
	}

	private MutableTaxAddress getValidUSAddress() {
		MutableTaxAddress address = new MutableTaxAddress();
		address.setStreet1("100 Ravine Lane NE");
		address.setStreet2("Suite 220");
		address.setCity("Bainbridge Island");
		address.setSubCountry("WA");
		address.setZipOrPostalCode("98110");
		address.setCountry("US");

		return address;
	}

	@SuppressWarnings("PMD.AvoidDecimalLiteralsInBigDecimalConstructor")
	private TaxableItemImpl generateTaxableItem(final double totalPrice,
			final int quantity,
			final String itemDescription,
			final String itemCode,
			final String taxCode) {
		TaxableItemImpl item = new TaxableItemImpl();
		item.setPrice(new BigDecimal(totalPrice));
		item.setQuantity(quantity);
		item.setItemCode(itemCode);
		item.setItemDescription(itemDescription);
		item.setTaxCode(taxCode);
		return item;
	}
}
