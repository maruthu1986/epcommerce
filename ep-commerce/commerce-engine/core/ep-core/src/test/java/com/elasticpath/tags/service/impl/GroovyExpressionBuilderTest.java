/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.tags.service.impl;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

/**
 * Tests for the groovy expression builder.
 */
public class GroovyExpressionBuilderTest {
	@Test
	public void buildExpression() throws Exception {
		String expression = GroovyExpressionBuilder.buildExpression(StringUtils.EMPTY);
		assertThat(expression)
			.startsWith(GroovyExpressionBuilder.DEF)
			.endsWith(GroovyExpressionBuilder.CLOSE);
	}

}