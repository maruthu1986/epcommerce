/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.inventory.strategy.impl;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;

import com.elasticpath.inventory.InventoryCommand;
import com.elasticpath.inventory.InventoryKey;
import com.elasticpath.inventory.command.impl.AbstractInventoryCommand;
import com.elasticpath.inventory.impl.InventoryDtoAssembler;
import com.elasticpath.inventory.log.InventoryLogContextAware;
import com.elasticpath.inventory.log.impl.InventoryLogSupport;
import com.elasticpath.inventory.strategy.InventoryStrategy;

/**
 * An abstract implementation of the {@link InventoryStrategy} interface.
 * Provides common functionality for the {@link InventoryCommand} execution. 
 */
public abstract class AbstractInventoryStrategy implements InventoryStrategy {

	private final InventoryDtoAssembler inventoryDtoAssembler;
	
	private InventoryLogSupport inventoryLogSupport;
	
	/**
	 * Constructor.
	 */
	public AbstractInventoryStrategy() {
		inventoryDtoAssembler = new InventoryDtoAssembler();
	}
	
	@Override
	public void executeCommand(final InventoryCommand command) {
		((AbstractInventoryCommand) command).execute(inventoryLogSupport);
		
		if (command instanceof InventoryLogContextAware) {
			InventoryLogContextAware logContextAware = (InventoryLogContextAware) command;
			inventoryLogSupport.logCommandExecution(logContextAware.getLogContext());
		}
	}

	/**
	 * Finds an InventoryKey in a set of keys.
	 * 
	 * @param skuCode The SkuCode.
	 * @param keys The set of keys.
	 * @throws IllegalArgumentException If the InventoryKey cannot be found.
	 * @return The InventoryKey if found.
	 */
	protected InventoryKey findInventoryKey(final String skuCode, final Set<InventoryKey> keys) {
		for (final InventoryKey key : keys) {
			if (key.getSkuCode().equals(skuCode)) {
				return key;
			}
		}
		throw new IllegalArgumentException("No inventory key was found for sku code: " + skuCode);
	}
	
	/**
	 * Return the {@link InventoryDtoAssembler} instance.
	 * 
	 * @return The inventoryDtoAssembler.
	 */
	protected InventoryDtoAssembler getInventoryDtoAssembler() {
		return inventoryDtoAssembler;
	}

	/**
	 * Get the SkuCodes from a set of InventoryKeys.
	 * 
	 * @param keys The InventoryKeys.
	 * @return The set of SkuCodes.
	 */
	protected Set<String> getSkuCodesFromInventoryKeys(final Set<InventoryKey> keys) {
		final Set<String> skuCodes = new HashSet<>();
		
		for (final InventoryKey key : keys) {
			skuCodes.add(key.getSkuCode());
		}
		
		return skuCodes;
	}
	
	/**
	 * Creates a map of WarehouseUids to InventoryKeys.
	 * 
	 * @param inventoryKeys The keys to convert into a map.
	 * @return The map.
	 */
	protected Map<Long, Set<InventoryKey>> sortInventoryKeysByWarehouses(final Set<InventoryKey> inventoryKeys) {
		final SetMultimap<Long, InventoryKey> result = HashMultimap.create();
		
		for (final InventoryKey key : inventoryKeys) {
			result.put(key.getWarehouseUid(), key);
		}
		
		return Multimaps.asMap(result);
	}

	/**
	 * Sets the inventory log support.
	 * 
	 * @param inventoryLogSupport inventory log support
	 */
	public void setInventoryLogSupport(final InventoryLogSupport inventoryLogSupport) {
		this.inventoryLogSupport = inventoryLogSupport;
}
	
}
