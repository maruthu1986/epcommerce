/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.reporting.giftcertificatesummary;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.impl.StoreImpl;

/**
 * Tests that GiftCertificateSummaryReportSection creates store name csv separated string correctly. 
 */
@SuppressWarnings("restriction")
public class GiftCertificateSummaryReportSectionTest {

	private static final int EIGHT = 8;
	private static final int SEVEN = 7;
	private static final int FIVE = 5;
	private static final int FOUR = 4;
	private static final int THREE = 3;
	private final GiftCertificateSummaryReportSection section = new GiftCertificateSummaryReportSection();
	
	/**
	 * Initialization.
	 */
	@Before
	public void setUp() {
		section.setAvailableStores(getAvailableStores(new long[] {1, 2, THREE, FOUR, FIVE}));
	}
	
	/**
	 * Tests that the csv separated strings are formed correctly.
	 */
	@Test	
	public void testCreateCommaSeparatedStoreNames() {		
		String csvString = section.getCommaSeparatedStoreNames(new long[] {2, FIVE});
		String storesNameString = "StoreName_2, StoreName_5"; //$NON-NLS-1$
		assertEquals("Store names csv separated string was not built correctly", storesNameString, csvString); //$NON-NLS-1$
		csvString = section.getCommaSeparatedStoreNames(new long[] {1});
		String storesNameString2 = "StoreName_1"; //$NON-NLS-1$
		assertEquals("Store names csv separated string was not built correctly", storesNameString2, csvString); //$NON-NLS-1$
		csvString = section.getCommaSeparatedStoreNames(new long[] {FOUR, FIVE});
		String storesNameString3 = "StoreName_4, StoreName_5"; //$NON-NLS-1$
		assertEquals("Store names csv separated string was not built correctly", storesNameString3, csvString); //$NON-NLS-1$
	}

	/**
	 * Tests that result string is null when searching for empty uids array.
	 */
	@Test	
	public void testCreateCommaSeparatedStoreNamesWithEmptyUids() {		
		String csvString = section.getCommaSeparatedStoreNames(new long[] {});
		assertNull("Store names csv separated string was not null", csvString); //$NON-NLS-1$		 
	}

	/**
	 * Tests that result string is empty if no matches for uids in array are found.
	 */
	@Test	
	public void testCreateCommaSeparatedStoreNamesWithNonExistentUids() {		
		String csvString = section.getCommaSeparatedStoreNames(new long[] {SEVEN, EIGHT});
		assertEquals("Store names csv separated string was not empty", StringUtils.EMPTY, csvString); //$NON-NLS-1$		 
	}
		
	private List<Store> getAvailableStores(final long[] uidPks) {
		ArrayList<Store> stores = new ArrayList<Store>();
		for (long uidPk : uidPks) {
			Store store = new StoreImpl();
			store.setUidPk(uidPk);
			store.setName("StoreName_" + uidPk); //$NON-NLS-1$
			stores.add(store);
		}
		return stores;
	}
	
}