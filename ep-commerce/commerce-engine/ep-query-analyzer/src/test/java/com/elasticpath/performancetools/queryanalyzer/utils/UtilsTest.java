/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.performancetools.queryanalyzer.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.File;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.performancetools.queryanalyzer.beans.JPAQuery;
import com.elasticpath.performancetools.queryanalyzer.beans.Operation;

/**
 * Test for {@link Utils}.
 */
@RunWith(MockitoJUnitRunner.class)
public class UtilsTest {

	@After
	public void clean() {
		System.getProperties().remove(SystemProperties.OUTPUT_JSON_FILE_PATH_SYSTEM_PROPERTY);
		System.getProperties().remove(SystemProperties.PRINT_JSON_TO_CONSOLE_ONLY_SYSTEM_PROPERTY);
	}

	@Test
	public void shouldGetOutputJSONFileReferenceUsingSystemPropertyWhenEnabled() {
		final String currentFolder = getClass().getClassLoader().getResource(".").getPath();
		System.setProperty(SystemProperties.OUTPUT_JSON_FILE_PATH_SYSTEM_PROPERTY, currentFolder + "db_statistics.json");

		final File actualOutputJSONFileReference = Utils.getOutputJSONFileIfEnabled();

		assertThat(actualOutputJSONFileReference)
				.isNotNull();
	}

	@Test
	public void shouldGetOutputJSONFileReferenceUsingDefaultSettingsWhenEnabled() {
		final String currentFolder = getClass().getClassLoader().getResource(".").getPath();
		System.setProperty("user.home", currentFolder);

		final File actualOutputJSONFileReference = Utils.getOutputJSONFileIfEnabled();

		assertThat(actualOutputJSONFileReference)
				.isNotNull();
	}

	@Test
	public void shouldNotGetOutputJSONFileReferenceWhenDisabled() {
		System.setProperty(SystemProperties.PRINT_JSON_TO_CONSOLE_ONLY_SYSTEM_PROPERTY, "1");

		final File actualOutputJSONFileReference = Utils.getOutputJSONFileIfEnabled();

		assertThat(actualOutputJSONFileReference)
				.isNull();
	}

	@Test
	public void shouldSetOperationThreadNameWhenFoundInLogLine() {
		final Matcher matcher = Patterns.THREAD_PATTERN.matcher("2016-06-14 09:49:32,919 [http-bio-9080-exec-10] ");
		final Operation operation = new Operation("CREATE", "uri1");

		Utils.setOperationThreadName(matcher, operation);

		assertThat(operation.getThread())
				.isEqualTo("http-bio-9080-exec-10");
	}

	@Test
	public void shouldNotSetOperationThreadNameWhenNotFoundInLogLine() {
		final Matcher matcher = Patterns.THREAD_PATTERN.matcher("2016-06-14 09:49:32,919 no thread");
		final Operation operation = new Operation("LINK", "uri2");

		Utils.setOperationThreadName(matcher, operation);

		assertThat(operation.getThread())
				.isNull();
	}

	@Test
	public void shouldSetOperationTimestampWhenFoundInLogLine() throws Exception {
		final Matcher matcher = Patterns.TIMESTAMP_PATTERN.matcher("2016-06-14 09:49:32,919 [http-bio-9080-exec-10] ");
		final Operation operation = new Operation("ADVISE_READ", "uri3");

		Utils.setOperationTimestamp(matcher, operation, false);
		final Date expectedTimestamp = new SimpleDateFormat(Utils.DATE_FORMAT_PATTERN, Locale.getDefault()).parse("2016-06-14 09:49:32,919");

		assertThat(operation.getStartedAt())
				.isEqualTo(expectedTimestamp);
	}

	@Test
	public void shouldNotSetOperationTimestampWhenNotFoundInLogLine() throws Exception {
		final Matcher matcher = Patterns.TIMESTAMP_PATTERN.matcher("no timestamp");
		final Operation operation = new Operation("DELETE", "uri4");

		Utils.setOperationTimestamp(matcher, operation, false);

		assertThat(operation.getStartedAt())
				.isNull();
	}

	@Test
	public void shouldSetJPAEagerRelationsWhenFoundInLogLine() throws Exception {
		final Matcher matcher = Patterns.EAGER_RELATIONS_PATTERN.matcher("2016-06-14 09:49:32,919 [http-bio-9080-exec-10] "
				+ "TRACE org.apache.openjpa.lib.log.SLF4JLogFactory$LogAdapter - "
				+ "Eager relations: [com.pkg.EntityImpl.eagerRelation1, "
				+ "com.pkg.EntityImpl.eagerRelation2]");
		final JPAQuery jpaQuery = new JPAQuery("jpa query");

		Utils.setJPAQueryEagerRelations(matcher, jpaQuery);

		assertThat(jpaQuery.getEagerRelations())
				.contains(("com.pkg.EntityImpl.eagerRelation1, com.pkg.EntityImpl.eagerRelation2"));

	}

	@Test
	public void shouldNotSetJPAEagerRelationsWhenNotFoundInLogLine() throws Exception {
		final Matcher matcher = Patterns.EAGER_RELATIONS_PATTERN.matcher("no eager relations");
		final JPAQuery jpaQuery = new JPAQuery("jpa query");

		Utils.setJPAQueryEagerRelations(matcher, jpaQuery);

		assertThat(jpaQuery.getEagerRelations())
				.isEmpty();
	}

	@Test
	public void shouldRemoveTabAndCRChars() {
		final String input = "Test\t\t\t String with tabs and \n\nCR chars";
		final String expected = "Test String with tabs and   CR chars";

		final String actual = Utils.removeTabAndCRChars(input);

		assertThat(actual)
				.isEqualTo(expected);
	}

	@Test
	public void shouldProcessMultiLineQueries() throws Exception {
		final String multiLineQuery = "FROM CartOrderImpl AS co, ShoppingCartMementoImpl AS cart\n"
				+ "WHERE UPPER(cart.storeCode) = UPPER(:_1)\n"
				+ "2016-06-14 09:49:32,963 [http-bio-9080-exec-10]";

		final BufferedReader bufferedReader = new BufferedReader(new StringReader(multiLineQuery));
		final StringBuilder queryBuffer = new StringBuilder("SELECT co");

		final String expectedQuery = "SELECT co\n"
				+ "FROM CartOrderImpl AS co, ShoppingCartMementoImpl AS cart\n"
				+ "WHERE UPPER(cart.storeCode) = UPPER(:_1)\n"
				+ "2016-06-14 09:49:32,963 [http-bio-9080-exec-10]";
		Utils.processMultiLineQueries(bufferedReader, queryBuffer);
		bufferedReader.close();

		assertThat(queryBuffer.toString())
				.isEqualTo(expectedQuery);

		bufferedReader.close();
	}
}
