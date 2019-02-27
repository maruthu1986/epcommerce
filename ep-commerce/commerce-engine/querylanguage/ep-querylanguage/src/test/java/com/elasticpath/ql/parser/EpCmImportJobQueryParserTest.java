/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.ql.parser;

import static org.assertj.core.api.Assertions.assertThat;

import static com.elasticpath.ql.asserts.ParseAssert.assertParseInvalid;
import static com.elasticpath.ql.asserts.ParseAssert.assertParseSuccessfull;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;

import com.elasticpath.ql.custom.cmimportjob.CmImportJobConfiguration;
import com.elasticpath.ql.parser.gen.EpQueryParserImpl;
import com.elasticpath.ql.parser.querybuilder.impl.JPQLQueryBuilder;
import com.elasticpath.ql.parser.valueresolver.impl.JPQLValueResolver;

/**
 * Test cases for JavaCC based implementation of EpQueryParser.
 */
public class EpCmImportJobQueryParserTest {

	private EpQueryParser queryParser;

	/**
	 * Setup test.
	 */
	@Before
	public void setUp() {
		CmImportJobConfiguration configuration = new CmImportJobConfiguration();
		configuration.setCompleteQueryBuilder(new JPQLQueryBuilder());
		configuration.setEpQLValueResolver(new JPQLValueResolver());
		configuration.initialize();

		Map<EPQueryType, AbstractEpQLCustomConfiguration> epQLObjectConfiguration
			= new HashMap<>();

		epQLObjectConfiguration.put(EPQueryType.CM_IMPORT_JOB, configuration);

		EpQueryAssembler epQueryAssembler = new EpQueryAssembler();
		epQueryAssembler.setEpQLObjectConfiguration(epQLObjectConfiguration);

		queryParser = new EpQueryParserImpl();
		queryParser.setEpQueryAssembler(epQueryAssembler);
	}

	/**
	 * Test if a epql WITH a where clause parses ok and returns expected JPQL.
	 */
	@Test
	public void testSuccessfulParse() {
		//first assert a syntactically correct parse
		EpQuery epQuery = assertParseSuccessfull("FIND CmImportJob", queryParser);

		//then assert the JPQL was exactly as expected (trim call is ok)
		assertThat(StringUtils.trim(epQuery.getNativeQuery().getNativeQuery()))
				.as("The returned JPQL was not as expected")
				.isEqualTo("SELECT im.guid FROM ImportJobImpl im ORDER BY im.guid ASC");
	}

	/**
	 * Should not parse it.
	 */
	@Test
	public void testInvalidParse() {
		assertParseInvalid("FIND CmClientImportJob ==", "Should not parse it.", queryParser);
	}

}
