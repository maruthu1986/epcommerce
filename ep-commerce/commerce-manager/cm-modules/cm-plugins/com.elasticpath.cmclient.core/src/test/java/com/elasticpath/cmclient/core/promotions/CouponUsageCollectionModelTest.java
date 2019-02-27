/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.promotions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.junit.Test;

import com.elasticpath.common.dto.CouponModelDto;
import com.elasticpath.common.dto.CouponUsageModelDto;
import com.elasticpath.domain.rules.CouponUsageType;
import com.elasticpath.service.rules.DuplicateCouponException;

/**
 * Unit tests for the {@code CouponUsageCollectionModel}.
 */
@SuppressWarnings({ "PMD.AvoidDuplicateLiterals" })
public class CouponUsageCollectionModelTest {
	
	/**
	 * Tests that the add method results in a coupon usage in the add list.
	 */
	@Test
	public void testAdd() {
		CouponCollectionModel couponUsageCollectionModel = new CouponCollectionModel(CouponUsageType.LIMIT_PER_SPECIFIED_USER);
		
		couponUsageCollectionModel.add("ABC", "test@test.com"); //$NON-NLS-1$ //$NON-NLS-2$
		
		List<CouponModelDto> outputList = couponUsageCollectionModel.getObjects();
		Collection<CouponModelDto> addList = couponUsageCollectionModel.getObjectsToAdd();
						
		assertEquals("The add collection should contain only the coupon usage", 1, addList.size()); //$NON-NLS-1$
		CouponModelDto couponUsage = addList.iterator().next();
		assertTrue(couponUsage instanceof CouponUsageModelDto);
		assertEquals("The email address should be on the coupon usage", //$NON-NLS-1$
				"test@test.com", ((CouponUsageModelDto) couponUsage).getEmailAddress()); //$NON-NLS-1$		
		assertEquals("The coupon should have the code", "ABC", couponUsage.getCouponCode()); //$NON-NLS-1$ //$NON-NLS-2$

		// Note that the object in the output collection does not necessarily have to be the same instance.
		assertTrue("The output collection should have the coupon usage", outputList.contains(couponUsage)); //$NON-NLS-1$
		assertEquals("The output collection should only have the coupon usage we added", 1, outputList.size()); //$NON-NLS-1$
	}
	
	/**
	 * Tests that the add method results in a coupon usage in the add list.
	 */
	@Test
	public void testSortThenAdd() {
		CouponCollectionModel couponUsageCollectionModel = new CouponCollectionModel(CouponUsageType.LIMIT_PER_SPECIFIED_USER);
		Comparator<CouponModelDto> codeComparator = new CouponUsageModelDto.CodeComparator();

		couponUsageCollectionModel.sort(SWT.UP, codeComparator);
		
		couponUsageCollectionModel.add("BBC", "test@test.com"); //$NON-NLS-1$ //$NON-NLS-2$
		couponUsageCollectionModel.add("ABC", "test@test.com"); //$NON-NLS-1$ //$NON-NLS-2$		
		
		List<CouponModelDto> outputList = couponUsageCollectionModel.getObjects();
		Collection<CouponModelDto> addList = couponUsageCollectionModel.getObjectsToAdd();
						
		assertEquals("The add collection should contain only two coupon usages", 2, addList.size()); //$NON-NLS-1$
		CouponUsageModelDto bbcCouponUsage = new CouponUsageModelDto(0, "BBC", "test@test.com", false); //$NON-NLS-1$ //$NON-NLS-2$
		CouponUsageModelDto abcCouponUsage = new CouponUsageModelDto(0, "ABC", "test@test.com", false);  //$NON-NLS-1$//$NON-NLS-2$
		assertTrue("The output collection should have the BBC coupon usage", addList.contains(bbcCouponUsage)); //$NON-NLS-1$
		assertTrue("The output collection should have the ABC coupon usage", addList.contains(abcCouponUsage)); //$NON-NLS-1$
		
		// Note that the object in the output collection does not necessarily have to be the same instance.
		assertEquals("The output collection should only have the two coupon usages we added", 2, outputList.size()); //$NON-NLS-1$
		
		Iterator<CouponModelDto> outputListIterator = outputList.iterator();
		CouponModelDto couponUsage = outputListIterator.next();	
		assertEquals("The coupon should have the code", "ABC", couponUsage.getCouponCode()); //$NON-NLS-1$ //$NON-NLS-2$
		CouponModelDto couponUsage2 = outputListIterator.next();	
		assertEquals("The coupon should have the code", "BBC", couponUsage2.getCouponCode()); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	/**
	 * Tests that the sort method sorts by email.
	 */
	@Test
	public void testSortByEmailThenAdd() {
		CouponCollectionModel couponUsageCollectionModel = new CouponCollectionModel(CouponUsageType.LIMIT_PER_SPECIFIED_USER);
		Comparator<CouponModelDto> emailComparator = new CouponUsageModelDto.EmailComparator();

		couponUsageCollectionModel.sort(SWT.UP, emailComparator);
		
		couponUsageCollectionModel.add("ABC", "test2@test.com"); //$NON-NLS-1$ //$NON-NLS-2$
		couponUsageCollectionModel.add("ABC", "test1@test.com"); //$NON-NLS-1$ //$NON-NLS-2$		
		
		List<CouponModelDto> outputList = couponUsageCollectionModel.getObjects();
		Collection<CouponModelDto> addList = couponUsageCollectionModel.getObjectsToAdd();
						
		assertEquals("The add collection should contain only two coupon usages", 2, addList.size()); //$NON-NLS-1$
		CouponUsageModelDto test1CouponUsage = new CouponUsageModelDto(0, "ABC", "test1@test.com", false); //$NON-NLS-1$ //$NON-NLS-2$
		CouponUsageModelDto test2CouponUsage = new CouponUsageModelDto(0, "ABC", "test2@test.com", false);  //$NON-NLS-1$//$NON-NLS-2$
		assertTrue("The output collection should have the test1 coupon usage", addList.contains(test1CouponUsage)); //$NON-NLS-1$
		assertTrue("The output collection should have the test2 coupon usage", addList.contains(test2CouponUsage)); //$NON-NLS-1$
		
		// Note that the object in the output collection does not necessarily have to be the same instance.
		assertEquals("The output collection should only have the two coupon usages we added", 2, outputList.size()); //$NON-NLS-1$
		
		Iterator<CouponModelDto> outputListIterator = outputList.iterator();
		CouponModelDto couponUsage = outputListIterator.next();	
		assertEquals("The first coupon should be test1", test1CouponUsage, couponUsage); //$NON-NLS-1$
		CouponModelDto couponUsage2 = outputListIterator.next();	
		assertEquals("The second coupon should be test2", test2CouponUsage, couponUsage2); //$NON-NLS-1$
	}
	
	/**
	 * Verify that a duplicate coupon usage cannot be added to the model.
	 */
	@Test(expected = DuplicateCouponException.class)
	public void testAddDuplicate() {
		CouponCollectionModel couponUsageCollectionModel = new CouponCollectionModel(CouponUsageType.LIMIT_PER_SPECIFIED_USER);		
				
		couponUsageCollectionModel.add("ABC", "test@test.com"); //$NON-NLS-1$ //$NON-NLS-2$
		couponUsageCollectionModel.add("ABC", "test@test.com"); //$NON-NLS-1$ //$NON-NLS-2$
				
		// Expect an exception
	}
	
	/**
	 * Tests that calling delete results in the coupon usage being removed from the model
	 * and added to the delete list.
	 */
	@Test
	public void testAddThenDelete() {
		CouponCollectionModel couponUsageCollectionModel = new CouponCollectionModel(CouponUsageType.LIMIT_PER_SPECIFIED_USER);		
				
		couponUsageCollectionModel.add("ABC", "test@test.com"); //$NON-NLS-1$ //$NON-NLS-2$
		couponUsageCollectionModel.delete("ABC", "test@test.com"); //$NON-NLS-1$ //$NON-NLS-2$
		
		List<CouponModelDto> outputList = couponUsageCollectionModel.getObjects();
		Collection<CouponModelDto> addList = couponUsageCollectionModel.getObjectsToAdd();
		Collection<CouponModelDto> deleteList = couponUsageCollectionModel.getObjectsToDelete();
						
		assertEquals("The add collection should contain nothing", 0, addList.size()); //$NON-NLS-1$
		assertEquals("The output collection should contain nothing", 0, outputList.size()); //$NON-NLS-1$
		assertEquals("The delete collection should contain nothing ", 0, deleteList.size()); //$NON-NLS-1$
	}
	
	/**
	 * Tests that the add method, without an email address, results in a coupon usage in the add list.
	 */
	@Test
	public void testAddNoEmail() {
		CouponCollectionModel couponUsageCollectionModel = new CouponCollectionModel(CouponUsageType.LIMIT_PER_COUPON);		
				
		couponUsageCollectionModel.add("ABC"); //$NON-NLS-1$ 
		
		List<CouponModelDto> outputList = couponUsageCollectionModel.getObjects();
		Collection<CouponModelDto> addList = couponUsageCollectionModel.getObjectsToAdd();
						
		assertEquals("The add collection should contain only the coupon usage", 1, addList.size()); //$NON-NLS-1$
		CouponModelDto couponUsage = addList.iterator().next();
		assertFalse(couponUsage instanceof CouponUsageModelDto);
		assertEquals("The coupon should have the code", "ABC", couponUsage.getCouponCode()); //$NON-NLS-1$ //$NON-NLS-2$

		// Note that the object in the output collection does not necessarily have to be the same instance.
		assertTrue("The output collection should have the coupon usage", outputList.contains(couponUsage)); //$NON-NLS-1$
		assertEquals("The output collection should only have the coupon usage we added", 1, outputList.size()); //$NON-NLS-1$
	}
	
	/**
	 * Verify that a duplicate coupon cannot be added to the model.
	 */
	@Test(expected = DuplicateCouponException.class)
	public void testAddDuplicateNoEmail() {
		CouponCollectionModel couponUsageCollectionModel = new CouponCollectionModel(CouponUsageType.LIMIT_PER_COUPON);		
				
		couponUsageCollectionModel.add("ABC"); //$NON-NLS-1$ 
		couponUsageCollectionModel.add("ABC"); //$NON-NLS-1$ 
				
		// Expect an exception
	}
	
	/**
	 * Tests that calling delete results in the coupon usage being removed from the model
	 * and added to the delete list.
	 */
	@Test
	public void testAddThenDeleteNoEmail() {
		CouponCollectionModel couponUsageCollectionModel = new CouponCollectionModel(CouponUsageType.LIMIT_PER_COUPON);		
				
		couponUsageCollectionModel.add("ABC"); //$NON-NLS-1$ 
		couponUsageCollectionModel.delete("ABC"); //$NON-NLS-1$ 
		
		List<CouponModelDto> outputList = couponUsageCollectionModel.getObjects();
		Collection<CouponModelDto> addList = couponUsageCollectionModel.getObjectsToAdd();
		Collection<CouponModelDto> deleteList = couponUsageCollectionModel.getObjectsToDelete();
						
		assertEquals("The add collection should contain nothing", 0, addList.size()); //$NON-NLS-1$
		assertEquals("The output collection should contain nothing", 0, outputList.size()); //$NON-NLS-1$
		assertEquals("The delete collection should contain nothing ", 0, deleteList.size()); //$NON-NLS-1$
	}
	
	/**
	 * Verifies that when the coupon code does not exist in the model that isCoupnCodeExists returns false.
	 */
	@Test
	public void testIsCouponCodeExistNotExist() {
		CouponCollectionModel couponUsageCollectionModel = new CouponCollectionModel(CouponUsageType.LIMIT_PER_COUPON);
		
		couponUsageCollectionModel.add("abc"); //$NON-NLS-1$
		
		assertFalse("Code should not exist", couponUsageCollectionModel.isCouponCodeExist("XYZ")); 		 //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	/**
	 * Verifies that when the coupon code does exist in the model that isCoupnCodeExists returns true.
	 */
	@Test
	public void testIsCouponCodeExistExists() {
		CouponCollectionModel couponUsageCollectionModel = new CouponCollectionModel(CouponUsageType.LIMIT_PER_COUPON);
		
		couponUsageCollectionModel.add("xyz"); //$NON-NLS-1$
		
		assertFalse("Code should exist", couponUsageCollectionModel.isCouponCodeExist("XYZ")); 		 //$NON-NLS-1$ //$NON-NLS-2$ 		
	}	
	
	/**
	 * Tests that a duplicate coupon code and email address return true.
	 */
	@Test
	public void testIsCouponCodeAndEmailAddressExistDuplicate() {
		CouponCollectionModel couponUsageCollectionModel = new CouponCollectionModel(CouponUsageType.LIMIT_PER_COUPON);		
		
		couponUsageCollectionModel.add("ABC", "test@test.com"); //$NON-NLS-1$ //$NON-NLS-2$
		
		boolean actualResult = couponUsageCollectionModel.isCouponCodeAndEmailAddressExist("ABC", "test@test.com"); //$NON-NLS-1$ //$NON-NLS-2$
		
		assertTrue("Duplicate should be detected", actualResult); //$NON-NLS-1$
	}
	
	/**
	 * Tests that a duplicate coupon code and email address return true.
	 */
	@Test
	public void testIsCouponCodeAndEmailAddressExistDiffEmailAddress() {
		CouponCollectionModel couponUsageCollectionModel = new CouponCollectionModel(CouponUsageType.LIMIT_PER_COUPON);		
		
		couponUsageCollectionModel.add("ABC", "test@test.com"); //$NON-NLS-1$ //$NON-NLS-2$
		
		boolean actualResult = couponUsageCollectionModel.isCouponCodeAndEmailAddressExist("ABC", "diff@test.com"); //$NON-NLS-1$ //$NON-NLS-2$
		
		assertFalse("No duplicate", actualResult); //$NON-NLS-1$
	}
	
	/**
	 * Tests that a duplicate coupon code and email address return true.
	 */
	@Test
	public void testIsCouponCodeAndEmailAddressExistDiffCouponCode() {
		CouponCollectionModel couponUsageCollectionModel = new CouponCollectionModel(CouponUsageType.LIMIT_PER_COUPON);		
		
		couponUsageCollectionModel.add("ABC", "test@test.com"); //$NON-NLS-1$ //$NON-NLS-2$
		
		boolean actualResult = couponUsageCollectionModel.isCouponCodeAndEmailAddressExist("XYZ", "test@test.com"); //$NON-NLS-1$ //$NON-NLS-2$
		
		assertFalse("No duplicate", actualResult); //$NON-NLS-1$
	}
	
	/**
	 * Tests that a duplicate coupon code and email address, where the case is different, returns true.
	 * Note that this actually tests the {@code CouponUsageModel}.
	 */
	@Test
	public void testIsCouponCodeAndEmailAddressExistIgnoreCase() {
		CouponCollectionModel couponUsageCollectionModel = new CouponCollectionModel(CouponUsageType.LIMIT_PER_COUPON);		
		
		couponUsageCollectionModel.add("ABC", "test@test.com"); //$NON-NLS-1$ //$NON-NLS-2$
		
		boolean actualResult = couponUsageCollectionModel.isCouponCodeAndEmailAddressExist("abc", "TEST@test.com"); //$NON-NLS-1$ //$NON-NLS-2$
		
		assertTrue("Case is the only difference", actualResult); //$NON-NLS-1$
	}
}
