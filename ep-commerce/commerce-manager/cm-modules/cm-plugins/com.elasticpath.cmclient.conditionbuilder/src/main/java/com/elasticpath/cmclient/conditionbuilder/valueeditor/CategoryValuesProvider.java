/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.conditionbuilder.valueeditor;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.service.catalog.CategoryService;


/**
 * Category Value provider.
 */
public class CategoryValuesProvider implements SelectableValueResolver {
	
	private final CategoryService categoryService;
	
	/**
	 * Constructor.
	 */
	public CategoryValuesProvider() {
		super();
		this.categoryService = ServiceLocator.getService(ContextIdNames.CATEGORY_SERVICE);
	}

	/**
	 * Get the value, that will be used in tag framework expression.
	 * Not implemented for values, that support editing via finder dialogs.
	 * 
	 * @param selectionIndex , that passed from UI combobox.
	 *            
	 * @return a value, that will be used in tag framework expression
	 */
	public Object getValueBySelectionIndex(final int selectionIndex) {	
		return null; 
	}

	@Override
	public String getNameByValue(final Object value) {	
		final Category category = categoryService.findByCode(String.valueOf(value));
		if (category != null) {
			return category.getDisplayName(CorePlugin.getDefault().getDefaultLocale());
		}
		return String.valueOf(value);
	}


}
