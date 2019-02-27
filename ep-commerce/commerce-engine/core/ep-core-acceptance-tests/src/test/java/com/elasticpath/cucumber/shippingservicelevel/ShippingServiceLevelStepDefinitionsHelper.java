/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.cucumber.shippingservicelevel;

import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.cucumber.CucumberConstants;
import com.elasticpath.cucumber.ScenarioContextValueHolder;
import com.elasticpath.domain.shipping.ShippingRegion;
import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.shipping.ShippingOptionTransformer;
import com.elasticpath.service.shipping.ShippingRegionService;
import com.elasticpath.service.shipping.ShippingServiceLevelService;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;
import com.elasticpath.test.persister.ShippingRegionTestPersister;
import com.elasticpath.test.persister.StoreTestPersister;
import com.elasticpath.test.persister.TestApplicationContext;

/**
 * Help class for {@link ShippingServiceLevelStepDefinitions}.
 */
public class ShippingServiceLevelStepDefinitionsHelper {

	@Inject
	@Named("storeHolder")
	private ScenarioContextValueHolder<Store> storeHolder;
	
	@Inject
	@Named("shippingOptionHolder")
	private ScenarioContextValueHolder<ShippingOption> shippingOptionHolder;
	
	@Autowired
	private TestApplicationContext tac;
	
	@Autowired
	private ShippingRegionService shippingRegionService;
	
	@Autowired
	private ShippingServiceLevelService shippingServiceLevelService;

	@Autowired
	private ShippingOptionTransformer shippingOptionTransformer;
	
	/**
	 * Sets up shipping service levels for a given store.
	 *
	 * @param sslProperties the shipping service level info
	 */
	public void setUpShippingServiceLevels(final List<Map<String, String>> sslProperties) {	

		ShippingServiceLevel defaultSSL = null;
		
		StoreTestPersister storeTestPersister = tac.getPersistersFactory().getStoreTestPersister();
		
		for (Map<String, String> properties : sslProperties) {

			String code = properties.get(CucumberConstants.FIELD_SHIPPING_SERVICE_LEVEL_CODE);
			String name = "display name for " + code;
			defaultSSL = storeTestPersister.persistShippingServiceLevelFixedPriceCalcMethod(
					storeHolder.get(),
					properties.get(CucumberConstants.FIELD_REGION),
					name,
					"SSL_CARRIER",
					properties.get(CucumberConstants.FIELD_PRICE),
					code);

		}

		shippingOptionHolder.set(shippingOptionTransformer.transform(defaultSSL, () -> null, storeHolder.get().getDefaultLocale()));
	}
	
	/**
	 * Sets up shipping regions for a given store.
	 *
	 * @param shippingRegionProperties the shipping region info
	 */
	public void setUpShippingRegions(final List<Map<String, String>> shippingRegionProperties) {
		
		sanityCheck(storeHolder.get());
		
		StoreTestPersister storeTestPersister = tac.getPersistersFactory().getStoreTestPersister();
		
		for (Map<String, String> properties : shippingRegionProperties) {
			
			String shippingRegionName = properties.get(CucumberConstants.FIELD_REGION);
			
			ShippingRegion shippingRegion = shippingRegionService.findByName(shippingRegionName);
			
			if (shippingRegion == null) {
				storeTestPersister.persistShippingRegion(shippingRegionName, properties.get(CucumberConstants.FIELD_REGION_STRING));
			}
		}
	}
	
	private void sanityCheck(final Store store) {
		
		ShippingRegionTestPersister shippingRegionTestPersister = tac.getPersistersFactory().getShippingRegionTestPersister();
		
		List<ShippingServiceLevel> shippingServiceLevels = shippingServiceLevelService.findByStoreAndState(store.getCode(), true);
		
		for (ShippingServiceLevel shippingServiceLevel : shippingServiceLevels) {
			shippingServiceLevelService.remove(shippingServiceLevel);
		}
		
		shippingRegionTestPersister.clearExistingData();
	}
}
