/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.catalog.wizards.product.create;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.custom.CCombo;

import com.elasticpath.common.dto.pricing.PriceListAssignmentsDTO;
import com.elasticpath.commons.util.Pair;

/**
 * Price list combo model.
 */
public class ComboModel {
	private final List<ComboElement> elements = new ArrayList<>();

	/**
	 * Sets the unique plas.
	 * @param uniquePlas the list of plas
	 */
	public void setPlas(final List<PriceListAssignmentsDTO> uniquePlas) {
		for (PriceListAssignmentsDTO plaDto : uniquePlas) {
			elements.add(new ComboElement(plaDto, Boolean.FALSE));
		}			
	}

	/**
	 *Marks the combo item as dirty that corresponds to the model being edited. 
	 * @param guid the model's guid
	 */
	public void setDirty(final String guid) {
		for (int i = 0; i < elements.size(); i++) {
			ComboElement element = elements.get(i);
			if (element.getPlaDto().getPriceListGuid().equals(guid)) {
				elements.set(i, new ComboElement(element.getPlaDto(), Boolean.TRUE));
			}
		}
	}

	/**
	 * 
	 * @param index index
	 * @return the PLA dto that corresponds to the index
	 */
	public PriceListAssignmentsDTO get(final int index) {
		return elements.get(index).getPlaDto();
	}

	/**
	 * 
	 * @param plGuid price list guid
	 * @return the PLA dto that corresponds to the index
	 */
	public int get(final String plGuid) {
		int counter = 0;
		for (ComboElement comboElement : elements) {
			if (comboElement.getPlaDto().getPriceListGuid().equals(plGuid)) {
				return counter;
			}
			counter++;
		}
		return -1;
	}
	
	/**
	 * Populates the combo.
	 * @param combo combo
	 */
	public void populate(final CCombo combo) {
		for (ComboElement comboElement : elements) {
			combo.add(comboElement.buildPriceListComboItem());
		}
	}

	/**
	 * Represents the combo element. 
	 */
	private class ComboElement extends Pair<PriceListAssignmentsDTO, Boolean> {

		private static final long serialVersionUID = 5262071915979384765L;
		private static final String DIRTY = "* "; //$NON-NLS-1$
		private static final String NOT_DIRTY = "  "; //$NON-NLS-1$

		/**
		 * Constructor.
		 * @param dto the dto
		 * @param dirty true, if dirty
		 */
		ComboElement(final PriceListAssignmentsDTO dto, final Boolean dirty) {
			super(dto, dirty);
		}
		
		/**
		 * @return true, if dirty
		 */
		public boolean isDirty() {
			return getSecond();
		}
		
		/**
		 * @return PLA DTO
		 */
		public PriceListAssignmentsDTO getPlaDto() {
			return getFirst();
		}

		/**
		 * Create the String to represent the item in the combo.
		 * @return the string for the item.
		 */
		public String buildPriceListComboItem() {
			if (isDirty()) {
				return DIRTY + getBaseString(getPlaDto()); 
			}
			return  NOT_DIRTY + getBaseString(getPlaDto());
		}		

		/**
		 * @param dto the PLA DTO
		 * @return the base string item for the  PLA DTO
		 */
		public String getBaseString(final PriceListAssignmentsDTO dto) {
			return dto.getPriceListName() + " (" + dto.getPriceListCurrency().getCurrencyCode() + ")";  //$NON-NLS-1$//$NON-NLS-2$
		}
	}
}