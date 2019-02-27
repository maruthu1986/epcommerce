/**
 * Copyright (c) Elastic Path Software Inc., 2011.
 */
package com.elasticpath.test.integration.inventory;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.NestedRuntimeException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaOptimisticLockingFailureException;
import org.springframework.orm.jpa.JpaSystemException;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.AvailabilityCriteria;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.inventory.InventoryDto;
import com.elasticpath.inventory.InventoryKey;
import com.elasticpath.inventory.dao.InventoryDao;
import com.elasticpath.inventory.dao.InventoryJournalDao;
import com.elasticpath.inventory.dao.InventoryJournalLockDao;
import com.elasticpath.inventory.domain.Inventory;
import com.elasticpath.inventory.domain.InventoryJournal;
import com.elasticpath.inventory.domain.InventoryJournalLock;
import com.elasticpath.inventory.domain.impl.InventoryImpl;
import com.elasticpath.inventory.domain.impl.InventoryJournalImpl;
import com.elasticpath.inventory.strategy.InventoryJournalRollup;
import com.elasticpath.inventory.strategy.impl.JournalingInventoryStrategy;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.test.db.DbTestCase;
import com.elasticpath.test.integration.DirtiesDatabase;

/**
 * Tests the implementation of {@link JournalingInventoryStrategy}.
 */
public class ConcurrentInventoryJournalRollupServiceTest extends DbTestCase {

	private final static Logger LOG = Logger.getLogger(ConcurrentInventoryJournalRollupServiceTest.class); 
	
	@Autowired private InventoryDao inventoryDao;
	@Autowired private InventoryJournalDao inventoryJournalDao;
	@Autowired private InventoryJournalLockDao inventoryJournalLockDao;
	@Autowired
	@Qualifier("syncedInventoryJournalRollupService1")
	private SyncedInventoryJournalRollupService syncedInventoryJournalRollupService1;
	@Autowired
	@Qualifier("syncedInventoryJournalRollupService2")
	private SyncedInventoryJournalRollupService syncedInventoryJournalRollupService2;
	@Autowired private PersistenceEngine persistenceEngine;
	@Autowired private JournalingInventoryStrategy journalingInventoryStrategy;
	
	InventoryKey inventoryKey1;
	InventoryKey inventoryKey2;
	private Thread rollupThread1;
	private Thread rollupThread2;
	
	Map<InventoryKey, LockWrapper> lockWrapperMap = new HashMap<>();
	List<Throwable> exceptions = new ArrayList<>();

	private InventoryKey inventoryKey3;
	
	/**
	 * Sets up the test case.
	 */
	@Before
	public void setUp() throws Exception {
		inventoryKey1 = new InventoryKey("skuCode1", 10l);
		insertInventory(inventoryKey1, 2, 10);
		
		inventoryKey2 = new InventoryKey("skuCode2", 20l);
		insertInventory(inventoryKey2, 3, 15);
		
		insertInventoryJournal(inventoryKey1, 2, 0);
		insertInventoryJournal(inventoryKey1, 0, 3);
		insertInventoryJournal(inventoryKey1, -4, -4);
		
		insertInventoryJournal(inventoryKey2, 7, 0);
		insertInventoryJournal(inventoryKey2, 0, -2);
		insertInventoryJournal(inventoryKey2, -11, -11);
		
		insertInventoryJournalLock(inventoryKey1);
		
		// This journal only have one row, should not be touched.
		inventoryKey3 = new InventoryKey("sku3", 30l);
		insertInventoryJournal(inventoryKey3, 100, 0);
		
		lockWrapperMap.put(inventoryKey1, new LockWrapper(inventoryKey1));
		lockWrapperMap.put(inventoryKey2, new LockWrapper(inventoryKey2));
	}

	private void insertInventoryJournalLock(final InventoryKey inventoryKey) {
		InventoryJournalLock inventoryJournalLock = getBeanFactory().getBean(ContextIdNames.INVENTORY_JOURNAL_LOCK);
		inventoryJournalLock.setSkuCode(inventoryKey.getSkuCode());
		inventoryJournalLock.setWarehouseUid(inventoryKey.getWarehouseUid());
		inventoryJournalLockDao.saveOrUpdate(inventoryJournalLock);
	}

	private void insertInventoryJournal(final InventoryKey inventoryKey, final int allocateDelta, final int onHandDelta) {
		InventoryJournal inventoryJournal = new InventoryJournalImpl();
		inventoryJournal.setAllocatedQuantityDelta(allocateDelta);
		inventoryJournal.setQuantityOnHandDelta(onHandDelta);
		inventoryJournal.setSkuCode(inventoryKey.getSkuCode());
		inventoryJournal.setWarehouseUid(inventoryKey.getWarehouseUid());
		inventoryJournalDao.saveOrUpdate(inventoryJournal);
	}

	private void insertInventory(final InventoryKey inventoryKey, final int allocatedQuantity, final int quantityOnHand) {
		InventoryImpl inventory = new InventoryImpl();
		inventory.setAllocatedQuantity(allocatedQuantity);
		inventory.setQuantityOnHand(quantityOnHand);
		inventory.setSkuCode(inventoryKey.getSkuCode());
		inventory.setWarehouseUid(inventoryKey.getWarehouseUid());
		inventoryDao.saveOrUpdate(inventory);
	}
	
	private long getJournalRowCount() {
		List<Long> res = persistenceEngine.retrieve("SELECT COUNT(j) FROM InventoryJournalImpl j");
		
		if (res.isEmpty()) {
			return 0;
		}
		
		return res.get(0);
	}

	void rollup(final SyncedInventoryJournalRollupService inventoryJournalRollupService) {
		LOG.info("InventoryJournal rollup started");
		List<InventoryKey> inventoryKeys = inventoryJournalRollupService.getAllInventoryKeys();
		
		assertEquals(2, inventoryKeys.size());
		
		for (InventoryKey inventoryKey : inventoryKeys) {
			LockWrapper lockWrapper = lockWrapperMap.get(inventoryKey);
			try {
				inventoryJournalRollupService.processRollup(inventoryKey, lockWrapper);
			} catch (DataIntegrityViolationException e) {
				logException(e);
			} catch (JpaOptimisticLockingFailureException e) {
				logException(e);
			} catch (JpaSystemException e) {
				logException(e);
			}
		}
		LOG.info("InventoryJournal rollup ended");
	}
	
	private void logException(final NestedRuntimeException e) {
		LOG.info("InventoryJournal rollup contention, task terminated: " + e.getMostSpecificCause().getMessage());
		exceptions.add(e);
	}

	/**
	 * rollup inventory journal.
	 */
	@DirtiesDatabase
	@Test
	public void testConcurrentRollup() {

		final ProductSku productSku1 = createDbProductSKU(inventoryKey1, AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK);
		final ProductSku productSku2 = createDbProductSKU(inventoryKey2, AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK);

		// TINVENTORYJOURNAL shall have 9 rows.
		assertEquals(9, getJournalRowCount());
		
		rollupConcurrently();
		
		// wait until both threads finish
		try {
			rollupThread1.join();
			rollupThread2.join();
		} catch (InterruptedException e) {
		}
		
		assertEquals(2, exceptions.size());
		
		// TINVENTORYJOURNAL shall only have 5 rows now.
		assertEquals(5, getJournalRowCount());
				
		// TINVENTORY is not modified by roll up service.
		Inventory inventory1 = inventoryDao.getInventory(inventoryKey1.getSkuCode(), inventoryKey1.getWarehouseUid());
		assertEquals(2, inventory1.getAllocatedQuantity());
		assertEquals(10, inventory1.getQuantityOnHand());

		Inventory inventory2 = inventoryDao.getInventory(inventoryKey2.getSkuCode(), inventoryKey2.getWarehouseUid());
		assertEquals(3, inventory2.getAllocatedQuantity());
		assertEquals(15, inventory2.getQuantityOnHand());
		
		InventoryJournalRollup rollup = inventoryJournalDao.getRollup(inventoryKey1);
		assertEquals(-2, rollup.getAllocatedQuantityDelta());
		assertEquals(-1, rollup.getQuantityOnHandDelta());
		
		rollup = inventoryJournalDao.getRollup(inventoryKey2);
		assertEquals(-4, rollup.getAllocatedQuantityDelta());
		assertEquals(-13, rollup.getQuantityOnHandDelta());
		
		InventoryDto inventoryDto1 = journalingInventoryStrategy.getInventory(productSku1, inventory1.getWarehouseUid());
		assertEquals(0, inventoryDto1.getAllocatedQuantity());
		assertEquals(9, inventoryDto1.getQuantityOnHand());
		assertEquals(9, inventoryDto1.getAvailableQuantityInStock());
		
		InventoryDto inventoryDto2 = journalingInventoryStrategy.getInventory(productSku2, inventory2.getWarehouseUid());
		assertEquals(-1, inventoryDto2.getAllocatedQuantity());
		assertEquals(2, inventoryDto2.getQuantityOnHand());
		assertEquals(3, inventoryDto2.getAvailableQuantityInStock());
	}

	private void rollupConcurrently() {
		rollupThread1 = new Thread( () -> rollup(syncedInventoryJournalRollupService1));
		rollupThread2 = new Thread( () -> rollup(syncedInventoryJournalRollupService2));
		
		rollupThread1.start();
		rollupThread2.start();
	}
}
