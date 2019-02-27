/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.pricelistmanager.controller;

import java.util.Collection;
import java.util.Locale;

import com.elasticpath.cmclient.core.dto.catalog.PriceListEditorModel;
import com.elasticpath.common.dto.pricing.BaseAmountDTO;
import com.elasticpath.common.dto.pricing.PriceListDescriptorDTO;
import com.elasticpath.common.pricing.service.BaseAmountFilterExt;
import com.elasticpath.domain.catalog.Catalog;

/**
 * Controller class for moderating PriceListEditor parts and providing data management through the internal model.
 * Clients should go through the controller for data related operations whenever possible.
 */
public interface PriceListEditorController {

	/**
	 * Saves or updates the base amount that has been edited.
	 * 
	 * @param oldBaseAmountDTO the old base amount containing original values
	 * @param newBaseAmountDTO the new base amount to be saved
	 */
	void updateBaseAmountDTO(BaseAmountDTO oldBaseAmountDTO, BaseAmountDTO newBaseAmountDTO);
	
	/**
	 * Checks if the price tier exists.
	 * 
	 * @param baseAmountDTO base amount dto
	 * @return true, if price tier exists
	 */
	boolean isPriceTierExists(BaseAmountDTO baseAmountDTO);
	
	/**
	 * Saves the base amount that has been edited.
	 * 
	 * @param newBaseAmountDTO the new base amount to be saved
	 */
	void addBaseAmountDTO(BaseAmountDTO newBaseAmountDTO);
	
	/**
	 * Deletes the base amount that has been selected.
	 * 
	 * @param baseAmountDTO the base amount to be deleted
	 */
	void deleteBaseAmountDTO(BaseAmountDTO baseAmountDTO);

	/**
	 * Checks if user tries to enter a base amount that already exists in either DB or change set kept by the model.
	 * 
	 * 1) User might try to add a brand new base amount (not persistent, not even in the change set)
	 *    Should check if there is a logically equal one (same objectGuid, objectType and quantity) in the DB or in the change set.
	 *    
	 * 2) User might try to edit an existing base amount (persistent, not in the change set)
	 *    Should check if there is a logically equal one in the DB or in the change set
	 *    
	 * 3) User might try to edit a newly added base amount (not persistent, but in the change set) 
	 *    Should check DB and change set. However this base amount doesn't have a GUID. So it should check if this
	 *    one logically equals the old one (the one that was cloned before editing this one)
	 * 
	 * @param oldBaseAmountDTO the base amount to be checked with the new base amount
	 * @param newBaseAmountDTO the base amount to be validated
	 * @return the validation result
	 */
	boolean isUniqueBaseAmountDTO(BaseAmountDTO oldBaseAmountDTO, BaseAmountDTO newBaseAmountDTO);
	
	/**
	 * Reload the model from storage.
	 */
	void reloadModel();
	
	/**
	 * Save any changes to the model.
	 */
	void saveModel();
	
	/**
	 * Retrieve the complete of BaseAmountDTOs managed by this controller.
	 * 
	 * @return list of BaseAmountDTOs
	 */
	Collection<BaseAmountDTO> getAllBaseAmounts();

	/**
	 * Retrieve the PriceListDescriptorDTO managed by this controller.
	 * @return internal PriceListDescriptorDTO
	 */
	PriceListDescriptorDTO getPriceListDescriptor();

	/**
	 * Checks whether the given base amount was added in this session.
	 * 
	 * @param baseAmount the base amount
	 * @return true if added
	 */
	boolean isNewlyAdded(BaseAmountDTO baseAmount);

	/**
	 * Checks if Price List descriptor name is unique.
	 *
	 * @return true if name is unique, false otherwise
	 */
	boolean isPriceListNameUnique();
	
	/**
	 * Checks if base amount has been deleted in current session.
	 *
	 * @param baseAmountDto - the base amount
	 * @return true if deleted, false otherwise
	 */
	boolean isDeleted(BaseAmountDTO baseAmountDto);
	
	/**
	 * Checks if base amount has been updated in current session.
	 *
	 * @param baseAmountDto - the base amount
	 * @return true if edited, false otherwise
	 */
	boolean isEdited(BaseAmountDTO baseAmountDto);
	
	/**
	 * Returns collection of catalogs for a given base amount.
	 *
	 * @param baseAmountDTO - the base amount
	 * @return collection of catalogs for a given base amount
	 */
	Collection<Catalog> getCatalogsFor(BaseAmountDTO baseAmountDTO);	

	/**
	 * Returns collection of all price list descriptors.
	 *
	 * @return collection of all price list descriptors
	 */
	Collection<PriceListDescriptorDTO> getAllPriceListDesctiptorsDTO();

	/**
	 * Sets price list descriptor guid.
	 *
	 * @param plGuid - price list descriptor guid
	 */
	void setPriceListDescriptorGuid(String plGuid);

	/**
	 *  Base amounts filter that will be used to control list of requested base amounts.
	 *
	 * @return - base amounts filter
	 */
	BaseAmountFilterExt getBaseAmountsFilter();

	/**
	 * Base amount second filter, that used for perform filtering on obtained from server side result.
	 *
	 * @return - base amounts filter
	 */	
	BaseAmountFilterExt getBaseAmountsUiFilter();
	
	/**
	 * Returns controller model <code>PriceListEditorModel</code>.
	 *
	 * @return PriceListEditorModel - controller model
	 */
	PriceListEditorModel getModel();


	/**
	 * Sets the pricelist model.
	 * @param model the model
	 */
	void setModel(PriceListEditorModel model);
	
	/**
	 * @return true if model data that this controller references is persisted.
	 */
	boolean isModelPersistent();
	
	/**
	 * Sets current locale.
	 * @param currentLocale current locale
	 */
	void setCurrentLocale(Locale currentLocale);
	
	/**
	 * @return current locale
	 * @return
	 */
	Locale getCurrentLocale();
}