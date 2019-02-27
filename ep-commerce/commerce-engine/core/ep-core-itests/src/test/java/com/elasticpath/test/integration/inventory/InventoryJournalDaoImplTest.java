/**
 * Copyright (c) Elastic Path Software Inc., 2011
 */
package com.elasticpath.test.integration.inventory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.TableGenerator;

import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.domain.catalog.AvailabilityCriteria;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.store.impl.WarehouseImpl;
import com.elasticpath.inventory.InventoryDto;
import com.elasticpath.inventory.InventoryKey;
import com.elasticpath.inventory.dao.InventoryDao;
import com.elasticpath.inventory.dao.InventoryJournalDao;
import com.elasticpath.inventory.domain.InventoryJournal;
import com.elasticpath.inventory.domain.impl.InventoryImpl;
import com.elasticpath.inventory.domain.impl.InventoryJournalImpl;
import com.elasticpath.inventory.strategy.InventoryJournalRollup;
import com.elasticpath.service.catalog.ProductSkuService;
import com.elasticpath.test.db.DbTestCase;
import com.elasticpath.test.integration.DirtiesDatabase;

/**
 * Tests the implementation of {@link InventoryJournalDao}.
 */
public class InventoryJournalDaoImplTest extends DbTestCase {

	private static final String SKU_CODE1 = "skuCode1";
	private static final String SKU_CODE2 = "skuCode2";
	private static final String SKU_CODE3 = "skuCode3";
	private static final String SKU_CODE4 = "skuCode4";
	private static final String SKU_CODE5 = "skuCode5";
	private static final String EMPTY_SKU_CODE = "emptySkuCode";
	private static final String NON_EXISTENT_SKU_CODE = "nonExistentSku";
	private static final String SKU_CODE_WITH_NO_INVENTORY = "skuCodeWithNoInventory";
	
	private static final long NON_EXISTENT_WAREHOUSE_UID = 99L;
	private static final long WAREHOUSE_10 = 10L;
	private static final long WAREHOUSE_100 = 100L;
	
	private Date restockDate;

	@Autowired
	private InventoryDao inventoryDao;

	@Autowired
	private ProductSkuService productSkuService;

	@Autowired
	private InventoryJournalDao inventoryJournalDao;

	/**
	 * Sets up the test case.
	 */
	@Before
	public void setUp() throws Exception {
		addInventoryJournalEntry(SKU_CODE1, WAREHOUSE_10, 2, 3);
		addInventoryJournalEntry(SKU_CODE1, WAREHOUSE_10, -23, -29);
		addInventoryJournalEntry(SKU_CODE2, WAREHOUSE_10, 31, 37);
		addInventoryJournalEntry(SKU_CODE1, WAREHOUSE_100, 101, 103);
		addInventoryJournalEntry(EMPTY_SKU_CODE, WAREHOUSE_10, 0, 0);
		addInventoryJournalEntry(SKU_CODE3, WAREHOUSE_100, 4, -6);
		addInventoryJournalEntry(SKU_CODE3, WAREHOUSE_100, 4, -2);
		addInventoryJournalEntry(SKU_CODE_WITH_NO_INVENTORY, WAREHOUSE_10, 245, 634);
		
		restockDate = getRestockDate();

		addInventory(SKU_CODE1, WAREHOUSE_10, 30, 3, 40, 10, 11, restockDate, false);
		addInventory(SKU_CODE2, WAREHOUSE_10, 14, 5, 60, 12, 13, restockDate, false);
		addInventory(EMPTY_SKU_CODE, WAREHOUSE_10, 0, 0, 0, 0, 0, restockDate, false);
		addInventory(SKU_CODE3, WAREHOUSE_100, 9, 5, 1, 11, 6, restockDate, false);
		
		addInventory(SKU_CODE5, WAREHOUSE_10, 11, 5, 10, 26, 18, restockDate, false);
		addInventoryJournalEntry(SKU_CODE5, WAREHOUSE_10, 4, -2);		
	}

	/**
	 * No entries should match so the sum should be 0.
	 */
	@DirtiesDatabase
	@Test
	public void testSumNoMatches() {
		InventoryJournalRollup inventoryJournalRollup = inventoryJournalDao.getRollup(new InventoryKey("skuCodeDoesNotExist", 1000l));
		verifyQuantitiesOnInventoryJournalRollup(inventoryJournalRollup, 0, 0);
	}
	
	/**
	 * Two entries should be summed and two entries ignored.
	 */
	@DirtiesDatabase
	@Test
	public void testSumSkuCode1AndWarehouseUid10() {
		InventoryJournalRollup inventoryJournalRollup = inventoryJournalDao.getRollup(new InventoryKey(SKU_CODE1, WAREHOUSE_10));
		int expectedAllocatedQuantityDelta = -21;
		int expectedQuantityOnHandDelta = -26;
		verifyQuantitiesOnInventoryJournalRollup(inventoryJournalRollup, expectedAllocatedQuantityDelta, expectedQuantityOnHandDelta);
	}

	/**
	 * Test that we can remove inventory journal entries that belong to certain inventories by keys.
	 */
	@DirtiesDatabase
	@Test
	public void removeJournalRowByInventoryKey() {
		InventoryKey key = new InventoryKey(SKU_CODE1, WAREHOUSE_10);

		List<Long> uidsByKey = inventoryJournalDao.getUidsByKey(key);
		assertFalse(uidsByKey.isEmpty()); //assert that journal row exists
		
		inventoryJournalDao.removeByKey(key); //remove it
		uidsByKey = inventoryJournalDao.getUidsByKey(key);
		assertTrue(uidsByKey.isEmpty()); //and assert that it's gone now
	}
	
	/**
	 * Test the retrieving low stock functionality for the journaling dao. 
	 */
	@DirtiesDatabase
	@Test
	public void testFindLowStockInventories() {
		List<InventoryDto> inventoryResults = getLowStockInventoryResults(WAREHOUSE_10, SKU_CODE1, SKU_CODE2);
		int expectedResultCount = 2;
		verifyInventoryDtoResultCount(inventoryResults, expectedResultCount);
		
		InventoryDto inventoryDto = getDtoFromListBySkuCodeAndWarehouseUid(inventoryResults, SKU_CODE1, WAREHOUSE_10);	
		int expectedOnHandQuantity = 4;
		int expectedAllocatedQuantity = -18;
		verifyQuantitiesOnInventoryDto(inventoryDto, expectedOnHandQuantity, expectedAllocatedQuantity);
		
		inventoryDto = getDtoFromListBySkuCodeAndWarehouseUid(inventoryResults, SKU_CODE2, WAREHOUSE_10);	
		expectedOnHandQuantity = 51;
		expectedAllocatedQuantity = 36;
		verifyQuantitiesOnInventoryDto(inventoryDto, expectedOnHandQuantity, expectedAllocatedQuantity);
	}
	
	/**
	 * Test the retrieving low stock functionality with a non-existent sku code. 
	 */
	@DirtiesDatabase
	@Test
	public void testLowStockWithMissingSku() {
		List<InventoryDto> inventoryResults = getLowStockInventoryResults(WAREHOUSE_10, NON_EXISTENT_SKU_CODE);
		verifyInventoryDtoResultCount(inventoryResults, 0);
	}
	
	/**
	 * Test the retrieving low stock functionality from a specific warehouse with an empty sku code list. 
	 */
	@DirtiesDatabase
	@Test
	public void testLowStockWithEmptySkuList() {
		List<InventoryDto> inventoryResults = getLowStockInventoryResults(WAREHOUSE_10);
		int expectedResultCount = 4;
		verifyInventoryDtoResultCount(inventoryResults, expectedResultCount);
		verifySkuCodesInResults(inventoryResults, WAREHOUSE_10, SKU_CODE1, SKU_CODE2, EMPTY_SKU_CODE, SKU_CODE5);
	}
	
	/**
	 * Test the retrieving low stock functionality with a non-existent warehouse uid. 
	 */
	@DirtiesDatabase
	@Test
	public void testLowStockWithMissingWarehouse() {
		List<InventoryDto> inventoryResults = getLowStockInventoryResults(NON_EXISTENT_WAREHOUSE_UID, SKU_CODE1);		
		verifyInventoryDtoResultCount(inventoryResults, 0);
	}

	/**
	 * Test the retrieving low stock functionality with a non-existent inventory. 
	 */
	@DirtiesDatabase
	@Test
	public void testLowStockWithMissingInventory() {
		List<InventoryDto> inventoryResults = getLowStockInventoryResults(NON_EXISTENT_WAREHOUSE_UID, SKU_CODE_WITH_NO_INVENTORY);		
		verifyInventoryDtoResultCount(inventoryResults, 0);
	}

	/**
	 * Test the retrieving low stock functionality with all inventory values set to zero. 
	 */
	@DirtiesDatabase
	@Test
	public void testLowStockWithAllZeroes() {
		List<InventoryDto> inventoryResults = getLowStockInventoryResults(WAREHOUSE_10, EMPTY_SKU_CODE);		
		verifyInventoryDtoResultCount(inventoryResults, 1);

		int expectedOnHandQuantity = 0;
		int expectedAllocatedQuantity = 0;
		InventoryDto dto = inventoryResults.get(0);
		verifyQuantitiesOnInventoryDto(dto, expectedOnHandQuantity, expectedAllocatedQuantity);
	}

	/**
	 * Test the retrieving low stock functionality when on-hand quantity exactly matches reorder minimum. 
	 */
	@DirtiesDatabase
	@Test
	public void testLowStockWhenOnHandEqualsReorderMinimum() {
		List<InventoryDto> inventoryResults = getLowStockInventoryResults(WAREHOUSE_100, SKU_CODE3);
		verifyInventoryDtoResultCount(inventoryResults, 1);
		InventoryDto dto = inventoryResults.get(0);
		int expectedOnHandQuantity = 1;
		verifyQuantitiesOnInventoryDto(dto, expectedOnHandQuantity, 13);
	}

	/**
	 * Test the retrieving low stock functionality before and after a new journal entry is added. 
	 */
	@DirtiesDatabase
	@Test
	public void testLowStockBeforeAndAfterANewJournalIsAdded() {
		addInventory(SKU_CODE4, WAREHOUSE_10, 8, 5, 10, 13, 2, restockDate, false);
		List<InventoryDto> inventoryResults = getLowStockInventoryResults(WAREHOUSE_10, SKU_CODE4);
		verifyInventoryDtoResultCount(inventoryResults, 0);

		addInventoryJournalEntry(SKU_CODE4, WAREHOUSE_10, 3, 0);
		inventoryResults = getLowStockInventoryResults(WAREHOUSE_10, SKU_CODE4);
		verifyInventoryDtoResultCount(inventoryResults, 1);

		InventoryDto dto = inventoryResults.get(0);
		verifyQuantitiesOnInventoryDto(dto, 8, 8);
	}
	
	/**
	 * Test retrieval of all inventory keys. 
	 */
	@DirtiesDatabase
	@Test
	public void testGetAllInventoryKeys() {
		List<InventoryKey> inventoryKeys = inventoryJournalDao.getAllInventoryKeys(0);
		assertEquals("Inventory Key count is incorrect.", 7, inventoryKeys.size());
		verifyKeyInListWithSkuCodeAndWarehouseUid(inventoryKeys, SKU_CODE1, WAREHOUSE_10);
		verifyKeyInListWithSkuCodeAndWarehouseUid(inventoryKeys, SKU_CODE1, WAREHOUSE_100);
		verifyKeyInListWithSkuCodeAndWarehouseUid(inventoryKeys, SKU_CODE2, WAREHOUSE_10);
		verifyKeyInListWithSkuCodeAndWarehouseUid(inventoryKeys, EMPTY_SKU_CODE, WAREHOUSE_10);
		verifyKeyInListWithSkuCodeAndWarehouseUid(inventoryKeys, SKU_CODE3, WAREHOUSE_100);
		verifyKeyInListWithSkuCodeAndWarehouseUid(inventoryKeys, SKU_CODE_WITH_NO_INVENTORY, WAREHOUSE_10);
		verifyKeyInListWithSkuCodeAndWarehouseUid(inventoryKeys, SKU_CODE5, WAREHOUSE_10);
	}
	
	/**
	 * Test getRollupByUids() functionality.
	 */
	@DirtiesDatabase
	@Test
	public void testGetRollupByUids() {
		InventoryKey inventoryKey = new InventoryKey(SKU_CODE1, WAREHOUSE_10);
		List<Long> journalUids = inventoryJournalDao.getUidsByKey(inventoryKey);
		assertEquals("Journal uid list size incorrect.", journalUids.size(), 2);
		InventoryJournalRollup inventoryJournalRollup = inventoryJournalDao.getRollupByUids(journalUids);
		verifyQuantitiesOnInventoryJournalRollup(inventoryJournalRollup, -21, -26);
	}
	
	/**
	 * Test getRollupByUids() functionality with non-existent uid.
	 */
	@DirtiesDatabase
	@Test
	public void testGetRollupByUidsWithNonExistentUid() {
		InventoryKey inventoryKey = new InventoryKey(SKU_CODE2, WAREHOUSE_100);
		List<Long> journalUids = inventoryJournalDao.getUidsByKey(inventoryKey);
		assertEquals("Journal uid list size incorrect.", journalUids.size(), 0);
	}

	/**
	 * Test getRollupByUids() functionality.
	 */
	@DirtiesDatabase
	@Test
	public void testGetRollupBySkusInWarehouse() {
		Set<String> skuCodes = Sets.newHashSet(SKU_CODE1, SKU_CODE2);
		int skuCodesSize = skuCodes.size();

		Map<String, InventoryJournalRollup> rollups = inventoryJournalDao.getInventoryRollupsForSkusInWarehouse(skuCodes, WAREHOUSE_10);

		assertEquals("There should be " + skuCodesSize + " rollups", rollups.size(), 2);

		verifyQuantitiesOnInventoryJournalRollup(rollups.get(SKU_CODE1), -21, -26);
		verifyQuantitiesOnInventoryJournalRollup(rollups.get(SKU_CODE2), 31, 37);
	}
	
	/**
	 * Test removeAll functionality.
	 */
	@DirtiesDatabase
	@Test
	public void testRemoveAll() {
		List<Long> journalUids = getJournalIdsForSkuCodeAndWarehouseUid(SKU_CODE1, WAREHOUSE_10);
		assertEquals("Journal uid list size incorrect.", journalUids.size(), 2);
		inventoryJournalDao.removeAll(journalUids);
		InventoryJournalRollup inventoryJournalRollup = inventoryJournalDao.getRollupByUids(journalUids);
		assertTrue("No rollup result should be returned for this key.", inventoryJournalRollup == null);
		
		journalUids = getJournalIdsForSkuCodeAndWarehouseUid(SKU_CODE1, WAREHOUSE_100);
		assertEquals("Journal uid list size incorrect.", journalUids.size(), 1);
		inventoryJournalRollup = inventoryJournalDao.getRollupByUids(journalUids);
		verifyQuantitiesOnInventoryJournalRollup(inventoryJournalRollup, 101, 103);
	}


	@DirtiesDatabase
	@Test
	public void testAllocationSize() throws Exception {

		final String jpaGeneratedKeysNativeQuery = "select last_value from jpa_generated_keys where id='TINVENTORYJOURNAL'";
		final int lowerLoopBoundary = 2000;
		final int allocationSize = getAllocationSizeFromAnnotatedMethod("getUidPk");

		//ensure that allocationSize attribute has desired value
		assertEquals(10000, allocationSize);

		final int upperLoopBoundary = lowerLoopBoundary + allocationSize;

		Connection conn = null;
		PreparedStatement stmt = null;

		try{
			conn = getPersistenceEngine().getConnection();
			stmt = conn.prepareStatement(jpaGeneratedKeysNativeQuery);

			//get the latest state from jpa_generated_keys table for TINVENTORYJOURNAL table
			int lastGeneratedKey = getLastGeneratedKey(stmt);

			//insert *allocationSize* records into TINVENTORYJOURNAL table
			for (int i=lowerLoopBoundary;i<upperLoopBoundary;i++) {
				addInventoryJournalEntry(SKU_CODE1, WAREHOUSE_10, i, 3000);
			}

			//get the latest state from jpa_generated_keys table
			int newGeneratedKey = getLastGeneratedKey(stmt);

			//new generated key must be exactly *allocationSize* far from the last one
			assertEquals(lastGeneratedKey + allocationSize, newGeneratedKey);

		}finally {
			stmt.close();
			conn.close();
		}
	}

	private int getAllocationSizeFromAnnotatedMethod(final String method) throws Exception {
		TableGenerator tableGeneratorAnnotation = InventoryJournalImpl.class.getMethod(method).getDeclaredAnnotation(TableGenerator.class);
		return tableGeneratorAnnotation.allocationSize();
	}

	private int getLastGeneratedKey(final PreparedStatement stmt) throws Exception {
		ResultSet rs = null;

		try {
			rs = stmt.executeQuery();

			if (rs.next()){
				return rs.getInt("last_value");
			}

			return 0;
		} finally {
			rs.close();
		}
	}
	
	private List<Long> getJournalIdsForSkuCodeAndWarehouseUid(String skuCode, long warehouseUid) {
		InventoryKey inventoryKey = new InventoryKey(skuCode, warehouseUid);
		List<Long> journalUids = inventoryJournalDao.getUidsByKey(inventoryKey);
		return journalUids;
	}
	
	private void verifyKeyInListWithSkuCodeAndWarehouseUid(List<InventoryKey> inventoryKeys, String skuCode, long warehouseUid) {
		InventoryKey inventoryKey = getKeyFromListBySkuCodeAndWarehouseUid(inventoryKeys, skuCode, warehouseUid);
		assertTrue ("Unable to find key with sku code " + skuCode + " and warehouse uid " + Long.toString(warehouseUid), inventoryKey != null);
	}
	
	private void verifyQuantitiesOnInventoryDto(InventoryDto dto, int onHandQuantity, int allocatedQuantity) {
		assertEquals("Inventory on hand quantity doesn't match calculated value", onHandQuantity, dto.getQuantityOnHand());
		assertEquals("Inventory allocated quantity doesn't match calculated value", allocatedQuantity, dto.getAllocatedQuantity());
	}
	
	private void verifyInventoryDtoResultCount(List<InventoryDto> inventoryResults, int resultCount) {
		String expectedMessage = "Expected " + Integer.toString(resultCount) + " result(s). ";
		assertEquals(expectedMessage, resultCount, inventoryResults.size());
	}

	private void verifyQuantitiesOnInventoryJournalRollup(
			InventoryJournalRollup inventoryJournalRollup, int allocatedQuantityDelta, int quantityOnHandDelta) {
		assertTrue("InventoryJournalRollup is null", inventoryJournalRollup != null);
		assertEquals("Allocated quantity does not match.", allocatedQuantityDelta, inventoryJournalRollup.getAllocatedQuantityDelta());
		assertEquals("Quantity on hand does not match.", quantityOnHandDelta, inventoryJournalRollup.getQuantityOnHandDelta());
	}

	private InventoryJournal createInventoryJournal(final String skuCode, final long warehouseUid,
			final int allocatedQuantityDelta, final int quantityOnHandDelta) {
		final InventoryJournal inventoryJournal = new InventoryJournalImpl();
		inventoryJournal.setAllocatedQuantityDelta(allocatedQuantityDelta);
		inventoryJournal.setQuantityOnHandDelta(quantityOnHandDelta);
		inventoryJournal.setSkuCode(skuCode);
		inventoryJournal.setWarehouseUid(warehouseUid);
		
		return inventoryJournal;
	}
	
	private void addInventoryJournalEntry(final String skuCode, final long warehouseUid,
			final int allocatedQuantityDelta, final int quantityOnHandDelta) {
		final InventoryJournal inventoryJournal = createInventoryJournal(skuCode, warehouseUid, allocatedQuantityDelta, quantityOnHandDelta);
		inventoryJournalDao.saveOrUpdate(inventoryJournal);
	}

	private void addInventory(final String skuCode, final long warehouseUid, final int quantityOnHand,
			final int allocatedQuantity, final int reorderMinimum, final int reservedQuantity,
			final int reorderQuantity, final Date restockDate, final boolean skuExists) {
		InventoryImpl inventory = new InventoryImpl();
		inventory.setSkuCode(skuCode);
		inventory.setWarehouseUid(warehouseUid);
		inventory.setReorderMinimum(reorderMinimum);
		inventory.setReservedQuantity(reservedQuantity);
		inventory.setReorderQuantity(reorderQuantity);
		inventory.setRestockDate(restockDate);
		inventory.setQuantityOnHand(quantityOnHand);
		inventory.setAllocatedQuantity(allocatedQuantity);
		inventoryDao.saveOrUpdate(inventory);

		if (!skuExists) {
			WarehouseImpl warehouse = new WarehouseImpl();
			warehouse.setUidPk(warehouseUid);

			final Product product = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
					scenario.getCategory(), warehouse); // Catalog Test Persister adds the product to inventory. Remove the default sku code
			ProductSku productSku = product.getDefaultSku();
			inventoryJournalDao.removeByKey(new InventoryKey(productSku.getSkuCode(), warehouseUid));
			productSku.setSkuCode(skuCode);
			product.setAvailabilityCriteria(AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK);
			productSkuService.saveOrUpdate(productSku);

		}
	}

	private List<InventoryDto> getLowStockInventoryResults(final long warehouseUid, final String... skuCodes) {
		Set<String> skuCodeSet = new HashSet<>();
		
		if (skuCodes != null) {
			for (String skuCode : skuCodes) {
				skuCodeSet.add(skuCode);
			}
		}

		return inventoryJournalDao.findLowStockInventories(skuCodeSet, warehouseUid); 
	}
	
	private void verifySkuCodesInResults(final List<InventoryDto> inventoryResults, final long warehouseUid, final String... skuCodes) {
		for (String skuCode : skuCodes) {
			InventoryDto inventoryDto = getDtoFromListBySkuCodeAndWarehouseUid(inventoryResults, skuCode, warehouseUid);
			assertFalse("Results with " + skuCode + " should be included.", inventoryDto == null);
		}
	}
	
	private InventoryDto getDtoFromListBySkuCodeAndWarehouseUid(List<InventoryDto> inventoryDtos, String skuCode, long warehouseUid) {
		for (InventoryDto inventoryDto : inventoryDtos) {
			if (inventoryDto.getSkuCode() == skuCode && inventoryDto.getWarehouseUid() == warehouseUid) {
				return inventoryDto;
			}
		}
		return null;
	}

	private InventoryKey getKeyFromListBySkuCodeAndWarehouseUid(List<InventoryKey> inventoryKeys, String skuCode, long warehouseUid) {
		for (InventoryKey inventoryKey : inventoryKeys) {
			if (inventoryKey.getSkuCode() == skuCode && inventoryKey.getWarehouseUid() == warehouseUid) {
				return inventoryKey;
			}
		}
		return null;
	}

	private Date getRestockDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 2011);
		calendar.set(Calendar.MONTH, 4);
		calendar.set(Calendar.DATE, 31);
		return calendar.getTime();
	}
}
