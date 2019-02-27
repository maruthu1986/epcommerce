/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.importexport.importer.importers.impl;

import java.util.List;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.adapters.category.CategoryAdapter;
import com.elasticpath.importexport.common.caching.CachingService;
import com.elasticpath.importexport.common.dto.category.CategoryDTO;
import com.elasticpath.importexport.common.dto.category.LinkedCategoryDTO;
import com.elasticpath.importexport.common.exception.runtime.PopulationRollbackException;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.common.util.Message;
import com.elasticpath.importexport.importer.configuration.ImporterConfiguration;
import com.elasticpath.importexport.importer.context.ImportContext;
import com.elasticpath.importexport.importer.importers.CollectionsStrategy;
import com.elasticpath.importexport.importer.importers.SavingStrategy;
import com.elasticpath.importexport.importer.types.CollectionStrategyType;
import com.elasticpath.importexport.importer.types.DependentElementType;
import com.elasticpath.importexport.importer.types.ImportStrategyType;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.catalog.CategoryLookup;
import com.elasticpath.service.catalog.CategoryService;

/**
 * Category importer implementation.
 */
public class CategoryImporterImpl extends AbstractImporterImpl<Category, CategoryDTO> { // NOPMD
	
	private static final Logger LOG = Logger.getLogger(CategoryImporterImpl.class);

	private CategoryAdapter categoryAdapter;

	private DomainAdapter<Category, LinkedCategoryDTO> linkedCategoryAdapter;

	private CategoryLookup categoryLookup;
	private CategoryService categoryService;

	private CachingService cachingService;

	private SavingStrategy<Category, LinkedCategoryDTO> linkedCategorySavingStrategy;

	private final SetMultimap<String, Integer> orderingMap = HashMultimap.create();

	@Override
	public void initialize(final ImportContext context, final SavingStrategy<Category, CategoryDTO> savingStrategy) {
		super.initialize(context, savingStrategy);
		orderingMap.clear();

		final SavingManager<Category> categorySavingManager = new SavingManager<Category>() {

			@Override
			public Category update(final Category persistable) {
				return categoryService.saveOrUpdate(persistable);
			}

			@Override
			public void save(final Category persistable) {
				update(persistable);
			}

		};
		getSavingStrategy().setSavingManager(categorySavingManager);

		linkedCategorySavingStrategy = AbstractSavingStrategy.createStrategy(ImportStrategyType.INSERT_OR_UPDATE, categorySavingManager);
		linkedCategorySavingStrategy.setDomainAdapter(linkedCategoryAdapter);
	}

	@Override
	public boolean executeImport(final CategoryDTO object) { // NOPMD
		sanityCheck();
		orderingCheck(object);

		setImportStatus(object);
		
		final Category obtainedCategory = findPersistentObject(object);
		checkDuplicateGuids(object, obtainedCategory);
		
		final Category category = getSavingStrategy().populateAndSaveObject(obtainedCategory, object);

		if (category != null) {
			linkedCategorySavingStrategy.setLifecycleListener(new DefaultLifecycleListener() {
				@Override
				public void beforeSave(final Persistable persistable) {
					final Category linkedCategory = (Category) persistable;
					setMasterCategory(linkedCategory);
					setParentCategory(linkedCategory);
				}

				protected void setMasterCategory(final Category linkedCategory) {
					linkedCategory.setMasterCategory(category);
				}

				protected void setParentCategory(final Category linkedCategory) {
					if (StringUtils.isEmpty(object.getParentCategoryCode())) {
						return;
					}

					final Category linkedParent = getCachingService().findCategoryByCode(
							object.getParentCategoryCode(), linkedCategory.getCatalog().getCode());
					if (linkedParent != null) {
						linkedCategory.setParent(linkedParent);
					}
				}
			});

			List<LinkedCategoryDTO> linkedCategoryDTOList = object.getLinkedCategoryDTOList();

			for (LinkedCategoryDTO linkedCategoryDTO : linkedCategoryDTOList) {
				importLinkedCategory(object, linkedCategoryDTO);
			}
			return true;
		}
		return false;
	}

	/**
	 * Imports a linked category.
	 *
	 * @param masterCategoryDto the master category's dto
	 * @param linkedCategoryDTO the linked category's dto
	 */
	protected void importLinkedCategory(
			final CategoryDTO masterCategoryDto, final LinkedCategoryDTO linkedCategoryDTO) {
		Catalog catalog = getCachingService().findCatalogByCode(linkedCategoryDTO.getVirtualCatalogCode());
		if (catalog == null) {
			throw new PopulationRollbackException("IE-30701", masterCategoryDto.getCategoryCode(), linkedCategoryDTO.getVirtualCatalogCode());
		}
		if (catalog.isMaster()) {
			throw new PopulationRollbackException("IE-30702", masterCategoryDto.getCategoryCode(), linkedCategoryDTO.getVirtualCatalogCode());
		}
		Category linkedCategory = categoryLookup.findByCategoryAndCatalogCode(
				masterCategoryDto.getCategoryCode(), linkedCategoryDTO.getVirtualCatalogCode());
		if (linkedCategory != null && !StringUtils.equals(linkedCategory.getGuid(), linkedCategoryDTO.getGuid())) {
			throw new PopulationRollbackException("IE-30704", linkedCategoryDTO.getGuid());
		}

		linkedCategorySavingStrategy.populateAndSaveObject(linkedCategory, linkedCategoryDTO);
	}

	private void orderingCheck(final CategoryDTO object) {
		final String parentCategoryCode = object.getParentCategoryCode();
		if (orderingMap.get(parentCategoryCode).contains(object.getOrder())) {
			LOG.warn(new Message("IE-30700", object.getCategoryCode()));
		}
		orderingMap.put(parentCategoryCode, object.getOrder());
	}

	@Override
	protected String getDtoGuid(final CategoryDTO dto) {
		return dto.getCategoryCode();
	}

	@Override
	protected Category findPersistentObject(final CategoryDTO dto) {
		Category category = cachingService.findCategoryByCode(dto.getCategoryCode(), dto.getCatalogCode());
		if (category == null) {
			return null;
		}
		return getCategoryLookup().findByUid(category.getUidPk());
	}
	
	@Override
	protected void setImportStatus(final CategoryDTO object) {
		getStatusHolder().setImportStatus("(" + object.getCategoryCode() + ")");
	}

	@Override
	protected DomainAdapter<Category, CategoryDTO> getDomainAdapter() {
		return categoryAdapter;
	}

	@Override
	public String getImportedObjectName() {
		return CategoryDTO.ROOT_ELEMENT;
	}

	@Override
	protected CollectionsStrategy<Category, CategoryDTO> getCollectionsStrategy() {
		return new CategoryCollectionsStrategy(getContext().getImportConfiguration().getImporterConfiguration(JobType.CATEGORY));
	}

	/**
	 * Gets the categoryAdapter.
	 * 
	 * @return the categoryAdapter
	 */
	public CategoryAdapter getCategoryAdapter() {
		return categoryAdapter;
	}

	/**
	 * Sets the categoryAdapter.
	 * 
	 * @param categoryAdapter the categoryAdapter to set
	 */
	public void setCategoryAdapter(final CategoryAdapter categoryAdapter) {
		this.categoryAdapter = categoryAdapter;
	}

	/**
	 * Gets the linkedCategoryAdapter.
	 * 
	 * @return the linkedCategoryAdapter
	 */
	public DomainAdapter<Category, LinkedCategoryDTO> getLinkedCategoryAdapter() {
		return linkedCategoryAdapter;
	}

	/**
	 * Sets the linkedCategoryAdapter.
	 * 
	 * @param linkedCategoryAdapter the linkedCategoryAdapter to set
	 */
	public void setLinkedCategoryAdapter(final DomainAdapter<Category, LinkedCategoryDTO> linkedCategoryAdapter) {
		this.linkedCategoryAdapter = linkedCategoryAdapter;
	}

	/**
	 * Gets the cachingService.
	 * 
	 * @return the cachingService
	 */
	public CachingService getCachingService() {
		return cachingService;
	}

	/**
	 * Sets the cachingService.
	 * 
	 * @param cachingService the cachingService to set
	 */
	public void setCachingService(final CachingService cachingService) {
		this.cachingService = cachingService;
	}

	/**
	 * Gets the categoryService.
	 * 
	 * @return the categoryService
	 */
	public CategoryService getCategoryService() {
		return categoryService;
	}

	protected CategoryLookup getCategoryLookup() {
		return categoryLookup;
	}

	public void setCategoryLookup(final CategoryLookup categoryLookup) {
		this.categoryLookup = categoryLookup;
	}

	/**
	 * Sets the categoryService.
	 * 
	 * @param categoryService the categoryService to set
	 */
	public void setCategoryService(final CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	@Override
	public Class<? extends CategoryDTO> getDtoClass() {
		return CategoryDTO.class;
	}

	/**
	 * Collections strategy for category object.
	 */
	private static final class CategoryCollectionsStrategy implements CollectionsStrategy<Category, CategoryDTO> {

		private final boolean isAttributeClearStrategy;

		CategoryCollectionsStrategy(final ImporterConfiguration importerConfiguration) {
			isAttributeClearStrategy = importerConfiguration.getCollectionStrategyType(DependentElementType.CATEGORY_ATTRIBUTES).equals(
					CollectionStrategyType.CLEAR_COLLECTION);
		}

		@Override
		public void prepareCollections(final Category domainObject, final CategoryDTO dto) {
			if (isAttributeClearStrategy) {
				domainObject.getAttributeValueMap().clear();
			}
		}

		@Override
		public boolean isForPersistentObjectsOnly() {
			return true;
		}
	}
}
