/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
/**
 * 
 */
package com.elasticpath.tags.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.tags.domain.Condition;
import com.elasticpath.tags.domain.LogicalOperator;
import com.elasticpath.tags.domain.LogicalOperatorType;
import com.elasticpath.tags.domain.TagDefinition;
import com.elasticpath.tags.domain.TagValueType;
import com.elasticpath.tags.service.ConditionValidationFacade;
import com.elasticpath.tags.service.InvalidConditionTreeException;
import com.elasticpath.tags.service.TagDefinitionReader;
import com.elasticpath.validation.domain.ValidationResult;

/**
 * Tests for GroovyConditionDSLBuilder. Condition building basing on the data type resolving is being tested.
 * 
 */
public class GroovyConditionDSLBuilderTest  {

	private GroovyConditionDSLBuilderImpl cBuilder;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private TagDefinitionReader tagDefinitionReader;
	private ConditionValidationFacade tagConditionValidationFacade;
	
	private static final String CART_SUBTOTAL = "CART_SUBTOTAL";
	private static final String SELLING_CHANNEL = "SELLING_CHANNEL";
	private static final String CATEGORIES_VISITED = "CATEGORIES_VISITED";
	private static final String CUSTOMER_AGE_YEARS = "CUSTOMER_AGE_YEARS"; 
	private static final String CUSTOMER_GENDER = "CUSTOMER_GENDER";
	private static final String DAYS_SINCE_LAST_VISIT = "DAYS_SINCE_LAST_VISIT";

	private final TagDefinition cartSubtotalTagDefinition = context.mock(TagDefinition.class, "cartSubtotalTagDefinition");
	private final TagDefinition sellingChannelTagDefinition = context.mock(TagDefinition.class, "sellingChannelTagDefinition");
	private final TagDefinition categoriesVisitedTagDefinition = context.mock(TagDefinition.class, "categoriesVisitedTagDefinition");
	private final TagDefinition customerAgeYearsTagDefinition = context.mock(TagDefinition.class, "customerAgeYearsTagDefinition");
	private final TagDefinition customerGenderTagDefinition = context.mock(TagDefinition.class, "customerGenderTagDefinition");
	private final TagDefinition daysSinceLastVisitTagDefinition = context.mock(TagDefinition.class, "daysSinceLastVisitTagDefinition");

	private final TagValueType moneyTagValueType = context.mock(TagValueType.class, "moneyTagValueType");
	private final TagValueType sellingChannelTagValueType = context.mock(TagValueType.class, "sellingChannelTagValueType");
	private final TagValueType categoriesVisitedTagValueType = context.mock(TagValueType.class, "categoriesVisitedTagValueType");
	private final TagValueType customerAgeYearsTagValueType = context.mock(TagValueType.class, "customerAgeYearsTagValueType");
	private final TagValueType customerGenderTagValueType = context.mock(TagValueType.class, "customerGenderTagValueType");
	private final TagValueType daysSinceLastVisitTagValueType = context.mock(TagValueType.class, "daysSinceLastVisitTagValueType");

	
	/**
	 * Initializing data type names map.
	 */
	@Before
	public void setUp() {
		tagDefinitionReader = context.mock(TagDefinitionReader.class, "TagDefinitionReader");
		tagConditionValidationFacade = new ConditionValidationFacade() {
			@Override
			public ValidationResult validate(final Condition condition)
					throws IllegalArgumentException {
				return ValidationResult.VALID;
			}
			@Override
			public ValidationResult validate(final Condition condition,
					final Object newValue) throws IllegalArgumentException {
				return ValidationResult.VALID;
			}
			@Override
			public ValidationResult validateTree(
					final LogicalOperator logicalOperatorTreeRootNode)
					throws IllegalArgumentException {
				return ValidationResult.VALID;
			}
			
		};
		cBuilder = new GroovyConditionDSLBuilderImpl();
		cBuilder.setTagDefinitionReader(tagDefinitionReader);
		cBuilder.setValidationFacade(tagConditionValidationFacade);
		prepareExpectationsForTagDefinitions();
	}

	private void prepareExpectationsForTagDefinitions() {

		final List<TagDefinition> definitions = new ArrayList<>();
		definitions.add(cartSubtotalTagDefinition);
		definitions.add(sellingChannelTagDefinition);
		definitions.add(categoriesVisitedTagDefinition);
		definitions.add(customerAgeYearsTagDefinition);
		definitions.add(customerGenderTagDefinition);
		definitions.add(daysSinceLastVisitTagDefinition);

		context.checking(new Expectations() {
			{
				allowing(tagDefinitionReader).getTagDefinitions();
				will(returnValue(definitions));

				//CART_SUBTOTAL setup
				allowing(moneyTagValueType).getJavaType();
				will(returnValue(BigDecimal.class.getCanonicalName()));

				allowing(cartSubtotalTagDefinition).getValueType();
				will(returnValue(moneyTagValueType));

				allowing(cartSubtotalTagDefinition).getGuid();
				will(returnValue(CART_SUBTOTAL));

				//SELLING_CHANNEL setup
				allowing(sellingChannelTagValueType).getJavaType();
				will(returnValue(String.class.getCanonicalName()));

				allowing(sellingChannelTagDefinition).getValueType();
				will(returnValue(sellingChannelTagValueType));

				allowing(sellingChannelTagDefinition).getGuid();
				will(returnValue(SELLING_CHANNEL));
				
				//CATEGORIES_VISITED setup
				allowing(categoriesVisitedTagValueType).getJavaType();
				will(returnValue(String.class.getCanonicalName()));

				allowing(categoriesVisitedTagDefinition).getValueType();
				will(returnValue(categoriesVisitedTagValueType));

				allowing(categoriesVisitedTagDefinition).getGuid();
				will(returnValue(CATEGORIES_VISITED));
				
				//CUSTOMER_AGE_YEARS setup
				allowing(customerAgeYearsTagValueType).getJavaType();
				will(returnValue(Integer.class.getCanonicalName()));

				allowing(customerAgeYearsTagDefinition).getValueType();
				will(returnValue(customerAgeYearsTagValueType));

				allowing(customerAgeYearsTagDefinition).getGuid();
				will(returnValue(CUSTOMER_AGE_YEARS));

				//CUSTOMER_GENDER setup
				allowing(customerGenderTagValueType).getJavaType();
				will(returnValue(String.class.getCanonicalName()));

				allowing(customerGenderTagDefinition).getValueType();
				will(returnValue(customerGenderTagValueType));

				allowing(customerGenderTagDefinition).getGuid();
				will(returnValue(CUSTOMER_GENDER));

				//DAYS_SINCE_LAST_VISIT setup
				allowing(daysSinceLastVisitTagValueType).getJavaType();
				will(returnValue(Long.class.getCanonicalName()));

				allowing(daysSinceLastVisitTagDefinition).getValueType();
				will(returnValue(daysSinceLastVisitTagValueType));

				allowing(daysSinceLastVisitTagDefinition).getGuid();
				will(returnValue(DAYS_SINCE_LAST_VISIT));

			}
		});
	}
	
	/**
	 * Tests that the result of the conversion from Conditional Expression string, using DSL builder to root tree, and then conversion of that tree
	 * back returns the original conditional expression string.
	 * @throws InvalidConditionTreeException if the condition tree has an invalid format or data
	 */
	@Test
	public void testConditionStringConversionToLogicalOperatorTree() throws InvalidConditionTreeException {


		String conditionString = " { AND { CART_SUBTOTAL.equalTo (-100.0G) }  } ";

		LogicalOperator root = cBuilder.getLogicalOperationTree(conditionString);
		String result = cBuilder.getConditionalDSLString(root);
		assertEquals(conditionString, result);

		conditionString = " { AND { CART_SUBTOTAL.equalTo (100.0G) }  { "
				+ "OR { SELLING_CHANNEL.equalTo 'UKSTORE' }  { SELLING_CHANNEL.equalTo 'USASTORE' }  }  } ";

		root = cBuilder.getLogicalOperationTree(conditionString);

		assertNull(root.getParentLogicalOperator());
		
		result = cBuilder.getConditionalDSLString(root);
		assertEquals(conditionString, result);


		conditionString = " { AND { CART_SUBTOTAL.equalTo (100.0G) } "
				+ " { OR { CATEGORIES_VISITED.includes 'PANASONIC' }  { CATEGORIES_VISITED.includesIgnoreCase 'SONY' }  } " 
				+	" { AND { CUSTOMER_AGE_YEARS.lessThan (50i) }  { CUSTOMER_GENDER.equalTo 'F' }  }  } ";
		
		root = cBuilder.getLogicalOperationTree(conditionString);
		
		assertNull(root.getParentLogicalOperator());
		
		result = cBuilder.getConditionalDSLString(root);
		assertEquals(conditionString, result);

	
		
	}
	
	/**
	 * Tests performs building logical operator from wrong string and makes sure that operation fails, 
	 * because negative value is not in the brackets. 
	 * @throws InvalidConditionTreeException if the condition tree has an invalid format or data
	 * 
	 */
	@Test
	public void testConditionStringConversionToLogicalOperatorTreeFails() throws InvalidConditionTreeException {

		String incorrectConditionString = " { AND { CART_SUBTOTAL.equalTo '-100' }  } ";

		boolean exceptionThrown = false;
		LogicalOperator root = cBuilder.getLogicalOperationTree(incorrectConditionString);			
		String result = cBuilder.getConditionalDSLString(root);	
		assertFalse(incorrectConditionString.equals(result));
				

		incorrectConditionString = " { AND { CART_SUBTOTAL.equalTo -100 }  } ";

		exceptionThrown = false;
		try {
		cBuilder.getLogicalOperationTree(incorrectConditionString);			
		} catch (Exception exception) {
				exceptionThrown = true;
		}
		
		assertTrue("Exception expected but not thrown ", exceptionThrown);
		
		incorrectConditionString = " { AND { CART_SUBTOTAL.equalTo -100G }  } ";

		exceptionThrown = false;
		try {
		cBuilder.getLogicalOperationTree(incorrectConditionString);			
		} catch (Exception exception) {
				exceptionThrown = true;
		}
		
		assertTrue("Exception expected but not thrown ", exceptionThrown);

	}

	/**
	 * Tests that logical operator is correctly translated into string. 
	 * @throws InvalidConditionTreeException if the condition tree has an invalid format or data
	 * 
	 */
	@Test
	public void testLogicalOperatorToString() throws InvalidConditionTreeException {

		String conditionString = " { AND { CART_SUBTOTAL.greaterThan (1000.0G) }  } ";
		
		final LogicalOperator root = new LogicalOperator(LogicalOperatorType.AND);
		Condition condition = new Condition(cartSubtotalTagDefinition, "greaterThan", new BigDecimal("1000"));
		root.addCondition(condition);
		String result = cBuilder.getConditionalDSLString(root);
		assertEquals(conditionString, result);

	}

}
