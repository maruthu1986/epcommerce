/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.email.producer.spi.composer.util.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.tools.generic.DateTool;
import org.apache.velocity.tools.generic.EscapeTool;
import org.apache.velocity.tools.generic.IteratorTool;
import org.apache.velocity.tools.generic.ListTool;
import org.apache.velocity.tools.generic.MathTool;
import org.apache.velocity.tools.generic.NumberTool;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.constants.WebConstants;
import com.elasticpath.commons.util.impl.StoreThemeMessageSource;
import com.elasticpath.commons.util.impl.VelocityGeographyHelperImpl;
import com.elasticpath.domain.catalogview.SeoUrlBuilder;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.misc.Geography;
import com.elasticpath.domain.store.Store;
import com.elasticpath.email.domain.EmailProperties;
import com.elasticpath.email.producer.spi.composer.util.EmailContextFactory;
import com.elasticpath.money.MoneyFormatter;
import com.elasticpath.service.customer.CustomerService;

/**
 * Factory class that creates the velocity context for generating email documents.
 */
public class EmailContextFactoryImpl implements EmailContextFactory {

	private Geography geography;
	private VelocityGeographyHelperImpl geographyHelper;
	private MoneyFormatter moneyFormatter;
	private StoreThemeMessageSource storeThemeMessageSource;
	private CustomerService customerService;
	private BeanFactory beanFactory;

	@Override
	public Map<String, Object> createVelocityContext(final Store store, final EmailProperties emailProperties) {
		final Map<String, Object> velocityContext = createBaseVelocityContext(store);
		velocityContext.putAll(getRequestSpecificContextProperties(emailProperties));
		// do this last to overwrite our defaults if passed properties already has them
		velocityContext.putAll(emailProperties.getTemplateResources());
		// additional VM information that should not be overwritten
		if (emailProperties.getStoreCode() != null) {
			String storeUrl = store.getUrl();
			velocityContext.put("store", store);
			velocityContext.put("baseImgUrl", storeUrl);
			velocityContext.put("storeUrl", trimUrl(storeUrl));
		}

		return velocityContext;
	}

	/**
	 * Creates the base velocity context for the given store.
	 *
	 * @param store the store
	 * @return a context map
	 */
	protected Map<String, Object> createBaseVelocityContext(final Store store) {
		final Map<String, Object> result = new HashMap<>();
		result.put("msgSource", getStoreThemeMessageSource());
		result.put(WebConstants.SEOURLBUILDER, getSeoUrlBuilder(store));
		result.put(WebConstants.GEOGRAPHY_HELPER, getGeographyHelper());
		result.put(WebConstants.MONEY_FORMATTER, getMoneyFormatter());
		result.put("mathTool", new MathTool());
		result.put("dateTool", new DateTool());
		result.put("numberTool", new NumberTool());
		result.put("iteratorTool", new IteratorTool());
		result.put("listTool", new ListTool());
		result.put("escapeTool", new EscapeTool());

		return result;
	}

	/**
	 * Sets the referenced object UIDs and puts them into the velocity template resources. Note: This is needed as there are many fields that are not
	 * serializable (e.g. Set, List)
	 *
	 * @param emailProperties the Email properties
	 * @return a Map of properties to add to the context
	 */
	protected Map<String, Object> getRequestSpecificContextProperties(final EmailProperties emailProperties) {
		final Map<String, Object> contextProps = new HashMap<>();

		// populate customer object into the template resources if a customer uid is specified
		Object customerUidString = emailProperties.getTemplateResources().get("customerUid");
		if (customerUidString != null) {
			long customerUid = Long.parseLong(customerUidString.toString());
			Customer customer = customerService.get(customerUid);
			contextProps.put("customer", customer);
		}

		return contextProps;
	}

	/**
	 * Trims the ending slash from the given url if it has one.
	 * @param url the url to trim
	 * @return the url without an ending slash
	 */
	private String trimUrl(final String url) {
		return StringUtils.chomp(url, "/");
	}

	/**
	 * Creates and returns an SEO URL Builder for the given {@link com.elasticpath.domain.store.Store}.
	 * This implementation creates a new one from the Spring application context
	 * and sets its Store.
	 *
	 * @param store the store
	 * @return an SeoUrlBuilder for the given store.
	 */
	protected SeoUrlBuilder getSeoUrlBuilder(final Store store) {
		SeoUrlBuilder seoUrlBuilder = beanFactory.getBean(ContextIdNames.SEO_URL_BUILDER);
		seoUrlBuilder.setStore(store);
		return seoUrlBuilder;
	}

	/**
	 * Lazy-loads a VelocityGeographyHelper class.
	 * @return the helper
	 */
	protected VelocityGeographyHelperImpl getGeographyHelper() {
		if (geographyHelper == null) {
			geographyHelper = new VelocityGeographyHelperImpl(getGeography());
		}
		return geographyHelper;
	}

	protected MoneyFormatter getMoneyFormatter() {
		return moneyFormatter;
	}

	public void setMoneyFormatter(final MoneyFormatter moneyFormatter) {
		this.moneyFormatter = moneyFormatter;
	}

	/**
	 * Set the geography properties map.
	 *
	 * @param geography the geography properties map.
	 */
	public void setGeography(final Geography geography) {
		this.geography = geography;
	}

	protected Geography getGeography() {
		return geography;
	}

	/**
	 * Gets a store-specific message source for rendering velocity templates. This implementation gets an instance from the Spring application
	 * context.
	 *
	 * @return the StoreThemeMessageSource instance
	 */
	protected StoreThemeMessageSource getStoreThemeMessageSource() {
		return storeThemeMessageSource;
	}

	public void setStoreThemeMessageSource(final StoreThemeMessageSource storeMessageSource) {
		this.storeThemeMessageSource = storeMessageSource;
	}

	public void setCustomerService(final CustomerService customerService) {
		this.customerService = customerService;
	}

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
}
