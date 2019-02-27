/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.test.integration.promotions;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.rules.RuleCondition;
import com.elasticpath.domain.rules.RuleParameter;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.service.rules.RuleService;
import com.elasticpath.service.rules.RuleSetService;
import com.elasticpath.test.integration.DirtiesDatabase;
import com.elasticpath.test.integration.junit.DatabaseHandlingTestExecutionListener;
import com.elasticpath.test.persister.PromotionTestPersister;
import com.elasticpath.test.persister.TestApplicationContext;
import com.elasticpath.test.persister.testscenarios.SimpleStoreScenario;
import com.elasticpath.test.support.junit.JmsRegistrationTestExecutionListener;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/integration-context.xml")
@SuppressWarnings("PMD.AbstractNaming")
@TestExecutionListeners({
		JmsRegistrationTestExecutionListener.class,
		DatabaseHandlingTestExecutionListener.class,
		DependencyInjectionTestExecutionListener.class,
		DirtiesContextTestExecutionListener.class
//		TransactionalTestExecutionListener.class
})
public class NonTransactionalRuleServiceImplITest {
	@Autowired
	private RuleService ruleService;

	@Autowired
	private RuleSetService ruleSetService;

	@Autowired
	private PersistenceEngine persistenceEngine;

	@Autowired
	private BeanFactory beanFactory;

	@Autowired
	private TestApplicationContext tac;

	@DirtiesDatabase
	@Test
	public void testThatNewRuleElementsArePersistedOnUpdate() {
		// Given
		Rule promo = givenABasicPromotionRule();

		// When
		RuleCondition condition = getBeanFactory().getBean(ContextIdNames.CART_SUBTOTAL_COND);
		condition.getParameters().iterator().next().setValue("50.00");
		promo.addCondition(condition);

		Rule updatedPromo = ruleService.update(promo);

		// Then
		assertEquals("Updated promo should include the newly added condition",
				1, updatedPromo.getConditions().size());
		assertEquals("Updated promo should have the correct condition",
				"50.00", updatedPromo.getConditions().iterator().next().getParamValue(RuleParameter.SUBTOTAL_AMOUNT_KEY));

		Rule reloadedPromo = ruleService.findByRuleCode(promo.getCode());
		assertEquals("Updated and reloaded promo should include the newly added condition",
				1, reloadedPromo.getConditions().size());
	}

	@DirtiesDatabase
	@Test
	public void testThatNewRuleElementsArePersistedOnUpdateWithSerialization() throws Exception {
		// Given
		Rule promo = givenABasicPromotionRule();
		Rule serializedPromo = serializeRule(promo);

		// When
		RuleCondition condition = getBeanFactory().getBean(ContextIdNames.CART_SUBTOTAL_COND);
		condition.getParameters().iterator().next().setValue("50.00");
		serializedPromo.addCondition(condition);

		Rule updatedPromo = ruleService.update(serializedPromo);

		// Then
		assertEquals("Updated promo should include the newly added condition",
				1, updatedPromo.getConditions().size());
		assertEquals("Updated promo should have the correct condition",
				"50.00", updatedPromo.getConditions().iterator().next().getParamValue(RuleParameter.SUBTOTAL_AMOUNT_KEY));

		Rule reloadedPromo = ruleService.findByRuleCode(promo.getCode());
		assertEquals("Updated and reloaded promo should include the newly added condition",
				1, reloadedPromo.getConditions().size());
	}

	protected Rule serializeRule(final Rule promo) throws IOException, ClassNotFoundException {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(byteStream);
		oos.writeObject(promo);
		ByteArrayInputStream byteInStream = new ByteArrayInputStream(byteStream.toByteArray());
		ObjectInputStream ois = new ObjectInputStream(byteInStream);
		return (Rule) ois.readObject();
	}

	protected Rule givenABasicPromotionRule() {
		SimpleStoreScenario scenario = getTac().useScenario(SimpleStoreScenario.class);
		PromotionTestPersister promoPersister = getTac().getPersistersFactory().getPromotionTestPersister();
		return promoPersister.createAndPersistSimpleShoppingCartPromotion("testpromo", scenario.getStore().getCode(), "promocode");
	}

	/**
	 * @return the tac
	 */
	protected TestApplicationContext getTac() {
		return tac;
	}

	protected BeanFactory getBeanFactory() {
		return beanFactory;
	}
}
