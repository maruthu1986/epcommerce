/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.promotions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.SWT;

import com.elasticpath.common.dto.CouponModelDto;
import com.elasticpath.common.dto.CouponUsageModelDto;
import com.elasticpath.domain.rules.CouponConfig;
import com.elasticpath.domain.rules.CouponUsageType;
import com.elasticpath.service.rules.DuplicateCouponException;

/**
 * Represents the model for the CouponEditorDialog. This includes representing just coupons
 * or coupon codes and email addresses ready to create coupons and couponUsages.
 */
public class CouponCollectionModel {
	
	private final List<CouponModelDto> couponUsages = new ArrayList<CouponModelDto>();
	
	private final Set<CouponModelDto> addSet = new HashSet<CouponModelDto>();
	
	private final Set<CouponModelDto> deleteSet = new HashSet<CouponModelDto>();
	
	private CouponUsageType usageType;

	private int sortDirection;

	private Comparator<CouponModelDto> comparator;

	private CouponConfig couponConfig;
	
	private CouponValidator couponValidator;
	
	/**
	 * Create a new model with the given usage type.
	 * 
	 * @param usageType the coupon usage type
	 */
	public CouponCollectionModel(final CouponUsageType usageType) {
		super();
		this.usageType = usageType;
	}

	/**
	 * Create a new model.
	 */
	public CouponCollectionModel() {
		super();
	}
	
	/**
	 * Adds a couponCode to the model.
	 * @param couponCode the code to add.
	 */
	public void add(final String couponCode) {
		this.add(couponCode, null);
	}
	
	/**
	 * Adds a coupon model dto to the collection.
	 * 
	 * @param couponModelDto the coupon model dto.
	 */
	public void add(final CouponModelDto couponModelDto) {
		if (couponUsages.contains(couponModelDto)) {
			throw new DuplicateCouponException(null, couponModelDto.getCouponCode());
		}

		couponUsages.add(couponModelDto);
		addSet.add(couponModelDto);

		internalSort();
	}
	
	/**
	 * Adds a CouponUsage and related Coupon to the model.
	 * 
	 * @param couponCode The coupon code.
	 * @param emailAddress The email address.
	 */
	public void add(final String couponCode, final String emailAddress) {
		CouponModelDto couponUsage = createCouponModelDto(couponCode, emailAddress);

		add(couponUsage);
	}

	private CouponModelDto createCouponModelDto(final String couponCode, final String emailAddress) {
		CouponModelDto couponUsage = null;
		if (emailAddress == null) {
			couponUsage = new CouponModelDto(0, couponCode, false);
		} else {
			couponUsage = new CouponUsageModelDto(0, couponCode, emailAddress, false);
		}
		return couponUsage;
	}
	
	/**
	 * Deletes the Coupon from the model. Note that only the CouponUsage
	 * for the couponCode will be removed.
	 * 
	 * @param couponCode The coupon code to delete.
	 */
	public void delete(final String couponCode) {
		this.delete(couponCode, null);		
	}

	/**
	 * Deletes the CouponUsage from the model. Note that only the CouponUsage
	 * for the couponCode and emailAddress tuple will be removed.
	 * 
	 * @param couponCode The coupon code to delete.
	 * @param emailAddress The email address to delete.
	 */
	public void delete(final String couponCode, final String emailAddress) {
		CouponModelDto couponUsage = createCouponModelDto(couponCode, emailAddress);
		
		delete(couponUsage);
	}
	
	/**
	 * Deletes the coupon model dto if it exists in the collection.
	 * 
	 * @param couponModelDto the coupon model dto.
	 */
	public void delete(final CouponModelDto couponModelDto) {
		// If the coupon was in the addset then we should neither add it or delete it
		if (!addSet.remove(couponModelDto)) {
			deleteSet.add(couponModelDto);
		}

		couponUsages.remove(couponModelDto);
	}

	/**
	 * @return The current collection of objects.
	 */
	public List<CouponModelDto> getObjects() {
		return Collections.unmodifiableList(couponUsages);
	}

	/**
	 * 
	 * @return The collection of objects to be added to the database.
	 */
	public Collection<CouponModelDto> getObjectsToAdd() {
		return Collections.unmodifiableCollection(addSet);
	}
	
	/**
	 * 
	 * @return The collection of objects to be deleted to the database.
	 */
	public Collection<CouponModelDto> getObjectsToDelete() {
		return Collections.unmodifiableCollection(deleteSet);
	}

	/**
	 * Sorts, and ensures future adds and deletes keep the sort, the model.
	 * @param sortDirection The direction: SWT.UP or SWT.DOWN.
	 * @param comparator The comparator to use for the sort.
	 */
	public void sort(final int sortDirection, final Comparator<CouponModelDto> comparator) {
		this.sortDirection = sortDirection;
		this.comparator = comparator;
		
		internalSort();	
	}
	
	private void internalSort() {
		if (comparator != null) {
			Comparator<CouponModelDto> comparator = this.comparator;
			if (sortDirection == SWT.DOWN) {
				comparator = Collections.reverseOrder(comparator);
			}
			Collections.sort(couponUsages, comparator);
		}
	}

	/**
	 * Copy the given model.
	 * 
	 * @param modelToCopy the model to copy
	 */
	public void copyFrom(final CouponCollectionModel modelToCopy) {
		couponUsages.clear();
		addSet.clear();
		deleteSet.clear();
		couponUsages.addAll(modelToCopy.getObjects());
		addSet.addAll(modelToCopy.getObjectsToAdd());
		deleteSet.addAll(modelToCopy.getObjectsToDelete());
		sortDirection = modelToCopy.getSortDirection();
		comparator = modelToCopy.getComparator();
	}

	/**
	 * 
	 * @param couponConfig The coupon config to set
	 */
	public void setCouponConfig(final CouponConfig couponConfig) {
		this.couponConfig = couponConfig;
	}
	
	/**
	 * 
	 * @return the coupon config
	 */
	public CouponConfig getCouponConfig() {
		return couponConfig;
	}
	
	/**
	 * Get the usage type associated with this model.
	 * 
	 * @return the usageType
	 */
	public CouponUsageType getUsageType() {
		return usageType;
	}
	
	/**
	 * Returns true if the model contains a coupon with {@code coupon code}. 
	 * @param couponCode The coupon code
	 * @return True if it exists.
	 */
	public boolean isCouponCodeExist(final String couponCode) {
		for (CouponModelDto couponUsageModel : couponUsages) {
			if (couponUsageModel.getCouponCode().equals(couponCode)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Get the sort direction.
	 * 
	 * @return the sort direction
	 */
	public int getSortDirection() {
		return sortDirection;
	}
	
	/**
	 * Get the comparator.
	 * 
	 * @return the comparator
	 */
	public Comparator<CouponModelDto> getComparator() {
		return comparator;
	}

	/**
	 * Returns true if {@code couponCode} and {@code emailAddress} exist as a coupon usage
	 * in the model.
	 *  
	 * @param couponCode The coupon code.
	 * @param emailAddress The email address.
	 * @return True iff the combination exists.
	 */
	public boolean isCouponCodeAndEmailAddressExist(final String couponCode,
			final String emailAddress) {
		CouponModelDto couponUsage = createCouponModelDto(couponCode, emailAddress);
		if (couponUsages.contains(couponUsage)) {
			return true;
		}
		
		return false;
	}
	
	
	/**
	 *
	 * @return the couponValidator
	 */
	public CouponValidator getCouponValidator() {
		return couponValidator;
	}

	/**
	 *
	 * @param couponValidator the couponValidator to set
	 */
	public void setCouponValidator(final CouponValidator couponValidator) {
		this.couponValidator = couponValidator;
	}
}

