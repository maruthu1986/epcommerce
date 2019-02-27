/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.tax.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.tax.TaxCategory;
import com.elasticpath.domain.tax.TaxCategoryTypeEnum;
import com.elasticpath.domain.tax.TaxJurisdiction;
import com.elasticpath.domain.tax.TaxRegion;
import com.elasticpath.plugin.tax.domain.TaxAddress;
import com.elasticpath.service.impl.AbstractEpPersistenceServiceImpl;
import com.elasticpath.service.tax.TaxJurisdictionExistException;
import com.elasticpath.service.tax.TaxJurisdictionService;

/**
 * The default implementation of <code>TaxJurisdictionService</code>.
 */
@SuppressWarnings({ "PMD.CyclomaticComplexity", "PMD.GodClass" })
public class TaxJurisdictionServiceImpl extends AbstractEpPersistenceServiceImpl implements TaxJurisdictionService {

	@Override
	public List<String> getCountryCodesInUse() {
		sanityCheck();

		return getPersistenceEngine().retrieveByNamedQuery("SELECT_COUNTRIES_IN_USE");
	}

	@Override
	public TaxJurisdiction add(final TaxJurisdiction taxJurisdiction) throws TaxJurisdictionExistException {
		sanityCheck();

		if (regionCodeExists(taxJurisdiction)) {
			throw new TaxJurisdictionExistException("TaxJurisdiction with the same region code already exists with the same parent");
		}

		getPersistenceEngine().save(taxJurisdiction);
		clearTaxJurisdictionCache(taxJurisdiction);
		return taxJurisdiction;
	}

	/**
	 * Ensure when tax jurisdictions are changed that any cached versions are cleared out.
	 *
	 * @param taxJurisdiction the tax jurisdiction to clear from the cache.
	 */
	protected void clearTaxJurisdictionCache(final TaxJurisdiction taxJurisdiction) {
		if (getPersistenceEngine().isCacheEnabled()) {
			getPersistenceEngine().evictObjectFromCache(taxJurisdiction);
			for (TaxCategory category : taxJurisdiction.getTaxCategorySet()) {
				getPersistenceEngine().evictObjectFromCache(category);
			}
		}
	}

	@Override
	public TaxJurisdiction update(final TaxJurisdiction taxJurisdiction) throws TaxJurisdictionExistException {
		sanityCheck();

		if (regionCodeExists(taxJurisdiction)) {
			throw new TaxJurisdictionExistException("TaxJurisdiction with the same region code already exists with the same parent");
		}
		TaxJurisdiction updatedTaxJurisdiction = getPersistenceEngine().merge(taxJurisdiction);
		clearTaxJurisdictionCache(updatedTaxJurisdiction);
		return updatedTaxJurisdiction;
	}

	private boolean regionCodeExists(final TaxJurisdiction taxJurisdiction) throws EpServiceException {
		if (taxJurisdiction.getRegionCode() == null) {
			throw new EpServiceException("Region code not set.");
		}

		List<TaxJurisdiction> results = getPersistenceEngine().retrieveByNamedQuery("TAXJURISDICTION_SELECT_BY_COUNTRY_CODE",
				taxJurisdiction.getRegionCode());

		boolean regionExists = false;
		if (results.size() > 1) {
			throw new EpServiceException("Inconsistent date: multiple taxJurisdiction with region \"" + taxJurisdiction.getRegionCode()
					+ "\" exists.");
		} else if (results.size() == 1 && taxJurisdiction.getUidPk() != results.get(0).getUidPk()) {
			regionExists = true;
		}
		return regionExists;
	}

	@Override
	public void remove(final TaxJurisdiction taxJurisdiction) throws EpServiceException {
		sanityCheck();
		getPersistenceEngine().delete(taxJurisdiction);
	}

	@Override
	public List<TaxJurisdiction> list() throws EpServiceException {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("TAXJURISDICTION_SELECT_ALL");
	}

	@Override
	public TaxJurisdiction load(final long taxJurisdictionUid) throws EpServiceException {
		sanityCheck();
		TaxJurisdiction taxJurisdiction = null;
		if (taxJurisdictionUid <= 0) {
			taxJurisdiction = getBean(ContextIdNames.TAX_JURISDICTION);
		} else {
			taxJurisdiction = getPersistentBeanFinder().load(ContextIdNames.TAX_JURISDICTION, taxJurisdictionUid);
		}
		return taxJurisdiction;
	}

	@Override
	public TaxJurisdiction get(final long taxJurisdictionUid) throws EpServiceException {
		sanityCheck();
		TaxJurisdiction taxJurisdiction = null;
		if (taxJurisdictionUid <= 0) {
			taxJurisdiction = getBean(ContextIdNames.TAX_JURISDICTION);
		} else {
			taxJurisdiction = getPersistentBeanFinder().get(ContextIdNames.TAX_JURISDICTION, taxJurisdictionUid);
		}

		return taxJurisdiction;
	}

	@Override
	public Object getObject(final long uid) throws EpServiceException {
		return get(uid);
	}

	@Override
	public Collection<Long> getTaxJurisdictionsInUse() throws EpServiceException {
		sanityCheck();
		Set<Long> taxJurisdictionsInUse = new HashSet<>();
		List<Long> queryResponse = getPersistenceEngine().retrieveByNamedQuery("TAX_JURISDICTION_UIDS_WITH_STORE");
		taxJurisdictionsInUse.addAll(queryResponse);
		return taxJurisdictionsInUse;
	}

	@Override
	public TaxJurisdiction retrieveEnabledInStoreTaxJurisdiction(final String storeCode, final TaxAddress address) throws EpServiceException {
		sanityCheck();

		if (StringUtils.isBlank(storeCode) || address == null) {
			throw new EpServiceException("Tax calculation address or store code are not set.");
		}

		// Make sure the match starts from the right country since the region codes are not unique.
		// For example, how to distinguish "CA" (Canada) from "CA" (California).
		final String taxJurisdictionToFind = address.getCountry();

		final List<TaxJurisdiction> taxJurisdictions = getPersistenceEngine().retrieveByNamedQuery("TAX_JURISDICTIONS_FROM_STORE_BY_COUNTRY_CODE",
				storeCode, taxJurisdictionToFind);

		TaxJurisdiction foundTaxJurisdiction = null;

		if (taxJurisdictions.size() > 1) {
			throw new EpServiceException("Invalid taxJurisdiction configuration, cannot find tax jurisdiction: " + taxJurisdictionToFind);
		} else if (taxJurisdictions.size() == 1) {
			foundTaxJurisdiction = filterTaxJurisdictonByRegion(address, taxJurisdictions.get(0));
		}

		return foundTaxJurisdiction;
	}

	/**
	 * Given a Tax Jurisdiction, return a new instance that only contains
	 * tax categories that apply to the region that the given address belongs to.
	 *
	 * @param address the address whose region to filter by
	 * @param taxJurisdiction the persisted tax jurisdiction
	 * @return a filtered <code>TaxJurisdiction</code>
	 */
	protected TaxJurisdiction filterTaxJurisdictonByRegion(final TaxAddress address, final TaxJurisdiction taxJurisdiction) {

		final TaxJurisdiction foundTaxJurisdiction = getBean(ContextIdNames.TAX_JURISDICTION);
		foundTaxJurisdiction.setUidPk(taxJurisdiction.getUidPk());
		foundTaxJurisdiction.setRegionCode(taxJurisdiction.getRegionCode());
		foundTaxJurisdiction.setPriceCalculationMethod(taxJurisdiction.getPriceCalculationMethod());

		final Set<TaxCategory> categorySet = taxJurisdiction.getTaxCategorySet();

		// If the tax jurisdiction contains tax categories, filter them for ones that match the address given
		// If not we can skip the filtering since there are no categories to filter

		if (CollectionUtils.isNotEmpty(categorySet)) {
			for (TaxCategoryTypeEnum categoryTypeEnum : TaxCategoryTypeEnum.values()) {
				String region = null;

				switch (categoryTypeEnum) {
					case FIELD_MATCH_COUNTRY:
						region = address.getCountry();
						break;
					case FIELD_MATCH_SUBCOUNTRY:
						region = address.getSubCountry();
						break;
					case FIELD_MATCH_CITY:
						region = address.getCity();
						break;
					case FIELD_MATCH_ZIP_POSTAL_CODE:
						region = address.getZipOrPostalCode();
						break;
					default:
						break;
				}

				if (StringUtils.isNotEmpty(region)) {
					addTaxCategoriesToTaxJurisdiction(foundTaxJurisdiction, categorySet, categoryTypeEnum, region);
				}
			}
		}

		// Ensure the JPA cache is not confused by the new TaxJurisdiction object
		clearTaxJurisdictionCache(foundTaxJurisdiction);

		return foundTaxJurisdiction;
	}

	/**
	 * Add any tax categories of the appropriate type and region to the jurisdiction.
	 *
	 * @param foundTaxJurisdiction the jurisdiction to add categories to
	 * @param categorySet the set of tax categories to inspect
	 * @param categoryTypeEnum the type of category to match
	 * @param region the region to match
	 */
	protected void addTaxCategoriesToTaxJurisdiction(final TaxJurisdiction foundTaxJurisdiction, final Set<TaxCategory> categorySet,
													 final TaxCategoryTypeEnum categoryTypeEnum, final String region) {
		for (TaxCategory taxCategory : categorySet) {
			final TaxRegion foundTaxRegion = taxCategory.getTaxRegion(region);
			if (taxCategory.getFieldMatchType() == categoryTypeEnum && foundTaxRegion != null) {
				final TaxCategory foundTaxCategory = getBean(ContextIdNames.TAX_CATEGORY);

				foundTaxCategory.setName(taxCategory.getName());
				foundTaxCategory.setLocalizedProperties(taxCategory.getLocalizedProperties());
				foundTaxCategory.setFieldMatchType(taxCategory.getFieldMatchType());

				foundTaxCategory.addTaxRegion(foundTaxRegion);
				foundTaxJurisdiction.getTaxCategorySet().add(foundTaxCategory);
			}
		}
	}

	@Override
	public List<TaxJurisdiction> findByUids(final Collection<Long> taxJurisdictionUids) {
		sanityCheck();
		if (taxJurisdictionUids == null || taxJurisdictionUids.isEmpty()) {
			return new ArrayList<>();
		}
		return getPersistenceEngine().<TaxJurisdiction, Long>retrieveByNamedQueryWithList("TAX_JURISDICTION_BY_UIDS", "list", taxJurisdictionUids);
	}

	@Override
	public List<TaxJurisdiction> findByGuids(final Collection<String> taxJurisdictionUids) {
		sanityCheck();
		if (taxJurisdictionUids == null || taxJurisdictionUids.isEmpty()) {
			return new ArrayList<>();
		}
		return getPersistenceEngine().<TaxJurisdiction, String>retrieveByNamedQueryWithList("TAX_JURISDICTION_BY_GUIDS", "list", taxJurisdictionUids);
	}

	@Override
	public TaxJurisdiction findByGuid(final String guid) throws EpServiceException {
		final List<TaxJurisdiction> jurisdictions = getPersistenceEngine().retrieveByNamedQuery("TAXJURISDICTION_FIND_BY_GUID", guid);
		if (jurisdictions.isEmpty()) {
			return null;
		}
		if (jurisdictions.size() > 1) {
			throw new EpServiceException("Inconsistent data -- duplicate guid:" + guid);
		}
		return jurisdictions.get(0);
	}

}
