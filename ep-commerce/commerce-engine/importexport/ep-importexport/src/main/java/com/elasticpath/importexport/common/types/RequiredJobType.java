/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.importexport.common.types;

/**
 * Required Job Type determines type of export/import operations. Each Job Type reflects type of complex object to be processed: Product for example.
 * So appropriate classes can be used for execution of the job according to its type.
 */
public enum RequiredJobType {

	/** Required job type for Product. */
	PRODUCT(JobType.PRODUCT),

	/** Required job type for Catalog. */
	CATALOG(JobType.CATALOG),

	/** Required job type for Category. */
	CATEGORY(JobType.CATEGORY),

	/** Required job type for Promotion. */
	PROMOTION(JobType.PROMOTION),

	/** Required job type for Price List Descriptor. */
	PRICELISTDESCRIPTOR(JobType.PRICELISTDESCRIPTOR),

	/** Required job type for SystemConfiguration. */
	SYSTEMCONFIGURATION(JobType.SYSTEMCONFIGURATION),
	
	/** Required job type for Content Space. */
	CONTENTSPACE(JobType.CONTENTSPACE),

	/** Required job type for Dynamic Content. */
	DYNAMICCONTENT(JobType.DYNAMICCONTENT),

	/** Required job for Dynamic Content Delivery. */
	DYNAMICCONTENTDELIVERY(JobType.DYNAMICCONTENTDELIVERY),

	/** Required job for Price List Assignments. */
	PRICELISTASSIGNMENT(JobType.PRICELISTASSIGNMENT),

	/** Payment Gateways. */
	PAYMENTGATEWAY(JobType.PAYMENTGATEWAY),

	/** Taxes. */
	TAXCODE(JobType.TAXCODE), TAXJURISDICTION(JobType.TAXJURISDICTION),

	/** Warehouses. */
	WAREHOUSE(JobType.WAREHOUSE),

	/** Stores. */
	STORE(JobType.STORE),

	/** Stores associations. */
	STORE_ASSOCIATION(JobType.STORE_ASSOCIATION),
	
	/** Customers. */
	CUSTOMER(JobType.CUSTOMER),
	
	/** Customer Groups. */
	CUSTOMER_GROUP(JobType.CUSTOMER_GROUP),

	/** Commerce Manager Users. */
	CMUSER(JobType.CMUSER),

	/** User Roles. */
	USER_ROLE(JobType.USER_ROLE),
	
	/** Customer Profile Attributes. */
	CUSTOMERPROFILE_ATTRIBUTE(JobType.CUSTOMERPROFILE_ATTRIBUTE),

	/** Coupon set. */
	COUPONSET(JobType.COUPONSET),

	/** Gift Certificates. */
	GIFT_CERTIFICATE(JobType.GIFT_CERTIFICATE),

	/** Shipping Service Level. */
	SHIPPING_SERVICE_LEVEL(JobType.SHIPPING_SERVICE_LEVEL),
	
	/** Shipping Regions. */
	SHIPPING_REGION(JobType.SHIPPING_REGION),
	
	/** Saved Condition. */
	SAVED_CONDITION(JobType.SAVED_CONDITION),

	/** ImportJob. */
	CM_IMPORT_JOB(JobType.CM_IMPORT_JOB),

	/** Data Policy. */
	DATA_POLICY(JobType.DATA_POLICY),

	/** Customer Consents. */
	CUSTOMER_CONSENT(JobType.CUSTOMER_CONSENT);

	private JobType jobType;

	/**
	 * Private Constructor.
	 *
	 * @param jobType job type
	 */
	RequiredJobType(final JobType jobType) {
		this.jobType = jobType;
	}

	/**
	 * Gets job type.
	 * 
	 * @return the job type
	 */
	public JobType getJobType() {
		return jobType;
	}
}
