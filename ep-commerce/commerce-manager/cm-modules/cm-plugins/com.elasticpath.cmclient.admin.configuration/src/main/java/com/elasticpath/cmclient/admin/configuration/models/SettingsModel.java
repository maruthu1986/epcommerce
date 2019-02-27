/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.admin.configuration.models;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.PredicateUtils;

import com.elasticpath.cmclient.admin.configuration.listener.SettingDefinitionUpdateListener;
import com.elasticpath.cmclient.admin.configuration.listener.SettingValueUpdateListener;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.settings.SettingsService;
import com.elasticpath.settings.domain.SettingDefinition;
import com.elasticpath.settings.domain.SettingMetadata;
import com.elasticpath.settings.domain.SettingValue;

/**
 * The settings model separates our views from the underlying services, logic, and other UI elements
 * that use the same set of Settings Definition objects.
 * 
 * This provides a separate application logic layer in between the UI and services for us to perform operations 
 * on the domain model objects being used by the UI layer before they are passed on for display.
 * e.g. Providing filtering of definitions for display 
 * 
 * Methods are delegated through to the settings service when needed.
 *
 */
public class SettingsModel {

	private Set <SettingDefinition> currentDefinitions;
	
	private final List<SettingDefinitionUpdateListener> listeners = new LinkedList<>();
	
	private final List<SettingValueUpdateListener> valueListeners = new LinkedList<>();

	private final SettingsService settingsService = 
		(SettingsService) ServiceLocator.getService(ContextIdNames.SETTINGS_SERVICE);
	
	/**
	 * Get all settings definitions. This doesn't need to go to the service every time.
	 * The view might not need to know of all the latest set of definitions until a refresh button is pressed.
	 *  
	 * @return all settings definitions held by the model 
	 */
	public Set <SettingDefinition> getAllDefinitions() {
		if (currentDefinitions == null) {
			currentDefinitions = settingsService.getAllSettingDefinitions(); 
		}
		return currentDefinitions;
	}
	
	/**
	 * Get all setting values for a specific setting definition. 
	 * @param definition the definition for which you want to retrieve the values
	 * @return all setting values held by the model
	 */
	public Set<SettingValue> getAllValues(final SettingDefinition definition) {
		return settingsService.getSettingValues(definition.getPath(), null);
	}
	
	
	/**
	 * Get the set of metadata for a definition that's managed by our model class.
	 * This will ensure correct object state on editing and persistence.
	 *  
	 * @param definition the definition to match
	 * @return set of settings metadata
	 */
	public Set <SettingMetadata> getManagedMetadataForDefinition(final SettingDefinition definition) {
		if (!currentDefinitions.contains(definition)) {
			// Add definition to managed set if not already in cache. Always get a fresh copy from service.
			currentDefinitions.add(settingsService.getSettingDefinition(definition.getPath()));
		}
		
		SettingDefinition def = (SettingDefinition) CollectionUtils.find(currentDefinitions, PredicateUtils.equalPredicate(definition));
		
		Set <SettingMetadata> metadataSet = new HashSet<>();
		metadataSet.addAll(def.getMetadata().values());
		
		return metadataSet;
	}
	


	/**
	 * Fire notification to listeners that a definition has changed.
	 * @param definition that was changed
	 */
	protected void fireDefinitionChanged(final SettingDefinition definition) {
		for (SettingDefinitionUpdateListener listenener : listeners) {
			listenener.settingDefinitionUpdated(definition);
		}
	}

	/**
	 * Get setting value for multiple contexts.
	 * 
	 * @param path the setting definition path
	 * @param contexts the contexts to lookup
	 * @return set of setting values found
	 */
	public Set<SettingValue> getSettingValues(final String path, final String ... contexts) {
		return settingsService.getSettingValues(path, contexts);
	}
	
	/**
	 * Get setting value for a context.
	 * 
	 * @param path the setting definition path
	 * @param context the contexts to lookup
	 * @return setting value
	 */
	public SettingValue getSettingValue(final String path, final String context) {
		return settingsService.getSettingValue(path, context);
	}

	/**
	 * Create Setting Value.
	 * 
	 * @param definition the definition to create setting value under
	 * @param context the context to create
	 * @return settingValue created
	 */
	public SettingValue createSettingValue(
			final SettingDefinition definition, final String context) {
		return settingsService.createSettingValue(definition, context);
	}

	/**
	 * Add setting value and notify any listeners of updates.
	 * 
	 * @param definition setting definition to which the value belongs
	 * @param value the settingvalue that's been added
	 */
	public void addSettingValue(final SettingDefinition definition, final SettingValue value) {
		settingsService.updateSettingValue(value);
		fireSettingValueUpdated(value);
	}
	
	/**
	 * Update setting value and notify any listeners of updates.
	 * 
	 * @param definition setting definition to which the value belongs
	 * @param value the settingvalue that's been updated
	 */
	public void updateSettingValue(final SettingDefinition definition, final SettingValue value) {
		SettingValue updatedValue = settingsService.updateSettingValue(value);
		fireSettingValueUpdated(updatedValue);
	}
	
	/**
	 * Edit metadata for a definition.
	 * Adds/replaces metadata in the setting definition for given metadata's key.
	 * This expects that keys don't get changed.
	 * 
	 * @param def the definition to edit.
	 * @param metadata to edit.
	 */
	public void updateManagedDefinitionMetadata(final SettingDefinition def, final SettingMetadata metadata) {
		SettingDefinition definition = (SettingDefinition) CollectionUtils.find(currentDefinitions, PredicateUtils.equalPredicate(def));
		definition.getMetadata().put(metadata.getKey(), metadata);
		this.updateSettingDefinition(definition);	
	}
	
	
	/**
	 * Managed method for adding a setting definition metadata.
	 * Uses cached copy of definitions to do any real work.
	 * 
	 * @param def the definition to add metadata to.
	 * @param metadata to add to the definition.
	 */
	public void addManagedDefinitionMetadata(final SettingDefinition def, final SettingMetadata metadata) {
		updateManagedDefinitionMetadata(def, metadata);
	}
	

	/**
	 * Removes metadata from managed setting definition.
	 * 
	 * @param def the definition to remove metadata from
	 * @param settingMetadata the metadata to remove
	 */
	public void deleteManagedSettingMetadata(final SettingDefinition def, final SettingMetadata settingMetadata) {
		SettingDefinition definition = (SettingDefinition) CollectionUtils.find(currentDefinitions, PredicateUtils.equalPredicate(def));
		definition.getMetadata().remove(settingMetadata.getKey());
		updateSettingDefinition(definition);
	}
	
	/**
	 * Fire notification to listeners that a value has changed.
	 * @param value that was changed
	 */
	private void fireSettingValueUpdated(final SettingValue value) { 
		for (SettingValueUpdateListener listenener : valueListeners) {
			listenener.settingValueUpdated(value);
		}
	}

	/**
	 * Registers a <code>SettingDefinitionUpdateListener</code> listener.
	 * 
	 * @param listener the SettingDefinitionUpdateListener
	 */
	public void registerSettingDefinitionUpdateListener(final SettingDefinitionUpdateListener listener) {
		if (!this.listeners.contains(listener)) {
			this.listeners.add(listener);
		}
	}
	
	/**
	 * Registers a <code>SettingValueUpdateListener</code> listener.
	 * 
	 * @param listener the SettingValueUpdateListener
	 */
	public void registerSettingValueUpdateListener(final SettingValueUpdateListener listener) {
		if (!this.valueListeners.contains(listener)) {
			this.valueListeners.add(listener);
		}
	}

	/**
	 * Unregisters a <code>SettingDefinitionUpdateListener</code> listener.
	 *
	 * @param listener the SettingDefinitionUpdateListener
	 */
	public void unregisterSettingValueUpdateListener(final SettingDefinitionUpdateListener listener) {
		if (this.listeners.contains(listener)) {
			this.listeners.remove(listener);
		}
	}

	/**
	 * Unregisters a <code>SettingDefinitionUpdateListener</code> listener.
	 *
	 * @param listener the SettingDefinitionUpdateListener
	 */
	public void unregisterSettingValueUpdateListener(final SettingValueUpdateListener listener) {
		if (this.valueListeners.contains(listener)) {
			this.valueListeners.remove(listener);
		}
	}
	
	/**
	 * Delete a setting value.
	 * @param definition setting definition to which the setting value belongs
	 * @param settingValue to delete
	 */
	public void deleteSettingValue(final SettingDefinition definition, final SettingValue settingValue) {
		settingsService.deleteSettingValue(settingValue);
		fireSettingValueUpdated(settingValue);
	}


	/**
	 * Update a setting definition.
	 * @param settingDefinition to update
	 */
	public void updateSettingDefinition(final SettingDefinition settingDefinition) {
		SettingDefinition updatedDefinition = settingsService.updateSettingDefinition(settingDefinition);
		currentDefinitions.remove(settingDefinition);
		currentDefinitions.add(updatedDefinition);

		fireDefinitionChanged(updatedDefinition);
	}
}
