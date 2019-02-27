/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.test.integration.promotions;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.misc.LocalizedProperties;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.rules.RuleAction;
import com.elasticpath.domain.rules.RuleCondition;
import com.elasticpath.domain.rules.RuleElement;
import com.elasticpath.domain.rules.RuleParameter;
import com.elasticpath.domain.rules.RuleScenarios;
import com.elasticpath.domain.rules.RuleSet;
import com.elasticpath.domain.sellingcontext.SellingContext;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.PromotionConfigureService;
import com.elasticpath.service.rules.RuleService;
import com.elasticpath.service.rules.RuleSetService;
import com.elasticpath.service.store.StoreService;
import com.elasticpath.test.integration.BasicSpringContextTest;
import com.elasticpath.test.integration.DirtiesDatabase;
import com.elasticpath.test.persister.testscenarios.SimpleStoreScenario;

/**
 * Integration tests that ensure the rule service behaves as expected.
 */
public class RuleServiceImplTest extends BasicSpringContextTest {

	private static final String RULE_CODE_ONE = "activeRuleInStoreOne";
	private static final String RULE_CODE_TWO = "activeRuleInStoreTwo";
	private static final String RULE_CODE_THREE = "inactiveRuleInStoreOne";
	private static final String STORE_CODE_ONE = "storeCodeOne";
	private static final String STORE_CODE_TWO = "storeCodeTwo";

	@Autowired
	private RuleService ruleService;

	@Autowired
	private RuleSetService ruleSetService;

	@Autowired
	private StoreService storeService;

	@Autowired
	private PromotionConfigureService promotionConfigureService;

	/**
	 * Test persisting and loading display name.
	 */
	@DirtiesDatabase
	@Test
	public void testDisplayName() {
		final String englishDisplayName = "English Display Name";
		final String canadianDisplayName = "Canadian Display Name";
		final String germanDisplayName = "Deutsch Angezeigter Name";
		final String ruleCode = "ruleCode";

		// Create the rule
		Rule rule = createRule(ruleCode);

		// Add display names in several locales
		LocalizedProperties localizedProperties = rule.getLocalizedProperties();
		localizedProperties.setValue(Rule.LOCALIZED_PROPERTY_DISPLAY_NAME, Locale.ENGLISH, englishDisplayName);
		final Locale canadianEnglish = new Locale("en", "CA");
		localizedProperties.setValue(Rule.LOCALIZED_PROPERTY_DISPLAY_NAME, canadianEnglish, canadianDisplayName);
		localizedProperties.setValue(Rule.LOCALIZED_PROPERTY_DISPLAY_NAME, Locale.GERMAN, germanDisplayName);

		// Save the rule and reload it
		ruleService.add(rule);
		Rule loadedRule = ruleService.findByRuleCode(ruleCode);

		assertThat(loadedRule.getDisplayName(Locale.ENGLISH))
				.as("Rule should contain the English display name")
				.isEqualTo(englishDisplayName);

		assertThat(loadedRule.getDisplayName(Locale.GERMAN))
				.as("Rule should contain the German display name")
				.isEqualTo(germanDisplayName);

		assertThat(loadedRule.getDisplayName(canadianEnglish))
				.as("Rule should contain the Canadian English display name")
				.isEqualTo(canadianDisplayName);
	}

	/**
	 * Test finding the active rule id and selling context by scenario and store code.
	 */
	@DirtiesDatabase
	@Test
	public void testFindActiveRuleIdSellingContextByScenarioAndStore() {

		SimpleStoreScenario scenario = getTac().useScenario(SimpleStoreScenario.class);
		Store storeOne = scenario.getStore();
		storeOne.setCode(STORE_CODE_ONE);
		storeService.saveOrUpdate(storeOne);

		Store storeTwo = getTac().getPersistersFactory().getStoreTestPersister().persistStore(scenario.getCatalog(), scenario.getWarehouse(),
				STORE_CODE_TWO, "USD");

		Rule activeRuleInStoreOne = createRule(RULE_CODE_ONE, storeOne, true);
		ruleService.add(activeRuleInStoreOne);

		Rule activeRuleInStoreTwo = createRule(RULE_CODE_TWO, storeTwo, true);
		ruleService.add(activeRuleInStoreTwo);

		Rule inactiveRuleInStoreOne = createRule(RULE_CODE_THREE, storeOne, false);
		ruleService.add(inactiveRuleInStoreOne);

		List<Object[]> results = ruleService.findActiveRuleIdSellingContextByScenarioAndStore(RuleScenarios.CART_SCENARIO, STORE_CODE_ONE);

		assertThat(results)
				.size()
				.as("One result should have been found")
				.isEqualTo(1);

		assertThat(results.get(0))
				.as("The result should contain the expected rule id and selling context")
				.contains(activeRuleInStoreOne.getUidPk(), activeRuleInStoreOne.getSellingContext());
	}

	/**
	 * Test finding a rule by code.
	 */
	@DirtiesDatabase
	@Test
	public void testFindRuleByCode() {
		SimpleStoreScenario scenario = getTac().useScenario(SimpleStoreScenario.class);

		Rule activeRuleOne = createRule(RULE_CODE_ONE, scenario.getStore(), true);
		ruleService.add(activeRuleOne);

		Rule activeRuleTwo = createRule(RULE_CODE_TWO, scenario.getStore(), true);
		ruleService.add(activeRuleTwo);

		Rule loadedRule = ruleService.findByRuleCode(RULE_CODE_ONE);

		assertThat(loadedRule)
				.as("Result should have been found")
				.isEqualTo(activeRuleOne);
	}

	/**
	 * Test finding rules by codes.
	 */
	@DirtiesDatabase
	@Test
	public void testFindRuleByCodes() {
		SimpleStoreScenario scenario = getTac().useScenario(SimpleStoreScenario.class);

		Rule activeRuleOne = createRule(RULE_CODE_ONE, scenario.getStore(), true);
		ruleService.add(activeRuleOne);

		Rule activeRuleTwo = createRule(RULE_CODE_TWO, scenario.getStore(), true);
		ruleService.add(activeRuleTwo);

		Collection<Rule> loadedRules = ruleService.findByRuleCodes(Arrays.asList(RULE_CODE_ONE, RULE_CODE_TWO));

		assertThat(loadedRules)
				.size()
				.as("Two results should have been found")
				.isEqualTo(2);

		assertThat(loadedRules)
				.as("The result should contain the expected rules")
				.contains(activeRuleOne, activeRuleTwo);
	}

	@DirtiesDatabase
	@Test
	public void verifyAllowedLimitRetrievableForGivenRuleId() {
		final Long expectedAllowedLimit = 5L;

		final SimpleStoreScenario scenario = getTac().useScenario(SimpleStoreScenario.class);

		final Rule rule = createRule(RULE_CODE_ONE, scenario.getStore(), true);

		final RuleCondition condition = getBeanFactory().getBean("limitedUsagePromotionCondition");

		promotionConfigureService.retrieveRuleParameterByKey(condition, RuleParameter.ALLOWED_LIMIT)
				.setValue(String.valueOf(expectedAllowedLimit));

		promotionConfigureService.retrieveRuleParameterByKey(condition, RuleParameter.LIMITED_USAGE_PROMOTION_ID)
				.setValue(rule.getCode());

		rule.addCondition(condition);

		final Rule persistedRule = ruleService.add(rule);

		assertThat(ruleService.getAllowedLimit(persistedRule.getUidPk()))
				.isEqualTo(expectedAllowedLimit);
	}

	private Rule createRule(final String ruleCode, final Store storeCode, final boolean isEnabled) {
		Rule rule = createRule(ruleCode);
		rule.setEnabled(isEnabled);
		rule.setStore(storeCode);

		SellingContext sellingContext = getBeanFactory().getBean(ContextIdNames.SELLING_CONTEXT);
		sellingContext.setGuid(ruleCode);
		sellingContext.setName(ruleCode);
		sellingContext.setPriority(1);

		rule.setSellingContext(sellingContext);

		return rule;
	}

	private Rule createRule(final String ruleCode) {
		RuleSet ruleSet = getBeanFactory().getBean(ContextIdNames.RULE_SET);
		ruleSet.setLastModifiedDate(new Date());
		ruleSet.setName(ruleCode);
		ruleSet.setScenario(RuleScenarios.CART_SCENARIO);
		ruleSet = ruleSetService.add(ruleSet);

		RuleParameter ruleParam = getBeanFactory().getBean(ContextIdNames.RULE_PARAMETER);
		ruleParam.setKey(RuleParameter.DISCOUNT_PERCENT_KEY);
		ruleParam.setValue("10");
		RuleAction ruleAction = getBeanFactory().getBean(ContextIdNames.CART_SUBTOTAL_PERCENT_DISCOUNT_ACTION);
		ruleAction.getParameters().clear();
		ruleAction.addParameter(ruleParam);

		Rule rule = getBeanFactory().getBean(ContextIdNames.PROMOTION_RULE);
		rule.setName(ruleCode);
		rule.setCode(ruleCode);
		rule.setRuleSet(ruleSet);
		rule.addAction(ruleAction);

		return rule;
	}
}
