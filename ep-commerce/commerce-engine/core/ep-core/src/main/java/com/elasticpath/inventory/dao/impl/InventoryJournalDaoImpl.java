/**
 * Copyright (c) Elastic Path Software Inc., 2011.
 */
package com.elasticpath.inventory.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.elasticpath.inventory.InventoryDto;
import com.elasticpath.inventory.InventoryKey;
import com.elasticpath.inventory.dao.InventoryJournalDao;
import com.elasticpath.inventory.domain.InventoryJournal;
import com.elasticpath.inventory.strategy.InventoryJournalRollup;
import com.elasticpath.inventory.strategy.impl.InventoryJournalRollupImpl;
import com.elasticpath.persistence.dao.impl.AbstractDaoImpl;

/**
 * Data access methods for <code>InventoryJournal</code>.
 */
public class InventoryJournalDaoImpl extends AbstractDaoImpl implements InventoryJournalDao {

	@Override
	public InventoryJournal saveOrUpdate(final InventoryJournal inventoryJournal) {
		return getPersistenceEngine().saveOrUpdate(inventoryJournal);
	}

	@Override
	public InventoryJournalRollup getRollup(final InventoryKey inventoryKey) {
		final List<InventoryJournalRollup> result = getPersistenceEngine().retrieveByNamedQuery(
				"INVENTORY_JOURNAL_SUM_DELTAS_BY_SKUCODE_AND_WAREHOUSE_UID",
				inventoryKey.getSkuCode(),
				inventoryKey.getWarehouseUid());

		if (result.isEmpty()) {
			return new InventoryJournalRollupImpl(inventoryKey.getSkuCode(), inventoryKey.getWarehouseUid(), 0L, 0L);
		}

		return result.get(0);
	}

	@Override
	public Map<String, InventoryJournalRollup> getInventoryRollupsForSkusInWarehouse(final Set<String> skuCodes, final long warehouseUid) {
		final Map<String, InventoryJournalRollup> result = new HashMap<>();
		final List<InventoryJournalRollup> rollups = getPersistenceEngine().retrieveByNamedQueryWithList(
				"INVENTORY_JOURNAL_SUM_DELTAS_BY_SKUCODES_AND_WAREHOUSE_UID", "list",
				skuCodes, warehouseUid);

		for (InventoryJournalRollup rollup : rollups) {
			result.put(rollup.getInventoryKey().getSkuCode(), rollup);
		}
		return result;
	}

	@Override
	public InventoryJournalRollup getRollupByUids(final List<Long> journalUids) {
		
		List<InventoryJournalRollup> result = getPersistenceEngine().retrieveByNamedQueryWithList(
				"INVENTORY_JOURNAL_ROLLUPS_BY_UIDS", "uids", journalUids);
		
		if (result.isEmpty()) {
			return null;
		}
		
		InventoryJournalRollup baseRollup = result.get(0);
		if (result.size() > 1) {
			int allocationQuantityTotalDelta = 0;
			int quantityOnHandTotalDelta = 0;

			for (InventoryJournalRollup rollUp : result) {
					allocationQuantityTotalDelta += rollUp.getAllocatedQuantityDelta();
					quantityOnHandTotalDelta += rollUp.getQuantityOnHandDelta();
			}

			baseRollup.setAllocatedQuantityDelta(allocationQuantityTotalDelta);
			baseRollup.setQuantityOnHandDelta(quantityOnHandTotalDelta);
		}

		return baseRollup;
		
	}

	@Override
	public List<Long> getUidsByKey(final InventoryKey inventoryKey) {
		return getPersistenceEngine().retrieveByNamedQuery("INVENTORY_JOURNAL_UIDS_BY_KEY",
				inventoryKey.getSkuCode(),
				inventoryKey.getWarehouseUid());
	}

	@Override
	public void removeAll(final List<Long> journalUids) {
		getPersistenceEngine().executeNamedQueryWithList("INVENTORY_JOURNAL_DELETE_BY_UIDS", "uids", journalUids);
	}
	
	@Override
	public void removeByKey(final InventoryKey inventoryKey) {
		getPersistenceEngine().executeNamedQuery("INVENTORY_JOURNAL_DELETE_BY_SKU_WAREHOUSE",
				inventoryKey.getSkuCode(),
				inventoryKey.getWarehouseUid());
	}

	@Override
	public List<InventoryKey> getAllInventoryKeys(final int minimumRollupRows) {
		return getPersistenceEngine().retrieveByNamedQuery("INVENTORY_JOURNAL_ALL_KEYS_WITH_MIN_ROWS", minimumRollupRows);
	}

	@Override
	public List<InventoryDto> findLowStockInventories(final Set<String> skuCodes, final long warehouseUid) {
		if (skuCodes == null || skuCodes.isEmpty()) {
			return getPersistenceEngine().retrieveByNamedQuery("LOWSTOCK_JOURNALING_ALL", warehouseUid);
		}
		return getPersistenceEngine().retrieveByNamedQueryWithList("LOWSTOCK_JOURNALING", "list", new ArrayList<>(skuCodes), warehouseUid);
	}
	
}
