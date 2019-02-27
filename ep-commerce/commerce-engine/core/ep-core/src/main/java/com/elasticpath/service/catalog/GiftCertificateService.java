/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.catalog;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.store.Store;
import com.elasticpath.money.Money;
import com.elasticpath.service.EpPersistenceService;
import com.elasticpath.service.payment.GiftCertificateTransactionService;

/**
 * Provides gift certificate related business services.
 */
public interface GiftCertificateService extends EpPersistenceService {

	/**
	 * Adds the given giftCertificate.
	 * 
	 * @param giftCertificate the Gift Certificate to add
	 * @return the persisted instance of Gift Certificate
	 * @throws EpServiceException - in case of any errors
	 */
	GiftCertificate add(GiftCertificate giftCertificate) throws EpServiceException;

	/**
	 * Load the giftCertificate with the given Uid.
	 * 
	 * @param giftCertificateUid the Gift Certificate Uid
	 * @return the giftCertificate if ID exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	GiftCertificate load(long giftCertificateUid) throws EpServiceException;

	/**
	 * Get the Gift Certificate with the given UID. Return null if no matching record exists.
	 * 
	 * @param giftCertificateUid the Gift Certificate UID.
	 * @return the Gift Certificate if UID exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	GiftCertificate get(long giftCertificateUid) throws EpServiceException;

	/**
	 * Generic get method for all persistable domain models.
	 * 
	 * @param uid the persisted instance uid
	 * @return the persisted instance if exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	@Override
	Object getObject(long uid) throws EpServiceException;

	/**
	 * Save or update the given giftCertificate.
	 * 
	 * @param giftCertificate the Gift Certificate to save or update
	 * @return the merged gift certificate
	 * @throws EpServiceException - in case of any errors
	 */
	GiftCertificate saveOrUpdate(GiftCertificate giftCertificate) throws EpServiceException;

	/**
	 * Load the Gift Certificate with the given UID.
	 * 
	 * @param giftCertificateUid the giftCertificate UID
	 * @return the Gift Certificate if UID exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	GiftCertificate getByUid(long giftCertificateUid) throws EpServiceException;

	/**
	 * Deletes the Gift Certificate.
	 * 
	 * @param giftCertificateUid the Gift Certificate Uid to be removed
	 * @throws EpServiceException - in case of any errors
	 */
	void removeGiftCertificate(long giftCertificateUid) throws EpServiceException;

	/**
	 * List all gift certificates stored in the database.
	 * 
	 * @return a list of gift certificates
	 * @throws EpServiceException - in case of any errors
	 */
	List<GiftCertificate> list() throws EpServiceException;

	/**
	 * Returns a list of <code>GiftCertificate</code> based on the given uids.
	 * 
	 * @param giftCertificateUids a collection of giftCertificate uids
	 * @return a list of <code>GiftCertificate</code>s
	 */
	List<GiftCertificate> findByUids(Collection<Long> giftCertificateUids);

	/**
	 * Returns all gift certificate uids as a list.
	 * 
	 * @return all gift certficate uids as a list
	 */
	List<Long> findAllUids();

	/**
	 * Retrieves list of <code>GiftCertificate</code> uids where the last modified date is later than the specified date.
	 * 
	 * @param date date to compare with the last modified date
	 * @return list of <code>GiftCertificate</code> whose last modified date is later than the specified date
	 */
	List<Long> findUidsByModifiedDate(Date date);

	/**
	 * Retrieves list of <code>GiftCertificate</code> where the last modified date is later than the specified date.
	 * 
	 * @param date date to compare with the last modified date
	 * @return list of <code>GiftCertificate</code> whose last modified date is later than the specified date
	 */
	List<GiftCertificate> findByModifiedDate(Date date);

	/**
	 * Retrieves <code>GiftCertificate</code> given the gift certificate code.
	 * 
	 * @param giftCertificateCode Gift Certificate Code to compare with
	 * @return list of <code>GiftCertificate</code>
	 */
	GiftCertificate findByGiftCertificateCode(String giftCertificateCode);
	
	/**
	 * Retrieves {@link GiftCertificate} given the gift certificate code. If that gift certificate
	 * is not within the given store, returns <code>null</code>.
	 * 
	 * @param giftCertificateCode Gift Certificate Code to compare with
	 * @param store the store to search
	 * @return a {@link GiftCertificate}
	 */
	GiftCertificate findByGiftCertificateCode(String giftCertificateCode, Store store);

	/**
	 * Retrieves <code>GiftCertificate</code> given the gift certificate guid.
	 * 
	 * @param guid Gift Certificate guid to compare with
	 * @return list of <code>GiftCertificate</code>
	 */
	GiftCertificate findByGuid(String guid);
	
	/**
	 * save or merge the given giftCertificate.
	 * 
	 * @param giftCertificate the Gift Certificate to merge
	 * @return giftCertificate
	 * @throws EpServiceException - in case of any errors
	 * @deprecated use {@link #saveOrUpdate(GiftCertificate)} instead
	 */
	@Deprecated
	GiftCertificate saveOrMerge(GiftCertificate giftCertificate) throws EpServiceException;

	/**
	 * return true if gift certificate code exist.
	 * 
	 * @param giftCertificateCode Gift Certificate Code to compare with
	 * @param storeUid the store UIDPK this certificate should be valid for 
	 * @return true if gift certificate code exist
	 */
	boolean isGiftCertificateCodeExist(String giftCertificateCode, long storeUid);
	
	/**
	 * Obtains the balance of the gift certificate.
	 * 
	 * @param giftCertificate the gift certificate
	 * @return the balance amount
	 */
	BigDecimal getBalance(GiftCertificate giftCertificate);
	
	/**
	 * Set the gift certificate transaction service.
	 * 
	 * @param giftCertificateTransactionService the giftCertificateTransactionService to set
	 */
	void setGiftCertificateTransactionService(GiftCertificateTransactionService giftCertificateTransactionService);
	
	/**
	 * Get detail data for gift certificate reporting. 
	 * 
	 * @param storeUid the store UID
	 * @param startDate optional begin of report period
	 * @param endDate optional end  of report period
	 * @param currencies list of currencies 
	 * @return list of reporting rows as array. Array hold following fields
	 *     Date of purchase [Data];
	 *     GC number [String];
	 *     Recipient [String];
	 *     Sender [String];
	 *     Currency [String];
	 *     Original amount [BigDecimal];
	 *     Outstanding amount [BigDecimal]. 
	 */
	List<Object[]> getGiftCertificateDetailData(long storeUid, Date startDate, Date endDate, List<String> currencies);
	
	/**
	 * Get detail data for gift certificate summary report. 
	 * 
	 * @param storeUidPks the stores UIDs
	 * @param startDate optional begin of report period
	 * @param endDate optional end  of report period
	 * @param currencyCode optional currency code
	 * @return list of reporting rows as array with following fields:
	 *    store name;
	 *    currency;
	 *    amount;
	 *    transactions amount
	 */
	List<Object[]> getGiftCertificateSummaryData(
			List<Long> storeUidPks, 
			String currencyCode, 
			Date startDate, 
			Date endDate);	
	
	
	/**
	 * Retrieves list of <code>OrderPayment</code>s associated with the specified gift certificate grouped by their shipments.
	 * 
	 * @param uidPk GiftCertificate uid.
	 * @return map of shipments to their payments.
	 */
	Map<Order, Money> retrieveOrdersBalances(long uidPk);

	/**
	 * Resends the gift certificate.
	 * 
	 * @param emailAddress the recipient email address
	 * @param orderGuid the order guid
	 * @param orderSkuGuid the order sku guid
	 */
	void resendGiftCertificate(String emailAddress, String orderGuid, String orderSkuGuid);

}
