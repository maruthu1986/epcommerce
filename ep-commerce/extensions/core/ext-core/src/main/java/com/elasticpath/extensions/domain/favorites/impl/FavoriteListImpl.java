package com.elasticpath.extensions.domain.favorites.impl;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.impl.AbstractShoppingListImpl;
import com.elasticpath.domain.shoppingcart.impl.ShoppingItemImpl;
import com.elasticpath.extensions.domain.favorites.FavoriteList;
import com.elasticpath.persistence.support.FetchGroupConstants;
import org.apache.log4j.Logger;
import org.apache.openjpa.persistence.DataCache;
import org.apache.openjpa.persistence.FetchAttribute;
import org.apache.openjpa.persistence.FetchGroup;
import org.apache.openjpa.persistence.FetchGroups;
import org.apache.openjpa.persistence.ElementDependent;
import org.apache.openjpa.persistence.jdbc.ElementForeignKey;
import org.apache.openjpa.persistence.jdbc.ElementJoinColumn;
import org.apache.openjpa.persistence.jdbc.ForeignKeyAction;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.OrderBy;
import javax.persistence.OneToMany;
import javax.persistence.FetchType;
import javax.persistence.CascadeType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Entity
@Table(name = FavoriteListImpl.TABLE_NAME)
@DataCache(enabled = false)
@FetchGroups({
        @FetchGroup(
                name = FetchGroupConstants.SHOPPING_ITEM_CHILD_ITEMS,
                attributes = {
                        @FetchAttribute(name = "allItems", recursionDepth = -1)
                }
        )
})
public class FavoriteListImpl extends AbstractShoppingListImpl implements FavoriteList {
    private static final Logger LOG = Logger.getLogger(FavoriteListImpl.class);

    /**
     * Serial version id.
     */
    private static final long serialVersionUID = 5000000001L;
    /**
     * The name of the table & generator to use for persistence.
     */
    public static final String TABLE_NAME = "TMFAVORITES";
    private String storeCode;
    private long uidPk;
    private List<ShoppingItem> allItems = new ArrayList<>();

    @Override
    public ShoppingItem addItem(final ShoppingItem item) {
        LOG.info("ENTERED FavoriteListImpl");
        ShoppingItem newItem = getBean(ContextIdNames.SHOPPING_ITEM);
        newItem.setSkuGuid(item.getSkuGuid());

        Map<String, String> fields = item.getFields();
        for (final Map.Entry<String, String> fieldEntry : fields.entrySet()) {
            newItem.setFieldValue(fieldEntry.getKey(), fieldEntry.getValue());
        }

        getAllItems().add(newItem);
        LOG.info("EXIT FavoriteListImpl");
        return newItem;
    }

    @Override
    public void removeItemBySkuGuid(final String skuGuid) {
        List<ShoppingItem> items = getAllItems().stream()
                .filter(item -> item.getSkuGuid().equals(skuGuid))
                .collect(Collectors.toList());

        if (!items.isEmpty()) {
            getAllItems().removeAll(items);
        }
    }

    @Override
    public void removeItem(final String favoriteItemGuid) {
        getAllItems().stream()
                .filter(item -> item.getGuid().equals(favoriteItemGuid))
                .findFirst()
                .map(item -> getAllItems().remove(item));
    }

    @Override
    @Column(name = "STORECODE")
    public String getStoreCode() {
        return this.storeCode;
    }

    @Override
    public void setStoreCode(final String storeCode) {
        this.storeCode = storeCode;
    }

    @Override
    @OneToMany(targetEntity = ShoppingItemImpl.class, cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @ElementJoinColumn(name = "FAVORITES_UID", updatable = false)
    @ElementForeignKey(updateAction = ForeignKeyAction.CASCADE)
    @ElementDependent
    @OrderBy("ordering")
    public List<ShoppingItem> getAllItems() {
        return allItems;
    }

    @Override
    public void setAllItems(final List<ShoppingItem> allItems) {
        this.allItems = allItems;
    }

    @Override
    @Id
    @Column(name = "UIDPK")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
    @TableGenerator(name = TABLE_NAME, table = "JPA_GENERATED_KEYS", pkColumnName = "ID",
            valueColumnName = "LAST_VALUE", pkColumnValue = TABLE_NAME, allocationSize = HIGH_CONCURRENCY_ALLOCATION_SIZE)
    public long getUidPk() {
        return this.uidPk;
    }

    @Override
    public void setUidPk(final long uidPk) {
        this.uidPk = uidPk;
    }
}

