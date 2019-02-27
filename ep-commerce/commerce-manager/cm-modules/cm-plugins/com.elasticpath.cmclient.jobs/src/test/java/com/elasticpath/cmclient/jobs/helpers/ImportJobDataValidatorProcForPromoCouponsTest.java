/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.jobs.helpers;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import com.elasticpath.domain.dataimport.ImportDataType;
import com.elasticpath.domain.dataimport.ImportField;
import com.elasticpath.domain.dataimport.ImportJob;
import com.elasticpath.domain.dataimport.ImportJobRequest;
import com.elasticpath.service.dataimport.ImportService;

import org.eclipse.rap.rwt.testfixture.TestContext;

/**
 * Test ImportJobDataValidatorProcForPromoCoupons.
 */
public class ImportJobDataValidatorProcForPromoCouponsTest {

	private static final String IMPORT_DATA_TYPE_NAME_FOR_COUPON = "importDataTypeNameForCoupon"; //$NON-NLS-1$

	private static final String EMAIL_ADDRESS = "emailAddress"; //$NON-NLS-1$

	private static final String COUPON_CODE = "couponCode"; //$NON-NLS-1$

	private static final String MESSAGE = "msg"; //$NON-NLS-1$
	
	private ImportJobDataValidatorProcForPromoCoupons validator;

	@Mock
	private ImportJobRequest request;

	@Mock
	private ImportService importService;

	@Mock
	private ImportJob job;

	@Rule
	public final MockitoRule rule = MockitoJUnit.rule();

	@Mock
	private ImportDataType importDataTypeForCoupon;
	
	private Map<String, ImportField> couponEmailMap;
	
	private Map<String, ImportField> couponMap;

	@Rule
	public TestContext context = new TestContext();
	
	/** setup test. */
	@Before
	public void setUp() {
		validator = new ImportJobDataValidatorProcForPromoCoupons(MESSAGE, request) {
			@Override
			protected ImportService getImportService() {
				return importService;
			}
		};

		couponEmailMap = new LinkedHashMap<String, ImportField>();
		couponEmailMap.put(COUPON_CODE, null);
		couponEmailMap.put(EMAIL_ADDRESS, null);
		
		couponMap = new LinkedHashMap<String, ImportField>();
		couponMap.put(COUPON_CODE, null);
		
	}
	
	/**
	 * Test that if we supply a correct header validator returns true.
	 * @throws InterruptedException exception
	 */
	@Test
	public void testDoCsvValidateHeader() throws InterruptedException {
		
		final List<List<String>> previewData = new ArrayList<List<String>>();
		final List<String> header = new ArrayList<String>();
		previewData.add(header);
		header.add(COUPON_CODE);
		header.add(EMAIL_ADDRESS);

		when(request.getImportJob()).thenReturn(job);
		when(importService.getPreviewData(job, 1, true)).thenReturn(previewData);
		when(job.getImportDataTypeName()).thenReturn(IMPORT_DATA_TYPE_NAME_FOR_COUPON);
		when(importService.findImportDataType(IMPORT_DATA_TYPE_NAME_FOR_COUPON)).thenReturn(importDataTypeForCoupon);
		when(importDataTypeForCoupon.getImportFields()).thenReturn(couponEmailMap);

		validator.doCsvValidate();
		assertTrue(validator.csvValidationFaults().isEmpty());
	}

	/**
	 * Test that if we supply an incorrect header validator returns false.
	 * @throws InterruptedException exception
	 */
	@Test
	public void testDoCsvValidateInvalidColumnNameInHeader() throws InterruptedException {
		
		final List<List<String>> previewData = new ArrayList<List<String>>();
		final List<String> header = new ArrayList<String>();
		previewData.add(header);
		header.add(COUPON_CODE + "z"); //$NON-NLS-1$
		header.add(EMAIL_ADDRESS + "z"); //$NON-NLS-1$

		when(request.getImportJob()).thenReturn(job);
		when(importService.getPreviewData(job, 1, true)).thenReturn(previewData);
		when(job.getImportDataTypeName()).thenReturn(IMPORT_DATA_TYPE_NAME_FOR_COUPON);
		when(importService.findImportDataType(IMPORT_DATA_TYPE_NAME_FOR_COUPON)).thenReturn(importDataTypeForCoupon);
		when(importDataTypeForCoupon.getImportFields()).thenReturn(couponEmailMap);

		validator.doCsvValidate();
		assertFalse(validator.csvValidationFaults().isEmpty());
	}
	
	/**
	 * Test that if we supply an incorrect header validator returns false.
	 * @throws InterruptedException exception
	 */
	@Test
	public void testDoCsvValidateWrongColumnNumberInHeader() throws InterruptedException {
		
		final List<List<String>> previewData = new ArrayList<List<String>>();
		final List<String> header = new ArrayList<String>();
		previewData.add(header);
		header.add(COUPON_CODE + "z"); //$NON-NLS-1$
		header.add(EMAIL_ADDRESS + "z"); //$NON-NLS-1$

		when(request.getImportJob()).thenReturn(job);
		when(importService.getPreviewData(job, 1, true)).thenReturn(previewData);
		when(job.getImportDataTypeName()).thenReturn(IMPORT_DATA_TYPE_NAME_FOR_COUPON);
		when(importService.findImportDataType(IMPORT_DATA_TYPE_NAME_FOR_COUPON)).thenReturn(importDataTypeForCoupon);
		when(importDataTypeForCoupon.getImportFields()).thenReturn(couponMap);

		validator.doCsvValidate();
		assertFalse(validator.csvValidationFaults().isEmpty());
	}
	
	/**
	 * Test that if we supply an incorrect header validator returns false.
	 * @throws InterruptedException exception
	 */
	@Test
	public void testDoCsvValidateEmptyHeader() throws InterruptedException {
		
		final List<List<String>> previewData = new ArrayList<List<String>>();

		when(request.getImportJob()).thenReturn(job);
		when(importService.getPreviewData(job, 1, true)).thenReturn(previewData);
		when(job.getImportDataTypeName()).thenReturn(IMPORT_DATA_TYPE_NAME_FOR_COUPON);
		when(importService.findImportDataType(IMPORT_DATA_TYPE_NAME_FOR_COUPON)).thenReturn(importDataTypeForCoupon);
		when(importDataTypeForCoupon.getImportFields()).thenReturn(couponEmailMap);

		validator.doCsvValidate();
		assertFalse(validator.csvValidationFaults().isEmpty());
	}
}
