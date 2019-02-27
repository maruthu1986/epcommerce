/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.tags.service.impl;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.lang.String.valueOf;
import static java.lang.System.currentTimeMillis;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonMap;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.FutureTask;

import com.google.common.collect.ImmutableList;
import groovy.lang.Script;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.Phases;
import org.junit.Before;
import org.junit.Test;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.cache.SimpleTimeoutCache;
import com.elasticpath.tags.Tag;
import com.elasticpath.tags.TagSet;
import com.elasticpath.tags.domain.ConditionalExpression;
import com.elasticpath.tags.domain.impl.ConditionalExpressionImpl;
import com.elasticpath.tags.service.GroovyConditionProcessingService;
import com.elasticpath.test.MapBasedSimpleTimeoutCache;

/**
 * Test the groovy ConditionalExpressional evaluator evaluates logical operations written in groovy dsl.
 */
@SuppressWarnings({ "PMD.TooManyMethods", "PMD.ExcessiveClassLength" })
public class GroovyConditionEvaluatorServiceImplTest {

	private static final String CS_LOCATION_CONTAINS_US = "{location.contains \"US\"} \n";
	private static final String MEMBER = "member";
	private static final int MINUS_TEN_INTEGER = -10;
	private static final int MINUS_NINE_INTEGER = -9;
	
	private static final float MINUS_TEN_FLOAT = -10.0f;
	private static final float MINUS_NINE_FLOAT = -9.0f;
	
	private static final long MINUS_TEN_LONG = -10L;
	private static final long MINUS_NINE_LONG = -9L;
	
	private static final BigDecimal MINUS_TEN_BIGDECIMAL = new BigDecimal("-10.0");
	private static final BigDecimal MINUS_NINE_BIGDECIMAL = new BigDecimal("-9.0");

	
	private static final int TEN = 10;
	private static final int THREE = 3;
	private static final int FIFTY = 50;
	private static final int THIRTY = 30;
	private static final int FIVE = 5;
	private static final String FOO = "foo";
	private static final String AGE = "age";
	private static final String NAME = "name";
	private static final String LOCATION = "location";
	private static final String SEARCH_TERMS = "SEARCH_TERMS";
	private static final String INSTORE_SEARCH_TERMS = "INSTORE_SEARCH_TERMS";
	
	private static final float FLOAT_ONE = 1.1f;
	private static final float FLOAT_TWO = 2.2f;
	private static final float FLOAT_THREE = 3.3f;

	private static final BigDecimal BIGDECIMAL_ONE = new BigDecimal("11.111");
	private static final BigDecimal BIGDECIMAL_TWO = new BigDecimal("22.222");
	private static final BigDecimal BIGDECIMAL_THREE = new BigDecimal("33.333");

	private final GroovyConditionProcessingServiceImpl processor = new GroovyConditionProcessingServiceImpl();

	private final GroovyConditionEvaluatorServiceImpl evaluator = new GroovyConditionEvaluatorServiceImpl();

	@Before
	public void setUp() {
		final SimpleTimeoutCache<String, FutureTask<Script>> processorCache = new MapBasedSimpleTimeoutCache<>();
		processor.setScriptCache(processorCache);

		final SimpleTimeoutCache<ConditionEvaluationCacheKey, Boolean> evaluationCache = new MapBasedSimpleTimeoutCache<>();

		evaluator.setConditionProcessingService(processor);
		evaluator.setEvaluationCache(evaluationCache);
	}

	/**
	 * Test an empty condition.
	 */
	@Test
	public void testEmptyCondition() {
		ConditionalExpression condition = new ConditionalExpressionImpl();
		condition.setGuid(valueOf(currentTimeMillis()));
		String conditionString = EMPTY;
		condition.setConditionString(conditionString);
		final Map<String, Tag> trueMap = singletonMap(
			AGE, new Tag(TEN)
		);

		assertThat(evaluator.evaluateConditionOnMap(trueMap, condition)).isTrue();
	}

	/**
	 * Test the operator lessThan.
	 */
	@Test
	public void testLessThanOperator() {
		ConditionalExpression condition = givenAConditionWithString("age.lessThan 9");
		Map<String, Tag> trueMap = singletonMap(
			AGE, new Tag(0)
		);

		assertThat(evaluator.evaluateConditionOnMap(trueMap, condition)).isTrue();

		final Map<String, Tag> falseMap = singletonMap(
			AGE, new Tag(FIFTY)
		);

		assertThat(evaluator.evaluateConditionOnMap(falseMap, condition)).isFalse();
	}
	
	
	/**
	 * Test negative integer values support in tag framework. 
	 */
	@Test
	public void testNegativeInteger() {
		ConditionalExpression condition = givenAConditionWithString("age.equalTo (-9)");
		final Map<String, Tag> trueMap = new HashMap<>();
		trueMap.put(AGE, new Tag(MINUS_TEN_INTEGER));
		assertThat(evaluator.evaluateConditionOnMap(trueMap, condition)).isFalse();

		trueMap.clear();
		trueMap.put(AGE, new Tag(MINUS_NINE_INTEGER));
		assertThat(evaluator.evaluateConditionOnMap(trueMap, condition)).isTrue();

		condition = givenAConditionWithString("age.lessThan (-9)");
		trueMap.clear();
		trueMap.put(AGE, new Tag(MINUS_TEN_INTEGER));
		assertThat(evaluator.evaluateConditionOnMap(trueMap, condition)).isTrue();
	}
	
	
	/**
	 * Test negative float values support in tag framework. 
	 */
	@Test
	public void testNegativeFloat() {
		ConditionalExpression condition = givenAConditionWithString("age.equalTo (-9f)");
		final Map<String, Tag> trueMap = new HashMap<>();
		trueMap.put(AGE, new Tag(MINUS_TEN_FLOAT));
		assertThat(evaluator.evaluateConditionOnMap(trueMap, condition)).isFalse();

		trueMap.clear();
		trueMap.put(AGE, new Tag(MINUS_NINE_FLOAT));
		assertThat(evaluator.evaluateConditionOnMap(trueMap, condition)).isTrue();

		condition = givenAConditionWithString("age.lessThan (-9f)");
		trueMap.clear();
		trueMap.put(AGE, new Tag(MINUS_TEN_FLOAT));
		assertThat(evaluator.evaluateConditionOnMap(trueMap, condition)).isTrue();
	}
	
	/**
	 * Test negative float values support in tag framework. 
	 */
	@Test
	public void testNegativeLong() {
		ConditionalExpression condition = givenAConditionWithString("age.equalTo (-9L)");
		final Map<String, Tag> trueMap = new HashMap<>();
		trueMap.put(AGE, new Tag(MINUS_TEN_LONG));
		assertThat(evaluator.evaluateConditionOnMap(trueMap, condition)).isFalse();

		trueMap.clear();
		trueMap.put(AGE, new Tag(MINUS_NINE_LONG));
		assertThat(evaluator.evaluateConditionOnMap(trueMap, condition)).isTrue();

		condition = givenAConditionWithString("age.lessThan (-9L)");
		trueMap.clear();
		trueMap.put(AGE, new Tag(MINUS_TEN_LONG));
		assertThat(evaluator.evaluateConditionOnMap(trueMap, condition)).isTrue();
	}
	
	/**
	 * Test negative big decimal values support in tag framework. 
	 * WARNING !!! See BigDecimal equals method for more details about big decimal compare. 
	 */
	@Test
	public void testNegativeBigDecimal() {
		ConditionalExpression condition = givenAConditionWithString("age.equalTo (-9.0G)");
		Map<String, Tag> trueMap = new HashMap<>();
		trueMap.put(AGE, new Tag(MINUS_TEN_BIGDECIMAL));
		assertThat(evaluator.evaluateConditionOnMap(trueMap, condition)).isFalse();

		trueMap.clear();
		trueMap.put(AGE, new Tag(MINUS_NINE_BIGDECIMAL));
		assertThat(evaluator.evaluateConditionOnMap(trueMap, condition)).isTrue();

		condition = givenAConditionWithString("age.lessThan (-9.0G)");
		trueMap.clear();
		trueMap.put(AGE, new Tag(MINUS_TEN_BIGDECIMAL));
		assertThat(evaluator.evaluateConditionOnMap(trueMap, condition)).isTrue();
	}
	

	/**
	 * Test the operator lessThan.
	 * This test show string usage instead of digits for compare digits values.
	 */
	@Test
	public void testLessThanOperator2() {
		// Looks like tag framework still compare strings instead of digits
		ConditionalExpression condition = givenAConditionWithString("age.lessThan (9)");
		Map<String, Tag> trueMap = singletonMap(
			AGE, new Tag(TEN)
		);
		assertThat(evaluator.evaluateConditionOnMap(trueMap, condition)).isFalse();

		Map<String, Tag> trueMap2 = singletonMap(
			AGE, new Tag(FIVE)
		);

		assertThat(evaluator.evaluateConditionOnMap(trueMap2, condition)).isTrue();

		Map<String, Tag> trueMap3 = singletonMap(
			AGE, new Tag(THIRTY)
		);

		assertThat(evaluator.evaluateConditionOnMap(trueMap3, condition)).isFalse(); //here must be assertFalse


	}
	
	/**
	 * Test the operator greaterThan.
	 */
	@Test
	public void testGreaterThanOperator() {
		ConditionalExpression condition = givenAConditionWithString("age.greaterThan 2");
		Map<String, Tag> trueMap = singletonMap(
			AGE, new Tag(THREE)
		);

		assertThat(evaluator.evaluateConditionOnMap(trueMap, condition)).isTrue();

		Map<String, Tag> falseMap = singletonMap(
			AGE, new Tag(1)
		);

		assertThat(evaluator.evaluateConditionOnMap(falseMap, condition)).isFalse();
	}

	/**
	 * Test the operator lessThanOrEqualTo.
	 */
	@Test
	public void testLessThanOrEqualToOperator() {
		ConditionalExpression condition = givenAConditionWithString("age.lessThanOrEqualTo 30");
		Map<String, Tag> trueMap = singletonMap(
			AGE, new Tag(THIRTY)
		);

		assertThat(evaluator.evaluateConditionOnMap(trueMap, condition)).isTrue();

		Map<String, Tag> trueMap2 = singletonMap(
			AGE, new Tag(FIVE)
		);

		assertThat(evaluator.evaluateConditionOnMap(trueMap2, condition)).isTrue();

		Map<String, Tag> falseMap = singletonMap(
			AGE, new Tag(FIFTY)
		);

		assertThat(evaluator.evaluateConditionOnMap(falseMap, condition)).isFalse();
	}

	/**
	 * Test the operator greaterThanOrEqualTo.
	 */
	@Test
	public void testGreaterThanOrEqualToOperator() {
		ConditionalExpression condition = givenAConditionWithString("age.greaterThanOrEqualTo 30");
		Map<String, Tag> trueMap = singletonMap(
			AGE, new Tag(THIRTY)
		);

		assertThat(evaluator.evaluateConditionOnMap(trueMap, condition)).isTrue();

		Map<String, Tag> trueMap2 = singletonMap(
			AGE, new Tag(FIFTY)
		);

		assertThat(evaluator.evaluateConditionOnMap(trueMap2, condition)).isTrue();

		Map<String, Tag> falseMap = singletonMap(
			AGE, new Tag(FIVE)
		);

		assertThat(evaluator.evaluateConditionOnMap(falseMap, condition)).isFalse();
	}

	/**
	 * Test the operator equalTo.
	 */
	@Test
	public void testEqualToOperator() {
		ConditionalExpression condition = givenAConditionWithString("foo.equalTo 'bar'");
		Map<String, Tag> trueMap = singletonMap(
			FOO, new Tag("bar")
		);

		assertThat(evaluator.evaluateConditionOnMap(trueMap, condition)).isTrue();

		Map<String, Tag> falseMap = singletonMap(
			FOO, new Tag(FOO)
		);

		assertThat(evaluator.evaluateConditionOnMap(falseMap, condition)).isFalse();
	}

	/**
	 * Test the operator notEqualTo.
	 */
	@Test
	public void testNotEqualToOperator() {
		ConditionalExpression condition = givenAConditionWithString("foo.notEqualTo \"bar\"");
		Map<String, Tag> trueMap = singletonMap(
			FOO, new Tag(FOO)
		);

		assertThat(evaluator.evaluateConditionOnMap(trueMap, condition)).isTrue();

		Map<String, Tag> falseMap = singletonMap(
			FOO, new Tag("bar")
		);

		assertThat(evaluator.evaluateConditionOnMap(falseMap, condition)).isFalse();
	}
	
	/**
	 * Test includes operation on list values.
	 * Intersection between lists will be used 
	 * in VisitedCategoryTagger.
	 */
	@Test
	public void testIntersection() {

		ConditionalExpression condition = new ConditionalExpressionImpl();
		condition.setConditionString("{ AND { AND { SEARCH_TERMS.includes 'one' }  }  { AND { SEARCH_TERMS.includes 'two' }  }  }");
		condition.setGuid(valueOf(currentTimeMillis() + "_1"));

		Map<String, Tag> searchTermMap = singletonMap(
			SEARCH_TERMS, new Tag("one,two")
		);

		assertThat(evaluator.evaluateConditionOnMap(searchTermMap, condition)).isTrue();

		searchTermMap = singletonMap(
			SEARCH_TERMS, new Tag("two,one")
		);

		assertThat(evaluator.evaluateConditionOnMap(searchTermMap, condition)).isTrue();

		searchTermMap = singletonMap(
			SEARCH_TERMS, new Tag("two, zzz, one")
		);

		assertThat(evaluator.evaluateConditionOnMap(searchTermMap, condition)).isTrue();

		searchTermMap = singletonMap(
			SEARCH_TERMS, new Tag("'two', 'zzz', 'one'")
		);

		assertThat(evaluator.evaluateConditionOnMap(searchTermMap, condition)).isTrue();

		condition = new ConditionalExpressionImpl();
		condition.setConditionString("{ AND { SEARCH_TERMS.includes 'category code 1' }  { SEARCH_TERMS.includes 'category code 2' }  }");
		condition.setGuid(valueOf(currentTimeMillis() + "_2"));

		condition = new ConditionalExpressionImpl();

		condition.setConditionString("{ AND { SEARCH_TERMS.includes 'category' }  { SEARCH_TERMS.includes 'code' }  }");
		condition.setGuid(valueOf(currentTimeMillis() + "_3"));

		searchTermMap = singletonMap(
			SEARCH_TERMS, new Tag("category,code")
		);


		assertThat(evaluator.evaluateConditionOnMap(searchTermMap, condition)).isTrue();

	}
	
	/**
	 * Test the operator includes.
	 */
	@Test
	public void testIncludesOperator() {
		ConditionalExpression condition = givenAConditionWithString("foo.includes \"bar\"");
		Map<String, Tag> trueMap = singletonMap(
			FOO, new Tag("foobar")
		);

		assertThat(evaluator.evaluateConditionOnMap(trueMap, condition)).isTrue();

		Map<String, Tag> falseMap = singletonMap(
			FOO, new Tag(FOO)
		);

		assertThat(evaluator.evaluateConditionOnMap(falseMap, condition)).isFalse();
	}
	
	/**
	 * Test the operator includesIgnoreCase.
	 */
	@Test
	public void testIncludesIgnoreCase() {
		ConditionalExpression condition = givenAConditionWithString("foo.includesIgnoreCase \"Foo\"");
		Map<String, Tag> trueMap = singletonMap(
			FOO, new Tag("OnefOOTwo")
		);

		assertThat(evaluator.evaluateConditionOnMap(trueMap, condition)).isTrue();

		Map<String, Tag> falseMap = singletonMap(
			FOO, new Tag("zoo")
		);

		assertThat(evaluator.evaluateConditionOnMap(falseMap, condition)).isFalse();
	}
	
	/**
	 * Test the operator equalsIgnoreCase.
	 */
	@Test
	public void testEqualsIgnoreCase() {
		ConditionalExpression condition = givenAConditionWithString("foo.equalsIgnoreCase 'bAr'");
		Map<String, Tag> trueMap = singletonMap(
			FOO, new Tag("bar")
		);

		assertThat(evaluator.evaluateConditionOnMap(trueMap, condition)).isTrue();

		Map<String, Tag> falseMap = singletonMap(
			FOO, new Tag(FOO)
		);

		assertThat(evaluator.evaluateConditionOnMap(falseMap, condition)).isFalse();
	}
	
	
	/**
	 * Test evaluation of nested logical conditions produced by UI.
	 */
	@Test
	public void testConditionAfterUI() {
		ConditionalExpression condition = new ConditionalExpressionImpl();
		// This condition produced by UI. Test will be failed.
		/*condition.setConditionString(
				"{ AND " 
				+
				"{ AND { INSTORE_SEARCH_TERMS.includesIgnoreCase 'Julia' }  }  " 
				+
				"{ OR  { INSTORE_SEARCH_TERMS.equalsIgnoreCase 'zzz' }  }  " 
				+
				"}");*/
		condition.setConditionString(
			"{ OR "
			+
			"{ INSTORE_SEARCH_TERMS.includesIgnoreCase 'Julia' }   "
			+
			"{ INSTORE_SEARCH_TERMS.equalsIgnoreCase 'zzz' }   "
			+
			"}");

		condition.setGuid(valueOf(currentTimeMillis() + "_1"));

		Map<String, Tag> trueMap = singletonMap(
			INSTORE_SEARCH_TERMS, new Tag("Julia")
		);

		assertThat(evaluator.evaluateConditionOnMap(trueMap, condition)).isTrue();

	}

	
	//  
	

	/**
	 * Test the operator notIncludes.
	 */
	@Test
	public void testNotIncludesOperator() {
		ConditionalExpression condition = givenAConditionWithString("foo.notIncludes \"bar\"");
		Map<String, Tag> trueMap = singletonMap(
			FOO, new Tag(FOO)
		);

		assertThat(evaluator.evaluateConditionOnMap(trueMap, condition)).isTrue();

		Map<String, Tag> falseMap = singletonMap(
			FOO, new Tag("foobar")
		);

		assertThat(evaluator.evaluateConditionOnMap(falseMap, condition)).isFalse();
	}



	/**
	 * Empty conditions should evaluateConditionOnMap() to true.
	 */
	@Test
	public void testExpressionEvaluationTrueIfEmpty() {
		ConditionalExpression condition = new ConditionalExpressionImpl();
		condition.setGuid(valueOf(currentTimeMillis()));
		condition.setConditionString(EMPTY);
		assertThat(evaluator.evaluateConditionOnMap(new HashMap<>(), condition)).isTrue();
	}


	/**
	 * Test evaluation of a singular contains condition in an AND clause
	 * (all non nested conditions are assumed to be AND-ed in the end)
	 * location.contains "US".
	 */
	@Test
	public void testExpressionEvaluationSingularContains() {
		ConditionalExpression condition = givenAConditionWithString("location.contains \"US\"");
		Map<String, Tag> trueMap = singletonMap(
			LOCATION, new Tag("US")
		);

		assertThat(evaluator.evaluateConditionOnMap(trueMap, condition)).isTrue();

		Map<String, Tag> falseMap = singletonMap(
			LOCATION, new Tag("CANADA")
		);

		assertThat(evaluator.evaluateConditionOnMap(falseMap, condition)).isFalse();
	}


	/**
	 * Test evaluation of a singular IS condition in an AND clause
	 * (all non nested conditions are assumed to be AND-ed in the end)
	 * name.is "smith".
	 */
	@Test
	public void testExpressionNonExistantOperatorIs() {
		ConditionalExpression condition = givenAConditionWithString("name.is \"smith\"");
		Map<String, Tag> trueMap = singletonMap(
			NAME, new Tag("smith")
		);

		assertThat(evaluator.evaluateConditionOnMap(trueMap, condition)).isFalse();
	}

	/**
	 * Test evaluation of multiple conditions in an AND clause.
	 * {AND
	 * 	{location.contains "US"}
	 *  {name.is "obama"}
	 *  {title.is "president"}
	 * }
	 */
	@Test
	public void testExpressionEvaluationAND() {
		ConditionalExpression condition = new ConditionalExpressionImpl();
		condition.setGuid(valueOf(currentTimeMillis()));

		String conditionString = "\t{AND \n"
								 + CS_LOCATION_CONTAINS_US
								 + "{name.equalTo \"obama\"} \n"
								 + "{title.equalTo \"president\"}}\n";
		condition.setConditionString(conditionString);


		Map<String, Tag> trueMap = new HashMap<>();
		trueMap.put(LOCATION, new Tag("US"));
		trueMap.put(NAME, new Tag("obama"));
		trueMap.put("title", new Tag("president"));

		assertThat(evaluator.evaluateConditionOnMap(trueMap, condition)).isTrue();

		Map<String, Tag> falseMap = singletonMap(
			LOCATION, new Tag("CANADA")
		);

		assertThat(evaluator.evaluateConditionOnMap(falseMap, condition)).isFalse();
	}


	/**
	 * Test evaluation of multiple conditions in an OR clause.
	 * {OR
	 * 	{location.contains "US"}
	 *  {name.is "obama"}
	 *  {title.is "president"}
	 * }
	 */
	@Test
	public void testExpressionEvaluationOR() {
		ConditionalExpression condition = new ConditionalExpressionImpl();
		condition.setGuid(valueOf(currentTimeMillis()));

		String conditionString = "\t{OR \n"
								 + CS_LOCATION_CONTAINS_US
								 + "{name.equalTo \"jones\"} \n"
								 + "{title.equalTo \"president\"}}\n";
		condition.setConditionString(conditionString);

		Map<String, Tag> trueMap = singletonMap(
			"title", new Tag("president")
		);
		assertThat(evaluator.evaluateConditionOnMap(trueMap, condition)).isTrue();

		Map<String, Tag> trueMap2 = singletonMap(
			LOCATION, new Tag("US")
		);
		assertThat(evaluator.evaluateConditionOnMap(trueMap2, condition)).isTrue();

		Map<String, Tag> trueMap3 = singletonMap(
			NAME, new Tag("jones")
		);
		assertThat(evaluator.evaluateConditionOnMap(trueMap3, condition)).isTrue();

		Map<String, Tag> falseMap = singletonMap(
			NAME, new Tag("foobar")
		);
		assertThat(evaluator.evaluateConditionOnMap(falseMap, condition)).isFalse();
	}

	/**
	 * Test evaluation of nested logical conditions.
	 *
	 * User is in the US AND is named obama, AND is either gold or platinum member,
	 * AND is out of the age 16-60 range
	 * {AND
	 * 	{location.contains "US"}
	 *  {name.is "obama"}
	 *  {OR
	 *  	{member.is "gold"}
	 *  	{member.is "platinum"}
	 *  }
	 *  {OR
	 *  	{AND
	 *  		{ age.lessThan 16 }
	 *  		{ age.greaterThan 60 }
	 *  	}
	 *  }
	 * }
	 */
	@Test
	public void testExpressionEvaluationNested() {
		ConditionalExpression condition = new ConditionalExpressionImpl();
		condition.setGuid(valueOf(currentTimeMillis()));
		final String name = "obama";
		final String age = AGE;

		String conditionString =
			"{AND \n"
			+ CS_LOCATION_CONTAINS_US
			+ "{name.equalTo \"obama\"} \n"
			+ "{OR \n"
			+ "  {member.contains \"gold\"} \n"
			+ "  {member.contains \"platinum\"}} \n"
			+ "{AND \n"
			+ "  {AND \n"
			+ "    { age.lessThan 60 } \n"
			+ "    { age.greaterThan 16 } }\n"
			+ "} }\n";
		condition.setConditionString(conditionString);

		Map<String, Tag> trueMap = new HashMap<>();
		trueMap.put(LOCATION, new Tag("US"));
		trueMap.put(NAME, new Tag(name));
		trueMap.put(MEMBER, new Tag("gold"));
		trueMap.put(age, new Tag(FIFTY));

		assertThat(evaluator.evaluateConditionOnMap(trueMap, condition)).isTrue();

		//No member
		Map<String, Tag> falseMap = new HashMap<>();
		falseMap.put(LOCATION, new Tag("US"));
		falseMap.put(NAME, new Tag(name));
		falseMap.put(age, new Tag(FIVE));

		assertThat(evaluator.evaluateConditionOnMap(falseMap, condition)).isFalse();

		//Age in 16-60 range
		Map<String, Tag> falseMap2 = new HashMap<>();
		falseMap2.put(LOCATION, new Tag("US"));
		falseMap2.put(NAME, new Tag(name));
		falseMap2.put(MEMBER, new Tag("gold"));
		falseMap2.put(age, new Tag(FIFTY));

		assertThat(evaluator.evaluateConditionOnMap(falseMap2, condition)).isTrue();
	}

	/******************** Float tests ******************************/
	
	/**
	 * Test the operator equalTo.
	 */
	@Test
	public void testFloatEqualToOperator() {
		ConditionalExpression condition = givenAConditionWithString("foo.equalTo 2.2f");
		Map<String, Tag> trueMap = singletonMap(
			FOO, new Tag(FLOAT_TWO)
		);

		assertThat(evaluator.evaluateConditionOnMap(trueMap, condition)).isTrue();

		Map<String, Tag> falseMap = singletonMap(
			FOO, new Tag(FLOAT_THREE)
		);

		assertThat(evaluator.evaluateConditionOnMap(falseMap, condition)).isFalse();
	}

	/**
	 * Test the operator greaterThan.
	 */
	@Test
	public void testFloatGreaterThanOperator() {
		ConditionalExpression condition = givenAConditionWithString("foo.greaterThan 2.2f");
		Map<String, Tag> trueMap = singletonMap(
			FOO, new Tag(FLOAT_THREE)
		);

		assertThat(evaluator.evaluateConditionOnMap(trueMap, condition)).isTrue();

		Map<String, Tag> falseMap = singletonMap(
			FOO, new Tag(FLOAT_ONE)
		);

		assertThat(evaluator.evaluateConditionOnMap(falseMap, condition)).isFalse();
	}

	/**
	 * Test the operator lessThan.
	 */
	@Test
	public void testFloatLessThanOperator() {
		ConditionalExpression condition = givenAConditionWithString("foo.lessThan 2.2f");
		Map<String, Tag> trueMap = singletonMap(
			FOO, new Tag(FLOAT_ONE)
		);

		assertThat(evaluator.evaluateConditionOnMap(trueMap, condition)).isTrue();

		Map<String, Tag> falseMap = singletonMap(
			FOO, new Tag(FLOAT_THREE)
		);

		assertThat(evaluator.evaluateConditionOnMap(falseMap, condition)).isFalse();
	}

	/**
	 * Test the operator greaterThanOrEqualTo.
	 */
	@Test
	public void testFloatGreaterThanOrEqualToOperator() {
		ConditionalExpression condition = givenAConditionWithString("foo.greaterThanOrEqualTo 2.2f");
		Map<String, Tag> trueMap = singletonMap(
			FOO, new Tag(FLOAT_THREE)
		);

		assertThat(evaluator.evaluateConditionOnMap(trueMap, condition)).isTrue();

		Map<String, Tag> trueMap2 = singletonMap(
			FOO, new Tag(FLOAT_TWO)
		);

		assertThat(evaluator.evaluateConditionOnMap(trueMap2, condition)).isTrue();

		Map<String, Tag> falseMap = singletonMap(
			FOO, new Tag(FLOAT_ONE)
		);

		assertThat(evaluator.evaluateConditionOnMap(falseMap, condition)).isFalse();
	}

	/**
	 * Test the operator lessThanOrEqualTo.
	 */
	@Test
	public void testFloatLessThanOrEqualToOperator() {
		ConditionalExpression condition = givenAConditionWithString("foo.lessThanOrEqualTo 2.2f");
		Map<String, Tag> trueMap = singletonMap(
			FOO, new Tag(FLOAT_ONE)
		);

		assertThat(evaluator.evaluateConditionOnMap(trueMap, condition)).isTrue();

		Map<String, Tag> trueMap2 = singletonMap(
			FOO, new Tag(FLOAT_TWO)
		);

		assertThat(evaluator.evaluateConditionOnMap(trueMap2, condition)).isTrue();

		Map<String, Tag> falseMap = singletonMap(
			FOO, new Tag(FLOAT_THREE)
		);

		assertThat(evaluator.evaluateConditionOnMap(falseMap, condition)).isFalse();
	}

	/****************** BigDecimal tests *********************/
	/**
	 * Test the operator equalTo.
	 */
	@Test
	public void testBigDecimalEqualToOperator() {
		ConditionalExpression condition = givenAConditionWithString("foo.equalTo 22.222G");
		Map<String, Tag> trueMap = singletonMap(
			FOO, new Tag(BIGDECIMAL_TWO)
		);

		assertThat(evaluator.evaluateConditionOnMap(trueMap, condition)).isTrue();

		Map<String, Tag> falseMap = singletonMap(
			FOO, new Tag(BIGDECIMAL_ONE)
		);

		assertThat(evaluator.evaluateConditionOnMap(falseMap, condition)).isFalse();
	}

	/**
	 * Test the operator greaterThan.
	 */
	@Test
	public void testBigDecimalGreaterThanOperator() {
		ConditionalExpression condition = givenAConditionWithString("foo.greaterThan 22.222G");
		Map<String, Tag> trueMap = singletonMap(
			FOO, new Tag(BIGDECIMAL_THREE)
		);

		assertThat(evaluator.evaluateConditionOnMap(trueMap, condition)).isTrue();

		Map<String, Tag> falseMap = singletonMap(
			FOO, new Tag(BIGDECIMAL_ONE)
		);

		assertThat(evaluator.evaluateConditionOnMap(falseMap, condition)).isFalse();
	}

	/**
	 * Test the operator lessThan.
	 */
	@Test
	public void testBigDecimalLessThanOperator() {
		ConditionalExpression condition = givenAConditionWithString("foo.lessThan 22.222G");
		Map<String, Tag> trueMap = singletonMap(
			FOO, new Tag(BIGDECIMAL_ONE)
		);

		assertThat(evaluator.evaluateConditionOnMap(trueMap, condition)).isTrue();

		Map<String, Tag> falseMap = singletonMap(
			FOO, new Tag(BIGDECIMAL_THREE)
		);

		assertThat(evaluator.evaluateConditionOnMap(falseMap, condition)).isFalse();
	}

	/**
	 * Test the operator greaterThanOrEqualTo.
	 */
	@Test
	public void testBigDecimalGreaterThanOrEqualToOperator() {
		ConditionalExpression condition = givenAConditionWithString("foo.greaterThanOrEqualTo 22.222G");
		Map<String, Tag> trueMap = singletonMap(
			FOO, new Tag(BIGDECIMAL_THREE)
		);

		assertThat(evaluator.evaluateConditionOnMap(trueMap, condition)).isTrue();

		Map<String, Tag> trueMap2 = singletonMap(
			FOO, new Tag(BIGDECIMAL_TWO)
		);

		assertThat(evaluator.evaluateConditionOnMap(trueMap2, condition)).isTrue();

		Map<String, Tag> falseMap = singletonMap(
			FOO, new Tag(BIGDECIMAL_ONE)
		);

		assertThat(evaluator.evaluateConditionOnMap(falseMap, condition)).isFalse();
	}

	/**
	 * Test the operator lessThanOrEqualTo.
	 */
	@Test
	public void testBigDecimalLessThanOrEqualToOperator() {
		ConditionalExpression condition = givenAConditionWithString("foo.lessThanOrEqualTo 22.222G");
		Map<String, Tag> trueMap = singletonMap(
			FOO, new Tag(BIGDECIMAL_ONE)
		);

		assertThat(evaluator.evaluateConditionOnMap(trueMap, condition)).isTrue();

		Map<String, Tag> trueMap2 = singletonMap(
			FOO, new Tag(BIGDECIMAL_TWO)
		);

		assertThat(evaluator.evaluateConditionOnMap(trueMap2, condition)).isTrue();

		Map<String, Tag> falseMap = singletonMap(
			FOO, new Tag(BIGDECIMAL_THREE)
		);

		assertThat(evaluator.evaluateConditionOnMap(falseMap, condition)).isFalse();
	}
	
	/****************** Boolean tests *********************/
	/**
	 * Test the operator equalTo.
	 */
	@Test
	public void testBooleanEqualToOperator() {
		ConditionalExpression condition = givenAConditionWithString("foo.equalTo true");
		Map<String, Tag> trueMap = singletonMap(
			FOO, new Tag(TRUE)
		);

		assertThat(evaluator.evaluateConditionOnMap(trueMap, condition)).isTrue();

		Map<String, Tag> falseMap = singletonMap(
			FOO, new Tag(FALSE)
		);

		assertThat(evaluator.evaluateConditionOnMap(falseMap, condition)).isFalse();
	}
	
	/**
	 * Test the operator greaterThan.
	 */
	@Test
	public void testBooleanGreaterThanOperator() {
		ConditionalExpression condition = givenAConditionWithString("foo.greaterThan true");
		Map<String, Tag> trueMap = singletonMap(
			FOO, new Tag(TRUE)
		);

		assertThat(evaluator.evaluateConditionOnMap(trueMap, condition)).isFalse();

		Map<String, Tag> falseMap = singletonMap(
			FOO, new Tag(FALSE)
		);

		assertThat(evaluator.evaluateConditionOnMap(falseMap, condition)).isFalse();
	}
	
	/**
	 * Test the operator lessThan.
	 */
	@Test
	public void testBooleanLessThanOperator() {
		ConditionalExpression condition = givenAConditionWithString("foo.lessThan true");
		Map<String, Tag> trueMap = singletonMap(
			FOO, new Tag(TRUE)
		);

		assertThat(evaluator.evaluateConditionOnMap(trueMap, condition)).isFalse();

		Map<String, Tag> falseMap = singletonMap(
			FOO, new Tag(FALSE)
		);

		assertThat(evaluator.evaluateConditionOnMap(falseMap, condition)).isTrue();
	}

	/**
	 * Test the operator greaterThanOrEqualTo.
	 */
	@Test
	public void testBooleanGreaterThanOrEqualToOperator() {
		ConditionalExpression condition = givenAConditionWithString("foo.greaterThanOrEqualTo true");
		Map<String, Tag> trueMap = singletonMap(
			FOO, new Tag(TRUE)
		);

		assertThat(evaluator.evaluateConditionOnMap(trueMap, condition)).isTrue();

		Map<String, Tag> falseMap = singletonMap(
			FOO, new Tag(FALSE)
		);

		assertThat(evaluator.evaluateConditionOnMap(falseMap, condition)).isFalse();
	}

	/**
	 * Test the operator lessThanOrEqualTo.
	 */
	@Test
	public void testBooleanLessThanOrEqualToOperator() {
		ConditionalExpression condition = givenAConditionWithString("foo.lessThanOrEqualTo true");
		Map<String, Tag> trueMap = singletonMap(
			FOO, new Tag(TRUE)
		);

		assertThat(evaluator.evaluateConditionOnMap(trueMap, condition)).isTrue();

		Map<String, Tag> falseMap = singletonMap(
			FOO, new Tag(FALSE)
		);

		assertThat(evaluator.evaluateConditionOnMap(falseMap, condition)).isTrue();
	}

	@Test(expected = EpServiceException.class)
	public void testExceptionProcessingThrowsServiceException() throws Exception {
		ConditionalExpression condition = givenAConditionWithString("foo.equalTo 'barf'");
		GroovyConditionProcessingService mockProcessor = mock(GroovyConditionProcessingService.class);
		when(mockProcessor.preprocess(condition)).thenThrow(new CompilationFailedException(Phases.ALL, null));

		GroovyConditionEvaluatorServiceImpl failingEvaluator = new GroovyConditionEvaluatorServiceImpl();
		failingEvaluator.setConditionProcessingService(mockProcessor);

		Map<String, Tag> tagMap = singletonMap(FOO, new Tag("barf"));
		failingEvaluator.evaluateConditionOnMap(tagMap, condition);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testCacheMiss() {
		SimpleTimeoutCache<ConditionEvaluationCacheKey, Boolean> cache = mock(SimpleTimeoutCache.class);
		GroovyConditionEvaluatorServiceImpl cachingEvaluator = givenEvaluatorWithMockCache(cache);

		ConditionalExpression condition = givenAConditionWithString("foo.equalTo 'blah'");
		TagSet tagSet = new TagSet();
		tagSet.addTag(FOO, new Tag("blah"));

		ConditionEvaluationCacheKey expectedKey = new ConditionEvaluationCacheKey(tagSet.getTags(), condition, emptyList());
		when(cache.get(expectedKey)).thenReturn(null);

		boolean result = cachingEvaluator.evaluateConditionOnTags(tagSet, condition);
		verify(cache).put(expectedKey, result);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testCacheHit() {
		SimpleTimeoutCache<ConditionEvaluationCacheKey, Boolean> cache = mock(SimpleTimeoutCache.class);
		GroovyConditionEvaluatorServiceImpl cachingEvaluator = givenEvaluatorWithMockCache(cache);

		ConditionalExpression condition = givenAConditionWithString("foo.equalTo 'baz'");
		TagSet tagSet = new TagSet();
		tagSet.addTag(FOO, new Tag("baz"));

		ConditionEvaluationCacheKey expectedKey = new ConditionEvaluationCacheKey(tagSet.getTags(), condition, emptyList());
		when(cache.get(expectedKey)).thenReturn(TRUE);

		boolean result = cachingEvaluator.evaluateConditionOnTags(tagSet, condition);
		assertThat(result).isTrue();
		verify(cache, never()).put(expectedKey, result);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testCacheIgnoresSpecifiedTags() {
		SimpleTimeoutCache<ConditionEvaluationCacheKey, Boolean> cache = mock(SimpleTimeoutCache.class);
		GroovyConditionEvaluatorServiceImpl cachingEvaluator = givenEvaluatorWithMockCache(cache);

		ImmutableList<String> exclusions = ImmutableList.of(FOO);
		cachingEvaluator.setExcludedFromCacheTags(exclusions);

		ConditionalExpression condition = givenAConditionWithString("age.equalTo 50");
		TagSet tagSet = new TagSet();
		tagSet.addTag(FOO, new Tag("baz"));
		tagSet.addTag(AGE, new Tag(FIFTY));

		ConditionEvaluationCacheKey expectedKey = new ConditionEvaluationCacheKey(tagSet.getTags(), condition, exclusions);
		assertThat(expectedKey.getTags()).containsKey(AGE).doesNotContainKey(FOO);

		when(cache.get(expectedKey)).thenReturn(null);
		boolean result = cachingEvaluator.evaluateConditionOnTags(tagSet, condition);
		verify(cache).get(expectedKey);
		verify(cache).put(expectedKey, result);
	}


	// Private helper methods

	private ConditionalExpression givenAConditionWithString(final String stringCondition) {
		ConditionalExpression condition = new ConditionalExpressionImpl();
		condition.setGuid(String.valueOf(System.currentTimeMillis()));
		String conditionString = "\t{AND {\n\t" + stringCondition + "\n\t} }\n";
		condition.setConditionString(conditionString);
		return condition;
	}

	private GroovyConditionEvaluatorServiceImpl givenEvaluatorWithMockCache(final SimpleTimeoutCache<ConditionEvaluationCacheKey, Boolean> cache) {
		GroovyConditionEvaluatorServiceImpl cachingEvaluator = new GroovyConditionEvaluatorServiceImpl();
		cachingEvaluator.setEvaluationCache(cache);
		cachingEvaluator.setConditionProcessingService(processor);
		return cachingEvaluator;
	}
}
