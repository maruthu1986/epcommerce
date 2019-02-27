/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.importexport.common.comparators;

import java.util.Comparator;

import com.elasticpath.common.dto.DisplayValue;
import com.elasticpath.importexport.common.dto.catalogs.CartItemModifierFieldDTO;
import com.elasticpath.importexport.common.dto.catalogs.CartItemModifierFieldOptionDTO;
import com.elasticpath.importexport.common.dto.catalogs.SkuOptionValueDTO;
import com.elasticpath.importexport.common.dto.dynamiccontent.ParameterValueDTO;
import com.elasticpath.importexport.common.dto.inventory.InventoryWarehouseDTO;
import com.elasticpath.importexport.common.dto.productcategory.ProductCategoryDTO;
import com.elasticpath.importexport.common.dto.products.AttributeValuesDTO;
import com.elasticpath.importexport.common.dto.products.ProductSkuDTO;
import com.elasticpath.importexport.common.dto.promotion.ExceptionDTO;
import com.elasticpath.importexport.common.dto.promotion.ParameterDTO;
import com.elasticpath.importexport.common.dto.promotion.cart.ActionDTO;
import com.elasticpath.importexport.common.dto.promotion.rule.ConditionDTO;
import com.elasticpath.importexport.common.dto.settings.MetadataDTO;
import com.elasticpath.importexport.common.dto.tag.ConditionalExpressionDTO;

/**
 * Comparator classes for exporting objects from Import/Export in sorted order.
 */
public final class ExportComparators {
	/**
	 * Prevent this class from being instantiated.
	 */
	private ExportComparators() {
		// Do nothing
	}

	/**
	 * Export comparator for {@link ParameterDTO} objects.
	 */
	public static final Comparator<ParameterDTO> PARAMETER_DTO = Comparator
			.comparing(ParameterDTO::getKey)
			.thenComparing(ParameterDTO::getValue);

	/**
	 * Export comparator for {@link ExceptionDTO} objects.
	 */
	public static final Comparator<ExceptionDTO> EXCEPTION_DTO = Comparator
			.comparing(ExceptionDTO::getExceptionType)
			.thenComparing(ExceptionDTO::getExceptionParameters, new ListComparator<>(PARAMETER_DTO));

	/**
	 * Export comparator for {@link ActionDTO} objects.
	 */
	public static final Comparator<ActionDTO> ACTION_DTO = Comparator
			.comparing(ActionDTO::getType)
			.thenComparing(ActionDTO::getParameters, new ListComparator<>(PARAMETER_DTO))
			.thenComparing(ActionDTO::getExceptions, new ListComparator<>(EXCEPTION_DTO));

	/**
	 * Export comparator for {@link AttributeValuesDTO} objects.
	 */
	public static final Comparator<AttributeValuesDTO> ATTRIBUTE_VALUES_DTO = Comparator
			.comparing(AttributeValuesDTO::getKey);

	/**
	 * Export comparator for {@link CartItemModifierFieldDTO} objects.
	 */
	public static final Comparator<CartItemModifierFieldDTO> CART_ITEM_MODIFIER_FIELD_DTO = Comparator
			.comparing(CartItemModifierFieldDTO::getOrdering);

	/**
	 * Export comparator for {@link CartItemModifierFieldOptionDTO} objects.
	 */
	public static final Comparator<CartItemModifierFieldOptionDTO> CART_ITEM_MODIFIER_FIELD_OPTION_DTO = Comparator
			.comparing(CartItemModifierFieldOptionDTO::getOrdering);

	/**
	 * Export comparator for {@link ConditionalExpressionDTO} objects.
	 */
	public static final Comparator<ConditionalExpressionDTO> CONDITIONAL_EXPRESSION_DTO_COMPARATOR = Comparator
			.comparing(ConditionalExpressionDTO::getGuid);

	/**
	 * Export comparator for {@link ConditionDTO} objects.
	 */
	public static final Comparator<ConditionDTO> CONDITION_DTO_COMPARATOR = Comparator
			.comparing(ConditionDTO::getKind)
			.thenComparing(ConditionDTO::getType)
			.thenComparing(ConditionDTO::getParameters, new ListComparator<>(PARAMETER_DTO))
			.thenComparing(ConditionDTO::getExceptions, new ListComparator<>(EXCEPTION_DTO));

	/**
	 * Export comparator for {@link DisplayValue} objects.
	 */
	public static final Comparator<DisplayValue> DISPLAY_VALUE_COMPARATOR = Comparator
			.comparing(DisplayValue::getLanguage, Comparator.nullsFirst(String::compareTo));

	/**
	 * Export comparator for {@link InventoryWarehouseDTO} objects.
	 */
	public static final Comparator<InventoryWarehouseDTO> INVENTORY_WAREHOUSE_DTO_COMPARATOR = Comparator
			.comparing(InventoryWarehouseDTO::getCode);

	/**
	 * Export comparator for {@link MetadataDTO} objects.
	 */
	public static final Comparator<MetadataDTO> METADATA_DTO_COMPARATOR = Comparator
			.comparing(MetadataDTO::getKey);

	/**
	 * Export comparator for {@link ParameterValueDTO} objects.
	 */
	public static final Comparator<ParameterValueDTO> PARAMETER_VALUE_DTO_COMPARATOR = Comparator
			.comparing(ParameterValueDTO::getGuid);

	/**
	 * Export comparator for {@link ProductCategoryDTO} objects.
	 */
	public static final Comparator<ProductCategoryDTO> PRODUCT_CATEGORY_DTO_COMPARATOR = Comparator
			.comparing(ProductCategoryDTO::getCategoryCode);

	/**
	 * Export comparator for {@link ProductSkuDTO} objects.
	 */
	public static final Comparator<ProductSkuDTO> PRODUCT_SKU_DTO_COMPARATOR = Comparator
			.comparing(ProductSkuDTO::getSkuCode);

	/**
	 * Export comparator for {@link SkuOptionValueDTO} objects.
	 */
	public static final Comparator<SkuOptionValueDTO> SKU_OPTION_VALUE_DTO_COMPARATOR = Comparator
			.comparing(SkuOptionValueDTO::getOrdering);
}
