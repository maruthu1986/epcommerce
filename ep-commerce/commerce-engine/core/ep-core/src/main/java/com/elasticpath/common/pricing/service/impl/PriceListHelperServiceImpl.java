/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.common.pricing.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.elasticpath.common.dto.ChangeSetObjects;
import com.elasticpath.common.dto.category.ChangeSetObjectsImpl;
import com.elasticpath.common.dto.pricing.BaseAmountDTO;
import com.elasticpath.common.dto.pricing.PriceListDescriptorDTO;
import com.elasticpath.common.pricing.service.BaseAmountFilter;
import com.elasticpath.common.pricing.service.PriceListHelperService;
import com.elasticpath.common.pricing.service.PriceListLookupService;
import com.elasticpath.common.pricing.service.PriceListService;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.ConstituentItem;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.pricing.PriceListStack;

/**
 * Implementation of {@link PriceListHelperService} interface.
 */
@SuppressWarnings("PMD.GodClass")
public class PriceListHelperServiceImpl implements PriceListHelperService {

	private static final String PRODUCT_TYPE = "PRODUCT"; //$NON-NLS-1$

	private static final String PRODUCT_SKU_TYPE = "SKU"; //$NON-NLS-1$

	private PriceListLookupService priceListLookupService;

	private PriceListService priceListService;

	private BeanFactory beanFactory;

	@Override
	public Map<PriceListDescriptorDTO, List<BaseAmountDTO>> getPriceListMap(final Product product) {
		return getPriceListMap(product, false);
	}

	@Override
	public Map<PriceListDescriptorDTO, List<BaseAmountDTO>> getPriceListMap(final Product product, final boolean masterOnly) {
		return getPriceInfoInternal(product.getCode(), PRODUCT_TYPE, prepareDescriptorsList(product, masterOnly));
	}

	@Override
	public Map<PriceListDescriptorDTO, List<BaseAmountDTO>> getPriceListMap(final Product product,
			final List<PriceListDescriptorDTO> priceListDescriptors) {
		return getPriceInfoInternal(product.getCode(), PRODUCT_TYPE, priceListDescriptors);
	}


	@Override
	public Map<PriceListDescriptorDTO, List<BaseAmountDTO>> getPriceListMap(final ProductSku productSku) {
		final Product product = productSku.getProduct();
		final Map<PriceListDescriptorDTO, List<BaseAmountDTO>> map = new HashMap<>();
		for (PriceListDescriptorDTO priceListDescriptorDTO : findAllDescriptors(product)) {

			List<BaseAmountDTO> list = findBaseAmounts(priceListDescriptorDTO.getGuid(), product.getCode(), PRODUCT_TYPE);
			list.addAll(findBaseAmounts(priceListDescriptorDTO.getGuid(), productSku.getSkuCode(), PRODUCT_SKU_TYPE));

			map.put(priceListDescriptorDTO, list);
		}
		return map;
	}

	@Override
	public Map<PriceListDescriptorDTO, List<BaseAmountDTO>> getPriceListMap(
			final ProductSku productSku,
			final List<PriceListDescriptorDTO> priceListDescriptors) {

		Map<PriceListDescriptorDTO, List<BaseAmountDTO>> productMap =
			getPriceInfoInternal(productSku.getProduct().getCode(), PRODUCT_TYPE, priceListDescriptors);
		Map<PriceListDescriptorDTO, List<BaseAmountDTO>> skuMap =
			getPriceInfoInternal(productSku.getSkuCode(), PRODUCT_SKU_TYPE, priceListDescriptors);

		Map<PriceListDescriptorDTO, List<BaseAmountDTO>> resultMap = new HashMap<>(productMap);

		for (final Map.Entry<PriceListDescriptorDTO, List<BaseAmountDTO>> priceListEntry : skuMap.entrySet()) {
			if (priceListEntry.getValue() == null || priceListEntry.getValue().isEmpty()) {
				continue;
			}
			resultMap.get(priceListEntry.getKey()).addAll(priceListEntry.getValue());
		}
		return resultMap;
	}

	@Override
	public Map<PriceListDescriptorDTO, List<BaseAmountDTO>> getPriceListMap(final BaseAmountFilter filter, final String ... currencyCodes) {
		final ListMultimap<PriceListDescriptorDTO, BaseAmountDTO> descriptorAmountsMap = ArrayListMultimap.create();
		List<BaseAmountDTO> baseAmounts = findBaseAmounts(filter);

		final Map<String, PriceListDescriptorDTO> plDescriptors = getPriceListDescriptorDTOs(baseAmounts);
		removeUnwantedCurrencies(plDescriptors, currencyCodes);

		for (BaseAmountDTO baseAmount : baseAmounts) {
			PriceListDescriptorDTO priceListDescriptorDTO = plDescriptors.get(baseAmount.getPriceListDescriptorGuid());
			//only find the non-hidden price lists
			if (priceListDescriptorDTO != null && !priceListDescriptorDTO.isHidden()) {
				descriptorAmountsMap.put(priceListDescriptorDTO, baseAmount);
			}
		}
		return asSerializableMap(descriptorAmountsMap);
	}

	private void removeUnwantedCurrencies(final Map<String, PriceListDescriptorDTO> plDescriptors, final String ... currencyCodes) {
		if (ArrayUtils.isEmpty(currencyCodes)) {
			return;
		}
		for (Iterator<PriceListDescriptorDTO> iter = plDescriptors.values().iterator(); iter.hasNext();) {
			boolean remove = true;
			String currencyCode =  iter.next().getCurrencyCode();
			for (String currency : currencyCodes) {
				if (StringUtils.equalsIgnoreCase(currency, currencyCode) || StringUtils.isEmpty(currency)) {
					remove = false;
					break;
				}
			}
			if (remove) {
				iter.remove();
			}
		}
	}

	/**
	 * Returns a shallow copy of the given {@link ListMultimap} that is serializable if and only if all keys and values are also serializable.
	 * @param descriptorAmountsMap the {@link ListMultimap} to copy
	 * @param <K> the key type
	 * @param <V> the value type
	 * @return the serializable map
	 */
	static <K, V> Map<K, List<V>> asSerializableMap(final ListMultimap<K, V> descriptorAmountsMap) {
		HashMap<K, List<V>> result = new HashMap<>();
		for (Map.Entry<K, List<V>> entry : Multimaps.asMap(descriptorAmountsMap).entrySet()) {
			result.put(entry.getKey(), new ArrayList<>(entry.getValue()));
		}
		return result;
	}

	/**
	 * Gets the actual Price Information Map for code, type and pre-loaded descriptors.
	 *
	 * @param code the Product or SKU code
	 * @param objectType the PRODUCT, or SKU string.
	 * @param priceListDescriptors the list of PriceListDescriptorDTOs
	 * @return <code>Map</code> between {@link PriceListDescriptorDTO} and {@link BaseAmountDTO}
	 */
	Map<PriceListDescriptorDTO, List<BaseAmountDTO>> getPriceInfoInternal(final String code,
			final String objectType, final List<PriceListDescriptorDTO> priceListDescriptors) {
		Map<PriceListDescriptorDTO, List<BaseAmountDTO>> map = new HashMap<>();
		for (PriceListDescriptorDTO priceListDescriptorDTO : priceListDescriptors) {
			map.put(priceListDescriptorDTO, findBaseAmounts(priceListDescriptorDTO.getGuid(), code, objectType));
		}
		return map;
	}

	/**
	 * Creates a list of <code>PriceListDescriptorDTO</code>s for <code>Product</code>.
	 *
	 * @param product the {@link Product} instance
	 * @param masterOnly if true the only Master Catalog's descriptor will be loaded and shipped with corresponding BaseAmounts, otherwise all.
	 * @return List of <code>PriceListDescriptorDTO</code>
	 */
	List<PriceListDescriptorDTO> prepareDescriptorsList(final Product product, final boolean masterOnly) {
		if (masterOnly) {
			final Catalog masterCatalog = product.getMasterCatalog();
			return findAllDescriptors(masterCatalog, getDefaultCurrencyFor(masterCatalog));
		}

		return findAllDescriptors(product);
	}

	@Override
	public void processBaseAmountChangeSets(final Collection<ChangeSetObjects<BaseAmountDTO>> baseAmountChangeSetCollection) {
		for (ChangeSetObjects<BaseAmountDTO> baseAmountChangeSet : baseAmountChangeSetCollection) {
			priceListService.modifyBaseAmountChangeSet(baseAmountChangeSet);
		}
	}

	/**
	 * Finds {@link PriceListDescriptorDTO}s for {@link Product} using its Catalogs and SupportedCurrencies.
	 *
	 * @param product the {@link Product} instance
	 * @return List of <code>PriceListDescriptorDTO</code>s
	 */
	@Override
	public List<PriceListDescriptorDTO> findAllDescriptors(final Product product) {
		final List<PriceListDescriptorDTO> result = new ArrayList<>();
		for (Catalog catalog : product.getCatalogs()) {
			for (Currency currency : getAllCurrenciesFor(catalog)) {
				result.addAll(findAllDescriptors(catalog, currency));
			}
		}
		// need to remove duplicates that are created in findAllDescriptors(catalog, currency)
		for (int index = 0; index < result.size(); index++) {
			final PriceListDescriptorDTO currentPld = result.get(index);
			for (int subIndex = index + 1; subIndex < result.size(); subIndex++) {
				final PriceListDescriptorDTO checkPld = result.get(subIndex);
				if (currentPld.getGuid().equals(checkPld.getGuid())) {
					result.remove(subIndex);
				}
			}
		}

		return result;
	}

	/**
	 * Finds {@link PriceListDescriptorDTO}s for {@link Catalog}.
	 *
	 * @param catalog the {@link Catalog} instance
	 * @return List of <code>PriceListDescriptorDTO</code>s
	 */
	@Override
	public List<PriceListDescriptorDTO> findAllDescriptors(final Catalog catalog) {
		return priceListService.listByCatalog(catalog);
	}

	@Override
	public void removePricesForProduct(final Product product) {
		priceListService.modifyBaseAmountChangeSet(prepareChangeSetForProduct(product));
	}

	/**
	 * Prepares the ChangeSet List.
	 *
	 * @param product {@link Product} instance.
	 * @return List of ChangeSets
	 */
	ChangeSetObjects<BaseAmountDTO> prepareChangeSetForProduct(final Product product) {
		ChangeSetObjects<BaseAmountDTO> baseAmountChangeSet = new ChangeSetObjectsImpl<>();
		baseAmountChangeSet.getRemovalList().addAll(findProductBaseAmounts(null, product.getCode()));
		if (product.hasMultipleSkus()) {
			for (ProductSku productSku : product.getProductSkus().values()) {
				baseAmountChangeSet.getRemovalList().addAll(findProductSkuBaseAmounts(null, productSku.getSkuCode()));
			}
		}
		return baseAmountChangeSet;
	}

	@Override
	public void removePricesForProductSkus(final Collection<ProductSku> productSkus) {
		ChangeSetObjects<BaseAmountDTO> baseAmountChangeSet = new ChangeSetObjectsImpl<>();
		for (ProductSku productSku : productSkus) {
			baseAmountChangeSet.getRemovalList().addAll(findProductSkuBaseAmounts(null, productSku.getSkuCode()));
		}
		priceListService.modifyBaseAmountChangeSet(baseAmountChangeSet);
	}

	@Override
	public List<PriceListDescriptorDTO> findAllDescriptors(final Catalog catalog, final Currency currency) {
		PriceListStack stack = null;
		if (catalog != null && currency != null) {
			stack = priceListLookupService.getPriceListStack(catalog.getCode(), currency, null);
		}
		if (stack != null)  {
			return createPldDtos(currency, stack.getPriceListStack());
		}
		return Collections.emptyList();
	}

	private List<PriceListDescriptorDTO> createPldDtos(final Currency currency, final List<String> pldGuids) {
		List<PriceListDescriptorDTO> descriptors = new ArrayList<>();
		for (String pldGuid : pldGuids) {
			PriceListDescriptorDTO priceListDescriptorDTO = new PriceListDescriptorDTO();
			priceListDescriptorDTO.setGuid(pldGuid);
			priceListDescriptorDTO.setCurrencyCode(currency.getCurrencyCode());
			descriptors.add(priceListDescriptorDTO);
		}
		return descriptors;
	}

	/**
	 * Finds base amounts by given price list guid and product guid.
	 *
	 * @param priceListGuid price list guid
	 * @param productGuid product guid
	 * @param objectType the object type of the base amount being searched
	 * @return list of base amounts
	 */
	List<BaseAmountDTO> findBaseAmounts(final String priceListGuid, final String productGuid, final String objectType) {
		final BaseAmountFilter baseAmountFilter = beanFactory.getBean(ContextIdNames.BASE_AMOUNT_FILTER);
		baseAmountFilter.setPriceListDescriptorGuid(priceListGuid);
		baseAmountFilter.setObjectGuid(productGuid);
		baseAmountFilter.setObjectType(objectType);
		return findBaseAmounts(baseAmountFilter);
	}

	/**
	 * Finds base amounts by given {@link BaseAmountFilter}.
	 * @param baseAmountFilter the {@link BaseAmountFilter}
	 * @return list of base amounts
	 */
	List<BaseAmountDTO> findBaseAmounts(final BaseAmountFilter baseAmountFilter) {
		return new ArrayList<>(priceListService.getBaseAmounts(baseAmountFilter));
	}

	/**
	 * Get all the {@link PriceListDescriptorDTO}s for the <code>baseAmounts</code>.
	 * @param baseAmounts the baseAmounts to get the PriceListDescriptorDTO's for.
	 * @return map of guid to price list descriptor DTOs.
	 */
	private Map<String, PriceListDescriptorDTO> getPriceListDescriptorDTOs(final List<BaseAmountDTO> baseAmounts) {
		List<String> guids = new ArrayList<>();
		for (BaseAmountDTO dto : baseAmounts) {
			guids.add(dto.getPriceListDescriptorGuid());
		}
		Map<String, PriceListDescriptorDTO> resultMap = new HashMap<>();
		for (PriceListDescriptorDTO dto : priceListService.getPriceListDescriptors(guids)) {
			resultMap.put(dto.getGuid(), dto);
		}
		return resultMap;
	}


	/** Calls {@link #findBaseAmounts(String, String, String)}. */
	private List<BaseAmountDTO> findProductBaseAmounts(final String priceListGuid, final String productGuid) {
		return findBaseAmounts(priceListGuid, productGuid, PRODUCT_TYPE);
	}

	/** Calls {@link #findBaseAmounts(String, String, String)}. */
	private List<BaseAmountDTO> findProductSkuBaseAmounts(final String priceListGuid, final String productGuid) {
		return findBaseAmounts(priceListGuid, productGuid, PRODUCT_SKU_TYPE);
	}

	/**
	 * @param priceListLookupService the priceListLookupService to set
	 */
	public void setPriceListLookupService(final PriceListLookupService priceListLookupService) {
		this.priceListLookupService = priceListLookupService;
	}

	/**
	 * @param priceListService the priceListService to set
	 */
	public void setPriceListService(final PriceListService priceListService) {
		this.priceListService = priceListService;
	}

	/**
	 * Returns all supported currencies for catalog.
	 *
	 * @param catalog - catalog
	 * @return set of supported currencies.
	 */
	@Override
	public Set<Currency> getAllCurrenciesFor(final Catalog catalog) {
		if (null == catalog) {
			return null;
		}
		List<PriceListDescriptorDTO> allPriceListDescriptors = priceListService.listByCatalog(catalog);
		if (CollectionUtils.isEmpty(allPriceListDescriptors)) {
			return Collections.emptySet();
		}
		Set<Currency> allCatalogCurrencies = new LinkedHashSet<>();
		for (PriceListDescriptorDTO priceListAssignment : allPriceListDescriptors) {
			String currencyCode = priceListAssignment.getCurrencyCode();
			Currency currency = Currency.getInstance(currencyCode);
			allCatalogCurrencies.add(currency);
		}
		return allCatalogCurrencies;
	}


	/**
	 * Returns default currency for catalog.
	 * @param catalog - catalog
	 * @return default currency
	 */
	@Override
	public Currency getDefaultCurrencyFor(final Catalog catalog) {
		if (null == catalog) {
			return null;
		}
		Set<Currency> currencies = getAllCurrenciesFor(catalog);
		if (currencies.isEmpty()) {
			return null;
		}
		return currencies.iterator().next();
	}

	@Override
	public Map<PriceListDescriptorDTO, List<BaseAmountDTO>> getPriceListMap(final ConstituentItem constituentItem,
			final List<PriceListDescriptorDTO> priceListDescriptors) {
		if (constituentItem.isProductSku()) {
			return getPriceListMap(constituentItem.getProductSku(), priceListDescriptors);
		}
		return getPriceListMap(constituentItem.getProduct(), priceListDescriptors);
	}

	@Override
	public Map<PriceListDescriptorDTO, List<BaseAmountDTO>> getPriceListMap(final ConstituentItem constituentItem) {
		if (constituentItem.isProductSku()) {
			return getPriceListMap(constituentItem.getProductSku());
		}
		return getPriceListMap(constituentItem.getProduct());
	}

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
}
