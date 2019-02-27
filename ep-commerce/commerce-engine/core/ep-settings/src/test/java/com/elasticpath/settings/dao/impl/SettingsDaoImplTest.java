/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
/**
 * 
 */
package com.elasticpath.settings.dao.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.settings.domain.SettingDefinition;
import com.elasticpath.settings.domain.SettingValue;

/**
 * Tests for com.elasticpath.settings.dao.impl.SettingsDaoImpl.
 */
@RunWith(MockitoJUnitRunner.class)
public class SettingsDaoImplTest {

	public static final String SETTING_VALUES_BY_PATH = "SETTING_VALUES_BY_PATH";
	public static final String SETTING_DEFINITION_BY_PATH = "SETTING_DEFINITION_BY_PATH";
	public static final String SETTING_VALUE_BY_PATH_AND_CASE_INSENSITIVE_CONTEXT = "SETTING_VALUE_BY_PATH_AND_CASE_INSENSITIVE_CONTEXT";
	public static final String LIST_PLACEHOLDER = "list";
	@Mock
	private PersistenceEngine mockPersistenceEngine;

	@InjectMocks
	private SettingsDaoImpl settingsDao;

	private static final String PATH = "SOME/PATH";
	private static final int ARRAYSIZE = 3;
	private static final String CONTEXT = "SOMECONTEXT";

	/**
	 * Test that the findSettingDefinition method calls the "retrieveByNamedQuery" function and returns a null SettingDefinition 
	 * because there currently is no settingDefinition that matches the path in the database.
	 */
	@Test
	public void testFindSettingDefinition() {
		final List<SettingDefinition> defs = new ArrayList<>();
		when(mockPersistenceEngine.retrieveByNamedQuery(SETTING_DEFINITION_BY_PATH, PATH)).thenAnswer(answer -> defs);

		SettingDefinition returnedDef = settingsDao.findSettingDefinition(PATH);
		assertThat(returnedDef).isNull();
		verify(mockPersistenceEngine).retrieveByNamedQuery(SETTING_DEFINITION_BY_PATH, PATH);
	}
	
	/**
	 * Tests the findSettingDefinition is called with the "PATH" argument and it passes the call to the service method 
	 * "retrieveByNamedQuery" once, and returns the SettingDefinition accordingly.
	 */
	@Test
	public void testFindSettingDefinitionNotEmpty() {
		final List<SettingDefinition> defs = new ArrayList<>();
		SettingDefinition inputDef = mock(SettingDefinition.class);
		defs.add(inputDef);
		when(mockPersistenceEngine.retrieveByNamedQuery(SETTING_DEFINITION_BY_PATH, PATH)).thenAnswer(answer -> defs);

		SettingDefinition returnedDef = settingsDao.findSettingDefinition(PATH);
		assertThat(returnedDef).isEqualTo(inputDef);
		verify(mockPersistenceEngine).retrieveByNamedQuery(SETTING_DEFINITION_BY_PATH, PATH);
	}

	/**
	 * Test method findSettingDefinitions is called with the "PATH" argument and it passes the call to the service method
	 * "retrieveByNamedQuery" once, and returns a Set of SettingDefinitions. 
	 */
	@Test
	public void testFindSettingDefinitions() {
		final List<SettingDefinition> defs = new ArrayList<>();
		when(mockPersistenceEngine.retrieveByNamedQuery("SETTING_DEFINITIONS_BY_PARTIAL_PATH", "%" + PATH + "%"))
			.thenAnswer(answer -> defs);

		Set<SettingDefinition> newSet = new HashSet<>();
		Set<SettingDefinition> returnedSet = settingsDao.findSettingDefinitions(PATH);
		assertThat(returnedSet).isEqualTo(newSet);
		verify(mockPersistenceEngine).retrieveByNamedQuery("SETTING_DEFINITIONS_BY_PARTIAL_PATH", "%" + PATH + "%");
	}
	
	/**
	 * Test method findAllSettingDefinitions to check if the dao calls the retrieveByNamedQuery method
	 * with the appropriate query and a Set of SettingDefitions is returned.
	 */
	@Test
	public void testFindAllSettingDefinitions() {
		final List<SettingDefinition> defs = new ArrayList<>();
		when(mockPersistenceEngine.retrieveByNamedQuery("SETTING_DEFINITIONS_GET_ALL")).thenAnswer(answer -> defs);

		Set<SettingDefinition> returnedSet = settingsDao.findAllSettingDefinitions();
		Set<SettingDefinition> matchDefs = new HashSet<>();
		assertThat(returnedSet).isEqualTo(matchDefs);
		verify(mockPersistenceEngine).retrieveByNamedQuery("SETTING_DEFINITIONS_GET_ALL");

	}
	
	/**
	 * Test method findSettingValue to check if the dao calls the "retrieveByNamedQuery" method and the appropriate dao response is returned,
	 * using the arguments passed from the dao to the PersistenceEngine.
	 */
	@Test
	public void testFindSettingValue() {
		String settingsContext = "SOMECONTEXT";
		final List<Object> objs = new ArrayList<>();
		when(mockPersistenceEngine.retrieveByNamedQuery(SETTING_VALUE_BY_PATH_AND_CASE_INSENSITIVE_CONTEXT, PATH, settingsContext))
			.thenReturn(objs);

		SettingValue returnedSettingValue = settingsDao.findSettingValue(PATH, settingsContext);
		assertThat(returnedSettingValue).isNull();
		verify(mockPersistenceEngine).retrieveByNamedQuery(SETTING_VALUE_BY_PATH_AND_CASE_INSENSITIVE_CONTEXT, PATH, settingsContext);
	}
	
	/**
	 * Tests the findSettingValues method calls the "retrieveByNamedQuery" function once, and has PATH/CONTEXT as arguments
	 * then returns the SettingValue properly when the list is not empty.
	 */
	@Test
	public void testFindSettingValuesNotEmpty() {
		String settingsContext = "SOMECONTEXT";
		final List<Object> objs = new ArrayList<>();
		SettingValue val = mock(SettingValue.class);
		objs.add(val);
		when(mockPersistenceEngine.retrieveByNamedQuery(SETTING_VALUE_BY_PATH_AND_CASE_INSENSITIVE_CONTEXT, PATH, settingsContext))
			.thenReturn(objs);

		SettingValue returnedSettingValue = settingsDao.findSettingValue(PATH, settingsContext);
		assertThat(returnedSettingValue).isEqualTo(val);
		verify(mockPersistenceEngine).retrieveByNamedQuery(SETTING_VALUE_BY_PATH_AND_CASE_INSENSITIVE_CONTEXT, PATH, settingsContext);
	}

	@Test
	public void testFindSettingValueWhenContextIsNull() {
		String settingsContext = null;
		final List<Object> objs = new ArrayList<>();
		SettingValue val = mock(SettingValue.class);
		objs.add(val);
		when(mockPersistenceEngine.retrieveByNamedQuery("SETTING_VALUE_BY_PATH_AND_CONTEXT", PATH, settingsContext)).thenReturn(objs);

		SettingValue returnedSettingValue = settingsDao.findSettingValue(PATH, settingsContext);
		assertThat(returnedSettingValue).isEqualTo(val);
		verify(mockPersistenceEngine).retrieveByNamedQuery("SETTING_VALUE_BY_PATH_AND_CONTEXT", PATH, settingsContext);
	}
	
	/**
	 * Test that the findSettingValues method calls retrieveByNamedQuery if an empty array of contexts is passed in
	 * with a defined PATH.
	 */
	@Test
	public void testFindSettingValuesWithEmptyArray() {
		final List<SettingValue> value = new ArrayList<>();
		when(mockPersistenceEngine.retrieveByNamedQuery(SETTING_VALUES_BY_PATH, PATH)).thenAnswer(answer -> value);

		Set<SettingValue> newValues = new HashSet<>();
		Set<SettingValue> returnedSet = settingsDao.findSettingValues(PATH, new String[] {});
		assertThat(returnedSet).isEqualTo(newValues);
		verify(mockPersistenceEngine).retrieveByNamedQuery(SETTING_VALUES_BY_PATH, PATH);
	}

	/**
	 * Test that the findSettingValues method calls retrieveByNamedQueryWithList if a non-empty array of contexts is passed in
	 * as well as a path argument.
	 */
	@Test
	public void testFindSettingValues() {
		final List<SettingValue> value = new ArrayList<>();
		when(mockPersistenceEngine.retrieveByNamedQueryWithList("SETTING_VALUES_BY_PATH_AND_CONTEXTS", LIST_PLACEHOLDER,
			Collections.<String>singletonList(CONTEXT), PATH))
			.thenAnswer(answer -> value);

		Set<SettingValue> newValues = new HashSet<>();
		Set<SettingValue> returnedSet = settingsDao.findSettingValues(PATH, CONTEXT);
		assertThat(returnedSet).isEqualTo(newValues);
		verify(mockPersistenceEngine).retrieveByNamedQueryWithList("SETTING_VALUES_BY_PATH_AND_CONTEXTS", LIST_PLACEHOLDER,
			Collections.<String>singletonList(CONTEXT), PATH);
	}

	/**
	 * Test that the findSettingValues method calls retrieveByNamedQueryWithList if a null contexts is passed in
	 * as well as a path argument.
	 */
	@Test
	public void testFindSettingValuesWithNullArray() {
		final List<SettingValue> value = new ArrayList<>();
		when(mockPersistenceEngine.retrieveByNamedQuery(SETTING_VALUES_BY_PATH, PATH)).thenAnswer(answer -> value);

		Set<SettingValue> newValues = new HashSet<>();
		Set<SettingValue> returnedSet = settingsDao.findSettingValues(PATH);
		assertThat(returnedSet).isEqualTo(newValues);
		verify(mockPersistenceEngine).retrieveByNamedQuery(SETTING_VALUES_BY_PATH, PATH);
	}
	
	
	/**
	 * Test that the updateSettingDefinition calls the "saveOrUpdate" method once with the SettingDefinition in the PersistenceEngine.
	 */
	@Test
	public void testUpdateSettingDefinition() {
		final SettingDefinition definition = mock(SettingDefinition.class);
		when(mockPersistenceEngine.saveOrUpdate(definition)).thenReturn(definition);

		SettingDefinition returnedDefinition = settingsDao.updateSettingDefinition(definition);
		assertThat(returnedDefinition).isSameAs(definition);
		verify(mockPersistenceEngine).saveOrUpdate(definition);

	}

	/**
	 * Test that the updateSettingValue calls the "saveOrUpdate" method once with the SettingValue in the PersistenceEngine.
	 */
	@Test
	public void testUpdateSettingValue() {
		final SettingValue value = mock(SettingValue.class);
		when(mockPersistenceEngine.saveOrUpdate(value)).thenReturn(value);

		SettingValue returnedValue = settingsDao.updateSettingValue(value);
		assertThat(returnedValue).isSameAs(value);
		verify(mockPersistenceEngine).saveOrUpdate(value);
	}

	/**
	 * Test that the deleteSettingDefinition calls the "delete" method once with a void return in the PersistenceEngine,
	 * with a settingDefinition as an argument.
	 */
	@Test
	public void testDeleteSettingDefinition() {
		final SettingDefinition definition = mock(SettingDefinition.class);
		settingsDao.deleteSettingDefinition(definition);
		verify(mockPersistenceEngine).delete(definition);
	}

	/**
	 * Test that the deleteSettingValue calls the "delete" method once with a void return in the PersistenceEngine,
	 * with a settingValue as an argument.
	 */
	@Test
	public void testDeleteSettingValue() {
		final SettingValue value = mock(SettingValue.class);
		settingsDao.deleteSettingValue(value);
		verify(mockPersistenceEngine).delete(value);
	}

	/**
	 * Test that the deleteSettingValues calls the "executeNamedQueryWithList" method once with a correct return value in the PersistenceEngine.
	 */
	@Test
	public void testDeleteSettingValues() {
		final String[] contexts = new String[ARRAYSIZE];
		contexts[0] = "SOMECONTEXT1";
		contexts[1] = "SOMECONTEXT2";
		contexts[2] = "SOMECONTEXT3";
		when(mockPersistenceEngine.retrieveByNamedQueryWithList("SETTING_VALUE_UIDS_BY_PATH_AND_CONTEXTS", LIST_PLACEHOLDER,
			Arrays.<String>asList(contexts), PATH)).thenReturn(null);
		when(mockPersistenceEngine.executeNamedQueryWithList("DELETE_SETTINGVALUES_BY_UID", LIST_PLACEHOLDER, null))
			.thenReturn(0);

		settingsDao.deleteSettingValues(PATH, contexts);

		verify(mockPersistenceEngine).retrieveByNamedQueryWithList("SETTING_VALUE_UIDS_BY_PATH_AND_CONTEXTS", LIST_PLACEHOLDER,
			Arrays.<String>asList(contexts), PATH);
		verify(mockPersistenceEngine).executeNamedQueryWithList("DELETE_SETTINGVALUES_BY_UID", LIST_PLACEHOLDER, null);
	}
	
	/**
	 * Test that the getSettingDefinitionCount calls the "retrieveByNamedQuery" PersistenceEngine method with the appropriate arguments
	 * and the values returned can be parsed correctly into an integer value.
	 */
	@Test
	public void testGetSettingDefinitionCount() {
		final Object[] objs = new Object[]{PATH};
		final List<Object> list = new ArrayList<>();
		Long value = 1L;
		list.add(value);
		when(mockPersistenceEngine.retrieveByNamedQuery("SETTING_DEFINITIONS_COUNT_BY_PATH", objs)).thenReturn(list);

		int returnedVal = settingsDao.getSettingDefinitionCount(PATH);
		assertThat(returnedVal).isEqualTo(1);
		verify(mockPersistenceEngine).retrieveByNamedQuery("SETTING_DEFINITIONS_COUNT_BY_PATH", objs);
	}
	
	/**
	 * Test that the getSettingValueCount calls the appropriate PersistenceEngine method with the appropriate arguments
	 * and the values returned can be parsed correctly into an integer value.
	 */
	@Test
	public void testGetSettingValueCount() {
		final Object[] objs = new Object[]{PATH, CONTEXT};
		final List<Object> list = new ArrayList<>();
		Long value = 0L;
		list.add(value);
		when(mockPersistenceEngine.retrieveByNamedQuery("SETTING_VALUES_COUNT_BY_PATH_AND_CONTEXT", objs)).thenReturn(list);

		int returnedVal = settingsDao.getSettingValueCount(PATH, CONTEXT);
		assertThat(returnedVal).isEqualTo(0);
		verify(mockPersistenceEngine).retrieveByNamedQuery("SETTING_VALUES_COUNT_BY_PATH_AND_CONTEXT", objs);
	}

	/**
	 * Test that the getSettingDefinitionMaxOverrideValues calls the appropriate PersistenceEngine method with the appropriate arguments
	 * and the values returned can be parsed correctly into an integer value.
	 */
	@Test
	public void testGetSettingDefinitionMaxOverrideValues() {
		final Object[] objs = new Object[]{PATH};
		final List<Integer> list = new ArrayList<>();
		Integer value = 0;
		list.add(value);
		when(mockPersistenceEngine.retrieveByNamedQuery("SETTING_DEFINITION_MAX_OVERRIDE_VALUES", objs)).thenAnswer(answer -> list);

		int returnedVal = settingsDao.getSettingDefinitionMaxOverrideValues(PATH);
		assertThat(returnedVal).isEqualTo(0);
		verify(mockPersistenceEngine).retrieveByNamedQuery("SETTING_DEFINITION_MAX_OVERRIDE_VALUES", objs);
	}
}
