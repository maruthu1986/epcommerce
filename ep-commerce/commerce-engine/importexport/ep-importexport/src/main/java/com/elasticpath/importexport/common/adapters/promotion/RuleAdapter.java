/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.importexport.common.adapters.promotion;

import static com.elasticpath.importexport.common.comparators.ExportComparators.CONDITION_DTO_COMPARATOR;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.rules.RuleCondition;
import com.elasticpath.domain.rules.RuleElement;
import com.elasticpath.domain.rules.RuleElementType;
import com.elasticpath.domain.rules.RuleScenarios;
import com.elasticpath.importexport.common.adapters.AbstractDomainAdapterImpl;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.dto.promotion.rule.ActionDTO;
import com.elasticpath.importexport.common.dto.promotion.rule.AndDTO;
import com.elasticpath.importexport.common.dto.promotion.rule.BooleanComponentDTO;
import com.elasticpath.importexport.common.dto.promotion.rule.ConditionDTO;
import com.elasticpath.importexport.common.dto.promotion.rule.ConditionsDTO;
import com.elasticpath.importexport.common.dto.promotion.rule.OrDTO;
import com.elasticpath.importexport.common.dto.promotion.rule.RuleDTO;
import com.elasticpath.importexport.common.exception.runtime.PopulationRuntimeException;

/**
 * The implementation of <code>DomainAdapter</code> interface.<br>
 * It is responsible for data transformation between <code>RuleElement</code> and <code>RuleDTO</code> objects.
 */
public class RuleAdapter extends AbstractDomainAdapterImpl<Rule, RuleDTO> {

	private DomainAdapter<RuleElement, ConditionDTO> conditionAdapter;

	/** String constant for ShoppingCartPromotion type. */
	public static final String SHOPPING_CART_PROMOTION = "ShoppingCartPromotion";

	/** String constant for CatalogPromotion type. */
	public static final String CATALOG_PROMOTION = "CatalogPromotion";

	@Override
	public void populateDTO(final Rule source, final RuleDTO target) {
		target.setCode(source.getCode());

		target.setConditions(createConditionsDTO(source));
		target.setActions(createActionDTOList(source));
	}

	/**
	 * Creates populated ConditionsDTO from Rule eligibilities and conditions.
	 * 
	 * @param source rule containing conditions and eligibilities
	 * @return ConditionsDTO containing conditions and eligibilities
	 */
	ConditionsDTO createConditionsDTO(final Rule source) {
		final ConditionsDTO conditionsDto = new ConditionsDTO();

		final List<ConditionDTO> conditions = new ArrayList<>();
		for (RuleCondition ruleCondition : source.getConditions()) {
			final ConditionDTO conditionDto = new ConditionDTO();
			conditionAdapter.populateDTO(ruleCondition, conditionDto);
			conditions.add(conditionDto);
		}
		Collections.sort(conditions, CONDITION_DTO_COMPARATOR);

		final BooleanComponentDTO limitedConditionDTO = retrieveLimitedUsageCondition(conditions);
		final BooleanComponentDTO couponCodeDTO = retrieveCouponCodeCondition(conditions);

		if (!conditions.isEmpty()) {
			final BooleanComponentDTO eligibilitiesAndConditionsDTO = new AndDTO();
			eligibilitiesAndConditionsDTO.setComponents(Arrays.asList(createConditionComposition(conditions, source.getConditionOperator())));
			conditionsDto.setConditionsComponent(eligibilitiesAndConditionsDTO);
		}

		addCouponCondition(conditionsDto, couponCodeDTO);
		addLimitedCondition(conditionsDto, limitedConditionDTO);

		return conditionsDto;
	}

	private void addLimitedCondition(final ConditionsDTO conditionsDto, final BooleanComponentDTO limitedConditionDTO) {
		if (limitedConditionDTO != null) {
			final BooleanComponentDTO highLevelAndDTO = new AndDTO();
			highLevelAndDTO.setComponents(Arrays.asList(
					limitedConditionDTO,
					conditionsDto.getConditionsComposite()));
			conditionsDto.setConditionsComponent(highLevelAndDTO);
		}
	}

	private void addCouponCondition(final ConditionsDTO conditionsDto, final BooleanComponentDTO couponConditionDTO) {
		if (couponConditionDTO != null) {
			final BooleanComponentDTO highLevelAndDTO = new AndDTO();
			highLevelAndDTO.setComponents(Arrays.asList(
					couponConditionDTO,
					conditionsDto.getConditionsComposite()));
			conditionsDto.setConditionsComponent(highLevelAndDTO);
		}
	}

	/**
	 * Finds <code>ConditionDTO</code> for limited usage and removes it from the list of conditions.
	 *
	 * @param conditions the list of <code>ConditionDTO</code> objects
	 * @return <code>ConditionDTO</code> based on limited usage condition if exists
	 */
	BooleanComponentDTO retrieveLimitedUsageCondition(final List<ConditionDTO> conditions) {
		return retrieveConditionOfType(RuleElementType.LIMITED_USAGE_PROMOTION_CONDITION, conditions);
	}

	/**
	 * Finds <code>ConditionDTO</code> for coupon code and removes it from the list of conditions.
	 *
	 * @param conditions the list of <code>ConditionDTO</code> objects
	 * @return <code>ConditionDTO</code> based on coupon code condition if exists
	 */
	BooleanComponentDTO retrieveCouponCodeCondition(final List<ConditionDTO> conditions) {
		return retrieveConditionOfType(RuleElementType.LIMITED_USE_COUPON_CODE_CONDITION, conditions);
	}

	/**
	 * Finds <code>ConditionDTO</code> for the given type and removes it from the list of conditions.
	 *
	 * @param elementType the <code>RuleElementType</code> to match
	 * @param conditions the list of <code>ConditionDTO</code> objects
	 * @return populated <code>ConditionDTO</code> based on the given type of condition if exists
	 */
	BooleanComponentDTO retrieveConditionOfType(final RuleElementType elementType, final List<ConditionDTO> conditions) {
		final Optional<ConditionDTO> conditionDTO = conditions.stream()
			.filter(condition -> elementType.getPropertyKey().equals(condition.getType()))
			.findAny();

		conditions.removeIf(condition -> elementType.getPropertyKey().equals(condition.getType()));

		return conditionDTO.orElse(null);
	}

	/**
	 * Create a <code>ConditionDTO</code> from the given <code>RuleCondition</code>.
	 *
	 * @param condition the <code>RuleCondition</code>
	 * @return populated <code>ConditionDTO</code>
	 */
	ConditionDTO createConditionDTO(final RuleCondition condition) {
		final ConditionDTO conditionDto = new ConditionDTO();
		conditionAdapter.populateDTO(condition, conditionDto);
		return conditionDto;
	}

	/**
	 * Recursively fills logical DTO composition from the set of <code>ConditionDTO</code> objects.
	 *
	 * @param conditions set of <code>ConditionDTO</code> domain objects
	 * @param makeConjunction compose components with AND operator if true, with OR otherwise
	 * @return logical composition of <code>ConditionDTO</code> objects united by OR and AND operators
	 */
	BooleanComponentDTO createConditionComposition(final List<ConditionDTO> conditions, final boolean makeConjunction) {

		if (conditions.isEmpty()) {
			throw new PopulationRuntimeException("IE-10707");
		}

		if (conditions.size() == 1) {
			return retrieveCondition(conditions);
		} else if (conditions.size() == 2) {
			final BooleanComponentDTO compositionDto = getBooleanClauseDTO(makeConjunction);
			compositionDto.setComponents(Arrays.asList(retrieveCondition(conditions), retrieveCondition(conditions)));
			return compositionDto;
		}

		final BooleanComponentDTO compositionDto = getBooleanClauseDTO(makeConjunction);

		final BooleanComponentDTO conditionDto = retrieveCondition(conditions);

		compositionDto.setComponents(Arrays.asList(conditionDto,
				createConditionComposition(conditions, makeConjunction)));

		return compositionDto;
	}

	/**
	 * Retrieves first <code>ConditionDTO</code> from <code>ConditionDTO</code> and removes it from the list.
	 *
	 * @param conditions the set of <code>ConditionDTO</code> objects
	 * @return populated <code>ConditionDTO</code> object
	 */
	BooleanComponentDTO retrieveCondition(final List<ConditionDTO> conditions) {
		return conditions.remove(0);
	}

	/**
	 * Creates <code>BooleanComponentDTO</code> by logical operator.
	 * 
	 * @param makeConjunction makes conjunction operator AND if true, OR otherwise
	 * @return <code>BooleanComponentDTO</code> instance
	 */
	BooleanComponentDTO getBooleanClauseDTO(final boolean makeConjunction) {
		if (makeConjunction) {
			return new AndDTO();
		}
		return new OrDTO();
	}

	/**
	 * Creates populated RuleElementDTO List from the Set of RuleElements.
	 *
	 * @param source set of RuleElements
	 * @return List of RuleElementDTO
	 */
	List<ActionDTO> createActionDTOList(final Rule source) {
		final ActionDTO actionDto = new ActionDTO();
		
		if (source.getRuleSet().getScenario() == RuleScenarios.CART_SCENARIO) {
			actionDto.setType(SHOPPING_CART_PROMOTION);
		} else {
			actionDto.setType(CATALOG_PROMOTION);
		}
		actionDto.setCode(source.getCode());

		return Arrays.asList(actionDto);
	}

	@Override
	public void populateDomain(final RuleDTO source, final Rule target) {
		final BooleanComponentDTO composite = source.getConditions().getConditionsComposite();
		if (composite != null) {
			populateRuleElements(composite.getComponents().get(0), target);
			if (composite.getComponents().size() > 1) {
				populateRuleElements(composite.getComponents().get(1), target);
			}
		}
	}

	/**
	 * Delegate population of rule elements to <code>BooleanComponentDTO</code> composite.
	 * 
	 * @param source boolean operator represented by DTO component
	 * @param target <code>Rule</code> instance to populate
	 */
	void populateRuleElements(final BooleanComponentDTO source, final Rule target) {
		target.setConditionOperator(source.getCompositeOperator());
		source.populateDomainObject(target, conditionAdapter);
	}

	/**
	 * Sets ConditionAdapter.
	 * 
	 * @param conditionAdapter the ConditionAdapter instance
	 */
	public final void setConditionAdapter(final DomainAdapter<RuleElement, ConditionDTO> conditionAdapter) {
		this.conditionAdapter = conditionAdapter;
	}

	@Override
	public RuleDTO createDtoObject() {
		return new RuleDTO();
	}
}
