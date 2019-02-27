/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.importexport.common.adapters.promotion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.rules.RuleCondition;
import com.elasticpath.domain.rules.RuleElement;
import com.elasticpath.domain.rules.RuleScenarios;
import com.elasticpath.domain.rules.RuleSet;
import com.elasticpath.domain.rules.impl.CartContainsItemsOfCategoryConditionImpl;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.caching.CachingService;
import com.elasticpath.importexport.common.dto.promotion.rule.ActionDTO;
import com.elasticpath.importexport.common.dto.promotion.rule.AndDTO;
import com.elasticpath.importexport.common.dto.promotion.rule.BooleanComponentDTO;
import com.elasticpath.importexport.common.dto.promotion.rule.ConditionDTO;
import com.elasticpath.importexport.common.dto.promotion.rule.ConditionsDTO;
import com.elasticpath.importexport.common.dto.promotion.rule.OrDTO;
import com.elasticpath.importexport.common.dto.promotion.rule.RuleDTO;
import com.elasticpath.importexport.common.exception.runtime.PopulationRuntimeException;

/**
 * Tests population of <code>RuleDTO</code> from <code>Rule</code> and back to front.
 */
@RunWith(MockitoJUnitRunner.class)
public class RuleAdapterTest {

	private final RuleAdapter ruleAdapter = new RuleAdapter();

	@Mock
	private CachingService cachingService;

	@Mock
	private DomainAdapter<RuleElement, ConditionDTO> conditionAdapter;

	private int numberOfCalls;

	private int numberOfRetrievals;

	/**
	 * Prepares <code>RuleAdapter</code>.
	 *
	 * @throws Exception in case it fails to initialize rule adapter
	 */
	@Before
	public void setUp() throws Exception {
		ruleAdapter.setCachingService(cachingService);
		ruleAdapter.setConditionAdapter(conditionAdapter);
	}

	/**
	 * Tests population of Action DTO objects from <code>Rule</code> domain object.
	 */
	@Test
	public void testCreateActionDTOList() {
		final Rule rule = mock(Rule.class);
		final RuleSet ruleSet = mock(RuleSet.class);

		final String promotionCode = "1234-4321";

		when(rule.getCode()).thenReturn(promotionCode);
		when(rule.getRuleSet()).thenReturn(ruleSet);
		when(ruleSet.getScenario()).thenReturn(RuleScenarios.CART_SCENARIO);

		List<ActionDTO> actionDTOList = ruleAdapter.createActionDTOList(rule);

		assertThat(actionDTOList).size().isEqualTo(1);
		assertThat(actionDTOList.get(0).getCode()).isEqualTo(promotionCode);
		assertThat(actionDTOList.get(0).getType()).isEqualTo(RuleAdapter.SHOPPING_CART_PROMOTION);

		verify(rule, times(1)).getCode();
		verify(rule, times(1)).getRuleSet();
		verify(ruleSet, times(1)).getScenario();
	}

	/**
	 * Checks that getBooleanClauseDto returns AndDTO for conjunction (true) and OrDTO for disjunction (false).
	 */
	@Test
	public void testGetBooleanClauseDto() {
		assertThat(ruleAdapter.getBooleanClauseDTO(true)).isInstanceOf(AndDTO.class);
		assertThat(ruleAdapter.getBooleanClauseDTO(false)).isInstanceOf(OrDTO.class);
	}

	/**
	 * Checks that pinchCondition method removes one condition from the list and asks condition adapter to populate condition DTO.
	 */
	@Test
	public void testRetrieveCondition() {
		final ConditionDTO firstCondition = new ConditionDTO();
		final ConditionDTO secondCondition = new ConditionDTO();
		final List<ConditionDTO> ruleElements = new ArrayList<>();
		ruleElements.add(firstCondition);
		ruleElements.add(secondCondition);

		ruleAdapter.retrieveCondition(ruleElements);
		assertThat(ruleElements).size().isEqualTo(1);
	}

	/**
	 * Tests guard restricting empty lists with conditions and eligibilities.
	 */
	@Test(expected = PopulationRuntimeException.class)
	public void testCreateConditionComposition01() {
		ruleAdapter.createConditionComposition(Collections.<ConditionDTO>emptyList(), true);
	}

	/**
	 * Tests number of recursive calls and retrievals of <code>RuleElement</code> objects from the list.
	 * Only one call occurs if list size is equal to 1 or 2. It is recursion finish criterion.
	 * All elements should be retrieved from the list (check number of retrievals for that purpose).
	 */
	@Test
	public void testCreateConditionComposition02() {

		final RuleAdapter ruleAdapter = new RuleAdapter() {

			@Override
			BooleanComponentDTO retrieveCondition(final List<ConditionDTO> conditions) {
				++numberOfRetrievals;
				conditions.remove(0);
				return null;
			}

			@Override
			BooleanComponentDTO createConditionComposition(final List<ConditionDTO> conditions, final boolean makeConjunction) {
				++numberOfCalls;
				return super.createConditionComposition(conditions, makeConjunction);
			}
		};

		ConditionDTO firstCondition = new ConditionDTO();
		ConditionDTO secondCondition = new ConditionDTO();
		ConditionDTO thirdCondition = new ConditionDTO();
		ConditionDTO fourthCondition = new ConditionDTO();
		final List<ConditionDTO> ruleElements = new ArrayList<>();

		numberOfCalls = 0;
		numberOfRetrievals = 0;
		ruleElements.clear();
		ruleElements.add(firstCondition);
		int expectedNumberOfRetrievals = ruleElements.size();
		ruleAdapter.createConditionComposition(ruleElements, true);
		assertThat(numberOfCalls).isEqualTo(1);
		assertThat(numberOfRetrievals).isEqualTo(expectedNumberOfRetrievals);

		numberOfCalls = 0;
		numberOfRetrievals = 0;
		ruleElements.clear();
		ruleElements.add(firstCondition);
		expectedNumberOfRetrievals = ruleElements.size();
		ruleAdapter.createConditionComposition(ruleElements, false); // logical operator doesn't matter
		assertThat(numberOfCalls).isEqualTo(1);
		assertThat(numberOfRetrievals).isEqualTo(expectedNumberOfRetrievals);

		numberOfCalls = 0;
		numberOfRetrievals = 0;
		ruleElements.clear();
		ruleElements.add(firstCondition);
		ruleElements.add(secondCondition);
		expectedNumberOfRetrievals = ruleElements.size();
		ruleAdapter.createConditionComposition(ruleElements, true);
		assertThat(numberOfCalls).isEqualTo(1);
		assertThat(numberOfRetrievals).isEqualTo(expectedNumberOfRetrievals);

		numberOfCalls = 0;
		numberOfRetrievals = 0;
		ruleElements.clear();
		ruleElements.add(firstCondition);
		ruleElements.add(secondCondition);
		expectedNumberOfRetrievals = ruleElements.size();
		ruleAdapter.createConditionComposition(ruleElements, false); // logical operator doesn't matter
		assertThat(numberOfCalls).isEqualTo(1);
		assertThat(numberOfRetrievals).isEqualTo(expectedNumberOfRetrievals);

		numberOfCalls = 0;
		numberOfRetrievals = 0;
		ruleElements.clear();
		ruleElements.add(firstCondition);
		ruleElements.add(secondCondition);
		ruleElements.add(thirdCondition);
		expectedNumberOfRetrievals = ruleElements.size();
		ruleAdapter.createConditionComposition(ruleElements, false);
		assertThat(numberOfCalls).isEqualTo(2); // one recursive call of the method from itself
		assertThat(numberOfRetrievals).isEqualTo(expectedNumberOfRetrievals);

		final int expectedCallAmount = 3;
		numberOfCalls = 0;
		numberOfRetrievals = 0;
		ruleElements.clear();
		ruleElements.add(firstCondition);
		ruleElements.add(secondCondition);
		ruleElements.add(thirdCondition);
		ruleElements.add(fourthCondition);
		expectedNumberOfRetrievals = ruleElements.size();
		ruleAdapter.createConditionComposition(ruleElements, false);
		assertThat(numberOfCalls).isEqualTo(expectedCallAmount);
		assertThat(numberOfRetrievals).isEqualTo(expectedNumberOfRetrievals);
	}

	/**
	 * Tests that adapter gets conditions, eligibilities and its operators from <code>Rule</code>.
	 */
	@Test
	public void testCreateConditionsDTO() {

		final RuleAdapter ruleAdapter = new RuleAdapter() {
			@Override
			BooleanComponentDTO createConditionComposition(final List<ConditionDTO> conditions, final boolean makeConjunction) {
				return null;
			}
		};
		ruleAdapter.setConditionAdapter(conditionAdapter);

		final Rule rule = mock(Rule.class);
		final Set<RuleCondition> conditions = new HashSet<>();
		CartContainsItemsOfCategoryConditionImpl condition = new CartContainsItemsOfCategoryConditionImpl();
		conditions.add(condition);

		when(rule.getConditions()).thenReturn(conditions);
		when(rule.getConditionOperator()).thenReturn(true);

		ruleAdapter.createConditionsDTO(rule);

		verify(conditionAdapter, times(1)).populateDTO(same(condition), any(ConditionDTO.class));

		verify(rule, times(1)).getConditions();
		verify(rule, times(1)).getConditionOperator();
	}

	/**
	 * Checks that <code>BooleanComponentDTO</code> is used to populate domain object.
	 * This behavior is predefined by Composite design pattern
	 */
	@Test
	public void testPopulateRuleElements() {
		final BooleanComponentDTO source = mock(BooleanComponentDTO.class);
		final Rule target = mock(Rule.class);
		final boolean conditionOperator = true;

		when(source.getCompositeOperator()).thenReturn(conditionOperator);

		ruleAdapter.populateRuleElements(source, target);

		verify(target, times(1)).setConditionOperator(conditionOperator);
		verify(source, times(1)).populateDomainObject(target, conditionAdapter);
		verify(source, times(1)).getCompositeOperator();
	}

	/**
	 * Verifies that type of DTO created by <code>RuleAdapter</code> is <code>RuleDTO</code>.
	 */
	@Test
	public void testCreateDtoObject() {
		assertThat(ruleAdapter.createDtoObject()).isInstanceOf(RuleDTO.class);
	}

	/**
	 * Checks that when no ruleconditions are present, the populate Domain call doesn't throw an exception.
	 */
	@Test
	public void testPopulateDomainWhenNoConditions() {

		RuleDTO source = mock(RuleDTO.class);
		Rule target = mock(Rule.class);
		ConditionsDTO ruleConditions = mock(ConditionsDTO.class);

		when(source.getConditions()).thenReturn(ruleConditions);
		when(ruleConditions.getConditionsComposite()).thenReturn(null);


		try {
			ruleAdapter.populateDomain(source, target);
		} catch (Exception exception) {

			fail("Should not throw NPE");
		}


	}

}
