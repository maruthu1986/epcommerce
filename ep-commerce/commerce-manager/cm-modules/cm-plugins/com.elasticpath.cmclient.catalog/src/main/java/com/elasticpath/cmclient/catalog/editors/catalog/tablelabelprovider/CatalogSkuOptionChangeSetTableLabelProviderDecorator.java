/**
 * Copyright (c) Elastic Path Software Inc., 2011
 */
package com.elasticpath.cmclient.catalog.editors.catalog.tablelabelprovider;

import org.eclipse.swt.graphics.Image;

import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.editors.TableItems;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;

/**
 * A modification to the standard {@link ChangeSetTableLabelProviderDecorator} for the SkuOption section. <br>
 * Do not show change set locks on SkuOptionValues.
 */
public class CatalogSkuOptionChangeSetTableLabelProviderDecorator extends ChangeSetTableLabelProviderDecorator<SkuOption> {

	private final TableItems<SkuOptionValue> skuOptionValueTableItems;

	/**
	 * Constructor.
	 *
	 * @param decoratedChangeSetTableLabelProvider the table label provider to be decorated
	 * @param skuOptionTableItems the change set objects
	 * @param skuOptionValueTableItems the table items; used to track change set actions on SkuValueOptions.
	 */
	
	public CatalogSkuOptionChangeSetTableLabelProviderDecorator(final ExtensibleTableLabelProvider decoratedChangeSetTableLabelProvider,
			final TableItems<SkuOption> skuOptionTableItems, final TableItems<SkuOptionValue> skuOptionValueTableItems) {
		super(decoratedChangeSetTableLabelProvider, skuOptionTableItems);
		this.skuOptionValueTableItems = skuOptionValueTableItems;
	}



	@Override
	public Image getColumnImage(final Object element, final int columnIndex) {
		if (element instanceof SkuOption) {
			return super.getColumnImage(element, columnIndex);
		} else if (element instanceof SkuOptionValue) {
			SkuOptionValue skuOptionValue = (SkuOptionValue) element;
			return getSkuOptionValueChangeSetActionImage(skuOptionValue, columnIndex);
		}
		
		return null;
	}
	
	private Image getSkuOptionValueChangeSetActionImage(final SkuOptionValue skuOptionValue, final int columnIndex) {
		Image changeSetActionImage = getDecoratedTableLabelProvider().getColumnImage(skuOptionValue, columnIndex);
		final String columnName = getColumnIndexRegistryName(columnIndex);

		if (CHANGE_SET_ACTION_INDEX.equals(columnName)) {
			if (skuOptionValueTableItems.getAddedItems().contains(skuOptionValue)) {
				changeSetActionImage = CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_STATE_ADDED_SMALL);
			} else if (skuOptionValueTableItems.getRemovedItems().contains(skuOptionValue)) {
				changeSetActionImage = CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_STATE_DELETED_SMALL);
			} else if (skuOptionValueTableItems.getModifiedItems().contains(skuOptionValue)) {
				changeSetActionImage = CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_STATE_CHANGED_SMALL);
			}
		}	
		return changeSetActionImage;
		
	}

}
