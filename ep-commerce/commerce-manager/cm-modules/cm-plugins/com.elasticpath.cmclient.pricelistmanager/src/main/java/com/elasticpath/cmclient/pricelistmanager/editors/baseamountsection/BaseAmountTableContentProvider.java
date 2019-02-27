/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.pricelistmanager.editors.baseamountsection;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.elasticpath.cmclient.pricelistmanager.controller.PriceListEditorController;
import com.elasticpath.common.dto.pricing.BaseAmountDTO;

/**
 * Base Amount Table Content Provider class.
 */
@SuppressWarnings({ "PMD.GodClass" })
public class BaseAmountTableContentProvider implements IStructuredContentProvider {
	
	private int updown = 1;
	
	private final PriceListEditorController controller;
	
	private final BaseAmountSection baseAmountSection;
	
	/**
	 * Constructor .
	 * @param controller to get access to edited state and change set.
	 * @param baseAmountSection <code>BaseAmountSection</code> class.
	 */
	public BaseAmountTableContentProvider(
			final PriceListEditorController controller,
			final BaseAmountSection baseAmountSection) {
		this.controller = controller;
		this.baseAmountSection = baseAmountSection;
	}
	
	private final Comparator<BaseAmountDTO> changesComparator = new Comparator<BaseAmountDTO>()   {

		public int compare(final BaseAmountDTO dto, final BaseAmountDTO other) {
			Integer thisVal = getDtoState(dto);
			Integer anotherVal = getDtoState(other);
			return thisVal.compareTo(anotherVal) * updown;			
		}

		private int getDtoState(final BaseAmountDTO dto) {
			if (controller.isNewlyAdded(dto)) {
				return -1;				
			}
			if (controller.isEdited(dto)) {
				return 0;				
			}			
			if (controller.isDeleted(dto)) {
				return 1;				
			}
			return 2;
		}
	};

	private final Comparator<BaseAmountDTO> lockedComparator = new Comparator<BaseAmountDTO>()   {
		public int compare(final BaseAmountDTO dto, final BaseAmountDTO other) {
			Boolean dtoLocked = baseAmountSection.isObjectLocked(dto); 
			Boolean otherLocked = baseAmountSection.isObjectLocked(other);
			return otherLocked.compareTo(dtoLocked) * updown;
		}

	};


	private final Comparator<BaseAmountDTO> objectGuidComparator = (dto, other) -> dto.getObjectGuid().compareTo(other
			.getObjectGuid()) * updown;

	private final Comparator<BaseAmountDTO> objectTypeComparator = new Comparator<BaseAmountDTO>()   {

		private static final String ONE  = "1"; //$NON-NLS-1$
		private static final String ZERO = "0"; //$NON-NLS-1$
		private static final String PRODUCT = "PRODUCT";  //$NON-NLS-1$

		public int compare(final BaseAmountDTO dto, final BaseAmountDTO other) {
			final String dtoType = getDtoTypeString(dto);
			final String otherType = getDtoTypeString(other);
			return dtoType.compareTo(otherType) * updown;			
		}
		
		private String getDtoTypeString(final BaseAmountDTO dto) {
			StringBuilder type = new StringBuilder();
			if (dto.getObjectType().equalsIgnoreCase(PRODUCT)) {
				type.append(ZERO);				
			} else {
				type.append(ONE);				
			}

			if (dto.isMultiSku()) {
				type.append(ONE);								
			} else {
				type.append(ZERO);
								
			}
			return type.toString(); 
		}

	};

	private final Comparator<BaseAmountDTO> quantityComparator = (dto, other) -> dto.getQuantity().compareTo(other
			.getQuantity()) * updown;

	private final Comparator<BaseAmountDTO> listValueComparator = (dto, other) -> compareBigDecimals(dto
					.getListValue(),
			other.getListValue());

	private final Comparator<BaseAmountDTO> saleValueComparator = (dto, other) -> compareBigDecimals(dto
					.getSaleValue(),
			other.getSaleValue());


	private  Comparator<BaseAmountDTO> productCodeComparator;

	private final Comparator<BaseAmountDTO> productNameComparator = (dto, other) -> compareStringValues(dto
			.getProductName(), other.getProductName());

	private final Comparator<BaseAmountDTO> skuCodeComparator = (dto, other) -> compareStringValues(dto.getSkuCode(),
			other.getSkuCode());

	private final Comparator<BaseAmountDTO> skuConfigComparator = (dto, other) -> compareStringValues(dto
			.getSkuConfiguration(), other.getSkuConfiguration());

	private Comparator<BaseAmountDTO> defaultComparator = objectGuidComparator;


	/**
	 * Return the BaseAmountDTOs of customer as array.
	 * 
	 * @param element List of DTOs as input
	 * @return the addresses
	 */
	public Object[] getElements(final Object element) {
		PriceListEditorController controller = (PriceListEditorController) element;
		List<BaseAmountDTO> bat = new ArrayList<>(controller.getAllBaseAmounts());
		if (defaultComparator != null) {
			Collections.sort(bat, defaultComparator);
		}
		return bat.toArray();
	}

	

	/**
	 * Get the current comparator.
	 * @return current comparator
	 */
	public Comparator<BaseAmountDTO> getDefaultComparator() {
		return defaultComparator;
	}

	/**
	 * Set the current comparator.
	 * @param defaultComparator current comparator
	 */
	public void setDefaultComparator(final Comparator<BaseAmountDTO> defaultComparator) {
		this.defaultComparator = defaultComparator;
	}

	/**
	 * Get the object guid comparator.
	 * @return comparator.
	 */
	public Comparator<BaseAmountDTO> getObjectGuidComparator() {
		return objectGuidComparator;
	}

	/**
	 * Get the object type comparator. Object type comparator mix object type with multi sku and bundle flags.
	 * @return object type comparator.
	 */
	public Comparator<BaseAmountDTO> getObjectTypeComparator() {
		return objectTypeComparator;
	}

	/**
	 * 
	 * @return quantity comparator.
	 */
	public Comparator<BaseAmountDTO> getQuantityComparator() {
		return quantityComparator;
	}

	/**
	 * 
	 * @return list value comparator
	 */
	public Comparator<BaseAmountDTO> getListValueComparator() {
		return listValueComparator;
	}

	/**
	 * 
	 * @return sale price comparator
	 */
	public Comparator<BaseAmountDTO> getSaleValueComparator() {
		return saleValueComparator;
	}

	/**
	 * 
	 * @return product code comparator
	 */
	public Comparator<BaseAmountDTO> getProductCodeComparator() {
		if (productCodeComparator == null) {
			productCodeComparator = (dto, other) -> compareStringValues(dto.getProductCode(), other.getProductCode());
		}
		return productCodeComparator;
	}
	
	/**
	 * Sets product code comparator. 
	 *
	 * @param productCodeComparator product code comparator.
	 */
	public void setProductCodeComparator(final Comparator<BaseAmountDTO> productCodeComparator) {
		this.productCodeComparator = productCodeComparator;
	}

	/**
	 * 
	 * @return product name comparator
	 */
	public Comparator<BaseAmountDTO> getProductNameComparator() {
		return productNameComparator;
	}

	/**
	 * 
	 * @return sku code comparator
	 */
	public Comparator<BaseAmountDTO> getSkuCodeComparator() {
		return skuCodeComparator;
	}

	/**
	 * 
	 * @return sku configuration comparator
	 */
	public Comparator<BaseAmountDTO> getSkuConfigComparator() {
		return skuConfigComparator;
	}
	
	/**
	 * 
	 * @return locked comparator
	 */
	public Comparator<BaseAmountDTO> getLockedComparator() {
		return lockedComparator;
	}
	

	/**
	 * 
	 * @return changes comparator.
	 */
	public Comparator<BaseAmountDTO> getChangesComparator() {
		return changesComparator;
	}



	/**
	 * Set up/down direction.
	 * @param updown direction to set.
	 */
	public void setUpdown(final int updown) {
		this.updown = updown;
	}

	@Override
	public void dispose() {
		// nothing to dispose
	}

	/**
	 * Not needed unless search results sharing same view.
	 * 
	 * @param viewer the view
	 * @param oldObject the old object
	 * @param newObject the new object
	 */
	@Override
	public void inputChanged(final Viewer viewer, final Object oldObject, final Object newObject) {
		// not needed
	}
	
	/**
	 * Compare two BigDecimals. Null values allowed. 
	 * @param value optional value
	 * @param otherValue other optional value
	 * @return compare result
	 */
	private int compareBigDecimals(final BigDecimal value,
			final BigDecimal otherValue) {
		if (value != null && otherValue == null) {
			return -1 * updown;
		} else if (value == null && otherValue != null) {
			return 1 * updown;
		} else if (value == null && otherValue == null) {
			return 0;				
		}
		return value.compareTo(otherValue) * updown;
	}  

	/**
	 * Compare two strings. Null values allowed. 
	 * @param value optional value
	 * @param otherValue other optional value
	 * @return compare result
	 */	
	private int compareStringValues(final String value, final String otherValue) {
		if (value != null && otherValue == null) {
			return -1 * updown;
		} else if (value == null && otherValue != null) {
			return 1 * updown;
		} else if (value == null && otherValue == null) {
			return 0;				
		}
		return value.compareToIgnoreCase(otherValue) * updown;
	}  
	
	
}