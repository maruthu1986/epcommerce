package com.elasticpath.extensions.domain.favorites;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.impl.ShoppingItemImpl;
import com.elasticpath.extensions.domain.favorites.impl.FavoriteListImpl;
import com.elasticpath.test.integration.BasicSpringContextTest;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FavoriteListTest extends BasicSpringContextTest {
    private static final String SKU1 = "sku1";
    private FavoriteListImpl favoriteList;

    /**
     * The setup method.
     */
    @Before
    public void setUp() {
        favoriteList = new FavoriteListImpl() {
            private static final long serialVersionUID = -4858143846698274756L;

            @Override
            public ShoppingItem addItem(final ShoppingItem item) {
                ShoppingItem shoppingItem = new ShoppingItemImpl();
                shoppingItem.setSkuGuid(item.getSkuGuid());
                getAllItems().add(shoppingItem);
                return item;
            }
        };
    }

    @Test
    public void testFavoriteItem() {
        ShoppingItem item = new ShoppingItemImpl();
        ProductSku sku1 = new ProductSkuImpl();
        sku1.initialize();
        sku1.setSkuCode(SKU1);
        item.setSkuGuid(sku1.getGuid());

        favoriteList.addItem(item);

        assertThat(favoriteList.getAllItems().isEmpty())
                .as("Check")
                .isFalse();
        favoriteList.removeItemBySkuGuid(sku1.getGuid());
        assertThat(favoriteList.getAllItems().isEmpty())
                .as("Item should have been removed")
                .isTrue();
    }

    @Test
    public void testMultipleFavoriteItems() {
        ShoppingItem item = new ShoppingItemImpl();
        ProductSku sku1 = new ProductSkuImpl();
        sku1.initialize();
        sku1.setSkuCode(SKU1);
        item.setSkuGuid(sku1.getGuid());

        favoriteList.addItem(item);
        favoriteList.addItem(item);

        assertThat(favoriteList.getAllItems().size())
                .as("Check")
                .isEqualTo(2);
        favoriteList.removeItemBySkuGuid(sku1.getGuid());
        assertThat(favoriteList.getAllItems().isEmpty())
                .as("Item should have been removed")
                .isTrue();
    }
}
