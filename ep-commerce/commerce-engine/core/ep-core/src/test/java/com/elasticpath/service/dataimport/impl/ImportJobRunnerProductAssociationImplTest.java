/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.service.dataimport.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.hamcrest.collection.IsArray;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.util.impl.UtilityImpl;
import com.elasticpath.domain.ElasticPath;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductAssociation;
import com.elasticpath.domain.catalog.ProductAssociationType;
import com.elasticpath.domain.catalog.impl.CatalogImpl;
import com.elasticpath.domain.catalog.impl.ProductAssociationImpl;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.dataimport.ImportBadRow;
import com.elasticpath.domain.dataimport.ImportJob;
import com.elasticpath.domain.dataimport.ImportJobRequest;
import com.elasticpath.domain.dataimport.ImportJobState;
import com.elasticpath.domain.dataimport.ImportType;
import com.elasticpath.domain.dataimport.impl.AbstractImportTypeImpl;
import com.elasticpath.domain.dataimport.impl.ImportBadRowImpl;
import com.elasticpath.domain.dataimport.impl.ImportDataTypeProductAssociationImpl;
import com.elasticpath.domain.dataimport.impl.ImportFaultImpl;
import com.elasticpath.domain.dataimport.impl.ImportJobRequestImpl;
import com.elasticpath.persistence.CsvFileReader;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.persistence.api.PersistenceSession;
import com.elasticpath.persistence.api.Transaction;
import com.elasticpath.persistence.openjpa.JpaPersistenceEngine;
import com.elasticpath.service.catalog.ProductAssociationService;
import com.elasticpath.service.changeset.ChangeSetService;
import com.elasticpath.service.dataimport.ImportGuidHelper;
import com.elasticpath.service.dataimport.ImportJobStatusHandler;
import com.elasticpath.service.dataimport.ImportService;
import com.elasticpath.service.search.query.ProductAssociationSearchCriteria;


/**
 * Test for {@link ImportJobRunnerProductAssociationImpl}.
 */
//@SuppressWarnings("PMD.NonStaticInitializer")
public class ImportJobRunnerProductAssociationImplTest {

	private static final String TARGET_PRODUCT1_CODE = "10000654";
	private static final String TARGET_PRODUCT2_CODE = "10000888";
	private static final String SOURCE_PRODUCT_CODE = "10020228";
	private static final String IMPORT_PROCESS_ID = "id1";
	private static final String[] CSV_LINE1 = new String[] { SOURCE_PRODUCT_CODE, TARGET_PRODUCT1_CODE, "4", "0", "1", "1" };
	private static final String[] CSV_LINE2 = new String[] { SOURCE_PRODUCT_CODE, TARGET_PRODUCT2_CODE, "4", "0", "1", "2" };

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private ImportJob importJob;
	private UtilityImpl utility;
	private ImportService importService;
	private PersistenceSession persistenceSession;
	private ImportJobStatusHandler jobStatusHandler;
	private CsvFileReader csvFileReader;
	private ChangeSetService changeSetService;
	private ElasticPath elasticPath;
	private JpaPersistenceEngine persistenceEngine;
	private CmUser cmUser;
	private ProductAssociationService productAssociationService;
	private ImportGuidHelper importGuidHelper;
	private Transaction transaction;
	private Catalog catalog;
	private EntityManager entityManager;

	/**
	 * Test initialization.
	 *
	 */
	@Before
	public void setUp() {
		final ImportDataTypeProductAssociationImpl importDataType = new ImportDataTypeProductAssociationImpl();

		importService = context.mock(ImportService.class);
		csvFileReader = context.mock(CsvFileReader.class);
		elasticPath = context.mock(ElasticPath.class);
		changeSetService = context.mock(ChangeSetService.class);
		jobStatusHandler = context.mock(ImportJobStatusHandler.class);
		persistenceEngine = context.mock(JpaPersistenceEngine.class);
		persistenceSession = context.mock(PersistenceSession.class);
		transaction = context.mock(Transaction.class);
		importJob = context.mock(ImportJob.class);
		cmUser = context.mock(CmUser.class);
		productAssociationService = context.mock(ProductAssociationService.class);
		importGuidHelper = context.mock(ImportGuidHelper.class);
		catalog = context.mock(Catalog.class);
		entityManager = context.mock(EntityManager.class);

		importDataType.init(null);
		utility = new UtilityImpl();
		final Map<String, Integer> mapping = new HashMap<>();
		mapping.put("sourceProductCode", 0);
		context.checking(new Expectations() {
			{
				allowing(elasticPath).getBean(ContextIdNames.PRODUCT_ASSOCIATION); will(returnValue(new ProductAssociationImpl()));
				allowing(elasticPath).getBean(ContextIdNames.IMPORT_BAD_ROW); will(returnValue(new ImportBadRowImpl()));
				allowing(elasticPath).getBean(ContextIdNames.IMPORT_FAULT); will(returnValue(new ImportFaultImpl()));

				allowing(importService).findImportDataType(with(any(String.class))); will(returnValue(importDataType));
				allowing(importService).initImportDataTypeLocalesAndCurrencies(importDataType, importJob);

				allowing(transaction).commit();
				allowing(persistenceSession).beginTransaction(); will(returnValue(transaction));
				allowing(persistenceSession).close();

				allowing(persistenceEngine).getSharedPersistenceSession(); will(returnValue(persistenceSession));
				allowing(persistenceEngine).isCacheEnabled(); will(returnValue(false));
				allowing(persistenceEngine).getEntityManager();	will(returnValue(entityManager));

				allowing(entityManager).setFlushMode(with(any(FlushModeType.class)));

				allowing(importJob).getImportDataTypeName(); will(returnValue("Test Product Association Import Data Type"));
				allowing(importJob).getMappings(); will(returnValue(mapping));
				allowing(importJob).getCatalog(); will(returnValue(catalog));

				allowing(catalog).getCode(); will(returnValue("Test_Catalog"));

				allowing(jobStatusHandler).getImportJobStatus(IMPORT_PROCESS_ID);
				allowing(jobStatusHandler).reportCurrentRow(with(any(String.class)), with(any(int.class)));
				allowing(jobStatusHandler).reportImportJobState(with(any(String.class)), with(any(ImportJobState.class)));
				allowing(jobStatusHandler).isImportJobCancelled(IMPORT_PROCESS_ID); will(returnValue(true));

				allowing(cmUser).getGuid(); will(returnValue("GUID-1"));

				allowing(csvFileReader).close();
				allowing(changeSetService).isChangeSetEnabled(); will(returnValue(false));
			}
		});

	}

	/**
	 * Test method for 'com.elasticpath.service.impl.ImportJobRunnerProductAssociationImpl.run()' when the import CSV
	 * does not have duplicate data, then 'com.elasticpath.service.dataimport.impl.ImportJobStatusHandlerImpl.reportFailedRows()'
	 * is not invoked.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testRunWithoutDuplicateData() {
		final ImportJobRunnerProductAssociationImpl importJobRunnerImpl = setupImportJobRunner(true);
		final List<String[]> csvLines = new ArrayList<>();
		csvLines.add(CSV_LINE1);
		csvLines.add(CSV_LINE2);

		setImportJobType(AbstractImportTypeImpl.INSERT_UPDATE_TYPE);

		context.checking(new Expectations() {
			{
				int lineIndex = 0;

				allowing(csvFileReader).readNext(); will(returnValue(csvLines.get(lineIndex++)));
				oneOf(csvFileReader).getTopLines(importJobRunnerImpl.getCommitUnit()); will(returnValue(csvLines));

				never(jobStatusHandler).reportBadRows(with(equal(IMPORT_PROCESS_ID)), with(IsArray.<ImportBadRow>array(anything())));
				never(jobStatusHandler).reportFailedRows(with(any(String.class)), with(any(int.class)));

				allowing(productAssociationService).findByCriteria(with(any(ProductAssociationSearchCriteria.class)));
				will(returnValue(Collections.emptyList()));

				allowing(persistenceSession).save(with(any(Persistable.class)));
			}
		});

		importJobRunnerImpl.setPersistenceListenerMetadataMap(new LinkedHashMap<>());
		importJobRunnerImpl.run();
	}

	/**
	 * Test method for 'com.elasticpath.service.impl.ImportJobRunnerProductAssociationImpl.run()' when the import CSV has
	 * 2 rows of duplicate data, then 'com.elasticpath.service.dataimport.impl.ImportJobStatusHandlerImpl.reportFailedRows()'
	 * is invoked twice.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testRunWithDuplicateData() {
		final ImportJobRunnerProductAssociationImpl importJobRunnerImpl = setupImportJobRunner(true);
		final List<String[]> csvLines = new ArrayList<>();
		csvLines.add(CSV_LINE1);
		csvLines.add(CSV_LINE1);   // duplicate row
		csvLines.add(CSV_LINE2);
		csvLines.add(CSV_LINE2);   // duplicate row

		setImportJobType(AbstractImportTypeImpl.INSERT_UPDATE_TYPE);

		context.checking(new Expectations() {
			{
				int lineIndex = 0;

				allowing(csvFileReader).readNext(); will(returnValue(csvLines.get(lineIndex++)));
				oneOf(csvFileReader).getTopLines(importJobRunnerImpl.getCommitUnit()); will(returnValue(csvLines));

				exactly(2).of(jobStatusHandler).reportBadRows(with(equal(IMPORT_PROCESS_ID)), with(IsArray.<ImportBadRow>array(anything())));
				exactly(2).of(jobStatusHandler).reportFailedRows(with(any(String.class)), with(any(int.class)));

				allowing(productAssociationService).findByCriteria(with(any(ProductAssociationSearchCriteria.class)));
				will(returnValue(Collections.emptyList()));
				allowing(persistenceSession).save(with(any(Persistable.class)));
			}
		});

		importJobRunnerImpl.setPersistenceListenerMetadataMap(new LinkedHashMap<>());
		importJobRunnerImpl.run();
	}

	/**
	 * Tests deleteExistingProductAssociationsAndAddNewProductAssociation deletes existing product associations
	 * on the source product when the source product imports a new association for the first time.
	 */
	@Test
	public void ensureDeleteProductAssociationsWhenImportTypeIsClearThenInsertAndEntityNotYetImported() {
		setImportJobType(AbstractImportTypeImpl.CLEAR_INSERT_TYPE);

		context.checking(new Expectations() {
			{
				oneOf(importGuidHelper).deleteProductAssociations(with(any(String.class)), with(any(String.class)));

				allowing(persistenceSession).save(with(any(Persistable.class)));
			}
		});

		final ImportJobRunnerProductAssociationImpl importJobRunnerImpl = setupImportJobRunner(false);
		importJobRunnerImpl.importOneRow(CSV_LINE1, persistenceSession);
	}

	/**
	 * Tests deleteExistingProductAssociationsAndAddNewProductAssociation does not delete existing product associations
	 * on the source product when the source product imports subsequent new associations.
	 */
	@Test
	public void ensureDeleteProductAssociationsIsNotCalledWhenImportTypeIsClearThenInsertAndEntityIsAlreadyImported() {
		setImportJobType(AbstractImportTypeImpl.CLEAR_INSERT_TYPE);
		context.checking(new Expectations() {
			{
				never(importGuidHelper).deleteProductAssociations(with(any(String.class)), with(any(String.class)));

				allowing(persistenceSession).save(with(any(Persistable.class)));
			}
		});

		final ImportJobRunnerProductAssociationImpl importJobRunnerImpl = setupImportJobRunner(true);
		importJobRunnerImpl.importOneRow(CSV_LINE1, persistenceSession);
	}

	/**
	 * Tests addNewProductAssociation throws an exception when the product association to insert already exists.
	 */
	@Test(expected = EpServiceException.class)
	public void ensureAddNewProductAssociationThrowsExceptionWhenAssociationAlreadyExists() {
		setImportJobType(AbstractImportTypeImpl.INSERT_TYPE);

		final ProductAssociation existingAssociation = createProductAssociation(SOURCE_PRODUCT_CODE,
				TARGET_PRODUCT1_CODE, 4, new Date(0));

		context.checking(new Expectations() {
			{
				allowing(productAssociationService).findByCriteria(with(any(ProductAssociationSearchCriteria.class)));
				will(returnValue(Collections.singletonList(existingAssociation)));
			}
		});

		final ImportJobRunnerProductAssociationImpl importJobRunnerImpl = setupImportJobRunner(true);
		importJobRunnerImpl.importOneRow(CSV_LINE1, persistenceSession);
	}

	/**
	 * Tests addNewProductAssociation saves the new product association when it doesn't already exist.
	 */
	@Test
	public void ensureAddNewProductAssociationSavesNewProductAssociation() {
		setImportJobType(AbstractImportTypeImpl.INSERT_TYPE);

		context.checking(new Expectations() {
			{
				allowing(productAssociationService).findByCriteria(with(any(ProductAssociationSearchCriteria.class)));
				will(returnValue(Collections.emptyList()));

				oneOf(persistenceSession).save(with(any(Persistable.class)));
			}
		});

		final ImportJobRunnerProductAssociationImpl importJobRunnerImpl = setupImportJobRunner(true);
		importJobRunnerImpl.importOneRow(CSV_LINE1, persistenceSession);
	}

	/**
	 * Tests addOrReplaceNewProductAssociation updates an existing product association.
	 */
	@Test
	public void ensureAddOrReplaceNewProductAssociationUpdatesExistingAssociation() {
		final ProductAssociation existingAssociation = createProductAssociation(SOURCE_PRODUCT_CODE, TARGET_PRODUCT1_CODE, 4, new Date(0));
		final ProductAssociation newAssociation = createProductAssociation(SOURCE_PRODUCT_CODE, TARGET_PRODUCT1_CODE, 4, new Date(0));

		setImportJobType(AbstractImportTypeImpl.INSERT_UPDATE_TYPE);

		context.checking(new Expectations() {
			{
				allowing(productAssociationService).findByCriteria(with(any(ProductAssociationSearchCriteria.class)));
				will(returnValue(Collections.singletonList(existingAssociation)));

				oneOf(persistenceSession).save(existingAssociation);
				never(persistenceSession).save(newAssociation);
			}
		});

		final ImportJobRunnerProductAssociationImpl importJobRunnerImpl = setupImportJobRunner(true);
		importJobRunnerImpl.addOrReplaceNewProductAssociation(CSV_LINE1, persistenceSession, newAssociation);
	}

	/**
	 * Tests addOrReplaceNewProductAssociation saves the new product association when it doesn't already exist.
	 */
	@Test
	public void ensureAddOrReplaceNewProductAssociationSavesNewProductAssociation() {
		final ProductAssociation existingAssociation = createProductAssociation(SOURCE_PRODUCT_CODE, TARGET_PRODUCT1_CODE, 4, new Date(0));
		final ProductAssociation newAssociation = createProductAssociation(SOURCE_PRODUCT_CODE, TARGET_PRODUCT1_CODE, 4, new Date(0));

		setImportJobType(AbstractImportTypeImpl.INSERT_UPDATE_TYPE);

		context.checking(new Expectations() {
			{
				allowing(productAssociationService).findByCriteria(with(any(ProductAssociationSearchCriteria.class)));
				will(returnValue(Collections.emptyList()));

				never(persistenceSession).save(existingAssociation);
				oneOf(persistenceSession).save(newAssociation);
			}
		});

		final ImportJobRunnerProductAssociationImpl importJobRunnerImpl = setupImportJobRunner(true);
		importJobRunnerImpl.addOrReplaceNewProductAssociation(CSV_LINE1, persistenceSession, newAssociation);
	}

	private void setImportJobType(final ImportType importType) {
		context.checking(new Expectations() {
			{
				allowing(importJob).getImportType();
				will(returnValue(importType));
			}
		});
	}

	private ImportJobRequest getRequest(final ImportJob importJob, final Locale locale, final CmUser cmUser) {
		ImportJobRequest request = new ImportJobRequestImpl(IMPORT_PROCESS_ID);
		request.setImportJob(importJob);
		request.setReportingLocale(locale);
		request.setInitiator(cmUser);
		request.setImportSource("file.csv");
		return request;
	}

	private ProductAssociation createProductAssociation(final String sourceProductCode,
			final String targetProductCode,
			final Integer associationTypeOrdinal,
			final Date startDate) {
		ProductAssociation productAssociation = new ProductAssociationImpl();
		Product sourceProduct = new ProductImpl();
		sourceProduct.setCode(sourceProductCode);

		Product targetProduct = new ProductImpl();
		targetProduct.setCode(targetProductCode);

		Catalog catalog = new CatalogImpl();
		catalog.setCode("TestStore");

		productAssociation.setSourceProduct(sourceProduct);
		productAssociation.setTargetProduct(targetProduct);
		productAssociation.setAssociationType(ProductAssociationType.fromOrdinal(associationTypeOrdinal));
		productAssociation.setCatalog(catalog);
		productAssociation.setStartDate(startDate);
		return productAssociation;
	}

	private ImportJobRunnerProductAssociationImpl setupImportJobRunner(
			final boolean isAssociationAlreadyImportedToProduct) {
		ImportJobRunnerProductAssociationImpl importJobRunnerImpl = new ImportJobRunnerProductAssociationImpl() {
			@Override
			protected CsvFileReader getCsvFileReader() {
				return csvFileReader;
			}

			@Override
			protected boolean isAnAssociationAlreadyImportedToProduct(final String sourceProductCode) {
				return isAssociationAlreadyImportedToProduct;
			}

			@Override
			protected void updateContent(final String[] nextLine, final Persistable persistenceObject) {
				Product sourceProduct = new ProductImpl();
				sourceProduct.setCode(nextLine[0]);

				Product targetProduct = new ProductImpl();
				targetProduct.setCode(nextLine[1]);

				Catalog catalog = new CatalogImpl();
				catalog.setCode("TestStore");

				Date startDate = new Date(0);

				((ProductAssociation) persistenceObject).setSourceProduct(sourceProduct);
				((ProductAssociation) persistenceObject).setTargetProduct(targetProduct);
				((ProductAssociation) persistenceObject)
						.setAssociationType(ProductAssociationType.fromOrdinal(Integer.valueOf(nextLine[2])));
				((ProductAssociation) persistenceObject).setCatalog(catalog);
				((ProductAssociation) persistenceObject).setStartDate(startDate);
			}
		};

		importJobRunnerImpl.setImportGuidHelper(importGuidHelper);
		importJobRunnerImpl.setElasticPath(elasticPath);
		importJobRunnerImpl.setImportService(importService);
		importJobRunnerImpl.setProductAssociationService(productAssociationService);
		importJobRunnerImpl.setUtility(this.utility);
		importJobRunnerImpl.setPersistenceEngine(persistenceEngine);
		importJobRunnerImpl.setImportJobStatusHandler(jobStatusHandler);
		importJobRunnerImpl.setChangeSetService(changeSetService);

		importJobRunnerImpl.init(getRequest(importJob, Locale.US, cmUser), IMPORT_PROCESS_ID);
		return importJobRunnerImpl;
	}
}
