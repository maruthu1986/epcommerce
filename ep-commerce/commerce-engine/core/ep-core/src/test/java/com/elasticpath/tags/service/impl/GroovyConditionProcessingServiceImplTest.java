/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.tags.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.concurrent.FutureTask;

import groovy.lang.Script;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.cache.SimpleTimeoutCache;
import com.elasticpath.tags.domain.ConditionalExpression;
import com.elasticpath.tags.domain.impl.ConditionalExpressionImpl;

/**
 * Test that {@link GroovyConditionEvaluatorServiceImpl} behaves as expected.
 */
@RunWith(MockitoJUnitRunner.class)
public class GroovyConditionProcessingServiceImplTest {

	@Mock
	private SimpleTimeoutCache<String, FutureTask<Script>> cache;

	@InjectMocks
	private final GroovyConditionProcessingServiceImpl processor = new GroovyConditionProcessingServiceImpl();

	@Test
	public void testCacheHit() throws Exception {
		ConditionalExpression conditionalExpression = givenAConditionWithString("foo.equalTo 'bar'");

		Script script = mock(Script.class);
		FutureTask<Script> compilationTask = new FutureTask<>(() -> script);
		compilationTask.run();
		when(cache.get(conditionalExpression.getConditionString())).thenReturn(compilationTask);

		Script result = processor.preprocess(conditionalExpression);
		assertThat(result).isEqualTo(script);
		verify(cache).get(conditionalExpression.getConditionString());
		verifyNoMoreInteractions(cache);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testCacheMiss() throws Exception {
		ConditionalExpression conditionalExpression = givenAConditionWithString("foo.equalTo 'bar'");

		Script script = mock(Script.class);
		FutureTask<Script> compilationTask = new FutureTask<>(() -> script);
		compilationTask.run();
		when(cache.get(conditionalExpression.getConditionString())).thenReturn(null);

		processor.preprocess(conditionalExpression);
		verify(cache).get(conditionalExpression.getConditionString());
		verify(cache).put(eq(conditionalExpression.getConditionString()), any(FutureTask.class));
	}

	// Private helper methods

	private ConditionalExpression givenAConditionWithString(final String stringCondition) {
		ConditionalExpression condition = new ConditionalExpressionImpl();
		condition.setGuid(String.valueOf(System.currentTimeMillis()));
		String conditionString = "\t{AND {\n\t" + stringCondition + "\n\t} }\n";
		condition.setConditionString(conditionString);
		return condition;
	}
}