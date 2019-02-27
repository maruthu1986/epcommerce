/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.customer;

import io.reactivex.Completable;
import io.reactivex.Single;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerAddress;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.rest.command.ExecutionResult;

/**
 * Loads objects from core.
 */
public interface CustomerRepository {

	/**
	 * Create a new Customer Domain Object. This method does not perform any persistence.
	 * @return Customer.
	 */
	Customer createNewCustomerEntity();

	/**
	 * Finds customer by username and store code.
	 * Do not use this unless you don't have the user's GUID.
	 *
	 * @param storeCode store code.
	 * @param userId    user Id.
	 * @return Customer.
	 */
	ExecutionResult<Customer> findCustomerByUserId(String storeCode, String userId);

	/**
	 * Finds customer by user guid and store code.
	 *
	 * @param guid user guid.
	 * @return Customer.
	 */
	ExecutionResult<Customer> findCustomerByGuid(String guid);

	/**
	 * Gets the customer.
	 *
	 * @param guid customer guid.
	 * @return the Single customer
	 */
	Single<Customer> getCustomer(String guid);

	/**
	 * Updates the customer.
	 *
	 * @param customer the customer.
	 * @return A result based on whether the update was successful.
	 * @deprecated use {@link CustomerRepository#updateCustomerAsCompletable(Customer)}
	 */
	@Deprecated
	ExecutionResult<Void> updateCustomer(Customer customer);

	/**
	 * Updates the customer.
	 *
	 * @param customer the customer.
	 * @return Completable with the deferred execution of the update operation.
	 */
	Completable updateCustomerAsCompletable(Customer customer);

	/**
	 * Merge anonymous to registered customer.
	 *
	 * @param customerSession    the customer session
	 * @param recipientCustomer  the recipient customer guid
	 * @param validatedStoreCode the validated store code
	 * @return an execution result based on whether the merge was successful
	 */
	ExecutionResult<Object> mergeCustomer(CustomerSession customerSession, Customer recipientCustomer,
			String validatedStoreCode);

	/**
	 * Adds the customer address.
	 *
	 * @param customer the customer
	 * @param address  the new address
	 * @return the customer
	 */
	Single<Customer> addAddress(Customer customer, CustomerAddress address);

	/**
	 * Updates the customer address.
	 *
	 * @param customer the customer
	 * @param address  the new address
	 * @return the customer
	 */
	Completable updateAddress(Customer customer, CustomerAddress address);

	/**
	 * Update the customer.
	 *
	 * @param updatedCustomer the updated customer
	 * @return the updated customer
	 */
	Single<Customer> update(Customer updatedCustomer);

	/**
	 * Authenticate an Anonymous User.
	 *
	 * @param customer the customer
	 * @return a customer
	 */
	ExecutionResult<Customer> addUnauthenticatedUser(Customer customer);

	/**
	 * Customer is first time buyer.
	 *
	 * @param customer the customer
	 * @return customer is first time buyer
	 */
	boolean isFirstTimeBuyer(Customer customer);
}
