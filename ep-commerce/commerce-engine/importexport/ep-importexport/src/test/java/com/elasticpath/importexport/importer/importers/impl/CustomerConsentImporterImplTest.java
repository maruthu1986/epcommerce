/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.importexport.importer.importers.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.common.dto.datapolicy.CustomerConsentDTO;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.datapolicy.ConsentAction;
import com.elasticpath.domain.datapolicy.CustomerConsent;
import com.elasticpath.domain.datapolicy.DataPolicy;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.exception.runtime.ImportRuntimeException;
import com.elasticpath.importexport.importer.configuration.ImportConfiguration;
import com.elasticpath.importexport.importer.context.ImportContext;
import com.elasticpath.importexport.importer.importers.SavingStrategy;
import com.elasticpath.importexport.importer.types.ImportStrategyType;
import com.elasticpath.service.customer.CustomerService;
import com.elasticpath.service.datapolicy.CustomerConsentService;
import com.elasticpath.service.datapolicy.DataPolicyService;

/**
 * Test for <code>CustomerConsentImporterImpl</code>.
 */
@RunWith(MockitoJUnitRunner.class)
public class CustomerConsentImporterImplTest {

	private static final String CUSTOMER_CONSENT_GUID = "CUSTOMER_CONSENT_GUID";
	private static final String DATA_POLICY_GUID = "DATA_POLICY_GUID";
	private static final String CUSTOMER_GUID = "CUSTOMER_GUID";

	@InjectMocks
	private CustomerConsentImporterImpl customerConsentImporterImpl;

	@Mock
	private DomainAdapter<CustomerConsent, CustomerConsentDTO> customerConsentAdapter;

	@Mock
	private CustomerConsentService customerConsentService;

	@Mock
	private CustomerService customerService;

	@Mock
	private DataPolicyService dataPolicyService;

	@Mock
	private Appender mockAppender;

	@Captor
	private ArgumentCaptor<LoggingEvent> captorLoggingEvent;

	private SavingStrategy<CustomerConsent, CustomerConsentDTO> mockSavingStrategy;

	/**
	 * SetUps the test.
	 */
	@Before
	public void setUp() {
		customerConsentImporterImpl.setStatusHolder(new ImportStatusHolder());

		Logger root = Logger.getRootLogger();
		root.addAppender(mockAppender);
		root.setLevel(Level.INFO);

		mockSavingStrategy = AbstractSavingStrategy.createStrategy(ImportStrategyType.INSERT, new SavingManager<CustomerConsent>() {
			@Override
			public void save(final CustomerConsent persistable) {
				// do nothing
			}

			@Override
			public CustomerConsent update(final CustomerConsent persistable) {
				return null;
			}
		});
	}

	/**
	 * Check an import with non-initialized importer.
	 */
	@Test
	public void testExecuteNonInitializedImport() {
		assertThatThrownBy(() -> customerConsentImporterImpl.executeImport(createCustomerConsentDTO()))
				.isInstanceOf(ImportRuntimeException.class)
				.hasMessageStartingWith("IE-30501");
	}

	/**
	 * Check an import of customer consents.
	 */
	@Test
	public void testExecuteImport() throws AssertionError {
		ImportConfiguration importConfiguration = new ImportConfiguration();
		importConfiguration.setImporterConfigurationMap(new HashMap<>());

		when(customerConsentService.findByGuid(CUSTOMER_CONSENT_GUID)).thenReturn(null);
		when(customerService.findByGuid(CUSTOMER_GUID)).thenReturn(mock(Customer.class));
		when(dataPolicyService.findByGuid(DATA_POLICY_GUID)).thenReturn(mock(DataPolicy.class));

		customerConsentImporterImpl.initialize(new ImportContext(importConfiguration), mockSavingStrategy);
		boolean status = customerConsentImporterImpl.executeImport(createCustomerConsentDTO());

		verify(mockAppender, times(0)).doAppend(captorLoggingEvent.capture());

		List<LoggingEvent> loggingEvent = captorLoggingEvent.getAllValues();

		assertThat(CustomerConsentDTO.ROOT_ELEMENT)
				.isEqualTo(customerConsentImporterImpl.getImportedObjectName());
		assertThat(customerConsentImporterImpl.getSavingStrategy())
				.isNotNull();
		assertThat(status)
				.isTrue();
		assertThat(loggingEvent)
				.isEmpty();
	}

	/**
	 * Test that the export throws a warning when the customer consent exists already.
	 */
	@Test
	public void testExecuteImportExistingCustomerConsent() throws AssertionError {
		ImportConfiguration importConfiguration = new ImportConfiguration();
		importConfiguration.setImporterConfigurationMap(new HashMap<>());

		when(customerConsentService.findByGuid(CUSTOMER_CONSENT_GUID)).thenReturn(mock(CustomerConsent.class));

		customerConsentImporterImpl.initialize(new ImportContext(importConfiguration), mockSavingStrategy);
		boolean status = customerConsentImporterImpl.executeImport(createCustomerConsentDTO());

		verify(mockAppender).doAppend(captorLoggingEvent.capture());

		LoggingEvent loggingEvent = captorLoggingEvent.getAllValues().get(0);

		assertThat(status)
				.isFalse();
		assertThat("IE-31300 [CUSTOMER_CONSENT_GUID]")
				.isEqualTo(loggingEvent.getMessage().toString());
		assertThat(Level.WARN)
				.isEqualTo(loggingEvent.getLevel());
	}

	/**
	 * Test that the export throws a warning when the data policy does not exist already.
	 */
	@Test
	public void testExecuteImportDataPolicyDoesNotExist() throws AssertionError {
		ImportConfiguration importConfiguration = new ImportConfiguration();
		importConfiguration.setImporterConfigurationMap(new HashMap<>());

		when(customerConsentService.findByGuid(CUSTOMER_CONSENT_GUID)).thenReturn(null);
		when(customerService.findByGuid(CUSTOMER_GUID)).thenReturn(mock(Customer.class));
		when(dataPolicyService.findByGuid(DATA_POLICY_GUID)).thenReturn(null);

		customerConsentImporterImpl.initialize(new ImportContext(importConfiguration), mockSavingStrategy);
		boolean status = customerConsentImporterImpl.executeImport(createCustomerConsentDTO());

		verify(mockAppender).doAppend(captorLoggingEvent.capture());

		LoggingEvent loggingEvent = captorLoggingEvent.getAllValues().get(0);

		assertThat(status)
				.isFalse();
		assertThat("IE-31302 [CUSTOMER_CONSENT_GUID, DATA_POLICY_GUID]")
				.isEqualTo(loggingEvent.getMessage().toString());
		assertThat(Level.WARN)
				.isEqualTo(loggingEvent.getLevel());
	}

	/**
	 * Test that the export throws a warning when the customer does not exist already.
	 */
	@Test
	public void testExecuteImportCustomerDoesNotExist()throws AssertionError {
		ImportConfiguration importConfiguration = new ImportConfiguration();
		importConfiguration.setImporterConfigurationMap(new HashMap<>());

		when(customerConsentService.findByGuid(CUSTOMER_CONSENT_GUID)).thenReturn(null);
		when(customerService.findByGuid(CUSTOMER_GUID)).thenReturn(null);

		customerConsentImporterImpl.initialize(new ImportContext(importConfiguration), mockSavingStrategy);
		boolean status = customerConsentImporterImpl.executeImport(createCustomerConsentDTO());

		verify(mockAppender).doAppend(captorLoggingEvent.capture());

		LoggingEvent loggingEvent = captorLoggingEvent.getAllValues().get(0);

		assertThat(status)
				.isFalse();
		assertThat("IE-31301 [CUSTOMER_CONSENT_GUID, CUSTOMER_GUID]")
				.isEqualTo(loggingEvent.getMessage().toString());
		assertThat(Level.WARN)
				.isEqualTo(loggingEvent.getLevel());
	}

	/**
	 * Test method for {@link CustomerConsentImporterImpl#findPersistentObject(CustomerConsentDTO)}.
	 */
	@Test
	public void testFindPersistentObjectCustomerConsentDTO() {
		final CustomerConsent customerConsent = mock(CustomerConsent.class);

		when(customerConsentService.findByGuid(CUSTOMER_CONSENT_GUID)).thenReturn(customerConsent);

		assertThat(customerConsent)
				.isEqualTo(customerConsentImporterImpl.findPersistentObject(createCustomerConsentDTO()));
	}


	/**
	 * Test method for {@link CustomerConsentImporterImpl#getDomainAdapter()}.
	 */
	@Test
	public void testGetDomainAdapter() {
		assertThat(customerConsentAdapter)
				.isEqualTo(customerConsentImporterImpl.getDomainAdapter());
	}

	/**
	 * Test method for
	 * {@link CustomerConsentImporterImpl
	 * #getDtoGuid{@link com.elasticpath.importexport.common.dto.datapolicy.CustomerConsentsDTO})}.
	 */
	@Test
	public void testGetDtoGuidCustomerConsentDTO() {
		final CustomerConsentDTO dto = createCustomerConsentDTO();

		assertThat(CUSTOMER_CONSENT_GUID)
				.isEqualTo(customerConsentImporterImpl.getDtoGuid(dto));
	}

	/**
	 * Test method for {@link CustomerConsentImporterImpl#getImportedObjectName()}.
	 */
	@Test
	public void testGetImportedObjectName() {
		assertThat(CustomerConsentDTO.ROOT_ELEMENT)
				.isEqualTo(customerConsentImporterImpl.getImportedObjectName());
	}

	/**
	 * The import classes should at least contain the DTO class we are operating on.
	 */
	@Test
	public void testDtoClass() {
		assertThat(CustomerConsentDTO.class)
				.isEqualTo(customerConsentImporterImpl.getDtoClass());
	}

	/**
	 * The auxiliary JAXB class list must not be null (can be empty).
	 */
	@Test
	public void testAuxiliaryJaxbClasses() {
		assertThat(customerConsentImporterImpl.getAuxiliaryJaxbClasses())
				.isNotNull();
	}

	private CustomerConsentDTO createCustomerConsentDTO() {
		final CustomerConsentDTO customerConsentDTO = new CustomerConsentDTO();
		customerConsentDTO.setGuid(CUSTOMER_CONSENT_GUID);
		customerConsentDTO.setCustomerGuid(CUSTOMER_GUID);
		customerConsentDTO.setDataPolicyGuid(DATA_POLICY_GUID);
		customerConsentDTO.setAction(ConsentAction.GRANTED.getName());
		customerConsentDTO.setConsentDate(new Date());

		return customerConsentDTO;
	}
}
