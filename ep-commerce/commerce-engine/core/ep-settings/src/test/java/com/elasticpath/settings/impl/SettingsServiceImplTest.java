/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.settings.impl;

import static java.util.Locale.getDefault;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.settings.SettingMaxOverrideException;
import com.elasticpath.settings.dao.SettingsDao;
import com.elasticpath.settings.domain.SettingDefinition;
import com.elasticpath.settings.domain.SettingValue;

/**
 * Tests for com.elasticpath.settings.impl.SettingsServiceImpl.
 */
@SuppressWarnings({"PMD.AvoidCatchingNPE", "PMD.TooManyMethods"})
@RunWith(MockitoJUnitRunner.class)
public class SettingsServiceImplTest {

	@Mock
	private SettingsDao mockSettingsDao;

	@InjectMocks
	private SettingsServiceImpl service;

	private static final int ARRAYSIZE = 3;
	private static final String PATH = "SOME/PATH";
	private static final String CONTEXT = "somecontext";

	/**
	 * Test that the given partial path is passed to the DAO when calling "findSettingDefinitions" method, 
	 * and that the DAO's response is wrapped in an modifiable set.
	 */
	@Test
	public void testGetSettingDefinitions() {
		final Set<SettingDefinition> definitions = new HashSet<>();
		when(mockSettingsDao.findSettingDefinitions(PATH)).thenReturn(definitions);

		Set<SettingDefinition> returnedDefinitions = service.getSettingDefinitions(PATH);

		//check that it is modifiable
		boolean value = returnedDefinitions.add(mock(SettingDefinition.class));
		assertThat(value).isTrue();
		verify(mockSettingsDao).findSettingDefinitions(PATH);
	}
	
	/**
	 * Test that calling getSettingDefinitions with a null partialPath throws
	 * a NPE before calling the dao method "findSettingDefinitions". 
	 */
	@Test(expected = NullPointerException.class)
	public void testGetSettingDefinitionsNullPath() {
		when(mockSettingsDao.findSettingDefinitions(null)).thenReturn(null);
		service.getSettingDefinitions(null);
		verify(mockSettingsDao).findSettingDefinitions(null);
	}

	/**
	 * Test that the given path is passed to the DAO when calling "findSettingDefinition", and that the dao's response is
	 * a returned SettingDefinition for the given path.
	 */
	@Test
	public void testGetSettingDefinition() {
		final SettingDefinition definition = mock(SettingDefinition.class);
		when(mockSettingsDao.findSettingDefinition(PATH)).thenReturn(definition);
		SettingDefinition returnedDefinition = service.getSettingDefinition(PATH);
		assertThat(returnedDefinition).isSameAs(definition);
		verify(mockSettingsDao).findSettingDefinition(PATH);
	}
	
	/**
	 * Test that the getAllSettingDefinitions method appropriately calls the "findAllSettingDefinitions"
	 * method with no parameters and is returned a set of setting definitions (modifiable set).
	 */
	@Test
	public void testGetAllSettingDefinitions() {
		final Set<SettingDefinition> defs = new HashSet<>();
		when(mockSettingsDao.findAllSettingDefinitions()).thenReturn(defs);
		Set<SettingDefinition> returnedDefs = service.getAllSettingDefinitions();
		assertThat(returnedDefs)
			.isNotNull()
			.isEqualTo(defs);
		verify(mockSettingsDao).findAllSettingDefinitions();

		//check that the set is modifiable
		boolean value = returnedDefs.add(mock(SettingDefinition.class));
		assertThat(value).isTrue();

	}

	/**
	 * Test that the given path and context are passed to the DAO method call for "findSettingValue", and 
	 * that the dao's response is returned in the form of a SettingValue. 
	 */
	@Test
	public void testGetSettingValue() {
		final String settingContext = "somecontexxt";
		final SettingValue value = mock(SettingValue.class);
		when(mockSettingsDao.findSettingValue(PATH, settingContext)).thenReturn(value);
		SettingValue returnedDefinition = service.getSettingValue(PATH, settingContext);
		assertThat(returnedDefinition).isSameAs(value);
		verify(mockSettingsDao).findSettingValue(PATH, settingContext);
	}

	/**
	 * Test that getting the setting value is insensitive to the case of the context.
	 */
	@Test
	public void testGetSettingValueIsCaseInsensitive() {
		final String settingContext = "SOMECONTEXT";
		final SettingValue value = mock(SettingValue.class);
		when(mockSettingsDao.findSettingValue(PATH, settingContext.toLowerCase(getDefault()))).thenReturn(value);
		SettingValue returnedDefinition = service.getSettingValue(PATH, settingContext);
		assertThat(returnedDefinition).isSameAs(value);
		verify(mockSettingsDao).findSettingValue(PATH, settingContext.toLowerCase(getDefault()));
	}

	/**
	 * Test that the null path and null context arguments will result in an NullPointerException
	 * when passed to the service method "getSettingValue". 
	 */
	@Test
	public void testGetSettingValueNullArguments() {
		assertThatThrownBy(() -> service.getSettingValue(null, null))
			.isInstanceOf(IllegalArgumentException.class);
	}
	
	/**
	 * Test that a null path argument with a defined context argument will result in an NullPointerException
	 * when passed to the service method "getSettingValue". 
	 */
	@Test
	public void testGetSettingValuePathNullArgument() {
		assertThatThrownBy(() -> service.getSettingValue(null, CONTEXT))
			.isInstanceOf(IllegalArgumentException.class);
	}
	
	/**
	 * Test that a defined path argument with a null context argument will result in an NullPointerException
	 * when passed to the service method "getSettingValue". 
	 */
	@Test
	public void testGetSettingValueContextNullArgument() {
		assertThatThrownBy(() -> service.getSettingValue(null, PATH))
			.isInstanceOf(IllegalArgumentException.class);
	}
	
	/**
	 * Test that getSettingValues calls the dao method "findSettingValues" with the same arguments that were passed into the service method.
	 * Also check that the returned set is modifiable.
	 */
	@Test
	public void testGetSettingValues() {
		final String[] contexts = new String[ARRAYSIZE];
		contexts[0] = "SOMECONTEXT1";
		contexts[1] = "somecontext2";
		contexts[2] = "SOMECONTEXT3";
		final Set<SettingValue> values = new HashSet<>();
		when(mockSettingsDao.findSettingValues(PATH, contexts)).thenReturn(values);

		Set<SettingValue> returnedValues = service.getSettingValues(PATH, contexts);
		//Check that it's modifiable
		boolean value = returnedValues.add(mock(SettingValue.class));
		assertThat(value).isTrue();
		verify(mockSettingsDao).findSettingValues(PATH, contexts);
	}
	
	/**
	 * Test that calling getSettingValues with a null context and defined path still returns a set of SettingValues from the dao
	 * when it calls "findSettingValues".
	 */
	@Test
	public void testGetSettingValuesNullContext() {
		final Set<SettingValue> values = new HashSet<>();
		when(mockSettingsDao.findSettingValues(PATH)).thenReturn(values);
		Set<SettingValue> returnedValues = service.getSettingValues(PATH);
		assertThat(returnedValues).isNotNull();
		verify(mockSettingsDao).findSettingValues(PATH);
	}

	/**
	 * Test that calling getSettingValues with a empty context array still returns a set of SettingValues from the dao
	 * when the service method "findSettingValues" is called with context and path arguments.
	 */
	@Test
	public void testGetSettingValuesEmptyContexts() {
		final Set<SettingValue> values = new HashSet<>();
		final String[] cons = new String[]{};
		when(mockSettingsDao.findSettingValues(PATH, cons)).thenReturn(values);
		Set<SettingValue> returnedValues = service.getSettingValues(PATH, cons);
		assertThat(returnedValues).isNotNull();
		verify(mockSettingsDao).findSettingValues(PATH, cons);
	}
	
	/**
	 * Test that deleteSettingDefinition calls the dao method "deleteSettingDefinitions" with 
	 * the same argument that was passed into the service method.
	 */
	@Test
	public void testDeleteSettingDefinition() {
		final SettingDefinition mockSettingDefinition = mock(SettingDefinition.class);
		service.deleteSettingDefinition(mockSettingDefinition);
	}
	
	/**
	 * Test that deleteSettingValue calls the dao method "deleteSettingValue" with the same argument that was passed into the service method.
	 */
	@Test
	public void testDeleteSettingValue() {
		final SettingValue value = mock(SettingValue.class);
		service.deleteSettingValue(value);
		verify(mockSettingsDao).deleteSettingValue(value);
	}

	/**
	 * Test that deleteSettingValues calls the dao method "deleteSettingValues" with the same arguments that were passed into the service method,
	 * and that it returns the number of setting values that were deleted.
	 */
	@Test
	public void testDeleteSettingValues() {
		final String[] contexts = new String[ARRAYSIZE];
		contexts[0] = "SOMECONTEXT1";
		contexts[1] = "SOMECONTEXT2";
		contexts[2] = "SOMECONTEXT3";
		//assume the dao reports that all requested settingValues were deleted
		when(mockSettingsDao.deleteSettingValues(PATH, contexts)).thenReturn(contexts.length);
		service.deleteSettingValues(PATH, contexts);
		verify(mockSettingsDao).deleteSettingValues(PATH, contexts);
	}

	/**
	 * Test that updateSettingDefinition, when passed a SettingDefinition that is persistent,
	 * calls the dao method "updateSettingDefinition" with the same argument that was passed into the service method.
	 */
	@Test
	public void testUpdateSettingDefinition() {

		//The method checks if the setting definition is already persisted, in our case it is true
		//and then the update of the definition should proceed without a problem.
		final SettingDefinition mockSettingDefinition = mock(SettingDefinition.class);
		when(mockSettingDefinition.isPersisted()).thenReturn(true);
		when(mockSettingsDao.updateSettingDefinition(mockSettingDefinition)).thenReturn(mockSettingDefinition);

		SettingDefinition returnedDef = service.updateSettingDefinition(mockSettingDefinition);
		assertThat(returnedDef).isSameAs(mockSettingDefinition);
		verify(mockSettingsDao).updateSettingDefinition(mockSettingDefinition);
	}

	/**
	 * Test that calling updateSettingDefinition with a SettingDefinition that is not persistent
	 * and has a PATH of a SettingDefinition that is already persistent will throw an EpServiceException.
	 */
	@Test
	public void testUpdateSettingDefinitionDuplicate() {

		//The method checks if the setting definition is already persisted, in this case it is 
		//not persisted, thus checks if the setting definition already exists
		final SettingDefinition mockSettingDefinition = mock(SettingDefinition.class);
		when(mockSettingDefinition.isPersisted()).thenReturn(false);
		when(mockSettingDefinition.getPath()).thenReturn(PATH);
		when(mockSettingsDao.getSettingDefinitionCount(PATH)).thenReturn(1);

		assertThatThrownBy(() -> service.updateSettingDefinition(mockSettingDefinition))
			.isInstanceOf(EpServiceException.class);
		verify(mockSettingsDao).getSettingDefinitionCount(PATH);
	}
	
	/**
	 * Test that updateSettingValue calls the dao method "updateSettingValue" with the same argument that was passed into the service method,
	 * and a SettingValue is returned.
	 */
	@Test
	public void testUpdateSettingValue() {

		final SettingValue mockSettingValue = mock(SettingValue.class);
		//Obtain the maximum number of overrides that are allowed for a setting definition, in this case we allow zero overrides
		when(mockSettingsDao.getSettingDefinitionMaxOverrideValues(PATH)).thenReturn(0);

		when(mockSettingValue.getPath()).thenReturn(PATH);

		//The method checks if the setting value is already persisted, in our case it is true
		//and then the update of the value should proceed without a problem.
		when(mockSettingValue.isPersisted()).thenReturn(true);

		when(mockSettingsDao.updateSettingValue(mockSettingValue)).thenReturn(mockSettingValue);
		SettingValue returnedValue = service.updateSettingValue(mockSettingValue);
		assertThat(mockSettingValue).isEqualTo(returnedValue);
		verify(mockSettingsDao).getSettingDefinitionMaxOverrideValues(PATH);
		verify(mockSettingsDao).updateSettingValue(mockSettingValue);
	}

	/**
	 * Test that after the addition of one SettingValue to the settingsDao, if another
	 * SettingValue with the same path and context is created to be added it will get 
	 * rejected by the dao and throw an EpServiceException.
	 */
	@Test
	public void testDuplicateSettingValue() {
		final SettingValue mockSettingValue = mock(SettingValue.class);
		//Obtain the maximum number of overrides that are allowed for a setting definition, in this case we allow zero overrides
		when(mockSettingsDao.getSettingDefinitionMaxOverrideValues(PATH)).thenReturn(0);

		//The method checks if the setting value is already persisted, in our case it is false
		when(mockSettingValue.isPersisted()).thenReturn(false);

		//The method then checks if the setting value exists and in our case we return true
		//and an exception is thrown because a duplicate setting value exists
		when(mockSettingValue.getPath()).thenReturn(PATH);

		when(mockSettingValue.getContext()).thenReturn(CONTEXT);
		when(mockSettingsDao.getSettingValueCount(PATH, CONTEXT)).thenReturn(1);

		assertThatThrownBy(() -> service.updateSettingValue(mockSettingValue))
			.isInstanceOf(EpServiceException.class);
		verify(mockSettingsDao).getSettingDefinitionMaxOverrideValues(PATH);
		verify(mockSettingsDao).getSettingValueCount(PATH, CONTEXT);
	}
	
	/**
	 *  Test that ensures that an error is thrown if trying to add a new setting value for a specific
	 *  setting but the maximum number of value overrides are already present, causing an error to be thrown. 
	 */
	@Test
	public void testMaxOverrideSettingValueExists() {
		final SettingValue mockSettingValue = mock(SettingValue.class);
		//Obtain the maximum number of overrides that are allowed for a setting definition, in this case we allow one overrides
		when(mockSettingsDao.getSettingDefinitionMaxOverrideValues(PATH)).thenReturn(1);

		//The method checks if the setting value is already persisted, in our case it is false
		when(mockSettingValue.isPersisted()).thenReturn(false);

		//The method then checks if the setting value exists and in our case we return false
		when(mockSettingValue.getPath()).thenReturn(PATH);

		when(mockSettingValue.getContext()).thenReturn(CONTEXT);

		when(mockSettingsDao.getSettingValueCount(PATH, CONTEXT)).thenReturn(0);

		//The method then checks if the setting's value that are currently persisted exceed the number
		//that are allowed, in this case we are at the limit and throw an exception when trying to add another
		when(mockSettingsDao.getSettingValueCount(PATH, null)).thenReturn(1);

		assertThatThrownBy(() -> service.updateSettingValue(mockSettingValue))
			.isInstanceOf(SettingMaxOverrideException.class);
		verify(mockSettingsDao).getSettingDefinitionMaxOverrideValues(PATH);
		verify(mockSettingsDao).getSettingValueCount(PATH, CONTEXT);
		verify(mockSettingsDao).getSettingValueCount(PATH, null);
	}
	
	/**
	 *  Test that addition of a setting value proceeds when the maximum override value is set to negative one
	 *  meaning that there are an infinite number of overrides possible for a setting. 
	 */
	@Test
	public void testMaxOverrideSettingValueInfinite() {
		final SettingValue mockSettingValue = mock(SettingValue.class);
		//Obtain the maximum number of overrides that are allowed for a setting definition, in this case we allow unlimited overrides
		when(mockSettingsDao.getSettingDefinitionMaxOverrideValues(PATH)).thenReturn(-1);

		//The method checks if the setting value is already persisted, in our case it is false
		when(mockSettingValue.isPersisted()).thenReturn(false);

		//The method then checks if the setting value exists and in our case we return false
		when(mockSettingValue.getPath()).thenReturn(PATH);

		when(mockSettingValue.getContext()).thenReturn(CONTEXT);

		when(mockSettingsDao.getSettingValueCount(PATH, CONTEXT)).thenReturn(0);

		//The method then checks if the setting's value that are currently persisted exceed the number
		//that are allowed, in this case we are under the limit so the new setting value is persisted
		when(mockSettingsDao.updateSettingValue(mockSettingValue)).thenReturn(mockSettingValue);
		SettingValue returnedValue = service.updateSettingValue(mockSettingValue);
		assertThat(mockSettingValue).isEqualTo(returnedValue);
		verify(mockSettingsDao, atLeastOnce()).getSettingDefinitionMaxOverrideValues(PATH);
		verify(mockSettingsDao).getSettingValueCount(PATH, CONTEXT);
		verify(mockSettingsDao).updateSettingValue(mockSettingValue);
	}
	
	/**
	 * Test that getObject throws an unsupportedOperationException because the service supports more than one type of object.
	 */
	@Test
	public void testGetObjectLong() {
		final long uid = 22222222;
		assertThatThrownBy(() -> service.getObject(uid))
			.isInstanceOf(UnsupportedOperationException.class);
	}

	/**
	 * Tests the method settingDefinitionExists, make sure that the arguments are passed to the 
	 * dao, and the appropriate values are returned when a SettingDefinition does exist already with a specified path.
	 */
	@Test
	public void testSettingDefinitionExists() {
		when(mockSettingsDao.getSettingDefinitionCount(PATH)).thenReturn(1);
		boolean value = service.settingDefinitionExists(PATH);
		assertThat(value).isTrue();
		verify(mockSettingsDao).getSettingDefinitionCount(PATH);
	}
	
	/**
	 * Tests the method settingDefinitionExists, makes sure that the arguments passed to the dao, and
	 * the appropriate values are returned when a SettingDefinition doesn't exist already with a specified path.
	 */
	@Test
	public void testSettingDefinitionDoesntExist() {
		when(mockSettingsDao.getSettingDefinitionCount(PATH)).thenReturn(0);
		boolean value = service.settingDefinitionExists(PATH);
		assertThat(value).isFalse();
		verify(mockSettingsDao).getSettingDefinitionCount(PATH);
	}
	
	/**
	 * Tests the method settingValueExists, makes sure that the arguments are passed to the 
	 * dao, and the appropriate values are returned when a SettingValue does exists with a specific
	 * path and context.
	 */
	@Test
	public void testSettingValueExists() {
		when(mockSettingsDao.getSettingValueCount(PATH, CONTEXT)).thenReturn(1);
		boolean value = service.settingValueExists(PATH, CONTEXT);
		assertThat(value).isTrue();
		verify(mockSettingsDao).getSettingValueCount(PATH, CONTEXT);
	}

	/**
	 * Tests the method settingValueExists, and that the lookup is case-insensitive.
	 */
	@Test
	public void testSettingValueExistsAndIsCaseInsensitive() {
		when(mockSettingsDao.getSettingValueCount(PATH, CONTEXT)).thenReturn(1);
		boolean value = service.settingValueExists(PATH, CONTEXT.toUpperCase(getDefault()));
		assertThat(value).isTrue();
		verify(mockSettingsDao).getSettingValueCount(PATH, CONTEXT);
	}
	
	/**
	 * Tests the method settingValueExists, makes sure that the arguments are passed to the
	 * dao, and the appropriate values are returned when a SettingValue does not exist with a specific
	 * path and context.
	 */
	@Test
	public void testSettingValueDoesntExist() {
		when(mockSettingsDao.getSettingValueCount(PATH, CONTEXT)).thenReturn(0);
		boolean value = service.settingValueExists(PATH, CONTEXT);
		assertThat(value).isFalse();
		verify(mockSettingsDao).getSettingValueCount(PATH, CONTEXT);
	}
}
