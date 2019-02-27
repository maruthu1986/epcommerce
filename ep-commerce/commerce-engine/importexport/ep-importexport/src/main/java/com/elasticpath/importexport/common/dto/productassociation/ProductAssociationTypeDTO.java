/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.importexport.common.dto.productassociation;

import java.util.Collection;

import com.elasticpath.common.dto.Dto;
import com.elasticpath.commons.exception.EpIntBindException;
import com.elasticpath.commons.util.extenum.AbstractExtensibleEnum;
import com.elasticpath.domain.catalog.ProductAssociationType;

/**
 * The ProductAssociationTypeDTO.
 */
public class ProductAssociationTypeDTO extends AbstractExtensibleEnum<ProductAssociationTypeDTO> implements Dto {
	private static final long serialVersionUID = 1L;

	private static final int CROSS_SELL_ORDINAL = 1;

	private static final String CROSS_SELL_NAME = "CrossSell";

	private static final ProductAssociationType CROSS_SELL_TYPE = ProductAssociationType.CROSS_SELL;

	private static final int UP_SELL_ORDINAL = 2;

	private static final String UP_SELL_NAME = "UpSell";

	private static final ProductAssociationType UP_SELL_TYPE = ProductAssociationType.UP_SELL;

	private static final int WARRANTY_ORDINAL = 3;

	private static final String WARRANTY_NAME = "Warranty";

	private static final ProductAssociationType WARRANTY_TYPE = ProductAssociationType.WARRANTY;

	private static final int ACCESSORY_ORDINAL = 4;

	private static final String ACCESSORY_NAME = "Accessory";

	private static final ProductAssociationType ACCESSORY_TYPE = ProductAssociationType.ACCESSORY;

	private static final int REPLACEMENT_ORDINAL = 5;

	private static final String REPLACEMENT_NAME = "Replacement";

	private static final ProductAssociationType REPLACEMENT_TYPE = ProductAssociationType.REPLACEMENT;

	private static final int RECOMMENDATION_ORDINAL = 6;

	private static final String RECOMMENDATION_NAME = "Recommendation";

	private static final ProductAssociationType RECOMMENDATION_TYPE = ProductAssociationType.RECOMMENDATION;

	/**
	 * Cross Sell Product Association Type DTO.
	 */
	public static final ProductAssociationTypeDTO CROSS_SELL = new ProductAssociationTypeDTO(CROSS_SELL_ORDINAL,
		CROSS_SELL_NAME,
		CROSS_SELL_TYPE);

	/**
	 * Cross Sell Product Association Type DTO.
	 */
	public static final ProductAssociationTypeDTO UP_SELL = new ProductAssociationTypeDTO(UP_SELL_ORDINAL,
		UP_SELL_NAME,
		UP_SELL_TYPE);

	/**
	 * Cross Sell Product Association Type DTO.
	 */
	public static final ProductAssociationTypeDTO WARRANTY = new ProductAssociationTypeDTO(WARRANTY_ORDINAL,
		WARRANTY_NAME,
		WARRANTY_TYPE);

	/**
	 * Cross Sell Product Association Type DTO.
	 */
	public static final ProductAssociationTypeDTO ACCESSORY = new ProductAssociationTypeDTO(ACCESSORY_ORDINAL,
		ACCESSORY_NAME,
		ACCESSORY_TYPE);

	/**
	 * Cross Sell Product Association Type DTO.
	 */
	public static final ProductAssociationTypeDTO REPLACEMENT = new ProductAssociationTypeDTO(REPLACEMENT_ORDINAL,
		REPLACEMENT_NAME,
		REPLACEMENT_TYPE);

	/**
	 * Cross Sell Product Association Type DTO.
	 */
	public static final ProductAssociationTypeDTO RECOMMENDATION = new ProductAssociationTypeDTO(RECOMMENDATION_ORDINAL,
		RECOMMENDATION_NAME,
		RECOMMENDATION_TYPE);

	private final ProductAssociationType productAssociationType;
	/**
	 * This field is here to support legacy data xmls that use these CAMEL-CASE names in their data xmls.
	 * We cannot directly use the name field on extensible enum since it will be uppercased on exports.
	 */
	private final String importExportName;

	/**
	 * Constructor.
	 *
	 * @param ordinal the ordinal.
	 * @param importExportName the name used in data xml <type> tag
	 * @param productAssociationType the corresponding product association type to this DTO.
	 */
	protected ProductAssociationTypeDTO(final int ordinal, final String importExportName,
										final ProductAssociationType productAssociationType) {
		super(ordinal, importExportName, ProductAssociationTypeDTO.class);
		this.productAssociationType = productAssociationType;
		this.importExportName = importExportName;
	}

	/**
	 * @return the product association type corresponding to this DTO.
	 */
	public ProductAssociationType type() {
		return productAssociationType;
	}

	/**
	 * @return the name used in xml data files.
	 */
	public String getImportExportName() {
		return importExportName;
	}

	/**
	 * Converts productAssociationType to ProductAssociationTypeDTO.
	 *
	 * @param productAssociationType the productAssociationType
	 * @return ProductAssociationTypeDTO
	 */
	public static ProductAssociationTypeDTO valueOf(final ProductAssociationType productAssociationType) {
		for (ProductAssociationTypeDTO productAssociationTypeDTO : values(ProductAssociationTypeDTO.class)) {
			if (productAssociationTypeDTO.type().equals(productAssociationType)) {
				return productAssociationTypeDTO;
			}
		}
		throw new EpIntBindException("Invalid association productAssociationType index: " + productAssociationType);
	}

	/**
	 * @param importExportName the import export name of the product association type dto to retrieve
	 * @return the product association type dto corresponding to this name
	 */
	public static ProductAssociationTypeDTO valueOf(final String importExportName) {
		return valueOf(importExportName, ProductAssociationTypeDTO.class);
	}

	@Override
	protected Class<ProductAssociationTypeDTO> getEnumType() {
		return ProductAssociationTypeDTO.class;
	}

	/**
	 * @return all Product Association Type DTO values.
	 */
	public static Collection<ProductAssociationTypeDTO> values() {
		return values(ProductAssociationTypeDTO.class);
	}
}