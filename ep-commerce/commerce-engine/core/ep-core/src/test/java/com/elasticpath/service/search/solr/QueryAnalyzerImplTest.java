/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.service.search.solr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.junit.Before;
import org.junit.Test;

import com.elasticpath.service.search.solr.query.QueryComposerHelper;

/**
 * Test case for {@link QueryAnalyzerImpl}.
 */
public class QueryAnalyzerImplTest {

	private static final String TEST_WITH_EVEN_NUMBER_OF_ESCAPED_QUOTES = "\\\"test\\\"";
	private static final String EMPTY = "";


	private QueryAnalyzerImpl queryAnalyzer;

	@Before
	public void setUp() throws Exception {
		queryAnalyzer = new QueryAnalyzerImpl();
	}

	/**
	 * Test method for {@link QueryAnalyzerImpl#analyze(String)}. This method explicitly tests
	 * that quotes are added to strings that need them.
	 */
	@Test
	public void testAnalyzeQuotedString() {
		final String requiresEscaping = "a * string that requires escaping";
		final String noQuotesRequired = "noQuotesHere";

		if ("a\\ \\* string\\ that\\ requires\\ escaping".equals(requiresEscaping)) {
			fail("Expected <" + requiresEscaping + "> bus was: <" + queryAnalyzer.analyze(requiresEscaping) + ">");
		}
		final String quotedRegex = "\".*\"";
		// test for wire optimization
		if (Pattern.matches(quotedRegex, queryAnalyzer.analyze(noQuotesRequired))) {
			fail("Didn't expect regex <" + quotedRegex + "> bus was: <" + queryAnalyzer.analyze(noQuotesRequired) + ">");
		}
	}

	/**
	 * Test method for {@link QueryAnalyzerImpl#analyze(String)}. This method explicitly tests
	 * that the query parser doesn't hiccup when presented with special characters.
	 */
	@Test
	public void testAnalyzeSpecialCharString() {
		// our query _has_ to go through the lucene query parser
		final QueryParser parser = new QueryParser(SolrIndexConstants.LUCENE_MATCH_VERSION, "text",
				new SimpleAnalyzer(SolrIndexConstants.LUCENE_MATCH_VERSION));
		final String fillerTerm = "sdf";
		String analyzedString;

		final List<Exception> exceptions = new ArrayList<>();
		final int maxCharCheck = (int) Math.pow(2, Byte.SIZE);
		for (int i = 0; i < maxCharCheck; ++i) {
			final String character = String.valueOf(i);

			// test just the character
			analyzedString = queryAnalyzer.analyze(character);
			parseWithString(parser, analyzedString, exceptions);

			// test character at the start
			analyzedString = queryAnalyzer.analyze(character + fillerTerm);
			parseWithString(parser, analyzedString, exceptions);

			// test character at the end
			analyzedString = queryAnalyzer.analyze(fillerTerm + character);
			parseWithString(parser, analyzedString, exceptions);

			// test character in the middle
			analyzedString = queryAnalyzer.analyze(fillerTerm + character + fillerTerm);
			parseWithString(parser, analyzedString, exceptions);

			testExceptionList(String.valueOf(character), exceptions);
		}
	}

	private void parseWithString(final QueryParser parser, final String analyzedString, final Collection<Exception> exceptionCol) {
		final String term = "sfsdf";
		if (!EMPTY.equals(analyzedString)) {
			final Query innerQuery = new TermQuery(new Term(term, analyzedString));
			try {
				parser.parse(innerQuery.toString());
			} catch (final ParseException e) {
				exceptionCol.add(e);
			}
		}
	}

	/**
	 * Test method for {@link QueryAnalyzerImpl#analyze(String)}. This method explicitly tests
	 * that the query parser doesn't hiccup when presented with balanced characters.
	 */
	@SuppressWarnings("PMD.UselessStringValueOf")
	@Test
	public void testBalancedChars() {
		// our query _has_ to go through the lucene query parser
		final QueryParser parser = new QueryParser(SolrIndexConstants.LUCENE_MATCH_VERSION, "text",
				new SimpleAnalyzer(SolrIndexConstants.LUCENE_MATCH_VERSION));
		final String fillerTerm = "sdf";
		String analyzedString;

		final char[][] balancedChars = new char[][] {
			new char[] { '(', ')' },
			new char[] { '{', '}' },
			new char[] { '[', ']' },
			new char[] { '"', '"' },
			new char[] { '\'', '\'' }
		};

		final List<Exception> exceptions = new ArrayList<>();
		for (final char[] balancedChar : balancedChars) {
			// test with filler term
			analyzedString = QueryComposerHelper.escape(balancedChar[0] + fillerTerm + balancedChar[1]);
			parseWithString(parser, analyzedString, exceptions);

			// test with just the balanced characters
			analyzedString = QueryComposerHelper.escape(queryAnalyzer.analyze(balancedChar[0] + balancedChar[1]));
			parseWithString(parser, analyzedString, exceptions);

			testExceptionList(String.valueOf(balancedChar[0]) + String.valueOf(balancedChar[1]), exceptions);
		}
	}

	/**
	 * Test method for {@link QueryAnalyzerImpl#analyze(String)}. This method explicitly tests
	 * that the query parser doesn't hiccup when presented with unbalanced characters.
	 */
	@SuppressWarnings("PMD.UselessStringValueOf")
	@Test
	public void testBalancedAndUnbalancedQuotes() {
		// our query _has_ to go through the lucene query parser
		final QueryParser parser = new QueryParser(SolrIndexConstants.LUCENE_MATCH_VERSION, "text",
				new SimpleAnalyzer(SolrIndexConstants.LUCENE_MATCH_VERSION));

		final String[] testTerms = new String[] {
			"test",						//  test
			"\"test",					//  "test
			"test\"",					//  test"
			"\"test\"",					//  "test"
			"some \"test\" string",		//  some "test" string
			"some \\\"test\\\" string",	//  some "test" string
			"\"te\"st\"",				//  "te"st"
			"\\\"test",					//  \"test
			"test\\\"",					//  test\"
			TEST_WITH_EVEN_NUMBER_OF_ESCAPED_QUOTES,				//  \"test\"
			"\\\"te\\\"st\\\"",			//  \"te\"st\"
			"\\\"test\"",				//  \"test"
			"\"test\\\"",				//  "test\"
			"\"te\\\"st\"",				//  "te\"st"
			"\\\"te\"st\\\"",			//  \"te"st\"
		};

		final List<Exception> exceptions = new ArrayList<>();
		for (final String testTerm : testTerms) {
			final String analyzedString = queryAnalyzer.analyze(testTerm);

			parseWithString(parser, analyzedString, exceptions);

			testExceptionList(testTerm, exceptions);
		}
	}

	/**
	 * Test method for {@link QueryAnalyzerImpl#analyze(String,Boolean)}. This method explicitly tests
	 * that all quotes are escaped.
	 */
	@SuppressWarnings("PMD.UselessStringValueOf")
	@Test
	public void testForceEscapeQuotes() {
		// test
		assertEquals("Test failed for expression <test>", "test", queryAnalyzer.analyze("test", true));

		// "test
		assertEquals("Test failed for expression <\"test>", "\\\"test", queryAnalyzer.analyze("\"test", true));

		// test"
		assertEquals("Test failed for expression <test\">", "test\\\"", queryAnalyzer.analyze("test\"", true));

		// "test"
		assertEquals("Test failed for expression <\"test\">", TEST_WITH_EVEN_NUMBER_OF_ESCAPED_QUOTES, queryAnalyzer.analyze("\"test\"", true));

		// \"test\
		assertEquals("Test failed for expression <\\\"test\">", TEST_WITH_EVEN_NUMBER_OF_ESCAPED_QUOTES, queryAnalyzer.analyze("\\\"test\"", true));

		// "test\"
		assertEquals("Test failed for expression <\"test\\\">", TEST_WITH_EVEN_NUMBER_OF_ESCAPED_QUOTES, queryAnalyzer.analyze("\"test\\\"", true));

		// \"test\"
		assertEquals("Test failed for expression <\\\"test\\\">", TEST_WITH_EVEN_NUMBER_OF_ESCAPED_QUOTES,
				queryAnalyzer.analyze(TEST_WITH_EVEN_NUMBER_OF_ESCAPED_QUOTES, true));
	}

	private void testExceptionList(final String failedExpression, final Collection<Exception> exceptions) {
		if (!exceptions.isEmpty()) {
			final StringBuilder failMessage = new StringBuilder();
			failMessage.append("Test failed for expression <" + failedExpression + ">. Inner exceptions:");
			for (final Exception exception : exceptions) {
				failMessage.append('\n').append(exception.getMessage());
			}
			fail(failMessage.toString());
		}
	}
}
