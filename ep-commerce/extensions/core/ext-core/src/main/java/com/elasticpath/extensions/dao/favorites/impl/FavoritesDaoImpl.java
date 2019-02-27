package com.elasticpath.extensions.dao.favorites.impl;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.domain.catalog.CategoryLoadTuner;
import com.elasticpath.domain.catalog.ProductLoadTuner;
import com.elasticpath.domain.catalog.ProductSkuLoadTuner;
import com.elasticpath.domain.catalog.ShoppingItemLoadTuner;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.extensions.dao.favorites.FavoritesDao;
import com.elasticpath.extensions.domain.favorites.FavoriteList;
import com.elasticpath.extensions.domain.favorites.impl.FavoriteListImpl;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.service.misc.FetchPlanHelper;
import org.apache.log4j.Logger;

import java.util.List;

public class FavoritesDaoImpl implements FavoritesDao {
    private static final Logger LOG = Logger.getLogger(FavoritesDaoImpl.class);
    private PersistenceEngine persistenceEngine;
    private ProductSkuLoadTuner productSkuLoadTuner;
    private ProductLoadTuner productLoadTuner;
    private CategoryLoadTuner categoryLoadTuner;
    private ShoppingItemLoadTuner shoppingItemLoadTuner;
    private FetchPlanHelper fetchPlanHelper;

    @Override
    public FavoriteList findByShopper(final Shopper shopper) {
        LOG.info("ENTERED FavoritesDaoImpl.findByShopper()");
        configureLoadTuners();

        PersistenceEngine persister = getPersistenceEngine();
        Object[] params = new Object[]{shopper.getUidPk()};
        List<FavoriteList> results = persister.retrieveByNamedQuery("FAVORITELIST_BY_SHOPPING_CONTEXT", params);
        FavoriteList favoriteList = null;
        if (!results.isEmpty()) {
            favoriteList = results.get(0);
        }
        fetchPlanHelper.clearFetchPlan();
        LOG.info("EXIT FavoritesDaoImpl.findByShopper() ** favoriteList ** "+favoriteList);
        return favoriteList;
    }

    @Override
    public FavoriteList get(final long uid) throws EpServiceException {
        LOG.info("FavoritesDaoImpl.get() START uid :: "+uid);
        configureLoadTuners();
        FavoriteList favoriteList = this.getPersistenceEngine().load(FavoriteListImpl.class, uid);
        LOG.info("FavoritesDaoImpl.get() favoriteList :: "+favoriteList);
        fetchPlanHelper.clearFetchPlan();
        return favoriteList;
    }

    @Override
    public FavoriteList findByGuid(final String guid) {
        LOG.info("FavoritesDaoImpl.findByGuid() START guid :: "+guid);
        configureLoadTuners();
        List<FavoriteList> results = getPersistenceEngine().retrieveByNamedQuery("FAVORITELIST_BY_GUID", guid);
        LOG.info("FavoritesDaoImpl.findByGuid() FavoriteList :: "+results);

        FavoriteList favoriteList = null;
        if (!results.isEmpty()) {
            favoriteList = results.get(0);
        }
        fetchPlanHelper.clearFetchPlan();
        LOG.info("FavoritesDaoImpl.findByGuid() END favoriteList :: "+favoriteList);
        return favoriteList;
    }

    @Override
    public void remove(final FavoriteList favoriteList) {
        if (favoriteList != null) {
            this.getPersistenceEngine().delete(favoriteList);
        }
    }

    @Override
    public FavoriteList saveOrUpdate(final FavoriteList favoriteList) {
        return getPersistenceEngine().saveOrUpdate(favoriteList);
    }

    /**
     * Get product sku load tuner.
     *
     * @return the product sku load tuner
     */
    public ProductSkuLoadTuner getProductSkuLoadTuner() {
        return productSkuLoadTuner;
    }

    /**
     * Set product sku load tuner.
     *
     * @param productSkuLoadTuner the product sku load tuner
     */
    public void setProductSkuLoadTuner(final ProductSkuLoadTuner productSkuLoadTuner) {
        this.productSkuLoadTuner = productSkuLoadTuner;
    }

    /**
     * Get product load tuner.
     *
     * @return the product load tuner
     */
    public ProductLoadTuner getProductLoadTuner() {
        return productLoadTuner;
    }

    /**
     * Set product load tuner.
     *
     * @param productLoadTuner the product load tuner
     */
    public void setProductLoadTuner(final ProductLoadTuner productLoadTuner) {
        this.productLoadTuner = productLoadTuner;
    }

    /**
     * Get category load tuner.
     *
     * @return the category load tuner
     */
    public CategoryLoadTuner getCategoryLoadTuner() {
        return categoryLoadTuner;
    }

    /**
     * Set category load tuner.
     *
     * @param categoryLoadTuner the category load tuner
     */
    public void setCategoryLoadTuner(final CategoryLoadTuner categoryLoadTuner) {
        this.categoryLoadTuner = categoryLoadTuner;
    }

    /**
     * Get shopping item load tuner.
     *
     * @return the shopping item load tuner
     */
    public ShoppingItemLoadTuner getShoppingItemLoadTuner() {
        return shoppingItemLoadTuner;
    }

    /**
     * Set shopping item load tuner.
     *
     * @param shoppingItemLoadTuner the shopping item load tuner
     */
    public void setShoppingItemLoadTuner(final ShoppingItemLoadTuner shoppingItemLoadTuner) {
        this.shoppingItemLoadTuner = shoppingItemLoadTuner;
    }

    /**
     * Get fetch plan helper.
     *
     * @return the fetch plan helper
     */
    public FetchPlanHelper getFetchPlanHelper() {
        return fetchPlanHelper;
    }

    /**
     * Set fetch plan helper.
     *
     * @param fetchPlanHelper the fetch plan helper
     */
    public void setFetchPlanHelper(final FetchPlanHelper fetchPlanHelper) {
        this.fetchPlanHelper = fetchPlanHelper;
    }


    /**
     * Configure the load tuners.
     */
    protected void configureLoadTuners() {
        fetchPlanHelper.configureLoadTuner(productLoadTuner);
        fetchPlanHelper.configureLoadTuner(productSkuLoadTuner);
        fetchPlanHelper.configureLoadTuner(categoryLoadTuner);
        fetchPlanHelper.configureLoadTuner(shoppingItemLoadTuner);
    }

    /**
     * Get the persistence Engine.
     *
     * @return the persistence engine
     */
    public PersistenceEngine getPersistenceEngine() {
        return persistenceEngine;
    }

    /**
     * Set the persistence Engine.
     *
     * @param persistenceEngine the persistence engine
     */
    public void setPersistenceEngine(final PersistenceEngine persistenceEngine) {
        this.persistenceEngine = persistenceEngine;
    }

    @Override
    public int deleteEmptyFavoriteListsByShopperUids(final List<Long> shopperUids) {
        if (shopperUids == null) {
            throw new EpServiceException("shopperUids must not be null");
        }
        if (shopperUids.isEmpty()) {
            return 0;
        }

        return persistenceEngine.executeNamedQueryWithList("DELETE_EMPTY_FAVORITELISTS_BY_SHOPPER_UID", "list", shopperUids);
    }

    @Override
    public int deleteAllFavoriteListsByShopperUids(final List<Long> shopperUids) {
        if (shopperUids == null) {
            throw new EpServiceException("shopperUids must not be null");
        }
        if (shopperUids.isEmpty()) {
            return 0;
        }

        return persistenceEngine.executeNamedQueryWithList("DELETE_ALL_FAVORITELISTS_BY_SHOPPER_UID", "list", shopperUids);
    }
}
